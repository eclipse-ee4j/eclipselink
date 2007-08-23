/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions.coordination;

import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.queries.*;
import deprecated.xml.*;
import deprecated.sdk.*;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.*;
import org.eclipse.persistence.sessions.coordination.MergeChangeSetCommand;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>:
 * <p> Provide XML conversion for MergeChangeSetCommand that can be send to non-toplink or non-java applications
 * <b>Description</b>:
 * <p> SDK project
 * <b>Responsibilities</b>:
 * <ul>
 * <li> SDK mappings of MergeChangeSetCommand
 * </ul>
 * @author Steven Vo
 * @since OracleAS TopLink 10<i>g</i> (9.0.4)
 * @see CommandConverter
 */
public class CommandProject extends Project {

    /**
     * Default constructor.
     */
    public CommandProject() {
        super();
        this.initialize();
    }

    /**
     * Build the project name.
     */
    public String buildName() {
        return "XML Synchronize Cache Command";
    }

    /**
     * Build the login settings.
     */
    public Login buildLogin() {

        /*
        XMLStreamLogin login = new XMLStreamLogin();
        login.setAccessorClass(XMLStreamAccessor.class);
        */
        XMLFileLogin login = new XMLFileLogin();

        // the default is the user's temporary directory;
        // to change, use the following:
        login.setBaseDirectoryName((new java.io.File("../")).getAbsolutePath());

        // set up sequence table
        login.setSequenceRootElementName("sequence");
        login.setSequenceNameElementName("name");
        login.setSequenceCounterElementName("count");

        // create the directories if they don't already exist
        login.createDirectoriesAsNeeded();

        return login;
    }

    /**
     * Initialize the project.
     */
    public void initialize() {
        this.setName(this.buildName());
        this.setLogin(this.buildLogin());

        this.addDescriptor(this.buildMergeChangeSetCommandDescriptor());
        this.addDescriptor(this.buildUnitOfWorkChangeSetDescriptor());
        this.addDescriptor(this.buildObjectChangeSetDescriptor());
        this.addDescriptor(this.buildAggregateObjectChangeSetDescriptor());

        this.addDescriptor(this.buildChangeRecordDescriptor());
        this.addDescriptor(this.buildDirectToFieldChangeRecordDescriptor());
        this.addDescriptor(this.buildDirectMapChangeRecordDescriptor());
        this.addDescriptor(this.buildDirectCollectionChangeRecordDescriptor());
        this.addDescriptor(this.buildObjectReferenceChangeRecord());
        this.addDescriptor(this.buildCollectionChangeRecordDescriptor());
        this.addDescriptor(this.buildAggregateChangeRecordDescriptor());
        this.addDescriptor(this.buildAggregateCollectionChangeRecordDescriptor());
        this.addDescriptor(this.buildTransformationMappingChangeRecord());
        this.addDescriptor(this.buildAssociationDescriptor());

    }

