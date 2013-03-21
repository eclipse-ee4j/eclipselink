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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.queries.CoreMappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.AnyAttributeMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalContext;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: This is how the XML Any Attribute Mapping is handled when
 * used with the TreeObjectBuilder.</p>
 */

public class XMLAnyAttributeMappingNodeValue extends MappingNodeValue implements ContainerValue {
    private AnyAttributeMapping xmlAnyAttributeMapping;
    private int index = -1;
    
    public XMLAnyAttributeMappingNodeValue(AnyAttributeMapping xmlAnyAttributeMapping) {
        super();
        this.xmlAnyAttributeMapping = xmlAnyAttributeMapping;
    }

    public boolean isOwningNode(XPathFragment xPathFragment) {
        return xPathFragment == null;
    }

    @Override
    public boolean isWrapperAllowedAsCollectionName() {
        return false;
    }

    public boolean marshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        if (xmlAnyAttributeMapping.isReadOnly()) {
            return false;
        }
        Object collection = xmlAnyAttributeMapping.getAttributeValueFromObject(object);
        if (collection == null) {
            return false;
        }
        CoreContainerPolicy cp = getContainerPolicy();
        Object iter = cp.iteratorFor(collection);
        if (!cp.hasNext(iter)) {
            return false;
        }
        XPathFragment groupingElements = marshalRecord.openStartGroupingElements(namespaceResolver);
        List extraNamespaces = new ArrayList();
        NamespaceResolver nr = marshalRecord.getNamespaceResolver();
        while (cp.hasNext(iter)) {
            Map.Entry entry = (Map.Entry)cp.nextEntry(iter, session);
            Object key = entry.getKey();
            if (key instanceof QName) {
                QName name = (QName) key;
                String value = entry.getValue().toString();

                String qualifiedName = name.getLocalPart();
                if (nr != null) {
                    String prefix = nr.resolveNamespaceURI(name.getNamespaceURI());
                    if ((prefix != null) && prefix.length() > 0) {
                        qualifiedName = prefix + Constants.COLON+ qualifiedName;
                    } else if (name.getNamespaceURI() != null && name.getNamespaceURI().length() > 0) {
                        String generatedPrefix = nr.generatePrefix();
                        if(marshalRecord.hasCustomNamespaceMapper()) {
                            String customPrefix = marshalRecord.getMarshaller().getNamespacePrefixMapper().getPreferredPrefix(name.getNamespaceURI(), generatedPrefix, true);
                            if(customPrefix != null && customPrefix.length() > 0) {
                                generatedPrefix = customPrefix;
                            }
                        }
                        qualifiedName = generatedPrefix + Constants.COLON + qualifiedName;
                        nr.put(generatedPrefix, name.getNamespaceURI());
                        extraNamespaces.add(generatedPrefix);                        
                        marshalRecord.namespaceDeclaration(generatedPrefix, name.getNamespaceURI());

                    }
                }
                if(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(name.getNamespaceURI())){
                    marshalRecord.namespaceDeclaration(name.getLocalPart(), name.getNamespaceURI());
                }else{
                    marshalRecord.attribute(name.getNamespaceURI(), name.getLocalPart(), qualifiedName, value);
                }
            }
        }

        for (int i = 0; i < extraNamespaces.size(); i++) {
            marshalRecord.getNamespaceResolver().removeNamespace((String) extraNamespaces.get(i));
        }
        marshalRecord.closeStartGroupingElements(groupingElements);
        return true;
    }

    public void attribute(UnmarshalRecord unmarshalRecord, String namespaceURI, String localName, String value) {        
        boolean includeAttribute = true;
        if(!xmlAnyAttributeMapping.isNamespaceDeclarationIncluded() && javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)){
            includeAttribute = false;               
        }else if(!xmlAnyAttributeMapping.isSchemaInstanceIncluded() && javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(namespaceURI)){
            includeAttribute = false;               
        }
                    
        if(includeAttribute){
            CoreContainerPolicy cp = xmlAnyAttributeMapping.getContainerPolicy();
            Object containerInstance = unmarshalRecord.getContainerInstance(this);
            QName key = new QName(namespaceURI, localName);            
            cp.addInto(key, value, containerInstance, unmarshalRecord.getSession());
        }          
    }

    public Object getContainerInstance() {
        return xmlAnyAttributeMapping.getContainerPolicy().containerInstance();
    }

    public void setContainerInstance(Object object, Object container) {
        xmlAnyAttributeMapping.setAttributeValueInObject(object, container);
    }

    public CoreMappedKeyMapContainerPolicy getContainerPolicy() {
        return (CoreMappedKeyMapContainerPolicy) xmlAnyAttributeMapping.getContainerPolicy();
    }

    public boolean isContainerValue() {
        return true;
    }

    public boolean marshalSingleValue(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Object value, CoreAbstractSession session, NamespaceResolver namespaceResolver, MarshalContext marshalContext) {
        return true;
    }

    public AnyAttributeMapping getMapping() {
        return xmlAnyAttributeMapping;
    }

    public boolean getReuseContainer() {
        return getMapping().getReuseContainer();
    }
   
    /**
     *  INTERNAL:
     *  Used to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord 
     */  
    public void setIndex(int index){
    	this.index = index;
    }
    
    /**
     * INTERNAL:
     * Set to track the index of the corresponding containerInstance in the containerInstances Object[] on UnmarshalRecord
     * Set during TreeObjectBuilder initialization 
     */
    public int getIndex(){
    	return index;
    }

    /**
     * INTERNAL
     * Return true if an empty container should be set on the object if there
     * is no presence of the collection in the XML document.
     * @since EclipseLink 2.3.3
     */
    public boolean isDefaultEmptyContainer() {
        return getMapping().isDefaultEmptyContainer();
    }

}
