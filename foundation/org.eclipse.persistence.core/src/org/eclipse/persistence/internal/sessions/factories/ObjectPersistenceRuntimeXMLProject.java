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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.internal.sessions.factories;

// javase imports
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

// Java extension imports
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.descriptors.AllFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.ChangedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.FetchGroupManager;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.InterfacePolicy;
import org.eclipse.persistence.descriptors.PessimisticLockingPolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.ReturningPolicy;
import org.eclipse.persistence.descriptors.SelectedFieldsLockingPolicy;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.copying.AbstractCopyPolicy;
import org.eclipse.persistence.descriptors.copying.CloneCopyPolicy;
import org.eclipse.persistence.descriptors.copying.InstantiationCopyPolicy;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.NoExpiryCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.eis.EISLogin;
//import org.eclipse.persistence.eis.adapters.xmlfile.XMLFileSequence;
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeDirectCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeObjectMapping;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.eis.mappings.EISOneToManyMapping;
import org.eclipse.persistence.eis.mappings.EISOneToOneMapping;
import org.eclipse.persistence.eis.mappings.EISTransformationMapping;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.history.HistoryPolicy;
import org.eclipse.persistence.internal.descriptors.FieldTransformation;
import org.eclipse.persistence.internal.descriptors.FieldTranslation;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.descriptors.MethodBasedFieldTransformation;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.descriptors.QueryArgument;
import org.eclipse.persistence.internal.descriptors.QueryKeyReference;
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.descriptors.TypeMapping;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.ExpressionOperatorConverter;
import org.eclipse.persistence.internal.expressions.FieldExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.LogicalExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.expressions.RelationExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.history.HistoricalDatabaseTable;
import org.eclipse.persistence.internal.identitymaps.CacheIdentityMap;
import org.eclipse.persistence.internal.identitymaps.FullIdentityMap;
import org.eclipse.persistence.internal.identitymaps.HardCacheWeakIdentityMap;
import org.eclipse.persistence.internal.identitymaps.NoIdentityMap;
import org.eclipse.persistence.internal.identitymaps.SoftCacheWeakIdentityMap;
import org.eclipse.persistence.internal.identitymaps.WeakIdentityMap;
import org.eclipse.persistence.internal.indirection.BasicIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.ContainerIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.indirection.NoIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.internal.indirection.TransparentIndirectionPolicy;
import org.eclipse.persistence.internal.oxm.Namespace;
import org.eclipse.persistence.internal.oxm.QNameInheritancePolicy;
import org.eclipse.persistence.internal.oxm.XMLConversionPair;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.InterfaceContainerPolicy;
import org.eclipse.persistence.internal.queries.ListContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.queries.MappedKeyMapContainerPolicy;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.PropertyAssociation;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.TypedAssociation;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.mappings.converters.ClassInstanceConverter;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.querykeys.DirectQueryKey;
import org.eclipse.persistence.mappings.querykeys.ForeignReferenceQueryKey;
import org.eclipse.persistence.mappings.querykeys.OneToManyQueryKey;
import org.eclipse.persistence.mappings.querykeys.OneToOneQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.NestedTableMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDataTypeDescriptor;
import org.eclipse.persistence.mappings.structures.ReferenceMapping;
import org.eclipse.persistence.mappings.structures.StructureMapping;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaFileReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteAllQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.DirectReadQuery;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.InsertObjectQuery;
import org.eclipse.persistence.queries.JPQLCall;
import org.eclipse.persistence.queries.MethodBaseQueryRedirector;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.queries.UpdateObjectQuery;
import org.eclipse.persistence.queries.ValueReadQuery;
import org.eclipse.persistence.sequencing.DefaultSequence;
import org.eclipse.persistence.sequencing.NativeSequence;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.TableSequence;
import org.eclipse.persistence.sequencing.UnaryTableSequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import static org.eclipse.persistence.sessions.factories.XMLProjectReader.SCHEMA_DIR;
import static org.eclipse.persistence.sessions.factories.XMLProjectReader.TOPLINK_10_SCHEMA;

/**
 * INTERNAL: Define the TopLink OX project and descriptor information to read a OracleAS TopLink 10<i>g</i> (10.0.3) project from an XML file. Note any changes must be reflected in the OPM XML schema.
 */
public class ObjectPersistenceRuntimeXMLProject extends NamespaceResolvableProject {

    /**
     * INTERNAL: Return a new descriptor project.
     */
    public ObjectPersistenceRuntimeXMLProject() {
        super();
    }

    @Override
    protected void buildDescriptors() {
        addDescriptor(buildProjectDescriptor());
        addDescriptor(buildClassDescriptorDescriptor());
        addDescriptor(buildRelationalDescriptorDescriptor());
        addDescriptor(buildObjectRelationalDataTypeDescriptorDescriptor());

        addDescriptor(buildDatasourceLoginDescriptor());
        addDescriptor(buildDatabaseLoginDescriptor());

        addDescriptor(buildInheritancePolicyDescriptor());
        addDescriptor(buildInterfacePolicyDescriptor());
        addDescriptor(buildOptimisticLockingPolicyDescriptor());
        addDescriptor(buildAllFieldsLockingPolicyDescriptor());
        addDescriptor(buildSelectedFieldsLockingPolicyDescriptor());
        addDescriptor(buildChangedFieldsLockingPolicyDescriptor());
        addDescriptor(buildVersionLockingPolicyDescriptor());
        addDescriptor(buildTimestmapLockingPolicyDescriptor());
        addDescriptor(buildEventManagerDescriptor());
        addDescriptor(buildQueryManagerDescriptor());

        addDescriptor(buildDatabaseQueryDescriptor());
        addDescriptor(buildReadQueryDescriptor());
        addDescriptor(buildObjectLevelReadQueryDescriptor());
        addDescriptor(buildReadAllObjectQueryDescriptor());
        addDescriptor(buildReadObjectQueryDescriptor());
        addDescriptor(buildDataReadQueryDescriptor());
        addDescriptor(buildDataModifyQueryDescriptor());
        addDescriptor(buildDirectReadQueryDescriptor());
        addDescriptor(buildValueReadQueryDescriptor());
        addDescriptor(buildDeleteObjectQueryDescriptor());
        addDescriptor(buildDeleteAllQueryDescriptor());
        addDescriptor(buildInsertObjectQueryDescriptor());
        addDescriptor(buildUpdateObjectQueryDescriptor());
        addDescriptor(buildDoesExistQueryDescriptor());
        addDescriptor(buildReportQueryDescriptor());

        addDescriptor(buildCallDescriptor());
        addDescriptor(buildSQLCallDescriptor());
        addDescriptor(buildJPQLCallDescriptor());
        addDescriptor(buildMethodBaseQueryRedirectorDescriptor());
        addDescriptor(buildInMemoryQueryIndirectionPolicyDescriptor());
        addDescriptor(buildInstantiationPolicyDescriptor());
        addDescriptor(buildCopyPolicyDescriptor());
        addDescriptor(buildCloneCopyPolicyDescriptor());
        addDescriptor(buildInstantiationCopyPolicyDescriptor());
        addDescriptor(buildContainerPolicyDescriptor());
        addDescriptor(buildInterfaceContainerPolicyDescriptor());
        addDescriptor(buildMapContainerPolicyDescriptor());
        addDescriptor(buildCollectionContainerPolicyDescriptor());
        addDescriptor(buildListContainerPolicyDescriptor());
        addDescriptor(buildDirectMapContainerPolicyDescriptor());
        addDescriptor(buildIndirectionPolicyDescriptor());
        addDescriptor(buildBasicIndirectionPolicyDescriptor());
        addDescriptor(buildTransparentIndirectionPolicyDescriptor());
        addDescriptor(buildProxyIndirectionPolicyDescriptor());
        addDescriptor(buildContainerIndirectionPolicyDescriptor());
        addDescriptor(buildAssociationDescriptor());
        addDescriptor(buildPropertyAssociationDescriptor());
        addDescriptor(buildFieldTranslationDescriptor());
        addDescriptor(buildTypedAssociationDescriptor());
        addDescriptor(buildTypeMappingDescriptor());
        addDescriptor(buildFieldTransformationDescriptor());
        addDescriptor(buildMethodBasedFieldTransformationDescriptor());
        addDescriptor(buildTransformerBasedFieldTransformationDescriptor());
        addDescriptor(buildQueryArgumentDescriptor());
        addDescriptor(buildQueryKeyReferenceDescriptor());
        addDescriptor(buildReportItemDescriptor());
        addDescriptor(buildQueryResultCachePolicyDescriptor());

        addDescriptor(buildQueryKeyDescriptor());
        addDescriptor(buildDirectQueryKeyDescriptor());
        addDescriptor(buildDatabaseTableDescriptor());
        addDescriptor(buildDatabaseFieldDescriptor());

        addDescriptor(buildDatabaseMappingDescriptor());

        addDescriptor(buildAbstractDirectMappingDescriptor());
        addDescriptor(buildDirectToFieldMappingDescriptor());
        addDescriptor(buildXMLDirectMappingDescriptor());
        try {
            Class typesafeenumClass = (Class)new PrivilegedClassForName("org.eclipse.persistence.jaxb.JAXBTypesafeEnumConverter").run();
            addDescriptor(buildTypesafeEnumConverterDescriptor(typesafeenumClass));
        }
        catch (ClassNotFoundException cnfe) {
            // The JAXB component isn't available, so no need to do anything
        }
        addDescriptor(buildConverterDescriptor());
        addDescriptor(buildObjectTypeConverterDescriptor());
        addDescriptor(buildSerializedObjectConverterDescriptor());
        addDescriptor(buildTypeConversionConverterDescriptor());

        addDescriptor(buildAbstractTransformationMappingDescriptor());
        addDescriptor(buildTransformationMappingDescriptor());
        addDescriptor(buildXMLTransformationMappingDescriptor());

        addDescriptor(buildAggregateMappingDescriptor());
        addDescriptor(buildAggregateObjectMappingDescriptor());
        addDescriptor(buildStructureMappingDescriptor());
        addDescriptor(buildObjectArrayMappingDescriptor());

        addDescriptor(buildForeignReferenceMappingDescriptor());
        addDescriptor(buildCollectionMappingDescriptor());
        addDescriptor(buildOneToManyMappingMappingDescriptor());
        addDescriptor(buildManyToManyMappingMappingDescriptor());
        addDescriptor(buildAggregateCollectionMappingDescriptor());
        addDescriptor(buildDirectCollectionMappingDescriptor());
        addDescriptor(buildDirectMapMappingDescriptor());
        addDescriptor(buildNestedTableMappingDescriptor());
        addDescriptor(buildObjectReferenceMappingDescriptor());
        addDescriptor(buildOneToOneMappingDescriptor());
        addDescriptor(buildReferenceMappingDescriptor());
        addDescriptor(buildVariableOneToOneMappingDescriptor());

        addDescriptor(buildAbstractCompositeDirectCollectionMappingDescriptor());
        addDescriptor(buildXMLCompositeDirectCollectionMappingDescriptor());
        addDescriptor(buildArrayMappingDescriptor());

        addDescriptor(buildExpressionDescriptor());
        addDescriptor(buildLogicalExpressionDescriptor());
        addDescriptor(buildRelationExpressionDescriptor());
        addDescriptor(buildFunctionExpressionDescriptor());
        addDescriptor(buildParameterExpressionDescriptor());
        addDescriptor(buildConstantExpressionDescriptor());
        addDescriptor(buildFieldExpressionDescriptor());
        addDescriptor(buildQueryKeyExpressionDescriptor());
        addDescriptor(buildExpressionBuilderDescriptor());

        // TopLink OX
        addDescriptor(buildAbstractCompositeObjectMappingDescriptor());
        addDescriptor(buildXMLCompositeObjectMappingDescriptor());
        addDescriptor(buildAbstractCompositeCollectionMappingDescriptor());
        addDescriptor(buildXMLCompositeCollectionMappingDescriptor());
        addDescriptor(buildXMLAnyCollectionMappingDescriptor());
        addDescriptor(buildXMLAnyObjectMappingDescriptor());
        addDescriptor(buildOXXMLDescriptorDescriptor());
        addDescriptor(buildXMLFieldDescriptor());
        addDescriptor(buildXMLUnionFieldDescriptor());
        addDescriptor(buildXMLConversionPairDescriptor());
        addDescriptor(buildNamespaceResolverDescriptor());
        addDescriptor(buildNamespaceDescriptor());
        addDescriptor(buildXMLSchemaReferenceDescriptor());
        addDescriptor(buildXMLSchemaClassPathReferenceDescriptor());
        addDescriptor(buildXMLSchemaFileReferenceDescriptor());
        addDescriptor(buildXMLSchemaURLReferenceDescriptor());
        addDescriptor(buildXMLLoginDescriptor());
        addDescriptor(buildQNameInheritancePolicyDescriptor());

        addDescriptor(buildCacheInvalidationPolicyDescriptor());
        addDescriptor(buildNoExpiryCacheInvalidationPolicyDescriptor());
        addDescriptor(buildTimeToLiveCacheInvalidationPolicyDescriptor());
        addDescriptor(buildDailyCacheInvalidationPolicyDescriptor());

        addDescriptor(buildHistoryPolicyDescriptor());
        addDescriptor(buildHistoryTableDescriptor());

        addDescriptor(buildReturningPolicyDescriptor());
        addDescriptor(buildReturningFieldInfoDescriptor());

        // cmp
        addDescriptor(buildCMPPolicyDescriptor());
        addDescriptor(buildPessimisticLockingPolicyDescriptor());

        // fetch group
        addDescriptor(buildFetchGroupManagerDescriptor());
        addDescriptor(buildFetchGroupDescriptor());

        // sequences
        addDescriptor(buildSequenceDescriptor());
        addDescriptor(buildDefaultSequenceDescriptor());
        addDescriptor(buildNativeSequenceDescriptor());
        addDescriptor(buildTableSequenceDescriptor());
        addDescriptor(buildUnaryTableSequenceDescriptor());
        try {
            Class.forName("org.eclipse.persistence.eis.adapters.xmlfile.XMLFileSequence");
            addDescriptor(buildXMLFileSequenceDescriptor());
        } catch (Exception missing) {
            // Ignore.
        }
        // change policy
        addDescriptor(buildChangePolicyDescriptor());
        addDescriptor(buildDeferredChangeDetectionPolicyDescriptor());
        addDescriptor(buildObjectChangeTrackingPolicyDescriptor());
        addDescriptor(buildAttributeChangeTrackingPolicyDescriptor());

        /*
         * support for additional runtime elements: // expressions (query, qk, mapping) // stored proc // properties // attribute accessors // inheritance extractors // converter class
         * 
         * is being added incrementally through ObjectPersistenceRuntimeXMLProject_11_1_1
         */
    }

    @Override
    public String getPrimaryNamespacePrefix() {
        return TOPLINK_PREFIX;
    }

    @Override
    public String getPrimaryNamespace() {
        return TOPLINK_NAMESPACE;
    }

    @Override
    public String getSecondaryNamespacePrefix() {
        return OPM_PREFIX;
    }

    @Override
    public String getSecondaryNamespace() {
        return OPM_NAMESPACE;
    }

