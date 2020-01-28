package dk.jyskit.salescloud.application.model;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class Amounts implements Serializable {
	private final static int NUM_VALUES = 3;
	
	private long[] amounts = new long[NUM_VALUES];		// real amount * 100 for each payment category
	
	public Amounts() {
		for (int i = 0; i < NUM_VALUES; i++) {
			this.amounts[i] = 0;
		}
	}
	
	public Amounts(long ... amounts) {
		this.amounts = new long[amounts.length];
		for (int i = 0; i < amounts.length; i++) {
			this.amounts[i] = amounts[i];
		}
	}

	public Amounts(Amounts a) {
		this.amounts = new long[a.amounts.length];
		for (int i = 0; i < a.amounts.length; i++) {
			this.amounts[i] = a.amounts[i];
		}
	}

	public long getOneTimeFee() {
		return sum(FeeCategory.ONETIME_FEE);
	}

	public Float getOneTimeFeeAsFloat() {
		return x100ToFloat(getOneTimeFee());
	}

	public String getOneTimeFeeFormatted() {
		return getFormattedNoDecimals(getOneTimeFee()) + " kr.";
	}

	public void setOneTimeFee(long oneTimeFee) {
		this.amounts[FeeCategory.ONETIME_FEE.getFromIndex()] = oneTimeFee;
	}

	public long getInstallationFee() {
		return sum(FeeCategory.INSTALLATION_FEE);
	}

	public Float getInstallationFeeAsFloat() {
		return x100ToFloat(getInstallationFee());
	}

	public String getInstallationFeeFormatted() {
		return getFormattedNoDecimals(getInstallationFee()) + " kr.";
	}

	public void setInstallationFee(long installationFee) {
		this.amounts[FeeCategory.INSTALLATION_FEE.getFromIndex()] = installationFee;
	}

	public long getRecurringFee() {
		return sum(FeeCategory.RECURRING_FEE);
	}

	public Float getRecurringFeeAsFloat() {
		return x100ToFloat(getRecurringFee());
	}

	public String getRecurringFeeFormatted() {
		return getFormattedNoDecimals(getRecurringFee()) + " kr.";
	}

	public void setRecurringFee(long recurringFee) {
		this.amounts[FeeCategory.RECURRING_FEE.getFromIndex()] = recurringFee;
	}
	
	public long getNonRecurringFees() {
		return sum(FeeCategory.NON_RECURRING_FEE);
	}

	public Float getNonRecurringFeeAsFloat() {
		return x100ToFloat(getNonRecurringFees());
	}

	public String getNonRecurringFeesFormatted() {
		return getFormattedNoDecimals(getNonRecurringFees()) + " kr.";
	}

	public String getNonRecurringFeesFormattedAmount() {
		return getFormattedNoDecimals(getNonRecurringFees());
	}

	public static String getFormattedNoDecimals(long amount) {
		return String.format(new Locale("da", "DK"), "%,.0f", amount / 100d);
	}

	public static String getFormattedWithDecimals(long amount) {
		return String.format(new Locale("da", "DK"), "%,.2f", amount / 100d);
	}

	public static long stringToLong(String s) {
		try {
			return Long.valueOf(s.replace(".", "").replace(",", ""));
		} catch (Exception e) {
			return 0;
		}
	}

	public long[] getAmounts() {
		return amounts;
	}

	public long[] getAmounts(FeeCategory feeCategory) {
		Amounts a = new Amounts();
		for (int i = feeCategory.getFromIndex(); i <= feeCategory.getToIndex(); i++) {
			a.amounts[i] = amounts[i];
		}
		return a.amounts;
	}

	public Amounts clone() {
		return new Amounts(amounts);
	}

	public void setAmounts(long[] amounts) {
		this.amounts = amounts;
	}

	public Amounts add(Amounts amountsToAdd) {
		for (int i = 0; i < amounts.length; i++) {
			amounts[i] += amountsToAdd.getAmounts()[i];
		}
		return this;
	}
	
	public Amounts subtract(Amounts amountsToSubtract) {
		for (int i = 0; i < amounts.length; i++) {
			amounts[i] -= amountsToSubtract.getAmounts()[i];
		}
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Amounts)) {
			return false;
		}
		Amounts other = (Amounts) obj;
		for (int i = 0; i < amounts.length; i++) {
			if (amounts[i] != other.amounts[i]) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < amounts.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(amounts[i]);
		}
		return sb.toString();
	}

	public void multiplyBy(int n) {
		for (int i = 0; i < amounts.length; i++) {
			amounts[i] = n * amounts[i];
		}
	}

	public void multiplyBy(OrderLineCount orderLineCount) {
		amounts[FeeCategory.RECURRING_FEE.getFromIndex()] 		*= orderLineCount.getCountTotal();
		amounts[FeeCategory.INSTALLATION_FEE.getFromIndex()] 	*= orderLineCount.getCountNew();
		amounts[FeeCategory.ONETIME_FEE.getFromIndex()] 		*= orderLineCount.getCountNew();
	}

	public Amounts nonNegative() {
		return floor(new Amounts());
	}

	public Amounts floor(Amounts otherAmounts) {
		Amounts result = new Amounts(amounts);
		for (int i = 0; i < NUM_VALUES; i++) {
			result.amounts[i] = Math.max(otherAmounts.amounts[i], result.amounts[i]);
		}
		return result;
	}
	
	public Amounts ceil(Amounts otherAmounts) {
		Amounts result = new Amounts(amounts);
		for (int i = 0; i < NUM_VALUES; i++) {
			result.amounts[i] = Math.min(otherAmounts.amounts[i], result.amounts[i]);
		}
		return result;
	}

	public static Amounts min(Amounts a1, Amounts a2) {
		Amounts result = new Amounts(a1);
		for (int i = 0; i < result.amounts.length; i++) {
			result.amounts[i] = Math.min(a1.amounts[i], a2.amounts[i]);
		}
		return result;
	}

	public static Amounts max(Amounts a1, Amounts a2) {
		Amounts result = new Amounts(a1);
		for (int i = 0; i < result.amounts.length; i++) {
			result.amounts[i] = Math.max(a1.amounts[i], a2.amounts[i]);
		}
		return result;
	}

	public static Float x100ToFloat(long amount) {
		float f = amount;
		return Math.round(f) / 100f;
	}

	public long sum(FeeCategory feeCategory) {
		long result = 0;
		for (int i = feeCategory.getFromIndex(); i <= feeCategory.getToIndex(); i++) {
			result += amounts[i];
		}
		return result;
	}

	public boolean isAllZero() {
		for (int i = 0; i < NUM_VALUES; i++) {
			if (this.amounts[i] != 0) {
				return false;
			}
		}
		return true;
	}
}
