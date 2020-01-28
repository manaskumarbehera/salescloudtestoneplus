package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@Embeddable
public class ProductionItem implements Serializable {
	@NonNull
	private String text;
	
	@NonNull
	private String productionId;
}