    protected ClassDescriptor buildAggregateCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AggregateCollectionMapping.class);
        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        XMLCompositeCollectionMapping sourceToTargetKeyFieldAssociationsMapping = new XMLCompositeCollectionMapping();
        sourceToTargetKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        // Handle translation of foreign key associations to hashtables.
        sourceToTargetKeyFieldAssociationsMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                AggregateCollectionMapping mapping = (AggregateCollectionMapping)object;
                List sourceFields = mapping.getSourceKeyFields();
                List targetFields = mapping.getTargetForeignKeyFields();
                List associations = new ArrayList(sourceFields.size());
                for (int index = 0; index < sourceFields.size(); index++) {
                    associations.add(new Association(targetFields.get(index), sourceFields.get(index)));
                }
                return associations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                AggregateCollectionMapping mapping = (AggregateCollectionMapping)object;
                List associations = (List)value;
                mapping.setSourceKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                mapping.setTargetForeignKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                Iterator iterator = associations.iterator();
                while (iterator.hasNext()) {
                    Association association = (Association)iterator.next();
                    mapping.getSourceKeyFields().add((DatabaseField)association.getValue());
                    mapping.getTargetForeignKeyFields().add((DatabaseField)association.getKey());
                }
            }
        });
        sourceToTargetKeyFieldAssociationsMapping.setAttributeName("sourceToTargetKeyFieldAssociations");
        sourceToTargetKeyFieldAssociationsMapping.setXPath(getPrimaryNamespaceXPath() + "target-foreign-key/" + getSecondaryNamespaceXPath() + "field-reference");
        descriptor.addMapping(sourceToTargetKeyFieldAssociationsMapping);

        XMLDirectMapping relationshipPartnerAttributeNameMapping = new XMLDirectMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setXPath(getPrimaryNamespaceXPath() + "bidirectional-target-attribute/text()");
        descriptor.addMapping(relationshipPartnerAttributeNameMapping);

        XMLDirectMapping usesBatchReadingMapping = new XMLDirectMapping();
        usesBatchReadingMapping.setAttributeName("usesBatchReading");
        usesBatchReadingMapping.setGetMethodName("shouldUseBatchReading");
        usesBatchReadingMapping.setSetMethodName("setUsesBatchReading");
        usesBatchReadingMapping.setXPath(getPrimaryNamespaceXPath() + "batch-reading/text()");
        usesBatchReadingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesBatchReadingMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container");
        descriptor.addMapping(containerPolicyMapping);

        XMLCompositeObjectMapping indirectionPolicyMapping = new XMLCompositeObjectMapping();
        indirectionPolicyMapping.setReferenceClass(IndirectionPolicy.class);
        // Handle translation of NoIndirectionPolicy -> null.
        indirectionPolicyMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                IndirectionPolicy policy = ((ForeignReferenceMapping)object).getIndirectionPolicy();
                if (policy instanceof NoIndirectionPolicy) {
                    return null;
                }
                return policy;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                IndirectionPolicy policy = (IndirectionPolicy)value;
                if (value == null) {
                    policy = new NoIndirectionPolicy();
                }
                ((ForeignReferenceMapping)object).setIndirectionPolicy(policy);
            }
        });
        indirectionPolicyMapping.setAttributeName("indirectionPolicy");
        indirectionPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "indirection");
        descriptor.addMapping(indirectionPolicyMapping);

        XMLCompositeObjectMapping selectionQueryMapping = new XMLCompositeObjectMapping();
        selectionQueryMapping.setAttributeName("selectionQuery");
        selectionQueryMapping.setGetMethodName("getSelectionQuery");
        selectionQueryMapping.setSetMethodName("setSelectionQuery");
        selectionQueryMapping.setReferenceClass(ReadQuery.class);
        selectionQueryMapping.setXPath(getPrimaryNamespaceXPath() + "selection-query");
        descriptor.addMapping(selectionQueryMapping);

        // delete-all query
        return descriptor;
    }

    protected ClassDescriptor buildAggregateMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(AggregateMapping.class);

        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        XMLDirectMapping referenceClassMapping = new XMLDirectMapping();
        referenceClassMapping.setAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClass");
        referenceClassMapping.setSetMethodName("setReferenceClass");
        referenceClassMapping.setXPath(getPrimaryNamespaceXPath() + "reference-class/text()");
        descriptor.addMapping(referenceClassMapping);

        return descriptor;
    }

    protected ClassDescriptor buildAggregateObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AggregateObjectMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AggregateMapping.class);

        XMLDirectMapping isNullAllowedMapping = new XMLDirectMapping();
        isNullAllowedMapping.setAttributeName("isNullAllowed");
        isNullAllowedMapping.setGetMethodName("isNullAllowed");
        isNullAllowedMapping.setSetMethodName("setIsNullAllowed");
        isNullAllowedMapping.setXPath(getPrimaryNamespaceXPath() + "allow-null/text()");
        isNullAllowedMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(isNullAllowedMapping);

        XMLCompositeCollectionMapping aggregateToSourceFieldNameAssociationsMapping = new XMLCompositeCollectionMapping();
        aggregateToSourceFieldNameAssociationsMapping.setReferenceClass(FieldTranslation.class);
        // Handle translation of fields associations string to field.
        aggregateToSourceFieldNameAssociationsMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                /*bug 322233: AttributeOverrides and AssociationOverride  
                 * changed getAggregateToSourceFieldAssociations to hold String->DatabaseField associations
                 */
                AggregateObjectMapping mapping = (AggregateObjectMapping)object;
                Vector associations = mapping.getAggregateToSourceFieldAssociations();
                Vector translations = new Vector(associations.size());
                for (int index = 0; index < associations.size(); index++) {
                    Association association = (Association)associations.get(index);
                    FieldTranslation translation = new FieldTranslation();
                    translation.setKey(new DatabaseField((String)association.getKey()));
                    translation.setValue(association.getValue());
                    translations.add(translation);
                }
                return translations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                AggregateObjectMapping mapping = (AggregateObjectMapping)object;
                Vector associations = (Vector)value;
                for (int index = 0; index < associations.size(); index++) {
                    Association association = (Association)associations.get(index);
                    association.setKey(((DatabaseField)association.getKey()).getQualifiedName());
                }

                mapping.setAggregateToSourceFieldAssociations(associations);
            }
        });
        aggregateToSourceFieldNameAssociationsMapping.setAttributeName("aggregateToSourceFieldNameAssociationsMapping");
        aggregateToSourceFieldNameAssociationsMapping.setXPath(getPrimaryNamespaceXPath() + "field-translations/" + getPrimaryNamespaceXPath() + "field-translation");
        descriptor.addMapping(aggregateToSourceFieldNameAssociationsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildArrayMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ArrayMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeDirectCollectionMapping.class);

        XMLDirectMapping structureMapping = new XMLDirectMapping();
        structureMapping.setAttributeName("structureName");
        structureMapping.setGetMethodName("getStructureName");
        structureMapping.setSetMethodName("setStructureName");
        structureMapping.setXPath(getPrimaryNamespaceXPath() + "structure/text()");
        descriptor.addMapping(structureMapping);

        return descriptor;
    }

    protected ClassDescriptor buildBasicIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(BasicIndirectionPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(IndirectionPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildCollectionContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CollectionContainerPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(InterfaceContainerPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CollectionMapping.class);

        descriptor.getInheritancePolicy().setParentClass(ForeignReferenceMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildContainerIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ContainerIndirectionPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(IndirectionPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ContainerPolicy.class);
        descriptor.setDefaultRootElement("container-policy");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(CollectionContainerPolicy.class, getPrimaryNamespaceXPath() + "container-policy");
        descriptor.getInheritancePolicy().addClassIndicator(ListContainerPolicy.class, getPrimaryNamespaceXPath() + "list-container-policy");
        descriptor.getInheritancePolicy().addClassIndicator(MapContainerPolicy.class, getPrimaryNamespaceXPath() + "map-container-policy");
        descriptor.getInheritancePolicy().addClassIndicator(MappedKeyMapContainerPolicy.class, getPrimaryNamespaceXPath() + "direct-map-container-policy");

        return descriptor;
    }

    protected ClassDescriptor buildCopyPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(AbstractCopyPolicy.class);
        descriptor.setDefaultRootElement("copy-policy");
        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(CloneCopyPolicy.class, getPrimaryNamespaceXPath() + "clone-copy-policy");
        descriptor.getInheritancePolicy().addClassIndicator(InstantiationCopyPolicy.class, getPrimaryNamespaceXPath() + "instantiation-copy-policy");

        return descriptor;
    }

    protected ClassDescriptor buildCloneCopyPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(CloneCopyPolicy.class);
        descriptor.setDefaultRootElement("copy-policy");
        descriptor.getInheritancePolicy().setParentClass(AbstractCopyPolicy.class);
        XMLDirectMapping methodNameMapping = new XMLDirectMapping();
        methodNameMapping.setAttributeName("methodName");
        methodNameMapping.setGetMethodName("getMethodName");
        methodNameMapping.setSetMethodName("setMethodName");
        methodNameMapping.setXPath(getPrimaryNamespaceXPath() + "method/text()");
        descriptor.addMapping(methodNameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildInstantiationCopyPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(InstantiationCopyPolicy.class);
        descriptor.setDefaultRootElement("copy-policy");
        descriptor.getInheritancePolicy().setParentClass(AbstractCopyPolicy.class);
        return descriptor;
    }

    public ClassDescriptor buildDatasourceLoginDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatasourceLogin.class);
        descriptor.setDefaultRootElement("login");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DatabaseLogin.class, getPrimaryNamespaceXPath() + "database-login");
        descriptor.getInheritancePolicy().addClassIndicator(EISLogin.class, getPrimaryNamespaceXPath() + "eis-login");
        descriptor.getInheritancePolicy().addClassIndicator(XMLLogin.class, getPrimaryNamespaceXPath() + "xml-login");

        XMLDirectMapping platformMapping = new XMLDirectMapping();
        platformMapping.setAttributeName("platform");
        platformMapping.setGetMethodName("getDatasourcePlatform");
        platformMapping.setSetMethodName("usePlatform");
        platformMapping.setConverter(new Converter() {
            protected DatabaseMapping mapping;
            private Map platformList;
            private String oldPrefix = "oracle.toplink.";
            private String newPrefix = "org.eclipse.persistence.";
            private String oldOxmPrefix = oldPrefix + "ox.";
            private String newOxmPrefix = newPrefix + "oxm.";

            public Object convertObjectValueToDataValue(Object objectValue, Session session) {
                if (objectValue == null) {
                    return null;
                }
                return objectValue.getClass().getName();
            }

            public Object convertDataValueToObjectValue(Object fieldValue, Session session) {
                if(fieldValue == null) {
                    return null;
                }
                if(((String)fieldValue).startsWith(oldPrefix)) {
                    if(((String)fieldValue).startsWith(oldOxmPrefix)) {
                        fieldValue = ((String)fieldValue).replaceFirst(oldOxmPrefix, newOxmPrefix);
                    } else {
                        fieldValue = ((String)fieldValue).replaceFirst(oldPrefix, newPrefix);
                    }
                }
                // convert deprecated platforms to new platforms
                Object result = platformList.get(fieldValue);
                if (result != null) {
                    fieldValue = result;
                }

                Object attributeValue;
                Class attributeClass = (Class)((AbstractSession)session).getDatasourcePlatform().convertObject(fieldValue, ClassConstants.CLASS);
                try {
                    if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                        try {
                            attributeValue = AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(attributeClass));
                        }
                        catch (PrivilegedActionException exception) {
                            throw ConversionException.couldNotBeConverted(fieldValue, attributeClass, exception.getException());
                        }
                    }
                    else {
                        attributeValue = PrivilegedAccessHelper.newInstanceFromClass(attributeClass);
                    }
                }
                catch (Exception exception) {
                    throw ConversionException.couldNotBeConverted(fieldValue, attributeClass, exception);
                }

                return attributeValue;
            }

            public boolean isMutable() {
                return false;
            }

            public void initialize(DatabaseMapping mapping, Session session) {
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
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.OraclePlatform", "org.eclipse.persistence.platform.database.OraclePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.PointBasePlatform", "org.eclipse.persistence.platform.database.PointBasePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.SQLAnyWherePlatform", "org.eclipse.persistence.platform.database.SQLAnywherePlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.SQLServerPlatform", "org.eclipse.persistence.platform.database.SQLServerPlatform");
                this.platformList.put("org.eclipse.persistence.internal.databaseaccess.SybasePlatform", "org.eclipse.persistence.platform.database.SybasePlatform");
                this.platformList.put("org.eclipse.persistence.oraclespecific.Oracle8Platform", "org.eclipse.persistence.platform.database.oracle.Oracle8Platform");
                this.platformList.put("org.eclipse.persistence.oraclespecific.Oracle9Platform", "org.eclipse.persistence.platform.database.oracle.Oracle9Platform");
                this.platformList.put("org.eclipse.persistence.platform.database.SQLAnyWherePlatform", "org.eclipse.persistence.platform.database.SQLAnywherePlatform");
                this.mapping = mapping;
                // CR#... Mapping must also have the field classification.
                if (this.mapping.isDirectToFieldMapping()) {
                    AbstractDirectMapping directMapping = (AbstractDirectMapping)this.mapping;

                    // Allow user to specify field type to override computed value. (i.e. blob, nchar)
                    if (directMapping.getFieldClassification() == null) {
                        directMapping.setFieldClassification(ClassConstants.STRING);
                    }
                }
            }

        });
        platformMapping.setXPath(getPrimaryNamespaceXPath() + "platform-class/text()");
        descriptor.addMapping(platformMapping);

        XMLDirectMapping userNameMapping = new XMLDirectMapping();
        userNameMapping.setAttributeName("userName");
        userNameMapping.setGetMethodName("getUserName");
        userNameMapping.setSetMethodName("setUserName");
        userNameMapping.setXPath(getPrimaryNamespaceXPath() + "user-name/text()");
        descriptor.addMapping(userNameMapping);

        XMLDirectMapping passwordMapping = new XMLDirectMapping();
        passwordMapping.setAttributeName("password");
        passwordMapping.setGetMethodName("getPassword");
        passwordMapping.setSetMethodName("setEncryptedPassword");
        passwordMapping.setXPath(getPrimaryNamespaceXPath() + "password/text()");
        descriptor.addMapping(passwordMapping);

        XMLDirectMapping usesExternalConnectionPoolingMapping = new XMLDirectMapping();
        usesExternalConnectionPoolingMapping.setAttributeName("usesExternalConnectionPooling");
        usesExternalConnectionPoolingMapping.setGetMethodName("shouldUseExternalConnectionPooling");
        usesExternalConnectionPoolingMapping.setSetMethodName("setUsesExternalConnectionPooling");
        usesExternalConnectionPoolingMapping.setXPath(getPrimaryNamespaceXPath() + "external-connection-pooling/text()");
        usesExternalConnectionPoolingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesExternalConnectionPoolingMapping);

        XMLDirectMapping usesExternalTransactionControllerMapping = new XMLDirectMapping();
        usesExternalTransactionControllerMapping.setAttributeName("usesExternalTransactionController");
        usesExternalTransactionControllerMapping.setGetMethodName("shouldUseExternalTransactionController");
        usesExternalTransactionControllerMapping.setSetMethodName("setUsesExternalTransactionController");
        usesExternalTransactionControllerMapping.setXPath(getPrimaryNamespaceXPath() + "external-transaction-controller/text()");
        usesExternalTransactionControllerMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesExternalTransactionControllerMapping);

        XMLCompositeObjectMapping defaultSequenceMapping = new XMLCompositeObjectMapping();
        defaultSequenceMapping.setAttributeName("defaultSequence");
        defaultSequenceMapping.setSetMethodName("setDefaultSequence");
        defaultSequenceMapping.setGetMethodName("getDefaultSequenceToWrite");
        defaultSequenceMapping.setReferenceClass(Sequence.class);
        defaultSequenceMapping.setXPath(getPrimaryNamespaceXPath() + "sequencing/" + getPrimaryNamespaceXPath() + "default-sequence");
        descriptor.addMapping(defaultSequenceMapping);

        XMLCompositeCollectionMapping sequencesMapping = new XMLCompositeCollectionMapping();
        MapContainerPolicy containerPolicy = new MapContainerPolicy(HashMap.class);
        containerPolicy.setKeyName("name", Sequence.class.getName());
        sequencesMapping.setContainerPolicy(containerPolicy);
        sequencesMapping.setAttributeName("sequences");
        sequencesMapping.setSetMethodName("setSequences");
        sequencesMapping.setGetMethodName("getSequencesToWrite");
        sequencesMapping.setReferenceClass(Sequence.class);
        sequencesMapping.setXPath(getPrimaryNamespaceXPath() + "sequencing/" + getPrimaryNamespaceXPath() + "sequences/" + getPrimaryNamespaceXPath() + "sequence");
        descriptor.addMapping(sequencesMapping);

        return descriptor;
    }

    public ClassDescriptor buildDatabaseLoginDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatabaseLogin.class);

        descriptor.getInheritancePolicy().setParentClass(DatasourceLogin.class);

        XMLDirectMapping driverClassNameMapping = new XMLDirectMapping();
        driverClassNameMapping.setAttributeName("driverClassName");
        driverClassNameMapping.setGetMethodName("getDriverClassName");
        driverClassNameMapping.setSetMethodName("setDriverClassName");
        driverClassNameMapping.setXPath(getPrimaryNamespaceXPath() + "driver-class/text()");
        descriptor.addMapping(driverClassNameMapping);

        XMLDirectMapping driverURLMapping = new XMLDirectMapping();
        driverURLMapping.setAttributeName("connectionString");
        driverURLMapping.setGetMethodName("getConnectionString");
        driverURLMapping.setSetMethodName("setConnectionString");
        driverURLMapping.setXPath(getPrimaryNamespaceXPath() + "connection-url/text()");
        descriptor.addMapping(driverURLMapping);

        XMLDirectMapping shouldBindAllParametersMapping = new XMLDirectMapping();
        shouldBindAllParametersMapping.setAttributeName("shouldBindAllParameters");
        shouldBindAllParametersMapping.setGetMethodName("shouldBindAllParameters");
        shouldBindAllParametersMapping.setSetMethodName("setShouldBindAllParameters");
        shouldBindAllParametersMapping.setXPath(getPrimaryNamespaceXPath() + "bind-all-parameters/text()");
        shouldBindAllParametersMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldBindAllParametersMapping);

        XMLDirectMapping shouldCacheAllStatementsMapping = new XMLDirectMapping();
        shouldCacheAllStatementsMapping.setAttributeName("shouldCacheAllStatements");
        shouldCacheAllStatementsMapping.setGetMethodName("shouldCacheAllStatements");
        shouldCacheAllStatementsMapping.setSetMethodName("setShouldCacheAllStatements");
        shouldCacheAllStatementsMapping.setXPath(getPrimaryNamespaceXPath() + "cache-all-statements/text()");
        shouldCacheAllStatementsMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldCacheAllStatementsMapping);

        XMLDirectMapping usesByteArrayBindingMapping = new XMLDirectMapping();
        usesByteArrayBindingMapping.setAttributeName("usesByteArrayBinding");
        usesByteArrayBindingMapping.setGetMethodName("shouldUseByteArrayBinding");
        usesByteArrayBindingMapping.setSetMethodName("setUsesByteArrayBinding");
        usesByteArrayBindingMapping.setXPath(getPrimaryNamespaceXPath() + "byte-array-binding/text()");
        usesByteArrayBindingMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(usesByteArrayBindingMapping);

        XMLDirectMapping usesStringBindingMapping = new XMLDirectMapping();
        usesStringBindingMapping.setAttributeName("usesStringBinding");
        usesStringBindingMapping.setGetMethodName("shouldUseStringBinding");
        usesStringBindingMapping.setSetMethodName("setUsesStringBinding");
        usesStringBindingMapping.setXPath(getPrimaryNamespaceXPath() + "string-binding/text()");
        usesStringBindingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesStringBindingMapping);

        XMLDirectMapping stringBindingSizeMapping = new XMLDirectMapping();
        stringBindingSizeMapping.setAttributeName("stringBindingSize");
        stringBindingSizeMapping.setGetMethodName("getStringBindingSize");
        stringBindingSizeMapping.setSetMethodName("setStringBindingSize");
        stringBindingSizeMapping.setXPath(getPrimaryNamespaceXPath() + "string-binding-size/text()");
        stringBindingSizeMapping.setNullValue(Integer.valueOf(255));
        descriptor.addMapping(stringBindingSizeMapping);

        XMLDirectMapping usesStreamsForBindingMapping = new XMLDirectMapping();
        usesStreamsForBindingMapping.setAttributeName("usesStreamsForBinding");
        usesStreamsForBindingMapping.setGetMethodName("shouldUseStreamsForBinding");
        usesStreamsForBindingMapping.setSetMethodName("setUsesStreamsForBinding");
        usesStreamsForBindingMapping.setXPath(getPrimaryNamespaceXPath() + "streams-for-binding/text()");
        usesStreamsForBindingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesStreamsForBindingMapping);

        XMLDirectMapping shouldForceFieldNamesToUpperCaseMapping = new XMLDirectMapping();
        shouldForceFieldNamesToUpperCaseMapping.setAttributeName("shouldForceFieldNamesToUpperCase");
        shouldForceFieldNamesToUpperCaseMapping.setGetMethodName("shouldForceFieldNamesToUpperCase");
        shouldForceFieldNamesToUpperCaseMapping.setSetMethodName("setShouldForceFieldNamesToUpperCase");
        shouldForceFieldNamesToUpperCaseMapping.setXPath(getPrimaryNamespaceXPath() + "force-field-names-to-upper-case/text()");
        shouldForceFieldNamesToUpperCaseMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldForceFieldNamesToUpperCaseMapping);

        XMLDirectMapping shouldOptimizeDataConversionMapping = new XMLDirectMapping();
        shouldOptimizeDataConversionMapping.setAttributeName("shouldOptimizeDataConversion");
        shouldOptimizeDataConversionMapping.setGetMethodName("shouldOptimizeDataConversion");
        shouldOptimizeDataConversionMapping.setSetMethodName("setShouldOptimizeDataConversion");
        shouldOptimizeDataConversionMapping.setXPath(getPrimaryNamespaceXPath() + "optimize-data-conversion/text()");
        shouldOptimizeDataConversionMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(shouldOptimizeDataConversionMapping);

        XMLDirectMapping shouldTrimStringsMapping = new XMLDirectMapping();
        shouldTrimStringsMapping.setAttributeName("shouldTrimStrings");
        shouldTrimStringsMapping.setGetMethodName("shouldTrimStrings");
        shouldTrimStringsMapping.setSetMethodName("setShouldTrimStrings");
        shouldTrimStringsMapping.setXPath(getPrimaryNamespaceXPath() + "trim-strings/text()");
        shouldTrimStringsMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(shouldTrimStringsMapping);

        XMLDirectMapping usesBatchWritingMapping = new XMLDirectMapping();
        usesBatchWritingMapping.setAttributeName("usesBatchWriting");
        usesBatchWritingMapping.setGetMethodName("shouldUseBatchWriting");
        usesBatchWritingMapping.setSetMethodName("setUsesBatchWriting");
        usesBatchWritingMapping.setNullValue(Boolean.FALSE);
        usesBatchWritingMapping.setXPath(getPrimaryNamespaceXPath() + "batch-writing/text()");
        descriptor.addMapping(usesBatchWritingMapping);

        XMLDirectMapping usesJDBCBatchWritingMapping = new XMLDirectMapping();
        usesJDBCBatchWritingMapping.setAttributeName("usesJDBCBatchWriting");
        usesJDBCBatchWritingMapping.setGetMethodName("shouldUseJDBCBatchWriting");
        usesJDBCBatchWritingMapping.setSetMethodName("setUsesJDBCBatchWriting");
        usesJDBCBatchWritingMapping.setXPath(getPrimaryNamespaceXPath() + "jdbc-batch-writing/text()");
        usesJDBCBatchWritingMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(usesJDBCBatchWritingMapping);

        // datasources
        return descriptor;
    }

    protected ClassDescriptor buildDailyCacheInvalidationPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DailyCacheInvalidationPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(CacheInvalidationPolicy.class);

        XMLDirectMapping expiryMinuteMapping = new XMLDirectMapping();
        expiryMinuteMapping.setAttributeName("expiryTime");
        expiryMinuteMapping.setGetMethodName("getExpiryTime");
        expiryMinuteMapping.setSetMethodName("setExpiryTime");
        XMLField expiryTimeField = new XMLField(getPrimaryNamespaceXPath() + "expiry-time/text()");
        expiryTimeField.setIsTypedTextField(true);
        expiryMinuteMapping.setField(expiryTimeField);
        descriptor.addMapping(expiryMinuteMapping);

        return descriptor;
    }

    protected ClassDescriptor buildExpressionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Expression.class);
        descriptor.setDefaultRootElement("expression");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(LogicalExpression.class, getPrimaryNamespaceXPath() + "logic-expression");
        descriptor.getInheritancePolicy().addClassIndicator(RelationExpression.class, getPrimaryNamespaceXPath() + "relation-expression");
        descriptor.getInheritancePolicy().addClassIndicator(ConstantExpression.class, getPrimaryNamespaceXPath() + "constant-expression");
        descriptor.getInheritancePolicy().addClassIndicator(QueryKeyExpression.class, getPrimaryNamespaceXPath() + "query-key-expression");
        descriptor.getInheritancePolicy().addClassIndicator(ParameterExpression.class, getPrimaryNamespaceXPath() + "parameter-expression");
        descriptor.getInheritancePolicy().addClassIndicator(FieldExpression.class, getPrimaryNamespaceXPath() + "field-expression");
        descriptor.getInheritancePolicy().addClassIndicator(FunctionExpression.class, getPrimaryNamespaceXPath() + "function-expression");
        descriptor.getInheritancePolicy().addClassIndicator(ExpressionBuilder.class, getPrimaryNamespaceXPath() + "base-expression");

        return descriptor;
    }

    protected ClassDescriptor buildLogicalExpressionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(LogicalExpression.class);
        descriptor.setDefaultRootElement("logic-expression");

        descriptor.getInheritancePolicy().setParentClass(Expression.class);

        XMLDirectMapping operatorMapping = new XMLDirectMapping();
        operatorMapping.setAttributeName("operator");
        ObjectTypeConverter operatorConverter = new ObjectTypeConverter();
        operatorConverter.addConversionValue("and", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.And)));
        operatorConverter.addConversionValue("or", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Or)));
        operatorMapping.setConverter(operatorConverter);
        operatorMapping.setXPath("@operator");
        descriptor.addMapping(operatorMapping);

        XMLCompositeObjectMapping leftMapping = new XMLCompositeObjectMapping();
        leftMapping.setAttributeName("firstChild");
        leftMapping.setGetMethodName("getFirstChild");
        leftMapping.setSetMethodName("setFirstChild");
        leftMapping.setReferenceClass(Expression.class);
        leftMapping.setXPath(getPrimaryNamespaceXPath() + "left");
        descriptor.addMapping(leftMapping);

        XMLCompositeObjectMapping rightMapping = new XMLCompositeObjectMapping();
        rightMapping.setAttributeName("secondChild");
        rightMapping.setGetMethodName("getSecondChild");
        rightMapping.setSetMethodName("setSecondChild");
        rightMapping.setReferenceClass(Expression.class);
        rightMapping.setXPath(getPrimaryNamespaceXPath() + "right");
        descriptor.addMapping(rightMapping);

        return descriptor;
    }

    protected ClassDescriptor buildRelationExpressionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RelationExpression.class);
        descriptor.setDefaultRootElement("relation-expression");

        descriptor.getInheritancePolicy().setParentClass(Expression.class);

        // Child value expressions need their backpointer to their local base set,
        // this is not persisted so must be hooked back up after loading.
        descriptor.getEventManager().addListener(new DescriptorEventAdapter() {
            public void postBuild(DescriptorEvent event) {
                RelationExpression expression = (RelationExpression)event.getObject();
                if ((expression.getFirstChild() != null) && (expression.getSecondChild() != null)) {
                    if (expression.getSecondChild().isValueExpression()) {
                        expression.getSecondChild().setLocalBase(expression.getFirstChild());
                    }
                    if (expression.getFirstChild().isValueExpression()) {
                        expression.getFirstChild().setLocalBase(expression.getSecondChild());
                    }
                }
            }
        });

        XMLDirectMapping operatorMapping = new XMLDirectMapping();
        operatorMapping.setAttributeName("operator");
        ObjectTypeConverter operatorConverter = new ObjectTypeConverter();
        operatorConverter.addConversionValue("equal", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Equal)));
        operatorConverter.addConversionValue("notEqual", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.NotEqual)));
        operatorConverter.addConversionValue("like", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Like)));
        operatorConverter.addConversionValue("notLike", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.NotLike)));
        operatorConverter.addConversionValue("greaterThan", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.GreaterThan)));
        operatorConverter.addConversionValue("greaterThanEqual", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.GreaterThanEqual)));
        operatorConverter.addConversionValue("lessThan", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.LessThan)));
        operatorConverter.addConversionValue("lessThanEqual", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.LessThanEqual)));
        operatorMapping.setConverter(operatorConverter);
        operatorMapping.setXPath("@operator");
        descriptor.addMapping(operatorMapping);

        XMLCompositeObjectMapping leftMapping = new XMLCompositeObjectMapping();
        leftMapping.setAttributeName("firstChild");
        leftMapping.setGetMethodName("getFirstChild");
        leftMapping.setSetMethodName("setFirstChild");
        leftMapping.setReferenceClass(Expression.class);
        leftMapping.setXPath(getPrimaryNamespaceXPath() + "left");
        descriptor.addMapping(leftMapping);

        XMLCompositeObjectMapping rightMapping = new XMLCompositeObjectMapping();
        rightMapping.setAttributeName("secondChild");
        rightMapping.setGetMethodName("getSecondChild");
        rightMapping.setSetMethodName("setSecondChild");
        rightMapping.setReferenceClass(Expression.class);
        rightMapping.setXPath(getPrimaryNamespaceXPath() + "right");
        descriptor.addMapping(rightMapping);

        return descriptor;
    }

    protected ClassDescriptor buildExpressionBuilderDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ExpressionBuilder.class);
        descriptor.setDefaultRootElement("base-expression");

        descriptor.getInheritancePolicy().setParentClass(Expression.class);

        return descriptor;
    }

    protected ClassDescriptor buildConstantExpressionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ConstantExpression.class);
        descriptor.setDefaultRootElement("constant-expression");

        descriptor.getInheritancePolicy().setParentClass(Expression.class);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setField(buildTypedField(getPrimaryNamespaceXPath() + "value/text()"));
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

    protected ClassDescriptor buildQueryKeyExpressionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(QueryKeyExpression.class);
        descriptor.setDefaultRootElement("query-key-expression");

        descriptor.getInheritancePolicy().setParentClass(Expression.class);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping shouldUseOuterJoinMapping = new XMLDirectMapping();
        shouldUseOuterJoinMapping.setAttributeName("shouldUseOuterJoin");
        shouldUseOuterJoinMapping.setNullValue(Boolean.FALSE);
        shouldUseOuterJoinMapping.setXPath("@outer-join");
        descriptor.addMapping(shouldUseOuterJoinMapping);

        XMLDirectMapping toManyRelationshipMapping = new XMLDirectMapping();
        toManyRelationshipMapping.setAttributeName("shouldQueryToManyRelationship");
        toManyRelationshipMapping.setNullValue(Boolean.FALSE);
        toManyRelationshipMapping.setXPath("@any-of");
        descriptor.addMapping(toManyRelationshipMapping);

        XMLCompositeObjectMapping baseMapping = new XMLCompositeObjectMapping();
        baseMapping.setAttributeName("baseExpression");
        baseMapping.setReferenceClass(Expression.class);
        baseMapping.setXPath(getPrimaryNamespaceXPath() + "base");
        descriptor.addMapping(baseMapping);

        return descriptor;
    }

    protected ClassDescriptor buildParameterExpressionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ParameterExpression.class);
        descriptor.setDefaultRootElement("parameter-expression");

        descriptor.getInheritancePolicy().setParentClass(Expression.class);

        XMLCompositeObjectMapping parameterMapping = new XMLCompositeObjectMapping();
        parameterMapping.setAttributeName("field");
        parameterMapping.setReferenceClass(DatabaseField.class);
        parameterMapping.setXPath(getPrimaryNamespaceXPath() + "parameter");
        ((XMLField)parameterMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(parameterMapping);

        XMLCompositeObjectMapping baseMapping = new XMLCompositeObjectMapping();
        baseMapping.setAttributeName("baseExpression");
        baseMapping.setReferenceClass(Expression.class);
        baseMapping.setXPath(getPrimaryNamespaceXPath() + "base");
        descriptor.addMapping(baseMapping);

        return descriptor;
    }

    protected ClassDescriptor buildFieldExpressionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FieldExpression.class);
        descriptor.setDefaultRootElement("field-expression");

        descriptor.getInheritancePolicy().setParentClass(Expression.class);

        XMLCompositeObjectMapping parameterMapping = new XMLCompositeObjectMapping();
        parameterMapping.setAttributeName("field");
        parameterMapping.setReferenceClass(DatabaseField.class);
        parameterMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        ((XMLField)parameterMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(parameterMapping);

        XMLCompositeObjectMapping baseMapping = new XMLCompositeObjectMapping();
        baseMapping.setAttributeName("baseExpression");
        baseMapping.setReferenceClass(Expression.class);
        baseMapping.setXPath(getPrimaryNamespaceXPath() + "base");
        descriptor.addMapping(baseMapping);

        return descriptor;
    }

    protected ClassDescriptor buildFunctionExpressionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FunctionExpression.class);
        descriptor.setDefaultRootElement("function-expression");

        descriptor.getInheritancePolicy().setParentClass(Expression.class);

        // A function's base is always its first child so not persisted,
        // Is fixed up to be its first child or new expression builder if no children.
        // Child value expressions need their backpointer to their local base set,
        // this is not persisted so must be hooked back up after loading.
        descriptor.getEventManager().addListener(new DescriptorEventAdapter() {
            public void postBuild(DescriptorEvent event) {
                FunctionExpression expression = (FunctionExpression)event.getObject();
                for (int index = 0; index < expression.getChildren().size(); index++) {
                    Expression child = (Expression)expression.getChildren().get(index);
                    if (child.isValueExpression()) {
                        child.setLocalBase(new ExpressionBuilder());
                    }
                }
                if (expression.getChildren().size() > 0) {
                    expression.setBaseExpression((Expression)expression.getChildren().get(0));
                }
                else {
                    expression.setBaseExpression(new ExpressionBuilder());
                }
            }
        });

        XMLDirectMapping operatorMapping = new XMLDirectMapping();
        operatorMapping.setAttributeName("operator");
        ExpressionOperatorConverter operatorConverter = new ExpressionOperatorConverter();
        operatorConverter.addConversionValue("like", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Like)));
        operatorConverter.addConversionValue("notLike", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.NotLike)));
        operatorConverter.addConversionValue("not", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Not)));
        operatorConverter.addConversionValue("isNull", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.IsNull)));
        operatorConverter.addConversionValue("notNull", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.NotNull)));
        operatorConverter.addConversionValue("ascending", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Ascending)));
        operatorConverter.addConversionValue("descending", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Descending)));
        // These are platform specific so not on operator.
        operatorConverter.addConversionValue("upper", new ExpressionOperator(ExpressionOperator.ToUpperCase, NonSynchronizedVector.newInstance(0)));
        operatorConverter.addConversionValue("lower", new ExpressionOperator(ExpressionOperator.ToLowerCase, NonSynchronizedVector.newInstance(0)));
        // Aggregate functions
        operatorConverter.addConversionValue("count", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Count)));
        operatorConverter.addConversionValue("sum", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Sum)));
        operatorConverter.addConversionValue("average", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Average)));
        operatorConverter.addConversionValue("maximum", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Maximum)));
        operatorConverter.addConversionValue("minimum", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Minimum)));
        // standardDeviation is platform specific.
        operatorConverter.addConversionValue("standardDeviation", new ExpressionOperator(ExpressionOperator.StandardDeviation, NonSynchronizedVector.newInstance(0)));
        operatorConverter.addConversionValue("variance", new ExpressionOperator(ExpressionOperator.Variance, NonSynchronizedVector.newInstance(0)));
        operatorConverter.addConversionValue("distinct", ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Distinct)));
        operatorMapping.setConverter(operatorConverter);
        operatorMapping.setXPath("@function");
        descriptor.addMapping(operatorMapping);

        XMLCompositeCollectionMapping childrenMapping = new XMLCompositeCollectionMapping();
        childrenMapping.useCollectionClass(NonSynchronizedVector.class);
        childrenMapping.setAttributeName("children");
        childrenMapping.setReferenceClass(Expression.class);
        childrenMapping.setXPath(getPrimaryNamespaceXPath() + "arguments/" + getPrimaryNamespaceXPath() + "argument");
        descriptor.addMapping(childrenMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDatabaseQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatabaseQuery.class);
        descriptor.setDefaultRootElement("query");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(ReadAllQuery.class, getPrimaryNamespaceXPath() + "read-all-query");
        descriptor.getInheritancePolicy().addClassIndicator(ReadObjectQuery.class, getPrimaryNamespaceXPath() + "read-object-query");
        descriptor.getInheritancePolicy().addClassIndicator(DataReadQuery.class, getPrimaryNamespaceXPath() + "data-read-query");
        descriptor.getInheritancePolicy().addClassIndicator(DataModifyQuery.class, getPrimaryNamespaceXPath() + "data-modify-query");
        descriptor.getInheritancePolicy().addClassIndicator(DirectReadQuery.class, getPrimaryNamespaceXPath() + "direct-read-query");
        descriptor.getInheritancePolicy().addClassIndicator(ValueReadQuery.class, getPrimaryNamespaceXPath() + "value-read-query");
        descriptor.getInheritancePolicy().addClassIndicator(DeleteObjectQuery.class, getPrimaryNamespaceXPath() + "delete-object-query");
        descriptor.getInheritancePolicy().addClassIndicator(DeleteAllQuery.class, getPrimaryNamespaceXPath() + "delete-all-query");
        descriptor.getInheritancePolicy().addClassIndicator(InsertObjectQuery.class, getPrimaryNamespaceXPath() + "insert-object-query");
        descriptor.getInheritancePolicy().addClassIndicator(UpdateObjectQuery.class, getPrimaryNamespaceXPath() + "update-object-query");
        descriptor.getInheritancePolicy().addClassIndicator(DoesExistQuery.class, getPrimaryNamespaceXPath() + "does-exist-query");
        descriptor.getInheritancePolicy().addClassIndicator(ReportQuery.class, getPrimaryNamespaceXPath() + "report-query");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLCompositeObjectMapping expressionMapping = new XMLCompositeObjectMapping();
        expressionMapping.setAttributeName("selectionCriteria");
        expressionMapping.setGetMethodName("getSelectionCriteria");
        expressionMapping.setSetMethodName("setSelectionCriteria");
        expressionMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                return ((DatabaseQuery)object).getSelectionCriteria();
            }

            public void setAttributeValueInObject(Object object, Object value) {
                if (!(object instanceof ObjectLevelReadQuery)) {
                    return;
                }
                ObjectLevelReadQuery query = (ObjectLevelReadQuery)object;
                Expression expression = (Expression)value;
                if (expression != null) {
                    expression = expression.rebuildOn(query.getExpressionBuilder());
                }
                query.setSelectionCriteria(expression);
            }
        });
        expressionMapping.setReferenceClass(Expression.class);
        expressionMapping.setXPath(getSecondaryNamespaceXPath() + "criteria");
        descriptor.addMapping(expressionMapping);

        XMLCompositeCollectionMapping argumentsMapping = new XMLCompositeCollectionMapping();

        // Handle translation of argument lists to query-arguments.
        argumentsMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                DatabaseQuery query = (DatabaseQuery)object;
                List<String> arguments = query.getArguments();
                List<String> types = query.getArgumentTypeNames();
                List<Object> values = query.getArgumentValues();
                Vector queryArguments = new Vector(arguments.size());
                for (int index = 0; index < arguments.size(); index++) {
                    QueryArgument queryArgument = new QueryArgument();
                    queryArgument.setKey(arguments.get(index));
                    if (!types.isEmpty()) {
                        queryArgument.setTypeName(types.get(index));
                    }
                    if (!values.isEmpty()) {
                        queryArgument.setValue(values.get(index));
                    }
                    if (query.hasNullableArguments()
                            && query.getNullableArguments().contains(new DatabaseField((String)queryArgument.getKey()))) {
                        queryArgument.setNullable(true);
                    }
                    queryArguments.add(queryArgument);
                }
                return queryArguments;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                DatabaseQuery query = (DatabaseQuery)object;
                List queryArguments = (List)value;
                List<String> arguments = new ArrayList<String>(queryArguments.size());
                List<Class> types = new ArrayList<Class>(queryArguments.size());
                List<Object> values = new ArrayList<Object>(queryArguments.size());
                for (int index = 0; index < queryArguments.size(); index++) {
                    QueryArgument queryArgument = (QueryArgument)queryArguments.get(index);
                    arguments.add((String)queryArgument.getKey());
                    if (queryArgument.getValue() != null) {
                        values.add(queryArgument.getValue());
                    }
                    if (queryArgument.getType() != null) {
                        types.add(queryArgument.getType());
                    }
                    if (queryArgument.isNullable()) {
                        query.getNullableArguments().add(new DatabaseField((String)queryArgument.getKey()));
                    }
                }
                query.setArguments(arguments);
                if (!types.isEmpty()) {
                    query.setArgumentTypes(types);
                }
                if (!values.isEmpty()) {
                    query.setArgumentValues(values);
                }
            }
        });
        argumentsMapping.setAttributeName("argumentsMapping");
        argumentsMapping.setXPath(getSecondaryNamespaceXPath() + "arguments/" + getSecondaryNamespaceXPath() + "argument");
        argumentsMapping.setReferenceClass(QueryArgument.class);
        descriptor.addMapping(argumentsMapping);

        XMLDirectMapping shouldMaintainCacheMapping = new XMLDirectMapping();
        shouldMaintainCacheMapping.setAttributeName("shouldMaintainCache");
        shouldMaintainCacheMapping.setGetMethodName("shouldMaintainCache");
        shouldMaintainCacheMapping.setSetMethodName("setShouldMaintainCache");
        shouldMaintainCacheMapping.setXPath(getPrimaryNamespaceXPath() + "maintain-cache/text()");
        shouldMaintainCacheMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(shouldMaintainCacheMapping);

        XMLDirectMapping shouldBindAllParametersMapping = new XMLDirectMapping();
        shouldBindAllParametersMapping.setAttributeName("shouldBindAllParameters");
        shouldBindAllParametersMapping.setXPath(getPrimaryNamespaceXPath() + "bind-all-parameters/text()");
        descriptor.addMapping(shouldBindAllParametersMapping);

        XMLDirectMapping shouldCacheStatementMapping = new XMLDirectMapping();
        shouldCacheStatementMapping.setAttributeName("shouldCacheStatement");
        shouldCacheStatementMapping.setXPath(getPrimaryNamespaceXPath() + "cache-statement/text()");
        descriptor.addMapping(shouldCacheStatementMapping);

        XMLDirectMapping queryTimeoutMapping = new XMLDirectMapping();
        queryTimeoutMapping.setAttributeName("queryTimeout");
        queryTimeoutMapping.setGetMethodName("getQueryTimeout");
        queryTimeoutMapping.setSetMethodName("setQueryTimeout");
        queryTimeoutMapping.setXPath(getPrimaryNamespaceXPath() + "timeout/text()");
        queryTimeoutMapping.setNullValue(Integer.valueOf(DescriptorQueryManager.DefaultTimeout));
        descriptor.addMapping(queryTimeoutMapping);

        // feaure 2297
        XMLDirectMapping shouldPrepareMapping = new XMLDirectMapping();
        shouldPrepareMapping.setAttributeName("shouldPrepare");
        shouldPrepareMapping.setGetMethodName("shouldPrepare");
        shouldPrepareMapping.setSetMethodName("setShouldPrepare");
        shouldPrepareMapping.setXPath(getPrimaryNamespaceXPath() + "prepare/text()");
        shouldPrepareMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(shouldPrepareMapping);

        XMLCompositeObjectMapping callMapping = new XMLCompositeObjectMapping();
        callMapping.setAttributeName("call");
        callMapping.setGetMethodName("getDatasourceCall");
        callMapping.setSetMethodName("setDatasourceCall");
        callMapping.setReferenceClass(Call.class);
        callMapping.setXPath(getPrimaryNamespaceXPath() + "call");
        descriptor.addMapping(callMapping);

        XMLCompositeObjectMapping redirectorMapping = new XMLCompositeObjectMapping();
        redirectorMapping.setAttributeName("redirector");
        redirectorMapping.setGetMethodName("getRedirector");
        redirectorMapping.setSetMethodName("setRedirector");
        redirectorMapping.setReferenceClass(MethodBaseQueryRedirector.class);
        redirectorMapping.setXPath(getPrimaryNamespaceXPath() + "query-redirector");
        descriptor.addMapping(redirectorMapping);

        return descriptor;
    }

    protected ClassDescriptor buildQueryResultCachePolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(QueryResultsCachePolicy.class);

        XMLCompositeObjectMapping invalidationPolicyMapping = new XMLCompositeObjectMapping();
        invalidationPolicyMapping.setAttributeName("invalidationPolicy");
        invalidationPolicyMapping.setReferenceClass(CacheInvalidationPolicy.class);
        invalidationPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "invalidation-policy");
        descriptor.addMapping(invalidationPolicyMapping);
        XMLDirectMapping maximumCachedResultsMapping = new XMLDirectMapping();
        maximumCachedResultsMapping.setAttributeName("maximumCachedResults");
        maximumCachedResultsMapping.setGetMethodName("getMaximumCachedResults");
        maximumCachedResultsMapping.setSetMethodName("setMaximumCachedResults");
        maximumCachedResultsMapping.setXPath(getPrimaryNamespaceXPath() + "maximum-cached-results/text()");
        maximumCachedResultsMapping.setNullValue(Integer.valueOf(100));
        descriptor.addMapping(maximumCachedResultsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildCacheInvalidationPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CacheInvalidationPolicy.class);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(NoExpiryCacheInvalidationPolicy.class, getPrimaryNamespaceXPath() + "no-expiry-cache-invalidation-policy");
        descriptor.getInheritancePolicy().addClassIndicator(TimeToLiveCacheInvalidationPolicy.class, getPrimaryNamespaceXPath() + "time-to-live-cache-invalidation-policy");
        descriptor.getInheritancePolicy().addClassIndicator(DailyCacheInvalidationPolicy.class, getPrimaryNamespaceXPath() + "daily-cache-invalidation-policy");

        XMLDirectMapping updateOnReadMapping = new XMLDirectMapping();
        updateOnReadMapping.setAttributeName("shouldUpdateReadTimeOnUpdate");
        updateOnReadMapping.setGetMethodName("shouldUpdateReadTimeOnUpdate");
        updateOnReadMapping.setSetMethodName("setShouldUpdateReadTimeOnUpdate");
        updateOnReadMapping.setXPath(getPrimaryNamespaceXPath() + "update-read-time-on-update/text()");
        updateOnReadMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(updateOnReadMapping);

        return descriptor;
    }

    protected ClassDescriptor buildCallDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Call.class);
        descriptor.setDefaultRootElement("call");
        descriptor.descriptorIsAggregate();

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(SQLCall.class, getPrimaryNamespaceXPath() + "sql-call");
        descriptor.getInheritancePolicy().addClassIndicator(JPQLCall.class, getPrimaryNamespaceXPath() + "ejbql-call");
        return descriptor;
    }

    protected ClassDescriptor buildSQLCallDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SQLCall.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(Call.class);

        XMLDirectMapping sqlStringMapping = new XMLDirectMapping();
        sqlStringMapping.setAttributeName("sqlString");
        sqlStringMapping.setGetMethodName("getSQLString");
        sqlStringMapping.setSetMethodName("setSQLString");
        sqlStringMapping.setXPath(getPrimaryNamespaceXPath() + "sql/text()");
        descriptor.addMapping(sqlStringMapping);

        return descriptor;
    }

    protected ClassDescriptor buildJPQLCallDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(JPQLCall.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(Call.class);

        XMLDirectMapping sqlStringMapping = new XMLDirectMapping();
        sqlStringMapping.setAttributeName("ejbqlString");
        sqlStringMapping.setGetMethodName("getEjbqlString");
        sqlStringMapping.setSetMethodName("setEjbqlString");
        sqlStringMapping.setXPath(getPrimaryNamespaceXPath() + "ejbql/text()");
        descriptor.addMapping(sqlStringMapping);

        return descriptor;
    }

    // feature 2297
    protected ClassDescriptor buildReadQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReadQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseQuery.class);

        XMLDirectMapping maxRowsMapping = new XMLDirectMapping();
        maxRowsMapping.setAttributeName("maxRows");
        maxRowsMapping.setGetMethodName("getMaxRows");
        maxRowsMapping.setSetMethodName("setMaxRows");
        maxRowsMapping.setXPath(getPrimaryNamespaceXPath() + "max-rows/text()");
        maxRowsMapping.setNullValue(Integer.valueOf(0));
        descriptor.addMapping(maxRowsMapping);

        XMLDirectMapping firstResultMapping = new XMLDirectMapping();
        firstResultMapping.setAttributeName("firstResult");
        firstResultMapping.setGetMethodName("getFirstResult");
        firstResultMapping.setSetMethodName("setFirstResult");
        firstResultMapping.setXPath(getPrimaryNamespaceXPath() + "first-result/text()");
        firstResultMapping.setNullValue(Integer.valueOf(0));
        descriptor.addMapping(firstResultMapping);
        XMLDirectMapping fetchSizeMapping = new XMLDirectMapping();
        fetchSizeMapping.setAttributeName("fetchSize");
        fetchSizeMapping.setGetMethodName("getFetchSize");
        fetchSizeMapping.setSetMethodName("setFetchSize");
        fetchSizeMapping.setXPath(getPrimaryNamespaceXPath() + "fetch-size/text()");
        fetchSizeMapping.setNullValue(Integer.valueOf(0));
        descriptor.addMapping(fetchSizeMapping);

        XMLCompositeObjectMapping queryResultCachingPolicyMapping = new XMLCompositeObjectMapping();
        queryResultCachingPolicyMapping.setAttributeName("queryResultCachingPolicy");
        queryResultCachingPolicyMapping.setReferenceClass(QueryResultsCachePolicy.class);
        queryResultCachingPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "query-result-cache-policy");
        descriptor.addMapping(queryResultCachingPolicyMapping);
        return descriptor;
    }

    protected ClassDescriptor buildObjectLevelReadQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectLevelReadQuery.class);

        descriptor.getInheritancePolicy().setParentClass(ReadQuery.class);

        XMLDirectMapping referenceClassMapping = new XMLDirectMapping();
        referenceClassMapping.setAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClass");
        referenceClassMapping.setSetMethodName("setReferenceClass");
        referenceClassMapping.setXPath(getPrimaryNamespaceXPath() + "reference-class/text()");
        descriptor.addMapping(referenceClassMapping);

        XMLDirectMapping refreshIdentityMapping = new XMLDirectMapping();
        refreshIdentityMapping.setAttributeName("shouldRefreshIdentityMapResult");
        refreshIdentityMapping.setGetMethodName("shouldRefreshIdentityMapResult");
        refreshIdentityMapping.setSetMethodName("setShouldRefreshIdentityMapResult");
        refreshIdentityMapping.setXPath(getPrimaryNamespaceXPath() + "refresh/text()");
        refreshIdentityMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(refreshIdentityMapping);

        XMLDirectMapping refreshRemoteIdentityMapping = new XMLDirectMapping();
        refreshRemoteIdentityMapping.setAttributeName("shouldRefreshRemoteIdentityMapResult");
        refreshRemoteIdentityMapping.setGetMethodName("shouldRefreshRemoteIdentityMapResult");
        refreshRemoteIdentityMapping.setSetMethodName("setShouldRefreshRemoteIdentityMapResult");
        refreshRemoteIdentityMapping.setXPath(getPrimaryNamespaceXPath() + "remote-refresh/text()");
        refreshRemoteIdentityMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(refreshRemoteIdentityMapping);

        XMLDirectMapping cascadePolicyMapping = new XMLDirectMapping();
        cascadePolicyMapping.setAttributeName("cascadePolicy");
        cascadePolicyMapping.setGetMethodName("getCascadePolicy");
        cascadePolicyMapping.setSetMethodName("setCascadePolicy");
        ObjectTypeConverter cascadePolicyConverter = new ObjectTypeConverter();
        cascadePolicyConverter.addConversionValue("none", Integer.valueOf(DatabaseQuery.NoCascading));
        cascadePolicyConverter.addConversionValue("all", Integer.valueOf(DatabaseQuery.CascadeAllParts));
        cascadePolicyConverter.addConversionValue("private", Integer.valueOf(DatabaseQuery.CascadePrivateParts));
        cascadePolicyMapping.setConverter(cascadePolicyConverter);
        cascadePolicyMapping.setNullValue(Integer.valueOf(DatabaseQuery.NoCascading));
        cascadePolicyMapping.setXPath(getPrimaryNamespaceXPath() + "cascade-policy/text()");
        descriptor.addMapping(cascadePolicyMapping);

        XMLDirectMapping cacheUsageMapping = new XMLDirectMapping();
        cacheUsageMapping.setAttributeName("cacheUsage");
        cacheUsageMapping.setGetMethodName("getCacheUsage");
        cacheUsageMapping.setSetMethodName("setCacheUsage");
        cacheUsageMapping.setXPath(getPrimaryNamespaceXPath() + "cache-usage/text()");
        ObjectTypeConverter cacheUsageConverter = new ObjectTypeConverter();
        cacheUsageConverter.addConversionValue("exact-primary-key", Integer.valueOf(ObjectLevelReadQuery.CheckCacheByExactPrimaryKey));
        cacheUsageConverter.addConversionValue("primary-key", Integer.valueOf(ObjectLevelReadQuery.CheckCacheByPrimaryKey));
        cacheUsageConverter.addConversionValue("cache-only", Integer.valueOf(ObjectLevelReadQuery.CheckCacheOnly));
        cacheUsageConverter.addConversionValue("cache-then-database", Integer.valueOf(ObjectLevelReadQuery.CheckCacheThenDatabase));
        cacheUsageConverter.addConversionValue("conform", Integer.valueOf(ObjectLevelReadQuery.ConformResultsInUnitOfWork));
        cacheUsageConverter.addConversionValue("none", Integer.valueOf(ObjectLevelReadQuery.DoNotCheckCache));
        cacheUsageConverter.addConversionValue("use-descriptor-setting", Integer.valueOf(ObjectLevelReadQuery.UseDescriptorSetting));
        cacheUsageMapping.setConverter(cacheUsageConverter);
        cacheUsageMapping.setNullValue(Integer.valueOf(ObjectLevelReadQuery.UseDescriptorSetting));
        descriptor.addMapping(cacheUsageMapping);

        XMLDirectMapping lockModeMapping = new XMLDirectMapping();
        lockModeMapping.setAttributeName("lockMode");
        lockModeMapping.setGetMethodName("getLockMode");
        lockModeMapping.setSetMethodName("setLockMode");
        lockModeMapping.setXPath(getPrimaryNamespaceXPath() + "lock-mode/text()");
        ObjectTypeConverter lockModeConverter = new ObjectTypeConverter();
        lockModeConverter.addConversionValue("default", Short.valueOf(ObjectLevelReadQuery.DEFAULT_LOCK_MODE));
        lockModeConverter.addConversionValue("lock", Short.valueOf(ObjectLevelReadQuery.LOCK));
        lockModeConverter.addConversionValue("lock-no-wait", Short.valueOf(ObjectLevelReadQuery.LOCK_NOWAIT));
        lockModeConverter.addConversionValue("none", Short.valueOf(ObjectLevelReadQuery.NO_LOCK));
        lockModeMapping.setConverter(lockModeConverter);
        lockModeMapping.setNullValue(Short.valueOf(ObjectLevelReadQuery.DEFAULT_LOCK_MODE));
        descriptor.addMapping(lockModeMapping);

        XMLDirectMapping distinctStateMapping = new XMLDirectMapping();
        distinctStateMapping.setAttributeName("distinctState");
        distinctStateMapping.setGetMethodName("getDistinctState");
        distinctStateMapping.setSetMethodName("setDistinctState");
        distinctStateMapping.setXPath(getPrimaryNamespaceXPath() + "distinct-state/text()");
        ObjectTypeConverter distinctStateConverter = new ObjectTypeConverter();
        distinctStateConverter.addConversionValue("dont-use-distinct", Short.valueOf(ObjectLevelReadQuery.DONT_USE_DISTINCT));
        distinctStateConverter.addConversionValue("none", Short.valueOf(ObjectLevelReadQuery.UNCOMPUTED_DISTINCT));
        distinctStateConverter.addConversionValue("use-distinct", Short.valueOf(ObjectLevelReadQuery.USE_DISTINCT));
        distinctStateMapping.setConverter(distinctStateConverter);
        distinctStateMapping.setNullValue(Short.valueOf(ObjectLevelReadQuery.UNCOMPUTED_DISTINCT));
        descriptor.addMapping(distinctStateMapping);

        XMLCompositeObjectMapping inMemoryQueryIndirectionPolicyMapping = new XMLCompositeObjectMapping();
        inMemoryQueryIndirectionPolicyMapping.setAttributeName("inMemoryQueryIndirectionPolicy");
        inMemoryQueryIndirectionPolicyMapping.setReferenceClass(InMemoryQueryIndirectionPolicy.class);
        // Handle translation of default to null.
        inMemoryQueryIndirectionPolicyMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                InMemoryQueryIndirectionPolicy policy = ((ObjectLevelReadQuery)object).getInMemoryQueryIndirectionPolicy();
                if (policy.shouldThrowIndirectionException()) {
                    return null;
                }
                return policy;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                if (value == null) {
                    return;
                }
                InMemoryQueryIndirectionPolicy policy = (InMemoryQueryIndirectionPolicy)value;
                ((ObjectLevelReadQuery)object).setInMemoryQueryIndirectionPolicy(policy);
            }
        });
        inMemoryQueryIndirectionPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "in-memory-querying");
        descriptor.addMapping(inMemoryQueryIndirectionPolicyMapping);

        // fetch group setting
        XMLDirectMapping useDefaultFetchGroupMapping = new XMLDirectMapping();
        useDefaultFetchGroupMapping.setAttributeName("shouldUseDefaultFetchGroup");
        useDefaultFetchGroupMapping.setXPath(getPrimaryNamespaceXPath() + "use-default-fetch-group/text()");
        useDefaultFetchGroupMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(useDefaultFetchGroupMapping);

        XMLCompositeObjectMapping fetchGroupMapping = new XMLCompositeObjectMapping();
        fetchGroupMapping.setAttributeName("fetchGroup");
        fetchGroupMapping.setReferenceClass(FetchGroup.class);
        fetchGroupMapping.setXPath(getPrimaryNamespaceXPath() + "fetch-group");
        descriptor.addMapping(fetchGroupMapping);

        XMLDirectMapping fetchGroupNameMapping = new XMLDirectMapping();
        fetchGroupNameMapping.setAttributeName("fetchGroupName");
        fetchGroupNameMapping.setXPath(getPrimaryNamespaceXPath() + "fetch-group-name/text()");
        descriptor.addMapping(fetchGroupNameMapping);

        // shouldUseExclusiveConnection setting
        XMLDirectMapping useExclusiveConnectionMapping = new XMLDirectMapping();
        useExclusiveConnectionMapping.setAttributeName("shouldUseExclusiveConnection");
        useExclusiveConnectionMapping.setXPath(getPrimaryNamespaceXPath() + "use-exclusive-connection/text()");
        useExclusiveConnectionMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(useExclusiveConnectionMapping);

        XMLCompositeCollectionMapping joinedAttributeMapping = new XMLCompositeCollectionMapping();
        joinedAttributeMapping.useCollectionClass(NonSynchronizedVector.class);
        joinedAttributeMapping.setAttributeName("joinedAttributeExpressions");
        joinedAttributeMapping.setGetMethodName("getJoinedAttributeExpressions");
        joinedAttributeMapping.setSetMethodName("setJoinedAttributeExpressions");
        joinedAttributeMapping.setReferenceClass(Expression.class);
        joinedAttributeMapping.setXPath(getPrimaryNamespaceXPath() + "joined-attribute-expressions/" + getPrimaryNamespaceXPath() + "expression");
        descriptor.addMapping(joinedAttributeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildReadObjectQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReadObjectQuery.class);
        descriptor.getInheritancePolicy().setParentClass(ObjectLevelReadQuery.class);

        return descriptor;
    }

    protected ClassDescriptor buildReadAllObjectQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReadAllQuery.class);

        descriptor.getInheritancePolicy().setParentClass(ObjectLevelReadQuery.class);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container");
        descriptor.addMapping(containerPolicyMapping);

        XMLCompositeCollectionMapping batchReadMapping = new XMLCompositeCollectionMapping();
        batchReadMapping.useCollectionClass(NonSynchronizedVector.class);
        batchReadMapping.setAttributeName("batchReadAttributeExpressions");
        batchReadMapping.setGetMethodName("getBatchReadAttributeExpressions");
        batchReadMapping.setSetMethodName("setBatchReadAttributeExpressions");
        batchReadMapping.setReferenceClass(Expression.class);
        batchReadMapping.setXPath(getPrimaryNamespaceXPath() + "batch-read-attribute-expressions/" + getPrimaryNamespaceXPath() + "expression");
        descriptor.addMapping(batchReadMapping);

        XMLCompositeCollectionMapping orderByMapping = new XMLCompositeCollectionMapping();
        orderByMapping.useCollectionClass(NonSynchronizedVector.class);
        orderByMapping.setAttributeName("orderByExpressions");
        orderByMapping.setReferenceClass(Expression.class);
        orderByMapping.setXPath(getPrimaryNamespaceXPath() + "order-by-expressions/" + getPrimaryNamespaceXPath() + "expression");
        descriptor.addMapping(orderByMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDeleteObjectQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DeleteObjectQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseQuery.class);

        return descriptor;
    }

    protected ClassDescriptor buildInsertObjectQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InsertObjectQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseQuery.class);

        return descriptor;
    }

    protected ClassDescriptor buildUpdateObjectQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(UpdateObjectQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseQuery.class);

        return descriptor;
    }

    protected ClassDescriptor buildDoesExistQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DoesExistQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseQuery.class);

        XMLDirectMapping existenceCheckMapping = new XMLDirectMapping();
        existenceCheckMapping.setAttributeName("existenceCheck");
        existenceCheckMapping.setGetMethodName("getExistencePolicy");
        existenceCheckMapping.setSetMethodName("setExistencePolicy");
        existenceCheckMapping.setXPath(getPrimaryNamespaceXPath() + "existence-check/text()");
        ObjectTypeConverter existenceCheckConverter = new ObjectTypeConverter();
        existenceCheckConverter.addConversionValue("check-cache", Integer.valueOf(DoesExistQuery.CheckCache));
        existenceCheckConverter.addConversionValue("check-database", Integer.valueOf(DoesExistQuery.CheckDatabase));
        existenceCheckConverter.addConversionValue("assume-existence", Integer.valueOf(DoesExistQuery.AssumeExistence));
        existenceCheckConverter.addConversionValue("assume-non-existence", Integer.valueOf(DoesExistQuery.AssumeNonExistence));
        existenceCheckMapping.setConverter(existenceCheckConverter);
        existenceCheckMapping.setNullValue(Integer.valueOf(DoesExistQuery.CheckCache));
        descriptor.addMapping(existenceCheckMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDataReadQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DataReadQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseQuery.class);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container");
        descriptor.addMapping(containerPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDataModifyQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DataModifyQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseQuery.class);

        return descriptor;
    }

    protected ClassDescriptor buildDeleteAllQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DeleteAllQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseQuery.class);

        return descriptor;
    }

    protected ClassDescriptor buildDirectReadQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DirectReadQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DataReadQuery.class);

        return descriptor;
    }

    protected ClassDescriptor buildValueReadQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ValueReadQuery.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseQuery.class);

        return descriptor;
    }

    protected ClassDescriptor buildReportQueryDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReportQuery.class);
        descriptor.getInheritancePolicy().setParentClass(ReadAllQuery.class);

        XMLDirectMapping returnChoiceMapping = new XMLDirectMapping();
        returnChoiceMapping.setAttributeName("returnChoice");
        returnChoiceMapping.setXPath(getPrimaryNamespaceXPath() + "return-choice/text()");
        ObjectTypeConverter returnChoiceConverter = new ObjectTypeConverter();
        returnChoiceConverter.addConversionValue("return-single-result", Integer.valueOf(ReportQuery.ShouldReturnSingleResult));
        returnChoiceConverter.addConversionValue("return-single-value", Integer.valueOf(ReportQuery.ShouldReturnSingleValue));
        returnChoiceConverter.addConversionValue("return-single-attribute", Integer.valueOf(ReportQuery.ShouldReturnSingleAttribute));
        returnChoiceMapping.setConverter(returnChoiceConverter);
        returnChoiceMapping.setNullValue(Integer.valueOf(0));
        descriptor.addMapping(returnChoiceMapping);

        XMLDirectMapping retrievePrimaryKeysMapping = new XMLDirectMapping();
        retrievePrimaryKeysMapping.setAttributeName("shouldRetrievePrimaryKeys");
        retrievePrimaryKeysMapping.setXPath(getPrimaryNamespaceXPath() + "retrieve-primary-keys/text()");
        ObjectTypeConverter retrievePrimaryKeysConverter = new ObjectTypeConverter();
        retrievePrimaryKeysConverter.addConversionValue("full-primary-key", Integer.valueOf(ReportQuery.FULL_PRIMARY_KEY));
        retrievePrimaryKeysConverter.addConversionValue("first-primary-key", Integer.valueOf(ReportQuery.FIRST_PRIMARY_KEY));
        retrievePrimaryKeysConverter.addConversionValue("no-primary-key", Integer.valueOf(ReportQuery.NO_PRIMARY_KEY));
        retrievePrimaryKeysMapping.setConverter(retrievePrimaryKeysConverter);
        returnChoiceMapping.setNullValue(Integer.valueOf(ReportQuery.NO_PRIMARY_KEY));
        descriptor.addMapping(retrievePrimaryKeysMapping);

        XMLCompositeCollectionMapping reportItemsMapping = new XMLCompositeCollectionMapping();
        reportItemsMapping.useCollectionClass(NonSynchronizedVector.class);
        reportItemsMapping.setAttributeName("items");
        reportItemsMapping.setReferenceClass(ReportItem.class);
        reportItemsMapping.setXPath(getPrimaryNamespaceXPath() + "report-items/" + getPrimaryNamespaceXPath() + "item");
        descriptor.addMapping(reportItemsMapping);

        XMLCompositeCollectionMapping groupByMapping = new XMLCompositeCollectionMapping();
        groupByMapping.useCollectionClass(NonSynchronizedVector.class);
        groupByMapping.setAttributeName("groupByExpressions");
        groupByMapping.setReferenceClass(Expression.class);
        groupByMapping.setXPath(getPrimaryNamespaceXPath() + "group-by-expressions/" + getPrimaryNamespaceXPath() + "expression");
        descriptor.addMapping(groupByMapping);

        return descriptor;
    }

    protected ClassDescriptor buildReportItemDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReportItem.class);
        descriptor.setDefaultRootElement("item");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath(getPrimaryNamespaceXPath() + "name/text()");
        descriptor.addMapping(nameMapping);

        XMLCompositeObjectMapping attributeExpressionMapping = new XMLCompositeObjectMapping();
        attributeExpressionMapping.setAttributeName("attributeExpression");
        attributeExpressionMapping.setReferenceClass(Expression.class);
        attributeExpressionMapping.setXPath(getPrimaryNamespaceXPath() + "attribute-expression");
        descriptor.addMapping(attributeExpressionMapping);

        return descriptor;
    }

    protected ClassDescriptor buildMethodBaseQueryRedirectorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MethodBaseQueryRedirector.class);
        descriptor.setDefaultRootElement("method-base-query-redirector");

        XMLDirectMapping methodNameMapping = new XMLDirectMapping();
        methodNameMapping.setAttributeName("methodName");
        methodNameMapping.setGetMethodName("getMethodName");
        methodNameMapping.setSetMethodName("setMethodName");
        methodNameMapping.setXPath(getPrimaryNamespaceXPath() + "method-name/text()");
        descriptor.addMapping(methodNameMapping);

        XMLDirectMapping methodClassMapping = new XMLDirectMapping();
        methodClassMapping.setAttributeName("methodClass");
        methodClassMapping.setGetMethodName("getMethodClass");
        methodClassMapping.setSetMethodName("setMethodClass");
        methodClassMapping.setXPath(getPrimaryNamespaceXPath() + "method-class/text()");
        descriptor.addMapping(methodClassMapping);
        return descriptor;
    }

    protected ClassDescriptor buildInMemoryQueryIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InMemoryQueryIndirectionPolicy.class);
        descriptor.setDefaultRootElement("in-memory-querying");

        XMLDirectMapping policyMapping = new XMLDirectMapping();
        policyMapping.setAttributeName("policy");
        policyMapping.setGetMethodName("getPolicy");
        policyMapping.setSetMethodName("setPolicy");
        policyMapping.setXPath(getPrimaryNamespaceXPath() + "policy/text()");
        ObjectTypeConverter policyConverter = new ObjectTypeConverter();
        policyConverter.addConversionValue("ignore-exceptions-return-conformed", Integer.valueOf(InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_CONFORMED));
        policyConverter.addConversionValue("ignore-exceptions-returned-not-conformed", Integer.valueOf(InMemoryQueryIndirectionPolicy.SHOULD_IGNORE_EXCEPTION_RETURN_NOT_CONFORMED));
        policyConverter.addConversionValue("trigger-indirection", Integer.valueOf(InMemoryQueryIndirectionPolicy.SHOULD_THROW_INDIRECTION_EXCEPTION));
        policyConverter.addConversionValue("throw-indirection-exception", Integer.valueOf(InMemoryQueryIndirectionPolicy.SHOULD_TRIGGER_INDIRECTION));
        policyMapping.setConverter(policyConverter);
        policyMapping.setNullValue(Integer.valueOf(InMemoryQueryIndirectionPolicy.SHOULD_THROW_INDIRECTION_EXCEPTION));
        descriptor.addMapping(policyMapping);
        return descriptor;
    }

    protected ClassDescriptor buildDatabaseMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatabaseMapping.class);
        descriptor.setDefaultRootElement("attribute-mapping");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DirectToFieldMapping.class, getPrimaryNamespaceXPath() + "direct-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(TransformationMapping.class, getPrimaryNamespaceXPath() + "transformation-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(OneToOneMapping.class, getPrimaryNamespaceXPath() + "one-to-one-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(VariableOneToOneMapping.class, getPrimaryNamespaceXPath() + "variable-one-to-one-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(OneToManyMapping.class, getPrimaryNamespaceXPath() + "one-to-many-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(ManyToManyMapping.class, getPrimaryNamespaceXPath() + "many-to-many-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(AggregateObjectMapping.class, getPrimaryNamespaceXPath() + "aggregate-object-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(AggregateCollectionMapping.class, getPrimaryNamespaceXPath() + "aggregate-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(DirectCollectionMapping.class, getPrimaryNamespaceXPath() + "direct-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(DirectMapMapping.class, getPrimaryNamespaceXPath() + "direct-map-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(NestedTableMapping.class, getPrimaryNamespaceXPath() + "nested-table-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(StructureMapping.class, getPrimaryNamespaceXPath() + "structure-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(ReferenceMapping.class, getPrimaryNamespaceXPath() + "reference-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(ArrayMapping.class, getPrimaryNamespaceXPath() + "array-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(ObjectArrayMapping.class, getPrimaryNamespaceXPath() + "object-array-mapping");
        DirectToXMLTypeMappingHelper.getInstance().addClassIndicator(descriptor, getPrimaryNamespaceXPath());
        descriptor.getInheritancePolicy().addClassIndicator(AbstractTransformationMapping.class, getPrimaryNamespaceXPath() + "abstract-transformation-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(AbstractCompositeDirectCollectionMapping.class, getPrimaryNamespaceXPath() + "abstract-composite-direct-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(AbstractCompositeObjectMapping.class, getPrimaryNamespaceXPath() + "abstract-composite-object-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(AbstractCompositeCollectionMapping.class, getPrimaryNamespaceXPath() + "abstract-composite-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLDirectMapping.class, getPrimaryNamespaceXPath() + "xml-direct-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLTransformationMapping.class, getPrimaryNamespaceXPath() + "xml-transformation-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLCompositeDirectCollectionMapping.class, getPrimaryNamespaceXPath() + "xml-composite-direct-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLCompositeObjectMapping.class, getPrimaryNamespaceXPath() + "xml-composite-object-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLCompositeCollectionMapping.class, getPrimaryNamespaceXPath() + "xml-composite-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLAnyCollectionMapping.class, getPrimaryNamespaceXPath() + "xml-any-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLAnyObjectMapping.class, getPrimaryNamespaceXPath() + "xml-any-object-mapping");

        descriptor.getInheritancePolicy().addClassIndicator(EISDirectMapping.class, getPrimaryNamespaceXPath() + "eis-direct-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(EISTransformationMapping.class, getPrimaryNamespaceXPath() + "eis-transformation-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(EISCompositeDirectCollectionMapping.class, getPrimaryNamespaceXPath() + "eis-composite-direct-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(EISCompositeObjectMapping.class, getPrimaryNamespaceXPath() + "eis-composite-object-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(EISCompositeCollectionMapping.class, getPrimaryNamespaceXPath() + "eis-composite-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(EISOneToOneMapping.class, getPrimaryNamespaceXPath() + "eis-one-to-one-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(EISOneToManyMapping.class, getPrimaryNamespaceXPath() + "eis-one-to-many-mapping");

        XMLDirectMapping XMLDirectMapping = new XMLDirectMapping();
        XMLDirectMapping.setAttributeName("attributeName");
        XMLDirectMapping.setGetMethodName("getAttributeName");
        XMLDirectMapping.setSetMethodName("setAttributeName");
        XMLDirectMapping.setXPath(getSecondaryNamespaceXPath() + "attribute-name/text()");
        descriptor.addMapping(XMLDirectMapping);

        XMLDirectMapping readonlyMapping = new XMLDirectMapping();
        readonlyMapping.setAttributeName("isReadOnly");
        readonlyMapping.setGetMethodName("isReadOnly");
        readonlyMapping.setSetMethodName("setIsReadOnly");
        readonlyMapping.setXPath(getSecondaryNamespaceXPath() + "read-only/text()");
        readonlyMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(readonlyMapping);

        XMLDirectMapping XMLDirectMapping3 = new XMLDirectMapping();
        XMLDirectMapping3.setAttributeName("getMethodName");
        XMLDirectMapping3.setGetMethodName("getGetMethodName");
        XMLDirectMapping3.setSetMethodName("setGetMethodName");
        XMLDirectMapping3.setXPath(getSecondaryNamespaceXPath() + "get-method/text()");
        descriptor.addMapping(XMLDirectMapping3);

        XMLDirectMapping XMLDirectMapping4 = new XMLDirectMapping();
        XMLDirectMapping4.setAttributeName("setMethodName");
        XMLDirectMapping4.setGetMethodName("getSetMethodName");
        XMLDirectMapping4.setSetMethodName("setSetMethodName");
        XMLDirectMapping4.setXPath(getSecondaryNamespaceXPath() + "set-method/text()");
        descriptor.addMapping(XMLDirectMapping4);

        XMLCompositeCollectionMapping propertiesMapping = new XMLCompositeCollectionMapping();
        propertiesMapping.setAttributeName("properties");
        propertiesMapping.setReferenceClass(PropertyAssociation.class);
        propertiesMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                DatabaseMapping mapping = (DatabaseMapping)object;
                Vector propertyAssociations = new NonSynchronizedVector();
                for (Iterator i = mapping.getProperties().entrySet().iterator(); i.hasNext();) {
                    Map.Entry me = (Map.Entry)i.next();
                    PropertyAssociation propertyAssociation = new PropertyAssociation();
                    propertyAssociation.setKey(me.getKey());
                    propertyAssociation.setValue(me.getValue());
                    propertyAssociations.add(propertyAssociation);
                }
                return propertyAssociations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                DatabaseMapping mapping = (DatabaseMapping)object;
                Vector propertyAssociations = (Vector)value;
                for (int i = 0; i < propertyAssociations.size(); i++) {
                    PropertyAssociation propertyAssociation = (PropertyAssociation)propertyAssociations.get(i);
                    mapping.getProperties().put(propertyAssociation.getKey(), propertyAssociation.getValue());
                }
            }
        });
        propertiesMapping.setXPath(getSecondaryNamespaceXPath() + "properties/" + getSecondaryNamespaceXPath() + "property");
        descriptor.addMapping(propertiesMapping);

        return descriptor;
    }

    protected ClassDescriptor buildClassDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ClassDescriptor.class);
        descriptor.setDefaultRootElement("class-mapping-descriptor");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(ClassDescriptor.class, getPrimaryNamespaceXPath() + "relational-class-mapping-descriptor");
        descriptor.getInheritancePolicy().addClassIndicator(RelationalDescriptor.class, getPrimaryNamespaceXPath() + "relational-class-mapping-descriptor");
        descriptor.getInheritancePolicy().addClassIndicator(ObjectRelationalDataTypeDescriptor.class, getPrimaryNamespaceXPath() + "object-relational-class-mapping-descriptor");
        descriptor.getInheritancePolicy().addClassIndicator(EISDescriptor.class, getPrimaryNamespaceXPath() + "eis-class-mapping-descriptor");
        descriptor.getInheritancePolicy().addClassIndicator(XMLDescriptor.class, getPrimaryNamespaceXPath() + "xml-class-mapping-descriptor");
        descriptor.getInheritancePolicy().addClassIndicator(ClassDescriptor.class, getPrimaryNamespaceXPath() + "class-mapping-descriptor");

        descriptor.getEventManager().setPostBuildSelector("applyAmendmentMethod");

        XMLDirectMapping javaClassMapping = new XMLDirectMapping();
        javaClassMapping.setAttributeName("javaClass");
        javaClassMapping.setGetMethodName("getJavaClass");
        javaClassMapping.setSetMethodName("setJavaClass");
        javaClassMapping.setXPath(getSecondaryNamespaceXPath() + "class/text()");
        descriptor.addMapping(javaClassMapping);

        XMLDirectMapping aliasMapping = new XMLDirectMapping();
        aliasMapping.setAttributeName("alias");
        aliasMapping.setGetMethodName("getAlias");
        aliasMapping.setSetMethodName("setAlias");
        aliasMapping.setXPath(getSecondaryNamespaceXPath() + "alias/text()");
        descriptor.addMapping(aliasMapping);

        XMLCompositeCollectionMapping primaryKeyFieldNamesMapping = new XMLCompositeCollectionMapping();
        primaryKeyFieldNamesMapping.setAttributeName("primaryKeyFields");
        primaryKeyFieldNamesMapping.setReferenceClass(DatabaseField.class);
        primaryKeyFieldNamesMapping.setGetMethodName("getPrimaryKeyFields");
        primaryKeyFieldNamesMapping.setSetMethodName("setPrimaryKeyFields");
        primaryKeyFieldNamesMapping.setXPath(getSecondaryNamespaceXPath() + "primary-key/" + getSecondaryNamespaceXPath() + "field");
        ((XMLField)primaryKeyFieldNamesMapping.getField()).setLeafElementType(fieldQname);
        primaryKeyFieldNamesMapping.useCollectionClass(ArrayList.class);
        descriptor.addMapping(primaryKeyFieldNamesMapping);

        XMLDirectMapping descriptorIsReadOnlyMapping = new XMLDirectMapping();
        descriptorIsReadOnlyMapping.setAttributeName("shouldBeReadOnly");
        descriptorIsReadOnlyMapping.setGetMethodName("shouldBeReadOnly");
        descriptorIsReadOnlyMapping.setSetMethodName("setShouldBeReadOnly");
        descriptorIsReadOnlyMapping.setXPath(getSecondaryNamespaceXPath() + "read-only/text()");
        descriptorIsReadOnlyMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(descriptorIsReadOnlyMapping);

        XMLCompositeObjectMapping inheritancePolicyMapping = new XMLCompositeObjectMapping();
        inheritancePolicyMapping.setAttributeName("inheritancePolicy");
        inheritancePolicyMapping.setGetMethodName("getInheritancePolicyOrNull");
        inheritancePolicyMapping.setSetMethodName("setInheritancePolicy");
        inheritancePolicyMapping.setReferenceClass(InheritancePolicy.class);
        inheritancePolicyMapping.setXPath(getSecondaryNamespaceXPath() + "inheritance");
        descriptor.addMapping(inheritancePolicyMapping);

        XMLCompositeObjectMapping eventManagerMapping = new XMLCompositeObjectMapping();
        eventManagerMapping.setAttributeName("eventManager");
        eventManagerMapping.setGetMethodName("getEventManager");
        eventManagerMapping.setSetMethodName("setEventManager");
        eventManagerMapping.setReferenceClass(DescriptorEventManager.class);
        eventManagerMapping.setXPath(getSecondaryNamespaceXPath() + "events");
        descriptor.addMapping(eventManagerMapping);

        XMLCompositeObjectMapping queryManagerMapping = new XMLCompositeObjectMapping();
        queryManagerMapping.setAttributeName("queryManager");
        queryManagerMapping.setGetMethodName("getQueryManager");
        queryManagerMapping.setSetMethodName("setQueryManager");
        queryManagerMapping.setReferenceClass(DescriptorQueryManager.class);
        queryManagerMapping.setXPath(getSecondaryNamespaceXPath() + "querying");
        descriptor.addMapping(queryManagerMapping);

        XMLCompositeCollectionMapping aggregateCollectionMapping = new XMLCompositeCollectionMapping();
        aggregateCollectionMapping.useCollectionClass(NonSynchronizedVector.class);
        aggregateCollectionMapping.setAttributeName("mappings");
        aggregateCollectionMapping.setReferenceClass(DatabaseMapping.class);
        aggregateCollectionMapping.setXPath(getSecondaryNamespaceXPath() + "attribute-mappings/" + getSecondaryNamespaceXPath() + "attribute-mapping");
        aggregateCollectionMapping.setSetMethodName("setMappings");
        aggregateCollectionMapping.setGetMethodName("getMappings");
        descriptor.addMapping(aggregateCollectionMapping);

        XMLDirectMapping descriptorTypeMapping = new XMLDirectMapping();
        descriptorTypeMapping.setAttributeName("descriptorTypeValue");
        descriptorTypeMapping.setGetMethodName("getDescriptorTypeValue");
        descriptorTypeMapping.setSetMethodName("setDescriptorTypeValue");
        ObjectTypeConverter descriptorTypeConverter = new ObjectTypeConverter();
        descriptorTypeConverter.addConversionValue("aggregate", "Aggregate");
        descriptorTypeConverter.addConversionValue("aggregate-collection", "Aggregate collection");
        descriptorTypeConverter.addConversionValue("composite", "Composite");
        descriptorTypeConverter.addConversionValue("composite-collection", "Composite collection");
        descriptorTypeConverter.addConversionValue("interface", "Interface");
        descriptorTypeConverter.addConversionValue("independent", "Normal");
        descriptorTypeMapping.setConverter(descriptorTypeConverter);
        descriptorTypeMapping.setXPath(getPrimaryNamespaceXPath() + "descriptor-type/text()");
        descriptor.addMapping(descriptorTypeMapping);

        XMLCompositeObjectMapping interfacePolicyMapping = new XMLCompositeObjectMapping();
        interfacePolicyMapping.setAttributeName("interfacePolicy");
        interfacePolicyMapping.setGetMethodName("getInterfacePolicyOrNull");
        interfacePolicyMapping.setSetMethodName("setInterfacePolicy");
        interfacePolicyMapping.setReferenceClass(InterfacePolicy.class);
        interfacePolicyMapping.setXPath(getPrimaryNamespaceXPath() + "interfaces");
        descriptor.addMapping(interfacePolicyMapping);

        XMLCompositeObjectMapping lockingPolicyMapping = new XMLCompositeObjectMapping();
        lockingPolicyMapping.setAttributeName("lockingPolicy");
        lockingPolicyMapping.setGetMethodName("getOptimisticLockingPolicy");
        lockingPolicyMapping.setSetMethodName("setOptimisticLockingPolicy");
        lockingPolicyMapping.setReferenceClass(VersionLockingPolicy.class);
        lockingPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "locking");
        descriptor.addMapping(lockingPolicyMapping);

        XMLDirectMapping sequenceNameMapping = new XMLDirectMapping();
        sequenceNameMapping.setAttributeName("sequenceNumberName");
        sequenceNameMapping.setGetMethodName("getSequenceNumberName");
        sequenceNameMapping.setSetMethodName("setSequenceNumberName");
        sequenceNameMapping.setXPath(getPrimaryNamespaceXPath() + "sequencing/" + getPrimaryNamespaceXPath() + "sequence-name/text()");
        descriptor.addMapping(sequenceNameMapping);

        XMLDirectMapping sequenceFieldMapping = new XMLDirectMapping();
        sequenceFieldMapping.setAttributeName("sequenceNumberField");
        sequenceFieldMapping.setGetMethodName("getSequenceNumberFieldName");
        sequenceFieldMapping.setSetMethodName("setSequenceNumberFieldName");
        sequenceFieldMapping.setXPath(getPrimaryNamespaceXPath() + "sequencing/" + getPrimaryNamespaceXPath() + "sequence-field/@name");
        descriptor.addMapping(sequenceFieldMapping);

        XMLDirectMapping identityMapClassMapping = new XMLDirectMapping();
        identityMapClassMapping.setAttributeName("identityMapClass");
        identityMapClassMapping.setGetMethodName("getIdentityMapClass");
        identityMapClassMapping.setSetMethodName("setIdentityMapClass");
        ObjectTypeConverter identityMapClassConverter = new ObjectTypeConverter();
        identityMapClassConverter.addConversionValue("none", NoIdentityMap.class);
        identityMapClassConverter.addConversionValue("full", FullIdentityMap.class);
        identityMapClassConverter.addConversionValue("cache", CacheIdentityMap.class);
        identityMapClassConverter.addConversionValue("weak-reference", WeakIdentityMap.class);
        identityMapClassConverter.addConversionValue("soft-cache-weak-reference", SoftCacheWeakIdentityMap.class);
        identityMapClassConverter.addConversionValue("hard-cache-weak-reference", HardCacheWeakIdentityMap.class);
        identityMapClassMapping.setConverter(identityMapClassConverter);
        identityMapClassMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "cache-type/text()");
        identityMapClassMapping.setNullValue(SoftCacheWeakIdentityMap.class);
        descriptor.addMapping(identityMapClassMapping);

        XMLDirectMapping remoteIdentityMapClassMapping = new XMLDirectMapping();
        remoteIdentityMapClassMapping.setAttributeName("remoteIdentityMapClass");
        remoteIdentityMapClassMapping.setGetMethodName("getRemoteIdentityMapClass");
        remoteIdentityMapClassMapping.setSetMethodName("setRemoteIdentityMapClass");
        ObjectTypeConverter remoteIdentityMapClassConverter = new ObjectTypeConverter();
        remoteIdentityMapClassConverter.addConversionValue("none", NoIdentityMap.class);
        remoteIdentityMapClassConverter.addConversionValue("full", FullIdentityMap.class);
        remoteIdentityMapClassConverter.addConversionValue("cache", CacheIdentityMap.class);
        remoteIdentityMapClassConverter.addConversionValue("weak-reference", WeakIdentityMap.class);
        remoteIdentityMapClassConverter.addConversionValue("soft-cache-weak-reference", SoftCacheWeakIdentityMap.class);
        remoteIdentityMapClassConverter.addConversionValue("hard-cache-weak-reference", HardCacheWeakIdentityMap.class);
        remoteIdentityMapClassMapping.setConverter(remoteIdentityMapClassConverter);
        remoteIdentityMapClassMapping.setXPath(getPrimaryNamespaceXPath() + "remote-caching/" + getPrimaryNamespaceXPath() + "cache-type/text()");
        remoteIdentityMapClassMapping.setNullValue(SoftCacheWeakIdentityMap.class);
        descriptor.addMapping(remoteIdentityMapClassMapping);

        XMLDirectMapping identityMapSizeMapping = new XMLDirectMapping();
        identityMapSizeMapping.setAttributeName("identityMapSize");
        identityMapSizeMapping.setGetMethodName("getIdentityMapSize");
        identityMapSizeMapping.setSetMethodName("setIdentityMapSize");
        identityMapSizeMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "cache-size/text()");
        identityMapSizeMapping.setNullValue(Integer.valueOf(100));
        descriptor.addMapping(identityMapSizeMapping);

        XMLDirectMapping remoteIdentityMapSizeMapping = new XMLDirectMapping();
        remoteIdentityMapSizeMapping.setAttributeName("remoteIdentityMapSize");
        remoteIdentityMapSizeMapping.setGetMethodName("getRemoteIdentityMapSize");
        remoteIdentityMapSizeMapping.setSetMethodName("setRemoteIdentityMapSize");
        remoteIdentityMapSizeMapping.setXPath(getPrimaryNamespaceXPath() + "remote-caching/" + getPrimaryNamespaceXPath() + "cache-size/text()");
        remoteIdentityMapSizeMapping.setNullValue(Integer.valueOf(100));
        descriptor.addMapping(remoteIdentityMapSizeMapping);

        XMLDirectMapping shouldAlwaysRefreshCacheMapping = new XMLDirectMapping();
        shouldAlwaysRefreshCacheMapping.setAttributeName("shouldAlwaysRefreshCache");
        shouldAlwaysRefreshCacheMapping.setGetMethodName("shouldAlwaysRefreshCache");
        shouldAlwaysRefreshCacheMapping.setSetMethodName("setShouldAlwaysRefreshCache");
        shouldAlwaysRefreshCacheMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "always-refresh/text()");
        shouldAlwaysRefreshCacheMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldAlwaysRefreshCacheMapping);

        XMLDirectMapping shouldAlwaysRefreshCacheOnRemoteMapping = new XMLDirectMapping();
        shouldAlwaysRefreshCacheOnRemoteMapping.setAttributeName("shouldAlwaysRefreshCacheOnRemote");
        shouldAlwaysRefreshCacheOnRemoteMapping.setGetMethodName("shouldAlwaysRefreshCacheOnRemote");
        shouldAlwaysRefreshCacheOnRemoteMapping.setSetMethodName("setShouldAlwaysRefreshCacheOnRemote");
        shouldAlwaysRefreshCacheOnRemoteMapping.setXPath(getPrimaryNamespaceXPath() + "remote-caching/" + getPrimaryNamespaceXPath() + "always-refresh/text()");
        shouldAlwaysRefreshCacheOnRemoteMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldAlwaysRefreshCacheOnRemoteMapping);

        XMLDirectMapping shouldOnlyRefreshCacheIfNewerVersionMapping = new XMLDirectMapping();
        shouldOnlyRefreshCacheIfNewerVersionMapping.setAttributeName("shouldOnlyRefreshCacheIfNewerVersion");
        shouldOnlyRefreshCacheIfNewerVersionMapping.setGetMethodName("shouldOnlyRefreshCacheIfNewerVersion");
        shouldOnlyRefreshCacheIfNewerVersionMapping.setSetMethodName("setShouldOnlyRefreshCacheIfNewerVersion");
        shouldOnlyRefreshCacheIfNewerVersionMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "only-refresh-cache-if-newer-version/text()");
        shouldOnlyRefreshCacheIfNewerVersionMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldOnlyRefreshCacheIfNewerVersionMapping);

        XMLDirectMapping shouldDisableCacheHitsMapping = new XMLDirectMapping();
        shouldDisableCacheHitsMapping.setAttributeName("shouldDisableCacheHits");
        shouldDisableCacheHitsMapping.setGetMethodName("shouldDisableCacheHits");
        shouldDisableCacheHitsMapping.setSetMethodName("setShouldDisableCacheHits");
        shouldDisableCacheHitsMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "disable-cache-hits/text()");
        shouldDisableCacheHitsMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldDisableCacheHitsMapping);

        XMLDirectMapping shouldDisableCacheHitsOnRemoteMapping = new XMLDirectMapping();
        shouldDisableCacheHitsOnRemoteMapping.setAttributeName("shouldDisableCacheHitsOnRemote");
        shouldDisableCacheHitsOnRemoteMapping.setGetMethodName("shouldDisableCacheHitsOnRemote");
        shouldDisableCacheHitsOnRemoteMapping.setSetMethodName("setShouldDisableCacheHitsOnRemote");
        shouldDisableCacheHitsOnRemoteMapping.setXPath(getPrimaryNamespaceXPath() + "remote-caching/" + getPrimaryNamespaceXPath() + "disable-cache-hits/text()");
        shouldDisableCacheHitsOnRemoteMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldDisableCacheHitsOnRemoteMapping);

        XMLDirectMapping shouldAlwaysConformResultsInUnitOfWorkMapping = new XMLDirectMapping();
        shouldAlwaysConformResultsInUnitOfWorkMapping.setAttributeName("shouldAlwaysConformResultsInUnitOfWork");
        shouldAlwaysConformResultsInUnitOfWorkMapping.setGetMethodName("shouldAlwaysConformResultsInUnitOfWork");
        shouldAlwaysConformResultsInUnitOfWorkMapping.setSetMethodName("setShouldAlwaysConformResultsInUnitOfWork");
        shouldAlwaysConformResultsInUnitOfWorkMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "always-conform/text()");
        shouldAlwaysConformResultsInUnitOfWorkMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldAlwaysConformResultsInUnitOfWorkMapping);

        XMLDirectMapping isIsolatedMapping = new XMLDirectMapping();
        isIsolatedMapping.setAttributeName("isIsolated");
        isIsolatedMapping.setGetMethodName("isIsolated");
        isIsolatedMapping.setSetMethodName("setIsIsolated");
        isIsolatedMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "isolated/text()");
        isIsolatedMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(isIsolatedMapping);
        XMLDirectMapping unitOfWorkCacheIsolationLevelMapping = new XMLDirectMapping();
        unitOfWorkCacheIsolationLevelMapping.setAttributeName("unitOfWorkCacheIsolationLevel");
        unitOfWorkCacheIsolationLevelMapping.setGetMethodName("getUnitOfWorkCacheIsolationLevel");
        unitOfWorkCacheIsolationLevelMapping.setSetMethodName("setUnitOfWorkCacheIsolationLevel");
        unitOfWorkCacheIsolationLevelMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "unitofwork-isolation-level/text()");
        ObjectTypeConverter unitOfWorkCacheIsolationLevelConverter = new ObjectTypeConverter();
        unitOfWorkCacheIsolationLevelConverter.addConversionValue("use-session-cache-after-transaction", Integer.valueOf(ClassDescriptor.USE_SESSION_CACHE_AFTER_TRANSACTION));
        unitOfWorkCacheIsolationLevelConverter.addConversionValue("isolate-new-data-after-transaction", Integer.valueOf(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION));
        unitOfWorkCacheIsolationLevelConverter.addConversionValue("isolate-cache-after-transaction", Integer.valueOf(ClassDescriptor.ISOLATE_CACHE_AFTER_TRANSACTION));
        unitOfWorkCacheIsolationLevelConverter.addConversionValue("isolate-cache-always", Integer.valueOf(ClassDescriptor.ISOLATE_CACHE_ALWAYS));
        unitOfWorkCacheIsolationLevelMapping.setConverter(unitOfWorkCacheIsolationLevelConverter);
        unitOfWorkCacheIsolationLevelMapping.setNullValue(Integer.valueOf(ClassDescriptor.ISOLATE_NEW_DATA_AFTER_TRANSACTION));
        descriptor.addMapping(unitOfWorkCacheIsolationLevelMapping);

        XMLCompositeObjectMapping cacheInvalidationPolicyMapping = new XMLCompositeObjectMapping();
        cacheInvalidationPolicyMapping.setAttributeName("cacheInvalidationPolicy");
        cacheInvalidationPolicyMapping.setReferenceClass(CacheInvalidationPolicy.class);
        cacheInvalidationPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "cache-invalidation-policy");
        descriptor.addMapping(cacheInvalidationPolicyMapping);

        XMLDirectMapping cacheSyncTypeMapping = new XMLDirectMapping();
        cacheSyncTypeMapping.setAttributeName("cacheSynchronizationType");
        cacheSyncTypeMapping.setGetMethodName("getCacheSynchronizationType");
        cacheSyncTypeMapping.setSetMethodName("setCacheSynchronizationType");
        cacheSyncTypeMapping.setXPath(getPrimaryNamespaceXPath() + "caching/" + getPrimaryNamespaceXPath() + "cache-sync-type/text()");
        ObjectTypeConverter cacheSyncTypeConverter = new ObjectTypeConverter();
        cacheSyncTypeConverter.addConversionValue("invalidation", Integer.valueOf(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS));
        cacheSyncTypeConverter.addConversionValue("no-changes", Integer.valueOf(ClassDescriptor.DO_NOT_SEND_CHANGES));
        cacheSyncTypeConverter.addConversionValue("change-set-with-new-objects", Integer.valueOf(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES));
        cacheSyncTypeConverter.addConversionValue("change-set", Integer.valueOf(ClassDescriptor.SEND_OBJECT_CHANGES));
        cacheSyncTypeMapping.setConverter(cacheSyncTypeConverter);
        cacheSyncTypeMapping.setNullValue(Integer.valueOf(ClassDescriptor.UNDEFINED_OBJECT_CHANGE_BEHAVIOR));
        descriptor.addMapping(cacheSyncTypeMapping);

        XMLCompositeObjectMapping historyPolicyMapping = new XMLCompositeObjectMapping();
        historyPolicyMapping.setAttributeName("historyPolicy");
        historyPolicyMapping.setGetMethodName("getHistoryPolicy");
        historyPolicyMapping.setSetMethodName("setHistoryPolicy");
        historyPolicyMapping.setReferenceClass(HistoryPolicy.class);
        historyPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "history-policy");
        descriptor.addMapping(historyPolicyMapping);

        XMLCompositeObjectMapping returningPolicyMapping = new XMLCompositeObjectMapping();
        returningPolicyMapping.setAttributeName("returningPolicy");
        returningPolicyMapping.setGetMethodName("getReturningPolicy");
        returningPolicyMapping.setSetMethodName("setReturningPolicy");
        returningPolicyMapping.setReferenceClass(ReturningPolicy.class);
        returningPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "returning-policy");
        descriptor.addMapping(returningPolicyMapping);

        XMLDirectMapping amendmentClassMapping = new XMLDirectMapping();
        amendmentClassMapping.setAttributeName("amendmentClass");
        amendmentClassMapping.setGetMethodName("getAmendmentClass");
        amendmentClassMapping.setSetMethodName("setAmendmentClass");
        amendmentClassMapping.setXPath(getPrimaryNamespaceXPath() + "amendment/" + getPrimaryNamespaceXPath() + "amendment-class/text()");
        descriptor.addMapping(amendmentClassMapping);

        XMLDirectMapping amendmentMethodNameMapping = new XMLDirectMapping();
        amendmentMethodNameMapping.setAttributeName("amendmentMethodName");
        amendmentMethodNameMapping.setGetMethodName("getAmendmentMethodName");
        amendmentMethodNameMapping.setSetMethodName("setAmendmentMethodName");
        amendmentMethodNameMapping.setXPath(getPrimaryNamespaceXPath() + "amendment/" + getPrimaryNamespaceXPath() + "amendment-method/text()");
        descriptor.addMapping(amendmentMethodNameMapping);

        XMLCompositeObjectMapping instantiationPolicyMapping = new XMLCompositeObjectMapping();
        instantiationPolicyMapping.setAttributeName("instantiationPolicy");
        instantiationPolicyMapping.setGetMethodName("getInstantiationPolicy");
        instantiationPolicyMapping.setSetMethodName("setInstantiationPolicy");
        instantiationPolicyMapping.setReferenceClass(InstantiationPolicy.class);
        instantiationPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "instantiation");
        descriptor.addMapping(instantiationPolicyMapping);

        XMLCompositeObjectMapping copyPolicyMapping = new XMLCompositeObjectMapping();
        copyPolicyMapping.setAttributeName("copyPolicy");
        copyPolicyMapping.setGetMethodName("getCopyPolicy");
        copyPolicyMapping.setSetMethodName("setCopyPolicy");
        copyPolicyMapping.setReferenceClass(AbstractCopyPolicy.class);
        copyPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "copying");
        descriptor.addMapping(copyPolicyMapping);

        XMLCompositeCollectionMapping queryKeysMapping = new XMLCompositeCollectionMapping();
        queryKeysMapping.setAttributeName("queryKeys");
        queryKeysMapping.setReferenceClass(QueryKey.class);
        queryKeysMapping.setXPath(getPrimaryNamespaceXPath() + "query-keys/" + getPrimaryNamespaceXPath() + "query-key");
        queryKeysMapping.setSetMethodName("setQueryKeys");
        queryKeysMapping.setGetMethodName("getQueryKeys");
        queryKeysMapping.useMapClass(HashMap.class, "getName");
        descriptor.addMapping(queryKeysMapping);

        XMLCompositeObjectMapping cmpPolicyMapping = new XMLCompositeObjectMapping();
        cmpPolicyMapping.setAttributeName("cmpPolicy");
        cmpPolicyMapping.setGetMethodName("getCMPPolicy");
        cmpPolicyMapping.setSetMethodName("setCMPPolicy");
        cmpPolicyMapping.setReferenceClass(CMPPolicy.class);
        cmpPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "cmp-policy");
        descriptor.addMapping(cmpPolicyMapping);

        XMLCompositeObjectMapping fetchGroupManagerMapping = new XMLCompositeObjectMapping();
        fetchGroupManagerMapping.setAttributeName("fetchGroupManager");
        fetchGroupManagerMapping.setGetMethodName("getFetchGroupManager");
        fetchGroupManagerMapping.setSetMethodName("setFetchGroupManager");
        fetchGroupManagerMapping.setReferenceClass(FetchGroupManager.class);
        fetchGroupManagerMapping.setXPath(getPrimaryNamespaceXPath() + "fetch-groups");
        descriptor.addMapping(fetchGroupManagerMapping);

        XMLCompositeObjectMapping changePolicyMapping = new XMLCompositeObjectMapping();
        changePolicyMapping.setAttributeName("changePolicy");
        changePolicyMapping.setReferenceClass(ObjectChangePolicy.class);
        changePolicyMapping.setXPath(getPrimaryNamespaceXPath() + "change-policy");
        descriptor.addMapping(changePolicyMapping);

        XMLCompositeCollectionMapping propertiesMapping = new XMLCompositeCollectionMapping();
        propertiesMapping.setAttributeName("properties");
        propertiesMapping.setReferenceClass(PropertyAssociation.class);
        propertiesMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                ClassDescriptor desc = (ClassDescriptor)object;
                Vector propertyAssociations = new NonSynchronizedVector();
                for (Iterator i = desc.getProperties().entrySet().iterator(); i.hasNext();) {
                    Map.Entry me = (Map.Entry)i.next();
                    PropertyAssociation propertyAssociation = new PropertyAssociation();
                    propertyAssociation.setKey(me.getKey());
                    propertyAssociation.setValue(me.getValue());
                    propertyAssociations.add(propertyAssociation);
                }
                return propertyAssociations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                ClassDescriptor desc = (ClassDescriptor)object;
                Vector propertyAssociations = (Vector)value;
                for (int i = 0; i < propertyAssociations.size(); i++) {
                    PropertyAssociation propertyAssociation = (PropertyAssociation)propertyAssociations.get(i);
                    desc.getProperties().put(propertyAssociation.getKey(), propertyAssociation.getValue());
                }
            }
        });
        propertiesMapping.setXPath(getSecondaryNamespaceXPath() + "properties/" + getSecondaryNamespaceXPath() + "property");
        descriptor.addMapping(propertiesMapping);

        return descriptor;
    }

    protected ClassDescriptor buildRelationalDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RelationalDescriptor.class);
        descriptor.getInheritancePolicy().setParentClass(ClassDescriptor.class);

        XMLCompositeCollectionMapping tablesMapping = new XMLCompositeCollectionMapping();
        tablesMapping.useCollectionClass(NonSynchronizedVector.class);
        tablesMapping.setAttributeName("tables/table");
        tablesMapping.setGetMethodName("getTables");
        tablesMapping.setSetMethodName("setTables");
        tablesMapping.setXPath(getPrimaryNamespaceXPath() + "tables/" + getPrimaryNamespaceXPath() + "table");
        tablesMapping.setReferenceClass(DatabaseTable.class);
        descriptor.addMapping(tablesMapping);

        XMLCompositeCollectionMapping multipleTablesPrimaryKey = new XMLCompositeCollectionMapping();
        multipleTablesPrimaryKey.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                ClassDescriptor mapping = (ClassDescriptor)object;
                Vector associations = mapping.getMultipleTablePrimaryKeyAssociations();
                for (int index = 0; index < associations.size(); index++) {
                    Association association = (Association)associations.get(index);
                    association.setKey(new DatabaseField((String)association.getKey()));
                    association.setValue(new DatabaseField((String)association.getValue()));
                }
                return associations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                ClassDescriptor mapping = (ClassDescriptor)object;
                Vector associations = (Vector)value;
                for (int index = 0; index < associations.size(); index++) {
                    Association association = (Association)associations.get(index);
                    association.setKey(((DatabaseField)association.getKey()).getQualifiedName());
                    association.setValue(((DatabaseField)association.getValue()).getQualifiedName());
                }
                mapping.setForeignKeyFieldNamesForMultipleTable(associations);
            }
        });
        multipleTablesPrimaryKey.setAttributeName("multipleTablesPrimaryKeys");
        multipleTablesPrimaryKey.setReferenceClass(Association.class);
        multipleTablesPrimaryKey.setXPath(getPrimaryNamespaceXPath() + "multiple-table-primary-keys/" + getSecondaryNamespaceXPath() + "field-reference");
        descriptor.addMapping(multipleTablesPrimaryKey);

        XMLCompositeCollectionMapping multipleTables = new XMLCompositeCollectionMapping();
        multipleTables.setReferenceClass(Association.class);
        multipleTables.setXPath(getPrimaryNamespaceXPath() + "multiple-table-foreign-keys/" + getSecondaryNamespaceXPath() + "field-reference");
        multipleTables.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                ClassDescriptor mapping = (ClassDescriptor)object;
                Vector associations = mapping.getMultipleTableForeignKeyAssociations();
                for (int index = 0; index < associations.size(); index++) {
                    Association association = (Association)associations.get(index);
                    association.setKey(new DatabaseField((String)association.getKey()));
                    association.setValue(new DatabaseField((String)association.getValue()));
                }
                return associations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                ClassDescriptor mapping = (ClassDescriptor)object;
                Vector associations = (Vector)value;
                for (int index = 0; index < associations.size(); index++) {
                    Association association = (Association)associations.get(index);
                    association.setKey(((DatabaseField)association.getKey()).getQualifiedName());
                    association.setValue(((DatabaseField)association.getValue()).getQualifiedName());
                }
                mapping.setForeignKeyFieldNamesForMultipleTable(associations);
            }
        });
        multipleTables.setAttributeName("multipleTablesForeignKeys");
        descriptor.addMapping(multipleTables);

        return descriptor;
    }

    protected ClassDescriptor buildDirectCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DirectCollectionMapping.class);

        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        XMLDirectMapping usesBatchReadingMapping = new XMLDirectMapping();
        usesBatchReadingMapping.setAttributeName("usesBatchReading");
        usesBatchReadingMapping.setGetMethodName("shouldUseBatchReading");
        usesBatchReadingMapping.setSetMethodName("setUsesBatchReading");
        usesBatchReadingMapping.setXPath(getPrimaryNamespaceXPath() + "batch-reading/text()");
        usesBatchReadingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesBatchReadingMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("containerPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container");
        descriptor.addMapping(containerPolicyMapping);

        XMLCompositeObjectMapping indirectionPolicyMapping = new XMLCompositeObjectMapping();
        indirectionPolicyMapping.setReferenceClass(IndirectionPolicy.class);
        // Handle translation of NoIndirectionPolicy -> null.
        indirectionPolicyMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                IndirectionPolicy policy = ((ForeignReferenceMapping)object).getIndirectionPolicy();
                if (policy instanceof NoIndirectionPolicy) {
                    return null;
                }
                return policy;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                IndirectionPolicy policy = (IndirectionPolicy)value;
                if (value == null) {
                    policy = new NoIndirectionPolicy();
                }
                ((ForeignReferenceMapping)object).setIndirectionPolicy(policy);
            }
        });
        indirectionPolicyMapping.setAttributeName("indirectionPolicy");
        indirectionPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "indirection");
        descriptor.addMapping(indirectionPolicyMapping);

        XMLCompositeObjectMapping selectionQueryMapping = new XMLCompositeObjectMapping();
        selectionQueryMapping.setAttributeName("selectionQuery");
        selectionQueryMapping.setGetMethodName("getSelectionQuery");
        selectionQueryMapping.setSetMethodName("setSelectionQuery");
        selectionQueryMapping.setReferenceClass(ReadQuery.class);
        selectionQueryMapping.setXPath(getPrimaryNamespaceXPath() + "selection-query");
        descriptor.addMapping(selectionQueryMapping);

        XMLDirectMapping referenceTableMapping = new XMLDirectMapping();
        referenceTableMapping.setAttributeName("referenceTableName");
        // CR#2407 Call getReferenceTableQualifiedName that includes table qualifier.
        referenceTableMapping.setGetMethodName("getReferenceTableQualifiedName");
        referenceTableMapping.setSetMethodName("setReferenceTableName");
        referenceTableMapping.setXPath(getPrimaryNamespaceXPath() + "reference-table/text()");
        descriptor.addMapping(referenceTableMapping);

        XMLCompositeObjectMapping directFieldMapping = new XMLCompositeObjectMapping();
        directFieldMapping.setAttributeName("directField");
        directFieldMapping.setGetMethodName("getDirectField");
        directFieldMapping.setSetMethodName("setDirectField");
        directFieldMapping.setXPath(getPrimaryNamespaceXPath() + "direct-field");
        directFieldMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)directFieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(directFieldMapping);

        XMLCompositeCollectionMapping sourceToReferenceKeyFieldAssociationsMapping = new XMLCompositeCollectionMapping();
        sourceToReferenceKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        // Handle translation of foreign key associations to hashtables.
        sourceToReferenceKeyFieldAssociationsMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                List sourceFields = ((DirectCollectionMapping)object).getSourceKeyFields();
                List referenceFields = ((DirectCollectionMapping)object).getReferenceKeyFields();
                List associations = new ArrayList(sourceFields.size());
                for (int index = 0; index < sourceFields.size(); index++) {
                    associations.add(new Association(referenceFields.get(index), sourceFields.get(index)));
                }
                return associations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                DirectCollectionMapping mapping = (DirectCollectionMapping)object;
                List associations = (List)value;
                mapping.setSourceKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                mapping.setReferenceKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                Iterator iterator = associations.iterator();
                while (iterator.hasNext()) {
                    Association association = (Association)iterator.next();
                    mapping.getSourceKeyFields().add((DatabaseField)association.getValue());
                    mapping.getReferenceKeyFields().add((DatabaseField)association.getKey());
                }
            }
        });
        sourceToReferenceKeyFieldAssociationsMapping.setAttributeName("sourceToReferenceKeyFieldAssociations");
        sourceToReferenceKeyFieldAssociationsMapping.setXPath(getPrimaryNamespaceXPath() + "reference-foreign-key/" + getSecondaryNamespaceXPath() + "field-reference");
        descriptor.addMapping(sourceToReferenceKeyFieldAssociationsMapping);

        XMLCompositeObjectMapping valueConverterMapping = new XMLCompositeObjectMapping();
        valueConverterMapping.setAttributeName("valueConverter");
        valueConverterMapping.setGetMethodName("getValueConverter");
        valueConverterMapping.setSetMethodName("setValueConverter");
        valueConverterMapping.setXPath(getPrimaryNamespaceXPath() + "value-converter");
        valueConverterMapping.setReferenceClass(Converter.class);
        descriptor.addMapping(valueConverterMapping);

        XMLCompositeObjectMapping historyPolicyMapping = new XMLCompositeObjectMapping();
        historyPolicyMapping.setAttributeName("historyPolicy");
        historyPolicyMapping.setGetMethodName("getHistoryPolicy");
        historyPolicyMapping.setSetMethodName("setHistoryPolicy");
        historyPolicyMapping.setReferenceClass(HistoryPolicy.class);
        historyPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "history-policy");
        descriptor.addMapping(historyPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDirectMapMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DirectMapMapping.class);

        descriptor.getInheritancePolicy().setParentClass(DirectCollectionMapping.class);

        XMLCompositeObjectMapping directKeyFieldMapping = new XMLCompositeObjectMapping();
        directKeyFieldMapping.setAttributeName("directKeyField");
        directKeyFieldMapping.setGetMethodName("getDirectKeyField");
        directKeyFieldMapping.setSetMethodName("setDirectKeyField");
        directKeyFieldMapping.setXPath(getPrimaryNamespaceXPath() + "direct-key-field");
        directKeyFieldMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)directKeyFieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(directKeyFieldMapping);

        XMLCompositeObjectMapping keyConverterMapping = new XMLCompositeObjectMapping();
        keyConverterMapping.setAttributeName("keyConverter");
        keyConverterMapping.setGetMethodName("getKeyConverter");
        keyConverterMapping.setSetMethodName("setKeyConverter");
        keyConverterMapping.setXPath(getPrimaryNamespaceXPath() + "key-converter");
        keyConverterMapping.setReferenceClass(Converter.class);
        descriptor.addMapping(keyConverterMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDirectMapContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MappedKeyMapContainerPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(InterfaceContainerPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildDirectQueryKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(DirectQueryKey.class);

        descriptor.getInheritancePolicy().setParentClass(QueryKey.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(fieldMapping);

        return descriptor;
    }

    protected ClassDescriptor buildAbstractDirectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(AbstractDirectMapping.class);

        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath(getSecondaryNamespaceXPath() + "field");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
       descriptor.addMapping(fieldMapping);

        XMLDirectMapping nullValueMapping = new XMLDirectMapping();
        nullValueMapping.setAttributeName("nullValue");
        nullValueMapping.setGetMethodName("getNullValue");
        nullValueMapping.setSetMethodName("setNullValue");
        nullValueMapping.setField(buildTypedField(getSecondaryNamespaceXPath() + "null-value/text()"));
        descriptor.addMapping(nullValueMapping);

        XMLCompositeObjectMapping converterMapping = new XMLCompositeObjectMapping();
        converterMapping.setAttributeName("converter");
        converterMapping.setGetMethodName("getConverter");
        converterMapping.setSetMethodName("setConverter");
        converterMapping.setXPath(getSecondaryNamespaceXPath() + "converter");
        converterMapping.setReferenceClass(Converter.class);
        descriptor.addMapping(converterMapping);
        return descriptor;
    }

    protected ClassDescriptor buildDirectToFieldMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.descriptorIsAggregate();

        descriptor.setJavaClass(DirectToFieldMapping.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractDirectMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildXMLDirectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLDirectMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(AbstractDirectMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildEventManagerDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(DescriptorEventManager.class);
        descriptor.setDefaultRootElement("event-policy");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DescriptorEventManager.class, getPrimaryNamespaceXPath() + "event-policy");

        XMLCompositeDirectCollectionMapping eventListenersMapping = new XMLCompositeDirectCollectionMapping();
        eventListenersMapping.setAttributeName("eventListeners");
        eventListenersMapping.setGetMethodName("getEventListeners");
        eventListenersMapping.setSetMethodName("setEventListeners");
        eventListenersMapping.useCollectionClass(org.eclipse.persistence.internal.helper.NonSynchronizedVector.class);
        eventListenersMapping.setValueConverter(new ClassInstanceConverter());
        eventListenersMapping.setXPath(getSecondaryNamespaceXPath() + "event-listeners/" + getSecondaryNamespaceXPath() + "event-listener/text()");
        descriptor.addMapping(eventListenersMapping);

        XMLDirectMapping postBuildSelectorMapping = new XMLDirectMapping();
        postBuildSelectorMapping.setAttributeName("getPostBuildSelector");
        postBuildSelectorMapping.setGetMethodName("getPostBuildSelector");
        postBuildSelectorMapping.setSetMethodName("setPostBuildSelector");
        postBuildSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "post-build-method/text()");
        descriptor.addMapping(postBuildSelectorMapping);

        XMLDirectMapping preWriteSelectorMapping = new XMLDirectMapping();
        preWriteSelectorMapping.setAttributeName("preWriteSelector");
        preWriteSelectorMapping.setGetMethodName("getPreWriteSelector");
        preWriteSelectorMapping.setSetMethodName("setPreWriteSelector");
        preWriteSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "pre-write-method/text()");
        descriptor.addMapping(preWriteSelectorMapping);

        XMLDirectMapping postWriteSelectorMapping = new XMLDirectMapping();
        postWriteSelectorMapping.setAttributeName("postWriteSelector");
        postWriteSelectorMapping.setGetMethodName("getPostWriteSelector");
        postWriteSelectorMapping.setSetMethodName("setPostWriteSelector");
        postWriteSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "post-write-method/text()");
        descriptor.addMapping(postWriteSelectorMapping);

        XMLDirectMapping preInsertSelectorMapping = new XMLDirectMapping();
        preInsertSelectorMapping.setAttributeName("preInsertSelector");
        preInsertSelectorMapping.setGetMethodName("getPreInsertSelector");
        preInsertSelectorMapping.setSetMethodName("setPreInsertSelector");
        preInsertSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "pre-insert-method/text()");
        descriptor.addMapping(preInsertSelectorMapping);

        XMLDirectMapping postInsertSelectorMapping = new XMLDirectMapping();
        postInsertSelectorMapping.setAttributeName("postInsertSelector");
        postInsertSelectorMapping.setGetMethodName("getPostInsertSelector");
        postInsertSelectorMapping.setSetMethodName("setPostInsertSelector");
        postInsertSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "post-insert-method/text()");
        descriptor.addMapping(postInsertSelectorMapping);

        XMLDirectMapping preUpdateSelectorMapping = new XMLDirectMapping();
        preUpdateSelectorMapping.setAttributeName("preUpdateSelector");
        preUpdateSelectorMapping.setGetMethodName("getPreUpdateSelector");
        preUpdateSelectorMapping.setSetMethodName("setPreUpdateSelector");
        preUpdateSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "pre-update-method/text()");
        descriptor.addMapping(preUpdateSelectorMapping);

        XMLDirectMapping postUpdateSelectorMapping = new XMLDirectMapping();
        postUpdateSelectorMapping.setAttributeName("postUpdateSelector");
        postUpdateSelectorMapping.setGetMethodName("getPostUpdateSelector");
        postUpdateSelectorMapping.setSetMethodName("setPostUpdateSelector");
        postUpdateSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "post-update-method/text()");
        descriptor.addMapping(postUpdateSelectorMapping);

        XMLDirectMapping preDeleteSelectorMapping = new XMLDirectMapping();
        preDeleteSelectorMapping.setAttributeName("preDeleteSelector");
        preDeleteSelectorMapping.setGetMethodName("getPreDeleteSelector");
        preDeleteSelectorMapping.setSetMethodName("setPreDeleteSelector");
        preDeleteSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "pre-delete-method/text()");
        descriptor.addMapping(preDeleteSelectorMapping);

        XMLDirectMapping postDeleteSelectorMapping = new XMLDirectMapping();
        postDeleteSelectorMapping.setAttributeName("postDeleteSelector");
        postDeleteSelectorMapping.setGetMethodName("getPostDeleteSelector");
        postDeleteSelectorMapping.setSetMethodName("setPostDeleteSelector");
        postDeleteSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "post-delete-method/text()");
        descriptor.addMapping(postDeleteSelectorMapping);

        XMLDirectMapping aboutToInsertSelectorMapping = new XMLDirectMapping();
        aboutToInsertSelectorMapping.setAttributeName("aboutToInsertSelector");
        aboutToInsertSelectorMapping.setGetMethodName("getAboutToInsertSelector");
        aboutToInsertSelectorMapping.setSetMethodName("setAboutToInsertSelector");
        aboutToInsertSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "about-to-insert-method/text()");
        descriptor.addMapping(aboutToInsertSelectorMapping);

        XMLDirectMapping aboutToUpdateSelectorMapping = new XMLDirectMapping();
        aboutToUpdateSelectorMapping.setAttributeName("aboutToUpdateSelector");
        aboutToUpdateSelectorMapping.setGetMethodName("getAboutToUpdateSelector");
        aboutToUpdateSelectorMapping.setSetMethodName("setAboutToUpdateSelector");
        aboutToUpdateSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "about-to-update-method/text()");
        descriptor.addMapping(aboutToUpdateSelectorMapping);

        XMLDirectMapping postCloneSelectorMapping = new XMLDirectMapping();
        postCloneSelectorMapping.setAttributeName("postCloneSelector");
        postCloneSelectorMapping.setGetMethodName("getPostCloneSelector");
        postCloneSelectorMapping.setSetMethodName("setPostCloneSelector");
        postCloneSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "post-clone-method/text()");
        descriptor.addMapping(postCloneSelectorMapping);

        XMLDirectMapping postMergeSelectorMapping = new XMLDirectMapping();
        postMergeSelectorMapping.setAttributeName("postMergeSelector");
        postMergeSelectorMapping.setGetMethodName("getPostMergeSelector");
        postMergeSelectorMapping.setSetMethodName("setPostMergeSelector");
        postMergeSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "post-merge-method/text()");
        descriptor.addMapping(postMergeSelectorMapping);

        XMLDirectMapping postRefreshSelectorMapping = new XMLDirectMapping();
        postRefreshSelectorMapping.setAttributeName("getPostRefreshSelector");
        postRefreshSelectorMapping.setGetMethodName("getPostRefreshSelector");
        postRefreshSelectorMapping.setSetMethodName("setPostRefreshSelector");
        postRefreshSelectorMapping.setXPath(getPrimaryNamespaceXPath() + "post-refresh-method/text()");
        descriptor.addMapping(postRefreshSelectorMapping);

        return descriptor;
    }

    protected ClassDescriptor buildForeignReferenceMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ForeignReferenceMapping.class);

        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        XMLDirectMapping referenceClassMapping = new XMLDirectMapping();
        referenceClassMapping.setAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClass");
        referenceClassMapping.setSetMethodName("setReferenceClass");
        referenceClassMapping.setXPath(getSecondaryNamespaceXPath() + "reference-class/text()");
        descriptor.addMapping(referenceClassMapping);

        XMLDirectMapping isPrivateOwnedMapping = new XMLDirectMapping();
        isPrivateOwnedMapping.setAttributeName("isPrivateOwned");
        isPrivateOwnedMapping.setGetMethodName("isPrivateOwned");
        isPrivateOwnedMapping.setSetMethodName("setIsPrivateOwned");
        isPrivateOwnedMapping.setXPath(getSecondaryNamespaceXPath() + "private-owned/text()");
        isPrivateOwnedMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(isPrivateOwnedMapping);

        XMLDirectMapping cascadePersistMapping = new XMLDirectMapping();
        cascadePersistMapping.setAttributeName("cascadePersist");
        cascadePersistMapping.setGetMethodName("isCascadePersist");
        cascadePersistMapping.setSetMethodName("setCascadePersist");
        cascadePersistMapping.setXPath(getSecondaryNamespaceXPath() + "cascade-persist/text()");
        cascadePersistMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(cascadePersistMapping);

        XMLDirectMapping cascadeMergeMapping = new XMLDirectMapping();
        cascadeMergeMapping.setAttributeName("cascadeMerge");
        cascadeMergeMapping.setGetMethodName("isCascadeMerge");
        cascadeMergeMapping.setSetMethodName("setCascadeMerge");
        cascadeMergeMapping.setXPath(getSecondaryNamespaceXPath() + "cascade-merge/text()");
        cascadeMergeMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(cascadeMergeMapping);

        XMLDirectMapping cascadeRefreshMapping = new XMLDirectMapping();
        cascadeRefreshMapping.setAttributeName("cascadeRefresh");
        cascadeRefreshMapping.setGetMethodName("isCascadeRefresh");
        cascadeRefreshMapping.setSetMethodName("setCascadeRefresh");
        cascadeRefreshMapping.setXPath(getSecondaryNamespaceXPath() + "cascade-refresh/text()");
        cascadeRefreshMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(cascadeRefreshMapping);

        XMLDirectMapping cascadeRemoveMapping = new XMLDirectMapping();
        cascadeRemoveMapping.setAttributeName("cascadeRemove");
        cascadeRemoveMapping.setGetMethodName("isCascadeRemove");
        cascadeRemoveMapping.setSetMethodName("setCascadeRemove");
        cascadeRemoveMapping.setXPath(getSecondaryNamespaceXPath() + "cascade-remove/text()");
        cascadeRemoveMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(cascadeRemoveMapping);

        return descriptor;
    }

    protected ClassDescriptor buildHistoryPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(HistoryPolicy.class);
        descriptor.setDefaultRootElement("history-policy");

        XMLCompositeCollectionMapping historyTablesMapping = new XMLCompositeCollectionMapping();
        historyTablesMapping.useCollectionClass(NonSynchronizedVector.class);
        historyTablesMapping.setAttributeName("historicalTables");
        historyTablesMapping.setReferenceClass(HistoricalDatabaseTable.class);
        historyTablesMapping.setGetMethodName("getHistoricalTables");
        historyTablesMapping.setSetMethodName("setHistoricalTables");
        historyTablesMapping.setXPath(getPrimaryNamespaceXPath() + "history-tables/" + getPrimaryNamespaceXPath() + "history-table");
        descriptor.addMapping(historyTablesMapping);

        XMLCompositeCollectionMapping startFieldNamesMapping = new XMLCompositeCollectionMapping();
        startFieldNamesMapping.useCollectionClass(NonSynchronizedVector.class);
        startFieldNamesMapping.setAttributeName("startFields");
        startFieldNamesMapping.setReferenceClass(DatabaseField.class);
        startFieldNamesMapping.setGetMethodName("getStartFields");
        startFieldNamesMapping.setSetMethodName("setStartFields");
        startFieldNamesMapping.setXPath(getPrimaryNamespaceXPath() + "start-fields/" + getPrimaryNamespaceXPath() + "start-field");
        ((XMLField)startFieldNamesMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(startFieldNamesMapping);

        XMLCompositeCollectionMapping endFieldNamesMapping = new XMLCompositeCollectionMapping();
        endFieldNamesMapping.useCollectionClass(NonSynchronizedVector.class);
        endFieldNamesMapping.setAttributeName("endFields");
        endFieldNamesMapping.setReferenceClass(DatabaseField.class);
        endFieldNamesMapping.setGetMethodName("getEndFields");
        endFieldNamesMapping.setSetMethodName("setEndFields");
        endFieldNamesMapping.setXPath(getPrimaryNamespaceXPath() + "end-fields/" + getPrimaryNamespaceXPath() + "end-field");
        ((XMLField)endFieldNamesMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(endFieldNamesMapping);

        XMLDirectMapping shouldHandleWritesMapping = new XMLDirectMapping();
        shouldHandleWritesMapping.setAttributeName("shouldHandleWrites");
        shouldHandleWritesMapping.setGetMethodName("shouldHandleWrites");
        shouldHandleWritesMapping.setSetMethodName("setShouldHandleWrites");
        shouldHandleWritesMapping.setXPath(getPrimaryNamespaceXPath() + "handle-writes/text()");
        shouldHandleWritesMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(shouldHandleWritesMapping);

        XMLDirectMapping useDatabaseTimeMapping = new XMLDirectMapping();
        useDatabaseTimeMapping.setAttributeName("shouldUseLocalTime");
        useDatabaseTimeMapping.setGetMethodName("shouldUseDatabaseTime");
        useDatabaseTimeMapping.setSetMethodName("setShouldUseDatabaseTime");
        useDatabaseTimeMapping.setXPath(getPrimaryNamespaceXPath() + "use-database-time/text()");
        useDatabaseTimeMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(useDatabaseTimeMapping);

        // No support has been added for defining the type of database field,
        // as no support exists for defining the classification of DirectToFieldMappings.
        return descriptor;
    }

    protected ClassDescriptor buildHistoryTableDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(HistoricalDatabaseTable.class);
        descriptor.setDefaultRootElement("history-table");

        XMLDirectMapping sourceMapping = new XMLDirectMapping();
        sourceMapping.setAttributeName("name");
        sourceMapping.setGetMethodName("getName");
        sourceMapping.setSetMethodName("setName");
        sourceMapping.setXPath(getPrimaryNamespaceXPath() + "source/text()");
        descriptor.addMapping(sourceMapping);

        XMLDirectMapping historyMapping = new XMLDirectMapping();
        historyMapping.setAttributeName("historicalName");
        historyMapping.setGetMethodName("getQualifiedName");
        historyMapping.setSetMethodName("setHistoricalName");
        historyMapping.setXPath(getPrimaryNamespaceXPath() + "history/text()");
        descriptor.addMapping(historyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(IndirectionPolicy.class);
        descriptor.setDefaultRootElement("indirection-policy");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(BasicIndirectionPolicy.class, getPrimaryNamespaceXPath() + "value-holder-indirection-policy");
        descriptor.getInheritancePolicy().addClassIndicator(TransparentIndirectionPolicy.class, getPrimaryNamespaceXPath() + "transparent-collection-indirection-policy");
        descriptor.getInheritancePolicy().addClassIndicator(ProxyIndirectionPolicy.class, getPrimaryNamespaceXPath() + "proxy-indirection-policy");
        descriptor.getInheritancePolicy().addClassIndicator(ContainerIndirectionPolicy.class, getPrimaryNamespaceXPath() + "container-indirection-policy");

        return descriptor;
    }

    protected ClassDescriptor buildInheritancePolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InheritancePolicy.class);
        descriptor.setDefaultRootElement("inheritance-policy");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(InheritancePolicy.class, getPrimaryNamespaceXPath() + "inheritance-policy");
        descriptor.getInheritancePolicy().addClassIndicator(QNameInheritancePolicy.class, getPrimaryNamespaceXPath() + "qname-inheritance-policy");
        descriptor.getInheritancePolicy().addClassIndicator(String.class, "string");

        XMLDirectMapping parentClassMapping = new XMLDirectMapping();
        parentClassMapping.setAttributeName("parentClass");
        parentClassMapping.setGetMethodName("getParentClass");
        parentClassMapping.setSetMethodName("setParentClass");
        parentClassMapping.setXPath(getSecondaryNamespaceXPath() + "parent-class/text()");
        descriptor.addMapping(parentClassMapping);

        XMLDirectMapping shouldReadSubclassesMapping = new XMLDirectMapping();
        shouldReadSubclassesMapping.setAttributeName("shouldReadSubclasses");
        shouldReadSubclassesMapping.setGetMethodName("shouldReadSubclassesValue");
        shouldReadSubclassesMapping.setSetMethodName("setShouldReadSubclasses");
        shouldReadSubclassesMapping.setXPath(getPrimaryNamespaceXPath() + "read-subclasses-on-queries/text()");
        descriptor.addMapping(shouldReadSubclassesMapping);

        XMLDirectMapping readAllSubclassesViewMapping = new XMLDirectMapping();
        readAllSubclassesViewMapping.setAttributeName("readAllSubclassesView");
        readAllSubclassesViewMapping.setGetMethodName("getReadAllSubclassesViewName");
        readAllSubclassesViewMapping.setSetMethodName("setReadAllSubclassesViewName");
        readAllSubclassesViewMapping.setXPath(getPrimaryNamespaceXPath() + "all-subclasses-view/text()");
        descriptor.addMapping(readAllSubclassesViewMapping);

        XMLDirectMapping shouldUseClassNameAsIndicatorMapping = new XMLDirectMapping();
        shouldUseClassNameAsIndicatorMapping.setAttributeName("shouldUseClassNameAsIndicator");
        shouldUseClassNameAsIndicatorMapping.setGetMethodName("shouldUseClassNameAsIndicator");
        shouldUseClassNameAsIndicatorMapping.setSetMethodName("setShouldUseClassNameAsIndicator");
        shouldUseClassNameAsIndicatorMapping.setXPath(getPrimaryNamespaceXPath() + "use-class-name-as-indicator/text()");
        shouldUseClassNameAsIndicatorMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(shouldUseClassNameAsIndicatorMapping);

        XMLDirectMapping classExtractionMethodMapping = new XMLDirectMapping();
        classExtractionMethodMapping.setAttributeName("classExtractionMethod");
        classExtractionMethodMapping.setGetMethodName("getClassExtractionMethodName");
        classExtractionMethodMapping.setSetMethodName("setClassExtractionMethodName");
        classExtractionMethodMapping.setXPath(getPrimaryNamespaceXPath() + "class-extraction-method/text()");
        descriptor.addMapping(classExtractionMethodMapping);

        XMLCompositeObjectMapping classIndicatorFieldNameMapping = new XMLCompositeObjectMapping();
        classIndicatorFieldNameMapping.setAttributeName("classIndicatorField");
        classIndicatorFieldNameMapping.setReferenceClass(DatabaseField.class);
        classIndicatorFieldNameMapping.setGetMethodName("getClassIndicatorField");
        classIndicatorFieldNameMapping.setSetMethodName("setClassIndicatorField");
        classIndicatorFieldNameMapping.setXPath(getPrimaryNamespaceXPath() + "class-indicator-field");
        ((XMLField)classIndicatorFieldNameMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(classIndicatorFieldNameMapping);

        XMLCompositeCollectionMapping classIndicatorsMapping = new XMLCompositeCollectionMapping();
        classIndicatorsMapping.setAttributeName("classIndicatorAssociations");
        classIndicatorsMapping.setGetMethodName("getClassIndicatorAssociations");
        classIndicatorsMapping.setSetMethodName("setClassIndicatorAssociations");
        classIndicatorsMapping.setXPath(getPrimaryNamespaceXPath() + "class-indicator-mappings/" + getPrimaryNamespaceXPath() + "class-indicator-mapping");
        classIndicatorsMapping.setReferenceClass(TypedAssociation.class);
        descriptor.addMapping(classIndicatorsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildQNameInheritancePolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(QNameInheritancePolicy.class);

        descriptor.getInheritancePolicy().setParentClass(InheritancePolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Converter.class);

        descriptor.setDefaultRootElement("converter");
        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(ObjectTypeConverter.class, getPrimaryNamespaceXPath() + "object-type-converter");
        descriptor.getInheritancePolicy().addClassIndicator(TypeConversionConverter.class, getPrimaryNamespaceXPath() + "type-conversion-converter");
        descriptor.getInheritancePolicy().addClassIndicator(SerializedObjectConverter.class, getPrimaryNamespaceXPath() + "serialized-object-converter");
        try {
            Class typesafeenumClass = (Class)new PrivilegedClassForName("org.eclipse.persistence.jaxb.JAXBTypesafeEnumConverter").run();
            descriptor.getInheritancePolicy().addClassIndicator(typesafeenumClass, getPrimaryNamespaceXPath() + "typesafe-enumeration-converter");
        }
        catch (ClassNotFoundException cnfe) {
            // The JAXB component isn't available, so no need to do anything
        }
        return descriptor;
    }

    protected ClassDescriptor buildInstantiationPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InstantiationPolicy.class);
        descriptor.setDefaultRootElement("instantiation-policy");

        XMLDirectMapping methodNameMapping = new XMLDirectMapping();
        methodNameMapping.setAttributeName("methodName");
        methodNameMapping.setGetMethodName("getMethodName");
        methodNameMapping.setSetMethodName("setMethodName");
        methodNameMapping.setXPath(getPrimaryNamespaceXPath() + "method/text()");
        descriptor.addMapping(methodNameMapping);

        XMLDirectMapping factoryClassMapping = new XMLDirectMapping();
        factoryClassMapping.setAttributeName("factoryClass");
        factoryClassMapping.setGetMethodName("getFactoryClass");
        factoryClassMapping.setSetMethodName("setFactoryClass");
        factoryClassMapping.setXPath(getPrimaryNamespaceXPath() + "factory-class/text()");
        descriptor.addMapping(factoryClassMapping);

        XMLDirectMapping factoryMethodNameMapping = new XMLDirectMapping();
        factoryMethodNameMapping.setAttributeName("factoryMethod");
        factoryMethodNameMapping.setGetMethodName("getFactoryMethodName");
        factoryMethodNameMapping.setSetMethodName("setFactoryMethodName");
        factoryMethodNameMapping.setXPath(getPrimaryNamespaceXPath() + "factory-method/text()");
        descriptor.addMapping(factoryMethodNameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildInterfaceContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InterfaceContainerPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(ContainerPolicy.class);

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeName("containerClass");
        keyMapping.setGetMethodName("getContainerClass");
        keyMapping.setSetMethodName("setContainerClass");
        keyMapping.setXPath(getPrimaryNamespaceXPath() + "collection-type/text()");
        descriptor.addMapping(keyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildInterfacePolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InterfacePolicy.class);
        descriptor.setDefaultRootElement("interfaces");

        XMLCompositeDirectCollectionMapping parentInterfacesMapping = new XMLCompositeDirectCollectionMapping();
        parentInterfacesMapping.setAttributeElementClass(Class.class);
        parentInterfacesMapping.setAttributeName("parentInterfaces");
        parentInterfacesMapping.setGetMethodName("getParentInterfaces");
        parentInterfacesMapping.setSetMethodName("setParentInterfaces");
        parentInterfacesMapping.setXPath(getPrimaryNamespaceXPath() + "interface/text()");
        descriptor.addMapping(parentInterfacesMapping);

        XMLDirectMapping implementorDescriptorMapping = new XMLDirectMapping();
        implementorDescriptorMapping.setAttributeName("implementorDescriptor");
        implementorDescriptorMapping.setGetMethodName("getImplementorDescriptor");
        implementorDescriptorMapping.setSetMethodName("setImplementorDescriptor");
        implementorDescriptorMapping.setXPath(getPrimaryNamespaceXPath() + "implementor-descriptor/text()");
        descriptor.addMapping(implementorDescriptorMapping);

        return descriptor;
    }

    protected ClassDescriptor buildListContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(ListContainerPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(CollectionContainerPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildManyToManyMappingMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ManyToManyMapping.class);

        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        XMLDirectMapping relationTableMapping = new XMLDirectMapping();
        relationTableMapping.setAttributeName("relationTableName");
        // CR#2407 Call getRelationTableQualifiedName that includes table qualifier.
        relationTableMapping.setGetMethodName("getRelationTableQualifiedName");
        relationTableMapping.setSetMethodName("setRelationTableName");
        relationTableMapping.setXPath(getSecondaryNamespaceXPath() + "relation-table/text()");
        descriptor.addMapping(relationTableMapping);

        XMLCompositeCollectionMapping sourceToRelationKeyFieldAssociationsMapping = new XMLCompositeCollectionMapping();
        sourceToRelationKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        // Handle translation of foreign key associations to hashtables.
        sourceToRelationKeyFieldAssociationsMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                List sourceFields = ((ManyToManyMapping)object).getSourceKeyFields();
                List relationFields = ((ManyToManyMapping)object).getSourceRelationKeyFields();
                List associations = new ArrayList(sourceFields.size());
                for (int index = 0; index < sourceFields.size(); index++) {
                    associations.add(new Association(relationFields.get(index), sourceFields.get(index)));
                }
                return associations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                ManyToManyMapping mapping = (ManyToManyMapping)object;
                List associations = (List)value;
                mapping.setSourceKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                mapping.setSourceRelationKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                Iterator iterator = associations.iterator();
                while (iterator.hasNext()) {
                    Association association = (Association)iterator.next();
                    mapping.getSourceKeyFields().add((DatabaseField)association.getValue());
                    mapping.getSourceRelationKeyFields().add((DatabaseField)association.getKey());
                }
            }
        });
        sourceToRelationKeyFieldAssociationsMapping.setAttributeName("sourceToRelationKeyFieldAssociationsMapping");
        sourceToRelationKeyFieldAssociationsMapping.setXPath(getSecondaryNamespaceXPath() + "source-relation-foreign-key/" + getSecondaryNamespaceXPath() + "field-reference");
        descriptor.addMapping(sourceToRelationKeyFieldAssociationsMapping);

        XMLCompositeCollectionMapping targetToRelationKeyFieldAssociationsMapping = new XMLCompositeCollectionMapping();
        targetToRelationKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        // Handle translation of foreign key associations to hashtables.
        targetToRelationKeyFieldAssociationsMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                List targetFields = ((ManyToManyMapping)object).getTargetKeyFields();
                List relationFields = ((ManyToManyMapping)object).getTargetRelationKeyFields();
                List associations = new ArrayList(targetFields.size());
                for (int index = 0; index < targetFields.size(); index++) {
                    associations.add(new Association(relationFields.get(index), targetFields.get(index)));
                }
                return associations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                ManyToManyMapping mapping = (ManyToManyMapping)object;
                List associations = (List)value;
                mapping.setTargetKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                mapping.setTargetRelationKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                Iterator iterator = associations.iterator();
                while (iterator.hasNext()) {
                    Association association = (Association)iterator.next();
                    mapping.getTargetKeyFields().add((DatabaseField)association.getValue());
                    mapping.getTargetRelationKeyFields().add((DatabaseField)association.getKey());
                }
            }
        });
        targetToRelationKeyFieldAssociationsMapping.setAttributeName("targetToRelationKeyFieldAssociations");
        targetToRelationKeyFieldAssociationsMapping.setXPath(getSecondaryNamespaceXPath() + "target-relation-foreign-key/" + getSecondaryNamespaceXPath() + "field-reference");
        descriptor.addMapping(targetToRelationKeyFieldAssociationsMapping);

        XMLDirectMapping relationshipPartnerAttributeNameMapping = new XMLDirectMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setXPath(getPrimaryNamespaceXPath() + "bidirectional-target-attribute/text()");
        descriptor.addMapping(relationshipPartnerAttributeNameMapping);

        XMLDirectMapping usesBatchReadingMapping = new XMLDirectMapping();
        usesBatchReadingMapping.setAttributeName("usesBatchReading");
        usesBatchReadingMapping.setGetMethodName("shouldUseBatchReading");
        usesBatchReadingMapping.setSetMethodName("setUsesBatchReading");
        usesBatchReadingMapping.setXPath(getPrimaryNamespaceXPath() + "batch-reading/text()");
        usesBatchReadingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesBatchReadingMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container");
        descriptor.addMapping(containerPolicyMapping);

        XMLCompositeObjectMapping indirectionPolicyMapping = new XMLCompositeObjectMapping();
        indirectionPolicyMapping.setReferenceClass(IndirectionPolicy.class);
        // Handle translation of NoIndirectionPolicy -> null.
        indirectionPolicyMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                IndirectionPolicy policy = ((ForeignReferenceMapping)object).getIndirectionPolicy();
                if (policy instanceof NoIndirectionPolicy) {
                    return null;
                }
                return policy;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                IndirectionPolicy policy = (IndirectionPolicy)value;
                if (value == null) {
                    policy = new NoIndirectionPolicy();
                }
                ((ForeignReferenceMapping)object).setIndirectionPolicy(policy);
            }
        });
        indirectionPolicyMapping.setAttributeName("indirectionPolicy");
        indirectionPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "indirection");
        descriptor.addMapping(indirectionPolicyMapping);

        XMLCompositeObjectMapping selectionQueryMapping = new XMLCompositeObjectMapping();
        selectionQueryMapping.setAttributeName("selectionQuery");
        selectionQueryMapping.setGetMethodName("getSelectionQuery");
        selectionQueryMapping.setSetMethodName("setSelectionQuery");
        selectionQueryMapping.setReferenceClass(ReadQuery.class);
        selectionQueryMapping.setXPath(getPrimaryNamespaceXPath() + "selection-query");
        descriptor.addMapping(selectionQueryMapping);

        XMLCompositeObjectMapping insertQueryMapping = new XMLCompositeObjectMapping();
        insertQueryMapping.setAttributeName("insertQuery");
        insertQueryMapping.setGetMethodName("getInsertQuery");
        insertQueryMapping.setSetMethodName("setInsertQuery");
        insertQueryMapping.setReferenceClass(DataModifyQuery.class);
        insertQueryMapping.setXPath(getPrimaryNamespaceXPath() + "insert-query");
        descriptor.addMapping(insertQueryMapping);

        XMLCompositeObjectMapping deleteQueryMapping = new XMLCompositeObjectMapping();
        deleteQueryMapping.setAttributeName("deleteQuery");
        deleteQueryMapping.setGetMethodName("getDeleteQuery");
        deleteQueryMapping.setSetMethodName("setDeleteQuery");
        deleteQueryMapping.setReferenceClass(DataModifyQuery.class);
        deleteQueryMapping.setXPath(getPrimaryNamespaceXPath() + "delete-query");
        descriptor.addMapping(deleteQueryMapping);

        XMLCompositeObjectMapping deleteAllQueryMapping = new XMLCompositeObjectMapping();
        deleteAllQueryMapping.setAttributeName("deleteAllQuery");
        deleteAllQueryMapping.setGetMethodName("getDeleteAllQuery");
        deleteAllQueryMapping.setSetMethodName("setDeleteAllQuery");
        deleteAllQueryMapping.setReferenceClass(DataModifyQuery.class);
        deleteAllQueryMapping.setXPath(getPrimaryNamespaceXPath() + "delete-all-query");
        descriptor.addMapping(deleteAllQueryMapping);

        XMLCompositeObjectMapping historyPolicyMapping = new XMLCompositeObjectMapping();
        historyPolicyMapping.setAttributeName("historyPolicy");
        historyPolicyMapping.setGetMethodName("getHistoryPolicy");
        historyPolicyMapping.setSetMethodName("setHistoryPolicy");
        historyPolicyMapping.setReferenceClass(HistoryPolicy.class);
        historyPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "history-policy");
        descriptor.addMapping(historyPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildMapContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MapContainerPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(InterfaceContainerPolicy.class);

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeName("keyName");
        keyMapping.setGetMethodName("getKeyName");
        keyMapping.setSetMethodName("setKeyName");
        keyMapping.setXPath(getPrimaryNamespaceXPath() + "map-key-method/text()");
        descriptor.addMapping(keyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildNestedTableMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NestedTableMapping.class);

        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(fieldMapping);

        XMLDirectMapping structureMapping = new XMLDirectMapping();
        structureMapping.setAttributeName("structureName");
        structureMapping.setGetMethodName("getStructureName");
        structureMapping.setSetMethodName("setStructureName");
        structureMapping.setXPath(getPrimaryNamespaceXPath() + "structure/text()");
        descriptor.addMapping(structureMapping);

        return descriptor;
    }

    protected ClassDescriptor buildNoExpiryCacheInvalidationPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NoExpiryCacheInvalidationPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(CacheInvalidationPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildObjectArrayMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectArrayMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeCollectionMapping.class);

        XMLDirectMapping structureMapping = new XMLDirectMapping();
        structureMapping.setAttributeName("structureName");
        structureMapping.setGetMethodName("getStructureName");
        structureMapping.setSetMethodName("setStructureName");
        structureMapping.setXPath(getPrimaryNamespaceXPath() + "structure/text()");
        descriptor.addMapping(structureMapping);

        return descriptor;
    }

    protected ClassDescriptor buildObjectReferenceMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectReferenceMapping.class);

        descriptor.getInheritancePolicy().setParentClass(ForeignReferenceMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildObjectRelationalDataTypeDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectRelationalDataTypeDescriptor.class);

        descriptor.getInheritancePolicy().setParentClass(RelationalDescriptor.class);

        XMLDirectMapping structureMapping = new XMLDirectMapping();
        structureMapping.setAttributeName("structureName");
        structureMapping.setGetMethodName("getStructureName");
        structureMapping.setSetMethodName("setStructureName");
        structureMapping.setXPath(getPrimaryNamespaceXPath() + "structure/text()");
        descriptor.addMapping(structureMapping);

        XMLCompositeCollectionMapping orderedFieldsMapping = new XMLCompositeCollectionMapping();
        orderedFieldsMapping.useCollectionClass(NonSynchronizedVector.class);
        orderedFieldsMapping.setAttributeName("orderedFields");
        orderedFieldsMapping.setXPath(getPrimaryNamespaceXPath() + "field-order/" + getPrimaryNamespaceXPath() + "field");
        orderedFieldsMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)orderedFieldsMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(orderedFieldsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildObjectTypeConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectTypeConverter.class);

        descriptor.getInheritancePolicy().setParentClass(Converter.class);

        XMLDirectMapping XMLDirectMapping4 = new XMLDirectMapping();
        XMLDirectMapping4.setAttributeName("defaultAttributeValue");
        XMLDirectMapping4.setGetMethodName("getDefaultAttributeValue");
        XMLDirectMapping4.setSetMethodName("setDefaultAttributeValue");
        XMLDirectMapping4.setXPath(getPrimaryNamespaceXPath() + "default-value/text()");
        descriptor.addMapping(XMLDirectMapping4);

        XMLCompositeCollectionMapping fieldToAttributeValueAssociationsMapping = new XMLCompositeCollectionMapping();
        fieldToAttributeValueAssociationsMapping.setAttributeName("fieldToAttributeValueAssociations");
        fieldToAttributeValueAssociationsMapping.setGetMethodName("getFieldToAttributeValueAssociations");
        fieldToAttributeValueAssociationsMapping.setSetMethodName("setFieldToAttributeValueAssociations");
        fieldToAttributeValueAssociationsMapping.setXPath(getPrimaryNamespaceXPath() + "type-mappings/" + getPrimaryNamespaceXPath() + "type-mapping");
        fieldToAttributeValueAssociationsMapping.setReferenceClass(TypeMapping.class);
        descriptor.addMapping(fieldToAttributeValueAssociationsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildOneToManyMappingMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OneToManyMapping.class);

        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        XMLCompositeCollectionMapping sourceToTargetKeyFieldAssociationsMapping = new XMLCompositeCollectionMapping();
        sourceToTargetKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        // Handle translation of foreign key associations to hashtables.
        sourceToTargetKeyFieldAssociationsMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                List sourceFields = ((OneToManyMapping)object).getSourceKeyFields();
                List targetFields = ((OneToManyMapping)object).getTargetForeignKeyFields();
                List associations = new ArrayList(sourceFields.size());
                for (int index = 0; index < sourceFields.size(); index++) {
                    associations.add(new Association(targetFields.get(index), sourceFields.get(index)));
                }
                return associations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                OneToManyMapping mapping = (OneToManyMapping)object;
                List associations = (List)value;
                mapping.setSourceKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                mapping.setTargetForeignKeyFields(NonSynchronizedVector.newInstance(associations.size()));
                Iterator iterator = associations.iterator();
                while (iterator.hasNext()) {
                    Association association = (Association)iterator.next();
                    mapping.getSourceKeyFields().add((DatabaseField)association.getValue());
                    mapping.getTargetForeignKeyFields().add((DatabaseField)association.getKey());
                }
            }
        });
        sourceToTargetKeyFieldAssociationsMapping.setAttributeName("sourceToTargetKeyFieldAssociations");
        sourceToTargetKeyFieldAssociationsMapping.setXPath(getSecondaryNamespaceXPath() + "target-foreign-key/" + getSecondaryNamespaceXPath() + "field-reference");
        descriptor.addMapping(sourceToTargetKeyFieldAssociationsMapping);

        XMLDirectMapping relationshipPartnerAttributeNameMapping = new XMLDirectMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setXPath(getPrimaryNamespaceXPath() + "bidirectional-target-attribute/text()");
        descriptor.addMapping(relationshipPartnerAttributeNameMapping);

        XMLDirectMapping usesBatchReadingMapping = new XMLDirectMapping();
        usesBatchReadingMapping.setAttributeName("usesBatchReading");
        usesBatchReadingMapping.setGetMethodName("shouldUseBatchReading");
        usesBatchReadingMapping.setSetMethodName("setUsesBatchReading");
        usesBatchReadingMapping.setXPath(getPrimaryNamespaceXPath() + "batch-reading/text()");
        usesBatchReadingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesBatchReadingMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container");
        descriptor.addMapping(containerPolicyMapping);

        XMLCompositeObjectMapping indirectionPolicyMapping = new XMLCompositeObjectMapping();
        indirectionPolicyMapping.setReferenceClass(IndirectionPolicy.class);
        // Handle translation of NoIndirectionPolicy -> null.
        indirectionPolicyMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                IndirectionPolicy policy = ((ForeignReferenceMapping)object).getIndirectionPolicy();
                if (policy instanceof NoIndirectionPolicy) {
                    return null;
                }
                return policy;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                IndirectionPolicy policy = (IndirectionPolicy)value;
                if (value == null) {
                    policy = new NoIndirectionPolicy();
                }
                ((ForeignReferenceMapping)object).setIndirectionPolicy(policy);
            }
        });
        indirectionPolicyMapping.setAttributeName("indirectionPolicy");
        indirectionPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "indirection");
        descriptor.addMapping(indirectionPolicyMapping);

        XMLCompositeObjectMapping selectionQueryMapping = new XMLCompositeObjectMapping();
        selectionQueryMapping.setAttributeName("selectionQuery");
        selectionQueryMapping.setGetMethodName("getSelectionQuery");
        selectionQueryMapping.setSetMethodName("setSelectionQuery");
        selectionQueryMapping.setReferenceClass(ReadQuery.class);
        selectionQueryMapping.setXPath(getPrimaryNamespaceXPath() + "selection-query");
        descriptor.addMapping(selectionQueryMapping);

        // delete-all query
        return descriptor;
    }

    protected ClassDescriptor buildOneToOneMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OneToOneMapping.class);

        descriptor.getInheritancePolicy().setParentClass(ObjectReferenceMapping.class);

        XMLCompositeCollectionMapping sourceToTargetKeyFieldAssociationsMapping = new XMLCompositeCollectionMapping();
        sourceToTargetKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        // Handle translation of foreign key associations to hashtables.
        sourceToTargetKeyFieldAssociationsMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                Map sourceToTargetKeyFields = ((OneToOneMapping)object).getSourceToTargetKeyFields();
                List associations = new ArrayList(sourceToTargetKeyFields.size());
                Iterator iterator = sourceToTargetKeyFields.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    associations.add(new Association(entry.getKey(), entry.getValue()));
                }
                return associations;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                OneToOneMapping mapping = (OneToOneMapping)object;
                List associations = (List)value;
                mapping.setSourceToTargetKeyFields(new HashMap(associations.size() + 1));
                mapping.setTargetToSourceKeyFields(new HashMap(associations.size() + 1));
                Iterator iterator = associations.iterator();
                while (iterator.hasNext()) {
                    Association association = (Association)iterator.next();
                    mapping.getSourceToTargetKeyFields().put((DatabaseField)association.getKey(), (DatabaseField)association.getValue());
                    mapping.getTargetToSourceKeyFields().put((DatabaseField)association.getValue(), (DatabaseField)association.getKey());
                }
            }
        });
        sourceToTargetKeyFieldAssociationsMapping.setAttributeName("sourceToTargetKeyFieldAssociations");
        sourceToTargetKeyFieldAssociationsMapping.setXPath(getSecondaryNamespaceXPath() + "foreign-key/" + getSecondaryNamespaceXPath() + "field-reference");
        descriptor.addMapping(sourceToTargetKeyFieldAssociationsMapping);

        XMLCompositeCollectionMapping foreignKeyFieldNamesMapping = new XMLCompositeCollectionMapping();
        foreignKeyFieldNamesMapping.useCollectionClass(NonSynchronizedVector.class);
        foreignKeyFieldNamesMapping.setAttributeName("foreignKeyFields");
        foreignKeyFieldNamesMapping.setGetMethodName("getForeignKeyFields");
        foreignKeyFieldNamesMapping.setSetMethodName("setForeignKeyFields");
        foreignKeyFieldNamesMapping.setXPath(getSecondaryNamespaceXPath() + "foreign-key-fields/" + getSecondaryNamespaceXPath() + "field");
        foreignKeyFieldNamesMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)foreignKeyFieldNamesMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(foreignKeyFieldNamesMapping);

        XMLDirectMapping relationshipPartnerAttributeNameMapping = new XMLDirectMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setXPath(getPrimaryNamespaceXPath() + "bidirectional-target-attribute/text()");
        descriptor.addMapping(relationshipPartnerAttributeNameMapping);

        XMLDirectMapping usesBatchReadingMapping = new XMLDirectMapping();
        usesBatchReadingMapping.setAttributeName("usesBatchReading");
        usesBatchReadingMapping.setGetMethodName("shouldUseBatchReading");
        usesBatchReadingMapping.setSetMethodName("setUsesBatchReading");
        usesBatchReadingMapping.setXPath(getPrimaryNamespaceXPath() + "batch-reading/text()");
        usesBatchReadingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesBatchReadingMapping);

        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("joinFetch");
        joinFetchMapping.setXPath(getPrimaryNamespaceXPath() + "join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("true", Integer.valueOf(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("false", Integer.valueOf(ForeignReferenceMapping.NONE));
        joinFetchMapping.setConverter(joinFetchConverter);
        joinFetchMapping.setNullValue(ForeignReferenceMapping.NONE);
        descriptor.addMapping(joinFetchMapping);        
        
        XMLCompositeObjectMapping indirectionPolicyMapping = new XMLCompositeObjectMapping();
        indirectionPolicyMapping.setReferenceClass(IndirectionPolicy.class);
        // Handle translation of NoIndirectionPolicy -> null.
        indirectionPolicyMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                IndirectionPolicy policy = ((ForeignReferenceMapping)object).getIndirectionPolicy();
                if (policy instanceof NoIndirectionPolicy) {
                    return null;
                }
                return policy;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                IndirectionPolicy policy = (IndirectionPolicy)value;
                if (value == null) {
                    policy = new NoIndirectionPolicy();
                }
                ((ForeignReferenceMapping)object).setIndirectionPolicy(policy);
            }
        });
        indirectionPolicyMapping.setAttributeName("indirectionPolicy");
        indirectionPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "indirection");
        descriptor.addMapping(indirectionPolicyMapping);

        XMLCompositeObjectMapping selectionQueryMapping = new XMLCompositeObjectMapping();
        selectionQueryMapping.setAttributeName("selectionQuery");
        selectionQueryMapping.setGetMethodName("getSelectionQuery");
        selectionQueryMapping.setSetMethodName("setSelectionQuery");
        selectionQueryMapping.setReferenceClass(ReadQuery.class);
        selectionQueryMapping.setXPath(getPrimaryNamespaceXPath() + "selection-query");
        descriptor.addMapping(selectionQueryMapping);

        return descriptor;
    }

    protected ClassDescriptor buildAssociationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Association.class);
        descriptor.setDefaultRootElement("field-reference");

        XMLCompositeObjectMapping keyMapping = new XMLCompositeObjectMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setXPath(getSecondaryNamespaceXPath() + "source-field");
        keyMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)keyMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(keyMapping);

        XMLCompositeObjectMapping valueMapping = new XMLCompositeObjectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setXPath(getSecondaryNamespaceXPath() + "target-field");
        valueMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)valueMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

    protected ClassDescriptor buildPropertyAssociationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PropertyAssociation.class);
        descriptor.setDefaultRootElement("properties");

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setXPath("@name");
        descriptor.addMapping(keyMapping);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setXPath(getSecondaryNamespaceXPath() + "value/text()");
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

    protected ClassDescriptor buildFieldTranslationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FieldTranslation.class);
        descriptor.setDefaultRootElement("field-translation");

        XMLCompositeObjectMapping keyMapping = new XMLCompositeObjectMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setXPath(getPrimaryNamespaceXPath() + "source-field");
        keyMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)keyMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(keyMapping);

        XMLCompositeObjectMapping valueMapping = new XMLCompositeObjectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setXPath(getPrimaryNamespaceXPath() + "target-field");
        valueMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)valueMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

    protected ClassDescriptor buildTypedAssociationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TypedAssociation.class);
        descriptor.setDefaultRootElement("class-indicator-mapping");

        descriptor.getEventManager().setPostBuildSelector("postBuild");

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeClassification(Class.class);
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setXPath(getPrimaryNamespaceXPath() + "class/text()");
        descriptor.addMapping(keyMapping);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setField(buildTypedField(getPrimaryNamespaceXPath() + "class-indicator/text()"));
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

    protected XMLField buildTypedField(String fieldName) {
        XMLField field = new XMLField(fieldName);
        field.setIsTypedTextField(true);
        field.addConversion(new QName(XMLConstants.SCHEMA_URL, XMLConstants.TIME), java.sql.Time.class);
        field.addConversion(new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE), java.sql.Date.class);
        field.addConversion(new QName(getPrimaryNamespace(), "java-character"), Character.class);
        field.addConversion(new QName(getPrimaryNamespace(), "java-util-date"), java.util.Date.class);
        field.addConversion(new QName(getPrimaryNamespace(), "java-timestamp"), java.sql.Timestamp.class);
        return field;
    }

    protected ClassDescriptor buildFieldTransformationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FieldTransformation.class);
        descriptor.setDefaultRootElement("field-transformation");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().setShouldReadSubclasses(true);
        descriptor.getInheritancePolicy().addClassIndicator(FieldTransformation.class, getPrimaryNamespaceXPath() + "field-transformation");
        descriptor.getInheritancePolicy().addClassIndicator(MethodBasedFieldTransformation.class, getPrimaryNamespaceXPath() + "method-based-field-transformation");
        descriptor.getInheritancePolicy().addClassIndicator(TransformerBasedFieldTransformation.class, getPrimaryNamespaceXPath() + "transformer-based-field-transformation");

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);

        descriptor.addMapping(fieldMapping);

        return descriptor;
    }

    protected ClassDescriptor buildMethodBasedFieldTransformationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MethodBasedFieldTransformation.class);
        descriptor.getInheritancePolicy().setParentClass(FieldTransformation.class);

        XMLDirectMapping methodNameMapping = new XMLDirectMapping();
        methodNameMapping.setAttributeName("methodName");
        methodNameMapping.setXPath(getPrimaryNamespaceXPath() + "method/text()");
        methodNameMapping.setGetMethodName("getMethodName");
        methodNameMapping.setSetMethodName("setMethodName");

        descriptor.addMapping(methodNameMapping);
        return descriptor;
    }

    protected ClassDescriptor buildTransformerBasedFieldTransformationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TransformerBasedFieldTransformation.class);
        descriptor.getInheritancePolicy().setParentClass(FieldTransformation.class);

        XMLDirectMapping methodNameMapping = new XMLDirectMapping();
        methodNameMapping.setAttributeName("transformerClass");
        methodNameMapping.setXPath(getPrimaryNamespaceXPath() + "transformer-class/text()");
        methodNameMapping.setGetMethodName("getTransformerClass");
        methodNameMapping.setSetMethodName("setTransformerClass");

        descriptor.addMapping(methodNameMapping);
        return descriptor;
    }

    protected ClassDescriptor buildQueryArgumentDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(QueryArgument.class);
        descriptor.setDefaultRootElement("query-argument");

        descriptor.getEventManager().setPostBuildSelector("postBuild");

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setXPath("@name");
        descriptor.addMapping(keyMapping);

        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setGetMethodName("getType");
        typeMapping.setSetMethodName("setType");
        typeMapping.setXPath(getSecondaryNamespaceXPath() + "type/text()");
        descriptor.addMapping(typeMapping);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setField(buildTypedField(getSecondaryNamespaceXPath() + "value/text()"));
        descriptor.addMapping(valueMapping);

        XMLDirectMapping nullableMapping = new XMLDirectMapping();
        nullableMapping.setAttributeName("nullable");
        nullableMapping.setGetMethodName("isNullable");
        nullableMapping.setSetMethodName("setNullable");
        nullableMapping.setNullValue(Boolean.FALSE);
        nullableMapping.setField(buildTypedField(getSecondaryNamespaceXPath() + "nullable/text()"));
        descriptor.addMapping(nullableMapping);

        return descriptor;
    }

    protected ClassDescriptor buildTypeMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TypeMapping.class);
        descriptor.setDefaultRootElement("type-mapping");

        descriptor.getEventManager().setPostBuildSelector("postBuild");

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setField(buildTypedField(getPrimaryNamespaceXPath() + "object-value/text()"));
        descriptor.addMapping(valueMapping);

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setField(buildTypedField(getPrimaryNamespaceXPath() + "data-value/text()"));
        descriptor.addMapping(keyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildProjectDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Project.class);
        descriptor.setDefaultRootElement(getPrimaryNamespaceXPath() + "object-persistence");

        descriptor.setSchemaReference(new XMLSchemaClassPathReference(SCHEMA_DIR + TOPLINK_10_SCHEMA));

        XMLTransformationMapping versionMapping = new XMLTransformationMapping();
        versionMapping.addFieldTransformer("@version", getConstantTransformerForProjectVersionMapping());
        descriptor.addMapping(versionMapping);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setSetMethodName("setName");
        nameMapping.setGetMethodName("getName");
        nameMapping.setXPath(getSecondaryNamespaceXPath() + "name/text()");
        descriptor.addMapping(nameMapping);

        XMLCompositeCollectionMapping descriptorsMapping = new XMLCompositeCollectionMapping();
        descriptorsMapping.useCollectionClass(NonSynchronizedVector.class);
        descriptorsMapping.setAttributeName("descriptors");
        descriptorsMapping.setSetMethodName("setOrderedDescriptors");
        descriptorsMapping.setGetMethodName("getOrderedDescriptors");
        descriptorsMapping.setReferenceClass(ClassDescriptor.class);
        descriptorsMapping.setXPath(getSecondaryNamespaceXPath() + "class-mapping-descriptors/" + getSecondaryNamespaceXPath() + "class-mapping-descriptor");
        descriptor.addMapping(descriptorsMapping);

        XMLCompositeObjectMapping loginMapping = new XMLCompositeObjectMapping();
        loginMapping.setSetMethodName("setDatasourceLogin");
        loginMapping.setGetMethodName("getDatasourceLogin");
        loginMapping.setAttributeName("login");
        loginMapping.setReferenceClass(DatasourceLogin.class);
        loginMapping.setXPath(getPrimaryNamespaceXPath() + "login");
        descriptor.addMapping(loginMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     */
    protected ConstantTransformer getConstantTransformerForProjectVersionMapping() {
        return new ConstantTransformer("Oracle TopLink - 10g Release 1 (10.1.3)");
    }

    protected ClassDescriptor buildProxyIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(ProxyIndirectionPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(IndirectionPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildQueryKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(QueryKey.class);
        descriptor.setDefaultRootElement("query-key");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(QueryKey.class, getPrimaryNamespaceXPath() + "query-key");
        descriptor.getInheritancePolicy().addClassIndicator(OneToOneQueryKey.class, getPrimaryNamespaceXPath() + "one-to-one-query-key");
        descriptor.getInheritancePolicy().addClassIndicator(OneToManyQueryKey.class, getPrimaryNamespaceXPath() + "one-to-many-query-key");
        descriptor.getInheritancePolicy().addClassIndicator(DirectQueryKey.class, getPrimaryNamespaceXPath() + "direct-query-key");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildRelationshipQueryKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ForeignReferenceQueryKey.class);
        descriptor.setDefaultRootElement("relationship-query-key");

        descriptor.getInheritancePolicy().setParentClass(QueryKey.class);

        XMLDirectMapping referenceClassMapping = new XMLDirectMapping();
        referenceClassMapping.setAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClass");
        referenceClassMapping.setSetMethodName("setReferenceClass");
        referenceClassMapping.setXPath(getPrimaryNamespaceXPath() + "reference-class/text()");
        descriptor.addMapping(referenceClassMapping);

        return descriptor;
    }

    protected ClassDescriptor buildOneToOneQueryKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OneToOneQueryKey.class);
        descriptor.setDefaultRootElement("one-to-one-query-key");

        descriptor.getInheritancePolicy().setParentClass(ForeignReferenceQueryKey.class);

        return descriptor;
    }

    protected ClassDescriptor buildOneToManyQueryKeyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OneToManyQueryKey.class);
        descriptor.setDefaultRootElement("one-to-many-query-key");

        descriptor.getInheritancePolicy().setParentClass(ForeignReferenceQueryKey.class);

        return descriptor;
    }

    protected ClassDescriptor buildQueryManagerDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DescriptorQueryManager.class);
        descriptor.setDefaultRootElement("query-policy");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DescriptorQueryManager.class, getPrimaryNamespaceXPath() + "query-policy");

        XMLCompositeCollectionMapping namedQueriesMapping = new XMLCompositeCollectionMapping();
        namedQueriesMapping.setReferenceClass(DatabaseQuery.class);
        namedQueriesMapping.useCollectionClass(Vector.class);
        namedQueriesMapping.setAttributeName("queries");
        namedQueriesMapping.setGetMethodName("getAllQueries");
        namedQueriesMapping.setSetMethodName("setAllQueries");
        namedQueriesMapping.setXPath(getSecondaryNamespaceXPath() + "queries/" + getSecondaryNamespaceXPath() + "query");
        descriptor.addMapping(namedQueriesMapping);

        XMLDirectMapping queryTimeoutMapping = new XMLDirectMapping();
        queryTimeoutMapping.setAttributeName("queryTimeout");
        queryTimeoutMapping.setGetMethodName("getQueryTimeout");
        queryTimeoutMapping.setSetMethodName("setQueryTimeout");
        queryTimeoutMapping.setXPath(getPrimaryNamespaceXPath() + "timeout/text()");
        queryTimeoutMapping.setNullValue(Integer.valueOf(DescriptorQueryManager.DefaultTimeout));
        descriptor.addMapping(queryTimeoutMapping);

        XMLCompositeObjectMapping insertQueryMapping = new XMLCompositeObjectMapping();
        insertQueryMapping.setAttributeName("insertQuery");
        insertQueryMapping.setGetMethodName("getInsertQuery");
        insertQueryMapping.setSetMethodName("setInsertQuery");
        insertQueryMapping.setXPath(getPrimaryNamespaceXPath() + "insert-query");
        insertQueryMapping.setReferenceClass(InsertObjectQuery.class);
        descriptor.addMapping(insertQueryMapping);

        XMLCompositeObjectMapping updateQueryMapping = new XMLCompositeObjectMapping();
        updateQueryMapping.setAttributeName("updateQuery");
        updateQueryMapping.setGetMethodName("getUpdateQuery");
        updateQueryMapping.setSetMethodName("setUpdateQuery");
        updateQueryMapping.setXPath(getPrimaryNamespaceXPath() + "update-query");
        updateQueryMapping.setReferenceClass(UpdateObjectQuery.class);
        descriptor.addMapping(updateQueryMapping);

        XMLCompositeObjectMapping deleteQueryMapping = new XMLCompositeObjectMapping();
        deleteQueryMapping.setAttributeName("deleteQuery");
        deleteQueryMapping.setGetMethodName("getDeleteQuery");
        deleteQueryMapping.setSetMethodName("setDeleteQuery");
        deleteQueryMapping.setXPath(getPrimaryNamespaceXPath() + "delete-query");
        deleteQueryMapping.setReferenceClass(DeleteObjectQuery.class);
        descriptor.addMapping(deleteQueryMapping);

        XMLCompositeObjectMapping doesExistQueryMapping = new XMLCompositeObjectMapping();
        doesExistQueryMapping.setAttributeName("doesExistQuery");
        // Handle translation of default does-exist to null.
        doesExistQueryMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                DoesExistQuery query = ((DescriptorQueryManager)object).getDoesExistQuery();
                if ((!query.isCallQuery()) && query.shouldCheckCacheForDoesExist()) {
                    return null;
                }
                return query;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                DoesExistQuery query = (DoesExistQuery)value;
                if (value == null) {
                    return;
                }
                ((DescriptorQueryManager)object).setDoesExistQuery(query);
            }
        });
        doesExistQueryMapping.setXPath(getPrimaryNamespaceXPath() + "does-exist-query");
        doesExistQueryMapping.setReferenceClass(DoesExistQuery.class);
        descriptor.addMapping(doesExistQueryMapping);

        XMLCompositeObjectMapping readObjectQueryMapping = new XMLCompositeObjectMapping();
        readObjectQueryMapping.setAttributeName("readObjectQuery");
        readObjectQueryMapping.setGetMethodName("getReadObjectQuery");
        readObjectQueryMapping.setSetMethodName("setReadObjectQuery");
        readObjectQueryMapping.setXPath(getPrimaryNamespaceXPath() + "read-object-query");
        readObjectQueryMapping.setReferenceClass(ReadObjectQuery.class);
        descriptor.addMapping(readObjectQueryMapping);

        XMLCompositeObjectMapping readAllQueryMapping = new XMLCompositeObjectMapping();
        readAllQueryMapping.setAttributeName("readAllQuery");
        readAllQueryMapping.setGetMethodName("getReadAllQuery");
        readAllQueryMapping.setSetMethodName("setReadAllQuery");
        readAllQueryMapping.setXPath(getPrimaryNamespaceXPath() + "read-all-query");
        readAllQueryMapping.setReferenceClass(ReadAllQuery.class);
        descriptor.addMapping(readAllQueryMapping);

        return descriptor;
    }

    protected ClassDescriptor buildReferenceMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReferenceMapping.class);

        descriptor.getInheritancePolicy().setParentClass(ObjectReferenceMapping.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(fieldMapping);

        return descriptor;
    }

    protected ClassDescriptor buildReturningPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReturningPolicy.class);
        descriptor.setDefaultRootElement("returning-policy");

        XMLCompositeCollectionMapping returningFieldInfoMapping = new XMLCompositeCollectionMapping();
        returningFieldInfoMapping.useCollectionClass(NonSynchronizedVector.class);
        returningFieldInfoMapping.setAttributeName("infos");
        returningFieldInfoMapping.setReferenceClass(ReturningPolicy.Info.class);
        returningFieldInfoMapping.setGetMethodName("getFieldInfos");
        returningFieldInfoMapping.setSetMethodName("setFieldInfos");
        returningFieldInfoMapping.setXPath(getPrimaryNamespaceXPath() + "returning-field-infos/" + getPrimaryNamespaceXPath() + "returning-field-info");
        descriptor.addMapping(returningFieldInfoMapping);

        return descriptor;
    }

    protected ClassDescriptor buildReturningFieldInfoDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ReturningPolicy.Info.class);
        descriptor.setDefaultRootElement("returning-policy-infos");

        XMLDirectMapping referenceClassMapping = new XMLDirectMapping();
        referenceClassMapping.setAttributeName("referenceClass");
        referenceClassMapping.setGetMethodName("getReferenceClass");
        referenceClassMapping.setSetMethodName("setReferenceClass");
        referenceClassMapping.setXPath(getPrimaryNamespaceXPath() + "reference-class/text()");
        descriptor.addMapping(referenceClassMapping);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(fieldMapping);

        XMLDirectMapping sourceMapping1 = new XMLDirectMapping();
        sourceMapping1.setAttributeName("isInsert");
        sourceMapping1.setGetMethodName("isInsert");
        sourceMapping1.setSetMethodName("setIsInsert");
        sourceMapping1.setXPath(getPrimaryNamespaceXPath() + "insert/text()");
        descriptor.addMapping(sourceMapping1);

        XMLDirectMapping sourceMapping2 = new XMLDirectMapping();
        sourceMapping2.setAttributeName("isInsertModeReturnOnly");
        sourceMapping2.setGetMethodName("isInsertModeReturnOnly");
        sourceMapping2.setSetMethodName("setIsInsertModeReturnOnly");
        sourceMapping2.setXPath(getPrimaryNamespaceXPath() + "insert-mode-return-only/text()");
        descriptor.addMapping(sourceMapping2);

        XMLDirectMapping sourceMapping3 = new XMLDirectMapping();
        sourceMapping3.setAttributeName("isUpdate");
        sourceMapping3.setGetMethodName("isUpdate");
        sourceMapping3.setSetMethodName("setIsUpdate");
        sourceMapping3.setXPath(getPrimaryNamespaceXPath() + "update/text()");
        descriptor.addMapping(sourceMapping3);

        return descriptor;
    }

    protected ClassDescriptor buildAbstractCompositeCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AbstractCompositeCollectionMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AggregateMapping.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(fieldMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container");
        descriptor.addMapping(containerPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLCompositeCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLCompositeCollectionMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeCollectionMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildXMLAnyCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLAnyCollectionMapping.class);

        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(fieldMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container");
        descriptor.addMapping(containerPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildAbstractCompositeObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AbstractCompositeObjectMapping.class);
        descriptor.getInheritancePolicy().setParentClass(AggregateMapping.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(fieldMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLAnyObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLAnyObjectMapping.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(fieldMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLCompositeObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLCompositeObjectMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeObjectMapping.class);
        return descriptor;
    }

    protected ClassDescriptor buildDatabaseTableDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatabaseTable.class);

        descriptor.setDefaultRootElement("table");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getQualifiedName");
        nameMapping.setSetMethodName("setPossiblyQualifiedName");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDatabaseFieldDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatabaseField.class);
        descriptor.setDefaultRootElement("field");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DatabaseField.class, getSecondaryNamespaceXPath() + "column");
        descriptor.getInheritancePolicy().addClassIndicator(XMLField.class, getPrimaryNamespaceXPath() + "node");
        descriptor.getInheritancePolicy().addClassIndicator(XMLUnionField.class, getPrimaryNamespaceXPath() + "union-node");

        XMLSchemaReference reference = new XMLSchemaClassPathReference();
        reference.setSchemaContext("/"+ getSecondaryNamespaceXPath() + "column");
        descriptor.setSchemaReference(reference);
        
        XMLDirectMapping tableMapping = new XMLDirectMapping();
        tableMapping.setAttributeName("table");
        tableMapping.setGetMethodName("getTableName");
        tableMapping.setSetMethodName("setTableName");
        tableMapping.setXPath("@table");
        tableMapping.setNullValue("");
        descriptor.addMapping(tableMapping);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("@name");
        nameMapping.getNullPolicy().setNullRepresentedByEmptyNode(false);
        descriptor.addMapping(nameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildAbstractCompositeDirectCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AbstractCompositeDirectCollectionMapping.class);

        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath(getPrimaryNamespaceXPath() + "field");
        ((XMLField)fieldMapping.getField()).setLeafElementType(fieldQname);
       descriptor.addMapping(fieldMapping);

        XMLCompositeObjectMapping valueConverterMapping = new XMLCompositeObjectMapping();
        valueConverterMapping.setAttributeName("valueConverter");
        valueConverterMapping.setGetMethodName("getValueConverter");
        valueConverterMapping.setSetMethodName("setValueConverter");
        valueConverterMapping.setXPath(getPrimaryNamespaceXPath() + "value-converter");
        valueConverterMapping.setReferenceClass(Converter.class);
        descriptor.addMapping(valueConverterMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container");
        descriptor.addMapping(containerPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLCompositeDirectCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLCompositeDirectCollectionMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeDirectCollectionMapping.class);
        return descriptor;
    }

    protected ClassDescriptor buildSerializedObjectConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SerializedObjectConverter.class);

        descriptor.getInheritancePolicy().setParentClass(Converter.class);

        return descriptor;
    }

    protected ClassDescriptor buildStructureMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StructureMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeObjectMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildTimestmapLockingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TimestampLockingPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(VersionLockingPolicy.class);

        XMLDirectMapping usesServerTimeMapping = new XMLDirectMapping();
        usesServerTimeMapping.setAttributeName("usesServerTime");
        usesServerTimeMapping.setGetMethodName("usesServerTime");
        usesServerTimeMapping.setSetMethodName("setUsesServerTime");
        usesServerTimeMapping.setXPath(getPrimaryNamespaceXPath() + "server-time/text()");
        usesServerTimeMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesServerTimeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildTimeToLiveCacheInvalidationPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TimeToLiveCacheInvalidationPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(CacheInvalidationPolicy.class);

        XMLDirectMapping timeToLiveMapping = new XMLDirectMapping();
        timeToLiveMapping.setAttributeName("timeToLive");
        timeToLiveMapping.setXPath(getPrimaryNamespaceXPath() + "time-to-live/text()");
        descriptor.addMapping(timeToLiveMapping);

        return descriptor;
    }

    protected ClassDescriptor buildAbstractTransformationMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AbstractTransformationMapping.class);

        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        XMLDirectMapping attributeMethodNameMapping = new XMLDirectMapping();
        attributeMethodNameMapping.setAttributeName("attributeMethodName");
        attributeMethodNameMapping.setGetMethodName("getAttributeMethodName");
        attributeMethodNameMapping.setSetMethodName("setAttributeTransformation");
        attributeMethodNameMapping.setXPath(getPrimaryNamespaceXPath() + "attribute-method/text()");
        descriptor.addMapping(attributeMethodNameMapping);

        XMLDirectMapping attributeTransformerClassMapping = new XMLDirectMapping();
        attributeTransformerClassMapping.setAttributeName("attributeTransformerClass");
        attributeTransformerClassMapping.setGetMethodName("getAttributeTransformerClass");
        attributeTransformerClassMapping.setSetMethodName("setAttributeTransformerClass");
        attributeTransformerClassMapping.setXPath(getPrimaryNamespaceXPath() + "attribute-transformer/text()");
        descriptor.addMapping(attributeTransformerClassMapping);

        XMLDirectMapping isMutableMapping = new XMLDirectMapping();
        isMutableMapping.setAttributeName("isMutable");
        isMutableMapping.setGetMethodName("isMutable");
        isMutableMapping.setSetMethodName("setIsMutable");
        isMutableMapping.setNullValue(Boolean.TRUE);
        isMutableMapping.setXPath(getPrimaryNamespaceXPath() + "mutable/text()");
        descriptor.addMapping(isMutableMapping);

        XMLCompositeObjectMapping indirectionPolicyMapping = new XMLCompositeObjectMapping();
        indirectionPolicyMapping.setReferenceClass(IndirectionPolicy.class);
        // Handle translation of NoIndirectionPolicy -> null.
        indirectionPolicyMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                IndirectionPolicy policy = ((AbstractTransformationMapping)object).getIndirectionPolicy();
                if (policy instanceof NoIndirectionPolicy) {
                    return null;
                }
                return policy;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                IndirectionPolicy policy = (IndirectionPolicy)value;
                if (value == null) {
                    policy = new NoIndirectionPolicy();
                }
                ((AbstractTransformationMapping)object).setIndirectionPolicy(policy);
            }
        });
        indirectionPolicyMapping.setAttributeName("indirectionPolicy");
        indirectionPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "indirection");
        descriptor.addMapping(indirectionPolicyMapping);

        XMLCompositeCollectionMapping fieldTransformationsMapping = new XMLCompositeCollectionMapping();

        // Handle translation of field-method hashtable to field-transformations.
        fieldTransformationsMapping.setAttributeName("fieldTransformations");
        fieldTransformationsMapping.setGetMethodName("getFieldTransformations");
        fieldTransformationsMapping.setSetMethodName("setFieldTransformations");
        fieldTransformationsMapping.setXPath(getPrimaryNamespaceXPath() + "field-transformations/" + getPrimaryNamespaceXPath() + "field-transformation");
        fieldTransformationsMapping.setReferenceClass(FieldTransformation.class);
        descriptor.addMapping(fieldTransformationsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildTransformationMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TransformationMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AbstractTransformationMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildXMLTransformationMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLTransformationMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AbstractTransformationMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildTransparentIndirectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TransparentIndirectionPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(IndirectionPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildTypesafeEnumConverterDescriptor(Class jaxbTypesafeEnumConverter) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(jaxbTypesafeEnumConverter);

        descriptor.getInheritancePolicy().setParentClass(Converter.class);
        return descriptor;
    }

    protected ClassDescriptor buildTypeConversionConverterDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TypeConversionConverter.class);

        descriptor.getInheritancePolicy().setParentClass(Converter.class);

        XMLDirectMapping objectClassMapping = new XMLDirectMapping();
        objectClassMapping.setAttributeName("objectClass");
        objectClassMapping.setGetMethodName("getObjectClass");
        objectClassMapping.setSetMethodName("setObjectClass");
        objectClassMapping.setXPath(getPrimaryNamespaceXPath() + "object-class/text()");
        descriptor.addMapping(objectClassMapping);

        XMLDirectMapping dataClassMapping = new XMLDirectMapping();
        dataClassMapping.setAttributeName("dataClass");
        dataClassMapping.setGetMethodName("getDataClass");
        dataClassMapping.setSetMethodName("setDataClass");
        dataClassMapping.setXPath(getPrimaryNamespaceXPath() + "data-class/text()");
        descriptor.addMapping(dataClassMapping);

        return descriptor;
    }

    protected ClassDescriptor buildVariableOneToOneMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(VariableOneToOneMapping.class);

        descriptor.getInheritancePolicy().setParentClass(ObjectReferenceMapping.class);

        XMLDirectMapping relationshipPartnerAttributeNameMapping = new XMLDirectMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setXPath(getPrimaryNamespaceXPath() + "bidirectional-target-attribute/text()");
        descriptor.addMapping(relationshipPartnerAttributeNameMapping);

        XMLDirectMapping usesBatchReadingMapping = new XMLDirectMapping();
        usesBatchReadingMapping.setAttributeName("usesBatchReading");
        usesBatchReadingMapping.setGetMethodName("shouldUseBatchReading");
        usesBatchReadingMapping.setSetMethodName("setUsesBatchReading");
        usesBatchReadingMapping.setXPath(getPrimaryNamespaceXPath() + "batch-reading/text()");
        usesBatchReadingMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(usesBatchReadingMapping);

        XMLCompositeObjectMapping indirectionPolicyMapping = new XMLCompositeObjectMapping();
        indirectionPolicyMapping.setReferenceClass(IndirectionPolicy.class);
        // Handle translation of NoIndirectionPolicy -> null.
        indirectionPolicyMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                IndirectionPolicy policy = ((ForeignReferenceMapping)object).getIndirectionPolicy();
                if (policy instanceof NoIndirectionPolicy) {
                    return null;
                }
                return policy;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                IndirectionPolicy policy = (IndirectionPolicy)value;
                if (value == null) {
                    policy = new NoIndirectionPolicy();
                }
                ((ForeignReferenceMapping)object).setIndirectionPolicy(policy);
            }
        });
        indirectionPolicyMapping.setAttributeName("indirectionPolicy");
        indirectionPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "indirection");
        descriptor.addMapping(indirectionPolicyMapping);

        XMLCompositeObjectMapping selectionQueryMapping = new XMLCompositeObjectMapping();
        selectionQueryMapping.setAttributeName("selectionQuery");
        selectionQueryMapping.setGetMethodName("getSelectionQuery");
        selectionQueryMapping.setSetMethodName("setSelectionQuery");
        selectionQueryMapping.setReferenceClass(ReadQuery.class);
        selectionQueryMapping.setXPath(getPrimaryNamespaceXPath() + "selection-query");
        descriptor.addMapping(selectionQueryMapping);

        XMLCompositeObjectMapping typeFieldMapping = new XMLCompositeObjectMapping();
        typeFieldMapping.setAttributeName("typeField");
        typeFieldMapping.setGetMethodName("getTypeField");
        typeFieldMapping.setSetMethodName("setTypeField");
        typeFieldMapping.setReferenceClass(DatabaseField.class);
        typeFieldMapping.setXPath(getPrimaryNamespaceXPath() + "type-field");
        ((XMLField)typeFieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(typeFieldMapping);

        XMLCompositeCollectionMapping foreignKeyFieldsMapping = new XMLCompositeCollectionMapping();
        foreignKeyFieldsMapping.useCollectionClass(NonSynchronizedVector.class);
        foreignKeyFieldsMapping.setAttributeName("foreignKeyFields");
        foreignKeyFieldsMapping.setGetMethodName("getForeignKeyFields");
        foreignKeyFieldsMapping.setSetMethodName("setForeignKeyFields");
        foreignKeyFieldsMapping.setXPath(getPrimaryNamespaceXPath() + "foreign-key-fields/" + getPrimaryNamespaceXPath() + "field");
        foreignKeyFieldsMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)foreignKeyFieldsMapping.getField()).setLeafElementType(new QName(getSecondaryNamespace(), "field"));
        descriptor.addMapping(foreignKeyFieldsMapping);

        XMLCompositeCollectionMapping sourceFieldToTargetQueryKeyMapping = new XMLCompositeCollectionMapping();
        sourceFieldToTargetQueryKeyMapping.setAttributeName("sourceToTargetQueryKeyNames");
        sourceFieldToTargetQueryKeyMapping.setXPath(getPrimaryNamespaceXPath() + "foreign-key-to-query-key/" + getPrimaryNamespaceXPath() + "query-key-reference");
        sourceFieldToTargetQueryKeyMapping.setGetMethodName("getSourceToTargetQueryKeyFieldAssociations");
        sourceFieldToTargetQueryKeyMapping.setSetMethodName("setSourceToTargetQueryKeyFieldAssociations");
        // Handle translation of query key associations string to field.
        sourceFieldToTargetQueryKeyMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                VariableOneToOneMapping mapping = (VariableOneToOneMapping)object;
                Vector associations = mapping.getSourceToTargetQueryKeyFieldAssociations();
                Vector queryKeyReferences = new Vector(associations.size());
                for (int index = 0; index < associations.size(); index++) {
                    Association association = (Association)associations.get(index);
                    QueryKeyReference reference = new QueryKeyReference();
                    reference.setKey(new DatabaseField((String)association.getKey()));
                    reference.setValue(association.getValue());
                    queryKeyReferences.add(reference);
                }
                return queryKeyReferences;
            }

            public void setAttributeValueInObject(Object object, Object value) {
                VariableOneToOneMapping mapping = (VariableOneToOneMapping)object;
                Vector associations = (Vector)value;
                for (int index = 0; index < associations.size(); index++) {
                    Association association = (Association)associations.get(index);
                    association.setKey(((DatabaseField)association.getKey()).getQualifiedName());
                }
                mapping.setSourceToTargetQueryKeyFieldAssociations(associations);
            }
        });
        sourceFieldToTargetQueryKeyMapping.setReferenceClass(QueryKeyReference.class);
        descriptor.addMapping(sourceFieldToTargetQueryKeyMapping);

        XMLCompositeCollectionMapping classIndicatorsMapping = new XMLCompositeCollectionMapping();
        classIndicatorsMapping.setAttributeName("classIndicatorAssociations");
        classIndicatorsMapping.setGetMethodName("getClassIndicatorAssociations");
        classIndicatorsMapping.setSetMethodName("setClassIndicatorAssociations");
        classIndicatorsMapping.setXPath(getPrimaryNamespaceXPath() + "class-indicator-mappings/" + getPrimaryNamespaceXPath() + "class-indicator-mapping");
        classIndicatorsMapping.setReferenceClass(TypedAssociation.class);
        descriptor.addMapping(classIndicatorsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildQueryKeyReferenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(QueryKeyReference.class);
        descriptor.setDefaultRootElement("query-key-reference");

        XMLCompositeObjectMapping keyMapping = new XMLCompositeObjectMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setReferenceClass(DatabaseField.class);
        keyMapping.setXPath(getPrimaryNamespaceXPath() + "source-field");
        ((XMLField)keyMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(keyMapping);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setXPath(getPrimaryNamespaceXPath() + "target-query-key/text()");
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

    protected ClassDescriptor buildOptimisticLockingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(OptimisticLockingPolicy.class);
        descriptor.setDefaultRootElement("locking-policy");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(VersionLockingPolicy.class, getPrimaryNamespaceXPath() + "version-locking-policy");
        descriptor.getInheritancePolicy().addClassIndicator(TimestampLockingPolicy.class, getPrimaryNamespaceXPath() + "timestamp-locking-policy");
        descriptor.getInheritancePolicy().addClassIndicator(SelectedFieldsLockingPolicy.class, getPrimaryNamespaceXPath() + "selected-fields-locking-policy");
        descriptor.getInheritancePolicy().addClassIndicator(ChangedFieldsLockingPolicy.class, getPrimaryNamespaceXPath() + "changed-fields-locking-policy");
        descriptor.getInheritancePolicy().addClassIndicator(AllFieldsLockingPolicy.class, getPrimaryNamespaceXPath() + "all-fields-locking-policy");

        return descriptor;
    }

    protected ClassDescriptor buildVersionLockingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(VersionLockingPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(OptimisticLockingPolicy.class);

        XMLCompositeObjectMapping versionFieldMapping = new XMLCompositeObjectMapping();
        versionFieldMapping.setAttributeName("writeLockField");
        versionFieldMapping.setGetMethodName("getWriteLockField");
        versionFieldMapping.setSetMethodName("setWriteLockField");
        versionFieldMapping.setXPath(getPrimaryNamespaceXPath() + "version-field");
        versionFieldMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)versionFieldMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(versionFieldMapping);

        XMLDirectMapping shouldStoreInCacheMapping = new XMLDirectMapping();
        shouldStoreInCacheMapping.setAttributeName("isStoredInCache");
        shouldStoreInCacheMapping.setGetMethodName("isStoredInCache");
        shouldStoreInCacheMapping.setSetMethodName("setIsStoredInCache");
        shouldStoreInCacheMapping.setXPath(getPrimaryNamespaceXPath() + "store-version-in-cache/text()");
        shouldStoreInCacheMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(shouldStoreInCacheMapping);

        return descriptor;
    }

    protected ClassDescriptor buildSelectedFieldsLockingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SelectedFieldsLockingPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(OptimisticLockingPolicy.class);

        XMLCompositeCollectionMapping fieldsMapping = new XMLCompositeCollectionMapping();
        fieldsMapping.useCollectionClass(NonSynchronizedVector.class);
        fieldsMapping.setAttributeName("lockFields");
        fieldsMapping.setXPath(getPrimaryNamespaceXPath() + "fields/" + getPrimaryNamespaceXPath() + "field");
        fieldsMapping.setReferenceClass(DatabaseField.class);
        ((XMLField)fieldsMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(fieldsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildAllFieldsLockingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AllFieldsLockingPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(OptimisticLockingPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildChangedFieldsLockingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ChangedFieldsLockingPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(OptimisticLockingPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildCompositeObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLCompositeObjectMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeObjectMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildCompositeCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLCompositeCollectionMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeCollectionMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildDirectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLDirectMapping.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(DirectToFieldMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildOXXMLDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLDescriptor.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(ClassDescriptor.class);
        XMLCompositeDirectCollectionMapping defaultRootElementMapping = new XMLCompositeDirectCollectionMapping();
        defaultRootElementMapping.setAttributeName("defaultRootElement");
        defaultRootElementMapping.setGetMethodName("getTableNames");
        defaultRootElementMapping.setSetMethodName("setTableNames");
        defaultRootElementMapping.setXPath(getPrimaryNamespaceXPath() + "default-root-element/text()");
        descriptor.addMapping(defaultRootElementMapping);

        XMLDirectMapping shouldPreserveDocument = new XMLDirectMapping();
        shouldPreserveDocument.setAttributeName("shouldPreserveDocument");
        shouldPreserveDocument.setGetMethodName("shouldPreserveDocument");
        shouldPreserveDocument.setSetMethodName("setShouldPreserveDocument");
        shouldPreserveDocument.setNullValue(Boolean.FALSE);
        shouldPreserveDocument.setXPath(getPrimaryNamespaceXPath() + "should-preserve-document/text()");
        descriptor.addMapping(shouldPreserveDocument);

        XMLCompositeObjectMapping namespaceResolverMapping = new XMLCompositeObjectMapping();
        namespaceResolverMapping.setXPath(getPrimaryNamespaceXPath() + "namespace-resolver");
        namespaceResolverMapping.setAttributeName("namespaceResolver");
        namespaceResolverMapping.setGetMethodName("getNamespaceResolver");
        namespaceResolverMapping.setSetMethodName("setNamespaceResolver");
        namespaceResolverMapping.setReferenceClass(NamespaceResolver.class);
        descriptor.addMapping(namespaceResolverMapping);

        XMLCompositeObjectMapping schemaReferenceMapping = new XMLCompositeObjectMapping();
        schemaReferenceMapping.setAttributeName("schemaReference");
        schemaReferenceMapping.setXPath(getPrimaryNamespaceXPath() + "schema");
        schemaReferenceMapping.setReferenceClass(XMLSchemaReference.class);
        descriptor.addMapping(schemaReferenceMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLSchemaReferenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLSchemaReference.class);
        descriptor.descriptorIsAggregate();
        descriptor.setDefaultRootElement("schema-reference");
        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(XMLSchemaReference.class, getPrimaryNamespaceXPath() + "schema-reference");
        descriptor.getInheritancePolicy().addClassIndicator(XMLSchemaClassPathReference.class, getPrimaryNamespaceXPath() + "schema-class-path-reference");
        descriptor.getInheritancePolicy().addClassIndicator(XMLSchemaFileReference.class, getPrimaryNamespaceXPath() + "schema-file-reference");
        descriptor.getInheritancePolicy().addClassIndicator(XMLSchemaURLReference.class, getPrimaryNamespaceXPath() + "schema-url-reference");

        XMLDirectMapping resourceMapping = new XMLDirectMapping();
        resourceMapping.setAttributeName("resource");
        resourceMapping.setXPath(getPrimaryNamespaceXPath() + "resource/text()");
        descriptor.addMapping(resourceMapping);

        XMLDirectMapping contextMapping = new XMLDirectMapping();
        contextMapping.setAttributeName("schemaContext");
        contextMapping.setXPath(getPrimaryNamespaceXPath() + "schema-context/text()");
        descriptor.addMapping(contextMapping);

        XMLDirectMapping nodeTypeMapping = new XMLDirectMapping();
        nodeTypeMapping.setAttributeName("type");
        nodeTypeMapping.setXPath(getPrimaryNamespaceXPath() + "node-type/text()");

        ObjectTypeConverter nodeTypeConverter = new ObjectTypeConverter();
        nodeTypeConverter.addConversionValue("element", Integer.valueOf(XMLSchemaReference.ELEMENT));
        nodeTypeConverter.addConversionValue("simple-type", Integer.valueOf(XMLSchemaReference.SIMPLE_TYPE));
        nodeTypeConverter.addConversionValue("complex-type", Integer.valueOf(XMLSchemaReference.COMPLEX_TYPE));
        nodeTypeConverter.addConversionValue("group", Integer.valueOf(XMLSchemaReference.GROUP));
        nodeTypeMapping.setConverter(nodeTypeConverter);

        descriptor.addMapping(nodeTypeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLFieldDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLField.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseField.class);

        XMLDirectMapping typedFieldMapping = new XMLDirectMapping();
        typedFieldMapping.setAttributeName("isTypedTextField");
        typedFieldMapping.setGetMethodName("isTypedTextField");
        typedFieldMapping.setSetMethodName("setIsTypedTextField");
        typedFieldMapping.setXPath(getPrimaryNamespaceXPath() + "typed-text-field/text()");
        typedFieldMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(typedFieldMapping);

        XMLDirectMapping singleNodeMapping = new XMLDirectMapping();
        singleNodeMapping.setAttributeName("usesSingleNode");
        singleNodeMapping.setGetMethodName("usesSingleNode");
        singleNodeMapping.setSetMethodName("setUsesSingleNode");
        singleNodeMapping.setXPath(getPrimaryNamespaceXPath() + "single-node/text()");
        singleNodeMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(singleNodeMapping);

        XMLDirectMapping schemaTypeMapping = new XMLDirectMapping();
        schemaTypeMapping.setAttributeName("schemaType");
        schemaTypeMapping.setGetMethodName("getSchemaType");
        schemaTypeMapping.setSetMethodName("setSchemaType");
        schemaTypeMapping.setXPath(getPrimaryNamespaceXPath() + "schema-type/text()");
        descriptor.addMapping(schemaTypeMapping);

        XMLCompositeCollectionMapping xmlToJavaPairsMapping = new XMLCompositeCollectionMapping();
        xmlToJavaPairsMapping.setXPath(getPrimaryNamespaceXPath() + "xml-to-java-conversion-pair");
        xmlToJavaPairsMapping.useCollectionClass(ArrayList.class);
        xmlToJavaPairsMapping.setReferenceClass(XMLConversionPair.class);
        xmlToJavaPairsMapping.setAttributeName("userXMLTypes");
        xmlToJavaPairsMapping.setGetMethodName("getUserXMLTypesForDeploymentXML");
        xmlToJavaPairsMapping.setSetMethodName("setUserXMLTypesForDeploymentXML");
        descriptor.addMapping(xmlToJavaPairsMapping);

        XMLCompositeCollectionMapping javaToXMLPairsMapping = new XMLCompositeCollectionMapping();
        javaToXMLPairsMapping.useCollectionClass(ArrayList.class);
        javaToXMLPairsMapping.setXPath(getPrimaryNamespaceXPath() + "java-to-xml-conversion-pair");
        javaToXMLPairsMapping.setReferenceClass(XMLConversionPair.class);
        javaToXMLPairsMapping.setAttributeName("userJavaTypes");
        javaToXMLPairsMapping.setGetMethodName("getUserJavaTypesForDeploymentXML");
        javaToXMLPairsMapping.setSetMethodName("setUserJavaTypesForDeploymentXML");
        descriptor.addMapping(javaToXMLPairsMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLUnionFieldDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLUnionField.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseField.class);

        XMLDirectMapping typedFieldMapping = new XMLDirectMapping();
        typedFieldMapping.setAttributeName("isTypedTextField");
        typedFieldMapping.setGetMethodName("isTypedTextField");
        typedFieldMapping.setSetMethodName("setIsTypedTextField");
        typedFieldMapping.setXPath(getPrimaryNamespaceXPath() + "typed-text-field/text()");
        typedFieldMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(typedFieldMapping);

        XMLDirectMapping singleNodeMapping = new XMLDirectMapping();
        singleNodeMapping.setAttributeName("usesSingleNode");
        singleNodeMapping.setGetMethodName("usesSingleNode");
        singleNodeMapping.setSetMethodName("setUsesSingleNode");
        singleNodeMapping.setXPath(getPrimaryNamespaceXPath() + "single-node/text()");
        singleNodeMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(singleNodeMapping);

        XMLCompositeDirectCollectionMapping schemaTypeMapping = new XMLCompositeDirectCollectionMapping();
        schemaTypeMapping.setAttributeName("schemaTypes");
        schemaTypeMapping.setGetMethodName("getSchemaTypes");
        schemaTypeMapping.setSetMethodName("setSchemaTypes");
        schemaTypeMapping.useCollectionClass(ArrayList.class);
        schemaTypeMapping.setAttributeElementClass(QName.class);
        schemaTypeMapping.setXPath(getPrimaryNamespaceXPath() + "schema-type/text()");
        descriptor.addMapping(schemaTypeMapping);

        XMLCompositeCollectionMapping xmlToJavaPairsMapping = new XMLCompositeCollectionMapping();
        xmlToJavaPairsMapping.setXPath(getPrimaryNamespaceXPath() + "xml-to-java-conversion-pair");
        xmlToJavaPairsMapping.setReferenceClass(XMLConversionPair.class);
        xmlToJavaPairsMapping.useCollectionClass(ArrayList.class);
        xmlToJavaPairsMapping.setAttributeName("userXMLTypes");
        xmlToJavaPairsMapping.setGetMethodName("getUserXMLTypesForDeploymentXML");
        xmlToJavaPairsMapping.setSetMethodName("setUserXMLTypesForDeploymentXML");
        descriptor.addMapping(xmlToJavaPairsMapping);

        XMLCompositeCollectionMapping javaToXMLPairsMapping = new XMLCompositeCollectionMapping();
        javaToXMLPairsMapping.setXPath(getPrimaryNamespaceXPath() + "java-to-xml-conversion-pair");
        javaToXMLPairsMapping.useCollectionClass(ArrayList.class);
        javaToXMLPairsMapping.setReferenceClass(XMLConversionPair.class);
        javaToXMLPairsMapping.setAttributeName("userJavaTypes");
        javaToXMLPairsMapping.setGetMethodName("getUserJavaTypesForDeploymentXML");
        javaToXMLPairsMapping.setSetMethodName("setUserJavaTypesForDeploymentXML");
        descriptor.addMapping(javaToXMLPairsMapping);
        return descriptor;
    }

    protected ClassDescriptor buildXMLConversionPairDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLConversionPair.class);

        XMLDirectMapping xmlTypeMapping = new XMLDirectMapping();
        xmlTypeMapping.setXPath(getPrimaryNamespaceXPath() + "qname/text()");
        xmlTypeMapping.setAttributeName("xmlType");
        xmlTypeMapping.setGetMethodName("getXmlType");
        xmlTypeMapping.setSetMethodName("setXmlType");
        descriptor.addMapping(xmlTypeMapping);

        XMLDirectMapping javaTypeMapping = new XMLDirectMapping();
        javaTypeMapping.setXPath(getPrimaryNamespaceXPath() + "class-name/text()");
        javaTypeMapping.setAttributeName("javaType");
        javaTypeMapping.setGetMethodName("getJavaType");
        javaTypeMapping.setSetMethodName("setJavaType");
        descriptor.addMapping(javaTypeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLLoginDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLLogin.class);
        descriptor.getInheritancePolicy().setParentClass(DatasourceLogin.class);

        return descriptor;
    }

    protected ClassDescriptor buildNamespaceResolverDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(NamespaceResolver.class);

        XMLCompositeCollectionMapping namespaceMapping = new XMLCompositeCollectionMapping();
        namespaceMapping.setXPath(getPrimaryNamespaceXPath() + "namespaces/" + getPrimaryNamespaceXPath() + "namespace");
        namespaceMapping.setAttributeName("namespaces");
        namespaceMapping.setGetMethodName("getNamespaces");
        namespaceMapping.setSetMethodName("setNamespaces");
        namespaceMapping.setReferenceClass(Namespace.class);
        descriptor.addMapping(namespaceMapping);

        return descriptor;
    }

    protected ClassDescriptor buildNamespaceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Namespace.class);

        XMLDirectMapping prefixMapping = new XMLDirectMapping();
        prefixMapping.setXPath(getPrimaryNamespaceXPath() + "prefix/text()");
        prefixMapping.setAttributeName("prefix");
        prefixMapping.setGetMethodName("getPrefix");
        prefixMapping.setSetMethodName("setPrefix");
        descriptor.addMapping(prefixMapping);

        XMLDirectMapping uriMapping = new XMLDirectMapping();
        uriMapping.setXPath(getPrimaryNamespaceXPath() + "namespace-uri/text()");
        uriMapping.setAttributeName("namespaceURI");
        uriMapping.setGetMethodName("getNamespaceURI");
        uriMapping.setSetMethodName("setNamespaceURI");
        descriptor.addMapping(uriMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLSchemaClassPathReferenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLSchemaClassPathReference.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(XMLSchemaReference.class);

        return descriptor;
    }

    protected ClassDescriptor buildXMLSchemaFileReferenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLSchemaFileReference.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(XMLSchemaReference.class);

        return descriptor;
    }

    protected ClassDescriptor buildXMLSchemaURLReferenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(XMLSchemaURLReference.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(XMLSchemaReference.class);

        return descriptor;
    }

    protected ClassDescriptor buildCMPPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(CMPPolicy.class);
        descriptor.setDefaultRootElement("cmp-policy");

        XMLDirectMapping modificationDeferralLevelMapping = new XMLDirectMapping();
        modificationDeferralLevelMapping.setAttributeName("modificationDeferralLevel");
        modificationDeferralLevelMapping.setGetMethodName("getDeferModificationsUntilCommit");
        modificationDeferralLevelMapping.setSetMethodName("setDeferModificationsUntilCommit");
        ObjectTypeConverter modificationDeferralLevelConverter = new ObjectTypeConverter();
        modificationDeferralLevelConverter.addConversionValue("all-modifications", Integer.valueOf(CMPPolicy.ALL_MODIFICATIONS));
        modificationDeferralLevelConverter.addConversionValue("update-modifications", Integer.valueOf(CMPPolicy.UPDATE_MODIFICATIONS));
        modificationDeferralLevelConverter.addConversionValue("none", Integer.valueOf(CMPPolicy.NONE));
        modificationDeferralLevelMapping.setConverter(modificationDeferralLevelConverter);
        modificationDeferralLevelMapping.setXPath(getPrimaryNamespaceXPath() + "defer-until-commit/text()");
        modificationDeferralLevelMapping.setNullValue(Integer.valueOf(CMPPolicy.ALL_MODIFICATIONS));
        descriptor.addMapping(modificationDeferralLevelMapping);

        XMLDirectMapping nonDeferredCreateTimeMapping = new XMLDirectMapping();
        nonDeferredCreateTimeMapping.setAttributeName("nonDeferredCreateTime");
        nonDeferredCreateTimeMapping.setGetMethodName("getNonDeferredCreateTime");
        nonDeferredCreateTimeMapping.setSetMethodName("setNonDeferredCreateTime");
        ObjectTypeConverter nonDeferredCreateTimeConverter = new ObjectTypeConverter();
        nonDeferredCreateTimeConverter.addConversionValue("after-ejbcreate", Integer.valueOf(CMPPolicy.AFTER_EJBCREATE));
        nonDeferredCreateTimeConverter.addConversionValue("after-ejbpostcreate", Integer.valueOf(CMPPolicy.AFTER_EJBPOSTCREATE));
        nonDeferredCreateTimeConverter.addConversionValue("undefined", Integer.valueOf(CMPPolicy.UNDEFINED));
        nonDeferredCreateTimeMapping.setConverter(nonDeferredCreateTimeConverter);
        nonDeferredCreateTimeMapping.setXPath(getPrimaryNamespaceXPath() + "non-deferred-create-time/text()");
        nonDeferredCreateTimeMapping.setNullValue(Integer.valueOf(CMPPolicy.UNDEFINED));
        descriptor.addMapping(nonDeferredCreateTimeMapping);

        XMLCompositeObjectMapping pessimisticLockingPolicyMapping = new XMLCompositeObjectMapping();
        pessimisticLockingPolicyMapping.setAttributeName("pessimisticLockingPolicy");
        pessimisticLockingPolicyMapping.setGetMethodName("getPessimisticLockingPolicy");
        pessimisticLockingPolicyMapping.setSetMethodName("setPessimisticLockingPolicy");
        pessimisticLockingPolicyMapping.setReferenceClass(PessimisticLockingPolicy.class);
        pessimisticLockingPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "pessimistic-locking");
        descriptor.addMapping(pessimisticLockingPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildPessimisticLockingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PessimisticLockingPolicy.class);
        descriptor.setDefaultRootElement("pessimistic-locking-policy");

        XMLDirectMapping lockingModeMapping = new XMLDirectMapping();
        lockingModeMapping.setXPath(getPrimaryNamespaceXPath() + "locking-mode/text()");
        lockingModeMapping.setAttributeName("lockingMode");
        lockingModeMapping.setGetMethodName("getLockingMode");
        lockingModeMapping.setSetMethodName("setLockingMode");
        ObjectTypeConverter lockingModeConverter = new ObjectTypeConverter();
        lockingModeConverter.addConversionValue("wait", Short.valueOf(ObjectLevelReadQuery.LOCK));
        lockingModeConverter.addConversionValue("no-wait", Short.valueOf(ObjectLevelReadQuery.LOCK_NOWAIT));
        lockingModeMapping.setConverter(lockingModeConverter);
        descriptor.addMapping(lockingModeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildSequenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Sequence.class);
        descriptor.setDefaultRootElement("sequence");

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DefaultSequence.class, getPrimaryNamespaceXPath() + "default-sequence");
        descriptor.getInheritancePolicy().addClassIndicator(NativeSequence.class, getPrimaryNamespaceXPath() + "native-sequence");
        descriptor.getInheritancePolicy().addClassIndicator(TableSequence.class, getPrimaryNamespaceXPath() + "table-sequence");
        descriptor.getInheritancePolicy().addClassIndicator(UnaryTableSequence.class, getPrimaryNamespaceXPath() + "unary-table-sequence");
        try {
            descriptor.getInheritancePolicy().addClassIndicator(Class.forName("org.eclipse.persistence.eis.adapters.xmlfile.XMLFileSequence"), getPrimaryNamespaceXPath() + "xmlfile-sequence");
        } catch (Exception missing) {
            // Ignore.
        }
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath(getPrimaryNamespaceXPath() + "name/text()");
        nameMapping.setNullValue("");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping preallocationSizeMapping = new XMLDirectMapping();
        preallocationSizeMapping.setAttributeName("preallocationSize");
        preallocationSizeMapping.setGetMethodName("getPreallocationSize");
        preallocationSizeMapping.setSetMethodName("setPreallocationSize");
        preallocationSizeMapping.setXPath(getPrimaryNamespaceXPath() + "preallocation-size/text()");
        preallocationSizeMapping.setNullValue(Integer.valueOf(50));
        descriptor.addMapping(preallocationSizeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDefaultSequenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DefaultSequence.class);

        descriptor.getInheritancePolicy().setParentClass(Sequence.class);

        return descriptor;
    }

    protected ClassDescriptor buildNativeSequenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NativeSequence.class);

        descriptor.getInheritancePolicy().setParentClass(Sequence.class);

        return descriptor;
    }

    protected ClassDescriptor buildTableSequenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(TableSequence.class);

        descriptor.getInheritancePolicy().setParentClass(Sequence.class);

        XMLDirectMapping tableNameMapping = new XMLDirectMapping();
        tableNameMapping.setAttributeName("tableName");
        // CR#2407 Call getQualifiedSequenceTableName that includes table qualifier.
        // tableNameMapping.setGetMethodName("getQualifiedSequenceTableName");
        tableNameMapping.setGetMethodName("getTableName");
        tableNameMapping.setSetMethodName("setTableName");
        tableNameMapping.setXPath(getPrimaryNamespaceXPath() + "table/text()");
        tableNameMapping.setNullValue("SEQUENCE");
        descriptor.addMapping(tableNameMapping);

        XMLDirectMapping nameFieldNameMapping = new XMLDirectMapping();
        nameFieldNameMapping.setAttributeName("nameFieldName");
        nameFieldNameMapping.setGetMethodName("getNameFieldName");
        nameFieldNameMapping.setSetMethodName("setNameFieldName");
        nameFieldNameMapping.setXPath(getPrimaryNamespaceXPath() + "name-field/text()");
        nameFieldNameMapping.setNullValue("SEQ_NAME");
        descriptor.addMapping(nameFieldNameMapping);

        XMLDirectMapping counterFieldNameMapping = new XMLDirectMapping();
        counterFieldNameMapping.setAttributeName("counterFieldName");
        counterFieldNameMapping.setGetMethodName("getCounterFieldName");
        counterFieldNameMapping.setSetMethodName("setCounterFieldName");
        counterFieldNameMapping.setXPath(getPrimaryNamespaceXPath() + "counter-field/text()");
        counterFieldNameMapping.setNullValue("SEQ_COUNT");
        descriptor.addMapping(counterFieldNameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildUnaryTableSequenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(UnaryTableSequence.class);

        descriptor.getInheritancePolicy().setParentClass(Sequence.class);

        XMLDirectMapping counterFieldNameMapping = new XMLDirectMapping();
        counterFieldNameMapping.setAttributeName("counterFieldName");
        counterFieldNameMapping.setGetMethodName("getCounterFieldName");
        counterFieldNameMapping.setSetMethodName("setCounterFieldName");
        counterFieldNameMapping.setXPath(getPrimaryNamespaceXPath() + "counter-field/text()");
        counterFieldNameMapping.setNullValue("SEQ_COUNT");
        descriptor.addMapping(counterFieldNameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLFileSequenceDescriptor() {
        // Can no longer reference class directly as in a different project.
        XMLDescriptor descriptor = new XMLDescriptor();
        try {
            descriptor.setJavaClass(Class.forName("org.eclipse.persistence.eis.adapters.xmlfile.XMLFileSequence"));
        } catch (Exception missing) {
            descriptor.setJavaClass(new DefaultSequence() {}.getClass());
        }

        descriptor.getInheritancePolicy().setParentClass(Sequence.class);

        return descriptor;
    }

    protected ClassDescriptor buildFetchGroupManagerDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FetchGroupManager.class);

        XMLCompositeObjectMapping defaultFetchGroupMapping = new XMLCompositeObjectMapping();
        defaultFetchGroupMapping.setAttributeName("defaultFetchGroup");
        defaultFetchGroupMapping.setReferenceClass(FetchGroup.class);
        defaultFetchGroupMapping.setXPath(getPrimaryNamespaceXPath() + "default-fetch-group");
        descriptor.addMapping(defaultFetchGroupMapping);

        XMLCompositeCollectionMapping fetchGroupManagerMapping = new XMLCompositeCollectionMapping();
        fetchGroupManagerMapping.setAttributeName("fetchGroups");
        fetchGroupManagerMapping.setReferenceClass(FetchGroup.class);
        fetchGroupManagerMapping.useMapClass(HashMap.class, "getName");
        fetchGroupManagerMapping.setXPath(getPrimaryNamespaceXPath() + "fetch-group");
        descriptor.addMapping(fetchGroupManagerMapping);

        return descriptor;
    }

    protected ClassDescriptor buildFetchGroupDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FetchGroup.class);
        descriptor.setDefaultRootElement("fetch-group");

        XMLDirectMapping fetchGroupNameMapping = new XMLDirectMapping();
        fetchGroupNameMapping.setAttributeName("name");
        fetchGroupNameMapping.setXPath(getPrimaryNamespaceXPath() + "name");
        descriptor.addMapping(fetchGroupNameMapping);

        XMLCompositeDirectCollectionMapping fetchGroupAttributeMapping = new XMLCompositeDirectCollectionMapping();
        CollectionContainerPolicy containerPolicy = new CollectionContainerPolicy(TreeSet.class);
        fetchGroupAttributeMapping.setContainerPolicy(containerPolicy);
        fetchGroupAttributeMapping.setAttributeName("attributes");
        fetchGroupAttributeMapping.setGetMethodName("getAttributeNames");
        fetchGroupAttributeMapping.setSetMethodName("setAttributeNames");
        fetchGroupAttributeMapping.setXPath(getPrimaryNamespaceXPath() + "fetch-group-attributes/" + getPrimaryNamespaceXPath() + "fetch-group-attribute/text()");
        descriptor.addMapping(fetchGroupAttributeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildChangePolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectChangePolicy.class);
        descriptor.setDefaultRootElement("change-policy");
        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DeferredChangeDetectionPolicy.class, getPrimaryNamespaceXPath() + "deferred-detection-change-policy");
        descriptor.getInheritancePolicy().addClassIndicator(ObjectChangeTrackingPolicy.class, getPrimaryNamespaceXPath() + "object-level-change-policy");
        descriptor.getInheritancePolicy().addClassIndicator(AttributeChangeTrackingPolicy.class, getPrimaryNamespaceXPath() + "attribute-level-change-policy");

        return descriptor;
    }

    protected ClassDescriptor buildDeferredChangeDetectionPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DeferredChangeDetectionPolicy.class);
        descriptor.setDefaultRootElement("change-policy");
        descriptor.getInheritancePolicy().setParentClass(ObjectChangePolicy.class);
        return descriptor;
    }

    protected ClassDescriptor buildObjectChangeTrackingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ObjectChangeTrackingPolicy.class);
        descriptor.setDefaultRootElement("change-policy");
        descriptor.getInheritancePolicy().setParentClass(ObjectChangePolicy.class);
        return descriptor;
    }

    protected ClassDescriptor buildAttributeChangeTrackingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AttributeChangeTrackingPolicy.class);
        descriptor.setDefaultRootElement("change-policy");
        descriptor.getInheritancePolicy().setParentClass(ObjectChangePolicy.class);
        return descriptor;
    }
}
