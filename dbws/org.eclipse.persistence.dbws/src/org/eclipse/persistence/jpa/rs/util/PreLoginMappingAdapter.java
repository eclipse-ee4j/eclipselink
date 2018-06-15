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
//     tware -  Initial implementation
package org.eclipse.persistence.jpa.rs.util;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeAccessor;
import org.eclipse.persistence.internal.dynamic.ValuesAccessor;
import org.eclipse.persistence.internal.jaxb.SessionEventListener;
import org.eclipse.persistence.internal.jaxb.XMLJavaTypeConverter;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.jpa.weaving.RestAdapterClassWriter;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.jaxb.DefaultXMLNameTransformer;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.util.xmladapters.RelationshipLinkAdapter;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.SessionEvent;

/**
 * This adapter alters the way the JAXBContext handles relationships for an existing persistence unit.
 * It changes non-private relationship mappings to be read-only and replaces those mappings with a mapping
 * to a weaved-in list of relationships that will produce links.
 * @author tware
 *
 */
public class PreLoginMappingAdapter extends SessionEventListener {

    protected AbstractSession jpaSession;

    /**
     * Instantiates a new pre login mapping adapter.
     *
     * @param jpaSession the jpa session
     */
    public PreLoginMappingAdapter(AbstractSession jpaSession) {
        this.jpaSession = jpaSession;
    }

