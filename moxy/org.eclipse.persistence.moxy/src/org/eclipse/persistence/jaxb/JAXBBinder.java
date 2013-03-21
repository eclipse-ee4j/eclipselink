/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.persistence.internal.jaxb.WrappedValue;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;

import org.eclipse.persistence.oxm.XMLBinder;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.documentpreservation.RelativePositionOrderingPolicy;

/**
 * INTERNAL
 * <p><b>Purpose:</b> Provide a TopLink implementation of the javax.xml.bind.Binder interface</p>
 * <p><b>Responsibilities:</b>
 * <ul>
 * <li>Provide an implementation of Binder</li>
 * <li>Provide a means to preserve unmapped XML Data</li>
 * </ul></p>
 *
 * @author      mmacivor
 * @since       Oracle TopLink 11.1.1.0.0
 * @see         javax.xml.bind.Binder
 */
public class JAXBBinder extends Binder {

    private XMLContext xmlContext;
    private XMLBinder xmlBinder;

    public JAXBBinder(XMLContext xmlContext) {
        this.xmlContext = xmlContext;
        this.xmlBinder = this.xmlContext.createBinder();
        this.xmlBinder.getDocumentPreservationPolicy().setNodeOrderingPolicy(new RelativePositionOrderingPolicy());
        this.xmlBinder.setErrorHandler(new JAXBErrorHandler(JAXBContext.DEFAULT_VALIDATION_EVENT_HANDER));
    }

    public void marshal(Object obj, Object xmlNode) throws MarshalException {
        if (null == obj || null == xmlNode) {
            throw new IllegalArgumentException();
        }
        
        try {
            if (obj instanceof JAXBElement) {
                JAXBElement jaxbElem = (JAXBElement) obj;
                Root xmlRoot = new Root();
                xmlRoot.setObject(jaxbElem.getValue());
                xmlRoot.setLocalName(jaxbElem.getName().getLocalPart());
                xmlRoot.setNamespaceURI(jaxbElem.getName().getNamespaceURI());
                xmlBinder.marshal(xmlRoot, (Node) xmlNode);
            } else {
                xmlBinder.marshal(obj, (Node) xmlNode);
            }
        } catch (Exception e) {
            throw new MarshalException(e);
        }
    }

    public Object unmarshal(Object obj) throws JAXBException {
        if (null == obj) {
            throw new IllegalArgumentException();
        }

        try {
            Object returnValue = xmlBinder.unmarshal((Node) obj);
            if (returnValue instanceof Root) {
            	Root xmlRoot = (Root) returnValue;
                if(xmlRoot.getObject() instanceof JAXBElement) {
                    return xmlRoot.getObject();
                }
                return new JAXBElement(new QName(xmlRoot.getNamespaceURI(), xmlRoot.getLocalName()), xmlRoot.getObject().getClass(), xmlRoot.getObject());
            } else {
                return returnValue;
            }
        } catch (Exception e) {
            throw new UnmarshalException(e);
        }
    }

    public JAXBElement unmarshal(Object obj, Class javaClass) throws JAXBException  {
        if (null == obj || null == javaClass) {
            throw new IllegalArgumentException();
        }

        try {
        	Root xmlRoot = (Root) xmlBinder.unmarshal((Node) obj, javaClass);
            return new JAXBElement(new QName(xmlRoot.getNamespaceURI(), xmlRoot.getLocalName()), javaClass, xmlRoot.getObject());
        } catch (Exception e) {
            throw new UnmarshalException(e);
        }
    }

    public Object getXMLNode(Object obj) {
        if (null == obj) {
            throw new IllegalArgumentException();
        }
        if(obj instanceof JAXBElement && !(obj instanceof WrappedValue)) {
            return xmlBinder.getXMLNode(((JAXBElement)obj).getValue());
        }
        
        return xmlBinder.getXMLNode(obj);
    }

    public Object updateXML(Object obj) {
        return updateXML(obj, getXMLNode(obj));
    }

    public Object updateXML(Object obj, Object xmlNode) {
        if (null == obj || null == xmlNode) {
            throw new IllegalArgumentException();
        }

        if (obj instanceof JAXBElement && !(obj instanceof WrappedValue)) {
            obj = ((JAXBElement) obj).getValue();
        }
        xmlBinder.updateXML(obj, ((Element) xmlNode));
        return xmlNode;
    }

    public Object getJAXBNode(Object obj) {
        if (null == obj) {
            throw new IllegalArgumentException();
        }

        return xmlBinder.getObject((Node) obj);
    }

