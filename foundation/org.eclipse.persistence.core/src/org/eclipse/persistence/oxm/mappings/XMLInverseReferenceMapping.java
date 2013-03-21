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
 *     rbarkhouse - 2009-11-26 13:04:58 - 2.0 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm.mappings;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.mappings.InverseReferenceMapping;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.ListContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.remote.DistributedSession;

/**
 * This mapping is used to map a back-pointer.  It represents the "opposite" of one of the
 * following relationship mappings:<br><br>
 *
 * <ul>
 * <li>XMLCompositeObjectMapping
 * <li>XMLCompositeCollectionMapping
 * <li>XMLObjectReferenceMapping
 * <li>XMLCollectionReferenceMapping
 * </ul>
 *
 * When configuring an XMLInverseReferenceMapping, the "mappedBy" field must be set to the
 * field on the reference class that maps to this Descriptor.  For example:<br><br>
 *
 * <code>
 * // EMPLOYEE has a collection of PHONEs (phoneNumbers)<br>
 * // PHONE has a back-pointer to EMPLOYEE (owningEmployee)<br><br>
 *
 * // EMPLOYEE Descriptor<br>
 * XMLCompositeCollectionMapping phone = new XMLCompositeCollectionMapping();<br>
 * phone.setReferenceClassName("org.example.PhoneNumber");<br>
 * phone.setAttributeName("phoneNumbers");<br>
 * ...<br><br>
 *
 * // PHONE Descriptor<br>
 * XMLInverseReferenceMapping owningEmployee = new XMLInverseReferenceMapping();<br>
 * owningEmployee.setReferenceClassName("org.example.Employee");<br>
 * owningEmployee.setMappedBy("phoneNumbers");<br>
 * owningEmployee.setAttributeName("owningEmployee");<br>
 * ...<br>
 * </code>
 */
public class XMLInverseReferenceMapping extends AggregateMapping implements InverseReferenceMapping<AbstractSession, AttributeAccessor, ContainerPolicy, ClassDescriptor, DatabaseField, DatabaseMapping, XMLRecord>, ContainerMapping {

    private String mappedBy;
    private ContainerPolicy containerPolicy;
    private DatabaseMapping inlineMapping;    