    /* (non-Javadoc)
     * @see org.eclipse.persistence.internal.jaxb.SessionEventListener#preLogin(org.eclipse.persistence.sessions.SessionEvent)
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void preLogin(SessionEvent event) {
        Project project = event.getSession().getProject();
        ClassLoader cl = jpaSession.getDatasourcePlatform().getConversionManager().getLoader();
        DefaultXMLNameTransformer xmlNameTransformer = new DefaultXMLNameTransformer();
        for (Object descriptorAlias : project.getAliasDescriptors().keySet()) {
            ClassDescriptor descriptor = (ClassDescriptor) project.getAliasDescriptors().get(descriptorAlias);

            if (!PersistenceWeavedRest.class.isAssignableFrom(descriptor.getJavaClass())) {
                continue;
            }

            if (descriptor.isXMLDescriptor()) {
                XMLDescriptor xmlDescriptor = (XMLDescriptor) project.getAliasDescriptors().get(descriptorAlias);
                if (null != xmlDescriptor) {
                    if (null == xmlDescriptor.getDefaultRootElement()) {
                        // set root element
                        xmlDescriptor.setDefaultRootElement(xmlNameTransformer.transformRootElementName(xmlDescriptor.getJavaClass().getName()));
                        // set resultAlwaysXMLRoot to false, so that the elements are not wrapped in JAXBElements when unmarshalling.
                        xmlDescriptor.setResultAlwaysXMLRoot(false);
                    }
                }
            }

            XMLCompositeCollectionMapping relationshipMapping = new XMLCompositeCollectionMapping();
            relationshipMapping.setAttributeName("_persistence_relationshipInfo");
            relationshipMapping.setGetMethodName("_persistence_getRelationships");
            relationshipMapping.setSetMethodName("_persistence_setRelationships");
            relationshipMapping.setDescriptor(descriptor);
            CollectionContainerPolicy containerPolicy = new CollectionContainerPolicy(ArrayList.class);
            relationshipMapping.setContainerPolicy(containerPolicy);
            relationshipMapping.setField(new XMLField("_relationships"));
            relationshipMapping.setReferenceClass(Link.class);
            XMLJavaTypeConverter converter = new XMLJavaTypeConverter(RelationshipLinkAdapter.class);
            converter.initialize(relationshipMapping, event.getSession());
            relationshipMapping.setConverter(converter);
            descriptor.addMapping(relationshipMapping);

            XMLCompositeObjectMapping hrefMapping = new XMLCompositeObjectMapping();
            hrefMapping.setAttributeName("_persistence_href");
            hrefMapping.setGetMethodName("_persistence_getHref");
            hrefMapping.setSetMethodName("_persistence_setHref");
            hrefMapping.setDescriptor(descriptor);
            hrefMapping.setField(new XMLField("_link"));
            hrefMapping.setReferenceClass(Link.class);
            hrefMapping.setXPath(".");
            descriptor.addMapping(hrefMapping);

            XMLCompositeObjectMapping itemLinksMapping = new XMLCompositeObjectMapping();
            itemLinksMapping.setAttributeName("_persistence_links");
            itemLinksMapping.setGetMethodName("_persistence_getLinks");
            itemLinksMapping.setSetMethodName("_persistence_setLinks");
            itemLinksMapping.setDescriptor(descriptor);
            itemLinksMapping.setReferenceClass(ItemLinks.class);
            itemLinksMapping.setXPath(".");
            descriptor.addMapping(itemLinksMapping);

            ClassDescriptor jpaDescriptor = jpaSession.getDescriptorForAlias(descriptor.getAlias());

            Vector<DatabaseMapping> descriptorMappings = (Vector<DatabaseMapping>) descriptor.getMappings().clone();
            for (DatabaseMapping mapping : descriptorMappings) {
                if (mapping.isXMLMapping()) {
                    if (mapping.isAbstractCompositeObjectMapping() || mapping.isAbstractCompositeCollectionMapping()) {
                        if (mapping.isAbstractCompositeCollectionMapping()) {
                            XMLInverseReferenceMapping inverseMapping = ((XMLCompositeCollectionMapping) mapping).getInverseReferenceMapping();
                            if (inverseMapping != null) {
                                break;
                            }
                        } else if (mapping.isAbstractCompositeObjectMapping()) {
                            XMLInverseReferenceMapping inverseMapping = ((XMLCompositeObjectMapping) mapping).getInverseReferenceMapping();
                            if (inverseMapping != null) {
                                break;
                            }
                        }

                        if (jpaDescriptor != null) {
                            DatabaseMapping dbMapping = jpaDescriptor.getMappingForAttributeName(mapping.getAttributeName());
                            if ((dbMapping != null) && (dbMapping instanceof ForeignReferenceMapping)) {
                                ForeignReferenceMapping jpaMapping = (ForeignReferenceMapping) dbMapping;
                                if (jpaMapping.getMappedBy() != null) {
                                    ClassDescriptor inverseDescriptor = project.getDescriptorForAlias(jpaMapping.getReferenceDescriptor().getAlias());
                                    if (inverseDescriptor != null) {
                                        DatabaseMapping inverseMapping = inverseDescriptor.getMappingForAttributeName(jpaMapping.getMappedBy());
                                        if (inverseMapping != null) {
                                            convertMappingToXMLInverseReferenceMapping(inverseDescriptor, inverseMapping, jpaMapping);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            InheritancePolicy inheritancePolicy = descriptor.getInheritancePolicyOrNull();
            if ((inheritancePolicy != null) && (inheritancePolicy.isRootParentDescriptor())) {
                boolean isAbstract = Modifier.isAbstract(descriptor.getJavaClass().getModifiers());
                if (isAbstract) {
                    Class subClassToInstantiate = null;
                    Map<?, ?> classIndicatorMapping = inheritancePolicy.getClassIndicatorMapping();
                    // get one of subclasses that extends this abstract class
                    for (Map.Entry<?, ?> entry : classIndicatorMapping.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof Class) {
                            subClassToInstantiate = (Class) value;
                            isAbstract = Modifier.isAbstract(subClassToInstantiate.getModifiers());
                            if (!isAbstract) {
                                InstantiationPolicy instantiationPolicy = new InstantiationPolicy();
                                instantiationPolicy.useFactoryInstantiationPolicy(new ConcreteSubclassFactory(subClassToInstantiate), "createConcreteSubclass");
                                descriptor.setInstantiationPolicy(instantiationPolicy);
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (Object descriptorAlias : project.getAliasDescriptors().keySet()) {
            ClassDescriptor descriptor = (ClassDescriptor) project.getAliasDescriptors().get(descriptorAlias);
            ClassDescriptor jpaDescriptor = jpaSession.getDescriptorForAlias(descriptor.getAlias());
            Vector<DatabaseMapping> descriptorMappings = (Vector<DatabaseMapping>) descriptor.getMappings().clone();

            for (DatabaseMapping mapping : descriptorMappings) {
                if (mapping.isXMLMapping()) {
                    if (mapping.isAbstractCompositeObjectMapping() || mapping.isAbstractCompositeCollectionMapping()) {
                        if (jpaDescriptor != null) {
                            DatabaseMapping dbMapping = jpaDescriptor.getMappingForAttributeName(mapping.getAttributeName());
                            if ((dbMapping instanceof ForeignReferenceMapping)) {
                                ForeignReferenceMapping jpaMapping = (ForeignReferenceMapping) dbMapping;
                                ClassDescriptor jaxbDescriptor = project.getDescriptorForAlias(jpaMapping.getDescriptor().getAlias());
                                convertMappingToXMLChoiceMapping(jaxbDescriptor, jpaMapping, cl, jpaSession);
                            }
                        } else if (mapping instanceof XMLCompositeObjectMapping) {
                            // Fix for Bug 403113 - JPA-RS Isn't Serializing an Embeddable defined in an ElementCollection to JSON Correctly
                            // add choice mapping for one-to-one relationships within embeddables
                            // Based on (http://wiki.eclipse.org/EclipseLink/Examples/JPA/NoSQL#Step_2_:_Map_the_data),
                            // the mappedBy option on relationships is not supported for NoSQL data, so no need to add inverse mapping
                            XMLCompositeObjectMapping jpaMapping = (XMLCompositeObjectMapping) mapping;
                            ClassDescriptor jaxbDescriptor = project.getDescriptorForAlias(jpaMapping.getDescriptor().getAlias());
                            if (jaxbDescriptor != null) {
                                Class clazz = jpaMapping.getReferenceClass();
                                if (clazz != null) {
                                    if ((jpaSession.getDescriptor(clazz) != null) && (jpaSession.getDescriptor(clazz).isEISDescriptor()))
                                        convertMappingToXMLChoiceMapping(jaxbDescriptor, jpaMapping, cl, jpaSession);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Update the targetMapping to have the same accessor as the originMapping
     * @param originMapping
     * @param targetMapping
     */
    private static void copyAccessorToMapping(DatabaseMapping originMapping, DatabaseMapping targetMapping) {
        if (originMapping.getAttributeAccessor().isVirtualAttributeAccessor()) {
            VirtualAttributeAccessor accessor = new VirtualAttributeAccessor();
            accessor.setGetMethodName(originMapping.getGetMethodName());
            accessor.setSetMethodName(originMapping.getSetMethodName());
            targetMapping.setAttributeAccessor(accessor);
        }
        if (originMapping.getAttributeAccessor().isValuesAccessor()) {
            ValuesAccessor accessor = new ValuesAccessor(originMapping);
            accessor.setAttributeName(originMapping.getAttributeAccessor().getAttributeName());
            targetMapping.setAttributeAccessor(accessor);
        } else {
            targetMapping.setAttributeName(originMapping.getAttributeName());
            targetMapping.setGetMethodName(originMapping.getGetMethodName());
            targetMapping.setSetMethodName(originMapping.getSetMethodName());
        }
    }

