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
package org.eclipse.persistence.eis;

// javase imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

// Java extension imports
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.interactions.XMLInteraction;
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeDirectCollectionMapping;
import org.eclipse.persistence.eis.mappings.EISCompositeObjectMapping;
import org.eclipse.persistence.eis.mappings.EISDirectMapping;
import org.eclipse.persistence.eis.mappings.EISOneToManyMapping;
import org.eclipse.persistence.eis.mappings.EISOneToOneMapping;
import org.eclipse.persistence.eis.mappings.EISTransformationMapping;
import org.eclipse.persistence.internal.descriptors.InteractionArgument;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.indirection.NoIndirectionPolicy;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.factories.NamespaceResolvableProject;
import org.eclipse.persistence.internal.sessions.factories.NamespaceResolverWithPrefixes;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ObjectReferenceMapping;
import org.eclipse.persistence.mappings.converters.ClassInstanceConverter;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeDirectCollectionMapping;
import org.eclipse.persistence.mappings.foundation.AbstractCompositeObjectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.ModifyQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sessions.DatasourceLogin;

/**
 * INTERNAL:
 * <p><code>EISObjectPersistenceXMLProject</code> defines the EclipseLink EIS 
 * project and descriptor information to read a EclipseLink project from an XML 
 * file.  The EIS meta-data must be defined separately as it has separate jar 
 * dependencies that must not be required if not using EIS.
 */
public class EISObjectPersistenceXMLProject extends NamespaceResolvableProject {

    /**
     * INTERNAL:
     * Return a new descriptor project.
     */
    public EISObjectPersistenceXMLProject() {
        super();
    }
    
    public EISObjectPersistenceXMLProject(NamespaceResolverWithPrefixes namespaceResolverWithPrefixes) {
        super(namespaceResolverWithPrefixes);
    }

    @Override
    protected void buildDescriptors() {
        addDescriptor(buildEISDescriptorDescriptor());
        addDescriptor(buildXMLInteractionDescriptor());
        addDescriptor(buildEISLoginDescriptor());
        addDescriptor(buildInteractionArgumentDescriptor());
        addDescriptor(buildEISDirectMappingDescriptor());
        addDescriptor(buildEISTransformationMappingDescriptor());
        addDescriptor(buildEISCompositeDirectCollectionMappingDescriptor());
        addDescriptor(buildEISCompositeObjectMappingDescriptor());
        addDescriptor(buildEISCompositeCollectionMappingDescriptor());
        addDescriptor(buildEISOneToOneMappingDescriptor());
        addDescriptor(buildEISOneToManyMappingDescriptor());
    }

