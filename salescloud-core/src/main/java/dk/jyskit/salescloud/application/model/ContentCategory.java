package dk.jyskit.salescloud.application.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dk.jyskit.waf.application.model.BaseEntity;

@Entity(name="ContentCategory")
@Table(name = "contentcategory")
@Data
@EqualsAndHashCode(callSuper=true, of={})
@RequiredArgsConstructor
@NoArgsConstructor
public class ContentCategory extends BaseEntity {
	@NonNull
	private String name;
}
