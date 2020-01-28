package dk.jyskit.waf.application.services.sendmail;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailResult {
	private List<NameAndEmail> goodRecipients = new ArrayList<>();
	private List<NameAndEmail> badRecipients = new ArrayList<>();
}
