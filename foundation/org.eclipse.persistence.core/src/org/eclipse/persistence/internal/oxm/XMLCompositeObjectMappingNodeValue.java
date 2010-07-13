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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import java.lang.reflect.Modifier;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.deferred.CompositeObjectMappingContentHandler;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshalListener;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.sessions.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Composite Object Mapping is handled
 * when used with the TreeObjectBuilder.</p>
 */
public class XMLCompositeObjectMappingNodeValue extends XMLRelationshipMappingNodeValue implements NullCapableValue {
    private XMLCompositeObjectMapping xmlCompositeObjectMapping;

    public XMLCompositeObjectMappingNodeValue(XMLCompositeObjectMapping xmlCompositeObjectMapping) {
        super();
        this.xmlCompositeObjectMapping = xmlCompositeObjectMapping;
    }

    /**
     * Marshal any 'self' mapped attributes.
     *
     * @param xPathFragment
     * @param marshalRecord
     * @param object
     * @param session
     * @param namespaceResolver
     * @param marshaller
     * @return
     */
    public boolean marshalSelfAttributes(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, XMLMarshaller marshaller) {
        Object objectValue = xmlCompositeObjectMapping.getAttributeValueFromObject(object);
        if (xmlCompositeObjectMapping.getConverter() != null) {
            Converter converter = xmlCompositeObjectMapping.getConverter();
            if (converter instanceof XMLConverter) {
                objectValue = ((XMLConverter)converter).convertObjectValueToDataValue(objectValue, session, marshaller);
            } else {
                objectValue = converter.convertObjectValueToDataValue(objectValue, session);
            }
        }
        XMLDescriptor descriptor = (XMLDescriptor)session.getDescriptor(objectValue);
        if(descriptor != null){
            TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();
            return objectBuilder.marshalAttributes(marshalRecord, objectValue, session);
        }
        return false;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver) {
        return marshal(xPathFragment, marshalRecord, object, session, namespaceResolver, ObjectMarshalContext.getInstance());
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xmlCompositeObjectMapping.isReadOnly()) {
            return false;
        }
        Object objectValue = marshalContext.getAttributeValue(object, xmlCompositeObjectMapping);
        return this.marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, marshalContext);
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object objectValue, AbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        if (xPathFragment.hasLeafElementType()) {
            marshalRecord.setLeafElementType(xPathFragment.getLeafElementType());
        }

        XMLMarshaller marshaller = marshalRecord.getMarshaller();
        Converter converter = xmlCompositeObjectMapping.getConverter();
        if (null != converter) {
            if (converter instanceof XMLConverter) {
                objectValue = ((XMLConverter)converter).convertObjectValueToDataValue(objectValue, session, marshaller);
            } else {
                objectValue = converter.convertObjectValueToDataValue(objectValue, session);
            }
        }
        if (null == objectValue) {
            return xmlCompositeObjectMapping.getNullPolicy().compositeObjectMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        }

        XMLMarshalListener marshalListener = null;
        if (null != marshaller && null != (marshalListener = marshaller.getMarshalListener())) {
            marshalListener.beforeMarshal(objectValue);
        }

        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        boolean isSelfFragment = xPathFragment.isSelfFragment;
        marshalRecord.closeStartGroupingElements(groupingFragment);

        UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeObjectMapping.getKeepAsElementPolicy();
        if (((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) && objectValue instanceof Node) {
            if(isSelfFragment){
                NodeList children = ((org.w3c.dom.Element) objectValue).getChildNodes();
                for(int i =0, size=children.getLength(); i<size ; i++){
                    Node next = children.item(i);
                    if(next.getNodeType() == Node.ELEMENT_NODE){
                        marshalRecord.node(next, marshalRecord.getNamespaceResolver());
                        return true;
                    }
                }
            }else{
                marshalRecord.node((Node) objectValue, marshalRecord.getNamespaceResolver());
                return true;
            }
        }
        XMLDescriptor descriptor = (XMLDescriptor)xmlCompositeObjectMapping.getReferenceDescriptor();
        if(descriptor == null || descriptor.hasInheritance()){
            descriptor = (XMLDescriptor)session.getDescriptor(objectValue.getClass());
        }

        if(descriptor != null){
            TreeObjectBuilder objectBuilder = (TreeObjectBuilder)descriptor.getObjectBuilder();

            if (!isSelfFragment) {
                xPathNode.startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, objectBuilder, objectValue);
            }

            List extraNamespaces = objectBuilder.addExtraNamespacesToNamespaceResolver(descriptor, marshalRecord, session);
            writeExtraNamespaces(extraNamespaces, marshalRecord, session);
            if(!isSelfFragment) {
                objectBuilder.addXsiTypeAndClassIndicatorIfRequired(marshalRecord, descriptor, (XMLDescriptor) xmlCompositeObjectMapping.getReferenceDescriptor(), (XMLField)xmlCompositeObjectMapping.getField(), false);
            }

            objectBuilder.buildRow(marshalRecord, objectValue, session, marshaller, xPathFragment, WriteType.UNDEFINED);

            if (!isSelfFragment) {
                marshalRecord.endElement(xPathFragment, namespaceResolver);
            }
            objectBuilder.removeExtraNamespacesFromNamespaceResolver(marshalRecord, extraNamespaces, session);
        } else {
            if (!isSelfFragment) {
                xPathNode.startElement(marshalRecord, xPathFragment, object, session, namespaceResolver, null, objectValue);
            }

            QName schemaType = getSchemaType((XMLField) xmlCompositeObjectMapping.getField(), objectValue, session);
            String stringValue = getValueToWrite(schemaType, objectValue, (XMLConversionManager) session.getDatasourcePlatform().getConversionManager(), namespaceResolver);
            updateNamespaces(schemaType, marshalRecord,((XMLField)xmlCompositeObjectMapping.getField()));
            marshalRecord.characters(stringValue);

            if (!isSelfFragment) {
                marshalRecord.endElement(xPathFragment, namespaceResolver);
            }
        }
        if (null != marshalListener) {
            marshalListener.afterMarshal(objectValue);
        }
        return true;
    }

    public boolean startElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            unmarshalRecord.removeNullCapableValue(this);

            XMLDescriptor xmlDescriptor = (XMLDescriptor)xmlCompositeObjectMapping.getReferenceDescriptor();
            if (null == xmlDescriptor) {
                xmlDescriptor = findReferenceDescriptor(xPathFragment, unmarshalRecord, atts, xmlCompositeObjectMapping,xmlCompositeObjectMapping.getKeepAsElementPolicy());

                if(xmlDescriptor == null){
                    if(xmlCompositeObjectMapping.getField() != null){
                        //try leaf element type
                        QName leafType = ((XMLField)xmlCompositeObjectMapping.getField()).getLastXPathFragment().getLeafElementType();
                        if (leafType != null) {
                            XPathFragment frag = new XPathFragment();
                            String xpath = leafType.getLocalPart();
                            String uri = leafType.getNamespaceURI();
                            if (uri != null && uri.length() > 0) {
                                frag.setNamespaceURI(uri);
                                String prefix = ((XMLDescriptor)xmlCompositeObjectMapping.getDescriptor()).getNonNullNamespaceResolver().resolveNamespaceURI(uri);
                                if (prefix != null && prefix.length() > 0) {
                                    xpath = prefix + XMLConstants.COLON + xpath;
                                }
                            }
                            frag.setXPath(xpath);
                            XMLContext xmlContext = unmarshalRecord.getUnmarshaller().getXMLContext();
                            xmlDescriptor = xmlContext.getDescriptorByGlobalType(frag);
                        }
                    }
                }

                UnmarshalKeepAsElementPolicy policy = xmlCompositeObjectMapping.getKeepAsElementPolicy();
                if (((xmlDescriptor == null) && (policy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT)) || (policy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)) {
                    if(unmarshalRecord.getTypeQName() != null){
                        Class theClass = (Class)((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(unmarshalRecord.getTypeQName());
                        if(theClass == null){
                            setupHandlerForKeepAsElementPolicy(unmarshalRecord, xPathFragment, atts);
                            return true;
                        }
                    }else{
                        setupHandlerForKeepAsElementPolicy(unmarshalRecord, xPathFragment, atts);
                        return true;
                    }
                }
            }

            //
            //  Null Composite Objects are marshalled in 2 ways when the input XML node is empty.
             // (1) as null
             //     - isNullRepresentedByEmptyNode = true
            //  (2) as empty object
            //      - isNullRepresentedByEmptyNode = false
            //   A deferred contentHandler is used to queue events until we are able to determine
            //   whether we are in one of empty/simple/complex state.
            //   Control is returned to the UnmarshalHandler after creation of (1) or (2) above is started.
            //   Object creation was deferred to the DeferredContentHandler
            //
            // Check if we need to create the DeferredContentHandler based on policy state
            if(xmlCompositeObjectMapping.getNullPolicy().isNullRepresentedByEmptyNode() || xmlCompositeObjectMapping.getNullPolicy().isNullRepresentedByXsiNil()) {
                String qnameString = xPathFragment.getLocalName();
                if(xPathFragment.getPrefix() != null) {
                    qnameString = xPathFragment.getPrefix()  + XMLConstants.COLON + qnameString;
                }
                if(null != xmlDescriptor) {
                    // Process null capable value
                    CompositeObjectMappingContentHandler aHandler = new CompositeObjectMappingContentHandler(//
                        unmarshalRecord, this, xmlCompositeObjectMapping, atts, xPathFragment, xmlDescriptor);
                    // Send control to the handler
                    aHandler.startElement(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), qnameString, atts);
                    XMLReader xmlReader = unmarshalRecord.getXMLReader();
                    xmlReader.setContentHandler(aHandler);
                    xmlReader.setLexicalHandler(aHandler);
                }
            } else {
                boolean isNull = xmlCompositeObjectMapping.getNullPolicy().valueIsNull(atts);
                if (isNull) {
                    xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), null);
                } else {
                    XMLField xmlFld = (XMLField)this.xmlCompositeObjectMapping.getField();
                    if (xmlFld.hasLastXPathFragment()) {
                        unmarshalRecord.setLeafElementType(xmlFld.getLastXPathFragment().getLeafElementType());
                    }
                    processChild(xPathFragment, unmarshalRecord, atts, xmlDescriptor, xmlCompositeObjectMapping);
                }
            }

        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
        return true;
    }

    public void endElement(XPathFragment xPathFragment, UnmarshalRecord unmarshalRecord) {

        if (null == unmarshalRecord.getChildRecord()) {
            SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
            UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeObjectMapping.getKeepAsElementPolicy();

            if ((((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT))) && (builder.getNodes().size() != 0)) {

                if(unmarshalRecord.getTypeQName() != null){
                    Class theClass = (Class)((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(unmarshalRecord.getTypeQName());
                    if(theClass != null){
                        //handle simple text
                        endElementProcessText(unmarshalRecord, xmlCompositeObjectMapping.getConverter(), xPathFragment, null);
                        return;
                    }
                }

                if (builder.getDocument() != null) {
                    setOrAddAttributeValueForKeepAsElement(builder, (XMLMapping) xmlCompositeObjectMapping, (XMLConverter) xmlCompositeObjectMapping.getConverter(), unmarshalRecord, false, null);
                    return;
                }
            }else{
                //handle simple text
                endElementProcessText(unmarshalRecord, xmlCompositeObjectMapping.getConverter(), xPathFragment, null);
                return;
            }

        } else {
            Object object = unmarshalRecord.getChildRecord().getCurrentObject();
            if (xmlCompositeObjectMapping.getConverter() != null) {
                Converter converter = xmlCompositeObjectMapping.getConverter();
                if (converter instanceof XMLConverter) {
                    object = ((XMLConverter)converter).convertDataValueToObjectValue(object, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
                } else {
                    object = converter.convertDataValueToObjectValue(object, unmarshalRecord.getSession());
                }
            }
            // Set the child object on the parent
            unmarshalRecord.setAttributeValue(object, xmlCompositeObjectMapping);
            XMLInverseReferenceMapping inverseReferenceMapping = xmlCompositeObjectMapping.getInverseReferenceMapping();
            if(null != inverseReferenceMapping) {
                if(inverseReferenceMapping.getContainerPolicy() == null) {
                    inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(object, unmarshalRecord.getCurrentObject());
                } else {
                    Object backpointerContainer = inverseReferenceMapping.getAttributeAccessor().getAttributeValueFromObject(object);
                    if(backpointerContainer == null) {
                        backpointerContainer = inverseReferenceMapping.getContainerPolicy().containerInstance();
                        inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(object, backpointerContainer);
                    }
                    inverseReferenceMapping.getContainerPolicy().addInto(unmarshalRecord.getCurrentObject(), backpointerContainer, unmarshalRecord.getSession());
                }
            }
            unmarshalRecord.setChildRecord(null);
        }
    }

    public void endSelfNodeValue(UnmarshalRecord unmarshalRecord, UnmarshalRecord selfRecord, Attributes attributes) {
        if(xmlCompositeObjectMapping.getNullPolicy().valueIsNull(attributes)){
            xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), null);
            return;
        }

        if (unmarshalRecord.getFragmentBuilder().getDocument() != null) {
            UnmarshalKeepAsElementPolicy keepAsElementPolicy = xmlCompositeObjectMapping.getKeepAsElementPolicy();

            SAXFragmentBuilder builder = unmarshalRecord.getFragmentBuilder();
            if ((((keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT) || (keepAsElementPolicy == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT)))&& (builder.getNodes().size() != 0) ) {
                if(unmarshalRecord.getTypeQName() != null){
                    Class theClass = (Class)((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).getDefaultXMLTypes().get(unmarshalRecord.getTypeQName());
                    if(theClass != null){
                        //handle simple text
                        endElementProcessText(unmarshalRecord, xmlCompositeObjectMapping.getConverter(), null, null);
                        return;
                    }
                }
                Element element = (Element) builder.getNodes().remove(builder.getNodes().size() -1);

                String xsiType = null;
                if(null != element) {
                    xsiType = element.getAttributeNS(XMLConstants.SCHEMA_INSTANCE_URL, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
                }
                if(null != xsiType) {
                    xsiType = xsiType.trim();
                    Object value = element;
                    String namespace = null;
                    int colonIndex = xsiType.indexOf(XMLConstants.COLON);
                    if (colonIndex > -1) {
                        String prefix = xsiType.substring(0, colonIndex);
                        namespace = unmarshalRecord.resolveNamespacePrefix(prefix);
                        if(null == namespace) {
                            namespace = XMLPlatformFactory.getInstance().getXMLPlatform().resolveNamespacePrefix(element, prefix);
                        }
                        String name = xsiType.substring(colonIndex + 1);
                        QName qName = new QName(namespace, xsiType.substring(colonIndex + 1));
                        Class theClass = (Class) XMLConversionManager.getDefaultXMLTypes().get(qName);
                        if (theClass != null) {
                            value = ((XMLConversionManager) unmarshalRecord.getSession().getDatasourcePlatform().getConversionManager()).convertObject(element.getTextContent(), theClass, qName);
                        }
                    }
                    xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), value);
                } else {
                    xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), element);
                }
            }
        } else {
            Object valueToSet = selfRecord.getCurrentObject();
            Converter converter = xmlCompositeObjectMapping.getConverter();
            if (null != converter) {
                if (converter instanceof XMLConverter) {
                    valueToSet = ((XMLConverter)converter).convertDataValueToObjectValue(valueToSet, unmarshalRecord.getSession(), unmarshalRecord.getUnmarshaller());
                } else {
                    valueToSet = converter.convertDataValueToObjectValue(valueToSet, unmarshalRecord.getSession());
                }
            }

            xmlCompositeObjectMapping.setAttributeValueInObject(unmarshalRecord.getCurrentObject(), valueToSet);
            XMLInverseReferenceMapping inverseReferenceMapping = xmlCompositeObjectMapping.getInverseReferenceMapping();
            if (null != inverseReferenceMapping) {
                inverseReferenceMapping.getAttributeAccessor().setAttributeValueInObject(unmarshalRecord.getCurrentObject(), valueToSet);
            }
        }
    }

    public UnmarshalRecord buildSelfRecord(UnmarshalRecord unmarshalRecord, Attributes atts) {
        try {
            XMLDescriptor xmlDescriptor = (XMLDescriptor)xmlCompositeObjectMapping.getReferenceDescriptor();
            if (null == xmlDescriptor) {
                xmlDescriptor = findReferenceDescriptor(null, unmarshalRecord, atts, xmlCompositeObjectMapping,xmlCompositeObjectMapping.getKeepAsElementPolicy());
            }

            if(xmlDescriptor != null){
	            if (xmlDescriptor.hasInheritance()) {
	                unmarshalRecord.setAttributes(atts);
	                Class clazz = xmlDescriptor.getInheritancePolicy().classFromRow(unmarshalRecord, unmarshalRecord.getSession());
	                if (clazz == null) {
	                    // no xsi:type attribute - look for type indicator on the default root element
	                    QName leafElementType = unmarshalRecord.getLeafElementType();

	                    // if we have a user-set type, try to get the class from the inheritance policy
	                    if (leafElementType != null) {
	                        Object indicator = xmlDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(leafElementType);
	                        if(indicator != null) {
	                            clazz = (Class)indicator;
	                        }
	                    }
	                }

	                if (clazz != null) {
	                    xmlDescriptor = (XMLDescriptor)unmarshalRecord.getSession().getDescriptor(clazz);
	                } else {
	                    // since there is no xsi:type attribute, use the reference descriptor set
	                    // on the mapping -  make sure it is non-abstract
	                    if (Modifier.isAbstract(xmlDescriptor.getJavaClass().getModifiers())) {
	                        // need to throw an exception here
	                        throw DescriptorException.missingClassIndicatorField(unmarshalRecord, xmlDescriptor.getInheritancePolicy().getDescriptor());
	                    }
	                }
	            }
	            TreeObjectBuilder stob2 = (TreeObjectBuilder)xmlDescriptor.getObjectBuilder();
	            UnmarshalRecord childRecord = (UnmarshalRecord)stob2.createRecord(unmarshalRecord.getSession());
	            childRecord.setUnmarshaller(unmarshalRecord.getUnmarshaller());
	            childRecord.setSelfRecord(true);
	            unmarshalRecord.setChildRecord(childRecord);
	            childRecord.setXMLReader(unmarshalRecord.getXMLReader());
	            childRecord.startDocument();
	            childRecord.initializeRecord(this.xmlCompositeObjectMapping);

	            return childRecord;
            } else{
            	return null;
            }

        } catch (SAXException e) {
            throw XMLMarshalException.unmarshalException(e);
        }
    }

    public void setNullValue(Object object, Session session) {
        xmlCompositeObjectMapping.setAttributeValueInObject(object, null);
    }

    public boolean isNullCapableValue() {
        XMLField xmlField = (XMLField)xmlCompositeObjectMapping.getField();
        if (xmlField.getLastXPathFragment().isSelfFragment) {
            return false;
        }
        return xmlCompositeObjectMapping.getNullPolicy().getIsSetPerformedForAbsentNode();
    }

    public XMLCompositeObjectMapping getMapping() {
        return xmlCompositeObjectMapping;
    }

    protected void setOrAddAttributeValue(UnmarshalRecord unmarshalRecord, Object value, XPathFragment xPathFragment, Object collection){
        unmarshalRecord.setAttributeValue(value, xmlCompositeObjectMapping);
    }
}