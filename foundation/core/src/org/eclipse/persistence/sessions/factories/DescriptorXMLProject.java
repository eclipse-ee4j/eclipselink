/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.factories;

import java.util.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.queries.*;
import deprecated.sdk.*;
import org.eclipse.persistence.sessions.*;
import deprecated.workbench.expressions.*;
import deprecated.xml.*;

/**
 * INTERNAL:
 * Old deployment XML project.
 * Note this class is only used for backward compatiblity and should no longer be changed.
 * Any new changes must be done to the new ObjectPersistenceXMLProject,
 * and any changes MUST be reflected in the OPM XML schema.
 * Define the TopLink project and descriptor information to read a TopLink project from an XML file.
 * @see ObjectPersistenceXMLProject
 */
public class DescriptorXMLProject extends Project implements FalseUndefinedTrue {

    /**
     * INTERNAL:
     * Return a new descriptor project.
     */
    public DescriptorXMLProject() {
        addDescriptor(buildProjectDescriptor());
        addDescriptor(buildDatabaseLoginDescriptor());
        addDescriptor(buildDescriptorDescriptor());
        addDescriptor(buildObjectRelationalDataTypeDescriptorDescriptor());
        addDescriptor(buildSDKDescriptorDescriptor());
        addDescriptor(buildXMLDescriptorDescriptor());

        addDescriptor(buildInheritancePolicyDescriptor());
        addDescriptor(buildInterfacePolicyDescriptor());
        addDescriptor(buildVersionLockingPolicyDescriptor());
        addDescriptor(buildTimestmapLockingPolicyDescriptor());
        addDescriptor(buildEventManagerDescriptor());
        addDescriptor(buildQueryManagerDescriptor());
        addDescriptor(buildMethodBaseQueryRedirectorDescriptor());
        addDescriptor(buildDatabaseQueryDescriptor());
        addDescriptor(buildReadQueryDescriptor());
        addDescriptor(buildObjectLevelReadQueryDescriptor());
        addDescriptor(buildReadAllObjectQueryDescriptor());
        addDescriptor(buildReadObjectQueryDescriptor());
        addDescriptor(buildInMemoryQueryIndirectionPolicyDescriptor());
        addDescriptor(buildInstantiationPolicyDescriptor());
        addDescriptor(buildCopyPolicyDescriptor());
        addDescriptor(buildAssociationDescriptor());
        addDescriptor(buildTypedAssociationDescriptor());
        addDescriptor(buildContainerPolicyDescriptor());
        addDescriptor(buildInterfaceContainerPolicyDescriptor());
        addDescriptor(buildMapContainerPolicyDescriptor());
        addDescriptor(buildCollectionContainerPolicyDescriptor());
        addDescriptor(buildListContainerPolicyDescriptor());
        addDescriptor(buildIndirectionPolicyDescriptor());
        addDescriptor(buildBasicIndirectionPolicyDescriptor());
        addDescriptor(buildNoIndirectionPolicyDescriptor());
        addDescriptor(buildTransparentIndirectionPolicyDescriptor());
        addDescriptor(buildProxyIndirectionPolicyDescriptor());
        addDescriptor(buildContainerIndirectionPolicyDescriptor());

        addDescriptor(buildQueryKeyDescriptor());
        addDescriptor(buildDirectQueryKeyDescriptor());

        addDescriptor(buildDatabaseMappingDescriptor());

        addDescriptor(buildDirectToFieldMappingDescriptor());
        addDescriptor(buildObjectTypeMappingDescriptor());
        addDescriptor(buildSerializedObjectMappingDescriptor());
        addDescriptor(buildTypeConversionMappingDescriptor());

        addDescriptor(buildTransformationMappingDescriptor());

        addDescriptor(buildAggregateMappingDescriptor());
        addDescriptor(buildAggregateObjectMappingDescriptor());
        addDescriptor(buildSDKAggregateObjectMappingDescriptor());
        addDescriptor(buildStructureMappingDescriptor());
        addDescriptor(buildSDKAggregateCollectionMappingDescriptor());
        addDescriptor(buildObjectArrayMappingDescriptor());

        addDescriptor(buildForeignReferenceMappingDescriptor());
        addDescriptor(buildCollectionMappingDescriptor());
        addDescriptor(buildOneToManyMappingMappingDescriptor());
        addDescriptor(buildManyToManyMappingMappingDescriptor());
        addDescriptor(buildAggregateCollectionMappingDescriptor());
        addDescriptor(buildDirectCollectionMappingDescriptor());
        addDescriptor(buildNestedTableMappingDescriptor());
        addDescriptor(buildObjectReferenceMappingDescriptor());
        addDescriptor(buildOneToOneMappingDescriptor());
        addDescriptor(buildReferenceMappingDescriptor());
        addDescriptor(buildVariableOneToOneMappingDescriptor());

        addDescriptor(buildSDKDirectCollectionMappingDescriptor());
        addDescriptor(buildArrayMappingDescriptor());

        addDescriptor(buildCallDescriptor());
        addDescriptor(buildSQLCallDescriptor());

        addDescriptor(buildExpressionRepresentationDescriptor());
        addDescriptor(buildCompoundExpressionRepresentationDescriptor());
        addDescriptor(buildBasicExpressionRepresentationDescriptor());
        addDescriptor(buildBinaryExpressionRepresentationDescriptor());
        addDescriptor(buildUnaryExpressionRepresentationDescriptor());
        addDescriptor(buildExpressionArgumentRepresentationDescriptor());
        addDescriptor(buildQueryableArgumentRepresentationDescriptor());
        addDescriptor(buildLiteralArgumentRepresentationDescriptor());
        addDescriptor(buildParameterArgumentRepresentationDescriptor());
    }

