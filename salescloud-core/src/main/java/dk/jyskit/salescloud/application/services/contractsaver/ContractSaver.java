package dk.jyskit.salescloud.application.services.contractsaver;

import dk.jyskit.salescloud.application.model.Contract;

import java.io.Serializable;

public interface ContractSaver extends Serializable {
	void save(Contract contract);
}
