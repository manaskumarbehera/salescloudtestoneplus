package dk.jyskit.salescloud.application.model;

import java.util.ArrayList;
import java.util.List;

import dk.jyskit.salescloud.application.MobileSession;

public enum MobileContractMode {
	RENEGOTIATION 			(2, "Genforhandling", false, true)
	,NEW_SALE 				(0, "Nysalg", true, false)  // mode_nysalg
	,VOICE_TO_SWITCHBOARD 	(1, "Mobilpakker -> Omstilling", true, true)
	// One+
//	,ADD_TO_SOLUTION		(3, "Tilkøb", true, false)    // Grundprodukt (One+ løsnin, Løsning med puljebrugere, Løsning med enkeltbrugere) udløses ikke
	,CONVERSION			 	(4, "Konvertering", true, true)   // TODO: Som nysalg, men nuller oprettelser og installation + evt. andet installationsprodukt
	,CONVERSION_1_TO_1	 	(5, "Konvertering (1 til 1)", false, true)	// TODO
//	,EXPANSION			 	(6, "Udvidelse", false, true)			// TODO
	;

	public boolean isNewAccount() {
		return newAccount;
	}

	public boolean isExistingAccount() {
		return existingAccount;
	}

	//	ONE_Nysalg(indtastning:kun "nye", CDM output: alle brugere i en lang liste)
//	ONE_Tilkøb(indtastning:kun "nye", CDM output: alle brugere i en lang liste)
//	ONE_Konvertering 1:1 (indtastning:kun "gamle", CDM output: alle brugere i en lang liste)
//	ONE_Konvertering med tilkøb (indtastning:kun "nye/gamle", CDM output: 2 lister ordrelinier)
////	ONE_Genforhandling 1:1 (indtastning:kun "gamle", CDM output: alle brugere i en lang liste (gamle))
////	ONE_Genforhandling med tilkøb indtastning:kun "nye/gamle", CDM output: 2 lister ordrelinier)


	private int id;
	private String text;
	private boolean newAccount;
	private boolean existingAccount;

	public String getLabelForNewEditor() {
		return "Nysalg";
	}

	public String getLabelForExistingEditor() {
		return "TODO";
	}

	private MobileContractMode(int id, String text, boolean newAccount, boolean existingAccount) {
		this.id 	= id;
		this.text	= text;
		this.newAccount = newAccount;
		this.existingAccount = existingAccount;
	}
	
	public int getId() {
		return id;
	}
	
	public String getText() {
		MobileContract contract = MobileSession.get().getContract();
		if (contract.getBusinessArea().getBusinessAreaId() == BusinessAreas.TDC_OFFICE) {
			if (this.equals(RENEGOTIATION)) {
				return "Tilkøb";
			}
		}
		return text;
	}
	
	public static List<MobileContractMode> valuesAsList() {
		List<MobileContractMode> list = new ArrayList<MobileContractMode>(values().length);
		for (MobileContractMode numberTransferType : values()) {
			list.add(numberTransferType);
		}
		return list;
	}
}
