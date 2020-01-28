package dk.jyskit.waf.wicket.components.forms.jsr303form.components.compositefielddemo;

import java.io.Serializable;

public class Composite implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String email;
	private final String name;

	public Composite(String email, String name) {
				this.email = email;
				this.name = name;
}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}
}