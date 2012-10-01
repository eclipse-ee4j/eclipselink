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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.jaxb.SessionEventListener;
import org.eclipse.persistence.internal.jaxb.XMLJavaTypeConverter;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.weaving.PersistenceWeavedRest;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.oxm.XMLField;
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
    
    public void preLogin(SessionEvent event) {
        Project project = event.getSession().getProject();
        for (Object descriptorAlias: project.getAliasDescriptors().keySet()){
            ClassDescriptor descriptor = (ClassDescriptor)project.getAliasDescriptors().get(descriptorAlias);
            Class descriptorClass = descriptor.getJavaClass();
            if (!DynamicEntity.class.isAssignableFrom(descriptorClass) && PersistenceWeavedRest.class.isAssignableFrom(descriptorClass)){
                XMLCompositeCollectionMapping relationshipMapping = new XMLCompositeCollectionMapping();
                InstanceVariableAttributeAccessor accessor = new InstanceVariableAttributeAccessor();
                accessor.setAttributeName("_persistence_relationshipInfo");
                relationshipMapping.setAttributeAccessor(accessor);
                relationshipMapping.setAttributeName("_persistence_relationshipInfo");
                relationshipMapping.setDescriptor(descriptor);
                CollectionContainerPolicy containerPolicy = new CollectionContainerPolicy(ArrayList.class);
                relationshipMapping.setContainerPolicy(containerPolicy);
                relationshipMapping.setField(new XMLField("_relationships"));
                relationshipMapping.setReferenceClass(Link.class);
                XMLJavaTypeConverter converter = new XMLJavaTypeConverter(RelationshipLinkAdapter.class);
                converter.initialize(relationshipMapping, event.getSession());
                relationshipMapping.setConverter(converter);
                descriptor.addMapping(relationshipMapping);
                
            }
            ClassDescriptor jpaDescriptor = jpaSession.getDescriptorForAlias(descriptor.getAlias());
            for (DatabaseMapping mapping: descriptor.getMappings()){
                if (mapping.isXMLMapping()){
                    if (mapping.isAbstractCompositeObjectMapping() || mapping.isAbstractCompositeCollectionMapping()){
                        if (mapping.isAbstractCompositeCollectionMapping()){
                            if (((XMLCompositeCollectionMapping)mapping).getInverseReferenceMapping() != null){
                                break;
                            }
                        } else  if (mapping.isAbstractCompositeObjectMapping()){
                            XMLCompositeObjectMapping compositeMapping = (XMLCompositeObjectMapping)mapping;
                            if (compositeMapping.getField().getName().equals("persistence_href")){
                                compositeMapping.getField().setName("_href");
                            } 
                            if (((XMLCompositeObjectMapping)mapping).getInverseReferenceMapping() != null){
                                break;
                            }
                        }
                        
                        if (jpaDescriptor != null){
                            ForeignReferenceMapping jpaMapping = (ForeignReferenceMapping)jpaDescriptor.getMappingForAttributeName(mapping.getAttributeName());
    
                            if (jpaMapping != null && jpaMapping.getMappedBy() != null){
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
        jaxbInverseMapping.setAttributeName(mapping.getAttributeName());
        jaxbInverseMapping.setGetMethodName(mapping.getGetMethodName());
        jaxbInverseMapping.setSetMethodName(mapping.getSetMethodName());

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
    
}