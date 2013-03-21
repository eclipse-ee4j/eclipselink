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
package org.eclipse.persistence.oxm.record;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecord;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecordImpl;
import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * PUBLIC:
 * Provides a Record/Map API on an XML DOM element.
 */
public abstract class XMLRecord extends AbstractRecord implements AbstractMarshalRecord<AbstractSession, DatabaseField, XMLMarshaller, NamespaceResolver>, AbstractUnmarshalRecord<AbstractSession, DatabaseField, XMLUnmarshaller> {
    protected XMLUnmarshaller unmarshaller;
    private DocumentPreservationPolicy docPresPolicy;
    protected Object currentObject;
    protected AbstractSession session;
    
    protected boolean hasCustomNamespaceMapper;
    protected boolean equalNamespaceResolvers = false;
    
    private AbstractMarshalRecord<AbstractSession, DatabaseField, XMLMarshaller, NamespaceResolver> abstractMarshalRecord;

    /**
     * INTERNAL:
     * Nil: This is used to indicate that this field represents xsi:nil="true"
     */
    public static final XMLRecord.Nil NIL = org.eclipse.persistence.internal.oxm.record.XMLRecord.NIL;

    public XMLRecord() {
        super(null, null, 0);
        abstractMarshalRecord = new AbstractMarshalRecordImpl(this);
        // Required for subclasses.
    }

    /**
     * PUBLIC:
     * Add the field-value pair to the row.
     */
    @Override
    public Object get(String key) {
        return get(new XMLField(key));
    }

    /**
     * PUBLIC:
     * Add the field-value pair to the row.
     */
    @Override
    public Object put(String key, Object value) {
        return put(new XMLField(key), value);
    }
    
    
    /**
     * Marshal an attribute for the give namespaceURI, localName, preifx and value
     * @param namespaceURI
     * @param localName
     * @param prefix     
     * @param value
     */
    public void attributeWithoutQName(String namespaceURI, String localName, String prefix, String value){
        String qualifiedName = localName;
        if(prefix != null && prefix.length() >0){
            qualifiedName = prefix + getNamespaceSeparator() + qualifiedName;
        }
        attribute(namespaceURI, localName, qualifiedName, value);
    }
       
    /**
     * Marshal an attribute for the give namespaceURI, localName, qualifiedName and value
     * @param namespaceURI
     * @param localName
     * @param qName     
     * @param value
     */
    public void attribute(String namespaceURI, String localName, String qName, String value){
        XMLField xmlField = new XMLField(XMLConstants.ATTRIBUTE +qName);
        xmlField.setNamespaceResolver(getNamespaceResolver());
        xmlField.getLastXPathFragment().setNamespaceURI(namespaceURI);
        add(xmlField, value);
    }
    
