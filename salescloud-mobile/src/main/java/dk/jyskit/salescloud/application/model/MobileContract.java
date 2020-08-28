package dk.jyskit.salescloud.application.model;

import com.github.rjeschke.txtmark.Processor;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.MobileSalescloudApplication;
import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.dao.*;
import dk.jyskit.salescloud.application.extensions.MobileProductRelationTypeProvider;
import dk.jyskit.salescloud.application.model.CountProductOrBundleComparator.Criteria;
import dk.jyskit.salescloud.application.model.PartnerData.RemarkAndStars;
import dk.jyskit.salescloud.application.model.PartnerData.TypeCountTextAmount;
import dk.jyskit.salescloud.application.pages.admin.sorting.SortingType;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.pages.contractsummary.ContractDetails;
import dk.jyskit.salescloud.application.pages.contractsummary.OfferReportDataSource;
import dk.jyskit.salescloud.application.pages.switchboard.types.BundleSelection;
import dk.jyskit.salescloud.application.services.supercontract.SuperContractService;
import dk.jyskit.salescloud.application.utils.MapUtils;
import dk.jyskit.salescloud.application.utils.QuarterUtils;
import dk.jyskit.waf.application.utils.exceptions.SystemException;
import dk.jyskit.waf.utils.encryption.SimpleStringCipher;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.wicket.Component;
import org.apache.wicket.util.collections.MicroMap;
import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.PrivateOwned;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static dk.jyskit.salescloud.application.model.FiberErhvervBundleData.*;
import static dk.jyskit.salescloud.application.model.MobileProductBundleEnum.HARDWARE_BUNDLE;
import static dk.jyskit.salescloud.application.model.MobileProductGroupEnum.*;
import static dk.jyskit.salescloud.application.model.OneXdslBundleData.MODE_NYSALG;
import static dk.jyskit.salescloud.application.model.PartnerData.VARIANT_GENERELT;
import static dk.jyskit.salescloud.application.model.PartnerData.VARIANT_TASTEBILAG;
import static dk.jyskit.salescloud.application.pages.contractsummary.ContractDetails.INGEN_RABATAFTALE;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class MobileContract extends Contract {
	// BusinessAreas.MOBILE_VOICE:
	private int emphasisOnFixedMonthlyPrice = 1;
	private int emphasisOnDeviceSecurity = 1;
	private int emphasisOnNewDevices = 1;
	private int emphasisOnTravelling = 1;
	private int emphasisOnInputMobility = 1;

	// BusinessAreas.SWITCHBOARD:
	private int emphasisOnOrdersByPhone = 1;
	private int emphasisOnMainNumberWellKnown = 1;
	private int emphasisOnGoodService = 1;
	private int emphasisOnReceptionist = 1;
	private int emphasisOnTransferCalls = 1;

	private int confidenceRating = 1;

	@Size(max = 100)
	@Column(length = 100)
	private String extraProduct1Text;

	private long extraProduct1OneTimeFee;
	private long extraProduct1InstallationFee;
	private long extraProduct1RecurringFee;

	@Size(max = 100)
	@Column(length = 100)
	private String extraProduct2Text;

	private long extraProduct2OneTimeFee;
	private long extraProduct2InstallationFee;
	private long extraProduct2RecurringFee;

	@Size(max = 100)
	@Column(length = 100)
	private String extraProduct3Text;

	private long extraProduct3OneTimeFee;
	private long extraProduct3InstallationFee;
	private long extraProduct3RecurringFee;

	@Temporal(TemporalType.DATE)
	private Date installationDate;

	@Temporal(TemporalType.DATE)
	private Date contractStartDate;

	@Temporal(TemporalType.DATE)
	private Date contractStartDateNetwork;

	@Index
	@Temporal(TemporalType.DATE)
	private Date statusChangedDate;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	protected ContractStatusEnum status = ContractStatusEnum.OPEN;
	
	@Size(max = 1000)
	@Column(length = 1000)
	protected String productionOutputText;

	private Integer contractLength;
	private Integer contractLengthNetwork;

	private MobileContractType contractType = MobileContractType.STANDARD;
	private MobileContractMode contractMode = MobileContractMode.NEW_SALE;

	private InstallationType installationType = InstallationType.TDC_ON_SITE;

//	@OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<MobileProductBundle> bundles = new ArrayList<>();

	@OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
	@PrivateOwned
	@CascadeOnDelete
	private List<Subscription> subscriptions = new ArrayList<>();
	
	private Long salesforceNo;

	// --- Partner settings ---

	private long supportNoOfUsers;
	private long supportPricePrUser;	// Amount multiplied with 100
	private long supportRecurringFee;	// Amount multiplied with 100
	private int supportMonths = 12;
	private long rateNonRecurringFee;	// Amount multiplied with 100
	private int rateMonths = 12;
	private int upFrontPayment = 0;		// Amount multiplied with 100
	private boolean pbs = true;
	private boolean showProvision;

	private long installationFeeDiscount;	// Amount multiplied with 100. Amount is 0 or positive
	private long oneTimeFeeDiscount;	// Amount multiplied with 100. Amount is 0 or positive

	@Column(length = 20)
	@Size(max = 20)
	private String tmNumber;

	@Temporal(TemporalType.DATE)
	private Date tmNumberDate;

	@Column(length = 1024)
	@Size(max = 1024)
	private String variableInstallationFees; // variableInstallationFeeMap.toString()

	@Column(length = 1024)
	@Size(max = 1024)
	private String variableRecurringFees; // variableRecurringFeeMap.toString()

	@Column(length = 1024)
	@Size(max = 1024)
	private String variableCategories; // variableCategoryMap.toString()

	@Column(length = 2048)
	@Size(max = 2048)
	private String variableProductNames; // variableProductNameMap.toString()

	private Segment segment;

	// --- Partner settings (end of) ---

	// @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL,
	// orphanRemoval = true)
	// @PrivateOwned
	// @CascadeOnDelete
	// private List<ContractSubRecord> subRecords = new ArrayList<>();

	// --- Fordelsaftale ---

	private long contractSumMobile;
	private long contractSumFastnet;
	private long contractSumBroadband;

	// --- TDC Erhverv Rabataftale ---

	private long additionToKontraktsum;
	private long additionToKontraktsumNetwork;

	// --- Fordelsaftale (end of) ---

	// --- Wi-Fi ---

	@Column(length = 80)
	@Size(max = 80)
	private String orderHandlingRemarks;

	@Column(length = 500)
	@Size(min = 20, max = 500)
	private String technicalSolution;

	@Column(length = 100)
	@Size(max = 100)
	private String technicalContactName;

	@Column(length = 50)
	@Size(max = 50)
	private String technicalContactPhone;

	@Column(length = 100)
	@Size(max = 100)
	private String technicalContactEmail;

	private boolean buildingPlanAvailable;

	private boolean newAccount;

	@Enumerated(EnumType.STRING)
	@Column(length = 30)
	private InvoicingTypeEnum invoicingType;

	@Column(length = 200)
	@Size(max = 200)
	private String invoicingInfo;

	@Column(length = 50)
	@Size(max = 50)
	private String accountNo;

	@Column(length = 512)
	@Size(max = 512)
	private String orderConfirmationEmailAdresses;

	// --- Wi-Fi --- (end of)

	// --- Office --- (start)
	
	@Column(length = 50)
	@Size(max = 50)
	private String eFakturaEmail;
	
//	@Column(length = 20)
//	@Size(max = 20)
//	private String configurationUserName;
	
	@Column(length = 10)
	@Size(max = 10)
	private String configurationPassword;
	
//	private boolean configurationAccepted;
	
	// --- Office --- (end of)

	// --- One+ --- (start)

	private boolean poolsMode;

	private Long installationTypeBusinessEntityId;
	private Long installationTypeUserProfilesEntityId;
	private Long serviceLevelEntityId;

	private Integer additionalUserChanges;
	private Boolean callFlowChanges;
	private Boolean existingFlexConnectSubscriptions;

	// --- One+ --- (end of)

	@Column(length = 1400)
	@Size(max = 1400)
	private String adslBundlesJson = "";

	@Column(length = 1024)
	@Size(max = 1024)
	private String wiFiBundlesJson = "";

	@Column(length = 1400, name="FIBERBUNDLESJSON")
	@Size(max = 1400)
	private String fiberErhvervBundlesJson = "";

	@Column(length = 1400, name="FIBERBUNDLESPLUSJSON")
	@Size(max = 1400)
	private String fiberErhvervPlusBundlesJson = "";

	@Column(length = 1400)
	@Size(max = 1400)
	private String locationBundlesJson = "";

	private int countExistingSubscriptions;
	private int countNewSubscriptions;

	public MobileContractMode getContractMode() {
		if (contractMode == null) {
			contractMode = MobileContractMode.NEW_SALE;
		}
		return contractMode;
	}

	@Transient
	public List<XdslBundleData> getXdslBundles() {
		Gson gson = new Gson();
		List<XdslBundleData> xdslBundles = gson.fromJson(adslBundlesJson, new TypeToken<List<XdslBundleData>>() {
		}.getType());
		if (xdslBundles == null) {
			return new ArrayList<XdslBundleData>();
		}
		return xdslBundles;
	}

	@Transient
	public XdslBundleData getXdslBundle(int i) {
		Gson gson = new Gson();
		List<XdslBundleData> xdslBundles = gson.fromJson(adslBundlesJson, new TypeToken<List<XdslBundleData>>() {
		}.getType());
		if (xdslBundles == null) {
			xdslBundles = new ArrayList<XdslBundleData>();
		}

		while (xdslBundles.size() <= i) {
			xdslBundles.add(new XdslBundleData());
			setXdslBundles(xdslBundles);
		}
		return xdslBundles.get(i);
	}

	public void setXdslBundles(List<XdslBundleData> xdslBundles) {
		Gson gson = new Gson();
		adslBundlesJson = gson.toJson(xdslBundles, new TypeToken<List<XdslBundleData>>() {
		}.getType());
	}

	@Transient
	public List<OneXdslBundleData> getOneXdslBundles() {
		Gson gson = new Gson();
		List<OneXdslBundleData> xdslBundles = gson.fromJson(adslBundlesJson, new TypeToken<List<OneXdslBundleData>>() {
		}.getType());
		if (xdslBundles == null) {
			return new ArrayList<OneXdslBundleData>();
		}
		return xdslBundles;
	}

	@Transient
	public OneXdslBundleData getOneXdslBundle(int i) {
		Gson gson = new Gson();
		List<OneXdslBundleData> xdslBundles = gson.fromJson(adslBundlesJson, new TypeToken<List<OneXdslBundleData>>() {
		}.getType());
		if (xdslBundles == null) {
			xdslBundles = new ArrayList<OneXdslBundleData>();
		}

		while (xdslBundles.size() <= i) {
			xdslBundles.add(new OneXdslBundleData());
			setOneXdslBundles(xdslBundles);
		}
		return xdslBundles.get(i);
	}

	public void setOneXdslBundles(List<OneXdslBundleData> xdslBundles) {
		Gson gson = new Gson();
		adslBundlesJson = gson.toJson(xdslBundles, new TypeToken<List<OneXdslBundleData>>() {
		}.getType());
	}

	@Transient
	public List<WiFiBundleIds> getWiFiBundles() {
		Gson gson = new Gson();
		String s = wiFiBundlesJson
				.replace("\"a\":", "\"address\":")
				.replace("\"b\":", "\"contactName\":")
				.replace("\"c\":", "\"contactPhone\":")
				.replace("\"d\":", "\"areaSizeEntityId\":")
				.replace("\"e\":", "\"accessPointCount\":")
				.replace("\"f\":", "\"accessPointEntityId\":")
				.replace("\"g\":", "\"serviceLevelEntityId\":")
				.replace("\"h\":", "\"switchEntityId\":")
				.replace("\"i\":", "\"siteSurveyEntityId\":")
				.replace("\"j\":", "\"newAccess\":")
				.replace("\"k\":", "\"lidId\":");
		List<WiFiBundleIds> wifiBundles = gson.fromJson(s, new TypeToken<List<WiFiBundleIds>>() {
		}.getType());
		if (wifiBundles == null) {
			return new ArrayList<WiFiBundleIds>();
		}
		return wifiBundles;
	}

	public void setWiFiBundles(List<WiFiBundleIds> wifiBundles) {
		Gson gson = new Gson();
		wiFiBundlesJson = gson.toJson(wifiBundles, new TypeToken<List<WiFiBundleIds>>() {
		}.getType());
		wiFiBundlesJson = wiFiBundlesJson
				.replace("\"address\":", "\"a\":")
				.replace("\"contactName\":", "\"b\":")
				.replace("\"contactPhone\":", "\"c\":")
				.replace("\"areaSizeEntityId\":", "\"d\":")
				.replace("\"accessPointCount\":", "\"e\":")
				.replace("\"accessPointEntityId\":", "\"f\":")
				.replace("\"serviceLevelEntityId\":", "\"g\":")
				.replace("\"switchEntityId\":", "\"h\":")
				.replace("\"siteSurveyEntityId\":", "\"i\":")
				.replace("\"newAccess\":", "\"j\":")
				.replace("\"lidId\":", "\"k\":");
	}

	@Transient
	public List<FiberErhvervPlusBundleData> getFiberErhvervPlusBundles() {
		Gson gson = getFiberErhvervPlusBundlesGson();

		List<FiberErhvervPlusBundleData> fiberErhvervPlusBundles = null;
		try {
			fiberErhvervPlusBundles = gson.fromJson(fiberErhvervPlusBundlesJson, new TypeToken<List<FiberErhvervPlusBundleData>>() {}.getType());
		} catch (JsonSyntaxException e) {
			if (e.getMessage().indexOf("Expected a long but was BOOLEAN") != -1) {
				fiberErhvervPlusBundles = gson.fromJson(
						fiberErhvervPlusBundlesJson
								.replace("\"g\":false", "\"g\":99999")
								.replace("\"g\":true", "\"g\":2152480"),
						new TypeToken<List<FiberErhvervPlusBundleData>>() {}.getType());
			}
		}
		if (fiberErhvervPlusBundles == null) {
			return new ArrayList<FiberErhvervPlusBundleData>();
		}
		return fiberErhvervPlusBundles;
	}

	@Transient
	public FiberErhvervPlusBundleData getFiberErhvervPlusBundle(int i) {
		Gson gson = getFiberErhvervPlusBundlesGson();

		List<FiberErhvervPlusBundleData> bundles = null;
		try {
			bundles = gson.fromJson(fiberErhvervPlusBundlesJson, new TypeToken<List<FiberErhvervPlusBundleData>>() {}.getType());
		} catch (JsonSyntaxException e) {
			if (e.getMessage().indexOf("Expected a long but was BOOLEAN") != -1) {
				bundles = gson.fromJson(
						fiberErhvervPlusBundlesJson
								.replace("\"g\":false", "\"g\":99999")
								.replace("\"g\":true", "\"g\":2152480"),
						new TypeToken<List<FiberErhvervPlusBundleData>>() {}.getType());
			}
		}
		if (bundles == null) {
			bundles = new ArrayList<FiberErhvervPlusBundleData>();
		}
		while (bundles.size() <= i) {
			bundles.add(new FiberErhvervPlusBundleData());
			setFiberErhvervPlusBundles(bundles);
		}
		return bundles.get(i);
	}

	public void setFiberErhvervPlusBundles(List<FiberErhvervPlusBundleData> fiberErhvervPlusBundles) {
		Gson gson = getFiberErhvervPlusBundlesGson();
		fiberErhvervPlusBundlesJson = gson.toJson(fiberErhvervPlusBundles, new TypeToken<List<FiberErhvervPlusBundleData>>() {
		}.getType());
	}
	
	private Gson getFiberErhvervPlusBundlesGson() {
		GsonBuilder builder = new GsonBuilder();
//		builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
		return builder.create();
	}

	// --------------------------------

	@Transient
	public boolean isTdcInstallation() {
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			if (installationTypeBusinessEntityId == null) {
				return false;
			}
			InstallationType type = InstallationType.getByProduct(MobileProductDao.lookup().findById(installationTypeBusinessEntityId));
			return installationTypeBusinessEntityId == null
					? false
					: (InstallationType.TDC_ON_SITE.equals(type) ||
						InstallationType.TDC_REMOTE.equals(type) ||
						InstallationType.TDC_ON_SITE_REMOTE.equals(type)
					);
		} else {
			return !InstallationType.PARTNER.equals(getInstallationType());
		}
	}

	@Transient
	public String getInstallationTypeBusiness() {
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			if (installationTypeBusinessEntityId == null) {
				return "";
			}
			return MobileProductDao.lookup().findById(installationTypeBusinessEntityId).getPublicName();
		} else {
			return getInstallationType().getText();
		}
	}

	@Transient
	public boolean isTdcInstallationUserProfiles() {
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			if (installationTypeUserProfilesEntityId == null) {
				return false;
			}
			InstallationType type = InstallationType.getByProduct(MobileProductDao.lookup().findById(installationTypeUserProfilesEntityId));
			return installationTypeUserProfilesEntityId == null
					? false
					: (InstallationType.TDC_ON_SITE.equals(type) ||
					InstallationType.TDC_REMOTE.equals(type) ||
					InstallationType.TDC_ON_SITE_REMOTE.equals(type)
			);
		} else {
			return !InstallationType.PARTNER.equals(getInstallationType());
		}
	}

	@Transient
	public String getInstallationTypeUserProfiles() {
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			if (installationTypeUserProfilesEntityId == null) {
				return "";
			}
			return MobileProductDao.lookup().findById(installationTypeUserProfilesEntityId).getPublicName();
		} else {
			return getInstallationType().getText();
		}
	}

	@Transient
	public boolean isOyoInstallation() {
		if (installationTypeBusinessEntityId == null) {
			return false;
		}
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			return installationTypeBusinessEntityId == null
					? false
					: InstallationType.NONE.equals(InstallationType.getByProduct(MobileProductDao.lookup().findById(installationTypeBusinessEntityId)));
		} else {
			return false;
		}
	}

	@Transient
	public boolean isOyoInstallationUserProfiles() {
		if (installationTypeUserProfilesEntityId == null) {
			return false;
		}
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			return installationTypeUserProfilesEntityId == null
					? false
					: InstallationType.NONE.equals(InstallationType.getByProduct(MobileProductDao.lookup().findById(installationTypeUserProfilesEntityId)));
		} else {
			return false;
		}
	}

	@Transient
	public boolean isPartnerInstallation() {
		if (installationTypeBusinessEntityId == null) {
			return false;
		}
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			return installationTypeBusinessEntityId == null
					? false
					: InstallationType.PARTNER.equals(InstallationType.getByProduct(MobileProductDao.lookup().findById(installationTypeBusinessEntityId)));
		} else {
			return InstallationType.PARTNER.equals(getInstallationType());
		}
	}

	@Transient
	public boolean isPartnerInstallationUserProfiles() {
		if (installationTypeUserProfilesEntityId == null) {
			return false;
		}
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			return installationTypeUserProfilesEntityId == null
					? false
					: InstallationType.PARTNER.equals(InstallationType.getByProduct(MobileProductDao.lookup().findById(installationTypeUserProfilesEntityId)));
		}
		throw new SystemException("This is wrong!");
	}

	@Transient
	public List<LocationBundleData> getLocationBundles() {
		Gson gson = getLocationBundlesGson();

		List<LocationBundleData> bundles = null;
		try {
			bundles = gson.fromJson(locationBundlesJson, new TypeToken<List<LocationBundleData>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			log.warn("Json is bad!", e);
		}
		if (bundles == null) {
			return new ArrayList<>();
		}
		return bundles;
	}

	@Transient
	public LocationBundleData getLocationBundle(int i) {
		Gson gson = getLocationBundlesGson();

		List<LocationBundleData> bundles = null;
		try {
			bundles = gson.fromJson(locationBundlesJson, new TypeToken<List<LocationBundleData>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			log.warn("Json is bad!", e);
		}
		if (bundles == null) {
			bundles = new ArrayList<>();
		}

		while (bundles.size() <= i) {
			bundles.add(new LocationBundleData());
		}
		return bundles.get(i);
	}

	public void setLocationBundles(List<LocationBundleData> locationBundles) {
		Gson gson = getLocationBundlesGson();
		locationBundlesJson = gson.toJson(locationBundles, new TypeToken<List<LocationBundleData>>() {
		}.getType());
	}

	private Gson getLocationBundlesGson() {
		return new GsonBuilder().create();
	}

	public void addLocation(AccessTypeEnum accessType) {
		LocationBundleData location = new LocationBundleData();
		location.setAccessType(accessType.getId());
		List<LocationBundleData> locationBundles = getLocationBundles();
		locationBundles.add(location);
		setLocationBundles(locationBundles);

		List<OneXdslBundleData> oneXdslBundles = getOneXdslBundles();
		oneXdslBundles.add(new OneXdslBundleData());
		setOneXdslBundles(oneXdslBundles);

		List<FiberErhvervBundleData> fiberErhvervBundles = getFiberErhvervBundles();
		fiberErhvervBundles.add(new FiberErhvervBundleData());
		setFiberErhvervBundles(fiberErhvervBundles);

		List<FiberErhvervPlusBundleData> fiberErhvervPlusBundles = getFiberErhvervPlusBundles();
		fiberErhvervPlusBundles.add(new FiberErhvervPlusBundleData());
		setFiberErhvervPlusBundles(fiberErhvervPlusBundles);
	}

	public void removeLocation(int index) {
		List<LocationBundleData> bundles = getLocationBundles();
		bundles.remove(index);
		setLocationBundles(bundles);

		List<OneXdslBundleData> oneXdslBundles = getOneXdslBundles();
		oneXdslBundles.remove(index);
		setOneXdslBundles(oneXdslBundles);

		List<FiberErhvervBundleData> fiberErhvervBundles = getFiberErhvervBundles();
		fiberErhvervBundles.remove(index);
		setFiberErhvervBundles(fiberErhvervBundles);

		List<FiberErhvervPlusBundleData> fiberErhvervPlusBundles = getFiberErhvervPlusBundles();
		fiberErhvervPlusBundles.remove(index);
		setFiberErhvervPlusBundles(fiberErhvervPlusBundles);
	}

	@Transient
	public List<FiberErhvervBundleData> getFiberErhvervBundles() {
		Gson gson = getFiberErhvervBundlesGson();

		List<FiberErhvervBundleData> fiberBundles = null;
		try {
			fiberBundles = gson.fromJson(fiberErhvervBundlesJson, new TypeToken<List<FiberErhvervBundleData>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			log.warn("Json is bad!", e);
		}
		if (fiberBundles == null) {
			return new ArrayList<>();
		}
		return fiberBundles;
	}

	@Transient
	public FiberErhvervBundleData getFiberErhvervBundle(int i) {
		Gson gson = getFiberErhvervPlusBundlesGson();

		List<FiberErhvervBundleData> bundles = null;
		try {
			fiberErhvervBundlesJson = fiberErhvervBundlesJson.replace("\"a\":", "\"ar\":");
			bundles = gson.fromJson(fiberErhvervBundlesJson, new TypeToken<List<FiberErhvervBundleData>>() {}.getType());
		} catch (JsonSyntaxException e) {
			log.error("Could not convert json", e);
		}
		if (bundles == null) {
			bundles = new ArrayList<FiberErhvervBundleData>();
		}

		while (bundles.size() <= i) {
			bundles.add(new FiberErhvervBundleData());
			setFiberErhvervBundles(bundles);
		}
		return bundles.get(i);
	}

	public void setFiberErhvervBundles(List<FiberErhvervBundleData> fiberErhvervBundles) {
		Gson gson = getFiberErhvervBundlesGson();
		fiberErhvervBundlesJson = gson.toJson(fiberErhvervBundles, new TypeToken<List<FiberErhvervBundleData>>() {
		}.getType());
	}
	
	private Gson getFiberErhvervBundlesGson() {
		GsonBuilder builder = new GsonBuilder();
//		builder.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
		return builder.create();
	}

	// --------------------------------

//	@Transient
//	public List<UserProfileBundleData> getUserProfileBundles() {
//		Gson gson = getUserProfileBundlesGson();
//
//		List<UserProfileBundleData> userProfileBundles = null;
//		try {
//			userProfileBundles = gson.fromJson(userProfilesJson, new TypeToken<List<UserProfileBundleData>>() {
//			}.getType());
//		} catch (JsonSyntaxException e) {
//			log.warn("Json is bad!", e);
//		}
//		if (userProfileBundles == null) {
//			return new ArrayList<UserProfileBundleData>();
//		}
//		return userProfileBundles;
//	}
//
//	public void setUserProfileBundles(List<UserProfileBundleData> userProfileBundles) {
//		Gson gson = getUserProfileBundlesGson();
//		userProfilesJson = gson.toJson(userProfileBundles, new TypeToken<List<UserProfileBundleData>>() {
//		}.getType());
//	}
//
//	private Gson getUserProfileBundlesGson() {
//		GsonBuilder builder = new GsonBuilder();
//		return builder.create();
//	}

	// --------------------------------

	public void addSubscription(Subscription subscription) {
		subscriptions.add(subscription);
	}

	public MobileProductBundle addBundle(MobileProductBundle productBundle) {
		productBundles.add(productBundle);
		productBundle.setContract(this);
		return productBundle;
	}

	// --------------------------------

	/**
	 * Following a change in requested number of bundles, the list of
	 * subscription must be updated. This can fail, though. If the number of
	 * "non-clean" subscriptions using a bundle exceeds the updated count for
	 * the bundle, the update is considered invalid. If the update is valid, the
	 * list of subscription is adjusted accordingly. Note: the list of
	 * orderlines is not adjusted. That is the responsibility of the calling
	 * code.
	 * 
	 * return flag indicating success or failure
	 */
	public boolean adjustSubscriptions(Map<MobileProductBundle, BundleCount> bundleToCountMap,
			boolean standardBundles) {
		Map<MobileProductBundle, MutableInt> dirtyCounts = new HashMap<>();
		for (Subscription subscription : subscriptions) {
			if (subscription.isDirty()) {
				MutableInt count = dirtyCounts.get(subscription.getBundle());
				if (count == null) {
					dirtyCounts.put(subscription.getBundle(), new MutableInt(1));
				} else {
					count.increment();
				}
			}
		}
		for (MobileProductBundle bundle : dirtyCounts.keySet()) {
			MutableInt dirtyCount = dirtyCounts.get(bundle);
			Integer totalCount = bundleToCountMap.get(bundle).getCountExisting()
					+ bundleToCountMap.get(bundle).getCountNew();
			if (totalCount != null) {
				if (dirtyCount.intValue() > totalCount) {
					return false;
				}
			}
		}

		// Update is fine, so adjust subscriptions.
		Map<MobileProductBundle, MutableInt> currentCounts = new HashMap<>();
		for (Subscription subscription : subscriptions) {
			MutableInt count = currentCounts.get(subscription.getBundle());
			if (count == null) {
				currentCounts.put(subscription.getBundle(), new MutableInt(1));
			} else {
				count.increment();
			}
		}

		countExistingSubscriptions = 0;
		countNewSubscriptions = 0;
		for (MobileProductBundle bundle : bundleToCountMap.keySet()) {
			countExistingSubscriptions += bundleToCountMap.get(bundle).getCountExisting();
			countNewSubscriptions += bundleToCountMap.get(bundle).getCountNew();

			int currentCount = (currentCounts.get(bundle) == null) ? 0 : currentCounts.get(bundle).intValue();
			int difference = Math.max(0, bundleToCountMap.get(bundle).getCountNew()) - currentCount;
			if (difference > 0) {
				// add some
				for (int i = 0; i < difference; i++) {
					createSubscription(bundle);
				}
			} else if (difference < 0) {
				// delete some
				Iterator<Subscription> iter = subscriptions.iterator();
				while (difference != 0) {
					Subscription subscription = iter.next();

					if (subscription.getBundle().equals(bundle) && !subscription.isDirty()) {
						// Remove all references to subscription. If we don't do
						// this the cache will be
						// inconsistent.
						subscription.setContract(null);
						subscription.setBundle(null);
						Lookup.lookup(SubscriptionDao.class).delete(subscription);
						iter.remove();
						difference++;
					}
				}
			}
		}

		// This extra check is necessary
		Iterator<Subscription> iter = subscriptions.iterator();
		while (iter.hasNext()) {
			Subscription subscription = iter.next();
			if (subscription.getBundle() != null) {
				if (!bundleToCountMap.keySet().contains(subscription.getBundle())) {
// Ok to get rid of this?????
//					if (((MobileProductBundle) subscription.getBundle()).isStandardBundle() == standardBundles) {
						iter.remove();
//					}
				}
			}
		}
		return true;
	}

	/**
	 * Adjust order lines on the contract, relating to bundles.
	 * 
	 * @param bundleToCountMap
	 */
	public void adjustOrderLinesForBundles(Map<MobileProductBundle, BundleCount> bundleToCountMap, MobileProductBundleEnum bundleType) {
		Iterator<OrderLine> iter = orderLines.iterator();
		while (iter.hasNext()) {
			OrderLine orderLine = iter.next();
			if (orderLine.getBundle() != null
					&& (((MobileProductBundle) orderLine.getBundle()).getBundleType().equals(bundleType))
			) {
				iter.remove();
			}
		}

		for (MobileProductBundle productBundle : bundleToCountMap.keySet()) {
			BundleCount bundleCount = bundleToCountMap.get(productBundle);
			addOrderLine(new OrderLine(productBundle, bundleCount.getSubIndex(), bundleCount.getCountNew(),
					bundleCount.getCountExisting()));
		}
	}

	/**
	 * Adjust order lines on the contract, relating to products.
	 * 
	 * @param productToCountMap
	 */
	public void adjustOrderLinesForProducts(ProductGroup productGroup, Map<Product, List<CountAndInstallation>> productToCountMap, Integer subIndex) {
		Iterator<OrderLine> iter = orderLines.iterator();
		while (iter.hasNext()) {
			OrderLine orderLine = iter.next();
			if ((orderLine.getProduct() != null) && (productGroup.equals(orderLine.getProduct().getProductGroup()))) {
				if ((subIndex == null) || (subIndex.equals(orderLine.getSubIndex()))) {
					iter.remove();
				}
			}
		}

		for (Product product : productToCountMap.keySet()) {
			if (!product.getProductGroup().equals(productGroup)) {
				continue;
			}
			List<CountAndInstallation> countAndInstallations = productToCountMap.get(product);
			for (CountAndInstallation countAndInstallation : countAndInstallations) {
				if ((subIndex != null) && (!subIndex.equals(countAndInstallation.getSubIndex()))) {
					log.info("Skipped: " + countAndInstallation);
					continue;
				}

				boolean customFlag = false;
				if (countAndInstallation.getCountNew() == null) {
					customFlag = true; // All selected
					countAndInstallation.setCountNew(Constants.ORDERLINE_SUBSCRIBERS_SPECIAL_COUNT);
				}
				OrderLine orderLine = new OrderLine(product, countAndInstallation.getSubIndex(), countAndInstallation.getCountNew(), countAndInstallation.getCountExisting());
				orderLine.setCustomFlag(customFlag);
				if (!countAndInstallation.isInstallationSelected()) {
					orderLine.setCustomFlag1(true); // Partner installation!
				}
				addOrderLine(orderLine);

				// TODO: The following is a BIG problem, if related products are in a different group!!!!
				if (countAndInstallation.getCountNew() > 0) {
					if (countAndInstallation.isInstallationSelected()) {
						// // Means TDC installation
						for (ProductRelation productRelation : businessArea.getProductRelations(product, true, false,
								MobileProductRelationTypeProvider.ADD_ORDERLINES_0)) {
							for (int i = 1; i < productRelation.getProducts().size(); i++) {
								Product installationProduct = productRelation.getProducts().get(i);
								addOrderLine(new OrderLine(installationProduct, 0, 0, 0));
							}
						}
						for (ProductRelation productRelation : businessArea.getProductRelations(product, true, false,
								MobileProductRelationTypeProvider.ADD_ORDERLINES_1)) {
							for (int i = 1; i < productRelation.getProducts().size(); i++) {
								Product installationProduct = productRelation.getProducts().get(i);
								addOrderLine(new OrderLine(installationProduct, 0, 1, 0));
							}
						}
						for (ProductRelation productRelation : businessArea.getProductRelations(product, true, false,
								MobileProductRelationTypeProvider.ADD_ORDERLINES_N)) {
							for (int i = 1; i < productRelation.getProducts().size(); i++) {
								Product installationProduct = productRelation.getProducts().get(i);
								addOrderLine(new OrderLine(installationProduct, 0, countAndInstallation.getCountNew(), 0));
							}
						}
						// for (ProductRelation productRelation :
						// businessArea.getProductRelations(product, true, false,
						// MobileProductRelationTypeProvider.TDC_INSTALLATION)) {
						// for (int i = 1; i < productRelation.getProducts().size();
						// i++) {
						// Product installationProduct =
						// productRelation.getProducts().get(i);
						// addOrderLine(new OrderLine(installationProduct,
						// installationProduct.getMaxCount()));
						// }
						// }
					}
				}
			}
		}
	}

	public void adjustOrderLineForProduct(Product product, Integer subIndex, int countNew, int countExisting) {
		Iterator<OrderLine> iter = orderLines.iterator();
		while (iter.hasNext()) {
			OrderLine orderLine = iter.next();
			if (product.equals(orderLine.getProduct())) {
				iter.remove();
			}
		}
		addOrderLine(new OrderLine(product, subIndex, countNew, countExisting));
	}

	public void adjustOrderLineForExtraProducts() {
		Iterator<OrderLine> iter = orderLines.iterator();
		while (iter.hasNext()) {
			OrderLine orderLine = iter.next();
			if ((orderLine.getProduct() != null)
					&& (orderLine.getProduct().getProductId().startsWith(MobileProduct.PRODUCT_EXTRA_PREFIX))) {
				iter.remove();
			}
		}
		for (int i = 1; i <= 3; i++) {
			Product product = businessArea.getProductByProductId(MobileProduct.PRODUCT_EXTRA_PREFIX + i);
			if (!StringUtils.isEmpty(product.getPublicName())) {
				addOrderLine(new OrderLine(product, 0, 1, 0));
			}
		}
	}

	public void adjustOrderLinesForBundles(MobileContract contract, ArrayList<BundleSelection> bundleCountList,
			MobileProductBundleEnum bundleType) {
		Iterator<OrderLine> iter = orderLines.iterator();
		while (iter.hasNext()) {
			OrderLine orderLine = iter.next();
			if (orderLine.getBundle() != null
					&& (((MobileProductBundle) orderLine.getBundle()).getBundleType().equals(bundleType))) {
				iter.remove();
			}
		}

		for (BundleSelection bundleCount : bundleCountList) {
			if (bundleCount.isSelected() && (bundleCount.getBundle() != null)) {
				int countNew = 0;
				int countExisting = 0;
				if (MobileContractMode.RENEGOTIATION.equals(contract.getContractMode())
						|| MobileContractMode.CONVERSION.equals(contract.getContractMode())
						|| MobileContractMode.CONVERSION_1_TO_1.equals(contract.getContractMode())) {
					countExisting = 1;
				} else {
					countNew = 1;
				}
				contract.addOrderLine(new OrderLine(bundleCount.getBundle(), 0, countNew, countExisting)); // selected => 1
			}
		}
	}

	/**
	 * @param group
	 * @return
	 */
	@Transient
	private Amounts getProductAmountsByProductGroup(MobileProductGroupEnum group) {
		Amounts amounts = new Amounts();
		for (OrderLine orderLine : orderLines) {
			MobileSession.get().setPricingSubIndex(orderLine.getSubIndex());
			if (orderLine.getProduct() != null) {
				if (((MobileProduct) orderLine.getProduct()).isInGroup(group)) {
					// if (orderLine.getProduct().getPublicName().indexOf("DSL")
					// != -1) {
					// log.info("Adding for " +
					// orderLine.getProduct().getPublicName() + ": " +
					// orderLine.getDeferredTotalCount() + " / " +
					// orderLine.getProduct().getAmounts(orderLine.getDeferredTotalCount(),
					// false, false, this));
					// }
					Amounts a = orderLine.getProduct().getAmounts(orderLine.getDeferredCount(), false, false, this);
					if (a.getRecurringFee() > 0) {
						log.info(group.getDisplayText() + " - " + orderLine.getProduct().getPublicName() + " - " + a.getRecurringFeeFormatted());
					}
					amounts.add(a);
				}
			} else if (orderLine.getBundle() != null) {
				for (BundleProductRelation relation : orderLine.getBundle().getProducts()) {
					// !relation.getProductAccessType().equals(ProductAccessType.SEPARATE_COUNT) &&    <- SKAL DETTE TILFØJES???
					if ((!relation.getProductAccessType().equals(ProductAccessType.SEPARATE_COUNT)) && relation.isAddProductPrice() && (relation.getProduct() != null)
							&& ((MobileProduct) relation.getProduct()).isInGroup(group)) {
						// if
						// (orderLine.getBundle().getPublicName().indexOf("DSL")
						// != -1) {
						// log.info("Adding for " +
						// orderLine.getBundle().getPublicName() + ": " +
						// orderLine.getDeferredTotalCount() + " / " +
						// relation.getProduct().getAmounts(orderLine.getDeferredTotalCount(),
						// false, false, this));
						// }
						Amounts a = relation.getProduct().getAmounts(orderLine.getDeferredCount(), false, false, this);
						if (a.getRecurringFee() > 0) {
							log.info(group.getDisplayText() + " - " + orderLine.getBundle().getPublicName() + " - " + a.getRecurringFeeFormatted());
						}
						amounts.add(a);
					}
				}
			}
		}
		return handleInstallationType(amounts, group);
	}

	public CampaignProductRelation getCampaignProductRelation(Product product) {
		if (getCampaigns().size() > 0) {
			Campaign campaign = getCampaigns().get(0);
			for (CampaignProductRelation campaignProductRelation : campaign.getCampaignProducts()) {
				if (product.equals(campaignProductRelation.getProduct())) {
					return campaignProductRelation;
				}
			}
		}
		return null;
	}

	// /**
	// * @param group
	// * @return
	// */
	// @Transient
	// private Amounts getExtraProductsAmounts(boolean afterDiscount) {
	// Amounts amounts = new Amounts();
	// for (OrderLine orderLine : orderLines) {
	// if ((orderLine.getProduct() == null) && (orderLine.getBundle() == null))
	// {
	// amounts.add(orderLine.getAmounts(afterDiscount));
	// }
	// }
	// return amounts;
	// }

	/**
	 * Helper method for creating a subscription.
	 * 
	 * @param bundle
	 * @return
	 */
	private Subscription createSubscription(MobileProductBundle bundle) {
		Subscription subscription = new Subscription();
		subscription.setBundle(bundle);
		subscription.setContract(this);
		addSubscription(subscription);
		boolean hasDatadeling = false;
		for (BundleProductRelation product : bundle.getProducts()) {
			if (product.getProduct().getPublicName().startsWith("Datadeling")) {
				hasDatadeling = true;
				break;
			}
		}
		if (!hasDatadeling) {
			subscription.setDatadelingSimCardType(SimCardType.NA);
		}
		return subscription;
	}

	/**
	 * The returned array is used for contract summary.
	 * 
	 * @return
	 */
	@Transient
	public List<String[]> getFinansialOverviewLines() {
		ContractFinansialInfo infoNonNetwork	= null;
		ContractFinansialInfo infoNetwork		= null;
		if (businessArea.isOnePlus()) {
			// One+ uses two phases here
			// Phase 1: Only consider non-network orderlines. Use standard discount matrix.
			// Phase 2: Only consider network related orderlines. Use network discount matrix.

			infoNonNetwork 		= getContractFinansialInfo(true, false, false);
			infoNetwork 		= getContractFinansialInfo(false, true, false);
		} else {
			infoNonNetwork 	= getContractFinansialInfo(true, true, false);
		}

		ContractFinansialInfo infoTotal	= infoNonNetwork.clone();
		infoTotal.add(infoNetwork);

		PartnerData partnerData = getPartnerData(VARIANT_GENERELT);
		OfferReportDataSource.Data offerReportData = new OfferReportDataSource().getData(this, false);

		List<String[]> list = new ArrayList<>();

		//		TDC opsummering:
		//		Samlet oprettelsespris - pris hentes fra underskriftsside på tilbud
		//		Samlet installationspris - pris hentes fra underskriftsside på tilbud (er feltet skjult på tilbud, skjules feltet også i sammenfatningen)
		//		Samlet driftspris - pris hentes fra underskriftsside på tilbud

		//		TDC Erhvervscenter opsummering: (vises kun for EC profiler)
		//		Sum supportaftale - pris hentes fra “Rate løsning”
		//		Samlet kontantpris installation EC inkl. hardware - pris hentes fra “Installation”
		//		Samlet totalbeløb til rate løsning - pris hentes fra “Rate løsning”
		//		Samlet beløb pr. md. rate løsning - pris hentes fra “Rate løsning”
		//		Samlet beløb pr. md. rate løsning + support - pris hentes fra “Rate løsning”
		//		Samlet totalbeløb til rate hardware - pris hentes fra “Rate hardware”
		//		Samlet beløb pr. md. rate hardware - pris hentes fra “Rate hardware”


		list.add(new String[] { "", "Kontraktsum One+",
				"" + Amounts.getFormattedWithDecimals(infoNonNetwork.getRabataftaleKontraktsum()) + " kr." });
		list.add(new String[] { "", "Kontraktsum Netværk",
				"" + Amounts.getFormattedWithDecimals(infoNetwork.getRabataftaleKontraktsum()) + " kr." });

		list.add(new String[] { "space", "", "" });

		list.add(new String[] { "header", "TDC opsummering", "" });
		list.add(new String[] { "", "Samlet oprettelsespris",
				"" + Amounts.getFormattedWithDecimals(offerReportData.getTotal().getOneTimeFee()) + " kr." });
		list.add(new String[] { "", "Samlet installationspris",
				"" + Amounts.getFormattedWithDecimals(offerReportData.getTotal().getInstallationFee()) + " kr." });
		list.add(new String[] { "", "Samlet driftspris",
				"" + Amounts.getFormattedWithDecimals(offerReportData.getTotal().getRecurringFee()) + " kr." });

		if (MobileSession.get().userIsPartnerEC()) {
			list.add(new String[] { "space", "", "" });

			list.add(new String[] { "header", "TDC Erhvervscenter opsummering", "" });
			list.add(new String[] { "", "Sum supportaftale",
					partnerData.values.get("support_monthly") + " kr." });
			list.add(new String[] { "", "Samlet kontantpris installation EC inkl. hardware",
					partnerData.values.get("partnerInstallationAfterDiscountKontant") + " kr." });
			list.add(new String[] { "", "Samlet totalbeløb til rate løsning",
					partnerData.values.get("total_til_rate_betaling") + " kr." });

			list.add(new String[] { "", "Samlet beløb pr. md. rate løsning",
					partnerData.values.get("rate_monthly") + " kr." });
			list.add(new String[] { "", "Samlet beløb pr. md. rate løsning + support",
					partnerData.values.get("rate_and_support_monthly") + " kr." });
			list.add(new String[] { "", "Samlet totalbeløb til rate hardware",
					partnerData.values.get("total_hardware_value") + " kr." });
			list.add(new String[] { "", "Samlet beløb pr. md. rate hardware",
					partnerData.values.get("hardware_monthly") + " kr." });

			list.add(new String[] { "space", "", "" });

//		long supportMonthly = getSupportRecurringFee() + (getNoOfUsers(false) * getSupportPricePrUser());
//		long etableringAfRateAftale = getRateNonRecurringFee();
//		long totalTilRateBetalingFoerUpFront = partnerInstallationAfterDiscountRate + etableringAfRateAftale + hardwareTotalValue;
//		long totalTilRateBetaling = totalTilRateBetalingFoerUpFront - getUpFrontPayment();
//		long supportMonthly = getSupportRecurringFee() + (getNoOfUsers(false) * getSupportPricePrUser());
//		long rateMonthly = 0;
//		if (getRateMonths() >= 1) {
//			rateMonthly = Math.round(Math.ceil(((double) totalTilRateBetaling) / getRateMonths()));
//		}
//			data.values.put("partnerInstallationLinesRate", partnerInstallationLinesRate);
//			data.values.put("partnerInstallationBeforeDiscountRate", Amounts.getFormattedWithDecimals(partnerInstallationBeforeDiscountRate));
//			data.values.put("partnerInstallationAfterDiscountRate", Amounts.getFormattedWithDecimals(partnerInstallationAfterDiscountRate));
//			data.values.put("partnerInstallationLinesKontant", partnerInstallationLinesKontant);
//			data.values.put("partnerInstallationBeforeDiscountKontant", Amounts.getFormattedWithDecimals(partnerInstallationBeforeDiscountKontant));
//			data.values.put("partnerInstallationAfterDiscountKontant", Amounts.getFormattedWithDecimals(partnerInstallationAfterDiscountKontant));
//			data.values.put("etablering_af_rate_aftale",				Amounts.getFormattedWithDecimals(etableringAfRateAftale));
//			data.values.put("total_hardware_value", 					Amounts.getFormattedWithDecimals(hardwareTotalValue));
//			data.values.put("total_til_rate_betaling_foer_up_front", 	Amounts.getFormattedWithDecimals(totalTilRateBetaling));
//			data.values.put("total_til_rate_betaling",					Amounts.getFormattedWithDecimals(totalTilRateBetaling));

//			samlet_driftspris_TDC_plus_support = TDC drift pr. måned + sum supportaftale
//			samlet_driftspris_TDC_plus_rate_løsning_inkl_support = TDC drift pr. måned + samlet beløb pr. md rate løsning + support
//			samlet_driftspris_TDC_plus_rate_hardware = TDC drift pr. måned + samlet beløb pr. md. rate hardware
//			samlet_driftspris_TDC_plus_rate_hardware_plus_support = TDC drift pr. måned + samlet beløb pr. md. rate hardware + sum supportaftale

			list.add(new String[] { "header", "TDC + TDC Erhvervscenter opsummering", "" });
			list.add(new String[] { "", "Samlet driftspris TDC + Support",
					Amounts.getFormattedWithDecimals(offerReportData.getTotal().getRecurringFee() +
							Amounts.stringToLong((String) partnerData.values.get("support_monthly"))) + " kr." });
			list.add(new String[] { "", "Samlet driftspris TDC + Rate løsning inkl. Support",
					Amounts.getFormattedWithDecimals(offerReportData.getTotal().getRecurringFee() +
							Amounts.stringToLong((String) partnerData.values.get("rate_and_support_monthly"))) + " kr." });
			list.add(new String[] { "", "Samlet driftspris TDC + Rate hardware",
					Amounts.getFormattedWithDecimals(offerReportData.getTotal().getRecurringFee() +
							Amounts.stringToLong((String) partnerData.values.get("hardware_monthly"))) + " kr." });
			list.add(new String[] { "", "Samlet driftspris TDC + Rate hardware + Support",
					Amounts.getFormattedWithDecimals(offerReportData.getTotal().getRecurringFee() +
							Amounts.stringToLong((String) partnerData.values.get("hardware_monthly")) +
							Amounts.stringToLong((String) partnerData.values.get("support_monthly"))) + " kr." });
		}


//		if (getDiscountScheme(SwitchboardIpsaDiscountScheme.class) != null) {
//			list.add(new String[] { "", "Årlig IPSA rabatsum",
//					"" + Amounts.getFormattedNoDecimals(infoTotal.getIpsaSumPrYear()) + " kr." });
//		}
//		if (getDiscountScheme(RabatAftaleDiscountScheme.class) != null) {
//			if (businessArea.isWorks()) {
//				list.add(new String[] { "", "TDC Works kontraktsum",
//						"" + Amounts.getFormattedNoDecimals(infoTotal.getRabataftaleKontraktsum()) + " kr." });
//			} else if (businessArea.isFiberErhverv()) {
//				list.add(new String[] { "", "Kontraktsum",
//						"" + Amounts.getFormattedNoDecimals(infoTotal.getRabataftaleKontraktsum()) + " kr." });
//			} else if (businessArea.isOnePlus()) {
////				list.add(new String[] { "", "Kontraktsum",
////						"" + Amounts.getFormattedNoDecimals(infoTotal.getRabataftaleKontraktsum()) + " kr." });
//				list.add(new String[] { "", "Kontraktsum One+",
//						"" + Amounts.getFormattedNoDecimals(infoNonNetwork.getRabataftaleKontraktsum()) + " kr." });
//				list.add(new String[] { "", "Kontraktsum Netværk",
//						"" + Amounts.getFormattedNoDecimals(infoNetwork.getRabataftaleKontraktsum()) + " kr." });
//			} else {
//				list.add(new String[] { "", "FIXME",
//						"" + Amounts.getFormattedNoDecimals(infoTotal.getRabataftaleKontraktsum()) + " kr." });
//			}
//		}
//		if (businessArea.hasFeature(FeatureType.FORDELSAFTALE)) {
//			list.add(new String[] { "", "Årlig mobil kontraktsum",
//					"" + Amounts.getFormattedNoDecimals(infoTotal.getMobileSumPrYear()) + " kr." });
//		}
//
//		if (businessArea.hasFeature(FeatureType.GKS)) {
//			list.add(new String[] { "", "Årlig GKS sum",
//					"" + Amounts.getFormattedNoDecimals(infoTotal.getGksSumPrYear()) + " kr." });
//		}
//
//		list.add(new String[] { "space", "", "" });
//		list.add(new String[] { "header", "Samlet etableringspris", "" });
//		list.add(new String[] { "", "Oprettelse af løsningen",
//				"" + infoTotal.getContractTotalsBeforeDiscounts().getOneTimeFeeFormatted() });
//		list.add(new String[] { "", "Installation af løsningen",
//				"" + infoTotal.getContractTotalsBeforeDiscounts().getInstallationFeeFormatted() });
//
//		infoTotal.setContractDiscount(
//				infoTotal.getContractDiscounts().getOneTimeFee() + infoTotal.getContractDiscounts().getInstallationFee());
//		infoTotal.setCampaignDiscount(
//				infoTotal.getCampaignDiscounts().getOneTimeFee() + infoTotal.getCampaignDiscounts().getInstallationFee());
//		infoTotal.setTotalDiscount(infoTotal.getContractDiscount() + infoTotal.getCampaignDiscount());
////		infoTotal.setTotalBeforeDiscount(
////				contractAmountsBeforeDiscounts.getOneTimeFee() + contractAmountsBeforeDiscounts.getInstallationFee());
//		if (infoTotal.getTotalDiscount() > 0) {
//			list.add(new String[] { "total", "Ialt før rabat",
//					"" + Amounts.getFormattedNoDecimals(infoTotal.getContractTotalsBeforeDiscounts().getRecurringFee()) + " kr." });
//			if (infoTotal.getCampaignDiscount() > 0) {
//				list.add(new String[] { "", "Rabat (kampagne)",
//						"" + Amounts.getFormattedNoDecimals(infoTotal.getCampaignDiscount()) + " kr." });
//			}
//			if (infoTotal.getContractDiscount() > 0) {
//				list.add(new String[] { "", "Rabat (kontrakt)",
//						"" + Amounts.getFormattedNoDecimals(infoTotal.getContractDiscount()) + " kr." });
//			}
//		}
//		list.add(new String[] { "total", "Ialt", ""
//				+ Amounts.getFormattedNoDecimals(infoTotal.getContractTotalsBeforeDiscounts().getNonRecurringFees() - infoTotal.getTotalDiscount()) + " kr." });
//
//		// list.add(new String[] {"total", "Ialt", "" +
//		// Amounts.getFormattedNoDecimals(contractTotalsBeforeDiscounts.getOneTimeFee()
//		// + contractTotalsBeforeDiscounts.getInstallationFee()) + " kr."});
//		list.add(new String[] { "space", "", "" });
//		list.add(new String[] { "header", "Drift pr. måned", "" });
//		if (businessArea.isOnePlus()) {
////			list.add(new String[] { "", "TDC Erhverv One+", infoTotal.getSubscriptionTotals().clone().add(infoTotal.getRoamingAmounts())
////					.add(infoTotal.getFunctionsAmounts()).getRecurringFeeFormatted() });
//			list.add(new String[] { "", "TDC Erhverv One+", "" });
//		} else if (businessArea.isWorks()) {
//			list.add(new String[] { "", "TDC Works", infoTotal.getSubscriptionTotals().clone().add(infoTotal.getRoamingAmounts())
//					.add(infoTotal.getFunctionsAmounts()).getRecurringFeeFormatted() });
//		}
//
//		if (businessArea.hasFeature(FeatureType.SWITCHBOARD)) {
//			if (businessArea.isOnePlus()) {
//				list.add(new String[]{"", "Virksomhedsfunktionalitet", infoTotal.getSwitchboardTotals().getRecurringFeeFormatted()});
//				list.add(new String[]{"", "Tilvalg virksomhedsniveau",
//						infoTotal.getSwitchboardAddonAmounts().getRecurringFeeFormatted()});
//			} else {
//				list.add(new String[]{"", "TDC Omstilling", infoTotal.getSwitchboardTotals().getRecurringFeeFormatted()});
//				list.add(new String[]{"", "Tilvalg til Omstilling",
//						infoTotal.getSwitchboardAddonAmounts().getRecurringFeeFormatted()});
//			}
//		}
//		if (businessArea.hasFeature(FeatureType.MOBILE_BUNDLES_STANDARD)) {
//			list.add(new String[] { "", "" + infoTotal.getSubscriptionCount() + " stk. mobilabonnementer",
//					"" + infoTotal.getSubscriptionTotals().getRecurringFeeFormatted() });
//			list.add(new String[] { "", "Roaming tilvalg", "" + infoTotal.getRoamingAmounts().getRecurringFeeFormatted() });
//			list.add(new String[] { "", "Funktionstilvalg",
//					"" + infoTotal.getFunctionsAmounts().getRecurringFeeFormatted() });
//		}
//		if (infoTotal.getExtraProductsAmounts().getRecurringFee() > 0) {
//			list.add(new String[] { "", "Ekstra produkter",
//					"" + infoTotal.getExtraProductsAmounts().getRecurringFeeFormatted() });
//		}
//		if (businessArea.isOnePlus()) {
//			list.add(new String[] { "", "TDC Erhverv Netværk", "" });
//		}
//		if (businessArea.hasFeature(FeatureType.XDSL)) {
//			list.add(new String[] { "", "xDSL", infoTotal.getAdslAmounts().getRecurringFeeFormatted() });
//		}
//		if (businessArea.hasFeature(FeatureType.WIFI)) {
//			list.add(new String[] { "", "Wi-Fi", infoTotal.getWiFiAmounts().getRecurringFeeFormatted() });
//		}
//		if (businessArea.hasFeature(FeatureType.FIBER)) {
//			list.add(new String[] { "", "Fiber", infoTotal.getFiberAmounts().getRecurringFeeFormatted() });
//		}
//		if (!businessArea.isOnePlus()) {
//			if (businessArea.hasFeature(FeatureType.FIBER_ERHVERV)) {
//				list.add(new String[] { "", "Fiber", infoTotal.getFiberAmounts().getRecurringFeeFormatted() });
//			}
//		}
//
//		if (infoTotal.getContractDiscounts().getRecurringFee() + infoTotal.getCampaignDiscounts().getRecurringFee() > 0) {
//			list.add(new String[] { "total", "Ialt før rabat",
//					"" + infoTotal.getContractTotalsBeforeDiscounts().getRecurringFeeFormatted() });
//			if (infoTotal.getCampaignDiscounts().getRecurringFee() > 0) {
//				list.add(new String[] { "", "Rabat (kampagne)",
//						"" + infoTotal.getCampaignDiscounts().getRecurringFeeFormatted() });
//			}
//			if (infoTotal.getContractDiscounts().getRecurringFee() > 0) {
//				list.add(new String[] { "", "Rabat (kontrakt)",
//						"" + infoTotal.getContractDiscounts().getRecurringFeeFormatted() });
//			}
//		}
//		// list.add(new String[] {"total", "Ialt", "" +
//		// Amounts.getFormattedNoDecimals(contractAmountsBeforeDiscounts.getRecurringFee()
//		// - infoTotal.getContractDiscounts().getRecurringFee() -
//		// infoTotal.getCampaignDiscounts().getRecurringFee()) + " kr."});
//		list.add(new String[] { "total", "Ialt", ""
//				+ Amounts.getFormattedNoDecimals(infoTotal.getContractTotalsAfterDiscounts().getRecurringFee()) + " kr." });
//		list.add(new String[] { "space", "", "" });
//
//		if (infoTotal.getNoOfDiscountSchemes() > 0) {
//			list.add(new String[] { "space", "", "" });
//			list.add(new String[] { "discount", "Prisen er inkl. rabat", "" });
//			// if (infoTotal.getNoOfDiscountSchemes() > 1 ) {
//			// list.add(new String[] {"space", "", ""});
//			// list.add(new String[] {"discount", "Prisen er inkl. rabat", ""});
//			// } else {
//			// if ((infoTotal.getNoOfDiscountSchemes() == 1 ) &&
//			// ((infoTotal.getFixedDiscountPct() +
//			// infoTotal.getSwitchboardIpsaDiscountPct()) > 0)) {
//			// DiscountScheme discountScheme = getDiscountSchemes().get(0);
//			// if (discountScheme instanceof FixedDiscount) {
//			// list.add(new String[] {"discount", "Prisen er inkl. " +
//			// Amounts.getFormattedNoDecimals(infoTotal.getFixedDiscountPct()) + "%
//			// rabat", ""});
//			// } else if (discountScheme instanceof
//			// SwitchboardIpsaDiscountScheme) {
//			// list.add(new String[] {"discount", "Prisen er inkl. " +
//			// Amounts.getFormattedNoDecimals(infoTotal.getSwitchboardIpsaDiscountPct())
//			// + "% rabat", ""});
//			// }
//			// }
//			// }
//		}

		list.add(new String[] { "space", "", "" });
		list.add(new String[] { "", "Alle priser er ekskl. moms", "" });

		return list;
	}

	@Transient
	public ContractFinansialInfo getContractFinansialInfo(boolean useNonNetworkOrderLines, boolean useNetworkOrderLines, boolean partnerData) {
		if (useNonNetworkOrderLines == useNetworkOrderLines) {
			throw new IllegalArgumentException("");
		}

		ContractFinansialInfo info = new ContractFinansialInfo();

		ProductAndBundleFilter productAndBundleFilter = getRabataftaleProductFilter(useNonNetworkOrderLines, useNetworkOrderLines);

		if (getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
			if (useNonNetworkOrderLines) {
				CoreSession.get().setDiscountPointNonNetwork(null);
				info.setRabataftaleKontraktsum(
						(12 * getTotalAmountsBeforeDiscounts(productAndBundleFilter).getRecurringFee())
								+ (this.additionToKontraktsum * 100));

				CoreSession.get().setDiscountPointNonNetwork(Lookup.lookup(SuperContractService.class)
						.getDiscountPoint(getBusinessArea(), new BigDecimal(info.getRabataftaleKontraktsum()), this, false));
			}

			if (useNetworkOrderLines) {
				CoreSession.get().setDiscountPointNetwork(null);
				ContractFinansialInfo infoNetwork = new ContractFinansialInfo();
				infoNetwork.setRabataftaleKontraktsum(
						(12 * getTotalAmountsBeforeDiscounts(productAndBundleFilter).getRecurringFee())
								+ (this.additionToKontraktsumNetwork * 100));

				CoreSession.get().setDiscountPointNetwork(Lookup.lookup(SuperContractService.class)
						.getDiscountPoint(getBusinessArea(), new BigDecimal(infoNetwork.getRabataftaleKontraktsum()), this, true));
				if (useNonNetworkOrderLines) {
					info.add(infoNetwork);
				} else {
					info = infoNetwork;
				}
			}
		}

		for (DiscountScheme discountScheme : getDiscountSchemes()) {
			discountScheme.prepare(this);
		}

		Amounts contractAmountsBeforeDiscounts = getTotalAmountsBeforeDiscounts(getCheckTypeProductFilter(useNonNetworkOrderLines, useNetworkOrderLines));
		info.setContractTotalsBeforeDiscounts(contractAmountsBeforeDiscounts);
//		contractAmountsBeforeDiscounts = getTotalAmountsBeforeDiscounts(getAcceptAllProductFilter());

		/*
		 * Campaign discounts are deducted first. Contract discounts are
		 * deducted afterwards.
		 */
		info.setCampaignDiscounts(getCampaignDiscounts(productAndBundleFilter));
		info.setContractDiscounts(getContractDiscounts(productAndBundleFilter));

		if (useNonNetworkOrderLines) {
			if (getBusinessArea().hasFeature(FeatureType.IPSA)) {
				info.setIpsaSumPrYear(getIpsaSumPrYear());
			}

			info.setMobileSumPrYear(getMobileSumPrYear(partnerData));
		}

		if (getBusinessArea().hasFeature(FeatureType.GKS)) {
			info.setGksSumPrYear(getGksSumPrYear());
		}

		int subscriptionCount = 0;
		Map<String, MutableInt> bundleNameToSubscriptionCount = new HashMap<>();
		info.setBundleNameToSubscriptionCount(bundleNameToSubscriptionCount);
		for (OrderLine orderLine : getBundleOrderLines()) {
			MobileProductBundle productBundle = (MobileProductBundle) orderLine.getBundle();
			if (MobileProductBundleEnum.MOBILE_BUNDLE.equals(productBundle.getBundleType())) {
				if (useNonNetworkOrderLines) {
					info.getSubscriptionTotals()
							.add(productBundle.getAmounts(orderLine.getDeferredCount(), false, false, false, this));
					subscriptionCount += orderLine.getDeferredCount().getCountTotal();
					String bundleName = "Mix";
					if (!productBundle.isMixBundle()) {
						bundleName = productBundle.getPublicName();
					}
					bundleName = bundleName + " (" + businessArea.getTypeId() + ")";

					MutableInt bundleSubscriptionCount = bundleNameToSubscriptionCount.get(bundleName);
					if (bundleSubscriptionCount == null) {
						bundleSubscriptionCount = new MutableInt();
						bundleNameToSubscriptionCount.put(bundleName, bundleSubscriptionCount);
					}
					bundleSubscriptionCount.add(orderLine.getTotalCount());
				}
			} else if (MobileProductBundleEnum.SWITCHBOARD_BUNDLE.equals(productBundle.getBundleType())) {
				if (useNonNetworkOrderLines) {
					info.getSwitchboardTotals()
							.add(productBundle.getAmounts(orderLine.getDeferredCount(), false, false, false, this));

					String bundleName = productBundle.getPublicName() + " (" + businessArea.getTypeId() + ")";
					MutableInt bundleSubscriptionCount = bundleNameToSubscriptionCount.get(bundleName);
					if (bundleSubscriptionCount == null) {
						bundleSubscriptionCount = new MutableInt();
						bundleNameToSubscriptionCount.put(bundleName, bundleSubscriptionCount);
					}
					bundleSubscriptionCount.add(orderLine.getTotalCount());
				}
			} else if (MobileProductBundleEnum.XDSL_BUNDLE.equals(productBundle.getBundleType())) {
				if (useNetworkOrderLines) {
					String bundleName = productBundle.getPublicName() + " (" + businessArea.getTypeId() + ")";
					MutableInt bundleSubscriptionCount = bundleNameToSubscriptionCount.get(bundleName);
					if (bundleSubscriptionCount == null) {
						bundleSubscriptionCount = new MutableInt();
						bundleNameToSubscriptionCount.put(bundleName, bundleSubscriptionCount);
					}
					bundleSubscriptionCount.add(orderLine.getTotalCount());
				}
			} else if (MobileProductBundleEnum.OFFICE_BUNDLE.equals(productBundle.getBundleType())) {
				if (useNonNetworkOrderLines) {
					subscriptionCount += orderLine.getDeferredCount().getCountTotal();
				}
			}
		}
		if (useNonNetworkOrderLines) {
			if (subscriptionCount != getSubscriptions().size()) {
				log.error("subscriptionCount != contract.getSubscriptions().size(): " + subscriptionCount + " != "
						+ getSubscriptions().size());
			}
			info.setSubscriptionCount(subscriptionCount);

			if (businessArea.isOnePlus()) {
				info.setRoamingAmounts(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_ROAMING_ILD));
				info.setFunctionsAmounts(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_USER_ADDON_FUNCTIONS));
			} else {
				info.setRoamingAmounts(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_ADDON_ROAMING));
				info.setFunctionsAmounts(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_ADDON_FUNCTIONS));
			}
			info.setSwitchboardAmounts(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD));
			{
				Amounts a = getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_ADDON);
				a = a.add(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INCLUDED));
				a = a.add(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INSTALLATION));
				a = a.add(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_REMOTE));
				a = a.add(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_ADDON));
				a = a.add(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_ADDON_IDENTITY));
				a = a.add(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_SOLUTION_ADDON_FEATURES));

				info.setSwitchboardAddonAmounts(a);
			}
			info.setExtraProductsAmounts(getProductAmountsByProductGroup(MobileProductGroupEnum.PRODUCT_GROUP_EXTRA));
		}

		if (useNetworkOrderLines) {
			info.setAdslAmounts(getAmountsForBundle(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE));
			info.setWiFiAmounts(getAmountsForBundle(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE));
			info.setFiberAmounts(getAmountsForBundle(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE));
		}

		// info.setContractTotalsBeforeDiscounts(getAmounts(false));
		info.setContractTotalsAfterDiscounts(contractAmountsBeforeDiscounts.clone()
				.subtract(info.getContractDiscounts()).subtract(info.getCampaignDiscounts()));

		info.setNoOfDiscountSchemes(getDiscountSchemes().size());

		for (DiscountScheme discountScheme : getDiscountSchemes()) {
			if (discountScheme instanceof FixedDiscount) {
				info.setFixedDiscountPct(((FixedDiscount) discountScheme).getDiscountPercentages().getRecurringFee());
			} else if (discountScheme instanceof SwitchboardIpsaDiscountScheme) {
				info.setSwitchboardIpsaDiscountPct(
						((SwitchboardIpsaDiscountScheme) discountScheme).getDiscountPercentages().getRecurringFee());
			}
		}

		return info;
	}

	private Amounts getTotalAmountsBeforeDiscounts(ProductAndBundleFilter filter) {
		Amounts totals = new Amounts();
		for (OrderLine orderLine : orderLines) {
			MobileSession.get().setPricingSubIndex(orderLine.getSubIndex());
			Amounts amounts = handleInstallationType(orderLine.getAmountsBeforeDiscounts(filter), orderLine.getProductGroup());
			if (!amounts.isAllZero()) {
				// LOG - DISCOUNTS
//				log.info("getTotalAmountsBeforeDiscounts - " +
//						(orderLine.getBundle() == null ? orderLine.getProduct().getPublicName() : orderLine.getProductGroup().getName()) +
//						" - " + amounts.toString());
			}
			totals.add(amounts);
		}
		return totals;
	}

	private Amounts getTotalAmountsAfterDiscounts(boolean afterCampaignDiscounts, boolean afterContractDiscounts, ProductAndBundleFilter filter) {
		Amounts totals = new Amounts();
		for (OrderLine orderLine : orderLines) {
			totals.add(handleInstallationType(orderLine.getAmounts(afterCampaignDiscounts, afterContractDiscounts, filter),
					orderLine.getProductGroup()));
		}
		return totals;
	}

	private Amounts getAmountsForBundle(MobileProductGroupEnum bundleGroup) {
		Amounts amounts = new Amounts();
		for (MobileProductGroupEnum groupType : MobileProductGroupEnum.getByPrefix(bundleGroup.name() + "_")) {
			amounts.add(handleInstallationType(getProductAmountsByProductGroup(groupType), groupType));
		}
		return amounts;
	}

	private Amounts handleInstallationType(Amounts amounts, ProductGroup group) {
		return handleInstallationType(amounts, MobileProductGroupEnum.getValueByProductGroup(group));
	}

	private Amounts handleInstallationType(Amounts amounts, MobileProductGroupEnum groupType) {
		boolean userProfiles = (groupType.getKey()
				.indexOf(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE.getKey()) == 0);
		if (amounts.getInstallationFee() > 0) {
// 			log.info("");
		}
		if (userProfiles && isOyoInstallationUserProfiles()) {
			amounts.setInstallationFee(0);
		}
		if (!userProfiles && isOyoInstallation()) {
			amounts.setInstallationFee(0);
		}
		return amounts;
	}

	public Map<Object, CountProductOrBundleAmounts> getProductOrBundleCountInOrderLines(boolean cdmMode, boolean partnerData) {
		Map<Object, CountProductOrBundleAmounts> productOrBundleToCount = new HashMap<>();
		for (OrderLine orderLine : orderLines) {
			if (orderLine.getDeferredCount().getCountTotal() == 0) {
				continue;
			}
			if (orderLine.getProduct() != null) {
				Product product = orderLine.getProduct();
				updateDataForProduct(productOrBundleToCount, orderLine, product, null, partnerData);
			} else if (orderLine.getBundle() != null) {
				MobileProductBundle productBundle = (MobileProductBundle) orderLine.getBundle();

				boolean includeProducts = false;		// Introduced for One+ - drop feature again!?
				CountProductOrBundleAmounts bundleAmounts = updateDataForProductBundle(productOrBundleToCount, orderLine, productBundle, includeProducts);

				if (productBundle.isAddProductPrices()) {
					for (BundleProductRelation productRelation : orderLine.getBundle().getProducts()) {
						if (!productRelation.getProductAccessType().equals(ProductAccessType.SEPARATE_COUNT)) {  // ignore everything but INCLUDED?
							if ((productRelation.getProduct() != null) && productRelation.isAddProductPrice()) {
								Product product = productRelation.getProduct();
								// if (!cdmMode &&
								// productBundle.isAddProductPricesToBundlePrice())
								// {

								// This messes up CDM for ADSL in Works: if ((!cdmMode || getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT))
								boolean addToBundleAmounts;
								if (productBundle.isAddProductPricesToBundlePrice()) {
									if (cdmMode) {
										if ((getBusinessArea().isWorks() && ((MobileProductBundle) orderLine.getBundle()).getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE)) ||
											(getBusinessArea().isOnePlus() && ((MobileProductBundle) orderLine.getBundle()).getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE)) ||
												((getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) && ((MobileProductBundle) orderLine.getBundle()).getBundleType().equals(MobileProductBundleEnum.OFFICE_BUNDLE))) {
											addToBundleAmounts = true;
										} else {
											addToBundleAmounts = false;
										}
									} else {
										addToBundleAmounts = true;
									}
								} else {
									addToBundleAmounts = false;
								}

								if (addToBundleAmounts) {
									// Add amounts for product to amounts for bundle
									CountProductOrBundleAmounts productAmounts = updateDataForProduct(new HashMap<Object, CountProductOrBundleAmounts>(), orderLine, product, productBundle, partnerData);

									bundleAmounts.getAmountsAfterCampaignAndContractDiscounts()			.add(productAmounts.getAmountsAfterCampaignAndContractDiscounts());
									bundleAmounts.getBaseAmounts()										.add(productAmounts.getBaseAmounts());
									bundleAmounts.getBaseAmountsWithContractDiscountsDeducted()			.add(productAmounts.getBaseAmountsWithContractDiscountsDeducted());
									bundleAmounts.getCampaignDiscountAmounts()							.add(productAmounts.getCampaignDiscountAmounts());
									bundleAmounts.getCampaignDiscountsWithContractDiscountsDeducted()	.add(productAmounts.getCampaignDiscountsWithContractDiscountsDeducted());
//								System.out.println(bundleAmounts.getAmounts());
								} else {
									updateDataForProduct(productOrBundleToCount, orderLine, product, productBundle, partnerData);
								}
							}
						}
					}
				}
			}
		}
		return productOrBundleToCount;
	}

	/**
	 * @param productToCount
	 * @param orderLine
	 * @param product
	 */
	public CountProductOrBundleAmounts updateDataForProduct(Map<Object, CountProductOrBundleAmounts> productToCount,
			OrderLine orderLine, Product product, ProductBundle owningBundle, boolean partnerData) {
		MobileSession.get().setPricingSubIndex(orderLine.getSubIndex());  // !!!!!!!!
		CountProductOrBundleAmounts countProductOrBundleAmounts = productToCount.get(product);
		if (countProductOrBundleAmounts == null) {
			countProductOrBundleAmounts = new CountProductOrBundleAmounts();
			productToCount.put(product, countProductOrBundleAmounts);
		}
		countProductOrBundleAmounts.addCounts(orderLine.getDeferredCount());
		countProductOrBundleAmounts.setSubIndex(orderLine.getSubIndex());

		// Amounts amounts =
		// product.getAmounts(countProductOrBundleAmounts.getCount(),
		// afterDiscounts, this);
		// countProductOrBundleAmounts.setBaseAmounts(amounts);

		Amounts amounts = product.getAmounts(countProductOrBundleAmounts.getCount(), false, false, this);
		// This does not look right - merge the two methods?
		amounts = ((MobileProduct) product).adjustInstallation(partnerData, amounts);
		amounts = handleInstallationType(amounts, product.getProductGroup());

		countProductOrBundleAmounts.setBaseAmounts(amounts);

		CampaignProductRelation campaignProductRelation = getCampaignProductRelation(product);
		if (campaignProductRelation == null) {
			if ((owningBundle != null) && getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
				// Apply the % discount from the bundle, which the product is a part of.
				Amounts a = product.getPrice().clone();

				// This does not look right - merge the two methods?
				a = ((MobileProduct) product).adjustInstallation(partnerData, a);
				a = handleInstallationType(a, product.getProductGroup());

				a.multiplyBy(countProductOrBundleAmounts.getCount());

				// The product is part of a bundle, so it is not a network product, right?
				Amounts discount = getRabataftaleCampaignDiscounts(a, owningBundle.getRabataftaleCampaignDiscountMatrix(), new Amounts(), false);
				countProductOrBundleAmounts.setCampaignDiscountAmounts(discount);
			} else {
				countProductOrBundleAmounts.setCampaignDiscountAmounts(new Amounts());
			}
		} else {
			Amounts discounts = new Amounts();
			if (getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
				if ((campaignProductRelation.getCampaignPriceAmounts() == null) ||
						campaignProductRelation.getCampaignPriceAmounts().isAllZero()) {
					discounts = getRabataftaleCampaignDiscounts(product.getPrice().clone(),
							campaignProductRelation.getRabataftaleCampaignDiscountMatrix(), new Amounts(), false);
				} else {
					// The campaign discount is calculated to be the discount required to get to the fixed campaign price
					discounts = product.getPrice().clone()
							.subtract(product.getContractDiscounts(this,
								countProductOrBundleAmounts.getBaseAmounts(), countProductOrBundleAmounts.getBaseAmounts()))
							.subtract(campaignProductRelation.getCampaignPriceAmounts());
				}
			}
			if (discounts.isAllZero()) {
				discounts = campaignProductRelation.getCampaignDiscountAmounts().clone();
			}
			countProductOrBundleAmounts
					.setCampaignDiscountAmounts(
							handleInstallationType(discounts, product.getProductGroup())); // Durikke! - bruges det??

//			countProductOrBundleAmounts
//					.setCampaignDiscountAmounts(
//							handleInstallationType(
//									campaignProductRelation.getCampaignDiscountAmounts(),
//									product.getProductGroup())); // Durikke! - bruges det??
		}

		Amounts a = product.getAmounts(countProductOrBundleAmounts.getCount(), false, true, this);
		// This does not look right - merge the two methods?
		a = ((MobileProduct) product).adjustInstallation(partnerData, a);
		a = handleInstallationType(a, product.getProductGroup());

		countProductOrBundleAmounts.setAmountsAfterCampaignAndContractDiscounts(a);

		countProductOrBundleAmounts.getAmountsAfterCampaignAndContractDiscounts().subtract(countProductOrBundleAmounts.getCampaignDiscountAmounts());

		countProductOrBundleAmounts.setBaseAmountsWithContractDiscountsDeducted(
				handleInstallationType(
					countProductOrBundleAmounts.getBaseAmounts().clone().subtract(product.getContractDiscounts(this,
							countProductOrBundleAmounts.getBaseAmounts(), countProductOrBundleAmounts.getBaseAmounts())),
						product.getProductGroup())); // TODO:
																														// Suspicious

		countProductOrBundleAmounts.setCampaignDiscountsWithContractDiscountsDeducted(
				handleInstallationType(
					countProductOrBundleAmounts.getCampaignDiscountAmounts().clone()
							.subtract(product.getContractDiscounts(this,
									countProductOrBundleAmounts.getCampaignDiscountAmounts(),
									countProductOrBundleAmounts.getCampaignDiscountAmounts())),
						product.getProductGroup())); // TODO:
																								// Suspicious

		countProductOrBundleAmounts.setProduct((MobileProduct) product);
		return countProductOrBundleAmounts;
	}

	public boolean findInconsistencies() {
		boolean anyFound = false;
		for (OrderLine orderLine : orderLines) {
			if (orderLine.getDeferredCount().getCountTotal() == 0) {
				continue;
			}
			if (orderLine.getBundle() != null) {
				MobileProductBundle productBundle = (MobileProductBundle) orderLine.getBundle();

				Iterator<BundleProductRelation> iterator = productBundle.getProducts().iterator();
				while (iterator.hasNext()) {
					BundleProductRelation bundleProductRelation = iterator.next();
					if (bundleProductRelation.getProduct() == null) {
						log.warn("Contract " + getTitle() + "/" + getId()
								+ ": Bundle->Product relation with product=null: "
								+ bundleProductRelation.getProductBundleId() + "/"
								+ bundleProductRelation.getProductId());
						iterator.remove();
						anyFound = true;
					}
				}
			}
		}
		return anyFound;
	}

	/**
	 * @param productToCount
	 * @param orderLine
	 * @param productBundle
	 */
	private CountProductOrBundleAmounts updateDataForProductBundle(
			Map<Object, CountProductOrBundleAmounts> productToCount, OrderLine orderLine, ProductBundle productBundle, boolean includeProducts) {
		CountProductOrBundleAmounts countProductOrBundleAmounts = productToCount.get(productBundle);
		if (countProductOrBundleAmounts == null) {
			countProductOrBundleAmounts = new CountProductOrBundleAmounts();
			productToCount.put(productBundle, countProductOrBundleAmounts);
		}
		countProductOrBundleAmounts.addCounts(orderLine.getDeferredCount());
		countProductOrBundleAmounts.setSubIndex(null);

		if (includeProducts) {
			countProductOrBundleAmounts.setBaseAmounts(productBundle.getBundleWithProductsBaseAmounts(countProductOrBundleAmounts.getCount()));
		} else {
			countProductOrBundleAmounts.setBaseAmounts(productBundle.getBundleOnlyBaseAmounts(countProductOrBundleAmounts.getCount()));
		}

//		if (businessArea.isOnePlus()) {
//			// I have no idea why I have to do this for One+ and not for Works
//			countProductOrBundleAmounts.setCampaignDiscountAmounts(
//					productBundle.getBundleWithProductsCampaignDiscountAmounts(this, countProductOrBundleAmounts.getCount()));
//		} else {
//			countProductOrBundleAmounts.setCampaignDiscountAmounts(
//					productBundle.getBundleOnlyCampaignDiscountAmounts(this, countProductOrBundleAmounts.getCount()));
//		}
		countProductOrBundleAmounts.setCampaignDiscountAmounts(
				productBundle.getBundleOnlyCampaignDiscountAmounts(this, countProductOrBundleAmounts.getCount()));

		// countProductOrBundleAmounts.setAmountsAfterCampaignAndContractDiscounts(productBundle.getTotalBundleAmountsAfterAllDiscounts(countProductOrBundleAmounts.getCount(),
		// this));

		countProductOrBundleAmounts.setAmountsAfterCampaignAndContractDiscounts(
				productBundle.getBundleOnlyAmountsAfterAllDiscounts(countProductOrBundleAmounts.getCount(), this));
		// countProductOrBundleAmounts.setTotalAmountsAfterCampaignAndContractDiscounts(productBundle.getTotalBundleAmountsAfterAllDiscounts(countProductOrBundleAmounts.getCount(),
		// this));

		countProductOrBundleAmounts.setBaseAmountsWithContractDiscountsDeducted(productBundle
				.getBaseAmountsWithContractDiscountsDeducted(this, countProductOrBundleAmounts.getCount())); // bad

		countProductOrBundleAmounts.setCampaignDiscountsWithContractDiscountsDeducted(productBundle
				.getCampaignDiscountsWithContractDiscountsDeducted(this, countProductOrBundleAmounts.getCount()));

		countProductOrBundleAmounts.setProductBundle((MobileProductBundle) productBundle);
		return countProductOrBundleAmounts;
	}

	/**
	 * @return
	 * @param partnerVersion
	 */
	@Transient
	public List<String[]> getCdmOutputLines(boolean partnerVersion) {
		List<String[]> list = new ArrayList<>();

		if (!getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
			// ContractFinansialInfo info = getContractFinansialInfo();
			// discountPoint =
			// Lookup.lookup(SuperContractService.class).getDiscountPoint(getBusinessArea(),
			// new BigDecimal(info.getRabataftaleKontraktsum()),
			// getContractLength());
			// } else {
			// Fortællenr.

			for (ProductGroup productGroup : businessArea.getProductGroupsAndChildren()) {
				if (productGroup.getUniqueName().equals(MobileProductGroupEnum.PRODUCT_GROUP_PRODUCTION_OUTPUT_CDM.getKey())) {
					for (Product product : SortingHelper.sort(productGroup.getProducts(),
							SortingType.TYPE_PRODUCTION)) {
						if (!((MobileProduct) product).isExcludeFromProductionOutput()) {
							int count = 0;
							if (product.getMinCount() == product.getMaxCount()) {
								count = product.getMinCount();
							} else {
								if (businessArea.hasFeature(FeatureType.FIXED_DISCOUNT_VARIABLE)) {
									if (product.getPublicName().contains("1-årig aftale")) {
										count = (contractLength == 1 ? 1 : 0);
									} else if (product.getPublicName().contains("2-årig aftale")) {
										count = (contractLength == 2 ? 1 : 0);
									} else if (product.getPublicName().contains("3-årig aftale")) {
										count = (contractLength > 2 ? 1 : 0);
									}
								}
							}
							String discountType = " ";
							addFortaelleNrToCdmList(list, String.valueOf(count), product.getProductId(),
									product.getInternalName(), Amounts.getFormattedWithDecimals(0),
									Amounts.getFormattedWithDecimals(0), discountType);
						}
					}
					break;
				}
			}
			if (list.size() > 0) {
				list.add(new String[] { "BREAK", " ", " ", " ", " ", " " });
			}
		}

		Map<Object, CountProductOrBundleAmounts> productToCount = getProductOrBundleCountInOrderLines(true, false);

		List<CountProductOrBundleAmounts> sortedCountProductOrBundleAmounts = new ArrayList(productToCount.values());
		List<String> headers = new ArrayList();

		// CountProductOrBundleComparator comparator = new
		// CountProductOrBundleComparator(FeeCategory.RECURRING_FEE, null);
		CountProductOrBundleComparator comparator = new CountProductOrBundleComparator(Criteria.cdm, null);
		Collections.sort(sortedCountProductOrBundleAmounts, comparator);

		Amounts totalCampaignDiscounts = new Amounts(0, 0, 0);
		Amounts totals = new Amounts(0, 0, 0);
		
		for (CountProductOrBundleAmounts countProductOrBundleAmount : sortedCountProductOrBundleAmounts) {
			if (countProductOrBundleAmount.isProduct()) {
				MobileProduct product = countProductOrBundleAmount.getProduct();
				if (product.getProductId().startsWith("_omst_")) {
					continue;
				}
				if (product.getProductId().startsWith("_pk_")) {
					continue;
				}
				if (product.isExcludeFromProductionOutput()) {
					continue;
				}
				if (MobileProductGroupEnum.getValueByKey(product.getProductGroup().getUniqueName()).name()
						.startsWith(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE + "_")) {
					continue;
				}
				if (MobileProductGroupEnum.getValueByKey(product.getProductGroup().getUniqueName()).name()
						.startsWith(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE + "_LOCATION")) {
					continue;
				}
				if (MobileProductGroupEnum.getValueByKey(product.getProductGroup().getUniqueName()).name()
						.startsWith(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_GENERAL_SERVICE.name())) {
					continue;
				}
				if (MobileProductGroupEnum.getValueByKey(product.getProductGroup().getUniqueName()).name()
						.startsWith(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE + "_")) {
					continue;
				}
				if (MobileProductGroupEnum.getValueByKey(product.getProductGroup().getUniqueName()).name()
						.startsWith(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_NEW_BUNDLE + "_")) {
					continue;
				}
				if (product.getProductGroup().getUniqueName()
						.equals(MobileProductGroupEnum.PRODUCT_GROUP_TDC_WORKS_MOBILE_BUNDLE.getKey())) {
					continue;
				}
				if (partnerVersion) {
					if (((MobileProductGroup) product.getProductGroup()).isOfType(
							PRODUCT_GROUP_SOLUTION,
							PRODUCT_GROUP_SOLUTION_ADDON_IDENTITY,
							PRODUCT_GROUP_SOLUTION_ADDON_FEATURES,
							PRODUCT_GROUP_SOLUTION_POOL_DATA,
							PRODUCT_GROUP_SOLUTION_POOL_ILD,
							PRODUCT_GROUP_USER_ADDON,
							PRODUCT_GROUP_USER_ADDON_ROAMING_ILD,
							PRODUCT_GROUP_USER_ADDON_FUNCTIONS,
							PRODUCT_GROUP_SWITCHBOARD,
							PRODUCT_GROUP_SWITCHBOARD_ADDON,
							PRODUCT_GROUP_SWITCHBOARD_INCLUDED,
							PRODUCT_GROUP_SWITCHBOARD_SERVICE,
							PRODUCT_GROUP_PRODUCTION_OUTPUT,
							PRODUCT_GROUP_PRODUCTION_OUTPUT_CDM)) {
						continue;
					}
				} else {
					if (((MobileProductGroup) product.getProductGroup()).isOfType(
							PRODUCT_GROUP_PARTNER_HARDWARE,
							PRODUCT_GROUP_PARTNER_HARDWARE_FEATURE_PHONES,
							PRODUCT_GROUP_PARTNER_HARDWARE_MOBILT_BREDBAAND,
							PRODUCT_GROUP_PARTNER_HARDWARE_IP_FASTNET,
							PRODUCT_GROUP_PARTNER_HARDWARE_HEADSETS,
							PRODUCT_GROUP_PARTNER_BUNDLE,
							PRODUCT_GROUP_PARTNER_BUNDLE_ELEMENTS)) {
						continue;
					}
				}
			} else {
				MobileProductBundle bundle = countProductOrBundleAmount.getProductBundle();
				if (partnerVersion) {
					if (!bundle.getBundleType().equals(HARDWARE_BUNDLE)) {
						continue;
					}
				}
			}

			String notes = "";
			MobileProduct p = countProductOrBundleAmount.getProductSafely();
			if (p != null) {
				if (p.isRabataftaleDiscountEligible()) {
					notes = "R";
				}
				if (p.isIpsaDiscountEligible()) {
					notes = "I";
				}
				if (p.isDiscountEligible()) {
					notes = "M";
				}
			}
			MobileProductBundle b = countProductOrBundleAmount.getProductBundleSafely();
			if (b != null) {
				if (ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION == b.getAddToContractDiscount().intValue()) {
					notes = "R";
				}
				if (ProductBundle.IPSA_DISCOUNT_CONTRIBUTION == b.getAddToContractDiscount().intValue()) {
					notes = "I";
				}
				if (ProductBundle.FIXED_DISCOUNT_CONTRIBUTION == b.getAddToContractDiscount().intValue()) {
					notes = "M";
				}
			}

			boolean extraRowInOutput = false;
			String extraRowCode = null;
			String extraRowText = null;

			// Betydning af "Ekstra linie" flag:
			//
			// Non-recurring - når brutto er 100 og rabat er 20:
			// "ekstra linie" =>
			// PRODUKT......................100
			// ...
			// Kampagnerabat.................20
			//
			// "ingen ekstra linie" =>
			// PRODUKT.......................80
			//
			// Recurring - når brutto er 100 og rabat er 20:
			// "ekstra linie" =>
			// PRODUKT......................100
			// EKSTRA TEKST.................-20
			// ...
			// Kampagnerabat..................0
			//
			// "ingen ekstra linie" =>
			// PRODUKT.......................80

			// CDM Code and text may be overridden for products in campaigns
			String code = countProductOrBundleAmount.getProductId();
			String text = countProductOrBundleAmount.getInternalName().replace("&", "&amp;");
			if ((countProductOrBundleAmount.isProduct()
					&& (getCampaignProductRelation(countProductOrBundleAmount.getProduct()) != null))) {
				CampaignProductRelation campaignProductRelation = getCampaignProductRelation(
						countProductOrBundleAmount.getProduct());
				if (!StringUtils.isEmpty(campaignProductRelation.getOutputCodeOverride())) {
					code = campaignProductRelation.getOutputCodeOverride();
				}
				if (!StringUtils.isEmpty(campaignProductRelation.getOutputTextOverride())) {
					text = campaignProductRelation.getOutputTextOverride();
				}
			}

			if (!getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CONTRACT_DISCOUNT)) {
				if ((countProductOrBundleAmount.isProduct()
						&& (getCampaignProductRelation(countProductOrBundleAmount.getProduct()) != null)
						&& getCampaignProductRelation(countProductOrBundleAmount.getProduct()).isExtraRowInOutput())) {
					extraRowInOutput = true;
					extraRowCode = getCampaignProductRelation(countProductOrBundleAmount.getProduct())
							.getExtraOutputCode();
					extraRowText = getCampaignProductRelation(countProductOrBundleAmount.getProduct())
							.getExtraOutputText();
				} else if ((!countProductOrBundleAmount.isProduct()
						&& countProductOrBundleAmount.getProductBundle().isExtraRowInOutput())) {
					extraRowInOutput = true;
					extraRowCode = countProductOrBundleAmount.getProductBundle().getExtraRowInOutputCode();
					extraRowText = countProductOrBundleAmount.getProductBundle().getExtraRowInOutputText();
				}
			}
			
			if (countProductOrBundleAmount.getCount().getCountNew() > 0) {
				if (extraRowInOutput) {
					addToCdmList(list, totals, String.valueOf(countProductOrBundleAmount.getCount().getCountNew()), code, text,
							countProductOrBundleAmount.getBaseAmounts().getNonRecurringFees(),
							countProductOrBundleAmount.getBaseAmounts().getRecurringFee()
											* countProductOrBundleAmount.getCount().getCountNew()
											/ countProductOrBundleAmount.getCount().getCountTotal(),
							notes + "N");
					// Discount for recurring goes into extra line
					addToCdmList(list, totals, String.valueOf(countProductOrBundleAmount.getCount().getCountNew()),
							extraRowCode, extraRowText, 
							null, // Amounts.getFormattedWithDecimals(-countProductOrBundleAmount.getCampaignDiscountAmounts().getNonRecurringFees()),
							-countProductOrBundleAmount.getCampaignDiscountsWithContractDiscountsDeducted()
											.getRecurringFee() * countProductOrBundleAmount.getCount().getCountNew()
											/ countProductOrBundleAmount.getCount().getCountTotal(),
							notes + "N");
					// Discount for nonrecurring goes into "Kampagnerabat"
					totalCampaignDiscounts.add(countProductOrBundleAmount.getCampaignDiscountAmounts());
				} else {

					if (getBusinessArea().hasFeature(FeatureType.TDC_OFFICE)) {
						{
							String header = "Office365 bruger licenser:";
							if (!headers.contains(header) && (!countProductOrBundleAmount.isProduct()) && (countProductOrBundleAmount.getProductBundle().getBundleType().equals(MobileProductBundleEnum.OFFICE_BUNDLE))) {
								headers.add(header);
								addDirectiveToCdmList(list, "BREAK", " ");
								addDirectiveToCdmList(list, "BUNDLE_TITLE", header);
							}
						}
						{
							String header = "Office365 tenant:";
							if (!headers.contains(header) && (countProductOrBundleAmount.isProduct()) && (countProductOrBundleAmount.getProduct().getProductGroup().getUniqueName().equals(MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_BASIC.getKey()))) {
								headers.add(header);
								addDirectiveToCdmList(list, "BREAK", " ");
								addDirectiveToCdmList(list, "BUNDLE_TITLE", header);
							}
						}
						{
							String header = "Tilvalg:";
							if (!headers.contains(header) && (countProductOrBundleAmount.isProduct()) && (countProductOrBundleAmount.getProduct().getProductGroup().getUniqueName().equals(MobileProductGroupEnum.PRODUCT_GROUP_TDC_OFFICE_ADDON.getKey()))) {
								headers.add(header);
								addDirectiveToCdmList(list, "BREAK", " ");
								addDirectiveToCdmList(list, "BUNDLE_TITLE", header);
							}
						}
					}
					
					if (getBusinessArea().hasFeature(FeatureType.TDC_OFFICE)) {
						addToCdmListEvenIfZero(list, totals, String.valueOf(countProductOrBundleAmount.getCount().getCountNew()), code, text,
								countProductOrBundleAmount
										.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees(),
								countProductOrBundleAmount.getAmountsAfterCampaignAndContractDiscounts()
												.getRecurringFee() * countProductOrBundleAmount.getCount().getCountNew()
												/ countProductOrBundleAmount.getCount().getCountTotal(),
								"");
					} else {
						if (!handleProductAsPartOfLocation(countProductOrBundleAmount)) {
							if (getBusinessArea().isOnePlus()
									&& (countProductOrBundleAmount.getProductBundleSafely() != null)
									&& (((MobileProductBundle) countProductOrBundleAmount.getProductBundleSafely()).getBundleType().equals(HARDWARE_BUNDLE))) {
								// Ignore this
							} else {
								if (MobileSession.get().isBusinessAreaOnePlus()
										&& countProductOrBundleAmount.getProductSafely() != null
										&& StringUtils.equals("Mobile Only", countProductOrBundleAmount.getProductSafely().getInternalName())) {
									addToCdmListEvenIfZero(list, totals, String.valueOf(countProductOrBundleAmount.getCount().getCountNew()),
											code, text, countProductOrBundleAmount
													.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees(),
											countProductOrBundleAmount.getAmountsAfterCampaignAndContractDiscounts()
													.getRecurringFee() * countProductOrBundleAmount.getCount().getCountNew()
													/ countProductOrBundleAmount.getCount().getCountTotal(),
											notes + "N");
								} else {
									addToCdmList(list, totals, String.valueOf(countProductOrBundleAmount.getCount().getCountNew()), code, text,
											countProductOrBundleAmount
													.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees(),
											countProductOrBundleAmount.getAmountsAfterCampaignAndContractDiscounts()
													.getRecurringFee() * countProductOrBundleAmount.getCount().getCountNew()
													/ countProductOrBundleAmount.getCount().getCountTotal(),
											notes + "N");
								}
							}
						}
					}
				}
			}

			if (countProductOrBundleAmount.getCount().getCountExisting() > 0) {
				if (extraRowInOutput) {
					addToCdmList(list, totals, String.valueOf(countProductOrBundleAmount.getCount().getCountExisting()), code,
							text,
							countProductOrBundleAmount.getBaseAmounts().getNonRecurringFees(),
							countProductOrBundleAmount.getBaseAmounts().getRecurringFee()
											* countProductOrBundleAmount.getCount().getCountExisting()
											/ countProductOrBundleAmount.getCount().getCountTotal(),
							notes + "G");
					// Discount for recurring goes into extra line
					addToCdmList(list, totals, String.valueOf(countProductOrBundleAmount.getCount().getCountExisting()),
							extraRowCode, extraRowText, 
							null, // Amounts.getFormattedWithDecimals(-countProductOrBundleAmount.getCampaignDiscountAmounts().getNonRecurringFees()),
							-countProductOrBundleAmount
									.getCampaignDiscountsWithContractDiscountsDeducted().getRecurringFee()
									* countProductOrBundleAmount.getCount().getCountExisting()
									/ countProductOrBundleAmount.getCount().getCountTotal(),
							notes + "G");
					// Discount for nonrecurring goes into "Kampagnerabat"
					totalCampaignDiscounts.add(countProductOrBundleAmount.getCampaignDiscountAmounts());
				} else {
					addToCdmList(list, totals, String.valueOf(countProductOrBundleAmount.getCount().getCountExisting()), code,
							text, 
							null,
							countProductOrBundleAmount
									.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
									* countProductOrBundleAmount.getCount().getCountExisting()
									/ countProductOrBundleAmount.getCount().getCountTotal(),
							notes + "G");
				}
			}
		}  // end of: for (CountProductOrBundleAmounts countProductOrBundleAmount : sortedCountProductOrBundleAmounts) {

		if (businessArea.isOnePlus()) {
			Map<Product, MutableInt> productToTotalCountAcrossSubIndexes = new HashMap<>();
			for (MobileProductGroupEnum mainGroupType : new MobileProductGroupEnum[] {
					MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE, MobileProductGroupEnum.PRODUCT_GROUP_ACCESS, MobileProductGroupEnum.PRODUCT_GROUP_LOCATIONS }) {
				MobileProductGroup mainGroup = (MobileProductGroup) businessArea.getProductGroupByUniqueName(mainGroupType.getKey());
				for (ProductGroup mobileProductGroup : mainGroup.getChildProductGroups()) {
					for (OrderLine orderLine: getOrderLines(mobileProductGroup)) {
						if (orderLine.getProduct() != null) {
							MutableInt i = productToTotalCountAcrossSubIndexes.get(orderLine.getProduct());
							if (i == null) {
								productToTotalCountAcrossSubIndexes.put(orderLine.getProduct(), new MutableInt(orderLine.getCountNew()));
							} else {
								i.add(orderLine.getCountNew());
							}
						}
					}
				}
			}

			for (Integer locationIndex = 0; locationIndex < getLocationBundles().size(); locationIndex++) {
				LocationBundleData locationBundleData = getLocationBundles().get(locationIndex);
				String locationRemark = " ";

				if (locationBundleData.getAccessType() == AccessTypeEnum.NONE.getId()) {
					addDirectiveToCdmList(list, "BREAK", " ");
					addDirectiveToCdmList(list, "BUNDLE_TITLE", "Lokation " + (locationIndex + 1) + " : " + locationBundleData.getAddress());
					addDirectiveToCdmList(list, "BUNDLE_TITLE", "Access type - Ingen");
				} else if (locationBundleData.getAccessType() == AccessTypeEnum.XDSL.getId()) {
					OneXdslBundleData bundleData = getOneXdslBundles().get(locationIndex);

					MobileSession.get().setPricingSubIndex(locationIndex);
					addDirectiveToCdmList(list, "BREAK", " ");

					addDirectiveToCdmList(list, "BUNDLE_TITLE", "Lokation " + (locationIndex + 1) + " : " + locationBundleData.getAddress());
					addDirectiveToCdmList(list, "BUNDLE_TITLE", "Access type - xDSL");
					if (!StringUtils.isEmpty(bundleData.getSms())) {
						addDirectiveToCdmList(list, "BUNDLE_TITLE", "Mobil nr. til SMS varsling - " + bundleData.getSms());
					}
					if (!StringUtils.isEmpty(bundleData.getContactName())) {
						addDirectiveToCdmList(list, "BUNDLE_TITLE", "Kontaktperson overvågning - " + bundleData.getContactName());
					}
					if (!StringUtils.isEmpty(bundleData.getContactPhone())) {
						addDirectiveToCdmList(list, "BUNDLE_TITLE", "Kontaktperson telefonnummer overvågning - " + bundleData.getContactPhone());
					}
					if (!StringUtils.isEmpty(bundleData.getContactEmail())) {
						addDirectiveToCdmList(list, "BUNDLE_TITLE", "Kontaktperson email overvågning - " + bundleData.getContactEmail());
					}
					if (!StringUtils.isEmpty(bundleData.getTechPhone())) {
						addDirectiveToCdmList(list, "BUNDLE_TITLE", "Telefonnummer til teknikerbesøg - " + bundleData.getTechPhone());
					}
					if (!StringUtils.isEmpty(bundleData.getLineNo())) {
						addDirectiveToCdmList(list, "BUNDLE_TITLE", "Linje nr. ved genforhandling/konvertering af access - " + bundleData.getLineNo());
					}

					if (Boolean.TRUE.equals(bundleData.getReuseHardware())) {
						addDirectiveToCdmList(list, "BUNDLE_TITLE", "Eksisterende udstyr genbruges hvis muligt, valider i CU");
					}

//					MobileProduct product = (MobileProduct) businessArea.getProductById(bundleData.getSpeedEntityId());
//					if (product == null) {
//						continue;
//					}

					switch (bundleData.getMode()) {
						case MODE_NYSALG:
							locationRemark = "N";
							break;
						case OneXdslBundleData.MODE_GENFORHANDLING:
							locationRemark = "G";
							break;
						case OneXdslBundleData.MODE_KONVERTERING:
							locationRemark = "Æ";
							break;
					}
				}

				boolean hardwareHeaderPrinted 		= false;
				boolean installationHeaderPrinted 	= false;

				for (MobileProductGroupEnum mainGroupType : new MobileProductGroupEnum[] {
						MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE, MobileProductGroupEnum.PRODUCT_GROUP_ACCESS, MobileProductGroupEnum.PRODUCT_GROUP_LOCATIONS }) {
					MobileProductGroup mainGroup = (MobileProductGroup) businessArea.getProductGroupByUniqueName(mainGroupType.getKey());
					for (ProductGroup mobileProductGroup : mainGroup.getChildProductGroups()) {
						Map<MobileProduct, Integer> productToCountMap = new HashMap<>();

						for (OrderLine orderLine: getOrderLines(mobileProductGroup, locationIndex)) {
							if (partnerVersion) {
								if (((MobileProduct) orderLine.getProduct()).isInGroup(
										PRODUCT_GROUP_XDSL_BUNDLE_SPEED,
										PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED,
										PRODUCT_GROUP_XDSL_BUNDLE_MISC,
										PRODUCT_GROUP_XDSL_BUNDLE_SUPERVISION,
										PRODUCT_GROUP_ACCESS_INCLUDED,
										PRODUCT_GROUP_ACCESS_IP,
										PRODUCT_GROUP_ACCESS_QOS,
										PRODUCT_GROUP_ACCESS_REDUNDANCY,
										PRODUCT_GROUP_ACCESS_SERVICE,
										PRODUCT_GROUP_ACCESS_SUPERVISION,
										PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES,
										PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC,
										PRODUCT_GROUP_LOCATIONS_HARDWARE_IP)) {
									continue;
								}
							}
							if (PRODUCT_GROUP_LOCATIONS_INSTALLATION.getKey().equals(mobileProductGroup.getUniqueName())) {
								Integer subIndex = orderLine.getSubIndex();
								if (subIndex == null) {
									log.info("How is this possible? " + orderLine);
								} else {
									LocationBundleData locationBundle = getLocationBundle(subIndex);
									if ((partnerVersion && !locationBundle.isTDCInstallationProvider()) ||
										(!partnerVersion && locationBundle.isTDCInstallationProvider())) {
										if (!installationHeaderPrinted) {
											installationHeaderPrinted = true;
											addDirectiveToCdmList(list, "BUNDLE_TITLE", "Installation");
										}
										productToCountMap.put((MobileProduct) orderLine.getProduct(), orderLine.getCountNew());
									}
								}
							} else if (PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES.getKey().equals(mobileProductGroup.getUniqueName()) ||
										PRODUCT_GROUP_LOCATIONS_HARDWARE_IP.getKey().equals(mobileProductGroup.getUniqueName()) ||
										PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC.getKey().equals(mobileProductGroup.getUniqueName())) {
								Integer subIndex = orderLine.getSubIndex();
								if (subIndex == null) {
									log.info("How is this possible? " + orderLine);
								} else {
									LocationBundleData locationBundle = getLocationBundle(subIndex);
									if (locationBundle.isTDCHardwareProvider()
											|| PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES.getKey().equals(mobileProductGroup.getUniqueName())
											|| (("Forsendelsesgebyr".equals(orderLine.getProduct().getPublicName())) && !partnerVersion)) {
										if (!hardwareHeaderPrinted) {
											hardwareHeaderPrinted = true;
											addDirectiveToCdmList(list, "BUNDLE_TITLE", "Hardware");
										}
										productToCountMap.put((MobileProduct) orderLine.getProduct(), orderLine.getCountNew());
									}
								}
							} else {
								productToCountMap.put((MobileProduct) orderLine.getProduct(), orderLine.getCountNew());
							}
						}

						for (Product p : sortByOutputSortIndex(new ArrayList<>(productToCountMap.keySet()))) {
							CountProductOrBundleAmounts countAndAmounts = productToCount.get(p);
							MutableInt totalCount = productToTotalCountAcrossSubIndexes.get(p);
							int countForLocation = productToCountMap.get(p);
							if (!((MobileProduct) p).isExcludeFromProductionOutput() || !countAndAmounts.getAmounts().isAllZero()) {
								if (totalCount == null) {
									log.info("totalCount == null");
								} else if (countAndAmounts == null) {
									log.info("countAndAmounts == null / " +  p.getPublicName());
								} else if (countAndAmounts.getAmountsAfterCampaignAndContractDiscounts() == null) {
									log.info("countAndAmounts.getAmountsAfterCampaignAndContractDiscounts() == null / " +  p.getPublicName());
								} else if (totalCount.intValue() == 0) {
									log.info("totalCount.intValue() == 0 / " +  p.getPublicName());
								} else {
									String remark = " ";
									if (((MobileProductGroup) p.getProductGroup()).isOfType(
											PRODUCT_GROUP_XDSL_BUNDLE,
											PRODUCT_GROUP_XDSL_BUNDLE_SUPERVISION,
											PRODUCT_GROUP_XDSL_BUNDLE_MISC,
											PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED,
											PRODUCT_GROUP_XDSL_BUNDLE_SPEED,
											PRODUCT_GROUP_ACCESS_INCLUDED,
											PRODUCT_GROUP_ACCESS_QOS,
											PRODUCT_GROUP_ACCESS_SUPERVISION,
											PRODUCT_GROUP_ACCESS_SERVICE,
											PRODUCT_GROUP_ACCESS_IP,
											PRODUCT_GROUP_ACCESS_REDUNDANCY)) {
										remark = locationRemark;
									} else {
										if (countAndAmounts.getCountNew() > 0) {
											remark = "N";
										} else if (countAndAmounts.getCountExisting() > 0) {
											remark = "G";
										} else {
											remark = "?";
										}
									}
									addToCdmListEvenIfZero(list, totals, String.valueOf(countForLocation), p.getProductId(), p.getInternalName(),
											countForLocation * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees() / totalCount.intValue(),
											countForLocation * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee() / totalCount.intValue(),
											remark);
								}
							}
						}
					}
				}
			}
		} else {
			for (XdslBundleData bundleIds : getXdslBundles()) {
				addDirectiveToCdmList(list, "BREAK", " ");

				CountProductOrBundleAmounts countAndAmounts;

				{
					Product product = businessArea.getProductById(bundleIds.getSpeedEntityId());
					if (product == null) {
						log.warn("Product is null: " + bundleIds.getSpeedEntityId());
					} else {
						countAndAmounts = productToCount.get(product);
						addToCdmList(list, totals, String.valueOf(1), product.getProductId(), product.getInternalName(),
								countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
										/ countAndAmounts.getCount().getCountTotal(),
								countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
										/ countAndAmounts.getCount().getCountTotal(),
								" ");
					}
				}

				{
					for (Long entityId : bundleIds.getProductEntityIds()) {
						Product product = businessArea.getProductById(entityId);
						countAndAmounts = productToCount.get(product);
						addToCdmListEvenIfZero(list, totals, String.valueOf(1), product.getProductId(), product.getInternalName(),
								countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
										/ countAndAmounts.getCount().getCountTotal(),
								countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
										/ countAndAmounts.getCount().getCountTotal(),
								" ");
					}
//				countAndAmounts = productToCount.get(product);
//
//				addToCdmList(list, totals, String.valueOf(1), product.getProductId(), product.getInternalName(),
//						countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
//						/ countAndAmounts.getCount().getCountTotal(),
//						countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
//						/ countAndAmounts.getCount().getCountTotal(),
//						" ");
				}

				for (MobileProductGroupEnum group : new MobileProductGroupEnum[] {
						MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED
//					,MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_MANAGED_DEVICES
//					,MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE_CUSTOMER_DEVICES
				}) {
					for (Product p : businessArea.getProductGroupByUniqueName(group.getKey()).getProducts()) {
						countAndAmounts = productToCount.get(p);
						if ((countAndAmounts != null) && (countAndAmounts.getCount().getCountTotal() > 0)) {
							addToCdmList(list, totals, String.valueOf(1), p.getProductId(),
									p.getInternalName(),
									countAndAmounts.getAmountsAfterCampaignAndContractDiscounts()
											.getNonRecurringFees() / countAndAmounts.getCount().getCountTotal(),
									countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
											/ countAndAmounts.getCount().getCountTotal(),
									" ");
						}
					}
				}
			}

			{
				// Fiber bundles (Fiber Erhverv Plus)
				// The output here is based on orderlines which is really the best way to do it.
				MobileProductGroup mainGroup = (MobileProductGroup) businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE.getKey());
				if (mainGroup != null) {
					Map<Product, MutableInt> productToTotalCountAcrossSubIndexes = new HashMap<>();
					for (ProductGroup mobileProductGroup : mainGroup.getChildProductGroups()) {
						for (OrderLine orderLine: getOrderLines(mobileProductGroup)) {
							if (orderLine.getProduct() != null) {
								MutableInt i = productToTotalCountAcrossSubIndexes.get(orderLine.getProduct());
								if (i == null) {
									productToTotalCountAcrossSubIndexes.put(orderLine.getProduct(), new MutableInt(orderLine.getCountNew()));
								} else {
									i.add(orderLine.getCountNew());
								}
							}
						}
					}
					for (int locationIndex = 0; locationIndex < getFiberErhvervPlusBundles().size(); locationIndex++) {
						FiberErhvervPlusBundleData bundleData = getFiberErhvervPlusBundles().get(locationIndex);
						MobileSession.get().setPricingSubIndex(locationIndex);
						addDirectiveToCdmList(list, "BREAK", " ");
						String campaignCode = "Ingen";
						if (bundleData.getCampaign().indexOf(" A ") > -1) {
							campaignCode = "FAQ" + QuarterUtils.getQuarterNo(LocalDate.now());
						} else if (bundleData.getCampaign().indexOf(" B ") > -1) {
							campaignCode = "FBQ" + QuarterUtils.getQuarterNo(LocalDate.now());
						}
						addDirectiveToCdmList(list, "BUNDLE_TITLE", "Fiber - Lokation " + (locationIndex + 1) + " - 24 mdr. - Kampagnekode " + campaignCode + " : " + bundleData.getAddress());

						MobileProduct product = (MobileProduct) businessArea.getProductById(bundleData.getFiberSpeedEntityId());
						if (product == null) {
							continue;
						}

						Map<MobileProduct, Integer> productToCountMap = new HashMap<>();
						productToCountMap.put(product, 1);

						mainGroup = (MobileProductGroup) businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE.getKey());
						for (ProductGroup mobileProductGroup : mainGroup.getChildProductGroups()) {
							for (OrderLine orderLine: getOrderLines(mobileProductGroup, locationIndex)) {
								productToCountMap.put((MobileProduct) orderLine.getProduct(), orderLine.getCountNew());
							}
						}

						for (Product p : businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_BUNDLE_ITEMS.getKey()).getProducts()) {
							if (p.getMinCount().intValue() == 1 && p.getMaxCount().intValue() == 1) {
								productToCountMap.put((MobileProduct) p, 1);
							}
						}

						List<MobileProduct> sortedProducts = new ArrayList<>(productToCountMap.keySet());
						Collections.sort(sortedProducts, new Comparator<MobileProduct>() {
							@Override
							public int compare(MobileProduct a, MobileProduct b) {
								if (((MobileProductGroup) a.getProductGroup()).getOutputSortIndex() == ((MobileProductGroup) b.getProductGroup()).getOutputSortIndex()) {
									return (Long.valueOf(a.getOutputSortIndex()).compareTo(b.getOutputSortIndex()));
								} else {
									return (Long.valueOf(((MobileProductGroup) a.getProductGroup()).getOutputSortIndex()).compareTo(((MobileProductGroup) b.getProductGroup()).getOutputSortIndex()));
								}
							}
						});

						for (Product p : sortedProducts) {
							CountProductOrBundleAmounts countAndAmounts = productToCount.get(p);
							MutableInt totalCount = productToTotalCountAcrossSubIndexes.get(p);
							int countForLocation = productToCountMap.get(p);
							if (!((MobileProduct) p).isExcludeFromProductionOutput() || !countAndAmounts.getAmounts().isAllZero()) {
								addToCdmListEvenIfZero(list, totals, String.valueOf(countForLocation), p.getProductId(), p.getInternalName(),
										countForLocation * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees() / totalCount.intValue(),
										countForLocation * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee() / totalCount.intValue(),
										" ");
							}
						}
					}
				}
			}

			{
				// Fiber bundles (Fiber Erhverv)
				// The output here is based on orderlines which is really the best way to do it.
				MobileProductGroup mainGroup = (MobileProductGroup) businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_NEW_BUNDLE.getKey());
				if (mainGroup != null) {
					Map<Product, MutableInt> productToTotalCountAcrossSubIndexes = new HashMap<>();
					for (ProductGroup mobileProductGroup : mainGroup.getChildProductGroups()) {
						for (OrderLine orderLine: getOrderLines(mobileProductGroup)) {
							if (orderLine.getProduct() != null) {
								MutableInt i = productToTotalCountAcrossSubIndexes.get(orderLine.getProduct());
								if (i == null) {
									productToTotalCountAcrossSubIndexes.put(orderLine.getProduct(), new MutableInt(orderLine.getCountNew()));
								} else {
									i.add(orderLine.getCountNew());
								}
							}
						}
					}

					{
						for (Integer locationIndex = 0; locationIndex < getFiberErhvervBundles().size(); locationIndex++) {
							FiberErhvervBundleData bundleData = getFiberErhvervBundles().get(locationIndex);
							MobileSession.get().setPricingSubIndex(locationIndex);
							addDirectiveToCdmList(list, "BREAK", " ");

							addDirectiveToCdmList(list, "BUNDLE_TITLE", "Fiber - Lokation " + (locationIndex + 1) + " : " + bundleData.getAddress());
							addDirectiveToCdmList(list, "BUNDLE_TITLE", "Infrastruktur : " + FiberErhvervBundleData.getInfrastructureAsString(bundleData.getInfrastructureTypeNo()));

							if (!bundleData.nullOrNoEntity(bundleData.getRedundancyEntityId())) {
								addDirectiveToCdmList(list, "BUNDLE_TITLE", "Mobilnummer til SMS varsling: " + bundleData.getSmsAlertNo());
							}

							if (!bundleData.nullOrNoEntity(bundleData.getSupervisionEntityId())) {
								addDirectiveToCdmList(list, "BUNDLE_TITLE", "Kontaktperson overvågning: " + bundleData.getContactSupervision());
								addDirectiveToCdmList(list, "BUNDLE_TITLE", "Kontakt telefonnummer overvågning: " + bundleData.getContactSupervisionPhone());
								addDirectiveToCdmList(list, "BUNDLE_TITLE", "Kontakt email overvågning: " + bundleData.getContactSupervisionEmail());
							}
							addDirectiveToCdmList(list, "BUNDLE_TITLE", "Tlf. 30 min før: " + (bundleData.getContactPhone30Minutes() == null ? "" : bundleData.getContactPhone30Minutes()));

							if (Boolean.TRUE.equals(bundleData.getInspection())) {
								addDirectiveToCdmList(list, "BUNDLE_TITLE", "Kontaktperson besigtigelse: " + bundleData.getContactInspection());
								addDirectiveToCdmList(list, "BUNDLE_TITLE", "Kontakt telefonnummer besigtigelse: " + bundleData.getContactInspectionPhone());
							}

							MobileProduct product = (MobileProduct) businessArea.getProductById(bundleData.getSpeedEntityId());
							if (product == null) {
								continue;
							}

							Map<MobileProduct, Integer> productToCountMap = new HashMap<>();

							mainGroup = (MobileProductGroup) businessArea.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_FIBER_NEW_BUNDLE.getKey());
							for (ProductGroup mobileProductGroup : mainGroup.getChildProductGroups()) {
								for (OrderLine orderLine: getOrderLines(mobileProductGroup, locationIndex)) {
									productToCountMap.put((MobileProduct) orderLine.getProduct(), orderLine.getCountNew());
								}
							}

							for (Product p : sortByOutputSortIndex(new ArrayList<>(productToCountMap.keySet()))) {
								CountProductOrBundleAmounts countAndAmounts = productToCount.get(p);
								MutableInt totalCount = productToTotalCountAcrossSubIndexes.get(p);
								int countForLocation = productToCountMap.get(p);
								if (!((MobileProduct) p).isExcludeFromProductionOutput() || !countAndAmounts.getAmounts().isAllZero()) {
									addToCdmListEvenIfZero(list, totals, String.valueOf(countForLocation), p.getProductId(), p.getInternalName(),
											countForLocation * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees() / totalCount.intValue(),
											countForLocation * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee() / totalCount.intValue(),
											" ");
								}
							}
						}
					}
				}
			}

			{
				// Wi-Fi bundles
				for (int locationIndex = 0; locationIndex < getWiFiBundles().size(); locationIndex++) {
					WiFiBundleIds bundleIds = getWiFiBundles().get(locationIndex);
					if (!bundleIds.isValid()) {
						continue;
					}
					MobileSession.get().setPricingSubIndex(locationIndex);

					addDirectiveToCdmList(list, "BREAK", " ");
					addDirectiveToCdmList(list, "BUNDLE_TITLE", "Wi-Fi - Lokation " + (locationIndex + 1) + ": " + bundleIds.getAddress());

					Product wifiProduct;
					CountProductOrBundleAmounts countAndAmounts;

//			for (Product serviceProduct : businessArea.getProductGroupByUniqueName(
//					MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_GENERAL_SERVICE.getKey()).getProducts()) {
//				countAndAmounts = productToCount.get(serviceProduct);
//				if ((countAndAmounts != null) && (countAndAmounts.getCount().getCountTotal() > 0)) {
//					addToCdmList(list, totals, String.valueOf(1), serviceProduct.getProductId(),
//							serviceProduct.getInternalName(),
//							countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
//											/ countAndAmounts.getCount().getCountTotal(),
//							countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
//											/ countAndAmounts.getCount().getCountTotal(),
//							" ");
//				}
//			}

					wifiProduct = businessArea.getProductById(bundleIds.getAccessPointEntityId());
					countAndAmounts = productToCount.get(wifiProduct);
					if (bundleIds == null) {
						log.info("bundleIds == null");
					} else if (bundleIds.getAccessPointCount() == null) {
						log.info("bundleIds.getAccessPointCount() == null");
					}
					if (wifiProduct == null) {
						log.info("wifiProduct == null");
					}
					if (countAndAmounts == null) {
						log.info("countAndAmounts == null");
					}
					addToCdmList(list, totals, String.valueOf(bundleIds.getAccessPointCount()), wifiProduct.getProductId(), wifiProduct.getInternalName(),
							bundleIds.getAccessPointCount() * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
									/ countAndAmounts.getCount().getCountTotal(),
							bundleIds.getAccessPointCount() * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
									/ countAndAmounts.getCount().getCountTotal(),
							" ");

					wifiProduct = businessArea.getProductById(bundleIds.getServiceLevelEntityId());
					countAndAmounts = productToCount.get(wifiProduct);
					addToCdmListEvenIfZero(list, totals, String.valueOf(bundleIds.getAccessPointCount()), wifiProduct.getProductId(), wifiProduct.getInternalName(),
							bundleIds.getAccessPointCount() * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
									/ countAndAmounts.getCount().getCountTotal(),
							bundleIds.getAccessPointCount() * countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
									/ countAndAmounts.getCount().getCountTotal(),
							" ");

					wifiProduct = businessArea.getProductById(bundleIds.getSwitchEntityId());
					countAndAmounts = productToCount.get(wifiProduct);
					addToCdmListEvenIfZero(list, totals, String.valueOf(1), wifiProduct.getProductId(), wifiProduct.getInternalName(),
							countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
									/ countAndAmounts.getCount().getCountTotal(),
							countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
									/ countAndAmounts.getCount().getCountTotal(),
							" ");

//			addToCdmList(list, totals, String.valueOf(bundleIds.getAccessPointCount()), wifiProduct.getProductId(), wifiProduct.getInternalName(),
//					countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
//					/ countAndAmounts.getCount().getCountTotal(),
//					countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
//					/ countAndAmounts.getCount().getCountTotal(),
//					" ");

					// wifiProduct =
					// businessArea.getProductById(bundleIds.getAreaSizeEntityId());
					// countAndAmounts = productToCount.get(wifiProduct);
					// addToCdmList(list, String.valueOf(1),
					// wifiProduct.getProductId(),
					// wifiProduct.getInternalName(),
					// Amounts.getFormattedWithDecimals(countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
					// / countAndAmounts.getCount().getCountTotal()),
					// Amounts.getFormattedWithDecimals(countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
					// / countAndAmounts.getCount().getCountTotal()),
					// " ");

					wifiProduct = businessArea.getProductById(bundleIds.getSiteSurveyEntityId());
					countAndAmounts = productToCount.get(wifiProduct);
					addToCdmList(list, totals, String.valueOf(1), wifiProduct.getProductId(), wifiProduct.getInternalName(),
							countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getNonRecurringFees()
									/ countAndAmounts.getCount().getCountTotal(),
							countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
									/ countAndAmounts.getCount().getCountTotal(),
							" ");

					for (Product cablingProduct : businessArea
							.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_WIFI_BUNDLE_CABLING.getKey())
							.getProducts()) {
						// Update for this location (by the magic of MobileSession.pricingSubIndex)
						MicroMap<Object, CountProductOrBundleAmounts> tmpMap = new MicroMap<>();

						OrderLine orderLine = getOrderLineBySubIndex(cablingProduct, locationIndex);
						if (orderLine != null) {
							updateDataForProduct(tmpMap, orderLine, cablingProduct, null, false);
							countAndAmounts = tmpMap.get(cablingProduct);

							// countAndAmounts = productToCount.get(cablingProduct);
							if ((countAndAmounts != null) && (countAndAmounts.getCount().getCountTotal() > 0)) {
								addToCdmList(list, totals, "" + countAndAmounts.getCount().getCountTotal(),
										cablingProduct.getProductId(), cablingProduct.getInternalName(),
										countAndAmounts.getAmountsAfterCampaignAndContractDiscounts()
												.getNonRecurringFees() / countAndAmounts.getCount().getCountTotal(),
										countAndAmounts.getAmountsAfterCampaignAndContractDiscounts().getRecurringFee()
												/ countAndAmounts.getCount().getCountTotal(),
										" ");
							}
						}
					}
				}
			}
		}


