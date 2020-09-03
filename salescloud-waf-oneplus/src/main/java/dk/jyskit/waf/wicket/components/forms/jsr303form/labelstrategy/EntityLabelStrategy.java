package dk.jyskit.waf.wicket.components.forms.jsr303form.labelstrategy;

import org.apache.wicket.model.IModel;

/**
 * Tries to lookup "[entityType].[propKey]" then "{propKey]" and finally default
 * "[propKey]" if none of the properties is found in localization. As an example
 * we have an entity Person with a property "name". Then this property can be
 * labelled by
 * <ul>
 * <li>name=Navn</li>
 * </ul>
 * This can be share for all properties "name" regardless of entity type, but we
 * we need to customize only for Person then we can do:
 * <ul>
 * <li>name=Navn</li>
 * <li>Person.name=Personnavn</li>
 * </ul>
 *
 * @author m43634
 *
 */
public class EntityLabelStrategy implements ILabelStrategy {
	public EntityLabelStrategy(String entityType) {
		super();
		this.entityType = entityType;
	}

	private final String entityType;

	@Override
	public IModel<String> fieldLabel(String property) {
		return createModel(property);
	}

	protected IModel<String> createModel(final String property) {
		return new LabelModel(entityType, property);
	}

	@Override
	public IModel<String> groupLabel(String groupKey) {
		return createModel(groupKey);
	}

	@Override
	public IModel<String> columnLabel(String property) {
		return createModel(property);
	}

	@Override
	public IModel<String> buttonLabel(String labelKey) {
		return createModel(labelKey);
	}

	@Override
	public IModel<String> linkLabel(String labelKey) {
		return createModel(labelKey);
	}

}