    @Override
    public boolean isXMLMapping() {
        return true;
    }

    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        setFields(new Vector<DatabaseField> ());
        if(inlineMapping != null){        	
        	inlineMapping.initialize(session);
        }
    }

    public void preInitialize(AbstractSession session){
    	super.preInitialize(session);
    	if(inlineMapping != null){
    		inlineMapping.setDescriptor(this.descriptor);
    		inlineMapping.preInitialize(session);
    	}
    }
    
    @Override
    public void postInitialize(AbstractSession session) throws DescriptorException {
        // Get the corresponding mapping from the reference descriptor and set up the
        // inverse mapping.
        DatabaseMapping mapping = getReferenceDescriptor().getMappingForAttributeName(this.mappedBy);

        if (mapping instanceof XMLInverseReferenceMapping) {        
        	mapping  = ((XMLInverseReferenceMapping)mapping).getInlineMapping();
        }

        if (mapping instanceof XMLCompositeCollectionMapping) {
            XMLCompositeCollectionMapping oppositeMapping = (XMLCompositeCollectionMapping) mapping;
            oppositeMapping.setInverseReferenceMapping(this);
        }

        if (mapping instanceof XMLCompositeObjectMapping) {
            XMLCompositeObjectMapping oppositeMapping = (XMLCompositeObjectMapping) mapping;
            oppositeMapping.setInverseReferenceMapping(this);
        }

        if (mapping instanceof XMLObjectReferenceMapping) {
            XMLObjectReferenceMapping oppositeMapping = (XMLObjectReferenceMapping) mapping;
            oppositeMapping.setInverseReferenceMapping(this);
        }
        
        if (mapping instanceof XMLChoiceObjectMapping) {
            XMLChoiceObjectMapping oppositeMapping = (XMLChoiceObjectMapping) mapping;
            Collection<XMLMapping> nestedMappings = oppositeMapping.getChoiceElementMappings().values();
            for(XMLMapping next:nestedMappings) {
                if(next instanceof XMLCompositeObjectMapping) {
                    XMLCompositeObjectMapping compositeMapping = ((XMLCompositeObjectMapping)next);
                    if(compositeMapping.getReferenceClass() == this.getDescriptor().getJavaClass() || this.getDescriptor().getJavaClass().isAssignableFrom(compositeMapping.getReferenceClass())) {
                        compositeMapping.setInverseReferenceMapping(this);
                    }
                } else if(next instanceof XMLObjectReferenceMapping) {
                    XMLObjectReferenceMapping refMapping = ((XMLObjectReferenceMapping)next);
                    if(refMapping.getReferenceClass() == this.getDescriptor().getJavaClass()) {
                        refMapping.setInverseReferenceMapping(this);
                    }
                }
            }
        }

        if (mapping instanceof XMLChoiceCollectionMapping) {
            XMLChoiceCollectionMapping oppositeMapping = (XMLChoiceCollectionMapping) mapping;
            Collection<XMLMapping> nestedMappings = oppositeMapping.getChoiceElementMappings().values();
            for(XMLMapping next:nestedMappings) {
                if(next instanceof XMLCompositeCollectionMapping) {
                    XMLCompositeCollectionMapping compositeMapping = ((XMLCompositeCollectionMapping)next);
                    if(compositeMapping.getReferenceClass() == this.getDescriptor().getJavaClass() || this.getDescriptor().getJavaClass().isAssignableFrom(compositeMapping.getReferenceClass())) {
                        compositeMapping.setInverseReferenceMapping(this);
                    }
                } else if(next instanceof XMLCollectionReferenceMapping) {
                    XMLCollectionReferenceMapping refMapping = ((XMLCollectionReferenceMapping)next);
                    if(refMapping.getReferenceClass() == this.getDescriptor().getJavaClass()) {
                        refMapping.setInverseReferenceMapping(this);
                    }
                }
            }
        }
        
    	if(inlineMapping != null){
    		inlineMapping.postInitialize(session);
    	}
    }

    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    // == AggregateMapping methods ============================================

    @Override
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
    }

    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession) {
    }

    @Override
    public void buildCloneFromRow(AbstractRecord databaseRow,
            JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey,
            ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork,
            AbstractSession executionSession) {
    }

    @Override
    public void cascadePerformRemoveIfRequired(Object object,
            UnitOfWorkImpl uow, Map visitedObjects) {
    }

    @Override
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow,
            Map visitedObjects) {
    }

    @Override
    public ChangeRecord compareForChange(Object clone, Object backup,
            ObjectChangeSet owner, AbstractSession session) {
        return null;
    }

    @Override
    public boolean compareObjects(Object firstObject, Object secondObject,
            AbstractSession session) {
        return false;
    }

    @Override
    public void fixObjectReferences(Object object, Map objectDescriptors,
            Map processedObjects, ObjectLevelReadQuery query,
            DistributedSession session) {
    }

    @Override
    public void iterate(DescriptorIterator iterator) {
    }

    @Override
    public void mergeChangesIntoObject(Object target,
            ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
    }

    @Override
    public void mergeIntoObject(Object target, boolean isTargetUninitialized,
            Object source, MergeManager mergeManager, AbstractSession targetSession) {
    }

    // == ContainerPolicy methods =============================================

    public void setContainerPolicy(ContainerPolicy containerPolicy) {
        this.containerPolicy = containerPolicy;
    }

    public ContainerPolicy getContainerPolicy() {
        return this.containerPolicy;
    }

    public void useCollectionClass(Class concreteClass) {
        this.containerPolicy = new CollectionContainerPolicy(concreteClass);
    }

    public void useCollectionClassName(String concreteClass) {
        this.containerPolicy = new CollectionContainerPolicy(concreteClass);
    }

    public void useListClassName(String concreteClass) {
        this.containerPolicy = new ListContainerPolicy(concreteClass);
    }

    public void useMapClass(Class concreteClass, String methodName) {
        this.containerPolicy = new MapContainerPolicy(concreteClass);
    }

    public void useMapClassName(String concreteClass, String methodName) {
        this.containerPolicy = new MapContainerPolicy(concreteClass);
    }

    public DatabaseMapping getInlineMapping() {
        return inlineMapping;
    }

    public void setInlineMapping(DatabaseMapping inlineMapping) {
        this.inlineMapping = inlineMapping;
    }
    
	@Override
    public void writeSingleValue(Object value, Object object, XMLRecord record, AbstractSession session) {
    }

}