/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.eis;

import java.util.*;
import javax.xml.namespace.QName;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.eis.mappings.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.indirection.IndirectionPolicy;
import org.eclipse.persistence.internal.indirection.NoIndirectionPolicy;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.*;
import org.eclipse.persistence.mappings.foundation.*;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.*;

/**
 * INTERNAL:
 * <p><code>EISObjectPersistenceXMLProject</code> defines the EclipseLink EIS 
 * project and descriptor information to read a EclipseLink project from an XML 
 * file.  The EIS meta-data must be defined separately as it has separate jar 
 * dependencies that must not be required if not using EIS.
 */
public class EISObjectPersistenceXMLProject extends Project {

    /**
     * INTERNAL:
     * Return a new descriptor project.
     */
    public EISObjectPersistenceXMLProject() {
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

        // Set the namespaces on all descriptors.
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaceResolver.put("xsd", "http://www.w3.org/2001/XMLSchema");
        namespaceResolver.put("eclipselink", "http://xmlns.oracle.com/ias/xsds/eclipselink");

        for (Iterator descriptors = getDescriptors().values().iterator(); descriptors.hasNext();) {
            XMLDescriptor descriptor = (XMLDescriptor)descriptors.next();
            descriptor.setNamespaceResolver(namespaceResolver);
        }
    }

