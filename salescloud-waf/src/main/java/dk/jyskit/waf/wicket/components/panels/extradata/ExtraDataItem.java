package dk.jyskit.waf.wicket.components.panels.extradata;

import java.io.Serializable;

import dk.jyskit.waf.application.model.BaseEntity;
import dk.jyskit.waf.application.model.extradata.ExtraData;
import dk.jyskit.waf.application.model.extradata.ExtraDataDefinition;

@lombok.Data
public class ExtraDataItem implements Serializable {
	private ExtraData data;
	private ExtraDataDefinition definition;

	public ExtraDataItem(BaseEntity entity, ExtraDataDefinition definition) {
		this(createDataForDefinition(entity, definition), definition);
	}

	public ExtraDataItem(ExtraData data) {
		this(data,null);
	}

	public ExtraDataItem(ExtraData data, ExtraDataDefinition definition) {
		super();
		this.data = data;
		this.definition = definition;
	}

	private static ExtraData createDataForDefinition(BaseEntity entity, ExtraDataDefinition dataDef) {
		return new ExtraData(entity, dataDef.getName(), dataDef.getDefaultValue());
	}


}