    /**
     * Marshal a namespace declaration for the given prefix and url
     * @param prefix
     * @param url
     */
    public void namespaceDeclaration(String prefix, String namespaceURI){
        
        String existingPrefix = getNamespaceResolver().resolveNamespaceURI(namespaceURI);
        if(existingPrefix == null || (existingPrefix != null && !existingPrefix.equals(XMLConstants.EMPTY_STRING) && !existingPrefix.equals(prefix))){        
            XMLField xmlField = new XMLField("@" + javax.xml.XMLConstants.XMLNS_ATTRIBUTE + XMLConstants.COLON + prefix);
            xmlField.setNamespaceResolver(getNamespaceResolver());
            xmlField.getXPathFragment().setNamespaceURI(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
            add(xmlField, namespaceURI);
        }
    }
    
    /**
     * PUBLIC:
     * Get the local name of the context root element.
     */
    public abstract String getLocalName();

    /**
     * PUBLIC:
     *  Get the namespace URI for the context root element.
     */
    public abstract String getNamespaceURI();

    /**
     * PUBLIC:
     * Clear the sub-nodes of the DOM.
     */
    public abstract void clear();

    /**
     * PUBLIC:
     * Return the document.
     */
    public abstract Document getDocument();

    /**
     * PUBLIC:
     * Check if the value is contained in the row.
     */
    public boolean contains(Object value) {
        return values().contains(value);
    }

    /**
    * PUBLIC:
    * Return the DOM.
    */
    public abstract Node getDOM();

    /**
     * Return the XML string representation of the DOM.
     */
    public abstract String transformToXML();

    /**
     * INTERNAL:
     * Convert a DatabaseField to an XMLField
     */
    protected XMLField convertToXMLField(DatabaseField databaseField) {
        try {
            return (XMLField)databaseField;
        } catch (ClassCastException ex) {
            return new XMLField(databaseField.getName());
        }
    }
    
    protected List<XMLField> convertToXMLField(List<DatabaseField> databaseFields) {
        ArrayList<XMLField> xmlFields = new ArrayList(databaseFields.size());
        for(DatabaseField next:databaseFields) {
            try {
                xmlFields.add((XMLField)next);
            } catch(ClassCastException ex) {
                xmlFields.add(new XMLField(next.getName()));
            }
        }
        return xmlFields;
    }

    /**
     * INTERNAL:
     * Retrieve the value for the field. If missing null is returned.
     */
    public Object get(DatabaseField key) {
        return getIndicatingNoEntry(key);
    }
    /**
     * INTERNAL:
     * Retrieve the value for the field name.
     */
    public Object getIndicatingNoEntry(String fieldName) {
        return getIndicatingNoEntry(new XMLField(fieldName));
    }

    @Override
    public String resolveNamespacePrefix(String prefix) {
        return abstractMarshalRecord.resolveNamespacePrefix(prefix);
    }

    /**
     * INTERNAL:
     */
    @Override
    public XMLMarshaller getMarshaller() {
        return abstractMarshalRecord.getMarshaller();
    }

    /**
     * INTERNAL:
     */
    @Override
    public void setMarshaller(XMLMarshaller marshaller) {
        abstractMarshalRecord.setMarshaller(marshaller);

    }

    /**
     * INTERNAL:
     */
    public XMLUnmarshaller getUnmarshaller() {
        return unmarshaller;
    }

    /**
     * INTERNAL:
     */
    public void setUnmarshaller(XMLUnmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public void setDocPresPolicy(DocumentPreservationPolicy policy) {
        this.docPresPolicy = policy;
    }
    
    public DocumentPreservationPolicy getDocPresPolicy() {
        return docPresPolicy;
    }
    /**
     * INTERNAL:
     */
    @Override
    public Object getOwningObject() {
        return abstractMarshalRecord.getOwningObject();
    }

    /**
     * INTERNAL:
     */
    @Override
    public void setOwningObject(Object owningObject) {
        abstractMarshalRecord.setOwningObject(owningObject);
    }

    /**
     * INTERNAL:
     */
    public Object getCurrentObject() {
        return currentObject;
    }

    /**
     * INTERNAL:
     */
    public void setCurrentObject(Object obj) {
        this.currentObject = obj;
    }
    /**
     * INTERNAL:
     */
    @Override
    public XPathQName getLeafElementType() {
        return abstractMarshalRecord.getLeafElementType();
    }
    /**
     * INTERNAL:
     */
    @Override
    public void setLeafElementType(XPathQName leafElementType) {
        abstractMarshalRecord.setLeafElementType(leafElementType);
    }

    /**
     * INTERNAL:
     */
    @Override
    public void setLeafElementType(QName leafElementType) {
        abstractMarshalRecord.setLeafElementType(leafElementType);
    }
    
    public void setNamespaceResolver(NamespaceResolver nr) {
        abstractMarshalRecord.setNamespaceResolver(nr);
    }

    public NamespaceResolver getNamespaceResolver() {
        return abstractMarshalRecord.getNamespaceResolver();
    }

    public AbstractSession getSession() {
        return session;
    }

    public void setSession(AbstractSession session) {
        this.session = session;
    }

    public void setEqualNamespaceResolvers(boolean equalNRs) {
        this.equalNamespaceResolvers = equalNRs;
    }

    public boolean hasEqualNamespaceResolvers() {
        return equalNamespaceResolvers;
    }

    @Override
    public boolean isXOPPackage() {
        return abstractMarshalRecord.isXOPPackage();
    }

    @Override
    public void setXOPPackage(boolean isXOPPackage) {
        abstractMarshalRecord.setXOPPackage(isXOPPackage);
    }
    
    /**
     * INTERNAL:
     * Determine if namespaces will be considered during marshal/unmarshal operations.
     * @since 2.4
     */
    @Override
    public boolean isNamespaceAware() {
    	return abstractMarshalRecord.isNamespaceAware();
    }
    
    /**
     * INTERNAL:
	 * The character used to separate the prefix and uri portions when namespaces are present 
     * @since 2.4
     */    
    public char getNamespaceSeparator(){
    	return XMLConstants.COLON;
    }
	
    public boolean hasCustomNamespaceMapper() {
    	return hasCustomNamespaceMapper;    	
    }

    public void setCustomNamespaceMapper(boolean customNamespaceMapper) {
        this.hasCustomNamespaceMapper = customNamespaceMapper;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    @Override
    public List<Namespace> addExtraNamespacesToNamespaceResolver(Descriptor descriptor, CoreAbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers) {
        return abstractMarshalRecord.addExtraNamespacesToNamespaceResolver(descriptor, session, allowOverride, ignoreEqualResolvers);
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    @Override
    public boolean addXsiTypeAndClassIndicatorIfRequired(Descriptor descriptor, Descriptor referenceDescriptor, Field xmlField, boolean isRootElement) {
        return abstractMarshalRecord.addXsiTypeAndClassIndicatorIfRequired(descriptor, referenceDescriptor, xmlField, isRootElement);
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    @Override
   public boolean addXsiTypeAndClassIndicatorIfRequired(Descriptor descriptor, Descriptor referenceDescriptor, Field xmlField,
           Object originalObject, Object obj, boolean wasXMLRoot, boolean isRootElement) {
       return abstractMarshalRecord.addXsiTypeAndClassIndicatorIfRequired(descriptor, referenceDescriptor, xmlField, originalObject, obj, wasXMLRoot, isRootElement);
   }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    @Override
    public void removeExtraNamespacesFromNamespaceResolver(List<Namespace> extraNamespaces, CoreAbstractSession session) {
        abstractMarshalRecord.removeExtraNamespacesFromNamespaceResolver(extraNamespaces, session);
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    @Override
    public void writeXsiTypeAttribute(Descriptor descriptor, String typeUri,  String  typeLocal, String typePrefix, boolean addToNamespaceResolver) {
        abstractMarshalRecord.writeXsiTypeAttribute(descriptor, typeUri, typeLocal, typePrefix, addToNamespaceResolver);
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    @Override
    public void writeXsiTypeAttribute(Descriptor xmlDescriptor, XMLSchemaReference xmlRef, boolean addToNamespaceResolver) {
        abstractMarshalRecord.writeXsiTypeAttribute(xmlDescriptor, xmlRef, addToNamespaceResolver);
    }

}
