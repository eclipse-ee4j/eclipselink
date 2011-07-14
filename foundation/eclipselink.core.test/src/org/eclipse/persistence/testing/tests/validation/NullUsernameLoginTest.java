package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.testing.framework.TestCase;

/**
 * Test that invoking DatasourceLogin.setUserName(null) removes the 'user' property from
 * the DatasourceLogin's internal properties list, and DatasourceLogin.getUserName() 
 * returns null.
 * 
 * EclipseLink Bug 351374
 */
public class NullUsernameLoginTest extends TestCase {

    protected DatasourceLogin login;
   
    public void test() {
        login = getSession().getLogin().clone();
        login.setUserName(null);
    }
    
    public void verify() {
        assertNull("Login should return null for 'user'", login.getUserName());
        assertFalse("Login properties should not contain 'user'", login.getProperties().contains("user"));
    }
    
}
