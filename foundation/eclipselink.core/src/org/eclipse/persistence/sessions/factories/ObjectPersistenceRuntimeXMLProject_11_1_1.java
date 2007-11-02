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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.XMLConversionPair;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
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
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
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
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.IsSetNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.internal.identitymaps.SoftIdentityMap;
import org.eclipse.persistence.queries.CursoredStreamPolicy;
import org.eclipse.persistence.queries.ScrollableCursorPolicy;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.internal.queries.SortedCollectionContainerPolicy;

/**
 * INTERNAL:
 * Define the TopLink OX project and descriptor information to read an AS 11<i>g</i>
 * (11.1.1) project from an XML file.
 * Note any changes must be reflected in the XML schema.
 * This project contains the 11gR1 mappings to the 11gR1 schema.
 */
public class ObjectPersistenceRuntimeXMLProject_11_1_1 extends ObjectPersistenceRuntimeXMLProject {

    /**
     * INTERNAL:
     * Return a new descriptor project.
     */
    public ObjectPersistenceRuntimeXMLProject_11_1_1() {
        super();
        addDescriptor(buildCursoredStreamPolicyDescriptor());
        addDescriptor(buildScrollableCursorrPolicyDescriptor());
        
        // Stored procedure arguments
        addDescriptor(buildStoredProcedureArgumentDescriptor());
        addDescriptor(buildStoredProcedureOutArgumentsDescriptor());
        addDescriptor(buildStoredProcedureInOutArgumentsDescriptor());
        addDescriptor(buildStoredProcedureCallDescriptor());
        // 5877994 -- add metadata support for Stored Function Calls
        addDescriptor(buildStoredFunctionCallDescriptor()); 
        
        //5963607 -- add Sorted Collection mapping support
        addDescriptor(buildSortedCollectionContainerPolicyDescriptor());
                
        //TopLink OXM
        addDescriptor(buildXMLAnyAttributeMappingDescriptor());
        addDescriptor(buildXMLCollectionReferenceMappingDescriptor());
        addDescriptor(buildXMLObjectReferenceMappingDescriptor());
        addDescriptor(this.buildXMLFragmentMappingDescriptor());
        addDescriptor(this.buildXMLFragmentCollectionMappingDescriptor());

        // Add Null Policy Mappings
        addDescriptor(buildAbstractNullPolicyDescriptor());
        addDescriptor(buildNullPolicyDescriptor());
        addDescriptor(buildIsSetNullPolicyDescriptor());
        
        // Do not add any descriptors beyond this point or an namespaceResolver exception may occur
        
        // Set the namespaces on all descriptors.
        // Need to duplicate in subclass to ensure all NEW descriptors also get
        // NamespaceResolvers set. 
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaceResolver.put("xsd", "http://www.w3.org/2001/XMLSchema");
        namespaceResolver.put("opm", "http://xmlns.oracle.com/ias/xsds/opm");
        namespaceResolver.put("toplink", "http://xmlns.oracle.com/ias/xsds/toplink");

        for (Iterator descriptorIter = getDescriptors().values().iterator(); descriptorIter.hasNext();) {
            XMLDescriptor descriptor = (XMLDescriptor)descriptorIter.next();
            descriptor.setNamespaceResolver(namespaceResolver);
        }
    }
    
    @Override
    protected ClassDescriptor buildProjectDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildProjectDescriptor();
        descriptor.setSchemaReference(new XMLSchemaClassPathReference("xsd/toplink-object-persistence_11_1_1.xsd"));

        XMLDirectMapping defaultTemporalMutableMapping = new XMLDirectMapping();
        defaultTemporalMutableMapping.setAttributeName("defaultTemporalMutable");
        defaultTemporalMutableMapping.setSetMethodName("setDefaultTemporalMutable");
        defaultTemporalMutableMapping.setGetMethodName("getDefaultTemporalMutable");
        defaultTemporalMutableMapping.setXPath("opm:default-temporal-mutable/text()");
        defaultTemporalMutableMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(defaultTemporalMutableMapping);
        
        return descriptor;
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
        
        descriptor.getInheritancePolicy().addClassIndicator(XMLBinaryDataMapping.class, "toplink:xml-binary-data-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLFragmentMapping.class, "toplink:xml-fragment-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLFragmentCollectionMapping.class, "toplink:xml-fragment-collection-mapping");

        descriptor.getInheritancePolicy().addClassIndicator(XMLCollectionReferenceMapping.class, "toplink:xml-collection-reference-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLObjectReferenceMapping.class, "toplink:xml-object-reference-mapping");
        descriptor.getInheritancePolicy().addClassIndicator(XMLAnyAttributeMapping.class, "toplink:xml-any-attribute-mapping");
        
        return descriptor;
    }

    @Override
    protected ClassDescriptor buildAbstractDirectMappingDescriptor() {

        XMLDescriptor descriptor = (XMLDescriptor)super.buildAbstractDirectMappingDescriptor();
        
        XMLDirectMapping attributeClassificationMapping = new XMLDirectMapping();
        attributeClassificationMapping.setAttributeName("attributeClassification");
        attributeClassificationMapping.setGetMethodName("getAttributeClassification");
        attributeClassificationMapping.setSetMethodName("setAttributeClassification");
        attributeClassificationMapping.setXPath("toplink:attribute-classification/text()");
        descriptor.addMapping(attributeClassificationMapping);
    
        return descriptor;
    }

