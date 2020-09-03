package dk.jyskit.waf.wicket.utils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.wicket.ajax.AjaxRequestTarget;

import dk.jyskit.waf.application.utils.exceptions.SystemException;

/**
 * Poor mans function pointer to an ajax method.
 * <pre><code>
 *  void someMethod {
 *     ...
 *     final AjaxCall actionMethod = AjaxCall.create(LessonSharedPanel.this, "removeSharing")
 *     ...
 *     .. in an ajax component  {
 *      	public void onClick(AjaxRequestTarget target) {
 *					ajaxMethod.invoke(target);
 *        }
 *     ...   
 *     }
 *  
 *   public void removeSharing(AjaxRequestTarget target) {
 *     ... do something ...
 *     target.add(someComponent);
 *   }  
 * </code></pre>
 * 
 * @author Palfred	
 */
public class AjaxCall implements Serializable, IAjaxCall {
	private Object obj;
	private String methodName;

	public static IAjaxCall create(Object obj,  String methodName) {
		return new AjaxCall(obj, methodName);
	}

	private Method getMethod() throws NoSuchMethodException, SecurityException {
		return obj.getClass().getMethod(methodName, AjaxRequestTarget.class);
	}

	public AjaxCall(Object obj, String methodName) {
		this.obj = obj;
		this.methodName = methodName;
		try {
			// call to get system exception as early as possible
			getMethod();
		} catch (NoSuchMethodException | SecurityException e) {
			throw new SystemException(e);
		}
	}

	/* (non-Javadoc)
	 * @see dk.jyskit.wicket.utils.IAjaxCall#invoke(org.apache.wicket.ajax.AjaxRequestTarget)
	 */
	@Override
	public void invoke(AjaxRequestTarget target) {
		try {
			getMethod().invoke(obj, target);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new SystemException(e);
		}
	}
}
