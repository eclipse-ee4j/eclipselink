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
package org.eclipse.persistence.testing.oxm.mappings.transformation;

import java.io.FileOutputStream;
import java.net.URL;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.ObjectCopyingPolicy;
import org.xml.sax.helpers.DefaultHandler;
//import org.custommonkey.xmlunit.XMLTestCase;
import java.io.*;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import java.util.Calendar;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TransformationMappingTestCases extends XMLMappingTestCases 
{
  public TransformationMappingTestCases(String name) throws Exception {
	super(name);
	setProject(new TransformationMappingTestProject());
	setControlDocument("org/eclipse/persistence/testing/oxm/mappings/transformation/employee1.xml");
  }

  public Object getControlObject()
  {
    Employee emp = new Employee();
	emp.setName("John Smith");
    String[] hours = new String[2];
    hours[0] = "9:00AM";
    hours[1] = "5:00PM";
    emp.setNormalHours(hours);
    
    return emp;
  }
	
  public void testCopyObject() throws Exception 
  {
	AbstractSession session = (AbstractSession)super.xmlContext.getSession(0);
	ClassDescriptor descriptor = session.getDescriptor(Employee.class);
	ObjectCopyingPolicy policy = new ObjectCopyingPolicy();
	policy.setSession(session);
	Employee emp1 = (Employee)getControlObject();
	Employee emp2 = (Employee)descriptor.getObjectBuilder().copyObject(emp1, policy);
	boolean equal = descriptor.getObjectBuilder().compareObjects(emp1, emp2, session);
	assertTrue("The copy of the employee doesn't match the original", equal);
  }
}


