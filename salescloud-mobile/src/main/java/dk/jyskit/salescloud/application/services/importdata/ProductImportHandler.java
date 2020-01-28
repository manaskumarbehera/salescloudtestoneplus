package dk.jyskit.salescloud.application.services.importdata;

import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.waf.application.utils.exceptions.SystemException;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.MobileProductDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.waf.application.utils.exceptions.UserErrorException;
import dk.jyskit.waf.utils.dataimport.DataImportException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jan
 *
 * WAF pattern: this is how to do import
 */
@Slf4j
public class ProductImportHandler extends AbstractImportHandler {
	public static final String PRODUCT_GROUP 		= "Produktgruppe";
	
	@Inject private MobileProductDao productDao;
	@Inject private ProductGroupDao productGroupDao;

	private BusinessArea businessArea;

	private boolean skipRow;
	private MobileProduct product;
	private Amounts amounts;
	private List<MobileProductGroupEnum> groupsFilter;

	public ProductImportHandler(BusinessArea businessArea) {
		super();
		//	objects.put(Product.class, new HashMap<>());
		//	for (Product product: account.getProducts()) {
		//		putObject(Product.class, product.getName(), product);
		//	}
		this.businessArea = businessArea;
	}
	
	@Override
	public boolean getTwoPass() {
		// I want bean validation exceptions to be caught and reported in pass 1
		return true;
	}

	@Override
	protected List<Col> createCols() {
		List<Col> cols = new ArrayList<>();
		cols.add(new Col("Kommentar", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (StringUtils.equalsIgnoreCase((String) value, "Ikke aktiv")) {
					skipRow = true;
				}
			}
		});

