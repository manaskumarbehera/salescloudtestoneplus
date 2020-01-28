package dk.jyskit.salescloud.application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.StringUtils;

import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

import dk.jyskit.waf.wicket.utils.WicketUtils;

/**
 * Changing sortIndex is OK. Changing names (uppercase letters) is NOT OK (without modifying the database). 
 * 
 * @author jan
 */
@Embeddable
public enum InvoicingTypeEnum implements Serializable {
	PDF			(1,  "InvoicingTypeEnum.pdf")
	,EAN 		(2,  "InvoicingTypeEnum.ean")
	,EDI_FACT 	(3,  "InvoicingTypeEnum.edi_fact")
	,EDI_LIGHT	(4,  "InvoicingTypeEnum.edi_light")
	,OIO 		(5,  "InvoicingTypeEnum.oio")
	,BS 		(6,  "InvoicingTypeEnum.bs")
	;
	
	private int sortIndex;
	private String key;

	private InvoicingTypeEnum(int sortIndex, String key) {
		this.sortIndex 	= sortIndex;
		this.key 		= key;
	}
	
	public int getSortIndex() {
		return sortIndex;
	}
	
	public String getKey() {
		return key;
	}
	
	public static InvoicingTypeEnum getByKey(String key) {
		for (InvoicingTypeEnum value : values()) {
			if (value.key.equals(key)) {
				return value;
			}
		}
		return null;
	}
	
	public static List<InvoicingTypeEnum> valuesAsList() {
		List<InvoicingTypeEnum> list = new ArrayList<>();
		for (InvoicingTypeEnum value : values()) {
			list.add(value);
		}
		Collections.sort(list, new Comparator<InvoicingTypeEnum>() {
			@Override
			public int compare(InvoicingTypeEnum o1, InvoicingTypeEnum o2) {
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
		return new TextChoiceProvider<InvoicingTypeEnum>() {
			@Override
			protected String getDisplayText(InvoicingTypeEnum choice) {
				return toString(choice);
			}

			@Override
			protected Object getId(InvoicingTypeEnum choice) {
				return choice.getKey();
			}

			@Override
			public void query(String term, int page, Response<InvoicingTypeEnum> response) {
				if (!StringUtils.isEmpty(term)) {
					List<InvoicingTypeEnum> pageOfInvoicingTypeEnums = new ArrayList<>();
					term = term.toUpperCase();
					for (InvoicingTypeEnum type : valuesAsList()) {
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
			public Collection<InvoicingTypeEnum> toChoices(Collection<String> ids) {
				ArrayList<InvoicingTypeEnum> result = new ArrayList<InvoicingTypeEnum>();
				for (String id : ids) {
					result.add(getByKey(String.valueOf(id)));
				}
				return result;
			}
			
			private String toString(InvoicingTypeEnum type) {
				return type.toString();
			}
		};
	}
} 