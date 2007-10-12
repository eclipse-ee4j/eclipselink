/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.oxm;

import java.lang.reflect.Modifier;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class XMLRelationshipMappingNodeValue extends NodeValue {
	// Protected to public
    public void processChild(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts, XMLDescriptor xmlDescriptor) throws SAXException {
        if (xmlDescriptor.hasInheritance()) {
            unmarshalRecord.setAttributes(atts);
            Class classValue = xmlDescriptor.getInheritancePolicy().classFromRow(unmarshalRecord, (org.eclipse.persistence.internal.sessions.AbstractSession)unmarshalRecord.getSession());
            if (classValue == null) {
                // no xsi:type attribute - look for type indicator on the default root element
                QName leafElementType = unmarshalRecord.getLeafElementType();

                // if we have a user-set type, try to get the class from the inheritance policy
                if (leafElementType != null) {
                    Object indicator = xmlDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(leafElementType);

                    // if the inheritance policy does not contain the user-set type, throw an exception
                    if (indicator == null) {
                        throw DescriptorException.missingClassForIndicatorFieldValue(leafElementType, xmlDescriptor.getInheritancePolicy().getDescriptor());
                    }
                    classValue = (Class)indicator;
                }
            }
            if (classValue != null) {
                xmlDescriptor = (XMLDescriptor)unmarshalRecord.getSession().getDescriptor(classValue);
            } else {
                // since there is no xsi:type attribute, use the reference descriptor set 
                // on the mapping -  make sure it is non-abstract
                if (Modifier.isAbstract(xmlDescriptor.getJavaClass().getModifiers())) {
                    // need to throw an exception here
                    throw DescriptorException.missingClassIndicatorField(unmarshalRecord, xmlDescriptor.getInheritancePolicy().getDescriptor());
                }
            }
        }
        TreeObjectBuilder targetObjectBuilder = (TreeObjectBuilder)xmlDescriptor.getObjectBuilder();
        unmarshalRecord.setChildRecord((UnmarshalRecord)targetObjectBuilder.createRecord());
        unmarshalRecord.getChildRecord().setUnmarshaller(unmarshalRecord.getUnmarshaller());
        unmarshalRecord.getChildRecord().startDocument();
        unmarshalRecord.getChildRecord().startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), xPathFragment.getShortName(), atts);
        unmarshalRecord.getXMLReader().setContentHandler(unmarshalRecord.getChildRecord());
        try {
            unmarshalRecord.getXMLReader().setProperty("http://xml.org/sax/properties/lexical-handler", unmarshalRecord.getChildRecord());
        } catch (SAXNotRecognizedException ex) {
        } catch (SAXNotSupportedException ex) {
            //if lexical handling is not supported by this parser, just ignore. 
        }

        unmarshalRecord.getChildRecord().setXMLReader(unmarshalRecord.getXMLReader());
    }

    protected XMLDescriptor findReferenceDescriptor(UnmarshalRecord unmarshalRecord, Attributes atts, DatabaseMapping mapping) {
        XMLDescriptor returnDescriptor = null;
        XMLContext xmlContext = unmarshalRecord.getUnmarshaller().getXMLContext();

        //try xsi:type
        String schemaType = atts.getValue(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
        if ((schemaType != null) && (!schemaType.equals(""))) {
            XPathFragment frag = new XPathFragment();
            frag.setXPath(schemaType);

            if (frag.hasNamespace()) {
                String prefix = frag.getPrefix();
                String url = unmarshalRecord.resolveNamespacePrefix(prefix);
                frag.setNamespaceURI(url);
            }
            returnDescriptor = xmlContext.getDescriptorByGlobalType(frag);
        } else {
            //try leaf element type
            QName leafType = ((XMLField)mapping.getField()).getLastXPathFragment().getLeafElementType();
            if (leafType != null) {
                XPathFragment frag = new XPathFragment();
                String xpath = leafType.getLocalPart();
                String uri = leafType.getNamespaceURI();
                if ((uri != null) && !uri.equals("")) {
                    frag.setNamespaceURI(uri);
                    String prefix = ((XMLDescriptor)mapping.getDescriptor()).getNonNullNamespaceResolver().resolveNamespaceURI(uri);
                    if ((prefix != null) && !prefix.equals("")) {
                        xpath = prefix + ":" + xpath;
                    }
                }
                frag.setXPath(xpath);

                returnDescriptor = xmlContext.getDescriptorByGlobalType(frag);
            }
        }
        if (returnDescriptor == null) {
            throw XMLMarshalException.noDescriptorFound(mapping);
        }
        return returnDescriptor;
    }

    protected void addTypeAttribute(XMLDescriptor descriptor, MarshalRecord marshalRecord, String schemaContext) {
        String typeValue = schemaContext.substring(1);

        String xsiPrefix = null;
        if (descriptor.getNamespaceResolver() != null) {
            xsiPrefix = descriptor.getNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
        } else {
            xsiPrefix = XMLConstants.SCHEMA_INSTANCE_PREFIX;
            marshalRecord.attribute(XMLConstants.XMLNS_URL, XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
        }
        if (xsiPrefix == null) {
            xsiPrefix = descriptor.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_INSTANCE_PREFIX);
            marshalRecord.attribute(XMLConstants.XMLNS_URL, XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
        }
        marshalRecord.attribute(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE, xsiPrefix + ":" + XMLConstants.SCHEMA_TYPE_ATTRIBUTE, typeValue);
    }
    
    protected void writeExtraNamespaces(List extraNamespaces, XMLRecord xmlRecord, AbstractSession session) {
        if (extraNamespaces == null) {
            return;
        }
        for (int i = 0; i < extraNamespaces.size(); i++) {
            Namespace next = (Namespace)extraNamespaces.get(i);            
            ((MarshalRecord)xmlRecord).attribute(XMLConstants.XMLNS_URL, XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + next.getPrefix(), next.getNamespaceURI());            
        }
  
    }
}