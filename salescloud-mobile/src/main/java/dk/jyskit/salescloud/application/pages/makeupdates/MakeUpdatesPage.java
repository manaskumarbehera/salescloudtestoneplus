package dk.jyskit.salescloud.application.pages.makeupdates;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.jyskit.salescloud.application.dao.*;
import dk.jyskit.salescloud.application.model.*;
import dk.jyskit.salescloud.application.utils.json.JacksonJava8Module;
import net.minidev.json.JSONUtil;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.inject.Inject;

import dk.jyskit.salescloud.application.MobileSession;
import dk.jyskit.salescloud.application.pages.base.BasePage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@AuthorizeInstantiation({ AdminRole.ROLE_NAME, SalespersonRole.ROLE_NAME, SalesmanagerRole.ROLE_NAME,
		UserManagerRole.ROLE_NAME })
@SuppressWarnings("serial")
@Slf4j
public class MakeUpdatesPage extends BasePage {
	@Inject
	private ContractDao contractDao;
	@Inject
	private MobileContractDao mobileContractDao;
	@Inject
	private MobileContractSummaryDao mobileContractSummaryDao;
	@Inject
	private SystemUpdateDao systemUpdateDao;
	@Inject
	private BusinessAreaDao businessAreaDao;

	public MakeUpdatesPage(PageParameters parameters) {
		super(parameters);

		if ("backupdb".equals(parameters.get("cmd").toOptionalString())) {
			try {
				Date from = new SimpleDateFormat("yyyyMMdd").parse(parameters.get("from").toOptionalString());
				try {
					for (Contract c : contractDao.findNewerThan(from)) {
						MobileContract contract = (MobileContract) c;
						MobileSession.get().setContract(contract);

						// Export contract
						try {
							ObjectMapper objectMapper = new ObjectMapper();
							objectMapper.setVisibility(
									objectMapper.getSerializationConfig().
											getDefaultVisibilityChecker().
											withFieldVisibility(JsonAutoDetect.Visibility.ANY).
											withGetterVisibility(JsonAutoDetect.Visibility.NONE));

//							JacksonJava8Module module = new JacksonJava8Module();
//
//							module.addSerializer(MobileContract.class,
//									(obj, jgen) -> {
//										jgen.writeStringField("name", obj.getName());
//										jgen.writeStringField("image", obj.getStaticContent().getFileName());
//										jgen.writeObjectField("boxSets", obj.getBoxSets());
//									}
//							);




//							module.addSerializer(Product.class,
//									(obj, jgen) -> {
//										jgen.writeStringField("name", obj.getName());
//										jgen.writeStringField("image", obj.getStaticContent().getFileName());
//										jgen.writeObjectField("boxSets", obj.getBoxSets());
//									}
//							);
//
//							module.addSerializer(BoxSet.class,
//									(obj, jgen) -> {
//										jgen.writeStringField("tableDeviceId", obj.getTabletDeviceId());
//										jgen.writeObjectField("boxes", obj.getGameBoxs());
////										jgen.writeStringField("startPadlockCode", obj.getStartPadlockCode());
//									}
//							);
//
//							// GameBox and BoxType are "combined" in the json
//							module.addSerializer(GameBox.class,
//									(obj, jgen) -> {
//										ApplicationSession.get().setCurrentBox(obj);
//										jgen.writeStringField("name", obj.getBoxType().getName());
//										jgen.writeObjectField("startPuzzleItems", obj.getBoxType().getStartPuzzleItems());
//										jgen.writeObjectField("gameContainers", obj.getBoxType().getGameContainers());
//									}
//							);
//
//							module.addSerializer(GameContainer.class,
//									(obj, jgen) -> {
//										jgen.writeStringField("name", obj.getBoxType().getName() + " - " + obj.getName());
//										jgen.writeNumberField("qrCode", obj.getQrCode(ApplicationSession.get().getCurrentBox()));
//										jgen.writeObjectField("gameContainers", obj.getGameContainers());
//										jgen.writeBooleanField("transition", obj.isTransitionToNext());
//
//										// Add items for next box, if any
//										List<GameItem> gameItems = obj.getGameItems();
//										if (obj.isTransitionToNext()) {
//											if (ApplicationSession.get().getCurrentBox().getIndexInList() < obj.getBoxType().getProduct().getBoxTypes().size() - 1) {
//												gameItems = ApplicationSession.get().getCurrentBox().getBoxSet().getGameBoxs().get(ApplicationSession.get().getCurrentBox().getIndexInList()+1).getBoxType().getStartPuzzleItems();
//											}
//										}
//										jgen.writeObjectField("gameItems", gameItems);
//									}
//							);
//
//							module.addSerializer(GameItem.class,
//									(obj, jgen) -> {
//										jgen.writeStringField("name", obj.getName());
//										jgen.writeStringField("image", (obj.getStaticContent() == null ? "x_im_x_xxxxxxxxxxx.jpg" : obj.getStaticContent().getFileName()));
//										jgen.writeObjectField("hints", obj.getHints());
//									}
//							);
//
//							module.addSerializer(Hint.class,
//									(obj, jgen) -> {
//										jgen.writeStringField("markdown", obj.getMarkdown());
//										jgen.writeNumberField("level", obj.getLevel());
//										jgen.writeNumberField("penaltySeconds", obj.getPenaltySeconds());
//									}
//							);

//							ObjectMapper mapper = new ObjectMapper();
//							mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//							String s = mapper.registerModule(module)
//									.writer(new DefaultPrettyPrinter())
//									.writeValueAsString(contract);

							String json = objectMapper
									.writer(new DefaultPrettyPrinter())
									.writeValueAsString(contract);
							String s = json;
							System.out.println(s);
							FileUtils.writeStringToFile(new File("" + contract.getId() + ".json"), s);

						} catch (IOException e) {
							log.error("", e);
						}
					}
				} catch (Exception e) {
					log.error("FAIL", e);
					handleInitializationException(e);
				}
			} catch (ParseException e) {
				log.error("", e);
			}
		}

//		{
//			String name = "Adding contract summaries"; // Don't change this name!
//			
//			SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.SWITCHBOARD); 
//			// Could be any businessarea
//			
//			if (update == null) {
//				log.info("Update starting: " + name);
//				update = new SystemUpdate();
//				update.setBusinessAreaId(BusinessAreas.SWITCHBOARD);
//				update.setName(name);
//				systemUpdateDao.save(update);
//
//				try {
//					for (MobileContract contract : mobileContractDao.findAll()) {
//						MobileSession.get().setContract(contract);
//						
//						MobileContractSummary mcs = MobileContractSummary.create(contract);
//						mobileContractSummaryDao.save(mcs);
//					}
//				} catch (Exception e) {
//					log.error("FAIL", e);
//					handleInitializationException(e);
//				}
//
//				log.info("Update done: " + name);
//			}
//		}

//		{
//			String name = "Remove Fiber 4"; // Don't change this name!
//
//			SystemUpdate update = systemUpdateDao.findByName(name, BusinessAreas.FIBER);
//
//			if (update == null) {
//				log.info("Update starting: " + name);
//
//				BusinessArea fiber = businessAreaDao.findUniqueByField("businessAreaId", BusinessAreas.FIBER);
//				if (fiber != null) {
//					try {
//						log.info("Deleting...");
//						for (MobileContract contract : mobileContractDao.findByField("businessArea", fiber)) {
//							if (fiber.equals(contract.getBusinessArea())) {
//								log.info("Deleting " + contract.getName());
//								mobileContractDao.delete(contract);
//							}
//						}
//						businessAreaDao.delete(fiber);
//						log.info("Deleted");
//
//						update = new SystemUpdate();
//						update.setBusinessAreaId(BusinessAreas.TDC_WORKS);
//						update.setName(name);
//						systemUpdateDao.save(update);
//					} catch (Exception e) {
//						log.error("FAIL", e);
//						handleInitializationException(e);
//					}
//				} else {
//					log.info("Fiber not found");
//				}
//
//				log.info("Update done: " + name);
//			}
//		}
	}

