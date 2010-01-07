/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.oxm.converter.ConverterTestSuite;
import org.eclipse.persistence.testing.oxm.descriptor.rootelement.RootElementTestSuite;
import org.eclipse.persistence.testing.oxm.documentpreservation.DocumentPreservationTestSuite;
import org.eclipse.persistence.testing.oxm.inheritance.InheritanceTestSuite;
import org.eclipse.persistence.testing.oxm.readonly.ReadOnlyTestSuite;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.*;
import org.eclipse.persistence.testing.oxm.mappings.DeploymentXMLMappingTestSuite;

public class DeploymentXMLOXTestSuite extends TestCase 
{

	public DeploymentXMLOXTestSuite(String name){
	    super(name);
	}
	
  public static Test suite(){
    TestSuite suite = new TestSuite("Deployment XML OX Test Suite");
    
    suite.addTest(RootElementTestSuite.suite());        
    suite.addTest(InheritanceTestSuite.suite()); 
		suite.addTest(ConverterTestSuite.suite()); 
		suite.addTest(DocumentPreservationTestSuite.suite()); 
		suite.addTest(ReadOnlyTestSuite.suite());  
		suite.addTestSuite(XMLMarshalTestCases.class);  
		suite.addTestSuite(XMLUnmarshalTestCases.class); 
    				
    return suite;
  }
}

