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
package org.eclipse.persistence.internal.jpa.config.xml;

import java.util.ArrayList;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.config.classes.ConverterClassImpl;
import org.eclipse.persistence.internal.jpa.config.classes.EmbeddableImpl;
import org.eclipse.persistence.internal.jpa.config.classes.EntityImpl;
import org.eclipse.persistence.internal.jpa.config.classes.MappedSuperclassImpl;
import org.eclipse.persistence.internal.jpa.config.columns.TenantDiscriminatorColumnImpl;
import org.eclipse.persistence.internal.jpa.config.converters.ConverterImpl;
import org.eclipse.persistence.internal.jpa.config.converters.ObjectTypeConverterImpl;
import org.eclipse.persistence.internal.jpa.config.converters.StructConverterImpl;
import org.eclipse.persistence.internal.jpa.config.converters.TypeConverterImpl;
import org.eclipse.persistence.internal.jpa.config.mappings.AccessMethodsImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.HashPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.PartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.PinnedPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.RangePartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.ReplicationPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.RoundRobinPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.UnionPartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.partitioning.ValuePartitioningImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedNativeQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedPlsqlStoredFunctionQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedPlsqlStoredProcedureQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedStoredFunctionQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.NamedStoredProcedureQueryImpl;
import org.eclipse.persistence.internal.jpa.config.queries.OracleArrayImpl;
import org.eclipse.persistence.internal.jpa.config.queries.OracleObjectImpl;
import org.eclipse.persistence.internal.jpa.config.queries.PlsqlRecordImpl;
import org.eclipse.persistence.internal.jpa.config.queries.PlsqlTableImpl;
import org.eclipse.persistence.internal.jpa.config.queries.SqlResultSetMappingImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.SequenceGeneratorImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.TableGeneratorImpl;
import org.eclipse.persistence.internal.jpa.config.sequencing.UuidGeneratorImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ConverterAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.TenantDiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.MixedConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ObjectTypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.SerializedConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.HashPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.PartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.PinnedPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RangePartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ReplicationPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RoundRobinPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.UnionPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ValuePartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleArrayTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleObjectTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLRecordMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.UuidGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.config.AccessMethods;
import org.eclipse.persistence.jpa.config.Converter;
import org.eclipse.persistence.jpa.config.ConverterClass;
import org.eclipse.persistence.jpa.config.Embeddable;
import org.eclipse.persistence.jpa.config.Entity;
import org.eclipse.persistence.jpa.config.HashPartitioning;
import org.eclipse.persistence.jpa.config.MappedSuperclass;
import org.eclipse.persistence.jpa.config.Mappings;
import org.eclipse.persistence.jpa.config.NamedNativeQuery;
import org.eclipse.persistence.jpa.config.NamedPlsqlStoredFunctionQuery;
import org.eclipse.persistence.jpa.config.NamedPlsqlStoredProcedureQuery;
import org.eclipse.persistence.jpa.config.NamedQuery;
import org.eclipse.persistence.jpa.config.NamedStoredFunctionQuery;
import org.eclipse.persistence.jpa.config.NamedStoredProcedureQuery;
import org.eclipse.persistence.jpa.config.ObjectTypeConverter;
import org.eclipse.persistence.jpa.config.OracleArray;
import org.eclipse.persistence.jpa.config.OracleObject;
import org.eclipse.persistence.jpa.config.Partitioning;
import org.eclipse.persistence.jpa.config.PersistenceUnitMetadata;
import org.eclipse.persistence.jpa.config.PinnedPartitioning;
import org.eclipse.persistence.jpa.config.PlsqlRecord;
import org.eclipse.persistence.jpa.config.PlsqlTable;
import org.eclipse.persistence.jpa.config.RangePartitioning;
import org.eclipse.persistence.jpa.config.ReplicationPartitioning;
import org.eclipse.persistence.jpa.config.RoundRobinPartitioning;
import org.eclipse.persistence.jpa.config.SequenceGenerator;
import org.eclipse.persistence.jpa.config.SqlResultSetMapping;
import org.eclipse.persistence.jpa.config.StructConverter;
import org.eclipse.persistence.jpa.config.TableGenerator;
import org.eclipse.persistence.jpa.config.TenantDiscriminatorColumn;
import org.eclipse.persistence.jpa.config.TypeConverter;
import org.eclipse.persistence.jpa.config.UnionPartitioning;
import org.eclipse.persistence.jpa.config.UuidGenerator;
import org.eclipse.persistence.jpa.config.ValuePartitioning;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class MappingsImpl extends MetadataImpl<XMLEntityMappings> implements Mappings {

    public MappingsImpl() {
        super(new XMLEntityMappings());

        getMetadata().setConverters(new ArrayList<ConverterMetadata>());
        getMetadata().setConverterAccessors(new ArrayList<ConverterAccessor>());
        getMetadata().setEmbeddables(new ArrayList<EmbeddableAccessor>());
        getMetadata().setEntities(new ArrayList<EntityAccessor>());
        getMetadata().setHashPartitioning(new ArrayList<HashPartitioningMetadata>());
        getMetadata().setMappedSuperclasses(new ArrayList<MappedSuperclassAccessor>());
        getMetadata().setMixedConverters(new ArrayList<MixedConverterMetadata>());
        getMetadata().setNamedQueries(new ArrayList<NamedQueryMetadata>());
        getMetadata().setNamedStoredFunctionQueries(new ArrayList<NamedStoredFunctionQueryMetadata>());
        getMetadata().setNamedNativeQueries(new ArrayList<NamedNativeQueryMetadata>());
        getMetadata().setNamedPLSQLStoredFunctionQueries(new ArrayList<NamedPLSQLStoredFunctionQueryMetadata>());
        getMetadata().setNamedPLSQLStoredProcedureQueries(new ArrayList<NamedPLSQLStoredProcedureQueryMetadata>());
        getMetadata().setNamedStoredProcedureQueries(new ArrayList<NamedStoredProcedureQueryMetadata>());
        getMetadata().setObjectTypeConverters(new ArrayList<ObjectTypeConverterMetadata>());
        getMetadata().setOracleArrayTypes(new ArrayList<OracleArrayTypeMetadata>());
        getMetadata().setOracleObjectTypes(new ArrayList<OracleObjectTypeMetadata>());
        getMetadata().setPartitioning(new ArrayList<PartitioningMetadata>());
        getMetadata().setPinnedPartitioning(new ArrayList<PinnedPartitioningMetadata>());
        getMetadata().setPLSQLRecords(new ArrayList<PLSQLRecordMetadata>());
        getMetadata().setPLSQLTables(new ArrayList<PLSQLTableMetadata>());
        getMetadata().setRangePartitioning(new ArrayList<RangePartitioningMetadata>());
        getMetadata().setReplicationPartitioning(new ArrayList<ReplicationPartitioningMetadata>());
        getMetadata().setRoundRobinPartitioning(new ArrayList<RoundRobinPartitioningMetadata>());
        getMetadata().setSequenceGenerators(new ArrayList<SequenceGeneratorMetadata>());
        getMetadata().setSerializedConverters(new ArrayList<SerializedConverterMetadata>()); // TODO: add to config
        getMetadata().setSqlResultSetMappings(new ArrayList<SQLResultSetMappingMetadata>());
        getMetadata().setStructConverters(new ArrayList<StructConverterMetadata>());
        getMetadata().setTableGenerators(new ArrayList<TableGeneratorMetadata>());
        getMetadata().setTenantDiscriminatorColumns(new ArrayList<TenantDiscriminatorColumnMetadata>());
        getMetadata().setTypeConverters(new ArrayList<TypeConverterMetadata>());
        getMetadata().setUnionPartitioning(new ArrayList<UnionPartitioningMetadata>());
        getMetadata().setUuidGenerators(new ArrayList<UuidGeneratorMetadata>());
        getMetadata().setValuePartitioning(new ArrayList<ValuePartitioningMetadata>());
    }

    @Override
    public Converter addConverter() {
        ConverterImpl converter = new ConverterImpl();
        getMetadata().getConverters().add(converter.getMetadata());
        return converter;
    }

    @Override
    public ConverterClass addConverterClass() {
        ConverterClassImpl converterClass = new ConverterClassImpl();
        getMetadata().getConverterAccessors().add(converterClass.getMetadata());
        return converterClass;
    }

    @Override
    public Embeddable addEmbeddable() {
        EmbeddableImpl embeddable = new EmbeddableImpl();
        getMetadata().getEmbeddables().add(embeddable.getMetadata());
        return embeddable;
    }

    @Override
    public Entity addEntity() {
        EntityImpl entity = new EntityImpl();
        getMetadata().getEntities().add(entity.getMetadata());
        return entity;
    }

    @Override
    public HashPartitioning addHashPartitioning() {
        HashPartitioningImpl partitioning = new HashPartitioningImpl();
        getMetadata().getHashPartitioning().add(partitioning.getMetadata());
        return partitioning;
    }

    @Override
    public MappedSuperclass addMappedSuperclass() {
        MappedSuperclassImpl mappedSuperclass = new MappedSuperclassImpl();
        getMetadata().getMappedSuperclasses().add(mappedSuperclass.getMetadata());
        return mappedSuperclass;
    }

    @Override
    public NamedNativeQuery addNamedNativeQuery() {
        NamedNativeQueryImpl query = new NamedNativeQueryImpl();
        getMetadata().getNamedNativeQueries().add(query.getMetadata());
        return query;
    }

    @Override
    public NamedPlsqlStoredFunctionQuery addNamedPlsqlStoredFunctionQuery() {
        NamedPlsqlStoredFunctionQueryImpl query = new NamedPlsqlStoredFunctionQueryImpl();
        getMetadata().getNamedPLSQLStoredFunctionQueries().add(query.getMetadata());
        return query;
    }

    @Override
    public NamedPlsqlStoredProcedureQuery addNamedPlsqlStoredProcedureQuery() {
        NamedPlsqlStoredProcedureQueryImpl query = new NamedPlsqlStoredProcedureQueryImpl();
        getMetadata().getNamedPLSQLStoredProcedureQueries().add(query.getMetadata());
        return query;
    }

    @Override
    public NamedQuery addNamedQuery() {
        NamedQueryImpl query = new NamedQueryImpl();
        getMetadata().getNamedQueries().add(query.getMetadata());
        return query;
    }

    @Override
    public NamedStoredFunctionQuery addNamedStoredFunctionQuery() {
        NamedStoredFunctionQueryImpl query = new NamedStoredFunctionQueryImpl();
        getMetadata().getNamedStoredFunctionQueries().add(query.getMetadata());
        return query;
    }

    @Override
    public NamedStoredProcedureQuery addNamedStoredProcedureQuery() {
        NamedStoredProcedureQueryImpl query = new NamedStoredProcedureQueryImpl();
        getMetadata().getNamedStoredProcedureQueries().add(query.getMetadata());
        return query;
    }

    @Override
    public ObjectTypeConverter addObjectTypeConverter() {
        ObjectTypeConverterImpl converter = new ObjectTypeConverterImpl();
        getMetadata().getObjectTypeConverters().add(converter.getMetadata());
        return converter;
    }

    @Override
    public OracleArray addOracleArray() {
        OracleArrayImpl oracleArray = new OracleArrayImpl();
        getMetadata().getOracleArrayTypes().add(oracleArray.getMetadata());
        return oracleArray;
    }

    @Override
    public OracleObject addOracleObject() {
        OracleObjectImpl oracleObject = new OracleObjectImpl();
        getMetadata().getOracleObjectTypes().add(oracleObject.getMetadata());
        return oracleObject;
    }

    @Override
    public Partitioning addPartitioning() {
        PartitioningImpl partitioning = new PartitioningImpl();
        getMetadata().getPartitioning().add(partitioning.getMetadata());
        return partitioning;
    }

    @Override
    public PinnedPartitioning addPinnedPartitioning() {
        PinnedPartitioningImpl partitioning = new PinnedPartitioningImpl();
        getMetadata().getPinnedPartitioning().add(partitioning.getMetadata());
        return partitioning;
    }

    @Override
    public PlsqlRecord addPlsqlRecord() {
        PlsqlRecordImpl plsqlRecord = new PlsqlRecordImpl();
        getMetadata().getPLSQLRecords().add(plsqlRecord.getMetadata());
        return plsqlRecord;
    }

    @Override
    public PlsqlTable addPlsqlTable() {
        PlsqlTableImpl plsqlTable = new PlsqlTableImpl();
        getMetadata().getPLSQLTables().add(plsqlTable.getMetadata());
        return plsqlTable;
    }

    @Override
    public RangePartitioning addRangePartitioning() {
        RangePartitioningImpl partitioning = new RangePartitioningImpl();
        getMetadata().getRangePartitioning().add(partitioning.getMetadata());
        return partitioning;
    }

    @Override
    public ReplicationPartitioning addReplicationPartititioning() {
        ReplicationPartitioningImpl partitioning = new ReplicationPartitioningImpl();
        getMetadata().getReplicationPartitioning().add(partitioning.getMetadata());
        return partitioning;
    }

    @Override
    public RoundRobinPartitioning addRoundRobinPartitioning() {
        RoundRobinPartitioningImpl partitioning = new RoundRobinPartitioningImpl();
        getMetadata().getRoundRobinPartitioning().add(partitioning.getMetadata());
        return partitioning;
    }

    @Override
    public SequenceGenerator addSequenceGenerator() {
        SequenceGeneratorImpl generator = new SequenceGeneratorImpl();
        getMetadata().getSequenceGenerators().add(generator.getMetadata());
        return generator;
    }

    @Override
    public SqlResultSetMapping addSqlResultSetMapping() {
        SqlResultSetMappingImpl sqlResultSetMapping = new SqlResultSetMappingImpl();
        getMetadata().getSqlResultSetMappings().add(sqlResultSetMapping.getMetadata());
        return sqlResultSetMapping;
    }

    @Override
    public StructConverter addStructConverter() {
        StructConverterImpl converter = new StructConverterImpl();
        getMetadata().getStructConverters().add(converter.getMetadata());
        return converter;
    }

    @Override
    public TableGenerator addTableGenerator() {
        TableGeneratorImpl generator = new TableGeneratorImpl();
        getMetadata().getTableGenerators().add(generator.getMetadata());
        return generator;
    }

    @Override
    public TenantDiscriminatorColumn addTenantDiscriminatorColumn() {
        TenantDiscriminatorColumnImpl column = new TenantDiscriminatorColumnImpl();
        getMetadata().getTenantDiscriminatorColumns().add(column.getMetadata());
        return column;
    }

    @Override
    public TypeConverter addTypeConverter() {
        TypeConverterImpl converter = new TypeConverterImpl();
        getMetadata().getTypeConverters().add(converter.getMetadata());
        return converter;
    }

    @Override
    public UnionPartitioning addUnionPartitioning() {
        UnionPartitioningImpl partitioning = new UnionPartitioningImpl();
        getMetadata().getUnionPartitioning().add(partitioning.getMetadata());
        return partitioning;
    }

    @Override
    public UuidGenerator addUuidGenerator() {
        UuidGeneratorImpl generator = new UuidGeneratorImpl();
        getMetadata().getUuidGenerators().add(generator.getMetadata());
        return null;
    }

    @Override
    public ValuePartitioning addValuePartitioning() {
        ValuePartitioningImpl partitioning = new ValuePartitioningImpl();
        getMetadata().getValuePartitioning().add(partitioning.getMetadata());
        return partitioning;
    }

    @Override
    public Mappings setAccess(String access) {
        getMetadata().setAccess(access);
        return this;
    }

    @Override
    public AccessMethods setAccessMethods() {
        AccessMethodsImpl accessMethods = new AccessMethodsImpl();
        getMetadata().setAccessMethods(accessMethods.getMetadata());
        return accessMethods;
    }

    @Override
    public Mappings setCatalog(String catalog) {
        getMetadata().setCatalog(catalog);
        return this;
    }

    @Override
    public Mappings setPackage(String pkg) {
        getMetadata().setPackage(pkg);
        return this;
    }

    @Override
    public PersistenceUnitMetadata setPersistenceUnitMetadata() {
        PersistenceUnitMetadataImpl persistenceUnit = new PersistenceUnitMetadataImpl();
        getMetadata().setPersistenceUnitMetadata(persistenceUnit.getMetadata());
        return persistenceUnit;
    }

    @Override
    public Mappings setSchema(String schema) {
        getMetadata().setSchema(schema);
        return this;
    }

    @Override
    public Mappings setVersion(String version) {
        getMetadata().setVersion(version);
        return this;
    }

}