    /**
     * Build an XMLInverseMapping based on a particular mapping and replace that mapping with
     * the newly created XMLInverseMapping in jaxbDescriptor
     * @param jaxbDescriptor
     * @param mapping
     * @param mappedBy
     */
    private static void convertMappingToXMLInverseReferenceMapping(ClassDescriptor jaxbDescriptor, DatabaseMapping mapping, ForeignReferenceMapping jpaMapping) {
        if ((mapping != null) && (jaxbDescriptor != null)) {
            if (!(mapping.isXMLMapping())) {
                return;
            }

            if ((jpaMapping.isAggregateCollectionMapping()) || (jpaMapping.isAggregateMapping())) {
                return;
            }

            XMLInverseReferenceMapping jaxbInverseMapping = new XMLInverseReferenceMapping();
            copyAccessorToMapping(mapping, jaxbInverseMapping);
            jaxbInverseMapping.setProperties(mapping.getProperties());
            jaxbInverseMapping.setIsReadOnly(mapping.isReadOnly());
            jaxbInverseMapping.setMappedBy(jpaMapping.getAttributeName());

            if (mapping.isAbstractCompositeCollectionMapping()) {
                jaxbInverseMapping.setContainerPolicy(mapping.getContainerPolicy());
                jaxbInverseMapping.setReferenceClass(((XMLCompositeCollectionMapping) mapping).getReferenceClass());
            } else if (mapping.isAbstractCompositeObjectMapping()) {
                jaxbInverseMapping.setReferenceClass(((XMLCompositeObjectMapping) mapping).getReferenceClass());
            }

            jaxbDescriptor.removeMappingForAttributeName(mapping.getAttributeName());
            jaxbDescriptor.addMapping(jaxbInverseMapping);
        }
    }

