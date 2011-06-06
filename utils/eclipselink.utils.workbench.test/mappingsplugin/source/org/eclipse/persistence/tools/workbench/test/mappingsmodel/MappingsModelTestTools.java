/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.naming.InitialContext;

import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.ClassExtractor;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.FieldsLockingPolicy;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.InterfacePolicy;
import org.eclipse.persistence.descriptors.PessimisticLockingPolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.descriptors.ReturningPolicy;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.descriptors.copying.AbstractCopyPolicy;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.eis.EISConnectionSpec;
import org.eclipse.persistence.eis.interactions.EISInteraction;
import org.eclipse.persistence.eis.interactions.MappedInteraction;
import org.eclipse.persistence.eis.mappings.EISOneToOneMapping;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.descriptors.FieldTransformation;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.descriptors.MethodBasedFieldTransformation;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.expressions.BaseExpression;
import org.eclipse.persistence.internal.expressions.CompoundExpression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.DataExpression;
import org.eclipse.persistence.internal.expressions.ForUpdateClause;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.LiteralExpression;
import org.eclipse.persistence.internal.expressions.ObjectExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.expressions.QueryKeyExpression;
import org.eclipse.persistence.internal.helper.ConcurrentFixedCache;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.DatabaseQueryMechanism;
import org.eclipse.persistence.internal.queries.InterfaceContainerPolicy;
import org.eclipse.persistence.internal.queries.JPQLCallQueryMechanism;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.VariableOneToOneMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.mappings.querykeys.QueryKey;
import org.eclipse.persistence.mappings.transformers.MethodBasedAttributeTransformer;
import org.eclipse.persistence.mappings.xdb.DirectToXMLTypeMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.jaxp.JAXPTransformer;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.InMemoryQueryIndirectionPolicy;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.DefaultConnector;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumnPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWDatabase;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWLoginSpecHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorFieldPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWClassIndicatorValue;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorAfterLoadingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWMappingDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWDescriptorMultiTableInfoPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWInterfaceDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalPrimaryKeyPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalReturningPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalReturningPolicyInsertFieldReturnOnlyFlag;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWSecondaryTableHolder;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptorLockingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWUserDefinedQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisReturningPolicyInsertFieldReturnOnlyFlag;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWAttributeHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWClassHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWColumnPairHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWDescriptorQueryParameterHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMappingHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWMethodHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWNamedSchemaComponentHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWQueryKeyHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWQueryableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWReferenceHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.handles.MWTableHandle;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWClassBasedTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMethodBasedTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregatePathToColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWColumnQueryKeyPair;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlFieldTransformerAssociation;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProjectDefaultsPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWEisLoginSpec;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCompoundExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.AbstractNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.IdentityConstraintDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedAttributeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedModelGroup;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.ReferencedSimpleTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.SimpleSPIManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.db.jdbc.JDBCExternalDatabaseFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.classfile.CFExternalClassRepositoryFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.platformsmodel.DatabaseType;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.diff.CompositeDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ContainerDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ContainerDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffWrapper;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.DifferentiatorWrapper;
import org.eclipse.persistence.tools.workbench.utility.diff.EqualityDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ListAdapter;
import org.eclipse.persistence.tools.workbench.utility.diff.MapAdapter;
import org.eclipse.persistence.tools.workbench.utility.diff.MapEntryDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.NullDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.OrderedContainerDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;

/**
 * The main method simply builds the DiffEngine to see if all the
 * settings are valid.
 */
public class MappingsModelTestTools {

