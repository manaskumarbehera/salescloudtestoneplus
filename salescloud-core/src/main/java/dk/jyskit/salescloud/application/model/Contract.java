package dk.jyskit.salescloud.application.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.eclipse.persistence.annotations.PrivateOwned;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.waf.application.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity(name="Contract")
@Table(name = "contract")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@EqualsAndHashCode(callSuper=true, of={})
@RequiredArgsConstructor
@NoArgsConstructor
@Slf4j
public class Contract extends BaseEntity {
	public static final String CATEGORY_ID = "CATEGORY_ID";

	public final static SimpleDateFormat CONTRACT_DATE_FORMAT = new SimpleDateFormat("d/M yyyy");

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSINESS_AREA_ID")
	protected BusinessArea businessArea;

	@Nonnull
	@ManyToOne(optional = false)
	private SalespersonRole salesperson;
	
	@NonNull @NotNull
	@Column(length=100)
	protected String title;
	
	@NonNull @NotNull
	@Column(length=1000)
	protected String offerIntroText;

	/* Normally, you would expect bundles to be more "global" in scope, such as the bundles
	 * owned by campaigns, but bundles may also be defined in the scope of a contract. The
	 * existence of a bundle is not same as saying the customer has ordered it. An orderline
	 * referring to the bundle is required for that purpose. */
	@OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
	protected List<ProductBundle> productBundles = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = CATEGORY_ID)
	protected ContractCategory category;

	/* Note: sub-projects may only allow one campaign per contract, but the model supports multiple. */
	@ManyToMany
	@JoinTable(name = "contract_campaign", joinColumns = { @JoinColumn(name = "contract_id", referencedColumnName = "id") }, 
		inverseJoinColumns = { @JoinColumn(name = "campaign_id", referencedColumnName = "id") })
	protected List<Campaign> campaigns = new ArrayList<>();
		
	@AttributeOverrides({
	    @AttributeOverride(name="name", column= @Column(name="s_name")),
	    @AttributeOverride(name="position", column= @Column(name="s_position")),
	    @AttributeOverride(name="companyName", column= @Column(name="s_companyName")),
	    @AttributeOverride(name="companyId", column= @Column(name="s_companyId")),
	    @AttributeOverride(name="phone", column= @Column(name="s_phone")),
	    @AttributeOverride(name="email", column= @Column(name="s_email")),
	    @AttributeOverride(name="comment", column= @Column(name="s_comment")),
	    @AttributeOverride(name="address", column= @Column(name="s_address")),
	    @AttributeOverride(name="zipCode", column= @Column(name="s_zipCode")),
	    @AttributeOverride(name="city", column= @Column(name="s_city"))
	  })
	@Embedded
	protected BusinessEntity seller	= new BusinessEntity();
	
	@AttributeOverrides({
	    @AttributeOverride(name="name", column= @Column(name="c_name")),
	    @AttributeOverride(name="position", column= @Column(name="c_position")),
	    @AttributeOverride(name="companyName", column= @Column(name="c_companyName")),
	    @AttributeOverride(name="companyId", column= @Column(name="c_companyId")),
	    @AttributeOverride(name="phone", column= @Column(name="c_phone")),
	    @AttributeOverride(name="email", column= @Column(name="c_email")),
	    @AttributeOverride(name="comment", column= @Column(name="c_comment")),
	    @AttributeOverride(name="address", column= @Column(name="c_address")),
	    @AttributeOverride(name="zipCode", column= @Column(name="c_zipCode")),
	    @AttributeOverride(name="city", column= @Column(name="c_city"))
	  })
	@Embedded
	protected BusinessEntity customer	= new BusinessEntity();

	@OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
	protected List<OrderLine> orderLines = new ArrayList<OrderLine>();

//	public List<OrderLine> getOrderLines(Predicate<OrderLine> predicate) {
//		List<OrderLine> list = new ArrayList<>(orderLines.size());
//		for (OrderLine orderLine: orderLines) {
//
//		}
//		if (predicate == null || predicate.test())
//		return orderLines;
//	}

	// Se kommentar øverst i DiscountScheme
	// A contract may use more than one discount schemes
//	@ManyToMany
//	@JoinTable(name = "contract_discountscheme", joinColumns = { @JoinColumn(name = "CONTRACT_ID", referencedColumnName = "ID") }, inverseJoinColumns = { @JoinColumn(name = "DISCOUNT_SCHEME_ID", referencedColumnName = "ID") })
//	protected List<DiscountScheme> discountSchemes;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "contract_id", referencedColumnName = "id")
	@PrivateOwned
	private List<DiscountScheme> discountSchemes = new ArrayList<>();

	private Boolean deleted = Boolean.FALSE;   // Soft-delete
	
//	@Transient
//	protected Amounts contractAmountsBeforeDiscounts;

	private boolean useNonNetworkOrderLines;
	private boolean useNetworkOrderLines;

	// Contract settings
//	protected int vestingPeriodInYears = 1;
	