    protected ClassDescriptor buildAggregateCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(AggregateCollectionMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        SDKDirectCollectionMapping sourceKeyFieldNamesMapping = new SDKDirectCollectionMapping();
        sourceKeyFieldNamesMapping.setAttributeName("sourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setGetMethodName("getSourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setSetMethodName("setSourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setFieldName("source-key-fields");
        sourceKeyFieldNamesMapping.setElementDataTypeName("field");
        descriptor.addMapping(sourceKeyFieldNamesMapping);

        SDKDirectCollectionMapping targetForeignKeyFieldsMapping = new SDKDirectCollectionMapping();
        targetForeignKeyFieldsMapping.setAttributeName("targetForeignKeyFieldsNames");
        targetForeignKeyFieldsMapping.setGetMethodName("getTargetForeignKeyFieldNames");
        targetForeignKeyFieldsMapping.setSetMethodName("setTargetForeignKeyFieldNames");
        targetForeignKeyFieldsMapping.setFieldName("target-foreign-key-fields");
        targetForeignKeyFieldsMapping.setElementDataTypeName("field");
        descriptor.addMapping(targetForeignKeyFieldsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildAggregateMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(AggregateMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        DirectToFieldMapping referenceClassMapping = new DirectToFieldMapping();
        referenceClassMapping.setAttributeClassification(Class.class);
        referenceClassMapping.setAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClass");
        referenceClassMapping.setSetMethodName("setReferenceClass");
        referenceClassMapping.setFieldName("reference-class");
        descriptor.addMapping(referenceClassMapping);

        return descriptor;
    }

    protected ClassDescriptor buildAggregateObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(AggregateObjectMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(AggregateMapping.class);

        DirectToFieldMapping isNullAllowedMapping = new DirectToFieldMapping();
        isNullAllowedMapping.setAttributeName("isNullAllowed");
        isNullAllowedMapping.setGetMethodName("isNullAllowed");
        isNullAllowedMapping.setSetMethodName("setIsNullAllowed");
        isNullAllowedMapping.setFieldName("is-null-allowed");
        descriptor.addMapping(isNullAllowedMapping);

        SDKAggregateCollectionMapping aggregateToSourceFieldNameAssociationsMapping = new SDKAggregateCollectionMapping();
        aggregateToSourceFieldNameAssociationsMapping.setAttributeName("aggregateToSourceFieldNameAssociations");
        aggregateToSourceFieldNameAssociationsMapping.setGetMethodName("getAggregateToSourceFieldNameAssociations");
        aggregateToSourceFieldNameAssociationsMapping.setSetMethodName("setAggregateToSourceFieldNameAssociations");
        aggregateToSourceFieldNameAssociationsMapping.setFieldName("aggregate-to-source-field-name-associations");
        aggregateToSourceFieldNameAssociationsMapping.setReferenceClass(Association.class);
        descriptor.addMapping(aggregateToSourceFieldNameAssociationsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildArrayMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.mappings.structures.ArrayMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(SDKDirectCollectionMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildAssociationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(Association.class);
        descriptor.descriptorIsAggregate();
        descriptor.setRootElementName("association");

        DirectToFieldMapping keyMapping = new DirectToFieldMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setFieldName("association-key");
        descriptor.addMapping(keyMapping);

        DirectToFieldMapping valueMapping = new DirectToFieldMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setFieldName("association-value");
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

    protected ClassDescriptor buildBasicIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.indirection.IndirectionPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildCollectionContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.queries.CollectionContainerPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.queries.InterfaceContainerPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(CollectionMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(ForeignReferenceMapping.class);

        SDKAggregateObjectMapping containerPolicyMapping = new SDKAggregateObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.queries.ContainerPolicy.class);
        containerPolicyMapping.setFieldName("container-policy");
        descriptor.addMapping(containerPolicyMapping);

        DirectToFieldMapping ascOrderByMapping = new DirectToFieldMapping();
        ascOrderByMapping.setAttributeName("ascendingOrderBy");
        ascOrderByMapping.setGetMethodName("getAscendingOrderByQueryKey");
        ascOrderByMapping.setSetMethodName("addAscendingOrdering");
        ascOrderByMapping.setFieldName("ascending-order-by-query-key");
        descriptor.addMapping(ascOrderByMapping);

        DirectToFieldMapping descOrderByMapping = new DirectToFieldMapping();
        descOrderByMapping.setAttributeName("descendingOrderBy");
        descOrderByMapping.setGetMethodName("getDescendingOrderByQueryKey");
        descOrderByMapping.setSetMethodName("addDescendingOrdering");
        descOrderByMapping.setFieldName("descending-order-by-query-key");
        descriptor.addMapping(descOrderByMapping);

        return descriptor;
    }

    protected ClassDescriptor buildContainerIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.indirection.ContainerIndirectionPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.indirection.IndirectionPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.queries.ContainerPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.setRootElementName("mapping-container-policy");

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);

        return descriptor;
    }

    protected ClassDescriptor buildCopyPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.descriptors.copying.CloneCopyPolicy.class);
        descriptor.setRootElementName("descriptor-copy-policy");
        descriptor.descriptorIsAggregate();

        DirectToFieldMapping methodNameMapping = new DirectToFieldMapping();
        methodNameMapping.setAttributeName("methodName");
        methodNameMapping.setGetMethodName("getMethodName");
        methodNameMapping.setSetMethodName("setMethodName");
        methodNameMapping.setFieldName("method");
        descriptor.addMapping(methodNameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDatabaseLoginDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(DatabaseLogin.class);
        descriptor.setRootElementName("database-login");
        descriptor.descriptorIsAggregate();

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);

        DirectToFieldMapping platformMapping = new DirectToFieldMapping();
        platformMapping.setAttributeName("platform");
        platformMapping.setGetMethodName("getPlatformClassName");
        platformMapping.setSetMethodName("setPlatformClassName");
        platformMapping.setFieldName("platform");
        platformMapping.setConverter(new Converter(){
            private Map platformList;
            public Object convertObjectValueToDataValue(Object objectValue, Session session){
                //if this code is writin out, write out the converted value
                return objectValue;
            }

            public Object convertDataValueToObjectValue(Object dataValue, Session session){
                // convert deprecated platforms to new platforms
                Object result = platformList.get(dataValue);
                if (result == null){
                    return dataValue;
                }else{
                    return result;
                }
            }

            public boolean isMutable(){
                return false;
            }

            public void initialize(DatabaseMapping mapping, Session session){
                this.platformList = new HashMap();
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.AccessPlatform", "org.eclipse.persistence.platform.database.AccessPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.AttunityPlatform", "org.eclipse.persistence.platform.database.AttunityPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.CloudscapePlatform", "org.eclipse.persistence.platform.database.CloudscapePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.DatabasePlatform", "org.eclipse.persistence.platform.database.DatabasePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.DB2MainframePlatform", "org.eclipse.persistence.platform.database.DB2MainframePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.DB2Platform", "org.eclipse.persistence.platform.database.DB2Platform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.DBasePlatform", "org.eclipse.persistence.platform.database.DBasePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.HSQLPlatform", "org.eclipse.persistence.platform.database.HSQLPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.InformixPlatform", "org.eclipse.persistence.platform.database.InformixPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.OraclePlatform", "org.eclipse.persistence.platform.database.oracle.OraclePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.PointBasePlatform", "org.eclipse.persistence.platform.database.PointBasePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.SQLAnyWherePlatform", "org.eclipse.persistence.platform.database.SQLAnyWherePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.SQLServerPlatform", "org.eclipse.persistence.platform.database.SQLServerPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.SybasePlatform", "org.eclipse.persistence.platform.database.SybasePlatform");
                this.platformList.put("org.eclipse.persistence.oraclespecific.Oracle8Platform", "org.eclipse.persistence.platform.database.oracle.Oracle8Platform");
                this.platformList.put("org.eclipse.persistence.oraclespecific.Oracle9Platform", "org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
            }
            
        });
        descriptor.addMapping(platformMapping);

        DirectToFieldMapping driverClassNameMapping = new DirectToFieldMapping();
        driverClassNameMapping.setAttributeName("driverClassName");
        driverClassNameMapping.setGetMethodName("getDriverClassName");
        driverClassNameMapping.setSetMethodName("setDriverClassName");
        driverClassNameMapping.setFieldName("driver-class");
        descriptor.addMapping(driverClassNameMapping);

        DirectToFieldMapping driverURLMapping = new DirectToFieldMapping();
        driverURLMapping.setAttributeName("connectionString");
        driverURLMapping.setGetMethodName("getConnectionString");
        driverURLMapping.setSetMethodName("setConnectionString");
        driverURLMapping.setFieldName("connection-url");
        descriptor.addMapping(driverURLMapping);

        DirectToFieldMapping userNameMapping = new DirectToFieldMapping();
        userNameMapping.setAttributeName("userName");
        userNameMapping.setGetMethodName("getUserName");
        userNameMapping.setSetMethodName("setUserName");
        userNameMapping.setFieldName("user-name");
        descriptor.addMapping(userNameMapping);

        DirectToFieldMapping passwordMapping = new DirectToFieldMapping();
        passwordMapping.setAttributeName("password");
        passwordMapping.setGetMethodName("getPassword");
        passwordMapping.setSetMethodName("setEncryptedPassword");
        passwordMapping.setFieldName("password");
        descriptor.addMapping(passwordMapping);

        DirectToFieldMapping usesNativeSequencingMapping = new DirectToFieldMapping();
        usesNativeSequencingMapping.setAttributeName("usesNativeSequencing");
        usesNativeSequencingMapping.setGetMethodName("shouldUseNativeSequencing");
        usesNativeSequencingMapping.setSetMethodName("setUsesNativeSequencing");
        usesNativeSequencingMapping.setFieldName("uses-native-sequencing");
        descriptor.addMapping(usesNativeSequencingMapping);

        DirectToFieldMapping sequencePreallocationSizeMapping = new DirectToFieldMapping();
        sequencePreallocationSizeMapping.setAttributeName("sequencePreallocationSize");
        sequencePreallocationSizeMapping.setGetMethodName("getSequencePreallocationSize");
        sequencePreallocationSizeMapping.setSetMethodName("setSequencePreallocationSize");
        sequencePreallocationSizeMapping.setFieldName("sequence-preallocation-size");
        descriptor.addMapping(sequencePreallocationSizeMapping);

        DirectToFieldMapping sequenceTableNameMapping = new DirectToFieldMapping();
        sequenceTableNameMapping.setAttributeName("sequenceTableName");
        //CR#2407  Call getQualifiedSequenceTableName that includes table qualifier.
        sequenceTableNameMapping.setGetMethodName("getQualifiedSequenceTableName");
        sequenceTableNameMapping.setSetMethodName("setSequenceTableName");
        sequenceTableNameMapping.setFieldName("sequence-table");
        descriptor.addMapping(sequenceTableNameMapping);

        DirectToFieldMapping sequenceNameFieldNameMapping = new DirectToFieldMapping();
        sequenceNameFieldNameMapping.setAttributeName("sequenceNameFieldName");
        sequenceNameFieldNameMapping.setGetMethodName("getSequenceNameFieldName");
        sequenceNameFieldNameMapping.setSetMethodName("setSequenceNameFieldName");
        sequenceNameFieldNameMapping.setFieldName("sequence-name-field");
        descriptor.addMapping(sequenceNameFieldNameMapping);

        DirectToFieldMapping sequenceCounterFieldNameMapping = new DirectToFieldMapping();
        sequenceCounterFieldNameMapping.setAttributeName("sequenceCounterFieldName");
        sequenceCounterFieldNameMapping.setGetMethodName("getSequenceCounterFieldName");
        sequenceCounterFieldNameMapping.setSetMethodName("setSequenceCounterFieldName");
        sequenceCounterFieldNameMapping.setFieldName("sequence-counter-field");
        descriptor.addMapping(sequenceCounterFieldNameMapping);

        DirectToFieldMapping shouldBindAllParametersMapping = new DirectToFieldMapping();
        shouldBindAllParametersMapping.setAttributeName("shouldBindAllParameters");
        shouldBindAllParametersMapping.setGetMethodName("shouldBindAllParameters");
        shouldBindAllParametersMapping.setSetMethodName("setShouldBindAllParameters");
        shouldBindAllParametersMapping.setFieldName("should-bind-all-parameters");
        shouldBindAllParametersMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldBindAllParametersMapping);

        DirectToFieldMapping shouldCacheAllStatementsMapping = new DirectToFieldMapping();
        shouldCacheAllStatementsMapping.setAttributeName("shouldCacheAllStatements");
        shouldCacheAllStatementsMapping.setGetMethodName("shouldCacheAllStatements");
        shouldCacheAllStatementsMapping.setSetMethodName("setShouldCacheAllStatements");
        shouldCacheAllStatementsMapping.setFieldName("should-cache-all-statements");
        descriptor.addMapping(shouldCacheAllStatementsMapping);

        DirectToFieldMapping usesByteArrayBindingMapping = new DirectToFieldMapping();
        usesByteArrayBindingMapping.setAttributeName("usesByteArrayBinding");
        usesByteArrayBindingMapping.setGetMethodName("shouldUseByteArrayBinding");
        usesByteArrayBindingMapping.setSetMethodName("setUsesByteArrayBinding");
        usesByteArrayBindingMapping.setFieldName("uses-byte-array-binding");
        descriptor.addMapping(usesByteArrayBindingMapping);

        DirectToFieldMapping usesStringBindingMapping = new DirectToFieldMapping();
        usesStringBindingMapping.setAttributeName("usesStringBinding");
        usesStringBindingMapping.setGetMethodName("shouldUseStringBinding");
        usesStringBindingMapping.setSetMethodName("setUsesStringBinding");
        usesStringBindingMapping.setFieldName("uses-string-binding");
        descriptor.addMapping(usesStringBindingMapping);

        DirectToFieldMapping usesStreamsForBindingMapping = new DirectToFieldMapping();
        usesStreamsForBindingMapping.setAttributeName("usesStreamsForBinding");
        usesStreamsForBindingMapping.setGetMethodName("shouldUseStreamsForBinding");
        usesStreamsForBindingMapping.setSetMethodName("setUsesStreamsForBinding");
        usesStreamsForBindingMapping.setFieldName("uses-streams-for-binding");
        descriptor.addMapping(usesStreamsForBindingMapping);

        DirectToFieldMapping shouldForceFieldNamesToUpperCaseMapping = new DirectToFieldMapping();
        shouldForceFieldNamesToUpperCaseMapping.setAttributeName("shouldForceFieldNamesToUpperCase");
        shouldForceFieldNamesToUpperCaseMapping.setGetMethodName("shouldForceFieldNamesToUpperCase");
        shouldForceFieldNamesToUpperCaseMapping.setSetMethodName("setShouldForceFieldNamesToUpperCase");
        shouldForceFieldNamesToUpperCaseMapping.setFieldName("should-force-field-names-to-upper-case");
        descriptor.addMapping(shouldForceFieldNamesToUpperCaseMapping);

        DirectToFieldMapping shouldOptimizeDataConversionMapping = new DirectToFieldMapping();
        shouldOptimizeDataConversionMapping.setAttributeName("shouldOptimizeDataConversion");
        shouldOptimizeDataConversionMapping.setGetMethodName("shouldOptimizeDataConversion");
        shouldOptimizeDataConversionMapping.setSetMethodName("setShouldOptimizeDataConversion");
        shouldOptimizeDataConversionMapping.setFieldName("should-optimize-data-conversion");
        descriptor.addMapping(shouldOptimizeDataConversionMapping);

        DirectToFieldMapping shouldTrimStringsMapping = new DirectToFieldMapping();
        shouldTrimStringsMapping.setAttributeName("shouldTrimStrings");
        shouldTrimStringsMapping.setGetMethodName("shouldTrimStrings");
        shouldTrimStringsMapping.setSetMethodName("setShouldTrimStrings");
        shouldTrimStringsMapping.setFieldName("should-trim-strings");
        descriptor.addMapping(shouldTrimStringsMapping);

        DirectToFieldMapping usesBatchWritingMapping = new DirectToFieldMapping();
        usesBatchWritingMapping.setAttributeName("usesBatchWriting");
        usesBatchWritingMapping.setGetMethodName("shouldUseBatchWriting");
        usesBatchWritingMapping.setSetMethodName("setUsesBatchWriting");
        usesBatchWritingMapping.setFieldName("uses-batch-writing");
        descriptor.addMapping(usesBatchWritingMapping);

        DirectToFieldMapping usesJDBCBatchWritingMapping = new DirectToFieldMapping();
        usesJDBCBatchWritingMapping.setAttributeName("usesJDBCBatchWriting");
        usesJDBCBatchWritingMapping.setGetMethodName("shouldUseJDBCBatchWriting");
        usesJDBCBatchWritingMapping.setSetMethodName("setUsesJDBCBatchWriting");
        usesJDBCBatchWritingMapping.setFieldName("uses-jdbc-batch-writing");
        descriptor.addMapping(usesJDBCBatchWritingMapping);

        DirectToFieldMapping usesExternalConnectionPoolingMapping = new DirectToFieldMapping();
        usesExternalConnectionPoolingMapping.setAttributeName("usesExternalConnectionPooling");
        usesExternalConnectionPoolingMapping.setGetMethodName("shouldUseExternalConnectionPooling");
        usesExternalConnectionPoolingMapping.setSetMethodName("setUsesExternalConnectionPooling");
        usesExternalConnectionPoolingMapping.setFieldName("uses-external-connection-pooling");
        descriptor.addMapping(usesExternalConnectionPoolingMapping);

        DirectToFieldMapping usesExternalTransactionControllerMapping = new DirectToFieldMapping();
        usesExternalTransactionControllerMapping.setAttributeName("usesExternalTransactionController");
        usesExternalTransactionControllerMapping.setGetMethodName("shouldUseExternalTransactionController");
        usesExternalTransactionControllerMapping.setSetMethodName("setUsesExternalTransactionController");
        usesExternalTransactionControllerMapping.setFieldName("uses-external-transaction-controller");
        descriptor.addMapping(usesExternalTransactionControllerMapping);

        return descriptor;
    }

    protected ClassDescriptor buildCallDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Call.class);
        descriptor.setRootElementName("call");
        descriptor.descriptorIsAggregate();

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);

