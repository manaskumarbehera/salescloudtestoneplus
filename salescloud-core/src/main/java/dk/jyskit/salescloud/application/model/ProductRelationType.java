package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRelationType implements Serializable {
	private Long id;
	private String displayText;
	private boolean firstProductIsSpecial;
}
