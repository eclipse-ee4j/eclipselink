/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.helpers.DefaultValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.jaxb.JAXBErrorHandler;
import org.eclipse.persistence.jaxb.JAXBUnmarshallerHandler;
import org.eclipse.persistence.jaxb.attachment.AttachmentUnmarshallerAdapter;
import org.eclipse.persistence.internal.jaxb.WrappedValue;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>To Provide an implementation of the JAXB 2.0 Unmarshaller Interface
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide a JAXB wrapper on the XMLUnmarshaller API</li>
 * <li>Perform XML to Object Conversions</li>
 * </ul>
 * <p>This implementation of the JAXB 2.0 Unmarshaller interface provides the required functionality
 * by acting as a thin wrapper on the existing XMLMarshaller API.
 *
 * @author mmacivor
 * @since Oracle TopLink 11.1.1.0.0
 * @see javax.xml.bind.Unmarshaller
 * @see org.eclipse.persistence.oxm.XMLUnmarshaller
 */
public class JAXBUnmarshaller implements Unmarshaller {
    private ValidationEventHandler validationEventHandler;
    private XMLUnmarshaller xmlUnmarshaller;
    public static final String XML_JAVATYPE_ADAPTERS = "xml-javatype-adapters";
    public static final String STAX_SOURCE_CLASS_NAME = "javax.xml.transform.stax.StAXSource";
    private HashMap<Class, QName> generatedClassesToQName;

    public JAXBUnmarshaller(XMLUnmarshaller newXMLUnmarshaller) {
        super();
        validationEventHandler = new DefaultValidationEventHandler();
        xmlUnmarshaller = newXMLUnmarshaller;
        xmlUnmarshaller.setValidationMode(XMLUnmarshaller.NONVALIDATING);
        xmlUnmarshaller.setUnmarshalListener(new JAXBUnmarshalListener(this));
    }

    public XMLUnmarshaller getXMLUnmarshaller() {
        return xmlUnmarshaller;
    }

    public Object unmarshal(File file) throws JAXBException {
        if (file == null) {
            throw new IllegalArgumentException();
        }
        try {
            Object value = xmlUnmarshaller.unmarshal(file);
            if(generatedClassesToQName != null) {
            	QName qname = this.generatedClassesToQName.get(value.getClass());
            	if(qname != null) {
            		Object unwrappedValue = null;
            		if(value instanceof WrappedValue) {
            			unwrappedValue = ((WrappedValue)value).getWrappedValue();
            		}
            		value = new JAXBElement(qname, unwrappedValue.getClass(), unwrappedValue);
            	}
            }
            return value;
        } catch (Exception e) {
            throw new UnmarshalException(e);
        }
    }

    public Object unmarshal(InputStream inputStream) throws JAXBException {
        if (inputStream == null) {
            throw new IllegalArgumentException();
        }
        try {
            Object value = xmlUnmarshaller.unmarshal(inputStream);
            if(generatedClassesToQName != null) {
            	QName qname = this.generatedClassesToQName.get(value.getClass());
            	if(qname != null) {
            		Object unwrappedValue = null;
            		if(value instanceof WrappedValue) {
            			unwrappedValue = ((WrappedValue)value).getWrappedValue();
            		}
            		value = new JAXBElement(qname, unwrappedValue.getClass(), unwrappedValue);
            	}
            }
            return value;
        } catch (Exception e) {
            throw new UnmarshalException(e);
        }
    }

    public Object unmarshal(URL url) throws JAXBException {
        if (url == null) {
            throw new IllegalArgumentException();
        }
        try {
            Object value = xmlUnmarshaller.unmarshal(url);
            if(generatedClassesToQName != null) {
            	QName qname = this.generatedClassesToQName.get(value.getClass());
            	if(qname != null) {
            		Object unwrappedValue = null;
            		if(value instanceof WrappedValue) {
            			unwrappedValue = ((WrappedValue)value).getWrappedValue();
            		}
            		value = new JAXBElement(qname, unwrappedValue.getClass(), unwrappedValue);
            	
            	}
            }
            return value;
        } catch (Exception e) {
            throw new UnmarshalException(e);
        }
    }

