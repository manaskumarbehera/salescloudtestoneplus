package dk.jyskit.salescloud.application.pages;

import org.junit.Test;

import dk.jyskit.salescloud.application.pages.sales.masterdata.MasterDataPage;

public class ConfiguratorPageTest extends BasePageTest {
	@Test
	public void testStartPage() {
		tester.startPage(MasterDataPage.class);
	} 
}  