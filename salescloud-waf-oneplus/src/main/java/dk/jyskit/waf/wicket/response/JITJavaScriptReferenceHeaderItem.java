package dk.jyskit.waf.wicket.response;

import org.apache.wicket.markup.head.IReferenceHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;

public class JITJavaScriptReferenceHeaderItem extends JavaScriptReferenceHeaderItem
	implements
		IReferenceHeaderItem
{
	private boolean inHead;
	
	/**
	 * Creates a new {@code JITJavaScriptReferenceHeaderItem}.
	 * 
	 * @param reference
	 *            resource reference pointing to the javascript resource
	 * @param pageParameters
	 *            the parameters for this Javascript resource reference
	 * @param id
	 *            id that will be used to filter duplicate reference (it's still filtered by URL
	 *            too)
	 * @param defer
	 *            specifies that the execution of a script should be deferred (delayed) until after
	 *            the page has been loaded.
	 * @param charset
	 *            a non null value specifies the charset attribute of the script tag
	 * @param condition
	 *            the condition to use for Internet Explorer conditional comments. E.g. "IE 7".
	 * @param inHead
	 *            place javascript in head (as opposed to body)
	 */
	public JITJavaScriptReferenceHeaderItem(ResourceReference reference,
		PageParameters pageParameters, String id, boolean defer, String charset, String condition, boolean inHead)
	{
		super(reference, pageParameters, id, defer, charset, condition);
		this.inHead = inHead;
	}
	
	public boolean isInHead() {
		return inHead;
	}
	
	@Override
	public String toString()
	{
		return "JITJavaScriptReferenceHeaderItem(" + getReference() + ", " + getPageParameters() + ')';
	}
}
