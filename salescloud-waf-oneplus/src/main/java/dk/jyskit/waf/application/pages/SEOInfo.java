package dk.jyskit.waf.application.pages;

import lombok.Data;

@Data
public class SEOInfo {
	private String title;
	private String description;
	private String keywords;
	
	public SEOInfo(String title, String description, String keywords) {
		this.title 	 	 = title;
		this.description = description;
		this.keywords 	 = keywords;
	}
}
