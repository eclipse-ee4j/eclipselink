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
package org.eclipse.persistence.testing.oxm.converter.typesafeenum;

import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class TypeSafeEnumConverterTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/converter/typesafeenum/typesafeenum.xml";

    public TypeSafeEnumConverterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        TypeSafeEnumConverterProject p = new TypeSafeEnumConverterProject();
        p.getDescriptor(Employee.class).setAmendmentMethodName("amendDescriptor");
        p.getDescriptor(Employee.class).applyAmendmentMethod();
        setProject(p);
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setFirstName("Linda");
        employee.setLastName("Jones");

        MyTypeSafeEnumClass smallTypeSafeEnum = MyTypeSafeEnumClass.smallEnum;
        MyTypeSafeEnumClass mediumTypeSafeEnum = MyTypeSafeEnumClass.mediumEnum;
        MyTypeSafeEnumClass largeTypeSafeEnum = MyTypeSafeEnumClass.largeEnum;

        employee.setShirtSize(largeTypeSafeEnum);
        employee.addHatSize(mediumTypeSafeEnum);
        employee.addHatSize(smallTypeSafeEnum);

        return employee;
    }

    public void testNoEnumClassSpecified() throws Exception {
        TypeSafeEnumConverterProject p = new TypeSafeEnumConverterProject();
        p.getDescriptor(Employee.class).setAmendmentMethodName("amendDescriptorNoEnumClass");
        p.getDescriptor(Employee.class).applyAmendmentMethod();
        try {
            super.getXMLContext(p);
        } catch (Exception e) {            
            if (e instanceof IntegrityException) {
                Exception internalException = (Exception)((IntegrityException)e).getIntegrityChecker().getCaughtExceptions().get(0);
                if (internalException instanceof XMLMarshalException) {
                    assertTrue("An incorrect XMLMarshalException exception occurred.", ((XMLMarshalException)internalException).getErrorCode() == XMLMarshalException.ENUM_CLASS_NOT_SPECIFIED);
                    return;
                }
            } else {
                fail("An XMLMarhsalException should have been thrown but wasn't");
                return;
            }
        }
        fail("An Exception should have been thrown but wasn't");
    }

    public void testNoSuchMethod() throws Exception {
        TypeSafeEnumConverterProject p = new TypeSafeEnumConverterProject();
        p.getDescriptor(Employee.class).setAmendmentMethodName("amendDescriptorNoSuchMethod");
        p.getDescriptor(Employee.class).applyAmendmentMethod();
        try {
            super.getXMLContext(p);
        } catch (Exception e) {            
            if (e instanceof IntegrityException) {
                Exception internalException = (Exception)((IntegrityException)e).getIntegrityChecker().getCaughtExceptions().get(0);
                if (internalException instanceof XMLMarshalException) {
                    assertTrue("An incorrect XMLMarshalException exception occurred.", ((XMLMarshalException)internalException).getErrorCode() == XMLMarshalException.FROMSTRING_METHOD_ERROR);
                    return;
                }
            } else {
                fail("An XMLMarhsalException should have been thrown but wasn't");
                return;

            }
        }
        fail("An Exception should have been thrown but wasn't");
    }

    public void testInvalidEnumClassSpecified() throws Exception {
        TypeSafeEnumConverterProject p = new TypeSafeEnumConverterProject();
        p.getDescriptor(Employee.class).setAmendmentMethodName("amendDescriptorInvalidEnumClass");
        p.getDescriptor(Employee.class).applyAmendmentMethod();
        try {
            super.getXMLContext(p);
        } catch (Exception e) {            
            if (e instanceof IntegrityException) {
                Exception internalException = (Exception)((IntegrityException)e).getIntegrityChecker().getCaughtExceptions().get(0);
                if (internalException instanceof XMLMarshalException) {
                    assertTrue("An incorrect XMLMarshalException exception occurred.", ((XMLMarshalException)internalException).getErrorCode() == XMLMarshalException.INVALID_ENUM_CLASS_SPECIFIED);
                    return;
                }
            } else {
                fail("An XMLMarhsalException should have been thrown but wasn't");
                return;

            }
        }
        fail("An Exception should have been thrown but wasn't");
    }
}
