package dk.jyskit.waf.application.services.sendmail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class NameAndEmail {
	private String name;
	
	@NonNull
	private String email;
}
