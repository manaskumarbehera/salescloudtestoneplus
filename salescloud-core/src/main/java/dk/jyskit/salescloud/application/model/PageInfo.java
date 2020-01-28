package dk.jyskit.salescloud.application.model;

import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import dk.jyskit.waf.application.model.BaseEntity;

@Entity(name="PageInfo")
@Table(name = "pageinfo")
@Data
@EqualsAndHashCode(callSuper=true, of={"pageId"})
@RequiredArgsConstructor
@NoArgsConstructor
@ToString(of={"title"})
// PENDING DATABASE CHANGE - ADDED NEXT LINE:
@Indexes(value={@Index( name="ix_pagedId_businessArea", columnNames={"BUSINESSAREA_ID", "pageId"})})
public class PageInfo extends BaseEntity {
	@Enumerated(EnumType.STRING)
	private EntityState state;	 
	
	@NonNull
	private String pageId;
	
	@NonNull
	private String title;
	
	private String subTitle;
	
	@Column(length=10000)
	@Lob
	private String introMarkdown;

	@Column(length=15000)
	@Lob
	private String introHtml;

	@Column(length=10000)
	@Lob
	private String miscMarkdown;

	@Column(length=10000)
	@Lob
	private String miscHtml;

	@Column(length=10000)
	@Lob
	private String helpMarkdown;
  
	@Column(length=10000)
	@Lob
	private String helpHtml;
	
	@Nonnull
	@ManyToOne(optional = false)
	private BusinessArea businessArea;

	@ManyToMany
	@JoinTable(name = "page_content", joinColumns = { @JoinColumn(name = "page_id", referencedColumnName = "id") }, 
		inverseJoinColumns = { @JoinColumn(name = "content_id", referencedColumnName = "id") })
	private List<Content> contents;

	// --------------------------------
	
	public void addContent(Content content) {
		contents.add(content);
	}
}
