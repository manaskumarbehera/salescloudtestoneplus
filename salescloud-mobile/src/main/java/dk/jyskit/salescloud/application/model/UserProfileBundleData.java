package dk.jyskit.salescloud.application.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserProfileBundleData implements Serializable {
	@SerializedName("a") private Long bundleEntityId;
}
