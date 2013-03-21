/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractRecord;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.MediaType;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.ObjectBuilder;
import org.eclipse.persistence.internal.oxm.Root;
import org.eclipse.persistence.internal.oxm.XPathQName;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.w3c.dom.Node;

public class AbstractMarshalRecordImpl<
    ABSTRACT_SESSION extends CoreAbstractSession,
    FIELD extends CoreField,
    MARSHALLER extends Marshaller,
    NAMESPACE_RESOLVER extends NamespaceResolver> extends CoreAbstractRecord implements AbstractMarshalRecord<ABSTRACT_SESSION, FIELD, MARSHALLER, NAMESPACE_RESOLVER> {

    protected boolean equalNamespaceResolvers;
    protected boolean hasCustomNamespaceMapper;
    private boolean isXOPPackage;
    private XPathQName leafElementType;
    protected MARSHALLER marshaller;
    protected boolean namespaceAware = true;
    protected NAMESPACE_RESOLVER namespaceResolver;
    private Object owningObject;
    private AbstractMarshalRecord<ABSTRACT_SESSION, FIELD, MARSHALLER, NAMESPACE_RESOLVER> realRecord;
    protected ABSTRACT_SESSION session;

    public AbstractMarshalRecordImpl(AbstractMarshalRecord realRecord) {
        this.realRecord = realRecord;
    }

    @Override
    public List addExtraNamespacesToNamespaceResolver(Descriptor descriptor,
            CoreAbstractSession session, boolean allowOverride,
            boolean ignoreEqualResolvers) {
        if (equalNamespaceResolvers && !ignoreEqualResolvers) {
            return null;
        }

        org.eclipse.persistence.internal.oxm.NamespaceResolver descriptorNamespaceResolver = descriptor.getNamespaceResolver();
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

    public boolean addXsiTypeAndClassIndicatorIfRequired(Descriptor descriptor, Descriptor referenceDescriptor, Field xmlField, boolean isRootElement) {
        ObjectBuilder objectBuilder = (ObjectBuilder) descriptor.getObjectBuilder();
        boolean xsiTypeIndicatorField = objectBuilder.isXsiTypeIndicatorField();

        if(objectBuilder.addClassIndicatorFieldToRow(this)) {
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
                        objectBuilder.addClassIndicatorFieldToRow(this);
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
                Descriptor xdesc = marshaller.getContext().getDescriptor(qName);
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

    public void attribute(String namespaceURI, String localName,
            String qualifiedName, String value) {
        if(null != realRecord) {
            realRecord.attribute(namespaceURI, localName, qualifiedName, value);
        }
    }

    @Override
    public void attributeWithoutQName(String namespaceURI, String localName,
            String prefix, String value) {
        String qualifiedName = localName;
        if(prefix != null && prefix.length() >0){
            qualifiedName = prefix + getNamespaceSeparator() + qualifiedName;
        }
        attribute(namespaceURI, localName, qualifiedName, value);
    }

    @Override
    public Node getDOM() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public XPathQName getLeafElementType() {
        return leafElementType;
    }

    @Override
    public MARSHALLER getMarshaller() {
        return marshaller;
    }

    @Override
    public NAMESPACE_RESOLVER getNamespaceResolver() {
        return namespaceResolver;
    }

    @Override
    public char getNamespaceSeparator() {
        return Constants.COLON;
    }

    @Override
    public Object getOwningObject() {
        return owningObject;
    }

    @Override
    public ABSTRACT_SESSION getSession() {
        return session;
    }

    @Override
    public boolean hasCustomNamespaceMapper() {
        return hasCustomNamespaceMapper;
    }

    @Override
    public boolean hasEqualNamespaceResolvers() {
        return equalNamespaceResolvers;
    }

    /**
     * Determine if namespaces will be considered during marshal/unmarshal operations.
     */
    @Override
    public boolean isNamespaceAware() {
        return namespaceAware;
    }

    @Override
    public boolean isXOPPackage() {
        return isXOPPackage;
    }

    @Override
    public void namespaceDeclaration(String prefix, String typeUri) {
        if(realRecord != null) {
            realRecord.namespaceDeclaration(prefix, typeUri);
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Object put(FIELD field, Object object) {
        if(null != realRecord) {
            return realRecord.put(field, object);
        }
        throw new UnsupportedOperationException();
    }

    @Override
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

    @Override
    public String resolveNamespacePrefix(String prefix) {
        return null;
    }

    @Override
    public void setCustomNamespaceMapper(boolean customNamespaceMapper) {
        this.hasCustomNamespaceMapper = customNamespaceMapper;
    }

    @Override
    public void setEqualNamespaceResolvers(boolean equalNRs) {
        this.equalNamespaceResolvers = equalNRs;
    }

    @Override
    public void setLeafElementType(QName type) {
        if(type != null){
            setLeafElementType(new XPathQName(type, isNamespaceAware()));
        }
    }

    @Override
    public void setLeafElementType(XPathQName type) {
        leafElementType = type;
    }

    @Override
    public void setMarshaller(MARSHALLER marshaller) {
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

    @Override
    public void setNamespaceResolver(NAMESPACE_RESOLVER namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

    @Override
    public void setOwningObject(Object owningObject) {
        this.owningObject = owningObject;
    }

    @Override
    public void setSession(ABSTRACT_SESSION session) {
        this.session = session;
    }

    @Override
    public void setXOPPackage(boolean isXOPPackage) {
        this.isXOPPackage = isXOPPackage;
    }

    @Override
    public void writeXsiTypeAttribute(Descriptor descriptor, String typeUri,
            String typeLocal, String typePrefix, boolean addToNamespaceResolver) {
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

    @Override
    public void writeXsiTypeAttribute(Descriptor xmlDescriptor, XMLSchemaReference xmlRef, boolean addToNamespaceResolver) {
        QName contextAsQName = xmlRef.getSchemaContextAsQName();
        
        if(contextAsQName == null){
            contextAsQName = xmlRef.getSchemaContextAsQName(namespaceResolver);
        }
        if (contextAsQName != null) {
            writeXsiTypeAttribute(xmlDescriptor, contextAsQName.getNamespaceURI(), contextAsQName.getLocalPart(), null, addToNamespaceResolver);
        }
    }

}
