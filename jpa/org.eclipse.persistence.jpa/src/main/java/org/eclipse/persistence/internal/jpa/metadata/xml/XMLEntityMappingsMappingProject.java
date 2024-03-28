/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping file
//     05/23/2008-1.0M8 Guy Pelletier
//       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
//     07/22/2008-1.1 Guy Pelletier
//       - 237315: Support converters on attribute mappings in the eclipselink orm.xml schema
//     08/27/2008-1.1 Guy Pelletier
//       - 211329: Add sequencing on non-id attribute(s) support to the EclipseLink-ORM.XML Schema
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     02/06/2009-2.0 Guy Pelletier
//       - 248293: JPA 2.0 Element Collections (part 2)
//     02/25/2009-2.0 Guy Pelletier
//       - 265359: JPA 2.0 Element Collections - Metadata processing portions
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     04/03/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
//     06/09/2009-2.0 Guy Pelletier
//       - 249037: JPA 2.0 persisting list item index
//     06/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
//     11/06/2009-2.0 Guy Pelletier
//       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
//     12/2/2009-2.1 Guy Pelletier
//       - 296289: Add current annotation metadata support on mapped superclasses to EclipseLink-ORM.XML Schema
//     12/18/2009-2.1 Guy Pelletier
//       - 211323: Add class extractor support to the EclipseLink-ORM.XML Schema
//     01/19/2010-2.1 Guy Pelletier
//       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
//     03/08/2010-2.1 Guy Pelletier
//       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM
//     04/09/2010-2.1 Guy Pelletier
//       - 307050: Add defaults for access methods of a VIRTUAL access type
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     05/04/2010-2.1 Guy Pelletier
//       - 309373: Add parent class attribute to EclipseLink-ORM
//     09/16/2010-2.2 Guy Pelletier
//       - 283028: Add support for letting an @Embeddable extend a @MappedSuperclass
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
//     10/28/2010-2.2 Guy Pelletier
//       - 3223850: Primary key metadata issues
//     01/25/2011-2.3 Guy Pelletier
//       - 333913: @OrderBy and <order-by/> without arguments should order by primary
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     03/28/2011-2.3 Guy Pelletier
//       - 341152: From XML cache interceptor and query redirector metadata don't support package specification
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 8)
//     07/03/2011-2.3.1 Guy Pelletier
//       - 348756: m_cascadeOnDelete boolean should be changed to Boolean
//     08/18/2011-2.3.1 Guy Pelletier
//       - 355093: Add new 'includeCriteria' flag to Multitenant metadata
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
//      //     30/05/2012-2.4 Guy Pelletier
//       - 354678: Temp classloader is still being used during metadata processing
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     10/25/2012-2.5 Guy Pelletier
//       - 3746888: JPA 2.1 Converter support
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
//     02/20/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
package org.eclipse.persistence.internal.jpa.metadata.xml;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedIdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransformationAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.TransientAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VariableOneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.additionalcriteria.AdditionalCriteriaMetadata;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheIndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheInterceptorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheMetadata;
import org.eclipse.persistence.internal.jpa.metadata.cache.TimeOfDayMetadata;
import org.eclipse.persistence.internal.jpa.metadata.changetracking.ChangeTrackingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AssociationOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.DiscriminatorClassMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.DiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.FieldMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.ForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinFieldMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.OrderColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyForeignKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.TenantDiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConversionValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConvertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.LobMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.MixedConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.ObjectTypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.SerializedConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.StructConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TypeConverterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CloneCopyPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.CustomCopyPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.copypolicy.InstantiationCopyPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.graphs.NamedAttributeNodeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.graphs.NamedEntityGraphMetadata;
import org.eclipse.persistence.internal.jpa.metadata.graphs.NamedSubgraphMetadata;
import org.eclipse.persistence.internal.jpa.metadata.inheritance.InheritanceMetadata;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.locking.OptimisticLockingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.AccessMethodsMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.BatchFetchMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.CascadeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.MapKeyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.OrderByMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.ReturnInsertMetadata;
import org.eclipse.persistence.internal.jpa.metadata.multitenant.MultitenantMetadata;
import org.eclipse.persistence.internal.jpa.metadata.multitenant.TenantTableDiscriminatorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.nosql.NoSqlMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.HashPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.PartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.PinnedPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RangePartitionMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RangePartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ReplicationPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.RoundRobinPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.UnionPartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ValuePartitionMetadata;
import org.eclipse.persistence.internal.jpa.metadata.partitioning.ValuePartitioningMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.ColumnResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.ConstructorResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.EntityResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.FetchAttributeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.FetchGroupMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.FieldResultMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedNativeQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedPLSQLStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredFunctionQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.NamedStoredProcedureQueryMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleArrayTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.OracleObjectTypeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLParameterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLRecordMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.PLSQLTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.QueryHintMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.QueryRedirectorsMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.SQLResultSetMappingMetadata;
import org.eclipse.persistence.internal.jpa.metadata.queries.StoredProcedureParameterMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.UuidGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sop.SerializedObjectPolicyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.structures.ArrayAccessor;
import org.eclipse.persistence.internal.jpa.metadata.structures.StructMetadata;
import org.eclipse.persistence.internal.jpa.metadata.structures.StructureAccessor;
import org.eclipse.persistence.internal.jpa.metadata.tables.CheckConstraintMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.CollectionTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.IndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.JoinTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.SecondaryTableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.TableMetadata;
import org.eclipse.persistence.internal.jpa.metadata.tables.UniqueConstraintMetadata;
import org.eclipse.persistence.internal.jpa.metadata.transformers.ReadTransformerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.transformers.WriteTransformerMetadata;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;

/**
 * INTERNAL:
 * EclipseLink ORM mapping file OX mapping project.
 * <p>
 * Key notes:
 * - Elements mappings (per descriptor) must remain in order of definition in
 *   XML. This ensure on any write out the elements will be written in a valid
 *   order (to be read back in and validated correctly).
 * - Attribute mappings (per descriptor) can be in any order (but recommended
 *   to keep them in order of definition in XML as well)
 * - methods should be preserved in alphabetical order.
 * - Individual mappings should be built within get methods and used within
 *   individual descriptor build methods.
 *
 * @author Guy Pelletier
 */
public class XMLEntityMappingsMappingProject extends org.eclipse.persistence.sessions.Project {

    public XMLEntityMappingsMappingProject(String ormNamespace, String xsdLocation) {
        setName("MetadataMappingProject");

        addDescriptor(buildEntityMappingsDescriptor(xsdLocation));
        addDescriptor(buildPersistenceUnitMetadataDescriptor());
        addDescriptor(buildPersistenceUnitDefaultsDescriptor());

        addDescriptor(buildEntityDescriptor());
        addDescriptor(buildEmbeddableDescriptor());
        addDescriptor(buildMappedSuperclassDescriptor());

        addDescriptor(buildInheritanceDescriptor());
        addDescriptor(buildTableDescriptor());
        addDescriptor(buildIndexDescriptor());
        addDescriptor(buildSecondaryTableDescriptor());
        addDescriptor(buildUniqueConstraintDescriptor());
        addDescriptor(buildCheckConstraintDescriptor());
        addDescriptor(buildAttributesDescriptor());
        addDescriptor(buildEntityListenerDescriptor());
        addDescriptor(buildPrimaryKeyDescriptor());
        addDescriptor(buildOptimisticLockingDescriptor());
        addDescriptor(buildCacheDescriptor());
        addDescriptor(buildCacheInterceptorDescriptor());
        addDescriptor(buildCacheIndexDescriptor());
        addDescriptor(buildFetchAttributeDescriptor());
        addDescriptor(buildFetchGroupDescriptor());
        addDescriptor(buildTimeOfDayDescriptor());

        addDescriptor(buildColumnDescriptor());
        addDescriptor(buildOrderColumnDescriptor());
        addDescriptor(buildOrderByDescriptor());
        addDescriptor(buildJoinColumnDescriptor());
        addDescriptor(buildForeignKeyDescriptor());
        addDescriptor(buildPrimaryKeyJoinColumnDescriptor());
        addDescriptor(buildPrimaryKeyForeignKeyDescriptor());
        addDescriptor(buildAccessMethodsDescriptor());
        addDescriptor(buildAssociationOverrideDescriptor());
        addDescriptor(buildAttributeOverrideDescriptor());
        addDescriptor(buildDiscriminatorColumnDescriptor());
        addDescriptor(buildDiscriminatorClassDescriptor());

        addDescriptor(buildAdditionalCriteriaDescriptor());
        addDescriptor(buildMultitenantDescriptor());
        addDescriptor(buildTenantDiscriminatorColumnDescriptor());
        addDescriptor(buildTenantTableDiscriminatorDescriptor());
        addDescriptor(buildNamedQueryDescriptor());
        addDescriptor(buildNamedNativeQueryDescriptor());
        addDescriptor(buildSqlResultSetMappingDescriptor());
        addDescriptor(buildQueryHintDescriptor());
        addDescriptor(buildEntityResultDescriptor());
        addDescriptor(buildFieldResultDescriptor());
        addDescriptor(buildColumnResultDescriptor());
        addDescriptor(buildConstructorResultDescriptor());
        addDescriptor(buildDefaultRedirectorsDescriptor());
        addDescriptor(buildNamedEntityGraphDescriptor());
        addDescriptor(buildNamedAttributeNodeDescriptor());
        addDescriptor(buildNamedSubgraphDescriptor());
        addDescriptor(buildSerializedObjectPolicyDescriptor());

        addDescriptor(buildNamedStoredProcedureQueryDescriptor());
        addDescriptor(buildNamedStoredFunctionQueryDescriptor());
        addDescriptor(buildStoredProcedureParameterDescriptor());
        addDescriptor(buildNamedPLSQLStoredProcedureQueryDescriptor());
        addDescriptor(buildNamedPLSQLStoredFunctionQueryDescriptor());
        addDescriptor(buildOracleObjectTypeDescriptor());
        addDescriptor(buildOracleArrayTypeDescriptor());
        addDescriptor(buildPLSQLRecordDescriptor());
        addDescriptor(buildPLSQLTableDescriptor());
        addDescriptor(buildPLSQLParameterDescriptor());
        addDescriptor(buildArrayDescriptor());
        addDescriptor(buildStructureDescriptor());
        addDescriptor(buildStructDescriptor());

        addDescriptor(buildIdDescriptor());
        addDescriptor(buildEmbeddedIdDescriptor());
        addDescriptor(buildTransientDescriptor());
        addDescriptor(buildVersionDescriptor());
        addDescriptor(buildBasicDescriptor());
        addDescriptor(buildReturnInsertDescriptor());
        addDescriptor(buildCascadeTypeDescriptor());
        addDescriptor(buildManyToOneDescriptor());
        addDescriptor(buildOneToOneDescriptor());
        addDescriptor(buildOneToManyDescriptor());
        addDescriptor(buildManyToManyDescriptor());
        addDescriptor(buildJoinTableDescriptor());
        addDescriptor(buildEmbeddedDescriptor());
        addDescriptor(buildElementCollectionDescriptor());
        addDescriptor(buildCollectionTableDescriptor());
        addDescriptor(buildBasicCollectionDescriptor());
        addDescriptor(buildBasicMapDescriptor());
        addDescriptor(buildVariableOneToOneDescriptor());
        addDescriptor(buildMapKeyDescriptor());
        addDescriptor(buildBatchFetchDescriptor());

        addDescriptor(buildGeneratedValueDescriptor());
        addDescriptor(buildSequenceGeneratorDescriptor());
        addDescriptor(buildTableGeneratorDescriptor());
        addDescriptor(buildUuidGeneratorDescriptor());

        addDescriptor(buildLobDescriptor());
        addDescriptor(buildTemporalDescriptor());
        addDescriptor(buildEnumeratedDescriptor());
        addDescriptor(buildConvertDescriptor());
        addDescriptor(buildConverterDescriptor());
        addDescriptor(buildMixedConverterDescriptor());
        addDescriptor(buildTypeConverterDescriptor());
        addDescriptor(buildSerializedConverterDescriptor());
        addDescriptor(buildObjectTypeConverterDescriptor());
        addDescriptor(buildConversionValueDescriptor());
        addDescriptor(buildStructConverterDescriptor());
        addDescriptor(buildChangeTrackingDescriptor());

        addDescriptor(buildReadTransformerDescriptor());
        addDescriptor(buildWriteTransformerDescriptor());
        addDescriptor(buildTransformationDescriptor());

        addDescriptor(buildCustomCopyPolicyDescriptor());
        addDescriptor(buildCloneCopyPolicyDescriptor());
        addDescriptor(buildInstantiationCopyPolicyDescriptor());

        addDescriptor(buildPartitioningDescriptor());
        addDescriptor(buildReplicationPartitioningDescriptor());
        addDescriptor(buildRoundRobinPartitioningDescriptor());
        addDescriptor(buildRangePartitioningDescriptor());
        addDescriptor(buildRangePartitionDescriptor());
        addDescriptor(buildValuePartitioningDescriptor());
        addDescriptor(buildValuePartitionDescriptor());
        addDescriptor(buildHashPartitioningDescriptor());
        addDescriptor(buildUnionPartitioningDescriptor());
        addDescriptor(buildPinnedPartitioningDescriptor());

        addDescriptor(buildPropertyDescriptor());

        addDescriptor(buildNoSqlDescriptor());
        addDescriptor(buildFieldDescriptor());
        addDescriptor(buildJoinFieldDescriptor());

        // Set the name spaces on all descriptors.
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        //namespaceResolver.put("xsd", "http://www.w3.org/2001/XMLSchema");
        namespaceResolver.put("orm", ormNamespace);

        for (ClassDescriptor descriptor : getDescriptors().values()) {
            ((XMLDescriptor)descriptor).setNamespaceResolver(namespaceResolver);
        }

        XMLLogin xmlLogin = new XMLLogin();
        DOMPlatform platform = new DOMPlatform();
        xmlLogin.setDatasourcePlatform(platform);
        this.setDatasourceLogin(xmlLogin);
    }


    protected void addConverterMappings(ClassDescriptor descriptor) {
        descriptor.addMapping(getConverterMapping());
        descriptor.addMapping(getTypeConverterMapping());
        descriptor.addMapping(getObjectTypeConverterMapping());
        descriptor.addMapping(getSerializedConverterMapping());
        descriptor.addMapping(getStructConverterMapping());
    }

