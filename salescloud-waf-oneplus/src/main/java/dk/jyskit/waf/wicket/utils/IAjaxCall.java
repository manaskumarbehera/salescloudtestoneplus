package dk.jyskit.waf.wicket.utils;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;

public interface IAjaxCall extends Serializable {

	public abstract void invoke(AjaxRequestTarget target);

}