    public Object unmarshal(InputSource inputSource) throws JAXBException {
        if (inputSource == null) {
            throw new IllegalArgumentException();
        }
        try {
            Object value = xmlUnmarshaller.unmarshal(inputSource);
            if(generatedClassesToQName != null) {
            	QName qname = this.generatedClassesToQName.get(value.getClass());
            	if(qname != null) {
            		Object unwrappedValue = null;
            		if(value instanceof WrappedValue) {
            			unwrappedValue = ((WrappedValue)value).getWrappedValue();
            		}
            		value = new JAXBElement(qname, unwrappedValue.getClass(), unwrappedValue);
            	}
            }
            return value;
        } catch (Exception e) {
            throw new UnmarshalException(e);
        }
    }

    public Object unmarshal(Reader reader) throws JAXBException {
        if(reader == null) {
            throw new IllegalArgumentException();
        }
        try {
            Object value = xmlUnmarshaller.unmarshal(reader);
            if(generatedClassesToQName != null) {
            	QName qname = this.generatedClassesToQName.get(value.getClass());
            	if(qname != null) {
            		Object unwrappedValue = null;
            		if(value instanceof WrappedValue) {
            			unwrappedValue = ((WrappedValue)value).getWrappedValue();
            		}
            		value = new JAXBElement(qname, unwrappedValue.getClass(), unwrappedValue);
            	}
            }
            return value;
        } catch(Exception ex) {
            throw new JAXBException(ex);
        }
    }
    public Object unmarshal(Node node) throws JAXBException {
        if (node == null) {
            throw new IllegalArgumentException();
        }
        try {
            Object value = xmlUnmarshaller.unmarshal(node);
            if(this.generatedClassesToQName != null) {
            	QName qname = this.generatedClassesToQName.get(value.getClass());
            	if(qname != null) {
            		Object unwrappedValue = null;
            		if(value instanceof WrappedValue) {
            			unwrappedValue = ((WrappedValue)value).getWrappedValue();
            		}
            		value = new JAXBElement(qname, unwrappedValue.getClass(), unwrappedValue);
            	}
            }
            return value;
        } catch (Exception e) {
            throw new UnmarshalException(e);
        }
    }

    /**
     * Create a JAXBElement instance.  If the object is an instance
     * of XMLRoot, we will use its field values to create the 
     * JAXBElement.  If the object is not an XMLRoot instance, we
     * will have to determine the 'name' value.  This will be done 
     * using the object's descriptor default root element - any
     * prefix will be resolved, and a QName created.
     *  
     * @param obj
     * @return
     */
    private JAXBElement buildJAXBElementFromObject(Object obj) {
    	// if an XMLRoot was returned, the root element != the default root 
    	// element of the object being marshalled to - need to create a 
    	// JAXBElement from the returned XMLRoot object
    	if (obj instanceof XMLRoot) {
    		XMLRoot xmlRoot = ((XMLRoot)obj);
    		QName qname = new QName(xmlRoot.getNamespaceURI(), xmlRoot.getLocalName());
    		return new JAXBElement(qname, xmlRoot.getObject().getClass(), xmlRoot.getObject()); 
    	}
    	
    	// at this point, the default root element of the object being marshalled 
    	// to == the root element - here we need to create a JAXBElement 
    	// instance using information from the returned object
    	org.eclipse.persistence.sessions.Session sess = xmlUnmarshaller.getXMLContext().getSession(obj);
    	XMLDescriptor desc = (XMLDescriptor) sess.getClassDescriptor(obj);
    	
		// here we are assuming that if we've gotten this far, there
    	// must be a default root element set on the descriptor.  if
    	// this is incorrect, we need to check for null and throw an
    	// exception
    	String rootName = desc.getDefaultRootElement();
    	if (rootName == null) {
    		// TODO:  we should probably throw an exception at this point
            return new JAXBElement(new QName(""), obj.getClass(), obj);
    	}
        String rootNamespaceUri = null;
        int idx = rootName.indexOf(":");
        if (idx != -1) {
            rootNamespaceUri = desc.getNamespaceResolver().resolveNamespacePrefix(rootName.substring(0, idx)); 
            rootName = rootName.substring(idx + 1);
        }
        
    	QName qname;
        if (rootNamespaceUri == null) {
        	qname = new QName(rootName);
        } else {
        	qname = new QName(rootNamespaceUri, rootName);
        }

        return new JAXBElement(qname, obj.getClass(), obj);
    }

