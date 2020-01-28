package dk.jyskit.salescloud.application.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CountAndPrice implements Serializable {
	private Integer count;
	private Integer price;
}
