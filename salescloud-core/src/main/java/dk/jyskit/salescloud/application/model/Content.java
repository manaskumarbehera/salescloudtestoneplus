package dk.jyskit.salescloud.application.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dk.jyskit.waf.application.model.BaseEntity;

@Entity(name="Content")
@Table(name = "content")
@Data
@EqualsAndHashCode(callSuper=true, of={})
@RequiredArgsConstructor
@NoArgsConstructor
public class Content extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private ContentCategory category;
	
	@NonNull
	private String html;
}
