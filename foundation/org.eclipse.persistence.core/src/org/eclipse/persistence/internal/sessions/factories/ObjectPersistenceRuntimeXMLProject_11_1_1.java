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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static java.lang.Integer.MIN_VALUE;

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.DatabaseType;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.identitymaps.SoftIdentityMap;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.queries.CollectionContainerPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.SortedCollectionContainerPolicy;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.mappings.structures.ObjectRelationalDatabaseField;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.documentpreservation.AppendNewElementsOrderingPolicy;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.documentpreservation.IgnoreNewElementsOrderingPolicy;
import org.eclipse.persistence.oxm.documentpreservation.NodeOrderingPolicy;
import org.eclipse.persistence.oxm.documentpreservation.RelativePositionOrderingPolicy;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
import org.eclipse.persistence.oxm.mappings.XMLNillableMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.internal.oxm.XMLChoiceFieldToClassAssociation;
import org.eclipse.persistence.internal.oxm.documentpreservation.DescriptorLevelDocumentPreservationPolicy;
import org.eclipse.persistence.internal.oxm.documentpreservation.NoDocumentPreservationPolicy;
import org.eclipse.persistence.internal.oxm.documentpreservation.XMLBinderPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleArrayType;
import org.eclipse.persistence.platform.database.oracle.jdbc.OracleObjectType;
import org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCursor;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredFunctionCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLCollection;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.CursoredStreamPolicy;
import org.eclipse.persistence.queries.ScrollableCursorPolicy;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.IN;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.INOUT;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT;
import static org.eclipse.persistence.internal.databaseaccess.DatasourceCall.OUT_CURSOR;
import static org.eclipse.persistence.internal.helper.DatabaseField.NULL_SQL_TYPE;
import static org.eclipse.persistence.sessions.factories.XMLProjectReader.SCHEMA_DIR;
import static org.eclipse.persistence.sessions.factories.XMLProjectReader.TOPLINK_11_SCHEMA;

/**
 * INTERNAL:
 * Define the TopLink OX project and descriptor information to read an AS 11<i>g</i>
 * (11.1.1) project from an XML file.
 * Note any changes must be reflected in the XML schema.
 * This project contains the 11gR1 mappings to the 11gR1 schema.
 */
@SuppressWarnings("unchecked")
public class ObjectPersistenceRuntimeXMLProject_11_1_1 extends ObjectPersistenceRuntimeXMLProject {

    /**
     * Wrap the type in a type wrapper to handle XML conversion.
     */
    public static DatabaseTypeWrapper wrapType(DatabaseType databaseType) {
        if (databaseType.isComplexDatabaseType()) {
            ComplexDatabaseType complexType = (ComplexDatabaseType)databaseType;
            if (complexType.isArray()) {
                return  new OracleArrayTypeWrapper(databaseType);
            }
            if (complexType.isStruct()) {
            	return new OracleObjectTypeWrapper(databaseType);
            } 
            if (complexType.isRecord()) {
            	return new PLSQLRecordWrapper(databaseType);
            }
            if (complexType.isCollection()) {
            	return new PLSQLCollectionWrapper(databaseType);
            }
            if (complexType.isCursor()) {
                return new PLSQLCursorWrapper(databaseType);
            }
        } else if (databaseType.isJDBCType()) {
            return new JDBCTypeWrapper(databaseType);
        } else {
            return new SimplePLSQLTypeWrapper(databaseType);
        }
        return null;
    }
    /**
     * Unwrap the type from a type wrapper to handle XML conversion.
     */
    public static DatabaseType unwrapType(DatabaseTypeWrapper databaseType) {
        return databaseType.getWrappedType();
    }

    public ObjectPersistenceRuntimeXMLProject_11_1_1() {
        super();
    }

    @Override
    public void buildDescriptors() {
        super.buildDescriptors();
        addDescriptor(buildCursoredStreamPolicyDescriptor());
        addDescriptor(buildScrollableCursorPolicyDescriptor());

        // Stored procedure arguments
        addDescriptor(buildStoredProcedureArgumentDescriptor());
        addDescriptor(buildStoredProcedureOutArgumentsDescriptor());
        addDescriptor(buildStoredProcedureInOutArgumentsDescriptor());
        addDescriptor(buildStoredProcedureOutCursorArgumentsDescriptor());
        addDescriptor(buildStoredProcedureCallDescriptor());
        // 5877994 -- add metadata support for Stored Function Calls
        addDescriptor(buildStoredFunctionCallDescriptor());

        //5963607 -- add Sorted Collection mapping support
        addDescriptor(buildSortedCollectionContainerPolicyDescriptor());

        //TopLink OXM
        addDescriptor(buildXMLAnyAttributeMappingDescriptor());
        addDescriptor(buildXMLCollectionReferenceMappingDescriptor());
        addDescriptor(buildXMLObjectReferenceMappingDescriptor());
        addDescriptor(buildXMLFragmentMappingDescriptor());
        addDescriptor(buildXMLFragmentCollectionMappingDescriptor());
        addDescriptor(buildXMLChoiceCollectionMappingDescriptor());
        addDescriptor(buildXMLChoiceFieldToClassAssociationDescriptor());
        addDescriptor(buildXMLChoiceObjectMappingDescriptor());

        // Add Null Policy Mappings
        addDescriptor(buildAbstractNullPolicyDescriptor());
        addDescriptor(buildNullPolicyDescriptor());
        addDescriptor(buildIsSetNullPolicyDescriptor());

        // 6029568 -- add metadata support for PLSQLStoredProcedureCall
        addDescriptor(buildObjectTypeFieldAssociationDescriptor());
        addDescriptor(buildDatabaseTypeWrapperDescriptor());
        addDescriptor(buildJDBCTypeWrapperDescriptor());
        addDescriptor(buildSimplePLSQLTypeWrapperDescriptor());
        addDescriptor(buildOracleArrayTypeWrapperDescriptor());
        addDescriptor(buildOracleObjectTypeWrapperDescriptor());
        addDescriptor(buildPLSQLCursorWrapperDescriptor());
        addDescriptor(buildPLSQLrecordWrapperDescriptor());
        addDescriptor(buildPLSQLCollectionWrapperDescriptor());
        addDescriptor(buildPLSQLargumentDescriptor());
        addDescriptor(buildPLSQLStoredProcedureCallDescriptor());
        addDescriptor(buildPLSQLStoredFunctionCallDescriptor());
        addDescriptor(buildOracleArrayTypeDescriptor());
        addDescriptor(buildOracleObjectTypeDescriptor());
        addDescriptor(buildPLSQLrecordDescriptor());
        addDescriptor(buildPLSQLCollectionDescriptor());
        addDescriptor(buildPLSQLCursorDescriptor());

        // 5757849 -- add metadata support for ObjectRelationalDatabaseField
        addDescriptor(buildObjectRelationalDatabaseFieldDescriptor());
        
        // 242452 -- add metadata support for XMLLogin's DocumentPreservationPolicy
        addDescriptor(buildDocumentPreservationPolicyDescriptor());
        addDescriptor(buildDescriptorLevelDocumentPreservationPolicyDescriptor());
        addDescriptor(buildNoDocumentPreservationPolicyDescriptor());
        addDescriptor(buildXMLBinderPolicyDescriptor());
        addDescriptor(buildNodeOrderingPolicyDescriptor());
        addDescriptor(buildAppendNewElementsOrderingPolicyDescriptor());
        addDescriptor(buildIgnoreNewElementsOrderingPolicyDescriptor());
        addDescriptor(buildRelativePositionOrderingPolicyDescriptor());
    }

    @Override
    protected ClassDescriptor buildProjectDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildProjectDescriptor();
        descriptor.setSchemaReference(new XMLSchemaClassPathReference(SCHEMA_DIR + TOPLINK_11_SCHEMA));

        XMLDirectMapping defaultTemporalMutableMapping = new XMLDirectMapping();
        defaultTemporalMutableMapping.setAttributeName("defaultTemporalMutable");
        defaultTemporalMutableMapping.setSetMethodName("setDefaultTemporalMutable");
        defaultTemporalMutableMapping.setGetMethodName("getDefaultTemporalMutable");
        defaultTemporalMutableMapping.setXPath(getSecondaryNamespaceXPath() + "default-temporal-mutable/text()");
        defaultTemporalMutableMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(defaultTemporalMutableMapping);

