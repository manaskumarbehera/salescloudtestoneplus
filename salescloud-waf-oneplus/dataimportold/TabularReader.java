package dk.jyskit.waf.utils.dataimport;

import java.io.IOException;

public abstract class TabularReader {
	private int currentRowNumber = 0;

	public abstract String[] readNext() throws IOException;

	protected int getCurrentRowNumber() {
		return currentRowNumber;
	}

	protected void increaseRowNumber() {
		currentRowNumber++;
	}
}
