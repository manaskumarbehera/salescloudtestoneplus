package dk.jyskit.salescloud.application.pages.admin.campaigns;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import dk.jyskit.salescloud.application.model.MobileCampaign;

@Data
@NoArgsConstructor
public class CampaignFormBean implements Serializable {
	public final static int MAX_BUNDLE = 6;
	
	private MobileCampaign campaign;
	
	private boolean bundle_0_enabled;
	private boolean bundle_1_enabled;
	private boolean bundle_2_enabled;
	private boolean bundle_3_enabled;
	private boolean bundle_4_enabled;
	private boolean bundle_5_enabled;
	private boolean bundle_6_enabled;
	
	private long bundle_0_0, bundle_0_1, bundle_0_2; 
	private long bundle_1_0, bundle_1_1, bundle_1_2;
	private long bundle_2_0, bundle_2_1, bundle_2_2;
	private long bundle_3_0, bundle_3_1, bundle_3_2;
	private long bundle_4_0, bundle_4_1, bundle_4_2;
	private long bundle_5_0, bundle_5_1, bundle_5_2;
	private long bundle_6_0, bundle_6_1, bundle_6_2;
}
