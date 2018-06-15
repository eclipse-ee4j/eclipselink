/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.internal.jpa.config.classes;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.AbstractAccessorImpl;
import org.eclipse.persistence.internal.jpa.config.changetracking.ChangeTrackingImpl;
import org.eclipse.persistence.internal.jpa.config.columns.AssociationOverrideImpl;
import org.eclipse.persistence.internal.jpa.config.columns.AttributeOverrideImpl;
import org.eclipse.persistence.internal.jpa.config.copypolicy.CloneCopyPolicyImpl;
import org.eclipse.persistence.internal.jpa.config.copypolicy.CopyPolicyImpl;
import org.eclipse.persistence.internal.jpa.config.copypolicy.InstantiationCopyPolicyImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.BasicImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.ElementCollectionImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.EmbeddedIdImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.EmbeddedImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.IdImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.ManyToManyImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.ManyToOneImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.OneToManyImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.OneToOneImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.TransformationImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.TransientImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.VariableOneToOneImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.VersionImpl;
import org.eclipse.persistence.internal.jpa.config.nosql.NoSqlImpl;
import org.eclipse.persistence.internal.jpa.config.queries.OracleArrayImpl;
import org.eclipse.persistence.internal.jpa.config.queries.OracleObjectImpl;
import org.eclipse.persistence.internal.jpa.config.queries.PlsqlRecordImpl;
import org.eclipse.persistence.internal.jpa.config.queries.PlsqlTableImpl;
import org.eclipse.persistence.internal.jpa.config.structures.ArrayImpl;
import org.eclipse.persistence.internal.jpa.config.structures.StructImpl;
import org.eclipse.persistence.internal.jpa.config.structures.StructureImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleArrayTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleObjectTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLRecordMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLTableMetadata;
import org.eclipse.persistence.jpa.config.Array;
import org.eclipse.persistence.jpa.config.AssociationOverride;
import org.eclipse.persistence.jpa.config.AttributeOverride;
import org.eclipse.persistence.jpa.config.Basic;
import org.eclipse.persistence.jpa.config.ChangeTracking;
import org.eclipse.persistence.jpa.config.CloneCopyPolicy;
import org.eclipse.persistence.jpa.config.CopyPolicy;
import org.eclipse.persistence.jpa.config.ElementCollection;
import org.eclipse.persistence.jpa.config.Embedded;
import org.eclipse.persistence.jpa.config.EmbeddedId;
import org.eclipse.persistence.jpa.config.Id;
import org.eclipse.persistence.jpa.config.InstantiationCopyPolicy;
import org.eclipse.persistence.jpa.config.ManyToMany;
import org.eclipse.persistence.jpa.config.ManyToOne;
import org.eclipse.persistence.jpa.config.NoSql;
import org.eclipse.persistence.jpa.config.OneToMany;
import org.eclipse.persistence.jpa.config.OneToOne;
import org.eclipse.persistence.jpa.config.OracleArray;
import org.eclipse.persistence.jpa.config.OracleObject;
import org.eclipse.persistence.jpa.config.PlsqlRecord;
import org.eclipse.persistence.jpa.config.PlsqlTable;
import org.eclipse.persistence.jpa.config.Struct;
import org.eclipse.persistence.jpa.config.Structure;
import org.eclipse.persistence.jpa.config.Transformation;
import org.eclipse.persistence.jpa.config.Transient;
import org.eclipse.persistence.jpa.config.VariableOneToOne;
import org.eclipse.persistence.jpa.config.Version;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
@SuppressWarnings("unchecked")
public abstract class AbstractClassImpl<T extends ClassAccessor, R> extends AbstractAccessorImpl<T, R> {

    public AbstractClassImpl(T t) {
        super(t);

        getMetadata().setAttributes(new Attributes());
        getMetadata().setAssociationOverrides(new ArrayList<AssociationOverrideMetadata>());
        getMetadata().setAttributeOverrides(new ArrayList<AttributeOverrideMetadata>());
        getMetadata().setOracleArrayTypes(new ArrayList<OracleArrayTypeMetadata>());
        getMetadata().setOracleObjectTypes(new ArrayList<OracleObjectTypeMetadata>());
        getMetadata().setPLSQLRecords(new ArrayList<PLSQLRecordMetadata>());
        getMetadata().setPLSQLTables(new ArrayList<PLSQLTableMetadata>());
    }

    public Array addArray() {
        ArrayImpl array = new ArrayImpl();
        getMetadata().getAttributes().getArrays().add(array.getMetadata());
        return array;
    }

    public AssociationOverride addAssociationOverride() {
        AssociationOverrideImpl associationOverride = new AssociationOverrideImpl();
        getMetadata().getAssociationOverrides().add(associationOverride.getMetadata());
        return associationOverride;
    }

    public AttributeOverride addAttributeOverride() {
        AttributeOverrideImpl attributeOverride = new AttributeOverrideImpl();
        getMetadata().getAttributeOverrides().add(attributeOverride.getMetadata());
        return attributeOverride;
    }

    public Basic addBasic() {
        BasicImpl basic = new BasicImpl();
        getMetadata().getAttributes().getBasics().add(basic.getMetadata());
        return basic;
    }

    public ElementCollection addElementCollection() {
        ElementCollectionImpl elementCollection = new ElementCollectionImpl();
        getMetadata().getAttributes().getElementCollections().add(elementCollection.getMetadata());
        return elementCollection;
    }

