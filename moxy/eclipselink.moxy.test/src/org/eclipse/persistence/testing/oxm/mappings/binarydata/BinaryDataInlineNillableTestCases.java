/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith -February 2010 - 2.1 
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.binarydata;

import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class BinaryDataInlineNillableTestCases extends XMLWithJSONMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataInlineNillable.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataInlineNillableWrite.xml";    
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/binarydata/BinaryDataInlineNillable.json";    

	public BinaryDataInlineNillableTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setWriteControlDocument(XML_WRITE_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		BinaryDataInlineProject p =new BinaryDataInlineProject();
		((XMLBinaryDataMapping)p.getClassDescriptor(Employee.class).getMappingForAttributeName("photo")).getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
		((XMLBinaryDataMapping)p.getClassDescriptor(Employee.class).getMappingForAttributeName("photo")).getNullPolicy().setNullRepresentedByXsiNil(true);
        setProject(p);
	}
	
    protected Object getControlObject() {
        Employee emp = new Employee(123);
        emp.setPhoto(null);
        //emp.setPhoto(new byte[]{0,1,2,3});
        emp.setExtraPhoto(new byte[]{0,1,2,3});
        return emp;
    }

}

