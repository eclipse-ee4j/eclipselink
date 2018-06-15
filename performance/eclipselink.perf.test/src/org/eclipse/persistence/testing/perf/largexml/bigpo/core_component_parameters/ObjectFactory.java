/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.largexml.bigpo.core_component_parameters;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.eclipse.persistence.testing.perf.largexml.bigpo.core_component_parameters package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Context_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Context");
    private final static QName _Component_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Component");
    private final static QName _ObjectClass_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "ObjectClass");
    private final static QName _CodeListID_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "CodeListID");
    private final static QName _CodeListURI_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "CodeListURI");
    private final static QName _ObjectClassQualifier_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "ObjectClassQualifier");
    private final static QName _Instance_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Instance");
    private final static QName _Name_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Name");
    private final static QName _DataTypeQualifier_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "DataTypeQualifier");
    private final static QName _Geopolitical_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Geopolitical");
    private final static QName _Cardinality_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Cardinality");
    private final static QName _DataType_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "DataType");
    private final static QName _CodeListAgencyName_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "CodeListAgencyName");
    private final static QName _LanguageID_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "LanguageID");
    private final static QName _IndustryClassification_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "IndustryClassification");
    private final static QName _ComponentType_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "ComponentType");
    private final static QName _PropertyTermQualifier_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "PropertyTermQualifier");
    private final static QName _Definition_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Definition");
    private final static QName _PropertyTerm_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "PropertyTerm");
    private final static QName _Version_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Version");
    private final static QName _SupportingRole_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "SupportingRole");
    private final static QName _BusinessProcess_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "BusinessProcess");
    private final static QName _Examples_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Examples");
    private final static QName _ProductClassification_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "ProductClassification");
    private final static QName _CodeListVersionID_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "CodeListVersionID");
    private final static QName _AssociatedObjectClassQualifier_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "AssociatedObjectClassQualifier");
    private final static QName _OfficialConstraint_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "OfficialConstraint");
    private final static QName _AssociatedObjectClass_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "AssociatedObjectClass");
    private final static QName _CodeListName_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "CodeListName");
    private final static QName _SystemCapability_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "SystemCapability");
    private final static QName _DictionaryEntryName_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "DictionaryEntryName");
    private final static QName _Contextualization_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "Contextualization");
    private final static QName _PropertyTermPrimaryNoun_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "PropertyTermPrimaryNoun");
    private final static QName _CodeListAgencyID_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "CodeListAgencyID");
    private final static QName _CodeListSchemeURI_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "CodeListSchemeURI");
    private final static QName _PropertyTermPossessiveNoun_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "PropertyTermPossessiveNoun");
    private final static QName _BusinessProcessRole_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "BusinessProcessRole");
    private final static QName _RepresentationTerm_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "RepresentationTerm");
    private final static QName _AlternativeBusinessTerms_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", "AlternativeBusinessTerms");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.persistence.testing.perf.largexml.bigpo.core_component_parameters
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ContextualizationType }
     *
     */
    public ContextualizationType createContextualizationType() {
        return new ContextualizationType();
    }

    /**
     * Create an instance of {@link ComponentType }
     *
     */
    public ComponentType createComponentType() {
        return new ComponentType();
    }

    /**
     * Create an instance of {@link InstanceType }
     *
     */
    public InstanceType createInstanceType() {
        return new InstanceType();
    }

    /**
     * Create an instance of {@link ContextType }
     *
     */
    public ContextType createContextType() {
        return new ContextType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContextType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Context")
    public JAXBElement<ContextType> createContext(ContextType value) {
        return new JAXBElement<ContextType>(_Context_QNAME, ContextType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ComponentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Component")
    public JAXBElement<ComponentType> createComponent(ComponentType value) {
        return new JAXBElement<ComponentType>(_Component_QNAME, ComponentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "ObjectClass")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createObjectClass(String value) {
        return new JAXBElement<String>(_ObjectClass_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "CodeListID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createCodeListID(String value) {
        return new JAXBElement<String>(_CodeListID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "CodeListURI")
    public JAXBElement<String> createCodeListURI(String value) {
        return new JAXBElement<String>(_CodeListURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "ObjectClassQualifier")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createObjectClassQualifier(String value) {
        return new JAXBElement<String>(_ObjectClassQualifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InstanceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Instance")
    public JAXBElement<InstanceType> createInstance(InstanceType value) {
        return new JAXBElement<InstanceType>(_Instance_QNAME, InstanceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "DataTypeQualifier")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createDataTypeQualifier(String value) {
        return new JAXBElement<String>(_DataTypeQualifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Geopolitical")
    public JAXBElement<String> createGeopolitical(String value) {
        return new JAXBElement<String>(_Geopolitical_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Cardinality")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createCardinality(String value) {
        return new JAXBElement<String>(_Cardinality_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "DataType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createDataType(String value) {
        return new JAXBElement<String>(_DataType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "CodeListAgencyName")
    public JAXBElement<String> createCodeListAgencyName(String value) {
        return new JAXBElement<String>(_CodeListAgencyName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "LanguageID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLanguageID(String value) {
        return new JAXBElement<String>(_LanguageID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "IndustryClassification")
    public JAXBElement<String> createIndustryClassification(String value) {
        return new JAXBElement<String>(_IndustryClassification_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "ComponentType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createComponentType(String value) {
        return new JAXBElement<String>(_ComponentType_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "PropertyTermQualifier")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createPropertyTermQualifier(String value) {
        return new JAXBElement<String>(_PropertyTermQualifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Definition")
    public JAXBElement<String> createDefinition(String value) {
        return new JAXBElement<String>(_Definition_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "PropertyTerm")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createPropertyTerm(String value) {
        return new JAXBElement<String>(_PropertyTerm_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Version")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createVersion(String value) {
        return new JAXBElement<String>(_Version_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "SupportingRole")
    public JAXBElement<String> createSupportingRole(String value) {
        return new JAXBElement<String>(_SupportingRole_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "BusinessProcess")
    public JAXBElement<String> createBusinessProcess(String value) {
        return new JAXBElement<String>(_BusinessProcess_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Examples")
    public JAXBElement<String> createExamples(String value) {
        return new JAXBElement<String>(_Examples_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "ProductClassification")
    public JAXBElement<String> createProductClassification(String value) {
        return new JAXBElement<String>(_ProductClassification_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "CodeListVersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createCodeListVersionID(String value) {
        return new JAXBElement<String>(_CodeListVersionID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "AssociatedObjectClassQualifier")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createAssociatedObjectClassQualifier(String value) {
        return new JAXBElement<String>(_AssociatedObjectClassQualifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "OfficialConstraint")
    public JAXBElement<String> createOfficialConstraint(String value) {
        return new JAXBElement<String>(_OfficialConstraint_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "AssociatedObjectClass")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createAssociatedObjectClass(String value) {
        return new JAXBElement<String>(_AssociatedObjectClass_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "CodeListName")
    public JAXBElement<String> createCodeListName(String value) {
        return new JAXBElement<String>(_CodeListName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "SystemCapability")
    public JAXBElement<String> createSystemCapability(String value) {
        return new JAXBElement<String>(_SystemCapability_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "DictionaryEntryName")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createDictionaryEntryName(String value) {
        return new JAXBElement<String>(_DictionaryEntryName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContextualizationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "Contextualization")
    public JAXBElement<ContextualizationType> createContextualization(ContextualizationType value) {
        return new JAXBElement<ContextualizationType>(_Contextualization_QNAME, ContextualizationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "PropertyTermPrimaryNoun")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createPropertyTermPrimaryNoun(String value) {
        return new JAXBElement<String>(_PropertyTermPrimaryNoun_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "CodeListAgencyID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createCodeListAgencyID(String value) {
        return new JAXBElement<String>(_CodeListAgencyID_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "CodeListSchemeURI")
    public JAXBElement<String> createCodeListSchemeURI(String value) {
        return new JAXBElement<String>(_CodeListSchemeURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "PropertyTermPossessiveNoun")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createPropertyTermPossessiveNoun(String value) {
        return new JAXBElement<String>(_PropertyTermPossessiveNoun_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "BusinessProcessRole")
    public JAXBElement<String> createBusinessProcessRole(String value) {
        return new JAXBElement<String>(_BusinessProcessRole_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "RepresentationTerm")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createRepresentationTerm(String value) {
        return new JAXBElement<String>(_RepresentationTerm_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0", name = "AlternativeBusinessTerms")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    public JAXBElement<String> createAlternativeBusinessTerms(String value) {
        return new JAXBElement<String>(_AlternativeBusinessTerms_QNAME, String.class, null, value);
    }

}
