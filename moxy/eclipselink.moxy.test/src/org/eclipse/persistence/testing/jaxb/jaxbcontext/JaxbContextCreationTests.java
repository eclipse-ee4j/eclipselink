package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.net.URL;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.exceptions.SessionLoaderException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
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

}
