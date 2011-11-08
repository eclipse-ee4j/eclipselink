/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 08 November 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype.factory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class FactoryTestCases extends TestCase {

    private JAXBContext jaxbContext;

    public FactoryTestCases(String name) throws Exception {
        super(name);
    }

    public String getName() {
        return "JAXB Factory Instantiation: " + super.getName();
    }

    /**
     * Concrete factory class, non-static factory method.
     */
    public void testNormalFactory() throws Exception {
        Class[] classes = new Class[] { Employee.class };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream bindings = classLoader.getResourceAsStream(NORMAL_FACTORY);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindings);

        jaxbContext = JAXBContextFactory.createContext(classes, properties);

        InputStream xml = classLoader.getResourceAsStream(EMPLOYEE_XML);
        Employee e = (Employee) jaxbContext.createUnmarshaller().unmarshal(xml);
        assertEquals(getControlObject(), e);
    }

    /**
     * Abstract factory class, static factory method.
     */
    public void testAbstractFactory() throws Exception {
        Class[] classes = new Class[] { Employee.class };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream bindings = classLoader.getResourceAsStream(ABSTRACTFACTORY);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindings);

        jaxbContext = JAXBContextFactory.createContext(classes, properties);

        InputStream xml = classLoader.getResourceAsStream(EMPLOYEE_XML);
        Employee e = (Employee) jaxbContext.createUnmarshaller().unmarshal(xml);
        assertEquals(getControlObject(), e);
    }

    /**
     * Abstract factory class, non-static factory method (ERROR).
     */
    public void testAbstractFactoryError() throws Exception {
        Class[] classes = new Class[] { Employee.class };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream bindings = classLoader.getResourceAsStream(ABSTRACTFACTORY_ERROR);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindings);

        Exception caughtException = null;
        try {
            jaxbContext = JAXBContextFactory.createContext(classes, properties);
        } catch (Exception e) {
            caughtException = e;
        }

        assertNotNull(caughtException);

        // Check for
        // "The factory class does not define a public default constructor, or the constructor raised an exception."
        try {
            JAXBException jaxbException = (JAXBException) caughtException;
            IntegrityException integrityException = (IntegrityException) jaxbException.getLinkedException();
            DescriptorException descriptorException = (DescriptorException) integrityException.getIntegrityChecker().getCaughtExceptions().firstElement();
            assertEquals("Incorrect error code:", DescriptorException.INSTANTIATION_WHILE_CONSTRUCTOR_INSTANTIATION_OF_FACTORY, descriptorException.getErrorCode());
        } catch (Exception e) {
            fail("The expected Exception structure was not thrown, instead caught: " + caughtException.getLocalizedMessage());
        }
    }

    /**
     * Concrete factory class with no default constructor, static factory method.
     */
    public void testNoDefaultConstructorFactory() throws Exception {
        Class[] classes = new Class[] { Employee.class };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream bindings = classLoader.getResourceAsStream(NODEFAULTCONSTRUCTOR);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindings);

        jaxbContext = JAXBContextFactory.createContext(classes, properties);

        InputStream xml = classLoader.getResourceAsStream(EMPLOYEE_XML);
        Employee e = (Employee) jaxbContext.createUnmarshaller().unmarshal(xml);
        assertEquals(getControlObject(), e);
    }

    /**
     * Concrete factory class with no default constructor, non-static factory method (ERROR).
     */
    public void testNoDefaultConstructorFactoryError() throws Exception {
        Class[] classes = new Class[] { Employee.class };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream bindings = classLoader.getResourceAsStream(NODEFAULTCONSTRUCTOR_ERROR);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindings);

        Exception caughtException = null;
        try {
            jaxbContext = JAXBContextFactory.createContext(classes, properties);
        } catch (Exception e) {
            caughtException = e;
        }

        assertNotNull(caughtException);

        // Check for
        // "The instance creation method [{0}], with no parameters, does not exist, or is not accessible."
        try {
            JAXBException jaxbException = (JAXBException) caughtException;
            IntegrityException integrityException = (IntegrityException) jaxbException.getLinkedException();
            DescriptorException descriptorException = (DescriptorException) integrityException.getIntegrityChecker().getCaughtExceptions().firstElement();
            assertEquals("Incorrect error code:", DescriptorException.NO_SUCH_METHOD_WHILE_INITIALIZING_INSTANTIATION_POLICY, descriptorException.getErrorCode());
        } catch (Exception e) {
            fail("The expected Exception structure was not thrown, instead caught: " + caughtException.getLocalizedMessage());
        }
    }

    /**
     * No factory class specified, no default constructor on domain class, static factory method on domain class.
     */
    public void testDomainClassFactoryMethod() throws Exception {
        Class[] classes = new Class[] { EmployeeWithFactoryMethod.class };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream bindings = classLoader.getResourceAsStream(DOMAINCLASSFACTORYMETHOD);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindings);

        jaxbContext = JAXBContextFactory.createContext(classes, properties);

        InputStream xml = classLoader.getResourceAsStream(EMPLOYEE_XML);
        EmployeeWithFactoryMethod e = (EmployeeWithFactoryMethod) jaxbContext.createUnmarshaller().unmarshal(xml);
        assertEquals(getControlObject2(), e);
    }

    /**
     * No factory class specified, no default constructor on domain class, non-static factory method on domain class (ERROR).
     */
    public void testDomainClassFactoryMethodError() throws Exception {
        Class[] classes = new Class[] { EmployeeWithNonStaticFactoryMethod.class };

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream bindings = classLoader.getResourceAsStream(DOMAINCLASSFACTORYMETHOD_ERROR);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, bindings);

        Exception caughtException = null;
        try {
            jaxbContext = JAXBContextFactory.createContext(classes, properties);
            InputStream xml = classLoader.getResourceAsStream(EMPLOYEE_XML);
            EmployeeWithNonStaticFactoryMethod e = (EmployeeWithNonStaticFactoryMethod) jaxbContext.createUnmarshaller().unmarshal(xml);
        } catch (Exception e) {
            caughtException = e;
        }

        assertNotNull(caughtException);

        // Check for
        // "Problem in creating new instance using creation method [x].  The creation method is not accessible."
        try {
            JAXBException jaxbException = (JAXBException) caughtException;
            DescriptorException descriptorException = (DescriptorException) jaxbException.getLinkedException();
            assertEquals("Incorrect error code:", DescriptorException.NULL_POINTER_WHILE_METHOD_INSTANTIATION, descriptorException.getErrorCode());
        } catch (Exception ex) {
            fail("The expected Exception structure was not thrown, instead caught: " + caughtException.getLocalizedMessage());
        }
    }

    // ========================================================================

    public Employee getControlObject() {
        Employee e = new Employee();
        e.id = EMP_ID;
        e.firstName = EMP_FIRST;
        e.lastName = EMP_LAST;
        e.fromFactoryMethod = true;
        return e;
    }

    public EmployeeWithFactoryMethod getControlObject2() {
        EmployeeWithFactoryMethod e = new EmployeeWithFactoryMethod(false);
        e.id = EMP_ID;
        e.firstName = EMP_FIRST;
        e.lastName = EMP_LAST;
        e.fromFactoryMethod = 1;
        return e;
    }

    private static final String RESOURCE_DIR = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/factory/";

    private static final String NORMAL_FACTORY = RESOURCE_DIR + "normalfactory.xml";
    private static final String ABSTRACTFACTORY = RESOURCE_DIR + "abstractfactory.xml";
    private static final String ABSTRACTFACTORY_ERROR = RESOURCE_DIR + "abstractfactoryerror.xml";
    private static final String NODEFAULTCONSTRUCTOR = RESOURCE_DIR + "nodefaultconstructor.xml";
    private static final String NODEFAULTCONSTRUCTOR_ERROR = RESOURCE_DIR + "nodefaultconstructorerror.xml";
    private static final String DOMAINCLASSFACTORYMETHOD = RESOURCE_DIR + "domainclassfactorymethod.xml";
    private static final String DOMAINCLASSFACTORYMETHOD_ERROR = RESOURCE_DIR + "domainclassfactorymethoderror.xml";

    private static final String EMPLOYEE_XML = RESOURCE_DIR + "employee.xml";

    public static final int EMP_ID = 101;
    public static final String EMP_FIRST = "Joe";
    public static final String EMP_LAST = "Oracle";

}