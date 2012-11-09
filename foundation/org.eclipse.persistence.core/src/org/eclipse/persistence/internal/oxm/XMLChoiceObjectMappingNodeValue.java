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
package org.eclipse.persistence.internal.oxm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalContext;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.XMLRoot;

import org.xml.sax.Attributes;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Choice Collection Mapping is 
 * handled when used with the TreeObjectBuilder.</p> 
 * @author mmacivor
 */
public class XMLChoiceObjectMappingNodeValue extends NodeValue implements NullCapableValue {
    private NodeValue choiceElementNodeValue;
    private Map<Class, NodeValue> choiceElementNodeValues;
    private XMLChoiceObjectMapping xmlChoiceMapping;
    //The first node value of the choice will be registered as a null capable value. If any
    //of the choice elements get hit, this needs to be removed as a null value.
    private XMLChoiceObjectMappingNodeValue nullCapableNodeValue;
    private XMLField xmlField;
    
    public XMLChoiceObjectMappingNodeValue(XMLChoiceObjectMapping mapping, XMLField xmlField) {
        this.xmlChoiceMapping = mapping;
        this.xmlField = xmlField;
        initializeNodeValue();
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return choiceElementNodeValue.isOwningNode(xPathFragment);
    }
    
    public void initializeNodeValue() {
        XMLMapping xmlMapping = xmlChoiceMapping.getChoiceElementMappings().get(xmlField);
        choiceElementNodeValue = getNodeValueForMapping(xmlMapping);
        //check for mappings to other classes with the same field
        for(Entry<Class, XMLMapping> entry:xmlChoiceMapping.getChoiceElementMappingsByClass().entrySet()) {
            XMLField field = xmlChoiceMapping.getClassToFieldMappings().get(entry.getKey());
            if(field != null && field.equals(this.xmlField)) {
                XMLMapping mappingForClass = entry.getValue();
                if(mappingForClass != xmlMapping) {
                    if(this.choiceElementNodeValues == null) {
                        choiceElementNodeValues = new HashMap<Class, NodeValue>();
                    }
                    choiceElementNodeValues.put(entry.getKey(), getNodeValueForMapping(mappingForClass));
                }
            }
        }
    }
    
    private NodeValue getNodeValueForMapping(XMLMapping xmlMapping) {
        if(xmlMapping instanceof XMLBinaryDataMapping){
            return new XMLBinaryDataMappingNodeValue((XMLBinaryDataMapping)xmlMapping);
        } else if(xmlMapping instanceof XMLDirectMapping) {
            return new XMLDirectMappingNodeValue((XMLDirectMapping)xmlMapping);
        } else if(xmlMapping instanceof XMLObjectReferenceMapping) {
            return new XMLObjectReferenceMappingNodeValue((XMLObjectReferenceMapping)xmlMapping, xmlField);
        } else {
            return new XMLCompositeObjectMappingNodeValue((XMLCompositeObjectMapping)xmlMapping);
        }
    }
    public void setNullCapableNodeValue(XMLChoiceObjectMappingNodeValue nodeValue) {
        this.nullCapableNodeValue = nodeValue;
    }
    
    public void setNullValue(Object object, CoreSession session) {
        xmlChoiceMapping.setAttributeValueInObject(object, null);
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        return this.marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if(xmlChoiceMapping.isReadOnly()) {
            return false;
        }
        Object value = xmlChoiceMapping.getFieldValue(object, (AbstractSession) session, marshalRecord);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, value, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        Class valueClass = null;
        if (value instanceof XMLRoot) {
            XMLRoot root = (XMLRoot)value;
            for(CoreField next:this.xmlChoiceMapping.getFields()) {
                XPathFragment fragment = ((XMLField)next).getXPathFragment();
                while(fragment != null && !fragment.nameIsText) {
                    if(fragment.getNextFragment() == null || fragment.getHasText()) {
                        if(fragment.getLocalName().equals(root.getLocalName())) {
                            String fragUri = fragment.getNamespaceURI();
                            String namespaceUri = root.getNamespaceURI();
                            if((namespaceUri == null && fragUri == null) || (namespaceUri != null && fragUri != null && namespaceUri.equals(fragUri))) {
                                if(next == this.xmlField) {
                                    return this.choiceElementNodeValue.marshalSingleValue(xPathFragment, marshalRecord, object, value, session, namespaceResolver, marshalContext);
                                } else {
                                    //If this root is associated with another field, then return and let that NodeValue handle it
                                    return false;
                                }
                            }
                        }
                    }
                    fragment = fragment.getNextFragment();
                }
            }
            valueClass = root.getObject().getClass();
        } 
        if (value != null) {
            if(valueClass == null) {
                valueClass = value.getClass();
            }
            XMLField fieldForClass = null;
            Class theClass = valueClass;
            while(theClass != null) {
                fieldForClass = xmlChoiceMapping.getClassToFieldMappings().get(valueClass);
                if(fieldForClass != null) {
                    break;
                }
                theClass = theClass.getSuperclass();
            }
            if (fieldForClass != null && fieldForClass.equals(this.xmlField)) {
                if(this.choiceElementNodeValues != null) {
                    NodeValue nodeValue = this.choiceElementNodeValues.get(theClass);
                    if(nodeValue != null) {
                        return nodeValue.marshalSingleValue(xPathFragment, marshalRecord, object, value, session, namespaceResolver, marshalContext);
                    }
                }
                return this.choiceElementNodeValue.marshalSingleValue(xPathFragment, marshalRecord, object, value, session, namespaceResolver, marshalContext);
            }
            List<XMLField> sourceFields = null;
            theClass = valueClass;
            while(theClass != null) {
                sourceFields = xmlChoiceMapping.getClassToSourceFieldsMappings().get(theClass);
                if(sourceFields != null) {
                    break;
                }
                theClass = theClass.getSuperclass();
            }
            if (sourceFields != null && sourceFields.contains(this.xmlField)) {
                return this.choiceElementNodeValue.marshalSingleValue(xPathFragment, marshalRecord, object, value, session, namespaceResolver, marshalContext);
            }
        }
        return false;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {
        unmarshalRecord.removeNullCapableValue(this.nullCapableNodeValue);
        if(null != xmlChoiceMapping.getConverter()) {
            UnmarshalContext unmarshalContext = unmarshalRecord.getUnmarshalContext();
            unmarshalRecord.setUnmarshalContext(new ChoiceUnmarshalContext(unmarshalContext, xmlChoiceMapping));
            this.choiceElementNodeValue.endElement(xPathFragment, unmarshalRecord);
            unmarshalRecord.setUnmarshalContext(unmarshalContext);
        } else {
            this.choiceElementNodeValue.endElement(xPathFragment, unmarshalRecord);
        }
    }
    
    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        unmarshalRecord.removeNullCapableValue(this.nullCapableNodeValue);
        return this.choiceElementNodeValue.startElement(xPathFragment, unmarshalRecord, atts);
    }
    
    public void setXPathNode(XPathNode xPathNode) {
        super.setXPathNode(xPathNode);
        this.choiceElementNodeValue.setXPathNode(xPathNode);
        if(this.choiceElementNodeValues != null) {
            for(NodeValue next:choiceElementNodeValues.values()) {
                next.setXPathNode(xPathNode);
            }
        }
    }    

    /**
     * The underlying choice element node value will handle attributes.
     * 
     */
    public void attribute(UnmarshalRecord unmarshalRecord, String URI, String localName, String value) {
        unmarshalRecord.removeNullCapableValue(this.nullCapableNodeValue);
        this.choiceElementNodeValue.attribute(unmarshalRecord, URI, localName, value);
    }
}