package dk.jyskit.salescloud.application.model;

import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;
import dk.jyskit.waf.wicket.utils.WicketUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.*;

/**
 * Changing sortIndex is OK. Changing names (uppercase letters) is NOT OK (without modifying the database). 
 * 
 * @author jan
 */
@Embeddable
public enum AccessTypeEnum implements Serializable {
	NONE			(1, 1,  "AccessTypeEnum.none", "Ingen access")
	,XDSL			(2, 2,  "AccessTypeEnum.xdsl", "XDSL")
	,FIBER 			(3, 3,  "AccessTypeEnum.fiber", "Fiber")
	,FIBER_PLUS 	(4, 4,  "AccessTypeEnum.fiber_plus", "Fiber Plus")
	;

	private int sortIndex;
	private int id;
	private String key;
	private String text;

	private AccessTypeEnum(int sortIndex, int id, String key, String text) {
		this.sortIndex 	= sortIndex;
		this.id 		= id;
		this.key 		= key;
		this.text = text;
	}
	
	public int getSortIndex() {
		return sortIndex;
	}
	
	public String getKey() {
		return key;
	}

	public int getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public static AccessTypeEnum getByKey(String key) {
		for (AccessTypeEnum value : values()) {
			if (value.key.equals(key)) {
				return value;
			}
		}
		return null;
	}

	public static AccessTypeEnum getById(int id) {
		for (AccessTypeEnum value : values()) {
			if (value.id == id) {
				return value;
			}
		}
		return null;
	}

	public static List<AccessTypeEnum> valuesAsList() {
		List<AccessTypeEnum> list = new ArrayList<>();
		for (AccessTypeEnum value : values()) {
			list.add(value);
		}
		Collections.sort(list, new Comparator<AccessTypeEnum>() {
			@Override
			public int compare(AccessTypeEnum o1, AccessTypeEnum o2) {
				return Integer.valueOf(o1.sortIndex).compareTo(o2.sortIndex);
			}
		});
		return list;
	}
	
	// --------------------------------
	
	public String toString() {
		return WicketUtils.getLocalized(key, key);
	}

	public static ChoiceProvider getChoiceProvider() {
		return new TextChoiceProvider<AccessTypeEnum>() {
			@Override
			protected String getDisplayText(AccessTypeEnum choice) {
				return toString(choice);
			}

			@Override
			protected Object getId(AccessTypeEnum choice) {
				return choice.getKey();
			}

			@Override
			public void query(String term, int page, Response<AccessTypeEnum> response) {
				if (!StringUtils.isEmpty(term)) {
					List<AccessTypeEnum> pageOfInvoicingTypeEnums = new ArrayList<>();
					term = term.toUpperCase();
					for (AccessTypeEnum type : valuesAsList()) {
						if (type.toString().toUpperCase().contains(term)) {
							pageOfInvoicingTypeEnums.add(type);
						}
					}
					response.addAll(pageOfInvoicingTypeEnums);
				} else {
					response.addAll(valuesAsList());
				}
				response.setHasMore(false);
			}

			@Override
			public Collection<AccessTypeEnum> toChoices(Collection<String> ids) {
				ArrayList<AccessTypeEnum> result = new ArrayList<AccessTypeEnum>();
				for (String id : ids) {
					result.add(getByKey(String.valueOf(id)));
				}
				return result;
			}
			
			private String toString(AccessTypeEnum type) {
				return type.toString();
			}
		};
	}
} 