    public JAXBElement unmarshal(Node node, Class javaClass) throws JAXBException {
    	return buildJAXBElementFromObject(xmlUnmarshaller.unmarshal(node, javaClass));
    }

    public Object unmarshal(Source source) throws JAXBException {
        if (source == null) {
            throw new IllegalArgumentException();
        }
        try {
            Object value = xmlUnmarshaller.unmarshal(source);
            if(this.generatedClassesToQName != null) {
            	QName qname = this.generatedClassesToQName.get(value.getClass());
            	if(qname != null) {
            		Object unwrappedValue = null;
            		if(value instanceof WrappedValue) {
            			unwrappedValue = ((WrappedValue)value).getWrappedValue();
            		}
            		value = new JAXBElement(qname, unwrappedValue.getClass(), unwrappedValue);
            	}
            }
            return value;
        } catch (Exception e) {
            throw new UnmarshalException(e);
        }
    }

    public JAXBElement unmarshal(Source source, Class javaClass) throws JAXBException {
    	return buildJAXBElementFromObject(xmlUnmarshaller.unmarshal(source, javaClass));
    }
    
    public JAXBElement unmarshal(XMLStreamReader streamReader, Class javaClass) throws JAXBException {
		Source source = null;
    	try {
			Class staxResult = PrivilegedAccessHelper.getClassForName(STAX_SOURCE_CLASS_NAME);
			Constructor cons = PrivilegedAccessHelper.getDeclaredConstructorFor(staxResult, new Class[]{XMLStreamReader.class}, false);
			source = (Source)PrivilegedAccessHelper.invokeConstructor(cons, new Object[]{streamReader});
		} catch(Exception ex) {
			throw new MarshalException(ex);
		}    	
		return this.unmarshal(source, javaClass);
    }
    
    public Object unmarshal(XMLStreamReader streamReader) throws JAXBException {
		Source source = null;
    	try {
			Class staxResult = PrivilegedAccessHelper.getClassForName(STAX_SOURCE_CLASS_NAME);
			Constructor cons = PrivilegedAccessHelper.getDeclaredConstructorFor(staxResult, new Class[]{XMLStreamReader.class}, false);
			source = (Source)PrivilegedAccessHelper.invokeConstructor(cons, new Object[]{streamReader});
		} catch(Exception ex) {
			throw new MarshalException(ex);
		}    	
		return this.unmarshal(source);
    }
    
    public JAXBElement unmarshal(XMLEventReader eventReader, Class javaClass) throws JAXBException {
		Source source = null;
    	try {
			Class staxResult = PrivilegedAccessHelper.getClassForName(STAX_SOURCE_CLASS_NAME);
			Constructor cons = PrivilegedAccessHelper.getDeclaredConstructorFor(staxResult, new Class[]{XMLEventReader.class}, false);
			source = (Source)PrivilegedAccessHelper.invokeConstructor(cons, new Object[]{eventReader});
		} catch(Exception ex) {
			throw new MarshalException(ex);
		}    	
		return this.unmarshal(source, javaClass);    
    }
    
