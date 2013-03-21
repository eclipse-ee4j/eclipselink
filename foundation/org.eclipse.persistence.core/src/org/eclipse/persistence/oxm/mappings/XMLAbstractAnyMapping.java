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
 *     rbarkhouse - 2009-04-14 - 2.0 - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.oxm.mappings;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * <p><b>Purpose:</b> An abstract superclass for XMLAnyObjectMapping and XMLAnyCollectionMapping.
 * Maps an attribute of an object to an xs:any construct in the schema. 
 * 
 *  @see XMLAnyObjectMapping
 *  @see XMLAnyCollectionMapping
 *
 */
public abstract class XMLAbstractAnyMapping extends DatabaseMapping {

    private UnmarshalKeepAsElementPolicy keepAsElementPolicy;  
    private boolean isWriteOnly;
    
    public UnmarshalKeepAsElementPolicy getKeepAsElementPolicy() {
        return keepAsElementPolicy;
    }

    public void setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy keepAsElementPolicy) {
        this.keepAsElementPolicy = keepAsElementPolicy;
    }
    
    protected XMLDescriptor getDescriptor(XMLRecord xmlRecord, AbstractSession session, QName rootQName) throws XMLMarshalException {
        if (rootQName == null) {
            rootQName = new QName(xmlRecord.getNamespaceURI(), xmlRecord.getLocalName());
        }
        XMLContext xmlContext = xmlRecord.getUnmarshaller().getXMLContext();
        XMLDescriptor xmlDescriptor = xmlContext.getDescriptor(rootQName);
        if (null == xmlDescriptor) {
            if (!(getKeepAsElementPolicy() == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT || getKeepAsElementPolicy() == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) {
                throw XMLMarshalException.noDescriptorWithMatchingRootElement(xmlRecord.getLocalName());
            }
        }
        return xmlDescriptor;
    }
    
    protected Object buildObjectForNonXMLRoot(ClassDescriptor referenceDescriptor, XMLConverter converter, ObjectBuildingQuery query, DOMRecord record, DOMRecord nestedRecord, JoinedAttributeManager joinManager, AbstractSession session, Node next, Object container, ContainerPolicy containerPolicy) {
        Object objectValue = null;
        
        if ((referenceDescriptor != null) && (getKeepAsElementPolicy() != UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) {
            ObjectBuilder builder = referenceDescriptor.getObjectBuilder();
            objectValue = builder.buildObject(query, nestedRecord, joinManager);
            if (converter != null) {
                objectValue = converter.convertDataValueToObjectValue(objectValue, session, record.getUnmarshaller());
            }                       
            if (containerPolicy != null) {
                containerPolicy.addInto(objectValue, container, session);
            }
        } else {
            if ((getKeepAsElementPolicy() == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (getKeepAsElementPolicy() == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) {
                XMLPlatformFactory.getInstance().getXMLPlatform().namespaceQualifyFragment((Element) next);
                objectValue = next;
                if (converter != null) {
                    objectValue = converter.convertDataValueToObjectValue(objectValue, session, record.getUnmarshaller());
                }
                if (containerPolicy != null) {
                    containerPolicy.addInto(objectValue, container, session);
                }
            }
        }
        
        return objectValue;
    }
    
    /**
     * Uses a given reference descriptor to build an object based on a given DOMRecord.  
     * If a converter is provided it is applied to the newly built object.  The 
     * reference descriptor will wrap the object in an XMLRoot if required, and the 
     * object will be added to the given Container Policy if it is non-null.   
     */
    protected Object buildObjectAndWrapInXMLRoot(ClassDescriptor referenceDescriptor, XMLConverter converter, ObjectBuildingQuery query, DOMRecord record, DOMRecord nestedRecord, JoinedAttributeManager joinManager, AbstractSession session, Node next, Object container, ContainerPolicy containerPolicy) {
        ObjectBuilder builder = referenceDescriptor.getObjectBuilder();
        Object objectValue = builder.buildObject(query, nestedRecord, joinManager);
        if (converter != null) {
            objectValue = converter.convertDataValueToObjectValue(objectValue, session, record.getUnmarshaller());
        }
        Object updated = ((XMLDescriptor) referenceDescriptor).wrapObjectInXMLRoot(objectValue, next.getNamespaceURI(), next.getLocalName(), next.getPrefix(), false, record.isNamespaceAware(), record.getUnmarshaller());
        if (containerPolicy != null) {
            containerPolicy.addInto(updated, container, session);
        }
        return updated;
    }
 
    /**
     * Convenience method that takes a given Node and applies namespace 
     * information, converts it if necessary, and adds the resulting 
     * object to the given ContainerPolicy if non-null. 
     */
    protected Object buildObjectNoReferenceDescriptor(DOMRecord record, XMLConverter converter, AbstractSession session, Node next, Object container, ContainerPolicy cp) {
        XMLPlatformFactory.getInstance().getXMLPlatform().namespaceQualifyFragment((Element) next);
        Object objectValue = next;
        if (converter != null) {
            objectValue = converter.convertDataValueToObjectValue(objectValue, session, record.getUnmarshaller());
        }
        if (cp != null) {
            cp.addInto(objectValue, container, session);
        }
        return objectValue;
    }
    
    /**
     * Convenience method that takes a given node and checks the first child for 
     * TEXT_NODE.  If the first child is a text node containing a non-empty 
     * String, the String value will be processed and wrapped in an XMLRoot.  
     * If schemaTypeQName represents a default XML type (boolean, dateTime, etc)
     * the String value will be converted to that type.  If a converter is 
     * provided it will be applied before wrapping in an XMLRoot. 
     */
    protected XMLRoot buildXMLRootForText(Node node, QName schemaTypeQName, XMLConverter converter, AbstractSession session, DOMRecord record) {
        XMLRoot rootValue = null;
        Node textchild = ((Element) node).getFirstChild();
        if ((textchild != null) && (textchild.getNodeType() == Node.TEXT_NODE)) {
            String stringValue = ((Text) textchild).getNodeValue();
            if ((stringValue != null) && stringValue.length() > 0) {
                Object convertedValue = stringValue;
                if (schemaTypeQName != null) {
                    Class theClass = (Class) XMLConversionManager.getDefaultXMLTypes().get(schemaTypeQName);
                    if (theClass != null) {
                        convertedValue = ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(convertedValue, theClass, schemaTypeQName);
                    }
                }
                if (converter != null) {
                    convertedValue = converter.convertDataValueToObjectValue(convertedValue, session, record.getUnmarshaller());
                }
                rootValue = new XMLRoot();
                rootValue.setLocalName(node.getLocalName());
                rootValue.setSchemaType(schemaTypeQName);
                rootValue.setNamespaceURI(node.getNamespaceURI());
                rootValue.setObject(convertedValue);
            }
        }
        return rootValue;
    }
    
    /**
     * Convenience method that builds an XMLRoot wrapping a given object.
     * The local name and uri are set using the given Node.
     */
    protected XMLRoot buildXMLRoot(Node node, Object object) {
        XMLRoot xmlRoot = new XMLRoot();
        xmlRoot.setLocalName(node.getLocalName());
        xmlRoot.setNamespaceURI(node.getNamespaceURI());
        xmlRoot.setObject(object);
        return xmlRoot;
    }
    
    public boolean isWriteOnly() {
        return this.isWriteOnly;
    }
    
    public void setIsWriteOnly(boolean b) {
        this.isWriteOnly = b;
    }
    
    public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        if(isWriteOnly()) {
            return;
        }
        super.setAttributeValueInObject(object, value);
    }
    
    public void preInitialize(AbstractSession session) throws DescriptorException {
        getAttributeAccessor().setIsWriteOnly(this.isWriteOnly());
        getAttributeAccessor().setIsReadOnly(this.isReadOnly());
        super.preInitialize(session);
    }
}