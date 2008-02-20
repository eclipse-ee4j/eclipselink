/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.eis;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.eis.cobol.CobolTestModel;
import org.eclipse.persistence.testing.tests.eis.xmlfile.XMLFileTestModel;

/**
 * Test TopLink EIS.
 * Note that some J2C driver require a J2EE server to run so,
 * some of the EIS tests are in this core test model,
 * and the rest are in the server test models.
 */
public class EISTestModel extends TestModel {
    public EISTestModel() {
        super();
        setDescription("This model tests EIS");
    }

    public void addTests() {
        addTest(getEISTestSuite());
    }

    public TestSuite getEISTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("EIS tests");
	/*AttunityConnect License expired
        suite.addTest(new AttunityTestModel());
        suite.addTest(new AttunityXMLTestModel());
        suite.addTest(new AttunityXMLTestModelXML());
        //addTest(new AttunityXMLTestModelClassGen()); //This model meant to test feature that is not currently supported	
        suite.addTest(new AttunitySessionBrokerTestModel());
	*/
        try{
	        Class aqTestModelClass = Class.forName("org.eclipse.persistence.testing.tests.eis.aq.AQTestModel");
	        TestModel aqTestModel = (TestModel)aqTestModelClass.newInstance(); 
	        suite.addTest(aqTestModel);
        } catch (Exception e){
        	getSession().logMessage("Warning: Unable to instantiate AQTestModel. Usually this means you did not " +
        			" build the Oracle-specific test classes.  If you are not running on Oracle, this is not a problem.");
        	if (getSession().getPlatform().isOracle()){
        		throw new TestProblemException("Unable to instantiate Oracle AQ tests.", e);
        	}
        	
        }
        suite.addTest(new CobolTestModel());
        suite.addTest(new XMLFileTestModel());
      //      suite.addTest(new MQTestModel());
        return suite;
    }

}
