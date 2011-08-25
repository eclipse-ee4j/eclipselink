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
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.oxm.MediaType;

/**
 * Test variations of setting/resetting Media Type on the context, marshaller and unmarshaller.
 */
public class JAXBContextMediaTypeTestCases extends TestCase{
    
    public void testCreateContextWithMediaTypeJSON() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        Map props = new HashMap();
        props.put(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/json");        
        JAXBContext ctx = JAXBContextFactory.createContext(classes, props);
        
        JAXBMarshaller m = (JAXBMarshaller)ctx.createMarshaller();        
        assertEquals("application/json", m.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
        JAXBUnmarshaller u = (JAXBUnmarshaller)ctx.createUnmarshaller();
        assertEquals("application/json", u.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
        
        JAXBMarshaller m2 = (JAXBMarshaller)ctx.createMarshaller();
        assertTrue(m2.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE)=="application/json");
        JAXBUnmarshaller u2 = (JAXBUnmarshaller)ctx.createUnmarshaller();
        assertTrue(u2.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE)=="application/json");
        
        m2.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/json");
        assertTrue(m2.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE)=="application/json");
        
        m2.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
        assertTrue(m2.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE)=="application/xml");
                
        assertEquals("application/json", m.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
        assertEquals("application/json", u.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
        assertEquals("application/json", u2.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
        
        JAXBMarshaller m3 = (JAXBMarshaller)ctx.createMarshaller();
        assertTrue(m3.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE)=="application/json");
        JAXBUnmarshaller u3 = (JAXBUnmarshaller)ctx.createUnmarshaller();
        assertTrue(u3.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE)=="application/json");
     
    }
    
    public void testCreateContextWithNoMediaType() throws JAXBException {
    	  Class[] classes = new Class[1];
          classes[0] = Employee.class;
          
          JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
          JAXBMarshaller m = (JAXBMarshaller)ctx.createMarshaller();
          assertEquals("application/xml", m.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
          assertEquals(MediaType.APPLICATION_XML,((JAXBMarshaller)m).getXMLMarshaller().getMediaType());
          JAXBUnmarshaller u = (JAXBUnmarshaller)ctx.createUnmarshaller();
          assertEquals("application/xml", u.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
          assertEquals(MediaType.APPLICATION_XML,((JAXBUnmarshaller)u).getXMLUnmarshaller().getMediaType());       
          
    }
    
    public void testCreateContextWithNullMediaType() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
        Map props = new HashMap();
        props.put(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, null);
        
        JAXBContext ctx = JAXBContextFactory.createContext(classes, null);   
        
        JAXBMarshaller m = (JAXBMarshaller)ctx.createMarshaller();
        assertEquals("application/xml", m.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_XML,((JAXBMarshaller)m).getXMLMarshaller().getMediaType());
        JAXBUnmarshaller u = (JAXBUnmarshaller)ctx.createUnmarshaller();
        assertEquals("application/xml", u.getProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE));
        assertEquals(MediaType.APPLICATION_XML,((JAXBUnmarshaller)u).getXMLUnmarshaller().getMediaType());       
 
    }

    public void testCreateMarshallerSetMediaTypeJSONString() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
                
        JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        Marshaller m = ctx.createMarshaller();
        m.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/json");
        assertEquals(MediaType.APPLICATION_JSON,((JAXBMarshaller)m).getXMLMarshaller().getMediaType());       
    }

    public void testCreateUnmarshallerSetMediaTypeJSONString() throws JAXBException {
        Class[] classes = new Class[1];
        classes[0] = Employee.class;
                
        JAXBContext ctx = JAXBContextFactory.createContext(classes, null);
        Unmarshaller u = ctx.createUnmarshaller();
        u.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/json");
        assertEquals(MediaType.APPLICATION_JSON,((JAXBUnmarshaller)u).getXMLUnmarshaller().getMediaType());       
    }

}
