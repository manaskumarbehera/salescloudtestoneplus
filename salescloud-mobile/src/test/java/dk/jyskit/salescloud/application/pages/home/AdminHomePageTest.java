package dk.jyskit.salescloud.application.pages.home;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import dk.jyskit.salescloud.application.pages.BasePageTest;
import dk.jyskit.salescloud.application.pages.auth.LoginPage;

public class AdminHomePageTest extends BasePageTest {
	@Test
	public void testGoodLogin() {
		tester.startPage(AdminHomePage.class);
		
		//assert rendered page class
	    tester.assertRenderedPage(LoginPage.class);
	    
	    FormTester formTester = tester.newFormTester("login:loginForm");
        
        formTester.setValue("username", "testadmin");
        formTester.setValue("password", "pw");

        formTester.submit();
        
		// tester.assertErrorMessages(new String[] { "Field 'username' is required." });
       
        tester.assertRenderedPage(AdminHomePage.class);   
	} 
}  