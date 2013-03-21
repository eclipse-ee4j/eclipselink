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
package org.eclipse.persistence.internal.oxm.record;

import java.lang.reflect.Modifier;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.queries.CoreAttributeGroup;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Context;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.oxm.unmapped.UnmappedContentHandler;
import org.eclipse.persistence.platform.xml.SAXDocumentBuilder;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.internal.oxm.record.XMLReader;
import org.eclipse.persistence.internal.oxm.record.namespaces.StackUnmarshalNamespaceResolver;
import org.eclipse.persistence.internal.oxm.record.namespaces.UnmarshalNamespaceResolver;

/**
 * INTERNAL:
 * <p><b>Purpose:</b>An implementation of ContentHandler used to handle the root element of an
 * XML Document during unmarshal.
 * <p><b>Responsibilities:</b><ul>
 * <li>Implement ContentHandler interface</li>
 * <li>Handle startElement event for the root-level element of an xml document</li>
 * <li>Handle inheritance, and descriptor lookup to determine the correct class associated with
 * this XML Element.</li>
 * </ul>
 *
 * @author bdoughan
 *
 */
public class SAXUnmarshallerHandler implements ExtendedContentHandler {
    private XMLReader xmlReader;
    private Context xmlContext;
    private UnmarshalRecord rootRecord;
    private Object object;
    private Descriptor descriptor;
    private Unmarshaller unmarshaller;
    private CoreAbstractSession session;
    private UnmarshalNamespaceResolver unmarshalNamespaceResolver;
    private UnmarshalKeepAsElementPolicy keepAsElementPolicy = new UnmarshalKeepAsElementPolicy() {

        @Override
        public boolean isKeepAllAsElement() {
            return false;
        }

        @Override
        public boolean isKeepNoneAsElement() {
            return true;
        }

        @Override
        public boolean isKeepUnknownAsElement() {
            return false;
        }

    };
    private SAXDocumentBuilder documentBuilder;
    private Locator2 locator;
    private boolean isNil;
    
    public SAXUnmarshallerHandler(Context xmlContext) {
        super();
        this.xmlContext = xmlContext;
        unmarshalNamespaceResolver = new StackUnmarshalNamespaceResolver();
    }

    public XMLReader getXMLReader() {
        return this.xmlReader;
    }

