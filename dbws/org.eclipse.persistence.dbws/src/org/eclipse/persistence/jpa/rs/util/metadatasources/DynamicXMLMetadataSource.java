/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      dclarke/tware - initial
//      tware
package org.eclipse.persistence.jpa.rs.util.metadatasources;

import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeDirectCollectionMapping;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeAccessor;
import org.eclipse.persistence.internal.jpa.weaving.RestAdapterClassWriter;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessMethods;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.XmlVirtualAccessMethods;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;

/**
 * {@link MetadataSource} used in the creation of dynamic JAXB contexts for applications.
 *
 * @author dclarke
 * @since EclipseLink 2.4.0
 */
public class DynamicXMLMetadataSource implements MetadataSource {
    private XmlBindings xmlBindings;

    public DynamicXMLMetadataSource(AbstractSession session, String packageName) {
        ObjectFactory objectFactory = new ObjectFactory();
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(packageName);

        JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);

        for (ClassDescriptor ormDescriptor : session.getProject().getOrderedDescriptors()) {
            String descriptorPackageName = "";
            if (ormDescriptor.getJavaClassName().lastIndexOf('.') > 0) {
                descriptorPackageName = ormDescriptor.getJavaClassName().substring(0, ormDescriptor.getJavaClassName().lastIndexOf('.'));
            }
            if (descriptorPackageName.equals(packageName)) {
                javaTypes.getJavaType().add(createJAXBType(ormDescriptor, objectFactory));
            }
        }
    }

    /**
     * Create a javaType to be used by JAXB to map a particular class.
     * For static classes, JAXB annotations, xml and defaults will be used to map the class.
     * For Dynamic classes we create properties for each JPA mapping on the class
     * thing we create is a
     * @param classDescriptor
     * @param objectFactory
     * @return
     */
    private JavaType createJAXBType(ClassDescriptor classDescriptor, ObjectFactory objectFactory) {
        JavaType javaType = new JavaType();
        String alias = classDescriptor.getAlias();
        if (alias == null || alias.isEmpty()) {
            alias = classDescriptor.getJavaClass().getSimpleName();
        }
        javaType.setName(alias);
        javaType.setJavaAttributes(new JavaAttributes());
        boolean isDynamic = DynamicEntity.class.isAssignableFrom(classDescriptor.getJavaClass());
        for (DatabaseMapping ormMapping : classDescriptor.getMappings()) {
            JAXBElement<XmlElement> element = createJAXBProperty(ormMapping, objectFactory, javaType, isDynamic);
            if (element != null) {
                javaType.getJavaAttributes().getJavaAttribute().add(element);
            }
        }

        // Embeddables don't need Rest adapters, return if the classDescriptor is an aggregate descriptor.
        if (classDescriptor.isAggregateDescriptor()) {
            return javaType;
        }

        // Set an adapter that is a subclass of ReferenceAdapter that can adapt the class to create a link for
        // the persistence_href field that has been weaved in.
        String name = RestAdapterClassWriter.constructClassNameForReferenceAdapter(classDescriptor.getJavaClassName());
        XmlJavaTypeAdapter adapter = new XmlJavaTypeAdapter();
        adapter.setValue(name);
        adapter.setValueType(classDescriptor.getJavaClassName());
        adapter.setType(classDescriptor.getJavaClassName());
        javaType.setXmlJavaTypeAdapter(adapter);

        return javaType;
    }

    /**
     * Create a JAXB property for a particular mapping.
     * This will only create JAXBProperties for mappings that are virtual - either because their
     * parent object is a dynamic class, or because the owning static class has virtual properties
     * @param mapping
     * @param objectFactory
     * @param owningType
     * @param isDynamic
     * @return
     */
    private JAXBElement<XmlElement> createJAXBProperty(DatabaseMapping mapping, ObjectFactory objectFactory, JavaType owningType, boolean isDynamic) {
        if (!mapping.getAttributeAccessor().isVirtualAttributeAccessor() &&
                !isDynamic) {
            return null;
        }
        XmlElement xmlElement = new XmlElement();
        xmlElement.setJavaAttribute(mapping.getAttributeName());
        if (mapping.isObjectReferenceMapping()) {
            xmlElement.setType(((ObjectReferenceMapping) mapping).getReferenceClassName());
        } else if (mapping.isCollectionMapping()) {
            if (mapping.isEISMapping()) {
                // No way to find out the type of the collection from EIS mappings, currently, so just set the container policy here...
                // It will be fine for simple collections
                if (mapping instanceof EISCompositeDirectCollectionMapping) {
                    xmlElement.setContainerType(((EISCompositeDirectCollectionMapping) mapping).getContainerPolicy().getContainerClassName());
                } else if (mapping instanceof EISCompositeCollectionMapping) {
                    xmlElement.setContainerType(((EISCompositeCollectionMapping) mapping).getContainerPolicy().getContainerClassName());
                    xmlElement.setType(((EISCompositeCollectionMapping) mapping).getReferenceClassName());
                }
            } else {
                xmlElement.setType(((CollectionMapping) mapping).getReferenceClassName());
                xmlElement.setContainerType(((CollectionMapping) mapping).getContainerPolicy().getContainerClassName());
            }
        } else {
            xmlElement.setType(mapping.getAttributeClassification().getName());
        }
        if (mapping.getAttributeAccessor().isVirtualAttributeAccessor()) {
            VirtualAttributeAccessor jpaAccessor = (VirtualAttributeAccessor) mapping.getAttributeAccessor();
            if (owningType.getXmlVirtualAccessMethods() == null) {
                XmlVirtualAccessMethods virtualAccessMethods = new XmlVirtualAccessMethods();
                virtualAccessMethods.setGetMethod(jpaAccessor.getGetMethodName());
                virtualAccessMethods.setSetMethod(jpaAccessor.getSetMethodName());
                owningType.setXmlVirtualAccessMethods(virtualAccessMethods);
            } else if (!owningType.getXmlVirtualAccessMethods().getGetMethod().equals(jpaAccessor.getGetMethodName())) {
                XmlAccessMethods accessMethods = new XmlAccessMethods();
                accessMethods.setGetMethod(jpaAccessor.getGetMethodName());
                accessMethods.setSetMethod(jpaAccessor.getSetMethodName());
                xmlElement.setXmlAccessMethods(accessMethods);
            }
        }
        return objectFactory.createXmlElement(xmlElement);
    }

    @Override
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }

}
