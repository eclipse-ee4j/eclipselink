/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.sessionsxml;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.TestProblemException;


public class SessionsXMLTestModel extends TestModel {
    public SessionsXMLTestModel() {
        super();
        setDescription("This model tests the Sessions XML feature.");
    }

    public SessionsXMLTestModel(boolean isSRG) {
        this();
        this.isSRG = isSRG;
    }

    public void addTests() {
        addTest(getSessionsXMLLoadingTestSuite());
        addTest(getSessionsXMLRCMTestSuite());
        addTest(getSessionManagerTestSuite());
        addTest(getSessionXMLSchemaTestSuite());
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.

    public void addSRGTests() {
        addTest(getSRGSessionXMLSchemaTestSuite());
    }

    public TestSuite getSessionsXMLLoadingTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Sessions.xml standard loading tests");
        // Added test for bug 2700794 
        suite.addTest(new JavaLogSessionsXMLTest());
        return suite;
    }

    public TestSuite getSessionXMLSchemaTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("XML Schema tests");
        suite.addTest(new SessionsXMLSchemaTest());
        addSessionsXMLSchemaWriteTest(suite);
        suite.addTest(new SessionsXMLSchemaNoLoggingTest());
        suite.addTest(new SessionsXMLSchemaDefaultValueTest());
        suite.addTest(new SessionsXMLServerSessionSchemaTest());
        suite.addTest(new SessionsXMLSessionEventListenerTest());
        if (!getExecutor().isServer) {
            suite.addTest(new SessionsXMLSchemaReloadTest());
        }
        suite.addTest(new SessionsXMLSchemaSunCORBATransportConfigTest());
        suite.addTest(new SessionsXMLSchemaJavaLogTest());
        suite.addTest(new SessionsXMLSchemaSequencingTest());
        suite.addTest(new SessionsXMLSchemaInvalidTagTest());
        suite.addTest(new SessionsXMLSchemaMisplacedDependentTagTest());
        suite.addTest(new SessionsXMLSchemaIncorrectTagValuesTest());
        suite.addTest(new SessionsXMLSchemaProjectXMLTest());
        suite.addTest(new SessionsXMLSchemaWebsphere61PlatformTest());
        suite.addTest(new SessionsXMLSchemaWeblogicPlatformTest());
        suite.addTest(new SessionsXMLSchemaLoggingOptionsTest());
        suite.addTest(new SessionsXMLSchemaJBossPlatformTest());
        suite.addTest(new SessionManagerGetMultipleSessionsTest());
        suite.addTest(new FailoverLoginSettingsTest());

        return suite;
    }

    //SRG test set is maintained by QA only, do NOT add any new tests into it.

    public TestSuite getSRGSessionXMLSchemaTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("XML Schema tests");
        suite.addTest(new SessionsXMLSchemaTest());
        addSessionsXMLSchemaWriteTest(suite);
        return suite;
    }

    public TestSuite getSessionsXMLRCMTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("sessions.xml with RCM loading tests");
        suite.addTest(new RcmWithRmiAndJndiTest());
        suite.addTest(new RcmWithRmiAndRmiRegistryTest());
        suite.addTest(new RcmWithTransportClassTest());
        suite.addTest(new RcmEncryptedPasswordTest());
        suite.addTest(new RCMWithJmsTopicTest());
        return suite;
    }

    public TestSuite getSessionManagerTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("SessionManager tests");

        suite.addTest(new SessionManagerGetSessionStringStringTest());
        suite.addTest(new SessionManagerGetSessionNotFoundTest());
        suite.addTest(new MWConfigModelEncryptionTest());
        return suite;
    }
    
    public void addSessionsXMLSchemaWriteTest(TestSuite suite){
    
        try{
            Class testCaseClass = Class.forName("org.eclipse.persistence.testing.tests.sessionsxml.SessionsXMLSchemaWriteTest");
            junit.framework.Test testCase = (junit.framework.Test)testCaseClass.newInstance();
            suite.addTest(testCase);
        } catch (Exception e){
            getSession().logMessage("Unable to load SessionsXMLSchemaWriteTest.  This usually occurs when the tests were compiled " +
                    " on a non-Oracle environment. If you are not running on Oracle, this is not a problem.");
            if (getSession().getPlatform().isOracle()){
                throw new TestProblemException("Could not load: SessionsXMLSchemaWriteTest", e);
            }
        }
    }
}
