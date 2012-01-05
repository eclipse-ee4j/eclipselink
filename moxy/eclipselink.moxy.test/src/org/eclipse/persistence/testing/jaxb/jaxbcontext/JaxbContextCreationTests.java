/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.testing.oxm.classloader.JARClassLoader;
import org.w3c.dom.Document;

public class JaxbContextCreationTests extends junit.framework.TestCase {

    public String getName() {
        return "JAXB Context Creation Tests: " + super.getName();
    }

    public void testCreateContextWithObjectFactory() throws Exception {
        JAXBContext context = JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext", Thread.currentThread().getContextClassLoader());
    }

    public void testCreateContextNoClassesOrSessions() throws Exception {
        try {
            JAXBContext context = JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext.fake", Thread.currentThread().getContextClassLoader());
        } catch(JAXBException ex) {
            assertTrue(((org.eclipse.persistence.exceptions.JAXBException)ex.getLinkedException()).getErrorCode() == org.eclipse.persistence.exceptions.JAXBException.NO_OBJECT_FACTORY_OR_JAXB_INDEX_IN_PATH);
            assertTrue(((org.eclipse.persistence.exceptions.JAXBException)ex.getLinkedException()).getInternalException() instanceof ValidationException);
        }
    }

    public void testCreateContextUnrelatedSessionsXml() throws Exception {
        JAXBContext context = JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext", new ClassLoader() {
            public URL getResource(String resourceName) {
                if(resourceName.equals("sessions.xml")) {
                    return getParent().getResource("org/eclipse/persistence/testing/jaxb/jaxbcontext/sessions.xml");
                }
                return this.getParent().getResource(resourceName);
            }
        });
    }

    public void testCreateContextUnrelatedSessionsXmlInvalidPath() throws Exception {
        try {
            JAXBContext context = JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext.fake", new ClassLoader() {
                public URL getResource(String resourceName) {
                    if(resourceName.equals("sessions.xml")) {
                        return getParent().getResource("org/eclipse/persistence/testing/jaxb/jaxbcontext/sessions.xml");
                    }
                    return this.getParent().getResource(resourceName);
                }
            });
        } catch(JAXBException ex) {
            assertTrue(((org.eclipse.persistence.exceptions.JAXBException)ex.getLinkedException()).getErrorCode() == org.eclipse.persistence.exceptions.JAXBException.NO_OBJECT_FACTORY_OR_JAXB_INDEX_IN_PATH);
            assertTrue(((org.eclipse.persistence.exceptions.JAXBException)ex.getLinkedException()).getInternalException() instanceof SessionLoaderException);
        }
    }

    public void testCreateContextWithStringClass() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = String.class;
        JAXBContextFactory.createContext(classes, null);
    }

    public void testCreateContextWithIntArrayClass() throws JAXBException {
        Class[] classes = new Class[1];
        int[] ints = new int[1];
        classes[0] = ints.getClass();
        JAXBContextFactory.createContext(classes, null);
    }

    public void testCreateContextWith_ClassArray_NullClassLoader() throws JAXBException {
        Class[] classes = new Class[1];
        int[] ints = new int[1];
        classes[0] = ints.getClass();
        JAXBContextFactory.createContext(classes, null);
    }

    public void testCreateContextWith_ClassArray_Map_NullClassLoader_Map() throws JAXBException {
        Class[] classes = new Class[1];
        int[] ints = new int[1];
        classes[0] = ints.getClass();
        JAXBContextFactory.createContext(classes, null, null);
    }

    public void testCreateContextWith_String_NullClassLoader() throws JAXBException {
        JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext", null);
    }

    public void testCreateContextWith_String_NullClassLoader_Map() throws JAXBException {
        JAXBContextFactory.createContext("org.eclipse.persistence.testing.jaxb.jaxbcontext", null, null);
    }

    public void testCreateContextWith_TypeArray_Map_NullClassLoader() throws JAXBException {
        Type[] types = new Type[1];
        types[0] = List.class;
        JAXBContextFactory.createContext(types, null, null);
    }

    public void testCreateContextWith_TypeMappingInfoArray_Map_NullClassLoader() throws JAXBException {
        TypeMappingInfo[] typeMappingInfos = new TypeMappingInfo[1];
        TypeMappingInfo listTMI = new TypeMappingInfo();
        listTMI.setType(List.class);
        listTMI.setXmlTagName(new QName("urn:example", "my-list"));
        typeMappingInfos[0] = listTMI;
        JAXBContextFactory.createContext(typeMappingInfos, null, null);
    }

    public void testCreateAbstractClassWithMultiArgConstructor() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = AbstractClassWithMultiArgConstructor.class;
        JAXBContextFactory.createContext(classes, null);
    }

    public void testCreateConcreteClassWithMultiArgConstructor() throws JAXBException {
        try {
            Class[] classes = new Class[1];
            classes[0] = ConcreteClassWithMultiArgConstructor.class;
            JAXBContextFactory.createContext(classes, null);
        } catch(JAXBException e) {
            org.eclipse.persistence.exceptions.JAXBException je = (org.eclipse.persistence.exceptions.JAXBException) e.getLinkedException();
            assertEquals(org.eclipse.persistence.exceptions.JAXBException.FACTORY_METHOD_OR_ZERO_ARG_CONST_REQ, je.getErrorCode());
            return;
        }
        fail();
    }

    public void testCreateContextWithGenerics() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = ConcreteClassWithGenerics.class;
        JAXBContextFactory.createContext(classes, null);
    }

    public void testCreateContextWithPathAndBindings() throws Exception {
        String oxmString = "org/eclipse/persistence/testing/jaxb/jaxbcontext/eclipselink-oxm.xml";
        InputStream oxm = ClassLoader.getSystemClassLoader().getResourceAsStream(oxmString);

        Map<String, Object> props = new HashMap<String, Object>();
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, oxm);

        // Specify some other, unrelated context path -- we want to ensure that we don't fail
        // due to lack of ObjectFactory/jaxb.index
        JAXBContext ctx = JAXBContext.newInstance("org.eclipse.persistence.testing.oxm.jaxb",
                ClassLoader.getSystemClassLoader(), props);

        Employee e = new Employee();
        e.id = 6;
        e.name = "Jeeves Sobs";
        e.put("tag", "tag-value");

        Document marshalDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Marshaller m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(e, marshalDoc);

        // Make sure OXM was picked up, "tag" property should have been added.
        Employee e2 = (Employee) ctx.createUnmarshaller().unmarshal(marshalDoc);
        assertEquals("OXM file was not processed during context creation.", e.get("tag"), e2.get("tag"));
    }

    public void testCreateContextXmlAnyAttributeSubTypeMap() throws Exception {
        JAXBContextFactory.createContext(new Class[]{XmlAnyAttributeSubTypeMapModel.class}, null);
    }

}