/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * PUBLIC:
 * Provides a Record/Map API on an XML DOM element.
 */
public abstract class XMLRecord extends AbstractRecord {
    protected XMLMarshaller marshaller;
    protected XMLUnmarshaller unmarshaller;
    private DocumentPreservationPolicy docPresPolicy;
    private Object owningObject;
    protected Object currentObject;
    private XPathQName leafElementType;
    protected NamespaceResolver namespaceResolver;
    protected AbstractSession session;
    private boolean isXOPPackage;
    protected char namespaceSeparator;
    protected boolean namespaceAware;
    
    /**
     * INTERNAL:
     * Nil: This is used to indicate that this field represents xsi:nil="true"
     */
    public static final XMLRecord.Nil NIL = new XMLRecord.Nil();

    /**
     * INTERNAL:
     * Nil: This is used to indicate that this field represents xsi:nil="true"
     */
    private static class Nil {
        private Nil() {
        }
    }

    public XMLRecord() {
        super(null, null);
        namespaceResolver = new NamespaceResolver();
        namespaceSeparator = XMLConstants.COLON;
        namespaceAware = true;
        // Required for subclasses.
    }
    
    /**
     * Return true is this record can support the usesSingleNode option on 
     * XMLCompositeDirectCollectionMapping
     * @ since 2.4
     */
    public boolean supportsSingleNode(){
    	return true;
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

    public String resolveNamespacePrefix(String prefix) {
        return null;
    }

    /**
     * INTERNAL:
     */
    public XMLMarshaller getMarshaller() {
        return marshaller;
    }

    /**
     * INTERNAL:
     */
    public void setMarshaller(XMLMarshaller marshaller) {
        this.marshaller = marshaller;
        if(marshaller != null){
            MediaType mediaType = marshaller.getMediaType();
            if(marshaller.getNamespaceResolver() != null){
               namespaceResolver = marshaller.getNamespaceResolver();
            }
            namespaceAware = (mediaType == MediaType.APPLICATION_XML || namespaceResolver.getPrefixesToNamespaces().size() > 0);
        }
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
    public Object getOwningObject() {
        return owningObject;
    }

    /**
     * INTERNAL:
     */
    public void setOwningObject(Object obj) {
        this.owningObject = obj;
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
    public XPathQName getLeafElementType() {
        return leafElementType;
    }
    /**
     * INTERNAL:
     */
    public void setLeafElementType(XPathQName type) {
        leafElementType = type;
    }

    /**
     * INTERNAL:
     */
    public void setLeafElementType(QName type) {
    	if(type != null){
    	    setLeafElementType(new XPathQName(type, isNamespaceAware()));
    	}
    }
    
    public void setNamespaceResolver(NamespaceResolver nr) {
        namespaceResolver = nr;
    }

    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    public AbstractSession getSession() {
        return session;
    }

    public void setSession(AbstractSession session) {
        this.session = session;
    }

    public boolean isXOPPackage() {
        return isXOPPackage;
    }

    public void setXOPPackage(boolean isXOPPackage) {
        this.isXOPPackage = isXOPPackage;
    }
    
    /**
     * INTERNAL:
     * Determine if namespaces will be considered during marshal/unmarshal operations.
     * @since 2.4
     */
    public boolean isNamespaceAware() {
    	return namespaceAware;
    }
    
    /**
     * INTERNAL:
	 * The character used to separate the prefix and uri portions when namespaces are present 
     * @since 2.4
     */
    public char getNamespaceSeparator(){
    	return namespaceSeparator;
    }
	
}
