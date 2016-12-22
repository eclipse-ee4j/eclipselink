/*******************************************************************************
 * Copyright (c) 1998, 2016 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.security.SecurityOnInitializingAttributeMethodTest.AttributeMethodAbstractSession;
import org.eclipse.persistence.testing.tests.security.SecurityOnInitializingAttributeMethodTest.AttributeMethodOneArg;
import org.eclipse.persistence.testing.tests.security.SecurityOnInitializingAttributeMethodTest.AttributeMethodSession;
import org.eclipse.persistence.testing.tests.security.SecurityWhileConvertingToMethodTest.ConvertMethodAbstractSession;
import org.eclipse.persistence.testing.tests.security.SecurityWhileConvertingToMethodTest.ConvertMethodNoArg;
import org.eclipse.persistence.testing.tests.security.SecurityWhileConvertingToMethodTest.ConvertMethodSession;
import org.eclipse.persistence.testing.tests.security.SecurityWhileInitializingClassExtractionMethodTest.ExtractionAbstractRecord;
import org.eclipse.persistence.testing.tests.security.SecurityWhileInitializingClassExtractionMethodTest.ExtractionRecord;
import org.eclipse.persistence.testing.tests.security.SecurityWhileInitializingCopyPolicyTest.Policy;
import org.eclipse.persistence.testing.tests.security.SecurityWhileInitializingCopyPolicyTest.WorkingPolicy;

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
    addTest(getSecurableBackwardsCompatibilityTestSuite());
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
        suite.addTest(new SecurityOnInitializingAttributeMethodTest(AttributeMethodOneArg.class));//ian added
        suite.addTest(new SecurityOnInitializingAttributeMethodTest(AttributeMethodAbstractSession.class));//ian added
        suite.addTest(new SecurityOnInitializingAttributeMethodTest(AttributeMethodSession.class));//ian added
        suite.addTest(new SecurityWhileConvertingToMethodTest(ConvertMethodNoArg.class));//ian added
        suite.addTest(new SecurityWhileConvertingToMethodTest(ConvertMethodSession.class));//ian added
        suite.addTest(new SecurityWhileConvertingToMethodTest(ConvertMethodAbstractSession.class));//ian added
        suite.addTest(new SecurityWhileInitializingAttributesInInstanceVariableAccessorTest());//ian added
        suite.addTest(new SecurityWhileInitializingAttributesInMethodAccessorTest());//ian added
        suite.addTest(new SecurityWhileInitializingClassExtractionMethodTest(ExtractionAbstractRecord.class));//ian added
        suite.addTest(new SecurityWhileInitializingClassExtractionMethodTest(ExtractionRecord.class));//ian added
        suite.addTest(new SecurityWhileInitializingCopyPolicyTest(Policy.class));//ian added
        suite.addTest(new SecurityWhileInitializingCopyPolicyTest(WorkingPolicy.class));

        return suite;
    }
    
    public static TestSuite getSecurableBackwardsCompatibilityTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Securable Backward Compatibility Tests");
        suite.setDescription("Tests the backwards compatibility of the (default) Securable reference implementation");
        
        suite.addTest(new SecurableBackwardsCompatibilityTest("testStringDecryption_PlainText"));
        suite.addTest(new SecurableBackwardsCompatibilityTest("testStringDecryption_DES_ECB"));
        suite.addTest(new SecurableBackwardsCompatibilityTest("testStringDecryption_AES_CBC"));
        suite.addTest(new SecurableBackwardsCompatibilityTest("testStringDecryption_AES_ECB"));
        suite.addTest(new SecurableBackwardsCompatibilityTest("testNullParameterDecryption"));
        suite.addTest(new SecurableBackwardsCompatibilityTest("testNullParameterEncryption"));
        
        return suite;
    }

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     * Unfortunately JUnit only allows suite methods to be static,
     * so it is not possible to generically do this.
     */
    public static junit.framework.TestSuite suite() {
        return new SecurityTestModel();
    }
}