    public ClassDescriptor buildMergeChangeSetCommandDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MergeChangeSetCommand.class);
        descriptor.setRootElementName("command");
        descriptor.setPrimaryKeyElementName("name");

        DirectToFieldMapping sourceSessionIdMapping = new DirectToFieldMapping();
        sourceSessionIdMapping.setAttributeName("idForSDK");
        sourceSessionIdMapping.setGetMethodName("getIdForSDK");
        sourceSessionIdMapping.setSetMethodName("setIdForSDK");
        sourceSessionIdMapping.setFieldName("name");
        descriptor.addMapping(sourceSessionIdMapping);

        SDKAggregateObjectMapping changeSetMapping = new SDKAggregateObjectMapping();
        changeSetMapping.setReferenceClass(UnitOfWorkChangeSet.class);
        changeSetMapping.setAttributeName("changeSet");
        changeSetMapping.setGetMethodName("getChangeSet");
        changeSetMapping.setSetMethodName("setChangeSet");
        changeSetMapping.setFieldName("change-set");
        descriptor.addMapping(changeSetMapping);

        return descriptor;
    }

    public ClassDescriptor buildUnitOfWorkChangeSetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(UnitOfWorkChangeSet.class);
        descriptor.setRootElementName("uow-change-set");
        descriptor.descriptorIsAggregate();

        // This is not the right collection - need to determine what to map
        SDKObjectCollectionMapping objectChangesMapping = new SDKObjectCollectionMapping();
        objectChangesMapping.setReferenceClass(ObjectChangeSet.class);
        objectChangesMapping.setUsesIndirection(false);
        objectChangesMapping.setAttributeName("objectChanges");
        objectChangesMapping.setGetMethodName("getInternalAllChangeSets");
        objectChangesMapping.setSetMethodName("setInternalAllChangeSets");
        objectChangesMapping.setFieldName("change-sets");
        objectChangesMapping.setReferenceDataTypeName("object-change-set");
        objectChangesMapping.setSourceForeignKeyFieldName("id");
        objectChangesMapping.setSelectionCall(new XMLReadAllCall(objectChangesMapping));
        descriptor.addMapping(objectChangesMapping);

        return descriptor;
    }

    public ClassDescriptor buildObjectChangeSetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectChangeSet.class);
        descriptor.setRootElementName("object-change-set");
        descriptor.setPrimaryKeyElementName("id");

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setGetMethodName("getId");
        idMapping.setSetMethodName("setId");
        idMapping.setFieldName("id");
        descriptor.addMapping(idMapping);

        SDKDirectCollectionMapping cacheKeyMapping = new SDKDirectCollectionMapping();
        cacheKeyMapping.setAttributeName("cacheKey");
        cacheKeyMapping.setGetMethodName("getPrimaryKeys");
        cacheKeyMapping.setSetMethodName("setPrimaryKeys");
        cacheKeyMapping.setFieldName("primary-key");
        cacheKeyMapping.setElementDataTypeName("value");
        descriptor.addMapping(cacheKeyMapping);

        DirectToFieldMapping classNameMapping = new DirectToFieldMapping();
        classNameMapping.setAttributeName("className");
        classNameMapping.setGetMethodName("getClassName");
        classNameMapping.setSetMethodName("setClassName");
        classNameMapping.setFieldName("class-name");
        descriptor.addMapping(classNameMapping);

        DirectToFieldMapping isNewMapping = new DirectToFieldMapping();
        isNewMapping.setAttributeName("isNew");
        isNewMapping.setGetMethodName("isNew");
        isNewMapping.setSetMethodName("setIsNew");
        isNewMapping.setFieldName("is-new");
        descriptor.addMapping(isNewMapping);

        DirectToFieldMapping shouldBeDeletedMapping = new DirectToFieldMapping();
        shouldBeDeletedMapping.setAttributeName("shouldBeDeleted");
        shouldBeDeletedMapping.setGetMethodName("shouldBeDeleted");
        shouldBeDeletedMapping.setSetMethodName("setShouldBeDeleted");
        shouldBeDeletedMapping.setFieldName("is-deleted");
        descriptor.addMapping(shouldBeDeletedMapping);

        DirectToFieldMapping isAggregateMapping = new DirectToFieldMapping();
        isAggregateMapping.setAttributeName("isAggregate");
        isAggregateMapping.setGetMethodName("isAggregate");
        isAggregateMapping.setSetMethodName("setIsAggregate");
        isAggregateMapping.setFieldName("is-aggregate");
        descriptor.addMapping(isAggregateMapping);

        DirectToFieldMapping versionMapping = new DirectToFieldMapping();
        versionMapping.setAttributeName("version");
        versionMapping.setGetMethodName("getWriteLockValue");
        versionMapping.setSetMethodName("setWriteLockValue");
        versionMapping.setFieldName("version");
        descriptor.addMapping(versionMapping);

        DirectToFieldMapping oldMapKeyMapping = new DirectToFieldMapping();
        oldMapKeyMapping.setAttributeName("oldKey");
        oldMapKeyMapping.setGetMethodName("getOldKey");
        oldMapKeyMapping.setSetMethodName("setOldKey");
        oldMapKeyMapping.setFieldName("old-map-key");
        descriptor.addMapping(oldMapKeyMapping);

        DirectToFieldMapping newMapKeyMapping = new DirectToFieldMapping();
        newMapKeyMapping.setAttributeName("newKey");
        newMapKeyMapping.setGetMethodName("getNewKey");
        newMapKeyMapping.setSetMethodName("setNewKey");
        newMapKeyMapping.setFieldName("new-map-key");
        descriptor.addMapping(newMapKeyMapping);

        SDKAggregateCollectionMapping changesMapping = new SDKAggregateCollectionMapping();
        changesMapping.setAttributeName("changes");
        changesMapping.setReferenceClass(ChangeRecord.class);
        changesMapping.setGetMethodName("getSDKChanges");
        changesMapping.setSetMethodName("setSDKChanges");
        changesMapping.setFieldName("changes");
        descriptor.addMapping(changesMapping);

        return descriptor;
    }

    public ClassDescriptor buildAggregateObjectChangeSetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AggregateObjectChangeSet.class);
        descriptor.setRootElementName("aggregate-object-change-set");
        descriptor.descriptorIsAggregate();

        DirectToFieldMapping classNameMapping = new DirectToFieldMapping();
        classNameMapping.setAttributeName("className");
        classNameMapping.setGetMethodName("getClassName");
        classNameMapping.setSetMethodName("setClassName");
        classNameMapping.setFieldName("class-name");
        descriptor.addMapping(classNameMapping);

        DirectToFieldMapping isNewMapping = new DirectToFieldMapping();
        isNewMapping.setAttributeName("isNew");
        isNewMapping.setGetMethodName("isNew");
        isNewMapping.setSetMethodName("setIsNew");
        isNewMapping.setFieldName("is-new");
        descriptor.addMapping(isNewMapping);

        DirectToFieldMapping shouldBeDeletedMapping = new DirectToFieldMapping();
        shouldBeDeletedMapping.setAttributeName("shouldBeDeleted;");
        shouldBeDeletedMapping.setGetMethodName("shouldBeDeleted");
        shouldBeDeletedMapping.setSetMethodName("setShouldBeDeleted");
        shouldBeDeletedMapping.setFieldName("is-deleted");
        descriptor.addMapping(shouldBeDeletedMapping);

        SDKAggregateCollectionMapping changesMapping = new SDKAggregateCollectionMapping();
        changesMapping.setAttributeName("changes");
        changesMapping.setReferenceClass(ChangeRecord.class);
        changesMapping.setGetMethodName("getChanges");
        changesMapping.setSetMethodName("setChanges");
        changesMapping.setFieldName("changes");
        descriptor.addMapping(changesMapping);

        return descriptor;
    }

    public ClassDescriptor buildChangeRecordDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ChangeRecord.class);
        descriptor.descriptorIsAggregate();
        descriptor.setRootElementName("change-record");

        // inheritance stuffss
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().addClassIndicator(ChangeRecord.class, "change-record-type");
        descriptor.getInheritancePolicy().addClassIndicator(DirectToFieldChangeRecord.class, "direct-to-field-type");
        descriptor.getInheritancePolicy().addClassIndicator(DirectMapChangeRecord.class, "direct-map-type");
        descriptor.getInheritancePolicy().addClassIndicator(DirectCollectionChangeRecord.class, "direct-collection-type");
        descriptor.getInheritancePolicy().addClassIndicator(ObjectReferenceChangeRecord.class, "object-reference-type");
        descriptor.getInheritancePolicy().addClassIndicator(CollectionChangeRecord.class, "collection-type");
        descriptor.getInheritancePolicy().addClassIndicator(AggregateChangeRecord.class, "aggregate-object-type");
        descriptor.getInheritancePolicy().addClassIndicator(AggregateCollectionChangeRecord.class, "aggregate-collection-type");
        descriptor.getInheritancePolicy().addClassIndicator(TransformationMappingChangeRecord.class, "transformation-type");

        DirectToFieldMapping attributeMapping = new DirectToFieldMapping();
        attributeMapping.setAttributeName("attibute");
        attributeMapping.setGetMethodName("getAttribute");
        attributeMapping.setSetMethodName("setAttribute");
        attributeMapping.setFieldName("attribute-name");
        descriptor.addMapping(attributeMapping);

        return descriptor;
    }

    public ClassDescriptor buildDirectToFieldChangeRecordDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DirectToFieldChangeRecord.class);
        descriptor.setRootElementName("change-record");
        descriptor.descriptorIsAggregate();

        // inheritance stuffs
        descriptor.getInheritancePolicy().setParentClass(ChangeRecord.class);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        DirectToFieldMapping newValueMapping = new DirectToFieldMapping();
        newValueMapping.setAttributeName("newValue");
        newValueMapping.setGetMethodName("getNewValue");
        newValueMapping.setSetMethodName("setNewValue");
        newValueMapping.setFieldName("new-value");
        descriptor.addMapping(newValueMapping);

        return descriptor;
    }

    public ClassDescriptor buildDirectMapChangeRecordDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DirectMapChangeRecord.class);
        descriptor.setRootElementName("change-record");
        descriptor.descriptorIsAggregate();

        // inheritance stuffs
        descriptor.getInheritancePolicy().setParentClass(ChangeRecord.class);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        SDKAggregateCollectionMapping addAssociations = new SDKAggregateCollectionMapping();
        addAssociations.setReferenceClass(Association.class);
        addAssociations.setAttributeName("adds");
        addAssociations.setGetMethodName("getAddAssociations");
        addAssociations.setSetMethodName("setAddAssociations");
        addAssociations.setFieldName("added-associations");
        descriptor.addMapping(addAssociations);

        SDKAggregateCollectionMapping removeAssociations = new SDKAggregateCollectionMapping();
        removeAssociations.setReferenceClass(Association.class);
        removeAssociations.setAttributeName("removes");
        removeAssociations.setGetMethodName("getRemoveAssociations");
        removeAssociations.setSetMethodName("setRemoveAssociations");
        removeAssociations.setFieldName("removed-associations");
        descriptor.addMapping(removeAssociations);

        return descriptor;
    }

    public ClassDescriptor buildAssociationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Association.class);
        descriptor.descriptorIsAggregate();
        descriptor.setRootElementName("association");

        DirectToFieldMapping key = new DirectToFieldMapping();
        key.setAttributeName("key");
        key.setGetMethodName("getKey");
        key.setSetMethodName("setKey");
        key.setFieldName("key");
        descriptor.addMapping(key);

        DirectToFieldMapping value = new DirectToFieldMapping();
        value.setAttributeName("value");
        value.setGetMethodName("getValue");
        value.setSetMethodName("setValue");
        value.setFieldName("value");
        descriptor.addMapping(value);

        return descriptor;
    }

    public ClassDescriptor buildDirectCollectionChangeRecordDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DirectCollectionChangeRecord.class);
        descriptor.setRootElementName("change-record");
        descriptor.descriptorIsAggregate();

        // inheritance stuffs
        descriptor.getInheritancePolicy().setParentClass(ChangeRecord.class);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        SDKDirectCollectionMapping addeObjectsMapping = new SDKDirectCollectionMapping();
        addeObjectsMapping.setAttributeName("addObjectList");
        addeObjectsMapping.setGetMethodName("getAddObjectList");
        addeObjectsMapping.setSetMethodName("setAddObjectList");
        addeObjectsMapping.setFieldName("added-objects");
        addeObjectsMapping.setElementDataTypeName("value");
        descriptor.addMapping(addeObjectsMapping);

        SDKDirectCollectionMapping removeObjectsMapping = new SDKDirectCollectionMapping();
        removeObjectsMapping.setAttributeName("removeObjectList");
        removeObjectsMapping.setGetMethodName("getRemoveObjectList");
        removeObjectsMapping.setSetMethodName("setRemoveObjectList");
        removeObjectsMapping.setFieldName("removed-objects");
        removeObjectsMapping.setElementDataTypeName("value");
        descriptor.addMapping(removeObjectsMapping);

        return descriptor;
    }

    public ClassDescriptor buildTransformationMappingChangeRecord() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TransformationMappingChangeRecord.class);
        descriptor.setRootElementName("change-record");
        descriptor.descriptorIsAggregate();

        // inheritance stuffs
        descriptor.getInheritancePolicy().setParentClass(ChangeRecord.class);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        SDKAggregateCollectionMapping rowCollection = new SDKAggregateCollectionMapping();
        rowCollection.setReferenceClass(Association.class);
        rowCollection.setAttributeName("rowCollection");
        rowCollection.setGetMethodName("getAssociationsFromRow");
        rowCollection.setSetMethodName("setRowFromAssociations");
        rowCollection.setFieldName("row");
        descriptor.addMapping(rowCollection);

        return descriptor;
    }

    public ClassDescriptor buildAggregateChangeRecordDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AggregateChangeRecord.class);
        descriptor.setRootElementName("change-record");
        descriptor.descriptorIsAggregate();

        // inheritance stuffs
        descriptor.getInheritancePolicy().setParentClass(ChangeRecord.class);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        SDKAggregateObjectMapping changeObject = new SDKAggregateObjectMapping();
        changeObject.setReferenceClass(AggregateObjectChangeSet.class);
        changeObject.setAttributeName("changedObject");
        changeObject.setGetMethodName("getChangedObject");
        changeObject.setSetMethodName("setChangedObject");
        changeObject.setFieldName("aggregate-object");
        descriptor.addMapping(changeObject);

        return descriptor;
    }

    public ClassDescriptor buildAggregateCollectionChangeRecordDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AggregateCollectionChangeRecord.class);
        descriptor.setRootElementName("change-record");
        descriptor.descriptorIsAggregate();

        // inheritance stuffs
        descriptor.getInheritancePolicy().setParentClass(ChangeRecord.class);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        SDKObjectCollectionMapping changeValues = new SDKObjectCollectionMapping();
        changeValues.setReferenceClass(ObjectChangeSet.class);
        changeValues.setAttributeName("changeValues");
        changeValues.setUsesIndirection(false);
        changeValues.setGetMethodName("getChangedValues");
        changeValues.setSetMethodName("setChangedValues");
        changeValues.setFieldName("aggregate-objects");
        changeValues.setReferenceDataTypeName("object-change-set");
        changeValues.setSourceForeignKeyFieldName("id");
        changeValues.setSelectionCall(new XMLReadAllCall(changeValues));
        descriptor.addMapping(changeValues);

        return descriptor;
    }

    public ClassDescriptor buildCollectionChangeRecordDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CollectionChangeRecord.class);
        descriptor.setRootElementName("change-record");
        descriptor.descriptorIsAggregate();

        // inheritance stuffs
        descriptor.getInheritancePolicy().setParentClass(ChangeRecord.class);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        SDKObjectCollectionMapping addObjectList = new SDKObjectCollectionMapping();
        addObjectList.setReferenceClass(ObjectChangeSet.class);
        addObjectList.setAttributeName("sdkAddObjects");
        addObjectList.setUsesIndirection(false);
        addObjectList.setGetMethodName("getAddObjectsForSDK");
        addObjectList.setSetMethodName("setAddObjectsForSDK");
        addObjectList.setFieldName("added-objects");
        addObjectList.setReferenceDataTypeName("object-change-set");
        addObjectList.setSourceForeignKeyFieldName("id");
        addObjectList.setSelectionCall(new XMLReadAllCall(addObjectList));
        descriptor.addMapping(addObjectList);

        SDKObjectCollectionMapping removeObjectList = new SDKObjectCollectionMapping();
        removeObjectList.setReferenceClass(ObjectChangeSet.class);
        removeObjectList.setAttributeName("sdkRemoveObjects");
        removeObjectList.setUsesIndirection(false);
        removeObjectList.setGetMethodName("getRemoveObjectsForSDK");
        removeObjectList.setSetMethodName("setRemoveObjectsForSDK");
        removeObjectList.setFieldName("removed-objects");
        removeObjectList.setReferenceDataTypeName("object-change-set");
        removeObjectList.setSourceForeignKeyFieldName("id");
        removeObjectList.setSelectionCall(new XMLReadAllCall(removeObjectList));
        descriptor.addMapping(removeObjectList);

        return descriptor;
    }

    public ClassDescriptor buildObjectReferenceChangeRecord() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectReferenceChangeRecord.class);
        descriptor.setRootElementName("change-record");
        descriptor.descriptorIsAggregate();

        // inheritance stuffs
        descriptor.getInheritancePolicy().setParentClass(ChangeRecord.class);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(false);

        OneToOneMapping newValue = new OneToOneMapping();
        newValue.setReferenceClass(ObjectChangeSet.class);
        newValue.setUsesIndirection(false);
        newValue.setAttributeName("new-value");
        newValue.setForeignKeyFieldName("object-change-set-id");
        newValue.setGetMethodName("getNewValue");
        newValue.setSetMethodName("setNewValue");
        newValue.setCustomSelectionQuery(this.buildReadObjectQuery(newValue));
        descriptor.addMapping(newValue);

        return descriptor;
    }

    /**
     * Build and return a read object query for the specified mapping.
     */
    public ReadObjectQuery buildReadObjectQuery(OneToOneMapping mapping) {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setCall(new XMLReadCall(mapping));
        return query;
    }
}