//		Amounts totals = getAmounts(false, getAcceptAllProductFilter()).clone();
//		totals.subtract(getContractDiscounts());
//		totals.subtract(getCampaignDiscounts());

		addToCdmList(list, totals, "1", "8257700", "Kampagnerabat",
				-totalCampaignDiscounts.getNonRecurringFees(), 
				null, 
				" ");
		// Amounts.getFormattedWithDecimals(totalCampaignDiscounts.getRecurringFee()),
		// " "});

		addDirectiveToCdmList(list, "BREAK", " ");
		addToCdmList(list, totals, "", "", "Samlet etablering / drift pr. mnd.", totals.getNonRecurringFees(), totals.getRecurringFee(), " ");

		return list;
	}

	private boolean handleProductAsPartOfLocation(CountProductOrBundleAmounts countProductOrBundleAmounts) {
		if (countProductOrBundleAmounts.isProduct()) {
			return (countProductOrBundleAmounts.getProduct().isNetworkProduct());
//			MobileProductGroup group = (MobileProductGroup) countProductOrBundleAmounts.getProduct().getProductGroup();
//			for (MobileProductGroupEnum groupType: new MobileProductGroupEnum[] {
//					MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE, MobileProductGroupEnum.PRODUCT_GROUP_ACCESS, MobileProductGroupEnum.PRODUCT_GROUP_LOCATIONS}) {
//				if (StringUtils.startsWith(group.getUniqueName(), groupType.getKey())) {
//					return true;
//				}
//			}
		}
		return false;
	}

	private List<MobileProduct> sortByOutputSortIndex(ArrayList list) {
		Collections.sort(list, new Comparator<MobileProduct>() {
			@Override
			public int compare(MobileProduct a, MobileProduct b) {
				if (((MobileProductGroup) a.getProductGroup()).getOutputSortIndex() == ((MobileProductGroup) b.getProductGroup()).getOutputSortIndex()) {
					return (Long.valueOf(a.getOutputSortIndex()).compareTo(b.getOutputSortIndex()));
				} else {
					return (Long.valueOf(((MobileProductGroup) a.getProductGroup()).getOutputSortIndex()).compareTo(((MobileProductGroup) b.getProductGroup()).getOutputSortIndex()));
				}
			}
		});
		return list;
	}

	private MobileProductGroup getProductGroup(BusinessArea businessArea, MobileProductGroupEnum groupValue) {
		for (ProductGroup productGroup : businessArea.getProductGroups()) {
			if (productGroup.getUniqueName().equals(groupValue.getKey())) {
				return (MobileProductGroup) productGroup;
			}
			for (ProductGroup pg : productGroup.getAll()) {
				if (pg.getUniqueName().equals(groupValue.getKey())) {
					return (MobileProductGroup) pg;
				}
			}
		}
		return null;
	}

	/**
	 * @param list
	 * @param count
	 * @param code
	 * @param text
	 * @param nonRecurring
	 * @param recurring
	 * @param remark
	 */
	private void addToCdmList(List<String[]> list, Amounts total, String count, String code, String text, Long nonRecurring, Long recurring, String remark) {
		if (((!Long.valueOf(0).equals(nonRecurring)) && (nonRecurring != null))
				|| ((!Long.valueOf(0).equals(recurring)) && (recurring != null))) {
			addToCdmListEvenIfZero(list, total, count, code, text, nonRecurring, recurring, remark);
		}
	}

	/**
	 * @param list
	 * @param count
	 * @param code
	 * @param text
	 * @param nonRecurring
	 * @param recurring
	 * @param remark
	 */
	private void addToCdmListEvenIfZero(List<String[]> list, Amounts total, String count, String code, String text, Long nonRecurring, Long recurring, String remark) {
		if (businessArea.getBusinessAreaId() == BusinessAreas.WIFI) {
			list.add(new String[] { count, code, text, nonRecurring == null ? "" : Amounts.getFormattedWithDecimals(nonRecurring), recurring == null ? "" : Amounts.getFormattedWithDecimals(recurring), "" });
		} else {
			list.add(new String[] { count, code, text, nonRecurring == null ? "" : Amounts.getFormattedWithDecimals(nonRecurring), recurring == null ? "" : Amounts.getFormattedWithDecimals(recurring), remark });
		}
		if (nonRecurring != null) {
			total.add(new Amounts(0, nonRecurring, 0));
		}
		if (recurring != null) {
			total.add(new Amounts(0, 0, recurring));
		}
	}

	/**
	 * @param list
	 * @param count
	 * @param code
	 */
	private void addDirectiveToCdmList(List<String[]> list, String count, String code) {
		if (list != null && list.size() > 0 && "BREAK".equals(count) && "BREAK".equals(list.get(list.size()-1)[0])) {
			return;
		}
		list.add(new String[] { count, code, " ", " ", " ", "" });
	}

	/**
	 * @param list
	 * @param count
	 * @param code
	 * @param text
	 * @param nonRecurring
	 * @param recurring
	 * @param remark
	 */
	private void addFortaelleNrToCdmList(List list, String count, String code, String text, String nonRecurring, String recurring, String remark) {
		list.add(new String[] { count, code, text, nonRecurring, recurring, remark });
	}

	@Transient
	public OrderLine getOrderLineByNabsCode(String nabsCode) {
		for (OrderLine orderLine : orderLines) {
			if (orderLine.getProduct() != null) {
				if (nabsCode.equals(((MobileProduct) orderLine.getProduct()).getNabsCode())) {
					return orderLine;
				}
			}
			if (orderLine.getBundle() != null) {
				for (BundleProductRelation productRelation : orderLine.getBundle().getProducts()) {
					if ((productRelation.getProduct() != null)
							&& nabsCode.equals(((MobileProduct) productRelation.getProduct()).getNabsCode())) {
						return orderLine;
					}
				}
			}
		}
		return null;
	}

	@Transient
	public int getIncompleteSubscriptions() {
		boolean isOffice = MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE; 
		
		int count = 0;
		for (Subscription s : getSubscriptions()) {
	        if (isOffice) {
				if (StringUtils.isEmpty(s.getFirstName()) || StringUtils.isEmpty(s.getLastName()) || StringUtils.isEmpty(s.getEmail())) {
					count++;
				}
	        } else {
				if ((s.getSimCardType() == null) || (s.getDatadelingSimCardType() == null)
						|| (s.getNumberTransferType() == null) || StringUtils.isEmpty(s.getDivision())
						|| StringUtils.isEmpty(s.getName())) {
					count++;
				} else {
					if ((!NumberTransferType.NEW.equals(s.getNumberTransferType()))
							&& (StringUtils.isEmpty(s.getMobileNumber())
									|| StringUtils.isEmpty(s.getIcc()))) {
						count++;
					}
				}
	        }
		}
		return count;
	}
	
	@Transient
	public int getDuplicateEmailsInSubscriptions() {
		boolean isOffice = MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE;
		Set<String> duplicates = new HashSet<>();
		if (isOffice) {
			Set<String> emails = new HashSet<>();
			for (Subscription s : getSubscriptions()) {
				if (!StringUtils.isEmpty(s.getEmail())) {
					if (emails.contains(s.getEmail())) {
						duplicates.add(s.getEmail());
					} else {
						emails.add(s.getEmail());
					}
				}
			}
		}
		return duplicates.size();
	}

	@Transient
	public int getUnassignedAddonProducts() {
		int unassignedCount = 0;
		for (OrderLine orderLine : getOrderLines()) {
			MobileProduct product = (MobileProduct) orderLine.getProduct();
			if (product != null) {
				if (product.isAddOn()) {
					if (!orderLine.isCustomFlag()) {
						int remainingCount = orderLine.getTotalCount();
						for (Subscription subscription : getSubscriptions()) {
							if (subscription.getProducts().contains(product)) {
								remainingCount--;
							}
						}
						unassignedCount += remainingCount;
					}
				}
			}
		}
		return unassignedCount;
	}

	@Transient
	public Integer getCountTotalForProduct(Product product) {
		Integer count = 0;
		for (OrderLine orderLine : getOrderLines()) {
			if (orderLine.getProduct() != null) {
				if (orderLine.getProduct().getId().equals(product.getId())) {
					if (orderLine.isCustomFlag()) {
						count = null; // "All"
					} else {
						count = orderLine.getTotalCount();
					}
					break;
				}
			}
		}
		return count;
	}

	@Transient
	public Integer getCountNewForProduct(Product product) {
		Integer count = 0;
		for (OrderLine orderLine : getOrderLines()) {
			if (orderLine.getProduct() != null) {
				if (orderLine.getProduct().getId().equals(product.getId())) {
					if (orderLine.isCustomFlag()) {
						count = null; // "All"
					} else {
						count = orderLine.getCountNew();
					}
					break;
				}
			}
		}
		return count;
	}

	@Transient
	public Integer getCountExistingForProduct(Product product) {
		Integer count = 0;
		for (OrderLine orderLine : getOrderLines()) {
			if (orderLine.getProduct() != null) {
				if (orderLine.getProduct().getId().equals(product.getId())) {
					// if (orderLine.isCustomFlag()) {
					// count = null; // "All"
					// } else {
					count = orderLine.getCountExisting();
					// }
					break;
				}
			}
		}
		return count;
	}

	@Transient
	public Integer getCountNewForProduct(Product product, int subIndex) {
		Integer count = 0;
		for (OrderLine orderLine : getOrderLines()) {
			if (orderLine.getProduct() != null) {
				if (orderLine.getProduct().getId().equals(product.getId()) && (subIndex == orderLine.getSubIndex())) {
					if (orderLine.isCustomFlag()) {
						count = null; // "All"
					} else {
						count = orderLine.getCountNew();
					}
					break;
				}
			}
		}
		return count;
	}

	@Transient
	public Integer getCountExistingForProduct(Product product, int subIndex) {
		Integer count = 0;
		for (OrderLine orderLine : getOrderLines()) {
			if (orderLine.getProduct() != null) {
				if (orderLine.getProduct().getId().equals(product.getId()) && (subIndex == orderLine.getSubIndex())) {
					// if (orderLine.isCustomFlag()) {
					// count = null; // "All"
					// } else {
					count = orderLine.getCountExisting();
					// }
					break;
				}
			}
		}
		return count;
	}

	@Transient
	public Integer getCountNewForBundle(ProductBundle productBundle) {
		Integer count = 0;
		for (OrderLine orderLine : getOrderLines()) {
			if (orderLine.getBundle() != null) {
				if (orderLine.getBundle().getId().equals(productBundle.getId())) {
					if (orderLine.isCustomFlag()) {
						count = null; // "All"
					} else {
						count = orderLine.getCountNew();
					}
					break;
				}
			}
		}
		return count;
	}

	@Transient
	public Integer getCountNewForBundle(ProductBundle productBundle, int subIndex) {
		Integer count = 0;
		for (OrderLine orderLine : getOrderLines()) {
			if (orderLine.getBundle() != null) {
				if (orderLine.getBundle().getId().equals(productBundle.getId()) && (subIndex == orderLine.getSubIndex())) {
					if (orderLine.isCustomFlag()) {
						count = null; // "All"
					} else {
						count = orderLine.getCountNew();
					}
					break;
				}
			}
		}
		return count;
	}

	/**
	 * Calculate IPSA sum pr. year. Note: Before-discount amounts must be used.
	 * 
	 * @return IPSA sum
	 */
	@Transient
	public long getIpsaSumPrYear() {
		long sum = 0;
		Map<Object, CountProductOrBundleAmounts> productOrBundleToCount = getProductOrBundleCountInOrderLines(false, false);
		for (Object productOrBundle : productOrBundleToCount.keySet()) {
			CountProductOrBundleAmounts count = productOrBundleToCount.get(productOrBundle);

			if (productOrBundle instanceof MobileProduct) {
				MobileProduct product = (MobileProduct) productOrBundle;
				if (product.isIpsaDiscountEligible()) {
					sum += 12 * count.getBaseAmounts().getRecurringFee();
				}
			} else {
				MobileProductBundle bundle = (MobileProductBundle) productOrBundle;
				// if
				// (bundle.getBundleType().equals(MobileProductBundleEnum.SWITCHBOARD_BUNDLE))
				// {
				if (ProductBundle.IPSA_DISCOUNT_CONTRIBUTION == bundle.getAddToContractDiscount().intValue()) {
					sum += 12 * count.getBaseAmounts().getRecurringFee();
				}
			}
		}
		return sum;
	}

	/**
	 * Calculate GKS sum pr. year. Note: Before-discount amounts must be used.
	 * 
	 * @return GKS sum
	 */
	@Transient
	public long getGksSumPrYear() {
		long sum = 0;
		Map<Object, CountProductOrBundleAmounts> productOrBundleToCount = getProductOrBundleCountInOrderLines(false, false);
		for (Object productOrBundle : productOrBundleToCount.keySet()) {
			CountProductOrBundleAmounts count = productOrBundleToCount.get(productOrBundle);

			if (productOrBundle instanceof MobileProduct) {
				MobileProduct product = (MobileProduct) productOrBundle;
				if (product.isGks()) {
					sum += 12 * count.getBaseAmounts().getRecurringFee();
				}
			} else {
				MobileProductBundle bundle = (MobileProductBundle) productOrBundle;
				if (bundle.isGks()) {
					sum += 12 * bundle.getAmounts(count.getCount(), false, true, false, this).getRecurringFee();
				}
			}
		}
		return sum;
	}

	/**
	 * Calculate Mobile sum pr. year. Note: After-campaign discount amounts must
	 * be used.
	 * 
	 * @return Mobile sum
	 */
	@Transient
	public long getMobileSumPrYear(boolean partnerData) {
		long sum = 0;
		Map<Object, CountProductOrBundleAmounts> productOrBundleToCount = getProductOrBundleCountInOrderLines(false, partnerData);
		for (Object productOrBundle : productOrBundleToCount.keySet()) {
			CountProductOrBundleAmounts count = productOrBundleToCount.get(productOrBundle);

			if (productOrBundle instanceof MobileProduct) {
				if (((MobileProduct) productOrBundle).isDiscountEligible()) {
					sum += 12 * productOrBundleToCount.get(productOrBundle).getAmounts().getRecurringFee();
				}
			} else {
				MobileProductBundle bundle = (MobileProductBundle) productOrBundle;
				if (bundle.getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE)) {
					sum += 12 * bundle.getAmounts(count.getCount(), false, true, false, this).getRecurringFee();
				}
			}
		}
		return sum;
	}

	public void initVariableValueMaps(Campaign campaign) {
		if (StringUtils.isEmpty(variableRecurringFees))  {
//		if (StringUtils.isEmpty(variableRecurringFees) || StringUtils.isEmpty(variableInstallationFees)
//				|| StringUtils.isEmpty(variableCategories) || StringUtils.isEmpty(variableProductNames)) {
			Map<Long, Integer> idToRecurringFeeMap = new HashMap<>();
			Map<Long, Integer> idToInstallationFeeMap = new HashMap<>();
			Map<Long, String> idToHardwareCategoryMap = new HashMap<>();
			Map<Long, String> idToHardwareProductNameMap = new HashMap<>();
			for (ProductGroup g : getBusinessArea().getProductGroups()) {
				for (ProductGroup cg : g.getChildProductGroups()) {
					for (Product product : cg.getProducts()) {
						if (((MobileProduct) product).isVariableRecurringFee()) {
							idToRecurringFeeMap.put(product.getId(),
									(int) (product.getPrice().getRecurringFee() / 100));
						}
						if (((MobileProduct) product).isVariableInstallationFee()) {
							idToInstallationFeeMap.put(product.getId(),
									(int) (product.getPrice().getInstallationFee() / 100));
						}
						if (((MobileProduct) product).isVariableCategory()) {
							idToHardwareCategoryMap.put(product.getId(), product.getPublicName());
						}
						if (((MobileProduct) product).isVariableProductName()) {
							idToHardwareProductNameMap.put(product.getId(), "");
						}
					}
				}
			}

			for (ProductBundle bundle: campaign.getProductBundles()) {
				MobileProductBundle b = (MobileProductBundle) bundle;
				if (HARDWARE_BUNDLE.equals(b.getBundleType())) {
					Amounts a = b.getAmountsBeforeDiscounts(OrderLineCount.one(), this);
					idToRecurringFeeMap.put(b.getId(), (int) (a.getRecurringFee() / 100));
					idToInstallationFeeMap.put(b.getId(), (int) (a.getInstallationFee() / 100));
					idToHardwareCategoryMap.put(b.getId(), b.getPublicName());
					idToHardwareProductNameMap.put(b.getId(), "");
				}
			}

			setVariableRecurringFees(MapUtils.longIntMapToString(idToRecurringFeeMap));
			setVariableInstallationFees(MapUtils.longIntMapToString(idToInstallationFeeMap));
			setVariableCategories(MapUtils.longStringMapToString(idToHardwareCategoryMap));
			setVariableProductNames(MapUtils.longStringMapToString(idToHardwareProductNameMap));
		}
	}

	public PartnerData getPartnerData(int variant) {
		PartnerData data = new PartnerData();

		Organisation partnerCenter = getSalesperson().getOrganisation();

		data.values.put("partner_companyName", fixAmpersand(partnerCenter.getCompanyName()));
		data.values.put("partner_address", fixAmpersand(partnerCenter.getAddress()));
		data.values.put("partner_full_address", fixAmpersand(partnerCenter.getFullAddress()));
		data.values.put("partner_zipCode", fixAmpersand(partnerCenter.getZipCode()));
		data.values.put("partner_city", fixAmpersand(partnerCenter.getCity()));
		data.values.put("partner_company_id", fixAmpersand(partnerCenter.getCompanyId()));
		data.values.put("partner_phone", fixAmpersand(partnerCenter.getPhone()));
		data.values.put("partner_email", fixAmpersand(partnerCenter.getEmail()));
		data.values.put("partner_support_phone", fixAmpersand(partnerCenter.getSupportPhone()));
		data.values.put("partner_support_email", fixAmpersand(partnerCenter.getSupportEmail()));

		data.values.put("is_mobile_voice", BusinessAreas.MOBILE_VOICE == getBusinessArea().getBusinessAreaId());
		data.values.put("is_switchboard", BusinessAreas.SWITCHBOARD == getBusinessArea().getBusinessAreaId());
		data.values.put("is_fiber", 
				BusinessAreas.FIBER == getBusinessArea().getBusinessAreaId() ||
				BusinessAreas.FIBER_ERHVERV == getBusinessArea().getBusinessAreaId()
				);
		data.values.put("is_wifi", BusinessAreas.WIFI == getBusinessArea().getBusinessAreaId());

		data.values.put("is_partnerinstallation", isPartnerInstallation());
		if (MobileSession.get().isBusinessAreaOnePlus()) {
			data.values.put("is_partnerinstallation_business", isPartnerInstallation());
			data.values.put("is_partnerinstallation_userprofiles", isPartnerInstallationUserProfiles());
		}

		switch (getBusinessArea().getBusinessAreaId()) {
		case BusinessAreas.MOBILE_VOICE:
			data.values.put("business_area", "TDC Erhvervscenter Mobilpakker");
			data.values.put("solution", "TDC Erhverv Mobilpakker løsning");
			break;
		case BusinessAreas.SWITCHBOARD:
			data.values.put("business_area", "TDC Erhvervscenter Omstilling");
			data.values.put("solution", "TDC Erhverv Omstillings løsning");
			break;
		case BusinessAreas.FIBER:
		case BusinessAreas.FIBER_ERHVERV:
			data.values.put("business_area", "TDC Erhvervscenter Fiber");
			data.values.put("solution", "TDC Erhverv Fiberløsning");
			break;
		case BusinessAreas.WIFI:
			data.values.put("business_area", "TDC Erhvervscenter Wi-Fi");
			data.values.put("solution", "TDC Erhverv Wi-Fi løsning");
			break;
		case BusinessAreas.TDC_WORKS:
			data.values.put("business_area", "TDC Works");
			data.values.put("solution", "TDC Works løsning");
			break;
		case BusinessAreas.ONE_PLUS:
			data.values.put("business_area", "TDC Erhverv One+");
			data.values.put("solution", "TDC Erhverv One+ løsning");
			break;
		}

		data.values.put("intro_text", getOfferIntroText() == null ? "" : Processor.process(getOfferIntroText()));
		data.values.put("production_output_text",
				getProductionOutputText() == null ? "" : Processor.process(getProductionOutputText()));

//		data.values.put("document_header", getBusinessArea().getName());
//		data.values.put("document_header", "TDC Erhverv Rabataftale");
		data.values.put("document_footer", getSalesperson().getUser().getEmail());

		data.values.put("document_title", "Support- og Rate aftale");

		data.values.put("date", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		data.values.put("checkbox_image", "/images/checkbox-icon.png");

		data.values.put("seller_name", fixAmpersand(getSalesperson().getUser().getFullName()));
		data.values.put("seller_companyName", fixAmpersand(getSeller().getCompanyName()));
		data.values.put("seller_address", fixAmpersand(getSeller().getAddress()));
		data.values.put("seller_full_address", fixAmpersand(getSeller().getFullAddress()));
		data.values.put("seller_zipCode", fixAmpersand(getSeller().getZipCode()));
		data.values.put("seller_city", fixAmpersand(getSeller().getCity()));
		data.values.put("seller_phone", fixAmpersand(getSalesperson().getUser().getSmsPhone()));
		data.values.put("seller_company_id", getSeller().getCompanyId());
		data.values.put("seller_organisation", getSalesperson().getOrganisation().getCompanyName());

		data.values.put("customer_name", fixAmpersand(getCustomer().getName()));
		data.values.put("customer_companyName", fixAmpersand(getCustomer().getCompanyName()));
		data.values.put("customer_address", fixAmpersand(getCustomer().getAddress()));
		data.values.put("customer_full_address", fixAmpersand(getCustomer().getFullAddress()));
		data.values.put("customer_zipCode", fixAmpersand(getCustomer().getZipCode()));
		data.values.put("customer_city", fixAmpersand(getCustomer().getCity()));
		data.values.put("customer_phone", fixAmpersand(getCustomer().getPhone()));
		data.values.put("customer_company_id", getCustomer().getCompanyId());
		data.values.put("customer_contact_email", getCustomer().getEmail());

		String campaignInfo = null;
		if (getCampaigns().size() > 0) {
			campaignInfo = ((MobileCampaign) getCampaigns().get(0)).getOfferText();
		} 
		if (campaignInfo == null) {
			data.values.put("campaign_info", "");
		} else {
			data.values.put("campaign_info", Processor.process(campaignInfo));
		}

		data.values.put("contract_date",
				new SimpleDateFormat("d. MMMM yyyy", CoreSession.get().getLocale()).format(getLastModificationDate()));
		data.values.put("is_pbs", isPbs());

		// --- Installationsrabat
		{
//			if (businessArea.isOnePlus()) {
				data.values.put("installation_fee_discount", Amounts.getFormattedWithDecimals(installationFeeDiscount));
				data.values.put("show_installation_fee_discount", installationFeeDiscount != 0);
				data.values.put("onetime_fee_discount", Amounts.getFormattedWithDecimals(oneTimeFeeDiscount));
				data.values.put("show_onetime_fee_discount", oneTimeFeeDiscount != 0);
//			} else {
//				for (OrderLine orderLine : getOrderLines()) {
//					if ((orderLine != null) && (orderLine.getProduct() != null)
//							&& (orderLine.getDeferredCount().getCountTotal() > 0)) {
//						if (orderLine.getProduct().getProductGroup().getUniqueName()
//								.equals(MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_INSTALLATION.getKey())) {
//							if (orderLine.getProduct().getInternalName().contains("Rabat")) {
//								installationFeeDiscount = orderLine.getAmounts(true, true, getAcceptAllProductFilter())
//										.getNonRecurringFees() / 100;
//								break;
//							}
//						}
//					}
//				}
//				data.values.put("special_rabat",
//						NumberFormat.getNumberInstance(new Locale("DA", "dk")).format(installationFeeDiscount));
//				data.values.put("show_rate_discount", installationFeeDiscount != 0);
//			}
		}

		boolean anyHardwareBundles = false;
		long hardwareTotalValue = 0;
		// --- Hardware
		{
			Map<String, RemarkAndStars> remarkMap = new HashMap<>();

			remarkMap.put("Produktet er omfattet af bytteret/service", new RemarkAndStars("Produktet er omfattet af bytteret/service", "*"));
			remarkMap.put("Produktet er omfattet af 36 måneders udvidet garanti", new RemarkAndStars("Produktet er omfattet af 36 måneders udvidet garanti", "**"));
			remarkMap.put("Smartphone bundling inkl. All Risk forsikring. Se medfølgende vilkår", new RemarkAndStars("Smartphone bundling inkl. All Risk forsikring. Se medfølgende vilkår", "***"));

			Amounts hardwareTotalAmounts = new Amounts();
			List<PartnerData.HardwareInfo> hardwareLines = new ArrayList<>();
			for (Product product : getBusinessArea()
					.getProductGroupByUniqueName(MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_HARDWARE.getKey())
					.getProducts()) {
				List<OrderLine> orderLines = getOrderLines(product);
				if ((orderLines.size() > 0) && (orderLines.get(0).getTotalCount() > 0)) {
					MobileProduct p = (MobileProduct) product;
					String[] remarksForProduct = new String[] {};
					if (!StringUtils.isEmpty(p.getRemarks())) {
						remarksForProduct = p.getRemarks().split(";");
					}
					for (String r : remarksForProduct) {
						RemarkAndStars remark = remarkMap.get(r);
						if (remark == null) {
							String stars = StringUtils.repeat('*', remarkMap.size() + 1);
							remark = new PartnerData.RemarkAndStars(r, stars);
							remarkMap.put(r, remark);
						}
					}

					Amounts a = orderLines.get(0).getAmounts(true, true, getAcceptAllProductFilter());
					hardwareTotalAmounts.add(a);

					String text = p.getActualName();
					for (String r : remarksForProduct) {
						text += " " + remarkMap.get(r).stars;
					}
					hardwareLines.add(new PartnerData.HardwareInfo("" + orderLines.get(0).getTotalCount(), text, 36 * a.getRecurringFee(), p));
				}
			}
			for (MobileProductBundleEnum bundleType: MobileProductBundleEnum.values()) {
				if (bundleType.equals(HARDWARE_BUNDLE)) {
					for (OrderLine bundleOrderLine : getOrderLines(bundleType)) {
						if (bundleOrderLine.getCountNew() > 0) {
							anyHardwareBundles = true;

							if (variant == VARIANT_TASTEBILAG) {
								Amounts a = bundleOrderLine.getAmounts(true, true, getAcceptAllProductFilter());
								hardwareTotalAmounts.add(a);

								long bundleTotal = 0;
								for (BundleProductRelation productRelation: bundleOrderLine.getBundle().getProducts()) {
									bundleTotal += productRelation.getProduct().price.getRecurringFee();
								}

								for (BundleProductRelation productRelation: bundleOrderLine.getBundle().getProducts()) {
									MobileProduct p = (MobileProduct) productRelation.getProduct();
									hardwareLines.add(new PartnerData.HardwareInfo("" + bundleOrderLine.getTotalCount(),
											p.getInternalName(), bundleTotal == 0 ? 0 :36 * a.getRecurringFee() * p.getPrice().getRecurringFee() / bundleTotal, p));
								}
							} else {
								Amounts a = bundleOrderLine.getAmounts(true, true, getAcceptAllProductFilter());
								hardwareTotalAmounts.add(a);

								String text = bundleOrderLine.getBundle().getPublicName();
								for (BundleProductRelation productRelation: bundleOrderLine.getBundle().getProducts()) {
									MobileProduct p = (MobileProduct) productRelation.getProduct();

									String[] remarksForProduct = new String[] {};
									if (!StringUtils.isEmpty(p.getRemarks())) {
										remarksForProduct = p.getRemarks().split(";");
									}
									for (String r : remarksForProduct) {
										RemarkAndStars remark = remarkMap.get(r);
										if (remark == null) {
											String stars = StringUtils.repeat('*', remarkMap.size() + 1);
											remark = new PartnerData.RemarkAndStars(r, stars);
											remarkMap.put(r, remark);
										}
									}

									if (remarksForProduct.length > 0) {
										for (String r : remarksForProduct) {
											text += " " + remarkMap.get(r).stars;
										}
										break;
									}
								}
								hardwareLines.add(new PartnerData.HardwareInfo("" + bundleOrderLine.getTotalCount(), text, Long.valueOf(0), null));
							}
						}
					}
				}
			}
			hardwareTotalValue = hardwareTotalAmounts.getRecurringFee() * getRateMonths();

			List<RemarkAndStars> remarks = new ArrayList<>();
			for (RemarkAndStars remark : remarkMap.values()) {
				remark.text = remark.text.replace("{$rate_months}", "" + getRateMonths());
				remarks.add(remark);
			}
			Collections.sort(remarks, new Comparator<RemarkAndStars>() {
				@Override
				public int compare(RemarkAndStars o1, RemarkAndStars o2) {
					return Integer.valueOf(o1.getStars().length()).compareTo(Integer.valueOf(o2.getStars().length()));
				}
			});
			data.values.put("hardwareRemarks", remarks);
			data.values.put("hardwareTotalRecurring", hardwareTotalAmounts.getRecurringFeeFormatted());
			data.values.put("hardwareLines", hardwareLines);
			data.values.put("is_hardware", hardwareLines.size() > 0);
			data.values.put("include_aig", anyHardwareBundles);
		}

		long partnerInstallationBeforeDiscountRate 		= 0;
		long partnerInstallationAfterDiscountRate 		= 0;
		List<TypeCountTextAmount> partnerInstallationLinesRate = new ArrayList<>();

		long partnerInstallationBeforeDiscountKontant 	= 0;
		long partnerInstallationAfterDiscountKontant	= 0;
		List<TypeCountTextAmount> partnerInstallationLinesKontant = new ArrayList<>();

		// --- Partner installation
		{
			for (OrderLine orderLine : getOrderLines()) {
				if ((orderLine != null) && (orderLine.getDeferredCount().getCountTotal() > 0)) {
					if (orderLine.getProduct() == null) {
						// -------
						// Bundles
						// -------
						if (isPartnerInstallation()) {
							MobileProductBundle bundle = (MobileProductBundle) orderLine.getBundle();
							for (BundleProductRelation pb : bundle.getProducts()) {
								if (pb.getProduct().getProductGroup().getUniqueName().equals(PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_REMOTE.getKey())) {
									Amounts a = orderLine.getAmounts(true, true, getAcceptAllProductFilter());
									if (a.getInstallationFee() != 0) {
										TypeCountTextAmount header = new TypeCountTextAmount("header", "", "Løsning", "", null);

										// Rate
										if (!partnerInstallationLinesRate.contains(header)) {
											partnerInstallationLinesRate.add(header);
										}
										partnerInstallationBeforeDiscountRate += a.getInstallationFee();
										partnerInstallationLinesRate.add(new TypeCountTextAmount("",
												"" + orderLine.getDeferredCount().getCountTotal(), pb.getProduct().getPublicName(),
												Amounts.getFormattedWithDecimals(a.getInstallationFee()), pb.getProduct()));

										// Kontant
										if (!partnerInstallationLinesKontant.contains(header)) {
											partnerInstallationLinesKontant.add(header);
										}
										partnerInstallationBeforeDiscountKontant += a.getInstallationFee();
										partnerInstallationLinesKontant.add(new TypeCountTextAmount("",
												"" + orderLine.getDeferredCount().getCountTotal(), pb.getProduct().getPublicName(),
												Amounts.getFormattedWithDecimals(a.getInstallationFee()), pb.getProduct()));
									}
								} else if (pb.getProduct().getProductGroup().getUniqueName().equals(PRODUCT_GROUP_PARTNER_BUNDLE.getKey())) {
									Amounts a = orderLine.getAmounts(true, true, getAcceptAllProductFilter());
									if (a.getOneTimeFee() != 0) {
										TypeCountTextAmount header = new TypeCountTextAmount("header", "", "Løsning", "", null);

										// Rate
										if (!partnerInstallationLinesRate.contains(header)) {
											partnerInstallationLinesRate.add(header);
										}
										partnerInstallationBeforeDiscountRate += a.getOneTimeFee();
										partnerInstallationLinesRate.add(new TypeCountTextAmount("",
												"" + orderLine.getDeferredCount().getCountTotal(), pb.getProduct().getPublicName(),
												Amounts.getFormattedWithDecimals(a.getOneTimeFee()), pb.getProduct()));

										// Kontant
										// Not added!
									}
								}
							}
						}
					} else {
						// --------
						// Products
						// --------
						MobileProduct product = (MobileProduct) orderLine.getProduct();

						if (((MobileProductGroup) product.getProductGroup()).isOfType(
								PRODUCT_GROUP_PARTNER_INSTALLATION, PRODUCT_GROUP_SWITCHBOARD_ADDON)) {
							if (!product.getInternalName().contains("Rabat")) {
								// Don't include discount here
								if (!orderLine.isInstallationHandledByTdc()) {
//								if (orderLine.isCustomFlag1()) {  // Correct??????????????????
									// Partner installation = TRUE
									Amounts a = orderLine.getAmounts(true, true, getAcceptAllProductFilter());
									if (a.getInstallationFee() != 0) {
										String productName = product.getActualName();
										TypeCountTextAmount header = new TypeCountTextAmount("header", "", "Løsning", "", null);

										// Rate
										partnerInstallationBeforeDiscountRate += a.getInstallationFee();
										if (!partnerInstallationLinesRate.contains(header)) {
											partnerInstallationLinesRate.add(header);
										}
										// Take "variable names" into account
										partnerInstallationLinesRate.add(new TypeCountTextAmount("",
												"" + orderLine.getDeferredCount().getCountTotal(), productName,
												Amounts.getFormattedWithDecimals(a.getInstallationFee()), product));

										// Kontant
										// Not added
									}
								}
							}
						}

						if (isPartnerInstallationUserProfiles()) {
							if (product.getProductGroup().getUniqueName().equals(PRODUCT_GROUP_STANDARD_BUNDLE_INSTALLATION_REMOTE.getKey())) {
								Amounts a = orderLine.getAmounts(true, true, getAcceptAllProductFilter());
								if (a.getInstallationFee() != 0) {
									TypeCountTextAmount header = new TypeCountTextAmount("header", "", "Løsning", "", null);

									// Rate
									if (!partnerInstallationLinesRate.contains(header)) {
										partnerInstallationLinesRate.add(header);
									}
									partnerInstallationBeforeDiscountRate += a.getInstallationFee();
									partnerInstallationLinesRate.add(new TypeCountTextAmount("",
											"" + orderLine.getDeferredCount().getCountTotal(), product.getPublicName(),
											Amounts.getFormattedWithDecimals(a.getInstallationFee()), product));

									// Kontant
									if (!partnerInstallationLinesKontant.contains(header)) {
										partnerInstallationLinesKontant.add(header);
									}
									partnerInstallationBeforeDiscountKontant += a.getInstallationFee();
									partnerInstallationLinesKontant.add(new TypeCountTextAmount("",
											"" + orderLine.getDeferredCount().getCountTotal(), product.getPublicName(),
											Amounts.getFormattedWithDecimals(a.getInstallationFee()), product));
								}
							}
						}

						for (int i=0; i<getLocationBundles().size(); i++) {
							LocationBundleData location = getLocationBundles().get(i);
							if (!location.isTDCHardwareProvider() && ((variant == VARIANT_GENERELT) || (variant == PartnerData.VARIANT_INSTALLATION))) {
								if ((orderLine.getSubIndex() != null) && (i == orderLine.getSubIndex())) {
									if (orderLine.getProduct() != null) {
										MobileProductGroupEnum[] groups = new MobileProductGroupEnum[] {
												PRODUCT_GROUP_LOCATIONS_HARDWARE_IP, PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC
										};
										for (MobileProductGroupEnum group : groups) {
											if (product.getProductGroup().getUniqueName().equals(group.getKey())) {
												Amounts a = orderLine.getAmounts(true, true, getAcceptAllProductFilter());
												if (a.getOneTimeFee() != 0) {
													TypeCountTextAmount header = new TypeCountTextAmount("header", "", "Lokation " + (i+1) + ": " + location.getAddress(), "", null);

													// Rate
													// Not added!

													// Kontant
													if (product.getPublicName().indexOf("Forsendelsesgebyr") == -1) {
														if (!partnerInstallationLinesKontant.contains(header)) {
															partnerInstallationLinesKontant.add(header);
														}
														// Note: We use onetime fee, not installation fee in this case!
														partnerInstallationBeforeDiscountKontant += a.getOneTimeFee();
														partnerInstallationLinesKontant.add(new TypeCountTextAmount("",
																"" + orderLine.getDeferredCount().getCountTotal(), product.getPublicName(),
																Amounts.getFormattedWithDecimals(a.getOneTimeFee()), product));
													}
												}
											}
										}
									}
								}
							}
							if (!location.isTDCInstallationProvider()) {
								if ((orderLine.getSubIndex() != null) && (i == orderLine.getSubIndex())) {
									if (orderLine.getProduct() != null) {
										MobileProductGroupEnum[] groups = new MobileProductGroupEnum[] {
												PRODUCT_GROUP_LOCATIONS_INSTALLATION
										};
										for (MobileProductGroupEnum group : groups) {
											if (product.getProductGroup().getUniqueName().equals(group.getKey())) {
												Amounts a = orderLine.getAmounts(true, true, getAcceptAllProductFilter());
												if (a.getInstallationFee() != 0) {
													TypeCountTextAmount header = new TypeCountTextAmount("header", "", "Lokation " + (i+1) + ": " + location.getAddress(), "", null);

													// Rate
													if (!partnerInstallationLinesRate.contains(header)) {
														partnerInstallationLinesRate.add(header);
													}
													partnerInstallationBeforeDiscountRate += a.getInstallationFee();
													partnerInstallationLinesRate.add(new TypeCountTextAmount("",
															"" + orderLine.getDeferredCount().getCountTotal(), product.getPublicName(),
															Amounts.getFormattedWithDecimals(a.getInstallationFee()), product));

													// Kontant
													if (!partnerInstallationLinesKontant.contains(header)) {
														partnerInstallationLinesKontant.add(header);
													}
													partnerInstallationBeforeDiscountKontant += a.getInstallationFee();
													partnerInstallationLinesKontant.add(new TypeCountTextAmount("",
															"" + orderLine.getDeferredCount().getCountTotal(), product.getPublicName(),
															Amounts.getFormattedWithDecimals(a.getInstallationFee()), product));
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			partnerInstallationAfterDiscountRate 	= Math.max(0, partnerInstallationBeforeDiscountRate - installationFeeDiscount);
			partnerInstallationAfterDiscountKontant = Math.max(0, partnerInstallationBeforeDiscountKontant - installationFeeDiscount);

			data.values.put("partnerInstallationLinesRate", partnerInstallationLinesRate);
			data.values.put("partnerInstallationBeforeDiscountRate", Amounts.getFormattedWithDecimals(partnerInstallationBeforeDiscountRate));
			data.values.put("partnerInstallationAfterDiscountRate", Amounts.getFormattedWithDecimals(partnerInstallationAfterDiscountRate));

			data.values.put("partnerInstallationLinesKontant", partnerInstallationLinesKontant);
			data.values.put("partnerInstallationBeforeDiscountKontant", Amounts.getFormattedWithDecimals(partnerInstallationBeforeDiscountKontant));
			data.values.put("partnerInstallationAfterDiscountKontant", Amounts.getFormattedWithDecimals(partnerInstallationAfterDiscountKontant));
		}

		// --- Support (og Ratebetaling)
		{
			long etableringAfRateAftale = getRateNonRecurringFee();
			long totalTilRateBetalingFoerUpFront = partnerInstallationAfterDiscountRate + etableringAfRateAftale + hardwareTotalValue;
			long totalTilRateBetaling = totalTilRateBetalingFoerUpFront - getUpFrontPayment();

			data.values.put("etablering_af_rate_aftale",				Amounts.getFormattedWithDecimals(etableringAfRateAftale));
			data.values.put("total_hardware_value", 					Amounts.getFormattedWithDecimals(hardwareTotalValue));
			data.values.put("total_til_rate_betaling_foer_up_front", 	Amounts.getFormattedWithDecimals(totalTilRateBetaling));
			data.values.put("total_til_rate_betaling",					Amounts.getFormattedWithDecimals(totalTilRateBetaling));

			long rateMonthly = 0;
			long hardwareMonthly = 0;
			if (getRateMonths() < 1) {
				data.values.put("rate_monthly", "-");
				data.values.put("hardware_monthly", "-");
			} else {
				rateMonthly = Math.round(Math.ceil(((double) totalTilRateBetaling) / getRateMonths()));
				data.values.put("rate_monthly", Amounts.getFormattedWithDecimals(rateMonthly));

				hardwareMonthly = Math.round(Math.ceil(((double) hardwareTotalValue) / getRateMonths()));
				data.values.put("hardware_monthly", Amounts.getFormattedWithDecimals(hardwareMonthly));
			}

			long supportMonthly = getSupportRecurringFee() + (getNoOfUsers(false) * getSupportPricePrUser());
			data.values.put("support_monthly", Amounts.getFormattedWithDecimals(supportMonthly));
			data.values.put("rate_and_support_monthly", Amounts.getFormattedWithDecimals(rateMonthly + supportMonthly));
			data.values.put("support_months", getSupportMonths());
			data.values.put("no_of_subscribers", getSubscriptions().size());
			data.values.put("no_of_users", getNoOfUsers(false));
			data.values.put("support_pr_user", Amounts.getFormattedWithDecimals(getSupportPricePrUser()));
			data.values.put("support_basic_fee", Amounts.getFormattedWithDecimals(getSupportRecurringFee()));
			data.values.put("rate_months", getRateMonths());
			data.values.put("show_up_front_payment", getUpFrontPayment() != 0);
			data.values.put("up_front_payment", Amounts.getFormattedWithDecimals(getUpFrontPayment()));
			data.values.put("total_monthly", Amounts.getFormattedWithDecimals(rateMonthly + supportMonthly));
		}

		return data;
	}

	private String fixAmpersand(String text) {
		if (text == null) {
			return "";
		} else {
			return text.replace("&", "&amp;");
		}
	}

	public List<Provision> getProvisions() {
		List<Provision> provisions = new ArrayList<>();
		for (OrderLine orderLine : getOrderLines()) {
			ProductBundle b = orderLine.getBundle();
			if (b == null) {
				MobileProduct product = (MobileProduct) orderLine.getProduct();
				for (Provision provision: product.getProvisions(orderLine)) {
					MobileProductGroup group = (MobileProductGroup) product.getProductGroup();
					if (group.isOfType(PRODUCT_GROUP_LOCATIONS_HARDWARE_SWITCHES)) {
						if (contractMode.equals(MobileContractMode.NEW_SALE)) {
							// TDC always provides switches, so partners are entitled to provision
							provisions.add(provision);
						}
					} else if (group.isOfType(PRODUCT_GROUP_LOCATIONS_HARDWARE_IP, PRODUCT_GROUP_LOCATIONS_HARDWARE_MISC)) {
						if (contractMode.equals(MobileContractMode.NEW_SALE)) {
							LocationBundleData location = getLocationBundle(orderLine.getSubIndex());
							if (location != null) {
								if (location.getHardwareProvider() != LocationBundleData.HARDWARE_PARTNER) {
									// TDC provides this hardware and is paid to do so - ie partners are entitled to provision
									provisions.add(provision);
								}
							}
						}
					} else if (group.isOfType(PRODUCT_GROUP_LOCATIONS_INSTALLATION)) {
						if (contractMode.equals(MobileContractMode.NEW_SALE)) {
							LocationBundleData location = getLocationBundle(orderLine.getSubIndex());
							if (location != null) {
								if (location.getInstallationProvider() != LocationBundleData.INSTALLATION_PARTNER) {
									// TDC is paid for the installation - ie partners are entitled to provision
									provisions.add(provision);
								}
							}
						}
					} else if (group.isOfType(PRODUCT_GROUP_STANDARD_BUNDLE_INSTALLATION_REMOTE)) {
						if (contractMode.equals(MobileContractMode.NEW_SALE)) {
							if (isTdcInstallationUserProfiles()) {
								provisions.add(provision);
							}
						}
					} else if (group.isOfType(PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_REMOTE)) {
						if (contractMode.equals(MobileContractMode.NEW_SALE)) {
							if (isTdcInstallation()) {
								provisions.add(provision);
							}
						}
					} else if (group.isOfType(PRODUCT_GROUP_XDSL_BUNDLE, PRODUCT_GROUP_XDSL_BUNDLE_SPEED,
							PRODUCT_GROUP_XDSL_BUNDLE_INCLUDED, PRODUCT_GROUP_XDSL_BUNDLE_MISC, PRODUCT_GROUP_XDSL_BUNDLE_SUPERVISION,
							PRODUCT_GROUP_ACCESS_QOS, PRODUCT_GROUP_ACCESS_IP, PRODUCT_GROUP_ACCESS_REDUNDANCY, PRODUCT_GROUP_ACCESS_INCLUDED,
							PRODUCT_GROUP_ACCESS_SERVICE, PRODUCT_GROUP_ACCESS_SUPERVISION)) {
						OneXdslBundleData xdsl = getOneXdslBundle(orderLine.getSubIndex());
						if (xdsl.getMode() == MODE_NYSALG) {
							provisions.add(provision);
						}
					} else {
						if (contractMode.equals(MobileContractMode.NEW_SALE)) {
							provisions.add(provision);
						}
//					} else if ((isPartnerInstallationUserProfiles() || !group.getUniqueName().equals(PRODUCT_GROUP_STANDARD_BUNDLE_INSTALLATION_REMOTE)) &&
//							(isPartnerInstallation() || !group.getUniqueName().equals(PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_REMOTE))) {
//						if (contractMode.equals(MobileContractMode.NEW_SALE)) {
//							provisions.add(provision);
//						}
					}
				}
			} else {
				MobileProductBundle bundle = (MobileProductBundle) b;
				for (Provision provision: bundle.getProvisions(orderLine)) {
					if (contractMode.equals(MobileContractMode.NEW_SALE)) {
						provisions.add(provision);
					}
				}
			}
		}

		if (contractMode.equals(MobileContractMode.CONVERSION) || contractMode.equals(MobileContractMode.CONVERSION_1_TO_1)) {
			ContractFinansialInfo infoNonNetwork = getContractFinansialInfo(true, false, false);
			long totalExcludingAccess = infoNonNetwork.getContractTotalsAfterDiscounts().getRecurringFee();

			Provision provision = new Provision();
			provision.setAmounts(new Amounts(0, 0, totalExcludingAccess));
			provision.setCount(1);
			provision.setText("Driftpris pr. md. ekskl. access (ifbm. konvertering):");
			provision.setType(Provision.TYPE_HEADER);
			provisions.add(provision);

			if (totalExcludingAccess > 0) {
				provision = new Provision();
				if (totalExcludingAccess <= 150000) {
					provision.setAmounts(new Amounts(0, 0, 300000));
				} else if (totalExcludingAccess <= 300000) {
					provision.setAmounts(new Amounts(0, 0, 600000));
				} else if (totalExcludingAccess <= 450000) {
					provision.setAmounts(new Amounts(0, 0, 1000000));
				} else if (totalExcludingAccess <= 600000) {
					provision.setAmounts(new Amounts(0, 0, 1400000));
				} else {
					provision.setAmounts(new Amounts(0, 0, 2000000));
				}
				provision.setCount(1);
				provision.setText("Drift (ekskl. access)");
				provision.setType(Provision.TYPE_STYK);
				provisions.add(provision);
			}
		}
		return provisions;
	}

	public void calculatePartnerProvision(MutableLong stykProvisions, MutableLong satsProvisions, MutableLong totalProvisions) {
		for (Provision provision: getProvisions()) {
			long a = (provision.getAmounts().getInstallationFee() + provision.getAmounts().getOneTimeFee() + provision.getAmounts().getRecurringFee()) / 100;
			if (provision.getType() == Provision.TYPE_SATS) {
				satsProvisions.add(a);
				totalProvisions.add(a);
			} else if (provision.getType() == Provision.TYPE_STYK) {
				stykProvisions.add(a);
				totalProvisions.add(a);
			}
		}
	}

	public float calculatePartnerProvisionFactor() {
		BusinessArea businessArea = getBusinessArea();
		Map<Object, CountProductOrBundleAmounts> productOrBundleToCount = getProductOrBundleCountInOrderLines(false, true);
		for (Object productOrBundle : productOrBundleToCount.keySet()) {
			CountProductOrBundleAmounts count = productOrBundleToCount.get(productOrBundle);

			if (!(productOrBundle instanceof MobileProduct)) {
				MobileProductBundle bundle = (MobileProductBundle) productOrBundle;
				if (count.getCount().getCountTotal() > 0) {
					if (MobileProductBundleEnum.XDSL_BUNDLE.equals(bundle.getBundleType())) {
						return businessArea.getProvisionFactorXDSL();
					}
				}
			}
		}
		return businessArea.getProvisionFactorGeneral();
	}
	
	@Transient
	public boolean hasOfficeImplementationInfo() {
		if (StringUtils.isEmpty(getTechnicalContactName()) || StringUtils.isEmpty(getTechnicalContactPhone()) || StringUtils.isEmpty(getTechnicalContactEmail()) || StringUtils.isEmpty(getEFakturaEmail())) {
			return false;
		}
		return true;
	}

	@Transient
	public String getConfigurationUrl() {
		try {
			return MobileSalescloudApplication.get().getSetting("baseurl") + "/konfiguration/" + businessArea.getId() + "/" + SimpleStringCipher.encrypt("" + id);
		} catch (Exception e) {
			log.error("Failed to construct url for id: " + id);
			return "";
		}
	}

	@Transient
	public String getImplementationUrl() {
		try {
			return MobileSalescloudApplication.get().getSetting("baseurl") + "/implementering/" + businessArea.getId() + "/" + SimpleStringCipher.encrypt("" + id);
		} catch (Exception e) {
			log.error("Failed to construct url for id: " + id);
			return "";
		}
	}
	
	@Transient
	public String getConfigurationUsername() {
		return "k" + id;
	}

	@Transient
	public boolean isBusinessArea(int businessAreaId) {
		return businessArea.getBusinessAreaId() == businessAreaId;
	}

	@Override
	@Transient
	public Component getStatusPanel(String componentId) {
		if (isBusinessArea(BusinessAreas.TDC_OFFICE)) {
			return new OfficeStatusPanel(componentId, this);
		}
		return super.getStatusPanel(componentId);
	}

	// --- One+ --- (start of)

	public void onOpen() {
		setSegment(SegmentDao.lookup().findUniqueByField("name", "BS Maximum"));
		campaignChanged(null, getCampaigns().get(0));	// Initialize stuff
	}

	@Transient
	public int getSubIndexOfUserProfileBundle(ProductBundle productBundle) {
//		int subIndex = -1;
//		for (UserProfileBundleData userProfile : getUserProfileBundles()) {
//			subIndex++;
//			if (Objects.equals(userProfile.getBundleEntityId(), productBundle.getId())) {
//				break;
//			}
//		}
//		return subIndex;
		List<MobileProductBundle> mobileBundles = productBundles.stream()
				.map(b -> (MobileProductBundle) b)
				.filter(mobileProductBundle -> mobileProductBundle.getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE))
				.sorted(Comparator.comparing(MobileProductBundle::getSortIndex))
				.collect(Collectors.toList());
		for (int i=0; i<mobileBundles.size(); i++) {
			if (Objects.equals(productBundle, mobileBundles.get(i))) {
				return i;
			}
		}
		return -1;
	}

	// --- One+ --- (end of)


//	class BooleanTypeAdapter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {
//		public JsonElement serialize(Boolean value, Type typeOfT, JsonSerializationContext context) {
//			return new JsonPrimitive(value == null ? 2 : value ? 1 : 0);
//		}
//
//		public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
//				throws JsonParseException {
//			int code = json.getAsInt();
//			return code == 0 ? false : code == 1 ? true : null;
//		}
//	}
	
	public static void main(String[] args) {
		System.out.println(QuarterUtils.getQuarterNo(LocalDate.now()));
	}

	public void campaignChanged(Campaign oldCampaign, Campaign newCampaign) {
		variableRecurringFees = null;
		initVariableValueMaps(newCampaign);

		if (getBusinessArea().hasFeature(FeatureType.USER_PROFILES)) {
			if ((oldCampaign != null) && !Objects.equals(oldCampaign, newCampaign)) {
				removeOrderlines(new Predicate<OrderLine>() {
					@Override
					public boolean test(OrderLine orderLine) {
						return (orderLine.getBundle() != null) &&
								(MobileProductBundleEnum.MOBILE_BUNDLE.equals(((MobileProductBundle) orderLine.getBundle()).getBundleType()));
					}
				});
				removeSubscriptions();

				Iterator<ProductBundle> bundleIterator = productBundles.iterator();
				while (bundleIterator.hasNext()) {
					MobileProductBundle productBundle = (MobileProductBundle) bundleIterator.next();
					if (productBundle.getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE)) {
						bundleIterator.remove();
					}
				}
			}

			// Copy bundles from campaign to contract
			// So, in case new bundles have been added, the contract will reflect it. But shouldn't we also delete
			// orderlines and bundles that are obsolete???
			for (ProductBundle pb: newCampaign.getProductBundles()) {
				MobileProductBundle productBundle = (MobileProductBundle) pb;
				if (productBundle.getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE)) {
					boolean found = false;
					for (ProductBundle existingProductBundle : productBundles) {
						if (StringUtils.equals(existingProductBundle.getPublicName(), productBundle.getPublicName())) {
							found = true;
							break;
						}
					}
					if (found) {
						log.info("Already exists: " + productBundle.getPublicName());
					} else {
						log.info(getTitle() + ": adding bundle: " + productBundle.getPublicName());
						MobileProductBundle newBundle = addBundle(((MobileProductBundle) productBundle).clone(true));
						newBundle.setCampaign(null);
						newBundle = MobileProductBundleDao.lookup().saveAndFlush(newBundle);
						((MobileProductBundle) productBundle).cloneProductRelations(newBundle);
					}
				}
			}
			log.info(getTitle() + ": bundles after: " + getProductBundles().size());
		}
	}

	public void removeOrderlines(Predicate<OrderLine> predicate) {
		Iterator<OrderLine> iter = orderLines.iterator();
		while (iter.hasNext()) {
			OrderLine orderLine = iter.next();
			if (predicate.test(orderLine)) {
				iter.remove();
			}
		}
	}

	public void removeSubscriptions() {
		Iterator<Subscription> iter = subscriptions.iterator();
		while (iter.hasNext()) {
			iter.next();
			iter.remove();
		}
	}

	@Transient
	protected ProductAndBundleFilter getCheckTypeProductFilter(boolean useNonNetworkOrderLines, boolean useNetworkOrderLines) {
		return new ProductAndBundleFilter() {
			@Override
			public boolean acceptProductBundle(ProductBundle productBundle) {
				if (businessArea.isOnePlus()) {
					if (useNetworkOrderLines) {
						return false;
					}
				}
				return true;   // No flag to check?!
			}

			@Override
			public boolean acceptProduct(Product product) {
				if (businessArea.isOnePlus()) {
					if (!useNonNetworkOrderLines && product.getProductGroup().getUniqueName().indexOf(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE.getKey()) == -1) {
						return false;
					}
					if (!useNetworkOrderLines && product.getProductGroup().getUniqueName().indexOf(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE.getKey()) != -1) {
						return false;
					}
				}
				return true;
			}

			@Override
			public boolean acceptOrderLine(OrderLine orderLine) {
				return orderLine.getBundle() == null ? acceptProduct(orderLine.getProduct()) : acceptProductBundle(orderLine.getBundle());
			}
		};
	}


	@Transient
	protected ProductAndBundleFilter getRabataftaleProductFilter(boolean useNonNetworkOrderLines, boolean useNetworkOrderLines) {
		return new ProductAndBundleFilter() {
			@Override
			public boolean acceptProductBundle(ProductBundle productBundle) {
				if (businessArea.isOnePlus()) {
					if (useNetworkOrderLines) {
						return false;
					}
				}
				return ProductBundle.RABATAFTALE_DISCOUNT_CONTRIBUTION == productBundle.getAddToContractDiscount();
			}

			@Override
			public boolean acceptProduct(Product product) {
				if (product.isRabataftaleDiscountEligible()) {
					if (businessArea.isOnePlus()) {
						if (!useNonNetworkOrderLines && product.getProductGroup().getUniqueName().indexOf(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE.getKey()) == -1) {
							return false;
						}
						if (!useNetworkOrderLines && product.getProductGroup().getUniqueName().indexOf(MobileProductGroupEnum.PRODUCT_GROUP_XDSL_BUNDLE.getKey()) != -1) {
							return false;
						}
					}
					return true;
				} else {
					return false;
				}
			}

			@Override
			public boolean acceptOrderLine(OrderLine orderLine) {
				return orderLine.getBundle() == null ? acceptProduct(orderLine.getProduct()) : acceptProductBundle(orderLine.getBundle());
			}
		};
	}

	@Transient
	public int getTotalCountNysalg(ProductAndBundleFilter filter) {
		int total = 0;
		for (OrderLine orderLine: getOrderLines()) {
			if (filter.acceptOrderLine(orderLine)) {
				total += orderLine.getCountNew();
			}
		}
		return total;
	}

	public boolean isMobileOnlySolution() {
		for (OrderLine orderLine: getBundleOrderLines()) {
			if ((MobileProductBundleEnum.SWITCHBOARD_BUNDLE.equals(((MobileProductBundle) orderLine.getBundle()).getBundleType()))
					&& (orderLine.getTotalCount() > 0)) {
				return orderLine.getBundle().getPublicName().toLowerCase().indexOf("only") > -1;
			}
		}
		return true;
	}

	public int getAdjustedContractLength() {
		if (contractStartDate == null) {
			return 0;
		}
		if (((MobileCampaign) getCampaigns().get(0)).isDisableContractDiscount()) {
			return 0;
		}

		return contractLength;
	}

	public int getAdjustedContractLengthNetwork() {
		if (contractStartDateNetwork == null) {
			return 0;
		}
		if (((MobileCampaign) getCampaigns().get(0)).isDisableContractDiscount()) {
			return 0;
		}
		return contractLengthNetwork;
	}

	@Transient
	public List<OrderLine> getOrderLines(MobileProductBundleEnum bundleType) {
		List<OrderLine> matchingOrderlines = new ArrayList<>();
		for (OrderLine orderLine : orderLines) {
			if (orderLine.getBundle() != null) {
				if (bundleType.equals(((MobileProductBundle) orderLine.getBundle()).getBundleType())) {
					matchingOrderlines.add(orderLine);
				}
			}
		}
		return matchingOrderlines;
	}

	@Transient
	public List<OrderLine> getOrderLines(MobileProductGroupEnum groupType) {
		List<OrderLine> matchingOrderlines = new ArrayList<>();
		for (OrderLine orderLine : orderLines) {
			if ((orderLine.getProduct() != null) && groupType.getKey().equals(orderLine.getProduct().getProductGroup().getUniqueName())) {
				matchingOrderlines.add(orderLine);
			}
		}
		return matchingOrderlines;
	}

	@Transient
	public boolean isUnityReceptionistSelected() {
		return (getTotalCountNysalg(new ProductAndBundleFilter() {
			@Override
			public boolean acceptOrderLine(OrderLine orderLine) {
				Product product = orderLine.getProduct();
				if (product == null) {
					return false;
				}
				return "Unity receptionist".equals(product.getPublicName());
			}
		}) > 0);
	}

	@Transient
	public int getNoOfUsers(boolean onlyNewUsers) {
		int noOfUsers = 0;
		for (OrderLine bundleOrderLine : getBundleOrderLines()) {
			MobileProductBundle bundle = (MobileProductBundle) bundleOrderLine.getBundle();
			if (bundle.getBundleType().equals(MobileProductBundleEnum.MOBILE_BUNDLE) &&
					!StringUtils.containsIgnoreCase(bundle.getPublicName(), "NummerReservation")) {
				if (onlyNewUsers) {
					noOfUsers += bundleOrderLine.getCountNew();
				} else {
					noOfUsers += bundleOrderLine.getTotalCount();
				}
			}
		}
		return noOfUsers;
	}

	@Transient
	public boolean hasDDI() {
		// Nysalg af DDI?
		return (getTotalCountNysalg(new ProductAndBundleFilter() {
			@Override
			public boolean acceptOrderLine(OrderLine orderLine) {
				return ((orderLine.getProduct() != null) && (
						(orderLine.getProduct().getProductId().equals("95 110 01"))
								|| (orderLine.getProduct().getProductId().equals("95 110 09"))
								|| (orderLine.getProduct().getProductId().equals("95 110 10"))));
			}
		}) > 0);
	}

	public ContractDetails getContractDetails() {
		ContractDetails contractDetails = new ContractDetails();
		if (getBusinessArea().hasFeature(FeatureType.RABATAFTALE_CAMPAIGN_DISCOUNT)) {
			ContractFinansialInfo info = getContractFinansialInfo(true, false, false);
			CoreSession.get().setDiscountPointNonNetwork(Lookup.lookup(SuperContractService.class).getDiscountPoint(getBusinessArea(),
					new BigDecimal(info.getRabataftaleKontraktsum()), this, false));
			if (getBusinessArea().isOnePlus()) {
				info = getContractFinansialInfo(false, true, false);
				CoreSession.get().setDiscountPointNetwork(Lookup.lookup(SuperContractService.class).getDiscountPoint(getBusinessArea(),
						new BigDecimal(info.getRabataftaleKontraktsum()), this, true));
			}
		}

		contractDetails = new ContractDetails();
		contractDetails.setContractType(getContractType());

		if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
			contractDetails.setContractLength(PERIOD_12_MONTHS_NO_DISCOUNT);
			if (Integer.valueOf(1).equals(getAdjustedContractLength())) {
				contractDetails.setContractLength(PERIOD_12_MONTHS);
			} else if (Integer.valueOf(2).equals(getAdjustedContractLength())) {
				contractDetails.setContractLength(PERIOD_24_MONTHS);
			} else if (Integer.valueOf(3).equals(getAdjustedContractLength())) {
				contractDetails.setContractLength(PERIOD_36_MONTHS);
			}
		} else {
			if (getContractLength() == null) {
				if (getBusinessArea().isOnePlus()) {
					contractDetails.setContractLength(INGEN_RABATAFTALE);
				} else {
					contractDetails.setContractLength("1 år");
				}
			} else {
				contractDetails.setContractLength(getContractLength() + " år");
			}
		}

		contractDetails.setInstallationDate(getInstallationDate());
		if (getContractStartDate() != null) {
			if (MobileSession.get().getBusinessArea().getBusinessAreaId() == BusinessAreas.FIBER_ERHVERV) {
				contractDetails.setContractStartDateFiberErhverv(getContractStartDate());
			} else {
				contractDetails.setContractStartDate(getContractStartDate());
			}
		}
		if (MobileSession.get().getBusinessArea().isOnePlus()) {
			if (getContractStartDateNetwork() != null) {
				contractDetails.setContractStartDateNetwork(getContractStartDateNetwork());
			}
		}

		contractDetails.setContractSumMobile(getContractSumMobile());
		contractDetails.setContractSumFastnet(getContractSumFastnet());
		contractDetails.setContractSumBroadband(getContractSumBroadband());
		FixedDiscount fixedDiscount = (FixedDiscount) getDiscountScheme(FixedDiscount.class);
		if (fixedDiscount != null) {
			contractDetails.setFixedDiscountPercentage(fixedDiscount.getDiscountPercentages().getRecurringFee() / 100);
		}

		contractDetails.setAdditionToKontraktsum(getAdditionToKontraktsum());
		contractDetails.setAdditionToKontraktsumNetwork(getAdditionToKontraktsumNetwork());
		contractDetails.setAdditionToKontraktsumFiberErhverv(getAdditionToKontraktsum());
		contractDetails.setStatus(getStatus());
		return contractDetails;
	}

	public void adjustOrderLinesForRemoteInstallation() {
		ProductGroupDao productGroupDao = Lookup.lookup(ProductGroupDao.class);

		int noOfUsers = getNoOfUsers(true);
		boolean calculatedForUsers 			= false;
		boolean calculatedForSwitchboard 	= false;
		String ÆNDRINGER_AF_VIRKSOMHEDSFUNKTION = "4401542";

		if (MobileContractMode.CONVERSION.equals(getContractMode())) {
			noOfUsers += getAdditionalUserChanges() == null ? 0 : getAdditionalUserChanges();

			boolean vkÆndringer = false;
			// Midlertidigt disabled (Rettelser 190905 side 75):
			// vkÆndringer = (getAdditionalUserChanges() == null);
			vkÆndringer = true;

			if (vkÆndringer) {
				// Udløs VK ændringer
				MobileProduct product = (MobileProduct) getBusinessArea().getProductByProductId(ÆNDRINGER_AF_VIRKSOMHEDSFUNKTION);
				if (product != null) {
					Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
					product.addToProductToCountsMap(productToCountMap, 1, 0, null);
					adjustOrderLinesForProducts(product.getProductGroup(), productToCountMap, null);

					removeOrderLinesForGroup(MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_INSTALLATION_REMOTE);

					calculatedForUsers = true;
				}
			}
		}

		if (!calculatedForUsers) {
			ProductGroup group = productGroupDao.findByBusinessAreaAndUniqueName(getBusinessArea(),
					MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_INSTALLATION_REMOTE.getKey());
			Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
			for (Product product: group.getProducts()) {
				try {
					if (StringUtils.contains(product.getPublicName(), "Konfig. af ")) {
						String s = product.getPublicName().trim()
								.replace("Konfig. af ", "")
								.replace(" brugere", "")
								.replace("+101", "101-999999");
						if (!StringUtils.isEmpty(s) && s.indexOf("-") > -1) {
							String[] fromTo = StringUtils.split(s, '-');
							int from 	= Integer.valueOf(fromTo[0]);
							int to 		= Integer.valueOf(fromTo[1]);
							if ((noOfUsers >= from) && (noOfUsers <= to)) {
								CountAndInstallation countAndInstallation = new CountAndInstallation();
								countAndInstallation.setCountNew(1);
								countAndInstallation.setCountExisting(0);
								List<CountAndInstallation> countAndInstallations = Lists.newArrayList();
								countAndInstallations.add(countAndInstallation);

								productToCountMap.put(product, countAndInstallations);
							}
						}
					}
				} catch (Exception e) {
					log.info("Something is bad", e);
				}
			}
			adjustOrderLinesForProducts(group, productToCountMap, null);

			removeOrderLinesForProduct(ÆNDRINGER_AF_VIRKSOMHEDSFUNKTION);
		}

//		ProductGroup group = productGroupDao.findByBusinessAreaAndUniqueName(getBusinessArea(),
//				MobileProductGroupEnum.PRODUCT_GROUP_SWITCHBOARD_INSTALLATION_REMOTE.getKey());
//		Map<Product, List<CountAndInstallation>> productToCountMap = new HashMap<>();
//		for (OrderLine orderLine : orderLines) {
//			if (orderLine.getBundle() != null) {
//				MobileProductBundle bundle = (MobileProductBundle) orderLine.getBundle();
//				if (bundle.getBundleType().equals(MobileProductBundleEnum.SWITCHBOARD_BUNDLE)) {
//					if (orderLine.getTotalCount() > 0) {
//						for (Product product : group.getProducts()) {
//							if (("" + bundle.getProductId()).equals(((MobileProduct) product).getFlags())) {
//								((MobileProduct) product).addToProductToCountsMap(productToCountMap, 1, 0, null);
//								break;
//							}
//						}
//					}
//				}
//			}
//		}
//		adjustOrderLinesForProducts(group, productToCountMap, null);
	}

	private void removeOrderLinesForProduct(String productId) {
		Iterator<OrderLine> orderLineIterator = orderLines.iterator();
		while (orderLineIterator.hasNext()) {
			OrderLine orderLine = orderLineIterator.next();
			if (Objects.equals(productId, orderLine.getProduct() == null ? null : orderLine.getProduct().getProductId())) {
				orderLineIterator.remove();
			}
		}
	}

	public void removeOrderLinesForGroup(MobileProductGroupEnum groupType) {
		ProductGroupDao productGroupDao = Lookup.lookup(ProductGroupDao.class);
		adjustOrderLinesForProducts(productGroupDao.findByBusinessAreaAndUniqueName(getBusinessArea(), groupType.getKey()), new HashMap<>(), null);
	}
}
