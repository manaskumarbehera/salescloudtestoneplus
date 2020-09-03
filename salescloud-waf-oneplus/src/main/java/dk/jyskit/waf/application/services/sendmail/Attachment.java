package dk.jyskit.waf.application.services.sendmail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Attachment {
	@NonNull
	private byte[] data;
	
	@NonNull
	private String fileName;
	
	@NonNull
	private String mimeType;
}