		cols.add(new Col("Produktgruppe", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value != null && ((String) value).indexOf("Omstilling -> Installation - lokation") != -1) {
					log.info("x");
				}
				MobileProductGroup group = (MobileProductGroup) businessArea.getProductGroupByFullPath((String) value);
				if (group == null) {
					log.error("Group not found: " + value);
					skipRow = true;
				}
				if (!skipRow && (groupsFilter != null)) {
					MobileProductGroup g = group;
					do {
						skipRow = true;
						for (MobileProductGroupEnum mpge: groupsFilter) {
							if (g.getUniqueName().equals(mpge.getKey())) {
								skipRow = false;
								break;
							}
						}
						g = (MobileProductGroup) g.getParentProductGroup();
					} while (g != null && !skipRow);
				}
				if (!skipRow) {
					if (pass == 0) {
						product.setProductGroup(group);
					} else {
						group.addProduct(product);
					}
				}
			}
		});
		
		cols.add(new Col("Navn", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setPublicName(value.toString());
			}
		}); 
		
		cols.add(new Col("Internt navn (Nabs/CDM kode)", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setInternalName(value.toString());
			}
		}); 
		
		cols.add(new Col("Kvik kode", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setKvikCode(value.toString());
			}
		});

		cols.add(new Col("Varenummer", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (pass==1) {
					if (product.getProductGroup() == null) {
						log.warn("Can we avoid this?");
					}
					for (Product p: product.getProductGroup().getProducts()) {
						if (!Objects.equals(p, product) && Objects.equals(p.getProductId(), value.toString())) {
							skipRow = true;
							break;
						}
					}
				}
				product.setProductId(value.toString());
			}
		}); 
		
		cols.add(new Col("Oprettelsespris", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (!"-".equals(value)) {
					product.getPrice().setOneTimeFee(getAmount(value));
				}
			}
		}); 
		
		cols.add(new Col("Installationspris", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (!"-".equals(value)) {
					product.getPrice().setInstallationFee(getAmount(value));
				}
			}
		}); 
		
		cols.add(new Col("Pris pr. betalingsperiode", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (!"-".equals(value)) {
					product.getPrice().setRecurringFee(getAmount(value));
				}
			}
		}); 
		
		cols.add(new Col("Pris pr. betalingsperiode x 100", false) {
			public void process(MobileProduct product, Object value, int pass) {
				if (!"-".equals(value)) {
					if (value instanceof Number) {
						product.getPrice().setRecurringFee(((Number) value).longValue());
					}
				}
			}
		}); 
		
		cols.add(new Col("Betalingsperiode", true) {
			public void process(MobileProduct product, Object value, int pass) {
				String s = (String) value;
				if (s.indexOf("eekly") != -1) {
					product.setPaymentFrequency(PaymentFrequency.WEEKLY);
				} else if (s.indexOf("onthly") != -1) {
					product.setPaymentFrequency(PaymentFrequency.MONTHLY);
				} else if (s.indexOf("uarterly") != -1) {
					product.setPaymentFrequency(PaymentFrequency.QUARTERLY);
				} else if (s.indexOf("early") != -1) {
					product.setPaymentFrequency(PaymentFrequency.YEARLY);
				} else {
					product.setPaymentFrequency(PaymentFrequency.MONTHLY);
				}
			}
		}); 
		
		cols.add(new Col("Std. antal", true) {
			public void process(MobileProduct product, Object value, int pass) {
				try {
					product.setDefaultCount(getInteger(value));
				} catch (Exception e) {
					product.setDefaultCount(0);
				}
			}
		}); 
		
		cols.add(new Col("Min. antal", true) {
			public void process(MobileProduct product, Object value, int pass) {
				try {
					product.setMinCount(getInteger(value));
				} catch (Exception e) {
					product.setMinCount(0);
				}
			}
		}); 
		
		cols.add(new Col("Max. antal", true) {
			public void process(MobileProduct product, Object value, int pass) {
				try {
					product.setMaxCount(getInteger(value));
				} catch (Exception e) {
					product.setMaxCount(0);
				}
			}
		}); 
		
		cols.add(new Col("Fordelsaftale rabatberettiget", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setDiscountEligible("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("IPSA rabatberettiget", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setIpsaDiscountEligible("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Rabataftale rabatberettiget", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setRabataftaleDiscountEligible("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("GKS", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setGks("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("TDC Installation", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setTdcInstallation("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Vis ikke i konfigurator", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setExcludeFromConfigurator("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Vis ikke i tastegrundlag", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setExcludeFromProductionOutput("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Vis ikke i tilbud", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setExcludeFromOffer("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Provision - vedr. inst. (pr. segment)", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value != null) {
					product.setProvisionInstallationFee("" + value);
				}
			}
		}); 
		
		cols.add(new Col("Provision - vedr. etabl. (pr. segment)", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value != null) {
					product.setProvisionOneTimeFee("" + value);
				}
			}
		}); 
		
		cols.add(new Col("Provision - vedr. mnd. bet. (pr. segment)", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value != null) {
					product.setProvisionRecurringFee("" + value);
				}
			}
		}); 
		
		cols.add(new Col("Variabel inst.pris", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setVariableInstallationFee("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Variabel driftpris", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setVariableRecurringFee("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Variabel kategori", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setVariableCategory("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Variabelt produktnavn", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setVariableProductName("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Noter", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setRemarks((String) value);
			}
		});

		cols.add(new Col("Specielle flag", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value instanceof String) {
					product.setFlags((String) value);
				} else {
					product.setFlags("" + value);
				}
			}
		});

		cols.add(new Col("Filter", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value instanceof String) {
					product.setFilter((String) value);
				} else {
					product.setFilter("" + value);
				}
			}
		});

		cols.add(new Col("Filter ID", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value instanceof String) {
					product.setFilterID((String) value);
				} else {
					product.setFilterID("" + value);
				}
			}
		});

		cols.add(new Col("Tæl alle abonnenter", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setSubscriberProduct("ja".equalsIgnoreCase((String) value));
			}
		}); 
		
		cols.add(new Col("Sortering i UI", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value instanceof Number) {
					product.setSortIndex(((Number) value).intValue());
				}
			}
		}); 
		
		cols.add(new Col("Sortering i CDM", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value instanceof Number) {
					product.setOutputSortIndex(((Number) value).intValue());
				}
			}
		}); 
		
		cols.add(new Col("Sortering i tilbud", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value instanceof Number) {
					product.setOfferSortIndex(((Number) value).intValue());
				}
			}
		});

		cols.add(new Col("Pulje pakke", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setPoolModeBundle("ja".equalsIgnoreCase((String) value));
			}
		});

		cols.add(new Col("Ikke-pulje pakke", true) {
			public void process(MobileProduct product, Object value, int pass) {
				product.setNonPoolModeBundle("ja".equalsIgnoreCase((String) value));
			}
		});

		cols.add(new Col("Pulje indeks", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value instanceof Number) {
					product.setPoolIndex(((Number) value).intValue());
				}
			}
		});

		cols.add(new Col("Pulje bidrag", true) {
			public void process(MobileProduct product, Object value, int pass) {
				if (value instanceof String) {
					product.setPoolContributions((String) value);
				}
			}
		});

		return cols;
	}

	private long getAmount(Object value) {
		if (value instanceof Integer) {
			return ((Integer) value).longValue();
		}
		String s = (String) value;
		if (StringUtils.isEmpty(s)) {
			return 0;
		} else {
			return 100 * Long.valueOf(StringUtils.split(s)[0].replace(".", ""));
		}
	}
	
	private Integer getInteger(Object value) {
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		} else if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value instanceof String) {
			if ("-".equals(value)) {
				return null;
			}
			if (StringUtils.isEmpty((String) value)) {
				return null;
			}
			try {
				return Integer.valueOf((String) value);
			} catch (Exception e) {
				log.error("Bad value", e);
			}
		}
		return null;
	}
	
	@Override
	public void handleInputRow(int rowNo, Object[] values, Map<String, Integer> columnToPositionMap, int pass) throws DataImportException {
		if (!isEmptyRow(values)) {
			if (rowNo % 100 == 0) {
				log.info("" + rowNo);
			}
			
			for (int i = 0; i < values.length; i++) {
				if (values[i] != null && values[i] instanceof String) {
					values[i] = ((String) values[i]).trim();
				}
			}
			
			if (pass == 0) {
				// Validate data
				beforeRow();
				for (Col col: cols) {
					Integer colIndex = columnToPositionMap.get(col.getKey().toLowerCase());
					if (col.isMandatory() || colIndex != null) {
						try {
							if (colIndex < values.length) {
								col.process(product, values[colIndex], pass);
							}
						} catch (Exception e) {
							log.error("Problem in row " + rowNo + ", col " + colIndex, e);
						}
					}
					if (skipRow) {
						break;
					}
				}
				
				if (!skipRow) {
					ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
					Validator validator = factory.getValidator();
					{
						Set<ConstraintViolation<MobileProduct>> errors = validator.validate(product);
						if (errors.size() > 0) {
							throw new UserErrorException("Ugyldige data i række " + rowNo + ": " + errors.iterator().next().getMessage());
						}
					}
				}
			} else {
				try {
					beforeRow();
					for (Col col: cols) {
						Integer colIndex = columnToPositionMap.get(col.getKey().toLowerCase());
						if (col.isMandatory() || colIndex != null) {
							if (colIndex < values.length) {
								col.process(product, values[colIndex], pass);
							}
						}
						if (skipRow) {
							break;
						}
					}
					if (!skipRow) {
						productDao.save(product);
					}
				} catch (Exception e) {
					if (e instanceof ConstraintViolationException) {
						ConstraintViolationException constraintViolationException = (ConstraintViolationException) e;
						
						for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
							log.error(constraintViolation.getMessage());
							log.error("I'm guessing this is the problem: \n"
									+ "An object of type '" + constraintViolation.getLeafBean().getClass().getSimpleName() 
									+ "' has a property '" + constraintViolation.getPropertyPath() + "' which has value '" 
									+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage() + "'");
						}
					} else {
						log.error("A problem occured during initialization", e);
					}
				}			
			}
		}
	}
	
	private void beforeRow() {
		product = new MobileProduct();
		product.setPrice(new Amounts());
		skipRow = false;
	}

	public static String getFixedAttributeName(String name) {
		return name.replace("  ", " ");
	}

	public void setProductGroupsFilter(List<MobileProductGroupEnum> groupsFilter) {
		this.groupsFilter = groupsFilter;
	}
}