	public static void main(String[] args) {
		Differentiator d = buildDiffEngine();
		try {
			Diff diff = d.diff("foo", "foo");
			System.out.println("diff: " + diff);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static DiffEngine buildDiffEngine() {
		DiffEngine diffEngine = new DiffEngine();

		ReflectiveDifferentiator rd;


		// ***** utility model classes

		rd = diffEngine.addReflectiveDifferentiator(AbstractModel.class);
			rd.ignoreFieldsNamed("changeSupport");

		rd = diffEngine.addReflectiveDifferentiator(AbstractNodeModel.class);
			rd.ignoreFieldsNamed(new String[] {"branchProblems", "dirty", "dirtyBranch", "problems"});
			rd.addReferenceFieldsNamed("parent");


		// ***** model classes (subclasses of MWModel)

		rd = diffEngine.addReflectiveDifferentiator(AbstractNamedSchemaComponent.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(IdentityConstraintDefinition.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(MWAbstractQuery.class);
			rd.addKeyFieldsNamed("name");
			
		rd = diffEngine.addReflectiveDifferentiator(classForName("org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAbstractTableReferenceMapping$ColumnPairAggregateRuntimeFieldNameGenerator"));
			rd.addReferenceFieldsNamed("mapping", "columnPair");

		rd = diffEngine.addReflectiveDifferentiator(MWAggregatePathToColumn.class);
			rd.setKeyDifferentiator(new ReflectiveDifferentiator.SimpleMethodKeyDifferentiator("description"));
			rd.addReferenceFieldsNamed("aggregateRuntimeFieldNameGenerator");

		rd = diffEngine.addReflectiveDifferentiator(MWClass.class);
			rd.addKeyFieldsNamed("name");
			rd.ignoreFieldsNamed("lastRefreshTimestamp", "interfaceScrubber", "typeScrubber");

		rd = diffEngine.addReflectiveDifferentiator(MWClassAttribute.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(MWClassBasedTransformer.class);
			rd.addKeyFieldsNamed("transformerClassHandle");

		rd = diffEngine.addReflectiveDifferentiator(MWClassIndicatorFieldPolicy.class);
			rd.ignoreFieldsNamed("conversionManager", "legacyIndicatorType");

		rd = diffEngine.addReflectiveDifferentiator(MWClassIndicatorValue.class);
			rd.addKeyFieldsNamed("indicatorValue", "descriptorValueHandle");

		rd = diffEngine.addReflectiveDifferentiator(MWClassRepository.class);
			rd.ignoreFieldsNamed(new String[] {"externalClassRepository", "userTypeNames", "persistLastRefresh"});
			Differentiator typesFieldDifferentiator = rd.getFieldDifferentiator("types");
			rd.setFieldDifferentiator("types", new ClassRepositoryTypesFieldDifferentiator(typesFieldDifferentiator));
			rd.setFieldDifferentiator("typeNames", new ClassRepositoryTypesFieldDifferentiator(typesFieldDifferentiator));
			rd.addReferenceCollectionFieldsNamed("userTypes");

		rd = diffEngine.addReflectiveDifferentiator(MWColumn.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(MWColumnQueryKeyPair.class);
			rd.addKeyFieldsNamed("queryKeyName");

		rd = diffEngine.addReflectiveDifferentiator(MWColumnPair.class);
			rd.addKeyFieldsNamed("sourceColumnHandle", "targetColumnHandle");

		rd = diffEngine.addReflectiveDifferentiator(MWCompoundExpression.class);
			rd.ignoreFieldsNamed("changes");

		rd = diffEngine.addReflectiveDifferentiator(MWDatabase.class);
			rd.ignoreFieldsNamed("connection", "tableNames", "schemaManager");

		rd = diffEngine.addReflectiveDifferentiator(MWDescriptor.class);
			rd.addKeyFieldsNamed("name");
			rd.ignoreFieldsNamed("legacyValuesMap");
			
		rd = diffEngine.addReflectiveDifferentiator(MWDescriptorAfterLoadingPolicy.class);

		rd = diffEngine.addReflectiveDifferentiator(classForName("org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy$CacheSizeHolderImpl"));
			rd.addReferenceFieldsNamed("this$0");		// "inner" class's backpointer to "outer" class

		rd = diffEngine.addReflectiveDifferentiator(classForName("org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy$NullCacheSizeHolder"));
		rd.addReferenceFieldsNamed("this$0");		// "inner" class's backpointer to "outer" class

		rd = diffEngine.addReflectiveDifferentiator(classForName("org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy$CacheTypeHolderImpl"));
			rd.addReferenceFieldsNamed("this$0");		// "inner" class's backpointer to "outer" class

		rd = diffEngine.addReflectiveDifferentiator(classForName("org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorCachingPolicy$NullCacheTypeHolder"));
		rd.addReferenceFieldsNamed("this$0");		// "inner" class's backpointer to "outer" class

		rd = diffEngine.addReflectiveDifferentiator(MWDescriptorMultiTableInfoPolicy.class);

		rd = diffEngine.addReflectiveDifferentiator(MWEisLoginSpec.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(MWEisReturningPolicyInsertFieldReturnOnlyFlag.class);
			rd.addKeyFieldsNamed("field");

		rd = diffEngine.addReflectiveDifferentiator(MWFieldTransformerAssociation.class);
			rd.addKeyFieldsNamed("fieldTransformer");

		rd = diffEngine.addReflectiveDifferentiator(MWInterfaceDescriptor.class);
			rd.ignoreFieldsNamed("implementorScrubber");

		rd = diffEngine.addReflectiveDifferentiator(MWLoginSpec.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(MWMapping.class);
			rd.addKeyFieldsNamed("name");
			rd.ignoreFieldsNamed("legacyValuesMap");

		rd = diffEngine.addReflectiveDifferentiator(MWMappingDescriptor.class);
			rd.ignoreFieldsNamed("inheritedAttributeScrubber");

		rd = diffEngine.addReflectiveDifferentiator(MWMethod.class);
			rd.setKeyDifferentiator(new ReflectiveDifferentiator.SimpleMethodKeyDifferentiator("signature"));
			rd.ignoreFieldsNamed("exceptionTypeScrubber");

		rd = diffEngine.addReflectiveDifferentiator(MWMethodBasedTransformer.class);
			rd.addKeyFieldsNamed("methodHandle");

		rd = diffEngine.addReflectiveDifferentiator(MWNamespace.class);
			rd.addKeyFieldsNamed("namespaceUrl");

		rd = diffEngine.addReflectiveDifferentiator(MWObjectTypeConverter.class);
			rd.addReferenceFieldsNamed("defaultValuePair");

		rd = diffEngine.addReflectiveDifferentiator(MWObjectTypeConverter.ValuePair.class);
			rd.addKeyFieldsNamed("dataValue", "attributeValue");

		rd = diffEngine.addReflectiveDifferentiator(MWOneToOneMapping.class);
			rd.ignoreFieldsNamed("targetForeignKeyScrubber");

		rd = diffEngine.addReflectiveDifferentiator(MWProject.class);
			rd.addKeyFieldsNamed("name");
			rd.ignoreFieldsNamed(new String[] {"spiManager", "descriptorNames", "legacyProject", "saveDirectory", "validator", "version"});

		rd = diffEngine.addReflectiveDifferentiator(MWProjectDefaultsPolicy.class);
			rd.ignoreFieldsNamed("policyBuilderMap");

		rd = diffEngine.addReflectiveDifferentiator(MWQueryParameter.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(MWReference.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(classForName("org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapping$AutoGeneratedQueryKey"));

		rd = diffEngine.addReflectiveDifferentiator(classForName("org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectContainerMapping$ColumnPairAggregateRuntimeFieldNameGenerator"));
			rd.addReferenceFieldsNamed("mapping", "columnPair");

		rd = diffEngine.addReflectiveDifferentiator(MWRelationalFieldTransformerAssociation.class);
			rd.addKeyFieldsNamed("columnHandle");

		rd = diffEngine.addReflectiveDifferentiator(MWRelationalPrimaryKeyPolicy.class);
			rd.ignoreFieldsNamed("primaryKeyScrubber");

		rd = diffEngine.addReflectiveDifferentiator(MWRelationalProject.class);
			rd.ignoreFieldsNamed("tableGenerationPolicy");

		rd = diffEngine.addReflectiveDifferentiator(MWRelationalReturningPolicy.class);
			rd.ignoreFieldsNamed("updateFieldScrubber");

		rd = diffEngine.addReflectiveDifferentiator(MWRelationalReturningPolicyInsertFieldReturnOnlyFlag.class);
			rd.addKeyFieldsNamed("columnHandle");

		rd = diffEngine.addReflectiveDifferentiator(MWSecondaryTableHolder.class);
			rd.addKeyFieldsNamed("tableHandle");

		rd = diffEngine.addReflectiveDifferentiator(MWTable.class);
			rd.addKeyFieldsNamed("catalog", "schema", "shortName");
			rd.ignoreFieldsNamed("lastRefreshTimestamp");
			
		rd = diffEngine.addReflectiveDifferentiator(DatabaseType.class);
			rd.addReferenceFieldNamed("jdbcType");
		
		rd = diffEngine.addReflectiveDifferentiator(org.eclipse.persistence.tools.workbench.platformsmodel.DatabasePlatform.class);
			rd.addReferenceCollectionFieldNamed("jdbcTypeToDatabaseTypeMappings");

		rd = diffEngine.addReflectiveDifferentiator(MWTableDescriptor.class);

		rd = diffEngine.addReflectiveDifferentiator(MWTableDescriptorLockingPolicy.class);
			rd.ignoreFieldsNamed("columnLockColumnScrubber");

		rd = diffEngine.addReflectiveDifferentiator(MWUserDefinedQueryKey.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(MWXmlField.class);
			rd.addKeyFieldsNamed("xpath");
			rd.ignoreFieldsNamed("xpathSteps");

		rd = diffEngine.addReflectiveDifferentiator(MWXmlFieldTransformerAssociation.class);
			rd.addKeyFieldsNamed("xmlField");

		rd = diffEngine.addReflectiveDifferentiator(MWXmlSchema.class);
			rd.addKeyFieldsNamed("name");

		rd = diffEngine.addReflectiveDifferentiator(MWXmlSchemaRepository.class);
			rd.ignoreFieldsNamed("schemaNames");

		rd = diffEngine.addReflectiveDifferentiator(ReferencedAttributeDeclaration.class);
			rd.addReferenceFieldsNamed("attribute");

		rd = diffEngine.addReflectiveDifferentiator(ReferencedComplexTypeDefinition.class);
			rd.addReferenceFieldsNamed("complexType");

		rd = diffEngine.addReflectiveDifferentiator(ReferencedElementDeclaration.class);
			rd.addReferenceFieldsNamed("element");

		rd = diffEngine.addReflectiveDifferentiator(ReferencedModelGroup.class);
			rd.addReferenceFieldsNamed("modelGroupDef");

		rd = diffEngine.addReflectiveDifferentiator(ReferencedSimpleTypeDefinition.class);
			rd.addReferenceFieldsNamed("simpleType");


		// ***** handles

		rd = diffEngine.addReflectiveDifferentiator(MWHandle.class);
			rd.ignoreFieldsNamed("scrubber", "dirty");
			rd.addReferenceFieldsNamed("parent");

		rd = diffEngine.addReflectiveDifferentiator(classForName("org.eclipse.persistence.tools.workbench.mappingsmodel.db.DatabaseTypeHandle"));
			rd.addKeyFieldsNamed("databaseType");
			rd.ignoreFieldsNamed("databaseTypeName");
			rd.addReferenceFieldsNamed("databaseType");

		rd = diffEngine.addReflectiveDifferentiator(MWNamedSchemaComponentHandle.class);
			rd.addKeyFieldsNamed("component");
			rd.ignoreFieldsNamed("schemaName", "qNamePath");
			rd.addReferenceFieldsNamed("component");

		rd = diffEngine.addReflectiveDifferentiator(MWAttributeHandle.class);
			rd.addKeyFieldsNamed("attribute");
			rd.ignoreFieldsNamed("attributeDeclaringTypeName", "attributeName");
			rd.addReferenceFieldsNamed("attribute");

		rd = diffEngine.addReflectiveDifferentiator(MWClassHandle.class);
			rd.addKeyFieldsNamed("type");
			rd.ignoreFieldsNamed("typeName");
			rd.addReferenceFieldsNamed("type");

		rd = diffEngine.addReflectiveDifferentiator(MWDescriptorHandle.class);
			rd.addKeyFieldsNamed("descriptor");
			rd.ignoreFieldsNamed("descriptorName");
			rd.addReferenceFieldsNamed("descriptor");

		rd = diffEngine.addReflectiveDifferentiator(MWDescriptorQueryParameterHandle.class);
			rd.addKeyFieldsNamed("queryParameter");
			rd.ignoreFieldsNamed(new String[] {"classDescriptorName", "querySignature", "queryParameterName"});
			rd.addReferenceFieldsNamed("queryParameter");

		rd = diffEngine.addReflectiveDifferentiator(MWColumnHandle.class);
			rd.addKeyFieldsNamed("column");
			rd.ignoreFieldsNamed("columnTableName", "columnName");
			rd.addReferenceFieldsNamed("column");

		rd = diffEngine.addReflectiveDifferentiator(MWColumnPairHandle.class);
			rd.addKeyFieldsNamed("columnPair");
			rd.ignoreFieldsNamed("tableName", "referenceName", "columnPairName");
			rd.addReferenceFieldsNamed("columnPair");

		rd = diffEngine.addReflectiveDifferentiator(MWLoginSpecHandle.class);
			rd.addKeyFieldsNamed("loginSpec");
			rd.ignoreFieldsNamed("loginSpecName");
			rd.addReferenceFieldsNamed("loginSpec");

		rd = diffEngine.addReflectiveDifferentiator(MWMappingHandle.class);
			rd.addKeyFieldsNamed("mapping");
			rd.ignoreFieldsNamed("mappingDescriptorName", "mappingName");
			rd.addReferenceFieldsNamed("mapping");

		rd = diffEngine.addReflectiveDifferentiator(MWMethodHandle.class);
			rd.addKeyFieldsNamed("method");
			rd.ignoreFieldsNamed("methodDeclaringTypeName", "methodSignature");
			rd.addReferenceFieldsNamed("method");

		rd = diffEngine.addReflectiveDifferentiator(MWQueryableHandle.class);
			rd.addKeyFieldsNamed("queryable");
			rd.ignoreFieldsNamed("mappingDescriptorName", "queryableName");
			rd.addReferenceFieldsNamed("queryable");

		rd = diffEngine.addReflectiveDifferentiator(MWQueryKeyHandle.class);
			rd.addKeyFieldsNamed("queryKey");
			rd.ignoreFieldsNamed("descriptorName", "queryKeyName");
			rd.addReferenceFieldsNamed("queryKey");

		rd = diffEngine.addReflectiveDifferentiator(MWReferenceHandle.class);
			rd.addKeyFieldsNamed("reference");
			rd.ignoreFieldsNamed("referenceTableName", "referenceName");
			rd.addReferenceFieldsNamed("reference");

		rd = diffEngine.addReflectiveDifferentiator(MWTableHandle.class);
			rd.addKeyFieldsNamed("table");
			rd.ignoreFieldsNamed("tableName");
			rd.addReferenceFieldsNamed("table");

		return diffEngine;
	}

	public static DiffEngine buildRuntimeDiffEngine() {
		DiffEngine diffEngine = new DiffEngine();

		ReflectiveDifferentiator rd;


		rd = diffEngine.addReflectiveDifferentiator(Project.class);
			rd.addCollectionFieldNamed("orderedDescriptors");
			rd.ignoreFieldNamed("aliasDescriptors");
		rd = diffEngine.addReflectiveDifferentiator(DatasourceLogin.class);
			rd.ignoreFieldNamed("securableObjectHolder");
			rd.setFieldDifferentiator("properties", new ContainerDifferentiator(MapAdapter.instance(), new MapEntryDifferentiator()) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}
	
					if (((Map) object2).containsKey("password")) {
						if (((char[])((Map) object2).get("password")).length == 0 && !((Map) object1).containsKey("password")) {
							return new NullDiff(object1, object2, this);					
						}
						
						if (((Map) object1).containsKey("password")) {
							char[] passwordArray1 = (char[])((Map)object1).get("password");
							char[] passwordArray2 = (char[])((Map)object1).get("password");
							if (passwordArray1 != null && passwordArray2 != null) {
								String password1 = new String(passwordArray1);
								String password2 = new String(passwordArray2);
								if (password1.equals(password2)) {
									return new NullDiff(object1, object2, this);
								}
							}
						}
						
					}
					return super.diff(object1, object2);	
				}
			});
		rd = diffEngine.addReflectiveDifferentiator(DatasourcePlatform.class);
			rd.ignoreFieldNamed("conversionManager");
			rd.ignoreFieldNamed("platformOperators");
		rd = diffEngine.addReflectiveDifferentiator(DefaultConnector.class);
		

		rd = diffEngine.addReflectiveDifferentiator(ClassDescriptor.class);
			rd.addKeyFieldNamed("javaClassName");
			rd.addCollectionFieldsNamed("mappings");
			rd.addReferenceCollectionFieldsNamed("primaryKeyFields");
			rd.ignoreFieldNamed("additionalTablePrimaryKeyFields");

		rd = diffEngine.addReflectiveDifferentiator(RelationalDescriptor.class);
		
		rd = diffEngine.addReflectiveDifferentiator(XMLSchemaReference.class);

		rd = diffEngine.addReflectiveDifferentiator(QueryKey.class);
			rd.addKeyFieldNamed("name");
			rd.addReferenceFieldNamed("descriptor");

		rd = diffEngine.addReflectiveDifferentiator(ObjectBuilder.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(AbstractCopyPolicy.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(DescriptorEventManager.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(DescriptorQueryManager.class);
			rd.addReferenceFieldNamed("descriptor");
			ContainerDifferentiator cd = rd.addMapFieldNamed("queries");
			MapEntryDifferentiator med = (MapEntryDifferentiator) cd.getElementDifferentiator();
			med.setValueDifferentiator(ContainerDifferentiator.forCollections(rd.getDefaultFieldDifferentiator()));

		rd = diffEngine.addReflectiveDifferentiator(ConcurrentFixedCache.class);
		rd = diffEngine.addReflectiveDifferentiator(InheritancePolicy.class);
			rd.addReferenceFieldNamed("parentDescriptor");
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(InstantiationPolicy.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(VersionLockingPolicy.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(FieldsLockingPolicy.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(DeferredChangeDetectionPolicy.class);
		rd = diffEngine.addReflectiveDifferentiator(ReturningPolicy.class);
			rd.ignoreFieldNamed("main"); //populated during initialization
			rd.addReferenceFieldNamed("descriptor");
        rd = diffEngine.addReflectiveDifferentiator(ReturningPolicy.Info.class);
		rd = diffEngine.addReflectiveDifferentiator(CacheInvalidationPolicy.class);
		rd = diffEngine.addReflectiveDifferentiator(InterfacePolicy.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(CMPPolicy.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(PessimisticLockingPolicy.class);

		rd = diffEngine.addReflectiveDifferentiator(DatabaseQuery.class);
			rd.addReferenceFieldNamed("name");
			rd.addReferenceFieldNamed("descriptor");
			rd.ignoreFieldNamed("shouldCloneCall");
		rd = diffEngine.addReflectiveDifferentiator(ReadAllQuery.class);
			rd.ignoreFieldNamed("containerPolicy");
		rd = (ReflectiveDifferentiator)diffEngine.setUserDifferentiator(ObjectLevelReadQuery.class,
				new ReflectiveDifferentiator(ObjectLevelReadQuery.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						if (object1 != null) {
							((ObjectLevelReadQuery) object1).getJoinedAttributeManager();
							((ObjectLevelReadQuery) object1).setShouldOuterJoinSubclasses(((ObjectLevelReadQuery) object1).shouldOuterJoinSubclasses());
						}
						if (object2 != null) {
							((ObjectLevelReadQuery) object2).getJoinedAttributeManager();
							((ObjectLevelReadQuery) object2).setShouldOuterJoinSubclasses(((ObjectLevelReadQuery) object2).shouldOuterJoinSubclasses());						}
						return super.diff(object1, object2);	
					}
			});
			rd.ignoreFieldNamed("defaultBuilder");
		rd = (ReflectiveDifferentiator) diffEngine.addReflectiveDifferentiator(JoinedAttributeManager.class);
			rd.addReferenceFieldNamed("descriptor");
			rd.addReferenceFieldNamed("baseQuery");
			rd.addReferenceFieldNamed("baseExpressionBuilder");
        rd = diffEngine.addReflectiveDifferentiator(ReportItem.class);
		rd = diffEngine.addReflectiveDifferentiator(DataReadQuery.class);
			rd.ignoreFieldNamed("containerPolicy");
		rd = diffEngine.addReflectiveDifferentiator(QueryResultsCachePolicy.class);
		rd = diffEngine.addReflectiveDifferentiator(DatabaseQueryMechanism.class);
			rd.addReferenceFieldNamed("query");
		rd = diffEngine.addReflectiveDifferentiator(Expression.class);
		rd = diffEngine.addReflectiveDifferentiator(DataExpression.class);
			rd = diffEngine.addReflectiveDifferentiator(BaseExpression.class);
			rd.addReferenceFieldNamed("baseExpression");
			rd.addReferenceFieldNamed("builder");
		rd = diffEngine.addReflectiveDifferentiator(CompoundExpression.class);
			rd.addReferenceFieldNamed("builder");
			rd.addReferenceFieldNamed("firstChild");
			rd.addReferenceFieldNamed("secondChild");
		rd = diffEngine.addReflectiveDifferentiator(ObjectExpression.class);
		rd = diffEngine.addReflectiveDifferentiator(FunctionExpression.class);
			rd.ignoreFieldsNamed("operator", "platformOperator");
		rd = diffEngine.addReflectiveDifferentiator(ConstantExpression.class);
			rd.addReferenceFieldNamed("localBase");
			rd = diffEngine.addReflectiveDifferentiator(LiteralExpression.class);
			rd.addReferenceFieldNamed("localBase");
		rd = diffEngine.addReflectiveDifferentiator(ParameterExpression.class);
			rd.addReferenceFieldNamed("localBase");
		rd = diffEngine.addReflectiveDifferentiator(QueryKeyExpression.class);
			rd.addKeyFieldNamed("name");
		rd = diffEngine.addReflectiveDifferentiator(DatasourceCall.class);
			rd.addReferenceFieldNamed("query");
		rd = diffEngine.addReflectiveDifferentiator(ContainerPolicy.class);
			rd.addReferenceFieldNamed("elementDescriptor");
            rd.ignoreFieldNamed("constructor");//not initialized until setContainerClass() is called when the deployment xml is read
        rd = diffEngine.addReflectiveDifferentiator(JPQLCallQueryMechanism.class);
        	rd.ignoreFieldNamed("ejbqlCall");
            
        rd = diffEngine.addReflectiveDifferentiator(AbstractCompositeDirectCollectionMapping.class);
        	rd.addReferenceFieldNamed("containerPolicy");
        	
     	rd = diffEngine.addReflectiveDifferentiator(InterfaceContainerPolicy.class);
			rd.setKeyDifferentiator(new ReflectiveDifferentiator.KeyDifferentiator() {
				public Diff keyDiff(Object object1, Object object2) {
					return EqualityDifferentiator.instance().diff(((InterfaceContainerPolicy) object1).getContainerClassName(), ((InterfaceContainerPolicy) object2).getContainerClassName());
				}
			});
			rd.ignoreFieldNamed("containerClass");
			rd.ignoreFieldNamed("containerClassName");

        rd = diffEngine.addReflectiveDifferentiator(NullPolicy.class);
            
		rd = diffEngine.addReflectiveDifferentiator(InMemoryQueryIndirectionPolicy.class);
		rd = diffEngine.addReflectiveDifferentiator(ForUpdateClause.class);
		
		rd = diffEngine.addReflectiveDifferentiator(DatabaseMapping.class);
			rd.setKeyDifferentiator(new ReflectiveDifferentiator.SimpleMethodKeyDifferentiator("getAttributeName"));
			rd.addReferenceFieldNamed("descriptor");			
		rd = diffEngine.addReflectiveDifferentiator(AbstractTransformationMapping.class);
			rd.addCollectionFieldNamed("fieldTransformations");

		rd = diffEngine.addReflectiveDifferentiator(OneToOneMapping.class);
			rd.addReferenceMapFieldsNamed("sourceToTargetKeyFields", "targetToSourceKeyFields");
		
		rd = diffEngine.addReflectiveDifferentiator(VariableOneToOneMapping.class);
			rd.addReferenceMapFieldsNamed("sourceToTargetQueryKeyNames");
		
		rd = diffEngine.addReflectiveDifferentiator(DirectToXMLTypeMapping.class);
			rd.ignoreFieldNamed("xmlParser");
		
		rd = diffEngine.addReflectiveDifferentiator(EISOneToOneMapping.class);
			rd.addReferenceMapFieldsNamed("sourceToTargetKeyFields", "targetToSourceKeyFields");
			
		rd = diffEngine.addReflectiveDifferentiator(XMLObjectReferenceMapping.class);
			rd.addReferenceMapFieldsNamed("sourceToTargetKeyFieldAssociations");
			rd.ignoreFieldNamed("sourceToTargetKeys");
			
			rd = diffEngine.addReflectiveDifferentiator(XMLDirectMapping.class);
			rd.addReferenceFieldNamed("nullPolicy");
			
		rd = diffEngine.addReflectiveDifferentiator(XMLCompositeObjectMapping.class);
			rd.addReferenceFieldNamed("nullPolicy");
			
		rd = diffEngine.addReflectiveDifferentiator(XMLCompositeCollectionMapping.class);
		
		rd = diffEngine.addReflectiveDifferentiator(DatabaseField.class);
			rd.addKeyFieldNamed("name");
			rd.ignoreFieldNamed("qualifiedName");			// this field is lazy-initialized
			Differentiator tableFieldDifferentiator = rd.getFieldDifferentiator("table");
			rd.setFieldDifferentiator("table", new DatabaseFieldTableFieldDifferentiator(tableFieldDifferentiator));

		rd = diffEngine.addReflectiveDifferentiator(DatabaseTable.class);
			rd.ignoreFieldNamed("qualifiedName");			// this field is lazy-initialized

		rd = diffEngine.addReflectiveDifferentiator(IndirectionPolicy.class);
			rd.addReferenceFieldNamed("mapping");
			
		rd = diffEngine.addReflectiveDifferentiator(AttributeAccessor.class);
		rd = diffEngine.addReflectiveDifferentiator(ObjectTypeConverter.class);
			rd.addReferenceFieldNamed("mapping");
		rd = diffEngine.addReflectiveDifferentiator(TypeConversionConverter.class);
			rd.addReferenceFieldNamed("mapping");
		rd = diffEngine.addReflectiveDifferentiator(SerializedObjectConverter.class);
			rd.addReferenceFieldNamed("mapping");
		
		rd = diffEngine.addReflectiveDifferentiator(MethodBasedAttributeTransformer.class);
			rd.addReferenceFieldNamed("mapping");
		
		rd = diffEngine.addReflectiveDifferentiator(FieldTransformation.class);
		rd = diffEngine.addReflectiveDifferentiator(MethodBasedFieldTransformation.class);
			rd.addKeyFieldNamed("methodName");
		rd = diffEngine.addReflectiveDifferentiator(TransformerBasedFieldTransformation.class);
			rd.setKeyDifferentiator(new ReflectiveDifferentiator.SimpleMethodKeyDifferentiator("getFieldName"));

		rd = diffEngine.addReflectiveDifferentiator(EISConnectionSpec.class);
		rd = diffEngine.addReflectiveDifferentiator(NamespaceResolver.class);
		rd = diffEngine.addReflectiveDifferentiator(InitialContext.class);
		
		rd = diffEngine.addReflectiveDifferentiator(XMLComparer.class);
		rd = diffEngine.addReflectiveDifferentiator(JAXPTransformer.class);
			rd.ignoreFieldNamed("transformer");

		return diffEngine;
	}	
	
	public static DiffEngine buildRuntimeDeploymentXmlDiffEngine() {
		DiffEngine diffEngine = new DiffEngine();

		ReflectiveDifferentiator rd;


		rd = diffEngine.addReflectiveDifferentiator(Project.class);
			rd.addCollectionFieldNamed("orderedDescriptors");
			rd.ignoreFieldNamed("aliasDescriptors");
			rd.ignoreFieldNamed("descriptors");
		rd = diffEngine.addReflectiveDifferentiator(DatasourceLogin.class);
			rd.ignoreFieldNamed("securableObjectHolder");
			rd.ignoreFieldsNamed("isEncryptedPasswordSet");
			rd.setFieldDifferentiator("properties", new ContainerDifferentiator(MapAdapter.instance(), new MapEntryDifferentiator()) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}
	
					if (((Map) object2).containsKey("password")) {
						if (((char[])((Map) object2).get("password")).length == 0 && !((Map) object1).containsKey("password")) {
							return new NullDiff(object1, object2, this);					
						}
						
						if (((Map) object1).containsKey("password")) {
							char[] passwordArray1 = (char[])((Map)object1).get("password");
							char[] passwordArray2 = (char[])((Map)object1).get("password");
							if (passwordArray1 != null && passwordArray2 != null) {
								String password1 = new String(passwordArray1);
								String password2 = new String(passwordArray2);
								if (password1.equals(password2)) {
									return new NullDiff(object1, object2, this);
								}
							}
						}
						
					}
					return super.diff(object1, object2);	
				}
			});
			
		diffEngine.setUserDifferentiator(DatasourcePlatform.class,
			new ReflectiveDifferentiator(DatasourcePlatform.class, diffEngine.getRecordingDifferentiator()) {
				public Diff diff(Object object1, Object object2) {
					if (object1 instanceof DatabasePlatform) {
						((DatabasePlatform) object1).getDefaultSequence();
					}
					return super.diff(object1, object2);	
				}
			});
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(DatasourcePlatform.class);
			rd.ignoreFieldNamed("conversionManager");
			rd.ignoreFieldNamed("platformOperators");
			rd.setFieldDifferentiator("sequences", new ContainerDifferentiator(MapAdapter.instance(), new MapEntryDifferentiator()) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}

					if (object2 == null && ((Map) object1).size() == 0) {
						return new NullDiff(object1, object2, this);
					}
					return super.diff(object1, object2);	
				}
			});
		
		rd = diffEngine.addReflectiveDifferentiator(DefaultConnector.class);
		
		diffEngine.setUserDifferentiator(ClassDescriptor.class,
				new ReflectiveDifferentiator(ClassDescriptor.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						return new CompositeDiff(object1, object2, new Diff[] {super.diff(object1, object2), bonusDiff(object1, object2)}, this);
					}
					
					private Diff bonusDiff(Object object1, Object object2) {
						if (((ClassDescriptor) object1).getAmendmentClass() == null) {
							return new NullDiff(object1, object2, this);
						}
						
						String descriptor1Name = ((ClassDescriptor) object1).getAmendmentClass().getName();
						String descriptor2Name = ((ClassDescriptor) object2).getAmendmentClassName();
						return EqualityDifferentiator.instance().diff(descriptor1Name, descriptor2Name);
					}
				});
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(ClassDescriptor.class);
			rd.addCollectionFieldNamed("mappings");
			rd.ignoreFieldNamed("additionalTablePrimaryKeyFields");			
			rd.ignoreFieldNamed("javaClass");
			rd.ignoreFieldNamed("javaClassName");
			rd.ignoreFieldNamed("amendmentClass");
			rd.ignoreFieldNamed("amendmentClassName");
			rd.ignoreFieldNamed("properties");
			
			rd.setKeyDifferentiator(new ReflectiveDifferentiator.KeyDifferentiator() {
				public Diff keyDiff(Object object1, Object object2) {
					return EqualityDifferentiator.instance().diff(((ClassDescriptor) object1).getJavaClass().getName(), ((ClassDescriptor) object2).getJavaClassName());
				}
			});
			
		rd = diffEngine.addReflectiveDifferentiator(RelationalDescriptor.class);

		rd = diffEngine.addReflectiveDifferentiator(XMLSchemaReference.class);
		
		rd = diffEngine.addReflectiveDifferentiator(QueryKey.class);
			rd.addKeyFieldNamed("name");
			rd.addReferenceFieldNamed("descriptor");

		rd = diffEngine.addReflectiveDifferentiator(ObjectBuilder.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(AbstractCopyPolicy.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(DescriptorEventManager.class);
			rd.addReferenceFieldNamed("descriptor");

		diffEngine.setUserDifferentiator(DescriptorQueryManager.class, 
			new ReflectiveDifferentiator(DescriptorQueryManager.class, diffEngine.getRecordingDifferentiator()) {
				public Diff diff(Object object1, Object object2) {
					((DescriptorQueryManager) object1).getDoesExistCall();
					return super.diff(object1, object2);
				}
			});
	
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(DescriptorQueryManager.class);
			rd.addReferenceFieldNamed("descriptor");
			ContainerDifferentiator cd = rd.addMapFieldNamed("queries");
			MapEntryDifferentiator med = (MapEntryDifferentiator) cd.getElementDifferentiator();
			med.setValueDifferentiator(ContainerDifferentiator.forCollections(rd.getDefaultFieldDifferentiator()));

		rd = diffEngine.addReflectiveDifferentiator(ConcurrentFixedCache.class);
			
		diffEngine.setUserDifferentiator(InheritancePolicy.class,
				new ReflectiveDifferentiator(InheritancePolicy.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						return new CompositeDiff(object1, object2, new Diff[] {super.diff(object1, object2), bonusDiff(object1, object2)}, this);
					}
						
					private Diff bonusDiff(Object object1, Object object2) {
						if (((InheritancePolicy) object1).getParentClass() == null) {
							return new NullDiff(object1, object2, this);
						}
							
						String parentClassName1 = ((InheritancePolicy) object1).getParentClass().getName();
						String parentClassName2 = ((InheritancePolicy) object2).getParentClassName();
						
						Map classIndicatorMapping = ((InheritancePolicy) object1).getClassIndicatorMapping();
						Hashtable classIndicatorNameMapping1 = new Hashtable();
						for (Iterator stream = classIndicatorMapping.keySet().iterator(); stream.hasNext(); ) {
							Object key = stream.next();
							classIndicatorNameMapping1.put(key, classIndicatorMapping.get(key));
						}
						
						Map classIndicatorNameMapping2 = ((InheritancePolicy) object2).getClassNameIndicatorMapping();
						
						return 
							new CompositeDiff(object1, object2, new Diff[] 
								{
									EqualityDifferentiator.instance().diff(parentClassName1, parentClassName2),
									ContainerDifferentiator.forMaps().diff(classIndicatorNameMapping1, classIndicatorNameMapping2)
								}, this);
					}
				});
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(InheritancePolicy.class);
			rd.addReferenceFieldNamed("parentDescriptor");
			rd.addReferenceFieldNamed("descriptor");
			rd.ignoreFieldNamed("parentClass");
			rd.ignoreFieldNamed("parentClassName");
			rd.ignoreFieldNamed("onlyInstancesExpression");
			rd.ignoreFieldNamed("withAllSubclassesExpression");
			rd.ignoreFieldNamed("classIndicatorMapping");
			rd.ignoreFieldNamed("classNameIndicatorMapping");
			
			
			
		rd = diffEngine.addReflectiveDifferentiator(ClassExtractor.class);
			
		
		diffEngine.setUserDifferentiator(InstantiationPolicy.class,
				new ReflectiveDifferentiator(InstantiationPolicy.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						return new CompositeDiff(object1, object2, new Diff[] {super.diff(object1, object2), bonusDiff(object1, object2)}, this);
					}
					
					private Diff bonusDiff(Object object1, Object object2) {
						if (((InstantiationPolicy) object1).getFactoryClass() == null) {
							return new NullDiff(object1, object2, this);
						}
						
						String factoryClassName1 = ((InstantiationPolicy) object1).getFactoryClass().getName();
						String factoryClassName2 = ((InstantiationPolicy) object2).getFactoryClassName();
						return EqualityDifferentiator.instance().diff(factoryClassName1, factoryClassName2);
					}
				});
		
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(InstantiationPolicy.class);
			rd.addReferenceFieldNamed("descriptor");
			rd.ignoreFieldNamed("factoryClass");
			rd.ignoreFieldNamed("factoryClassName");
		rd = diffEngine.addReflectiveDifferentiator(VersionLockingPolicy.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(FieldsLockingPolicy.class);
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(DeferredChangeDetectionPolicy.class);
		rd = diffEngine.addReflectiveDifferentiator(ReturningPolicy.class);
			rd.ignoreFieldNamed("main"); //populated during initialization
			rd.addReferenceFieldNamed("descriptor");
		rd = diffEngine.addReflectiveDifferentiator(CacheInvalidationPolicy.class);
		
		
		
		diffEngine.setUserDifferentiator(InterfacePolicy.class,
				new ReflectiveDifferentiator(InterfacePolicy.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						return new CompositeDiff(object1, object2, new Diff[] {super.diff(object1, object2), bonusDiff(object1, object2)}, this);
					}
					
					private Diff bonusDiff(Object object1, Object object2) {
						Vector parentInterfaces = ((InterfacePolicy) object1).getParentInterfaces();
						Vector parentInterfaceNames1 = new Vector();
						for (int i = 0; i < parentInterfaces.size(); i++) {
							parentInterfaceNames1.add(((Class) parentInterfaces.get(i)).getName());
						}
						
						Vector parentInterfaceNames2 = ((InterfacePolicy) object1).getParentInterfaceNames();
						return EqualityDifferentiator.instance().diff(parentInterfaceNames1, parentInterfaceNames2);
					}
				});
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(InterfacePolicy.class);
			rd.ignoreFieldNamed("parentInterfaceNames");			
			rd.ignoreFieldNamed("parentInterfaces");	
			rd.addReferenceFieldNamed("descriptor");

	        rd = diffEngine.addReflectiveDifferentiator(DataReadQuery.class);
			rd = diffEngine.addReflectiveDifferentiator(QueryResultsCachePolicy.class);
	        rd = diffEngine.addReflectiveDifferentiator(ReportItem.class);
			rd = diffEngine.addReflectiveDifferentiator(DatabaseQueryMechanism.class);
				rd.addReferenceFieldNamed("query");
			rd = diffEngine.addReflectiveDifferentiator(Expression.class);
			rd = diffEngine.addReflectiveDifferentiator(BaseExpression.class);
				rd.addReferenceFieldNamed("baseExpression");
				rd.addReferenceFieldNamed("builder");
			rd = diffEngine.addReflectiveDifferentiator(ObjectExpression.class);
				rd.addReferenceFieldNamed("derivedExpressions");			
			rd = diffEngine.addReflectiveDifferentiator(CompoundExpression.class);
				rd.addReferenceFieldNamed("builder");
				rd.addReferenceFieldNamed("firstChild");
				rd.addReferenceFieldNamed("secondChild");
			rd = diffEngine.addReflectiveDifferentiator(FunctionExpression.class);
				rd.addReferenceFieldNamed("children");
				rd.ignoreFieldsNamed("operator", "platformOperator");
			rd = diffEngine.addReflectiveDifferentiator(ConstantExpression.class);
				rd.addReferenceFieldNamed("localBase");
				//TODO this should be removed when the runtime fixed bug #3183462
				rd.setFieldDifferentiator("value", new ReflectiveDifferentiator(Object.class) {
					public Diff diff(Object object1, Object object2) {
						if (object1 == null && object2 == "") {
							return new NullDiff(object1, object2, this);
						}
						return super.diff(object1, object2);
					}
				});
			rd = diffEngine.addReflectiveDifferentiator(LiteralExpression.class);
				rd.addReferenceFieldNamed("localBase");
			rd = diffEngine.addReflectiveDifferentiator(ParameterExpression.class);
				rd.addReferenceFieldNamed("localBase");
			rd = diffEngine.addReflectiveDifferentiator(QueryKeyExpression.class);
				rd.addKeyFieldNamed("name");
			rd = diffEngine.addReflectiveDifferentiator(DatasourceCall.class);
				rd.addReferenceFieldNamed("query");
			rd = diffEngine.addReflectiveDifferentiator(ContainerPolicy.class);
				rd.addReferenceFieldNamed("elementDescriptor");
				rd.ignoreFieldNamed("constructor");//not initialized until setContainerClass() is called when the deployment xml is read

		    rd = diffEngine.addReflectiveDifferentiator(NullPolicy.class);

		diffEngine.setUserDifferentiator(DatabaseQuery.class,
				new ReflectiveDifferentiator(DatabaseQuery.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						return new CompositeDiff(object1, object2, new Diff[] {super.diff(object1, object2), bonusDiff(object1, object2)}, this);
					}
					
					private Diff bonusDiff(Object object1, Object object2) {
						List<Class> argumentTypes = ((DatabaseQuery) object1).getArgumentTypes();
						Vector argumentTypeNames1 = new Vector();
						for (int i = 0; i < argumentTypes.size(); i++) {
							argumentTypeNames1.add(((Class) argumentTypes.get(i)).getName());
						}
						
						List<String> argumentTypeNames2 = ((DatabaseQuery) object1).getArgumentTypeNames();
						return EqualityDifferentiator.instance().diff(argumentTypeNames1, argumentTypeNames2);
					}
				});
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(DatabaseQuery.class);
			rd.addKeyFieldNamed("name");
			rd.addReferenceFieldNamed("descriptor");
			rd.ignoreFieldNamed("argumentTypes");
			rd.ignoreFieldNamed("argumentTypeNames");
			rd.ignoreFieldNamed("shouldCloneCall");
			rd.setFieldDifferentiator("arguments", new OrderedContainerDifferentiator(ListAdapter.instance()) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}

					if (object1 == null && ((List) object2).size() == 0) {
						return new NullDiff(object1, object2, this);
					}
					return super.diff(object1, object2);	
				}
			});
			rd.setFieldDifferentiator("argumentValues", new OrderedContainerDifferentiator(ListAdapter.instance()) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}

					if (object1 == null && ((List) object2).size() == 0) {
						return new NullDiff(object1, object2, this);
					}
					return super.diff(object1, object2);	
				}
			});
			
			
		rd = diffEngine.addReflectiveDifferentiator(ReadAllQuery.class);
			rd.ignoreFieldNamed("containerPolicy");
			rd.setFieldDifferentiator("orderByExpressions", new OrderedContainerDifferentiator(ListAdapter.instance(), diffEngine.getRecordingDifferentiator()) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}

					if (object2 == null && ((List) object1).size() == 0) {
						return new NullDiff(object1, object2, this);
					}
					return super.diff(object1, object2);	
				}
			});
			rd.setFieldDifferentiator("batchReadAttributeExpressions", new OrderedContainerDifferentiator(ListAdapter.instance(), diffEngine.getUserDifferentiator(Expression.class)) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}

					if (((List) object1).size() == 0 && object2 == null) {
						return new NullDiff(object1, object2, this);
					}
					return super.diff(object1, object2);	
				}
			});
		
		rd = (ReflectiveDifferentiator) diffEngine.setUserDifferentiator(ObjectLevelReadQuery.class,
				new ReflectiveDifferentiator(ObjectLevelReadQuery.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						if (object1 == null && object2 == null) {
							return new NullDiff(object1, object2, this);
						}
						if (object1 != null) {
							((ObjectLevelReadQuery) object1).getJoinedAttributeManager();
						}
						if (object2 != null) {
							((ObjectLevelReadQuery) object2).getJoinedAttributeManager();
						}
						//						if (((ObjectLevelReadQuery) object1).getReferenceClass() == null) {
//							return new NullDiff(object1, object2, this);
//						}
//						else if (((ObjectLevelReadQuery) object2).getReferenceClass() == null &&
//							((ObjectLevelReadQuery) object2).getReferenceClassName() == null) {
							//this happens with the selectionQuery on a ForeignReferenceMapping
//							return new NullDiff(object1, object2, this);
//						}
						
//						String referenceClassName1 = ((ObjectLevelReadQuery) object1).getReferenceClass().getName();
//						String referenceClassName2 = ((ObjectLevelReadQuery) object2).getReferenceClassName();
//						return EqualityDifferentiator.instance().diff(referenceClassName1, referenceClassName2);
						return super.diff(object1, object2);
					}
				});
		rd = (ReflectiveDifferentiator) diffEngine.addReflectiveDifferentiator(ObjectBuildingQuery.class);
			rd.ignoreFieldNamed("referenceClass");			
			rd.ignoreFieldNamed("referenceClassName");
	
		rd = (ReflectiveDifferentiator) diffEngine.addReflectiveDifferentiator(JoinedAttributeManager.class); 
			rd.addReferenceFieldNamed("baseQuery");
			rd.addReferenceFieldNamed("descriptor");
			rd.addReferenceFieldNamed("baseExpressionBuilder");
			rd.setFieldDifferentiator("joinedAttributeExpressions", new OrderedContainerDifferentiator(ListAdapter.instance(), diffEngine.getUserDifferentiator(Expression.class)) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}
					
					if (object1 != null && ((List)object1).size() == 0 && object2 == null) {
						return new NullDiff(object1, object2, this);
					}
					if (object2 != null && ((List)object2).size() == 0 && object1 == null) {
						return new NullDiff(object1, object2, this);
					}
					return super.diff(object1, object2);	
				}
			});			

		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(ObjectLevelReadQuery.class);
			rd.ignoreFieldNamed("defaultBuilder");
					
		rd = diffEngine.addReflectiveDifferentiator(EISInteraction.class);
			rd.setFieldDifferentiator("arguments", new OrderedContainerDifferentiator(ListAdapter.instance()) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}

					if (object1 == null && ((List) object2).size() == 0) {
						return new NullDiff(object1, object2, this);
					}
					return super.diff(object1, object2);	
				}
			});
			
		rd = diffEngine.addReflectiveDifferentiator(MappedInteraction.class);
			rd.setFieldDifferentiator("argumentNames", new OrderedContainerDifferentiator(ListAdapter.instance()) {
				public Diff diff(Object object1, Object object2) {
					if (object1 == object2) {
						return new NullDiff(object1, object2, this);
					}

					if (object1 == null && ((List) object2).size() == 0) {
						return new NullDiff(object1, object2, this);
					}
					return super.diff(object1, object2);	
				}
			});
				
		rd = diffEngine.addReflectiveDifferentiator(InterfaceContainerPolicy.class);
			rd.setKeyDifferentiator(new ReflectiveDifferentiator.KeyDifferentiator() {
				public Diff keyDiff(Object object1, Object object2) {
					return EqualityDifferentiator.instance().diff(((InterfaceContainerPolicy) object1).getContainerClassName(), ((InterfaceContainerPolicy) object2).getContainerClassName());
				}
			});
			rd.ignoreFieldNamed("containerClass");
			rd.ignoreFieldNamed("containerClassName");

		
		rd = diffEngine.addReflectiveDifferentiator(InMemoryQueryIndirectionPolicy.class);
		rd = diffEngine.addReflectiveDifferentiator(ForUpdateClause.class);
		
		rd = diffEngine.addReflectiveDifferentiator(DatabaseMapping.class);
			rd.setKeyDifferentiator(new ReflectiveDifferentiator.SimpleMethodKeyDifferentiator("getAttributeName"));
			rd.addReferenceFieldNamed("descriptor");
			rd.ignoreFieldNamed("properties");
		rd = (ReflectiveDifferentiator)diffEngine.setUserDifferentiator(AbstractTransformationMapping.class,
					new ReflectiveDifferentiator(AbstractTransformationMapping.class, diffEngine.getRecordingDifferentiator()) {
						public Diff diff(Object object1, Object object2) {
							if (((AbstractTransformationMapping) object1).getAttributeTransformerClass() == null
									&& ((AbstractTransformationMapping) object2).getAttributeTransformerClassName() == null) {
								return new NullDiff(object1, object2, this);
							}
							
							String className1 = ((AbstractTransformationMapping) object1).getAttributeTransformerClass().getName();
							String className2 = ((AbstractTransformationMapping) object2).getAttributeTransformerClassName();
								
							return EqualityDifferentiator.instance().diff(className1, className2);
						}
						
					});
				rd.addCollectionFieldNamed("fieldTransformations");
				rd.ignoreFieldNamed("attributeTransformer");  // covered by the above custom diff.

		diffEngine.setUserDifferentiator(ForeignReferenceMapping.class,
				new ReflectiveDifferentiator(ForeignReferenceMapping.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						return new CompositeDiff(object1, object2, new Diff[] {super.diff(object1, object2), bonusDiff(object1, object2)}, this);
					}
					
					private Diff bonusDiff(Object object1, Object object2) {
						if (((ForeignReferenceMapping) object1).getReferenceClass() == null) {
							return new NullDiff(object1, object2, this);
						}
						
						String referenceClassName1 = ((ForeignReferenceMapping) object1).getReferenceClass().getName();
						String referenceClassName2 = ((ForeignReferenceMapping) object2).getReferenceClassName();
						return EqualityDifferentiator.instance().diff(referenceClassName1, referenceClassName2);
					}
				});
		
		
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(ForeignReferenceMapping.class);
			rd.ignoreFieldNamed("referenceClass");			
			rd.ignoreFieldNamed("referenceClassName");						
		
		rd = diffEngine.addReflectiveDifferentiator(CollectionMapping.class);
			rd.addReferenceFieldNamed("containerPolicy"); //yes this looks weird but the containerPolicy
										//on this and on it's selectionQuery are the same.
										//don't want to make the one on the selectionQuery a reference field,
										//because that one is used elsewhere.  This might have to
										//change if we ever allow the user to edit the selectionQuery more directly
		
		diffEngine.setUserDifferentiator(ManyToManyMapping.class,
				new ReflectiveDifferentiator(ManyToManyMapping.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						DataModifyQuery query = ((DataModifyQuery) ClassTools.getFieldValue(object1, "insertQuery"));
						query.getSelectionCriteria();
						query = ((DataModifyQuery) ClassTools.getFieldValue(object1, "deleteQuery"));
						query.getSelectionCriteria();
						query = ((DataModifyQuery) ClassTools.invokeMethod(object1, "getDeleteAllQuery"));
						query.getSelectionCriteria();
						return super.diff(object1, object2);
					}
		});

			
		diffEngine.setUserDifferentiator(VariableOneToOneMapping.class,
				new ReflectiveDifferentiator(VariableOneToOneMapping.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						return new CompositeDiff(object1, object2, new Diff[] {super.diff(object1, object2), bonusDiff(object1, object2)}, this);
					}
					
					private Diff bonusDiff(Object object1, Object object2) {
						Map typeIndicatorTranslation = ((VariableOneToOneMapping) object1).getTypeIndicatorTranslation();
						Hashtable typeIndicatorNameTranslation1 = new Hashtable();
						
						for (Iterator stream = typeIndicatorTranslation.keySet().iterator(); stream.hasNext();) {
							Object key = stream.next();
							if (key instanceof Class) {
								typeIndicatorNameTranslation1.put(((Class) key).getName(), typeIndicatorTranslation.get(key));
							}
						}
						Map typeIndicatorNameTranslation2 = ((VariableOneToOneMapping) object2).getTypeIndicatorNameTranslation();
						return EqualityDifferentiator.instance().diff(typeIndicatorNameTranslation1, typeIndicatorNameTranslation2);
					}
				});
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(VariableOneToOneMapping.class);
			rd.ignoreFieldNamed("typeIndicatorTranslation");			
			rd.ignoreFieldNamed("typeIndicatorNameTranslation");						
			rd.addReferenceMapFieldsNamed("sourceToTargetQueryKeyNames");

				
			diffEngine.setUserDifferentiator(AggregateMapping.class,
					new ReflectiveDifferentiator(AggregateMapping.class, diffEngine.getRecordingDifferentiator()) {
						public Diff diff(Object object1, Object object2) {
							return new CompositeDiff(object1, object2, new Diff[] {super.diff(object1, object2), bonusDiff(object1, object2)}, this);
						}
						
						private Diff bonusDiff(Object object1, Object object2) {
							if (((AggregateMapping) object1).getReferenceClass() == null) {
								return new NullDiff(object1, object2, this);
							}
							
							String referenceClassName1 = ((AggregateMapping) object1).getReferenceClass().getName();
							String referenceClassName2 = ((AggregateMapping) object2).getReferenceClassName();
							return EqualityDifferentiator.instance().diff(referenceClassName1, referenceClassName2);
						}
					});
			
			
			rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(AggregateMapping.class);
				rd.ignoreFieldNamed("referenceClass");			
				rd.ignoreFieldNamed("referenceClassName");			

		rd = diffEngine.addReflectiveDifferentiator(EISOneToOneMapping.class);
			rd.addReferenceMapFieldsNamed("sourceToTargetKeyFields", "targetToSourceKeyFields");
				
		rd = diffEngine.addReflectiveDifferentiator(XMLDirectMapping.class);
			rd.addReferenceFieldNamed("nullPolicy");
			
		rd = diffEngine.addReflectiveDifferentiator(XMLCompositeObjectMapping.class);
			rd.addReferenceFieldNamed("nullPolicy");
			
		rd = diffEngine.addReflectiveDifferentiator(XMLCompositeCollectionMapping.class);

		rd = diffEngine.addReflectiveDifferentiator(XMLObjectReferenceMapping.class);
			rd.addReferenceMapFieldsNamed("sourceToTargetKeyFieldAssociations");
			rd.ignoreFieldNamed("sourceToTargetKeys");

			rd = diffEngine.addReflectiveDifferentiator(OneToOneMapping.class);
			rd.addReferenceMapFieldsNamed("sourceToTargetKeyFields", "targetToSourceKeyFields");
		
		rd = diffEngine.addReflectiveDifferentiator(DatabaseField.class);
			rd.addKeyFieldNamed("name");
			rd.ignoreFieldNamed("qualifiedName");			// this field is lazy-initialized
			Differentiator tableFieldDifferentiator = rd.getFieldDifferentiator("table");
			rd.setFieldDifferentiator("table", new DatabaseFieldTableFieldDifferentiator(tableFieldDifferentiator));
		
		rd = diffEngine.addReflectiveDifferentiator(DatabaseTable.class);
			rd.ignoreFieldNamed("qualifiedName");			// this field is lazy-initialized

		rd = diffEngine.addReflectiveDifferentiator(IndirectionPolicy.class);
			rd.addReferenceFieldNamed("mapping");
			
		rd = diffEngine.addReflectiveDifferentiator(AttributeAccessor.class);
		rd = diffEngine.addReflectiveDifferentiator(ObjectTypeConverter.class);
			rd.ignoreFieldNamed("mapping");//set in the login initialize method
			
			
		diffEngine.setUserDifferentiator(TypeConversionConverter.class,
				new ReflectiveDifferentiator(TypeConversionConverter.class, diffEngine.getRecordingDifferentiator()) {
					public Diff diff(Object object1, Object object2) {
						return new CompositeDiff(object1, object2, new Diff[] {super.diff(object1, object2), bonusDiff(object1, object2)}, this);
					}
					
					private Diff bonusDiff(Object object1, Object object2) {
						if (((TypeConversionConverter) object1).getDataClass() == null) {
							return new NullDiff(object1, object2, this);
						}
						
						String dataClassName1 = ((TypeConversionConverter) object1).getDataClass().getName();
						String dataClassName2 = ((TypeConversionConverter) object2).getDataClassName();
						String objectClassName1 = ((TypeConversionConverter) object1).getObjectClass().getName();
						String objectClassName2 = ((TypeConversionConverter) object2).getObjectClassName();
						
						return new CompositeDiff(
								object1, 
								object2, 
								new Diff[] {
										EqualityDifferentiator.instance().diff(dataClassName1, dataClassName2),
										EqualityDifferentiator.instance().diff(objectClassName1, objectClassName2)
								}, 
								this
							);
					}
				});
		
		
		
		rd = (ReflectiveDifferentiator) diffEngine.getUserDifferentiator(TypeConversionConverter.class);
			rd.ignoreFieldNamed("dataClass");			
			rd.ignoreFieldNamed("dataClassName");
			rd.ignoreFieldNamed("objectClass");			
			rd.ignoreFieldNamed("objectClassName");	
			rd.ignoreFieldNamed("mapping");
		rd = diffEngine.addReflectiveDifferentiator(SerializedObjectConverter.class);
			rd.ignoreFieldNamed("mapping");//this is set in the login initialize method
		
		rd = diffEngine.addReflectiveDifferentiator(MethodBasedAttributeTransformer.class);
			rd.addReferenceFieldNamed("mapping");//TODO remove this if we login before comparing the project, this is set in the initialize method
		
		rd = diffEngine.addReflectiveDifferentiator(FieldTransformation.class);
		rd = diffEngine.addReflectiveDifferentiator(MethodBasedFieldTransformation.class);
			rd.addKeyFieldNamed("methodName");
			
		rd = (ReflectiveDifferentiator)diffEngine.setUserDifferentiator(TransformerBasedFieldTransformation.class,
					new ReflectiveDifferentiator(TransformerBasedFieldTransformation.class, diffEngine.getRecordingDifferentiator()) {
						public Diff diff(Object object1, Object object2) {
							if (((TransformerBasedFieldTransformation) object1).getTransformerClass() == null
									&& ((TransformerBasedFieldTransformation) object2).getTransformerClassName() == null) {
								return new NullDiff(object1, object2, this);
							}
							
							String className1 = ((TransformerBasedFieldTransformation) object1).getTransformerClass().getName();
							String className2 = ((TransformerBasedFieldTransformation) object2).getTransformerClassName();
								
							return EqualityDifferentiator.instance().diff(className1, className2);
						}
						
					});
			rd.setKeyDifferentiator(new ReflectiveDifferentiator.SimpleMethodKeyDifferentiator("getFieldName"));
			
		rd = diffEngine.addReflectiveDifferentiator(EISConnectionSpec.class);
		rd = diffEngine.addReflectiveDifferentiator(NamespaceResolver.class);
		rd = diffEngine.addReflectiveDifferentiator(InitialContext.class);
		
		rd = diffEngine.addReflectiveDifferentiator(XMLComparer.class);