    public Object updateJAXB(Object obj) throws JAXBException {
        if (null == obj) {
            throw new IllegalArgumentException();
        }

        try {
            xmlBinder.updateObject((Node) obj);
            Object updatedObj = xmlBinder.getObject((Node) obj);
            
            boolean shouldWrapInJAXBElement = true;
            
            Descriptor desc = (Descriptor) xmlBinder.getMarshaller().getXMLContext().getSession().getClassDescriptor(updatedObj);
            
            if (desc == null) {
                return updatedObj;
            }
            
            String objRootElem = desc.getDefaultRootElement();

            if (!desc.isResultAlwaysXMLRoot()) {
                if (objRootElem != null) {
                    String rootElemNS = objRootElem.substring(0, objRootElem.lastIndexOf(":"));
                    String rootElemName = objRootElem.substring(objRootElem.lastIndexOf(":") + 1);
                    String resolvedNS = desc.getNamespaceResolver().resolveNamespacePrefix(rootElemNS);
                    
                    String nodeName = ((Node) obj).getLocalName();
                    String nodeNS = ((Node) obj).getNamespaceURI();
                    
                    if (rootElemName.equals(nodeName) && resolvedNS.equals(nodeNS)) {
                        shouldWrapInJAXBElement = false;
                    }
                }
            }

            if (!shouldWrapInJAXBElement) {
                return updatedObj;
            } else {
                QName qname = new QName(((Node) obj).getNamespaceURI(), ((Node) obj).getLocalName());
                return new JAXBElement(qname, updatedObj.getClass(), updatedObj);
            }
        } catch (Exception e) {
            throw new JAXBException(e);
        }
    }

    // ============
    
    public void setSchema(Schema schema) {
        this.xmlBinder.setSchema(schema);
    }
    
    public Schema getSchema() {
        return this.xmlBinder.getSchema();
    }

    public void setEventHandler(ValidationEventHandler newValidationEventHandler) throws JAXBException {
        if (null == newValidationEventHandler) {            
            xmlBinder.setErrorHandler(new JAXBErrorHandler(JAXBContext.DEFAULT_VALIDATION_EVENT_HANDER));
        } else {            
            xmlBinder.setErrorHandler(new JAXBErrorHandler(newValidationEventHandler));
        }
    }
    
    public ValidationEventHandler getEventHandler() {
        JAXBErrorHandler jaxbErrorHandler = (JAXBErrorHandler) xmlBinder.getErrorHandler();
        return jaxbErrorHandler.getValidationEventHandler();
    }

    public Object getProperty(String propName) throws PropertyException {
        if (null == propName) {
            throw new IllegalArgumentException();
        }

        if (propName.equals(Marshaller.JAXB_ENCODING)) {
            return this.xmlBinder.getMarshaller().getEncoding();
        }
        if (propName.equals(Marshaller.JAXB_FORMATTED_OUTPUT)) {
            return this.xmlBinder.getMarshaller().isFormattedOutput();
        }
        if (propName.equals(Marshaller.JAXB_FRAGMENT)) {
            return this.xmlBinder.getMarshaller().isFragment();
        }
        if (propName.equals(Marshaller.JAXB_SCHEMA_LOCATION)) {
            return this.xmlBinder.getMarshaller().getSchemaLocation();
        }
        if (propName.equals(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION)) {
            return this.xmlBinder.getMarshaller().getNoNamespaceSchemaLocation();
        }

        throw new PropertyException(propName);
    }
    
    public void setProperty(String propName, Object value) throws PropertyException {
        if (null == propName) {
            throw new IllegalArgumentException(propName);
        }

        String valueString = (value == null) ? null : value.toString();

        if (propName.equals(Marshaller.JAXB_ENCODING)) {
            this.xmlBinder.getMarshaller().setEncoding(valueString);
            return;
        }
        if (propName.equals(Marshaller.JAXB_FORMATTED_OUTPUT)) {
            this.xmlBinder.getMarshaller().setFormattedOutput(Boolean.valueOf(valueString).booleanValue());
            return;
        }
        if (propName.equals(Marshaller.JAXB_FRAGMENT)) {
            this.xmlBinder.getMarshaller().setFragment(Boolean.valueOf(valueString).booleanValue());
            return;
        }
        if (propName.equals(Marshaller.JAXB_SCHEMA_LOCATION)) {
            this.xmlBinder.getMarshaller().setSchemaLocation(valueString);
            return;
        }
        if (propName.equals(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION)) {
            this.xmlBinder.getMarshaller().setNoNamespaceSchemaLocation(valueString);
            return;
        }

        throw new PropertyException(propName);
    }
    
    public XMLBinder getXMLBinder() {
        return xmlBinder;
    }
}