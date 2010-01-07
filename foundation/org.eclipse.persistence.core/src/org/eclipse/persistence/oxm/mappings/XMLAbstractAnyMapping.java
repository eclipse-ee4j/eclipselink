/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
    
    protected Object buildObjectAndWrapInXMLRoot(ClassDescriptor referenceDescriptor, XMLConverter converter, ObjectBuildingQuery query, DOMRecord record, DOMRecord nestedRecord, JoinedAttributeManager joinManager, AbstractSession session, Node next, Object container, ContainerPolicy containerPolicy) {
        Object objectValue = null;
        
        ObjectBuilder builder = referenceDescriptor.getObjectBuilder();
        objectValue = builder.buildObject(query, nestedRecord, joinManager);
        Object updated = ((XMLDescriptor) referenceDescriptor).wrapObjectInXMLRoot(objectValue, next.getNamespaceURI(), next.getLocalName(), next.getPrefix(), false);

        if (converter != null) {
            updated = converter.convertDataValueToObjectValue(updated, session, record.getUnmarshaller());
        }
        
        if (containerPolicy != null) {
            containerPolicy.addInto(updated, container, session);
        }
        
        return updated;
    }
 
    protected Object buildObjectNoReferenceDescriptor(DOMRecord record, AbstractSession session, Node next, Object container, ContainerPolicy cp) {
        XMLConverter converter = ((XMLAnyCollectionMapping) this).getConverter();
        
        XMLPlatformFactory.getInstance().getXMLPlatform().namespaceQualifyFragment((Element) next);
        Object objectValue = next;
        if (converter != null) {
            objectValue = converter.convertDataValueToObjectValue(objectValue, session, record.getUnmarshaller());
        }                       
        cp.addInto(objectValue, container, session);
        
        return objectValue;
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