//	@OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
//	protected List<Discount> discounts;
	
	// --------------------------------
	
	public void addProductBundle(ProductBundle productBundle) {
		productBundles.add(productBundle);
		productBundle.setContract(this);
	}

	public void addCampaign(Campaign campaign) {
		campaigns.add(campaign);
	}

	public void addOrderLine(OrderLine orderLine) {
		orderLines.add(orderLine);
		orderLine.setContract(this);
	}
	
	public void addDiscountScheme(DiscountScheme discountScheme) {
		discountSchemes.add(discountScheme);
	}
	
	public void removeDiscountScheme(DiscountScheme discountScheme) {
		for (DiscountScheme ds : discountSchemes) {
			if (ds.equals(discountScheme)) {
				discountSchemes.remove(discountScheme);
				break;
			}
		}
	}
	
	// --------------------------------
	
	@Transient
	public String getName() {
		return title;  // this is a hack to make GUI for deleting a contract work
	}

	@Transient
	public DiscountScheme getDiscountScheme(Class<? extends DiscountScheme> clazz) {
		for (DiscountScheme discountScheme : discountSchemes) {
			if (clazz.isAssignableFrom(discountScheme.getClass())) {
				return discountScheme;
			}
		}
		return null;
	}
	
	@Transient
	public Amounts getPriceForProduct(Product p) {
		return p.getPrice();
	}

	@Transient
	public Amounts getAmounts(boolean afterAllDiscounts, ProductAndBundleFilter filter) {
		// TILSYNELADENDE REGNER VI FORKERT MED afterAllDiscounts == true. Gør istedet:
		// Amounts totals = getAmounts(false).subtract(getContractDiscounts()).subtract(getCampaignDiscounts());

		Amounts totals = new Amounts();
		for (OrderLine orderLine : orderLines) {
			totals.add(orderLine.getAmounts(afterAllDiscounts, afterAllDiscounts, filter));
		}
		return totals;
	}

	/**
	 * @return discounts caused by contract-related discount schemes
	 */
	@Transient
	public Amounts getContractDiscounts(ProductAndBundleFilter productAndBundleFilter) {
		Amounts totals = new Amounts();
		for (OrderLine orderLine : orderLines) {
			if (productAndBundleFilter.acceptOrderLine(orderLine)) {
				CoreSession.get().setPricingSubIndex(orderLine.getSubIndex());
				totals.add(orderLine.getContractDiscounts());
			}
		}
		return totals;
	}
	
	@Transient
	public Amounts getCampaignDiscounts(ProductAndBundleFilter productAndBundleFilter) {
		Amounts totals = new Amounts();
		for (OrderLine orderLine : orderLines) {
			if (productAndBundleFilter.acceptOrderLine(orderLine)) {
				totals.add(orderLine.getCampaignDiscounts());
			}
		}
		return totals;
	}
	
	@Transient
	public List<OrderLine> getProductOrderLines() {
		List<OrderLine> productRelatedOrderlines = new ArrayList<>();
		for (OrderLine orderLine : orderLines) {
			if (orderLine.getProduct() != null) {
				productRelatedOrderlines.add(orderLine);
			}
		}
		return productRelatedOrderlines;
	}
	
	@Transient
	public List<OrderLine> getOrderLines(Product product) {
		List<OrderLine> matchingOrderlines = new ArrayList<>();
		for (OrderLine orderLine : orderLines) {
			if (product.equals(orderLine.getProduct())) {
				matchingOrderlines.add(orderLine);
			}
		}
		return matchingOrderlines;
	}

	@Transient
	public List<OrderLine> getOrderLinesBySubIndex(Integer subIndex) {
		List<OrderLine> matchingOrderlines = new ArrayList<>();
		for (OrderLine orderLine : orderLines) {
			if (Objects.equals(orderLine.getSubIndex(), subIndex)) {
				matchingOrderlines.add(orderLine);
			}
		}
		return matchingOrderlines;
	}

	@Transient
	public OrderLine getOrderLineBySubIndex(Product product, Integer subIndex) {
		for (OrderLine orderLine : orderLines) {
			if (product.equals(orderLine.getProduct())) {
				if (Objects.equals(orderLine.getSubIndex(), subIndex)) {
					return orderLine;
				}
			}
		}
		return null;
	}

	@Transient
	public List<OrderLine> getOrderLines(ProductGroup group) {
		List<OrderLine> matchingOrderlines = new ArrayList<>();
		for (OrderLine orderLine : orderLines) {
			if ((orderLine.getProduct() != null) && group.equals(orderLine.getProduct().getProductGroup())) {
				matchingOrderlines.add(orderLine);
			}
		}
		return matchingOrderlines;
	}

	@Transient
	public List<OrderLine> getOrderLines(ProductGroup group, Integer subIndex) {
		List<OrderLine> matchingOrderlines = new ArrayList<>();
		for (OrderLine orderLine : orderLines) {
			if ((orderLine.getProduct() != null) && group.equals(orderLine.getProduct().getProductGroup()) && 
					Objects.equals(orderLine.getSubIndex(), subIndex)) {
				matchingOrderlines.add(orderLine);
			}
		}
		return matchingOrderlines;
	}
	
	@Transient
	public OrderLine getAnyOrderLine(ProductGroup group) {
		for (OrderLine orderLine : orderLines) {
			if ((orderLine.getProduct() != null) && group.equals(orderLine.getProduct().getProductGroup())) {
				return orderLine;
			}
		}
		return null;
	}
	
	@Transient
	public boolean hasOrderLineFor(ProductGroup group) {
		for (Product product : group.getProducts()) {
			if (hasOrderLineFor(product)) {
				return true;
			}
		}
		return false;
	}
	
	@Transient
	public boolean hasOrderLineFor(Product product) {
		for (OrderLine orderLine : getOrderLines(product)) {
			if (orderLine != null && orderLine.getTotalCount() > 0) {
				return true;
			}
		}
		return false;
	}
	
	@Transient
	public OrderLine getOrderLine(ProductBundle productBundle) {
		for (OrderLine orderLine : orderLines) {
			if (productBundle.equals(orderLine.getBundle())) {
				return orderLine;
			}
		}
		return null;
	}
	
	@Transient
	public List<OrderLine> getBundleOrderLines() {
		List<OrderLine> productRelatedOrderlines = new ArrayList<>();
		for (OrderLine orderLine : orderLines) {
			if (orderLine.getBundle() != null) {
				productRelatedOrderlines.add(orderLine);
			}
		}
		return productRelatedOrderlines;
	}

	@Transient
	public String getDocumentFooterText() {
		return CONTRACT_DATE_FORMAT.format(new Date()) + " - " + salesperson.getUser().getFirstName() + " " + salesperson.getUser().getLastName() + " - " + getTitle();
	}

	/**
	 * @param beforeDiscountAmounts The amounts we use as a base for the calculation (Brutto)
	 * @param matrix Discount matrix
	 * @param discountsForNonRecurring
	 * @return
	 */
	public Amounts getRabataftaleCampaignDiscounts(Amounts beforeDiscountAmounts, String matrix, Amounts discountsForNonRecurring, boolean useNetworkDiscountMatrix) {
		Amounts discounts = new Amounts(0, 0, 0);
		if (getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
			discounts = discountsForNonRecurring.clone();
			discounts.setRecurringFee(0);
			if (!StringUtils.isEmpty(matrix)) {
				try {
					long divisor = 100;
					String[] divisorAnMatrix = matrix.split("/");
					if (divisorAnMatrix.length == 2) {
						divisor = Long.valueOf(divisorAnMatrix[0]);
						matrix  = divisorAnMatrix[1];
					}
					String[] yearsInMatrix = matrix.split("#");
					
					// The following does not look very nice. The problem is that to calculate discount we need total amount before discount. These numbers are
					// calculated by getTem5ContractSumPrYear(), which in turn will also try to calculate campaign discount - using discountPoint, which has
					// not yet been calculated. It's ok to handle the problem this way. As long as discountPoint is null we are only interested in before-discount amounts.
					DiscountPoint discountPoint;
					if (useNetworkDiscountMatrix) {
						discountPoint = CoreSession.get().getDiscountPointNetwork();
					} else {
						discountPoint = CoreSession.get().getDiscountPointNonNetwork();
					}
					if ((discountPoint != null) && (discountPoint.getStep() >= 0)) {
						String discountsForYear = yearsInMatrix[discountPoint.getYearIndex()];
						String discountAsString = discountsForYear.split(",")[discountPoint.getStep()].trim();
						if (discountAsString.endsWith("%")) {
							long pct = Long.valueOf(discountAsString.substring(0, discountAsString.length()-1));
							return new Amounts(0, 0, beforeDiscountAmounts.getRecurringFee() * pct / (100 * divisor));
						} else {
							long discount = Long.valueOf(discountAsString);
							if (discount > beforeDiscountAmounts.getRecurringFee()) {
								discounts.setRecurringFee(beforeDiscountAmounts.getRecurringFee());
							} else {
								discounts.setRecurringFee(discount);
							}
						}
					}
				} catch (Exception e) {
					log.error("Could not parse matrix: " + matrix);
				}
			}
		}
		return discounts;
	}

	@Transient
	public ProductAndBundleFilter getAcceptAllProductFilter() {
		return new ProductAndBundleFilter() {
			@Override
			public boolean acceptProductBundle(ProductBundle productBundle) {
				return true;   // No flag to check?!
			}

			@Override
			public boolean acceptProduct(Product product) {
				return true;
			}

			@Override
			public boolean acceptOrderLine(OrderLine orderLine) {
				return true;
			}
		};
	}

	@Transient
	public Component getStatusPanel(String componentId) {
		return new EmptyPanel(componentId);
	}

	public void onOpen() {
	}
} 