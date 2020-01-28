package dk.jyskit.salescloud.application.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PartnerData implements Serializable {
	public final static int VARIANT_GENERELT 				= 0;
	public final static int VARIANT_GODT_IGANG 				= 1;
	public final static int VARIANT_INSTALLATION 			= 2;
	public final static int VARIANT_SUPPORTAFTALE 			= 3;
	public final static int VARIANT_SUPPORT_OG_RATEAFTALE	= 4;
	public final static int VARIANT_TASTEBILAG				= 5;

	public Map<String, Object> values = new HashMap<>();

	@Data
	@AllArgsConstructor
	static public class TypeCountTextAmount implements Serializable {
		String type;
		String count;
		String text;
		String amount;
		Product product;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TypeCountTextAmount) {
				TypeCountTextAmount other = (TypeCountTextAmount) obj;
				return Objects.equals(other.type, type)
						&& Objects.equals(other.count, count)
						&& Objects.equals(other.text, text)
						&& Objects.equals(other.amount, amount)
						;
			}
			return false;
		}
	}

	@Data
	@AllArgsConstructor
	static public class CountText implements Serializable {
		String count;
		String text;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CountText) {
				CountText other = (CountText) obj;
				return Objects.equals(other.count, count)
						&& Objects.equals(other.text, text)
						;
			}
			return false;
		}
	}

	@Data
	@AllArgsConstructor
	static public class HardwareInfo implements Serializable {
		String count;
		String text;
		Long kontantpris;
		Product product;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CountText) {
				CountText other = (CountText) obj;
				return Objects.equals(other.count, count)
						&& Objects.equals(other.text, text)
						;
			}
			return false;
		}
	}

	@Data
	@AllArgsConstructor
	static public class RemarkAndStars implements Serializable {
		String text;
		String stars;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof RemarkAndStars) {
				RemarkAndStars other = (RemarkAndStars) obj;
				return Objects.equals(other.text, text)
						&& Objects.equals(other.stars, stars)
						;
			}
			return false;
		}
	}
}
