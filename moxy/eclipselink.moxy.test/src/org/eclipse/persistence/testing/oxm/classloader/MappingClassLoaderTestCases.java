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
package org.eclipse.persistence.testing.oxm.classloader;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.textui.TestRunner;
import org.w3c.dom.Document;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.oxm.attachment.XMLAttachmentUnmarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class MappingClassLoaderTestCases extends OXTestCase {
    private static final String ATTACHMENT_MARSHALLER_CLASS = "org.eclipse.persistence.testing.oxm.classloader.MyAttachmentMarshaller";
    private static final String ATTACHMENT_UNMARSHALLER_CLASS = "org.eclipse.persistence.testing.oxm.classloader.MyAttachmentUnmarshaller";
    // some mappings (binary data, choice) are not supported in DOM/DOC_PRES and/or deployment XML
    private static final String MAPPING_TEST_RESOURCE_ALL = "org/eclipse/persistence/testing/oxm/classloader/maptest-all.xml";
    private static final String MAPPING_TEST_RESOURCE_NO_CHOICE = "org/eclipse/persistence/testing/oxm/classloader/maptest-no-choice.xml";
    private static final String MAPPING_TEST_RESOURCE_NO_CHOICE_OR_BINARY = "org/eclipse/persistence/testing/oxm/classloader/maptest-no-choice-or-binary.xml";
    private static final String MAPPING_TEST_RESOURCE_NO_BINARY = "org/eclipse/persistence/testing/oxm/classloader/maptest-no-binary.xml";
    
    private ClassLoader classLoader;
    private XMLContext xmlContext;
    private XMLUnmarshaller unmarshaller;
    private XMLMarshaller marshaller;

    public MappingClassLoaderTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.classloader.MappingClassLoaderTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() throws Exception {
        classLoader = new JARClassLoader("org/eclipse/persistence/testing/oxm/classloader/MappingTests.jar");
        xmlContext = getXMLContext(new MappingTestProject(classLoader, platform, metadata), classLoader);
        unmarshaller = xmlContext.createUnmarshaller();
        unmarshaller.setAttachmentUnmarshaller((XMLAttachmentUnmarshaller) classLoader.loadClass(ATTACHMENT_UNMARSHALLER_CLASS).newInstance());
        marshaller = xmlContext.createMarshaller();
        marshaller.setAttachmentMarshaller((XMLAttachmentMarshaller) classLoader.loadClass(ATTACHMENT_MARSHALLER_CLASS).newInstance());
    }

    /**
     * Attempting to create the project with the wrong class loader should
     * cause a ClassNotFoundException.
     * 
     * @throws Exception
     */
    public void testClassLoadFailure() throws Exception {
        boolean exception = false;
        String msg = "";
        try {
            new MappingTestProject(Thread.currentThread().getContextClassLoader(), platform, metadata);
            msg = "\nA ClassNotFoundException did not occur as expected";
        } catch (RuntimeException re) {
            Throwable nestedException = re.getCause();
            // exception should be ClassNotFoundException - if not, an unexpected exception occurred
            try {
                ClassNotFoundException cnfe = (ClassNotFoundException) nestedException;
                exception = true;
            } catch (ClassCastException cce) {
                msg = "\nAn unexpected exception occurred: " + cce.getMessage();
            }
        }
        assertTrue(msg, exception);
    }
    
    public void testMappings() throws Exception {
        Document unmarshalDoc = null;
        Document marshalDoc = null;
        boolean exception = false;
        String msg = "";
        try {
            if (metadata == OXTestCase.Metadata.JAVA) {
                if (platform == OXTestCase.Platform.SAX) {
                    unmarshalDoc = parse(MAPPING_TEST_RESOURCE_ALL);
                } else {
                    unmarshalDoc = parse(MAPPING_TEST_RESOURCE_NO_CHOICE);
                }
            } else {
                if(platform == OXTestCase.platform.SAX) {
                    unmarshalDoc = parse(MAPPING_TEST_RESOURCE_NO_BINARY);
                } else {
                    unmarshalDoc = parse(MAPPING_TEST_RESOURCE_NO_CHOICE_OR_BINARY);
                }
            }
            Object rootObject = unmarshaller.unmarshal(unmarshalDoc);
            marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            marshaller.marshal(rootObject, marshalDoc);
        } catch (Exception x) {
            exception = true;
            x.printStackTrace();
            msg = x.getMessage();
        }
        assertFalse("\nAn unexpected exception occurred: " + msg, exception);
        assertXMLIdentical(unmarshalDoc, marshalDoc);
    }

    private Document parse(String xmlResource) throws Exception {
        InputStream inputStream = classLoader.getResourceAsStream(xmlResource);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        builderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder parser = builderFactory.newDocumentBuilder();
        Document doc = parser.parse(inputStream);
        removeEmptyTextNodes(doc);
        return doc;
    }
}
