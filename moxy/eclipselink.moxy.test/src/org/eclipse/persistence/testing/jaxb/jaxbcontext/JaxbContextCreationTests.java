package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.testing.oxm.classloader.JARClassLoader;

public class JaxbContextCreationTests extends junit.framework.TestCase {

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

}