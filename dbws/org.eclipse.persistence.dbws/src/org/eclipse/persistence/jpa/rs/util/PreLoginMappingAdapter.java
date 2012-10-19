/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware -  Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.VirtualAttributeAccessor;
import org.eclipse.persistence.internal.dynamic.ValuesAccessor;
import org.eclipse.persistence.internal.jaxb.SessionEventListener;
import org.eclipse.persistence.internal.jaxb.XMLJavaTypeConverter;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.jpa.weaving.RestAdapterClassWriter;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
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

    public PreLoginMappingAdapter(AbstractSession jpaSession){
        this.jpaSession = jpaSession;
    }

    @SuppressWarnings({ "unchecked" })
    public void preLogin(SessionEvent event) {
        Project project = event.getSession().getProject();
        for (Object descriptorAlias: project.getAliasDescriptors().keySet()){
            ClassDescriptor descriptor = (ClassDescriptor)project.getAliasDescriptors().get(descriptorAlias);
            Class<?> descriptorClass = descriptor.getJavaClass();
            if (PersistenceWeavedRest.class.isAssignableFrom(descriptorClass)){
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
            }

            ClassDescriptor jpaDescriptor = jpaSession.getDescriptorForAlias(descriptor.getAlias());
            Vector<DatabaseMapping> descriptorMappings = (Vector<DatabaseMapping>) descriptor.getMappings().clone();
            for (DatabaseMapping mapping: descriptorMappings){
                if (mapping.isXMLMapping()){
                    if (mapping.isAbstractCompositeObjectMapping() || mapping.isAbstractCompositeCollectionMapping()){
                        if (mapping.isAbstractCompositeCollectionMapping()){
                            XMLInverseReferenceMapping inverseMapping = ((XMLCompositeCollectionMapping)mapping).getInverseReferenceMapping();
                            if (inverseMapping != null){
                                break;
                            }
                        } else  if (mapping.isAbstractCompositeObjectMapping()){
                            XMLInverseReferenceMapping inverseMapping = ((XMLCompositeObjectMapping)mapping).getInverseReferenceMapping();
                            if (inverseMapping != null){
                                break;
                            }
                        }

                        if (jpaDescriptor != null) {
                            DatabaseMapping dbMapping = jpaDescriptor.getMappingForAttributeName(mapping.getAttributeName());
                            if ((dbMapping != null) && (dbMapping instanceof ForeignReferenceMapping)) {
                                ForeignReferenceMapping jpaMapping = (ForeignReferenceMapping) dbMapping;
                                if (jpaMapping != null) {
                                    if (jpaMapping.getMappedBy() != null) {
                                        ClassDescriptor inverseDescriptor = project.getDescriptorForAlias(jpaMapping.getReferenceDescriptor().getAlias());
                                        DatabaseMapping inverseMapping = inverseDescriptor.getMappingForAttributeName(jpaMapping.getMappedBy());
                                        convertMappingToXMLInverseReferenceMapping(inverseDescriptor, inverseMapping, jpaMapping.getAttributeName());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ClassLoader cl = jpaSession.getPlatform().getConversionManager().getLoader();
            descriptorMappings = (Vector<DatabaseMapping>) descriptor.getMappings().clone();

            for (DatabaseMapping mapping : descriptorMappings) {
                if (jpaDescriptor != null && mapping.isXMLMapping()){
                    if (mapping.isAbstractCompositeObjectMapping() || mapping.isAbstractCompositeCollectionMapping()) {
                        DatabaseMapping dbMapping = jpaDescriptor.getMappingForAttributeName(mapping.getAttributeName());
                        if ((dbMapping != null) && (dbMapping instanceof ForeignReferenceMapping)) {
                            ForeignReferenceMapping jpaMapping = (ForeignReferenceMapping) dbMapping;
                            if (jpaMapping != null) {
                                // Convert all ForeignReferenceMappings that are visible in JPA
                                // to ChoiceMapping to allow a link to be returned instead of the whole Object
                                // XMLInverseMappings are ignored in JAXB, so we should not convert those
                                convertMappingToXMLChoiceMapping(descriptor, jpaMapping, cl);
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
    public static void copyAccessorToMapping(DatabaseMapping originMapping, DatabaseMapping targetMapping){
        if (originMapping.getAttributeAccessor().isVirtualAttributeAccessor()){
            VirtualAttributeAccessor accessor = new VirtualAttributeAccessor();
            accessor.setGetMethodName(originMapping.getGetMethodName());
            accessor.setSetMethodName(originMapping.getSetMethodName());
            targetMapping.setAttributeAccessor(accessor);
        } if (originMapping.getAttributeAccessor().isValuesAccessor()){
            ValuesAccessor accessor = new ValuesAccessor(originMapping);
            accessor.setAttributeName(originMapping.getAttributeAccessor().getAttributeName());
            targetMapping.setAttributeAccessor(accessor);
        }else {
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
    protected static void convertMappingToXMLInverseReferenceMapping(ClassDescriptor jaxbDescriptor, DatabaseMapping mapping, String mappedBy){
        if (!(mapping.isXMLMapping() && (mapping.isAbstractCompositeCollectionMapping() || mapping.isAbstractCompositeObjectMapping()))){
            return;
        }
        XMLInverseReferenceMapping jaxbInverseMapping = new XMLInverseReferenceMapping();
        copyAccessorToMapping(mapping, jaxbInverseMapping);
        jaxbInverseMapping.setProperties(mapping.getProperties());
        jaxbInverseMapping.setIsReadOnly(mapping.isReadOnly());
        jaxbInverseMapping.setMappedBy(mappedBy);
        if (mapping.isAbstractCompositeCollectionMapping()){
            jaxbInverseMapping.setContainerPolicy(mapping.getContainerPolicy());
            jaxbInverseMapping.setReferenceClass(((XMLCompositeCollectionMapping)mapping).getReferenceClass());
        } else if (mapping.isAbstractCompositeObjectMapping()){
            jaxbInverseMapping.setReferenceClass(((XMLCompositeObjectMapping)mapping).getReferenceClass());
        }
        jaxbDescriptor.removeMappingForAttributeName(mapping.getAttributeName());
        jaxbDescriptor.addMapping(jaxbInverseMapping);
    }

    /**
     * Build an XMLChoiceObjectMapping based on a particular mapping and replace that mapping with
     * the newly created XMLChoiceObjectMapping in jaxbDescriptor.
     * @param jaxbDescriptor the jaxb descriptor
     * @param jpaMapping the jpa mapping
     * @param cl the classloader
     */
    protected static void convertMappingToXMLChoiceMapping(ClassDescriptor jaxbDescriptor, DatabaseMapping jpaMapping, ClassLoader cl) {
        if ((jpaMapping != null) && (jaxbDescriptor != null)) {
            DatabaseMapping jaxbMapping = jaxbDescriptor.getMappingForAttributeName(jpaMapping.getAttributeName());
            if (!(jaxbMapping.isXMLMapping() && (jaxbMapping.isAbstractCompositeCollectionMapping() || jaxbMapping.isAbstractCompositeObjectMapping()))) {
                return;
            }
            String attributeName = jpaMapping.getAttributeName();
            String adapterClassName = jpaMapping.getReferenceDescriptor().getJavaClassName() + "." + RestAdapterClassWriter.ADAPTER_INNER_CLASS_NAME;
            try {
                if (jaxbMapping.isAbstractCompositeObjectMapping()) {
                    XMLChoiceObjectMapping xmlChoiceMapping = new XMLChoiceObjectMapping();
                    xmlChoiceMapping.setAttributeName(attributeName);
                    copyAccessorToMapping(jaxbMapping, xmlChoiceMapping);
                    xmlChoiceMapping.setProperties(jaxbMapping.getProperties());
                    xmlChoiceMapping.addChoiceElement(attributeName, jpaMapping.getReferenceDescriptor().getJavaClass());
                    xmlChoiceMapping.addChoiceElement(attributeName, Link.class);
                    xmlChoiceMapping.setConverter(new XMLJavaTypeConverter(Class.forName(adapterClassName, true, cl)));
                    jaxbDescriptor.removeMappingForAttributeName(jaxbMapping.getAttributeName());
                    jaxbDescriptor.addMapping(xmlChoiceMapping);
                } else if (jaxbMapping.isAbstractCompositeCollectionMapping()) {
                    XMLChoiceCollectionMapping xmlChoiceMapping = new XMLChoiceCollectionMapping();
                    xmlChoiceMapping.setAttributeName(attributeName);
                    copyAccessorToMapping(jaxbMapping, xmlChoiceMapping);
                    xmlChoiceMapping.setProperties(jaxbMapping.getProperties());
                    xmlChoiceMapping.addChoiceElement(attributeName, Link.class);
                    xmlChoiceMapping.addChoiceElement(attributeName, jpaMapping.getReferenceDescriptor().getJavaClass());
                    xmlChoiceMapping.setConverter(new XMLJavaTypeConverter(Class.forName(adapterClassName, true, cl)));
                    jaxbDescriptor.removeMappingForAttributeName(jaxbMapping.getAttributeName());
                    jaxbDescriptor.addMapping(xmlChoiceMapping);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}