    @Override
    protected ClassDescriptor buildObjectLevelReadQueryDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildObjectLevelReadQueryDescriptor();
	
        XMLDirectMapping readOnlyMapping = new XMLDirectMapping();
        readOnlyMapping.setAttributeName("isReadOnly");
        readOnlyMapping.setXPath("toplink:read-only/text()");
        readOnlyMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(readOnlyMapping);
        
        XMLDirectMapping joinSubclassesMapping = new XMLDirectMapping();
        joinSubclassesMapping.setAttributeName("shouldOuterJoinSubclasses");
        joinSubclassesMapping.setXPath("toplink:outer-join-subclasses/text()");
        descriptor.addMapping(joinSubclassesMapping);

        return descriptor;
    }

    @Override   
    protected ClassDescriptor buildInheritancePolicyDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildInheritancePolicyDescriptor();
        
        XMLDirectMapping joinSubclassesMapping = new XMLDirectMapping();
        joinSubclassesMapping.setAttributeName("shouldOuterJoinSubclasses");
        joinSubclassesMapping.setXPath("toplink:outer-join-subclasses/text()");
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
    
    protected ClassDescriptor buildRelationalDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(RelationalDescriptor.class);
        descriptor.getInheritancePolicy().setParentClass(ClassDescriptor.class);

        XMLCompositeCollectionMapping tablesMapping = new XMLCompositeCollectionMapping();
        tablesMapping.useCollectionClass(org.eclipse.persistence.internal.helper.NonSynchronizedVector.class);
        tablesMapping.setAttributeName("tables/table");
        tablesMapping.setGetMethodName("getTables");
        tablesMapping.setSetMethodName("setTables");
        tablesMapping.setXPath("toplink:tables/toplink:table");
        tablesMapping.setReferenceClass(DatabaseTable.class);
        descriptor.addMapping(tablesMapping);

        XMLCompositeCollectionMapping foreignKeyForMultipleTables = new XMLCompositeCollectionMapping();
        foreignKeyForMultipleTables.setReferenceClass(Association.class);
        foreignKeyForMultipleTables.setAttributeName("foreignKeysForMultipleTables");
        foreignKeyForMultipleTables.setXPath("toplink:foreign-keys-for-multiple-table/opm:field-reference");
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
    
    protected ClassDescriptor buildScrollableCursorrPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(ScrollableCursorPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(ContainerPolicy.class);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildContainerPolicyDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildContainerPolicyDescriptor();

        descriptor.getInheritancePolicy().addClassIndicator(ScrollableCursorPolicy.class, "toplink:scrollable-cursor-policy");
        descriptor.getInheritancePolicy().addClassIndicator(CursoredStreamPolicy.class, "toplink:cursored-stream-policy");
        descriptor.getInheritancePolicy().addClassIndicator(SortedCollectionContainerPolicy.class, "toplink:sorted-collection-container-policy");

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildOneToOneMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildOneToOneMappingDescriptor();
        descriptor.removeMappingForAttributeName("usesJoiningMapping");
        
        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("joinFetch");
        joinFetchMapping.setXPath("toplink:join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", new Integer(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", new Integer(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", new Integer(ForeignReferenceMapping.NONE));
        joinFetchMapping.setConverter(joinFetchConverter);
        joinFetchMapping.setNullValue(ForeignReferenceMapping.NONE);
        descriptor.addMapping(joinFetchMapping);
        
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
        defaultRootElementMapping.setXPath("toplink:default-root-element/text()");
        descriptor.addMapping(defaultRootElementMapping);
        
        XMLCompositeObjectMapping defaultRootElementFieldMapping = new XMLCompositeObjectMapping();
        defaultRootElementFieldMapping.setAttributeName("defaultRootElementField");
        defaultRootElementFieldMapping.setGetMethodName("getDefaultRootElementField");
        defaultRootElementFieldMapping.setSetMethodName("setDefaultRootElementField");
        defaultRootElementFieldMapping.setXPath("toplink:default-root-element-field");
        defaultRootElementFieldMapping.setReferenceClass(XMLField.class);
        descriptor.addMapping(defaultRootElementFieldMapping);

        XMLDirectMapping shouldPreserveDocument = new XMLDirectMapping();
        shouldPreserveDocument.setAttributeName("shouldPreserveDocument");
        shouldPreserveDocument.setGetMethodName("shouldPreserveDocument");
        shouldPreserveDocument.setSetMethodName("setShouldPreserveDocument");
        shouldPreserveDocument.setNullValue(Boolean.FALSE);
        shouldPreserveDocument.setXPath("toplink:should-preserve-document/text()");
        descriptor.addMapping(shouldPreserveDocument);

        XMLCompositeObjectMapping namespaceResolverMapping = new XMLCompositeObjectMapping();
        namespaceResolverMapping.setXPath("toplink:namespace-resolver");
        namespaceResolverMapping.setAttributeName("namespaceResolver");
        namespaceResolverMapping.setGetMethodName("getNamespaceResolver");
        namespaceResolverMapping.setSetMethodName("setNamespaceResolver");
        namespaceResolverMapping.setReferenceClass(NamespaceResolver.class);
        descriptor.addMapping(namespaceResolverMapping);

        XMLCompositeObjectMapping schemaReferenceMapping = new XMLCompositeObjectMapping();
        schemaReferenceMapping.setAttributeName("schemaReference");
        schemaReferenceMapping.setXPath("toplink:schema");
        schemaReferenceMapping.setReferenceClass(XMLSchemaReference.class);
        descriptor.addMapping(schemaReferenceMapping);

        return descriptor;
    }

    @Override
    protected ClassDescriptor buildManyToManyMappingMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildManyToManyMappingMappingDescriptor();
        
        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("joinFetch");
        joinFetchMapping.setXPath("toplink:join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", new Integer(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", new Integer(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", new Integer(ForeignReferenceMapping.NONE));
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
        joinFetchMapping.setXPath("toplink:join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", new Integer(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", new Integer(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", new Integer(ForeignReferenceMapping.NONE));
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
        joinFetchMapping.setXPath("toplink:join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", new Integer(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", new Integer(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", new Integer(ForeignReferenceMapping.NONE));
        joinFetchMapping.setConverter(joinFetchConverter);
        joinFetchMapping.setNullValue(ForeignReferenceMapping.NONE);
        descriptor.addMapping(joinFetchMapping);
        
        return descriptor;
    }
    

    protected ClassDescriptor buildSortedCollectionContainerPolicyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.internal.queries.SortedCollectionContainerPolicy.class);

        descriptor.getInheritancePolicy().setParentClass(org.eclipse.persistence.internal.queries.CollectionContainerPolicy.class);

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeName("comparatorClass");
        keyMapping.setGetMethodName("getComparatorClass");
        keyMapping.setSetMethodName("setComparatorClass");
        keyMapping.setXPath("toplink:comparator-class/text()");
        descriptor.addMapping(keyMapping);

        return descriptor;
    }
    

    @Override    
    protected ClassDescriptor buildAggregateCollectionMappingDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildAggregateCollectionMappingDescriptor();
        
        XMLDirectMapping joinFetchMapping = new XMLDirectMapping();
        joinFetchMapping.setAttributeName("joinFetch");
        joinFetchMapping.setXPath("toplink:join-fetch/text()");
        ObjectTypeConverter joinFetchConverter = new ObjectTypeConverter();
        joinFetchConverter.addConversionValue("inner-join", new Integer(ForeignReferenceMapping.INNER_JOIN));
        joinFetchConverter.addConversionValue("outer-join", new Integer(ForeignReferenceMapping.OUTER_JOIN));
        joinFetchConverter.addConversionValue("none", new Integer(ForeignReferenceMapping.NONE));
        joinFetchMapping.setConverter(joinFetchConverter);
        joinFetchMapping.setNullValue(ForeignReferenceMapping.NONE);
        descriptor.addMapping(joinFetchMapping);
        
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
        fieldMapping.setXPath("toplink:field");
        descriptor.addMapping(fieldMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.queries.ContainerPolicy.class);
        containerPolicyMapping.setXPath("toplink:container");
        descriptor.addMapping(containerPolicyMapping);
        
        XMLDirectMapping xmlRootMapping = new XMLDirectMapping();
        xmlRootMapping.setAttributeName("useXMLRoot");
        xmlRootMapping.setGetMethodName("usesXMLRoot");
        xmlRootMapping.setSetMethodName("setUseXMLRoot");
        xmlRootMapping.setXPath("toplink:use-xml-root/text()");
        xmlRootMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(xmlRootMapping);

        XMLDirectMapping keepAsElementMapping = new XMLDirectMapping();
        keepAsElementMapping.setAttributeName("keepAsElementPolicy");
        keepAsElementMapping.setGetMethodName("getKeepAsElementPolicy");
        keepAsElementMapping.setSetMethodName("setKeepAsElementPolicy");
        keepAsElementMapping.setXPath("toplink:keep-as-element-policy");
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
        fieldMapping.setXPath("toplink:field");
        descriptor.addMapping(fieldMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.queries.ContainerPolicy.class);
        containerPolicyMapping.setXPath("toplink:container");
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
        containerPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.queries.ContainerPolicy.class);
        containerPolicyMapping.setXPath("toplink:containerpolicy");
        descriptor.addMapping(containerPolicyMapping);
        
        XMLDirectMapping useSingleNodeMapping = new XMLDirectMapping();
        useSingleNodeMapping.setAttributeName("usesSingleNode");
        useSingleNodeMapping.setGetMethodName("usesSingleNode");
        useSingleNodeMapping.setSetMethodName("setUsesSingleNode");
        useSingleNodeMapping.setXPath("toplink:uses-single-node/text()");
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
        sourceToTargetKeyFieldAssociationsMapping.setXPath("toplink:source-to-target-key-field-association/opm:field-reference");
        descriptor.addMapping(sourceToTargetKeyFieldAssociationsMapping);
        
        XMLCompositeCollectionMapping sourceToTargetKeysMapping = new XMLCompositeCollectionMapping();
        sourceToTargetKeysMapping.setReferenceClass(DatabaseField.class);
        sourceToTargetKeysMapping.setAttributeName("sourceToTargetKeys");
        sourceToTargetKeysMapping.setXPath("toplink:source-to-target-key-fields/toplink:field");
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
    protected ClassDescriptor buildXMLAnyObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLAnyObjectMapping.class);
        descriptor.getInheritancePolicy().setParentClass(DatabaseMapping.class);

        XMLCompositeObjectMapping fieldMapping = new XMLCompositeObjectMapping();
        fieldMapping.setAttributeName("field");
        fieldMapping.setReferenceClass(DatabaseField.class);
        fieldMapping.setGetMethodName("getField");
        fieldMapping.setSetMethodName("setField");
        fieldMapping.setXPath("toplink:field");
        descriptor.addMapping(fieldMapping);

        XMLDirectMapping xmlRootMapping = new XMLDirectMapping();
        xmlRootMapping.setAttributeName("useXMLRoot");
        xmlRootMapping.setGetMethodName("usesXMLRoot");
        xmlRootMapping.setSetMethodName("setUseXMLRoot");
        xmlRootMapping.setXPath("toplink:use-xml-root/text()");
        xmlRootMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(xmlRootMapping);

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
        typedFieldMapping.setXPath("toplink:typed-text-field/text()");
        typedFieldMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(typedFieldMapping);

        XMLDirectMapping singleNodeMapping = new XMLDirectMapping();
        singleNodeMapping.setAttributeName("usesSingleNode");
        singleNodeMapping.setGetMethodName("usesSingleNode");
        singleNodeMapping.setSetMethodName("setUsesSingleNode");
        singleNodeMapping.setXPath("toplink:single-node/text()");
        singleNodeMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(singleNodeMapping);

        XMLDirectMapping schemaTypeMapping = new XMLDirectMapping();
        schemaTypeMapping.setAttributeName("schemaType");
        schemaTypeMapping.setGetMethodName("getSchemaType");
        schemaTypeMapping.setSetMethodName("setSchemaType");
        schemaTypeMapping.setXPath("toplink:schema-type/text()");
        descriptor.addMapping(schemaTypeMapping);

        XMLCompositeCollectionMapping xmlToJavaPairsMapping = new XMLCompositeCollectionMapping();
        xmlToJavaPairsMapping.setXPath("toplink:xml-to-java-conversion-pair");
        xmlToJavaPairsMapping.useCollectionClass(ArrayList.class);
        xmlToJavaPairsMapping.setReferenceClass(XMLConversionPair.class);
        xmlToJavaPairsMapping.setAttributeName("userXMLTypes");
        xmlToJavaPairsMapping.setGetMethodName("getUserXMLTypesForDeploymentXML");
        xmlToJavaPairsMapping.setSetMethodName("setUserXMLTypesForDeploymentXML");
        descriptor.addMapping(xmlToJavaPairsMapping);

        XMLCompositeCollectionMapping javaToXMLPairsMapping = new XMLCompositeCollectionMapping();
        javaToXMLPairsMapping.useCollectionClass(ArrayList.class);
        javaToXMLPairsMapping.setXPath("toplink:java-to-xml-conversion-pair");
        javaToXMLPairsMapping.setReferenceClass(XMLConversionPair.class);
        javaToXMLPairsMapping.setAttributeName("userJavaTypes");
        javaToXMLPairsMapping.setGetMethodName("getUserJavaTypesForDeploymentXML");
        javaToXMLPairsMapping.setSetMethodName("setUserJavaTypesForDeploymentXML");
        descriptor.addMapping(javaToXMLPairsMapping);

        XMLDirectMapping leafElementTypeMapping = new XMLDirectMapping();
        leafElementTypeMapping.setAttributeName("leafElementType");
        leafElementTypeMapping.setGetMethodName("getLeafElementType");
        leafElementTypeMapping.setSetMethodName("setLeafElementType");
        leafElementTypeMapping.setXPath("toplink:leaf-element-type/text()");
        descriptor.addMapping(leafElementTypeMapping);
        
        return descriptor;
    }
    
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
        unitOfWorkCacheIsolationLevelConverter.addConversionValue("default", new Integer(ClassDescriptor.UNDEFINED_ISOLATATION));
        unitOfWorkCacheIsolationLevelMapping.setNullValue(new Integer(ClassDescriptor.UNDEFINED_ISOLATATION));
        
        return descriptor;
    }

    // support for Stored Procedure/Function Calls
    @Override
    protected ClassDescriptor buildCallDescriptor() {
      XMLDescriptor descriptor = (XMLDescriptor)super.buildCallDescriptor();
      descriptor.getInheritancePolicy().addClassIndicator(StoredProcedureCall.class,
          "toplink:stored-procedure-call");
      descriptor.getInheritancePolicy().addClassIndicator(StoredFunctionCall.class,
          "toplink:stored-function-call");
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
    public class StoredProcedureArgument {
          protected String argumentName;
          protected String argumentFieldName;
          protected Class argumentType;
          protected int argumentSQLType = DatabaseField.NULL_SQL_TYPE;
          protected String argumentSqlTypeName;
          protected Object argumentValue;
          protected StoredProcedureArgument nestedType;
    
          public StoredProcedureArgument() {
              super();
          }
          
          public Integer getArgType() {
              return DatasourceCall.IN;
          }
    
          public StoredProcedureArgument(DatabaseField dbfield) {
              this.setDatabaseField(dbfield);
          }
    
          public String getArgumentFieldName() {
              return argumentFieldName;
          }
    
          public void setArgumentFieldName(String argumentFieldName) {
              this.argumentFieldName = argumentFieldName;
          }
          public String getArgumentName() {
              return argumentName;
          }
          public void setArgumentName(String argumentName) {
              this.argumentName = argumentName;
          }
    
          /**
           * @return The value of the argument to be used to pass
           * to the procedure, or null if not set.
           */
          public Object getArgumentValue() {
              return argumentValue;
          }
    
          /**
           * @param argumentValue the value of the argument to be used to
           * pass to the procedure.
           */
          public void setArgumentValue(Object argumentValue) {
              this.argumentValue = argumentValue;
          }
    
          public DatabaseField getDatabaseField() {
              DatabaseField dbfield = new DatabaseField(argumentFieldName == null ? "" : argumentFieldName);
              dbfield.setType(argumentType);
              dbfield.setSqlType(argumentSQLType);
    
              if ((argumentSqlTypeName != null) && 
                  (!argumentSqlTypeName.equals(""))) {
                  dbfield = new ObjectRelationalDatabaseField(dbfield);
                  ((ObjectRelationalDatabaseField)dbfield).setSqlTypeName(argumentSqlTypeName);
                  if (nestedType != null) {
                      ((ObjectRelationalDatabaseField)dbfield).setNestedTypeField(nestedType.getDatabaseField());
                  }
              }
              return dbfield;
          }
    
          public void setDatabaseField(DatabaseField dbfield) {
              argumentFieldName = dbfield.getName();
              argumentType = dbfield.getType();
              argumentSQLType = dbfield.getSqlType();
    
              if (dbfield instanceof ObjectRelationalDatabaseField) {
                  argumentSqlTypeName = 
                          ((ObjectRelationalDatabaseField)dbfield).getSqlTypeName();
                  DatabaseField tempField = 
                      ((ObjectRelationalDatabaseField)dbfield).getNestedTypeField();
                  if (tempField != null) {
                      nestedType = 
                              new StoredProcedureArgument(tempField);
                  }
              }
          }
    }
    
    public class StoredProcedureInOutArgument extends StoredProcedureArgument {
          protected String outputArgumentName;
    
          public StoredProcedureInOutArgument() {
              super();
          }
          
          public StoredProcedureInOutArgument(DatabaseField dbfield) {
              super(dbfield);
          }
          
          public Integer getArgType() {
              return DatasourceCall.INOUT;
          }
          
          /**
           * @return outputArgumentName, or null if not set.
           */
          public String getOutputArgumentName() {
              return outputArgumentName;
          }
          public void setOutputArgumentName(String outputArgumentName) {
              this.outputArgumentName = outputArgumentName;
          }
    }
    
    public class StoredProcedureOutArgument extends StoredProcedureArgument {
        public StoredProcedureOutArgument() {
            super();
        }

        public Integer getArgType() {
            return DatasourceCall.OUT;
        }
        
        public StoredProcedureOutArgument(DatabaseField dbfield){
            super(dbfield);
        }
    }
    
    public class StoredProcedureArgumentInstantiationPolicy extends InstantiationPolicy {
        
        protected ObjectPersistenceRuntimeXMLProject_11_1_1 outer;
        public StoredProcedureArgumentInstantiationPolicy(ObjectPersistenceRuntimeXMLProject_11_1_1 outer) {
          this.outer = outer;
        }
        
        @Override
        public Object buildNewInstance() throws DescriptorException {
          return outer.new StoredProcedureArgument();
        }
    }
    
    public class StoredProcedureInOutArgumentInstantiationPolicy extends InstantiationPolicy {
        
        protected ObjectPersistenceRuntimeXMLProject_11_1_1 outer;
        public StoredProcedureInOutArgumentInstantiationPolicy(ObjectPersistenceRuntimeXMLProject_11_1_1 outer) {
          this.outer = outer;
        }
        
        @Override
        public Object buildNewInstance() throws DescriptorException {
          return outer.new StoredProcedureInOutArgument();
        }
    }
    
    public class StoredProcedureOutArgumentInstantiationPolicy extends InstantiationPolicy {
        
        protected ObjectPersistenceRuntimeXMLProject_11_1_1 outer;
          
        public StoredProcedureOutArgumentInstantiationPolicy(ObjectPersistenceRuntimeXMLProject_11_1_1 outer) {
            this.outer = outer;
        }
          
        @Override
        public Object buildNewInstance() throws DescriptorException {
            return outer.new StoredProcedureOutArgument();
        }
    }

    protected ClassDescriptor buildStoredProcedureArgumentDescriptor() {
        
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureArgument.class);
        // need policy 'cause TreeBuilder cannot use default constructor
        descriptor.setInstantiationPolicy(new StoredProcedureArgumentInstantiationPolicy(this));
        descriptor.descriptorIsAggregate();

        descriptor.setDefaultRootElement("argument");
        descriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
        descriptor.getInheritancePolicy().addClassIndicator(StoredProcedureArgument.class,
            "toplink:procedure-argument");
        descriptor.getInheritancePolicy().addClassIndicator(StoredProcedureInOutArgument.class,
            "toplink:procedure-inoutput-argument");
        descriptor.getInheritancePolicy().addClassIndicator(StoredProcedureOutArgument.class,
            "toplink:procedure-output-argument");
         
        XMLDirectMapping argumentNameMapping = new XMLDirectMapping();
        argumentNameMapping.setAttributeName("argumentName");
        argumentNameMapping.setXPath("toplink:procedure-argument-name/text()");
        descriptor.addMapping(argumentNameMapping);

        XMLDirectMapping argumentFieldNameMapping = new XMLDirectMapping();
        argumentFieldNameMapping.setAttributeName("argumentFieldName");
        argumentFieldNameMapping.setXPath("toplink:argument-name/text()");
        descriptor.addMapping(argumentFieldNameMapping);
         
        XMLDirectMapping argumentTypeMapping = new XMLDirectMapping();
        argumentTypeMapping.setAttributeName("argumentType");
        argumentTypeMapping.setXPath("toplink:procedure-argument-type/text()");
        descriptor.addMapping(argumentTypeMapping);
         
        XMLDirectMapping argumentSQLTypeMapping = new XMLDirectMapping();
        argumentSQLTypeMapping.setAttributeName("argumentSQLType");
        argumentSQLTypeMapping.setXPath("toplink:procedure-argument-sqltype/text()");
        argumentSQLTypeMapping.setNullValue(DatabaseField.NULL_SQL_TYPE);
        descriptor.addMapping(argumentSQLTypeMapping);
        
        XMLDirectMapping argumentSqlTypeNameMapping = new XMLDirectMapping();
        argumentSqlTypeNameMapping.setAttributeName("argumentSqlTypeName");
        argumentSqlTypeNameMapping.setXPath("toplink:procedure-argument-sqltype-name/text()");
        descriptor.addMapping(argumentSqlTypeNameMapping);

        XMLDirectMapping argumentValueMapping = new XMLDirectMapping();
        argumentValueMapping.setAttributeName("argumentValue");
        argumentValueMapping.setField(buildTypedField("toplink:argument-value/text()"));
        descriptor.addMapping(argumentValueMapping);
        
        return descriptor;
    }

    protected ClassDescriptor buildStoredProcedureInOutArgumentsDescriptor() {
        
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureInOutArgument.class);
        descriptor.setInstantiationPolicy(new StoredProcedureInOutArgumentInstantiationPolicy(this));
        descriptor.getInheritancePolicy().setParentClass(StoredProcedureArgument.class);

        //used incase the in databasefield is named different than the out databasefield
        XMLDirectMapping outputArgumentNameMapping = new XMLDirectMapping();
        outputArgumentNameMapping.setAttributeName("outputArgumentName");
        outputArgumentNameMapping.setXPath("toplink:output-argument-name/text()");
        descriptor.addMapping(outputArgumentNameMapping);
        
        return descriptor;
    }
    
    protected ClassDescriptor buildStoredProcedureOutArgumentsDescriptor() {

        //StoredProcedureOutArgument maps closest to a ObjectRelationalDatabseFieldObject
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(StoredProcedureOutArgument.class);
        descriptor.setInstantiationPolicy(new StoredProcedureOutArgumentInstantiationPolicy(this));
        descriptor.getInheritancePolicy().setParentClass(StoredProcedureArgument.class);

        return descriptor;
    }
    
    public class StoredProcedureArgumentsAccessor extends AttributeAccessor {
        
        public StoredProcedureArgumentsAccessor() {
            super();
        }
        
        @Override   
        public Object getAttributeValueFromObject(Object anObject) throws DescriptorException {
            
            StoredProcedureCall spc = (StoredProcedureCall)anObject;
            Vector parameterTypes = spc.getParameterTypes();
            Vector parameters = spc.getParameters();
            Vector procedureArgumentNames = spc.getProcedureArgumentNames();
            
            Vector procedureArguments = new Vector();
            for (int i = spc.getFirstParameterIndexForCallString(); i < parameterTypes.size(); i++) {
                Integer argumentType = (Integer) parameterTypes.get(i);
                Object argument = parameters.get(i);
                String argumentName = (String) procedureArgumentNames.get(i);

                if (DatasourceCall.IN.equals(argumentType)) {
                    StoredProcedureArgument inArgument;
                    
                    // set argument value or argument field .
                    if (!(argument instanceof DatabaseField)){
                        inArgument = new StoredProcedureArgument();
                        inArgument.setArgumentValue(argument);
                    }else{
                        inArgument = new StoredProcedureArgument((DatabaseField) argument);
                    }
                    inArgument.setArgumentName(argumentName);

                    procedureArguments.add(inArgument);
                } else if (DatasourceCall.INOUT.equals(argumentType)) {
                    StoredProcedureInOutArgument inOutArgument = null;
                    if (argument instanceof Object[]) {
                        Object[] objects = (Object[]) argument;
                        Object inputArgument = objects[0];
                        DatabaseField outputArgument = (DatabaseField) objects[1];
                        inOutArgument = new StoredProcedureInOutArgument(outputArgument);
                        // Set argument value or field name.
                        if (!(inputArgument instanceof DatabaseField)){
                            inOutArgument.setArgumentValue(inputArgument);
                        }else{
                            inOutArgument.setArgumentFieldName(((DatabaseField) inputArgument).getName());
                        }

                        // Set output argument name
                        inOutArgument.setOutputArgumentName(outputArgument.getName());
                        
                        inOutArgument.setArgumentName(argumentName);
                    }
                    procedureArguments.add(inOutArgument);
                } else if (DatasourceCall.OUT.equals(argumentType)) {
                    StoredProcedureOutArgument outArgument = new StoredProcedureOutArgument((DatabaseField)argument);
                    outArgument.setArgumentName(argumentName);
                    procedureArguments.add(outArgument);
                }
            }
            return procedureArguments;
        }
        
        @Override
        public void setAttributeValueInObject(Object domainObject, Object attributeValue) throws DescriptorException {
            
            StoredProcedureCall spc = (StoredProcedureCall)domainObject;
            //vector of arguments that need to be put into the call
            Vector procedureArguments = (Vector)attributeValue;
            for (int i = 0; i < procedureArguments.size(); i++) {
                StoredProcedureArgument spa = (StoredProcedureArgument) procedureArguments.get(i);
                if (spa.getArgType().equals(DatasourceCall.IN)) {
                    String inArgumentFieldName = spa.getArgumentFieldName();

                    // Either argument value or database field name need be specified in XML. 
                    // They can not be defined simultaneously.
                    if (inArgumentFieldName != null){
                        spc.getParameters().add(spa.getDatabaseField());
                    }else{
                        spc.getParameters().add(spa.getArgumentValue());
                    }

                    // Set argument name.
                    spc.getProcedureArgumentNames().add(spa.getArgumentName());

                    // Set argument type.
                    spc.getParameterTypes().add(DatasourceCall.IN);
                } else if (spa.getArgType().equals(DatasourceCall.INOUT)) {
                    StoredProcedureInOutArgument inOutArgument = (StoredProcedureInOutArgument) spa;

                    Object inField;

                    // Either argument value or database field name need be specified in XML. 
                    // They can not be defined simultaneously.
                    if (inOutArgument.getArgumentValue() == null){
                        inField = inOutArgument.getDatabaseField();
                    }else{
                        inField = inOutArgument.getArgumentValue();
                    }
                    DatabaseField outField = inOutArgument.getDatabaseField();
                    outField.setName(inOutArgument.getOutputArgumentName());
                    
                    //Set argument name.
                    spc.getProcedureArgumentNames().add(inOutArgument.getArgumentName());

                    
                    Object[] objects = { inField, outField };
                    spc.getParameters().add(objects);
                    
                    //Set argument type.
                    spc.getParameterTypes().add(DatasourceCall.INOUT);
                }else if (spa.getArgType().equals(DatasourceCall.OUT)) {
                    
                    //Set procedure argument name.
                    spc.getProcedureArgumentNames().add(spa.getArgumentName());

                    spc.getParameters().add(spa.getDatabaseField());
                    
                    //Set argument type.
                    spc.getParameterTypes().add(DatasourceCall.OUT);
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
        procedureNameMapping.setXPath("toplink:procedure-name/text()");
        descriptor.addMapping(procedureNameMapping);
        
        XMLDirectMapping cursorOutputProcedureMapping = new XMLDirectMapping();
        cursorOutputProcedureMapping.setAttributeName("isCursorOutputProcedure");
        cursorOutputProcedureMapping.setXPath("toplink:cursor-output-procedure/text()");
        descriptor.addMapping(cursorOutputProcedureMapping);

        XMLCompositeCollectionMapping storedProcArgumentsMapping = new XMLCompositeCollectionMapping();
        storedProcArgumentsMapping.useCollectionClass(NonSynchronizedVector.class);
        storedProcArgumentsMapping.setAttributeName("procedureArguments");
        storedProcArgumentsMapping.setAttributeAccessor(new StoredProcedureArgumentsAccessor());
        storedProcArgumentsMapping.setReferenceClass(StoredProcedureArgument.class);
        storedProcArgumentsMapping.setXPath("toplink:arguments/toplink:argument");
        descriptor.addMapping(storedProcArgumentsMapping);
        
        return descriptor;
    }
    
    public class StoredFunctionResultAccessor extends AttributeAccessor {
        
        public StoredFunctionResultAccessor() {
            super();
        }
        
        // for StoredFunctionCalls, the return value's information
        // is stored in the parameters list at index 0
        @Override
        public Object getAttributeValueFromObject(Object anObject) throws DescriptorException {
            StoredFunctionCall sfc = (StoredFunctionCall)anObject;
            Object argument = sfc.getParameters().get(0);
            String argumentName = (String)sfc.getProcedureArgumentNames().get(0);
            StoredProcedureOutArgument outArgument = new StoredProcedureOutArgument((DatabaseField)argument);
            outArgument.setArgumentName(argumentName);
            return outArgument;
        }
        
        @Override
        public void setAttributeValueInObject(Object domainObject, Object attributeValue) throws DescriptorException {
            StoredFunctionCall sfc = (StoredFunctionCall)domainObject;
            StoredProcedureOutArgument spoa = (StoredProcedureOutArgument)attributeValue;
            // Set procedure argument name.
            sfc.getProcedureArgumentNames().set(0, spoa.getArgumentName());
            sfc.getParameters().set(0, spoa.getDatabaseField());
            // Set argument type.
            sfc.getParameterTypes().set(0, DatasourceCall.OUT);
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
        storedFunctionResultMapping.setXPath("toplink:stored-function-result");
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
        isCDATAMapping.setXPath("toplink:is-cdata/text()");
        isCDATAMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(isCDATAMapping);
        
        // Add Null Policy
        XMLCompositeObjectMapping aMapping = new XMLCompositeObjectMapping();
        aMapping.setReferenceClass(AbstractNullPolicy.class);
        aMapping.setAttributeName("nullPolicy");
        aMapping.setXPath("toplink:null-policy");
        ((DatabaseMapping)aMapping).setAttributeAccessor(new NullPolicyAttributeAccessor());
        descriptor.addMapping(aMapping);       
        
        return descriptor;
    }
    
    protected ClassDescriptor buildXMLCompositeDirectCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLCompositeDirectCollectionMapping.class);

        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeDirectCollectionMapping.class);
        
        XMLDirectMapping isCDATAMapping = new XMLDirectMapping();
        isCDATAMapping.setAttributeName("isCDATA");
        isCDATAMapping.setGetMethodName("isCDATA");
        isCDATAMapping.setSetMethodName("setIsCDATA");
        isCDATAMapping.setXPath("toplink:is-cdata/text()");
        isCDATAMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(isCDATAMapping);
        
        return descriptor;
    }    
    
     protected ClassDescriptor buildXMLLoginDescriptor(){
        ClassDescriptor descriptor = super.buildXMLLoginDescriptor();
        
        XMLDirectMapping equalNamespaceResolversMapping = new XMLDirectMapping();
        equalNamespaceResolversMapping.setAttributeName("equalNamespaceResolvers");
        equalNamespaceResolversMapping.setGetMethodName("hasEqualNamespaceResolvers");
        equalNamespaceResolversMapping.setSetMethodName("setEqualNamespaceResolvers");
        equalNamespaceResolversMapping.setXPath("toplink:equal-namespace-resolvers/text()");
        equalNamespaceResolversMapping.setNullValue(Boolean.TRUE);
        descriptor.addMapping(equalNamespaceResolversMapping);
        
        return descriptor;
    }

    protected ClassDescriptor buildAbstractNullPolicyDescriptor() {
         XMLDescriptor aDescriptor = new XMLDescriptor();
         aDescriptor.setJavaClass(AbstractNullPolicy.class);
         aDescriptor.setDefaultRootElement("abstract-null-policy");

         XMLDirectMapping xnrnMapping = new XMLDirectMapping();
         xnrnMapping.setAttributeName("isNullRepresentedByXsiNil");
         xnrnMapping.setXPath("toplink:xsi-nil-represents-null/text()");
         xnrnMapping.setNullValue(Boolean.FALSE);         
         aDescriptor.addMapping(xnrnMapping);

         XMLDirectMapping enrnMapping = new XMLDirectMapping();
         enrnMapping.setAttributeName("isNullRepresentedByEmptyNode");
         enrnMapping.setXPath("toplink:empty-node-represents-null/text()");
         enrnMapping.setNullValue(Boolean.FALSE);         
         aDescriptor.addMapping(enrnMapping);

         XMLDirectMapping nrfxMapping = new XMLDirectMapping();
         nrfxMapping.setAttributeName("marshalNullRepresentation");
         nrfxMapping.setXPath("toplink:null-representation-for-xml/text()");
         // Restricted to XSI_NIL,ABSENT_NODE,EMPTY_NODE	
         EnumTypeConverter aConverter = new EnumTypeConverter(nrfxMapping, XMLNullRepresentationType.class, false);
         nrfxMapping.setConverter(aConverter);
         aDescriptor.addMapping(nrfxMapping);
         
         // Subclasses
         aDescriptor.getInheritancePolicy().setClassIndicatorField(new XMLField("@xsi:type"));
         aDescriptor.getInheritancePolicy().addClassIndicator(IsSetNullPolicy.class, "toplink:is-set-null-policy");
         aDescriptor.getInheritancePolicy().addClassIndicator(NullPolicy.class, "toplink:null-policy");

         return aDescriptor;
     }

     protected ClassDescriptor buildNullPolicyDescriptor() {
         XMLDescriptor aDescriptor = new XMLDescriptor();
         aDescriptor.setJavaClass(NullPolicy.class);
         aDescriptor.getInheritancePolicy().setParentClass(AbstractNullPolicy.class);

         // This boolean can only be set on the NullPolicy implementation even though the field is on the abstract class
         XMLDirectMapping xnranMapping = new XMLDirectMapping();
         xnranMapping.setAttributeName("isSetPerformedForAbsentNode");
         xnranMapping.setXPath("toplink:is-set-performed-for-absent-node/text()");
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
         isSetMethodNameMapping.setXPath("toplink:is-set-method-name/text()");
         aDescriptor.addMapping(isSetMethodNameMapping);

         // 20070922: Bug#6039730 - add IsSet capability for 1+ parameters for SDO
         XMLCompositeDirectCollectionMapping isSetParameterTypesMapping = new XMLCompositeDirectCollectionMapping();
         isSetParameterTypesMapping.setAttributeName("isSetParameterTypes");
         isSetParameterTypesMapping.setXPath("toplink:is-set-parameter-type");
         ((DatabaseMapping)isSetParameterTypesMapping).setAttributeAccessor(new IsSetNullPolicyIsSetParameterTypesAttributeAccessor());         
         aDescriptor.addMapping(isSetParameterTypesMapping);

         XMLCompositeDirectCollectionMapping isSetParametersMapping = new XMLCompositeDirectCollectionMapping();
         isSetParametersMapping.setAttributeName("isSetParameters");
         isSetParametersMapping.setXPath("toplink:is-set-parameter");         
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
         nullPolicyClassMapping.setXPath("toplink:null-policy");

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

     /**
      * INTERNAL:
      */
     protected ConstantTransformer getConstantTransformerForProjectVersionMapping() {
     	return new ConstantTransformer("Oracle TopLink - 11g Release 1 (11.1.1)");
     }
}