//		rd = diffEngine.addReflectiveDifferentiator(XDKTransformer.class);

		return diffEngine;
	}	
	
	/**
	 * checked exceptions suck
	 */
	private static Class classForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static SPIManager buildSPIManager() {
		SimpleSPIManager mgr = new SimpleSPIManager();
		mgr.setExternalClassRepositoryFactory(CFExternalClassRepositoryFactory.instance());
		mgr.setExternalDatabaseFactory(JDBCExternalDatabaseFactory.instance());
		return mgr;
	}

	private MappingsModelTestTools() {
		super();
		throw new UnsupportedOperationException();
	}


	// ********** member classes **********

	/**
	 * this differentiator will ignore any types added or removed from
	 * the class repository; as long as the added or removed types
	 * are not referenced anywhere else in the object graph, this should
	 * be OK; which should be the case since, if a type is referenced
	 * anywhere else in the object graph, it will be present in the
	 * repository (or something is very wrong); the added or removed
	 * types are typically byproducts of code that "faults in" types
	 * (e.g. MWClass#isAssignableToSet() will fault in the MWClass
	 * for java.util.Set)
	 */
	public static class ClassRepositoryTypesFieldDifferentiator extends DifferentiatorWrapper {
		public ClassRepositoryTypesFieldDifferentiator(Differentiator differentiator) {
			super(differentiator);
		}
		public Diff diff(Object object1, Object object2) {
			return new ClassRepositoryTypesFieldDiff(super.diff(object1, object2), this);
		}
	}

	/**
	 * note that if any of the types are different, this
	 * diff will report the adds and removes; this is
	 * because we only hack the #different() method
	 */
	public static class ClassRepositoryTypesFieldDiff extends DiffWrapper {
		public ClassRepositoryTypesFieldDiff(Diff diff, Differentiator differentiator) {
			super(diff, differentiator);
		}
		public boolean identical() {
			return ! this.different();
		}
		/**
		 * ignore the adds and removes; look only at the
		 * diffs for types in both maps
		 */
		public boolean different() {
			Diff[] diffs = ((ContainerDiff) this.getDiff()).getDiffs();
			for (int i = diffs.length; i-- > 0; ) {
				if (diffs[i].different()) {
					return true;
				}
			}
			return false;
		}
	}


	/**
	 * if one or the other of a pair of database fields does not have a
	 * table (i.e. the table does not have a name), ignore the table
	 */
	public static class DatabaseFieldTableFieldDifferentiator extends DifferentiatorWrapper {
		public DatabaseFieldTableFieldDifferentiator(Differentiator differentiator) {
			super(differentiator);
		}
		public Diff diff(Object object1, Object object2) {
			return new DatabaseFieldTableFieldDiff(super.diff(object1, object2), this);
		}
	}

	public static class DatabaseFieldTableFieldDiff extends DiffWrapper {
		public DatabaseFieldTableFieldDiff(Diff diff, Differentiator differentiator) {
			super(diff, differentiator);
		}
		public boolean identical() {
			return ! this.different();
		}
		/**
		 * if either table lacks a name, do not compare them
		 */
		public boolean different() {
			DatabaseTable table1 = (DatabaseTable) this.getObject1();
			DatabaseTable table2 = (DatabaseTable) this.getObject2();
			if ((table1.getName().length() == 0) || (table2.getName().length() == 0)) {
				return false;
			}
			return super.different();
		}
	}

}