        return descriptor;
    }

    /**
     * INTERNAL:
     */
    @Override
    protected ConstantTransformer getConstantTransformerForProjectVersionMapping() {
        return new ConstantTransformer("Oracle TopLink - 11g Release 1 (11.1.1)");
    }

    @Override
    public ClassDescriptor buildDatabaseLoginDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildDatabaseLoginDescriptor();

        XMLDirectMapping shouldBindAllParametersMapping = (XMLDirectMapping)descriptor.getMappingForAttributeName("shouldBindAllParameters");
        shouldBindAllParametersMapping.setNullValue(Boolean.TRUE);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildDatabaseMappingDescriptor() {
        ClassDescriptor descriptor = super.buildDatabaseMappingDescriptor();

        descriptor.getInheritancePolicy().addClassIndicator(XMLBinaryDataMapping.class, getPrimaryNamespaceXPath() + "xml-binary-data-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLBinaryDataCollectionMapping.class, getPrimaryNamespaceXPath() + "xml-binary-data-collection-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLFragmentMapping.class, getPrimaryNamespaceXPath() + "xml-fragment-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLFragmentCollectionMapping.class, getPrimaryNamespaceXPath() + "xml-fragment-collection-mapping");

        descriptor.getInheritancePolicy().addClassIndicator(XMLCollectionReferenceMapping.class, getPrimaryNamespaceXPath() + "xml-collection-reference-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLObjectReferenceMapping.class, getPrimaryNamespaceXPath() + "xml-object-reference-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLAnyAttributeMapping.class, getPrimaryNamespaceXPath() + "xml-any-attribute-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLChoiceObjectMapping.class, getPrimaryNamespaceXPath() + "xml-choice-object-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLChoiceCollectionMapping.class, getPrimaryNamespaceXPath() + "xml-choice-collection-mapping");

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildAbstractDirectMappingDescriptor() {

        XMLDescriptor descriptor = (XMLDescriptor)super.buildAbstractDirectMappingDescriptor();

        XMLDirectMapping attributeClassificationMapping = new XMLDirectMapping();
        attributeClassificationMapping.setAttributeName("attributeClassification");
        attributeClassificationMapping.setGetMethodName("getAttributeClassification");
        attributeClassificationMapping.setSetMethodName("setAttributeClassification");
        attributeClassificationMapping.setXPath(getPrimaryNamespaceXPath() + "attribute-classification/text()");
        descriptor.addMapping(attributeClassificationMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildObjectLevelReadQueryDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildObjectLevelReadQueryDescriptor();

        XMLDirectMapping readOnlyMapping = new XMLDirectMapping();
        readOnlyMapping.setAttributeName("isReadOnly");
        readOnlyMapping.setXPath(getPrimaryNamespaceXPath() + "read-only/text()");
        readOnlyMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(readOnlyMapping);

        XMLDirectMapping joinSubclassesMapping = new XMLDirectMapping();
        joinSubclassesMapping.setAttributeName("shouldOuterJoinSubclasses");
        joinSubclassesMapping.setXPath(getPrimaryNamespaceXPath() + "outer-join-subclasses/text()");
        descriptor.addMapping(joinSubclassesMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildInheritancePolicyDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildInheritancePolicyDescriptor();

        XMLDirectMapping joinSubclassesMapping = new XMLDirectMapping();
        joinSubclassesMapping.setAttributeName("shouldOuterJoinSubclasses");
        joinSubclassesMapping.setXPath(getPrimaryNamespaceXPath() + "outer-join-subclasses/text()");
        joinSubclassesMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(joinSubclassesMapping);

        return descriptor;
    }

    protected ClassDescriptor buildCursoredStreamPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(CursoredStreamPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(ContainerPolicy.class);

        return descriptor;
    }

    /*
     * this overridden method does <b>not</b> do super buildRelationalDescriptorDescriptor()
     * on purpose 'cause the way the fields are mapped/named changed from 10.3.x to 11.1.x
     */
    @Override
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

        XMLCompositeCollectionMapping foreignKeyForMultipleTables = new XMLCompositeCollectionMapping();
        foreignKeyForMultipleTables.setReferenceClass(Association.class);
        foreignKeyForMultipleTables.setAttributeName("foreignKeysForMultipleTables");
        foreignKeyForMultipleTables.setXPath(getPrimaryNamespaceXPath() + "foreign-keys-for-multiple-table/" + getSecondaryNamespaceXPath() + "field-reference");
        foreignKeyForMultipleTables.setAttributeAccessor(new AttributeAccessor() {
                public Object getAttributeValueFromObject(Object object) {
                    ClassDescriptor descriptor = (ClassDescriptor) object;
                    Vector associations = descriptor.getMultipleTableForeignKeyAssociations();
                    for (int index = 0; index < associations.size(); index++) {
                        Association association = (Association) associations.get(index);
                        String targetPrimaryKeyFieldName = (String) association.getKey();
                        association.setKey(new DatabaseField((String) association.getValue()));
                        association.setValue(new DatabaseField(targetPrimaryKeyFieldName));
                    }
                    return associations;
                }
                public void setAttributeValueInObject(Object object, Object value) {
                    ClassDescriptor descriptor = (ClassDescriptor) object;
                    Vector associations = (Vector) value;
                    for (int index = 0; index < associations.size(); index++) {
                        Association association = (Association) associations.get(index);
                        association.setKey(((DatabaseField) association.getKey()).getQualifiedName());
                        association.setValue(((DatabaseField) association.getValue()).getQualifiedName());
                    }
                    descriptor.setForeignKeyFieldNamesForMultipleTable(associations);
                }
            });
        descriptor.addMapping(foreignKeyForMultipleTables);

        return descriptor;
    }

    protected ClassDescriptor buildScrollableCursorPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(ScrollableCursorPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(ContainerPolicy.class);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildContainerPolicyDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildContainerPolicyDescriptor();

        descriptor.getInheritancePolicy().addClassIndicator(ScrollableCursorPolicy.class, getPrimaryNamespaceXPath() + "scrollable-cursor-policy");
        descriptor.getInheritancePolicy().addClassIndicator(CursoredStreamPolicy.class, getPrimaryNamespaceXPath() + "cursored-stream-policy");
        descriptor.getInheritancePolicy().addClassIndicator(SortedCollectionContainerPolicy.class, getPrimaryNamespaceXPath() + "sorted-collection-container-policy");

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildOneToOneMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildOneToOneMappingDescriptor();
        descriptor.removeMappingForAttributeName("joinFetch");

        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("joinFetch");
        joinFetchMapping.setXPath(getPrimaryNamespaceXPath() + "join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", Integer.valueOf(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", Integer.valueOf(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", Integer.valueOf(ForeignReferenceMapping.NONE));
        joinFetchMapping.setConverter(joinFetchConverter);
        joinFetchMapping.setNullValue(ForeignReferenceMapping.NONE);
        descriptor.addMapping(joinFetchMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildOXXMLDescriptorDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildOXXMLDescriptorDescriptor();

        XMLCompositeObjectMapping defaultRootElementFieldMapping = new XMLCompositeObjectMapping();
        defaultRootElementFieldMapping.setAttributeName("defaultRootElementField");
        defaultRootElementFieldMapping.setGetMethodName("getDefaultRootElementField");
        defaultRootElementFieldMapping.setSetMethodName("setDefaultRootElementField");
        defaultRootElementFieldMapping.setXPath(getPrimaryNamespaceXPath() + "default-root-element-field");
        defaultRootElementFieldMapping.setReferenceClass(XMLField.class);
        NullPolicy defaultRootElementFieldNullPolicy = new NullPolicy();
        defaultRootElementFieldNullPolicy.setSetPerformedForAbsentNode(false);
        defaultRootElementFieldMapping.setNullPolicy(defaultRootElementFieldNullPolicy);
        /* order is important for writing out
         * don't use addMapping: set parent descriptor and add after
         * first mapping built in super.buildOXXMLDescriptorDescriptor()
         */
        defaultRootElementFieldMapping.setDescriptor(descriptor);
        descriptor.getMappings().add(1, defaultRootElementFieldMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildManyToManyMappingMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildManyToManyMappingMappingDescriptor();

        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("joinFetch");
        joinFetchMapping.setXPath(getPrimaryNamespaceXPath() + "join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", Integer.valueOf(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", Integer.valueOf(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", Integer.valueOf(ForeignReferenceMapping.NONE));
        joinFetchMapping.setConverter(joinFetchConverter);
        joinFetchMapping.setNullValue(ForeignReferenceMapping.NONE);
        descriptor.addMapping(joinFetchMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildOneToManyMappingMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildOneToManyMappingMappingDescriptor();

        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("joinFetch");
        joinFetchMapping.setXPath(getPrimaryNamespaceXPath() + "join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", Integer.valueOf(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", Integer.valueOf(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", Integer.valueOf(ForeignReferenceMapping.NONE));
        joinFetchMapping.setConverter(joinFetchConverter);
        joinFetchMapping.setNullValue(ForeignReferenceMapping.NONE);
        descriptor.addMapping(joinFetchMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildDirectCollectionMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildDirectCollectionMappingDescriptor();

        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("joinFetch");
        joinFetchMapping.setXPath(getPrimaryNamespaceXPath() + "join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", Integer.valueOf(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", Integer.valueOf(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", Integer.valueOf(ForeignReferenceMapping.NONE));
        joinFetchMapping.setConverter(joinFetchConverter);
        joinFetchMapping.setNullValue(ForeignReferenceMapping.NONE);
        descriptor.addMapping(joinFetchMapping);

        return descriptor;
    }

    protected ClassDescriptor buildSortedCollectionContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SortedCollectionContainerPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(CollectionContainerPolicy.class);

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeName("comparatorClass");
        keyMapping.setGetMethodName("getComparatorClass");
        keyMapping.setSetMethodName("setComparatorClass");
        keyMapping.setXPath(getPrimaryNamespaceXPath() + "comparator-class/text()");
        descriptor.addMapping(keyMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildAggregateCollectionMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildAggregateCollectionMappingDescriptor();

        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("joinFetch");
        joinFetchMapping.setXPath(getPrimaryNamespaceXPath() + "join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", Integer.valueOf(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", Integer.valueOf(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", Integer.valueOf(ForeignReferenceMapping.NONE));
        joinFetchMapping.setConverter(joinFetchConverter);
        joinFetchMapping.setNullValue(ForeignReferenceMapping.NONE);
        descriptor.addMapping(joinFetchMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildXMLAnyCollectionMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildXMLAnyCollectionMappingDescriptor();

        XMLDirectMapping xmlRootMapping = new XMLDirectMapping();
        xmlRootMapping.setAttributeName("useXMLRoot");
        xmlRootMapping.setGetMethodName("usesXMLRoot");
        xmlRootMapping.setSetMethodName("setUseXMLRoot");
        xmlRootMapping.setXPath(getPrimaryNamespaceXPath() + "use-xml-root/text()");
        xmlRootMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(xmlRootMapping);

        XMLDirectMapping keepAsElementMapping = new XMLDirectMapping();
        keepAsElementMapping.setAttributeName("keepAsElementPolicy");
        keepAsElementMapping.setGetMethodName("getKeepAsElementPolicy");
        keepAsElementMapping.setSetMethodName("setKeepAsElementPolicy");
        keepAsElementMapping.setXPath(getPrimaryNamespaceXPath() + "keep-as-element-policy");
        EnumTypeConverter converter = new EnumTypeConverter(keepAsElementMapping, UnmarshalKeepAsElementPolicy.class, false);
        keepAsElementMapping.setConverter(converter);
        descriptor.addMapping(keepAsElementMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLAnyAttributeMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLAnyAttributeMapping.class);

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

    protected ClassDescriptor buildXMLCollectionReferenceMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLCollectionReferenceMapping.class);
        descriptor.getInheritancePolicy().setParentClass(XMLObjectReferenceMapping.class);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("containerPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
        containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "containerpolicy");
        descriptor.addMapping(containerPolicyMapping);

        XMLDirectMapping useSingleNodeMapping = new XMLDirectMapping();
        useSingleNodeMapping.setAttributeName("usesSingleNode");
        useSingleNodeMapping.setGetMethodName("usesSingleNode");
        useSingleNodeMapping.setSetMethodName("setUsesSingleNode");
        useSingleNodeMapping.setXPath(getPrimaryNamespaceXPath() + "uses-single-node/text()");
        descriptor.addMapping(useSingleNodeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLObjectReferenceMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLObjectReferenceMapping.class);
        descriptor.getInheritancePolicy().setParentClass(AggregateMapping.class);

        XMLCompositeCollectionMapping sourceToTargetKeyFieldAssociationsMapping = new XMLCompositeCollectionMapping();
        sourceToTargetKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        // Handle translation of foreign key associations to hashmaps.
        sourceToTargetKeyFieldAssociationsMapping.setAttributeAccessor(new AttributeAccessor() {
                public Object getAttributeValueFromObject(Object object) {
                    Map sourceToTargetKeyFields = ((XMLObjectReferenceMapping) object).getSourceToTargetKeyFieldAssociations();
                    List associations = new ArrayList(sourceToTargetKeyFields.size());
                    Iterator iterator = sourceToTargetKeyFields.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry)iterator.next();
                        associations.add(new Association(entry.getKey(), entry.getValue()));
                    }
                    return associations;
                }

                public void setAttributeValueInObject(Object object, Object value) {
                    XMLObjectReferenceMapping mapping = (XMLObjectReferenceMapping) object;
                    List associations = (List)value;
                    mapping.setSourceToTargetKeyFieldAssociations(new HashMap(associations.size() + 1));
                    Iterator iterator = associations.iterator();
                    while (iterator.hasNext()) {
                        Association association = (Association)iterator.next();
                        mapping.getSourceToTargetKeyFieldAssociations().put(association.getKey(), association.getValue());
                    }
                }
            });
        sourceToTargetKeyFieldAssociationsMapping.setAttributeName("sourceToTargetKeyFieldAssociations");
        sourceToTargetKeyFieldAssociationsMapping.setXPath(getPrimaryNamespaceXPath() + "source-to-target-key-field-association/" + getSecondaryNamespaceXPath() + "field-reference");
        descriptor.addMapping(sourceToTargetKeyFieldAssociationsMapping);

        XMLCompositeCollectionMapping sourceToTargetKeysMapping = new XMLCompositeCollectionMapping();
        sourceToTargetKeysMapping.setReferenceClass(DatabaseField.class);
        sourceToTargetKeysMapping.setAttributeName("sourceToTargetKeys");
        sourceToTargetKeysMapping.setXPath(getPrimaryNamespaceXPath() + "source-to-target-key-fields/" + getPrimaryNamespaceXPath() + "field");
        ((XMLField)sourceToTargetKeysMapping.getField()).setLeafElementType(fieldQname);
        descriptor.addMapping(sourceToTargetKeysMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLFragmentMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLFragmentMapping.class);
        descriptor.getInheritancePolicy().setParentClass(XMLDirectMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildXMLFragmentCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLFragmentCollectionMapping.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeDirectCollectionMapping.class);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildXMLAnyObjectMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildXMLAnyObjectMappingDescriptor();

        XMLDirectMapping xmlRootMapping = new XMLDirectMapping();
        xmlRootMapping.setAttributeName("useXMLRoot");
        xmlRootMapping.setGetMethodName("usesXMLRoot");
        xmlRootMapping.setSetMethodName("setUseXMLRoot");
        xmlRootMapping.setXPath(getPrimaryNamespaceXPath() + "use-xml-root/text()");
        xmlRootMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(xmlRootMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildXMLFieldDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildXMLFieldDescriptor();

        XMLDirectMapping leafElementTypeMapping = new XMLDirectMapping();
        leafElementTypeMapping.setAttributeName("leafElementType");
        leafElementTypeMapping.setGetMethodName("getLeafElementType");
        leafElementTypeMapping.setSetMethodName("setLeafElementType");
        leafElementTypeMapping.setXPath(getPrimaryNamespaceXPath() + "leaf-element-type/text()");
        descriptor.addMapping(leafElementTypeMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildClassDescriptorDescriptor() {
        ClassDescriptor descriptor = super.buildClassDescriptorDescriptor();

        XMLDirectMapping identityMapClassMapping = (XMLDirectMapping)descriptor.getMappingForAttributeName("identityMapClass");
        ObjectTypeConverter identityMapClassConverter = (ObjectTypeConverter)identityMapClassMapping.getConverter();
        identityMapClassConverter.addConversionValue("soft-reference", SoftIdentityMap.class);

	    XMLDirectMapping remoteIdentityMapClassMapping = (XMLDirectMapping)descriptor.getMappingForAttributeName("remoteIdentityMapClass");
        ObjectTypeConverter remoteIdentityMapClassConverter = (ObjectTypeConverter)remoteIdentityMapClassMapping.getConverter();
        remoteIdentityMapClassConverter.addConversionValue("soft-reference", SoftIdentityMap.class);

        XMLDirectMapping unitOfWorkCacheIsolationLevelMapping = (XMLDirectMapping)descriptor.getMappingForAttributeName("unitOfWorkCacheIsolationLevel");
        ObjectTypeConverter unitOfWorkCacheIsolationLevelConverter = (ObjectTypeConverter)unitOfWorkCacheIsolationLevelMapping.getConverter();
        unitOfWorkCacheIsolationLevelConverter.addConversionValue("default", Integer.valueOf(ClassDescriptor.UNDEFINED_ISOLATATION));
        unitOfWorkCacheIsolationLevelMapping.setNullValue(Integer.valueOf(ClassDescriptor.UNDEFINED_ISOLATATION));

        return descriptor;
    }

    // support for Stored Procedure/Function Calls
    @Override
    protected ClassDescriptor buildCallDescriptor() {
      XMLDescriptor descriptor = (XMLDescriptor)super.buildCallDescriptor();
      descriptor.getInheritancePolicy().addClassIndicator(StoredProcedureCall.class,
          getPrimaryNamespaceXPath() + "stored-procedure-call");
      descriptor.getInheritancePolicy().addClassIndicator(StoredFunctionCall.class,
          getPrimaryNamespaceXPath() + "stored-function-call");
      descriptor.getInheritancePolicy().addClassIndicator(PLSQLStoredProcedureCall.class,
          getPrimaryNamespaceXPath() + "plsql-stored-procedure-call");
      descriptor.getInheritancePolicy().addClassIndicator(PLSQLStoredFunctionCall.class,
              getPrimaryNamespaceXPath() + "plsql-stored-function-call");
      return descriptor;
    }

    /**
     * <p>
     * <b>Purpose</b>: helper classes - represent stored procedure arguments in XML
     * <p>
     *
     * @author Kyle Chen
     * @since 11
     *
     * mnorman - moved from o.t.i.workbench.storedprocedure to
     *           be nested inner classes of ObjectPersistenceRuntimeXMLProject_11_1_1
     *           so that they don't 'leak' out into the runtime
     */
    class StoredProcedureArgument {
          String argumentName;
          String argumentFieldName;
          Class argumentType;
          String argumentTypeName;
          int argumentSQLType = NULL_SQL_TYPE;
          String argumentSqlTypeName;
          Object argumentValue;
          StoredProcedureArgument nestedType;
          StoredProcedureArgument() {
              super();
          }
          StoredProcedureArgument(DatabaseField dbfield) {
              this.setDatabaseField(dbfield);
          }
          Integer getDirection() {
              return IN;
          }
          DatabaseField getDatabaseField() {
              DatabaseField dbfield = new DatabaseField(argumentFieldName == null ? "" : argumentFieldName);
              dbfield.type = argumentType;
              if (argumentType != null) {
                  dbfield.typeName = argumentType.getName();
              }
              else {
                  dbfield.typeName = argumentTypeName;
              }
              dbfield.sqlType = argumentSQLType;
              if ((argumentSqlTypeName != null) &&
                  (!argumentSqlTypeName.equals(""))) {
                  dbfield = new ObjectRelationalDatabaseField(dbfield);
                  ((ObjectRelationalDatabaseField)dbfield).setSqlTypeName(argumentSqlTypeName);
                  ((ObjectRelationalDatabaseField)dbfield).setSqlType(argumentSQLType);
                  if (nestedType != null) {
                      ((ObjectRelationalDatabaseField)dbfield).setNestedTypeField(
                          nestedType.getDatabaseField());
                  }
              }
              return dbfield;
          }
          void setDatabaseField(DatabaseField dbField) {
              String fieldName = dbField.getName();
              if (fieldName != null && fieldName.length() > 0) {
                 argumentFieldName = fieldName;
              }
              argumentType = dbField.type;
              argumentTypeName = dbField.typeName;
              argumentSQLType = dbField.sqlType;
              if (dbField.isObjectRelationalDatabaseField()) {
                  ObjectRelationalDatabaseField ordField =
                      (ObjectRelationalDatabaseField)dbField;
                  argumentSqlTypeName = ordField.getSqlTypeName();
                  argumentSQLType = ordField.getSqlType();
                  DatabaseField tempField = ordField.getNestedTypeField();
                  if (tempField != null) {
                      nestedType = new StoredProcedureArgument(tempField);
                  }
              }
          }
        public String getArgumentTypeName() {
            return argumentTypeName;
        }
        public void setArgumentTypeName(String argumentTypeName) {
            this.argumentTypeName = argumentTypeName;
        }
    }

    class StoredProcedureInOutArgument extends StoredProcedureArgument {
          String outputArgumentName;
          StoredProcedureInOutArgument() {
              super();
          }
          StoredProcedureInOutArgument(DatabaseField dbfield) {
              super(dbfield);
          }
          Integer getDirection() {
              return INOUT;
          }
    }

    class StoredProcedureOutArgument extends StoredProcedureArgument {
        StoredProcedureOutArgument() {
            super();
        }
        StoredProcedureOutArgument(DatabaseField dbfield){
            super(dbfield);
        }
        Integer getDirection() {
            return OUT;
        }
    }

    class StoredProcedureOutCursorArgument extends StoredProcedureOutArgument {
        StoredProcedureOutCursorArgument() {
            super();
        }
        StoredProcedureOutCursorArgument(DatabaseField dbfield){
            super(dbfield);
        }
        Integer getDirection() {
            return OUT_CURSOR;
        }
    }

    enum StoredProcedureArgumentType {
        STORED_PROCEDURE_ARG,
        STORED_PROCEDURE_INOUT_ARG,
        STORED_PROCEDURE_OUT_ARG,
        STORED_PROCEDURE_OUTCURSOR_ARG
    }
    class StoredProcedureArgumentInstantiationPolicy extends InstantiationPolicy {
        ObjectPersistenceRuntimeXMLProject_11_1_1 outer;
        StoredProcedureArgumentType argType;
        StoredProcedureArgumentInstantiationPolicy(
            ObjectPersistenceRuntimeXMLProject_11_1_1 outer, StoredProcedureArgumentType argType) {
            this.outer = outer;
            this.argType = argType;
        }
        @Override
        public Object buildNewInstance() throws DescriptorException {
            Object arg = null;
            switch (argType) {
                case STORED_PROCEDURE_ARG:
                    arg = outer.new StoredProcedureArgument();
                    break;
                case STORED_PROCEDURE_INOUT_ARG:
                    arg = outer.new StoredProcedureInOutArgument();
                    break;
                case STORED_PROCEDURE_OUT_ARG:
                    arg = outer.new StoredProcedureOutArgument();
                    break;
                case STORED_PROCEDURE_OUTCURSOR_ARG:
                    arg = outer.new StoredProcedureOutCursorArgument();
                    break;
            }
            return arg;
        }
    }

    protected ClassDescriptor buildStoredProcedureArgumentDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureArgument.class);
        // need policy 'cause TreeBuilder cannot use default constructor
        descriptor.setInstantiationPolicy(new StoredProcedureArgumentInstantiationPolicy(this,
            StoredProcedureArgumentType.STORED_PROCEDURE_ARG));
        descriptor.descriptorIsAggregate();

        descriptor.setDefaultRootElement("argument");
        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(StoredProcedureArgument.class,
            getPrimaryNamespaceXPath() + "procedure-argument");
        descriptor.getInheritancePolicy().addClassIndicator(StoredProcedureInOutArgument.class,
            getPrimaryNamespaceXPath() + "procedure-inoutput-argument");
        descriptor.getInheritancePolicy().addClassIndicator(StoredProcedureOutArgument.class,
            getPrimaryNamespaceXPath() + "procedure-output-argument");
        descriptor.getInheritancePolicy().addClassIndicator(StoredProcedureOutCursorArgument.class,
            getPrimaryNamespaceXPath() + "procedure-output-cursor-argument");

        XMLDirectMapping argumentNameMapping = new XMLDirectMapping();
        argumentNameMapping.setAttributeName("argumentName");
        argumentNameMapping.setXPath(getPrimaryNamespaceXPath() + "procedure-argument-name/text()");
        descriptor.addMapping(argumentNameMapping);

        XMLDirectMapping argumentFieldNameMapping = new XMLDirectMapping();
        argumentFieldNameMapping.setAttributeName("argumentFieldName");
        argumentFieldNameMapping.setXPath(getPrimaryNamespaceXPath() + "argument-name/text()");
        argumentFieldNameMapping.setNullValue("");
        descriptor.addMapping(argumentFieldNameMapping);

        XMLDirectMapping argumentTypeMapping = new XMLDirectMapping();
        argumentTypeMapping.setAttributeName("argumentType");
        argumentTypeMapping.setXPath(getPrimaryNamespaceXPath() + "procedure-argument-type/text()");
        descriptor.addMapping(argumentTypeMapping);

        XMLDirectMapping argumentSQLTypeMapping = new XMLDirectMapping();
        argumentSQLTypeMapping.setAttributeName("argumentSQLType");
        argumentSQLTypeMapping.setXPath(getPrimaryNamespaceXPath() + "procedure-argument-sqltype/text()");
        argumentSQLTypeMapping.setNullValue(NULL_SQL_TYPE);
        descriptor.addMapping(argumentSQLTypeMapping);

        XMLDirectMapping argumentSqlTypeNameMapping = new XMLDirectMapping();
        argumentSqlTypeNameMapping.setAttributeName("argumentSqlTypeName");
        argumentSqlTypeNameMapping.setXPath(getPrimaryNamespaceXPath() + "procedure-argument-sqltype-name/text()");
        descriptor.addMapping(argumentSqlTypeNameMapping);

        XMLDirectMapping argumentValueMapping = new XMLDirectMapping();
        argumentValueMapping.setAttributeName("argumentValue");
        argumentValueMapping.setField(buildTypedField(getPrimaryNamespaceXPath() + "argument-value/text()"));
        descriptor.addMapping(argumentValueMapping);

        XMLCompositeObjectMapping nestedTypeMapping = new XMLCompositeObjectMapping();
        nestedTypeMapping.setAttributeName("nestedType");
        nestedTypeMapping.setReferenceClass(StoredProcedureArgument.class);
        nestedTypeMapping.setXPath(getPrimaryNamespaceXPath() + "nested-type-field");
        descriptor.addMapping(nestedTypeMapping);

        return descriptor;
    }

    protected ClassDescriptor buildStoredProcedureInOutArgumentsDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureInOutArgument.class);
        descriptor.setInstantiationPolicy(new StoredProcedureArgumentInstantiationPolicy(this,
            StoredProcedureArgumentType.STORED_PROCEDURE_INOUT_ARG));
        descriptor.getInheritancePolicy().setParentClass(StoredProcedureArgument.class);

        //used in case the in databasefield is named different than the out databasefield
        XMLDirectMapping outputArgumentNameMapping = new XMLDirectMapping();
        outputArgumentNameMapping.setAttributeName("outputArgumentName");
        outputArgumentNameMapping.setXPath(getPrimaryNamespaceXPath() + "output-argument-name/text()");
        descriptor.addMapping(outputArgumentNameMapping);

        return descriptor;
    }

    protected ClassDescriptor buildStoredProcedureOutArgumentsDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureOutArgument.class);
        descriptor.setInstantiationPolicy(new StoredProcedureArgumentInstantiationPolicy(this,
            StoredProcedureArgumentType.STORED_PROCEDURE_OUT_ARG));
        descriptor.getInheritancePolicy().setParentClass(StoredProcedureArgument.class);

        return descriptor;
    }

    protected ClassDescriptor buildStoredProcedureOutCursorArgumentsDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureOutCursorArgument.class);
        descriptor.setInstantiationPolicy(new StoredProcedureArgumentInstantiationPolicy(this,
            StoredProcedureArgumentType.STORED_PROCEDURE_OUTCURSOR_ARG));
        descriptor.getInheritancePolicy().setParentClass(StoredProcedureArgument.class);

        return descriptor;
    }

    class StoredProcedureArgumentsAccessor extends AttributeAccessor {
        StoredProcedureArgumentsAccessor() {
            super();
        }
        @Override
        public Object getAttributeValueFromObject(Object anObject) throws DescriptorException {
            StoredProcedureCall spc = (StoredProcedureCall)anObject;
            List parameterTypes = spc.getParameterTypes();
            List parameters = spc.getParameters();
            List procedureArgumentNames = spc.getProcedureArgumentNames();
            List storedProcedureArguments = new Vector();
            for (int i = spc.getFirstParameterIndexForCallString(); i < parameterTypes.size(); i++) {
                StoredProcedureArgument spa = null;
                Integer direction = (Integer)parameterTypes.get(i);
                Object argument = parameters.get(i);
                String argumentName = (String)procedureArgumentNames.get(i);
                if (direction.equals(IN)) {
                    spa = new StoredProcedureArgument();
                }
                else if (direction.equals(OUT)) {
                    spa = new StoredProcedureOutArgument();
                }
                else if (direction.equals(INOUT)) {
                    spa = new StoredProcedureInOutArgument();
                    // outputArgumentName ??
                }
                else {
                    // assume OUT_CURSOR
                    spa = new StoredProcedureOutCursorArgument();
                }
                spa.argumentName = argumentName;
                if (argument instanceof DatabaseField) {
                    DatabaseField argField = (DatabaseField)argument;
                    spa.setDatabaseField(argField);
                }
                else {
                    if (argument instanceof Object[]) {
                       Object first = ((Object[])argument)[0];
                       DatabaseField secondField = (DatabaseField)((Object[])argument)[1];;
                       if (first instanceof DatabaseField) {
                           DatabaseField firstField = (DatabaseField)first;
                           spa.setDatabaseField(firstField);
                       }
                       else {
                           spa.argumentValue = first;
                           spa.setDatabaseField(secondField);
                       }
                       ((StoredProcedureInOutArgument)spa).outputArgumentName =
                           secondField.getName();
                    }
                    else {
                        spa.argumentValue = argument;
                    }
                }
                storedProcedureArguments.add(spa);
            }
            return storedProcedureArguments;
        }
        @Override
        public void setAttributeValueInObject(Object domainObject, Object attributeValue) throws DescriptorException {
            StoredProcedureCall spc = (StoredProcedureCall)domainObject;
            // vector of parameters/arguments to be added the call
            Vector procedureArguments = (Vector)attributeValue;
            for (int i = 0; i < procedureArguments.size(); i++) {
                StoredProcedureArgument spa = (StoredProcedureArgument)procedureArguments.get(i);
                Integer direction = spa.getDirection();
                DatabaseField dbField = spa.getDatabaseField();
                spc.getProcedureArgumentNames().add(spa.argumentName);
                if (direction.equals(IN)) {
                    if (spa.argumentValue != null) {
                        spc.appendIn(spa.argumentValue);
                    }
                    else {
                        spc.appendIn(dbField);
                    }
                }
                else if (direction.equals(OUT)) {
                    spc.appendOut(dbField);
                }
                else if (direction.equals(OUT_CURSOR)) {
                    spc.appendOutCursor(dbField);
                }
                else  if (direction.equals(INOUT)) {
                    StoredProcedureInOutArgument spaInOut = (StoredProcedureInOutArgument)spa;
                    DatabaseField outField = new DatabaseField(spaInOut.outputArgumentName);
                    outField.type = dbField.type;
                    if (spaInOut.argumentValue != null) {
                        spc.appendInOut(spaInOut.argumentValue, outField);
                    }
                    else {
                        spc.appendInOut(dbField, outField);
                    }
                }
            }
        }
    }

    protected ClassDescriptor buildStoredProcedureCallDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureCall.class);
        descriptor.getInheritancePolicy().setParentClass(Call.class);
        descriptor.descriptorIsAggregate();

        XMLDirectMapping procedureNameMapping = new XMLDirectMapping();
        procedureNameMapping.setAttributeName("procedureName");
        procedureNameMapping.setGetMethodName("getProcedureName");
        procedureNameMapping.setSetMethodName("setProcedureName");
        procedureNameMapping.setXPath(getPrimaryNamespaceXPath() + "procedure-name/text()");
        descriptor.addMapping(procedureNameMapping);

        XMLDirectMapping cursorOutputProcedureMapping = new XMLDirectMapping();
        cursorOutputProcedureMapping.setAttributeName("isCursorOutputProcedure");
        cursorOutputProcedureMapping.setXPath(getPrimaryNamespaceXPath() + "cursor-output-procedure/text()");
        cursorOutputProcedureMapping.setNullValue(false);
        descriptor.addMapping(cursorOutputProcedureMapping);

        XMLCompositeCollectionMapping storedProcArgumentsMapping = new XMLCompositeCollectionMapping();
        storedProcArgumentsMapping.useCollectionClass(NonSynchronizedVector.class);
        storedProcArgumentsMapping.setAttributeName("procedureArguments");
        storedProcArgumentsMapping.setAttributeAccessor(new StoredProcedureArgumentsAccessor());
        storedProcArgumentsMapping.setReferenceClass(StoredProcedureArgument.class);
        storedProcArgumentsMapping.setXPath(getPrimaryNamespaceXPath() + "arguments/" + getPrimaryNamespaceXPath() + "argument");
        descriptor.addMapping(storedProcArgumentsMapping);

        XMLCompositeCollectionMapping optionalMapping = new XMLCompositeCollectionMapping();
        optionalMapping.setAttributeName("optionalArguments");
        optionalMapping.setXPath(getPrimaryNamespaceXPath() + "optional-arguments/" + getPrimaryNamespaceXPath() + "argument");
        optionalMapping.setReferenceClass(DatabaseField.class);
        descriptor.addMapping(optionalMapping);

        return descriptor;
    }

    class StoredFunctionResultAccessor extends AttributeAccessor {
        StoredFunctionResultAccessor() {
            super();
        }
        // for StoredFunctionCalls, the return value's information
        // is stored in the parameters list at index 0
        @Override
        public Object getAttributeValueFromObject(Object anObject) throws DescriptorException {
            StoredFunctionCall sfc = (StoredFunctionCall)anObject;
            Object argument = sfc.getParameters().get(0);
            String argumentName = sfc.getProcedureArgumentNames().get(0);
            StoredProcedureOutArgument outArgument = new StoredProcedureOutArgument((DatabaseField)argument);
            outArgument.argumentName = argumentName;
            return outArgument;
        }
        @Override
        public void setAttributeValueInObject(Object domainObject, Object attributeValue) throws DescriptorException {
            StoredFunctionCall sfc = (StoredFunctionCall)domainObject;
            StoredProcedureOutArgument spoa = (StoredProcedureOutArgument)attributeValue;
            // Set procedure argument name.
            sfc.getProcedureArgumentNames().set(0, spoa.argumentName);
            sfc.getParameters().set(0, spoa.getDatabaseField());
            // Set argument type.
            sfc.getParameterTypes().set(0, OUT);
        }
    }

    protected ClassDescriptor buildStoredFunctionCallDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredFunctionCall.class);
        descriptor.getInheritancePolicy().setParentClass(StoredProcedureCall.class);
        descriptor.descriptorIsAggregate();

        XMLCompositeObjectMapping storedFunctionResultMapping = new XMLCompositeObjectMapping();
        storedFunctionResultMapping.setAttributeName("storedFunctionResult");
        storedFunctionResultMapping.setReferenceClass(StoredProcedureOutArgument.class);
        storedFunctionResultMapping.setAttributeAccessor(new StoredFunctionResultAccessor());
        storedFunctionResultMapping.setXPath(getPrimaryNamespaceXPath() + "stored-function-result");
        descriptor.addMapping(storedFunctionResultMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildXMLDirectMappingDescriptor() {
        ClassDescriptor descriptor = super.buildXMLDirectMappingDescriptor();

        XMLDirectMapping isCDATAMapping = new XMLDirectMapping();
        isCDATAMapping.setAttributeName("isCDATA");
        isCDATAMapping.setGetMethodName("isCDATA");
        isCDATAMapping.setSetMethodName("setIsCDATA");
        isCDATAMapping.setXPath(getPrimaryNamespaceXPath() + "is-cdata/text()");
        isCDATAMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(isCDATAMapping);

        // Add Null Policy
        XMLCompositeObjectMapping aMapping = new XMLCompositeObjectMapping();
        aMapping.setReferenceClass(AbstractNullPolicy.class);
        aMapping.setAttributeName("nullPolicy");
        aMapping.setXPath(getPrimaryNamespaceXPath() + "null-policy");
        ((DatabaseMapping)aMapping).setAttributeAccessor(new NullPolicyAttributeAccessor());
        descriptor.addMapping(aMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildXMLCompositeDirectCollectionMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildXMLCompositeDirectCollectionMappingDescriptor();

        XMLDirectMapping isCDATAMapping = new XMLDirectMapping();
        isCDATAMapping.setAttributeName("isCDATA");
        isCDATAMapping.setGetMethodName("isCDATA");
        isCDATAMapping.setSetMethodName("setIsCDATA");
        isCDATAMapping.setXPath(getPrimaryNamespaceXPath() + "is-cdata/text()");
        isCDATAMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(isCDATAMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildXMLLoginDescriptor(){
        ClassDescriptor descriptor = super.buildXMLLoginDescriptor();

        XMLDirectMapping equalNamespaceResolversMapping = new XMLDirectMapping();
        equalNamespaceResolversMapping.setAttributeName("equalNamespaceResolvers");
        equalNamespaceResolversMapping.setGetMethodName("hasEqualNamespaceResolvers");
        equalNamespaceResolversMapping.setSetMethodName("setEqualNamespaceResolvers");
        equalNamespaceResolversMapping.setXPath(getPrimaryNamespaceXPath() + "equal-namespace-resolvers/text()");
        equalNamespaceResolversMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(equalNamespaceResolversMapping);

        XMLCompositeObjectMapping documentPreservationPolicyMapping = new XMLCompositeObjectMapping();
        documentPreservationPolicyMapping.setReferenceClass(DocumentPreservationPolicy.class);
        documentPreservationPolicyMapping.setAttributeName("documentPreservationPolicy");
        documentPreservationPolicyMapping.setGetMethodName("getDocumentPreservationPolicy");
        documentPreservationPolicyMapping.setSetMethodName("setDocumentPreservationPolicy");
        documentPreservationPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "document-preservation-policy");
        descriptor.addMapping(documentPreservationPolicyMapping);

        return descriptor;
    }

    protected ClassDescriptor buildDocumentPreservationPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DocumentPreservationPolicy.class);
        descriptor.setDefaultRootElement("document-preservation-policy");

        XMLCompositeObjectMapping nodeOrderingPolicyMapping = new XMLCompositeObjectMapping();
        nodeOrderingPolicyMapping.setReferenceClass(NodeOrderingPolicy.class);
        nodeOrderingPolicyMapping.setAttributeName("nodeOrderingPolicy");
        nodeOrderingPolicyMapping.setGetMethodName("getNodeOrderingPolicy");
        nodeOrderingPolicyMapping.setSetMethodName("setNodeOrderingPolicy");
        nodeOrderingPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "node-ordering-policy");
        descriptor.addMapping(nodeOrderingPolicyMapping);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(DescriptorLevelDocumentPreservationPolicy.class, getPrimaryNamespaceXPath() + "descriptor-level-document-preservation-policy");
        descriptor.getInheritancePolicy().addClassIndicator(NoDocumentPreservationPolicy.class, getPrimaryNamespaceXPath() + "no-document-preservation-policy");
        descriptor.getInheritancePolicy().addClassIndicator(XMLBinderPolicy.class, getPrimaryNamespaceXPath() + "xml-binder-policy");

        return descriptor;
    }

    protected ClassDescriptor buildDescriptorLevelDocumentPreservationPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DescriptorLevelDocumentPreservationPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(DocumentPreservationPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildNoDocumentPreservationPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NoDocumentPreservationPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(DocumentPreservationPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildXMLBinderPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLBinderPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(DocumentPreservationPolicy.class);

        return descriptor;
    }
    
    protected ClassDescriptor buildNodeOrderingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(NodeOrderingPolicy.class);

        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(AppendNewElementsOrderingPolicy.class, getPrimaryNamespaceXPath() + "append-new-elements-ordering-policy");
        descriptor.getInheritancePolicy().addClassIndicator(IgnoreNewElementsOrderingPolicy.class, getPrimaryNamespaceXPath() + "ignore-new-elements-ordering-policy");
        descriptor.getInheritancePolicy().addClassIndicator(RelativePositionOrderingPolicy.class, getPrimaryNamespaceXPath() + "relative-position-ordering-policy");

        return descriptor;
    }

    protected ClassDescriptor buildAppendNewElementsOrderingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AppendNewElementsOrderingPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(NodeOrderingPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildIgnoreNewElementsOrderingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(IgnoreNewElementsOrderingPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(NodeOrderingPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildRelativePositionOrderingPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RelativePositionOrderingPolicy.class);
        descriptor.getInheritancePolicy().setParentClass(NodeOrderingPolicy.class);

        return descriptor;
    }

    protected ClassDescriptor buildAbstractNullPolicyDescriptor() {
         XMLDescriptor aDescriptor = new XMLDescriptor();
         aDescriptor.setJavaClass(AbstractNullPolicy.class);
         aDescriptor.setDefaultRootElement("abstract-null-policy");

         XMLDirectMapping xnrnMapping = new XMLDirectMapping();
         xnrnMapping.setAttributeName("isNullRepresentedByXsiNil");
         xnrnMapping.setXPath(getPrimaryNamespaceXPath() + "xsi-nil-represents-null/text()");
         xnrnMapping.setNullValue(Boolean.FALSE);
         aDescriptor.addMapping(xnrnMapping);

         XMLDirectMapping enrnMapping = new XMLDirectMapping();
         enrnMapping.setAttributeName("isNullRepresentedByEmptyNode");
         enrnMapping.setXPath(getPrimaryNamespaceXPath() + "empty-node-represents-null/text()");
         enrnMapping.setNullValue(Boolean.FALSE);
         aDescriptor.addMapping(enrnMapping);

         XMLDirectMapping nrfxMapping = new XMLDirectMapping();
         nrfxMapping.setAttributeName("marshalNullRepresentation");
         nrfxMapping.setXPath(getPrimaryNamespaceXPath() + "null-representation-for-xml/text()");
         // Restricted to XSI_NIL,ABSENT_NODE,EMPTY_NODE
         EnumTypeConverter aConverter = new EnumTypeConverter(nrfxMapping, XMLNullRepresentationType.class, false);
         nrfxMapping.setConverter(aConverter);
         aDescriptor.addMapping(nrfxMapping);

         // Subclasses
         aDescriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
         aDescriptor.getInheritancePolicy().addClassIndicator(IsSetNullPolicy.class, getPrimaryNamespaceXPath() + "is-set-null-policy");
         aDescriptor.getInheritancePolicy().addClassIndicator(NullPolicy.class, getPrimaryNamespaceXPath() + "null-policy");

         return aDescriptor;
     }

     @Override
     protected ClassDescriptor buildNamespaceResolverDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildNamespaceResolverDescriptor();

        XMLDirectMapping defaultNamespaceMapping = new XMLDirectMapping();
        defaultNamespaceMapping.setXPath(getPrimaryNamespaceXPath() + "default-namespace-uri");
        defaultNamespaceMapping.setAttributeName("defaultNamespaceURI");
        defaultNamespaceMapping.setGetMethodName("getDefaultNamespaceURI");
        defaultNamespaceMapping.setSetMethodName("setDefaultNamespaceURI");
        descriptor.addMapping(defaultNamespaceMapping);

        return descriptor;
     }

     protected ClassDescriptor buildNullPolicyDescriptor() {
         XMLDescriptor aDescriptor = new XMLDescriptor();
         aDescriptor.setJavaClass(NullPolicy.class);
         aDescriptor.getInheritancePolicy().setParentClass(AbstractNullPolicy.class);

         // This boolean can only be set on the NullPolicy implementation even though the field is on the abstract class
         XMLDirectMapping xnranMapping = new XMLDirectMapping();
         xnranMapping.setAttributeName("isSetPerformedForAbsentNode");
         xnranMapping.setXPath(getPrimaryNamespaceXPath() + "is-set-performed-for-absent-node/text()");
         xnranMapping.setNullValue(Boolean.TRUE);
         aDescriptor.addMapping(xnranMapping);

         return aDescriptor;
     }

     protected ClassDescriptor buildIsSetNullPolicyDescriptor() {
         // The IsSetPerformedForAbsentNode flag is always false on this IsSet mapping
    	 XMLDescriptor aDescriptor = new XMLDescriptor();
         aDescriptor.setJavaClass(IsSetNullPolicy.class);
         aDescriptor.getInheritancePolicy().setParentClass(AbstractNullPolicy.class);

         XMLDirectMapping isSetMethodNameMapping = new XMLDirectMapping();
         isSetMethodNameMapping.setAttributeName("isSetMethodName");
         isSetMethodNameMapping.setXPath(getPrimaryNamespaceXPath() + "is-set-method-name/text()");
         aDescriptor.addMapping(isSetMethodNameMapping);

         // 20070922: Bug#6039730 - add IsSet capability for 1+ parameters for SDO
         XMLCompositeDirectCollectionMapping isSetParameterTypesMapping = new XMLCompositeDirectCollectionMapping();
         isSetParameterTypesMapping.setAttributeName("isSetParameterTypes");
         isSetParameterTypesMapping.setXPath(getPrimaryNamespaceXPath() + "is-set-parameter-type");
         ((DatabaseMapping)isSetParameterTypesMapping).setAttributeAccessor(new IsSetNullPolicyIsSetParameterTypesAttributeAccessor());
         aDescriptor.addMapping(isSetParameterTypesMapping);

         XMLCompositeDirectCollectionMapping isSetParametersMapping = new XMLCompositeDirectCollectionMapping();
         isSetParametersMapping.setAttributeName("isSetParameters");
         isSetParametersMapping.setXPath(getPrimaryNamespaceXPath() + "is-set-parameter");
         ((DatabaseMapping)isSetParametersMapping).setAttributeAccessor(new IsSetNullPolicyIsSetParametersAttributeAccessor());
         aDescriptor.addMapping(isSetParametersMapping);

         return aDescriptor;
     }

     /**
      * INTERNAL:
      * Wrap the isset parameter object array as a Collection.
      * Prerequisite: parameterTypes must be set.
      */
     public class IsSetNullPolicyIsSetParametersAttributeAccessor extends AttributeAccessor {
         public IsSetNullPolicyIsSetParametersAttributeAccessor() {
        	 super();
         }

         @Override
         public Object getAttributeValueFromObject(Object object) throws DescriptorException {
        	 IsSetNullPolicy aPolicy = (IsSetNullPolicy)object;
       		 NonSynchronizedVector aCollection = new NonSynchronizedVector();
       		 for(int i = 0, size = aPolicy.getIsSetParameters().length; i<size;i++) {
       			 aCollection.add(aPolicy.getIsSetParameters()[i]);
       		 }
       		 return aCollection;
         }

         @Override
         public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        	 // Convert the collection of Strings to an array of Object values (round-trip)
        	 if(value instanceof Collection) {
    			 int i = 0;
    			 Object[] parameters = new Object[((Collection)value).size()];
    			 for(Iterator anIterator = ((Collection)value).iterator(); anIterator.hasNext();) {
   					 // Lookup the object type via the predefined parameterTypes array and convert based on that type
   					 parameters[i] = XMLConversionManager.getDefaultXMLManager().convertObject(//
   							 anIterator.next(), ((IsSetNullPolicy)object).getIsSetParameterTypes()[i++]);
    			 }
    			 ((IsSetNullPolicy)object).setIsSetParameters(parameters);
        	 } else {
        		 // Cast to object array
        		 ((IsSetNullPolicy)object).setIsSetParameters((Object[])value);
        	 }
         }
     }

     /**
      * INTERNAL:
      * Wrap the isset parameterType class array as a Collection
      */
     public class IsSetNullPolicyIsSetParameterTypesAttributeAccessor extends AttributeAccessor {
         public IsSetNullPolicyIsSetParameterTypesAttributeAccessor() {
             super();
         }

         @Override
         public Object getAttributeValueFromObject(Object object) throws DescriptorException {
        	 IsSetNullPolicy aPolicy = (IsSetNullPolicy)object;
       		 NonSynchronizedVector aCollection = new NonSynchronizedVector();
       		 for(int i = 0, size = aPolicy.getIsSetParameterTypes().length; i<size;i++) {
       			 aCollection.add(aPolicy.getIsSetParameterTypes()[i]);
       		 }
       		 return aCollection;
         }

         @Override
         public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
        	 try {
        		 // Get the Class of each entry in the collection
        		 if(value instanceof Collection) {
        			 Class[] parameterTypes = new Class[((Collection)value).size()];
        			 int i = 0;
        			 for(Iterator anIterator = ((Collection)value).iterator(); anIterator.hasNext();) {
        				 parameterTypes[i++] = Class.forName((String)anIterator.next());
        			 }
        			 ((IsSetNullPolicy)object).setIsSetParameterTypes(parameterTypes);
        		 } else {
        			 // cast to class array
        			 ((IsSetNullPolicy)object).setIsSetParameterTypes((Class[])value);
        		 }
        	 } catch (ClassNotFoundException e) {
        		 throw new RuntimeException(e);
        	 }
         }
     }

     @Override
     protected ClassDescriptor buildXMLCompositeObjectMappingDescriptor() {
         ClassDescriptor descriptor = super.buildXMLCompositeObjectMappingDescriptor();

         // Add Null Policy
         XMLCompositeObjectMapping nullPolicyClassMapping = new XMLCompositeObjectMapping();
         nullPolicyClassMapping.setReferenceClass(AbstractNullPolicy.class);
         nullPolicyClassMapping.setAttributeName("nullPolicy");
         nullPolicyClassMapping.setXPath(getPrimaryNamespaceXPath() + "null-policy");

         // Handle translation of (default) Null Policy states.
         ((DatabaseMapping)nullPolicyClassMapping).setAttributeAccessor(new NullPolicyAttributeAccessor());
         descriptor.addMapping(nullPolicyClassMapping);

         return descriptor;
     }

     /**
      * INTERNAL:
      * If the policy is the default NullPolicy with defaults set - then represent this default policy by null.
      */
     public class NullPolicyAttributeAccessor extends AttributeAccessor {

         public NullPolicyAttributeAccessor() {
             super();
         }

         @Override
         public Object getAttributeValueFromObject(Object object) throws DescriptorException {
          	// If the policy is default (NullPolicy(ispfan=true, inrben=false, inrbxnn=false, XMLNullRep=ABSENT_NODE) return null
          	AbstractNullPolicy value = ((XMLNillableMapping)object).getNullPolicy();
          	if(value instanceof NullPolicy) {
              	NullPolicy aPolicy = (NullPolicy)value;
              	if(aPolicy.getIsSetPerformedForAbsentNode() && !aPolicy.isNullRepresentedByEmptyNode() //
              			&& !aPolicy.isNullRepresentedByXsiNil() //
              			&& aPolicy.getMarshalNullRepresentation().equals(XMLNullRepresentationType.ABSENT_NODE)) {
              		// The default policy is represented by null
              		return null;
              	}
          	}
          	return ((XMLNillableMapping)object).getNullPolicy();
         }

         @Override
         public void setAttributeValueInObject(Object object, Object value) throws DescriptorException {
         	// If value is a default policy represented by null - return (NullPolicy(ispfan=true, inrben=false, inrbxn=false, XMLNullRep=ABSENT_NODE)
          	if(null == value) {
          		// Create and set a default policy
          		((XMLNillableMapping)object).setNullPolicy(new NullPolicy());
          	} else {
          		// Set the value as policy
              	((XMLNillableMapping)object).setNullPolicy((AbstractNullPolicy)value);
          	}
         }
     }

     public static final String TYPE_NAME = "type-name";

     protected ClassDescriptor buildOracleArrayTypeWrapperDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(OracleArrayTypeWrapper.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);

         XMLCompositeObjectMapping wrappedDatabaseTypeMapping = new XMLCompositeObjectMapping();
         wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
         wrappedDatabaseTypeMapping.setXPath(".");
         wrappedDatabaseTypeMapping.setReferenceClass(OracleArrayType.class);
         descriptor.addMapping(wrappedDatabaseTypeMapping);

         return descriptor;
     }
     protected ClassDescriptor buildOracleObjectTypeWrapperDescriptor() {
         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(OracleObjectTypeWrapper.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);

         XMLCompositeObjectMapping wrappedDatabaseTypeMapping = new XMLCompositeObjectMapping();
         wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
         wrappedDatabaseTypeMapping.setXPath(".");
         wrappedDatabaseTypeMapping.setReferenceClass(OracleObjectType.class);
         descriptor.addMapping(wrappedDatabaseTypeMapping);

         return descriptor;
     }
     protected ClassDescriptor buildPLSQLrecordWrapperDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(PLSQLRecordWrapper.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);

         XMLCompositeObjectMapping wrappedDatabaseTypeMapping = new XMLCompositeObjectMapping();
         wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
         wrappedDatabaseTypeMapping.setXPath(".");
         wrappedDatabaseTypeMapping.setReferenceClass(PLSQLrecord.class);
         descriptor.addMapping(wrappedDatabaseTypeMapping);

         return descriptor;
     }

     protected ClassDescriptor buildPLSQLCollectionWrapperDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(PLSQLCollectionWrapper.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);

         XMLCompositeObjectMapping wrappedDatabaseTypeMapping = new XMLCompositeObjectMapping();
         wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
         wrappedDatabaseTypeMapping.setXPath(".");
         wrappedDatabaseTypeMapping.setReferenceClass(PLSQLCollection.class);
         descriptor.addMapping(wrappedDatabaseTypeMapping);

         return descriptor;
     }
     protected ClassDescriptor buildPLSQLCursorWrapperDescriptor() {
         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(PLSQLCursorWrapper.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);

         XMLCompositeObjectMapping wrappedDatabaseTypeMapping = new XMLCompositeObjectMapping();
         wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
         wrappedDatabaseTypeMapping.setXPath(".");
         wrappedDatabaseTypeMapping.setReferenceClass(PLSQLCursor.class);
         descriptor.addMapping(wrappedDatabaseTypeMapping);

         return descriptor;
     }

     protected ClassDescriptor buildSimplePLSQLTypeWrapperDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(SimplePLSQLTypeWrapper.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);

         XMLDirectMapping wrappedDatabaseTypeMapping = new XMLDirectMapping();
         wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
         wrappedDatabaseTypeMapping.setXPath("@" + TYPE_NAME);
         EnumTypeConverter oraclePLSQLTypesEnumTypeConverter = new EnumTypeConverter(
             wrappedDatabaseTypeMapping, OraclePLSQLTypes.class, false);
         wrappedDatabaseTypeMapping.setConverter(oraclePLSQLTypesEnumTypeConverter);
         descriptor.addMapping(wrappedDatabaseTypeMapping);

         return descriptor;
     }

     protected ClassDescriptor buildJDBCTypeWrapperDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(JDBCTypeWrapper.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseTypeWrapper.class);

         XMLDirectMapping wrappedDatabaseTypeMapping = new XMLDirectMapping();
         wrappedDatabaseTypeMapping.setAttributeName("wrappedDatabaseType");
         wrappedDatabaseTypeMapping.setXPath("@" + TYPE_NAME);
         EnumTypeConverter jdbcTypesEnumTypeConverter = new EnumTypeConverter(
             wrappedDatabaseTypeMapping, JDBCTypes.class, false);
         wrappedDatabaseTypeMapping.setConverter(jdbcTypesEnumTypeConverter);
         descriptor.addMapping(wrappedDatabaseTypeMapping);

         return descriptor;
     }

     protected ClassDescriptor buildOracleArrayTypeDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(OracleArrayType.class);

         XMLDirectMapping typeNameMapping = new XMLDirectMapping();
         typeNameMapping.setAttributeName("typeName");
         typeNameMapping.setXPath(getPrimaryNamespaceXPath() + "type-name/text()");
         descriptor.addMapping(typeNameMapping);

         XMLDirectMapping compatibleTypeMapping = new XMLDirectMapping();
         compatibleTypeMapping.setAttributeName("compatibleType");
         compatibleTypeMapping.setXPath(getPrimaryNamespaceXPath() + "compatible-type/text()");
         descriptor.addMapping(compatibleTypeMapping);

         XMLDirectMapping javaTypeMapping = new XMLDirectMapping();
         javaTypeMapping.setAttributeName("javaType");
         javaTypeMapping.setXPath(getPrimaryNamespaceXPath() + "java-type/text()");
         descriptor.addMapping(javaTypeMapping);

         XMLCompositeObjectMapping databaseTypeMapping = new XMLCompositeObjectMapping();
         databaseTypeMapping.setAttributeName("databaseTypeWrapper");
         databaseTypeMapping.setAttributeAccessor(new AttributeAccessor() {
             public Object getAttributeValueFromObject(Object object) {
            	 OracleArrayType array = (OracleArrayType)object;
                 DatabaseType type = array.getNestedType();
                 return wrapType(type);
             }
             public void setAttributeValueInObject(Object object, Object value) {
            	 OracleArrayType array = (OracleArrayType)object;
                 DatabaseTypeWrapper type = (DatabaseTypeWrapper)value;
                 array.setNestedType(type.getWrappedType());
             }
         });
         databaseTypeMapping.setReferenceClass(DatabaseTypeWrapper.class);
         databaseTypeMapping.setXPath("nested-type");
         descriptor.addMapping(databaseTypeMapping);

         return descriptor;
     }
     /**
      * Builds a descriptor for the OracleObjectType class.
      */
     protected ClassDescriptor buildOracleObjectTypeDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(OracleObjectType.class);

         XMLDirectMapping typeNameMapping = new XMLDirectMapping();
         typeNameMapping.setAttributeName("typeName");
         typeNameMapping.setXPath(getPrimaryNamespaceXPath() + "type-name/text()");
         descriptor.addMapping(typeNameMapping);

         XMLDirectMapping compatibleTypeMapping = new XMLDirectMapping();
         compatibleTypeMapping.setAttributeName("compatibleType");
         compatibleTypeMapping.setXPath(getPrimaryNamespaceXPath() + "compatible-type/text()");
         descriptor.addMapping(compatibleTypeMapping);

         XMLDirectMapping javaTypeMapping = new XMLDirectMapping();
         javaTypeMapping.setAttributeName("javaType");
         javaTypeMapping.setXPath(getPrimaryNamespaceXPath() + "java-type/text()");
         descriptor.addMapping(javaTypeMapping);

         XMLCompositeCollectionMapping fieldsMapping = new XMLCompositeCollectionMapping();
         fieldsMapping.setAttributeName("fields");
         fieldsMapping.setReferenceClass(ObjectTypeFieldAssociation.class);
         // handle translation of 'field' LinkedHashMap
         fieldsMapping.setAttributeAccessor(new AttributeAccessor() {
             public Object getAttributeValueFromObject(Object object) {
                 Map fields = ((OracleObjectType) object).getFields();
                 List associations = new ArrayList(fields.size());
                 Iterator iterator = fields.entrySet().iterator();
                 while (iterator.hasNext()) {
                     Map.Entry entry = (Map.Entry)iterator.next();
                     associations.add(new ObjectTypeFieldAssociation(entry.getKey().toString(),  wrapType((DatabaseType) entry.getValue())));
                 }
                 return associations;
             }
             public void setAttributeValueInObject(Object object, Object value) {
            	 OracleObjectType objectType = (OracleObjectType) object;
                 List associations = (List) value;
                 Map fieldMap = new LinkedHashMap<String, DatabaseType>(associations.size() + 1);
                 Iterator iterator = associations.iterator();
                 while (iterator.hasNext()) {
                	 ObjectTypeFieldAssociation association = (ObjectTypeFieldAssociation)iterator.next();
                     fieldMap.put(association.getKey(), unwrapType((DatabaseTypeWrapper)association.getValue()));
                 }
                 objectType.setFields(fieldMap);
             }
         });
         fieldsMapping.setXPath(getPrimaryNamespaceXPath() + "fields/" + getPrimaryNamespaceXPath() + "field");
         descriptor.addMapping(fieldsMapping);
         
         return descriptor;
     }
     
     /**
      * Inner class used to map Map containers where the key is String and
      * the value is a DatabaseType.  The value must be wrapped/unwrapped
      * using the wrap/unwrap type methods on the outer class.
      */
     public class ObjectTypeFieldAssociation implements Map.Entry {
    	 String key;
    	 DatabaseTypeWrapper value;
    	 
    	 public ObjectTypeFieldAssociation() {
    		 super();
    	 }
    	 
    	 public ObjectTypeFieldAssociation(String key, DatabaseTypeWrapper value) {
    		 this.key = key;
    		 this.value = value;
    	 }

		public Object getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		public Object setValue(Object arg0) {
	        Object oldValue = this.value;
	        this.value = (DatabaseTypeWrapper) arg0;
	        return oldValue;
		}
     }
     /**
      * Builds a descriptor for the ObjectTypeFieldAssociation class.
      */
     protected ClassDescriptor buildObjectTypeFieldAssociationDescriptor() {
         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(ObjectTypeFieldAssociation.class);
         descriptor.setInstantiationPolicy(new ObjectTypeFieldAssociationInstantiationPolicy(this));
    	 
         XMLDirectMapping keyMapping = new XMLDirectMapping();
         keyMapping.setAttributeName("key");
         keyMapping.setXPath(getPrimaryNamespaceXPath() + "key/text()");
         descriptor.addMapping(keyMapping);
         
         XMLCompositeObjectMapping valueMapping = new XMLCompositeObjectMapping();
         valueMapping.setAttributeName("value");
         valueMapping.setReferenceClass(DatabaseTypeWrapper.class);
         valueMapping.setXPath(getPrimaryNamespaceXPath() + "value");
         descriptor.addMapping(valueMapping);
         
         return descriptor;
     }
     /**
      * Instantiation policy for ObjectTypeFieldAssociation class.  This policy
      * enables the default constructor of the inner class to be accessed.
      */
     class ObjectTypeFieldAssociationInstantiationPolicy extends InstantiationPolicy {
         ObjectPersistenceRuntimeXMLProject_11_1_1 outer;
         ObjectTypeFieldAssociationInstantiationPolicy(
             ObjectPersistenceRuntimeXMLProject_11_1_1 outer) {
             this.outer = outer;
         }
         @Override
         public Object buildNewInstance() throws DescriptorException {
             return outer.new ObjectTypeFieldAssociation();
         }
     }
     protected ClassDescriptor buildPLSQLrecordDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(PLSQLrecord.class);

         XMLDirectMapping typeNameMapping = new XMLDirectMapping();
         typeNameMapping.setAttributeName("typeName");
         typeNameMapping.setXPath(getPrimaryNamespaceXPath() + "type-name/text()");
         descriptor.addMapping(typeNameMapping);

         XMLDirectMapping compatibleTypeMapping = new XMLDirectMapping();
         compatibleTypeMapping.setAttributeName("compatibleType");
         compatibleTypeMapping.setXPath(getPrimaryNamespaceXPath() + "compatible-type/text()");
         descriptor.addMapping(compatibleTypeMapping);

         XMLDirectMapping javaTypeMapping = new XMLDirectMapping();
         javaTypeMapping.setAttributeName("javaType");
         javaTypeMapping.setXPath(getPrimaryNamespaceXPath() + "java-type/text()");
         descriptor.addMapping(javaTypeMapping);

         XMLCompositeCollectionMapping fieldsMapping = new XMLCompositeCollectionMapping();
         fieldsMapping.setAttributeName("fields");
         fieldsMapping.setReferenceClass(PLSQLargument.class);
         fieldsMapping.setXPath(getPrimaryNamespaceXPath() + "fields/" + getPrimaryNamespaceXPath() + "field");
         descriptor.addMapping(fieldsMapping);

         return descriptor;
     }

     protected ClassDescriptor buildPLSQLCursorDescriptor() {
         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(PLSQLCursor.class);

         XMLDirectMapping typeNameMapping = new XMLDirectMapping();
         typeNameMapping.setAttributeName("typeName");
         typeNameMapping.setXPath(getPrimaryNamespaceXPath() + "type-name/text()");
         descriptor.addMapping(typeNameMapping);

         return descriptor;
     }

     protected ClassDescriptor buildPLSQLCollectionDescriptor() {

        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PLSQLCollection.class);

        XMLDirectMapping typeNameMapping = new XMLDirectMapping();
        typeNameMapping.setAttributeName("typeName");
        typeNameMapping.setXPath(getPrimaryNamespaceXPath() + "type-name/text()");
        descriptor.addMapping(typeNameMapping);

        XMLDirectMapping compatibleTypeMapping = new XMLDirectMapping();
        compatibleTypeMapping.setAttributeName("compatibleType");
        compatibleTypeMapping.setXPath(getPrimaryNamespaceXPath() + "compatible-type/text()");
        descriptor.addMapping(compatibleTypeMapping);

        XMLDirectMapping javaTypeMapping = new XMLDirectMapping();
        javaTypeMapping.setAttributeName("javaType");
        javaTypeMapping.setXPath(getPrimaryNamespaceXPath() + "java-type/text()");
        descriptor.addMapping(javaTypeMapping);

        XMLCompositeObjectMapping databaseTypeMapping = new XMLCompositeObjectMapping();
        databaseTypeMapping.setAttributeName("databaseTypeWrapper");
        databaseTypeMapping.setAttributeAccessor(new AttributeAccessor() {
            public Object getAttributeValueFromObject(Object object) {
                PLSQLCollection collection = (PLSQLCollection)object;
                DatabaseType type = collection.getNestedType();
                return wrapType(type);
            }
            public void setAttributeValueInObject(Object object, Object value) {
                PLSQLCollection collection = (PLSQLCollection)object;
                DatabaseTypeWrapper type = (DatabaseTypeWrapper)value;
                collection.setNestedType(type.getWrappedType());
            }
        });
        databaseTypeMapping.setReferenceClass(DatabaseTypeWrapper.class);
        databaseTypeMapping.setXPath("nested-type");
        descriptor.addMapping(databaseTypeMapping);

        XMLDirectMapping isNestedTableMapping = new XMLDirectMapping();
        isNestedTableMapping.setAttributeName("isNestedTable");
        isNestedTableMapping.setXPath("@is-nested-table");
        isNestedTableMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(isNestedTableMapping);

        return descriptor;
    }

     protected ClassDescriptor buildDatabaseTypeWrapperDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(DatabaseTypeWrapper.class);
         descriptor.getInheritancePolicy().setClassIndicatorField(
             new XMLField("@xsi:type"));
         descriptor.getInheritancePolicy().addClassIndicator(
             JDBCTypeWrapper.class, getPrimaryNamespaceXPath() + "jdbc-type");
         descriptor.getInheritancePolicy().addClassIndicator(
             SimplePLSQLTypeWrapper.class, getPrimaryNamespaceXPath() + "plsql-type");
         descriptor.getInheritancePolicy().addClassIndicator(
             PLSQLRecordWrapper.class, getPrimaryNamespaceXPath() + "plsql-record");
         descriptor.getInheritancePolicy().addClassIndicator(
             PLSQLCollectionWrapper.class, getPrimaryNamespaceXPath() + "plsql-collection");
         descriptor.getInheritancePolicy().addClassIndicator(
             OracleArrayTypeWrapper.class, getPrimaryNamespaceXPath() + "varray");
         descriptor.getInheritancePolicy().addClassIndicator(
             OracleObjectTypeWrapper.class, getPrimaryNamespaceXPath() + "object-type");
         descriptor.getInheritancePolicy().addClassIndicator(
             PLSQLCursorWrapper.class, getPrimaryNamespaceXPath() + "plsql-cursor");
         return descriptor;
     }

     protected ClassDescriptor buildPLSQLargumentDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(PLSQLargument.class);

         XMLDirectMapping nameMapping = new XMLDirectMapping();
         nameMapping.setAttributeName("name");
         nameMapping.setXPath(getPrimaryNamespaceXPath() + "name/text()");
         descriptor.addMapping(nameMapping);

         XMLDirectMapping indexMapping = new XMLDirectMapping();
         indexMapping.setAttributeName("originalIndex");
         indexMapping.setXPath(getPrimaryNamespaceXPath() + "index/text()");
         indexMapping.setNullValue(-1);
         descriptor.addMapping(indexMapping);

         XMLDirectMapping directionMapping = new XMLDirectMapping();
         directionMapping.setAttributeName("direction");
         directionMapping.setXPath(getPrimaryNamespaceXPath() + "direction/text()");
         ObjectTypeConverter directionConverter = new ObjectTypeConverter();
         directionConverter.addConversionValue("IN", IN);
         directionConverter.addConversionValue("INOUT", INOUT);
         directionConverter.addConversionValue("OUT", OUT);
         directionMapping.setConverter(directionConverter);
         directionMapping.setNullValue(IN);
         descriptor.addMapping(directionMapping);

         XMLDirectMapping lengthMapping = new XMLDirectMapping();
         lengthMapping.setAttributeName("length");
         lengthMapping.setXPath(getPrimaryNamespaceXPath() + "length/text()");
         lengthMapping.setNullValue(255);
         descriptor.addMapping(lengthMapping);

         XMLDirectMapping precisionMapping = new XMLDirectMapping();
         precisionMapping.setAttributeName("precision");
         precisionMapping.setXPath(getPrimaryNamespaceXPath() + "precision/text()");
         precisionMapping.setNullValue(MIN_VALUE);
         descriptor.addMapping(precisionMapping);

         XMLDirectMapping scaleMapping = new XMLDirectMapping();
         scaleMapping.setAttributeName("scale");
         scaleMapping.setXPath(getPrimaryNamespaceXPath() + "scale/text()");
         scaleMapping.setNullValue(MIN_VALUE);
         descriptor.addMapping(scaleMapping);

         XMLDirectMapping cursorOutputMapping = new XMLDirectMapping();
         cursorOutputMapping.setAttributeName("cursorOutput");
         cursorOutputMapping.setXPath("@cursorOutput");
         cursorOutputMapping.setNullValue(Boolean.FALSE);
         descriptor.addMapping(cursorOutputMapping);

         XMLCompositeObjectMapping databaseTypeMapping = new XMLCompositeObjectMapping();
         databaseTypeMapping.setAttributeName("databaseTypeWrapper");
         databaseTypeMapping.setAttributeAccessor(new AttributeAccessor() {
     	    public Object getAttributeValueFromObject(Object object) {
     	    	PLSQLargument argument = (PLSQLargument)object;
     	    	DatabaseType type = argument.databaseType;
     	    	return wrapType(type);
     	    }

     	    public void setAttributeValueInObject(Object object, Object value) {
     	    	PLSQLargument argument = (PLSQLargument)object;
     	    	DatabaseTypeWrapper type = (DatabaseTypeWrapper)value;
     	    	argument.databaseType = type.getWrappedType();
     	    }
          });
         databaseTypeMapping.setReferenceClass(DatabaseTypeWrapper.class);
         databaseTypeMapping.setXPath(".");
         descriptor.addMapping(databaseTypeMapping);
         
         return descriptor;
     }

     protected XMLDescriptor buildPLSQLStoredProcedureCallDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(PLSQLStoredProcedureCall.class);
         descriptor.getInheritancePolicy().setParentClass(Call.class);
         descriptor.setDefaultRootElement(getPrimaryNamespaceXPath() + "plsql-stored-procedure-call");

         XMLDirectMapping procedureNameMapping = new XMLDirectMapping();
         procedureNameMapping.setAttributeName("procedureName");
         procedureNameMapping.setXPath(getPrimaryNamespaceXPath() + "procedure-name/text()");
         descriptor.addMapping(procedureNameMapping);

         XMLDirectMapping cursorOutputProcedureMapping = new XMLDirectMapping();
         cursorOutputProcedureMapping.setAttributeName("isCursorOutputProcedure");
         cursorOutputProcedureMapping.setXPath(getPrimaryNamespaceXPath() + "cursor-output-procedure/text()");
         cursorOutputProcedureMapping.setNullValue(false);
         descriptor.addMapping(cursorOutputProcedureMapping);

         XMLCompositeCollectionMapping argumentsMapping = new XMLCompositeCollectionMapping();
         argumentsMapping.setAttributeName("arguments");
         argumentsMapping.setXPath(getPrimaryNamespaceXPath() + "arguments/" + getPrimaryNamespaceXPath() + "argument");
         argumentsMapping.setReferenceClass(PLSQLargument.class);
         descriptor.addMapping(argumentsMapping);

         XMLCompositeCollectionMapping optionalMapping = new XMLCompositeCollectionMapping();
         optionalMapping.setAttributeName("optionalArguments");
         optionalMapping.setXPath(getPrimaryNamespaceXPath() + "optional-arguments/" + getPrimaryNamespaceXPath() + "argument");
         optionalMapping.setReferenceClass(DatabaseField.class);
         descriptor.addMapping(optionalMapping);

         return descriptor;
     }

     protected ClassDescriptor buildPLSQLStoredFunctionCallDescriptor() {

         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(PLSQLStoredFunctionCall.class);
         descriptor.getInheritancePolicy().setParentClass(PLSQLStoredProcedureCall.class);
         descriptor.setDefaultRootElement(getPrimaryNamespaceXPath() + "plsql-stored-function-call");
         descriptor.descriptorIsAggregate();

         return descriptor;
     }

     // 5757849 -- add metadata support for ObjectRelationalDatabaseField

     @Override
     protected ClassDescriptor buildDatabaseFieldDescriptor() {
         XMLDescriptor descriptor = (XMLDescriptor)super.buildDatabaseFieldDescriptor();
         descriptor.getInheritancePolicy().addClassIndicator(ObjectRelationalDatabaseField.class,
             getPrimaryNamespaceXPath() + "object-relational-field");

         return descriptor;
     }

     class ObjectRelationalDatabaseFieldInstantiationPolicy extends InstantiationPolicy {

         ObjectRelationalDatabaseFieldInstantiationPolicy() {
         }
         @Override
         public Object buildNewInstance() throws DescriptorException {
           return new ObjectRelationalDatabaseField("");
         }
     }
     protected ClassDescriptor buildObjectRelationalDatabaseFieldDescriptor() {
         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(ObjectRelationalDatabaseField.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseField.class);
         descriptor.setInstantiationPolicy(new ObjectRelationalDatabaseFieldInstantiationPolicy());

         XMLCompositeObjectMapping nestedFieldMapping = new XMLCompositeObjectMapping();
         nestedFieldMapping.setAttributeName("nestedTypeField");
         nestedFieldMapping.setXPath(getPrimaryNamespaceXPath() + "nested-type-field");
         nestedFieldMapping.setReferenceClass(DatabaseField.class);
         descriptor.addMapping(nestedFieldMapping);

         return descriptor;
     }
     
     protected ClassDescriptor buildXMLChoiceFieldToClassAssociationDescriptor() {
         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(XMLChoiceFieldToClassAssociation.class);
         
         XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
         fieldMapping.setAttributeName("xmlField");
         fieldMapping.setGetMethodName("getXmlField");
         fieldMapping.setSetMethodName("setXmlField");
         fieldMapping.setXPath(getPrimaryNamespaceXPath() + "xml-field");
         fieldMapping.setReferenceClass(XMLField.class);
         descriptor.addMapping(fieldMapping);
         
         XMLDirectMapping classNameMapping = new XMLDirectMapping();
         classNameMapping.setAttributeName("className");
         classNameMapping.setGetMethodName("getClassName");
         classNameMapping.setSetMethodName("setClassName");
         classNameMapping.setXPath(getPrimaryNamespaceXPath() + "class-name/text()");
         descriptor.addMapping(classNameMapping);
         
         return descriptor;
     }
     
     protected ClassDescriptor buildXMLChoiceCollectionMappingDescriptor() {
         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(XMLChoiceCollectionMapping.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);
         
         XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
         containerPolicyMapping.setAttributeName("containerPolicy");
         containerPolicyMapping.setReferenceClass(ContainerPolicy.class);
         containerPolicyMapping.setXPath(getPrimaryNamespaceXPath() + "container-policy");
         descriptor.addMapping(containerPolicyMapping);
         
         XMLCompositeCollectionMapping fieldToClassNameMapping = new XMLCompositeCollectionMapping();
         fieldToClassNameMapping.setAttributeName("fieldToClassAssociations");
         fieldToClassNameMapping.setGetMethodName("getChoiceFieldToClassAssociations");
         fieldToClassNameMapping.setSetMethodName("setChoiceFieldToClassAssociations");
         fieldToClassNameMapping.setReferenceClass(XMLChoiceFieldToClassAssociation.class);
         fieldToClassNameMapping.useCollectionClass(ArrayList.class);
         fieldToClassNameMapping.setXPath(getPrimaryNamespaceXPath() + "field-to-class-association");
         descriptor.addMapping(fieldToClassNameMapping);
         
         return descriptor;
     }
     
     protected ClassDescriptor buildXMLChoiceObjectMappingDescriptor() {
         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setJavaClass(XMLChoiceObjectMapping.class);
         descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);
         
         XMLCompositeCollectionMapping fieldToClassNameMapping = new XMLCompositeCollectionMapping();
         fieldToClassNameMapping.setAttributeName("fieldToClassAssociations");
         fieldToClassNameMapping.setGetMethodName("getChoiceFieldToClassAssociations");
         fieldToClassNameMapping.setSetMethodName("setChoiceFieldToClassAssociations");
         fieldToClassNameMapping.setReferenceClass(XMLChoiceFieldToClassAssociation.class);
         fieldToClassNameMapping.useCollectionClass(ArrayList.class);
         fieldToClassNameMapping.setXPath(getPrimaryNamespaceXPath() + "field-to-class-association");
         descriptor.addMapping(fieldToClassNameMapping);
         
         return descriptor;
     }     
     

}
