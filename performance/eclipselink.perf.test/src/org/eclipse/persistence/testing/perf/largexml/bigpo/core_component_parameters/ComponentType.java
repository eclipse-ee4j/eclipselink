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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for ComponentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ComponentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}ComponentType"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}DictionaryEntryName"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}Version" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}Definition"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}Cardinality" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}ObjectClassQualifier" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}ObjectClass" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}PropertyTermQualifier" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}PropertyTermPossessiveNoun" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}PropertyTermPrimaryNoun" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}PropertyTerm" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}RepresentationTerm" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}DataTypeQualifier" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}DataType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}AssociatedObjectClassQualifier" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}AssociatedObjectClass" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}AlternativeBusinessTerms" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0}Examples" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComponentType", propOrder = {
    "componentType",
    "dictionaryEntryName",
    "version",
    "definition",
    "cardinality",
    "objectClassQualifier",
    "objectClass",
    "propertyTermQualifier",
    "propertyTermPossessiveNoun",
    "propertyTermPrimaryNoun",
    "propertyTerm",
    "representationTerm",
    "dataTypeQualifier",
    "dataType",
    "associatedObjectClassQualifier",
    "associatedObjectClass",
    "alternativeBusinessTerms",
    "examples"
})
public class ComponentType {

    @XmlElement(name = "ComponentType", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String componentType;
    @XmlElement(name = "DictionaryEntryName", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String dictionaryEntryName;
    @XmlElement(name = "Version")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String version;
    @XmlElement(name = "Definition", required = true)
    protected String definition;
    @XmlElement(name = "Cardinality")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String cardinality;
    @XmlElement(name = "ObjectClassQualifier")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String objectClassQualifier;
    @XmlElement(name = "ObjectClass")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String objectClass;
    @XmlElement(name = "PropertyTermQualifier")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String propertyTermQualifier;
    @XmlElement(name = "PropertyTermPossessiveNoun")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String propertyTermPossessiveNoun;
    @XmlElement(name = "PropertyTermPrimaryNoun")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String propertyTermPrimaryNoun;
    @XmlElement(name = "PropertyTerm")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String propertyTerm;
    @XmlElement(name = "RepresentationTerm")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String representationTerm;
    @XmlElement(name = "DataTypeQualifier")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String dataTypeQualifier;
    @XmlElement(name = "DataType")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String dataType;
    @XmlElement(name = "AssociatedObjectClassQualifier")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String associatedObjectClassQualifier;
    @XmlElement(name = "AssociatedObjectClass")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String associatedObjectClass;
    @XmlElement(name = "AlternativeBusinessTerms")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String alternativeBusinessTerms;
    @XmlElement(name = "Examples")
    protected String examples;

    /**
     * Gets the value of the componentType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * Sets the value of the componentType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setComponentType(String value) {
        this.componentType = value;
    }

    /**
     * Gets the value of the dictionaryEntryName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDictionaryEntryName() {
        return dictionaryEntryName;
    }

    /**
     * Sets the value of the dictionaryEntryName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDictionaryEntryName(String value) {
        this.dictionaryEntryName = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the definition property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Sets the value of the definition property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDefinition(String value) {
        this.definition = value;
    }

    /**
     * Gets the value of the cardinality property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCardinality() {
        return cardinality;
    }

    /**
     * Sets the value of the cardinality property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCardinality(String value) {
        this.cardinality = value;
    }

    /**
     * Gets the value of the objectClassQualifier property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getObjectClassQualifier() {
        return objectClassQualifier;
    }

    /**
     * Sets the value of the objectClassQualifier property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setObjectClassQualifier(String value) {
        this.objectClassQualifier = value;
    }

    /**
     * Gets the value of the objectClass property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getObjectClass() {
        return objectClass;
    }

    /**
     * Sets the value of the objectClass property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setObjectClass(String value) {
        this.objectClass = value;
    }

    /**
     * Gets the value of the propertyTermQualifier property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPropertyTermQualifier() {
        return propertyTermQualifier;
    }

    /**
     * Sets the value of the propertyTermQualifier property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPropertyTermQualifier(String value) {
        this.propertyTermQualifier = value;
    }

    /**
     * Gets the value of the propertyTermPossessiveNoun property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPropertyTermPossessiveNoun() {
        return propertyTermPossessiveNoun;
    }

    /**
     * Sets the value of the propertyTermPossessiveNoun property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPropertyTermPossessiveNoun(String value) {
        this.propertyTermPossessiveNoun = value;
    }

    /**
     * Gets the value of the propertyTermPrimaryNoun property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPropertyTermPrimaryNoun() {
        return propertyTermPrimaryNoun;
    }

    /**
     * Sets the value of the propertyTermPrimaryNoun property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPropertyTermPrimaryNoun(String value) {
        this.propertyTermPrimaryNoun = value;
    }

    /**
     * Gets the value of the propertyTerm property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPropertyTerm() {
        return propertyTerm;
    }

    /**
     * Sets the value of the propertyTerm property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPropertyTerm(String value) {
        this.propertyTerm = value;
    }

    /**
     * Gets the value of the representationTerm property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRepresentationTerm() {
        return representationTerm;
    }

    /**
     * Sets the value of the representationTerm property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRepresentationTerm(String value) {
        this.representationTerm = value;
    }

    /**
     * Gets the value of the dataTypeQualifier property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDataTypeQualifier() {
        return dataTypeQualifier;
    }

    /**
     * Sets the value of the dataTypeQualifier property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDataTypeQualifier(String value) {
        this.dataTypeQualifier = value;
    }

    /**
     * Gets the value of the dataType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDataType(String value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the associatedObjectClassQualifier property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAssociatedObjectClassQualifier() {
        return associatedObjectClassQualifier;
    }

    /**
     * Sets the value of the associatedObjectClassQualifier property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAssociatedObjectClassQualifier(String value) {
        this.associatedObjectClassQualifier = value;
    }

    /**
     * Gets the value of the associatedObjectClass property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAssociatedObjectClass() {
        return associatedObjectClass;
    }

    /**
     * Sets the value of the associatedObjectClass property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAssociatedObjectClass(String value) {
        this.associatedObjectClass = value;
    }

    /**
     * Gets the value of the alternativeBusinessTerms property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAlternativeBusinessTerms() {
        return alternativeBusinessTerms;
    }

    /**
     * Sets the value of the alternativeBusinessTerms property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAlternativeBusinessTerms(String value) {
        this.alternativeBusinessTerms = value;
    }

    /**
     * Gets the value of the examples property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExamples() {
        return examples;
    }

    /**
     * Sets the value of the examples property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExamples(String value) {
        this.examples = value;
    }

}
