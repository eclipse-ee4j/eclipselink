/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.util.metadatasources;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.weaving.RestCollectionAdapterClassWriter;
import org.eclipse.persistence.internal.jpa.weaving.RestReferenceAdapterV2ClassWriter;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType.JavaAttributes;
import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlJavaTypeAdapter;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.Map;

/**
 * {@link org.eclipse.persistence.jaxb.metadata.MetadataSource} used in the creation of dynamic JAXB contexts
 * for applications in JPARS v2.0.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class DynamicXmlV2MetadataSource implements MetadataSource {
    private XmlBindings xmlBindings;

    /**
     * Creates a new DynamicXmlV2MetadataSource.
     *
     * @param session the session.
     * @param packageName the package name to process.
     */
    public DynamicXmlV2MetadataSource(AbstractSession session, String packageName) {
        final ObjectFactory objectFactory = new ObjectFactory();
        xmlBindings = new XmlBindings();
        xmlBindings.setPackageName(packageName);

        final JavaTypes javaTypes = new JavaTypes();
        xmlBindings.setJavaTypes(javaTypes);

        for (ClassDescriptor ormDescriptor : session.getProject().getOrderedDescriptors()) {
            String descriptorPackageName = "";
            int index = ormDescriptor.getJavaClassName().lastIndexOf('.');
            if (index > 0) {
                descriptorPackageName = ormDescriptor.getJavaClassName().substring(0, index);
            }
            if (descriptorPackageName.equals(packageName)) {
                javaTypes.getJavaType().add(createJAXBType(ormDescriptor, objectFactory));
            }
        }
    }

    /**
     * Create a javaType to be used by JAXB to map a particular class.
     */
    private JavaType createJAXBType(ClassDescriptor classDescriptor, ObjectFactory objectFactory) {
        JavaType javaType = new JavaType();
        String alias = classDescriptor.getAlias();
        if (alias == null || alias.isEmpty()) {
            alias = classDescriptor.getJavaClass().getSimpleName();
        }
        javaType.setName(alias);

        final JavaAttributes javaAttributes = new JavaAttributes();
        for (DatabaseMapping ormMapping : classDescriptor.getMappings()) {
            JAXBElement<XmlElement> element = null;
            if (ormMapping instanceof DirectCollectionMapping) {
                // Direct mapping -> no adapter
                continue;
            } else if (ormMapping.isCollectionMapping()) {
                // This is a collection mapping -> create collection adapter
                element = createCollectionProperty(ormMapping, objectFactory);
            } else if (ForeignReferenceMapping.class.isAssignableFrom(ormMapping.getClass())) {
                // FK mapping -> create reference adapter
                element = createProperty((ForeignReferenceMapping)ormMapping, objectFactory);
            }

            if (element != null) {
                javaAttributes.getJavaAttribute().add(element);
            }
        }

        if (!javaAttributes.getJavaAttribute().isEmpty()) {
            javaType.setJavaAttributes(javaAttributes);
        }

        return javaType;
    }

    /**
     * Create a JAXB property for given collection mapping.
     */
    private JAXBElement<XmlElement> createCollectionProperty(DatabaseMapping mapping, ObjectFactory objectFactory) {
        final XmlElement xmlElement = new XmlElement();
        xmlElement.setJavaAttribute(mapping.getAttributeName());
        xmlElement.setType(((CollectionMapping) mapping).getReferenceClassName());

        final XmlJavaTypeAdapter adapter = new XmlJavaTypeAdapter();
        final String adapterName = RestCollectionAdapterClassWriter.getClassName(((CollectionMapping) mapping).getReferenceClassName());
        adapter.setValue(adapterName);
        adapter.setType(Collection.class.getName());
        xmlElement.setXmlJavaTypeAdapter(adapter);

        return objectFactory.createXmlElement(xmlElement);
    }

    /**
     * Create a JAXB property for given reference mapping.
     */
    private JAXBElement<XmlElement> createProperty(ForeignReferenceMapping mapping, ObjectFactory objectFactory) {
        final String referenceClassName = mapping.getReferenceClassName();

        final XmlElement xmlElement = new XmlElement();
        xmlElement.setJavaAttribute(mapping.getAttributeName());
        xmlElement.setType(referenceClassName);

        final String adapterName = RestReferenceAdapterV2ClassWriter.getClassName(referenceClassName);
        final XmlJavaTypeAdapter adapter = new XmlJavaTypeAdapter();
        adapter.setValue(adapterName);
        adapter.setType(referenceClassName);
        xmlElement.setXmlJavaTypeAdapter(adapter);

        return objectFactory.createXmlElement(xmlElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
        return this.xmlBindings;
    }

}