    /**
     * Build an XMLChoiceObjectMapping based on a particular mapping and replace that mapping with
     * the newly created XMLChoiceObjectMapping in jaxbDescriptor.
     * @param jaxbDescriptor the jaxb descriptor
     * @param jpaMapping the jpa mapping
     * @param cl the classloader
     */
    @SuppressWarnings("rawtypes")
    private static void convertMappingToXMLChoiceMapping(ClassDescriptor jaxbDescriptor, DatabaseMapping jpaMapping, ClassLoader cl, AbstractSession jpaSession) {
        if ((jpaMapping != null) && (jaxbDescriptor != null)) {
            if ((jpaMapping instanceof ForeignReferenceMapping) && ((jpaMapping.isAggregateCollectionMapping()) || (jpaMapping.isAggregateMapping()))) {
                // Fix for Bug 402385 - JPA-RS: ClassNotFound when using ElementCollection of Embeddables
                // Aggregates don't have identity to create links, thus no weaved REST adapters to insert choice mappings
                return;
            }

            String attributeName = jpaMapping.getAttributeName();
            DatabaseMapping jaxbMapping = jaxbDescriptor.getMappingForAttributeName(jpaMapping.getAttributeName());
            if (!(jaxbMapping.isXMLMapping() && (jaxbMapping.isAbstractCompositeCollectionMapping() || jaxbMapping.isAbstractCompositeObjectMapping()))) {
                return;
            }

            ClassDescriptor refDesc = null;

            if (jpaMapping instanceof ForeignReferenceMapping) {
                Class clazz = ((ForeignReferenceMapping) jpaMapping).getReferenceClass();
                refDesc = jpaSession.getDescriptor(clazz);
            } else if (jpaMapping instanceof XMLCompositeObjectMapping) {
                Class clazz = ((XMLCompositeObjectMapping) jpaMapping).getReferenceClass();
                refDesc = jpaSession.getDescriptor(clazz);
            }

            if (refDesc == null) {
                return;
            }

            String adapterClassName = RestAdapterClassWriter.constructClassNameForReferenceAdapter(refDesc.getJavaClassName());
            if (adapterClassName != null) {
                try {
                    if (jaxbMapping.isAbstractCompositeObjectMapping()) {
                        XMLChoiceObjectMapping xmlChoiceMapping = new XMLChoiceObjectMapping();
                        xmlChoiceMapping.setAttributeName(attributeName);
                        copyAccessorToMapping(jaxbMapping, xmlChoiceMapping);
                        xmlChoiceMapping.setProperties(jaxbMapping.getProperties());

                        XMLCompositeObjectMapping compositeMapping = (XMLCompositeObjectMapping) jaxbMapping;
                        xmlChoiceMapping.addChoiceElement(compositeMapping.getXPath(), Link.class);
                        xmlChoiceMapping.addChoiceElement(compositeMapping.getXPath(), refDesc.getJavaClass());

                        xmlChoiceMapping.setConverter(new XMLJavaTypeConverter(Class.forName(adapterClassName, true, cl)));
                        jaxbDescriptor.removeMappingForAttributeName(jaxbMapping.getAttributeName());
                        jaxbDescriptor.addMapping(xmlChoiceMapping);

                    } else if (jaxbMapping.isAbstractCompositeCollectionMapping()) {
                        XMLChoiceCollectionMapping xmlChoiceMapping = new XMLChoiceCollectionMapping();
                        xmlChoiceMapping.setAttributeName(attributeName);
                        copyAccessorToMapping(jaxbMapping, xmlChoiceMapping);
                        xmlChoiceMapping.setProperties(jaxbMapping.getProperties());

                        XMLCompositeCollectionMapping compositeMapping = (XMLCompositeCollectionMapping) jaxbMapping;
                        xmlChoiceMapping.addChoiceElement(compositeMapping.getXPath(), Link.class);
                        xmlChoiceMapping.addChoiceElement(compositeMapping.getXPath(), refDesc.getJavaClass());

                        xmlChoiceMapping.setContainerPolicy(jaxbMapping.getContainerPolicy());
                        xmlChoiceMapping.setConverter(new XMLJavaTypeConverter(Class.forName(adapterClassName, true, cl)));
                        jaxbDescriptor.removeMappingForAttributeName(jaxbMapping.getAttributeName());
                        jaxbDescriptor.addMapping(xmlChoiceMapping);
                    }
                } catch (Exception ex) {
                    throw JPARSException.exceptionOccurred(ex);
                }
            }
        }
    }
}
