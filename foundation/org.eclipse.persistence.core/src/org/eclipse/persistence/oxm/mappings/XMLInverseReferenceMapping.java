/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.ContainerMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.remote.RemoteSession;

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
public class XMLInverseReferenceMapping extends AggregateMapping implements ContainerMapping {

    private String mappedBy;
    private ContainerPolicy containerPolicy;

    @Override
    public boolean isXMLMapping() {
        return true;
    }

    @Override
    public void initialize(AbstractSession session) throws DescriptorException {
        super.initialize(session);
        setFields(new Vector<DatabaseField> ());
    }

    @Override
    public void postInitialize(AbstractSession session) throws DescriptorException {
        // Get the corresponding mapping from the reference descriptor and set up the
        // inverse mapping.
        DatabaseMapping mapping = getReferenceDescriptor().getMappingForAttributeName(this.mappedBy);

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
    public void buildClone(Object original, Object clone, UnitOfWorkImpl unitOfWork) {
    }

    @Override
    public void buildCloneFromRow(AbstractRecord databaseRow,
            JoinedAttributeManager joinManager, Object clone,
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
            RemoteSession session) {
    }

    @Override
    public void iterate(DescriptorIterator iterator) {
    }

    @Override
    public void mergeChangesIntoObject(Object target,
            ChangeRecord changeRecord, Object source, MergeManager mergeManager) {
    }

    @Override
    public void mergeIntoObject(Object target, boolean isTargetUninitialized,
            Object source, MergeManager mergeManager) {
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

    public void useMapClass(Class concreteClass, String methodName) {
        this.containerPolicy = new MapContainerPolicy(concreteClass);
    }

}