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
package org.eclipse.persistence.internal.oxm.schema;

import java.util.ArrayList;

import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.schema.model.*;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.oxm.mappings.nullpolicy.NullPolicy;
import org.eclipse.persistence.sessions.Project;

/**
 * <p><b>Purpose</b>: TopLink OX project to map org.eclipse.persistence.internal.schema.model.*
 * Used by TopLink SDO and JAXB implementations
 */
public class SchemaModelProject extends Project {
    private NamespaceResolver namespaceResolver;

    public SchemaModelProject() {
        namespaceResolver = new NamespaceResolver();
        namespaceResolver.put(Constants.SCHEMA_PREFIX, "http://www.w3.org/2001/XMLSchema");

        addDescriptor(buildSchemaDescriptor());
        addDescriptor(buildAttributeGroupDescriptor());
        addDescriptor(buildComplexTypeDescriptor());
        addDescriptor(buildComplexContentDescriptor());
        addDescriptor(buildSimpleTypeDescriptor());
        addDescriptor(buildListDescriptor());
        addDescriptor(buildUnionDescriptor());
        addDescriptor(buildSimpleContentDescriptor());
        addDescriptor(buildElementDescriptor());
        addDescriptor(buildChoiceDescriptor());
        addDescriptor(buildSequenceDescriptor());
        addDescriptor(buildAllDescriptor());
        addDescriptor(buildAnyDescriptor());
        addDescriptor(buildAnyAttributeDescriptor());
        addDescriptor(buildAttributeDescriptor());
        addDescriptor(buildRestrictionDescriptor());
        addDescriptor(buildExtensionDescriptor());
        addDescriptor(buildImportDescriptor());
        addDescriptor(buildIncludeDescriptor());
        addDescriptor(buildAnnotationDescriptor());
        addDescriptor(buildGroupDescriptor());
        //addDescriptor(buildTypeDefParticleDescriptor());
        //addDescriptor(buildOccursDescriptor());
    }

