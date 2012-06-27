/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke/tware - initial 
 *      tware
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeAccessor;
import org.eclipse.persistence.internal.weaving.RelationshipInfo;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessMethods;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlSchema.XmlNs;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlVirtualAccessMethods;
import org.eclipse.persistence.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.sessions.server.Server;

/**
 * {@link MetadataSource} used in the creation of dynamic JAXB contexts for applications.
 * 
 * @see PersistenceFactory#createJAXBContext(Server)
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class DynamicXMLMetadataSource implements MetadataSource {

    private static final String LINK_NAMESPACE_URI = "http://www.w3.org/2005/Atom";
    private static final String LINK_PREFIX = "atom";
    private static final String LINK_LOCAL_NAME = "link";
    
    private XmlBindings xmlBindings;

    public DynamicXMLMetadataSource(Server session, String packageName) {
        ObjectFactory objectFactory = new ObjectFactory();
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(packageName);

        JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);

        XmlSchema xmlSchema = new XmlSchema();
        XmlNs atomNs = new XmlNs();
        atomNs.setPrefix(LINK_PREFIX);
        atomNs.setNamespaceUri(LINK_NAMESPACE_URI);
        xmlSchema.getXmlNs().add(atomNs);
        xmlBindings.setXmlSchema(xmlSchema);
        
        for (ClassDescriptor ormDescriptor : session.getProject().getOrderedDescriptors()) {
            String descriptorPackageName = "";
            if (ormDescriptor.getJavaClassName().lastIndexOf('.') > 0){
                descriptorPackageName = ormDescriptor.getJavaClassName().substring(0, ormDescriptor.getJavaClassName().lastIndexOf('.'));
            }
            if (descriptorPackageName.equals(packageName)){
                javaTypes.getJavaType().add(createJAXBType(ormDescriptor, objectFactory));
            }
        }
    }
    
    private JavaType createJAXBType(ClassDescriptor classDescriptor, ObjectFactory objectFactory) {
        JavaType javaType = new JavaType();
        javaType.setName(classDescriptor.getAlias());
        javaType.setJavaAttributes(new JavaAttributes());
        boolean isDynamic = DynamicEntity.class.isAssignableFrom(classDescriptor.getJavaClass());
        for (DatabaseMapping ormMapping : classDescriptor.getMappings()) {
            JAXBElement<XmlElement> element = createJAXBProperty(ormMapping, objectFactory, javaType, isDynamic);
            if (element != null){
                javaType.getJavaAttributes().getJavaAttribute().add(element);
            }
        }
        if (isDynamic){
            javaType.getJavaAttributes().getJavaAttribute().add(createSelfProperty(classDescriptor.getJavaClassName(), objectFactory));
            javaType.getJavaAttributes().getJavaAttribute().add(createRelationshipsProperty(classDescriptor.getJavaClassName(), objectFactory));
        }
        // Make them all root elements for now
        javaType.setXmlRootElement(new org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement());

        return javaType;
    }
    
    private JAXBElement<XmlElement> createJAXBProperty(DatabaseMapping mapping, ObjectFactory objectFactory, JavaType owningType, boolean isDynamic) {
        if (!mapping.getAttributeAccessor().isVirtualAttributeAccessor() && !isDynamic && (mapping.isPrivateOwned() || (!mapping.isObjectReferenceMapping() &&  !mapping.isCollectionMapping()) )){
            return null;
        }
        XmlElement xmlElement = new XmlElement();
        xmlElement.setJavaAttribute(mapping.getAttributeName());
        if (mapping.isObjectReferenceMapping()){
            xmlElement.setType(((ObjectReferenceMapping)mapping).getReferenceClassName());
            if (!mapping.isPrivateOwned()){
                xmlElement.setReadOnly(true);
            }
        } else if (mapping.isCollectionMapping()){
            xmlElement.setType(((CollectionMapping)mapping).getReferenceClassName());
            if (!mapping.isPrivateOwned()){
                xmlElement.setReadOnly(true);
            }
        } else {
            xmlElement.setType(mapping.getAttributeClassification().getName());
        }
        if (mapping.getAttributeAccessor().isVirtualAttributeAccessor()){
            VirtualAttributeAccessor jpaAccessor = (VirtualAttributeAccessor)mapping.getAttributeAccessor();
            if (owningType.getXmlVirtualAccessMethods() == null){
                XmlVirtualAccessMethods virtualAccessMethods = new XmlVirtualAccessMethods();
                virtualAccessMethods.setGetMethod(jpaAccessor.getGetMethodName());
                virtualAccessMethods.setSetMethod(jpaAccessor.getSetMethodName());
                owningType.setXmlVirtualAccessMethods(virtualAccessMethods);
            } else if (!owningType.getXmlVirtualAccessMethods().getGetMethod().equals(jpaAccessor.getGetMethodName())){
                XmlAccessMethods accessMethods = new XmlAccessMethods();
                accessMethods.setGetMethod(jpaAccessor.getGetMethodName());
                accessMethods.setSetMethod(jpaAccessor.getSetMethodName());
                xmlElement.setXmlAccessMethods(accessMethods);
            }

        }
        return objectFactory.createXmlElement(xmlElement);
    }

    public static JAXBElement<XmlElement> createSelfProperty(String ownerClassName, ObjectFactory objectFactory){
        XmlElement xmlElement = new XmlElement();
        xmlElement.setJavaAttribute("self");
        xmlElement.setType(ownerClassName);
        addXmlAdapter(xmlElement);
        return objectFactory.createXmlElement(xmlElement);
    }
    
    public static JAXBElement<XmlElement> createRelationshipsProperty(String ownerClassName, ObjectFactory objectFactory){
        XmlElement xmlElement = new XmlElement();
        xmlElement.setJavaAttribute("_persistence_relationshipInfo");
        xmlElement.setName("relationships");
        xmlElement.setType(RelationshipInfo.class.getName());
        xmlElement.setContainerType(List.class.getName());
        xmlElement.setWriteOnly(true);

        XmlJavaTypeAdapter adapter = new XmlJavaTypeAdapter();
        adapter.setValue(RelationshipLinkAdapter.class.getName());
        adapter.setValueType(Link.class.getName());
        adapter.setType(xmlElement.getType());
        xmlElement.setXmlJavaTypeAdapter(adapter);
        return objectFactory.createXmlElement(xmlElement);
    }
    
    
    public static void addXmlAdapter(XmlElement xmlElement) {
        xmlElement.setXmlPath(xmlElement.getJavaAttribute() + "/" + LINK_PREFIX + ":" + LINK_LOCAL_NAME + "[@rel='" + xmlElement.getJavaAttribute() + "']/@href");

        XmlJavaTypeAdapter adapter = new XmlJavaTypeAdapter();
        adapter.setValue(LinkAdapter.class.getName());
        adapter.setValueType(String.class.getName());
        adapter.setType(xmlElement.getType());
        xmlElement.setXmlJavaTypeAdapter(adapter);
    }
    
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }
    
}
