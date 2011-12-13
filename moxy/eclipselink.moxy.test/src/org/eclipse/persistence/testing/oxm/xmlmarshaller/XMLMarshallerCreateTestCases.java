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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import java.io.StringWriter;
import java.util.Vector;
import junit.textui.TestRunner;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class XMLMarshallerCreateTestCases extends OXTestCase {
    private static String SESSION_NAME = "XMLMarshallerTestSession";
    private static String SESSION_NAME2 = "XMLMarshallerTestSession2";

    public XMLMarshallerCreateTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshallerCreateTestCases" };
        TestRunner.main(arguments);
    }

    /**
     * Create an XMLMarshaller with a session name that does not exist in the sessions.xml file
     * An XMLValidation exception should be thrown from the XMLMarshaller constructor
     */
    public void testInvalidSessionName() {
        String sessionName = "invalidSessionName";

        try {
            XMLContext context = new XMLContext(sessionName);
        } catch (ValidationException exception) {
            assertTrue("A session not found exception should have been caught but wasn't.", exception.getErrorCode() == ValidationException.NO_SESSION_FOUND);
            return;
        } catch (Exception e) {
            assertTrue("A session not found exception should have been caught but wasn't.", false);
        }
        assertTrue("A session not found exception should have been caught but wasn't.", false);

    }

    public void testInvalidMappingTypeEIS() {
        Project project = new Project();
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Car.class);
        descriptor.setDefaultRootElement("test");

        EISDirectMapping mapping = new EISDirectMapping();
        mapping.setAttributeName("license");
        mapping.setXPath("license");
        descriptor.addMapping(mapping);
        project.addDescriptor(descriptor);
        try {
            XMLContext context = new XMLContext(project);
        } catch (DescriptorException exception) {
            assertTrue("An invalid mapping type exception should have been caught but wasn't.", exception.getErrorCode() == DescriptorException.INVALID_MAPPING_TYPE);
            return;
        } catch (IntegrityException exception) {
            Vector exceptions = exception.getIntegrityChecker().getCaughtExceptions();
            assertTrue("Too many exceptions were found...should have been 1: " + exceptions, exceptions.size() == 1);
            Exception e = (Exception)exceptions.elementAt(0);
            assertTrue("A DescriptorException should have been caught but wasn't.", e.getClass() == DescriptorException.class);
            assertTrue("An invalid mapping type exception should have been caught but wasn't.", ((DescriptorException)e).getErrorCode() == DescriptorException.INVALID_MAPPING_TYPE);
            return;
        }
        assertTrue("An invalid mapping type exception should have been caught but wasn't.", false);
    }

    public void testInvalidMappingTypeRelational() {
        Project project = new Project();
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Car.class);
        descriptor.setDefaultRootElement("test");

        DirectToFieldMapping mapping = new DirectToFieldMapping();
        mapping.setAttributeName("license");
        mapping.setFieldName("license");
        descriptor.addMapping(mapping);
        project.addDescriptor(descriptor);
        try {
            XMLContext context = new XMLContext(project);
        } catch (DescriptorException exception) {
            assertTrue("An invalid mapping type exception should have been caught but wasn't.", exception.getErrorCode() == DescriptorException.INVALID_MAPPING_TYPE);
            return;
        } catch (IntegrityException exception) {
            Vector exceptions = exception.getIntegrityChecker().getCaughtExceptions();
            assertTrue("Too many exceptions were found...should have been 1.", exceptions.size() == 1);
            Exception e = (Exception)exceptions.elementAt(0);
            assertTrue("A DescriptorException should have been caught but wasn't.", e.getClass() == DescriptorException.class);
            assertTrue("An invalid mapping type exception should have been caught but wasn't.", ((DescriptorException)e).getErrorCode() == DescriptorException.INVALID_MAPPING_TYPE);
            return;
        }
        assertTrue("An invalid mapping type exception should have been caught but wasn't.", false);
    }

    /**
     * Create an XMLMarshaller with 2 session names separated by a semi-colon
     * Ensure that objects from each of the 2 projects/sessions can be marshalled
     */
    public void testMultipleSessionNames() {
        String sessionName = SESSION_NAME + ":" + SESSION_NAME2;

        XMLContext context = new XMLContext(sessionName);

        assertNotNull(context);

        XMLMarshaller marshaller = context.createMarshaller();
        StringWriter writer1 = new StringWriter();
        marshaller.marshal(new Employee(), writer1);

        StringWriter writer2 = new StringWriter();
        marshaller.marshal(new Car(), writer2);

    }

    /**
     * Create an XMLMarshaller with 2 session names separated by an invalid delimiter
     * only semi-colons are valid delimiters.
     * The XMLMarshaller constructor should throw an XMLValidation exception
     */
    public void testMultipleSessionNamesInvalidDelimiter() {
        String sessionName = SESSION_NAME + ";" + SESSION_NAME2;
        try {
            XMLContext context = new XMLContext(sessionName);
        } catch (ValidationException exception) {
            assertTrue("A session not found exception should have been caught but wasn't.", exception.getErrorCode() == ValidationException.NO_SESSION_FOUND);
            return;
        } catch (Exception e) {
            assertTrue("A session not found exception should have been caught but wasn't.", false);
        }
        assertTrue("A session not found exception should have been caught but wasn't", false);
    }

    /**
     * Create a new XMLMarshaller with a project instead of a session.
     * Make sure that an object can be marshalled without exception.
     */
    public void testProject() {
        XMLMarshaller marshaller = null;
        XMLContext context = getXMLContext(new XMLMarshallerTestProject());

        assertNotNull(context);

        marshaller = context.createMarshaller();

        StringWriter writer1 = new StringWriter();
        marshaller.marshal(new Employee(), writer1);

    }
    public void testSessionWithEventListener() 
    {
      XMLMarshallerEventListener.eventTriggered = false;
      XMLContext context = new XMLContext("XMLMarshallerEventsTestSession");
      assertTrue("The postLogin SessionEvent was not triggered", XMLMarshallerEventListener.eventTriggered);
      
    }
}