    private XMLDescriptor buildSchemaDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Schema.class);
        descriptor.setDefaultRootElement(Constants.SCHEMA_PREFIX + ":" + "schema");
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLCompositeCollectionMapping importsMapping = new XMLCompositeCollectionMapping();
        importsMapping.setReferenceClass(Import.class);
        importsMapping.setAttributeName("imports");
        importsMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "import");
        descriptor.addMapping(importsMapping);

        XMLCompositeCollectionMapping includesMapping = new XMLCompositeCollectionMapping();
        includesMapping.setReferenceClass(Include.class);
        includesMapping.setAttributeName("includes");
        includesMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "include");
        descriptor.addMapping(includesMapping);

        XMLDirectMapping targetNamespaceMapping = new XMLDirectMapping();
        targetNamespaceMapping.setAttributeName("targetNamespace");
        targetNamespaceMapping.setXPath("@targetNamespace");
        descriptor.addMapping(targetNamespaceMapping);

        XMLDirectMapping defaultNamespaceMapping = new XMLDirectMapping();
        defaultNamespaceMapping.setAttributeName("defaultNamespace");
        XMLField xmlField = new XMLField();
        xmlField.setXPath("@xmlns");
        xmlField.getXPathFragment().setNamespaceURI(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        defaultNamespaceMapping.setField(xmlField);
        descriptor.addMapping(defaultNamespaceMapping);

        XMLAnyAttributeMapping attributesMapMapping = new XMLAnyAttributeMapping();
        attributesMapMapping.setAttributeName("attributesMap");
        attributesMapMapping.setGetMethodName("getAttributesMap");
        attributesMapMapping.setSetMethodName("setAttributesMap");
        descriptor.addMapping(attributesMapMapping);

        XMLCompositeCollectionMapping attributeGroupsMapping = new XMLCompositeCollectionMapping();
        attributeGroupsMapping.setReferenceClass(AttributeGroup.class);
        attributeGroupsMapping.setAttributeName("attributeGroups");
        attributeGroupsMapping.useMapClass(java.util.HashMap.class, "getName");
        attributeGroupsMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "attributeGroup");
        descriptor.addMapping(attributeGroupsMapping);

        XMLCompositeCollectionMapping groupsMapping = new XMLCompositeCollectionMapping();
        groupsMapping.setReferenceClass(Group.class);
        groupsMapping.setAttributeName("groups");
        groupsMapping.useMapClass(java.util.HashMap.class, "getName");
        groupsMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "group");
        descriptor.addMapping(groupsMapping);

        ObjectTypeConverter converter = new ObjectTypeConverter();
        converter.addConversionValue("unqualified", false);
        converter.addConversionValue("qualified", true);
        converter.setFieldClassification(String.class);

        XMLDirectMapping elementFormDefaultMapping = new XMLDirectMapping();
        elementFormDefaultMapping.setAttributeName("elementFormDefault");
        elementFormDefaultMapping.setXPath("@elementFormDefault");
        elementFormDefaultMapping.setNullValue(false);
        elementFormDefaultMapping.setConverter(converter);
        descriptor.addMapping(elementFormDefaultMapping);

        XMLDirectMapping attributeFormDefaultMapping = new XMLDirectMapping();
        attributeFormDefaultMapping.setAttributeName("attributeFormDefault");
        attributeFormDefaultMapping.setXPath("@attributeFormDefault");
        attributeFormDefaultMapping.setNullValue(false);
        attributeFormDefaultMapping.setConverter(converter);
        descriptor.addMapping(attributeFormDefaultMapping);

        XMLCompositeCollectionMapping topLevelComplexTypesMapping = new XMLCompositeCollectionMapping();
        topLevelComplexTypesMapping.setReferenceClass(ComplexType.class);
        topLevelComplexTypesMapping.setAttributeName("topLevelComplexTypes");
        topLevelComplexTypesMapping.useMapClass(java.util.HashMap.class, "getName");
        topLevelComplexTypesMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "complexType");
        descriptor.addMapping(topLevelComplexTypesMapping);

        XMLCompositeCollectionMapping topLevelElementsMapping = new XMLCompositeCollectionMapping();
        topLevelElementsMapping.setReferenceClass(Element.class);
        topLevelElementsMapping.setAttributeName("topLevelElements");
        topLevelElementsMapping.useMapClass(java.util.HashMap.class, "getName");
        topLevelElementsMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "element");
        descriptor.addMapping(topLevelElementsMapping);

        XMLCompositeCollectionMapping topLevelSimpleTypesMapping = new XMLCompositeCollectionMapping();
        topLevelSimpleTypesMapping.setReferenceClass(SimpleType.class);
        topLevelSimpleTypesMapping.setAttributeName("topLevelSimpleTypes");
        topLevelSimpleTypesMapping.useMapClass(java.util.HashMap.class, "getName");
        topLevelSimpleTypesMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "simpleType");
        descriptor.addMapping(topLevelSimpleTypesMapping);

        XMLCompositeCollectionMapping topLevelAttributesMapping = new XMLCompositeCollectionMapping();
        topLevelAttributesMapping.setReferenceClass(Attribute.class);
        topLevelAttributesMapping.setAttributeName("topLevelAttributes");
        topLevelAttributesMapping.useMapClass(java.util.HashMap.class, "getName");
        topLevelAttributesMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "attribute");
        descriptor.addMapping(topLevelAttributesMapping);

              XMLCompositeObjectMapping annotationMapping = new XMLCompositeObjectMapping();
        annotationMapping.setReferenceClass(Annotation.class);
        annotationMapping.setAttributeName("annotation");
        annotationMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "annotation");
        descriptor.addMapping(annotationMapping);

        descriptor.setNamespaceResolver(namespaceResolver);
        return descriptor;
    }

    private XMLDescriptor buildComplexTypeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ComplexType.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping abstractValueMapping = new XMLDirectMapping();
        abstractValueMapping.setAttributeName("abstractValue");
        abstractValueMapping.setXPath("@abstract");
        abstractValueMapping.setNullValue(false);
        descriptor.addMapping(abstractValueMapping);

        XMLDirectMapping mixedMapping = new XMLDirectMapping();
        mixedMapping.setAttributeName("mixed");
        mixedMapping.setXPath("@mixed");
        mixedMapping.setNullValue(false);
        descriptor.addMapping(mixedMapping);

        XMLCompositeObjectMapping annotationMapping = new XMLCompositeObjectMapping();
        annotationMapping.setReferenceClass(Annotation.class);
        annotationMapping.setAttributeName("annotation");
        annotationMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "annotation");
        descriptor.addMapping(annotationMapping);

        XMLCompositeObjectMapping sequenceMapping = new XMLCompositeObjectMapping();
        sequenceMapping.setReferenceClass(Sequence.class);
        sequenceMapping.setAttributeName("sequence");
        sequenceMapping.setSetMethodName("setSequence");
        sequenceMapping.setGetMethodName("getSequence");
        sequenceMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "sequence");
        descriptor.addMapping(sequenceMapping);

        XMLCompositeObjectMapping choiceMapping = new XMLCompositeObjectMapping();
        choiceMapping.setReferenceClass(Choice.class);
        choiceMapping.setAttributeName("choice");
        choiceMapping.setSetMethodName("setChoice");
        choiceMapping.setGetMethodName("getChoice");
        choiceMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "choice");
        descriptor.addMapping(choiceMapping);

        XMLCompositeObjectMapping allMapping = new XMLCompositeObjectMapping();
        allMapping.setReferenceClass(All.class);
        allMapping.setAttributeName("all");
        allMapping.setSetMethodName("setAll");
        allMapping.setGetMethodName("getAll");
        allMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "all");
        descriptor.addMapping(allMapping);

        XMLCompositeObjectMapping complextContentMapping = new XMLCompositeObjectMapping();

        complextContentMapping.setReferenceClass(ComplexContent.class);
        complextContentMapping.setAttributeName("complexContent");
        complextContentMapping.setGetMethodName("getComplexContent");
        complextContentMapping.setSetMethodName("setComplexContent");
        complextContentMapping.setXPath(Constants.SCHEMA_PREFIX + ":complexContent");
        descriptor.addMapping(complextContentMapping);

        XMLCompositeObjectMapping simpleContentMapping = new XMLCompositeObjectMapping();
        simpleContentMapping.setReferenceClass(SimpleContent.class);
        simpleContentMapping.setAttributeName("simpleContent");
        simpleContentMapping.setGetMethodName("getSimpleContent");
        simpleContentMapping.setSetMethodName("setSimpleContent");
        simpleContentMapping.setXPath(Constants.SCHEMA_PREFIX + ":simpleContent");
        descriptor.addMapping(simpleContentMapping);

        XMLAnyCollectionMapping orderedAttributesMapping = new XMLAnyCollectionMapping();
        orderedAttributesMapping.setAttributeName("orderedAttributes");
        descriptor.addMapping(orderedAttributesMapping);

        XMLCompositeObjectMapping anyAttributeMapping = new XMLCompositeObjectMapping();
        anyAttributeMapping.setReferenceClass(AnyAttribute.class);
        anyAttributeMapping.setAttributeName("anyAttribute");
        anyAttributeMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "anyAttribute");
        descriptor.addMapping(anyAttributeMapping);
        
        XMLAnyAttributeMapping attributesMapMapping = new XMLAnyAttributeMapping();
        attributesMapMapping.setAttributeName("attributesMap");
        attributesMapMapping.setGetMethodName("getAttributesMap");
        attributesMapMapping.setSetMethodName("setAttributesMap");
        descriptor.addMapping(attributesMapMapping);

        return descriptor;
    }

    private XMLDescriptor buildComplexContentDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ComplexContent.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping mixedMapping = new XMLDirectMapping();
        mixedMapping.setAttributeName("mixed");
        mixedMapping.setXPath("@mixed");
        mixedMapping.setNullValue(false);
        descriptor.addMapping(mixedMapping);

        XMLCompositeObjectMapping restrictionMapping = new XMLCompositeObjectMapping();
        restrictionMapping.setAttributeName("restriction");
        restrictionMapping.setReferenceClass(Restriction.class);
        restrictionMapping.setGetMethodName("getRestriction");
        restrictionMapping.setSetMethodName("setRestriction");
        restrictionMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "restriction");
        descriptor.addMapping(restrictionMapping);

        XMLCompositeObjectMapping extensionMapping = new XMLCompositeObjectMapping();
        extensionMapping.setAttributeName("extension");
        extensionMapping.setReferenceClass(Extension.class);
        extensionMapping.setGetMethodName("getExtension");
        extensionMapping.setSetMethodName("setExtension");
        extensionMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "extension");
        descriptor.addMapping(extensionMapping);
        return descriptor;
    }

    private XMLDescriptor buildSimpleContentDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SimpleContent.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLCompositeObjectMapping restrictionMapping = new XMLCompositeObjectMapping();
        restrictionMapping.setAttributeName("restriction");
        restrictionMapping.setReferenceClass(Restriction.class);
        restrictionMapping.setGetMethodName("getRestriction");
        restrictionMapping.setSetMethodName("setRestriction");
        restrictionMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "restriction");
        descriptor.addMapping(restrictionMapping);

        XMLCompositeObjectMapping extensionMapping = new XMLCompositeObjectMapping();
        extensionMapping.setAttributeName("extension");
        extensionMapping.setReferenceClass(Extension.class);
        extensionMapping.setGetMethodName("getExtension");
        extensionMapping.setSetMethodName("setExtension");
        extensionMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "extension");
        descriptor.addMapping(extensionMapping);

        return descriptor;
    }

    private XMLDescriptor buildSimpleTypeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(SimpleType.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLCompositeObjectMapping annotationMapping = new XMLCompositeObjectMapping();
        annotationMapping.setReferenceClass(Annotation.class);
        annotationMapping.setAttributeName("annotation");
        annotationMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "annotation");
        descriptor.addMapping(annotationMapping);

        XMLCompositeObjectMapping restrictionMapping = new XMLCompositeObjectMapping();
        restrictionMapping.setAttributeName("restriction");
        restrictionMapping.setReferenceClass(Restriction.class);
        restrictionMapping.setGetMethodName("getRestriction");
        restrictionMapping.setSetMethodName("setRestriction");
        restrictionMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "restriction");
        descriptor.addMapping(restrictionMapping);

        XMLCompositeObjectMapping listMapping = new XMLCompositeObjectMapping();
        listMapping.setAttributeName("list");
        listMapping.setReferenceClass(org.eclipse.persistence.internal.oxm.schema.model.List.class);
        listMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "list");
        descriptor.addMapping(listMapping);

        XMLCompositeObjectMapping unionMapping = new XMLCompositeObjectMapping();
        unionMapping.setAttributeName("union");
        unionMapping.setReferenceClass(Union.class);
        unionMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "union");
        descriptor.addMapping(unionMapping);

        XMLAnyAttributeMapping attributesMapMapping = new XMLAnyAttributeMapping();
        attributesMapMapping.setAttributeName("attributesMap");
        attributesMapMapping.setGetMethodName("getAttributesMap");
        attributesMapMapping.setSetMethodName("setAttributesMap");
        descriptor.addMapping(attributesMapMapping);

        return descriptor;
    }

    private XMLDescriptor buildListDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.internal.oxm.schema.model.List.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping itemTypeMapping = new XMLDirectMapping();
        itemTypeMapping.setAttributeName("itemType");
        itemTypeMapping.setXPath("@itemType");
        descriptor.addMapping(itemTypeMapping);

        XMLCompositeObjectMapping simpleTypeMapping = new XMLCompositeObjectMapping();
        simpleTypeMapping.setReferenceClass(SimpleType.class);
        simpleTypeMapping.setAttributeName("simpleType");
        simpleTypeMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "simpleType");
        descriptor.addMapping(simpleTypeMapping);

        return descriptor;
    }

    private XMLDescriptor buildUnionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Union.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLCompositeDirectCollectionMapping memberTypeMapping = new XMLCompositeDirectCollectionMapping();
        memberTypeMapping.setAttributeName("memberTypes");
        memberTypeMapping.setXPath("@memberTypes");
        memberTypeMapping.setUsesSingleNode(true);
        descriptor.addMapping(memberTypeMapping);

        XMLCompositeCollectionMapping simpleTypesMapping = new XMLCompositeCollectionMapping();
        simpleTypesMapping.setReferenceClass(SimpleType.class);
        simpleTypesMapping.setAttributeName("simpleTypes");
        simpleTypesMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "simpleType");
        descriptor.addMapping(simpleTypesMapping);

        return descriptor;
    }

    private XMLDescriptor buildRestrictionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Restriction.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping baseTypeMapping = new XMLDirectMapping();
        baseTypeMapping.setAttributeName("baseType");
        baseTypeMapping.setXPath("@base");
        descriptor.addMapping(baseTypeMapping);

        XMLCompositeObjectMapping sequenceMapping = new XMLCompositeObjectMapping();
        sequenceMapping.setReferenceClass(Sequence.class);
        sequenceMapping.setAttributeName("sequence");
        sequenceMapping.setSetMethodName("setSequence");
        sequenceMapping.setGetMethodName("getSequence");
        sequenceMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "sequence");
        descriptor.addMapping(sequenceMapping);

        XMLCompositeObjectMapping choiceMapping = new XMLCompositeObjectMapping();
        choiceMapping.setReferenceClass(Choice.class);
        choiceMapping.setAttributeName("choice");
        choiceMapping.setSetMethodName("setChoice");
        choiceMapping.setGetMethodName("getChoice");
        choiceMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "choice");
        descriptor.addMapping(choiceMapping);

        XMLCompositeObjectMapping allMapping = new XMLCompositeObjectMapping();
        allMapping.setReferenceClass(All.class);
        allMapping.setAttributeName("all");
        allMapping.setSetMethodName("setAll");
        allMapping.setGetMethodName("getAll");
        allMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "all");
        descriptor.addMapping(allMapping);

        XMLCompositeObjectMapping simpleTypeMapping = new XMLCompositeObjectMapping();
        simpleTypeMapping.setReferenceClass(SimpleType.class);
        simpleTypeMapping.setAttributeName("simpleType");
        simpleTypeMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "simpleType");
        descriptor.addMapping(simpleTypeMapping);

        XMLCompositeCollectionMapping attributesMapping = new XMLCompositeCollectionMapping();
        attributesMapping.setReferenceClass(Attribute.class);
        attributesMapping.setAttributeName("attributes");
        attributesMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "attribute");
        descriptor.addMapping(attributesMapping);

        XMLCompositeObjectMapping anyAttributeMapping = new XMLCompositeObjectMapping();
        anyAttributeMapping.setReferenceClass(AnyAttribute.class);
        anyAttributeMapping.setAttributeName("anyAttribute");
        anyAttributeMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "anyAttribute");
        descriptor.addMapping(anyAttributeMapping);

        XMLDirectMapping minInclusiveMapping = new XMLDirectMapping();
        minInclusiveMapping.setAttributeName("minInclusive");
        minInclusiveMapping.setSetMethodName("setMinInclusive");
        minInclusiveMapping.setGetMethodName("getMinInclusive");
        minInclusiveMapping.setXPath(Constants.SCHEMA_PREFIX + ":minInclusive/@value");
        descriptor.addMapping(minInclusiveMapping);

        XMLDirectMapping maxInclusiveMapping = new XMLDirectMapping();
        maxInclusiveMapping.setAttributeName("maxInclusive");
        maxInclusiveMapping.setSetMethodName("setMaxInclusive");
        maxInclusiveMapping.setGetMethodName("getMaxInclusive");
        maxInclusiveMapping.setXPath(Constants.SCHEMA_PREFIX + ":maxInclusive/@value");
        descriptor.addMapping(maxInclusiveMapping);

        XMLDirectMapping minExclusiveMapping = new XMLDirectMapping();
        minExclusiveMapping.setAttributeName("minExclusive");
        minExclusiveMapping.setSetMethodName("setMinExclusive");
        minExclusiveMapping.setGetMethodName("getMinExclusive");
        minExclusiveMapping.setXPath(Constants.SCHEMA_PREFIX + ":minExclusive/@value");
        descriptor.addMapping(minExclusiveMapping);
        
        XMLDirectMapping maxExclusiveMapping = new XMLDirectMapping();
        maxExclusiveMapping.setAttributeName("maxExclusive");
        maxExclusiveMapping.setSetMethodName("setMaxExclusive");
        maxExclusiveMapping.setGetMethodName("getMaxExclusive");
        maxExclusiveMapping.setXPath(Constants.SCHEMA_PREFIX + ":maxExclusive/@value");
        descriptor.addMapping(maxExclusiveMapping);

        XMLCompositeDirectCollectionMapping enumerationFacetsMapping = new XMLCompositeDirectCollectionMapping();
        enumerationFacetsMapping.setAttributeName("enumerationFacets");
        enumerationFacetsMapping.useCollectionClass(java.util.ArrayList.class);
        enumerationFacetsMapping.setXPath(Constants.SCHEMA_PREFIX + ":enumeration/@value");
        descriptor.addMapping(enumerationFacetsMapping);
        return descriptor;
    }

    private XMLDescriptor buildExtensionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Extension.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping baseTypeMapping = new XMLDirectMapping();
        baseTypeMapping.setAttributeName("baseType");
        baseTypeMapping.setXPath("@base");
        descriptor.addMapping(baseTypeMapping);

        XMLCompositeObjectMapping sequenceMapping = new XMLCompositeObjectMapping();
        sequenceMapping.setReferenceClass(Sequence.class);
        sequenceMapping.setAttributeName("sequence");
        sequenceMapping.setSetMethodName("setSequence");
        sequenceMapping.setGetMethodName("getSequence");
        sequenceMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "sequence");
        descriptor.addMapping(sequenceMapping);

        XMLCompositeObjectMapping choiceMapping = new XMLCompositeObjectMapping();
        choiceMapping.setReferenceClass(Choice.class);
        choiceMapping.setAttributeName("choice");
        choiceMapping.setSetMethodName("setChoice");
        choiceMapping.setGetMethodName("getChoice");
        choiceMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "choice");
        descriptor.addMapping(choiceMapping);

        XMLCompositeObjectMapping allMapping = new XMLCompositeObjectMapping();
        allMapping.setReferenceClass(All.class);
        allMapping.setAttributeName("all");
        allMapping.setSetMethodName("setAll");
        allMapping.setGetMethodName("getAll");
        allMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "all");
        descriptor.addMapping(allMapping);

        XMLAnyCollectionMapping orderedAttributesMapping = new XMLAnyCollectionMapping();
        orderedAttributesMapping.setAttributeName("orderedAttributes");
        descriptor.addMapping(orderedAttributesMapping);
        
        XMLCompositeObjectMapping anyAttributeMapping = new XMLCompositeObjectMapping();
        anyAttributeMapping.setReferenceClass(AnyAttribute.class);
        anyAttributeMapping.setAttributeName("anyAttribute");
        anyAttributeMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "anyAttribute");
        descriptor.addMapping(anyAttributeMapping);

        return descriptor;
    }

    private XMLDescriptor buildElementDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Element.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement(Constants.SCHEMA_PREFIX + ":element");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setXPath("@type");
        descriptor.addMapping(typeMapping);

        XMLDirectMapping formMapping = new XMLDirectMapping();
        formMapping.setAttributeName("form");
        formMapping.setXPath("@form");
        descriptor.addMapping(formMapping);
        
        XMLDirectMapping refMapping = new XMLDirectMapping();
        refMapping.setAttributeName("ref");
        refMapping.setXPath("@ref");
        descriptor.addMapping(refMapping);

        XMLDirectMapping minOccursMapping = new XMLDirectMapping();
        minOccursMapping.setAttributeName("minOccurs");
        minOccursMapping.setXPath("@minOccurs");
        minOccursMapping.setNullValue(Occurs.ONE);
        //minOccursMapping.setNullValue(Occurs.ZERO);
        descriptor.addMapping(minOccursMapping);

        XMLDirectMapping maxOccursMapping = new XMLDirectMapping();
        maxOccursMapping.setAttributeName("maxOccurs");
        maxOccursMapping.setXPath("@maxOccurs");
        maxOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(maxOccursMapping);

        XMLDirectMapping nillableMapping = new XMLDirectMapping();
        nillableMapping.setAttributeName("nillable");
        nillableMapping.setXPath("@nillable");
        nillableMapping.setNullValue(false);
        descriptor.addMapping(nillableMapping);

        XMLDirectMapping defaultMapping = new XMLDirectMapping();
        defaultMapping.setAttributeName("defaultValue");
        defaultMapping.setGetMethodName("getDefaultValue");
        defaultMapping.setSetMethodName("setDefaultValue");
        defaultMapping.setXPath("@default");
        ((NullPolicy) defaultMapping.getNullPolicy()).setSetPerformedForAbsentNode(false);
        descriptor.addMapping(defaultMapping);

        XMLDirectMapping fixedMapping = new XMLDirectMapping();
        fixedMapping.setAttributeName("fixed");
        fixedMapping.setXPath("@fixed");
        descriptor.addMapping(fixedMapping);

        XMLDirectMapping subGroupMapping = new XMLDirectMapping();
        subGroupMapping.setAttributeName("substitutionGroup");
        subGroupMapping.setXPath("@substitutionGroup");
        descriptor.addMapping(subGroupMapping);

        XMLCompositeObjectMapping annotationMapping = new XMLCompositeObjectMapping();
        annotationMapping.setReferenceClass(Annotation.class);
        annotationMapping.setAttributeName("annotation");
        annotationMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "annotation");
        descriptor.addMapping(annotationMapping);

        XMLCompositeObjectMapping simpleTypeMapping = new XMLCompositeObjectMapping();
        simpleTypeMapping.setReferenceClass(SimpleType.class);
        simpleTypeMapping.setAttributeName("simpleType");
        simpleTypeMapping.setGetMethodName("getSimpleType");
        simpleTypeMapping.setSetMethodName("setSimpleType");
        simpleTypeMapping.setXPath(Constants.SCHEMA_PREFIX + ":simpleType");
        descriptor.addMapping(simpleTypeMapping);

        XMLCompositeObjectMapping complexTypeMapping = new XMLCompositeObjectMapping();
        complexTypeMapping.setReferenceClass(ComplexType.class);
        complexTypeMapping.setAttributeName("complexType");
        complexTypeMapping.setGetMethodName("getComplexType");
        complexTypeMapping.setSetMethodName("setComplexType");
        complexTypeMapping.setXPath(Constants.SCHEMA_PREFIX + ":complexType");
        descriptor.addMapping(complexTypeMapping);

        XMLAnyAttributeMapping attributesMapMapping = new XMLAnyAttributeMapping();
        attributesMapMapping.setAttributeName("attributesMap");
        attributesMapMapping.setGetMethodName("getAttributesMap");
        attributesMapMapping.setSetMethodName("setAttributesMap");
        descriptor.addMapping(attributesMapMapping);

        return descriptor;
    }

    private XMLDescriptor buildChoiceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Choice.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement(Constants.SCHEMA_PREFIX + ":choice");
        //descriptor.getInheritancePolicy().setParentClass(TypeDefParticle.class);
        XMLDirectMapping minOccursMapping = new XMLDirectMapping();
        minOccursMapping.setAttributeName("minOccurs");
        minOccursMapping.setXPath("@minOccurs");
        minOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(minOccursMapping);

        XMLDirectMapping maxOccursMapping = new XMLDirectMapping();
        maxOccursMapping.setAttributeName("maxOccurs");
        maxOccursMapping.setXPath("@maxOccurs");
        maxOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(maxOccursMapping);

        XMLAnyCollectionMapping contentsMapping = new XMLAnyCollectionMapping();
        contentsMapping.setAttributeName("orderedElements");
        contentsMapping.setGetMethodName("getOrderedElements");
        contentsMapping.setSetMethodName("setOrderedElements");
        descriptor.addMapping(contentsMapping);

        return descriptor;

    }

    private XMLDescriptor buildAllDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(All.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement(Constants.SCHEMA_PREFIX + ":all");
        //				descriptor.getInheritancePolicy().setParentClass(TypeDefParticle.class);
        XMLDirectMapping minOccursMapping = new XMLDirectMapping();
        minOccursMapping.setAttributeName("minOccurs");
        minOccursMapping.setXPath("@minOccurs");
        minOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(minOccursMapping);

        XMLDirectMapping maxOccursMapping = new XMLDirectMapping();
        maxOccursMapping.setAttributeName("maxOccurs");
        maxOccursMapping.setXPath("@maxOccurs");
        maxOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(maxOccursMapping);

        XMLCompositeCollectionMapping elementsMapping = new XMLCompositeCollectionMapping();
        elementsMapping.setReferenceClass(Element.class);
        elementsMapping.setAttributeName("elements");
        elementsMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "element");
        descriptor.addMapping(elementsMapping);

        return descriptor;

    }

    private XMLDescriptor buildSequenceDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Sequence.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement(Constants.SCHEMA_PREFIX + ":sequence");
        //		descriptor.getInheritancePolicy().setParentClass(TypeDefParticle.class);
        XMLDirectMapping minOccursMapping = new XMLDirectMapping();
        minOccursMapping.setAttributeName("minOccurs");
        minOccursMapping.setXPath("@minOccurs");
        minOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(minOccursMapping);

        XMLDirectMapping maxOccursMapping = new XMLDirectMapping();
        maxOccursMapping.setAttributeName("maxOccurs");
        maxOccursMapping.setXPath("@maxOccurs");
        maxOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(maxOccursMapping);

        XMLAnyCollectionMapping contentsMapping = new XMLAnyCollectionMapping();
        contentsMapping.setAttributeName("orderedElements");
        contentsMapping.setGetMethodName("getOrderedElements");
        contentsMapping.setSetMethodName("setOrderedElements");
        descriptor.addMapping(contentsMapping);

        return descriptor;
    }

    private XMLDescriptor buildAnyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Any.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement(Constants.SCHEMA_PREFIX + ":any");

        XMLDirectMapping processContentsMapping = new XMLDirectMapping();
        processContentsMapping.setAttributeName("processContents");
        processContentsMapping.setXPath("@processContents");
        descriptor.addMapping(processContentsMapping);
        
        XMLDirectMapping namespaceMapping = new XMLDirectMapping();
        namespaceMapping.setAttributeName("namespace");
        namespaceMapping.setXPath("@namespace");
        descriptor.addMapping(namespaceMapping);

        XMLDirectMapping minOccursMapping = new XMLDirectMapping();
        minOccursMapping.setAttributeName("minOccurs");
        minOccursMapping.setXPath("@minOccurs");
        minOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(minOccursMapping);

        XMLDirectMapping maxOccursMapping = new XMLDirectMapping();
        maxOccursMapping.setAttributeName("maxOccurs");
        maxOccursMapping.setXPath("@maxOccurs");
        maxOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(maxOccursMapping);

        return descriptor;
    }

    private XMLDescriptor buildAttributeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Attribute.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement(Constants.SCHEMA_PREFIX + ":attribute");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setXPath("@type");
        descriptor.addMapping(typeMapping);

        XMLDirectMapping formMapping = new XMLDirectMapping();
        formMapping.setAttributeName("form");
        formMapping.setXPath("@form");
        descriptor.addMapping(formMapping);
        
        XMLDirectMapping useMapping = new XMLDirectMapping();
        useMapping.setAttributeName("use");
        useMapping.setXPath("@use");
        descriptor.addMapping(useMapping);

        XMLDirectMapping defaultMapping = new XMLDirectMapping();
        defaultMapping.setAttributeName("defaultValue");
        defaultMapping.setGetMethodName("getDefaultValue");
        defaultMapping.setSetMethodName("setDefaultValue");
        defaultMapping.setXPath("@default");
        ((NullPolicy) defaultMapping.getNullPolicy()).setSetPerformedForAbsentNode(false);
        descriptor.addMapping(defaultMapping);

        XMLDirectMapping fixedMapping = new XMLDirectMapping();
        fixedMapping.setAttributeName("fixed");
        fixedMapping.setXPath("@fixed");
        descriptor.addMapping(fixedMapping);

        XMLDirectMapping refMapping = new XMLDirectMapping();
        refMapping.setAttributeName("ref");
        refMapping.setXPath("@ref");
        descriptor.addMapping(refMapping);

        XMLCompositeObjectMapping annotationMapping = new XMLCompositeObjectMapping();
        annotationMapping.setReferenceClass(Annotation.class);
        annotationMapping.setAttributeName("annotation");
        annotationMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "annotation");
        descriptor.addMapping(annotationMapping);

        XMLCompositeObjectMapping simpleTypeMapping = new XMLCompositeObjectMapping();
        simpleTypeMapping.setReferenceClass(SimpleType.class);
        simpleTypeMapping.setAttributeName("simpleType");
        simpleTypeMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "simpleType");
        descriptor.addMapping(simpleTypeMapping);

        XMLAnyAttributeMapping attributesMapMapping = new XMLAnyAttributeMapping();
        attributesMapMapping.setAttributeName("attributesMap");
        attributesMapMapping.setGetMethodName("getAttributesMap");
        attributesMapMapping.setSetMethodName("setAttributesMap");
        descriptor.addMapping(attributesMapMapping);

        return descriptor;
    }

    private XMLDescriptor buildAnyAttributeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AnyAttribute.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement("any");

        XMLDirectMapping processContentsMapping = new XMLDirectMapping();
        processContentsMapping.setAttributeName("processContents");
        processContentsMapping.setXPath("@processContents");
        descriptor.addMapping(processContentsMapping);
        
        XMLDirectMapping namespaceMapping = new XMLDirectMapping();
        namespaceMapping.setAttributeName("namespace");
        namespaceMapping.setXPath("@namespace");
        descriptor.addMapping(namespaceMapping);

        return descriptor;
    }

    private XMLDescriptor buildImportDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Import.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping schemaLocationMapping = new XMLDirectMapping();
        schemaLocationMapping.setAttributeName("schemaLocation");
        schemaLocationMapping.setXPath(Constants.ATTRIBUTE+Constants.SCHEMA_LOCATION);
        descriptor.addMapping(schemaLocationMapping);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLDirectMapping namespaceMapping = new XMLDirectMapping();
        namespaceMapping.setAttributeName("namespace");
        namespaceMapping.setXPath("@namespace");
        descriptor.addMapping(namespaceMapping);

        return descriptor;
    }

    private XMLDescriptor buildIncludeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Include.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping schemaLocationMapping = new XMLDirectMapping();
        schemaLocationMapping.setAttributeName("schemaLocation");
        schemaLocationMapping.setXPath(Constants.ATTRIBUTE+Constants.SCHEMA_LOCATION);
        descriptor.addMapping(schemaLocationMapping);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        return descriptor;
    }

    private XMLDescriptor buildAnnotationDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Annotation.class);
        descriptor.setNamespaceResolver(namespaceResolver);

        XMLCompositeDirectCollectionMapping documentationMapping = new XMLCompositeDirectCollectionMapping();
        documentationMapping.setAttributeName("documentation");
        documentationMapping.useCollectionClass(ArrayList.class);
        documentationMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "documentation");
        ((XMLField)documentationMapping.getField()).setUsesSingleNode(false);
        descriptor.addMapping(documentationMapping);

        XMLFragmentCollectionMapping appInfoMapping = new XMLFragmentCollectionMapping();
        appInfoMapping.setAttributeName("appInfo");
        appInfoMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "appinfo");
        appInfoMapping.useCollectionClass(java.util.ArrayList.class);
        descriptor.addMapping(appInfoMapping);

        return descriptor;
    }

    private XMLDescriptor buildGroupDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Group.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement(Constants.SCHEMA_PREFIX + ":group");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping refMapping = new XMLDirectMapping();
        refMapping.setAttributeName("ref");
        refMapping.setXPath("@ref");
        descriptor.addMapping(refMapping);

        XMLDirectMapping minOccursMapping = new XMLDirectMapping();
        minOccursMapping.setAttributeName("minOccurs");
        minOccursMapping.setXPath("@minOccurs");
        minOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(minOccursMapping);

        XMLDirectMapping maxOccursMapping = new XMLDirectMapping();
        maxOccursMapping.setAttributeName("maxOccurs");
        maxOccursMapping.setXPath("@maxOccurs");
        maxOccursMapping.setNullValue(Occurs.ONE);
        descriptor.addMapping(maxOccursMapping);

        XMLCompositeObjectMapping annotationMapping = new XMLCompositeObjectMapping();
        annotationMapping.setReferenceClass(Annotation.class);
        annotationMapping.setAttributeName("annotation");
        annotationMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "annotation");
        descriptor.addMapping(annotationMapping);

        XMLCompositeObjectMapping sequenceMapping = new XMLCompositeObjectMapping();
        sequenceMapping.setReferenceClass(Sequence.class);
        sequenceMapping.setAttributeName("sequence");
        sequenceMapping.setSetMethodName("setSequence");
        sequenceMapping.setGetMethodName("getSequence");
        sequenceMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "sequence");
        descriptor.addMapping(sequenceMapping);

        XMLCompositeObjectMapping choiceMapping = new XMLCompositeObjectMapping();
        choiceMapping.setReferenceClass(Choice.class);
        choiceMapping.setAttributeName("choice");
        choiceMapping.setSetMethodName("setChoice");
        choiceMapping.setGetMethodName("getChoice");
        choiceMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "choice");
        descriptor.addMapping(choiceMapping);

        XMLCompositeObjectMapping allMapping = new XMLCompositeObjectMapping();
        allMapping.setReferenceClass(All.class);
        allMapping.setAttributeName("all");
        allMapping.setSetMethodName("setAll");
        allMapping.setGetMethodName("getAll");
        allMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "all");
        descriptor.addMapping(allMapping);

        return descriptor;
    }

    private XMLDescriptor buildAttributeGroupDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AttributeGroup.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement(Constants.SCHEMA_PREFIX + ":attributeGroup");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping refMapping = new XMLDirectMapping();
        refMapping.setAttributeName("ref");
        refMapping.setXPath("@ref");
        descriptor.addMapping(refMapping);

        XMLCompositeCollectionMapping attributesMapping = new XMLCompositeCollectionMapping();
        attributesMapping.setReferenceClass(Attribute.class);
        attributesMapping.useCollectionClass(ArrayList.class);
        attributesMapping.setAttributeName("attributes");
        attributesMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "attribute");
        descriptor.addMapping(attributesMapping);

        XMLCompositeObjectMapping anyAttributeMapping = new XMLCompositeObjectMapping();
        anyAttributeMapping.setReferenceClass(AnyAttribute.class);
        anyAttributeMapping.setAttributeName("anyAttribute");
        anyAttributeMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "anyAttribute");
        descriptor.addMapping(anyAttributeMapping);

        return descriptor;
    }

    /*
        private XMLDescriptor buildOccursDescriptor() {
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setJavaClass(Occurs.class);
            descriptor.setNamespaceResolver(namespaceResolver);

            XMLDirectMapping minOccursMapping = new XMLDirectMapping();
            minOccursMapping.setAttributeName("minOccurs");
            minOccursMapping.setXPath("@minOccurs");
            minOccursMapping.setNullValue(Occurs.ONE);
            descriptor.addMapping(minOccursMapping);

            XMLDirectMapping maxOccursMapping = new XMLDirectMapping();
            maxOccursMapping.setAttributeName("maxOccurs");
            maxOccursMapping.setXPath("@maxOccurs");
            maxOccursMapping.setNullValue(Occurs.ONE);
            descriptor.addMapping(maxOccursMapping);

            return descriptor;
        }
    */
    /*
        private XMLDescriptor buildTypeDefParticleDescriptor() {
            XMLDescriptor descriptor = new XMLDescriptor();
            descriptor.setJavaClass(TypeDefParticle.class);
            descriptor.setNamespaceResolver(namespaceResolver);

            ElementNameClassExtractor extractor = new ElementNameClassExtractor();
            extractor.addElementNameMapping(new QName(Constants.SCHEMA_URL, "sequence"),Sequence.class);
            extractor.addElementNameMapping(new QName(Constants.SCHEMA_URL, "choice"),Choice.class);
            extractor.addElementNameMapping(new QName(Constants.SCHEMA_URL, "all"),All.class);
            descriptor.getInheritancePolicy().setClassExtractor(extractor);


            //descriptor.getInheritancePolicy().setClassIndicatorField(new org.eclipse.persistence.oxm.XMLField(""));
            XMLDirectMapping minOccursMapping = new XMLDirectMapping();
            minOccursMapping.setAttributeName("minOccurs");
            minOccursMapping.setXPath("@minOccurs");
            minOccursMapping.setNullValue(Occurs.ONE);
            descriptor.addMapping(minOccursMapping);

            XMLDirectMapping maxOccursMapping = new XMLDirectMapping();
            maxOccursMapping.setAttributeName("maxOccurs");
            maxOccursMapping.setXPath("@maxOccurs");
            maxOccursMapping.setNullValue(Occurs.ONE);
            descriptor.addMapping(maxOccursMapping);

            XMLCompositeObjectMapping anyMapping = new XMLCompositeObjectMapping();
            anyMapping.setReferenceClass(Any.class);
            anyMapping.setAttributeName("any");
            anyMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "any");
            descriptor.addMapping(anyMapping);

            XMLCompositeCollectionMapping elementsMapping = new XMLCompositeCollectionMapping();
            elementsMapping.setReferenceClass(Element.class);
            elementsMapping.setAttributeName("elements");
            elementsMapping.setXPath(Constants.SCHEMA_PREFIX + ":" + "element");
            descriptor.addMapping(elementsMapping);

            return descriptor;
        }
            */
}