    public void setXMLReader(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    public Object getObject() {
        if(object == null) {
            if(this.descriptor != null) {
                object = this.descriptor.wrapObjectInXMLRoot(this.rootRecord, this.unmarshaller.isResultAlwaysXMLRoot());
            } else if(documentBuilder != null) {
                Node node = documentBuilder.getDocument().getDocumentElement();
                Root root = unmarshaller.createRoot();
                root.setLocalName(node.getLocalName());
                root.setNamespaceURI(node.getNamespaceURI());
                root.setObject(node);
                object = root;
            } else {
                if(rootRecord != null) {
                    object = this.rootRecord.getCurrentObject();
                }
            }
        }
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setDocumentLocator(Locator locator) {
        if (locator instanceof Locator2) {
            this.locator = (Locator2)locator;
            if(xmlReader != null){
        	    xmlReader.setLocator(locator);
            }
        }        
    }

    public UnmarshalNamespaceResolver getUnmarshalNamespaceResolver() {
        return this.unmarshalNamespaceResolver;
    }

    public void setUnmarshalNamespaceResolver(UnmarshalNamespaceResolver unmarshalNamespaceResolver) {
        this.unmarshalNamespaceResolver = unmarshalNamespaceResolver;
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        unmarshalNamespaceResolver.push(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        unmarshalNamespaceResolver.pop(prefix);
    }

    /**
     * INTERNAL:
     *
     * Resolve any mapping references.
     */
    public void resolveReferences() {
        if(null != rootRecord) {
            rootRecord.resolveReferences(session, unmarshaller.getIDResolver());
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        try {           
        	Descriptor xmlDescriptor = null;
            boolean isPrimitiveType = false;
            Class primitiveWrapperClass = null;
            String type = null;
            if(xmlReader.isNamespaceAware()){
                type = atts.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE);
            }else{
            	type = atts.getValue(Constants.EMPTY_STRING, Constants.SCHEMA_TYPE_ATTRIBUTE);
            }
            if (null != type) {
                XPathFragment typeFragment = new XPathFragment(type, xmlReader.namespaceSeparator, xmlReader.isNamespaceAware());
                // set the prefix using a reverse key lookup by uri value on namespaceMap 
                if (xmlReader.isNamespaceAware() && null != unmarshalNamespaceResolver) {
                    typeFragment.setNamespaceURI(unmarshalNamespaceResolver.getNamespaceURI(typeFragment.getPrefix()));
                }
                xmlDescriptor = xmlContext.getDescriptorByGlobalType(typeFragment);
                if(xmlDescriptor == null) {
                	QName lookupQName = null;
                	if(typeFragment.getNamespaceURI() == null){
                		lookupQName= new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, typeFragment.getLocalName());
                	}else{
                		lookupQName= new QName(typeFragment.getNamespaceURI(), typeFragment.getLocalName());
                	}
                    //check to see if type attribute represents simple type
                    primitiveWrapperClass = (Class)XMLConversionManager.getDefaultXMLTypes().get(lookupQName);
                }
            }
            
            if(xmlDescriptor == null){
            	String name;
                if (localName == null || localName.length() == 0) {
                    name = qName;
                } else {
                    name = localName;
                }

                XPathQName rootQName;
                if (namespaceURI == null || namespaceURI.length() == 0) {
                    rootQName = new XPathQName(name, xmlReader.isNamespaceAware() );
                } else {
                    rootQName = new XPathQName(namespaceURI, name, xmlReader.isNamespaceAware() );
                }
            	
            	xmlDescriptor = xmlContext.getDescriptor(rootQName);
            	
                if (null == xmlDescriptor) {
                    //check for a cached object and look for descriptor by class
                    Object obj = this.xmlReader.getCurrentObject(session, null);
                    if (obj != null) {
                        xmlDescriptor = (Descriptor)xmlContext.getSession(obj.getClass()).getDescriptor(obj.getClass());
                    }
                    if(xmlDescriptor == null) {
                        isPrimitiveType = primitiveWrapperClass != null;
                    }
                }
                if (null == xmlDescriptor && !isPrimitiveType) {
                    if(!this.keepAsElementPolicy.isKeepNoneAsElement()) {
                        this.documentBuilder = new SAXDocumentBuilder();
                        documentBuilder.startDocument();
                        //start any prefixes that have already been started
                        for(String prefix:this.unmarshalNamespaceResolver.getPrefixes()) {
                            documentBuilder.startPrefixMapping(prefix, this.unmarshalNamespaceResolver.getNamespaceURI(prefix));
                        }
                        documentBuilder.startElement(namespaceURI, localName, qName, atts);
                        this.xmlReader.setContentHandler(documentBuilder);
                        return;
                    }
                    Class unmappedContentHandlerClass = unmarshaller.getUnmappedContentHandlerClass();
                    if (null == unmappedContentHandlerClass) {
                        throw XMLMarshalException.noDescriptorWithMatchingRootElement(rootQName.toString());
                    } else {
                        UnmappedContentHandler unmappedContentHandler;

                        try {
                            PrivilegedNewInstanceFromClass privilegedNewInstanceFromClass = new PrivilegedNewInstanceFromClass(unmappedContentHandlerClass);
                            unmappedContentHandler = (UnmappedContentHandler)privilegedNewInstanceFromClass.run();
                        } catch (ClassCastException e) {
                            throw XMLMarshalException.unmappedContentHandlerDoesntImplement(e, unmappedContentHandlerClass.getName());
                        } catch (IllegalAccessException e) {
                            throw XMLMarshalException.errorInstantiatingUnmappedContentHandler(e, unmappedContentHandlerClass.getName());
                        } catch (InstantiationException e) {
                            throw XMLMarshalException.errorInstantiatingUnmappedContentHandler(e, unmappedContentHandlerClass.getName());
                        }
                        
                        UnmappedContentHandlerWrapper unmappedContentHandlerWrapper = new UnmappedContentHandlerWrapper(unmappedContentHandler, this);

                        unmappedContentHandler.startElement(namespaceURI, localName, qName, atts);
                        xmlReader.setContentHandler(unmappedContentHandler);

                        setObject(unmappedContentHandlerWrapper.getCurrentObject());
                        return;

                    }
                }
            }
            
            // for XMLObjectReferenceMappings we need a non-shared cache, so
            // try and get a Unit Of Work from the XMLContext
            session = xmlContext.getSession(xmlDescriptor);
            
            UnmarshalRecord unmarshalRecord;
            if (isPrimitiveType) {
                unmarshalRecord = unmarshaller.createRootUnmarshalRecord(primitiveWrapperClass);
                unmarshalRecord.setSession((CoreAbstractSession) unmarshaller.getContext().getSession());
                unmarshalRecord.setXMLReader(this.getXMLReader());
            } else if (xmlDescriptor.hasInheritance()) {
                unmarshalRecord = new UnmarshalRecordImpl(null);
                unmarshalRecord.setUnmarshaller(unmarshaller);
                unmarshalRecord.setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);
                unmarshalRecord.setXMLReader(this.getXMLReader());
                unmarshalRecord.setAttributes(atts);
                
                Class classValue = xmlDescriptor.getInheritancePolicy().classFromRow(new org.eclipse.persistence.oxm.record.UnmarshalRecord(unmarshalRecord), (CoreAbstractSession) session);
                
                if (classValue == null) {
                    // no xsi:type attribute - look for type indicator on the default root element
                    QName leafElementType = xmlDescriptor.getDefaultRootElementType();

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
                    // since there is no xsi:type attribute, we'll use the descriptor
                    // that was retrieved based on the rootQName -  we need to make 
                    // sure it is non-abstract
                    if (Modifier.isAbstract(xmlDescriptor.getJavaClass().getModifiers())) {
                        // need to throw an exception here
                        throw DescriptorException.missingClassIndicatorField((XMLRecord) unmarshalRecord, (org.eclipse.persistence.oxm.XMLDescriptor)xmlDescriptor.getInheritancePolicy().getDescriptor());
                    }
                }
                org.eclipse.persistence.oxm.record.UnmarshalRecord wrapper = (org.eclipse.persistence.oxm.record.UnmarshalRecord)xmlDescriptor.getObjectBuilder().createRecord((CoreAbstractSession) session);
                unmarshalRecord = wrapper.getUnmarshalRecord();
            } else {
                org.eclipse.persistence.oxm.record.UnmarshalRecord wrapper = (org.eclipse.persistence.oxm.record.UnmarshalRecord) xmlDescriptor.getObjectBuilder().createRecord((CoreAbstractSession) session);
                unmarshalRecord = wrapper.getUnmarshalRecord();
                unmarshalRecord.setXMLReader(this.getXMLReader());
            }
            this.descriptor = xmlDescriptor;
            this.rootRecord = unmarshalRecord;
            
            unmarshalRecord.setUnmarshaller(this.unmarshaller);
            unmarshalRecord.setXMLReader(this.getXMLReader());

            if (locator != null) {
                unmarshalRecord.setDocumentLocator(xmlReader.getLocator());
            }
            unmarshalRecord.setAttributes(atts);

            boolean hasNilAttribute = (atts != null && null != atts.getValue(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_NIL_ATTRIBUTE));                       
            unmarshalRecord.setNil(isNil || hasNilAttribute);
          
            unmarshalRecord.setUnmarshalNamespaceResolver(unmarshalNamespaceResolver);
            
            unmarshalRecord.startDocument();
            unmarshalRecord.initializeRecord((Mapping) null);
            xmlReader.setContentHandler(unmarshalRecord);
            xmlReader.setLexicalHandler(unmarshalRecord);
            
            Object attributeGroup = this.unmarshaller.getUnmarshalAttributeGroup();
            if(attributeGroup != null) {
                if(attributeGroup.getClass() == ClassConstants.STRING) {
                    CoreAttributeGroup group = descriptor.getAttributeGroup((String)attributeGroup);
                    if(group != null) {
                        unmarshalRecord.setUnmarshalAttributeGroup(group);
                    } else {
                        //Error
                    }
                } else if(attributeGroup instanceof CoreAttributeGroup) {
                    unmarshalRecord.setUnmarshalAttributeGroup((CoreAttributeGroup)attributeGroup);
                } else {
                    //Error case
                }
            }

            unmarshalRecord.startElement(namespaceURI, localName, qName, atts);

            // if we located the descriptor via xsi:type attribute, create and 
            // return an XMLRoot object 
        } catch (EclipseLinkException e) {
            if (null == xmlReader.getErrorHandler()) {
                throw e;
            } else {
                SAXParseException saxParseException = new SAXParseException(null, null, null, 0, 0, e);
                xmlReader.getErrorHandler().error(saxParseException);
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    public void characters(CharSequence characters) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public Unmarshaller getUnmarshaller() {
        return this.unmarshaller;
    }
    
    public void setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy policy) {
        this.keepAsElementPolicy = policy;
    }
    
    public UnmarshalKeepAsElementPolicy getKeepAsElementPolicy() {
        return this.keepAsElementPolicy;
    }

	@Override
	public void setNil(boolean isNil) {
		this.isNil = isNil;
		
	}

}