        return descriptor;
    }

    public ClassDescriptor buildSQLCallDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SQLCall.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(Call.class);

        DirectToFieldMapping sqlStringMapping = new DirectToFieldMapping();
        sqlStringMapping.setAttributeName("sqlString");
        sqlStringMapping.setGetMethodName("getSQLString");
        sqlStringMapping.setSetMethodName("setSQLString");
        sqlStringMapping.setFieldName("sql-string");
        descriptor.addMapping(sqlStringMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDatabaseQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatabaseQuery.class);
        descriptor.setRootElementName("database-query");
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);

        DirectToFieldMapping directToFieldMapping = new DirectToFieldMapping();
        directToFieldMapping.setAttributeName("name");
        directToFieldMapping.setGetMethodName("getName");
        directToFieldMapping.setSetMethodName("setName");
        directToFieldMapping.setFieldName("query-name");
        descriptor.addMapping(directToFieldMapping);

        // bugs 4034159 and 3432075
        ObjectTypeMapping shouldMaintainCacheMapping = new ObjectTypeMapping();
        shouldMaintainCacheMapping.setAttributeName("shouldMaintainCache");
        shouldMaintainCacheMapping.addConversionValue("false", new Integer(FalseUndefinedTrue.False));
        shouldMaintainCacheMapping.addConversionValue("undefined", new Integer(FalseUndefinedTrue.Undefined));
        shouldMaintainCacheMapping.addConversionValue("true", new Integer(FalseUndefinedTrue.True));
        shouldMaintainCacheMapping.setGetMethodName("deploymentShouldMaintainCache");
        shouldMaintainCacheMapping.setSetMethodName("deploymentSetShouldMaintainCache");
        shouldMaintainCacheMapping.setFieldName("query-should-maintain-cache");
        descriptor.addMapping(shouldMaintainCacheMapping);

        DirectToFieldMapping directToFieldMapping2 = new DirectToFieldMapping();
        directToFieldMapping2.setAttributeName("cascadePolicy");
        directToFieldMapping2.setGetMethodName("getCascadePolicy");
        directToFieldMapping2.setSetMethodName("setCascadePolicy");
        directToFieldMapping2.setFieldName("cascade-policy");
        descriptor.addMapping(directToFieldMapping2);

        DirectToFieldMapping directToFieldMapping3 = new DirectToFieldMapping();
        directToFieldMapping3.setAttributeName("sessionName");
        directToFieldMapping3.setGetMethodName("getSessionName");
        directToFieldMapping3.setSetMethodName("setSessionName");
        directToFieldMapping3.setFieldName("session-name");
        descriptor.addMapping(directToFieldMapping3);

        ObjectTypeMapping shouldBindAllParametersMapping = new ObjectTypeMapping();
        shouldBindAllParametersMapping.setAttributeName("shouldBindAllParameters");
        shouldBindAllParametersMapping.addConversionValue("false", new Integer(FalseUndefinedTrue.False));
        shouldBindAllParametersMapping.addConversionValue("undefined", new Integer(FalseUndefinedTrue.Undefined));
        shouldBindAllParametersMapping.addConversionValue("true", new Integer(FalseUndefinedTrue.True));
        shouldBindAllParametersMapping.setFieldName("should-bind-all-parameters");
        descriptor.addMapping(shouldBindAllParametersMapping);

        ObjectTypeMapping shouldCacheStatementMapping = new ObjectTypeMapping();
        shouldCacheStatementMapping.setAttributeName("shouldCacheStatement");
        shouldCacheStatementMapping.addConversionValue("false", new Integer(FalseUndefinedTrue.False));
        shouldCacheStatementMapping.addConversionValue("undefined", new Integer(FalseUndefinedTrue.Undefined));
        shouldCacheStatementMapping.addConversionValue("true", new Integer(FalseUndefinedTrue.True));
        shouldCacheStatementMapping.setFieldName("should-cache-statement");
        descriptor.addMapping(shouldCacheStatementMapping);

        DirectToFieldMapping directToFieldMapping6 = new DirectToFieldMapping();
        directToFieldMapping6.setAttributeName("shouldUseWrapperPolicy");
        directToFieldMapping6.setGetMethodName("shouldUseWrapperPolicy");
        directToFieldMapping6.setSetMethodName("setShouldUseWrapperPolicy");
        directToFieldMapping6.setFieldName("should-use-wrapper-policy");
        descriptor.addMapping(directToFieldMapping6);

        DirectToFieldMapping directToFieldMapping7 = new DirectToFieldMapping();
        directToFieldMapping7.setAttributeName("queryTimeout");
        directToFieldMapping7.setGetMethodName("getQueryTimeout");
        directToFieldMapping7.setSetMethodName("setQueryTimeout");
        directToFieldMapping7.setNullValue(new Integer(DescriptorQueryManager.DefaultTimeout));
        directToFieldMapping7.setFieldName("query-timeout");
        descriptor.addMapping(directToFieldMapping7);

        DirectToFieldMapping sqlStringMapping = new DirectToFieldMapping();
        sqlStringMapping.setAttributeName("sqlString");
        sqlStringMapping.setGetMethodName("getSQLString");
        sqlStringMapping.setSetMethodName("setSQLString");
        sqlStringMapping.setFieldName("sql-string");
        sqlStringMapping.readOnly();
        descriptor.addMapping(sqlStringMapping);

        DirectToFieldMapping ejbqlStringMapping = new DirectToFieldMapping();
        ejbqlStringMapping.setAttributeName("ejbqlString");
        ejbqlStringMapping.setGetMethodName("getEJBQLString");
        ejbqlStringMapping.setSetMethodName("setEJBQLString");
        ejbqlStringMapping.setFieldName("ejbql-string");
        descriptor.addMapping(ejbqlStringMapping);

        SDKAggregateObjectMapping callMapping = new SDKAggregateObjectMapping();
        callMapping.setAttributeName("call");
        callMapping.setGetMethodName("getDatasourceCall");
        callMapping.setSetMethodName("setCall");
        callMapping.setReferenceClass(Call.class);
        callMapping.setFieldName("call");
        descriptor.addMapping(callMapping);

        SDKDirectCollectionMapping argumentsMapping = new SDKDirectCollectionMapping();
        argumentsMapping.setAttributeName("arguments");
        argumentsMapping.setGetMethodName("getArguments");
        argumentsMapping.setSetMethodName("setArguments");
        argumentsMapping.setFieldName("query-arguments");
        argumentsMapping.setElementDataTypeName("string");
        descriptor.addMapping(argumentsMapping);

        SDKDirectCollectionMapping argumentValuesMapping = new SDKDirectCollectionMapping();
        argumentValuesMapping.setAttributeName("argumentValues");
        argumentValuesMapping.setGetMethodName("getArgumentValues");
        argumentValuesMapping.setSetMethodName("setArgumentValues");
        argumentValuesMapping.setFieldName("query-argument-values");
        argumentValuesMapping.setElementDataTypeName("values");
        descriptor.addMapping(argumentValuesMapping);

        // CR#3534 Support for argument types
        SDKDirectCollectionMapping argumentTypesMapping = new SDKDirectCollectionMapping();
        argumentTypesMapping.setAttributeName("argumentTypes");
        argumentTypesMapping.setGetMethodName("getArgumentTypes");
        argumentTypesMapping.setSetMethodName("setArgumentTypes");
        argumentTypesMapping.setFieldName("query-argument-types");
        argumentTypesMapping.setElementDataTypeName("values");
        argumentTypesMapping.setAttributeElementClass(java.lang.Class.class);
        descriptor.addMapping(argumentTypesMapping);

        SDKAggregateObjectMapping redirectorMapping = new SDKAggregateObjectMapping();
        redirectorMapping.setAttributeName("redirector");
        redirectorMapping.setGetMethodName("getRedirector");
        redirectorMapping.setSetMethodName("setRedirector");
        redirectorMapping.setReferenceClass(org.eclipse.persistence.queries.MethodBaseQueryRedirector.class);
        redirectorMapping.setFieldName("query-redirector");
        descriptor.addMapping(redirectorMapping);

        // feaure 2297
        DirectToFieldMapping shouldPrepareMapping = new DirectToFieldMapping();
        shouldPrepareMapping.setAttributeName("shouldPrepare");
        shouldPrepareMapping.setGetMethodName("shouldPrepare");
        shouldPrepareMapping.setSetMethodName("setShouldPrepare");
        shouldPrepareMapping.setFieldName("should-prepare");
        shouldPrepareMapping.setNullValue(new Boolean(true));
        descriptor.addMapping(shouldPrepareMapping);
        return descriptor;
    }

    // feature 2297
    protected ClassDescriptor buildReadQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.queries.ReadQuery.class);
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.queries.DatabaseQuery.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);

        DirectToFieldMapping shouldCacheQueryResultsMapping = new DirectToFieldMapping();
        shouldCacheQueryResultsMapping.setAttributeName("shouldCacheQueryResults");
        shouldCacheQueryResultsMapping.setGetMethodName("shouldCacheQueryResults");
        shouldCacheQueryResultsMapping.setSetMethodName("setShouldCacheQueryResults");
        shouldCacheQueryResultsMapping.setFieldName("should-cache-query-results");
        shouldCacheQueryResultsMapping.setNullValue(new Boolean(false));
        descriptor.addMapping(shouldCacheQueryResultsMapping);

        DirectToFieldMapping maxRowsMapping = new DirectToFieldMapping();
        maxRowsMapping.setAttributeName("maxRows");
        maxRowsMapping.setGetMethodName("getMaxRows");
        maxRowsMapping.setSetMethodName("setMaxRows");
        maxRowsMapping.setFieldName("max-rows");
        maxRowsMapping.setNullValue(new Integer(0));
        descriptor.addMapping(maxRowsMapping);
        return descriptor;
    }

    protected ClassDescriptor buildObjectLevelReadQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.queries.ObjectLevelReadQuery.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.queries.ReadQuery.class);

        DirectToFieldMapping referenceClassMapping = new DirectToFieldMapping();
        referenceClassMapping.setAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClass");
        referenceClassMapping.setSetMethodName("setReferenceClass");
        referenceClassMapping.setFieldName("reference-class");
        descriptor.addMapping(referenceClassMapping);

        DirectToFieldMapping refreshIdentityMapping = new DirectToFieldMapping();
        refreshIdentityMapping.setAttributeName("shouldRefreshIdentityMapResult");
        refreshIdentityMapping.setGetMethodName("shouldRefreshIdentityMapResult");
        refreshIdentityMapping.setSetMethodName("setShouldRefreshIdentityMapResult");
        refreshIdentityMapping.setFieldName("refresh-identity-map");
        descriptor.addMapping(refreshIdentityMapping);

        DirectToFieldMapping refreshRemoteIdentityMapping = new DirectToFieldMapping();
        refreshRemoteIdentityMapping.setAttributeName("shouldRefreshRemoteIdentityMapResult");
        refreshRemoteIdentityMapping.setGetMethodName("shouldRefreshRemoteIdentityMapResult");
        refreshRemoteIdentityMapping.setSetMethodName("setShouldRefreshRemoteIdentityMapResult");
        refreshRemoteIdentityMapping.setFieldName("refresh-remote-identity-map");
        refreshRemoteIdentityMapping.setNullValue(new Boolean(false));
        descriptor.addMapping(refreshRemoteIdentityMapping);

        DirectToFieldMapping cacheUsageMapping = new DirectToFieldMapping();
        cacheUsageMapping.setAttributeName("cacheUsage");
        cacheUsageMapping.setGetMethodName("getCacheUsage");
        cacheUsageMapping.setSetMethodName("setCacheUsage");
        cacheUsageMapping.setFieldName("cache-usage");
        descriptor.addMapping(cacheUsageMapping);

        DirectToFieldMapping lockModeMapping = new DirectToFieldMapping();
        lockModeMapping.setAttributeName("lockMode");
        lockModeMapping.setGetMethodName("getLockMode");
        lockModeMapping.setSetMethodName("setLockMode");
        lockModeMapping.setFieldName("lock-mode");
        descriptor.addMapping(lockModeMapping);

        // feature 2297
        DirectToFieldMapping distinctStateMapping = new DirectToFieldMapping();
        distinctStateMapping.setAttributeName("distinctState");
        distinctStateMapping.setGetMethodName("getDistinctState");
        distinctStateMapping.setSetMethodName("setDistinctState");
        distinctStateMapping.setFieldName("distinct-state");
        distinctStateMapping.setNullValue(new Integer(0));
        descriptor.addMapping(distinctStateMapping);

        SDKAggregateObjectMapping inMemoryQueryIndirectionPolicyMapping = new SDKAggregateObjectMapping();
        inMemoryQueryIndirectionPolicyMapping.setAttributeName("inMemoryQueryIndirectionPolicy");
        inMemoryQueryIndirectionPolicyMapping.setGetMethodName("getInMemoryQueryIndirectionPolicy");
        inMemoryQueryIndirectionPolicyMapping.setSetMethodName("setInMemoryQueryIndirectionPolicy");
        inMemoryQueryIndirectionPolicyMapping.setReferenceClass(org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy.class);
        inMemoryQueryIndirectionPolicyMapping.setFieldName("query-indirection");
        descriptor.addMapping(inMemoryQueryIndirectionPolicyMapping);

        //feature 7870-2639 add selection criteria
        SDKAggregateObjectMapping expressionMapping = new SDKAggregateObjectMapping();
        expressionMapping.setAttributeName("expression");
        expressionMapping.setGetMethodName("getSelectionCriteriaForPersistence");
        expressionMapping.setSetMethodName("setSelectionCriteriaFromPersistence");
        expressionMapping.setReferenceClass(deprecated.workbench.expressions.CompoundExpressionRepresentation.class);
        expressionMapping.setFieldName("main-expression");
        descriptor.addMapping(expressionMapping);

        return descriptor;
    }

    protected ClassDescriptor buildReadObjectQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.queries.ReadObjectQuery.class);
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.queries.ObjectLevelReadQuery.class);
        descriptor.descriptorIsAggregate();

        return descriptor;
    }

    protected ClassDescriptor buildReadAllObjectQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.queries.ReadAllQuery.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.queries.ObjectLevelReadQuery.class);

        return descriptor;
    }

    protected ClassDescriptor buildMethodBaseQueryRedirectorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.queries.MethodBaseQueryRedirector.class);
        descriptor.setRootElementName("method-base-query-redirector");
        descriptor.descriptorIsAggregate();

        DirectToFieldMapping methodNameMapping = new DirectToFieldMapping();
        methodNameMapping.setAttributeName("methodName");
        methodNameMapping.setGetMethodName("getMethodName");
        methodNameMapping.setSetMethodName("setMethodName");
        methodNameMapping.setFieldName("method-name");
        descriptor.addMapping(methodNameMapping);

        DirectToFieldMapping methodClassMapping = new DirectToFieldMapping();
        methodClassMapping.setAttributeClassification(Class.class);
        methodClassMapping.setAttributeName("methodClass");
        methodClassMapping.setGetMethodName("getMethodClass");
        methodClassMapping.setSetMethodName("setMethodClass");
        methodClassMapping.setFieldName("method-class");
        descriptor.addMapping(methodClassMapping);
        return descriptor;
    }

    // Feature 2297
    protected ClassDescriptor buildInMemoryQueryIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy.class);
        descriptor.setRootElementName("in-memory-query-indirection");
        descriptor.descriptorIsAggregate();

        DirectToFieldMapping policyMapping = new DirectToFieldMapping();
        policyMapping.setAttributeName("policy");
        policyMapping.setGetMethodName("getPolicy");
        policyMapping.setSetMethodName("setPolicy");
        policyMapping.setFieldName("policy");
        descriptor.addMapping(policyMapping);
        return descriptor;
    }

    protected ClassDescriptor buildExpressionRepresentationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(deprecated.workbench.expressions.ExpressionRepresentation.class);
        descriptor.setRootElementName("expression");
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("expression-class");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);

        //use an object type mapping to preserve object identity for the operator type
        ObjectTypeMapping operatorType = new ObjectTypeMapping();
        operatorType.setAttributeName("operatorType");
        operatorType.addConversionValue(CompoundExpressionRepresentation.AND, CompoundExpressionRepresentation.AND);
        operatorType.addConversionValue(CompoundExpressionRepresentation.OR, CompoundExpressionRepresentation.OR);
        operatorType.addConversionValue(CompoundExpressionRepresentation.NAND, CompoundExpressionRepresentation.NAND);
        operatorType.addConversionValue(CompoundExpressionRepresentation.NOR, CompoundExpressionRepresentation.NOR);
        operatorType.addConversionValue(BinaryExpressionRepresentation.EQUAL, BinaryExpressionRepresentation.EQUAL);
        operatorType.addConversionValue(BinaryExpressionRepresentation.EQUALS_IGNORE_CASE, BinaryExpressionRepresentation.EQUALS_IGNORE_CASE);
        operatorType.addConversionValue(BinaryExpressionRepresentation.GREATER_THAN, BinaryExpressionRepresentation.GREATER_THAN);
        operatorType.addConversionValue(BinaryExpressionRepresentation.GREATER_THAN_EQUAL, BinaryExpressionRepresentation.GREATER_THAN_EQUAL);
        operatorType.addConversionValue(UnaryExpressionRepresentation.IS_NULL, UnaryExpressionRepresentation.IS_NULL);
        operatorType.addConversionValue(BinaryExpressionRepresentation.LESS_THAN, BinaryExpressionRepresentation.LESS_THAN);
        operatorType.addConversionValue(BinaryExpressionRepresentation.LESS_THAN_EQUAL, BinaryExpressionRepresentation.LESS_THAN_EQUAL);
        operatorType.addConversionValue(BinaryExpressionRepresentation.LIKE, BinaryExpressionRepresentation.LIKE);
        operatorType.addConversionValue(BinaryExpressionRepresentation.LIKE_IGNORE_CASE, BinaryExpressionRepresentation.LIKE_IGNORE_CASE);
        operatorType.addConversionValue(BinaryExpressionRepresentation.NOT_EQUAL, BinaryExpressionRepresentation.NOT_EQUAL);
        operatorType.addConversionValue(BinaryExpressionRepresentation.NOT_LIKE, BinaryExpressionRepresentation.NOT_LIKE);
        operatorType.addConversionValue(UnaryExpressionRepresentation.NOT_NULL, UnaryExpressionRepresentation.NOT_NULL);
        operatorType.setFieldName("operator-type");
        descriptor.addMapping(operatorType);

        return descriptor;
    }

    protected ClassDescriptor buildCompoundExpressionRepresentationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(deprecated.workbench.expressions.CompoundExpressionRepresentation.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(deprecated.workbench.expressions.ExpressionRepresentation.class);

        // aggregate collection - expressions
        SDKAggregateCollectionMapping expressionsMapping = new SDKAggregateCollectionMapping();
        expressionsMapping.setAttributeName("expressions");
        expressionsMapping.setReferenceClass(deprecated.workbench.expressions.ExpressionRepresentation.class);
        expressionsMapping.useCollectionClass(Vector.class);
        expressionsMapping.setFieldName("expression-list");
        descriptor.addMapping(expressionsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildBasicExpressionRepresentationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(deprecated.workbench.expressions.BasicExpressionRepresentation.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(deprecated.workbench.expressions.ExpressionRepresentation.class);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);

        // 1-1 to the first BldrArgument
        SDKAggregateObjectMapping firstArgumentMapping = new SDKAggregateObjectMapping();
        firstArgumentMapping.setAttributeName("firstArgument");
        firstArgumentMapping.setReferenceClass(deprecated.workbench.expressions.QueryableArgumentRepresentation.class);
        firstArgumentMapping.setFieldName("first-argument");
        descriptor.addMapping(firstArgumentMapping);

        return descriptor;
    }

    protected ClassDescriptor buildBinaryExpressionRepresentationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(deprecated.workbench.expressions.BinaryExpressionRepresentation.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(deprecated.workbench.expressions.BasicExpressionRepresentation.class);

        // 1-1 to the second BldrArgument
        SDKAggregateObjectMapping secondArgumentMapping = new SDKAggregateObjectMapping();
        secondArgumentMapping.setAttributeName("secondArgument");
        secondArgumentMapping.setReferenceClass(deprecated.workbench.expressions.ExpressionArgumentRepresentation.class);
        secondArgumentMapping.setFieldName("second-argument");
        descriptor.addMapping(secondArgumentMapping);

        return descriptor;
    }

    protected ClassDescriptor buildUnaryExpressionRepresentationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(deprecated.workbench.expressions.UnaryExpressionRepresentation.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(deprecated.workbench.expressions.BasicExpressionRepresentation.class);

        return descriptor;
    }

    protected ClassDescriptor buildExpressionArgumentRepresentationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(deprecated.workbench.expressions.ExpressionArgumentRepresentation.class);
        descriptor.setRootElementName("argument");
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("argument-class");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);

        return descriptor;
    }

    protected ClassDescriptor buildQueryableArgumentRepresentationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(deprecated.workbench.expressions.QueryableArgumentRepresentation.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(deprecated.workbench.expressions.ExpressionArgumentRepresentation.class);

        DirectToFieldMapping isOuterJoinmapping = new DirectToFieldMapping();
        isOuterJoinmapping.setAttributeName("queryKey");
        isOuterJoinmapping.setGetMethodName("getQueryKeyName");
        isOuterJoinmapping.setSetMethodName("setQueryKeyName");
        isOuterJoinmapping.setFieldName("query-key-name");
        descriptor.addMapping(isOuterJoinmapping);

        SDKAggregateObjectMapping baseQueryKeyMapping = new SDKAggregateObjectMapping();
        baseQueryKeyMapping.setAttributeName("baseQueryKey");
        baseQueryKeyMapping.setReferenceClass(deprecated.workbench.expressions.QueryableArgumentRepresentation.class);
        baseQueryKeyMapping.setFieldName("base-query-key");
        descriptor.addMapping(baseQueryKeyMapping);

        DirectToFieldMapping isOuterJoinMapping = new DirectToFieldMapping();
        isOuterJoinMapping.setAttributeName("isOuterJoin");
        isOuterJoinMapping.setGetMethodName("isOuterJoin");
        isOuterJoinMapping.setSetMethodName("setIsOuterJoin");
        isOuterJoinMapping.setFieldName("outer-join");
        descriptor.addMapping(isOuterJoinMapping);

        DirectToFieldMapping isToManyMapping = new DirectToFieldMapping();
        isToManyMapping.setAttributeName("isToMany");
        isToManyMapping.setGetMethodName("isToMany");
        isToManyMapping.setSetMethodName("setIsToMany");
        isToManyMapping.setFieldName("to-many");
        descriptor.addMapping(isToManyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildLiteralArgumentRepresentationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(deprecated.workbench.expressions.LiteralArgumentRepresentation.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(deprecated.workbench.expressions.ExpressionArgumentRepresentation.class);

        DirectToFieldMapping typeMapping = new DirectToFieldMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setFieldName("type");
        descriptor.addMapping(typeMapping);

        // DTF Object value
        descriptor.addDirectMapping("value", "value");

        return descriptor;
    }

    protected ClassDescriptor buildParameterArgumentRepresentationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(deprecated.workbench.expressions.ParameterArgumentRepresentation.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(deprecated.workbench.expressions.ExpressionArgumentRepresentation.class);

        descriptor.addDirectMapping("parameterName", "parameter-name");

        return descriptor;
    }

    protected ClassDescriptor buildDatabaseMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(DatabaseMapping.class);
        descriptor.setRootElementName("database-mapping");
        descriptor.descriptorIsAggregate();

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);

        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("attributeName");
        directtofieldmapping.setGetMethodName("getAttributeName");
        directtofieldmapping.setSetMethodName("setAttributeName");
        directtofieldmapping.setFieldName("attribute-name");
        descriptor.addMapping(directtofieldmapping);

        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("isReadOnly");
        directtofieldmapping2.setGetMethodName("isReadOnly");
        directtofieldmapping2.setSetMethodName("setIsReadOnly");
        directtofieldmapping2.setFieldName("read-only");
        descriptor.addMapping(directtofieldmapping2);

        DirectToFieldMapping directtofieldmapping3 = new DirectToFieldMapping();
        directtofieldmapping3.setAttributeName("getMethodName");
        directtofieldmapping3.setGetMethodName("getGetMethodName");
        directtofieldmapping3.setSetMethodName("setGetMethodName");
        directtofieldmapping3.setFieldName("get-method-name");
        descriptor.addMapping(directtofieldmapping3);

        DirectToFieldMapping directtofieldmapping4 = new DirectToFieldMapping();
        directtofieldmapping4.setAttributeName("setMethodName");
        directtofieldmapping4.setGetMethodName("getSetMethodName");
        directtofieldmapping4.setSetMethodName("setSetMethodName");
        directtofieldmapping4.setFieldName("set-method-name");
        descriptor.addMapping(directtofieldmapping4);

        return descriptor;
    }

    protected ClassDescriptor buildDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().addClassIndicator(RelationalDescriptor.class, "org.eclipse.persistence.publicinterface.Descriptor");
        descriptor.getInheritancePolicy().addClassIndicator(RelationalDescriptor.class, "org.eclipse.persistence.descriptors.RelationalDescriptor");
        descriptor.getInheritancePolicy().addClassIndicator(SDKDescriptor.class, "deprecated.sdk.SDKDescriptor");
        descriptor.getInheritancePolicy().addClassIndicator(ObjectRelationalDataTypeDescriptor.class, "org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor");
        descriptor.getInheritancePolicy().addClassIndicator(XMLDescriptor.class, "deprecated.xml.XMLDescriptor");

        descriptor.setJavaClass(ClassDescriptor.class);
        descriptor.setRootElementName("descriptor");
        descriptor.setPrimaryKeyElementName("java-class");
        descriptor.descriptorIsAggregate();

        descriptor.getEventManager().setPostBuildSelector("applyAmendmentMethod");

        DirectToFieldMapping javaClassMapping = new DirectToFieldMapping();
        javaClassMapping.setAttributeClassification(Class.class);
        javaClassMapping.setAttributeName("javaClass");
        javaClassMapping.setGetMethodName("getJavaClass");
        javaClassMapping.setSetMethodName("setJavaClass");
        javaClassMapping.setFieldName("java-class");
        descriptor.addMapping(javaClassMapping);

        SDKDirectCollectionMapping tablesMapping = new SDKDirectCollectionMapping();
        tablesMapping.setAttributeName("tables");
        tablesMapping.setGetMethodName("getTableNames");
        tablesMapping.setSetMethodName("setTableNames");
        tablesMapping.setFieldName("tables");
        tablesMapping.setElementDataTypeName("table");
        descriptor.addMapping(tablesMapping);

        SDKAggregateCollectionMapping multipleTablesPrimaryKey = new SDKAggregateCollectionMapping();
        multipleTablesPrimaryKey.setAttributeName("multipleTablesPrimaryKeys");
        multipleTablesPrimaryKey.setReferenceClass(Association.class);
        multipleTablesPrimaryKey.setFieldName("multiple-table-primary-keys");
        multipleTablesPrimaryKey.setSetMethodName("setMultipleTablePrimaryKeyFieldNames");
        multipleTablesPrimaryKey.setGetMethodName("getMultipleTablePrimaryKeyAssociations");
        descriptor.addMapping(multipleTablesPrimaryKey);

        SDKAggregateCollectionMapping multipleTables = new SDKAggregateCollectionMapping();
        multipleTables.setAttributeName("multipleTablesForeignKeys");
        multipleTables.setReferenceClass(Association.class);
        multipleTables.setFieldName("multiple-table-foreign-keys");
        multipleTables.setSetMethodName("setMultipleTableForeignKeyFieldNames");
        multipleTables.setGetMethodName("getMultipleTableForeignKeyAssociations");
        descriptor.addMapping(multipleTables);

        SDKDirectCollectionMapping primaryKeyFieldNamesMapping = new SDKDirectCollectionMapping();
        primaryKeyFieldNamesMapping.setAttributeName("primaryKeyFieldNames");
        primaryKeyFieldNamesMapping.setGetMethodName("getPrimaryKeyFieldNames");
        primaryKeyFieldNamesMapping.setSetMethodName("setPrimaryKeyFieldNames");
        primaryKeyFieldNamesMapping.setFieldName("primary-key-fields");
        primaryKeyFieldNamesMapping.setElementDataTypeName("field");
        descriptor.addMapping(primaryKeyFieldNamesMapping);

        DirectToFieldMapping descriptorTypeMapping = new DirectToFieldMapping();
        descriptorTypeMapping.setAttributeName("descriptorTypeValue");
        descriptorTypeMapping.setGetMethodName("getDescriptorTypeValue");
        descriptorTypeMapping.setSetMethodName("setDescriptorTypeValue");
        descriptorTypeMapping.setFieldName("descriptor-type-value");
        descriptor.addMapping(descriptorTypeMapping);

        DirectToFieldMapping sequenceFieldNameMapping = new DirectToFieldMapping();
        sequenceFieldNameMapping.setAttributeName("sequenceNumberFieldName");
        sequenceFieldNameMapping.setGetMethodName("getSequenceNumberFieldName");
        sequenceFieldNameMapping.setSetMethodName("setSequenceNumberFieldName");
        sequenceFieldNameMapping.setFieldName("sequence-number-field");
        descriptor.addMapping(sequenceFieldNameMapping);

        DirectToFieldMapping sequenceNameMapping = new DirectToFieldMapping();
        sequenceNameMapping.setAttributeName("sequenceNumberName");
        sequenceNameMapping.setGetMethodName("getSequenceNumberName");
        sequenceNameMapping.setSetMethodName("setSequenceNumberName");
        sequenceNameMapping.setFieldName("sequence-number-name");
        descriptor.addMapping(sequenceNameMapping);

        DirectToFieldMapping identityMapClassMapping = new DirectToFieldMapping();
        identityMapClassMapping.setAttributeName("identityMapClass");
        identityMapClassMapping.setGetMethodName("getIdentityMapClass");
        identityMapClassMapping.setSetMethodName("setIdentityMapClass");
        identityMapClassMapping.setFieldName("identity-map-class");
        descriptor.addMapping(identityMapClassMapping);

        DirectToFieldMapping remoteIdentityMapClassMapping = new DirectToFieldMapping();
        remoteIdentityMapClassMapping.setAttributeName("remoteIdentityMapClass");
        remoteIdentityMapClassMapping.setGetMethodName("getRemoteIdentityMapClass");
        remoteIdentityMapClassMapping.setSetMethodName("setRemoteIdentityMapClass");
        remoteIdentityMapClassMapping.setFieldName("remote-identity-map-class");
        descriptor.addMapping(remoteIdentityMapClassMapping);

        DirectToFieldMapping identityMapSizeMapping = new DirectToFieldMapping();
        identityMapSizeMapping.setAttributeName("identityMapSize");
        identityMapSizeMapping.setGetMethodName("getIdentityMapSize");
        identityMapSizeMapping.setSetMethodName("setIdentityMapSize");
        identityMapSizeMapping.setFieldName("identity-map-size");
        descriptor.addMapping(identityMapSizeMapping);

        DirectToFieldMapping remoteIdentityMapSizeMapping = new DirectToFieldMapping();
        remoteIdentityMapSizeMapping.setAttributeName("remoteIdentityMapSize");
        remoteIdentityMapSizeMapping.setGetMethodName("getRemoteIdentityMapSize");
        remoteIdentityMapSizeMapping.setSetMethodName("setRemoteIdentityMapSize");
        remoteIdentityMapSizeMapping.setFieldName("remote-identity-map-size");
        descriptor.addMapping(remoteIdentityMapSizeMapping);

        DirectToFieldMapping shouldAlwaysRefreshCacheMapping = new DirectToFieldMapping();
        shouldAlwaysRefreshCacheMapping.setAttributeName("shouldAlwaysRefreshCache");
        shouldAlwaysRefreshCacheMapping.setGetMethodName("shouldAlwaysRefreshCache");
        shouldAlwaysRefreshCacheMapping.setSetMethodName("setShouldAlwaysRefreshCache");
        shouldAlwaysRefreshCacheMapping.setFieldName("should-always-refresh-cache");
        descriptor.addMapping(shouldAlwaysRefreshCacheMapping);

        DirectToFieldMapping shouldAlwaysRefreshCacheOnRemoteMapping = new DirectToFieldMapping();
        shouldAlwaysRefreshCacheOnRemoteMapping.setAttributeName("shouldAlwaysRefreshCacheOnRemote");
        shouldAlwaysRefreshCacheOnRemoteMapping.setGetMethodName("shouldAlwaysRefreshCacheOnRemote");
        shouldAlwaysRefreshCacheOnRemoteMapping.setSetMethodName("setShouldAlwaysRefreshCacheOnRemote");
        shouldAlwaysRefreshCacheOnRemoteMapping.setFieldName("should-always-refresh-cache-on-remote");
        descriptor.addMapping(shouldAlwaysRefreshCacheOnRemoteMapping);

        DirectToFieldMapping shouldOnlyRefreshCacheIfNewerVersionMapping = new DirectToFieldMapping();
        shouldOnlyRefreshCacheIfNewerVersionMapping.setAttributeName("shouldOnlyRefreshCacheIfNewerVersion");
        shouldOnlyRefreshCacheIfNewerVersionMapping.setGetMethodName("shouldOnlyRefreshCacheIfNewerVersion");
        shouldOnlyRefreshCacheIfNewerVersionMapping.setSetMethodName("setShouldOnlyRefreshCacheIfNewerVersion");
        shouldOnlyRefreshCacheIfNewerVersionMapping.setFieldName("should-only-refresh-cache-if-newer-version");
        descriptor.addMapping(shouldOnlyRefreshCacheIfNewerVersionMapping);

        DirectToFieldMapping shouldDisableCacheHitsMapping = new DirectToFieldMapping();
        shouldDisableCacheHitsMapping.setAttributeName("shouldDisableCacheHits");
        shouldDisableCacheHitsMapping.setGetMethodName("shouldDisableCacheHits");
        shouldDisableCacheHitsMapping.setSetMethodName("setShouldDisableCacheHits");
        shouldDisableCacheHitsMapping.setFieldName("should-disable-cache-hits");
        descriptor.addMapping(shouldDisableCacheHitsMapping);

        DirectToFieldMapping shouldDisableCacheHitsOnRemoteMapping = new DirectToFieldMapping();
        shouldDisableCacheHitsOnRemoteMapping.setAttributeName("shouldDisableCacheHitsOnRemote");
        shouldDisableCacheHitsOnRemoteMapping.setGetMethodName("shouldDisableCacheHitsOnRemote");
        shouldDisableCacheHitsOnRemoteMapping.setSetMethodName("setShouldDisableCacheHitsOnRemote");
        shouldDisableCacheHitsOnRemoteMapping.setFieldName("should-disable-cache-hits-on-remote");
        descriptor.addMapping(shouldDisableCacheHitsOnRemoteMapping);

        DirectToFieldMapping shouldAlwaysConformResultsInUnitOfWorkMapping = new DirectToFieldMapping();
        shouldAlwaysConformResultsInUnitOfWorkMapping.setAttributeName("shouldAlwaysConformResultsInUnitOfWork");
        shouldAlwaysConformResultsInUnitOfWorkMapping.setGetMethodName("shouldAlwaysConformResultsInUnitOfWork");
        shouldAlwaysConformResultsInUnitOfWorkMapping.setSetMethodName("setShouldAlwaysConformResultsInUnitOfWork");
        shouldAlwaysConformResultsInUnitOfWorkMapping.setFieldName("should-always-conform-results-in-unit-of-work");
        shouldAlwaysConformResultsInUnitOfWorkMapping.setNullValue(new Boolean(false));
        descriptor.addMapping(shouldAlwaysConformResultsInUnitOfWorkMapping);

        DirectToFieldMapping descriptorIsReadOnlyMapping = new DirectToFieldMapping();
        descriptorIsReadOnlyMapping.setAttributeName("shouldBeReadOnly");
        descriptorIsReadOnlyMapping.setGetMethodName("shouldBeReadOnly");
        descriptorIsReadOnlyMapping.setSetMethodName("setShouldBeReadOnly");
        descriptorIsReadOnlyMapping.setFieldName("descriptor-is-read-only");
        descriptorIsReadOnlyMapping.setNullValue(new Boolean(false));
        descriptor.addMapping(descriptorIsReadOnlyMapping);

        DirectToFieldMapping aliasMapping = new DirectToFieldMapping();
        aliasMapping.setAttributeName("alias");
        aliasMapping.setGetMethodName("getAlias");
        aliasMapping.setSetMethodName("setAlias");
        aliasMapping.setFieldName("alias");
        descriptor.addMapping(aliasMapping);

        DirectToFieldMapping amendmentMethodNameMapping = new DirectToFieldMapping();
        amendmentMethodNameMapping.setAttributeName("amendmentMethodName");
        amendmentMethodNameMapping.setGetMethodName("getAmendmentMethodName");
        amendmentMethodNameMapping.setSetMethodName("setAmendmentMethodName");
        amendmentMethodNameMapping.setFieldName("amendment-method");
        descriptor.addMapping(amendmentMethodNameMapping);

        DirectToFieldMapping amendmentClassMapping = new DirectToFieldMapping();
        amendmentClassMapping.setAttributeClassification(Class.class);
        amendmentClassMapping.setAttributeName("amendmentClass");
        amendmentClassMapping.setGetMethodName("getAmendmentClass");
        amendmentClassMapping.setSetMethodName("setAmendmentClass");
        amendmentClassMapping.setFieldName("amendment-class");
        descriptor.addMapping(amendmentClassMapping);

        SDKAggregateObjectMapping inheritancePolicyMapping = new SDKAggregateObjectMapping();
        inheritancePolicyMapping.setAttributeName("inheritancePolicy");
        inheritancePolicyMapping.setGetMethodName("getInheritancePolicyOrNull");
        inheritancePolicyMapping.setSetMethodName("setInheritancePolicy");
        inheritancePolicyMapping.setReferenceClass(org.eclipse.persistence.descriptors.InheritancePolicy.class);
        inheritancePolicyMapping.setFieldName("inheritance-policy");
        descriptor.addMapping(inheritancePolicyMapping);

        SDKAggregateObjectMapping interfacePolicyMapping = new SDKAggregateObjectMapping();
        interfacePolicyMapping.setAttributeName("interfacePolicy");
        interfacePolicyMapping.setGetMethodName("getInterfacePolicyOrNull");
        interfacePolicyMapping.setSetMethodName("setInterfacePolicy");
        interfacePolicyMapping.setReferenceClass(InterfacePolicy.class);
        interfacePolicyMapping.setFieldName("interface-policy");
        descriptor.addMapping(interfacePolicyMapping);

        SDKAggregateObjectMapping copyPolicyMapping = new SDKAggregateObjectMapping();
        copyPolicyMapping.setAttributeName("copyPolicy");
        copyPolicyMapping.setGetMethodName("getCopyPolicy");
        copyPolicyMapping.setSetMethodName("setCopyPolicy");
        copyPolicyMapping.setReferenceClass(org.eclipse.persistence.descriptors.copying.CloneCopyPolicy.class);
        copyPolicyMapping.setFieldName("copy-policy");
        descriptor.addMapping(copyPolicyMapping);

        SDKAggregateObjectMapping lockingPolicyMapping = new SDKAggregateObjectMapping();
        lockingPolicyMapping.setAttributeName("lockingPolicy");
        lockingPolicyMapping.setGetMethodName("getOptimisticLockingPolicy");
        lockingPolicyMapping.setSetMethodName("setOptimisticLockingPolicy");
        lockingPolicyMapping.setReferenceClass(VersionLockingPolicy.class);
        lockingPolicyMapping.setFieldName("locking-policy");
        descriptor.addMapping(lockingPolicyMapping);

        SDKAggregateObjectMapping instantiationPolicyMapping = new SDKAggregateObjectMapping();
        instantiationPolicyMapping.setAttributeName("instantiationPolicy");
        instantiationPolicyMapping.setGetMethodName("getInstantiationPolicy");
        instantiationPolicyMapping.setSetMethodName("setInstantiationPolicy");
        instantiationPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.descriptors.InstantiationPolicy.class);
        instantiationPolicyMapping.setFieldName("instantiation-policy");
        descriptor.addMapping(instantiationPolicyMapping);

        SDKAggregateObjectMapping queryManagerMapping = new SDKAggregateObjectMapping();
        queryManagerMapping.setAttributeName("queryManager");
        queryManagerMapping.setGetMethodName("getQueryManager");
        queryManagerMapping.setSetMethodName("setQueryManager");
        queryManagerMapping.setReferenceClass(org.eclipse.persistence.descriptors.DescriptorQueryManager.class);
        queryManagerMapping.setFieldName("query-manager");
        descriptor.addMapping(queryManagerMapping);

        SDKAggregateObjectMapping eventManagerMapping = new SDKAggregateObjectMapping();
        eventManagerMapping.setAttributeName("eventManager");
        eventManagerMapping.setGetMethodName("getEventManager");
        eventManagerMapping.setSetMethodName("setEventManager");
        eventManagerMapping.setReferenceClass(org.eclipse.persistence.descriptors.DescriptorEventManager.class);
        eventManagerMapping.setFieldName("event-manager");
        descriptor.addMapping(eventManagerMapping);

        SDKAggregateCollectionMapping queryKeysMapping = new SDKAggregateCollectionMapping();
        queryKeysMapping.setAttributeName("queryKeys");
        queryKeysMapping.setReferenceClass(org.eclipse.persistence.mappings.querykeys.QueryKey.class);
        queryKeysMapping.setFieldName("query-keys");
        queryKeysMapping.setSetMethodName("setQueryKeys");
        queryKeysMapping.setGetMethodName("getQueryKeys");
        queryKeysMapping.useMapClass(Hashtable.class, "getName");
        descriptor.addMapping(queryKeysMapping);

        SDKAggregateCollectionMapping aggregateCollectionMapping = new SDKAggregateCollectionMapping();
        aggregateCollectionMapping.setAttributeName("mappings");
        aggregateCollectionMapping.setReferenceClass(DatabaseMapping.class);
        aggregateCollectionMapping.setFieldName("mappings");
        aggregateCollectionMapping.setSetMethodName("setMappings");
        aggregateCollectionMapping.setGetMethodName("getMappings");
        descriptor.addMapping(aggregateCollectionMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDirectCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(DirectCollectionMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        DirectToFieldMapping referenceTableMapping = new DirectToFieldMapping();
        referenceTableMapping.setAttributeName("referenceTableName");
        //CR#2407  Call getReferenceTableQualifiedName that includes table qualifier.
        referenceTableMapping.setGetMethodName("getReferenceTableQualifiedName");
        referenceTableMapping.setSetMethodName("setReferenceTableName");
        referenceTableMapping.setFieldName("reference-table");
        descriptor.addMapping(referenceTableMapping);

        DirectToFieldMapping directFieldMapping = new DirectToFieldMapping();
        directFieldMapping.setAttributeName("directFieldName");
        directFieldMapping.setGetMethodName("getDirectFieldName");
        directFieldMapping.setSetMethodName("setDirectFieldName");
        directFieldMapping.setFieldName("direct-field");
        descriptor.addMapping(directFieldMapping);

        SDKDirectCollectionMapping sourceKeyFieldNamesMapping = new SDKDirectCollectionMapping();
        sourceKeyFieldNamesMapping.setAttributeName("sourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setGetMethodName("getSourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setSetMethodName("setSourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setFieldName("source-key-fields");
        sourceKeyFieldNamesMapping.setElementDataTypeName("field");
        descriptor.addMapping(sourceKeyFieldNamesMapping);

        SDKDirectCollectionMapping referenceKeyFieldsMapping = new SDKDirectCollectionMapping();
        referenceKeyFieldsMapping.setAttributeName("referenceKeyFieldsNames");
        referenceKeyFieldsMapping.setGetMethodName("getReferenceKeyFieldNames");
        referenceKeyFieldsMapping.setSetMethodName("setReferenceKeyFieldNames");
        referenceKeyFieldsMapping.setFieldName("reference-key-fields");
        referenceKeyFieldsMapping.setElementDataTypeName("field");
        descriptor.addMapping(referenceKeyFieldsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDirectQueryKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.mappings.querykeys.DirectQueryKey.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.mappings.querykeys.QueryKey.class);

        DirectToFieldMapping fieldNameMapping = new DirectToFieldMapping();
        fieldNameMapping.setAttributeName("fieldName");
        fieldNameMapping.setGetMethodName("getQualifiedFieldName");
        fieldNameMapping.setSetMethodName("setFieldName");
        fieldNameMapping.setFieldName("field-name");
        descriptor.addMapping(fieldNameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDirectToFieldMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(DirectToFieldMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("fieldName");
        directtofieldmapping2.setGetMethodName("getFieldName");
        directtofieldmapping2.setSetMethodName("setFieldName");
        directtofieldmapping2.setFieldName("field-name");
        descriptor.addMapping(directtofieldmapping2);

        DirectToFieldMapping directtofieldmapping3 = new DirectToFieldMapping();
        directtofieldmapping3.setAttributeName("nullValue");
        directtofieldmapping3.setGetMethodName("getNullValue");
        directtofieldmapping3.setSetMethodName("setNullValue");
        directtofieldmapping3.setFieldName("null-value");
        descriptor.addMapping(directtofieldmapping3);

        return descriptor;
    }

    protected ClassDescriptor buildEventManagerDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.descriptors.DescriptorEventManager.class);
        descriptor.setRootElementName("descriptor-event-manager");
        descriptor.descriptorIsAggregate();

        DirectToFieldMapping aboutToInsertSelectorMapping = new DirectToFieldMapping();
        aboutToInsertSelectorMapping.setAttributeName("aboutToInsertSelector");
        aboutToInsertSelectorMapping.setGetMethodName("getAboutToInsertSelector");
        aboutToInsertSelectorMapping.setSetMethodName("setAboutToInsertSelector");
        aboutToInsertSelectorMapping.setFieldName("about-to-insert-selector");
        descriptor.addMapping(aboutToInsertSelectorMapping);

        DirectToFieldMapping aboutToUpdateSelectorMapping = new DirectToFieldMapping();
        aboutToUpdateSelectorMapping.setAttributeName("aboutToUpdateSelector");
        aboutToUpdateSelectorMapping.setGetMethodName("getAboutToUpdateSelector");
        aboutToUpdateSelectorMapping.setSetMethodName("setAboutToUpdateSelector");
        aboutToUpdateSelectorMapping.setFieldName("about-to-update-selector");
        descriptor.addMapping(aboutToUpdateSelectorMapping);

        DirectToFieldMapping postBuildSelectorMapping = new DirectToFieldMapping();
        postBuildSelectorMapping.setAttributeName("getPostBuildSelector");
        postBuildSelectorMapping.setGetMethodName("getPostBuildSelector");
        postBuildSelectorMapping.setSetMethodName("setPostBuildSelector");
        postBuildSelectorMapping.setFieldName("post-build-selector");
        descriptor.addMapping(postBuildSelectorMapping);

        DirectToFieldMapping postRefreshSelectorMapping = new DirectToFieldMapping();
        postRefreshSelectorMapping.setAttributeName("getPostRefreshSelector");
        postRefreshSelectorMapping.setGetMethodName("getPostRefreshSelector");
        postRefreshSelectorMapping.setSetMethodName("setPostRefreshSelector");
        postRefreshSelectorMapping.setFieldName("post-refresh-selector");
        descriptor.addMapping(postRefreshSelectorMapping);

        DirectToFieldMapping postCloneSelectorMapping = new DirectToFieldMapping();
        postCloneSelectorMapping.setAttributeName("postCloneSelector");
        postCloneSelectorMapping.setGetMethodName("getPostCloneSelector");
        postCloneSelectorMapping.setSetMethodName("setPostCloneSelector");
        postCloneSelectorMapping.setFieldName("post-clone-selector");
        descriptor.addMapping(postCloneSelectorMapping);

        DirectToFieldMapping postDeleteSelectorMapping = new DirectToFieldMapping();
        postDeleteSelectorMapping.setAttributeName("postDeleteSelector");
        postDeleteSelectorMapping.setGetMethodName("getPostDeleteSelector");
        postDeleteSelectorMapping.setSetMethodName("setPostDeleteSelector");
        postDeleteSelectorMapping.setFieldName("post-delete-selector");
        descriptor.addMapping(postDeleteSelectorMapping);

        DirectToFieldMapping postInsertSelectorMapping = new DirectToFieldMapping();
        postInsertSelectorMapping.setAttributeName("postInsertSelector");
        postInsertSelectorMapping.setGetMethodName("getPostInsertSelector");
        postInsertSelectorMapping.setSetMethodName("setPostInsertSelector");
        postInsertSelectorMapping.setFieldName("post-insert-selector");
        descriptor.addMapping(postInsertSelectorMapping);

        DirectToFieldMapping postMergeSelectorMapping = new DirectToFieldMapping();
        postMergeSelectorMapping.setAttributeName("postMergeSelector");
        postMergeSelectorMapping.setGetMethodName("getPostMergeSelector");
        postMergeSelectorMapping.setSetMethodName("setPostMergeSelector");
        postMergeSelectorMapping.setFieldName("post-merge-selector");
        descriptor.addMapping(postMergeSelectorMapping);

        DirectToFieldMapping postUpdateSelectorMapping = new DirectToFieldMapping();
        postUpdateSelectorMapping.setAttributeName("postUpdateSelector");
        postUpdateSelectorMapping.setGetMethodName("getPostUpdateSelector");
        postUpdateSelectorMapping.setSetMethodName("setPostUpdateSelector");
        postUpdateSelectorMapping.setFieldName("post-update-selector");
        descriptor.addMapping(postUpdateSelectorMapping);

        DirectToFieldMapping preDeleteSelectorMapping = new DirectToFieldMapping();
        preDeleteSelectorMapping.setAttributeName("preDeleteSelector");
        preDeleteSelectorMapping.setGetMethodName("getPreDeleteSelector");
        preDeleteSelectorMapping.setSetMethodName("setPreDeleteSelector");
        preDeleteSelectorMapping.setFieldName("pre-delete-selector");
        descriptor.addMapping(preDeleteSelectorMapping);

        DirectToFieldMapping postWriteSelectorMapping = new DirectToFieldMapping();
        postWriteSelectorMapping.setAttributeName("postWriteSelector");
        postWriteSelectorMapping.setGetMethodName("getPostWriteSelector");
        postWriteSelectorMapping.setSetMethodName("setPostWriteSelector");
        postWriteSelectorMapping.setFieldName("post-write-selector");
        descriptor.addMapping(postWriteSelectorMapping);

        DirectToFieldMapping preInsertSelectorMapping = new DirectToFieldMapping();
        preInsertSelectorMapping.setAttributeName("preInsertSelector");
        preInsertSelectorMapping.setGetMethodName("getPreInsertSelector");
        preInsertSelectorMapping.setSetMethodName("setPreInsertSelector");
        preInsertSelectorMapping.setFieldName("pre-insert-selector");
        descriptor.addMapping(preInsertSelectorMapping);

        DirectToFieldMapping preUpdateSelectorMapping = new DirectToFieldMapping();
        preUpdateSelectorMapping.setAttributeName("preUpdateSelector");
        preUpdateSelectorMapping.setGetMethodName("getPreUpdateSelector");
        preUpdateSelectorMapping.setSetMethodName("setPreUpdateSelector");
        preUpdateSelectorMapping.setFieldName("pre-update-selector");
        descriptor.addMapping(preUpdateSelectorMapping);

        DirectToFieldMapping preWriteSelectorMapping = new DirectToFieldMapping();
        preWriteSelectorMapping.setAttributeName("preWriteSelector");
        preWriteSelectorMapping.setGetMethodName("getPreWriteSelector");
        preWriteSelectorMapping.setSetMethodName("setPreWriteSelector");
        preWriteSelectorMapping.setFieldName("pre-write-selector");
        descriptor.addMapping(preWriteSelectorMapping);

        return descriptor;
    }

    protected ClassDescriptor buildForeignReferenceMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(ForeignReferenceMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        DirectToFieldMapping referenceClassMapping = new DirectToFieldMapping();
        referenceClassMapping.setAttributeClassification(Class.class);
        referenceClassMapping.setAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClass");
        referenceClassMapping.setSetMethodName("setReferenceClass");
        referenceClassMapping.setFieldName("reference-class");
        descriptor.addMapping(referenceClassMapping);

        DirectToFieldMapping relationshipPartnerAttributeNameMapping = new DirectToFieldMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setFieldName("relationship-partner-attribute-name");
        descriptor.addMapping(relationshipPartnerAttributeNameMapping);

        DirectToFieldMapping isPrivateOwnedMapping = new DirectToFieldMapping();
        isPrivateOwnedMapping.setAttributeName("isPrivateOwned");
        isPrivateOwnedMapping.setGetMethodName("isPrivateOwned");
        isPrivateOwnedMapping.setSetMethodName("setIsPrivateOwned");
        isPrivateOwnedMapping.setFieldName("is-private-owned");
        descriptor.addMapping(isPrivateOwnedMapping);

        DirectToFieldMapping usesBatchReadingMapping = new DirectToFieldMapping();
        usesBatchReadingMapping.setAttributeName("usesBatchReading");
        usesBatchReadingMapping.setGetMethodName("shouldUseBatchReading");
        usesBatchReadingMapping.setSetMethodName("setUsesBatchReading");
        usesBatchReadingMapping.setFieldName("uses-batch-reading");
        descriptor.addMapping(usesBatchReadingMapping);

        SDKAggregateObjectMapping indirectionPolicyMapping = new SDKAggregateObjectMapping();
        indirectionPolicyMapping.setAttributeName("indirectionPolicy");
        indirectionPolicyMapping.setGetMethodName("getIndirectionPolicy");
        indirectionPolicyMapping.setSetMethodName("setIndirectionPolicy");
        indirectionPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.indirection.IndirectionPolicy.class);
        indirectionPolicyMapping.setFieldName("indirection-policy");
        descriptor.addMapping(indirectionPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.indirection.IndirectionPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.setRootElementName("mapping-indirection-policy");

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);

        return descriptor;
    }

    protected ClassDescriptor buildInheritancePolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.descriptors.InheritancePolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.setRootElementName("descriptor-inheritance-policy");

        DirectToFieldMapping parentClassMapping = new DirectToFieldMapping();
        parentClassMapping.setAttributeClassification(Class.class);
        parentClassMapping.setAttributeName("parentClass");
        parentClassMapping.setGetMethodName("getParentClass");
        parentClassMapping.setSetMethodName("setParentClass");
        parentClassMapping.setFieldName("parent-class");
        descriptor.addMapping(parentClassMapping);

        DirectToFieldMapping shouldReadSubclassesMapping = new DirectToFieldMapping();
        shouldReadSubclassesMapping.setAttributeName("shouldReadSubclasses");
        shouldReadSubclassesMapping.setGetMethodName("shouldReadSubclassesValue");
        shouldReadSubclassesMapping.setSetMethodName("setShouldReadSubclasses");
        shouldReadSubclassesMapping.setFieldName("should-read-subclasses");
        descriptor.addMapping(shouldReadSubclassesMapping);

        DirectToFieldMapping readAllSubclassesViewMapping = new DirectToFieldMapping();
        readAllSubclassesViewMapping.setAttributeName("readAllSubclassesView");
        readAllSubclassesViewMapping.setGetMethodName("getReadAllSubclassesViewName");
        readAllSubclassesViewMapping.setSetMethodName("setReadAllSubclassesViewName");
        readAllSubclassesViewMapping.setFieldName("read-all-subclasses-view");
        descriptor.addMapping(readAllSubclassesViewMapping);

        DirectToFieldMapping shouldUseClassNameAsIndicatorMapping = new DirectToFieldMapping();
        shouldUseClassNameAsIndicatorMapping.setAttributeName("shouldUseClassNameAsIndicator");
        shouldUseClassNameAsIndicatorMapping.setGetMethodName("shouldUseClassNameAsIndicator");
        shouldUseClassNameAsIndicatorMapping.setSetMethodName("setShouldUseClassNameAsIndicator");
        shouldUseClassNameAsIndicatorMapping.setFieldName("should-use-class-name-as-indicator");
        descriptor.addMapping(shouldUseClassNameAsIndicatorMapping);

        DirectToFieldMapping classExtractionMethodMapping = new DirectToFieldMapping();
        classExtractionMethodMapping.setAttributeName("classExtractionMethod");
        classExtractionMethodMapping.setGetMethodName("getClassExtractionMethodName");
        classExtractionMethodMapping.setSetMethodName("setClassExtractionMethodName");
        classExtractionMethodMapping.setFieldName("class-extraction-method");
        descriptor.addMapping(classExtractionMethodMapping);

        DirectToFieldMapping classIndicatorFieldNameMapping = new DirectToFieldMapping();
        classIndicatorFieldNameMapping.setAttributeName("classIndicatorFieldName");
        classIndicatorFieldNameMapping.setGetMethodName("getClassIndicatorFieldName");
        classIndicatorFieldNameMapping.setSetMethodName("setClassIndicatorFieldName");
        classIndicatorFieldNameMapping.setFieldName("class-indicator-field");
        descriptor.addMapping(classIndicatorFieldNameMapping);

        SDKAggregateCollectionMapping classIndicatorsMapping = new SDKAggregateCollectionMapping();
        classIndicatorsMapping.setAttributeName("classIndicatorAssociations");
        classIndicatorsMapping.setGetMethodName("getClassIndicatorAssociations");
        classIndicatorsMapping.setSetMethodName("setClassIndicatorAssociations");
        classIndicatorsMapping.setFieldName("class-indicator-associations");
        classIndicatorsMapping.setReferenceClass(TypedAssociation.class);
        descriptor.addMapping(classIndicatorsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildInstantiationPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.descriptors.InstantiationPolicy.class);
        descriptor.setRootElementName("descriptor-instantiation-policy");
        descriptor.descriptorIsAggregate();

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);

        DirectToFieldMapping methodNameMapping = new DirectToFieldMapping();
        methodNameMapping.setAttributeName("methodName");
        methodNameMapping.setGetMethodName("getMethodName");
        methodNameMapping.setSetMethodName("setMethodName");
        methodNameMapping.setFieldName("method");
        descriptor.addMapping(methodNameMapping);

        DirectToFieldMapping factoryClassMapping = new DirectToFieldMapping();
        factoryClassMapping.setAttributeClassification(Class.class);
        factoryClassMapping.setAttributeName("factoryClass");
        factoryClassMapping.setGetMethodName("getFactoryClass");
        factoryClassMapping.setSetMethodName("setFactoryClass");
        factoryClassMapping.setFieldName("factory-class");
        descriptor.addMapping(factoryClassMapping);

        DirectToFieldMapping factoryMethodNameMapping = new DirectToFieldMapping();
        factoryMethodNameMapping.setAttributeName("factoryMethod");
        factoryMethodNameMapping.setGetMethodName("getFactoryMethodName");
        factoryMethodNameMapping.setSetMethodName("setFactoryMethodName");
        factoryMethodNameMapping.setFieldName("factory-method");
        descriptor.addMapping(factoryMethodNameMapping);
        return descriptor;
    }

    protected ClassDescriptor buildInterfaceContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.queries.InterfaceContainerPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.queries.ContainerPolicy.class);

        DirectToFieldMapping keyMapping = new DirectToFieldMapping();
        keyMapping.setAttributeName("containerClass");
        keyMapping.setGetMethodName("getContainerClass");
        keyMapping.setSetMethodName("setContainerClass");
        keyMapping.setFieldName("container-class");
        descriptor.addMapping(keyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildInterfacePolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(InterfacePolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.setRootElementName("descriptor-interface-policy");

        DirectToFieldMapping implementorDescriptorMapping = new DirectToFieldMapping();
        implementorDescriptorMapping.setAttributeClassification(Class.class);
        implementorDescriptorMapping.setAttributeName("implementorDescriptor");
        implementorDescriptorMapping.setGetMethodName("getImplementorDescriptor");
        implementorDescriptorMapping.setSetMethodName("setImplementorDescriptor");
        implementorDescriptorMapping.setFieldName("implementor-descriptor");
        descriptor.addMapping(implementorDescriptorMapping);

        SDKDirectCollectionMapping parentInterfacesMapping = new SDKDirectCollectionMapping();
        parentInterfacesMapping.setAttributeElementClass(Class.class);
        parentInterfacesMapping.setAttributeName("parentInterfaces");
        parentInterfacesMapping.setGetMethodName("getParentInterfaces");
        parentInterfacesMapping.setSetMethodName("setParentInterfaces");
        parentInterfacesMapping.setFieldName("parent-interfaces");
        descriptor.addMapping(parentInterfacesMapping);

        return descriptor;
    }

    protected ClassDescriptor buildListContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.queries.ListContainerPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.queries.CollectionContainerPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildManyToManyMappingMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(ManyToManyMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        DirectToFieldMapping relationTableMapping = new DirectToFieldMapping();
        relationTableMapping.setAttributeName("relationTableName");
        //CR#2407  Call getRelationTableQualifiedName that includes table qualifier.
        relationTableMapping.setGetMethodName("getRelationTableQualifiedName");
        relationTableMapping.setSetMethodName("setRelationTableName");
        relationTableMapping.setFieldName("relation-table");
        descriptor.addMapping(relationTableMapping);

        SDKDirectCollectionMapping sourceKeyFieldNamesMapping = new SDKDirectCollectionMapping();
        sourceKeyFieldNamesMapping.setAttributeName("sourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setGetMethodName("getSourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setSetMethodName("setSourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setFieldName("source-key-fields");
        sourceKeyFieldNamesMapping.setElementDataTypeName("field");
        descriptor.addMapping(sourceKeyFieldNamesMapping);

        SDKDirectCollectionMapping sourceRelationKeyFieldsMapping = new SDKDirectCollectionMapping();
        sourceRelationKeyFieldsMapping.setAttributeName("sourceRelationKeyFieldsNames");
        sourceRelationKeyFieldsMapping.setGetMethodName("getSourceRelationKeyFieldNames");
        sourceRelationKeyFieldsMapping.setSetMethodName("setSourceRelationKeyFieldNames");
        sourceRelationKeyFieldsMapping.setFieldName("source-relation-key-fields");
        sourceRelationKeyFieldsMapping.setElementDataTypeName("field");
        descriptor.addMapping(sourceRelationKeyFieldsMapping);

        SDKDirectCollectionMapping targetKeyFieldNamesMapping = new SDKDirectCollectionMapping();
        targetKeyFieldNamesMapping.setAttributeName("targetKeyFieldNames");
        targetKeyFieldNamesMapping.setGetMethodName("getTargetKeyFieldNames");
        targetKeyFieldNamesMapping.setSetMethodName("setTargetKeyFieldNames");
        targetKeyFieldNamesMapping.setFieldName("target-key-fields");
        targetKeyFieldNamesMapping.setElementDataTypeName("field");
        descriptor.addMapping(targetKeyFieldNamesMapping);

        SDKDirectCollectionMapping targetRelationKeyFieldsMapping = new SDKDirectCollectionMapping();
        targetRelationKeyFieldsMapping.setAttributeName("targetRelationKeyFieldsNames");
        targetRelationKeyFieldsMapping.setGetMethodName("getTargetRelationKeyFieldNames");
        targetRelationKeyFieldsMapping.setSetMethodName("setTargetRelationKeyFieldNames");
        targetRelationKeyFieldsMapping.setFieldName("target-relation-key-fields");
        targetRelationKeyFieldsMapping.setElementDataTypeName("field");
        descriptor.addMapping(targetRelationKeyFieldsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildMapContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.queries.MapContainerPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.queries.InterfaceContainerPolicy.class);

        DirectToFieldMapping keyMapping = new DirectToFieldMapping();
        keyMapping.setAttributeName("keyName");
        keyMapping.setGetMethodName("getKeyName");
        keyMapping.setSetMethodName("setKeyName");
        keyMapping.setFieldName("key-method");
        descriptor.addMapping(keyMapping);

        DirectToFieldMapping elementClassMapping = new DirectToFieldMapping();
        elementClassMapping.setAttributeName("elementClass");
        elementClassMapping.setGetMethodName("getElementClass");
        elementClassMapping.setSetMethodName("setElementClass");
        elementClassMapping.setFieldName("element-class");
        descriptor.addMapping(elementClassMapping);

        return descriptor;
    }

    protected ClassDescriptor buildNestedTableMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.mappings.structures.NestedTableMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        DirectToFieldMapping fieldMapping = new DirectToFieldMapping();
        fieldMapping.setAttributeName("fieldName");
        fieldMapping.setGetMethodName("getFieldName");
        fieldMapping.setSetMethodName("setFieldName");
        fieldMapping.setFieldName("field");
        descriptor.addMapping(fieldMapping);

        DirectToFieldMapping structureMapping = new DirectToFieldMapping();
        structureMapping.setAttributeName("structureName");
        structureMapping.setGetMethodName("getStructureName");
        structureMapping.setSetMethodName("setStructureName");
        structureMapping.setFieldName("structure");
        descriptor.addMapping(structureMapping);

        return descriptor;
    }

    protected ClassDescriptor buildNoIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.indirection.NoIndirectionPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.indirection.IndirectionPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildObjectArrayMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.mappings.structures.ObjectArrayMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(SDKAggregateCollectionMapping.class);

        DirectToFieldMapping structureMapping = new DirectToFieldMapping();
        structureMapping.setAttributeName("structureName");
        structureMapping.setGetMethodName("getStructureName");
        structureMapping.setSetMethodName("setStructureName");
        structureMapping.setFieldName("structure");
        descriptor.addMapping(structureMapping);

        return descriptor;
    }

    protected ClassDescriptor buildObjectReferenceMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(ObjectReferenceMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(ForeignReferenceMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildObjectRelationalDataTypeDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(ClassDescriptor.class);

        DirectToFieldMapping structureMapping = new DirectToFieldMapping();
        structureMapping.setAttributeName("structureName");
        structureMapping.setGetMethodName("getStructureName");
        structureMapping.setSetMethodName("setStructureName");
        structureMapping.setFieldName("structure");
        descriptor.addMapping(structureMapping);

        return descriptor;
    }

    protected ClassDescriptor buildObjectTypeMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(ObjectTypeMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(DirectToFieldMapping.class);

        DirectToFieldMapping directtofieldmapping4 = new DirectToFieldMapping();
        directtofieldmapping4.setAttributeName("defaultAttributeValue");
        directtofieldmapping4.setGetMethodName("getDefaultAttributeValue");
        directtofieldmapping4.setSetMethodName("setDefaultAttributeValue");
        directtofieldmapping4.setFieldName("default-attribute-value");
        descriptor.addMapping(directtofieldmapping4);

        SDKAggregateCollectionMapping fieldToAttributeValueAssociationsMapping = new SDKAggregateCollectionMapping();
        fieldToAttributeValueAssociationsMapping.setAttributeName("fieldToAttributeValueAssociations");
        fieldToAttributeValueAssociationsMapping.setGetMethodName("getFieldToAttributeValueAssociations");
        fieldToAttributeValueAssociationsMapping.setSetMethodName("setFieldToAttributeValueAssociations");
        fieldToAttributeValueAssociationsMapping.setFieldName("field-to-attribute-value-associations");
        fieldToAttributeValueAssociationsMapping.setReferenceClass(TypedAssociation.class);
        descriptor.addMapping(fieldToAttributeValueAssociationsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildOneToManyMappingMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(OneToManyMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        SDKDirectCollectionMapping sourceKeyFieldNamesMapping = new SDKDirectCollectionMapping();
        sourceKeyFieldNamesMapping.setAttributeName("sourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setGetMethodName("getSourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setSetMethodName("setSourceKeyFieldNames");
        sourceKeyFieldNamesMapping.setFieldName("source-key-fields");
        sourceKeyFieldNamesMapping.setElementDataTypeName("field");
        descriptor.addMapping(sourceKeyFieldNamesMapping);

        SDKDirectCollectionMapping targetForeignKeyFieldsMapping = new SDKDirectCollectionMapping();
        targetForeignKeyFieldsMapping.setAttributeName("targetForeignKeyFieldNames");
        targetForeignKeyFieldsMapping.setGetMethodName("getTargetForeignKeyFieldNames");
        targetForeignKeyFieldsMapping.setSetMethodName("setTargetForeignKeyFieldNames");
        targetForeignKeyFieldsMapping.setFieldName("target-foreign-key-fields");
        targetForeignKeyFieldsMapping.setElementDataTypeName("field");
        descriptor.addMapping(targetForeignKeyFieldsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildOneToOneMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(OneToOneMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(ObjectReferenceMapping.class);

        DirectToFieldMapping usesJoiningMapping = new DirectToFieldMapping();
        usesJoiningMapping.setAttributeName("usesJoiningMapping");
        usesJoiningMapping.setGetMethodName("shouldUseJoining");
        usesJoiningMapping.setSetMethodName("setUsesJoining");
        usesJoiningMapping.setFieldName("uses-joining");
        descriptor.addMapping(usesJoiningMapping);

        SDKDirectCollectionMapping foreignKeyFieldNamesMapping = new SDKDirectCollectionMapping();
        foreignKeyFieldNamesMapping.setAttributeName("foreignKeyFieldNames");
        foreignKeyFieldNamesMapping.setGetMethodName("getForeignKeyFieldNames");
        foreignKeyFieldNamesMapping.setSetMethodName("setForeignKeyFieldNames");
        foreignKeyFieldNamesMapping.setFieldName("foreign-key-fields");
        foreignKeyFieldNamesMapping.setElementDataTypeName("field");
        descriptor.addMapping(foreignKeyFieldNamesMapping);

        SDKAggregateCollectionMapping sourceToTargetKeyFieldAssociationsMapping = new SDKAggregateCollectionMapping();
        sourceToTargetKeyFieldAssociationsMapping.setAttributeName("sourceToTargetKeyFieldAssociations");
        sourceToTargetKeyFieldAssociationsMapping.setGetMethodName("getSourceToTargetKeyFieldAssociations");
        sourceToTargetKeyFieldAssociationsMapping.setSetMethodName("setSourceToTargetKeyFieldAssociations");
        sourceToTargetKeyFieldAssociationsMapping.setFieldName("source-to-target-key-field-associations");
        sourceToTargetKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        descriptor.addMapping(sourceToTargetKeyFieldAssociationsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildProjectDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(Project.class);
        descriptor.setRootElementName("project");
        descriptor.setPrimaryKeyElementName("project-name");

        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("name");
        directtofieldmapping.setFieldName("project-name");
        directtofieldmapping.setSetMethodName("setName");
        directtofieldmapping.setGetMethodName("getName");
        descriptor.addMapping(directtofieldmapping);

        SDKAggregateObjectMapping loginMapping = new SDKAggregateObjectMapping();
        loginMapping.setAttributeName("login");
        loginMapping.setGetMethodName("getDatasourceLogin");
        loginMapping.setSetMethodName("setDatasourceLogin");
        loginMapping.setReferenceClass(DatabaseLogin.class);
        loginMapping.setFieldName("login");
        descriptor.addMapping(loginMapping);

        SDKDirectCollectionMapping defaultReadOnlyClassesMapping = new SDKDirectCollectionMapping();
        defaultReadOnlyClassesMapping.setAttributeName("defaultReadOnlyClasses");
        defaultReadOnlyClassesMapping.setGetMethodName("getDefaultReadOnlyClasses");
        defaultReadOnlyClassesMapping.setSetMethodName("setDefaultReadOnlyClasses");
        defaultReadOnlyClassesMapping.setFieldName("default-read-only-classes");
        defaultReadOnlyClassesMapping.setElementDataTypeName("class");
        defaultReadOnlyClassesMapping.setAttributeElementClass(Class.class);
        descriptor.addMapping(defaultReadOnlyClassesMapping);

        SDKAggregateCollectionMapping aggregateCollectionMapping = new SDKAggregateCollectionMapping();
        aggregateCollectionMapping.setAttributeName("descriptors");
        aggregateCollectionMapping.setReferenceClass(ClassDescriptor.class);
        aggregateCollectionMapping.setFieldName("descriptors");
        aggregateCollectionMapping.setSetMethodName("setOrderedDescriptors");
        aggregateCollectionMapping.setGetMethodName("getOrderedDescriptors");
        descriptor.addMapping(aggregateCollectionMapping);

        return descriptor;
    }

    protected ClassDescriptor buildProxyIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.indirection.IndirectionPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildQueryKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.mappings.querykeys.QueryKey.class);
        descriptor.setRootElementName("query-key");
        descriptor.descriptorIsAggregate();

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setFieldName("name");
        descriptor.addMapping(nameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildQueryManagerDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.descriptors.DescriptorQueryManager.class);
        descriptor.setRootElementName("descriptor-query-manager");
        descriptor.descriptorIsAggregate();

        DirectToFieldMapping existenceCheckMapping = new DirectToFieldMapping();
        existenceCheckMapping.setAttributeName("existenceCheck");
        existenceCheckMapping.setGetMethodName("getExistenceCheck");
        existenceCheckMapping.setSetMethodName("setExistenceCheck");
        existenceCheckMapping.setFieldName("existence-check");
        descriptor.addMapping(existenceCheckMapping);

        DirectToFieldMapping readObjectSQLStringMapping = new DirectToFieldMapping();
        readObjectSQLStringMapping.setAttributeName("readObjectSQLString");
        readObjectSQLStringMapping.setGetMethodName("getReadObjectSQLString");
        readObjectSQLStringMapping.setSetMethodName("setReadObjectSQLString");
        readObjectSQLStringMapping.setFieldName("read-object-sql");
        readObjectSQLStringMapping.readOnly();
        descriptor.addMapping(readObjectSQLStringMapping);

        DirectToFieldMapping readAllSQLStringMapping = new DirectToFieldMapping();
        readAllSQLStringMapping.setAttributeName("readAllSQLString");
        readAllSQLStringMapping.setGetMethodName("getReadAllSQLString");
        readAllSQLStringMapping.setSetMethodName("setReadAllSQLString");
        readAllSQLStringMapping.setFieldName("read-all-sql");
        readAllSQLStringMapping.readOnly();
        descriptor.addMapping(readAllSQLStringMapping);

        DirectToFieldMapping insertSQLStringMapping = new DirectToFieldMapping();
        insertSQLStringMapping.setAttributeName("insertSQLString");
        insertSQLStringMapping.setGetMethodName("getInsertSQLString");
        insertSQLStringMapping.setSetMethodName("setInsertSQLString");
        insertSQLStringMapping.setFieldName("insert-sql");
        insertSQLStringMapping.readOnly();
        descriptor.addMapping(insertSQLStringMapping);

        DirectToFieldMapping updateSQLStringMapping = new DirectToFieldMapping();
        updateSQLStringMapping.setAttributeName("updateSQLString");
        updateSQLStringMapping.setGetMethodName("getUpdateSQLString");
        updateSQLStringMapping.setSetMethodName("setUpdateSQLString");
        updateSQLStringMapping.setFieldName("update-sql");
        updateSQLStringMapping.readOnly();
        descriptor.addMapping(updateSQLStringMapping);

        DirectToFieldMapping deleteSQLStringMapping = new DirectToFieldMapping();
        deleteSQLStringMapping.setAttributeName("deleteSQLString");
        deleteSQLStringMapping.setGetMethodName("getDeleteSQLString");
        deleteSQLStringMapping.setSetMethodName("setDeleteSQLString");
        deleteSQLStringMapping.setFieldName("delete-sql");
        deleteSQLStringMapping.readOnly();
        descriptor.addMapping(deleteSQLStringMapping);

        DirectToFieldMapping doesExistSQLStringMapping = new DirectToFieldMapping();
        doesExistSQLStringMapping.setAttributeName("doesExistSQLString");
        doesExistSQLStringMapping.setGetMethodName("getDoesExistSQLString");
        doesExistSQLStringMapping.setSetMethodName("setDoesExistSQLString");
        doesExistSQLStringMapping.setFieldName("does-exist-sql");
        doesExistSQLStringMapping.readOnly();
        descriptor.addMapping(doesExistSQLStringMapping);

        SDKAggregateObjectMapping readObjectCallMapping = new SDKAggregateObjectMapping();
        readObjectCallMapping.setAttributeName("readObjectCall");
        readObjectCallMapping.setGetMethodName("getReadObjectCall");
        readObjectCallMapping.setSetMethodName("setReadObjectCall");
        readObjectCallMapping.setFieldName("read-object-call");
        readObjectCallMapping.setReferenceClass(Call.class);
        descriptor.addMapping(readObjectCallMapping);

        SDKAggregateObjectMapping readAllCallMapping = new SDKAggregateObjectMapping();
        readAllCallMapping.setAttributeName("readAllCall");
        readAllCallMapping.setGetMethodName("getReadAllCall");
        readAllCallMapping.setSetMethodName("setReadAllCall");
        readAllCallMapping.setFieldName("read-all-call");
        readAllCallMapping.setReferenceClass(Call.class);
        descriptor.addMapping(readAllCallMapping);

        SDKAggregateObjectMapping insertCallMapping = new SDKAggregateObjectMapping();
        insertCallMapping.setAttributeName("insertCall");
        insertCallMapping.setGetMethodName("getInsertCall");
        insertCallMapping.setSetMethodName("setInsertCall");
        insertCallMapping.setFieldName("insert-call");
        insertCallMapping.setReferenceClass(Call.class);
        descriptor.addMapping(insertCallMapping);

        SDKAggregateObjectMapping updateCallMapping = new SDKAggregateObjectMapping();
        updateCallMapping.setAttributeName("updateCall");
        updateCallMapping.setGetMethodName("getUpdateCall");
        updateCallMapping.setSetMethodName("setUpdateCall");
        updateCallMapping.setFieldName("update-sql");
        updateCallMapping.setReferenceClass(Call.class);
        descriptor.addMapping(updateCallMapping);

        SDKAggregateObjectMapping deleteCallMapping = new SDKAggregateObjectMapping();
        deleteCallMapping.setAttributeName("deleteCall");
        deleteCallMapping.setGetMethodName("getDeleteCall");
        deleteCallMapping.setSetMethodName("setDeleteCall");
        deleteCallMapping.setFieldName("delete-call");
        deleteCallMapping.setReferenceClass(Call.class);
        descriptor.addMapping(deleteCallMapping);

        SDKAggregateObjectMapping doesExistCallMapping = new SDKAggregateObjectMapping();
        doesExistCallMapping.setAttributeName("doesExistCall");
        doesExistCallMapping.setGetMethodName("getDoesExistCall");
        doesExistCallMapping.setSetMethodName("setDoesExistCall");
        doesExistCallMapping.setFieldName("does-exist-call");
        doesExistCallMapping.setReferenceClass(Call.class);
        descriptor.addMapping(doesExistCallMapping);

        SDKAggregateCollectionMapping namedQueriesMapping = new SDKAggregateCollectionMapping();
        namedQueriesMapping.setReferenceClass(org.eclipse.persistence.queries.DatabaseQuery.class);
        namedQueriesMapping.useCollectionClass(Vector.class);
        namedQueriesMapping.setAttributeName("queries");
        namedQueriesMapping.setGetMethodName("getAllQueries");
        namedQueriesMapping.setSetMethodName("setAllQueries");
        namedQueriesMapping.setFieldName("descriptor-named-queries");
        descriptor.addMapping(namedQueriesMapping);

        return descriptor;
    }

    protected ClassDescriptor buildReferenceMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.mappings.structures.ReferenceMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(ObjectReferenceMapping.class);

        DirectToFieldMapping fieldMapping = new DirectToFieldMapping();
        fieldMapping.setAttributeName("fieldName");
        fieldMapping.setGetMethodName("getFieldName");
        fieldMapping.setSetMethodName("setFieldName");
        fieldMapping.setFieldName("field");
        descriptor.addMapping(fieldMapping);

        return descriptor;
    }

    protected ClassDescriptor buildSDKAggregateCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(SDKAggregateCollectionMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(AggregateMapping.class);

        DirectToFieldMapping fieldMapping = new DirectToFieldMapping();
        fieldMapping.setAttributeName("fieldName");
        fieldMapping.setGetMethodName("getFieldName");
        fieldMapping.setSetMethodName("setFieldName");
        fieldMapping.setFieldName("field");
        descriptor.addMapping(fieldMapping);

        SDKAggregateObjectMapping containerPolicyMapping = new SDKAggregateObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.queries.ContainerPolicy.class);
        containerPolicyMapping.setFieldName("container-policy");
        descriptor.addMapping(containerPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildSDKAggregateObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(SDKAggregateObjectMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(AggregateMapping.class);

        DirectToFieldMapping fieldMapping = new DirectToFieldMapping();
        fieldMapping.setAttributeName("fieldName");
        fieldMapping.setGetMethodName("getFieldName");
        fieldMapping.setSetMethodName("setFieldName");
        fieldMapping.setFieldName("field");
        descriptor.addMapping(fieldMapping);

        return descriptor;
    }

    protected ClassDescriptor buildSDKDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(SDKDescriptor.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(ClassDescriptor.class);

        return descriptor;
    }

    protected ClassDescriptor buildSDKDirectCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(SDKDirectCollectionMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        DirectToFieldMapping fieldMapping = new DirectToFieldMapping();
        fieldMapping.setAttributeName("fieldName");
        fieldMapping.setGetMethodName("getFieldName");
        fieldMapping.setSetMethodName("setFieldName");
        fieldMapping.setFieldName("field");
        descriptor.addMapping(fieldMapping);

        DirectToFieldMapping elementDataTypeNameMapping = new DirectToFieldMapping();
        elementDataTypeNameMapping.setAttributeName("elementDataTypeName");
        elementDataTypeNameMapping.setGetMethodName("getElementDataTypeName");
        elementDataTypeNameMapping.setSetMethodName("setElementDataTypeName");
        elementDataTypeNameMapping.setFieldName("element-data-type");
        descriptor.addMapping(elementDataTypeNameMapping);

        DirectToFieldMapping fieldElementClassificationMapping = new DirectToFieldMapping();
        fieldElementClassificationMapping.setAttributeName("fieldElementClass");
        fieldElementClassificationMapping.setGetMethodName("getFieldElementClass");
        fieldElementClassificationMapping.setSetMethodName("setFieldElementClass");
        fieldElementClassificationMapping.setFieldName("field-element-class");
        descriptor.addMapping(fieldElementClassificationMapping);

        DirectToFieldMapping attributeElementClassificationMapping = new DirectToFieldMapping();
        attributeElementClassificationMapping.setAttributeName("attributeElementClass");
        attributeElementClassificationMapping.setGetMethodName("getAttributeElementClass");
        attributeElementClassificationMapping.setSetMethodName("setAttributeElementClass");
        attributeElementClassificationMapping.setFieldName("attribute-element-class");
        descriptor.addMapping(attributeElementClassificationMapping);

        SDKAggregateObjectMapping containerPolicyMapping = new SDKAggregateObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.queries.ContainerPolicy.class);
        containerPolicyMapping.setFieldName("container-policy");
        descriptor.addMapping(containerPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildSerializedObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(SerializedObjectMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(DirectToFieldMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildStructureMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.mappings.structures.StructureMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(SDKAggregateObjectMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildTimestmapLockingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(TimestampLockingPolicy.class);
        descriptor.descriptorIsAggregate();

        descriptor.getInheritancePolicy().setParentClass(VersionLockingPolicy.class);

        DirectToFieldMapping usesServerTimeMapping = new DirectToFieldMapping();
        usesServerTimeMapping.setAttributeName("usesServerTime");
        usesServerTimeMapping.setGetMethodName("usesServerTime");
        usesServerTimeMapping.setSetMethodName("setUsesServerTime");
        usesServerTimeMapping.setFieldName("uses-server-time");
        descriptor.addMapping(usesServerTimeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildTransformationMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(TransformationMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        DirectToFieldMapping attributeMethodNameMapping = new DirectToFieldMapping();
        attributeMethodNameMapping.setAttributeName("attributeMethodName");
        attributeMethodNameMapping.setGetMethodName("getAttributeMethodName");
        attributeMethodNameMapping.setSetMethodName("setAttributeMethodName");
        attributeMethodNameMapping.setFieldName("attribute-method");
        descriptor.addMapping(attributeMethodNameMapping);

        SDKAggregateObjectMapping indirectionPolicyMapping = new SDKAggregateObjectMapping();
        indirectionPolicyMapping.setAttributeName("indirectionPolicy");
        indirectionPolicyMapping.setGetMethodName("getIndirectionPolicy");
        indirectionPolicyMapping.setSetMethodName("setIndirectionPolicy");
        indirectionPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.indirection.IndirectionPolicy.class);
        indirectionPolicyMapping.setFieldName("indirection-policy");
        descriptor.addMapping(indirectionPolicyMapping);

        SDKAggregateCollectionMapping fieldNameToMethodNameAssociationsMapping = new SDKAggregateCollectionMapping();
        fieldNameToMethodNameAssociationsMapping.setAttributeName("fieldNameToMethodNameAssociations");
        fieldNameToMethodNameAssociationsMapping.setGetMethodName("getFieldNameToMethodNameAssociations");
        fieldNameToMethodNameAssociationsMapping.setSetMethodName("setFieldNameToMethodNameAssociations");
        fieldNameToMethodNameAssociationsMapping.setFieldName("field-to-method-associations");
        fieldNameToMethodNameAssociationsMapping.setReferenceClass(Association.class);
        descriptor.addMapping(fieldNameToMethodNameAssociationsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildTransparentIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.indirection.IndirectionPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildTypeConversionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(TypeConversionMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(DirectToFieldMapping.class);

        DirectToFieldMapping directtofieldmapping = new DirectToFieldMapping();
        directtofieldmapping.setAttributeName("attributeClassification");
        directtofieldmapping.setGetMethodName("getAttributeClassification");
        directtofieldmapping.setSetMethodName("setAttributeClassification");
        directtofieldmapping.setFieldName("attribute-classification");
        descriptor.addMapping(directtofieldmapping);

        DirectToFieldMapping directtofieldmapping2 = new DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("fieldClassification");
        directtofieldmapping2.setGetMethodName("getFieldClassification");
        directtofieldmapping2.setSetMethodName("setFieldClassification");
        directtofieldmapping2.setFieldName("field-classification");
        descriptor.addMapping(directtofieldmapping2);

        return descriptor;
    }

    protected ClassDescriptor buildTypedAssociationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(TypedAssociation.class);
        descriptor.descriptorIsAggregate();
        descriptor.setRootElementName("typed-association");

        descriptor.getEventManager().setPostBuildSelector("postBuild");

        DirectToFieldMapping keyMapping = new DirectToFieldMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setFieldName("association-key");
        descriptor.addMapping(keyMapping);

        DirectToFieldMapping valueMapping = new DirectToFieldMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setFieldName("association-value");
        descriptor.addMapping(valueMapping);

        DirectToFieldMapping keyTypeMapping = new DirectToFieldMapping();
        keyTypeMapping.setAttributeName("keyType");
        keyTypeMapping.setGetMethodName("getKeyType");
        keyTypeMapping.setSetMethodName("setKeyType");
        keyTypeMapping.setFieldName("association-key-type");
        descriptor.addMapping(keyTypeMapping);

        DirectToFieldMapping valueTypeMapping = new DirectToFieldMapping();
        valueTypeMapping.setAttributeName("valueType");
        valueTypeMapping.setGetMethodName("getValueType");
        valueTypeMapping.setSetMethodName("setValueType");
        valueTypeMapping.setFieldName("association-value-type");
        descriptor.addMapping(valueTypeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildVariableOneToOneMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(VariableOneToOneMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(ObjectReferenceMapping.class);

        DirectToFieldMapping typeFieldMapping = new DirectToFieldMapping();
        typeFieldMapping.setAttributeName("typeFieldName");
        typeFieldMapping.setGetMethodName("getTypeFieldName");
        typeFieldMapping.setSetMethodName("setTypeFieldName");
        typeFieldMapping.setFieldName("type-field");
        descriptor.addMapping(typeFieldMapping);

        SDKDirectCollectionMapping foreignKeyFieldNamesMapping = new SDKDirectCollectionMapping();
        foreignKeyFieldNamesMapping.setAttributeName("foreignKeyFieldNames");
        foreignKeyFieldNamesMapping.setGetMethodName("getForeignKeyFieldNames");
        foreignKeyFieldNamesMapping.setSetMethodName("setForeignKeyFieldNames");
        foreignKeyFieldNamesMapping.setFieldName("foreign-key-fields");
        foreignKeyFieldNamesMapping.setElementDataTypeName("field");
        descriptor.addMapping(foreignKeyFieldNamesMapping);

        SDKAggregateCollectionMapping sourceToTargetQueryKeyFieldAssociationsMapping = new SDKAggregateCollectionMapping();
        sourceToTargetQueryKeyFieldAssociationsMapping.setAttributeName("sourceToTargetQueryKeyFieldAssociations");
        sourceToTargetQueryKeyFieldAssociationsMapping.setGetMethodName("getSourceToTargetQueryKeyFieldAssociations");
        sourceToTargetQueryKeyFieldAssociationsMapping.setSetMethodName("setSourceToTargetQueryKeyFieldAssociations");
        sourceToTargetQueryKeyFieldAssociationsMapping.setFieldName("source-to-target-query-key-field-associations");
        sourceToTargetQueryKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        descriptor.addMapping(sourceToTargetQueryKeyFieldAssociationsMapping);

        SDKAggregateCollectionMapping classIndicatorsMapping = new SDKAggregateCollectionMapping();
        classIndicatorsMapping.setAttributeName("classIndicatorAssociations");
        classIndicatorsMapping.setGetMethodName("getClassIndicatorAssociations");
        classIndicatorsMapping.setSetMethodName("setClassIndicatorAssociations");
        classIndicatorsMapping.setFieldName("class-indicator-associations");
        classIndicatorsMapping.setReferenceClass(TypedAssociation.class);
        descriptor.addMapping(classIndicatorsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildVersionLockingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(VersionLockingPolicy.class);
        descriptor.descriptorIsAggregate();

        descriptor.setRootElementName("descriptor-locking-policy");

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("type");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);

        DirectToFieldMapping versionFieldMapping = new DirectToFieldMapping();
        versionFieldMapping.setAttributeName("writeLockFieldName");
        versionFieldMapping.setGetMethodName("getWriteLockFieldName");
        versionFieldMapping.setSetMethodName("setWriteLockFieldName");
        versionFieldMapping.setFieldName("write-lock-field");
        descriptor.addMapping(versionFieldMapping);

        DirectToFieldMapping shouldStoreInCacheMapping = new DirectToFieldMapping();
        shouldStoreInCacheMapping.setAttributeName("isStoredInCache");
        shouldStoreInCacheMapping.setGetMethodName("isStoredInCache");
        shouldStoreInCacheMapping.setSetMethodName("setIsStoredInCache");
        shouldStoreInCacheMapping.setFieldName("is-stored-in-cache");
        descriptor.addMapping(shouldStoreInCacheMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLDescriptor.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(SDKDescriptor.class);

        return descriptor;
    }
}
