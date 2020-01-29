package dk.jyskit.waf.wicket.components.widget;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;
import dk.jyskit.waf.wicket.components.panels.WrapperPanel;

/**
 * Widget component border.
 */
public class WidgetPanel extends Panel {
	/**
	 * Wrap a component in a Widget.
	 * Title Label can be configured using one of the label methods e.g. <P><code>add(WidgetPanel.wrap(somePanel).label("My Title");)</code></P>
	 * Can only be used on panel like components that only requires single tag element in the markup.
	 * @param component
	 * @return
	 */
	public static WidgetPanel wrap(Component component) {
		return new WidgetPanel(component.getId()).body(component);
	}

	public static WidgetPanel wrap(Component component, Type type) {
		return new WidgetPanel(component.getId(), type).body(component);
	}

	public static enum Type implements ICssClassNameProvider {
		Default("panel-default"), Primary("panel-primary"), Info("panel-info"), Success("panel-success"), Warning("panel-warning"), Danger("panel-danger");

		private final String cssClassName;

		private Type(String cssClassName) {
			this.cssClassName = cssClassName;
		}

		@Override
		public String cssClassName() {
			return cssClassName;
		}
	}

	private IModel<String> labelModel;
	private WebMarkupContainer container;

	public WidgetPanel(String id) {
		this(id, null, Type.Default);
	}

	public WidgetPanel(String id, Type type) {
		this(id, null, type);
	}

	public WidgetPanel(String id, IModel<?> model) {
		this(id, model, Type.Default);
	}

	public WidgetPanel(String id, IModel<?> model, Type type) {
		super(id, model);
		setOutputMarkupId(true);
		container = new TransparentWebMarkupContainer("widgetContainer");
		container.add(new CssClassNameAppender(type));
		super.add(container);
		this.labelModel = new Model<String>();
		container.add(createHead("widgetHead"));
		container.add(createBody("widgetBody"));
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(getBeforeWidgetPanel("beforeWidget"));
		add(getAfterWidgetPanel("afterWidget"));
	}

	public Component getBeforeWidgetPanel(String id) {
		return new EmptyPanel(id);
	}

	public Component getAfterWidgetPanel(String id) {
		return new EmptyPanel(id);
	}

	public WidgetPanel body(Component body) {
		container.addOrReplace(new WrapperPanel("widgetBody", body));
		return this;
	}

	public WebMarkupContainer getBodyContainer() {
		return container;
	}

	protected Component createHead(String wicketId) {
		return new WidgetHeadPanel(wicketId, new AbstractReadOnlyModel<String>() {

			@Override
			public String getObject() {
				return labelModel.getObject();
			}
		});
	}

	protected Component createBody(String wicketId) {
		return new EmptyPanel(wicketId);
	}

	public WidgetPanel label(String label) {
		this.labelModel.setObject(label);
		return this;
	}

	public WidgetPanel label(IModel<String> labelModel) {
		this.labelModel = labelModel;
		if (labelModel instanceof IComponentAssignedModel<?>) {
			((IComponentAssignedModel<?>)labelModel).wrapOnAssignment(this);
		}
		return this;
	}

	public WidgetPanel labelKey(String key) {
		return label(new StringResourceModel(key, this, getDefaultModel()));
	}
}
