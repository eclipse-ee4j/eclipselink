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
 *     rbarkhouse - 2009-11-16 14:08:13 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DynamicTestCases extends XMLMappingTestCases {

    private DynamicClassLoader dynamicClassLoader;
    private Project dynamicTestProject;

    private ArrayList objectsAlreadyCheckedForEquality;

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/dynamic/dynamic.xml";

    private static final String EMPLOYEE_CLASS_NAME = "org.eclipse.persistence.testing.oxm.dynamic.XEmployee";
    private static final String ADDRESS_CLASS_NAME = "org.eclipse.persistence.testing.oxm.dynamic.XAddress";
    private static final String PHONE_CLASS_NAME = "org.eclipse.persistence.testing.oxm.dynamic.XPhoneNumber";
    private static final String EMAIL_CLASS_NAME = "org.eclipse.persistence.testing.oxm.dynamic.XEmail";    
    private static final String SEC_CRED_CLASS_NAME = "org.eclipse.persistence.testing.oxm.dynamic.XSecurityCredential";    
    private static final String ROOT_CLASS_NAME = "org.eclipse.persistence.testing.oxm.dynamic.XRoot";    

    public DynamicTestCases(String name) throws Exception {
        super(name);

        ClassLoader sysClassLoader = Thread.currentThread().getContextClassLoader();
        dynamicClassLoader = new DynamicClassLoader(sysClassLoader);

        dynamicTestProject = DynamicTypeBuilder.loadDynamicProject((Project) new DynamicTestProject(), null, dynamicClassLoader);

        setProject(dynamicTestProject);
        setControlDocument(XML_RESOURCE);
    }

    public Object getControlObject() {
        try {
            Object rootObj = newInstance(ROOT_CLASS_NAME);
        	
            // =============
        	
        	Object employeeObj = newInstance(EMPLOYEE_CLASS_NAME);
            setValue(employeeObj, "name", "Jane Doe");

            // =============

            Object addressObj = newInstance(ADDRESS_CLASS_NAME);
            setValue(addressObj, "street", "123 Fake Street");

            setValue(addressObj, "owningEmployee", employeeObj);
            
            setValue(employeeObj, "address", addressObj);

            // =============

            Object phoneObj1 = newInstance(PHONE_CLASS_NAME);
            setValue(phoneObj1, "number", "123-4567");
            setValue(phoneObj1, "owningEmployee", employeeObj);

            Object phoneObj2 = newInstance(PHONE_CLASS_NAME);
            setValue(phoneObj2, "number", "234-5678");
            setValue(phoneObj2, "owningEmployee", employeeObj);

            ArrayList phoneNumbers = new ArrayList();
            phoneNumbers.add(phoneObj1);
            phoneNumbers.add(phoneObj2);

            setValue(employeeObj, "phoneNumbers", phoneNumbers);

            // =============
            
            Object email1 = newInstance(EMAIL_CLASS_NAME);
            setValue(email1, "id", "123");
            setValue(email1, "username", "jane.doe");
            setValue(email1, "domain", "eclipse.org");
            setValue(email1, "owningEmployee", employeeObj);

            Object email2 = newInstance(EMAIL_CLASS_NAME);
            setValue(email2, "id", "456");
            setValue(email2, "username", "jdoe1972");
            setValue(email2, "domain", "rocketmail.com");
            setValue(email2, "owningEmployee", employeeObj);            
            
            ArrayList emails = new ArrayList();
            emails.add(email1);
            emails.add(email2);

            setValue(employeeObj, "emails", emails);

            // =============
            
            Object secCred = newInstance(SEC_CRED_CLASS_NAME);
            setValue(secCred, "id", "123");
            setValue(secCred, "zone", "17A");            
            setValue(secCred, "keyNumber", "82347623847");
            
            setValue(employeeObj, "securityCredential", secCred);
            
            setValue(secCred, "owningEmployee", employeeObj);

            // =============            
            
            setValue(rootObj, "employee", employeeObj);
            setValue(rootObj, "emails", emails);
            setValue(rootObj, "securityCredential", secCred);

            return rootObj;
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }

    private Object newInstance(String name) throws Exception {
        Class theClass = dynamicClassLoader.loadClass(name);
        Constructor constructor = theClass.getConstructors()[0];

        return constructor.newInstance();
    }

    private void setValue(Object owner, String attributeName, Object value) throws Exception {
        Method setMethod = owner.getClass().getMethod("set", new Class[] { String.class, Object.class });
        setMethod.invoke(owner, new Object[] { attributeName, value });
    }

    private Object getValue(Object owner, String attributeName) throws Exception {
        Method getMethod = owner.getClass().getMethod("get", new Class[] { String.class });
        Object value = getMethod.invoke(owner, new Object[] { attributeName });
        return value;
    }

    @Override
    public void xmlToObjectTest(Object testObject) throws Exception {
        log("\n**xmlToObjectTest**");
        log("Expected:");
        Object controlObject = getReadControlObject();
        if (null == controlObject) {
            log((String) null);
        } else {
            log(controlObject.toString());
        }
        log("Actual:");
        if (null == testObject) {
            log((String) null);
        } else {
            log(testObject.toString());
        }

        if ((controlObject instanceof XMLRoot) && (testObject instanceof XMLRoot)) {
            XMLRoot controlRoot = (XMLRoot) controlObject;
            XMLRoot testRoot = (XMLRoot) testObject;
            compareXMLRootObjects(controlRoot, testRoot);
        } else {
            objectsAlreadyCheckedForEquality = new ArrayList();
            compareDynamicObjects(controlObject, testObject);
        }
    }

    private void compareDynamicObjects(Object controlObject, Object testObject) throws Exception {
        if (objectsAlreadyCheckedForEquality.contains(testObject)) {
            // To handle cyclic relationships, only check each object once.
            return;
        }

        if (!(controlObject instanceof DynamicEntityImpl)) {
            fail("controlObject [" + controlObject + "] was not an instance of DynamicEntityImpl.");
        }
        if (!(testObject instanceof DynamicEntityImpl)) {
            fail("testObject [" + testObject + "] was not an instance of DynamicEntityImpl.");
        }

        DynamicEntityImpl dynamicControl = (DynamicEntityImpl) controlObject;
        DynamicEntityImpl dynamicTest = (DynamicEntityImpl) testObject;

        objectsAlreadyCheckedForEquality.add(testObject);

        if (dynamicControl.getType().getNumberOfProperties() != dynamicTest.getType().getNumberOfProperties()) {
            fail("testObject and controlObject did not have the same number of properties.");
        }

        List<String> propNames = dynamicControl.getType().getPropertiesNames();
        Iterator<String> it = propNames.iterator();
        while (it.hasNext()) {
            String propName = it.next();

            Object controlValue = getValue(dynamicControl, propName);
            Object testValue = getValue(dynamicTest, propName);

            if ((testValue instanceof DynamicEntityImpl) && (controlValue instanceof DynamicEntityImpl)) {
                compareDynamicObjects(controlValue, testValue);
            } else if ((testValue instanceof List) && (controlValue instanceof List)) {
                if ((((List) testValue).size()) != (((List) controlValue).size())) {
                    fail(dynamicControl.getType().getName() + " " + propName + ": testValue was " + testValue + " but controlValue was " + controlValue);
                }
                for (int i = 0; i < ((List) controlValue).size(); i++) {
                    compareDynamicObjects(((List) controlValue).get(i), ((List) testValue).get(i));
                }
            } else {
                if (testValue == null && controlValue == null) {
                    continue;
                }
                if (testValue == null && controlValue != null) {
                    fail(dynamicControl.getType().getName() + " " + propName + ": testValue was " + testValue + " but controlValue was " + controlValue);
                }
                if (testValue != null && controlValue == null) {
                    fail(dynamicTest.getType().getName() + " " + propName + ": testValue was " + testValue + " but controlValue was " + controlValue);
                }
                if (!testValue.equals(controlValue)) {
                    fail(propName + ": testValue was " + testValue + " but controlValue was " + controlValue);
                }
            }
        }
    }

}