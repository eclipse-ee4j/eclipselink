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
package org.eclipse.persistence.internal.oxm;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.internal.oxm.mappings.XMLConverterMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.internal.oxm.record.deferred.DescriptorNotFoundContentHandler;
import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.core.queries.CoreAttributeItem;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class XMLRelationshipMappingNodeValue extends MappingNodeValue {

    // Protected to public
    public void processChild(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts, Descriptor xmlDescriptor, Mapping mapping) throws SAXException {
        if(xmlDescriptor == null){
            //Use the DescriptorNotFoundContentHandler to "look ahead" and determine if this is a simple or complex element
            //if it is complex the exception should be thrown
            DescriptorNotFoundContentHandler handler = new DescriptorNotFoundContentHandler(unmarshalRecord, mapping);
            String qnameString = xPathFragment.getLocalName();
            if(xPathFragment.getPrefix() != null) {
                qnameString = xPathFragment.getPrefix()  +Constants.COLON + qnameString;
            }
            handler.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), qnameString, atts);
            XMLReader xmlReader = unmarshalRecord.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.setLexicalHandler(handler);
            return;
        }

        if (xmlDescriptor.hasInheritance()) {
            unmarshalRecord.setAttributes(atts);
            CoreAbstractSession session = unmarshalRecord.getSession();
            Class classValue = ((ObjectBuilder)xmlDescriptor.getObjectBuilder()).classFromRow(unmarshalRecord, session);
            if (classValue == null) {
                // no xsi:type attribute - look for type indicator on the default root element
                XPathQName leafElementType = unmarshalRecord.getLeafElementType();

                // if we have a user-set type, try to get the class from the inheritance policy
                if (leafElementType != null) {
                    Object indicator = xmlDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(leafElementType);
                    if(indicator != null) {
                        classValue = (Class)indicator;
                    }
                }
            }
            if (classValue != null) {
                xmlDescriptor = (Descriptor)session.getDescriptor(classValue);
            } else {
                // since there is no xsi:type attribute, use the reference descriptor set
                // on the mapping -  make sure it is non-abstract
                if (Modifier.isAbstract(xmlDescriptor.getJavaClass().getModifiers())) {
                    // need to throw an exception here
                    throw DescriptorException.missingClassIndicatorField(unmarshalRecord, (org.eclipse.persistence.oxm.XMLDescriptor)xmlDescriptor.getInheritancePolicy().getDescriptor());
                }
            }
        }
        ObjectBuilder targetObjectBuilder = (ObjectBuilder)xmlDescriptor.getObjectBuilder();
        
        CoreAttributeGroup group = unmarshalRecord.getUnmarshalAttributeGroup();
        CoreAttributeGroup nestedGroup = null;
        if(group == XMLRecord.DEFAULT_ATTRIBUTE_GROUP) { 
            nestedGroup = group;
        }
        if(nestedGroup == null) {
            CoreAttributeItem item = group.getItem(getMapping().getAttributeName());
            nestedGroup = item.getGroup(xmlDescriptor.getJavaClass());
            if(nestedGroup == null) {
                if(item.getGroup() == null) {
                    nestedGroup = XMLRecord.DEFAULT_ATTRIBUTE_GROUP;
                } else {
                    nestedGroup = item.getGroup();
                }
            }
        }
        UnmarshalRecord childRecord = unmarshalRecord.getChildUnmarshalRecord(targetObjectBuilder);
        childRecord.setAttributes(atts);
        childRecord.startDocument();
        childRecord.initializeRecord((Mapping) null);
        childRecord.setUnmarshalAttributeGroup(nestedGroup);
        childRecord.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), xPathFragment.getShortName(), atts);

        XMLReader xmlReader = unmarshalRecord.getXMLReader();
        xmlReader.setContentHandler(childRecord);
        xmlReader.setLexicalHandler(childRecord);
    }

    protected Descriptor findReferenceDescriptor(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts, Mapping mapping, UnmarshalKeepAsElementPolicy policy) {
    	Descriptor returnDescriptor = null;
        //try xsi:type
        if(atts != null){
            Context xmlContext = unmarshalRecord.getUnmarshaller().getContext();
            String schemaType = null;
            if(unmarshalRecord.isNamespaceAware()){                
            	schemaType = atts.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE);
            }else{
            	schemaType = atts.getValue(Constants.EMPTY_STRING, Constants.SCHEMA_TYPE_ATTRIBUTE);
            }
            
            
            if(schemaType != null){
                schemaType = schemaType.trim();
                if(schemaType.length() > 0) {                       
                    XPathFragment frag = new XPathFragment(schemaType, unmarshalRecord.getNamespaceSeparator(), unmarshalRecord.isNamespaceAware());
                    
                    QName qname = null;
                    if (frag.hasNamespace()) {
                        String prefix = frag.getPrefix();
                        String url = unmarshalRecord.resolveNamespacePrefix(prefix);
                        frag.setNamespaceURI(url);

                        qname = new QName(url, frag.getLocalName());
                        unmarshalRecord.setTypeQName(qname);
                    } else {
                        String url = unmarshalRecord.resolveNamespacePrefix(Constants.EMPTY_STRING);
                        if(null != url) {
                            frag.setNamespaceURI(url);

                            qname = new QName(url, frag.getLocalName());
                            unmarshalRecord.setTypeQName(qname);
                        }
                        if(!unmarshalRecord.isNamespaceAware()){
                            qname = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI ,frag.getLocalName());
                            unmarshalRecord.setTypeQName(qname);
                        }
                    }
                    returnDescriptor = xmlContext.getDescriptorByGlobalType(frag);
                    if(returnDescriptor == null){
                        if(policy == null || (!policy.isKeepUnknownAsElement() && !policy.isKeepAllAsElement())){
                            Class theClass = (Class)((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(qname);
                            if(theClass == null){
                                throw XMLMarshalException.unknownXsiTypeValue(schemaType, mapping);
                            }
                        }
                    }
               }
            }
        }
        return returnDescriptor;
    }

    protected void addTypeAttribute(Descriptor descriptor, MarshalRecord marshalRecord, String schemaContext) {
        String typeValue = schemaContext.substring(1);

        String xsiPrefix = null;
        if (descriptor.getNamespaceResolver() != null) {
            xsiPrefix = descriptor.getNamespaceResolver().resolveNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        } else {
            xsiPrefix = Constants.SCHEMA_INSTANCE_PREFIX;            
            marshalRecord.namespaceDeclaration(xsiPrefix,  javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

        }
        if (xsiPrefix == null) {
            xsiPrefix = descriptor.getNamespaceResolver().generatePrefix(Constants.SCHEMA_INSTANCE_PREFIX);
            marshalRecord.namespaceDeclaration(xsiPrefix,  javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        }
        marshalRecord.attribute(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE, xsiPrefix + Constants.COLON + Constants.SCHEMA_TYPE_ATTRIBUTE, typeValue);
    }

    protected void writeExtraNamespaces(List extraNamespaces, XMLRecord xmlRecord, CoreAbstractSession session) {
        if (extraNamespaces == null) {
            return;
        }
        for (int i = 0, extraNamespacesSize=extraNamespaces.size(); i < extraNamespacesSize; i++) {
            Namespace next = (Namespace)extraNamespaces.get(i);
            String prefix = next.getPrefix();
            if(((MarshalRecord)xmlRecord).hasCustomNamespaceMapper()) {
                prefix = ((MarshalRecord)xmlRecord).getNamespaceResolver().resolveNamespaceURI(next.getNamespaceURI());
            }
           ((MarshalRecord)xmlRecord).namespaceDeclaration(prefix, next.getNamespaceURI());
        }

    }

    protected void setupHandlerForKeepAsElementPolicy(UnmarshalRecord unmarshalRecord, XPathFragment xPathFragment, Attributes atts) {
        SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
        builder.setOwningRecord(unmarshalRecord);
        try {
            String namespaceURI = Constants.EMPTY_STRING;
            if (xPathFragment.getNamespaceURI() != null) {
                namespaceURI = xPathFragment.getNamespaceURI();
            }
            String qName = xPathFragment.getLocalName();
            if (xPathFragment.getPrefix() != null) {
                qName = xPathFragment.getPrefix() + unmarshalRecord.getNamespaceSeparator() + qName;
            }

            if(!(unmarshalRecord.getPrefixesForFragment().isEmpty())) {
                for(Entry<String, String> next:((Map<String, String>) unmarshalRecord.getPrefixesForFragment()).entrySet()) {
                    builder.startPrefixMapping(next.getKey(), next.getValue());
                }
            }
            builder.startElement(namespaceURI, xPathFragment.getLocalName(), qName, atts);
            XMLReader xmlReader = unmarshalRecord.getXMLReader();
            xmlReader.setContentHandler(builder);
            xmlReader.setLexicalHandler(null);
        } catch (SAXException ex) {
        }
    }

    protected void setOrAddAttributeValueForKeepAsElement(SAXFragmentBuilder builder, Mapping mapping, XMLConverterMapping converter, UnmarshalRecord unmarshalRecord, boolean isCollection, Object collection) {
        Object node = builder.getNodes().remove(builder.getNodes().size() -1);
        if (converter != null) {
            node = converter.convertDataValueToObjectValue(node, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
        }

        if (isCollection) {
            if(collection != null){
                unmarshalRecord.addAttributeValue((ContainerValue) this, node, collection);
            }else{
                unmarshalRecord.addAttributeValue((ContainerValue) this, node);
            }
        } else {
            unmarshalRecord.setAttributeValue(node, mapping);
        }
    }

    protected void endElementProcessText(UnmarshalRecord unmarshalRecord, XMLConverterMapping converter, XPathFragment xPathFragment, Object collection) {
        Object value = unmarshalRecord.getCharacters().toString();

        unmarshalRecord.resetStringBuffer();
        if(!unmarshalRecord.isNil()) {
            QName qname = unmarshalRecord.getTypeQName();
            if (qname == null) {
                if(Constants.EMPTY_STRING.equals(value)) {
                    value = null;
                }
            } else {
                if(qname.equals(Constants.QNAME_QNAME)) {
                    value = ((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).buildQNameFromString((String)value, unmarshalRecord);
                } else {
                	Class theClass = getClassForQName(qname);
                    if (theClass != null) {
                        value = ((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).convertObject(value, theClass, qname);
                    }
                }
            }
            value = converter.convertDataValueToObjectValue(value, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
            setOrAddAttributeValue(unmarshalRecord, value, xPathFragment, collection);
        }
    }

    protected Class getClassForQName(QName qname){
    	CoreField field = getMapping().getField();
    	if(field != null){
    		return ((Field)field).getJavaClass(qname);
    	}
    	return (Class) XMLConversionManager.getDefaultXMLTypes().get(qname);
    }

    
    protected abstract void setOrAddAttributeValue(UnmarshalRecord unmarshalRecord, Object value, XPathFragment xPathFragment, Object collection);
}
