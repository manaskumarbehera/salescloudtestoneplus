package dk.jyskit.salescloud.application.pages.admin.dashboard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.googlecode.wickedcharts.wicket6.JavaScriptResourceRegistry;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.inject.Inject;
import com.googlecode.wickedcharts.highcharts.options.Axis;
import com.googlecode.wickedcharts.highcharts.options.AxisType;
import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.CreditOptions;
import com.googlecode.wickedcharts.highcharts.options.Options;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;
import com.googlecode.wickedcharts.highcharts.options.Title;
import com.googlecode.wickedcharts.highcharts.options.series.Coordinate;
import com.googlecode.wickedcharts.highcharts.options.series.CustomCoordinatesSeries;
import com.googlecode.wickedcharts.wicket6.highcharts.Chart;

import dk.jyskit.salescloud.application.CoreSession;
import dk.jyskit.salescloud.application.dao.BusinessAreaDao;
import dk.jyskit.salescloud.application.model.BusinessArea;
import dk.jyskit.salescloud.application.model.ContractStatusEnum;
import dk.jyskit.salescloud.application.model.Organisation;
import dk.jyskit.salescloud.application.model.SalesmanagerRole;
import dk.jyskit.waf.application.dao.DaoHelper;
import dk.jyskit.waf.utils.guice.Lookup;
import lombok.Data;

@Data
public class ChartFactory implements Serializable {
	private ChartTypeEnum chartType = ChartTypeEnum.BUSINESS_AREAS;
	private Organisation organisation = null;
//	private ContractActivityTypeEnum contractActivity = ContractActivityTypeEnum.TOTAL;
	