    /**
     * INTERNAL:
     * XSD: access-methods
     */
    protected ClassDescriptor buildAccessMethodsDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AccessMethodsMetadata.class);

        XMLDirectMapping getMethodMapping = new XMLDirectMapping();
        getMethodMapping.setAttributeName("m_getMethodName");
        getMethodMapping.setGetMethodName("getGetMethodName");
        getMethodMapping.setSetMethodName("setGetMethodName");
        getMethodMapping.setXPath("@get-method");
        descriptor.addMapping(getMethodMapping);

        XMLDirectMapping setMethodMapping = new XMLDirectMapping();
        setMethodMapping.setAttributeName("m_setMethodName");
        setMethodMapping.setGetMethodName("getSetMethodName");
        setMethodMapping.setSetMethodName("setSetMethodName");
        setMethodMapping.setXPath("@set-method");
        descriptor.addMapping(setMethodMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: additional-criteria
     */
    protected ClassDescriptor buildAdditionalCriteriaDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AdditionalCriteriaMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        XMLDirectMapping criteriaMapping = new XMLDirectMapping();
        criteriaMapping.setAttributeName("m_criteria");
        criteriaMapping.setGetMethodName("getCriteria");
        criteriaMapping.setSetMethodName("setCriteria");
        criteriaMapping.setXPath("orm:criteria");
        descriptor.addMapping(criteriaMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: association-override
     */
    protected ClassDescriptor buildAssociationOverrideDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AssociationOverrideMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getJoinColumnMapping());
        descriptor.addMapping(getForeignKeyMapping());
        descriptor.addMapping(getJoinTableMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: attribute-override
     */
    protected ClassDescriptor buildAttributeOverrideDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AttributeOverrideMetadata.class);

        descriptor.addMapping(getColumnMapping());
        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: attributes
     */
    protected ClassDescriptor buildAttributesDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLAttributes.class);

        // Element mappings - must remain in order of definition in XML.
        XMLCompositeObjectMapping embeddedIdMapping = new XMLCompositeObjectMapping();
        embeddedIdMapping.setAttributeName("m_embeddedId");
        embeddedIdMapping.setGetMethodName("getEmbeddedId");
        embeddedIdMapping.setSetMethodName("setEmbeddedId");
        embeddedIdMapping.setReferenceClass(EmbeddedIdAccessor.class);
        embeddedIdMapping.setXPath("orm:embedded-id");
        descriptor.addMapping(embeddedIdMapping);

        XMLCompositeCollectionMapping idsMapping = new XMLCompositeCollectionMapping();
        idsMapping.setAttributeName("m_ids");
        idsMapping.setGetMethodName("getIds");
        idsMapping.setSetMethodName("setIds");
        idsMapping.setReferenceClass(IdAccessor.class);
        idsMapping.setXPath("orm:id");
        descriptor.addMapping(idsMapping);

        XMLCompositeCollectionMapping basicsMapping = new XMLCompositeCollectionMapping();
        basicsMapping.setAttributeName("m_basics");
        basicsMapping.setGetMethodName("getBasics");
        basicsMapping.setSetMethodName("setBasics");
        basicsMapping.setReferenceClass(BasicAccessor.class);
        basicsMapping.setXPath("orm:basic");
        descriptor.addMapping(basicsMapping);

        XMLCompositeCollectionMapping basicCollectionsMapping = new XMLCompositeCollectionMapping();
        basicCollectionsMapping.setAttributeName("m_basicCollections");
        basicCollectionsMapping.setGetMethodName("getBasicCollections");
        basicCollectionsMapping.setSetMethodName("setBasicCollections");
        basicCollectionsMapping.setReferenceClass(BasicCollectionAccessor.class);
        basicCollectionsMapping.setXPath("orm:basic-collection");
        descriptor.addMapping(basicCollectionsMapping);

        XMLCompositeCollectionMapping basicMapsMapping = new XMLCompositeCollectionMapping();
        basicMapsMapping.setAttributeName("m_basicMaps");
        basicMapsMapping.setGetMethodName("getBasicMaps");
        basicMapsMapping.setSetMethodName("setBasicMaps");
        basicMapsMapping.setReferenceClass(BasicMapAccessor.class);
        basicMapsMapping.setXPath("orm:basic-map");
        descriptor.addMapping(basicMapsMapping);

        XMLCompositeCollectionMapping versionsMapping = new XMLCompositeCollectionMapping();
        versionsMapping.setAttributeName("m_versions");
        versionsMapping.setGetMethodName("getVersions");
        versionsMapping.setSetMethodName("setVersions");
        versionsMapping.setReferenceClass(VersionAccessor.class);
        versionsMapping.setXPath("orm:version");
        descriptor.addMapping(versionsMapping);

        XMLCompositeCollectionMapping manyToOnesMapping = new XMLCompositeCollectionMapping();
        manyToOnesMapping.setAttributeName("m_manyToOnes");
        manyToOnesMapping.setGetMethodName("getManyToOnes");
        manyToOnesMapping.setSetMethodName("setManyToOnes");
        manyToOnesMapping.setReferenceClass(ManyToOneAccessor.class);
        manyToOnesMapping.setXPath("orm:many-to-one");
        descriptor.addMapping(manyToOnesMapping);

        XMLCompositeCollectionMapping oneToManysMapping = new XMLCompositeCollectionMapping();
        oneToManysMapping.setAttributeName("m_oneToManys");
        oneToManysMapping.setGetMethodName("getOneToManys");
        oneToManysMapping.setSetMethodName("setOneToManys");
        oneToManysMapping.setReferenceClass(OneToManyAccessor.class);
        oneToManysMapping.setXPath("orm:one-to-many");
        descriptor.addMapping(oneToManysMapping);

        XMLCompositeCollectionMapping oneToOnesMapping = new XMLCompositeCollectionMapping();
        oneToOnesMapping.setAttributeName("m_oneToOnes");
        oneToOnesMapping.setGetMethodName("getOneToOnes");
        oneToOnesMapping.setSetMethodName("setOneToOnes");
        oneToOnesMapping.setReferenceClass(OneToOneAccessor.class);
        oneToOnesMapping.setXPath("orm:one-to-one");
        descriptor.addMapping(oneToOnesMapping);

        XMLCompositeCollectionMapping variableOneToOnesMapping = new XMLCompositeCollectionMapping();
        variableOneToOnesMapping.setAttributeName("m_variableOneToOnes");
        variableOneToOnesMapping.setGetMethodName("getVariableOneToOnes");
        variableOneToOnesMapping.setSetMethodName("setVariableOneToOnes");
        variableOneToOnesMapping.setReferenceClass(VariableOneToOneAccessor.class);
        variableOneToOnesMapping.setXPath("orm:variable-one-to-one");
        descriptor.addMapping(variableOneToOnesMapping);

        XMLCompositeCollectionMapping manyToManysMapping = new XMLCompositeCollectionMapping();
        manyToManysMapping.setAttributeName("m_manyToManys");
        manyToManysMapping.setGetMethodName("getManyToManys");
        manyToManysMapping.setSetMethodName("setManyToManys");
        manyToManysMapping.setReferenceClass(ManyToManyAccessor.class);
        manyToManysMapping.setXPath("orm:many-to-many");
        descriptor.addMapping(manyToManysMapping);

        XMLCompositeCollectionMapping elementCollectionsMapping = new XMLCompositeCollectionMapping();
        elementCollectionsMapping.setAttributeName("m_elementCollections");
        elementCollectionsMapping.setGetMethodName("getElementCollections");
        elementCollectionsMapping.setSetMethodName("setElementCollections");
        elementCollectionsMapping.setReferenceClass(ElementCollectionAccessor.class);
        elementCollectionsMapping.setXPath("orm:element-collection");
        descriptor.addMapping(elementCollectionsMapping);

        XMLCompositeCollectionMapping embeddedsMapping = new XMLCompositeCollectionMapping();
        embeddedsMapping.setAttributeName("m_embeddeds");
        embeddedsMapping.setGetMethodName("getEmbeddeds");
        embeddedsMapping.setSetMethodName("setEmbeddeds");
        embeddedsMapping.setReferenceClass(EmbeddedAccessor.class);
        embeddedsMapping.setXPath("orm:embedded");
        descriptor.addMapping(embeddedsMapping);

        XMLCompositeCollectionMapping transformationsMapping = new XMLCompositeCollectionMapping();
        transformationsMapping.setAttributeName("m_transformations");
        transformationsMapping.setGetMethodName("getTransformations");
        transformationsMapping.setSetMethodName("setTransformations");
        transformationsMapping.setReferenceClass(TransformationAccessor.class);
        transformationsMapping.setXPath("orm:transformation");
        descriptor.addMapping(transformationsMapping);

        XMLCompositeCollectionMapping transientsMapping = new XMLCompositeCollectionMapping();
        transientsMapping.setAttributeName("m_transients");
        transientsMapping.setGetMethodName("getTransients");
        transientsMapping.setSetMethodName("setTransients");
        transientsMapping.setReferenceClass(TransientAccessor.class);
        transientsMapping.setXPath("orm:transient");
        descriptor.addMapping(transientsMapping);

        XMLCompositeCollectionMapping structsMapping = new XMLCompositeCollectionMapping();
        structsMapping.setAttributeName("m_structures");
        structsMapping.setGetMethodName("getStructures");
        structsMapping.setSetMethodName("setStructures");
        structsMapping.setReferenceClass(StructureAccessor.class);
        structsMapping.setXPath("orm:structure");
        descriptor.addMapping(structsMapping);

        XMLCompositeCollectionMapping arrayMapping = new XMLCompositeCollectionMapping();
        arrayMapping.setAttributeName("m_arrays");
        arrayMapping.setGetMethodName("getArrays");
        arrayMapping.setSetMethodName("setArrays");
        arrayMapping.setReferenceClass(ArrayAccessor.class);
        arrayMapping.setXPath("orm:array");
        descriptor.addMapping(arrayMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: basic-collection
     */
    protected ClassDescriptor buildBasicCollectionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(BasicCollectionAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getValueColumnMapping());
        descriptor.addMapping(getConvertMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getCollectionTableMapping());
        descriptor.addMapping(getCascadeOnDeleteMapping());
        descriptor.addMapping(getJoinFetchMapping());
        descriptor.addMapping(getBatchFetchMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getNonCacheableMapping());

        // Attribute Mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: basic
     */
    protected ClassDescriptor buildBasicDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(BasicAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getColumnMapping());
        descriptor.addMapping(getFieldMapping());
        descriptor.addMapping(getIndexMapping());
        descriptor.addMapping(getCacheIndexMapping());
        descriptor.addMapping(getGeneratedValueMapping());
        descriptor.addMapping(getLobMapping());
        descriptor.addMapping(getTemporalMapping());
        descriptor.addMapping(getEnumeratedMapping());
        descriptor.addMapping(getConvertMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getTableGeneratorMapping());
        descriptor.addMapping(getSequenceGeneratorMapping());
        descriptor.addMapping(getUuidGeneratorMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getReturnInsertMapping());
        descriptor.addMapping(getReturnUpdateMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getOptionalAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getMutableAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: basic-map
     */
    protected ClassDescriptor buildBasicMapDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(BasicMapAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        XMLCompositeObjectMapping keyColumnMapping = new XMLCompositeObjectMapping();
        keyColumnMapping.setAttributeName("m_keyColumn");
        keyColumnMapping.setGetMethodName("getKeyColumn");
        keyColumnMapping.setSetMethodName("setKeyColumn");
        keyColumnMapping.setReferenceClass(ColumnMetadata.class);
        keyColumnMapping.setXPath("orm:key-column");
        descriptor.addMapping(keyColumnMapping);

        XMLDirectMapping keyConverterMapping = new XMLDirectMapping();
        keyConverterMapping.setAttributeName("m_keyConverter");
        keyConverterMapping.setGetMethodName("getKeyConverter");
        keyConverterMapping.setSetMethodName("setKeyConverter");
        keyConverterMapping.setXPath("orm:key-converter/text()");
        descriptor.addMapping(keyConverterMapping);

        descriptor.addMapping(getValueColumnMapping());

        XMLDirectMapping valueConverterMapping = new XMLDirectMapping();
        valueConverterMapping.setAttributeName("m_valueConverter");
        valueConverterMapping.setGetMethodName("getValueConverter");
        valueConverterMapping.setSetMethodName("setValueConverter");
        valueConverterMapping.setXPath("orm:value-converter/text()");
        descriptor.addMapping(valueConverterMapping);

        addConverterMappings(descriptor);
        descriptor.addMapping(getCollectionTableMapping());
        descriptor.addMapping(getCascadeOnDeleteMapping());
        descriptor.addMapping(getJoinFetchMapping());
        descriptor.addMapping(getBatchFetchMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getNonCacheableMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: batch-fetch
     */
    protected ClassDescriptor buildBatchFetchDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(BatchFetchMetadata.class);

        descriptor.addMapping(getTypeAttributeMapping());
        descriptor.addMapping(getSizeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: cache
     */
    protected ClassDescriptor buildCacheDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CacheMetadata.class);

        XMLDirectMapping expiryMapping = new XMLDirectMapping();
        expiryMapping.setAttributeName("m_expiry");
        expiryMapping.setGetMethodName("getExpiry");
        expiryMapping.setSetMethodName("setExpiry");
        expiryMapping.setXPath("orm:expiry/text()");
        descriptor.addMapping(expiryMapping);

        XMLCompositeObjectMapping expiryTimeOfDayMapping = new XMLCompositeObjectMapping();
        expiryTimeOfDayMapping.setAttributeName("m_expiryTimeOfDay");
        expiryTimeOfDayMapping.setGetMethodName("getExpiryTimeOfDay");
        expiryTimeOfDayMapping.setSetMethodName("setExpiryTimeOfDay");
        expiryTimeOfDayMapping.setReferenceClass(TimeOfDayMetadata.class);
        expiryTimeOfDayMapping.setXPath("orm:time-of-day");
        descriptor.addMapping(expiryTimeOfDayMapping);

        descriptor.addMapping(getSizeAttributeMapping());

        XMLDirectMapping sharedMapping = new XMLDirectMapping();
        sharedMapping.setAttributeName("m_shared");
        sharedMapping.setGetMethodName("getIsolation");
        sharedMapping.setSetMethodName("setIsolation");
        sharedMapping.setXPath("@shared");
        ObjectTypeConverter sharedConverter = new ObjectTypeConverter();
        sharedConverter.addConversionValue("true", "ISOLATED");
        sharedConverter.addConversionValue("false", "SHARED");
        sharedMapping.setConverter(sharedConverter);
        descriptor.addMapping(sharedMapping);

        XMLDirectMapping isolationMapping = new XMLDirectMapping();
        isolationMapping.setAttributeName("m_isolation");
        isolationMapping.setGetMethodName("getIsolation");
        isolationMapping.setSetMethodName("setIsolation");
        isolationMapping.setXPath("@isolation");
        descriptor.addMapping(isolationMapping);

        descriptor.addMapping(getTypeAttributeMapping());

        XMLDirectMapping alwaysRefreshMapping = new XMLDirectMapping();
        alwaysRefreshMapping.setAttributeName("m_alwaysRefresh");
        alwaysRefreshMapping.setGetMethodName("getAlwaysRefresh");
        alwaysRefreshMapping.setSetMethodName("setAlwaysRefresh");
        alwaysRefreshMapping.setXPath("@always-refresh");
        descriptor.addMapping(alwaysRefreshMapping);

        XMLDirectMapping refreshOnlyIfNewerMapping = new XMLDirectMapping();
        refreshOnlyIfNewerMapping.setAttributeName("m_refreshOnlyIfNewer");
        refreshOnlyIfNewerMapping.setGetMethodName("getRefreshOnlyIfNewer");
        refreshOnlyIfNewerMapping.setSetMethodName("setRefreshOnlyIfNewer");
        refreshOnlyIfNewerMapping.setXPath("@refresh-only-if-newer");
        descriptor.addMapping(refreshOnlyIfNewerMapping);

        XMLDirectMapping disableHitsMapping = new XMLDirectMapping();
        disableHitsMapping.setAttributeName("m_disableHits");
        disableHitsMapping.setGetMethodName("getDisableHits");
        disableHitsMapping.setSetMethodName("setDisableHits");
        disableHitsMapping.setXPath("@disable-hits");
        descriptor.addMapping(disableHitsMapping);

        XMLDirectMapping coordinationTypeMapping = new XMLDirectMapping();
        coordinationTypeMapping.setAttributeName("m_coordinationType");
        coordinationTypeMapping.setGetMethodName("getCoordinationType");
        coordinationTypeMapping.setSetMethodName("setCoordinationType");
        coordinationTypeMapping.setXPath("@coordination-type");
        descriptor.addMapping(coordinationTypeMapping);

        XMLDirectMapping databaseChangeNotificationTypeMapping = new XMLDirectMapping();
        databaseChangeNotificationTypeMapping.setAttributeName("m_databaseChangeNotificationType");
        databaseChangeNotificationTypeMapping.setGetMethodName("getDatabaseChangeNotificationType");
        databaseChangeNotificationTypeMapping.setSetMethodName("setDatabaseChangeNotificationType");
        databaseChangeNotificationTypeMapping.setXPath("@databaseChangeNotification-type");
        descriptor.addMapping(databaseChangeNotificationTypeMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: cache-interceptor
     */
    protected ClassDescriptor buildCacheInterceptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CacheInterceptorMetadata.class);

        XMLDirectMapping interceptorClassName = new XMLDirectMapping();
        interceptorClassName.setAttributeName("m_interceptorClassName");
        interceptorClassName.setGetMethodName("getInterceptorClassName");
        interceptorClassName.setSetMethodName("setInterceptorClassName");
        interceptorClassName.setXPath("@class");
        descriptor.addMapping(interceptorClassName);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: cascade-type
     */
    protected ClassDescriptor buildCascadeTypeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CascadeMetadata.class);

        XMLDirectMapping cascadeAllMapping = new XMLDirectMapping();
        cascadeAllMapping.setAttributeName("m_cascadeAll");
        cascadeAllMapping.setGetMethodName("getCascadeAll");
        cascadeAllMapping.setSetMethodName("setCascadeAll");
        cascadeAllMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy cascadeAllPolicy = new IsSetNullPolicy("isCascadeAll");
        cascadeAllPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        cascadeAllMapping.setNullPolicy(cascadeAllPolicy);
        cascadeAllMapping.setXPath("orm:cascade-all");
        descriptor.addMapping(cascadeAllMapping);

        descriptor.addMapping(getCascadePersistMapping());

        XMLDirectMapping cascadeMergeMapping = new XMLDirectMapping();
        cascadeMergeMapping.setAttributeName("m_cascadeMerge");
        cascadeMergeMapping.setGetMethodName("getCascadeMerge");
        cascadeMergeMapping.setSetMethodName("setCascadeMerge");
        cascadeMergeMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy cascadeMergePolicy = new IsSetNullPolicy("isCascadeMerge");
        cascadeMergePolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        cascadeMergeMapping.setNullPolicy(cascadeMergePolicy);
        cascadeMergeMapping.setXPath("orm:cascade-merge");
        descriptor.addMapping(cascadeMergeMapping);

        XMLDirectMapping cascadeRemoveMapping = new XMLDirectMapping();
        cascadeRemoveMapping.setAttributeName("m_cascadeRemove");
        cascadeRemoveMapping.setGetMethodName("getCascadeRemove");
        cascadeRemoveMapping.setSetMethodName("setCascadeRemove");
        cascadeRemoveMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy cascadeRemovePolicy = new IsSetNullPolicy("isCascadeRemove");
        cascadeRemovePolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        cascadeRemoveMapping.setNullPolicy(cascadeRemovePolicy);
        cascadeRemoveMapping.setXPath("orm:cascade-remove");
        descriptor.addMapping(cascadeRemoveMapping);

        XMLDirectMapping cascadeRefreshMapping = new XMLDirectMapping();
        cascadeRefreshMapping.setAttributeName("m_cascadeRefresh");
        cascadeRefreshMapping.setGetMethodName("getCascadeRefresh");
        cascadeRefreshMapping.setSetMethodName("setCascadeRefresh");
        cascadeRefreshMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy cascadeRefreshPolicy = new IsSetNullPolicy("isCascadeRefresh");
        cascadeRefreshPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        cascadeRefreshMapping.setNullPolicy(cascadeRefreshPolicy);
        cascadeRefreshMapping.setXPath("orm:cascade-refresh");
        descriptor.addMapping(cascadeRefreshMapping);

        XMLDirectMapping cascadeDetachMapping = new XMLDirectMapping();
        cascadeDetachMapping.setAttributeName("m_cascadeDetach");
        cascadeDetachMapping.setGetMethodName("getCascadeDetach");
        cascadeDetachMapping.setSetMethodName("setCascadeDetach");
        cascadeDetachMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy cascadeDetachPolicy = new IsSetNullPolicy("isCascadeDetach");
        cascadeDetachPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        cascadeDetachMapping.setNullPolicy(cascadeDetachPolicy);
        cascadeDetachMapping.setXPath("orm:cascade-detach");
        descriptor.addMapping(cascadeDetachMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: change-tracking
     */
    protected ClassDescriptor buildChangeTrackingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ChangeTrackingMetadata.class);

        descriptor.addMapping(getTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: clone-copy-policy
     */
    protected ClassDescriptor buildCloneCopyPolicyDescriptor(){
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CloneCopyPolicyMetadata.class);

        XMLDirectMapping methodMapping = new XMLDirectMapping();
        methodMapping.setAttributeName("methodName");
        methodMapping.setGetMethodName("getMethodName");
        methodMapping.setSetMethodName("setMethodName");
        methodMapping.setXPath("@method");
        descriptor.addMapping(methodMapping);

        XMLDirectMapping workingCopyMethodMapping = new XMLDirectMapping();
        workingCopyMethodMapping.setAttributeName("workingCopyMethodName");
        workingCopyMethodMapping.setGetMethodName("getWorkingCopyMethodName");
        workingCopyMethodMapping.setSetMethodName("setWorkingCopyMethodName");
        workingCopyMethodMapping.setXPath("@working-copy-method");
        descriptor.addMapping(workingCopyMethodMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: collection-table
     */
    protected ClassDescriptor buildCollectionTableDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CollectionTableMetadata.class);

        // In a JPA 2.0 specification, join columns will be specified. In an
        // EclipseLink specification, primary key join columns will be
        // specified. We therefore map both since the metadata object is built
        // to handle one or the other. XML will restrict the ability to set
        // both so this is not a problem.

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getJoinColumnMapping());
        descriptor.addMapping(getForeignKeyMapping());
        descriptor.addMapping(getPrimaryKeyJoinColumnMapping());
        descriptor.addMapping(getUniqueConstraintMapping());
        descriptor.addMapping(getIndexesMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getCatalogAttributeMapping());
        descriptor.addMapping(getSchemaAttributeMapping());
        descriptor.addMapping(getCreationSuffixAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: column
     */
    protected ClassDescriptor buildColumnDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ColumnMetadata.class);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getUniqueAttributeMapping());
        descriptor.addMapping(getNullableAttributeMapping());
        descriptor.addMapping(getInsertableAttributeMapping());
        descriptor.addMapping(getUpdatableAttributeMapping());
        descriptor.addMapping(getColumnDefinitionAttributeMapping());
        descriptor.addMapping(getTableAttributeMapping());
        descriptor.addMapping(getLengthAttributeMapping());
        descriptor.addMapping(getPrecisionAttributeMapping());
        descriptor.addMapping(getScaleAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: column-result
     */
    protected ClassDescriptor buildColumnResultDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ColumnResultMetadata.class);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getClassTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: column-result
     */
    protected ClassDescriptor buildConstructorResultDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ConstructorResultMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getConstructorColumnMapping());

        // Attribute mappings.
        descriptor.addMapping(getTargetClassAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: conversion-value
     */
    protected ClassDescriptor buildConversionValueDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ConversionValueMetadata.class);

        XMLDirectMapping dataValueMapping = new XMLDirectMapping();
        dataValueMapping.setAttributeName("m_dataValue");
        dataValueMapping.setGetMethodName("getDataValue");
        dataValueMapping.setSetMethodName("setDataValue");
        dataValueMapping.setXPath("@data-value");
        descriptor.addMapping(dataValueMapping);

        XMLDirectMapping objectValueMapping = new XMLDirectMapping();
        objectValueMapping.setAttributeName("m_objectValue");
        objectValueMapping.setGetMethodName("getObjectValue");
        objectValueMapping.setSetMethodName("setObjectValue");
        objectValueMapping.setXPath("@object-value");
        descriptor.addMapping(objectValueMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: convert
     */
    protected ClassDescriptor buildConvertDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ConvertMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getTextMapping());

        // Attribute mappings
        descriptor.addMapping(getConverterAttributeMapping());
        descriptor.addMapping(getAttributeNameAttributeMapping());
        descriptor.addMapping(getDisableConversionAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: converter
     */
    protected ClassDescriptor buildConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ConverterMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getClassAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: copy-policy
     */
    protected ClassDescriptor buildCustomCopyPolicyDescriptor(){
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(CustomCopyPolicyMetadata.class);
        XMLDirectMapping classMapping = new XMLDirectMapping();
        classMapping.setAttributeName("copyPolicyClassName");
        classMapping.setGetMethodName("getCopyPolicyClassName");
        classMapping.setSetMethodName("setCopyPolicyClassName");
        classMapping.setXPath("@class");
        descriptor.addMapping(classMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: cache-interceptor
     */
    protected ClassDescriptor buildDefaultRedirectorsDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(QueryRedirectorsMetadata.class);

        XMLDirectMapping allQueries = new XMLDirectMapping();
        allQueries.setAttributeName("defaultQueryRedirectorName");
        allQueries.setGetMethodName("getDefaultQueryRedirectorName");
        allQueries.setSetMethodName("setDefaultQueryRedirectorName");
        allQueries.setXPath("@all-queries");
        descriptor.addMapping(allQueries);

        XMLDirectMapping readAllQuery = new XMLDirectMapping();
        readAllQuery.setAttributeName("defaultReadAllQueryRedirectorName");
        readAllQuery.setGetMethodName("getDefaultReadAllQueryRedirectorName");
        readAllQuery.setSetMethodName("setDefaultReadAllQueryRedirectorName");
        readAllQuery.setXPath("@read-all");
        descriptor.addMapping(readAllQuery);

        XMLDirectMapping readObjectQuery = new XMLDirectMapping();
        readObjectQuery.setAttributeName("defaultReadObjectQueryRedirectorName");
        readObjectQuery.setGetMethodName("getDefaultReadObjectQueryRedirectorName");
        readObjectQuery.setSetMethodName("setDefaultReadObjectQueryRedirectorName");
        readObjectQuery.setXPath("@read-object");
        descriptor.addMapping(readObjectQuery);

        XMLDirectMapping reportQuery = new XMLDirectMapping();
        reportQuery.setAttributeName("defaultReportQueryRedirectorName");
        reportQuery.setGetMethodName("getDefaultReportQueryRedirectorName");
        reportQuery.setSetMethodName("setDefaultReportQueryRedirectorName");
        reportQuery.setXPath("@report");
        descriptor.addMapping(reportQuery);

        XMLDirectMapping updateQuery = new XMLDirectMapping();
        updateQuery.setAttributeName("defaultUpdateObjectQueryRedirectorName");
        updateQuery.setGetMethodName("getDefaultUpdateObjectQueryRedirectorName");
        updateQuery.setSetMethodName("setDefaultUpdateObjectQueryRedirectorName");
        updateQuery.setXPath("@update");
        descriptor.addMapping(updateQuery);

        XMLDirectMapping insertQuery = new XMLDirectMapping();
        insertQuery.setAttributeName("defaultInsertObjectQueryRedirectorName");
        insertQuery.setGetMethodName("getDefaultInsertObjectQueryRedirectorName");
        insertQuery.setSetMethodName("setDefaultInsertObjectQueryRedirectorName");
        insertQuery.setXPath("@insert");
        descriptor.addMapping(insertQuery);

        XMLDirectMapping deleteQuery = new XMLDirectMapping();
        deleteQuery.setAttributeName("defaultDeleteObjectQueryRedirectorName");
        deleteQuery.setGetMethodName("getDefaultDeleteObjectQueryRedirectorName");
        deleteQuery.setSetMethodName("setDefaultDeleteObjectQueryRedirectorName");
        deleteQuery.setXPath("@delete");
        descriptor.addMapping(deleteQuery);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: discriminator-class
     */
    protected ClassDescriptor buildDiscriminatorClassDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DiscriminatorClassMetadata.class);

        XMLDirectMapping discriminatorMapping = new XMLDirectMapping();
        discriminatorMapping.setAttributeName("m_discriminator");
        discriminatorMapping.setGetMethodName("getDiscriminator");
        discriminatorMapping.setSetMethodName("setDiscriminator");
        discriminatorMapping.setXPath("@discriminator");
        descriptor.addMapping(discriminatorMapping);

        descriptor.addMapping(getValueAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: discriminator-column
     */
    protected ClassDescriptor buildDiscriminatorColumnDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DiscriminatorColumnMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getDiscriminatorTypeAttributeMapping());
        descriptor.addMapping(getColumnDefinitionAttributeMapping());
        descriptor.addMapping(getLengthAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: element-collection
     */
    protected ClassDescriptor buildElementCollectionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ElementCollectionAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getOrderByMapping());
        descriptor.addMapping(getOrderColumnMapping());
        descriptor.addMapping(getMapKeyMapping());
        descriptor.addMapping(getMapKeyClassMapping());
        descriptor.addMapping(getMapKeyTemporalMapping());
        descriptor.addMapping(getMapKeyEnumeratedMapping());
        descriptor.addMapping(getMapKeyAttributeOverrideMapping());
        descriptor.addMapping(getMapKeyConvertMapping());
        descriptor.addMapping(getMapKeyAssociationOverrideMapping());
        descriptor.addMapping(getMapKeyColumnMapping());
        descriptor.addMapping(getMapKeyJoinColumnMapping());
        descriptor.addMapping(getMapKeyForeignKeyMapping());
        descriptor.addMapping(getColumnMapping());
        descriptor.addMapping(getTemporalMapping());
        descriptor.addMapping(getEnumeratedMapping());
        descriptor.addMapping(getLobMapping());
        descriptor.addMapping(getConvertMapping());
        descriptor.addMapping(getAttributeOverrideMapping());
        descriptor.addMapping(getAssociationOverrideMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getCollectionTableMapping());
        descriptor.addMapping(getFieldMapping());
        descriptor.addMapping(getCascadeOnDeleteMapping());
        descriptor.addMapping(getJoinFetchMapping());
        descriptor.addMapping(getBatchFetchMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getNonCacheableMapping());
        descriptor.addMapping(getDeleteAllMapping());
        descriptor.addMapping(getPartitioningMapping());
        descriptor.addMapping(getReplicationPartitioningMapping());
        descriptor.addMapping(getRoundRobinPartitioningMapping());
        descriptor.addMapping(getPinnedPartitioningMapping());
        descriptor.addMapping(getRangePartitioningMapping());
        descriptor.addMapping(getValuePartitioningMapping());
        descriptor.addMapping(getHashPartitioningMapping());
        descriptor.addMapping(getUnionPartitioningMapping());
        descriptor.addMapping(getPartitionedMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getTargetClassAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());
        descriptor.addMapping(getCompositeMemberAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: array
     */
    protected ClassDescriptor buildArrayDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ArrayAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getColumnMapping());
        descriptor.addMapping(getTemporalMapping());
        descriptor.addMapping(getEnumeratedMapping());
        descriptor.addMapping(getLobMapping());
        descriptor.addMapping(getConvertMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getTargetClassAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());
        descriptor.addMapping(getDatabaseTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: embeddable
     */
    protected ClassDescriptor buildEmbeddableDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EmbeddableAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getDescriptionMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getCustomizerMapping());
        descriptor.addMapping(getChangeTrackingMapping());
        descriptor.addMapping(getStructMapping());
        descriptor.addMapping(getNoSqlMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getCustomCopyPolicyMapping());
        descriptor.addMapping(getInstantiationCopyPolicyMapping());
        descriptor.addMapping(getCloneCopyPolicyMapping());
        descriptor.addMapping(getOracleObjectTypeMapping());
        descriptor.addMapping(getOracleArrayTypeMapping());
        descriptor.addMapping(getPLSQLRecordMapping());
        descriptor.addMapping(getPLSQLTableMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAttributeOverrideMapping());
        descriptor.addMapping(getAssociationOverrideMapping());
        descriptor.addMapping(getAttributesMapping());

        // Attribute mappings.
        descriptor.addMapping(getClassAttributeMapping());
        descriptor.addMapping(getParentClassAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getMetadataCompleteAttributeMapping());
        descriptor.addMapping(getExcludeDefaultMappingsAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: embedded
     */
    protected ClassDescriptor buildEmbeddedDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EmbeddedAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getAttributeOverrideMapping());
        descriptor.addMapping(getAssociationOverrideMapping());
        descriptor.addMapping(getConvertMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getFieldMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: structure
     */
    protected ClassDescriptor buildStructureDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StructureAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());
        descriptor.addMapping(getTargetClassAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: embedded-id
     */
    protected ClassDescriptor buildEmbeddedIdDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EmbeddedIdAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getAttributeOverrideMapping());
        descriptor.addMapping(getAssociationOverrideMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: entity
     */
    protected ClassDescriptor buildEntityDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EntityAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getDescriptionMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getMultitenantMapping());
        descriptor.addMapping(getAdditionalCriteriaMapping());
        descriptor.addMapping(getCustomizerMapping());
        descriptor.addMapping(getChangeTrackingMapping());

        XMLCompositeObjectMapping tableMapping = new XMLCompositeObjectMapping();
        tableMapping.setAttributeName("m_table");
        tableMapping.setGetMethodName("getTable");
        tableMapping.setSetMethodName("setTable");
        tableMapping.setReferenceClass(TableMetadata.class);
        tableMapping.setXPath("orm:table");
        descriptor.addMapping(tableMapping);

        XMLCompositeCollectionMapping secondaryTablesMapping = new XMLCompositeCollectionMapping();
        secondaryTablesMapping.setAttributeName("m_secondaryTables");
        secondaryTablesMapping.setGetMethodName("getSecondaryTables");
        secondaryTablesMapping.setSetMethodName("setSecondaryTables");
        secondaryTablesMapping.setReferenceClass(SecondaryTableMetadata.class);
        secondaryTablesMapping.setXPath("orm:secondary-table");
        descriptor.addMapping(secondaryTablesMapping);

        descriptor.addMapping(getStructMapping());
        descriptor.addMapping(getNoSqlMapping());
        descriptor.addMapping(getPrimaryKeyJoinColumnMapping());
        descriptor.addMapping(getPrimaryKeyForeignKeyMapping());
        descriptor.addMapping(getCascadeOnDeleteMapping());
        descriptor.addMapping(getIndexesMapping());
        descriptor.addMapping(getIdClassMapping());
        descriptor.addMapping(getPrimaryKeyMapping());

        XMLCompositeObjectMapping inheritanceMapping = new XMLCompositeObjectMapping();
        inheritanceMapping.setAttributeName("m_inheritance");
        inheritanceMapping.setGetMethodName("getInheritance");
        inheritanceMapping.setSetMethodName("setInheritance");
        inheritanceMapping.setReferenceClass(InheritanceMetadata.class);
        inheritanceMapping.setXPath("orm:inheritance");
        descriptor.addMapping(inheritanceMapping);

        XMLDirectMapping discriminatorValueMapping = new XMLDirectMapping();
        discriminatorValueMapping.setAttributeName("m_discriminatorValue");
        discriminatorValueMapping.setGetMethodName("getDiscriminatorValue");
        discriminatorValueMapping.setSetMethodName("setDiscriminatorValue");
        discriminatorValueMapping.setXPath("orm:discriminator-value/text()");
        descriptor.addMapping(discriminatorValueMapping);

        descriptor.addMapping(getDiscriminatorColumnMapping());
        descriptor.addMapping(getClassExtractorMapping());
        descriptor.addMapping(getOptimisticLockingMapping());
        descriptor.addMapping(getCacheMapping());
        descriptor.addMapping(getCacheInterceptorMapping());
        descriptor.addMapping(getCacheIndexesMapping());
        descriptor.addMapping(getFetchGroupMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getCustomCopyPolicyMapping());
        descriptor.addMapping(getInstantiationCopyPolicyMapping());
        descriptor.addMapping(getCloneCopyPolicyMapping());
        descriptor.addMapping(getSerializedObjectPolicyMapping());
        descriptor.addMapping(getSequenceGeneratorMapping());
        descriptor.addMapping(getTableGeneratorMapping());
        descriptor.addMapping(getUuidGeneratorMapping());
        descriptor.addMapping(getPartitioningMapping());
        descriptor.addMapping(getReplicationPartitioningMapping());
        descriptor.addMapping(getRoundRobinPartitioningMapping());
        descriptor.addMapping(getPinnedPartitioningMapping());
        descriptor.addMapping(getRangePartitioningMapping());
        descriptor.addMapping(getValuePartitioningMapping());
        descriptor.addMapping(getHashPartitioningMapping());
        descriptor.addMapping(getUnionPartitioningMapping());
        descriptor.addMapping(getPartitionedMapping());
        descriptor.addMapping(getNamedQueryMapping());
        descriptor.addMapping(getNamedNativeQueryMapping());
        descriptor.addMapping(getNamedStoredProcedureQueryMapping());
        descriptor.addMapping(getNamedStoredFunctionQueryMapping());
        descriptor.addMapping(getNamedPLSQLStoredProcedureQueryMapping());
        descriptor.addMapping(getNamedPLSQLStoredFunctionQueryMapping());
        descriptor.addMapping(getOracleObjectTypeMapping());
        descriptor.addMapping(getOracleArrayTypeMapping());
        descriptor.addMapping(getPLSQLRecordMapping());
        descriptor.addMapping(getPLSQLTableMapping());
        descriptor.addMapping(getResultSetMappingMapping());
        descriptor.addMapping(getQueryRedirectorsMapping());
        descriptor.addMapping(getExcludeDefaultListenersMapping());
        descriptor.addMapping(getExcludeSuperclassListenersMapping());
        descriptor.addMapping(getEntityListenersMapping());
        descriptor.addMapping(getPrePeristMapping());
        descriptor.addMapping(getPostPeristMapping());
        descriptor.addMapping(getPreRemoveMapping());
        descriptor.addMapping(getPostRemoveMapping());
        descriptor.addMapping(getPreUpdateMapping());
        descriptor.addMapping(getPostUpdateMapping());
        descriptor.addMapping(getPostLoadMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAttributeOverrideMapping());
        descriptor.addMapping(getAssociationOverrideMapping());
        descriptor.addMapping(getConvertMapping());
        descriptor.addMapping(getNamedEntityGraphMapping());
        descriptor.addMapping(getAttributesMapping());

        // Attribute mappings.
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_entityName");
        nameMapping.setGetMethodName("getEntityName");
        nameMapping.setSetMethodName("setEntityName");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        descriptor.addMapping(getClassAttributeMapping());
        descriptor.addMapping(getParentClassAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getCacheableAttributeMapping());
        descriptor.addMapping(getMetadataCompleteAttributeMapping());
        descriptor.addMapping(getExcludeDefaultMappingsAttributeMapping());
        descriptor.addMapping(getReadOnlyAttributeMapping());
        descriptor.addMapping(getExistenceCheckingAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: entity-listener
     */
    protected ClassDescriptor buildEntityListenerDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EntityListenerMetadata.class);

        descriptor.addMapping(getPrePeristMapping());
        descriptor.addMapping(getPostPeristMapping());
        descriptor.addMapping(getPreRemoveMapping());
        descriptor.addMapping(getPostRemoveMapping());
        descriptor.addMapping(getPreUpdateMapping());
        descriptor.addMapping(getPostUpdateMapping());
        descriptor.addMapping(getPostLoadMapping());
        descriptor.addMapping(getClassAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: entity-mappings
     */
    protected ClassDescriptor buildEntityMappingsDescriptor(String xsdLocation) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setDefaultRootElement("orm:entity-mappings");
        descriptor.setJavaClass(XMLEntityMappings.class);
        descriptor.setSchemaReference(new XMLSchemaClassPathReference("/" + xsdLocation));

        descriptor.addMapping(getDescriptionMapping());

        XMLCompositeObjectMapping persistenceUnitMetadataMapping = new XMLCompositeObjectMapping();
        persistenceUnitMetadataMapping.setAttributeName("m_persistenceUnitMetadata");
        persistenceUnitMetadataMapping.setGetMethodName("getPersistenceUnitMetadata");
        persistenceUnitMetadataMapping.setSetMethodName("setPersistenceUnitMetadata");
        persistenceUnitMetadataMapping.setReferenceClass(XMLPersistenceUnitMetadata.class);
        persistenceUnitMetadataMapping.setXPath("orm:persistence-unit-metadata");
        descriptor.addMapping(persistenceUnitMetadataMapping);

        XMLDirectMapping packageMapping = new XMLDirectMapping();
        packageMapping.setAttributeName("m_package");
        packageMapping.setGetMethodName("getPackage");
        packageMapping.setSetMethodName("setPackage");
        packageMapping.setXPath("orm:package/text()");
        descriptor.addMapping(packageMapping);

        descriptor.addMapping(getSchemaMapping());
        descriptor.addMapping(getCatalogMapping());
        descriptor.addMapping(getAccessMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getTenantDiscriminatorColumnsMapping());
        descriptor.addMapping(getMixedConverterMapping());
        descriptor.addMapping(getTypeConverterMapping());
        descriptor.addMapping(getObjectTypeConverterMapping());
        descriptor.addMapping(getSerializedConverterMapping());
        descriptor.addMapping(getStructConverterMapping());

        XMLCompositeCollectionMapping sequenceGeneratorsMapping = new XMLCompositeCollectionMapping();
        sequenceGeneratorsMapping.setAttributeName("m_sequenceGenerators");
        sequenceGeneratorsMapping.setGetMethodName("getSequenceGenerators");
        sequenceGeneratorsMapping.setSetMethodName("setSequenceGenerators");
        sequenceGeneratorsMapping.setReferenceClass(SequenceGeneratorMetadata.class);
        sequenceGeneratorsMapping.setXPath("orm:sequence-generator");
        descriptor.addMapping(sequenceGeneratorsMapping);

        XMLCompositeCollectionMapping tableGeneratorsMapping = new XMLCompositeCollectionMapping();
        tableGeneratorsMapping.setAttributeName("m_tableGenerators");
        tableGeneratorsMapping.setGetMethodName("getTableGenerators");
        tableGeneratorsMapping.setSetMethodName("setTableGenerators");
        tableGeneratorsMapping.setReferenceClass(TableGeneratorMetadata.class);
        tableGeneratorsMapping.setXPath("orm:table-generator");
        descriptor.addMapping(tableGeneratorsMapping);

        XMLCompositeCollectionMapping uuidGeneratorsMapping = new XMLCompositeCollectionMapping();
        uuidGeneratorsMapping.setAttributeName("m_uuidGenerators");
        uuidGeneratorsMapping.setGetMethodName("getUuidGenerators");
        uuidGeneratorsMapping.setSetMethodName("setUuidGenerators");
        uuidGeneratorsMapping.setReferenceClass(UuidGeneratorMetadata.class);
        uuidGeneratorsMapping.setXPath("orm:uuid-generator");
        descriptor.addMapping(uuidGeneratorsMapping);

        XMLCompositeCollectionMapping partitioningMapping = new XMLCompositeCollectionMapping();
        partitioningMapping.setAttributeName("m_partitioning");
        partitioningMapping.setGetMethodName("getPartitioning");
        partitioningMapping.setSetMethodName("setPartitioning");
        partitioningMapping.setReferenceClass(PartitioningMetadata.class);
        partitioningMapping.setXPath("orm:partitioning");
        descriptor.addMapping(partitioningMapping);

        partitioningMapping = new XMLCompositeCollectionMapping();
        partitioningMapping.setAttributeName("m_replicationPartitioning");
        partitioningMapping.setGetMethodName("getReplicationPartitioning");
        partitioningMapping.setSetMethodName("setReplicationPartitioning");
        partitioningMapping.setReferenceClass(ReplicationPartitioningMetadata.class);
        partitioningMapping.setXPath("orm:replication-partitioning");
        descriptor.addMapping(partitioningMapping);

        partitioningMapping = new XMLCompositeCollectionMapping();
        partitioningMapping.setAttributeName("m_roundRobinPartitioning");
        partitioningMapping.setGetMethodName("getRoundRobinPartitioning");
        partitioningMapping.setSetMethodName("setRoundRobinPartitioning");
        partitioningMapping.setReferenceClass(RoundRobinPartitioningMetadata.class);
        partitioningMapping.setXPath("orm:round-robin-partitioning");
        descriptor.addMapping(partitioningMapping);

        partitioningMapping = new XMLCompositeCollectionMapping();
        partitioningMapping.setAttributeName("m_pinnedPartitioning");
        partitioningMapping.setGetMethodName("getPinnedPartitioning");
        partitioningMapping.setSetMethodName("setPinnedPartitioning");
        partitioningMapping.setReferenceClass(PinnedPartitioningMetadata.class);
        partitioningMapping.setXPath("orm:pinned-partitioning");
        descriptor.addMapping(partitioningMapping);

        partitioningMapping = new XMLCompositeCollectionMapping();
        partitioningMapping.setAttributeName("m_rangePartitioning");
        partitioningMapping.setGetMethodName("getRangePartitioning");
        partitioningMapping.setSetMethodName("setRangePartitioning");
        partitioningMapping.setReferenceClass(RangePartitioningMetadata.class);
        partitioningMapping.setXPath("orm:range-partitioning");
        descriptor.addMapping(partitioningMapping);

        partitioningMapping = new XMLCompositeCollectionMapping();
        partitioningMapping.setAttributeName("m_valuePartitioning");
        partitioningMapping.setGetMethodName("getValuePartitioning");
        partitioningMapping.setSetMethodName("setValuePartitioning");
        partitioningMapping.setReferenceClass(ValuePartitioningMetadata.class);
        partitioningMapping.setXPath("orm:value-partitioning");
        descriptor.addMapping(partitioningMapping);

        partitioningMapping = new XMLCompositeCollectionMapping();
        partitioningMapping.setAttributeName("m_hashPartitioning");
        partitioningMapping.setGetMethodName("getHashPartitioning");
        partitioningMapping.setSetMethodName("setHashPartitioning");
        partitioningMapping.setReferenceClass(HashPartitioningMetadata.class);
        partitioningMapping.setXPath("orm:hash-partitioning");
        descriptor.addMapping(partitioningMapping);

        partitioningMapping = new XMLCompositeCollectionMapping();
        partitioningMapping.setAttributeName("m_unionPartitioning");
        partitioningMapping.setGetMethodName("getUnionPartitioning");
        partitioningMapping.setSetMethodName("setUnionPartitioning");
        partitioningMapping.setReferenceClass(UnionPartitioningMetadata.class);
        partitioningMapping.setXPath("orm:union-partitioning");
        descriptor.addMapping(partitioningMapping);

        descriptor.addMapping(getNamedQueryMapping());
        descriptor.addMapping(getNamedNativeQueryMapping());
        descriptor.addMapping(getNamedStoredProcedureQueryMapping());
        descriptor.addMapping(getNamedStoredFunctionQueryMapping());
        descriptor.addMapping(getNamedPLSQLStoredProcedureQueryMapping());
        descriptor.addMapping(getNamedPLSQLStoredFunctionQueryMapping());
        descriptor.addMapping(getOracleObjectTypeMapping());
        descriptor.addMapping(getOracleArrayTypeMapping());
        descriptor.addMapping(getPLSQLRecordMapping());
        descriptor.addMapping(getPLSQLTableMapping());
        descriptor.addMapping(getResultSetMappingMapping());

        XMLCompositeCollectionMapping mappedSuperclassMapping = new XMLCompositeCollectionMapping();
        mappedSuperclassMapping.setAttributeName("m_mappedSuperclasses");
        mappedSuperclassMapping.setGetMethodName("getMappedSuperclasses");
        mappedSuperclassMapping.setSetMethodName("setMappedSuperclasses");
        mappedSuperclassMapping.setReferenceClass(MappedSuperclassAccessor.class);
        mappedSuperclassMapping.setXPath("orm:mapped-superclass");
        descriptor.addMapping(mappedSuperclassMapping);

        XMLCompositeCollectionMapping entityMapping = new XMLCompositeCollectionMapping();
        entityMapping.setAttributeName("m_entities");
        entityMapping.setGetMethodName("getEntities");
        entityMapping.setSetMethodName("setEntities");
        entityMapping.setReferenceClass(EntityAccessor.class);
        entityMapping.setXPath("orm:entity");
        descriptor.addMapping(entityMapping);

        XMLCompositeCollectionMapping embeddableMapping = new XMLCompositeCollectionMapping();
        embeddableMapping.setAttributeName("m_embeddables");
        embeddableMapping.setGetMethodName("getEmbeddables");
        embeddableMapping.setSetMethodName("setEmbeddables");
        embeddableMapping.setReferenceClass(EmbeddableAccessor.class);
        embeddableMapping.setXPath("orm:embeddable");
        descriptor.addMapping(embeddableMapping);

        // What about the version attribute???
        XMLDirectMapping versionMapping = new XMLDirectMapping();
        versionMapping.setAttributeName("m_version");
        versionMapping.setGetMethodName("getVersion");
        versionMapping.setSetMethodName("setVersion");
        versionMapping.setXPath("@version");
        descriptor.addMapping(versionMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: entity-result
     */
    protected ClassDescriptor buildEntityResultDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EntityResultMetadata.class);

        XMLCompositeCollectionMapping fieldResultMapping = new XMLCompositeCollectionMapping();
        fieldResultMapping.setAttributeName("m_fieldResults");
        fieldResultMapping.setGetMethodName("getFieldResults");
        fieldResultMapping.setSetMethodName("setFieldResults");
        fieldResultMapping.setReferenceClass(FieldResultMetadata.class);
        fieldResultMapping.setXPath("orm:field-result");
        descriptor.addMapping(fieldResultMapping);

        XMLDirectMapping entityClassMapping = new XMLDirectMapping();
        entityClassMapping.setAttributeName("m_entityClassName");
        entityClassMapping.setGetMethodName("getEntityClassName");
        entityClassMapping.setSetMethodName("setEntityClassName");
        entityClassMapping.setXPath("@entity-class");
        descriptor.addMapping(entityClassMapping);

        XMLDirectMapping discriminatorColumnMapping = new XMLDirectMapping();
        discriminatorColumnMapping.setAttributeName("m_discriminatorColumn");
        discriminatorColumnMapping.setGetMethodName("getDiscriminatorColumn");
        discriminatorColumnMapping.setSetMethodName("setDiscriminatorColumn");
        discriminatorColumnMapping.setXPath("@discriminator-column");
        descriptor.addMapping(discriminatorColumnMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: enumerated
     */
    protected ClassDescriptor buildEnumeratedDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EnumeratedMetadata.class);

        XMLDirectMapping enumeratedMapping = new XMLDirectMapping();
        enumeratedMapping.setAttributeName("m_enumeratedType");
        enumeratedMapping.setGetMethodName("getEnumeratedType");
        enumeratedMapping.setSetMethodName("setEnumeratedType");
        enumeratedMapping.setXPath("text()");
        descriptor.addMapping(enumeratedMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: fetch-attribute
     */
    protected ClassDescriptor buildFetchAttributeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FetchAttributeMetadata.class);

        // Element mappings - must remain in order of definition in XML.

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: fetch-group
     */
    protected ClassDescriptor buildFetchGroupDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FetchGroupMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        XMLCompositeCollectionMapping attributesMapping = new XMLCompositeCollectionMapping();
        attributesMapping.setAttributeName("m_fetchAttributes");
        attributesMapping.setGetMethodName("getFetchAttributes");
        attributesMapping.setSetMethodName("setFetchAttributes");
        attributesMapping.setReferenceClass(FetchAttributeMetadata.class);
        attributesMapping.setXPath("orm:attribute");
        descriptor.addMapping(attributesMapping);

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());

        XMLDirectMapping loadMapping = new XMLDirectMapping();
        loadMapping.setAttributeName("m_load");
        loadMapping.setGetMethodName("getLoad");
        loadMapping.setSetMethodName("setLoad");
        loadMapping.setXPath("@load");
        descriptor.addMapping(loadMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: field
     */
    protected ClassDescriptor buildFieldDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FieldMetadata.class);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: field-result
     */
    protected ClassDescriptor buildFieldResultDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FieldResultMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getColumnAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: foreign-key
     */
    protected ClassDescriptor buildForeignKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ForeignKeyMetadata.class);

        // Element mappings - must remain in order of definition in XML.

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getForeignKeyDefinitionAttributeMapping());
        descriptor.addMapping(getConstraintModeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: generated-value
     */
    protected ClassDescriptor buildGeneratedValueDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(GeneratedValueMetadata.class);

        XMLDirectMapping strategyMapping = new XMLDirectMapping();
        strategyMapping.setAttributeName("m_strategy");
        strategyMapping.setGetMethodName("getStrategy");
        strategyMapping.setSetMethodName("setStrategy");
        strategyMapping.setXPath("@strategy");
        descriptor.addMapping(strategyMapping);

        XMLDirectMapping generatorMapping = new XMLDirectMapping();
        generatorMapping.setAttributeName("m_generator");
        generatorMapping.setGetMethodName("getGenerator");
        generatorMapping.setSetMethodName("setGenerator");
        generatorMapping.setXPath("@generator");
        descriptor.addMapping(generatorMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: hash-partitioning
     */
    protected ClassDescriptor buildHashPartitioningDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(HashPartitioningMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getUnionUnpartitionableQueriesAttributeMapping());
        descriptor.addMapping(getPartitionColumnMapping());
        descriptor.addMapping(getConnectionPoolsMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: id
     */
    protected ClassDescriptor buildIdDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(IdAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getColumnMapping());
        descriptor.addMapping(getFieldMapping());
        descriptor.addMapping(getIndexMapping());
        descriptor.addMapping(getCacheIndexMapping());
        descriptor.addMapping(getGeneratedValueMapping());
        descriptor.addMapping(getTemporalMapping());
        descriptor.addMapping(getEnumeratedMapping());
        descriptor.addMapping(getConvertMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getTableGeneratorMapping());
        descriptor.addMapping(getSequenceGeneratorMapping());
        descriptor.addMapping(getUuidGeneratorMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getMutableAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: index
     */
    protected ClassDescriptor buildIndexDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(IndexMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getColumnNamesMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getTableAttributeMapping());
        descriptor.addMapping(getCatalogAttributeMapping());
        descriptor.addMapping(getSchemaAttributeMapping());
        descriptor.addMapping(getColumnListAttributeMapping());
        descriptor.addMapping(getUniqueAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: cache-index
     */
    protected ClassDescriptor buildCacheIndexDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CacheIndexMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getColumnNamesMapping());

        // Attribute mappings.
        XMLDirectMapping updateableMapping = new XMLDirectMapping();
        updateableMapping.setAttributeName("updateable");
        updateableMapping.setGetMethodName("getUpdateable");
        updateableMapping.setSetMethodName("setUpdateable");
        updateableMapping.setXPath("@updateable");
        descriptor.addMapping(updateableMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: inheritance
     */
    protected ClassDescriptor buildInheritanceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InheritanceMetadata.class);

        XMLDirectMapping strategyMapping = new XMLDirectMapping();
        strategyMapping.setAttributeName("m_strategy");
        strategyMapping.setGetMethodName("getStrategy");
        strategyMapping.setSetMethodName("setStrategy");
        strategyMapping.setXPath("@strategy");
        descriptor.addMapping(strategyMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: instantiation-copy-policy
     */
    protected ClassDescriptor buildInstantiationCopyPolicyDescriptor(){
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InstantiationCopyPolicyMetadata.class);
        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: join-column
     */
    protected ClassDescriptor buildJoinColumnDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JoinColumnMetadata.class);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getReferencedColumnNameMapping());
        descriptor.addMapping(getUniqueAttributeMapping());
        descriptor.addMapping(getNullableAttributeMapping());
        descriptor.addMapping(getInsertableAttributeMapping());
        descriptor.addMapping(getUpdatableAttributeMapping());
        descriptor.addMapping(getColumnDefinitionAttributeMapping());
        descriptor.addMapping(getTableAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: join-field
     */
    protected ClassDescriptor buildJoinFieldDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JoinFieldMetadata.class);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());

        XMLDirectMapping referencedFieldNameMapping = new XMLDirectMapping();
        referencedFieldNameMapping.setAttributeName("m_referencedColumnName");
        referencedFieldNameMapping.setGetMethodName("getReferencedColumnName");
        referencedFieldNameMapping.setSetMethodName("setReferencedColumnName");
        referencedFieldNameMapping.setXPath("@referenced-field-name");
        descriptor.addMapping(referencedFieldNameMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: join-table
     */
    protected ClassDescriptor buildJoinTableDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JoinTableMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getJoinColumnMapping());
        descriptor.addMapping(getForeignKeyMapping());
        descriptor.addMapping(getInverseJoinColumnMapping());
        descriptor.addMapping(getInverseForeignKeyMapping());
        descriptor.addMapping(getUniqueConstraintMapping());
        descriptor.addMapping(getIndexesMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getCatalogAttributeMapping());
        descriptor.addMapping(getSchemaAttributeMapping());
        descriptor.addMapping(getCreationSuffixAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: lob
     */
    protected ClassDescriptor buildLobDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(LobMetadata.class);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: many-to-many
     */
    protected ClassDescriptor buildManyToManyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ManyToManyAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getOrderByMapping());
        descriptor.addMapping(getOrderColumnMapping());
        descriptor.addMapping(getMapKeyMapping());
        descriptor.addMapping(getMapKeyClassMapping());
        descriptor.addMapping(getMapKeyTemporalMapping());
        descriptor.addMapping(getMapKeyEnumeratedMapping());
        descriptor.addMapping(getMapKeyAttributeOverrideMapping());
        descriptor.addMapping(getMapKeyConvertMapping());
        descriptor.addMapping(getMapKeyAssociationOverrideMapping());
        descriptor.addMapping(getMapKeyColumnMapping());
        descriptor.addMapping(getMapKeyJoinColumnMapping());
        descriptor.addMapping(getMapKeyForeignKeyMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getJoinTableMapping());
        descriptor.addMapping(getJoinFieldMapping());
        descriptor.addMapping(getCascadeMapping());
        descriptor.addMapping(getCascadeOnDeleteMapping());
        descriptor.addMapping(getJoinFetchMapping());
        descriptor.addMapping(getBatchFetchMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getNonCacheableMapping());
        descriptor.addMapping(getPartitioningMapping());
        descriptor.addMapping(getReplicationPartitioningMapping());
        descriptor.addMapping(getRoundRobinPartitioningMapping());
        descriptor.addMapping(getPinnedPartitioningMapping());
        descriptor.addMapping(getRangePartitioningMapping());
        descriptor.addMapping(getValuePartitioningMapping());
        descriptor.addMapping(getHashPartitioningMapping());
        descriptor.addMapping(getUnionPartitioningMapping());
        descriptor.addMapping(getPartitionedMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getTargetEntityAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getMappedByAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: many-to-one
     */
    protected ClassDescriptor buildManyToOneDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ManyToOneAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getPrimaryKeyJoinColumnMapping());
        descriptor.addMapping(getPrimaryKeyForeignKeyMapping());
        descriptor.addMapping(getJoinColumnMapping());
        descriptor.addMapping(getForeignKeyMapping());
        descriptor.addMapping(getJoinTableMapping());
        descriptor.addMapping(getJoinFieldMapping());
        descriptor.addMapping(getCascadeMapping());
        descriptor.addMapping(getJoinFetchMapping());
        descriptor.addMapping(getBatchFetchMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getNonCacheableMapping());
        descriptor.addMapping(getPartitioningMapping());
        descriptor.addMapping(getReplicationPartitioningMapping());
        descriptor.addMapping(getRoundRobinPartitioningMapping());
        descriptor.addMapping(getPinnedPartitioningMapping());
        descriptor.addMapping(getRangePartitioningMapping());
        descriptor.addMapping(getValuePartitioningMapping());
        descriptor.addMapping(getHashPartitioningMapping());
        descriptor.addMapping(getUnionPartitioningMapping());
        descriptor.addMapping(getPartitionedMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getTargetEntityAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getOptionalAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getMapsIdAttributeMapping());
        descriptor.addMapping(getIdAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: map-key
     */
    protected ClassDescriptor buildMapKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MapKeyMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: mapped-superclass
     */
    protected ClassDescriptor buildMappedSuperclassDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MappedSuperclassAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getDescriptionMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getMultitenantMapping());
        descriptor.addMapping(getAdditionalCriteriaMapping());
        descriptor.addMapping(getCustomizerMapping());
        descriptor.addMapping(getChangeTrackingMapping());
        descriptor.addMapping(getIdClassMapping());
        descriptor.addMapping(getPrimaryKeyMapping());
        descriptor.addMapping(getOptimisticLockingMapping());
        descriptor.addMapping(getCacheMapping());
        descriptor.addMapping(getCacheIndexesMapping());
        descriptor.addMapping(getCacheInterceptorMapping());
        descriptor.addMapping(getFetchGroupMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getCustomCopyPolicyMapping());
        descriptor.addMapping(getInstantiationCopyPolicyMapping());
        descriptor.addMapping(getCloneCopyPolicyMapping());
        descriptor.addMapping(getSerializedObjectPolicyMapping());
        descriptor.addMapping(getSequenceGeneratorMapping());
        descriptor.addMapping(getTableGeneratorMapping());
        descriptor.addMapping(getUuidGeneratorMapping());
        descriptor.addMapping(getPartitioningMapping());
        descriptor.addMapping(getReplicationPartitioningMapping());
        descriptor.addMapping(getRoundRobinPartitioningMapping());
        descriptor.addMapping(getPinnedPartitioningMapping());
        descriptor.addMapping(getRangePartitioningMapping());
        descriptor.addMapping(getValuePartitioningMapping());
        descriptor.addMapping(getHashPartitioningMapping());
        descriptor.addMapping(getUnionPartitioningMapping());
        descriptor.addMapping(getPartitionedMapping());
        descriptor.addMapping(getNamedQueryMapping());
        descriptor.addMapping(getNamedNativeQueryMapping());
        descriptor.addMapping(getNamedStoredProcedureQueryMapping());
        descriptor.addMapping(getNamedStoredFunctionQueryMapping());
        descriptor.addMapping(getNamedPLSQLStoredProcedureQueryMapping());
        descriptor.addMapping(getNamedPLSQLStoredFunctionQueryMapping());
        descriptor.addMapping(getOracleObjectTypeMapping());
        descriptor.addMapping(getOracleArrayTypeMapping());
        descriptor.addMapping(getPLSQLRecordMapping());
        descriptor.addMapping(getPLSQLTableMapping());
        descriptor.addMapping(getResultSetMappingMapping());
        descriptor.addMapping(getQueryRedirectorsMapping());
        descriptor.addMapping(getExcludeDefaultListenersMapping());
        descriptor.addMapping(getExcludeSuperclassListenersMapping());
        descriptor.addMapping(getEntityListenersMapping());
        descriptor.addMapping(getPrePeristMapping());
        descriptor.addMapping(getPostPeristMapping());
        descriptor.addMapping(getPreRemoveMapping());
        descriptor.addMapping(getPostRemoveMapping());
        descriptor.addMapping(getPreUpdateMapping());
        descriptor.addMapping(getPostUpdateMapping());
        descriptor.addMapping(getPostLoadMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAttributeOverrideMapping());
        descriptor.addMapping(getAssociationOverrideMapping());
        descriptor.addMapping(getAttributesMapping());

        // Attribute mappings.
        descriptor.addMapping(getClassAttributeMapping());
        descriptor.addMapping(getParentClassAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getCacheableAttributeMapping());
        descriptor.addMapping(getMetadataCompleteAttributeMapping());
        descriptor.addMapping(getExcludeDefaultMappingsAttributeMapping());
        descriptor.addMapping(getReadOnlyAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: converter
     */
    protected ClassDescriptor buildMixedConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MixedConverterMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getClassAttributeMapping());
        descriptor.addMapping(getAutoApplyAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: multi-tenant
     */
    protected ClassDescriptor buildMultitenantDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MultitenantMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getTenantDiscriminatorColumnsMapping());
        descriptor.addMapping(getTenantTableDiscriminatorMapping());

        // Attribute mappings.
        descriptor.addMapping(getTypeAttributeMapping());
        descriptor.addMapping(getIncludeCriteriaAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: named-attribute-node
     */
    protected ClassDescriptor buildNamedAttributeNodeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedAttributeNodeMetadata.class);

        // Element mappings - must remain in order of definition in XML.

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getSubgraphAttributeMapping());
        descriptor.addMapping(getKeySubgraphAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: named-entity-graph
     */
    protected ClassDescriptor buildNamedEntityGraphDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedEntityGraphMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getNamedAttributeNodeMapping());
        descriptor.addMapping(getSubgraphMapping());
        descriptor.addMapping(getSubclassSubgraphMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getIncludeAllAttributesAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: named-native-query
     */
    protected ClassDescriptor buildNamedNativeQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedNativeQueryMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getQueryMapping());
        descriptor.addMapping(getHintMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getResultClassAttributeMapping());
        descriptor.addMapping(getResultSetMappingAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: named-plsql-stored-function-query
     */
    protected ClassDescriptor buildNamedPLSQLStoredFunctionQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedPLSQLStoredFunctionQueryMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getHintMapping());
        descriptor.addMapping(getPLSQLParametersMapping());

        XMLCompositeObjectMapping returnParameterMapping = new XMLCompositeObjectMapping();
        returnParameterMapping.setAttributeName("m_returnParameter");
        returnParameterMapping.setGetMethodName("getReturnParameter");
        returnParameterMapping.setSetMethodName("setReturnParameter");
        returnParameterMapping.setReferenceClass(PLSQLParameterMetadata.class);
        returnParameterMapping.setXPath("orm:return-parameter");
        descriptor.addMapping(returnParameterMapping);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getResultClassAttributeMapping());
        descriptor.addMapping(getResultSetMappingAttributeMapping());
        descriptor.addMapping(getFunctionNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: named-plsql-stored-procedure-query
     */
    protected ClassDescriptor buildNamedPLSQLStoredProcedureQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedPLSQLStoredProcedureQueryMetadata.class);

        descriptor.addMapping(getHintMapping());
        descriptor.addMapping(getPLSQLParametersMapping());

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getResultClassAttributeMapping());
        descriptor.addMapping(getResultSetMappingAttributeMapping());
        descriptor.addMapping(getProcedureNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: named-query
     */
    protected ClassDescriptor buildNamedQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedQueryMetadata.class);

        descriptor.addMapping(getQueryMapping());
        descriptor.addMapping(getHintMapping());
        descriptor.addMapping(getLockModeMapping());
        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: named-stored-function-query
     */
    protected ClassDescriptor buildNamedStoredFunctionQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedStoredFunctionQueryMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getHintMapping());
        descriptor.addMapping(getParametersMapping());

        XMLCompositeObjectMapping returnParameterMapping = new XMLCompositeObjectMapping();
        returnParameterMapping.setAttributeName("m_returnParameter");
        returnParameterMapping.setGetMethodName("getReturnParameter");
        returnParameterMapping.setSetMethodName("setReturnParameter");
        returnParameterMapping.setReferenceClass(StoredProcedureParameterMetadata.class);
        returnParameterMapping.setXPath("orm:return-parameter");
        descriptor.addMapping(returnParameterMapping);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getResultSetMappingAttributeMapping());
        descriptor.addMapping(getFunctionNameAttributeMapping());
        descriptor.addMapping(getCallByIndexAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: named-stored-procedure-query
     */
    protected ClassDescriptor buildNamedStoredProcedureQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedStoredProcedureQueryMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getResultClasses());
        descriptor.addMapping(getResultSetMappings());
        descriptor.addMapping(getHintMapping());
        descriptor.addMapping(getParametersMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getResultClassAttributeMapping());
        descriptor.addMapping(getResultSetMappingAttributeMapping());
        descriptor.addMapping(getProcedureNameAttributeMapping());
        descriptor.addMapping(getCallByIndexAttributeMapping());

        XMLDirectMapping returnsResultSetMapping = new XMLDirectMapping();
        returnsResultSetMapping.setAttributeName("m_returnsResultSet");
        returnsResultSetMapping.setGetMethodName("getReturnsResultSet");
        returnsResultSetMapping.setSetMethodName("setReturnsResultSet");
        returnsResultSetMapping.setXPath("@returns-result-set");
        descriptor.addMapping(returnsResultSetMapping);

        XMLDirectMapping multipleResultSetsMapping = new XMLDirectMapping();
        multipleResultSetsMapping.setAttributeName("m_multipleResultSets");
        multipleResultSetsMapping.setGetMethodName("getMultipleResultSets");
        multipleResultSetsMapping.setSetMethodName("setMultipleResultSets");
        multipleResultSetsMapping.setXPath("@multiple-result-sets");
        descriptor.addMapping(multipleResultSetsMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: named-subgraph
     */
    protected ClassDescriptor buildNamedSubgraphDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NamedSubgraphMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getNamedAttributeNodeMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getClassTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: no-sql
     */
    protected ClassDescriptor buildNoSqlDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NoSqlMetadata.class);

        // Attribute mappings.
        XMLDirectMapping dataTypeMapping = new XMLDirectMapping();
        dataTypeMapping.setAttributeName("m_dataType");
        dataTypeMapping.setGetMethodName("getDataType");
        dataTypeMapping.setSetMethodName("setDataType");
        dataTypeMapping.setXPath("@data-type");
        descriptor.addMapping(dataTypeMapping);

        XMLDirectMapping dataFormatMapping = new XMLDirectMapping();
        dataFormatMapping.setAttributeName("m_dataFormat");
        dataFormatMapping.setGetMethodName("getDataFormat");
        dataFormatMapping.setSetMethodName("setDataFormat");
        dataFormatMapping.setXPath("@data-format");
        descriptor.addMapping(dataFormatMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: object-type-converter
     */
    protected ClassDescriptor buildObjectTypeConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectTypeConverterMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getDataTypeAttributeMapping());
        descriptor.addMapping(getObjectTypeAttributeMapping());

        XMLCompositeCollectionMapping conversionValuesMapping = new XMLCompositeCollectionMapping();
        conversionValuesMapping.setAttributeName("m_conversionValues");
        conversionValuesMapping.setGetMethodName("getConversionValues");
        conversionValuesMapping.setSetMethodName("setConversionValues");
        conversionValuesMapping.setReferenceClass(ConversionValueMetadata.class);
        conversionValuesMapping.setXPath("orm:conversion-value");
        descriptor.addMapping(conversionValuesMapping);

        XMLDirectMapping defaultObjectMapping = new XMLDirectMapping();
        defaultObjectMapping.setAttributeName("m_defaultObjectValue");
        defaultObjectMapping.setGetMethodName("getDefaultObjectValue");
        defaultObjectMapping.setSetMethodName("setDefaultObjectValue");
        defaultObjectMapping.setXPath("orm:default-object-value");
        descriptor.addMapping(defaultObjectMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: one-to-many
     */
    protected ClassDescriptor buildOneToManyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OneToManyAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getOrderByMapping());
        descriptor.addMapping(getOrderColumnMapping());
        descriptor.addMapping(getMapKeyMapping());
        descriptor.addMapping(getMapKeyClassMapping());
        descriptor.addMapping(getMapKeyTemporalMapping());
        descriptor.addMapping(getMapKeyEnumeratedMapping());
        descriptor.addMapping(getMapKeyAttributeOverrideMapping());
        descriptor.addMapping(getMapKeyConvertMapping());
        descriptor.addMapping(getMapKeyAssociationOverrideMapping());
        descriptor.addMapping(getMapKeyColumnMapping());
        descriptor.addMapping(getMapKeyJoinColumnMapping());
        descriptor.addMapping(getMapKeyForeignKeyMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getJoinTableMapping());
        descriptor.addMapping(getJoinColumnMapping());
        descriptor.addMapping(getForeignKeyMapping());
        descriptor.addMapping(getJoinFieldMapping());
        descriptor.addMapping(getCascadeMapping());
        descriptor.addMapping(getCascadeOnDeleteMapping());
        descriptor.addMapping(getPrivateOwnedMapping());
        descriptor.addMapping(getJoinFetchMapping());
        descriptor.addMapping(getBatchFetchMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getNonCacheableMapping());
        descriptor.addMapping(getDeleteAllMapping());
        descriptor.addMapping(getPartitioningMapping());
        descriptor.addMapping(getReplicationPartitioningMapping());
        descriptor.addMapping(getRoundRobinPartitioningMapping());
        descriptor.addMapping(getPinnedPartitioningMapping());
        descriptor.addMapping(getRangePartitioningMapping());
        descriptor.addMapping(getValuePartitioningMapping());
        descriptor.addMapping(getHashPartitioningMapping());
        descriptor.addMapping(getUnionPartitioningMapping());
        descriptor.addMapping(getPartitionedMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getTargetEntityAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getMappedByAttributeMapping());
        descriptor.addMapping(getOrphanRemovalAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: one-to-one
     */
    protected ClassDescriptor buildOneToOneDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OneToOneAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getPrimaryKeyJoinColumnMapping());
        descriptor.addMapping(getPrimaryKeyForeignKeyMapping());
        descriptor.addMapping(getJoinColumnMapping());
        descriptor.addMapping(getForeignKeyMapping());
        descriptor.addMapping(getJoinTableMapping());
        descriptor.addMapping(getJoinFieldMapping());
        descriptor.addMapping(getCascadeMapping());
        descriptor.addMapping(getCascadeOnDeleteMapping());
        descriptor.addMapping(getPrivateOwnedMapping());
        descriptor.addMapping(getJoinFetchMapping());
        descriptor.addMapping(getBatchFetchMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getNonCacheableMapping());
        descriptor.addMapping(getPartitioningMapping());
        descriptor.addMapping(getReplicationPartitioningMapping());
        descriptor.addMapping(getRoundRobinPartitioningMapping());
        descriptor.addMapping(getPinnedPartitioningMapping());
        descriptor.addMapping(getRangePartitioningMapping());
        descriptor.addMapping(getValuePartitioningMapping());
        descriptor.addMapping(getHashPartitioningMapping());
        descriptor.addMapping(getUnionPartitioningMapping());
        descriptor.addMapping(getPartitionedMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getTargetEntityAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getOptionalAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getMappedByAttributeMapping());
        descriptor.addMapping(getOrphanRemovalAttributeMapping());
        descriptor.addMapping(getMapsIdAttributeMapping());
        descriptor.addMapping(getIdAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: optimistic-locking
     */
    protected ClassDescriptor buildOptimisticLockingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OptimisticLockingMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        XMLCompositeCollectionMapping selectedColumnsMapping = new XMLCompositeCollectionMapping();
        selectedColumnsMapping.setAttributeName("m_selectedColumns");
        selectedColumnsMapping.setGetMethodName("getSelectedColumns");
        selectedColumnsMapping.setSetMethodName("setSelectedColumns");
        selectedColumnsMapping.setReferenceClass(ColumnMetadata.class);
        selectedColumnsMapping.setXPath("orm:selected-column");
        descriptor.addMapping(selectedColumnsMapping);

        // Attribute mappings.
        descriptor.addMapping(getTypeAttributeMapping());

        XMLDirectMapping cascadeMapping = new XMLDirectMapping();
        cascadeMapping.setAttributeName("m_cascade");
        cascadeMapping.setGetMethodName("getCascade");
        cascadeMapping.setSetMethodName("setCascade");
        cascadeMapping.setXPath("@cascade");
        descriptor.addMapping(cascadeMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: order-by
     */
    protected ClassDescriptor buildOrderByDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OrderByMetadata.class);
        descriptor.addMapping(getValueMapping());
        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: order-column
     */
    protected ClassDescriptor buildOrderColumnDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OrderColumnMetadata.class);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getNullableAttributeMapping());
        descriptor.addMapping(getInsertableAttributeMapping());
        descriptor.addMapping(getUpdatableAttributeMapping());
        descriptor.addMapping(getColumnDefinitionAttributeMapping());

        XMLDirectMapping correctionTypeMapping = new XMLDirectMapping();
        correctionTypeMapping.setAttributeName("m_correctionType");
        correctionTypeMapping.setGetMethodName("getCorrectionType");
        correctionTypeMapping.setSetMethodName("setCorrectionType");
        correctionTypeMapping.setXPath("@correction-type");
        descriptor.addMapping(correctionTypeMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: partitioning
     */
    protected ClassDescriptor buildPartitioningDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PartitioningMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getClassAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: persistence-unit-defaults
     */
    protected ClassDescriptor buildPersistenceUnitDefaultsDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLPersistenceUnitDefaults.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getSchemaMapping());
        descriptor.addMapping(getCatalogMapping());

        XMLDirectMapping delimitedIdentifiersMapping = new XMLDirectMapping();
        delimitedIdentifiersMapping.setAttributeName("m_delimitedIdentifiers");
        delimitedIdentifiersMapping.setGetMethodName("getDelimitedIdentifiers");
        delimitedIdentifiersMapping.setSetMethodName("setDelimitedIdentifiers");
        delimitedIdentifiersMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy delimitedIdentifiersPolicy = new IsSetNullPolicy("isDelimitedIdentifiers");
        delimitedIdentifiersPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        delimitedIdentifiersMapping.setNullPolicy(delimitedIdentifiersPolicy);
        delimitedIdentifiersMapping.setXPath("orm:delimited-identifiers");
        descriptor.addMapping(delimitedIdentifiersMapping);

        descriptor.addMapping(getAccessMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getCascadePersistMapping());
        descriptor.addMapping(getTenantDiscriminatorColumnsMapping());
        descriptor.addMapping(getEntityListenersMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: persistence-unit-metadata
     */
    protected ClassDescriptor buildPersistenceUnitMetadataDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLPersistenceUnitMetadata.class);

        XMLDirectMapping xmlMappingMetadataCompleteMapping = new XMLDirectMapping();
        xmlMappingMetadataCompleteMapping.setAttributeName("m_xmlMappingMetadataComplete");
        xmlMappingMetadataCompleteMapping.setGetMethodName("getXMLMappingMetadataComplete");
        xmlMappingMetadataCompleteMapping.setSetMethodName("setXMLMappingMetadataComplete");
        xmlMappingMetadataCompleteMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy xmlMappingMetadataCompletePolicy = new IsSetNullPolicy("isXMLMappingMetadataComplete");
        xmlMappingMetadataCompletePolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        xmlMappingMetadataCompleteMapping.setNullPolicy(xmlMappingMetadataCompletePolicy);
        xmlMappingMetadataCompleteMapping.setXPath("orm:xml-mapping-metadata-complete");
        descriptor.addMapping(xmlMappingMetadataCompleteMapping);

        XMLDirectMapping excludeDefaultMappingsMapping = new XMLDirectMapping();
        excludeDefaultMappingsMapping.setAttributeName("m_excludeDefaultMappings");
        excludeDefaultMappingsMapping.setGetMethodName("getExcludeDefaultMappings");
        excludeDefaultMappingsMapping.setSetMethodName("setExcludeDefaultMappings");
        excludeDefaultMappingsMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy excludeDefaultMappingsPolicy = new IsSetNullPolicy("excludeDefaultMappings");
        excludeDefaultMappingsPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        excludeDefaultMappingsMapping.setNullPolicy(excludeDefaultMappingsPolicy);
        excludeDefaultMappingsMapping.setXPath("orm:exclude-default-mappings");
        descriptor.addMapping(excludeDefaultMappingsMapping);

        XMLCompositeObjectMapping persistenceUnitDefaultsMapping = new XMLCompositeObjectMapping();
        persistenceUnitDefaultsMapping.setAttributeName("m_persistenceUnitDefaults");
        persistenceUnitDefaultsMapping.setGetMethodName("getPersistenceUnitDefaults");
        persistenceUnitDefaultsMapping.setSetMethodName("setPersistenceUnitDefaults");
        persistenceUnitDefaultsMapping.setReferenceClass(XMLPersistenceUnitDefaults.class);
        persistenceUnitDefaultsMapping.setXPath("orm:persistence-unit-defaults");
        descriptor.addMapping(persistenceUnitDefaultsMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: pinned-partitioning
     */
    protected ClassDescriptor buildPinnedPartitioningDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PinnedPartitioningMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getConnectionPoolAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: plsql-parameter
     */
    protected ClassDescriptor buildPLSQLParameterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PLSQLParameterMetadata.class);

        // Attribute mappings.
        descriptor.addMapping(getDirectionAttributeMapping());
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getQueryParameterAttributeMapping());
        descriptor.addMapping(getOptionalAttributeMapping());
        descriptor.addMapping(getDatabaseTypeAttributeMapping());
        descriptor.addMapping(getLengthAttributeMapping());
        descriptor.addMapping(getPrecisionAttributeMapping());
        descriptor.addMapping(getScaleAttributeMapping());

        return descriptor;
    }
    /**
     * INTERNAL:
     * XSD: oracle-object
     */
    protected ClassDescriptor buildOracleObjectTypeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OracleObjectTypeMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getFieldsMapping());

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getJavaTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: oracle-array
     */
    protected ClassDescriptor buildOracleArrayTypeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OracleArrayTypeMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getJavaTypeAttributeMapping());

        descriptor.addMapping(getNestedTypeMapping());

        return descriptor;
    }


    /**
     * INTERNAL:
     * XSD: plsql-record
     */
    protected ClassDescriptor buildPLSQLRecordDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PLSQLRecordMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getFieldsMapping());

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getCompatibleTypeAttributeMapping());
        descriptor.addMapping(getJavaTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: plsql-table
     */
    protected ClassDescriptor buildPLSQLTableDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PLSQLTableMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getCompatibleTypeAttributeMapping());
        descriptor.addMapping(getJavaTypeAttributeMapping());

        descriptor.addMapping(getNestedTypeMapping());

        XMLDirectMapping isNestedTableMapping = new XMLDirectMapping();
        isNestedTableMapping.setAttributeName("isNestedTable");
        isNestedTableMapping.setGetMethodName("getNestedTable");
        isNestedTableMapping.setSetMethodName("setNestedTable");
        isNestedTableMapping.setXPath("@nested-table");
        descriptor.addMapping(isNestedTableMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: primary-key
     */
    protected ClassDescriptor buildPrimaryKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PrimaryKeyMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        XMLCompositeCollectionMapping columnsMapping = new XMLCompositeCollectionMapping();
        columnsMapping.setAttributeName("m_columns");
        columnsMapping.setReferenceClass(ColumnMetadata.class);
        columnsMapping.setGetMethodName("getColumns");
        columnsMapping.setSetMethodName("setColumns");
        columnsMapping.setXPath("orm:column");
        descriptor.addMapping(columnsMapping);

        // Attribute mappings.
        XMLDirectMapping validationMapping = new XMLDirectMapping();
        validationMapping.setAttributeName("m_validation");
        validationMapping.setGetMethodName("getValidation");
        validationMapping.setSetMethodName("setValidation");
        validationMapping.setXPath("@validation");
        descriptor.addMapping(validationMapping);

        XMLDirectMapping cacheKeyMapping = new XMLDirectMapping();
        cacheKeyMapping.setAttributeName("m_cacheKeyType");
        cacheKeyMapping.setGetMethodName("getCacheKeyType");
        cacheKeyMapping.setSetMethodName("setCacheKeyType");
        cacheKeyMapping.setXPath("@cache-key-type");
        descriptor.addMapping(cacheKeyMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: primary-key-foreign-key
     */
    protected ClassDescriptor buildPrimaryKeyForeignKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PrimaryKeyForeignKeyMetadata.class);

        // Element mappings - must remain in order of definition in XML.

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getForeignKeyDefinitionAttributeMapping());
        descriptor.addMapping(getConstraintModeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: primary-key-join-column
     */
    protected ClassDescriptor buildPrimaryKeyJoinColumnDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PrimaryKeyJoinColumnMetadata.class);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getReferencedColumnNameMapping());
        descriptor.addMapping(getColumnDefinitionAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: property
     */
    protected ClassDescriptor buildPropertyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PropertyMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getValueAttributeMapping());

        XMLDirectMapping valueTypeMapping = new XMLDirectMapping();
        valueTypeMapping.setAttributeName("m_valueTypeName");
        valueTypeMapping.setGetMethodName("getValueTypeName");
        valueTypeMapping.setSetMethodName("setValueTypeName");
        valueTypeMapping.setXPath("@value-type");
        descriptor.addMapping(valueTypeMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: query-hint
     */
    protected ClassDescriptor buildQueryHintDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(QueryHintMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getValueAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: range-partition
     */
    protected ClassDescriptor buildRangePartitionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RangePartitionMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        // none currently ...

        // Attribute mappings
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("startValue");
        mapping.setGetMethodName("getStartValue");
        mapping.setSetMethodName("setStartValue");
        mapping.setXPath("@start-value");
        descriptor.addMapping(mapping);

        mapping = new XMLDirectMapping();
        mapping.setAttributeName("endValue");
        mapping.setGetMethodName("getEndValue");
        mapping.setSetMethodName("setEndValue");
        mapping.setXPath("@end-value");
        descriptor.addMapping(mapping);

        descriptor.addMapping(getConnectionPoolAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: range-partitioning
     */
    protected ClassDescriptor buildRangePartitioningDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RangePartitioningMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getPartitionColumnMapping());
        descriptor.addMapping(getRangePartitionMapping());

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getUnionUnpartitionableQueriesAttributeMapping());
        descriptor.addMapping(getPartitionValueTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: read-transformer
     */
    protected ClassDescriptor buildReadTransformerDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReadTransformerMetadata.class);

        descriptor.addMapping(getTransformerClassAttributeMapping());
        descriptor.addMapping(getMethodAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: replication-partitioning
     */
    protected ClassDescriptor buildReplicationPartitioningDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReplicationPartitioningMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getConnectionPoolsMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: return-insert
     */
    protected ClassDescriptor buildReturnInsertDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReturnInsertMetadata.class);

        XMLDirectMapping returnOnlyMapping = new XMLDirectMapping();
        returnOnlyMapping.setAttributeName("m_returnOnly");
        returnOnlyMapping.setGetMethodName("getReturnOnly");
        returnOnlyMapping.setSetMethodName("setReturnOnly");
        returnOnlyMapping.setXPath("@return-only");
        descriptor.addMapping(returnOnlyMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: round-robin-partitioning
     */
    protected ClassDescriptor buildRoundRobinPartitioningDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RoundRobinPartitioningMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getReplicateWritesMapping());
        descriptor.addMapping(getConnectionPoolsMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: table
     */
    protected ClassDescriptor buildSecondaryTableDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SecondaryTableMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getPrimaryKeyJoinColumnMapping());
        descriptor.addMapping(getPrimaryKeyForeignKeyMapping());
        descriptor.addMapping(getUniqueConstraintMapping());
        descriptor.addMapping(getIndexesMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getCatalogAttributeMapping());
        descriptor.addMapping(getSchemaAttributeMapping());
        descriptor.addMapping(getCreationSuffixAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: sequence-generator
     */
    protected ClassDescriptor buildSequenceGeneratorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SequenceGeneratorMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());

        XMLDirectMapping sequenceNameMapping = new XMLDirectMapping();
        sequenceNameMapping.setAttributeName("m_sequenceName");
        sequenceNameMapping.setGetMethodName("getSequenceName");
        sequenceNameMapping.setSetMethodName("setSequenceName");
        sequenceNameMapping.setXPath("@sequence-name");
        descriptor.addMapping(sequenceNameMapping);

        descriptor.addMapping(getCatalogAttributeMapping());
        descriptor.addMapping(getSchemaAttributeMapping());
        descriptor.addMapping(getInitialValueAttributeMapping());
        descriptor.addMapping(getAllocationSizeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: SerializedObjectPolicy
     */
    protected ClassDescriptor buildSerializedObjectPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SerializedObjectPolicyMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getColumnMapping());

        // Attribute mappings.
        descriptor.addMapping(getClassAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: sql-result-set-mapping
     */
    protected ClassDescriptor buildSqlResultSetMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SQLResultSetMappingMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getEntityResultMapping());
        descriptor.addMapping(getConstructorResultMapping());
        descriptor.addMapping(getColumnResultMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: procedure-parameter
     */
    protected ClassDescriptor buildStoredProcedureParameterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureParameterMetadata.class);

        // Element mappings - must remain in order of definition in XML.

        // Attribute mappings.
        descriptor.addMapping(getDirectionAttributeMapping());
        descriptor.addMapping(getModeAttributeMapping());
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getQueryParameterAttributeMapping());
        descriptor.addMapping(getOptionalAttributeMapping());
        descriptor.addMapping(getTypeNameAttributeMapping());
        descriptor.addMapping(getClassTypeAttributeMapping());

        XMLDirectMapping jdbcTypeMapping = new XMLDirectMapping();
        jdbcTypeMapping.setAttributeName("m_jdbcType");
        jdbcTypeMapping.setGetMethodName("getJdbcType");
        jdbcTypeMapping.setSetMethodName("setJdbcType");
        jdbcTypeMapping.setXPath("@jdbc-type");
        descriptor.addMapping(jdbcTypeMapping);

        XMLDirectMapping jdbcTypeNameMapping = new XMLDirectMapping();
        jdbcTypeNameMapping.setAttributeName("m_jdbcTypeName");
        jdbcTypeNameMapping.setGetMethodName("getJdbcTypeName");
        jdbcTypeNameMapping.setSetMethodName("setJdbcTypeName");
        jdbcTypeNameMapping.setXPath("@jdbc-type-name");
        descriptor.addMapping(jdbcTypeNameMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: struct-converter
     */
    protected ClassDescriptor buildStructConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StructConverterMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());

        XMLDirectMapping converterMapping = new XMLDirectMapping();
        converterMapping.setAttributeName("m_converter");
        converterMapping.setGetMethodName("getConverter");
        converterMapping.setSetMethodName("setConverter");
        converterMapping.setXPath("@converter");
        descriptor.addMapping(converterMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: table
     */
    protected ClassDescriptor buildTableDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TableMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getCommentMapping());
        descriptor.addMapping(getUniqueConstraintMapping());
        descriptor.addMapping(getIndexesMapping());
        descriptor.addMapping(getCheckConstraintMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getCatalogAttributeMapping());
        descriptor.addMapping(getSchemaAttributeMapping());
        descriptor.addMapping(getCreationSuffixAttributeMapping());
        descriptor.addMapping(getOptionsAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: struct
     */
    protected ClassDescriptor buildStructDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StructMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        XMLCompositeDirectCollectionMapping fieldsMapping = new XMLCompositeDirectCollectionMapping();
        fieldsMapping.setAttributeName("m_fields");
        fieldsMapping.setGetMethodName("getFields");
        fieldsMapping.setSetMethodName("setFields");
        fieldsMapping.setXPath("orm:field");
        descriptor.addMapping(fieldsMapping);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: table-generator
     */
    protected ClassDescriptor buildTableGeneratorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TableGeneratorMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getUniqueConstraintMapping());
        descriptor.addMapping(getIndexesMapping());

        // Attribute mappings.
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_generatorName");
        nameMapping.setGetMethodName("getGeneratorName");
        nameMapping.setSetMethodName("setGeneratorName");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping tableMapping = new XMLDirectMapping();
        tableMapping.setAttributeName("m_name");
        tableMapping.setGetMethodName("getName");
        tableMapping.setSetMethodName("setName");
        tableMapping.setXPath("@table");
        descriptor.addMapping(tableMapping);

        descriptor.addMapping(getCatalogAttributeMapping());
        descriptor.addMapping(getSchemaAttributeMapping());
        descriptor.addMapping(getCreationSuffixAttributeMapping());

        XMLDirectMapping pkColumnNameMapping = new XMLDirectMapping();
        pkColumnNameMapping.setAttributeName("m_pkColumnName");
        pkColumnNameMapping.setGetMethodName("getPkColumnName");
        pkColumnNameMapping.setSetMethodName("setPkColumnName");
        pkColumnNameMapping.setXPath("@pk-column-name");
        descriptor.addMapping(pkColumnNameMapping);

        XMLDirectMapping valueColumnNameMapping = new XMLDirectMapping();
        valueColumnNameMapping.setAttributeName("m_valueColumnName");
        valueColumnNameMapping.setGetMethodName("getValueColumnName");
        valueColumnNameMapping.setSetMethodName("setValueColumnName");
        valueColumnNameMapping.setXPath("@value-column-name");
        descriptor.addMapping(valueColumnNameMapping);

        XMLDirectMapping pkColumnValueMapping = new XMLDirectMapping();
        pkColumnValueMapping.setAttributeName("m_pkColumnValue");
        pkColumnValueMapping.setGetMethodName("getPkColumnValue");
        pkColumnValueMapping.setSetMethodName("setPkColumnValue");
        pkColumnValueMapping.setXPath("@pk-column-value");
        descriptor.addMapping(pkColumnValueMapping);

        descriptor.addMapping(getInitialValueAttributeMapping());
        descriptor.addMapping(getAllocationSizeAttributeMapping());
        descriptor.addMapping(getOptionsAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: temporal
     */
    protected ClassDescriptor buildTemporalDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TemporalMetadata.class);

        XMLDirectMapping temporalMapping = new XMLDirectMapping();
        temporalMapping.setAttributeName("m_temporalType");
        temporalMapping.setGetMethodName("getTemporalType");
        temporalMapping.setSetMethodName("setTemporalType");
        temporalMapping.setXPath("text()");
        descriptor.addMapping(temporalMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: tenant-discriminator-column
     */
    protected ClassDescriptor buildTenantDiscriminatorColumnDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TenantDiscriminatorColumnMetadata.class);

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getContextPropertyAttributeMapping());
        descriptor.addMapping(getDiscriminatorTypeAttributeMapping());
        descriptor.addMapping(getColumnDefinitionAttributeMapping());
        descriptor.addMapping(getTableAttributeMapping());
        descriptor.addMapping(getLengthAttributeMapping());
        descriptor.addMapping(getPrimaryKeyAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: tenant-table-discriminator
     */
    protected ClassDescriptor buildTenantTableDiscriminatorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TenantTableDiscriminatorMetadata.class);

        // Attribute mappings
        descriptor.addMapping(getContextPropertyAttributeMapping());
        descriptor.addMapping(getTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: time-of-day
     */
    protected ClassDescriptor buildTimeOfDayDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TimeOfDayMetadata.class);

        XMLDirectMapping hourMapping = new XMLDirectMapping();
        hourMapping.setAttributeName("m_hour");
        hourMapping.setGetMethodName("getHour");
        hourMapping.setSetMethodName("setHour");
        hourMapping.setXPath("@hour");
        descriptor.addMapping(hourMapping);

        XMLDirectMapping minuteMapping = new XMLDirectMapping();
        minuteMapping.setAttributeName("m_minute");
        minuteMapping.setGetMethodName("getMinute");
        minuteMapping.setSetMethodName("setMinute");
        minuteMapping.setXPath("@minute");
        descriptor.addMapping(minuteMapping);

        XMLDirectMapping secondMapping = new XMLDirectMapping();
        secondMapping.setAttributeName("m_second");
        secondMapping.setGetMethodName("getSecond");
        secondMapping.setSetMethodName("setSecond");
        secondMapping.setXPath("@second");
        descriptor.addMapping(secondMapping);

        XMLDirectMapping millisecondMapping = new XMLDirectMapping();
        millisecondMapping.setAttributeName("m_millisecond");
        millisecondMapping.setGetMethodName("getMillisecond");
        millisecondMapping.setSetMethodName("setMillisecond");
        millisecondMapping.setXPath("@millisecond");
        descriptor.addMapping(millisecondMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: transformation
     */
    protected ClassDescriptor buildTransformationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TransformationAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        XMLCompositeObjectMapping readTransformerMapping = new XMLCompositeObjectMapping();
        readTransformerMapping.setAttributeName("m_readTransformer");
        readTransformerMapping.setGetMethodName("getReadTransformer");
        readTransformerMapping.setSetMethodName("setReadTransformer");
        readTransformerMapping.setReferenceClass(ReadTransformerMetadata.class);
        readTransformerMapping.setXPath("orm:read-transformer");
        descriptor.addMapping(readTransformerMapping);

        XMLCompositeCollectionMapping writeTransformersMapping = new XMLCompositeCollectionMapping();
        writeTransformersMapping.setAttributeName("m_writeTransformers");
        writeTransformersMapping.setGetMethodName("getWriteTransformers");
        writeTransformersMapping.setSetMethodName("setWriteTransformers");
        writeTransformersMapping.setReferenceClass(WriteTransformerMetadata.class);
        writeTransformersMapping.setXPath("orm:write-transformer");
        descriptor.addMapping(writeTransformersMapping);

        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getOptionalAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getMutableAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: transient
     */
    protected ClassDescriptor buildTransientDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TransientAccessor.class);

        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: type-converter
     */
    protected ClassDescriptor buildTypeConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TypeConverterMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getDataTypeAttributeMapping());
        descriptor.addMapping(getObjectTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: type-converter
     */
    protected ClassDescriptor buildSerializedConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SerializedConverterMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getClassAttributeMapping());

        XMLDirectMapping packageMapping = new XMLDirectMapping();
        packageMapping.setAttributeName("m_serializerPackage");
        packageMapping.setGetMethodName("getSerializerPackage");
        packageMapping.setSetMethodName("setSerializerPackage");
        packageMapping.setXPath("@serializer-package");
        descriptor.addMapping(packageMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: union-partitioning
     */
    protected ClassDescriptor buildUnionPartitioningDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(UnionPartitioningMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getReplicateWritesMapping());
        descriptor.addMapping(getConnectionPoolsMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: unique-constraint
     */
    protected ClassDescriptor buildUniqueConstraintDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(UniqueConstraintMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getColumnNamesMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getOptionsAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: check-constraint
     */
    protected ClassDescriptor buildCheckConstraintDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CheckConstraintMetadata.class);

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getConstraintAttributeMapping());
        descriptor.addMapping(getOptionsAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: uuid-generator
     */
    protected ClassDescriptor buildUuidGeneratorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(UuidGeneratorMetadata.class);

        descriptor.addMapping(getNameAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: value-partition
     */
    protected ClassDescriptor buildValuePartitionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ValuePartitionMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        // none currently ...

        // Attribute mappings
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("value");
        mapping.setGetMethodName("getValue");
        mapping.setSetMethodName("setValue");
        mapping.setXPath("@value");
        descriptor.addMapping(mapping);

        descriptor.addMapping(getConnectionPoolAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: value-partitioning
     */
    protected ClassDescriptor buildValuePartitioningDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ValuePartitioningMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getPartitionColumnMapping());
        descriptor.addMapping(getValuePartitionMapping());

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getUnionUnpartitionableQueriesAttributeMapping());
        descriptor.addMapping(getDefaultConnectionPoolAttributeMapping());
        descriptor.addMapping(getPartitionValueTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: variable-one-to-one
     */
    protected ClassDescriptor buildVariableOneToOneDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(VariableOneToOneAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getCascadeMapping());
        descriptor.addMapping(getDiscriminatorColumnMapping());
        descriptor.addMapping(getDiscriminatorClassMapping());
        descriptor.addMapping(getJoinColumnMapping());
        descriptor.addMapping(getPrivateOwnedMapping());
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());
        descriptor.addMapping(getNonCacheableMapping());
        descriptor.addMapping(getPartitioningMapping());
        descriptor.addMapping(getReplicationPartitioningMapping());
        descriptor.addMapping(getRoundRobinPartitioningMapping());
        descriptor.addMapping(getPinnedPartitioningMapping());
        descriptor.addMapping(getRangePartitioningMapping());
        descriptor.addMapping(getValuePartitioningMapping());
        descriptor.addMapping(getHashPartitioningMapping());
        descriptor.addMapping(getUnionPartitioningMapping());
        descriptor.addMapping(getPartitionedMapping());

        // Attribute mappings.
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getTargetInterfaceAttributeMapping());
        descriptor.addMapping(getFetchAttributeMapping());
        descriptor.addMapping(getOptionalAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getOrphanRemovalAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: version
     */
    protected ClassDescriptor buildVersionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(VersionAccessor.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getColumnMapping());
        descriptor.addMapping(getTemporalMapping());
        descriptor.addMapping(getConvertMapping());
        addConverterMappings(descriptor);
        descriptor.addMapping(getPropertyMapping());
        descriptor.addMapping(getAccessMethodsMapping());

        // Attribute mappings
        descriptor.addMapping(getNameAttributeMapping());
        descriptor.addMapping(getAccessAttributeMapping());
        descriptor.addMapping(getMutableAttributeMapping());
        descriptor.addMapping(getAttributeTypeAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     * XSD: write-transformer
     */
    protected ClassDescriptor buildWriteTransformerDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(WriteTransformerMetadata.class);

        // Element mappings - must remain in order of definition in XML.
        descriptor.addMapping(getColumnMapping());

        // Attribute mappings.
        descriptor.addMapping(getTransformerClassAttributeMapping());
        descriptor.addMapping(getMethodAttributeMapping());

        return descriptor;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getAccessAttributeMapping() {
        XMLDirectMapping accessMapping = new XMLDirectMapping();
        accessMapping.setAttributeName("m_access");
        accessMapping.setGetMethodName("getAccess");
        accessMapping.setSetMethodName("setAccess");
        accessMapping.setXPath("@access");
        return accessMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getAccessMapping() {
        XMLDirectMapping accessMapping = new XMLDirectMapping();
        accessMapping.setAttributeName("m_access");
        accessMapping.setGetMethodName("getAccess");
        accessMapping.setSetMethodName("setAccess");
        accessMapping.setXPath("orm:access/text()");
        return accessMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getAccessMethodsMapping() {
        XMLCompositeObjectMapping accessMethodsMapping = new XMLCompositeObjectMapping();
        accessMethodsMapping.setAttributeName("m_accessMethods");
        accessMethodsMapping.setGetMethodName("getAccessMethods");
        accessMethodsMapping.setSetMethodName("setAccessMethods");
        accessMethodsMapping.setReferenceClass(AccessMethodsMetadata.class);
        accessMethodsMapping.setXPath("orm:access-methods");
        return accessMethodsMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getAdditionalCriteriaMapping() {
        XMLCompositeObjectMapping additionalCriteriaMapping = new XMLCompositeObjectMapping();
        additionalCriteriaMapping.setAttributeName("m_additionalCriteria");
        additionalCriteriaMapping.setGetMethodName("getAdditionalCriteria");
        additionalCriteriaMapping.setSetMethodName("setAdditionalCriteria");
        additionalCriteriaMapping.setReferenceClass(AdditionalCriteriaMetadata.class);
        additionalCriteriaMapping.setXPath("orm:additional-criteria");
        return additionalCriteriaMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getAllocationSizeAttributeMapping() {
        XMLDirectMapping allocationSizeMapping = new XMLDirectMapping();
        allocationSizeMapping.setAttributeName("m_allocationSize");
        allocationSizeMapping.setGetMethodName("getAllocationSize");
        allocationSizeMapping.setSetMethodName("setAllocationSize");
        allocationSizeMapping.setXPath("@allocation-size");
        return allocationSizeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getAssociationOverrideMapping() {
        XMLCompositeCollectionMapping associationOverridesMapping = new XMLCompositeCollectionMapping();
        associationOverridesMapping.setAttributeName("m_associationOverrides");
        associationOverridesMapping.setGetMethodName("getAssociationOverrides");
        associationOverridesMapping.setSetMethodName("setAssociationOverrides");
        associationOverridesMapping.setReferenceClass(AssociationOverrideMetadata.class);
        associationOverridesMapping.setXPath("orm:association-override");
        return associationOverridesMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getAttributeNameAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_attributeName");
        mapping.setGetMethodName("getAttributeName");
        mapping.setSetMethodName("setAttributeName");
        mapping.setXPath("@attribute-name");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getAttributeOverrideMapping() {
        XMLCompositeCollectionMapping attributeOverridesMapping = new XMLCompositeCollectionMapping();
        attributeOverridesMapping.setAttributeName("m_attributeOverrides");
        attributeOverridesMapping.setGetMethodName("getAttributeOverrides");
        attributeOverridesMapping.setSetMethodName("setAttributeOverrides");
        attributeOverridesMapping.setReferenceClass(AttributeOverrideMetadata.class);
        attributeOverridesMapping.setXPath("orm:attribute-override");
        return attributeOverridesMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getAttributesMapping() {
        XMLCompositeObjectMapping attributesMapping = new XMLCompositeObjectMapping();
        attributesMapping.setAttributeName("m_attributes");
        attributesMapping.setGetMethodName("getAttributes");
        attributesMapping.setSetMethodName("setAttributes");
        attributesMapping.setReferenceClass(XMLAttributes.class);
        attributesMapping.setXPath("orm:attributes");
        return attributesMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getAttributeTypeAttributeMapping() {
        XMLDirectMapping attributeTypeMapping = new XMLDirectMapping();
        attributeTypeMapping.setAttributeName("m_attributeType");
        attributeTypeMapping.setGetMethodName("getAttributeType");
        attributeTypeMapping.setSetMethodName("setAttributeType");
        attributeTypeMapping.setXPath("@attribute-type");
        return attributeTypeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getAutoApplyAttributeMapping() {
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_autoApply");
        nameMapping.setGetMethodName("getAutoApply");
        nameMapping.setSetMethodName("setAutoApply");
        nameMapping.setXPath("@auto-apply");
        return nameMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getBatchFetchMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_batchFetch");
        mapping.setGetMethodName("getBatchFetch");
        mapping.setSetMethodName("setBatchFetch");
        mapping.setReferenceClass(BatchFetchMetadata.class);
        mapping.setXPath("orm:batch-fetch");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCacheableAttributeMapping() {
        XMLDirectMapping cacheableMapping = new XMLDirectMapping();
        cacheableMapping.setAttributeName("m_cacheable");
        cacheableMapping.setGetMethodName("getCacheable");
        cacheableMapping.setSetMethodName("setCacheable");
        cacheableMapping.setXPath("@cacheable");
        return cacheableMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getCacheInterceptorMapping() {
        XMLCompositeObjectMapping cacheMapping = new XMLCompositeObjectMapping();
        cacheMapping.setAttributeName("m_cacheInterceptor");
        cacheMapping.setGetMethodName("getCacheInterceptor");
        cacheMapping.setSetMethodName("setCacheInterceptor");
        cacheMapping.setReferenceClass(CacheInterceptorMetadata.class);
        cacheMapping.setXPath("orm:cache-interceptor");
        return cacheMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getCacheMapping() {
        XMLCompositeObjectMapping cacheMapping = new XMLCompositeObjectMapping();
        cacheMapping.setAttributeName("m_cache");
        cacheMapping.setGetMethodName("getCache");
        cacheMapping.setSetMethodName("setCache");
        cacheMapping.setReferenceClass(CacheMetadata.class);
        cacheMapping.setXPath("orm:cache");
        return cacheMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCallByIndexAttributeMapping() {
        XMLDirectMapping callByIndexMapping = new XMLDirectMapping();
        callByIndexMapping.setAttributeName("m_callByIndex");
        callByIndexMapping.setGetMethodName("getCallByIndex");
        callByIndexMapping.setSetMethodName("setCallByIndex");
        callByIndexMapping.setXPath("@call-by-index");
        return callByIndexMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getCascadeMapping() {
        XMLCompositeObjectMapping cascadeMapping = new XMLCompositeObjectMapping();
        cascadeMapping.setAttributeName("m_cascade");
        cascadeMapping.setGetMethodName("getCascade");
        cascadeMapping.setSetMethodName("setCascade");
        cascadeMapping.setReferenceClass(CascadeMetadata.class);
        cascadeMapping.setXPath("orm:cascade");
        return cascadeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCascadeOnDeleteMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_cascadeOnDelete");
        mapping.setGetMethodName("getCascadeOnDelete");
        mapping.setSetMethodName("setCascadeOnDelete");
        // Cascade on delete is not mapped as an empty type, rather a boolean.
        mapping.setXPath("orm:cascade-on-delete/text()");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCascadePersistMapping() {
        XMLDirectMapping cascadePersistMapping = new XMLDirectMapping();
        cascadePersistMapping.setAttributeName("m_cascadePersist");
        cascadePersistMapping.setGetMethodName("getCascadePersist");
        cascadePersistMapping.setSetMethodName("setCascadePersist");
        //cascadePersistMapping.setAttributeClassification(String.class);
        cascadePersistMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy cascadePersistPolicy = new IsSetNullPolicy("isCascadePersist");
        cascadePersistPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        cascadePersistMapping.setNullPolicy(cascadePersistPolicy);
        cascadePersistMapping.setXPath("orm:cascade-persist");
        return cascadePersistMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCatalogAttributeMapping() {
        XMLDirectMapping catalogMapping = new XMLDirectMapping();
        catalogMapping.setAttributeName("m_catalog");
        catalogMapping.setGetMethodName("getCatalog");
        catalogMapping.setSetMethodName("setCatalog");
        catalogMapping.setXPath("@catalog");
        return catalogMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCatalogMapping() {
        XMLDirectMapping catalogMapping = new XMLDirectMapping();
        catalogMapping.setAttributeName("m_catalog");
        catalogMapping.setGetMethodName("getCatalog");
        catalogMapping.setSetMethodName("setCatalog");
        catalogMapping.setXPath("orm:catalog/text()");
        return catalogMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getChangeTrackingMapping() {
        XMLCompositeObjectMapping changeTrackingMapping = new XMLCompositeObjectMapping();
        changeTrackingMapping.setAttributeName("m_changeTracking");
        changeTrackingMapping.setGetMethodName("getChangeTracking");
        changeTrackingMapping.setSetMethodName("setChangeTracking");
        changeTrackingMapping.setReferenceClass(ChangeTrackingMetadata.class);
        changeTrackingMapping.setXPath("orm:change-tracking");
        return changeTrackingMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getClassAttributeMapping() {
        XMLDirectMapping classMapping = new XMLDirectMapping();
        classMapping.setAttributeName("m_className");
        classMapping.setGetMethodName("getClassName");
        classMapping.setSetMethodName("setClassName");
        classMapping.setXPath("@class");
        return classMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getClassExtractorMapping() {
        XMLDirectMapping classExtractorMapping = new XMLDirectMapping();
        classExtractorMapping.setAttributeName("m_classExtractorName");
        classExtractorMapping.setGetMethodName("getClassExtractorName");
        classExtractorMapping.setSetMethodName("setClassExtractorName");
        classExtractorMapping.setXPath("orm:class-extractor/@class");
        return classExtractorMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getClassTypeAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_typeName");
        mapping.setGetMethodName("getTypeName");
        mapping.setSetMethodName("setTypeName");
        mapping.setXPath("@class");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getCloneCopyPolicyMapping() {
        XMLCompositeObjectMapping columnMapping = new XMLCompositeObjectMapping();
        columnMapping.setAttributeName("m_cloneCopyPolicy");
        columnMapping.setGetMethodName("getCloneCopyPolicy");
        columnMapping.setSetMethodName("setCloneCopyPolicy");
        columnMapping.setReferenceClass(CloneCopyPolicyMetadata.class);
        columnMapping.setXPath("orm:clone-copy-policy");
        return columnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getCollectionTableMapping() {
        XMLCompositeObjectMapping collectionTableMapping = new XMLCompositeObjectMapping();
        collectionTableMapping.setAttributeName("m_collectionTable");
        collectionTableMapping.setGetMethodName("getCollectionTable");
        collectionTableMapping.setSetMethodName("setCollectionTable");
        collectionTableMapping.setReferenceClass(CollectionTableMetadata.class);
        collectionTableMapping.setXPath("orm:collection-table");
        return collectionTableMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getColumnAttributeMapping() {
        XMLDirectMapping columnMapping = new XMLDirectMapping();
        columnMapping.setAttributeName("m_column");
        columnMapping.setGetMethodName("getColumn");
        columnMapping.setSetMethodName("setColumn");
        columnMapping.setXPath("@column");
        return columnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getColumnDefinitionAttributeMapping() {
        XMLDirectMapping columnDefinitionMapping = new XMLDirectMapping();
        columnDefinitionMapping.setAttributeName("m_columnDefinition");
        columnDefinitionMapping.setGetMethodName("getColumnDefinition");
        columnDefinitionMapping.setSetMethodName("setColumnDefinition");
        columnDefinitionMapping.setXPath("@column-definition");
        return columnDefinitionMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getColumnListAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_columnList");
        mapping.setGetMethodName("getColumnList");
        mapping.setSetMethodName("setColumnList");
        mapping.setXPath("@column-list");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getColumnMapping() {
        XMLCompositeObjectMapping columnMapping = new XMLCompositeObjectMapping();
        columnMapping.setAttributeName("m_column");
        columnMapping.setGetMethodName("getColumn");
        columnMapping.setSetMethodName("setColumn");
        columnMapping.setReferenceClass(ColumnMetadata.class);
        columnMapping.setXPath("orm:column");
        return columnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getFieldMapping() {
        XMLCompositeObjectMapping columnMapping = new XMLCompositeObjectMapping();
        columnMapping.setAttributeName("m_field");
        columnMapping.setGetMethodName("getField");
        columnMapping.setSetMethodName("setField");
        columnMapping.setReferenceClass(FieldMetadata.class);
        columnMapping.setXPath("orm:field");
        return columnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getForeignKeyMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_foreignKey");
        mapping.setGetMethodName("getForeignKey");
        mapping.setSetMethodName("setForeignKey");
        mapping.setReferenceClass(ForeignKeyMetadata.class);
        mapping.setXPath("orm:foreign-key");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getForeignKeyDefinitionAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_foreignKeyDefinition");
        mapping.setGetMethodName("getForeignKeyDefinition");
        mapping.setSetMethodName("setForeignKeyDefinition");
        mapping.setXPath("@foreign-key-definition");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeDirectCollectionMapping getColumnNamesMapping() {
        XMLCompositeDirectCollectionMapping columnNamesMapping = new XMLCompositeDirectCollectionMapping();
        columnNamesMapping.setAttributeName("m_columnNames");
        columnNamesMapping.setGetMethodName("getColumnNames");
        columnNamesMapping.setSetMethodName("setColumnNames");
        columnNamesMapping.setXPath("orm:column-name");
        return columnNamesMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getColumnResultMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_columnResults");
        mapping.setGetMethodName("getColumnResults");
        mapping.setSetMethodName("setColumnResults");
        mapping.setReferenceClass(ColumnResultMetadata.class);
        mapping.setXPath("orm:column-result");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCompatibleTypeAttributeMapping() {
        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("compatibleType");
        typeMapping.setGetMethodName("getCompatibleType");
        typeMapping.setSetMethodName("setCompatibleType");
        typeMapping.setXPath("@compatible-type");
        return typeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCompositeMemberAttributeMapping() {
        XMLDirectMapping attributeTypeMapping = new XMLDirectMapping();
        attributeTypeMapping.setAttributeName("m_compositeMember");
        attributeTypeMapping.setGetMethodName("getCompositeMember");
        attributeTypeMapping.setSetMethodName("setCompositeMember");
        attributeTypeMapping.setXPath("@composite-member");
        return attributeTypeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getConnectionPoolAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("connectionPool");
        mapping.setGetMethodName("getConnectionPool");
        mapping.setSetMethodName("setConnectionPool");
        mapping.setXPath("@connection-pool");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeDirectCollectionMapping getConnectionPoolsMapping() {
        XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
        mapping.setAttributeName("connectionPools");
        mapping.setGetMethodName("getConnectionPools");
        mapping.setSetMethodName("setConnectionPools");
        mapping.setXPath("orm:connection-pool");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getConstraintModeAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_constraintMode");
        mapping.setGetMethodName("getConstraintMode");
        mapping.setSetMethodName("setConstraintMode");
        mapping.setXPath("@constraint-mode");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getConstructorColumnMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_columnResults");
        mapping.setGetMethodName("getColumnResults");
        mapping.setSetMethodName("setColumnResults");
        mapping.setReferenceClass(ColumnResultMetadata.class);
        mapping.setXPath("orm:column");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getConstructorResultMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_constructorResults");
        mapping.setGetMethodName("getConstructorResults");
        mapping.setSetMethodName("setConstructorResults");
        mapping.setReferenceClass(ConstructorResultMetadata.class);
        mapping.setXPath("orm:constructor-result");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getContextPropertyAttributeMapping() {
        XMLDirectMapping contextPropertyMapping = new XMLDirectMapping();
        contextPropertyMapping.setAttributeName("m_contextProperty");
        contextPropertyMapping.setGetMethodName("getContextProperty");
        contextPropertyMapping.setSetMethodName("setContextProperty");
        contextPropertyMapping.setXPath("@context-property");
        return contextPropertyMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getConverterAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_converterClassName");
        mapping.setGetMethodName("getConverterClassName");
        mapping.setSetMethodName("setConverterClassName");
        mapping.setXPath("@converter");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getConverterMapping() {
        XMLCompositeCollectionMapping convertersMapping = new XMLCompositeCollectionMapping();
        convertersMapping.setAttributeName("m_converters");
        convertersMapping.setGetMethodName("getConverters");
        convertersMapping.setSetMethodName("setConverters");
        convertersMapping.setReferenceClass(ConverterMetadata.class);
        convertersMapping.setXPath("orm:converter");
        return convertersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getConvertMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_converts");
        mapping.setGetMethodName("getConverts");
        mapping.setSetMethodName("setConverts");
        mapping.setReferenceClass(ConvertMetadata.class);
        mapping.setXPath("orm:convert");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCreationSuffixAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_creationSuffix");
        mapping.setGetMethodName("getCreationSuffix");
        mapping.setSetMethodName("setCreationSuffix");
        mapping.setXPath("@creation-suffix");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getCustomCopyPolicyMapping() {
        XMLCompositeObjectMapping columnMapping = new XMLCompositeObjectMapping();
        columnMapping.setAttributeName("m_customCopyPolicy");
        columnMapping.setGetMethodName("getCustomCopyPolicy");
        columnMapping.setSetMethodName("setCustomCopyPolicy");
        columnMapping.setReferenceClass(CustomCopyPolicyMetadata.class);
        columnMapping.setXPath("orm:copy-policy");
        return columnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getCustomizerMapping() {
        XMLDirectMapping customizerMapping = new XMLDirectMapping();
        customizerMapping.setAttributeName("m_customizerClassName");
        customizerMapping.setGetMethodName("getCustomizerClassName");
        customizerMapping.setSetMethodName("setCustomizerClassName");
        customizerMapping.setXPath("orm:customizer/@class");
        return customizerMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getDatabaseTypeAttributeMapping() {
        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("m_databaseType");
        typeMapping.setGetMethodName("getDatabaseType");
        typeMapping.setSetMethodName("setDatabaseType");
        typeMapping.setXPath("@database-type");
        return typeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getDataTypeAttributeMapping() {
        XMLDirectMapping dataTypeMapping = new XMLDirectMapping();
        dataTypeMapping.setAttributeName("m_dataTypeName");
        dataTypeMapping.setGetMethodName("getDataTypeName");
        dataTypeMapping.setSetMethodName("setDataTypeName");
        dataTypeMapping.setXPath("@data-type");
        return dataTypeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getDefaultConnectionPoolAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("defaultConnectionPool");
        mapping.setGetMethodName("getDefaultConnectionPool");
        mapping.setSetMethodName("setDefaultConnectionPool");
        mapping.setXPath("@default-connection-pool");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getDeleteAllMapping() {
        XMLDirectMapping deleteAllMapping = new XMLDirectMapping();
        deleteAllMapping.setAttributeName("m_deleteAll");
        deleteAllMapping.setGetMethodName("getDeleteAll");
        deleteAllMapping.setSetMethodName("setDeleteAll");
        deleteAllMapping.setXPath("orm:delete-all");
        deleteAllMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy deleteAllPolicy = new IsSetNullPolicy("isDeleteAll");
        deleteAllPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        deleteAllMapping.setNullPolicy(deleteAllPolicy);
        return deleteAllMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getDescriptionMapping() {
        XMLDirectMapping descriptionMapping = new XMLDirectMapping();
        descriptionMapping.setAttributeName("m_description");
        descriptionMapping.setGetMethodName("getDescription");
        descriptionMapping.setSetMethodName("setDescription");
        descriptionMapping.setXPath("orm:description/text()");
        return descriptionMapping;
    }

    protected XMLDirectMapping getCommentMapping() {
        XMLDirectMapping descriptionMapping = new XMLDirectMapping();
        descriptionMapping.setAttributeName("m_comment");
        descriptionMapping.setGetMethodName("getComment");
        descriptionMapping.setSetMethodName("setComment");
        descriptionMapping.setXPath("orm:comment/text()");
        return descriptionMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getDirectionAttributeMapping() {
        XMLDirectMapping directionMapping = new XMLDirectMapping();
        directionMapping.setAttributeName("m_direction");
        directionMapping.setGetMethodName("getDirection");
        directionMapping.setSetMethodName("setDirection");
        directionMapping.setXPath("@direction");
        return directionMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getDisableConversionAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_disableConversion");
        mapping.setGetMethodName("getDisableConversion");
        mapping.setSetMethodName("setDisableConversion");
        mapping.setXPath("@disable-conversion");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getDiscriminatorClassMapping() {
        XMLCompositeCollectionMapping discriminatorClassMapping = new XMLCompositeCollectionMapping();
        discriminatorClassMapping.setAttributeName("m_discriminatorClasses");
        discriminatorClassMapping.setGetMethodName("getDiscriminatorClasses");
        discriminatorClassMapping.setSetMethodName("setDiscriminatorClasses");
        discriminatorClassMapping.setReferenceClass(DiscriminatorClassMetadata.class);
        discriminatorClassMapping.setXPath("orm:discriminator-class");
        return discriminatorClassMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getDiscriminatorColumnMapping() {
        XMLCompositeObjectMapping discriminatorColumnMapping = new XMLCompositeObjectMapping();
        discriminatorColumnMapping.setAttributeName("m_discriminatorColumn");
        discriminatorColumnMapping.setGetMethodName("getDiscriminatorColumn");
        discriminatorColumnMapping.setSetMethodName("setDiscriminatorColumn");
        discriminatorColumnMapping.setReferenceClass(DiscriminatorColumnMetadata.class);
        discriminatorColumnMapping.setXPath("orm:discriminator-column");
        return discriminatorColumnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getDiscriminatorTypeAttributeMapping() {
        XMLDirectMapping discriminatorTypeMapping = new XMLDirectMapping();
        discriminatorTypeMapping.setAttributeName("m_discriminatorType");
        discriminatorTypeMapping.setGetMethodName("getDiscriminatorType");
        discriminatorTypeMapping.setSetMethodName("setDiscriminatorType");
        discriminatorTypeMapping.setXPath("@discriminator-type");
        return discriminatorTypeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getEntityListenersMapping() {
        XMLCompositeCollectionMapping entityListenersMapping = new XMLCompositeCollectionMapping();
        entityListenersMapping.setAttributeName("m_entityListeners");
        entityListenersMapping.setGetMethodName("getEntityListeners");
        entityListenersMapping.setSetMethodName("setEntityListeners");
        entityListenersMapping.setReferenceClass(EntityListenerMetadata.class);
        entityListenersMapping.setXPath("orm:entity-listeners/orm:entity-listener");
        return entityListenersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getEntityResultMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_entityResults");
        mapping.setGetMethodName("getEntityResults");
        mapping.setSetMethodName("setEntityResults");
        mapping.setReferenceClass(EntityResultMetadata.class);
        mapping.setXPath("orm:entity-result");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getEnumeratedMapping() {
        XMLCompositeObjectMapping enumeratedMapping = new XMLCompositeObjectMapping();
        enumeratedMapping.setAttributeName("m_enumerated");
        enumeratedMapping.setGetMethodName("getEnumerated");
        enumeratedMapping.setSetMethodName("setEnumerated");
        enumeratedMapping.setReferenceClass(EnumeratedMetadata.class);
        enumeratedMapping.setXPath("orm:enumerated");
        return enumeratedMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getExcludeDefaultListenersMapping() {
        XMLDirectMapping excludeDefaultListenersMapping = new XMLDirectMapping();
        excludeDefaultListenersMapping.setAttributeName("m_excludeDefaultListeners");
        excludeDefaultListenersMapping.setGetMethodName("getExcludeDefaultListeners");
        excludeDefaultListenersMapping.setSetMethodName("setExcludeDefaultListeners");
        excludeDefaultListenersMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy excludeDefaultListenersPolicy = new IsSetNullPolicy("excludeDefaultListeners");
        excludeDefaultListenersPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        excludeDefaultListenersMapping.setNullPolicy(excludeDefaultListenersPolicy);
        excludeDefaultListenersMapping.setXPath("orm:exclude-default-listeners");
        return excludeDefaultListenersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getExcludeDefaultMappingsAttributeMapping() {
        XMLDirectMapping excludeDefaultMappingsMapping = new XMLDirectMapping();
        excludeDefaultMappingsMapping.setAttributeName("m_excludeDefaultMappings");
        excludeDefaultMappingsMapping.setGetMethodName("getExcludeDefaultMappings");
        excludeDefaultMappingsMapping.setSetMethodName("setExcludeDefaultMappings");
        excludeDefaultMappingsMapping.setXPath("@exclude-default-mappings");
        return excludeDefaultMappingsMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getExcludeSuperclassListenersMapping() {
        XMLDirectMapping excludeSuperclassListenersMapping = new XMLDirectMapping();
        excludeSuperclassListenersMapping.setAttributeName("m_excludeSuperclassListeners");
        excludeSuperclassListenersMapping.setGetMethodName("getExcludeSuperclassListeners");
        excludeSuperclassListenersMapping.setSetMethodName("setExcludeSuperclassListeners");
        excludeSuperclassListenersMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy excludeSuperclassListenersPolicy = new IsSetNullPolicy("excludeSuperclassListeners");
        excludeSuperclassListenersPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        excludeSuperclassListenersMapping.setNullPolicy(excludeSuperclassListenersPolicy);
        excludeSuperclassListenersMapping.setXPath("orm:exclude-superclass-listeners");
        return excludeSuperclassListenersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getExistenceCheckingAttributeMapping() {
        XMLDirectMapping existenceCheckingMapping = new XMLDirectMapping();
        existenceCheckingMapping.setAttributeName("m_existenceChecking");
        existenceCheckingMapping.setGetMethodName("getExistenceChecking");
        existenceCheckingMapping.setSetMethodName("setExistenceChecking");
        existenceCheckingMapping.setXPath("@existence-checking");
        return existenceCheckingMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getFetchAttributeMapping() {
        XMLDirectMapping fetchMapping = new XMLDirectMapping();
        fetchMapping.setAttributeName("m_fetch");
        fetchMapping.setGetMethodName("getFetch");
        fetchMapping.setSetMethodName("setFetch");
        fetchMapping.setXPath("@fetch");
        return fetchMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getFetchGroupMapping() {
        XMLCompositeCollectionMapping fetchGroupMapping = new XMLCompositeCollectionMapping();
        fetchGroupMapping.setAttributeName("m_fetchGroups");
        fetchGroupMapping.setGetMethodName("getFetchGroups");
        fetchGroupMapping.setSetMethodName("setFetchGroups");
        fetchGroupMapping.setReferenceClass(FetchGroupMetadata.class);
        fetchGroupMapping.setXPath("orm:fetch-group");
        return fetchGroupMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getFieldsMapping() {
        XMLCompositeCollectionMapping fieldsMapping = new XMLCompositeCollectionMapping();
        fieldsMapping.setAttributeName("fields");
        fieldsMapping.setGetMethodName("getFields");
        fieldsMapping.setSetMethodName("setFields");
        fieldsMapping.setReferenceClass(PLSQLParameterMetadata.class);
        fieldsMapping.setXPath("orm:field");
        return fieldsMapping;
    }

    /**
     * INTERNAL:
     * NOTE: Internally we re-use the procedure name attribute to store the
     * function name.
     */
    protected XMLDirectMapping getFunctionNameAttributeMapping() {
        XMLDirectMapping procedureNameMapping = new XMLDirectMapping();
        procedureNameMapping.setAttributeName("m_procedureName");
        procedureNameMapping.setGetMethodName("getProcedureName");
        procedureNameMapping.setSetMethodName("setProcedureName");
        procedureNameMapping.setXPath("@function-name");
        return procedureNameMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getGeneratedValueMapping() {
        XMLCompositeObjectMapping generatedValueMapping = new XMLCompositeObjectMapping();
        generatedValueMapping.setAttributeName("m_generatedValue");
        generatedValueMapping.setGetMethodName("getGeneratedValue");
        generatedValueMapping.setSetMethodName("setGeneratedValue");
        generatedValueMapping.setReferenceClass(GeneratedValueMetadata.class);
        generatedValueMapping.setXPath("orm:generated-value");
        return generatedValueMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getHashPartitioningMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_hashPartitioning");
        mapping.setGetMethodName("getHashPartitioning");
        mapping.setSetMethodName("setHashPartitioning");
        mapping.setReferenceClass(HashPartitioningMetadata.class);
        mapping.setXPath("orm:hash-partitioning");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getHintMapping() {
        XMLCompositeCollectionMapping hintMapping = new XMLCompositeCollectionMapping();
        hintMapping.setAttributeName("m_hints");
        hintMapping.setGetMethodName("getHints");
        hintMapping.setSetMethodName("setHints");
        hintMapping.setReferenceClass(QueryHintMetadata.class);
        hintMapping.setXPath("orm:hint");
        return hintMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getIdAttributeMapping() {
        XMLDirectMapping mappedByMapping = new XMLDirectMapping();
        mappedByMapping.setAttributeName("m_id");
        mappedByMapping.setGetMethodName("getId");
        mappedByMapping.setSetMethodName("setId");
        mappedByMapping.setXPath("@id");
        return mappedByMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getIdClassMapping() {
        XMLDirectMapping idClassMapping = new XMLDirectMapping();
        idClassMapping.setAttributeName("m_idClassName");
        idClassMapping.setGetMethodName("getIdClassName");
        idClassMapping.setSetMethodName("setIdClassName");
        idClassMapping.setXPath("orm:id-class/@class");
        return idClassMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getIncludeAllAttributesAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_includeAllAttributes");
        mapping.setGetMethodName("getIncludeAllAttributes");
        mapping.setSetMethodName("setIncludeAllAttributes");
        mapping.setXPath("@include-all-attributes");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getIncludeCriteriaAttributeMapping() {
        XMLDirectMapping includeCriteriaMapping = new XMLDirectMapping();
        includeCriteriaMapping.setAttributeName("m_includeCriteria");
        includeCriteriaMapping.setGetMethodName("getIncludeCriteria");
        includeCriteriaMapping.setSetMethodName("setIncludeCriteria");
        includeCriteriaMapping.setXPath("@include-criteria");
        return includeCriteriaMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getIndexesMapping() {
        XMLCompositeCollectionMapping indexesMapping = new XMLCompositeCollectionMapping();
        indexesMapping.setAttributeName("m_indexes");
        indexesMapping.setGetMethodName("getIndexes");
        indexesMapping.setSetMethodName("setIndexes");
        indexesMapping.setReferenceClass(IndexMetadata.class);
        indexesMapping.setXPath("orm:index");
        return indexesMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getCacheIndexesMapping() {
        XMLCompositeCollectionMapping indexesMapping = new XMLCompositeCollectionMapping();
        indexesMapping.setAttributeName("m_cacheIndexes");
        indexesMapping.setGetMethodName("getCacheIndexes");
        indexesMapping.setSetMethodName("setCacheIndexes");
        indexesMapping.setReferenceClass(CacheIndexMetadata.class);
        indexesMapping.setXPath("orm:cache-index");
        return indexesMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getIndexMapping() {
        XMLCompositeObjectMapping indexMapping = new XMLCompositeObjectMapping();
        indexMapping.setAttributeName("m_index");
        indexMapping.setGetMethodName("getIndex");
        indexMapping.setSetMethodName("setIndex");
        indexMapping.setReferenceClass(IndexMetadata.class);
        indexMapping.setXPath("orm:index");
        return indexMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getCacheIndexMapping() {
        XMLCompositeObjectMapping indexMapping = new XMLCompositeObjectMapping();
        indexMapping.setAttributeName("m_cacheIndex");
        indexMapping.setGetMethodName("getCacheIndex");
        indexMapping.setSetMethodName("setCacheIndex");
        indexMapping.setReferenceClass(CacheIndexMetadata.class);
        indexMapping.setXPath("orm:cache-index");
        return indexMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getCheckConstraintMapping() {
        XMLCompositeCollectionMapping checkConstraintMapping = new XMLCompositeCollectionMapping();
        checkConstraintMapping.setAttributeName("m_checkConstraints");
        checkConstraintMapping.setGetMethodName("getCheckConstraints");
        checkConstraintMapping.setSetMethodName("setCheckConstraints");
        checkConstraintMapping.setReferenceClass(CheckConstraintMetadata.class);
        checkConstraintMapping.setXPath("orm:check-constraint");
        return checkConstraintMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getInitialValueAttributeMapping() {
        XMLDirectMapping initialValueMapping = new XMLDirectMapping();
        initialValueMapping.setAttributeName("m_initialValue");
        initialValueMapping.setGetMethodName("getInitialValue");
        initialValueMapping.setSetMethodName("setInitialValue");
        initialValueMapping.setXPath("@initial-value");
        return initialValueMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getInsertableAttributeMapping() {
        XMLDirectMapping insertableMapping = new XMLDirectMapping();
        insertableMapping.setAttributeName("m_insertable");
        insertableMapping.setGetMethodName("getInsertable");
        insertableMapping.setSetMethodName("setInsertable");
        insertableMapping.setXPath("@insertable");
        return insertableMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getInstantiationCopyPolicyMapping() {
        XMLCompositeObjectMapping columnMapping = new XMLCompositeObjectMapping();
        columnMapping.setAttributeName("m_instantiationCopyPolicy");
        columnMapping.setGetMethodName("getInstantiationCopyPolicy");
        columnMapping.setSetMethodName("setInstantiationCopyPolicy");
        columnMapping.setReferenceClass(InstantiationCopyPolicyMetadata.class);
        columnMapping.setXPath("orm:instantiation-copy-policy");
        return columnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getInverseForeignKeyMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_inverseForeignKey");
        mapping.setGetMethodName("getInverseForeignKey");
        mapping.setSetMethodName("setInverseForeignKey");
        mapping.setReferenceClass(ForeignKeyMetadata.class);
        mapping.setXPath("orm:inverse-foreign-key");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getInverseJoinColumnMapping() {
        XMLCompositeCollectionMapping joinColumnMapping = new XMLCompositeCollectionMapping();
        joinColumnMapping.setAttributeName("m_inverseJoinColumns");
        joinColumnMapping.setGetMethodName("getInverseJoinColumns");
        joinColumnMapping.setSetMethodName("setInverseJoinColumns");
        joinColumnMapping.setReferenceClass(JoinColumnMetadata.class);
        joinColumnMapping.setXPath("orm:inverse-join-column");
        return joinColumnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getJavaTypeAttributeMapping() {
        XMLDirectMapping javaTypeMapping = new XMLDirectMapping();
        javaTypeMapping.setAttributeName("javaType");
        javaTypeMapping.setGetMethodName("getJavaType");
        javaTypeMapping.setSetMethodName("setJavaType");
        javaTypeMapping.setXPath("@java-type");
        return javaTypeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getJoinColumnMapping() {
        XMLCompositeCollectionMapping joinColumnMapping = new XMLCompositeCollectionMapping();
        joinColumnMapping.setAttributeName("m_joinColumns");
        joinColumnMapping.setGetMethodName("getJoinColumns");
        joinColumnMapping.setSetMethodName("setJoinColumns");
        joinColumnMapping.setReferenceClass(JoinColumnMetadata.class);
        joinColumnMapping.setXPath("orm:join-column");
        return joinColumnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getJoinFieldMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_joinFields");
        mapping.setGetMethodName("getJoinFields");
        mapping.setSetMethodName("setJoinFields");
        mapping.setReferenceClass(JoinFieldMetadata.class);
        mapping.setXPath("orm:join-field");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getJoinFetchMapping() {
        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("m_joinFetch");
        joinFetchMapping.setGetMethodName("getJoinFetch");
        joinFetchMapping.setSetMethodName("setJoinFetch");
        joinFetchMapping.setXPath("orm:join-fetch/text()");
        return joinFetchMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getJoinTableMapping() {
        XMLCompositeObjectMapping joinTableMapping = new XMLCompositeObjectMapping();
        joinTableMapping.setAttributeName("m_joinTable");
        joinTableMapping.setGetMethodName("getJoinTable");
        joinTableMapping.setSetMethodName("setJoinTable");
        joinTableMapping.setReferenceClass(JoinTableMetadata.class);
        joinTableMapping.setXPath("orm:join-table");
        return joinTableMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getKeySubgraphAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_keySubgraph");
        mapping.setGetMethodName("getKeySubgraph");
        mapping.setSetMethodName("setKeySubgraph");
        mapping.setXPath("@key-subgraph");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getLengthAttributeMapping() {
        XMLDirectMapping lengthMapping = new XMLDirectMapping();
        lengthMapping.setAttributeName("m_length");
        lengthMapping.setGetMethodName("getLength");
        lengthMapping.setSetMethodName("setLength");
        lengthMapping.setXPath("@length");
        return lengthMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getLobMapping() {
        XMLCompositeObjectMapping lobMapping = new XMLCompositeObjectMapping();
        lobMapping.setAttributeName("m_lob");
        lobMapping.setGetMethodName("getLob");
        lobMapping.setSetMethodName("setLob");
        lobMapping.setReferenceClass(LobMetadata.class);
        lobMapping.setXPath("orm:lob");
        return lobMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getLockModeMapping() {
        XMLDirectMapping lockModeMapping = new XMLDirectMapping();
        lockModeMapping.setAttributeName("m_lockMode");
        lockModeMapping.setGetMethodName("getLockMode");
        lockModeMapping.setSetMethodName("setLockMode");
        lockModeMapping.setXPath("orm:lock-mode");
        return lockModeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getMapKeyAssociationOverrideMapping() {
        XMLCompositeCollectionMapping associationOverridesMapping = new XMLCompositeCollectionMapping();
        associationOverridesMapping.setAttributeName("m_mapKeyAssociationOverrides");
        associationOverridesMapping.setGetMethodName("getMapKeyAssociationOverrides");
        associationOverridesMapping.setSetMethodName("setMapKeyAssociationOverrides");
        associationOverridesMapping.setReferenceClass(AssociationOverrideMetadata.class);
        associationOverridesMapping.setXPath("orm:map-key-association-override");
        return associationOverridesMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getMapKeyAttributeOverrideMapping() {
        XMLCompositeCollectionMapping attributeOverridesMapping = new XMLCompositeCollectionMapping();
        attributeOverridesMapping.setAttributeName("m_mapKeyAttributeOverrides");
        attributeOverridesMapping.setGetMethodName("getMapKeyAttributeOverrides");
        attributeOverridesMapping.setSetMethodName("setMapKeyAttributeOverrides");
        attributeOverridesMapping.setReferenceClass(AttributeOverrideMetadata.class);
        attributeOverridesMapping.setXPath("orm:map-key-attribute-override");
        return attributeOverridesMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getMapKeyClassMapping() {
        XMLDirectMapping mapKeyClassMapping = new XMLDirectMapping();
        mapKeyClassMapping.setAttributeName("m_mapKeyClassName");
        mapKeyClassMapping.setGetMethodName("getMapKeyClassName");
        mapKeyClassMapping.setSetMethodName("setMapKeyClassName");
        mapKeyClassMapping.setXPath("orm:map-key-class/@class");
        return mapKeyClassMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getMapKeyColumnMapping() {
        XMLCompositeObjectMapping mapKeyColumnMapping = new XMLCompositeObjectMapping();
        mapKeyColumnMapping.setAttributeName("m_mapKeyColumn");
        mapKeyColumnMapping.setGetMethodName("getMapKeyColumn");
        mapKeyColumnMapping.setSetMethodName("setMapKeyColumn");
        mapKeyColumnMapping.setReferenceClass(ColumnMetadata.class);
        mapKeyColumnMapping.setXPath("orm:map-key-column");
        return mapKeyColumnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getMapKeyConvertMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_mapKeyConverts");
        mapping.setGetMethodName("getMapKeyConverts");
        mapping.setSetMethodName("setMapKeyConverts");
        mapping.setReferenceClass(ConvertMetadata.class);
        mapping.setXPath("orm:map-key-convert");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getMapKeyEnumeratedMapping() {
        XMLCompositeObjectMapping mapKeyEnumeratedMapping = new XMLCompositeObjectMapping();
        mapKeyEnumeratedMapping.setAttributeName("m_mapKeyEnumerated");
        mapKeyEnumeratedMapping.setGetMethodName("getMapKeyEnumerated");
        mapKeyEnumeratedMapping.setSetMethodName("setMapKeyEnumerated");
        mapKeyEnumeratedMapping.setReferenceClass(EnumeratedMetadata.class);
        mapKeyEnumeratedMapping.setXPath("orm:map-key-enumerated");
        return mapKeyEnumeratedMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getMapKeyForeignKeyMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_mapKeyForeignKey");
        mapping.setGetMethodName("getMapKeyForeignKey");
        mapping.setSetMethodName("setMapKeyForeignKey");
        mapping.setReferenceClass(ForeignKeyMetadata.class);
        mapping.setXPath("orm:map-key-foreign-key");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getMapKeyJoinColumnMapping() {
        XMLCompositeCollectionMapping mapKeyJoinColumnMapping = new XMLCompositeCollectionMapping();
        mapKeyJoinColumnMapping.setAttributeName("m_mapKeyJoinColumns");
        mapKeyJoinColumnMapping.setGetMethodName("getMapKeyJoinColumns");
        mapKeyJoinColumnMapping.setSetMethodName("setMapKeyJoinColumns");
        mapKeyJoinColumnMapping.setReferenceClass(JoinColumnMetadata.class);
        mapKeyJoinColumnMapping.setXPath("orm:map-key-join-column");
        return mapKeyJoinColumnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getMapKeyMapping() {
        XMLCompositeObjectMapping mapKeyMapping = new XMLCompositeObjectMapping();
        mapKeyMapping.setAttributeName("m_mapKey");
        mapKeyMapping.setGetMethodName("getMapKey");
        mapKeyMapping.setSetMethodName("setMapKey");
        mapKeyMapping.setReferenceClass(MapKeyMetadata.class);
        mapKeyMapping.setXPath("orm:map-key");
        return mapKeyMapping;
    }

    /**
     * INTERNAL
     */
    protected XMLCompositeObjectMapping getMapKeyTemporalMapping() {
        XMLCompositeObjectMapping mapKeyTemporalMapping = new XMLCompositeObjectMapping();
        mapKeyTemporalMapping.setAttributeName("m_mapKeyTemporal");
        mapKeyTemporalMapping.setGetMethodName("getMapKeyTemporal");
        mapKeyTemporalMapping.setSetMethodName("setMapKeyTemporal");
        mapKeyTemporalMapping.setReferenceClass(TemporalMetadata.class);
        mapKeyTemporalMapping.setXPath("orm:map-key-temporal");
        return mapKeyTemporalMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getMappedByAttributeMapping() {
        XMLDirectMapping mappedByMapping = new XMLDirectMapping();
        mappedByMapping.setAttributeName("m_mappedBy");
        mappedByMapping.setGetMethodName("getMappedBy");
        mappedByMapping.setSetMethodName("setMappedBy");
        mappedByMapping.setXPath("@mapped-by");
        return mappedByMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getMapsIdAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_mapsId");
        mapping.setGetMethodName("getMapsId");
        mapping.setSetMethodName("setMapsId");
        mapping.setXPath("@maps-id");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getMetadataCompleteAttributeMapping() {
        XMLDirectMapping metadataCompleteMapping = new XMLDirectMapping();
        metadataCompleteMapping.setAttributeName("m_metadataComplete");
        metadataCompleteMapping.setGetMethodName("getMetadataComplete");
        metadataCompleteMapping.setSetMethodName("setMetadataComplete");
        metadataCompleteMapping.setXPath("@metadata-complete");
        return metadataCompleteMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getMethodAttributeMapping() {
        XMLDirectMapping methodMapping = new XMLDirectMapping();
        methodMapping.setAttributeName("m_method");
        methodMapping.setGetMethodName("getMethod");
        methodMapping.setSetMethodName("setMethod");
        methodMapping.setXPath("@method");
        return methodMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getMixedConverterMapping() {
        XMLCompositeCollectionMapping convertersMapping = new XMLCompositeCollectionMapping();
        convertersMapping.setAttributeName("m_mixedConverters");
        convertersMapping.setGetMethodName("getMixedConverters");
        convertersMapping.setSetMethodName("setMixedConverters");
        convertersMapping.setReferenceClass(MixedConverterMetadata.class);
        convertersMapping.setXPath("orm:converter");
        return convertersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getModeAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_mode");
        mapping.setGetMethodName("getMode");
        mapping.setSetMethodName("setMode");
        mapping.setXPath("@mode");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getMultitenantMapping() {
        XMLCompositeObjectMapping multitenantMapping = new XMLCompositeObjectMapping();
        multitenantMapping.setAttributeName("m_multitenant");
        multitenantMapping.setGetMethodName("getMultitenant");
        multitenantMapping.setSetMethodName("setMultitenant");
        multitenantMapping.setReferenceClass(MultitenantMetadata.class);
        multitenantMapping.setXPath("orm:multitenant");
        return multitenantMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getMutableAttributeMapping() {
        XMLDirectMapping mutableMapping = new XMLDirectMapping();
        mutableMapping.setAttributeName("m_mutable");
        mutableMapping.setGetMethodName("getMutable");
        mutableMapping.setSetMethodName("setMutable");
        mutableMapping.setXPath("@mutable");
        return mutableMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getNameAttributeMapping() {
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("@name");
        return nameMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getConstraintAttributeMapping() {
        XMLDirectMapping constraintMapping = new XMLDirectMapping();
        constraintMapping.setAttributeName("m_constraint");
        constraintMapping.setGetMethodName("getConstraint");
        constraintMapping.setSetMethodName("setConstraint");
        constraintMapping.setXPath("@constraint");
        return constraintMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getOptionsAttributeMapping() {
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("m_options");
        nameMapping.setGetMethodName("getOptions");
        nameMapping.setSetMethodName("setOptions");
        nameMapping.setXPath("@options");
        return nameMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getNamedAttributeNodeMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_namedAttributeNodes");
        mapping.setGetMethodName("getNamedAttributeNodes");
        mapping.setSetMethodName("setNamedAttributeNodes");
        mapping.setReferenceClass(NamedAttributeNodeMetadata.class);
        mapping.setXPath("orm:named-attribute-node");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getNamedEntityGraphMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_namedEntityGraphs");
        mapping.setGetMethodName("getNamedEntityGraphs");
        mapping.setSetMethodName("setNamedEntityGraphs");
        mapping.setReferenceClass(NamedEntityGraphMetadata.class);
        mapping.setXPath("orm:named-entity-graph");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getNamedNativeQueryMapping() {
        XMLCompositeCollectionMapping namedNativeQueryMapping = new XMLCompositeCollectionMapping();
        namedNativeQueryMapping.setAttributeName("m_namedNativeQueries");
        namedNativeQueryMapping.setGetMethodName("getNamedNativeQueries");
        namedNativeQueryMapping.setSetMethodName("setNamedNativeQueries");
        namedNativeQueryMapping.setReferenceClass(NamedNativeQueryMetadata.class);
        namedNativeQueryMapping.setXPath("orm:named-native-query");
        return namedNativeQueryMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getNamedPLSQLStoredFunctionQueryMapping() {
        XMLCompositeCollectionMapping namedStoredFunctionQueryMapping = new XMLCompositeCollectionMapping();
        namedStoredFunctionQueryMapping.setAttributeName("m_namedPLSQLStoredFunctionQueries");
        namedStoredFunctionQueryMapping.setGetMethodName("getNamedPLSQLStoredFunctionQueries");
        namedStoredFunctionQueryMapping.setSetMethodName("setNamedPLSQLStoredFunctionQueries");
        namedStoredFunctionQueryMapping.setReferenceClass(NamedPLSQLStoredFunctionQueryMetadata.class);
        namedStoredFunctionQueryMapping.setXPath("orm:named-plsql-stored-function-query");
        return namedStoredFunctionQueryMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getNamedPLSQLStoredProcedureQueryMapping() {
        XMLCompositeCollectionMapping namedStoredFunctionQueryMapping = new XMLCompositeCollectionMapping();
        namedStoredFunctionQueryMapping.setAttributeName("m_namedPLSQLStoredProcedureQueries");
        namedStoredFunctionQueryMapping.setGetMethodName("getNamedPLSQLStoredProcedureQueries");
        namedStoredFunctionQueryMapping.setSetMethodName("setNamedPLSQLStoredProcedureQueries");
        namedStoredFunctionQueryMapping.setReferenceClass(NamedPLSQLStoredProcedureQueryMetadata.class);
        namedStoredFunctionQueryMapping.setXPath("orm:named-plsql-stored-procedure-query");
        return namedStoredFunctionQueryMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getNamedQueryMapping() {
        XMLCompositeCollectionMapping namedQueryMapping = new XMLCompositeCollectionMapping();
        namedQueryMapping.setAttributeName("m_namedQueries");
        namedQueryMapping.setGetMethodName("getNamedQueries");
        namedQueryMapping.setSetMethodName("setNamedQueries");
        namedQueryMapping.setReferenceClass(NamedQueryMetadata.class);
        namedQueryMapping.setXPath("orm:named-query");
        return namedQueryMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getNamedStoredFunctionQueryMapping() {
        XMLCompositeCollectionMapping namedStoredFunctionQueryMapping = new XMLCompositeCollectionMapping();
        namedStoredFunctionQueryMapping.setAttributeName("m_namedStoredFunctionQueries");
        namedStoredFunctionQueryMapping.setGetMethodName("getNamedStoredFunctionQueries");
        namedStoredFunctionQueryMapping.setSetMethodName("setNamedStoredFunctionQueries");
        namedStoredFunctionQueryMapping.setReferenceClass(NamedStoredFunctionQueryMetadata.class);
        namedStoredFunctionQueryMapping.setXPath("orm:named-stored-function-query");
        return namedStoredFunctionQueryMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getNamedStoredProcedureQueryMapping() {
        XMLCompositeCollectionMapping namedStoredProcedureQueryMapping = new XMLCompositeCollectionMapping();
        namedStoredProcedureQueryMapping.setAttributeName("m_namedStoredProcedureQueries");
        namedStoredProcedureQueryMapping.setGetMethodName("getNamedStoredProcedureQueries");
        namedStoredProcedureQueryMapping.setSetMethodName("setNamedStoredProcedureQueries");
        namedStoredProcedureQueryMapping.setReferenceClass(NamedStoredProcedureQueryMetadata.class);
        namedStoredProcedureQueryMapping.setXPath("orm:named-stored-procedure-query");
        return namedStoredProcedureQueryMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getNestedTypeMapping() {
        XMLDirectMapping nestedTypeMapping = new XMLDirectMapping();
        nestedTypeMapping.setAttributeName("nestedType");
        nestedTypeMapping.setGetMethodName("getNestedType");
        nestedTypeMapping.setSetMethodName("setNestedType");
        nestedTypeMapping.setXPath("@nested-type");
        return nestedTypeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getNonCacheableMapping() {
        XMLDirectMapping noncacheable = new XMLDirectMapping();
        noncacheable.setAttributeName("m_nonCacheable");
        noncacheable.setGetMethodName("getNonCacheable");
        noncacheable.setSetMethodName("setNonCacheable");
        noncacheable.setConverter(new EmptyElementConverter());
        IsSetNullPolicy cacheablePolicy = new IsSetNullPolicy("isNonCacheable");
        cacheablePolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        noncacheable.setNullPolicy(cacheablePolicy);
        noncacheable.setXPath("orm:noncacheable");
        return noncacheable;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getNoSqlMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_noSql");
        mapping.setGetMethodName("getNoSql");
        mapping.setSetMethodName("setNoSql");
        mapping.setReferenceClass(NoSqlMetadata.class);
        mapping.setXPath("orm:no-sql");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getNullableAttributeMapping() {
        XMLDirectMapping nullableMapping = new XMLDirectMapping();
        nullableMapping.setAttributeName("m_nullable");
        nullableMapping.setGetMethodName("getNullable");
        nullableMapping.setSetMethodName("setNullable");
        nullableMapping.setXPath("@nullable");
        return nullableMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getObjectTypeAttributeMapping() {
        XMLDirectMapping objectTypeMapping = new XMLDirectMapping();
        objectTypeMapping.setAttributeName("m_objectTypeName");
        objectTypeMapping.setGetMethodName("getObjectTypeName");
        objectTypeMapping.setSetMethodName("setObjectTypeName");
        objectTypeMapping.setXPath("@object-type");
        return objectTypeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getObjectTypeConverterMapping() {
        XMLCompositeCollectionMapping objectTypeConvertersMapping = new XMLCompositeCollectionMapping();
        objectTypeConvertersMapping.setAttributeName("m_objectTypeConverters");
        objectTypeConvertersMapping.setGetMethodName("getObjectTypeConverters");
        objectTypeConvertersMapping.setSetMethodName("setObjectTypeConverters");
        objectTypeConvertersMapping.setReferenceClass(ObjectTypeConverterMetadata.class);
        objectTypeConvertersMapping.setXPath("orm:object-type-converter");
        return objectTypeConvertersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getOptimisticLockingMapping() {
        XMLCompositeObjectMapping optimisticLockingMapping = new XMLCompositeObjectMapping();
        optimisticLockingMapping.setAttributeName("m_optimisticLocking");
        optimisticLockingMapping.setGetMethodName("getOptimisticLocking");
        optimisticLockingMapping.setSetMethodName("setOptimisticLocking");
        optimisticLockingMapping.setReferenceClass(OptimisticLockingMetadata.class);
        optimisticLockingMapping.setXPath("orm:optimistic-locking");
        return optimisticLockingMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getOptionalAttributeMapping() {
        XMLDirectMapping optionalMapping = new XMLDirectMapping();
        optionalMapping.setAttributeName("m_optional");
        optionalMapping.setGetMethodName("getOptional");
        optionalMapping.setSetMethodName("setOptional");
        optionalMapping.setXPath("@optional");
        return optionalMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getOracleArrayTypeMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_oracleArrayTypes");
        mapping.setGetMethodName("getOracleArrayTypes");
        mapping.setSetMethodName("setOracleArrayTypes");
        mapping.setReferenceClass(OracleArrayTypeMetadata.class);
        mapping.setXPath("orm:oracle-array");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getOracleObjectTypeMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_oracleObjectTypes");
        mapping.setGetMethodName("getOracleObjectTypes");
        mapping.setSetMethodName("setOracleObjectTypes");
        mapping.setReferenceClass(OracleObjectTypeMetadata.class);
        mapping.setXPath("orm:oracle-object");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getOrderByMapping() {
        XMLCompositeObjectMapping orderByMapping = new XMLCompositeObjectMapping();
        orderByMapping.setAttributeName("m_orderBy");
        orderByMapping.setGetMethodName("getOrderBy");
        orderByMapping.setSetMethodName("setOrderBy");
        orderByMapping.setReferenceClass(OrderByMetadata.class);
        orderByMapping.setXPath("orm:order-by");
        return orderByMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getOrderColumnMapping() {
        XMLCompositeObjectMapping orderColumnMapping = new XMLCompositeObjectMapping();
        orderColumnMapping.setAttributeName("m_orderColumn");
        orderColumnMapping.setGetMethodName("getOrderColumn");
        orderColumnMapping.setSetMethodName("setOrderColumn");
        orderColumnMapping.setReferenceClass(OrderColumnMetadata.class);
        orderColumnMapping.setXPath("orm:order-column");
        return orderColumnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getOrphanRemovalAttributeMapping() {
        XMLDirectMapping orphanRemovalMapping = new XMLDirectMapping();
        orphanRemovalMapping.setAttributeName("m_orphanRemoval");
        orphanRemovalMapping.setGetMethodName("getOrphanRemoval");
        orphanRemovalMapping.setSetMethodName("setOrphanRemoval");
        orphanRemovalMapping.setXPath("@orphan-removal");
        return orphanRemovalMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getParametersMapping() {
        XMLCompositeCollectionMapping parametersMapping = new XMLCompositeCollectionMapping();
        parametersMapping.setAttributeName("m_parameters");
        parametersMapping.setGetMethodName("getParameters");
        parametersMapping.setSetMethodName("setParameters");
        parametersMapping.setReferenceClass(StoredProcedureParameterMetadata.class);
        parametersMapping.setXPath("orm:parameter");
        return parametersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getParentClassAttributeMapping() {
        XMLDirectMapping parentClassMapping = new XMLDirectMapping();
        parentClassMapping.setAttributeName("m_parentClassName");
        parentClassMapping.setGetMethodName("getParentClassName");
        parentClassMapping.setSetMethodName("setParentClassName");
        parentClassMapping.setXPath("@parent-class");
        return parentClassMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getRangePartitionMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("partitions");
        mapping.setGetMethodName("getPartitions");
        mapping.setSetMethodName("setPartitions");
        mapping.setReferenceClass(RangePartitionMetadata.class);
        mapping.setXPath("orm:partition");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getPartitionColumnMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("partitionColumn");
        mapping.setGetMethodName("getPartitionColumn");
        mapping.setSetMethodName("setPartitionColumn");
        mapping.setReferenceClass(ColumnMetadata.class);
        mapping.setXPath("orm:partition-column");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPartitionedMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_partitioned");
        mapping.setGetMethodName("getPartitioned");
        mapping.setSetMethodName("setPartitioned");
        mapping.setXPath("orm:partitioned");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getPartitioningMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_partitioning");
        mapping.setGetMethodName("getPartitioning");
        mapping.setSetMethodName("setPartitioning");
        mapping.setReferenceClass(PartitioningMetadata.class);
        mapping.setXPath("orm:partitioning");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPartitionValueTypeAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("partitionValueTypeName");
        mapping.setGetMethodName("getPartitionValueTypeName");
        mapping.setSetMethodName("setPartitionValueTypeName");
        mapping.setXPath("@partition-value-type");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getPinnedPartitioningMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_pinnedPartitioning");
        mapping.setGetMethodName("getPinnedPartitioning");
        mapping.setSetMethodName("setPinnedPartitioning");
        mapping.setReferenceClass(PinnedPartitioningMetadata.class);
        mapping.setXPath("orm:pinned-partitioning");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getPLSQLParametersMapping() {
        XMLCompositeCollectionMapping parametersMapping = new XMLCompositeCollectionMapping();
        parametersMapping.setAttributeName("m_parameters");
        parametersMapping.setGetMethodName("getParameters");
        parametersMapping.setSetMethodName("setParameters");
        parametersMapping.setReferenceClass(PLSQLParameterMetadata.class);
        parametersMapping.setXPath("orm:parameter");
        return parametersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getPLSQLRecordMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_plsqlRecords");
        mapping.setGetMethodName("getPLSQLRecords");
        mapping.setSetMethodName("setPLSQLRecords");
        mapping.setReferenceClass(PLSQLRecordMetadata.class);
        mapping.setXPath("orm:plsql-record");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getPLSQLTableMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_plsqlTables");
        mapping.setGetMethodName("getPLSQLTables");
        mapping.setSetMethodName("setPLSQLTables");
        mapping.setReferenceClass(PLSQLTableMetadata.class);
        mapping.setXPath("orm:plsql-table");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPostLoadMapping() {
        XMLDirectMapping postLoadMapping = new XMLDirectMapping();
        postLoadMapping.setAttributeName("m_postLoad");
        postLoadMapping.setGetMethodName("getPostLoad");
        postLoadMapping.setSetMethodName("setPostLoad");
        postLoadMapping.setXPath("orm:post-load/@method-name");
        return postLoadMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPostPeristMapping() {
        XMLDirectMapping postPersistMapping = new XMLDirectMapping();
        postPersistMapping.setAttributeName("m_postPersist");
        postPersistMapping.setGetMethodName("getPostPersist");
        postPersistMapping.setSetMethodName("setPostPersist");
        postPersistMapping.setXPath("orm:post-persist/@method-name");
        return postPersistMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPostRemoveMapping() {
        XMLDirectMapping postRemoveMapping = new XMLDirectMapping();
        postRemoveMapping.setAttributeName("m_postRemove");
        postRemoveMapping.setGetMethodName("getPostRemove");
        postRemoveMapping.setSetMethodName("setPostRemove");
        postRemoveMapping.setXPath("orm:post-remove/@method-name");
        return postRemoveMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPostUpdateMapping() {
        XMLDirectMapping postUpdateMapping = new XMLDirectMapping();
        postUpdateMapping.setAttributeName("m_postUpdate");
        postUpdateMapping.setGetMethodName("getPostUpdate");
        postUpdateMapping.setSetMethodName("setPostUpdate");
        postUpdateMapping.setXPath("orm:post-update/@method-name");
        return postUpdateMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPrecisionAttributeMapping() {
        XMLDirectMapping precisionMapping = new XMLDirectMapping();
        precisionMapping.setAttributeName("m_precision");
        precisionMapping.setGetMethodName("getPrecision");
        precisionMapping.setSetMethodName("setPrecision");
        precisionMapping.setXPath("@precision");
        return precisionMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPrePeristMapping() {
        XMLDirectMapping prePersistMapping = new XMLDirectMapping();
        prePersistMapping.setAttributeName("m_prePersist");
        prePersistMapping.setGetMethodName("getPrePersist");
        prePersistMapping.setSetMethodName("setPrePersist");
        prePersistMapping.setXPath("orm:pre-persist/@method-name");
        return prePersistMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPreRemoveMapping() {
        XMLDirectMapping preRemoveMapping = new XMLDirectMapping();
        preRemoveMapping.setAttributeName("m_preRemove");
        preRemoveMapping.setGetMethodName("getPreRemove");
        preRemoveMapping.setSetMethodName("setPreRemove");
        preRemoveMapping.setXPath("orm:pre-remove/@method-name");
        return preRemoveMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPreUpdateMapping() {
        XMLDirectMapping preUpdateMapping = new XMLDirectMapping();
        preUpdateMapping.setAttributeName("m_preUpdate");
        preUpdateMapping.setGetMethodName("getPreUpdate");
        preUpdateMapping.setSetMethodName("setPreUpdate");
        preUpdateMapping.setXPath("orm:pre-update/@method-name");
        return preUpdateMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPrimaryKeyAttributeMapping() {
        XMLDirectMapping primaryKeyMapping = new XMLDirectMapping();
        primaryKeyMapping.setAttributeName("m_primaryKey");
        primaryKeyMapping.setGetMethodName("getPrimaryKey");
        primaryKeyMapping.setSetMethodName("setPrimaryKey");
        primaryKeyMapping.setXPath("@primary-key");
        return primaryKeyMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getPrimaryKeyForeignKeyMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_primaryKeyForeignKey");
        mapping.setGetMethodName("getPrimaryKeyForeignKey");
        mapping.setSetMethodName("setPrimaryKeyForeignKey");
        mapping.setReferenceClass(PrimaryKeyForeignKeyMetadata.class);
        mapping.setXPath("orm:primary-key-foreign-key");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getPrimaryKeyJoinColumnMapping() {
        XMLCompositeCollectionMapping primaryKeyJoinColumnMapping = new XMLCompositeCollectionMapping();
        primaryKeyJoinColumnMapping.setAttributeName("m_primaryKeyJoinColumns");
        primaryKeyJoinColumnMapping.setGetMethodName("getPrimaryKeyJoinColumns");
        primaryKeyJoinColumnMapping.setSetMethodName("setPrimaryKeyJoinColumns");
        primaryKeyJoinColumnMapping.setReferenceClass(PrimaryKeyJoinColumnMetadata.class);
        primaryKeyJoinColumnMapping.setXPath("orm:primary-key-join-column");
        return primaryKeyJoinColumnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getPrimaryKeyMapping() {
        XMLCompositeObjectMapping primaryKeyMapping = new XMLCompositeObjectMapping();
        primaryKeyMapping.setAttributeName("m_primaryKey");
        primaryKeyMapping.setReferenceClass(PrimaryKeyMetadata.class);
        primaryKeyMapping.setXPath("orm:primary-key");
        return primaryKeyMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getPrivateOwnedMapping() {
        XMLDirectMapping privateOwnedMapping = new XMLDirectMapping();
        privateOwnedMapping.setAttributeName("m_privateOwned");
        privateOwnedMapping.setGetMethodName("getPrivateOwned");
        privateOwnedMapping.setSetMethodName("setPrivateOwned");
        privateOwnedMapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy privateOwnedPolicy = new IsSetNullPolicy("isPrivateOwned");
        privateOwnedPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        privateOwnedMapping.setNullPolicy(privateOwnedPolicy);
        privateOwnedMapping.setXPath("orm:private-owned");
        return privateOwnedMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getProcedureNameAttributeMapping() {
        XMLDirectMapping procedureNameMapping = new XMLDirectMapping();
        procedureNameMapping.setAttributeName("m_procedureName");
        procedureNameMapping.setGetMethodName("getProcedureName");
        procedureNameMapping.setSetMethodName("setProcedureName");
        procedureNameMapping.setXPath("@procedure-name");
        return procedureNameMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getPropertyMapping() {
        XMLCompositeCollectionMapping propertyMapping = new XMLCompositeCollectionMapping();
        propertyMapping.setAttributeName("m_properties");
        propertyMapping.setGetMethodName("getProperties");
        propertyMapping.setSetMethodName("setProperties");
        propertyMapping.setReferenceClass(PropertyMetadata.class);
        propertyMapping.setXPath("orm:property");
        return propertyMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getQueryMapping() {
        XMLDirectMapping queryMapping = new XMLDirectMapping();
        queryMapping.setAttributeName("m_query");
        queryMapping.setGetMethodName("getQuery");
        queryMapping.setSetMethodName("setQuery");
        queryMapping.setXPath("orm:query");
        return queryMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getQueryParameterAttributeMapping() {
        XMLDirectMapping queryParameterMapping = new XMLDirectMapping();
        queryParameterMapping.setAttributeName("m_queryParameter");
        queryParameterMapping.setGetMethodName("getQueryParameter");
        queryParameterMapping.setSetMethodName("setQueryParameter");
        queryParameterMapping.setXPath("@query-parameter");
        return queryParameterMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getQueryRedirectorsMapping() {
        XMLCompositeObjectMapping redirectorsMapping = new XMLCompositeObjectMapping();
        redirectorsMapping.setAttributeName("m_queryRedirectors");
        redirectorsMapping.setGetMethodName("getQueryRedirectors");
        redirectorsMapping.setSetMethodName("setQueryRedirectors");
        redirectorsMapping.setReferenceClass(QueryRedirectorsMetadata.class);
        redirectorsMapping.setXPath("orm:query-redirectors");
        return redirectorsMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getRangePartitioningMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_rangePartitioning");
        mapping.setGetMethodName("getRangePartitioning");
        mapping.setSetMethodName("setRangePartitioning");
        mapping.setReferenceClass(RangePartitioningMetadata.class);
        mapping.setXPath("orm:range-partitioning");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getReadOnlyAttributeMapping() {
        XMLDirectMapping readOnlyMapping = new XMLDirectMapping();
        readOnlyMapping.setAttributeName("m_readOnly");
        readOnlyMapping.setGetMethodName("getReadOnly");
        readOnlyMapping.setSetMethodName("setReadOnly");
        readOnlyMapping.setXPath("@read-only");
        return readOnlyMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getReferencedColumnNameMapping() {
        XMLDirectMapping referencedColumnNameMapping = new XMLDirectMapping();
        referencedColumnNameMapping.setAttributeName("m_referencedColumnName");
        referencedColumnNameMapping.setGetMethodName("getReferencedColumnName");
        referencedColumnNameMapping.setSetMethodName("setReferencedColumnName");
        referencedColumnNameMapping.setXPath("@referenced-column-name");
        return referencedColumnNameMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getReplicateWritesMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("replicateWrites");
        mapping.setGetMethodName("getReplicateWrites");
        mapping.setSetMethodName("setReplicateWrites");
        mapping.setXPath("@replicate-writes");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getReplicationPartitioningMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_replicationPartitioning");
        mapping.setGetMethodName("getReplicationPartitioning");
        mapping.setSetMethodName("setReplicationPartitioning");
        mapping.setReferenceClass(ReplicationPartitioningMetadata.class);
        mapping.setXPath("orm:replication-partitioning");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getResultClassAttributeMapping() {
        XMLDirectMapping resultClassMapping = new XMLDirectMapping();
        resultClassMapping.setAttributeName("m_resultClassName");
        resultClassMapping.setGetMethodName("getResultClassName");
        resultClassMapping.setSetMethodName("setResultClassName");
        resultClassMapping.setXPath("@result-class");
        return resultClassMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeDirectCollectionMapping getResultClasses() {
        XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
        mapping.setAttributeName("m_resultClassNames");
        mapping.setGetMethodName("getResultClassNames");
        mapping.setSetMethodName("setResultClassNames");
        mapping.setXPath("orm:result-class");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getResultSetMappingAttributeMapping() {
        XMLDirectMapping resultSetMappingMapping = new XMLDirectMapping();
        resultSetMappingMapping.setAttributeName("m_resultSetMapping");
        resultSetMappingMapping.setGetMethodName("getResultSetMapping");
        resultSetMappingMapping.setSetMethodName("setResultSetMapping");
        resultSetMappingMapping.setXPath("@result-set-mapping");
        return resultSetMappingMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getResultSetMappingMapping() {
        XMLCompositeCollectionMapping sqlResultSetMappingMapping = new XMLCompositeCollectionMapping();
        sqlResultSetMappingMapping.setAttributeName("m_sqlResultSetMappings");
        sqlResultSetMappingMapping.setGetMethodName("getSqlResultSetMappings");
        sqlResultSetMappingMapping.setSetMethodName("setSqlResultSetMappings");
        sqlResultSetMappingMapping.setReferenceClass(SQLResultSetMappingMetadata.class);
        sqlResultSetMappingMapping.setXPath("orm:sql-result-set-mapping");
        return sqlResultSetMappingMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeDirectCollectionMapping getResultSetMappings() {
        XMLCompositeDirectCollectionMapping mapping = new XMLCompositeDirectCollectionMapping();
        mapping.setAttributeName("m_resultSetMappings");
        mapping.setGetMethodName("getResultSetMappings");
        mapping.setSetMethodName("setResultSetMappings");
        mapping.setXPath("orm:result-set-mapping");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getReturnInsertMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_returnInsert");
        mapping.setGetMethodName("getReturnInsert");
        mapping.setSetMethodName("setReturnInsert");
        mapping.setReferenceClass(ReturnInsertMetadata.class);
        mapping.setXPath("orm:return-insert");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getReturnUpdateMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_returnUpdate");
        mapping.setGetMethodName("getReturnUpdate");
        mapping.setSetMethodName("setReturnUpdate");
        mapping.setConverter(new EmptyElementConverter());
        IsSetNullPolicy returnUpdatePolicy = new IsSetNullPolicy("isReturnUpdate");
        returnUpdatePolicy.setMarshalNullRepresentation(XMLNullRepresentationType.EMPTY_NODE);
        mapping.setNullPolicy(returnUpdatePolicy);
        mapping.setXPath("orm:return-update");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getRoundRobinPartitioningMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_roundRobinPartitioning");
        mapping.setGetMethodName("getRoundRobinPartitioning");
        mapping.setSetMethodName("setRoundRobinPartitioning");
        mapping.setReferenceClass(RoundRobinPartitioningMetadata.class);
        mapping.setXPath("orm:round-robin-partitioning");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getScaleAttributeMapping() {
        XMLDirectMapping scaleMapping = new XMLDirectMapping();
        scaleMapping.setAttributeName("m_scale");
        scaleMapping.setGetMethodName("getScale");
        scaleMapping.setSetMethodName("setScale");
        scaleMapping.setXPath("@scale");
        return scaleMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getSchemaAttributeMapping() {
        XMLDirectMapping schemaMapping = new XMLDirectMapping();
        schemaMapping.setAttributeName("m_schema");
        schemaMapping.setGetMethodName("getSchema");
        schemaMapping.setSetMethodName("setSchema");
        schemaMapping.setXPath("@schema");
        return schemaMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getSchemaMapping() {
        XMLDirectMapping schemaMapping = new XMLDirectMapping();
        schemaMapping.setAttributeName("m_schema");
        schemaMapping.setGetMethodName("getSchema");
        schemaMapping.setSetMethodName("setSchema");
        schemaMapping.setXPath("orm:schema/text()");
        return schemaMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getSequenceGeneratorMapping() {
        XMLCompositeObjectMapping sequenceGeneratorMapping = new XMLCompositeObjectMapping();
        sequenceGeneratorMapping.setAttributeName("m_sequenceGenerator");
        sequenceGeneratorMapping.setGetMethodName("getSequenceGenerator");
        sequenceGeneratorMapping.setSetMethodName("setSequenceGenerator");
        sequenceGeneratorMapping.setReferenceClass(SequenceGeneratorMetadata.class);
        sequenceGeneratorMapping.setXPath("orm:sequence-generator");
        return sequenceGeneratorMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getSerializedObjectPolicyMapping() {
        XMLCompositeObjectMapping serializedObjectPolicyMapping = new XMLCompositeObjectMapping();
        serializedObjectPolicyMapping.setAttributeName("m_serializedObjectPolicy");
        serializedObjectPolicyMapping.setGetMethodName("getSerializedObjectPolicy");
        serializedObjectPolicyMapping.setSetMethodName("setSerializedObjectPolicy");
        serializedObjectPolicyMapping.setReferenceClass(SerializedObjectPolicyMetadata.class);
        serializedObjectPolicyMapping.setXPath("orm:serialized-object");
        return serializedObjectPolicyMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getSizeAttributeMapping() {
        XMLDirectMapping sizeMapping = new XMLDirectMapping();
        sizeMapping.setAttributeName("m_size");
        sizeMapping.setGetMethodName("getSize");
        sizeMapping.setSetMethodName("setSize");
        sizeMapping.setXPath("@size");
        return sizeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getStructConverterMapping() {
        XMLCompositeCollectionMapping structConvertersMapping = new XMLCompositeCollectionMapping();
        structConvertersMapping.setAttributeName("m_structConverters");
        structConvertersMapping.setGetMethodName("getStructConverters");
        structConvertersMapping.setSetMethodName("setStructConverters");
        structConvertersMapping.setReferenceClass(StructConverterMetadata.class);
        structConvertersMapping.setXPath("orm:struct-converter");
        return structConvertersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getStructMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_struct");
        mapping.setGetMethodName("getStruct");
        mapping.setSetMethodName("setStruct");
        mapping.setReferenceClass(StructMetadata.class);
        mapping.setXPath("orm:struct");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getSubclassSubgraphMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_subclassSubgraph");
        mapping.setGetMethodName("getSubclassSubgraphs");
        mapping.setSetMethodName("setSubclassSubgraphs");
        mapping.setReferenceClass(NamedSubgraphMetadata.class);
        mapping.setXPath("orm:subclass-subgraph");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getSubgraphAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_subgraph");
        mapping.setGetMethodName("getSubgraph");
        mapping.setSetMethodName("setSubgraph");
        mapping.setXPath("@subgraph");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getSubgraphMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("m_subgraphs");
        mapping.setGetMethodName("getSubgraphs");
        mapping.setSetMethodName("setSubgraphs");
        mapping.setReferenceClass(NamedSubgraphMetadata.class);
        mapping.setXPath("orm:subgraph");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getTableAttributeMapping() {
        XMLDirectMapping tableMapping = new XMLDirectMapping();
        tableMapping.setAttributeName("m_table");
        tableMapping.setGetMethodName("getTable");
        tableMapping.setSetMethodName("setTable");
        tableMapping.setXPath("@table");
        return tableMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getTableGeneratorMapping() {
        XMLCompositeObjectMapping tableGeneratorMapping = new XMLCompositeObjectMapping();
        tableGeneratorMapping.setAttributeName("m_tableGenerator");
        tableGeneratorMapping.setGetMethodName("getTableGenerator");
        tableGeneratorMapping.setSetMethodName("setTableGenerator");
        tableGeneratorMapping.setReferenceClass(TableGeneratorMetadata.class);
        tableGeneratorMapping.setXPath("orm:table-generator");
        return tableGeneratorMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getTargetClassAttributeMapping() {
        XMLDirectMapping targetClassMapping = new XMLDirectMapping();
        targetClassMapping.setAttributeName("m_targetClassName");
        targetClassMapping.setGetMethodName("getTargetClassName");
        targetClassMapping.setSetMethodName("setTargetClassName");
        targetClassMapping.setXPath("@target-class");
        return targetClassMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getTargetEntityAttributeMapping() {
        XMLDirectMapping targetEntityMapping = new XMLDirectMapping();
        targetEntityMapping.setAttributeName("m_targetEntityName");
        targetEntityMapping.setGetMethodName("getTargetEntityName");
        targetEntityMapping.setSetMethodName("setTargetEntityName");
        targetEntityMapping.setXPath("@target-entity");
        return targetEntityMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getTargetInterfaceAttributeMapping() {
        XMLDirectMapping targetInterfaceMapping = new XMLDirectMapping();
        targetInterfaceMapping.setAttributeName("m_targetEntityName");
        targetInterfaceMapping.setGetMethodName("getTargetEntityName");
        targetInterfaceMapping.setSetMethodName("setTargetEntityName");
        targetInterfaceMapping.setXPath("@target-interface");
        return targetInterfaceMapping;
    }

    /**
     * INTERNAL
     */
    protected XMLCompositeObjectMapping getTemporalMapping() {
        XMLCompositeObjectMapping temporalMapping = new XMLCompositeObjectMapping();
        temporalMapping.setAttributeName("m_temporal");
        temporalMapping.setGetMethodName("getTemporal");
        temporalMapping.setSetMethodName("setTemporal");
        temporalMapping.setReferenceClass(TemporalMetadata.class);
        temporalMapping.setXPath("orm:temporal");
        return temporalMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getTenantDiscriminatorColumnsMapping() {
        XMLCompositeCollectionMapping tenantDiscriminatorsMapping = new XMLCompositeCollectionMapping();
        tenantDiscriminatorsMapping.setAttributeName("m_tenantDiscriminatorColumns");
        tenantDiscriminatorsMapping.setGetMethodName("getTenantDiscriminatorColumns");
        tenantDiscriminatorsMapping.setSetMethodName("setTenantDiscriminatorColumns");
        tenantDiscriminatorsMapping.setReferenceClass(TenantDiscriminatorColumnMetadata.class);
        tenantDiscriminatorsMapping.setXPath("orm:tenant-discriminator-column");
        return tenantDiscriminatorsMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getTenantTableDiscriminatorMapping() {
        XMLCompositeObjectMapping tenantTableDiscriminatorMapping = new XMLCompositeObjectMapping();
        tenantTableDiscriminatorMapping.setAttributeName("m_tenantTableDiscriminator");
        tenantTableDiscriminatorMapping.setGetMethodName("getTenantTableDiscriminator");
        tenantTableDiscriminatorMapping.setSetMethodName("setTenantTableDiscriminator");
        tenantTableDiscriminatorMapping.setReferenceClass(TenantTableDiscriminatorMetadata.class);
        tenantTableDiscriminatorMapping.setXPath("orm:tenant-table-discriminator");
        return tenantTableDiscriminatorMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getTextMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_text");
        mapping.setGetMethodName("getText");
        mapping.setSetMethodName("setText");
        mapping.setXPath("text()");
        return mapping;
    }

    /**
     * INTERNAL
     */
    protected XMLDirectMapping getTransformerClassAttributeMapping() {
        XMLDirectMapping transformerClassNameMapping = new XMLDirectMapping();
        transformerClassNameMapping.setAttributeName("m_transformerClassName");
        transformerClassNameMapping.setGetMethodName("getTransformerClassName");
        transformerClassNameMapping.setSetMethodName("setTransformerClassName");
        transformerClassNameMapping.setXPath("@transformer-class");
        return transformerClassNameMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getTypeAttributeMapping() {
        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("m_type");
        typeMapping.setGetMethodName("getType");
        typeMapping.setSetMethodName("setType");
        typeMapping.setXPath("@type");
        return typeMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getTypeConverterMapping() {
        XMLCompositeCollectionMapping typeConvertersMapping = new XMLCompositeCollectionMapping();
        typeConvertersMapping.setAttributeName("m_typeConverters");
        typeConvertersMapping.setGetMethodName("getTypeConverters");
        typeConvertersMapping.setSetMethodName("setTypeConverters");
        typeConvertersMapping.setReferenceClass(TypeConverterMetadata.class);
        typeConvertersMapping.setXPath("orm:type-converter");
        return typeConvertersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getSerializedConverterMapping() {
        XMLCompositeCollectionMapping typeConvertersMapping = new XMLCompositeCollectionMapping();
        typeConvertersMapping.setAttributeName("m_serializedConverters");
        typeConvertersMapping.setGetMethodName("getSerializedConverters");
        typeConvertersMapping.setSetMethodName("setSerializedConverters");
        typeConvertersMapping.setReferenceClass(SerializedConverterMetadata.class);
        typeConvertersMapping.setXPath("orm:serialized-converter");
        return typeConvertersMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getTypeNameAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("m_typeName");
        mapping.setGetMethodName("getTypeName");
        mapping.setSetMethodName("setTypeName");
        mapping.setXPath("@type");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getUnionPartitioningMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_unionPartitioning");
        mapping.setGetMethodName("getUnionPartitioning");
        mapping.setSetMethodName("setUnionPartitioning");
        mapping.setReferenceClass(UnionPartitioningMetadata.class);
        mapping.setXPath("orm:union-partitioning");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getUnionUnpartitionableQueriesAttributeMapping() {
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("unionUnpartitionableQueries");
        mapping.setGetMethodName("getUnionUnpartitionableQueries");
        mapping.setSetMethodName("setUnionUnpartitionableQueries");
        mapping.setXPath("@union-unpartitionable-queries");
        return mapping;
    }

    /**
     * INTERNAL
     */
    protected XMLDirectMapping getUniqueAttributeMapping() {
        XMLDirectMapping uniqueMapping = new XMLDirectMapping();
        uniqueMapping.setAttributeName("m_unique");
        uniqueMapping.setGetMethodName("getUnique");
        uniqueMapping.setSetMethodName("setUnique");
        uniqueMapping.setXPath("@unique");
        return uniqueMapping;
    }

    /**
     * INTERNAL
     */
    protected XMLCompositeCollectionMapping getUniqueConstraintMapping() {
        XMLCompositeCollectionMapping uniqueConstraintMapping = new XMLCompositeCollectionMapping();
        uniqueConstraintMapping.setAttributeName("m_uniqueConstraints");
        uniqueConstraintMapping.setGetMethodName("getUniqueConstraints");
        uniqueConstraintMapping.setSetMethodName("setUniqueConstraints");
        uniqueConstraintMapping.setReferenceClass(UniqueConstraintMetadata.class);
        uniqueConstraintMapping.setXPath("orm:unique-constraint");
        return uniqueConstraintMapping;
    }

    /**
     * INTERNAL
     */
    protected XMLDirectMapping getUpdatableAttributeMapping() {
        XMLDirectMapping updatableMapping = new XMLDirectMapping();
        updatableMapping.setAttributeName("m_updatable");
        updatableMapping.setGetMethodName("getUpdatable");
        updatableMapping.setSetMethodName("setUpdatable");
        updatableMapping.setXPath("@updatable");
        return updatableMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getUuidGeneratorMapping() {
        XMLCompositeObjectMapping uuidGeneratorMapping = new XMLCompositeObjectMapping();
        uuidGeneratorMapping.setAttributeName("m_uuidGenerator");
        uuidGeneratorMapping.setGetMethodName("getUuidGenerator");
        uuidGeneratorMapping.setSetMethodName("setUuidGenerator");
        uuidGeneratorMapping.setReferenceClass(UuidGeneratorMetadata.class);
        uuidGeneratorMapping.setXPath("orm:uuid-generator");
        return uuidGeneratorMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getValueAttributeMapping() {
        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("m_value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setXPath("@value");
        return valueMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getValueColumnMapping() {
        XMLCompositeObjectMapping valueColumnMapping = new XMLCompositeObjectMapping();
        valueColumnMapping.setAttributeName("m_valueColumn");
        valueColumnMapping.setGetMethodName("getValueColumn");
        valueColumnMapping.setSetMethodName("setValueColumn");
        valueColumnMapping.setReferenceClass(ColumnMetadata.class);
        valueColumnMapping.setXPath("orm:value-column");
        return valueColumnMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLDirectMapping getValueMapping() {
        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("m_value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setXPath("text()");
        return valueMapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeCollectionMapping getValuePartitionMapping() {
        XMLCompositeCollectionMapping mapping = new XMLCompositeCollectionMapping();
        mapping.setAttributeName("partitions");
        mapping.setGetMethodName("getPartitions");
        mapping.setSetMethodName("setPartitions");
        mapping.setReferenceClass(ValuePartitionMetadata.class);
        mapping.setXPath("orm:partition");
        return mapping;
    }

    /**
     * INTERNAL:
     */
    protected XMLCompositeObjectMapping getValuePartitioningMapping() {
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setAttributeName("m_valuePartitioning");
        mapping.setGetMethodName("getValuePartitioning");
        mapping.setSetMethodName("setValuePartitioning");
        mapping.setReferenceClass(ValuePartitioningMetadata.class);
        mapping.setXPath("orm:value-partitioning");
        return mapping;
    }
}