    public Object unmarshal(XMLEventReader eventReader) throws JAXBException {
		Source source = null;
    	try {
			Class staxResult = PrivilegedAccessHelper.getClassForName(STAX_SOURCE_CLASS_NAME);
			Constructor cons = PrivilegedAccessHelper.getDeclaredConstructorFor(staxResult, new Class[]{XMLEventReader.class}, false);
			source = (Source)PrivilegedAccessHelper.invokeConstructor(cons, new Object[]{eventReader});
		} catch(Exception ex) {
			throw new MarshalException(ex);
		}    	
		return this.unmarshal(source);
    }
    public UnmarshallerHandler getUnmarshallerHandler() {
        return new JAXBUnmarshallerHandler(this);
    }
 
    public void setValidating(boolean validate) throws JAXBException {
        if (validate) {
            xmlUnmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
        } else {
            xmlUnmarshaller.setValidationMode(XMLUnmarshaller.NONVALIDATING);
        }
    }

    public boolean isValidating() throws JAXBException {
        return xmlUnmarshaller.getValidationMode() != XMLUnmarshaller.NONVALIDATING;
    }

    public void setEventHandler(ValidationEventHandler newValidationEventHandler) throws JAXBException {
        if (null == newValidationEventHandler) {
            validationEventHandler = new DefaultValidationEventHandler();
        } else {
            validationEventHandler = newValidationEventHandler;
        }
        xmlUnmarshaller.setErrorHandler(new JAXBErrorHandler(validationEventHandler));
    }

    public ValidationEventHandler getEventHandler() throws JAXBException {
        return validationEventHandler;
    }

 
    public void setProperty(String key, Object value) throws PropertyException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        throw new PropertyException(key, value);
    }

        
    public Object getProperty(String key) throws PropertyException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        throw new PropertyException("Unsupported Property");
    }
    
    public Unmarshaller.Listener getListener() {
        return ((JAXBUnmarshalListener)xmlUnmarshaller.getUnmarshalListener()).getListener();
    }
    
    public void setListener(Unmarshaller.Listener listener) {
        ((JAXBUnmarshalListener)xmlUnmarshaller.getUnmarshalListener()).setListener(listener);
    }
    
    public XmlAdapter getAdapter(Class javaClass) {
        HashMap result = (HashMap) xmlUnmarshaller.getProperty(XML_JAVATYPE_ADAPTERS);
        if (result == null) {
            return null;
        }
        return (XmlAdapter) result.get(javaClass);
    }
    
    public void setAdapter(Class javaClass, XmlAdapter adapter) {
        HashMap result = (HashMap) xmlUnmarshaller.getProperty(XML_JAVATYPE_ADAPTERS);
        if (result == null) {
            result = new HashMap();
            xmlUnmarshaller.getProperties().put(XML_JAVATYPE_ADAPTERS, result);
        }
        result.put(javaClass, adapter);
    }
    
    public void setAdapter(XmlAdapter adapter) {
        setAdapter(adapter.getClass(), adapter);
    }
    
    public void setSchema(Schema schema) {
        this.xmlUnmarshaller.setSchema(schema);
    }
    
    public Schema getSchema() {
        return this.xmlUnmarshaller.getSchema();
    }
    
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return ((AttachmentUnmarshallerAdapter)xmlUnmarshaller.getAttachmentUnmarshaller()).getAttachmentUnmarshaller();
    }
    
    public void setAttachmentUnmarshaller(AttachmentUnmarshaller unmarshaller) {
        xmlUnmarshaller.setAttachmentUnmarshaller(new AttachmentUnmarshallerAdapter(unmarshaller));
    }
    
    public void setUnmarshalCallbacks(java.util.HashMap callbacks) {
        ((JAXBUnmarshalListener)xmlUnmarshaller.getUnmarshalListener()).setClassBasedUnmarshalEvents(callbacks);
    }
    
    public void setGeneratedClassesToQName(HashMap<Class, QName> classesToQName) {
    	this.generatedClassesToQName = classesToQName;
    }
    
}
    
