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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.models.interfaces.Employee;

/**
 * Bug 326091
 * @author dminsky 
 */
public class VariableOneToOneGetTypeFieldTest extends TestCase {
    
    protected VariableOneToOneMapping contactMapping;
    protected Object valueReturned1;
    protected Object valueReturned2;
    
    public VariableOneToOneGetTypeFieldTest() {
        super();
        setDescription("Verify VariableOneToOneMapping returns the configured type field value when passed a type field");
    }
    
    public void setup() {
        ClassDescriptor employeeDescriptor = getSession().getClassDescriptor(Employee.class);
        assertNotNull(employeeDescriptor);
        this.contactMapping = (VariableOneToOneMapping) employeeDescriptor.getMappingForAttributeName("contact");
        assertNotNull(contactMapping);
    }
    
    public void test() {
        this.valueReturned1 = contactMapping.valueFromObject(
                Employee.example1(), // EXAMPLE 1
                contactMapping.getTypeField(),
                (AbstractSession)getSession());
        
        this.valueReturned2 = contactMapping.valueFromObject(
                Employee.example3(), // EXAMPLE 3
                new DatabaseField(contactMapping.getTypeFieldName()), // new field w/ same name
                (AbstractSession)getSession());
    }
    
    public void verify() {
        Class actualClass;
        Object actualValue;
        
        // valueReturned1
        actualClass = Employee.example1().getContact().getClass();
        actualValue = contactMapping.getTypeIndicatorTranslation().get(actualClass);

        assertNotNull("VariableOneToOne type value 'valueReturned1' should not be null", valueReturned1);
        assertEquals("Incorrect value returned for type field value", actualValue, valueReturned1);

        // valueReturned2
        actualClass = Employee.example3().getContact().getClass();
        actualValue = contactMapping.getTypeIndicatorTranslation().get(actualClass);
        
        assertNotNull("VariableOneToOne type value 'valueReturned2' should not be null", valueReturned2);
        assertEquals("Incorrect value returned for type field value", actualValue, valueReturned2);
    }
    
    public void reset() {
        this.valueReturned1 = null;
        this.valueReturned2 = null;
    }

}
