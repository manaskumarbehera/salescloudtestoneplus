package dk.jyskit.waf.components.jquery.kendo;

import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import com.google.common.collect.Lists;

public class KendoJsReference  extends JavaScriptResourceReference {
  private static final long serialVersionUID = 1L;

  /**
   * Singleton instance of this reference
   */
  private static final KendoJsReference INSTANCE = new KendoJsReference();


  /**
   * @return the single instance of the resource reference
   */
  public static KendoJsReference instance() {
      return INSTANCE;
  }


  /**
   * Private constructor.
   */
  private KendoJsReference() {
      super(KendoJsReference.class, "js/kendo.web.js");
  }

  @Override
  public Iterable<? extends HeaderItem> getDependencies() {
      final List<HeaderItem> dependencies = Lists.newArrayList(super.getDependencies());
      dependencies.add(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));

      return dependencies;
  }
}