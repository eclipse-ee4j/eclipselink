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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.nillable;

import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLWithJSONMappingTestCases;

public class DirectIsSetNullPolicyElementAbsentIsSetAbsentFalseWithParamsTestCases extends XMLWithJSONMappingTestCases{
	// TC  UC 
    private final static String XML_RESOURCE = //
    "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNullPolicyElementAbsentIsSetAbsentFalseWithParams.xml";
    private final static String JSON_RESOURCE = //
        "org/eclipse/persistence/testing/oxm/mappings/directtofield/nillable/DirectIsSetNullPolicyElementAbsentIsSetAbsentFalseWithParams.json";

    
    public DirectIsSetNullPolicyElementAbsentIsSetAbsentFalseWithParamsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        AbstractNullPolicy aNullPolicy = new IsSetNullPolicy();
    	// Alter unmarshal policy state
    	aNullPolicy.setNullRepresentedByEmptyNode(false); // no effect
    	aNullPolicy.setNullRepresentedByXsiNil(false);  // no effect
    	// Alter marshal policy state
    	aNullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE); // no effect when isset=false
    	
        Class[] isSetParameterTypes = {String.class, String.class, Boolean.class, Integer.class, Short.class, Long.class, Double.class, Float.class, Byte.class, Character.class};
        //Class[] isSetParameterTypes = {String.class, String.class};

        // TODO: verify EMPTY_STRING behavior
        //Object[] isSetParameters = {"x","y", false, (int)1, (short)1, (long)1, (double)1.0, (float)1.0, (byte)1};
        Object[] isSetParameters = {"x","y", new Boolean(true), new Integer(255), new Short((short)32767), new Long(1), new Double(1.0), new Float(-1.0), new Byte((byte)32), new Character('C')};
        //Object[] isSetParameters = {"x","y"};

        ((IsSetNullPolicy)aNullPolicy).setIsSetMethodName("isSetFirstName");
        // Class[]
        ((IsSetNullPolicy)aNullPolicy).setIsSetParameterTypes(isSetParameterTypes);
        // Object[]
        ((IsSetNullPolicy)aNullPolicy).setIsSetParameters(isSetParameters);
        Project aProject = new DirectIsSetNodeNullPolicyProject(true);
        XMLDirectMapping aMapping = (XMLDirectMapping)aProject.getDescriptor(EmployeeIsSetParams.class)//
        .getMappingForAttributeName("firstName");
        aMapping.setNullPolicy(aNullPolicy);
        setProject(aProject);
    }

    protected Object getControlObject() {
        EmployeeIsSetParams anEmployee = new EmployeeIsSetParams();
        anEmployee.setId(123);
        //anEmployee.setFirstName(null);
        anEmployee.setLastName("Doe");
        return anEmployee;
    }
}
