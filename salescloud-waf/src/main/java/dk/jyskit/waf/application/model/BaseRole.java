package dk.jyskit.waf.application.model;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table (name="baserole")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper=true, of={})
@ToString(of={"roleName"})
@NoArgsConstructor
@RequiredArgsConstructor
public class BaseRole extends BaseEntity {
	public final static FontAwesomeIconType ICON = FontAwesomeIconType.group;
	
	@Nonnull
	protected String roleName;
	
	@Nonnull
	@ManyToOne(optional = false)
	@Valid
	protected BaseUser user;
}