    public Embedded addEmbedded() {
        EmbeddedImpl embedded = new EmbeddedImpl();
        getMetadata().getAttributes().getEmbeddeds().add(embedded.getMetadata());
        return embedded;
    }

    public Id addId() {
        IdImpl id = new IdImpl();
        getMetadata().getAttributes().getIds().add(id.getMetadata());
        return id;
    }

    public ManyToMany addManyToMany() {
        ManyToManyImpl manyToMany = new ManyToManyImpl();
        getMetadata().getAttributes().getManyToManys().add(manyToMany.getMetadata());
        return manyToMany;
    }

    public ManyToOne addManyToOne() {
        ManyToOneImpl manyToOne = new ManyToOneImpl();
        getMetadata().getAttributes().getManyToOnes().add(manyToOne.getMetadata());
        return manyToOne;
    }

    public OneToMany addOneToMany() {
        OneToManyImpl oneToMany = new OneToManyImpl();
        getMetadata().getAttributes().getOneToManys().add(oneToMany.getMetadata());
        return oneToMany;
    }

    public OneToOne addOneToOne() {
        OneToOneImpl oneToOne = new OneToOneImpl();
        getMetadata().getAttributes().getOneToOnes().add(oneToOne.getMetadata());
        return oneToOne;
    }

    public OracleArray addOracleArray() {
        OracleArrayImpl oracleArray = new OracleArrayImpl();
        getMetadata().getOracleArrayTypes().add(oracleArray.getMetadata());
        return oracleArray;
    }

    public OracleObject addOracleObject() {
       OracleObjectImpl oracleObject = new OracleObjectImpl();
       getMetadata().getOracleObjectTypes().add(oracleObject.getMetadata());
       return oracleObject;
    }

    public PlsqlRecord addPlsqlRecord() {
        PlsqlRecordImpl plsqlRecord = new PlsqlRecordImpl();
        getMetadata().getPLSQLRecords().add(plsqlRecord.getMetadata());
        return plsqlRecord;
    }

    public PlsqlTable addPlsqlTable() {
        PlsqlTableImpl plsqlTable = new PlsqlTableImpl();
        getMetadata().getPLSQLTables().add(plsqlTable.getMetadata());
        return plsqlTable;
    }

    public Structure addStructure() {
        StructureImpl structure = new StructureImpl();
        getMetadata().getAttributes().getStructures().add(structure.getMetadata());
        return structure;
    }

    public Transformation addTransformation() {
        TransformationImpl transformation = new TransformationImpl();
        getMetadata().getAttributes().getTransformations().add(transformation.getMetadata());
        return transformation;
    }

    public Transient addTransient() {
        TransientImpl trans = new TransientImpl();
        getMetadata().getAttributes().getTransients().add(trans.getMetadata());
        return trans;
    }

    public VariableOneToOne addVariableOneToOne() {
        VariableOneToOneImpl variableOneToOne = new VariableOneToOneImpl();
        getMetadata().getAttributes().getVariableOneToOnes().add(variableOneToOne.getMetadata());
        return variableOneToOne;
    }

    public Version addVersion() {
        VersionImpl version = new VersionImpl();
        getMetadata().getAttributes().getVersions().add(version.getMetadata());
        return version;
    }

    public ChangeTracking setChangeTracking() {
        ChangeTrackingImpl changeTracking = new ChangeTrackingImpl();
        getMetadata().setChangeTracking(changeTracking.getMetadata());
        return changeTracking;
    }

    public R setClass(String cls) {
        getMetadata().setClassName(cls);
        return (R) this;
    }

    public CloneCopyPolicy setCloneCopyPolicy() {
        CloneCopyPolicyImpl cloneCopyPolicy = new CloneCopyPolicyImpl();
        getMetadata().setCloneCopyPolicy(cloneCopyPolicy.getMetadata());
        return cloneCopyPolicy;
    }

    public CopyPolicy setCopyPolicy() {
        CopyPolicyImpl copyPolicy = new CopyPolicyImpl();
        getMetadata().setCustomCopyPolicy(copyPolicy.getMetadata());
        return copyPolicy;
    }

    public R setCustomizer(String customizer) {
        getMetadata().setCustomizerClassName(customizer);
        return (R) this;
    }

    public EmbeddedId setEmbeddedId() {
        EmbeddedIdImpl embeddedId = new EmbeddedIdImpl();
        getMetadata().getAttributes().setEmbeddedId(embeddedId.getMetadata());
        return embeddedId;
    }

    public R setExcludeDefaultMappings(Boolean excludeDefaultMappings) {
        getMetadata().setExcludeDefaultMappings(excludeDefaultMappings);
        return (R) this;
    }

    public InstantiationCopyPolicy setInstantiationCopyPolicy() {
        InstantiationCopyPolicyImpl copyPolicy = new InstantiationCopyPolicyImpl();
        getMetadata().setInstantiationCopyPolicy(copyPolicy.getMetadata());
        return copyPolicy;
    }

    public NoSql setNoSql() {
        NoSqlImpl noSql = new NoSqlImpl();
        getMetadata().setNoSql(noSql.getMetadata());
        return noSql;
    }

    public R setMetadataComplete(Boolean metadataComplete) {
        getMetadata().setMetadataComplete(metadataComplete);
        return (R) this;
    }

    public R setParentClass(String parentClass) {
        getMetadata().setParentClassName(parentClass);
        return (R) this;
    }

    public Struct setStruct() {
        StructImpl struct = new StructImpl();
        getMetadata().setStruct(struct.getMetadata());
        return struct;
    }
}
