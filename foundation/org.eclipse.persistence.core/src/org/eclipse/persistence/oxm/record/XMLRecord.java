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
package org.eclipse.persistence.oxm.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.MediaType;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
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
public abstract class XMLRecord extends AbstractRecord implements org.eclipse.persistence.internal.oxm.record.XMLRecord<AbstractSession, DatabaseField, XMLMarshaller, NamespaceResolver, XMLUnmarshaller> {
    protected XMLMarshaller marshaller;
    protected XMLUnmarshaller unmarshaller;
    private DocumentPreservationPolicy docPresPolicy;
    private Object owningObject;
    protected Object currentObject;
    private XPathQName leafElementType;
    protected NamespaceResolver namespaceResolver;
    protected AbstractSession session;
    private boolean isXOPPackage;
    protected boolean namespaceAware;
    
    protected boolean hasCustomNamespaceMapper;
    protected boolean equalNamespaceResolvers = false;

    /**
     * INTERNAL:
     * Nil: This is used to indicate that this field represents xsi:nil="true"
     */
    public static final XMLRecord.Nil NIL = org.eclipse.persistence.internal.oxm.record.XMLRecord.NIL;

    public XMLRecord() {
        super(null, null, 0);
        namespaceAware = true;
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
            if(marshaller.getNamespacePrefixMapper() != null){
            	namespaceAware = true;             	
            }else{
            	namespaceAware = mediaType.isApplicationXML();
            }
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

    public void setEqualNamespaceResolvers(boolean equalNRs) {
        this.equalNamespaceResolvers = equalNRs;
    }

    public boolean hasEqualNamespaceResolvers() {
        return equalNamespaceResolvers;
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
    public List<Namespace> addExtraNamespacesToNamespaceResolver(Descriptor desc, CoreAbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers) {
        if (equalNamespaceResolvers && !ignoreEqualResolvers) {
            return null;
        }

        org.eclipse.persistence.internal.oxm.NamespaceResolver descriptorNamespaceResolver = desc.getNamespaceResolver();
        if(null == descriptorNamespaceResolver || !descriptorNamespaceResolver.hasPrefixesToNamespaces()) {
            return null;
        }
        Map<String, String> prefixesToNamespaces = descriptorNamespaceResolver.getPrefixesToNamespaces();
        if(prefixesToNamespaces.size() == 0) {
            return null;
        }
        List returnList = new ArrayList(prefixesToNamespaces.size());
        org.eclipse.persistence.internal.oxm.NamespaceResolver marshalRecordNamespaceResolver = namespaceResolver;
        for(Entry<String, String> entry: prefixesToNamespaces.entrySet()) {

            //if isn't already on a parentadd namespace to this element
            String prefix = marshalRecordNamespaceResolver.resolveNamespaceURI(entry.getValue());

            if (prefix == null || prefix.length() == 0) {
                //if there is no prefix already declared for this uri in the nr add this one
                //unless that prefix is already bound to another namespace uri
                prefix = entry.getKey();
                if(hasCustomNamespaceMapper) {
                    String newPrefix = getMarshaller().getNamespacePrefixMapper().getPreferredPrefix(entry.getValue(), prefix, true);
                    if(newPrefix != null && !(newPrefix.length() == 0)) {
                        prefix = newPrefix;
                    }
                }
                String uri = marshalRecordNamespaceResolver.resolveNamespacePrefix(prefix);
                if(hasCustomNamespaceMapper || allowOverride || uri == null || uri.length() == 0) {
                    //if this uri is unknown, the cutom mapper will return the preferred prefix for this uri
                    marshalRecordNamespaceResolver.put(entry.getKey(), entry.getValue());
                    returnList.add(new Namespace(prefix, entry.getValue()));
                }
            } else if(allowOverride) {
                //if overrides are allowed, add the prefix if the URI is different
                if (!prefix.equals(entry.getKey()) && !hasCustomNamespaceMapper) {
                    //if prefix exists for uri but is different then add this
                    //unless using a custom namespace prefix mapper. Then prefix is expected to be different
                    marshalRecordNamespaceResolver.put(entry.getKey(), entry.getValue());
                    returnList.add(new Namespace(entry.getKey(), entry.getValue()));
                }
            }
        }
        return returnList;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public boolean addXsiTypeAndClassIndicatorIfRequired(Descriptor descriptor, Descriptor referenceDescriptor, Field xmlField, boolean isRootElement) {
        XMLObjectBuilder objectBuilder = (XMLObjectBuilder) descriptor.getObjectBuilder();
        boolean xsiTypeIndicatorField = objectBuilder.isXsiTypeIndicatorField();

        if (descriptor.hasInheritance() && !xsiTypeIndicatorField) {
            descriptor.getInheritancePolicy().addClassIndicatorFieldToRow(this);
            return true;
        }

        QName leafType = null;
        if (xmlField != null) {
            leafType = xmlField.getLeafElementType();

            XMLSchemaReference xmlRef = descriptor.getSchemaReference();
            if (xmlRef != null) {
                if (leafType == null) {
                    if (xmlRef.getType() == XMLSchemaReference.ELEMENT) {
                        return false;
                    }
                    if (referenceDescriptor == null) {
                        writeXsiTypeAttribute(descriptor, xmlRef, isRootElement);
                        return true;
                    }
                } else if (((xmlRef.getType() == XMLSchemaReference.COMPLEX_TYPE) || (xmlRef.getType() == XMLSchemaReference.SIMPLE_TYPE)) && xmlRef.getSchemaContext() != null && xmlRef.isGlobalDefinition()) {
                    QName ctxQName = xmlRef.getSchemaContextAsQName(descriptor.getNamespaceResolver());
                    if (!ctxQName.equals(leafType)) {
                        writeXsiTypeAttribute(descriptor, xmlRef, isRootElement);
                        return true;
                    }
                }
            }
        }

        if (referenceDescriptor != null && referenceDescriptor == descriptor) {
            return false;
        }
        if (descriptor.hasInheritance() && !descriptor.getInheritancePolicy().isRootParentDescriptor()) {
            CoreInheritancePolicy inheritancePolicy = descriptor.getInheritancePolicy();
            Field indicatorField = (Field) inheritancePolicy.getClassIndicatorField();
            if (indicatorField != null && xsiTypeIndicatorField) {
                Object classIndicatorValueObject = inheritancePolicy.getClassIndicatorMapping().get(descriptor.getJavaClass());
                String classIndicatorUri = null;
                String classIndicatorLocal= null;
                String classIndicatorPrefix= null;
                if (classIndicatorValueObject instanceof QName) {
                    QName classIndicatorQName = (QName) classIndicatorValueObject;
                    classIndicatorUri = classIndicatorQName.getNamespaceURI();
                    classIndicatorLocal = classIndicatorQName.getLocalPart();
                    classIndicatorPrefix = classIndicatorQName.getPrefix();
                } else {
                    String classIndicatorValue = (String) inheritancePolicy.getClassIndicatorMapping().get(descriptor.getJavaClass());
                    int nsindex = classIndicatorValue.indexOf(Constants.COLON);
                    String prefix = null;
                    if (nsindex != -1) {
                        classIndicatorLocal = classIndicatorValue.substring(nsindex + 1);
                        prefix = classIndicatorValue.substring(0, nsindex);
                    } else {
                        classIndicatorLocal = classIndicatorValue;
                    }
                    classIndicatorUri = descriptor.getNonNullNamespaceResolver().resolveNamespacePrefix(prefix);
                }
                if(leafType == null 
                        || isRootElement && marshaller.getMediaType().isApplicationJSON() && !marshaller.isIncludeRoot() 
                        || !(leafType.getLocalPart().equals(classIndicatorLocal))
                        || (classIndicatorUri == null && (leafType.getNamespaceURI() != null && leafType.getNamespaceURI().length() >0))
                        || (classIndicatorUri != null && !classIndicatorUri.equals(leafType.getNamespaceURI()))
                       ){
                    if (inheritancePolicy.hasClassExtractor()) {
                        inheritancePolicy.addClassIndicatorFieldToRow(this);
                    } else {
                        writeXsiTypeAttribute(descriptor, classIndicatorUri, classIndicatorLocal,classIndicatorPrefix, isRootElement);
                    }
                    return true;
                }
                return false;
            }

        }
        return false;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
   public boolean addXsiTypeAndClassIndicatorIfRequired(Descriptor descriptor, Descriptor referenceDescriptor, Field xmlField,
           Object originalObject, Object obj, boolean wasXMLRoot, boolean isRootElement) {
        if (wasXMLRoot) {
            XMLSchemaReference xmlRef = descriptor.getSchemaReference();

            if (descriptor != null) {
                Root xr = (Root) originalObject;

                if (xmlRef == null) {
                    return false;
                }
                String xmlRootLocalName = xr.getLocalName();
                String xmlRootUri = xr.getNamespaceURI();

                XPathQName qName = new XPathQName(xmlRootUri, xmlRootLocalName, namespaceAware);
                Descriptor xdesc = marshaller.getXMLContext().getDescriptor(qName);
                if (xdesc != null) {
                    boolean writeTypeAttribute = xdesc.getJavaClass() != descriptor.getJavaClass();
                    if (writeTypeAttribute) {
                        writeXsiTypeAttribute(descriptor, xmlRef, isRootElement);
                        return true;
                    }
                    return false;

                }

                if (xr.getDeclaredType() != null && xr.getDeclaredType() == xr.getObject().getClass()) {
                    return false;
                }

                boolean writeTypeAttribute = true;
                int tableSize = descriptor.getTableNames().size();
                for (int i = 0; i < tableSize; i++) {
                    if (!writeTypeAttribute) {
                        return false;
                    }
                    String defaultRootQualifiedName = (String) descriptor.getTableNames().get(i);
                    if (defaultRootQualifiedName != null) {
                        String defaultRootLocalName = null;
                        String defaultRootUri = null;
                        int colonIndex = defaultRootQualifiedName.indexOf(Constants.COLON);
                        if (colonIndex > 0) {
                            String defaultRootPrefix = defaultRootQualifiedName.substring(0, colonIndex);
                            defaultRootLocalName = defaultRootQualifiedName.substring(colonIndex + 1);
                            if (descriptor.getNamespaceResolver() != null) {
                                defaultRootUri = descriptor.getNamespaceResolver().resolveNamespacePrefix(defaultRootPrefix);
                            }
                        } else {
                            defaultRootLocalName = defaultRootQualifiedName;
                        }

                        if (xmlRootLocalName != null) {
                            if ((((defaultRootLocalName == null) && (xmlRootLocalName == null)) || (defaultRootLocalName.equals(xmlRootLocalName)))
                                && (((defaultRootUri == null) && (xmlRootUri == null)) || ((xmlRootUri != null) && (defaultRootUri != null) && (defaultRootUri.equals(xmlRootUri))))) {
                                // if both local name and uris are equal then don't need to write type attribute
                                return false;
                            }
                        }
                    } else {
                        // no default rootElement was set
                        // if xmlRootName = null then writeTypeAttribute = false
                        if (xmlRootLocalName == null) {
                            return false;
                        }
                    }
                }
                if (writeTypeAttribute && xmlRef != null) {
                    writeXsiTypeAttribute(descriptor, xmlRef, isRootElement);
                    return true;
                }
            }
            return false;
        } else {
            return addXsiTypeAndClassIndicatorIfRequired(descriptor, referenceDescriptor, xmlField, isRootElement);
        }
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public void removeExtraNamespacesFromNamespaceResolver(List<Namespace> extraNamespaces, CoreAbstractSession session) {
        if (extraNamespaces == null){
          return;
        }

         for (int i = 0; i < extraNamespaces.size(); i++) {
             Namespace nextExtraNamespace = (Namespace)extraNamespaces.get(i);
             String uri = namespaceResolver.resolveNamespacePrefix(nextExtraNamespace.getPrefix());
             if ((uri != null) && uri.equals(nextExtraNamespace.getNamespaceURI())) {
                namespaceResolver.removeNamespace(nextExtraNamespace.getPrefix());
             }
         }
     }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public void writeXsiTypeAttribute(Descriptor xmlDescriptor, String typeUri,  String  typeLocal, String typePrefix, boolean addToNamespaceResolver) {
        if (typeLocal == null){
            return;
        }
        String typeValue = typeLocal;
        if(isNamespaceAware() && typeUri != null && !typeUri.equals(Constants.EMPTY_STRING) && !typeUri.equals(namespaceResolver.getDefaultNamespaceURI())){
            String prefix = namespaceResolver.resolveNamespaceURI(typeUri);
            if(prefix != null && !prefix.equals(Constants.EMPTY_STRING)){
                typeValue = prefix + getNamespaceSeparator() + typeValue;
            } else if (typeUri.equals(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
                prefix = namespaceResolver.generatePrefix(Constants.SCHEMA_PREFIX);
                typeValue = prefix + getNamespaceSeparator() + typeValue;
                namespaceDeclaration(prefix, typeUri);
             
            } else if (typePrefix != null && !typePrefix.equals(Constants.EMPTY_STRING)){
                String existingUri = namespaceResolver.resolveNamespacePrefix(typePrefix);
                if(existingUri != null){
                    prefix = namespaceResolver.generatePrefix();
                }else{
                    prefix = typePrefix;   
                }               
                typeValue = prefix + getNamespaceSeparator() + typeValue;
                namespaceDeclaration(prefix, typeUri);
            }else{
                prefix = namespaceResolver.generatePrefix();
                typeValue = prefix + getNamespaceSeparator() + typeValue;
                namespaceDeclaration(prefix, typeUri);

            }
        }
        
        String xsiPrefix = null;
        if(isNamespaceAware()){
            xsiPrefix = namespaceResolver.resolveNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            if (xsiPrefix == null) {
                xsiPrefix = namespaceResolver.generatePrefix(Constants.SCHEMA_INSTANCE_PREFIX);
                namespaceDeclaration(xsiPrefix, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
                if(addToNamespaceResolver){
                    namespaceResolver.put(xsiPrefix, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
                }
            }
        }
        attributeWithoutQName(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.SCHEMA_TYPE_ATTRIBUTE, xsiPrefix, typeValue);
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    protected void writeXsiTypeAttribute(Descriptor xmlDescriptor, XMLSchemaReference xmlRef, boolean addToNamespaceResolver) {
        QName contextAsQName = xmlRef.getSchemaContextAsQName();
        
        if(contextAsQName == null){
            contextAsQName = xmlRef.getSchemaContextAsQName(namespaceResolver);
        }
        if (contextAsQName != null) {
            writeXsiTypeAttribute(xmlDescriptor, contextAsQName.getNamespaceURI(), contextAsQName.getLocalPart(), null, addToNamespaceResolver);
        }
    }

}