    protected ClassDescriptor buildEISDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISDescriptor.class);

        descriptor.getInheritancePolicy().setParentClass(ClassDescriptor.class);

        XMLDirectMapping structureMapping = new XMLDirectMapping();
        structureMapping.setAttributeName("dataTypeName");
        structureMapping.setGetMethodName("getDataTypeName");
        structureMapping.setSetMethodName("setDataTypeName");
        structureMapping.setXPath(getPrimaryNamespaceXPath() + "datatype/text()");
        descriptor.addMapping(structureMapping);

        XMLCompositeObjectMapping namespaceResolverMapping = new XMLCompositeObjectMapping();
        namespaceResolverMapping.setXPath(getPrimaryNamespaceXPath() + "namespace-resolver");
        namespaceResolverMapping.setAttributeName("namespaceResolver");
        namespaceResolverMapping.setGetMethodName("getNamespaceResolver");
        namespaceResolverMapping.setSetMethodName("setNamespaceResolver");
        namespaceResolverMapping.setReferenceClass(NamespaceResolver.class);
        descriptor.addMapping(namespaceResolverMapping);

        return descriptor;
    }

    protected ClassDescriptor buildXMLInteractionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(XMLInteraction.class);
        descriptor.descriptorIsAggregate();
        descriptor.getInheritancePolicy().setParentClass(Call.class);

        XMLDirectMapping functionNameMapping = new XMLDirectMapping();
        functionNameMapping.setAttributeName("functionName");
        functionNameMapping.setGetMethodName("getFunctionName");
        functionNameMapping.setSetMethodName("setFunctionName");
        functionNameMapping.setXPath(getPrimaryNamespaceXPath() + "function-name/text()");
        functionNameMapping.setNullValue("");
        descriptor.addMapping(functionNameMapping);

        XMLDirectMapping inputRecordNameMapping = new XMLDirectMapping();
        inputRecordNameMapping.setAttributeName("inputRecordName");
        inputRecordNameMapping.setGetMethodName("getInputRecordName");
        inputRecordNameMapping.setSetMethodName("setInputRecordName");
        inputRecordNameMapping.setXPath(getPrimaryNamespaceXPath() + "input-record-name/text()");
        inputRecordNameMapping.setNullValue("");
        descriptor.addMapping(inputRecordNameMapping);

        XMLDirectMapping inputRootElementNameMapping = new XMLDirectMapping();
        inputRootElementNameMapping.setAttributeName("inputRootElementName");
        inputRootElementNameMapping.setGetMethodName("getInputRootElementName");
        inputRootElementNameMapping.setSetMethodName("setInputRootElementName");
        inputRootElementNameMapping.setXPath(getPrimaryNamespaceXPath() + "input-root-element-name/text()");
        inputRootElementNameMapping.setNullValue("");
        descriptor.addMapping(inputRootElementNameMapping);

        XMLDirectMapping inputResultPathMapping = new XMLDirectMapping();
        inputResultPathMapping.setAttributeName("inputResultPath");
        inputResultPathMapping.setGetMethodName("getInputResultPath");
        inputResultPathMapping.setSetMethodName("setInputResultPath");
        inputResultPathMapping.setXPath(getPrimaryNamespaceXPath() + "input-result-path/text()");
        inputResultPathMapping.setNullValue("");
        descriptor.addMapping(inputResultPathMapping);

        XMLDirectMapping outputResultPathMapping = new XMLDirectMapping();
        outputResultPathMapping.setAttributeName("outputResultPath");
        outputResultPathMapping.setGetMethodName("getOutputResultPath");
        outputResultPathMapping.setSetMethodName("setOutputResultPath");
        outputResultPathMapping.setXPath(getPrimaryNamespaceXPath() + "output-result-path/text()");
        outputResultPathMapping.setNullValue("");
        descriptor.addMapping(outputResultPathMapping);

        XMLCompositeCollectionMapping argumentsMapping = new XMLCompositeCollectionMapping();

        // Handle translation of argument lists to interaction-arguments.
        argumentsMapping.setAttributeAccessor(new AttributeAccessor() {
                public Object getAttributeValueFromObject(Object object) {
                    XMLInteraction interaction = (XMLInteraction)object;
                    Vector argumentNames = interaction.getArgumentNames();
                    Vector arguments = interaction.getArguments();
                    Vector interactionArguments = new Vector(arguments.size());
                    for (int index = 0; index < arguments.size(); index++) {
                        InteractionArgument interactionArgument = new InteractionArgument();
                        interactionArgument.setArgumentName((String)argumentNames.get(index));
                        Object argument = arguments.get(index);
                        if (argument instanceof DatabaseField) {
                            interactionArgument.setKey(argument);
                        } else {
                            interactionArgument.setValue(argument);
                        }
                        interactionArguments.add(interactionArgument);
                    }
                    return interactionArguments;
                }

                public void setAttributeValueInObject(Object object, Object value) {
                    XMLInteraction interaction = (XMLInteraction)object;
                    Vector interactionArguments = (Vector)value;
                    Vector arguments = new Vector(interactionArguments.size());
                    Vector argumentNames = new Vector(interactionArguments.size());
                    Vector values = new Vector(interactionArguments.size());
                    for (int index = 0; index < interactionArguments.size(); index++) {
                        InteractionArgument interactionArgument = (InteractionArgument)interactionArguments.get(index);
                        if (interactionArgument.getKey() != null) {
                            arguments.add(new DatabaseField((String)interactionArgument.getKey()));
                        }
                        if (interactionArgument.getValue() != null) {
                            values.add(interactionArgument.getValue());
                        }
                        if (interactionArgument.getArgumentName() != null) {
                            argumentNames.add(interactionArgument.getArgumentName());
                        }
                    }
                    if (!arguments.isEmpty()) {
                        interaction.setArguments(arguments);
                    } else if (!values.isEmpty()) {
                        interaction.setArguments(values);
                    }
                    if (!argumentNames.isEmpty()) {
                        interaction.setArgumentNames(argumentNames);
                    }
                }
            });
        argumentsMapping.setAttributeName("arguments");
        argumentsMapping.setXPath(getPrimaryNamespaceXPath() + "input-arguments/" + getPrimaryNamespaceXPath() + "argument");
        argumentsMapping.setReferenceClass(InteractionArgument.class);
        descriptor.addMapping(argumentsMapping);

        XMLCompositeCollectionMapping outputArgumentsMapping = new XMLCompositeCollectionMapping();

        // Handle translation of argument lists to interaction-arguments.
        outputArgumentsMapping.setAttributeAccessor(new AttributeAccessor() {
                public Object getAttributeValueFromObject(Object object) {
                    XMLInteraction interaction = (XMLInteraction)object;
                    Vector arguments = interaction.getOutputArguments();
                    Vector argumentNames = interaction.getOutputArgumentNames();
                    Vector interactionArguments = new Vector(arguments.size());
                    for (int index = 0; index < arguments.size(); index++) {
                        InteractionArgument interactionArgument = new InteractionArgument();
                        interactionArgument.setKey(((DatabaseField)arguments.get(index)).getName());
                        interactionArgument.setArgumentName((String)argumentNames.get(index));
                        interactionArguments.add(interactionArgument);
                    }
                    return interactionArguments;
                }

                public void setAttributeValueInObject(Object object, Object value) {
                    XMLInteraction interaction = (XMLInteraction)object;
                    Vector interactionArguments = (Vector)value;
                    Vector arguments = new Vector(interactionArguments.size());
                    Vector argumentNames = new Vector(interactionArguments.size());
                    for (int index = 0; index < interactionArguments.size(); index++) {
                        InteractionArgument interactionArgument = (InteractionArgument)interactionArguments.get(index);
                        arguments.add(new DatabaseField((String)interactionArgument.getKey()));
                        argumentNames.add(interactionArgument.getArgumentName());
                    }
                    interaction.setOutputArguments(arguments);
                    interaction.setOutputArgumentNames(argumentNames);
                }
            });
        outputArgumentsMapping.setAttributeName("outputArguments");
        outputArgumentsMapping.setXPath(getPrimaryNamespaceXPath() + "output-arguments/" + getPrimaryNamespaceXPath() + "argument");
        outputArgumentsMapping.setReferenceClass(InteractionArgument.class);
        descriptor.addMapping(outputArgumentsMapping);

        return descriptor;
    }

    public ClassDescriptor buildEISLoginDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISLogin.class);

        descriptor.getInheritancePolicy().setParentClass(DatasourceLogin.class);

        XMLDirectMapping connectionSpecClassMapping = new XMLDirectMapping();
        connectionSpecClassMapping.setAttributeName("connectionSpec");
        connectionSpecClassMapping.setGetMethodName("getConnectionSpec");
        connectionSpecClassMapping.setSetMethodName("setConnectionSpec");
        connectionSpecClassMapping.setConverter(new ClassInstanceConverter());
        connectionSpecClassMapping.setXPath(getPrimaryNamespaceXPath() + "connection-spec-class/text()");
        descriptor.addMapping(connectionSpecClassMapping);

        XMLDirectMapping connectionFactoryURLMapping = new XMLDirectMapping();
        connectionFactoryURLMapping.setAttributeName("connectionFactoryURL");
        connectionFactoryURLMapping.setGetMethodName("getConnectionFactoryURL");
        connectionFactoryURLMapping.setSetMethodName("setConnectionFactoryURL");
        connectionFactoryURLMapping.setXPath(getPrimaryNamespaceXPath() + "connection-factory-url/text()");
        descriptor.addMapping(connectionFactoryURLMapping);

        return descriptor;
    }

    protected ClassDescriptor buildInteractionArgumentDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(InteractionArgument.class);
        descriptor.setDefaultRootElement("interaction-argument");

        XMLDirectMapping argumentNameMapping = new XMLDirectMapping();
        argumentNameMapping.setAttributeName("argumentName");
        argumentNameMapping.setGetMethodName("getArgumentName");
        argumentNameMapping.setSetMethodName("setArgumentName");
        argumentNameMapping.setXPath("@name");
        descriptor.addMapping(argumentNameMapping);

        XMLDirectMapping keyMapping = new XMLDirectMapping();
        keyMapping.setAttributeName("key");
        keyMapping.setGetMethodName("getKey");
        keyMapping.setSetMethodName("setKey");
        keyMapping.setXPath("@argument-name");
        descriptor.addMapping(keyMapping);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setGetMethodName("getValue");
        valueMapping.setSetMethodName("setValue");
        valueMapping.setField(buildTypedField(getPrimaryNamespaceXPath() + "argument-value/text()"));
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

    protected ClassDescriptor buildEISDirectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISDirectMapping.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractDirectMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildEISTransformationMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISTransformationMapping.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractTransformationMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildEISCompositeDirectCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISCompositeDirectCollectionMapping.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeDirectCollectionMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildEISCompositeObjectMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISCompositeObjectMapping.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeObjectMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildEISCompositeCollectionMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISCompositeCollectionMapping.class);
        descriptor.getInheritancePolicy().setParentClass(AbstractCompositeCollectionMapping.class);

        return descriptor;
    }

    protected ClassDescriptor buildEISOneToOneMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISOneToOneMapping.class);

        descriptor.getInheritancePolicy().setParentClass(ObjectReferenceMapping.class);

        XMLCompositeCollectionMapping sourceToTargetKeyFieldAssociationsMapping = new XMLCompositeCollectionMapping();
        sourceToTargetKeyFieldAssociationsMapping.setReferenceClass(Association.class);
        // Handle translation of foreign key associations to hashtables.
        sourceToTargetKeyFieldAssociationsMapping.setAttributeAccessor(new AttributeAccessor() {
                public Object getAttributeValueFromObject(Object object) {
                    Map sourceToTargetKeyFields = ((EISOneToOneMapping)object).getSourceToTargetKeyFields();
                    List associations = new ArrayList(sourceToTargetKeyFields.size());
                    Iterator iterator = sourceToTargetKeyFields.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry)iterator.next();
                        associations.add(new Association(entry.getKey(), entry.getValue()));
                    }
                    return associations;
                }

                public void setAttributeValueInObject(Object object, Object value) {
                    EISOneToOneMapping mapping = (EISOneToOneMapping)object;
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
        sourceToTargetKeyFieldAssociationsMapping.setXPath(getPrimaryNamespaceXPath() + "foreign-key/" + getPrimaryNamespaceXPath() + "field-reference");
        descriptor.addMapping(sourceToTargetKeyFieldAssociationsMapping);

        XMLCompositeCollectionMapping foreignKeyFieldNamesMapping = new XMLCompositeCollectionMapping();
        foreignKeyFieldNamesMapping.setAttributeName("foreignKeyFields");
        foreignKeyFieldNamesMapping.setGetMethodName("getForeignKeyFields");
        foreignKeyFieldNamesMapping.setSetMethodName("setForeignKeyFields");
        foreignKeyFieldNamesMapping.setXPath(getPrimaryNamespaceXPath() + "foreign-key-fields/" + getPrimaryNamespaceXPath() + "field");
        foreignKeyFieldNamesMapping.setReferenceClass(DatabaseField.class);
        descriptor.addMapping(foreignKeyFieldNamesMapping);

        XMLDirectMapping relationshipPartnerAttributeNameMapping = new XMLDirectMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setXPath(getPrimaryNamespaceXPath() + "bidirectional-target-attribute/text()");
        descriptor.addMapping(relationshipPartnerAttributeNameMapping);

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
        selectionQueryMapping.setReferenceClass(ReadQuery.class);
        selectionQueryMapping.setAttributeAccessor(new AttributeAccessor() {
                public Object getAttributeValueFromObject(Object object) {
                    if (((ForeignReferenceMapping)object).hasCustomSelectionQuery()) {
                        return ((ForeignReferenceMapping)object).getSelectionQuery();
                    }
                    return null;
                }

                public void setAttributeValueInObject(Object object, Object value) {
                    if ((value != null) && value instanceof ReadQuery) {
                        ((ForeignReferenceMapping)object).setCustomSelectionQuery((ReadQuery)value);
                    }
                }
            });
        selectionQueryMapping.setXPath(getPrimaryNamespaceXPath() + "selection-query");
        descriptor.addMapping(selectionQueryMapping);

        return descriptor;
    }

    protected ClassDescriptor buildEISOneToManyMappingDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISOneToManyMapping.class);

        XMLCompositeCollectionMapping foreignKeyFieldNamesMapping = new XMLCompositeCollectionMapping();
        foreignKeyFieldNamesMapping.setAttributeName("sourceForeignKeyFields");
        foreignKeyFieldNamesMapping.useCollectionClass(java.util.ArrayList.class);
        foreignKeyFieldNamesMapping.setGetMethodName("getSourceForeignKeyFields");
        foreignKeyFieldNamesMapping.setSetMethodName("setSourceForeignKeyFields");
        foreignKeyFieldNamesMapping.setXPath(getPrimaryNamespaceXPath() + "source-foreign-key-fields/" + getPrimaryNamespaceXPath() + "field");
        foreignKeyFieldNamesMapping.setReferenceClass(DatabaseField.class);
        descriptor.addMapping(foreignKeyFieldNamesMapping);

        XMLCompositeCollectionMapping targetForeignKeyFieldNamesMapping = new XMLCompositeCollectionMapping();
        targetForeignKeyFieldNamesMapping.setAttributeName("targetForeignKeyFields");
        targetForeignKeyFieldNamesMapping.useCollectionClass(java.util.ArrayList.class);
        targetForeignKeyFieldNamesMapping.setGetMethodName("getTargetForeignKeyFields");
        targetForeignKeyFieldNamesMapping.setSetMethodName("setTargetForeignKeyFields");
        targetForeignKeyFieldNamesMapping.setXPath(getPrimaryNamespaceXPath() + "target-foreign-key-fields/" + getPrimaryNamespaceXPath() + "field");
        targetForeignKeyFieldNamesMapping.setReferenceClass(DatabaseField.class);
        descriptor.addMapping(targetForeignKeyFieldNamesMapping);

        XMLCompositeObjectMapping foreignKeyGroupingElementMapping = new XMLCompositeObjectMapping();
        foreignKeyGroupingElementMapping.setAttributeName("field");
        foreignKeyGroupingElementMapping.setReferenceClass(DatabaseField.class);
        foreignKeyGroupingElementMapping.setGetMethodName("getForeignKeyGroupingElement");
        foreignKeyGroupingElementMapping.setSetMethodName("setForeignKeyGroupingElement");
        foreignKeyGroupingElementMapping.setXPath(getPrimaryNamespaceXPath() + "foreign-key-grouping-element");
        descriptor.addMapping(foreignKeyGroupingElementMapping);

        XMLDirectMapping relationshipPartnerAttributeNameMapping = new XMLDirectMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setXPath(getPrimaryNamespaceXPath() + "bidirectional-target-attribute/text()");
        descriptor.addMapping(relationshipPartnerAttributeNameMapping);

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
        selectionQueryMapping.setReferenceClass(ReadQuery.class);
        selectionQueryMapping.setAttributeAccessor(new AttributeAccessor() {
                public Object getAttributeValueFromObject(Object object) {
                    if (((ForeignReferenceMapping)object).hasCustomSelectionQuery()) {
                        return ((ForeignReferenceMapping)object).getSelectionQuery();
                    }
                    return null;
                }

                public void setAttributeValueInObject(Object object, Object value) {
                    if ((value != null) && value instanceof ReadQuery) {
                        ((ForeignReferenceMapping)object).setCustomSelectionQuery((ReadQuery)value);
                    }
                }
            });

        selectionQueryMapping.setXPath(getPrimaryNamespaceXPath() + "selection-query");
        descriptor.addMapping(selectionQueryMapping);

        // delete-all query
        XMLCompositeObjectMapping deleteAllQueryMapping = new XMLCompositeObjectMapping();
        deleteAllQueryMapping.setAttributeName("deleteAllQuery");
        deleteAllQueryMapping.setReferenceClass(ModifyQuery.class);
        deleteAllQueryMapping.setAttributeAccessor(new AttributeAccessor() {
                public Object getAttributeValueFromObject(Object object) {
                    boolean hasCustomDeleteAllQuery = ((EISOneToManyMapping)object).hasCustomDeleteAllQuery();
                    if (hasCustomDeleteAllQuery) {
                        return ((EISOneToManyMapping)object).getDeleteAllQuery();
                    } else {
                        return null;
                    }
                }

                public void setAttributeValueInObject(Object object, Object value) {
                    if ((value != null) && value instanceof ModifyQuery) {
                        ((EISOneToManyMapping)object).setCustomDeleteAllQuery((ModifyQuery)value);
                    }
                }
            });
        deleteAllQueryMapping.setXPath(getPrimaryNamespaceXPath() + "delete-all-query");
        descriptor.addMapping(deleteAllQueryMapping);

        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

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
}