    protected ClassDescriptor buildEISDescriptorDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EISDescriptor.class);

        descriptor.getInheritancePolicy().setParentClass(ClassDescriptor.class);

        XMLDirectMapping structureMapping = new XMLDirectMapping();
        structureMapping.setAttributeName("dataTypeName");
        structureMapping.setGetMethodName("getDataTypeName");
        structureMapping.setSetMethodName("setDataTypeName");
        structureMapping.setXPath("eclipselink:datatype/text()");
        descriptor.addMapping(structureMapping);

        XMLCompositeObjectMapping namespaceResolverMapping = new XMLCompositeObjectMapping();
        namespaceResolverMapping.setXPath("eclipselink:namespace-resolver");
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
        functionNameMapping.setXPath("eclipselink:function-name/text()");
        functionNameMapping.setNullValue("");
        descriptor.addMapping(functionNameMapping);

        XMLDirectMapping inputRecordNameMapping = new XMLDirectMapping();
        inputRecordNameMapping.setAttributeName("inputRecordName");
        inputRecordNameMapping.setGetMethodName("getInputRecordName");
        inputRecordNameMapping.setSetMethodName("setInputRecordName");
        inputRecordNameMapping.setXPath("eclipselink:input-record-name/text()");
        inputRecordNameMapping.setNullValue("");
        descriptor.addMapping(inputRecordNameMapping);

        XMLDirectMapping inputRootElementNameMapping = new XMLDirectMapping();
        inputRootElementNameMapping.setAttributeName("inputRootElementName");
        inputRootElementNameMapping.setGetMethodName("getInputRootElementName");
        inputRootElementNameMapping.setSetMethodName("setInputRootElementName");
        inputRootElementNameMapping.setXPath("eclipselink:input-root-element-name/text()");
        inputRootElementNameMapping.setNullValue("");
        descriptor.addMapping(inputRootElementNameMapping);

        XMLDirectMapping inputResultPathMapping = new XMLDirectMapping();
        inputResultPathMapping.setAttributeName("inputResultPath");
        inputResultPathMapping.setGetMethodName("getInputResultPath");
        inputResultPathMapping.setSetMethodName("setInputResultPath");
        inputResultPathMapping.setXPath("eclipselink:input-result-path/text()");
        inputResultPathMapping.setNullValue("");
        descriptor.addMapping(inputResultPathMapping);

        XMLDirectMapping outputResultPathMapping = new XMLDirectMapping();
        outputResultPathMapping.setAttributeName("outputResultPath");
        outputResultPathMapping.setGetMethodName("getOutputResultPath");
        outputResultPathMapping.setSetMethodName("setOutputResultPath");
        outputResultPathMapping.setXPath("eclipselink:output-result-path/text()");
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
        argumentsMapping.setXPath("eclipselink:input-arguments/eclipselink:argument");
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
        outputArgumentsMapping.setXPath("eclipselink:output-arguments/eclipselink:argument");
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
        connectionSpecClassMapping.setXPath("eclipselink:connection-spec-class/text()");
        descriptor.addMapping(connectionSpecClassMapping);

        XMLDirectMapping connectionFactoryURLMapping = new XMLDirectMapping();
        connectionFactoryURLMapping.setAttributeName("connectionFactoryURL");
        connectionFactoryURLMapping.setGetMethodName("getConnectionFactoryURL");
        connectionFactoryURLMapping.setSetMethodName("setConnectionFactoryURL");
        connectionFactoryURLMapping.setXPath("eclipselink:connection-factory-url/text()");
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
        valueMapping.setField(buildTypedField("eclipselink:argument-value/text()"));
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
                        mapping.getSourceToTargetKeyFields().put(association.getKey(), association.getValue());
                        mapping.getTargetToSourceKeyFields().put(association.getValue(), association.getKey());
                    }
                }
            });
        sourceToTargetKeyFieldAssociationsMapping.setAttributeName("sourceToTargetKeyFieldAssociations");
        sourceToTargetKeyFieldAssociationsMapping.setXPath("eclipselink:foreign-key/eclipselink:field-reference");
        descriptor.addMapping(sourceToTargetKeyFieldAssociationsMapping);

        XMLCompositeCollectionMapping foreignKeyFieldNamesMapping = new XMLCompositeCollectionMapping();
        foreignKeyFieldNamesMapping.setAttributeName("foreignKeyFields");
        foreignKeyFieldNamesMapping.setGetMethodName("getForeignKeyFields");
        foreignKeyFieldNamesMapping.setSetMethodName("setForeignKeyFields");
        foreignKeyFieldNamesMapping.setXPath("eclipselink:foreign-key-fields/eclipselink:field");
        foreignKeyFieldNamesMapping.setReferenceClass(DatabaseField.class);
        descriptor.addMapping(foreignKeyFieldNamesMapping);

        XMLDirectMapping relationshipPartnerAttributeNameMapping = new XMLDirectMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setXPath("eclipselink:bidirectional-target-attribute/text()");
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
        indirectionPolicyMapping.setXPath("eclipselink:indirection");
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
        selectionQueryMapping.setXPath("eclipselink:selection-query");
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
        foreignKeyFieldNamesMapping.setXPath("eclipselink:source-foreign-key-fields/eclipselink:field");
        foreignKeyFieldNamesMapping.setReferenceClass(DatabaseField.class);
        descriptor.addMapping(foreignKeyFieldNamesMapping);

        XMLCompositeCollectionMapping targetForeignKeyFieldNamesMapping = new XMLCompositeCollectionMapping();
        targetForeignKeyFieldNamesMapping.setAttributeName("targetForeignKeyFields");
        targetForeignKeyFieldNamesMapping.useCollectionClass(java.util.ArrayList.class);
        targetForeignKeyFieldNamesMapping.setGetMethodName("getTargetForeignKeyFields");
        targetForeignKeyFieldNamesMapping.setSetMethodName("setTargetForeignKeyFields");
        targetForeignKeyFieldNamesMapping.setXPath("eclipselink:target-foreign-key-fields/eclipselink:field");
        targetForeignKeyFieldNamesMapping.setReferenceClass(DatabaseField.class);
        descriptor.addMapping(targetForeignKeyFieldNamesMapping);

        XMLCompositeObjectMapping foreignKeyGroupingElementMapping = new XMLCompositeObjectMapping();
        foreignKeyGroupingElementMapping.setAttributeName("field");
        foreignKeyGroupingElementMapping.setReferenceClass(DatabaseField.class);
        foreignKeyGroupingElementMapping.setGetMethodName("getForeignKeyGroupingElement");
        foreignKeyGroupingElementMapping.setSetMethodName("setForeignKeyGroupingElement");
        foreignKeyGroupingElementMapping.setXPath("eclipselink:foreign-key-grouping-element");
        descriptor.addMapping(foreignKeyGroupingElementMapping);

        XMLDirectMapping relationshipPartnerAttributeNameMapping = new XMLDirectMapping();
        relationshipPartnerAttributeNameMapping.setAttributeName("relationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setGetMethodName("getRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setSetMethodName("setRelationshipPartnerAttributeName");
        relationshipPartnerAttributeNameMapping.setXPath("eclipselink:bidirectional-target-attribute/text()");
        descriptor.addMapping(relationshipPartnerAttributeNameMapping);

        XMLCompositeObjectMapping containerPolicyMapping = new XMLCompositeObjectMapping();
        containerPolicyMapping.setAttributeName("collectionPolicy");
        containerPolicyMapping.setGetMethodName("getContainerPolicy");
        containerPolicyMapping.setSetMethodName("setContainerPolicy");
        containerPolicyMapping.setReferenceClass(org.eclipse.persistence.internal.queries.ContainerPolicy.class);
        containerPolicyMapping.setXPath("eclipselink:container");
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
        indirectionPolicyMapping.setXPath("eclipselink:indirection");
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

        selectionQueryMapping.setXPath("eclipselink:selection-query");
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
        deleteAllQueryMapping.setXPath("eclipselink:delete-all-query");
        descriptor.addMapping(deleteAllQueryMapping);

        descriptor.getInheritancePolicy().setParentClass(CollectionMapping.class);

        return descriptor;
    }

    protected XMLField buildTypedField(String fieldName) {
        XMLField field = new XMLField(fieldName);
        field.setIsTypedTextField(true);
        field.addConversion(new QName(XMLConstants.SCHEMA_URL, XMLConstants.TIME), java.sql.Time.class);
        field.addConversion(new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE), java.sql.Date.class);
        field.addConversion(new QName("http://xmlns.oracle.com/ias/xsds/eclipselink", "java-character"), Character.class);
        field.addConversion(new QName("http://xmlns.oracle.com/ias/xsds/eclipselink", "java-util-date"), java.util.Date.class);
        field.addConversion(new QName("http://xmlns.oracle.com/ias/xsds/eclipselink", "java-timestamp"), java.sql.Timestamp.class);
        return field;
    }
}