	public Chart build() {
		Options options= new Options();
		options.setCredits(new CreditOptions().setEnabled(false));
//        options.setTitle(new Title("Kontrakter pr. dag"));
        options.setTitle(new Title(chartType.getDisplayText()));
        options.setChartOptions(new ChartOptions().setType(SeriesType.LINE));
        options.setxAxis(new Axis(AxisType.DATETIME).setTitle(new Title("Tid")));
        options.setyAxis(new Axis().setTitle(new Title("Antal")));
		JavaScriptResourceRegistry.getInstance().setHighchartsReference("https://code.highcharts.com/3.0.2/highcharts.js");
		JavaScriptResourceRegistry.getInstance().setHighchartsMoreReference("https://code.highcharts.com/3.0.2/highcharts-more.js");
		JavaScriptResourceRegistry.getInstance().setHighchartsExportingReference("https://code.highcharts.com/3.0.2/modules/exporting.js");

        if (ChartTypeEnum.BUSINESS_AREAS.equals(chartType)) {
        	BusinessAreaDao businessAreaDao = Lookup.lookup(BusinessAreaDao.class);
        	List<BusinessArea> businessAreas = businessAreaDao.findAll();
        	for (BusinessArea businessArea : businessAreas) {
        		if (!businessArea.isActive()) {
        			continue;
        		}
        		List<Coordinate<String, Float>> coordinates = new ArrayList<>();
        		Calendar cal = Calendar.getInstance();
        		cal.setTime(new Date());
        		cal.add(Calendar.MONTH, -3);
        		List<Pair<Date, Float>> contractsPerWeek = getContractsPerWeek(businessArea, cal.getTime());
        		for (Pair<Date, Float> pair : contractsPerWeek) {
        			cal.setTime(pair.getKey());
        			String jsString = String.format("Date.UTC(%d,%d,%d)", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        			coordinates.add(new Coordinate<>(jsString,pair.getValue()));
        		}
        		options.addSeries(new CustomCoordinatesSeries<String, Float>().setData(coordinates).setName(businessArea.getName()));
        	}
        } else {
        	for (ContractActivityTypeEnum contractActivityType: ContractActivityTypeEnum.valuesAsList()) {
        		List<Coordinate<String, Float>> coordinates = new ArrayList<>();
        		Calendar cal = Calendar.getInstance();
        		cal.setTime(new Date());
        		cal.add(Calendar.MONTH, -3);
        		List<Pair<Date, Float>> contractsPerWeek = getContractsPerWeek(contractActivityType, cal.getTime());
        		for (Pair<Date, Float> pair : contractsPerWeek) {
        			cal.setTime(pair.getKey());
        			String jsString = String.format("Date.UTC(%d,%d,%d)", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        			coordinates.add(new Coordinate<>(jsString,pair.getValue()));
        		}
        		options.addSeries(new CustomCoordinatesSeries<String, Float>().setData(coordinates).setName(contractActivityType.getDisplayText()));
        	}
        }
        
        final Chart chart = new Chart("chart", options);
        return chart;
    }

	public List<Pair<Date,Float>> getContractsPerWeek(BusinessArea businessArea, Date from) {
        EntityManager em = DaoHelper.getEntityManager();
        Query query = null;  
        boolean allDivisions = DivisionHelper.includeAllDivisions();
        
        if (allDivisions) {
            query = em.createNativeQuery("select creationdate, lastModificationDate from contract where business_area_id=? and (not deleted=true) and creationdate>? order by creationdate asc");  
        } else if (CoreSession.get().getActiveRoleClass().equals(SalesmanagerRole.class)) {
            query = em.createNativeQuery("select c.creationdate, r.division from contract c, SALESPERSONROLE r where c.salesperson_id = r.id and (not deleted=true) and business_area_id=? and creationdate>? order by creationdate asc");  
        }
        query.setParameter(1, businessArea.getId());  
        query.setParameter(2, from);  
        List<Object[]> values = query.getResultList();

        Date date = from;
        Map<Date, MutableInt> statisticsMap = new TreeMap<>();
        for (Object[] o : values) {
        	Date contractDate = (Date) o[0];
        	while(date.before(contractDate)) {
        		MutableInt mutableInt = statisticsMap.get(date);
        		if (mutableInt == null) {
            		statisticsMap.put(date, new MutableInt(0));
        		}
        		date = DateUtils.addDays(date, 1);
        	}
    		MutableInt mutableInt = statisticsMap.get(date);
    		if (mutableInt == null) {
    			mutableInt = new MutableInt();
        		statisticsMap.put(date, mutableInt);
    		}
    		if (!allDivisions) {
            	String division = (String) o[1];
            	if (DivisionHelper.skipDivision(CoreSession.get().getSalesmanagerRole(), division)) {
            		continue; // skip contract
            	}
    		}
    		mutableInt.add(1);
        }

        List<Pair<Date,Float>> result = new ArrayList<>(statisticsMap.size());
        for (Map.Entry<Date, MutableInt> e : statisticsMap.entrySet()) {
            if (e.getValue().intValue() > 0) {
                result.add(new ImmutablePair<>(e.getKey(), e.getValue().floatValue()));
            }
        }

        return result;
    }

	public List<Pair<Date,Float>> getContractsPerWeek(ContractActivityTypeEnum requestedActivityType, Date from) {
        EntityManager em = DaoHelper.getEntityManager();
        Query query = null;  
        boolean allDivisions = DivisionHelper.includeAllDivisions();
        
        if (allDivisions) {
            query = em.createNativeQuery("select c.creationdate, c.lastModificationDate, c.statusChangedDate, c.status, r.organisation_id from contract c, SALESPERSONROLE r where c.salesperson_id = r.id and (not deleted=true) and lastModificationDate>? order by lastModificationDate asc");  
        } else if (CoreSession.get().getActiveRoleClass().equals(SalesmanagerRole.class)) {
            query = em.createNativeQuery("select c.creationdate, c.lastModificationDate, c.statusChangedDate, c.status, r.organisation_id, r.division from contract c, SALESPERSONROLE r where c.salesperson_id = r.id and (not deleted=true) and lastModificationDate>? order by lastModificationDate asc");  
        }
        query.setParameter(1, from);  
        List<Object[]> values = query.getResultList();

        Date date = from;
        Map<Date, MutableInt> statisticsMap = new TreeMap<>();
        for (Object[] o : values) {
        	Date creationDate			= (Date) o[0];
        	Date lastModificationDate	= (Date) o[1];
        	Date statusChangedDate 		= (Date) o[2];
        	String statusValue = (String) o[3];
        	Long organisationId = (Long) o[4];
        	
        	if (chartType.equals(ChartTypeEnum.CONTRACT_ACTIVITY_ORGANISATION) && (!organisationId.equals(organisation.getId()))) {
        		continue;
        	}
        	
        	ContractStatusEnum status = (statusValue == null ? ContractStatusEnum.OPEN : ContractStatusEnum.valueOf(statusValue));
        	
        	Date dateOfInterest = creationDate;
        	if (lastModificationDate != null) {
            	dateOfInterest = lastModificationDate;
        	}
        	if (statusChangedDate != null) {
            	dateOfInterest = statusChangedDate;
        	}
        	
        	ContractActivityTypeEnum contractActivityType = ContractActivityTypeEnum.NEW;
        	
			if (ContractStatusEnum.OPEN.equals(status)) {
        		if (statusChangedDate != null) {
					if (!DateUtils.isSameDay(statusChangedDate, creationDate)) {
			        	contractActivityType = ContractActivityTypeEnum.CHANGED_AND_OPEN;
					}
        		}
			} else {
				if (ContractStatusEnum.CLOSED.equals(status)) {
					contractActivityType = ContractActivityTypeEnum.CLOSED_OTHER;
				} else if (ContractStatusEnum.WON.equals(status)) {
					contractActivityType = ContractActivityTypeEnum.WON;
				} else if (ContractStatusEnum.LOST.equals(status)) {
					contractActivityType = ContractActivityTypeEnum.LOST;
				}
			}
			
			if (requestedActivityType.equals(ContractActivityTypeEnum.ALL) || requestedActivityType.equals(contractActivityType)) {
	        	while(date.before(dateOfInterest)) {
	        		MutableInt mutableInt = statisticsMap.get(date);
	        		if (mutableInt == null) {
	            		statisticsMap.put(date, new MutableInt(0));
	        		}
	        		date = DateUtils.addDays(date, 1);
	        	}
	    		MutableInt mutableInt = statisticsMap.get(date);
	    		if (mutableInt == null) {
	    			mutableInt = new MutableInt();
	        		statisticsMap.put(date, mutableInt);
	    		}
	    		if (!allDivisions) {
	            	String division = (String) o[5];
	            	if (DivisionHelper.skipDivision(CoreSession.get().getSalesmanagerRole(), division)) {
	            		continue; // skip contract
	            	}
	    		}
	    		mutableInt.add(1);
			}
        }

        List<Pair<Date,Float>> result = new ArrayList<>(statisticsMap.size());
        for (Map.Entry<Date, MutableInt> e : statisticsMap.entrySet()) {
            if (e.getValue().intValue() > 0) {
                result.add(new ImmutablePair<>(e.getKey(), e.getValue().floatValue()));
            }
        }

        return result;
    }
}
