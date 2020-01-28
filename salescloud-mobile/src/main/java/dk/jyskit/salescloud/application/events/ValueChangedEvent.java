package dk.jyskit.salescloud.application.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ValueChangedEvent implements Serializable {
	private AjaxRequestTarget target;
	private Long entityId;
	private String productId;
	private Object oldValue;
	private Object newValue;
}
