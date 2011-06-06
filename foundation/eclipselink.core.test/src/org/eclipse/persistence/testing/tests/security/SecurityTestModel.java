/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.security;

import org.eclipse.persistence.testing.tests.validation.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Test model for Toplink's security classes JCEEncryptor and OldEncryptor. 
 * 
 * @author Guy Pelletier
 */
public class SecurityTestModel extends TestModel {
    public SecurityTestModel() {
        setDescription("This model tests the security features of TopLink.");
    }

    public void addRequiredSystems() {
        // none at this time 
    }

    public void addTests() {
    addTest(getJCETestSuite());
    addTest(getValidationSecurityTestSuite());
    addTest(new DatabaseLoginWithNoEncryptorTest());
  }

  public static TestSuite getJCETestSuite() {
    TestSuite suite = new TestSuite();
    suite.setName("JCEEncryptor");
    suite.setDescription("This suite tests the TopLink password encryption schemes");
    suite.addTest(new JCEEncryptionTest());
    return suite;
    }

    public static TestSuite getValidationSecurityTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Validation Security Tests");
        suite.setDescription("This suite includes Validation tests on security (Tests originally in validation model)");

        suite.addTest(new SecurityWhileInitializingInstantiationPolicyTest());//shannon added
        suite.addTest(new SecurityOnFindMethodTest());//ian added
        suite.addTest(new SecurityOnInitializingAttributeMethodTest());//ian added
        suite.addTest(new SecurityWhileConvertingToMethodTest());//ian added
        suite.addTest(new SecurityWhileInitializingAttributesInInstanceVariableAccessorTest());//ian added
        suite.addTest(new SecurityWhileInitializingAttributesInMethodAccessorTest());//ian added
        suite.addTest(new SecurityWhileInitializingClassExtractionMethodTest());//ian added
        suite.addTest(new SecurityWhileInitializingCopyPolicyTest());//ian added

        return suite;
    }
}