	private void handleInitializationException(Exception e) {
		if (e instanceof RollbackException) {
			if (((RollbackException) e).getCause() instanceof ConstraintViolationException) {
				handleConstraintViolationException((ConstraintViolationException) ((RollbackException) e).getCause());
			} else {
				log.error("A problem occured during initialization", e);
			}
		} else if (e instanceof ConstraintViolationException) {
			handleConstraintViolationException((ConstraintViolationException) e);
		} else if (e instanceof PersistenceException) {
			handlePersistenceException((PersistenceException) e);
		} else {
			log.error("A problem occured during initialization", e);
		}
	}

	private void handleConstraintViolationException(ConstraintViolationException e) {
		for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
			log.error(constraintViolation.getMessage());
			log.error("I'm guessing this is the problem: \n" + "An object of type '"
					+ constraintViolation.getLeafBean().getClass().getSimpleName() + "' has a property '"
					+ constraintViolation.getPropertyPath() + "' which has value '"
					+ constraintViolation.getInvalidValue() + "'. The problem is: '" + constraintViolation.getMessage()
					+ "'");
		}
	}

	private void handlePersistenceException(PersistenceException e) {
		if (e.getCause() instanceof ConstraintViolationException) {
			handleConstraintViolationException((ConstraintViolationException) e.getCause());
		} else {
			log.error("We may need to improve logging here!", e);
		}
	}
}
