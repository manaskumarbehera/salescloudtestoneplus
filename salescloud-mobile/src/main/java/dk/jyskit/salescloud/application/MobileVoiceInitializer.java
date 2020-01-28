package dk.jyskit.salescloud.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rjeschke.txtmark.Processor;
import com.google.inject.Inject;

import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.dao.CampaignDao;
import dk.jyskit.salescloud.application.dao.ContractDao;
import dk.jyskit.salescloud.application.dao.MobileProductDao;
import dk.jyskit.salescloud.application.dao.PageInfoDao;
import dk.jyskit.salescloud.application.dao.ProductBundleDao;
import dk.jyskit.salescloud.application.dao.ProductGroupDao;
import dk.jyskit.salescloud.application.dao.SystemUpdateDao;
import dk.jyskit.salescloud.application.extensions.MobileObjectFactory;
import dk.jyskit.salescloud.application.model.BundleProductRelation;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.BusinessAreas;
import dk.jyskit.salescloud.application.model.Campaign;
import dk.jyskit.salescloud.application.model.Contract;
import dk.jyskit.salescloud.application.model.FeatureType;
import dk.jyskit.salescloud.application.model.FeeCategory;
import dk.jyskit.salescloud.application.model.MobileContract;
import dk.jyskit.salescloud.application.model.MobileProduct;
import dk.jyskit.salescloud.application.model.MobileProductBundle;
import dk.jyskit.salescloud.application.model.MobileProductBundleEnum;
import dk.jyskit.salescloud.application.model.MobileProductGroupEnum;
import dk.jyskit.salescloud.application.model.PageInfo;
import dk.jyskit.salescloud.application.model.Product;
import dk.jyskit.salescloud.application.model.ProductBundle;
import dk.jyskit.salescloud.application.model.ProductGroup;
import dk.jyskit.salescloud.application.model.SalespersonRole;
import dk.jyskit.salescloud.application.model.SystemUpdate;
import dk.jyskit.salescloud.application.pages.CorePageIds;
import dk.jyskit.salescloud.application.pages.MobilePageIds;
import dk.jyskit.salescloud.application.pages.bundles.BundleCount;
import dk.jyskit.salescloud.application.services.contractsaver.ContractSaver;
import dk.jyskit.waf.application.Environment;
import dk.jyskit.waf.application.dao.RoleDao;
import dk.jyskit.waf.application.dao.UserDao;
import dk.jyskit.waf.application.model.BaseUser;
import dk.jyskit.waf.application.model.EntityState;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MobileVoiceInitializer extends BusinessAreaInitializer {

	@Inject private ContractSaver contractSaver;
	@Inject private BusinessAreaDao businessAreaDao;
	@Inject private MobileProductDao productDao;
	@Inject private ProductBundleDao productBundleDao;
	@Inject private CampaignDao campaignDao;
	@Inject private UserDao userDao;
	@Inject private RoleDao roleDao;
	@Inject private SystemUpdateDao systemUpdateDao;
	@Inject private PageInfoDao pageInfoDao;
	@Inject private ProductGroupDao productGroupDao;
	@Inject private ContractDao contractDao;
	@Inject private MobileObjectFactory objectFactory;
	private ProductGroup mobilPakkeGroup;
	private ProductGroup mobilPakkeTaleGroup;
	private ProductGroup mobilPakkeDataGroup;
	private ProductGroup mobilPakkeTilvalgGroup;
	private ProductGroup mobilPakkeUdlandGroup;
	private ProductGroup mobilMixGroup;
	private ProductGroup mobilMixTaleGroup;
	private ProductGroup mobilMixTaleTidGroup;
	private ProductGroup mobilMixDataGroup;
	private ProductGroup mobilMixDataAmountGroup;
	private ProductGroup mobilMixTilvalgGroup;
	private ProductGroup andreTilvalgGroup;
	private ProductGroup roamingGroup;
	private ProductGroup funktionerGroup;
	private ProductGroup extraGroup;
	
	public MobileVoiceInitializer() {
		super("Mobile Voice Initializer", BusinessAreas.MOBILE_VOICE, "TDC Mobilpakker", 
				"TDC Mobilpakker er abonnementer til både mindere og større virksomheder med mere værdi for pengene. "
				+ "Abonnementerne er mere end blot MB og Minutter. De giver mulighed for attraktive Pluspakker, der giver kunderne nye værktøjer og en nemmere hverdag.");
	}
	
	@Override
	public void makeUpgrades() {
		if (CoreApplication.get().getSetting(Environment.WAF_ENV).equals("dev")) {
			initGroups();
			
			// Now only necessary in dev mode
			update1();
			update2();
		}
		makeSystemUpdates(businessArea);
		
		businessAreaDao.save(businessArea);
	}

	private void update1() {
		Product product = coreProductDao.findByProductGroupAndProductId(businessArea.getId(), extraGroup.getUniqueName(), MobileProduct.PRODUCT_EXTRA_PREFIX + "1");
		if (product == null) {
			// --- missing products and product relations ---
			addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "1", "", "", 0, 0, 0, false, false, false, true, 1, 1);
			addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "2", "", "", 0, 0, 0, false, false, false, true, 1, 1);
			addProductWithProductId(extraGroup, MobileProduct.PRODUCT_EXTRA_PREFIX + "3", "", "", 0, 0, 0, false, false, false, true, 1, 1);
		}
	}
	
	private void update2() {
		List<Product> products = coreProductDao.findByBusinessArea(businessArea);
		for (Product product : products) {
			MobileProduct p = (MobileProduct) product;
			if (p.isIpsaDiscountEligible()) {
				((MobileProduct) product).setGks(true);
			} else {
				((MobileProduct) product).setGks(false);
			}
			productDao.save(p);
		}
	}
	
	private void makeSystemUpdates(BusinessArea businessArea) {
		{
			String name = "Features 1"; // Don't change this name!
			SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
			if ((update == null) || (Environment.isOneOf("dev"))) {
				log.info("Update starting: '" + name + "' for businessarea " + businessArea.getName());

				addFeatures();
				businessAreaDao.save(businessArea);
				
				log.info("Update done: " + name);
				update = new SystemUpdate();
				update.setBusinessAreaId(businessArea.getBusinessAreaId());
				update.setName(name);
				systemUpdateDao.save(update);
			}
		}
		
		{
			String name = "TDC Scale Mobilpakker"; // Don't change this name!
			SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
			if (update == null) {
				String oldName = "TDC Erhverv Mobil";
				String newName = "TDC Scale Mobilpakker";
				if (businessArea.getName().equals("TDC Erhverv Mobil")) {
					businessArea.setName(newName);
					businessArea.setIntroText(businessArea.getIntroText().replace(oldName, newName));
					
				}
				ContractDao contractDao = Lookup.lookup(ContractDao.class);
				List<Contract> contracts = contractDao.findByBusinessArea(businessArea);
				for (Contract contract : contracts) {
					if (contract.getOfferIntroText().contains(oldName)) {
						contract.setOfferIntroText(contract.getOfferIntroText().replace(oldName, newName));
//						contractDao.save(contract);
						contractSaver.save(contract);
					}
				}
				
				businessArea.setIntroText("TDC Mobilpakker er abonnementer til både mindre og større virksomheder.\n" + 
						"\n" + 
						"Af inkluderet indhold kan nævnes: Fri tale DK, Europa og dele af Verden. - 50 GB data i DK med 4G. - 3 GB data i Europa. - 3 stk. datasimkort. - Mulighed for optagelse af kald. - Omstilling. - Charfunktion. - Videofunktion. - TDC Play Musik og TDC Play Film & Serier.\n" + 
						"\n" + 
						"Der kan tilvælges 3 GB data i dele af verden til TDC Mobilpakke Ekstra Udland. - OBS: Tilvalget erstatter 3 GB data i Europa.\n" + 
						"\n" + 
						"OBS: Kampagner kan IKKE kombineres med Scale 2.0 løsninger!");
				
				for (PageInfo pageInfo : businessArea.getPages()) {
					pageInfo.setMiscMarkdown("<p><strong>Artikler fra TDC Perspektiv</strong></p>\n" + 
							"<ol>\n" + 
							"<li><a href=\"http://perspektiv.tdc.dk/mobilitet-baerer-ingen-forretningsvaerdi-i-sig-selv/\" target=\"_blank\">Gartner: Mobilitet bærer ingen forretningsværdi i sig selv</a></li>\n" + 
							"<li><a href=\"http://perspektiv.tdc.dk/altid-produktiv-ogsaa-udlandet/\" target=\"_blank\">Altid produktiv, også i udlandet</a></li>\n" + 
							"<li><a href=\"http://perspektiv.tdc.dk/collaboration-handler-om-oplevet-vaerdi/\" target=\"_blank\">Collaboration handler om oplevet værdi</a></li>\n" + 
							"</ol>\n");
					pageInfo.setMiscHtml(Processor.process(pageInfo.getMiscMarkdown()));

					if ("existingcontracts".equals(pageInfo.getPageId())) {
						pageInfo.setIntroMarkdown("Velkommen til TDC Mobilpakke salgskonfigurator.\n" + 
								"\n" + 
								"Konfiguratoren hjælper dig med at forberede samt afvikle dit kundemøde, herunder udarbejdet et tilbud.\n" + 
								"\n" + 
								"Det anbefales at du anvender Chrome, Firefox eller Opera browser. \n" + 
								"\n" + 
								"**OBS: Du kan altid få inspiration til din kundedialog ved at se nærmere på TDC Perspektiv artiklerne, som kan findes til højre.**");
						pageInfo.setIntroHtml(Processor.process(pageInfo.getIntroMarkdown()));
						pageInfo.setHelpMarkdown("Du har to muligheder for at arbejde med kundesager:\n" + 
								"\n" + 
								"  * Du kan åbne en eksisterende kundesag ved at klikke \"Vælg\" til venstre\n" + 
								"\n" + 
								"  * Du kan oprette en ny salgssag ved at klikke på \"Ny kundesag\"\n" + 
								"\n" + 
								"Du kan med fordel arbejde med kategorier for dine kundesager, for at skabe større overblik. Anvend ikonet med den lille mand øverst i konfiguratoren til højre og klik på \"Kundesagskategorier\" i menuen.");
						pageInfo.setHelpHtml(Processor.process(pageInfo.getHelpMarkdown()));
					} else if ("masterdata".equals(pageInfo.getPageId())) {
						pageInfo.setIntroMarkdown("For at kunne lave et tilbud, er det nødvendigt at du indtaster stamdata, som systemet kan brevflette med. Der er tale om oplysninger på dig som sælger, men også vigtige oplysninger omkring kunden.\n" + 
								"\n" + 
								"Hvis du indtaster CVR nummer på kunden, henter konfiguratoren selv øvrige tilgængelige data og slår virksomhedsadressen op på dækningskortet. Du kan på dækningskortet manuelt fremsøge alternative adresser.");
						pageInfo.setIntroHtml(Processor.process(pageInfo.getIntroMarkdown()));
						pageInfo.setHelpMarkdown("Det anbefales at du anvender knappen \"Videre\" igennem konfiguratoren. På denne måde sikres det, at du guides forbi alle relevante trin og derfor får angivet de nødvendige input til løsningen, samt løbende får gemt dine indtastninger.");
						pageInfo.setHelpHtml(Processor.process(pageInfo.getHelpMarkdown()));
					} else if ("Kundeprofil".equals(pageInfo.getTitle())) {
						pageInfo.setIntroMarkdown("Inden selve løsningen kan sammensættes, skal vi vide lidt om kunden og det pris-setup vi skal arbejde videre med.\n" + 
								"\n" + 
								"Angiv eventuel kampagne og anvend nedenstående spørgsmål for at afdække kundens behov. Find evt. hjælp til de enkelte emner i kassen til højre.");
						pageInfo.setIntroHtml(Processor.process(pageInfo.getIntroMarkdown()));
						pageInfo.setHelpMarkdown("**Kampagne:**\n" + 
								"\n" + 
								"I kampagneperioder vil kampagner kunne vælges i menuen til venstre.\n" + 
								"\n" + 
								"OBS:\n" + 
								"\n" + 
								"\n" + 
								"  * To eller flere kampagner kan ikke kombineres\n" + 
								"\n" + 
								"  * Vælges kampagne er det udelukkende de i kampagnen indeholdte abonnementer som præsenteres i konfiguratoren\n" + 
								"\n" + 
								"**Hjælp til behovsafdækning:**\n" + 
								"\n" + 
								"En fast pris er vigtig for kunden:\n" + 
								"\n" + 
								"  * Faste pakker hvor DK og EU tale/data er inkluderet. - ligeledes kan der tales i dele af verden\n" + 
								"\n" + 
								"Sikkerhed bekymrer kunden:\n" + 
								"\n" + 
								"  * Med Mobilsikkerhed får du antivirus, 10 GB databackup, antityveri og sikker browsing\n" + 
								"\n" + 
								"  * TDC har mulighed for at levere device manager system til håndtering af både smartphones, tablets og pc'ere\n" + 
								"\n" + 
								"Ny teknologi interesserer kunden:\n" + 
								"\n" + 
								"  *  TDC dækker 99 % af Danmark med mobildata hastigheder på mellem 17 og 71 Mbit med 4G. Se site vedr. netværket <a href=\"http://netvaerk.tdc.dk\" target=\"_blank\">her</a>\n" + 
								"\n" + 
								"  * Se dit forbrug eller angiv dine viderestillinger direkte i TDC Erhverv App\n" + 
								"\n" + 
								"Kunden har meget rejseaktivitet:\n" + 
								"\n" + 
								"  * TDC har introduceret hele EU (31 lande) som hjemmeroaming zone for tale\n" + 
								"\n" + 
								"  * 3 GB data til anvendelse i hele EU \n" + 
								"\n" + 
								"Kunden arbejder på farten:\n" + 
								"\n" + 
								"  * Datadeling er inkluderet, så du også er online på din tablet og PC mv.");
						pageInfo.setHelpHtml(Processor.process(pageInfo.getHelpMarkdown()));
					} else if ("TDC Mobile - Standard bundles".equals(pageInfo.getPageId())) {
						pageInfo.setIntroMarkdown("Med udgangspunkt i antallet af medarbejdere i kundens virksomhed, skal du nu vælge abonnementer til medarbejderne.\n" + 
								"\n" + 
								"Nedenstående abonnementer er pakketeret ud fra vores erfaringer omkring tale, data samt roaming behov i virksomheder.");
						pageInfo.setIntroHtml(Processor.process(pageInfo.getIntroMarkdown()));
						pageInfo.setHelpMarkdown("NB: Tryk på flere af de præsenterede services for at læse mere! - Husk roaming indhold, markeret med orange, i alle pakker!\n" + 
								"\n" + 
								"**Inkluderede services:**\n" + 
								"\n" + 
								"  * <a href=\"http://privat.tdc.dk/element.php?dogtag=p_tel_fordel_playm\" target=\"_blank\">TDC Play musik</a>\n" + 
								"\n" + 
								"*  TDC Play Film- og Seriepakken giver fri adgang til at se massevis af film og serier. Kunden kan se det hele på sin mobil, tablet og computer, når han vil, og så tit han vil. Man behøver ikke at bekymre sig om sit dataforbrug, for det er inkluderet. Brugervenligheden er i top med TDC Play TV og Film appen.\n" + 
								"\n" + 
								"\n" + 
								"  * 4G\n" + 
								"\n" + 
								"* Fri kald til voicemail\n" + 
								"\n" + 
								"  *  <a href=\"http://erhverv.tdc.dk/publish.php?id=14449\" target=\"_blank\">Online Selvbetjening</a>\n" + 
								"\n" + 
								"  * Voicemail\n" + 
								"\n" + 
								"  * Voice@mail\n" + 
								"\n" + 
								"  * HD Voice\n" + 
								"\n" + 
								"  * Konferencekald\n" + 
								"\n" + 
								"  * <a href=\"http://erhverv.tdc.dk/enterprise/element.php?dogtag=e_prod_bb_hotspot\" tar-get=\"_blank\">TDC Hotspot</a>\n" + 
								"\n" + 
								"<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" tar-get=\"_blank\">**Basis**</a>\n" + 
								"\n" + 
								"  * 10 timers tale, fri intern tale, sms og mms\n" + 
								"\n" + 
								"  * 1 GB data  med fleksible datapakker – Du kan bruge op til 4 ekstra datatrin á 49 kr. pr. md. Hvert datatrin indeholder samme mængde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n" + 
								"\n" + 
								"  * 4G\n" + 
								"\n" + 
								"<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" tar-get=\"_blank\">**Medium**</a>\n" + 
								"\n" + 
								"  * Fri timers tale, fri intern tale, sms og mms\n" + 
								"\n" + 
								"  * 5 GB data med fleksible datapakker – Du kan bruge op til 4 ekstra datatrin á 49 kr. pr. md. Hvert datatrin indeholder samme mængde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n" + 
								"\n" + 
								"  * 4G\n" + 
								"\n" + 
								"  * App Plus abonnement, med mulighed for køgrupper, omstilling, viderestilling, statusvisning og kalenderintegration\n" + 
								"\n" + 
								"<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" tar-get=\"_blank\">**Ekstra**</a>\n" + 
								"\n" + 
								"  * Fri tale, fri sms og mms\n" + 
								"\n" + 
								"  * 30 GB data med fleksible datapakker – Du kan bruge op til 4 ekstra datatrin á 49 kr. pr. md. Hvert datatrin indeholder samme mængde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n" + 
								"\n" + 
								"  * 4G\n" + 
								"\n" + 
								"  * App Plus abonnement, med mulighed for køgrupper, omstilling, viderestilling, statusvisning og kalenderintegration\n" + 
								"\n" + 
								"  * Datadeling – Op til 3 stk. ekstra SIM til deling af inkluderet data (4G) - Skal bestilles separat\n" + 
								"\n" + 
								"  * Mobilsikkerhed – antivirus og sikker browsing samt online storage\n" + 
								"\n" + 
								"  * Ekstra data i EU i trin af 3 GB til kr. 999,- pr. stk. (ikke rabatberettiget)\n" + 
								"\n" + 
								"<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" target=\"_blank\">**Ekstra Udland**</a>\n" + 
								"\n" + 
								"* Fri tale, fri sms og mms\n" + 
								"\n" + 
								"  * 50 GB data med fleksible datapakker – Du kan bruge op til 4 ekstra datatrin á 49 kr. pr. md. Hvert datatrin indeholder samme mængde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n" + 
								"\n" + 
								"  * 4G\n" + 
								"\n" + 
								"  * App Plus abonnement, med mulighed for køgrupper, omstilling, viderestilling, statusvisning og kalenderintegration\n" + 
								"\n" + 
								"  * Datadeling – Op til 3 stk. ekstra SIM til deling af inkluderet data (4G) - Skal bestilles separat\n" + 
								"\n" + 
								"  * Mobilsikkerhed – antivirus og sikker browsing samt online storage\n" + 
								"\n" + 
								"  * Ekstra data i EU i trin af 3 GB til kr. 999,- pr. stk. (ikke rabatberettiget)\n" + 
								"\n" + 
								"\n" + 
								"Følgende lande er inkluderet i Europa (alle netværk): Belgien, Bulgarien, Cypern, Estland, Finland, Frankrig, Gibraltar, Grækenland, Guadeloupe/Martinique og Fransk Guiana, Guernsey,\n" + 
								"Holland, Irland, Island, Isle of Man, Italien, Jersey, Kroatien ,Letland, Liechtenstein, Litauen, Luxem-bourg, Malta, Norge, Polen, Portugal, Réunion, Rumænien, Schweiz, Slovakiet, Slovenien,\n" + 
								"Spanien, Storbritannien, Sverige, Tjekkiet, Tyskland, Ungarn og Østrig.\n" + 
								"\n" + 
								"\n" + 
								"Følgende lande er herudover med i Verden Hjemmezone (Partnernetværk): Albanien: Vodafone Albanien. Australien: T elstra, SingTel Optus, Vodafone Hutchison. Brasilien: Vivo.\n" + 
								"Cambodia: Metfone (Viettel Group). Canada: Telus, Bell Mobility. Chile: Entel Telefonia Mòvil S.a. Egyp-ten: Vodafone. Færøerne: Faroese Telecom, Kall - Vodafone Faroe Islands. Ghana: Vodafone\n" + 
								"Ghana. Grønland: Tele Greenland. Hong Kong: Hutchison. Hviderusland: Life BeST. Indien: Vodafone Essar - Gujarat, Vodafone Essar - Dehli - Mumbai - Kolkata - Tamil Nadu - Kerala");
						pageInfo.setHelpHtml(Processor.process(pageInfo.getHelpMarkdown()));
					} else if ("TDC Mobile - Product Selection".equals(pageInfo.getPageId())) {
						pageInfo.setIntroMarkdown("Vælg herunder roaming tilvalg til kunden.\n" + 
								"\n" + 
								"Anvend evt. <a href=\"http://erhverv.tdc.dk/mobil/udlandspriser/udlandet.php\" target=\"_blank\">udlandsberegner</a> fra tdc.dk");
						pageInfo.setIntroHtml(Processor.process(pageInfo.getIntroMarkdown()));
						pageInfo.setHelpMarkdown("");
						pageInfo.setHelpHtml("");
					} else if ("TDC Mobile - Contract summary".equals(pageInfo.getPageId())) {
						pageInfo.setIntroMarkdown("Du kan nu danne et indledningsvist prisoverslag eller et færdigt tilbud over den konfigurerede løsning. Anvend knapperne i nedenstående felt.\n" + 
								"\n" + 
								"Rabatprocenten fra Fordels- eller Erhvervsaftale tastes under \"Kontrakt type\" og \"TDC Erhverv Rabaraftale\". NB: Du kan kun arbejde med de kendte rabatprocenter fra Fordels- eller Erhvervsaftale. - Hvis du anvender konfigurator i forbindelse med en kampagne, skal rabatprocent angives til 27.\n" + 
								"\n" + 
								"**NB: Du SKAL forsat udarbejde kontrakt (Fordelsaftale eller Erhvervskontrakt) i CDM eller Excel værktøj.**");
						pageInfo.setIntroHtml(Processor.process(pageInfo.getIntroMarkdown()));
						pageInfo.setHelpMarkdown("Rabatprocenter til TDC Erhverv Fordelsaftale:\n" + 
								"\n" + 
								"  * 2-årig: 25 %\n" + 
								"  * 3-årig: 27 %\n" + 
								"  * 4-årig: 29 % \n" + 
								"\n" + 
								"Knappen \"Vilkår\" giver dig abonnementsvilkår for TDC Mobilpakker.");
						pageInfo.setHelpHtml(Processor.process(pageInfo.getHelpMarkdown()));
					}
					pageInfoDao.save(pageInfo);
				}
				
				for (ProductGroup mainProductGroup : businessArea.getProductGroups()) {
					for (ProductGroup productGroup : mainProductGroup.getChildProductGroups()) {
						if ("add-on.roaming".equals(productGroup.getUniqueName())) {
							productGroup.setHelpMarkdown("**Verden Hjemmezone Data (3GB)** \n" + 
									"\n" + 
									"\n" + 
									"Datapakke på partnernetværk som gælder både i EU og Verden for kr. 1.400,- pr. mdr.(rabatberettiget). Erstatter de 3 GB til brug kun i Europa. - Yderligere data i trin af 3 GB til kr. 2.999,- pr. stk. (ikke rabatberettiget) - \n" + 
									"\n" + 
									"\n" + 
									"OBS: Kan KUN tilkøbes til TDC Erhverv Mobil Ekstra Udland\n" + 
									"\n" + 
									"\n" + 
									"\n" + 
									"\n" + 
									"For at gøre det nemmere for dig at vælge den rette løsning, har vi delt verden op i nogle overskuelige zoner:\n" + 
									"\n" + 
									"**Norden**  \n" + 
									"Finland, Island, Norge og Sverige\n" + 
									"\n" + 
									"**Europa**  \n" + 
									"Belgien, Bulgarien, Estland, Finland, Frankrig (inkl. Fransk Guniea, Guadelope, Martinique, Réunion og Monaco), Holland, Island, Italien (inkl. Vatikanstaten og San Marino), Kroatien, Letland, Liechtenstein, Litauen, Luxembourg, Malta, Norge, Polen, Portugal (inkl. Azorerne og Madeira), Rumænien, Schweiz, Slovakiet, Spanien (inkl. De Kanariske Øer), Storbritannien (inkl. Guernsey, Jersey og Isle of Man), Sverige, Tjekkiet, Tyskland, Ungarn, Østrig\n" + 
									"\n" + 
									"**Verden**  \n" + 
									"\n" + 
									"**World North**  \n" + 
									"Alaska, Canada, USA (incl Hawaii, Puerto Rico, Virgin Islands (US), Antigua & Barbuda, Barbados, Caymans Islands, Dominica, St. Lucia & Saint Vincent) \n" + 
									"\n" + 
									"\n" + 
									"Bemærk at prisen for kald i Norden er højere end hvis der ikke til vælges dette produkt. Grunden til dette er at det udelukkende er TDC World som reguleres.\n" + 
									"\n" + 
									"**World East**  \n" + 
									"Hong Kong, Indien, Japan, Kina, Libyen, Malaysia Singapore, Thailand\n" + 
									"\n" + 
									"**World Central**  \n" + 
									"Australien, Bahrain, For. Arabiske Emirater, Kuwait, New Zealand, Oman, Pakistan, Saudi Arabien, Sydafrika, Taiwan, Tunesien, Vietnam, Rusland.");
							productGroup.setHelpHtml(Processor.process(productGroup.getHelpMarkdown()));
						} else if ("add-on.functions".equals(productGroup.getUniqueName())) {
							productGroup.setHelpMarkdown("");
							productGroup.setHelpHtml(Processor.process(productGroup.getHelpMarkdown()));
						}
						productGroupDao.save(productGroup);
					}
				}
				
				update = new SystemUpdate();
				update.setBusinessAreaId(businessArea.getBusinessAreaId());
				update.setName(name);
				systemUpdateDao.save(update);
			}
		}
		
		{
			String name = "Partner elements 1"; // Don't change this name!
			SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
			if ((update == null) || (Environment.isOneOf("dev"))) {
				log.info("Update starting: '" + name + "' for businessarea " + businessArea.getName());

				createPage(businessArea, MobilePageIds.MOBILE_PARTNER_SETTINGS, "Partner ydelser", null, 
						"Ved angivelse af diverse partner relaterede indstillinger for partner tekniker, support ydelser, udstyr og kundesegment, er det muligt at konfigurere "
						+ "separate partner tilbud, i till\u00e6g til tilbuddet p\u00e5 l\u00f8sningen, samt ikke mindst beregne vejledende provision for l\u00f8sningen.",
						"**Indstillinger**  \n" + 
						"\n" + 
						"Grundindstillinger for support, rate og provision.  \n" + 
						"\n" + 
						"Antallet af brugere bestemmes p\u00e5 baggrund af de valgte profiler under fanen ”Pakker”. Pris pr. bruger og \n" + 
						"grundpris for support aftale inds\u00e6ttes. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
						"\n" + 
						"L\u00f8betiden i m\u00e5neder f\u00f8lger aftalel\u00e6ngden p\u00e5 l\u00f8sningen – 3 \u00e5rs binding p\u00e5 l\u00f8sningen = 36 m\u00e5neder p\u00e5 support osv. \n" + 
						"\n" + 
						"Etableringspris og l\u00f8betid i m\u00e5neder for rate aftale angives. Disse priser fastl\u00e6gges p\u00e5 baggrund af lokal governance i de enkelte TDC Erhvervscentre.  \n" + 
						"\n" + 
						"Ved valg af upfront betaling p\u00e5 rate aftale, lyder f\u00f8rste afdrag p\u00e5 kr. 1.500,-. Den resterende del af aftalesummen afregnes over den resterende l\u00f8betid.  \n" + 
						"\n" + 
						"Under fakturering tages der stilling til om kunden skal tilmeldes PBS eller email fakturering. Denne indstilling g\u00e6lder kun for partnerydelser og styres p\u00e5 baggrund af lokal governance.  \n" +
						"\n" + 
						"Angiv hvilket segment kunder har. Feltet er styrende for beregning af provision.  \n" + 
						"\n" + 
						"**Øvrige installationsydelser**  \n" + 
						"\n" + 
						"Afkryds relevante installationsydelser  \n" + 
						"\n" + 
						"Diverse installationsydelser angives. Evt. rabat p\u00e5 egne installationsydelser angives.\n" + 
						"\n" + 
						"**Hardware til rate**  \n" + 
						"\n" + 
						"Antal og m\u00e5nedspris for hardware angives. \n" + 
						"\n" + 
						"**Sammenfatning partner**  \n" + 
						"\n" + 
						"Hent regneark med vejledende partnerprovision.");
				
				ProductGroup partnerGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_PARTNER);
				productGroupDao.save(partnerGroup);
				
				ProductGroup group = createProductGroup(partnerGroup, MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_INSTALLATION);
				group.setHelpMarkdown("**Hjælpetekst**  \n" +
						"\n" +
						"todo  \n");
				group.setHelpHtml(Processor.process(group.getHelpMarkdown()));
				
				addProductWithProductId(group, "9810001", "Kunde uddannelse på dagen", "Kunde uddannelse på dagen", 0, 0, 138700, false, false, false, true, 0, 0);
				addProductWithProductId(group, "9810002", "Kunde uddannelse 1-2 uger efter inst.", "Kunde uddannelse 1-2 uger efter inst.", 0, 0, 153600, false, false, false, true, 0, 0);
				MobileProduct p = addProductWithProductId(group, "9810003", "Installation - Diverse", "Installation - Diverse", 0, 0, 0, false, false, false, true, 0, 0);
				p.setVariableInstallationFee(true);
				p.setExcludeFromConfigurator(true);
				addProductWithProductId(group, "9810004", "Installation - Rabat", "Installation - Rabat", 0, 0, 0, false, false, false, true, 0, 0).setVariableInstallationFee(true);
				productGroupDao.save(group);
				
				group = createProductGroup(partnerGroup, MobileProductGroupEnum.PRODUCT_GROUP_PARTNER_HARDWARE);
				group.setHelpMarkdown("**Hjælpetekst**  \n" +
						"\n" +
						"todo  \n");
				group.setHelpHtml(Processor.process(group.getHelpMarkdown()));
				
				addProductWithProductId(group, "9910001", "Huawei 4G USB modem", "Huawei 4G USB modem", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
				addProductWithProductId(group, "9910002", "Huawei 4G wifi router", "Huawei 4G wifi router", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
				addProductWithProductId(group, "9910003", "Jabra/Plantronics/Sennheiser BT headset", "Jabra/Plantronics/Sennheiser BT headset", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
				addProductWithProductId(group, "9910004", "Jabra/Plantronics/Sennheiser Trådløst Office headset", "Jabra/Plantronics/Sennheiser Trådløst Office headset", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
				addProductWithProductId(group, "9910005", "Jabra/Plantronics/Sennheiser Rørløfter", "Jabra/Plantronics/Sennheiser Rørløfter", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
				addProductWithProductId(group, "9910006", "KAZAM Mobil telefon", "KAZAM Mobil telefon", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af 48 måneders udvidet garanti").setVariableRecurringFee(true);
				addProductWithProductId(group, "9910007", "DORO Mobil telefon", "DORO Mobil telefon", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
				addProductWithProductId(group, "9910008", "ALCATEL Mobil telefon", "ALCATEL Mobil telefon", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
				addProductWithProductId(group, "9910009", "LG 88xx ip apparat", "LG 88xx ip apparat", 0, 0, 0, false, false, false, true, 0, 0, "Produktet er omfattet af bytteret/service").setVariableRecurringFee(true);
				addProductWithProductId(group, "9910010", "", "Diverse hardware 1", 0, 0, 0, false, false, false, true, 0, 0, null).setVariableRecurringFee(true);
				addProductWithProductId(group, "9910011", "", "Diverse hardware 2", 0, 0, 0, false, false, false, true, 0, 0, null).setVariableRecurringFee(true);
				addProductWithProductId(group, "9910012", "", "Diverse hardware 3", 0, 0, 0, false, false, false, true, 0, 0, null).setVariableRecurringFee(true);
				productGroupDao.save(group);
				
				String[] segments = new String[] {"BS05", "BS30", "BS50", "EP", "Soho", "CS"};
				
				// Oprettelse:
				
				setProvision(segments, FeeCategory.ONETIME_FEE, "MobilMix grundabonnement", "38 113 210 100 100 50");   // ???
				setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Mobilpakke Basis", "150 450 840 100 100 50");
				setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Mobilpakke Medium", "225 675 1260 200 200 50");
				setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Mobilpakke Ekstra", "275 825 1540 200 200 50");
				setProvision(segments, FeeCategory.ONETIME_FEE, "TDC Mobilpakke Ekstra Udland", "300 900 1680 200 200 50");
				setProvision(segments, FeeCategory.ONETIME_FEE, "Antal tilvalg MOBIL MIX", "13 38 70 25 25 25");			// ???
				
				// Installation af løsning: 
				// N/A
				
				// Drift: 
				
				setProvision(segments, FeeCategory.RECURRING_FEE, "TDC Scale Mobilpakke Basis", "150 450 840 100 100 50");
				setProvision(segments, FeeCategory.RECURRING_FEE, "TDC Scale Mobilpakke Ekstra", "275 825 1540 200 200 50");
				setProvision(segments, FeeCategory.RECURRING_FEE, "TDC Scale Mobilpakke Ekstra Udland", "300 900 1680 200 200 50");
				
//				setProvision(segments, null, "Grund indstallation, brugerprofiler, søgegruppe mv.", 0, "150 450 840 100 100 50");		// ???
//				setProvision(segments, null, "Hjælp til indspilning af besked", 0, "275 825 1540 200 200 50");						// ???
//				setProvision(segments, null, "Opstart og kørsel", 0, "300 900 1680 200 200 50");										// ???
				
				businessAreaDao.save(businessArea);
				
//				Date date = DateUtils.addDays(new Date(), -30);
//				for (Contract contract: contractDao.findAll()) {
//					log.info("Contract " + contract.getId());
//					if ((contract.getCreationDate() != null) && date.before(contract.getCreationDate())) {
//						if (StringUtils.isEmpty(((MobileContract) contract).getVariableRecurringFees())) {
//							Map<Long, Integer> idToOneTimeFeeMap = new HashMap<>();
//							for (ProductGroup g : businessArea.getProductGroups()) {
//								for (ProductGroup cg : g.getChildProductGroups()) {
//									for (Product product : cg.getProducts()) {
//										if (((MobileProduct) product).isVariableRecurringFee()) {
//											idToOneTimeFeeMap.put(product.getId(), (int) (product.getPrice().getRecurringFee() / 100));
//										}
//									}
//								}
//							}
//							((MobileContract) contract).setVariableRecurringFees(MapUtils.mapToString(idToOneTimeFeeMap));
//							contractDao.save(contract);
//						}
//					}
//				}
				
				log.info("Update done: " + name);
				update = new SystemUpdate();
				update.setBusinessAreaId(businessArea.getBusinessAreaId());
				update.setName(name);
				systemUpdateDao.save(update);
			}
		}

		{
			String name = "Partner provision factors"; // Don't change this name!
			SystemUpdate update = systemUpdateDao.findByName(name, businessArea.getBusinessAreaId());
			if ((update == null) || (Environment.isOneOf("dev"))) {
				log.info("Update starting: '" + name + "' for businessarea " + businessArea.getName());

				businessArea.setProvisionFactorGeneral(0.9f);
				businessArea.setProvisionFactorXDSL(1.0f);
				
				businessAreaDao.save(businessArea);
				
				log.info("Update done: " + name);
				update = new SystemUpdate();
				update.setBusinessAreaId(businessArea.getBusinessAreaId());
				update.setName(name);
				systemUpdateDao.save(update);
			}
		}
		
		{
			String name = "Make MOBILE_VOICE inactive"; // Don't change this name!
			SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.MOBILE_VOICE);
			if (update == null) {
				log.info("Update starting: '" + name + "' for businessarea MOBILE_VOICE");
				
				List<BusinessArea> bas = businessAreaDao.findByField("name", "TDC Scale Mobilpakker");
				if (bas.size() == 1) {
					BusinessArea ba = bas.get(0);
					ba.setEntityState(EntityState.INACTIVE);
					businessAreaDao.save(ba);
				} else {
					bas = businessAreaDao.findByField("name", "TDC Mobilpakker");
					if (bas.size() == 1) {
						BusinessArea ba = bas.get(0);
						ba.setEntityState(EntityState.INACTIVE);
						businessAreaDao.save(ba);
					}
					
				}
				
				update = new SystemUpdate();
				update.setBusinessAreaId(BusinessAreas.MOBILE_VOICE);
				update.setName(name);
				systemUpdateDao.save(update);
			}
		}
	}

	private void addFeatures() {
		businessArea.addFeature(FeatureType.MOBILE_BUNDLES_STANDARD);
		businessArea.addFeature(FeatureType.MOBILE_BUNDLES_MIX);
		businessArea.addFeature(FeatureType.FIXED_DISCOUNT_SPECIFIED);
		businessArea.addFeature(FeatureType.OUTPUT_AUTHORITY);
		businessArea.addFeature(FeatureType.OUTPUT_PARTNER_SUPPORT);
		businessArea.addFeature(FeatureType.NETWORK_COVERAGE_MAP);
		businessArea.addFeature(FeatureType.FORDELSAFTALE);
		businessArea.addFeature(FeatureType.SHOW_INSTALLATION_DATE);
		businessArea.addFeature(FeatureType.SHOW_CONTRACT_START_DATE);
	}
	
	private void setProvision(String[] segments, FeeCategory category, String productName, String provisions) {
		for (ProductGroup productGroup : businessArea.getProductGroupsAndChildren()) {
			for (Product product : productGroup.getProducts()) {
				MobileProduct p = (MobileProduct) product;
				if (productName.equals(p.getInternalNameRaw())) {
					switch (category) {
					case ONETIME_FEE:
						System.out.println("Group: " + productGroup.getName() + ", Product: " + p.getInternalNameRaw() + ", Provisions (one-time): " + provisions);
						p.setProvisionOneTimeFee(provisions);
//						p.setBusinessValueOneTimeFee(businessValue);
						return;

					case INSTALLATION_FEE:
						System.out.println("Group: " + productGroup.getName() + ", Product: " + p.getInternalNameRaw() + ", Provisions (installation): " + provisions);
						p.setProvisionInstallationFee(provisions);
//						p.setBusinessValueInstallationFee(businessValue);
						return;

					case RECURRING_FEE:
						System.out.println("Group: " + productGroup.getName() + ", Product: " + p.getInternalNameRaw() + ", Provisions (recurring): " + provisions);
						p.setProvisionRecurringFee(provisions);
//						p.setBusinessValueRecurringFee(businessValue);
						return;

					default:
						break;
					}
				}
			}
		}
		System.out.println("Mobile Voice - No provision set for: " + productName);
	}

	@Override
	protected String getMiscMarkdown() {
		return "**Artikler fra TDC Perspektiv**\n\n" +
				"  1. <a href=\"http://perspektiv.tdc.dk/styrk-virksomheden-med-4g-daekning/\" target=\"_blank\">Styrk virksomheden med 4G dækning</a>\n" +
				"  2. <a href=\"http://perspektiv.tdc.dk/business-apps-kan-goere-dit-arbejde-nemmere/\" target=\"_blank\">Business apps kan gøre dit arbejde nemmere</a>\n" +
				"  3. <a href=\"http://perspektiv.tdc.dk/et-mobilt-loeft-til-arbejdsglaede-og-kundeservice/\" target=\"_blank\">Et mobilt løft til arbejdsglæde og kundeservice</a>\n";
	}

	@Override
	public void initPages(BusinessArea businessArea) {
		{
			createPage(businessArea, CorePageIds.SALES_EXISTING_CONTRACTS, "Vælg kundesag", null, 
					"Velkommen til Mobil Voice salgskonfigurator.\n\nKonfiguratoren skal hj\u00e6lpe dig til at lave det materiale, du skal bruge overfor dine kunder. Du kan danne et prisoverslag eller et egentligt tilbud, som kan bruges til at afstemme \u00f8konomi og l\u00f8sning med kunderne.\n\nDu kan altid f\u00e5 inspiration til din kundedialog ved at se n\u00e6rmere p\u00e5 TDC Perspektiv artiklerne som kan findes til h\u00f8jre.",
					"Du har to muligheder for at arbejde med kundesager:\n\n  * Du kan \u00e5bne en eksisterende kundesag ved at klikke \"V\u00e6lg\" til venstre\n\n  * Du kan oprette en ny salgssag ved at klikke p\u00e5 \"Ny kundesag\"\n\nDu kan med fordel arbejde med kategorier for dine kundesager for at skabe st\u00f8rre overblik. Anvend ikonet med den lille mand \u00f8verst i konfiguratoren til h\u00f8jre og klik p\u00e5 \"Kundesagskategorier\" i menuen.");
			
			createPage(businessArea, CorePageIds.SALES_MASTER_DATA, "Stamdata", null,
					"For at kunne lave et tilbud og et tastebilag, er det n\u00f8dvendigt at du indtaster stamdata, som systemet kan brevflette med. Der er tale om oplysninger p\u00e5 dig som s\u00e6lger, men ogs\u00e5 vigtige oplysninger omkring kunden.\n\nHvis du indtaster CVR nummer henter konfiguratoren selv \u00f8vrige data og sl\u00e5r virksomhedsadressen op p\u00e5 d\u00e6kningskortet. Du kan p\u00e5 d\u00e6kningskortet manuelt frems\u00f8ge alternative adresser, ligesom du, ved anvendelse af indstillingspanelerne i sidderne, kan f\u00e5 vist forskellige teknologier og fremtidig netv\u00e6rksudbygning for 3 m\u00e5neder.\n\nNB: Kontroller venligst de opsl\u00e5ede oplysninger p\u00e5 kunden", 
					"Det anbefales at du anvender knappen \"Videre\" igennem konfiguratoren. P\u00e5 denne m\u00e5de sikres det, at du guides forbi alle relevante trin og derfor f\u00e5r angivet de n\u00f8dvendige input til l\u00f8sningen.\n\nDe enkelte trin i en eksisterende kundesag vil altid v\u00e6re tilg\u00e6ngelige via menupunkterne, som ligger i toppen af konfiguratoren.");
			
			createPage(businessArea, MobilePageIds.MOBILE_CONTRACT_SETTINGS, "Kundeprofil", null, 
					"Inden selve l\u00f8sningen, skal vi vide lidt om kunden og det pris-setup vi skal arbejde videre med.\n\nAnvend nedenst\u00e5ende sp\u00f8rgsm\u00e5l for at afd\u00e6kke kundens behov. Find evt. hj\u00e6lp til de enkelte emner i kassen til h\u00f8jre.\n\n**NB: Det er vigtigt du anvender mark\u00f8rerne ved sp\u00f8rgsm\u00e5lene, da vi anvender input aktivt for at blive klogere p\u00e5 vores kunder.**", 
					"**Kampagne:**\n\nAktive kampagner vil i kampagneperidoen kunne v\u00e6lges i drop down.\n\nOBS:\n\n\n  * To eller flere kampagner kan ikke kombineres\n\n  * V\u00e6lges kampagne er det udelukkende de i kampagnen indeholdte abonnementer som pr\u00e6senteres i konfiguratoren\n\n**Hj\u00e6lp til behovsafd\u00e6kning:**\n\nEn fast pris er vigtig for kunden:\n\n  * Faste pakker hvor tale og data er inkluderet\n\n  * Budgetpakken giver ro i maven omkring opkald til voicemail samt indholdstakserede sms\'er\n\n  *  Sp\u00e6rring for roaming er en mulighed i TDC\n\nSikkerhed bekymrer kunden:\n\n  * Med Mobilsikkerhed f\u00e5r du antivirus, 10 GB databackup, antityveri og sikker browsing\n\n  * TDC har mulighed for at levere device manager system til h\u00e5ndtering af b\u00e5de smartphones, tablets og pc\'ere\n\nNy teknologi interesserer kunden:\n\n  *  Til sommer 2015 d\u00e6kker TDC 99 % af Danmark med mobildata hastigheder p\u00e5 mellem 17 og 71 Mbit med 4G. Se l\u00f8bende udrulning <a href=\"http://netvaerk.tdc.dk\" target=\"_blank\">her</a>\n\n  * Se dit forbrug eller angiv dine viderestillinger direkte i TDC Erhverv App\n\nKunden har meget rejseaktivitet:\n\n  * TDC har fordelagtige tale og data roaming pakker for Norden og Europa\n\nKunden arbejder p\u00e5 farten:\n\n  * Du kan til store datapakker tilv\u00e6lge datadeling, s\u00e5 du ogs\u00e5 er online p\u00e5 din tablet eller PC");
			
			createPage(businessArea, MobilePageIds.MOBILE_STANDARD_BUNDLES, "Abonnementer - TDC Erhverv Mobilpakker", null, 
					"Med udgangspunkt i antallet af medarbejdere i kundens virksomhed skal du nu v\u00e6lge abonnementer til medarbejderne.\n\nNedenst\u00e5ende abonnementer er pakketeret ud fra vores erfaringer omkring tale samt data behov i virksomheder. Hvis kunden ikke \u00f8nsker pakkerne, kan du sammens\u00e6tte 3 skr\u00e6ddersyede under TDC Erhverv Mobil Mix.",
					"NB: Tryk p\u00e5 flere af de pr\u00e6senterede services for at l\u00e6se mere!\n\n**Inkluderede services:**\n\n  * <a href=\"http://privat.tdc.dk/element.php?dogtag=p_tel_fordel_playm\" target=\"_blank\">TDC Play musik</a>\n\n  *  <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_samlet_app\" target=\"_blank\">TDC Erhverv app</a>\n\n  *  <a href=\"http://erhverv.tdc.dk/publish.php?id=14449\" target=\"_blank\">Online Selvbetjening</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_op_vm\" target=\"_blank\">Voicemail</a>\n\n  * Voice@mail\n\n  * HD Voice\n\n  *  <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_brug_kk\" target=\"_blank\">Konferencekald</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_brug_vs\" target=\"_blank\">Personlig viderestilling</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/faq.php?id=27051\" target=\"_blank\">Dataroaming gr\u00e6nser i hele verden (360 kr. pr. mdr.)</a>\n\n  * <a href=\"http://erhverv.tdc.dk/enterprise/element.php?dogtag=e_prod_bb_hotspot\" target=\"_blank\">TDC Hotspot</a>\n\n  * Pr\u00e6senter andet nummer\n\n<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" target=\"_blank\">**Basis**</a>\n\n  * 5 timers tale, fri intern tale, sms og mms\n\n  * 500 Mb data med fleksible datapakker \u2013 Du kan bruge op til 4 ekstra datatrin \u00e1 49 kr. pr. md. Hvert datatrin indeholder samme m\u00e6ngde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n\n  * Datahastighed op til 6 mbit/s\n\n  * Statusvisning\n\n<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" target=\"_blank\">**Medium**</a>\n\n  * 10 timers tale, fri intern tale, sms og mms\n\n  * 1 GB data med fleksible datapakker \u2013 Du kan bruge op til 4 ekstra datatrin \u00e1 49 kr. pr. md. Hvert datatrin indeholder samme m\u00e6ngde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n\n  * Datahastighed op til 10 mbit/s\n\n  * App Plus abonnement, med mulighed for k\u00f8grupper, omstilling, viderestilling, statusvisning og kalenderintegration\n\n  * Budgetpakke med fri voicemail\n\n<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" target=\"_blank\">**Ekstra**</a>\n\n  * Fri tale, fri sms og mms\n\n  * 5 GB data med fleksible datapakker \u2013 Du kan bruge op til 4 ekstra datatrin \u00e1 49 kr. pr. md. Hvert datatrin indeholder samme m\u00e6ngde data, som mobilpakken indeholder (Kan lukke for ekstra datatrin)\n\n  * Fuld hastighed p\u00e5 3G og 4G - datahastighed op til 150 mbit/s\n\n  * App Plus abonnement, med mulighed for k\u00f8grupper, omstilling, viderestilling, statusvisning og kalenderintegration\n\n  * Budgetpakke med fri voicemail\n\n  * Datadeling \u2013 \u00e9t ekstra SIM til deling af inkluderet data\n\n  * Mobilsikkerhed \u2013 antivirus og sikker browsing samt online storage\n\n<a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_mobil_abo\" target=\"_blank\">**Ekstra Udland**</a>\n\n  * Samme indhold som \"Ekstra\" men til den internationale mobilbruger med EU zone \u2013 2 timers tale, fri sms og 100 MB data i EU");
			
			createPage(businessArea, MobilePageIds.MOBILE_MIX_BUNDLES, "Abonnementer - TDC Erhverv MobilMix", null, 
					"Med udgangspunkt i antallet af medarbejdere i kundens virksomhed skal du nu sammens\u00e6tte abonnementer til medarbejderne.\n\nFor hvert abonnement er der mulighed for at lave tilpasninger s\u00e5 l\u00f8sningen skr\u00e6ddersyes den enkelte bruger. Du kan maksimalt sammens\u00e6tte 3 forskellige pakker pr. kunde", 
					"NB: Tryk p\u00e5 flere af de pr\u00e6senterede services for at l\u00e6se mere!\n\n**Inkluderede services:**\n\n  * <a href=\"http://privat.tdc.dk/element.php?dogtag=p_tel_fordel_playm\" target=\"_blank\">TDC Play musik</a>\n\n  *  <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_prod_samlet_app\" target=\"_blank\">TDC Erhverv app</a>\n\n  *  <a href=\"http://erhverv.tdc.dk/publish.php?id=14449\" target=\"_blank\">Online Selvbetjening</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_op_vm\" target=\"_blank\">Voicemail</a>\n\n  * Voice@mail\n\n  * HD Voice\n\n  *  <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_brug_kk\" target=\"_blank\">Konferencekald</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/publish.php?dogtag=e_help_mobil_brug_vs\" target=\"_blank\">Personlig viderestilling</a>\n\n  * <a href=\"http://kundeservice.tdc.dk/erhverv/faq.php?id=27051\" target=\"_blank\">Dataroaming gr\u00e6nser i hele verden (360 kr. pr. mdr.)</a>\n\n  * <a href=\"http://erhverv.tdc.dk/enterprise/element.php?dogtag=e_prod_bb_hotspot\" target=\"_blank\">TDC Hotspot</a>\n\n  * Pr\u00e6senter andet nummer\n\n <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_mobil_mix\" target=\"_blank\">**Tale**</a>\n\n  * 5 timer svarer til 15 minutter pr. arbejdsdag\n\n  * 10 timer svarer til 30 minutter pr. arbejdsdag\n\n  * 20 timer svarer til 60 minutter pr. arbejdsdag\n\n  * Fri tale giver frit forbrug med budgetsikkerhed\n\nV\u00e6lger du ogs\u00e5 fri intern tale, kan du tale alt det du vil med dine kolleger. De skal blot oprettes i samme lokalnummer gruppe.\n\n <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_mobil_mix\" target=\"_blank\">**Data**</a>\n\n  * 500 MB: Medarbejderen er mest p\u00e5 kontoret, hvor han som oftest tjekker sin mail og surfer via computeren. N\u00e5r han er ude, bruger han applikationer og holder sig orienteret p\u00e5 nyhedssites.\n\n  * 500 MB - 2 GB: Medarbejderens smartphone er uundv\u00e6rlig som arbejdsredskab. Mail og kalender er altid synkroniseret, s\u00e5 han kan holde sig orienteret om indkomne mails, n\u00e5r han er p\u00e5 farten. Han ser enkelte nyhedsklip og downloader dokumenter direkte til sin smartphone.\n\n  * 2-5 GB: Smartphonen er medarbejderens prim\u00e6re netmedie og arbejdsredskab. Mail og kalender er selvf\u00f8lgelig altid synkroniseret, den fungerer som navigation i bilen, som nyhedsmedie, og bruges til at vise streamede nyheds-spots. Smartphonen bruges desuden til at lave backup af dokumenter p\u00e5 en online-storage applikation.\n\n <a href=\"http://erhverv.tdc.dk/element.php?dogtag=e_mobil_mix\" target=\"_blank\">**Tilvalg**</a>\n\n  * App Plus abonnement, med mulighed for k\u00f8 grupper, omstilling, viderestilling, statusvisning og kalenderintegration\n\n  * Budgetpakke med fri voicemail og gratis oprettelse samt m\u00e5nedsabonnement p\u00e5 udvalgte roamingpakker\n\n  * Datadeling \u2013 \u00e9t ekstra SIM til deling af inkluderet data\n\n  * Mobilsikkerhed \u2013 antivirus og sikker browsing p\u00e5 mobilen samt sikker online storage\n\n  * Statusvisning \u2013 se kollegers status, t\u00e6ndt eller slukket, ledige eller optaget mv.");
			
			createPage(businessArea, MobilePageIds.MOBILE_PRODUCT_SELECTION, "Abonnementer - tilpasninger", null, 
					"V\u00e6lg herunder roaming tilvalg til kunden.\n\nAnvend evt. <a href=\"http://erhverv.tdc.dk/mobil/udlandspriser/udlandet.php\" target=\"_blank\">udlandsberegner</a> fra tdc.dk");
			
			createPage(businessArea, MobilePageIds.MOBILE_CONTRACT_SUMMARY, "Sammenfatning", null, 
					"Du kan nu danne et indledningsvist prisoverslag eller et f\u00e6rdigt tilbud over den konfigurerede l\u00f8sning. Anvend knapperne i nedenst\u00e5ende felt.\n\nRabatprocenten fra Fordels- eller Erhvervsaftale tastes under \"Kontrakt type\" og \"Valgfri rabat\". NB: Du kan kun arbejde med de kendte rabatprocenter fra Fordels- eller Erhvervsaftale.\n\nEfter kundeaccept skal du forsat udarbejde kontrakt i CDM eller Excel v\u00e6rkt\u00f8j.", 
					"Knappen \"Vilk\u00e5r\" giver dig abonnementsvilk\u00e5r for TDC Erhverv Mobilpakker og TDC Erhverv MobilMix.");
			
			createPage(businessArea, MobilePageIds.MOBILE_SUBSCRIPTION_CONFIGURATION, "Konfigurering af abonnementer", null, 
					"For at kunne bestille abonnementerne korrekt, skal de konfigureres til de forskellige medarbejdere hos kunden.\n\nAngiv herunder type, mobilnummer, navn/afdeling, ICC nummer og simkort type, samt marker hvilke tilvalg der skal knyttes til hvilke abonnementer.\n\nDer g\u00e6lder f\u00f8lgende regler ved udfyldelse:\n\n  * Ved \"Nyt telefonnummer\" skal Navn, Afdeling og Simkort type angives\n\n  * Ved \"Flytning til TDC\" skal Mobil nummer, Navn, Afdeling, ICC nummer og Simkort type angives\n\n  * Ved \"Eksisterende TDC nr.\" skal Mobil nummer, Navn, Afdeling, ICC nummer og Simkort type angives");
		}
	}
	
	@Override
	public void initProducts(BusinessArea businessArea) {
		addFeatures();

		// ----------------------------
		// Products
		// ----------------------------
		{
			// Groups
			
			initGroups();

			roamingGroup.setHelpMarkdown("For at gøre det nemmere for dig at vælge den rette løsning, har vi derfor delt verden op i nogle overskuelige zoner:\n\n" +
					"**Norden**  \n" + 
					"Finland, Island, Norge og Sverige\n\n" +
					"**Europa**  \n" + 
					"Belgien, Bulgarien, Estland, Finland, Frankrig (inkl. Fransk Guniea, Guadelope, Martinique, Réunion og Monaco), Holland, Island, Italien (inkl. Vatikanstaten og San Marino), Kroatien, "
					+ "Letland, Liechtenstein, Litauen, Luxembourg, Malta, Norge, Polen, Portugal (inkl. Azorerne og Madeira), Rumænien, Schweiz, Slovakiet, Spanien (inkl. De Kanariske Øer), "
					+ "Storbritannien (inkl. Guernsey, Jersey og Isle of Man), Sverige, Tjekkiet, Tyskland, Ungarn, Østrig\n\n" +
					"**Verden**  \n" + 
					"**World North**  \n" + 
					"Norden + Alaska, Canada, USA (incl Hawaii, Puerto Rico, Virgin Islands (US), Antigua & Barbuda, Barbados, Caymans Islands, Dominica, St. Lucia & Saint Vincent)\n\n" +
					"**World East**  \n" + 
					"Hong Kong, Indien, Japan, Kina, Kuwait, Libyen, Malaysia, New Zealand, Oman, Singapore, Thailand, Tunesien\n\n" +
					"**World Central**  \n" + 
					"Australien, Bahrain, For. Arabiske Emirater, Pakistan, Saudi Arabien, Sydafrika, Taiwan, Vietnam\n\n" +
					"*Bemærk at udlandsabonnementer og -pakker ikke er rabatberettigede.*");
			roamingGroup.setHelpHtml(Processor.process(roamingGroup.getHelpMarkdown()));
			
			funktionerGroup.setHelpMarkdown("**Kort forklaring til relevante funktionstilvalg**  \n" +
					"\n" +
					"**4G**  \n" + 
					"4G giver LTE adgang med hastighed 150/50 Mbit/s. En eventuel hastighedsnedsættelse følger speed drop på den prisplanen der er på abonnementet.\n" +
					"\n" +
					"**Begrænset bruger**  \n" + 
					"Med Begrænset bruger kan du angive hvor meget den enkelte medarbejder kan ringe ud. Medarbejderen kan som standard kun ringe til andre mobiltelefoner i samme lokalnummergruppe. OBS: Virker ikke i udlandet.\n" +
					"\n" +
					"**TDC Play Film og Seriepakke**  \n" + 
					"Adgang til film og serier via TDC Play App'en. Hvis kunden befinder sig på TDC's netværk, er streaming af film og serier på App'en inkluderet i kundens abonnement.\n" +
					"\n" +
					"**Secure Mobil**  \n" + 
					"Punkt-til-punkt sikkerhedsløsning. Nem og sikker mobil adgang til virksomhedens interne netværk - uden brug af VPN klienter. Virker også fra udlandet.\n" +
					"\n" +
					"**Saldomax**  \n" + 
					"Saldomax er et maksimumsbeløb for forbrug (følger måned). Når dette beløb er nået, spærres mobiltelefonen automatisk for yderligere forbrug. "
					+ "Ophæves ved kald til IVR. Du kan altid ringe til kundeservice og 112.\n");
			funktionerGroup.setHelpHtml(Processor.process(funktionerGroup.getHelpMarkdown()));
			
			// Products (Thomas)
			
			// "Falske" produkter. Taletid regnes som værende implicitte i standard bundles. Det gør det bare unødigt kompliceret.
			addProductWithNabsAndKvikCode(mobilPakkeGroup, "_pk_BASIS","TDC Erhverv Mobilpakke Basis","TDC Erhverv Mobilpakke Basis","TDC Erhverv Mobilpakke Basis",9900, 21200, 0, true, false);
			addProductWithNabsAndKvikCode(mobilPakkeGroup, "_pk_MEDIUM","TDC Erhverv Mobilpakke Medium","TDC Erhverv Mobilpakke Medium","TDC Erhverv Mobilpakke Medium",9900, 33200, 0, true, false);
			addProductWithNabsAndKvikCode(mobilPakkeGroup, "_pk_EKSTRA","TDC Erhverv Mobilpakke Ekstra","TDC Erhverv Mobilpakke Ekstra","TDC Erhverv Mobilpakke Ekstra",9900, 43900, 0, true, false);
			addProductWithNabsAndKvikCode(mobilPakkeGroup, "_pk_UDLAND","TDC Erhverv Mobilpakke Ekstra Udland","TDC Erhverv Mobilpakke Ekstra Udland","TDC Erhverv Mobilpakke Ekstra Udland",9900, 59900, 0, true, false);
			
			addProductWithNabsAndKvikCode(mobilPakkeTaleGroup, "_pk_TALE1", "", "5 Timers tale", "5 Timers tale", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeTaleGroup, "_pk_TALE2", "", "10 Timers tale", "10 Timers tale", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeTaleGroup, "_pk_TALE3", "", "20 Timers tale", "20 Timers tale", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeTaleGroup, "_pk_TALE4", "", "Fri tale", "Fri tale", 0, 0, 0, false, false);
			
			addProductWithNabsAndKvikCode(mobilPakkeDataGroup, "_pk_DATA1", "", "500 MB Data", "500 MB Data Trintakseret", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeDataGroup, "_pk_DATA2", "", "1 GB Data", "1 GB Data Trintakseret", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeDataGroup, "_pk_DATA3", "", "5 GB Data", "5 GB Data Trintakseret", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeDataGroup, "_pk_DATA2a", "", "5 GB Data", "5 GB Data Trintakseret - inkl. 1 Trin gratis", 0, 0, 0, false, false);

			addProductWithNabsAndKvikCode(mobilPakkeTilvalgGroup, "_pk_TILVALG1", "", "Budgetpakke", "Budgetpakke", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeTilvalgGroup, "_pk_TILVALG2", "", "App Plus", "App Plus, inkluderet", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeTilvalgGroup, "_pk_TILVALG3", "", "MobilSikkerhed", "MobilSikkerhed, inkluderet", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeTilvalgGroup, "_pk_TILVALG4", "", "4G", "4G Hastighed  inkluderet", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeTilvalgGroup, "_pk_TILVALG5", "", "Fri intern tale og fri sms/mms", "Fri intern tale og fri sms/mms", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeTilvalgGroup, "_pk_TILVALG_DATADELING", "", "Datadeling", "Datadeling, inkluderet", 0, 0, 0, false, false);
			addProductWithNabsAndKvikCode(mobilPakkeTilvalgGroup, "_pk_TILVALG_DATADELING_SIMKORT", "", "Datadeling Simkort Type", "Datadeling Simkort Type", 0, 0, 0, false, false);
			
			addProductWithNabsAndKvikCode(mobilPakkeUdlandGroup, "_pk_UDLAND1", "", "Udland EU Zone", "Udland EU Zone", 0, 0, 0, false, false);
			
			addProductWithNabsAndKvikCode(roamingGroup, "GNOR2T","TDC Norden 2T tale og SMS","Norden 2 timers tale","Norden lille tale og SMS", 49.00, 99.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GNORFRIT","TDC Norden 100 Timers tale og Fri SMS/MMS","Norden 100 timers tale","Norden stor tale og fri SMS/MMS",49.00,399.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GNOR100MB","TDC Norden 100 MB data","Norden 100 MB data","Norden lille data",49.00,149.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GNOR1GB","TDC Norden 1GB data","Norden 1 GB data","Norden stor data",49.00,599.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GEUR2T","TDC Europa 2T tale og fri SMS","Europa 2 timers tale","EU lille tale og sms",49.00,149.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GEURFRIT","TDC Europa 100T tale og fri SMS/MMS","Europa 100 timers tale","EU Stor tale og sms/MMS",49.00,499.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GEUR100MB","TDC Europa 100MB Data","Europa 100 MB data","EU Lille data",49.00,199.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GEUR1GB","TDC Europa 1GB Data","Europa 1 GB data","EU stor data",49.00,699.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GTDCWCENT","TDC World North","TDC World North","Rabat pris voice North",49.00,20.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GTDCWEAST","TDC World East","TDC World East","Rabat pris voice East",49.00,20.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GTDCWCENT","TDC World Central","TDC World Central","Rabat pris voice North",49.00,20.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "GDRME50","Øvrige verden månedspakke 50 MB","Øvrige Verden månedspakke 50 MB (1500,- pr pakke efter forbrug)","Rabat pris data øvrig verden",99.00,5.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(roamingGroup, "ASPROA","Spærring for roaming","Skal vi spærre nogle abonnementer for roaming?","Spær for roaming",0.00,0.00, 0.0, "0", "Ikke rabat", 0);
			
			addProductWithNabsAndKvikCode(funktionerGroup, "GTEM4G","TDC Erhverv 4G Mobil","4G","Op til 150 mbit",30.00,29.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(funktionerGroup, "GGRXTBBRU","Begrænset bruger","Begrænset bruger","Begrænset bruger",31.00,0.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(funktionerGroup, "GMEPLAY2","TDC Play Film- og Seriepakken","TDC Play Film- og Seriepakken","Film og Serier",0.00,23.20, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(funktionerGroup, "AMAX","Saldomax","Saldomax","Saldomax",0.00,0.00, 0.0, "0", "MobileE", 0);
			
			addProductWithNabsAndKvikCode(mobilMixGroup, "GMERHV","MobilMix grundabonnement","MobilMix grundabonnement","MobilMix grundabonnement",99.00,49.00, 0.0, "0", "MobileE", 0);
			
			addProductWithNabsAndKvikCode(mobilMixTaleTidGroup, "GVPAKMEB","5 Timer","5 Timer","Lille tale",30.00,69.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixTaleTidGroup, "GVPAKMEC","10 Timer","10 Timer","Mellem tale",30.00,109.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixTaleTidGroup, "GVPAKMED","20 Timer","20 Timer","Stor tale",30.00,149.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixTaleTidGroup, "GVPAKMEG","Fri Tale","Fri tale","Fri tale",30.00,169.00, 0.0, "0", "MobileE", 0);
			
			addProductWithNabsAndKvikCode(mobilMixTaleGroup, "GMEFRISM","Fri SMS/MMS","Fri SMS/MMS","Fri sms/MMS",49.00,19.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixTaleGroup, "GMEFIK","Fri interne kald","Fri interne kald","Fri kollega kald",49.00,19.00, 0.0, "0", "MobileE", 0);
			
			addProductWithNabsAndKvikCode(mobilMixDataAmountGroup, "GDPMECSC","500 MB. Data Trintakseret","500 MB Data Trintakseret","500 mb med automatisk topop",30.00,69.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixDataAmountGroup, "GDPMEDSC","1 GB. Data Trintakseret","1 GB Data Trintakseret","1 gb med automatisk topop",30.00,129.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixDataAmountGroup, "GDPMEESC","2 GB. Data Trintakseret","2 GB Data Trintakseret","2 gb med automatisk topop",30.00,159.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixDataAmountGroup, "GDPMEGSC","5 GB. Data Trintakseret","5 GB Data Trintakseret","5 gb med automatisk topop",30.00,179.00, 0.0, "0", "MobileE", 0);
			
			addProductWithNabsAndKvikCode(mobilMixTilvalgGroup, "GMEBUD","Budgetpakke","Budgetpakke","Fri voicemail",49.00,29.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixTilvalgGroup, "GMESIK","TDC Erhverv Mobilsikkerhed","TDC Erhverv Mobilsikkerhed","Sikkerheds pakke",49.00,29.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixTilvalgGroup, "GMEAPP","App Plus","App Plus","App med status",49.00,49.00, 0.0, "0", "MobileE", 0);
			addProductWithNabsAndKvikCode(mobilMixTilvalgGroup, "GEXCE1  ","Datadeling","Datadeling","Datadeling",49.00,29.00, 0.0, "0", "Ikke rabat", 0);
			addProductWithNabsAndKvikCode(mobilMixTilvalgGroup, "GMESTATV","Statusvisning","Statusvisning","Send status til andre",49.00,9.00, 0.0, "0", "MobileE", 0);
		}
		
		Campaign someCampaign = null;
		// ----------------------------
		// Campaigns
		// ----------------------------
		{
			Campaign campaign;
			
			campaign = objectFactory.createCampaign();
			campaign.setName("Ingen kampagne, men rabataftale");
			campaign.setFromDate(null);
			campaign.setToDate(null);		
			businessArea.addCampaign(campaign);
			
			// ----------------------------
			// Campaigns -> Bundles
			// ----------------------------
			{
				MobileProductBundle baseBundle = new MobileProductBundle();
				baseBundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
				baseBundle.setPublicName("Basis");
				baseBundle.setProductId("GMEPAK2");
				baseBundle.setKvikCode("TDC Erhverv Mobilpakke Basis");
				baseBundle.setInternalName(baseBundle.getKvikCode());
				baseBundle.setSortIndex(1);
				productBundleDao.save(baseBundle);
				campaign.addProductBundle(baseBundle);

				MobileProductBundle mediumBundle = new MobileProductBundle();
				mediumBundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
				mediumBundle.setPublicName("Medium");
				mediumBundle.setProductId("GMEPAK3");
				mediumBundle.setKvikCode("TDC Erhverv Mobilpakke Medium");
				mediumBundle.setInternalName(mediumBundle.getKvikCode());
				mediumBundle.setSortIndex(2);
				productBundleDao.save(mediumBundle);
				campaign.addProductBundle(mediumBundle);
				
				MobileProductBundle ekstraBundle = new MobileProductBundle();
				ekstraBundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
				ekstraBundle.setPublicName("Ekstra");
				ekstraBundle.setProductId("GMEPAK4");
				ekstraBundle.setKvikCode("TDC Erhverv Mobilpakke Ekstra");
				ekstraBundle.setInternalName(ekstraBundle.getKvikCode());
				ekstraBundle.setSortIndex(3);
				productBundleDao.save(ekstraBundle);
				campaign.addProductBundle(ekstraBundle);
				
				MobileProductBundle ekstraUdlandBundle = new MobileProductBundle();
				ekstraUdlandBundle.setBundleType(MobileProductBundleEnum.MOBILE_BUNDLE);
				ekstraUdlandBundle.setPublicName("Ekstra Udland");
				ekstraUdlandBundle.setProductId("GMEPAK5");
				ekstraUdlandBundle.setKvikCode("TDC Erhverv Mobilpakke Ekstra Udland");
				ekstraUdlandBundle.setInternalName(ekstraUdlandBundle.getKvikCode());
				ekstraUdlandBundle.setSortIndex(4);
				productBundleDao.save(ekstraUdlandBundle);
				campaign.addProductBundle(ekstraUdlandBundle);
				
				campaign = campaignDao.save(campaign);		// Bundles needs an ID
				
				addProductToBundle(baseBundle, "_pk_BASIS", true);	
				addProductToBundle(baseBundle, "_pk_TALE1", true);	
				addProductToBundle(baseBundle, "_pk_DATA1", true);	
				addProductToBundle(baseBundle, "_pk_TILVALG5", true);
				
				addProductToBundle(mediumBundle, "_pk_MEDIUM", true);	
				addProductToBundle(mediumBundle, "_pk_TALE2", true);	
				addProductToBundle(mediumBundle, "_pk_DATA2", true);	
				addProductToBundle(mediumBundle, "_pk_TILVALG1", true);
				addProductToBundle(mediumBundle, "_pk_TILVALG2", true);
				addProductToBundle(mediumBundle, "_pk_TILVALG5", true);
				
				addProductToBundle(ekstraBundle, "_pk_EKSTRA", true);	
				addProductToBundle(ekstraBundle, "_pk_TALE4", true);	
				addProductToBundle(ekstraBundle, "_pk_DATA3", true);	
				addProductToBundle(ekstraBundle, "_pk_TILVALG1", true);
				addProductToBundle(ekstraBundle, "_pk_TILVALG2", true);
				addProductToBundle(ekstraBundle, "_pk_TILVALG3", true);
				addProductToBundle(ekstraBundle, "_pk_TILVALG4", true);
				addProductToBundle(ekstraBundle, "_pk_TILVALG5", true);
				addProductToBundle(ekstraBundle, "_pk_TILVALG_DATADELING", true);
				
				addProductToBundle(ekstraUdlandBundle, "_pk_UDLAND", true);	
				addProductToBundle(ekstraUdlandBundle, "_pk_TALE4", true);	
				addProductToBundle(ekstraUdlandBundle, "_pk_DATA3", true);	
				addProductToBundle(ekstraUdlandBundle, "_pk_TILVALG1", true);
				addProductToBundle(ekstraUdlandBundle, "_pk_TILVALG2", true);
				addProductToBundle(ekstraUdlandBundle, "_pk_TILVALG3", true);
				addProductToBundle(ekstraUdlandBundle, "_pk_TILVALG4", true);
				addProductToBundle(ekstraUdlandBundle, "_pk_TILVALG5", true);
				addProductToBundle(ekstraUdlandBundle, "_pk_TILVALG_DATADELING", true);
				addProductToBundle(ekstraUdlandBundle, "_pk_UDLAND1", true);
			}
		}

		businessAreaDao.save(businessArea);  // unødvendigt, ikke?
	}

	@Override
	public void initTestContracts(BusinessArea businessArea) {
		for (BaseUser user : userDao.findAll()) {
			SalespersonRole salesperson = (SalespersonRole) user.getRole(SalespersonRole.class);
			if ((salesperson != null) && (salesperson.getUser().getUsername().startsWith("partner"))) {
				MobileContract contract = (MobileContract) objectFactory.createAndSaveContract(businessArea, salesperson);
				salesperson.addContract(contract);
				
				contract.setTitle("Test kontrakt");
				
				// Lets say the user orders 5 of each bundle associated with
				// the campaign.
				Map<MobileProductBundle, BundleCount> bundleToCountMap = new HashMap<>(); 
				for (ProductBundle productBundle : contract.getCampaigns().get(0).getProductBundles()) {
					if (MobileProductBundleEnum.MOBILE_BUNDLE.equals(((MobileProductBundle) productBundle).getBundleType())) {
						int subIndex = 0;
						BundleCount bc = new BundleCount((MobileProductBundle) productBundle, subIndex, 5, 0);
						bundleToCountMap.put((MobileProductBundle) productBundle, bc);
					}
				}

				// If the user wants to adjust what he has ordered (numbers, not type of bundles)
				// we first check if this is ok. You can't take away subscriptions that are already
				// fully or partly configured.
				if (contract.adjustSubscriptions(bundleToCountMap, true)) {
					// The change was ok, so adjust orderlines as well
					contract.adjustOrderLinesForBundles(bundleToCountMap, MobileProductBundleEnum.MOBILE_BUNDLE);
				}
				roleDao.save(salesperson);
			}
		}
	}

	protected void addProductToBundle(MobileProductBundle bundle, String nabs, boolean addProductPrice) {
		BundleProductRelation relation = new BundleProductRelation();
		relation.setProduct(productDao.findByField("productId", nabs).get(0));
		relation.setAddProductPrice(addProductPrice);
		relation.setSortIndex(1l);
		bundle.addProductRelation(relation);
	}

	private void initGroups() {
		mobilPakkeGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE);
		mobilPakkeTaleGroup = createProductGroup(mobilPakkeGroup, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_SPEECH);
		mobilPakkeDataGroup = createProductGroup(mobilPakkeGroup, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_DATA);
		mobilPakkeTilvalgGroup = createProductGroup(mobilPakkeGroup, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_ADDON);
		mobilPakkeUdlandGroup = createProductGroup(mobilPakkeGroup, MobileProductGroupEnum.PRODUCT_GROUP_STANDARD_BUNDLE_NON_DOMESTIC);

		mobilMixGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE);
		mobilMixTaleGroup = createProductGroup(mobilMixGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH);
		mobilMixTaleTidGroup = createProductGroup(mobilMixTaleGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_SPEECH_TIME);
		mobilMixDataGroup = createProductGroup(mobilMixGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA);
		mobilMixDataAmountGroup = createProductGroup(mobilMixDataGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_DATA_AMOUNT);
		mobilMixTilvalgGroup = createProductGroup(mobilMixGroup, MobileProductGroupEnum.PRODUCT_GROUP_MIX_BUNDLE_ADDON);

		andreTilvalgGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_ADDON);
		roamingGroup = createProductGroup(andreTilvalgGroup, MobileProductGroupEnum.PRODUCT_GROUP_ADDON_ROAMING);
		funktionerGroup = createProductGroup(andreTilvalgGroup, MobileProductGroupEnum.PRODUCT_GROUP_ADDON_FUNCTIONS);
		
		extraGroup = createProductGroup(businessArea, MobileProductGroupEnum.PRODUCT_GROUP_EXTRA);
	}
}
