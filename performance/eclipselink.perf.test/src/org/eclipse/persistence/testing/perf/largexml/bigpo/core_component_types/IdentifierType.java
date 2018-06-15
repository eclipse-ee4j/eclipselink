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
package org.eclipse.persistence.testing.perf.largexml.bigpo.core_component_types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:cct="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;CCT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Identifier. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;A character string to identify and distinguish uniquely, one instance of an object in an identification scheme from all other objects in the same scheme together with relevant supplementary information.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Identifier&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Type&lt;/ccts:PropertyTerm&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for IdentifierType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="IdentifierType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>normalizedString">
 *       &lt;attribute name="identificationSchemeID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="identificationSchemeName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="identificationSchemeAgencyID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="identificationSchemeAgencyName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="identificationSchemeVersionID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="identificationSchemeURI" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="identificationSchemeDataURI" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentifierType", propOrder = {
    "value"
})
@XmlSeeAlso({
    org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.IdentifierType.class
})
public class IdentifierType {

    @XmlValue
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String value;
    @XmlAttribute(name = "identificationSchemeID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String identificationSchemeID;
    @XmlAttribute(name = "identificationSchemeName")
    protected String identificationSchemeName;
    @XmlAttribute(name = "identificationSchemeAgencyID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String identificationSchemeAgencyID;
    @XmlAttribute(name = "identificationSchemeAgencyName")
    protected String identificationSchemeAgencyName;
    @XmlAttribute(name = "identificationSchemeVersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String identificationSchemeVersionID;
    @XmlAttribute(name = "identificationSchemeURI")
    @XmlSchemaType(name = "anyURI")
    protected String identificationSchemeURI;
    @XmlAttribute(name = "identificationSchemeDataURI")
    @XmlSchemaType(name = "anyURI")
    protected String identificationSchemeDataURI;

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the identificationSchemeID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentificationSchemeID() {
        return identificationSchemeID;
    }

    /**
     * Sets the value of the identificationSchemeID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentificationSchemeID(String value) {
        this.identificationSchemeID = value;
    }

    /**
     * Gets the value of the identificationSchemeName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentificationSchemeName() {
        return identificationSchemeName;
    }

    /**
     * Sets the value of the identificationSchemeName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentificationSchemeName(String value) {
        this.identificationSchemeName = value;
    }

    /**
     * Gets the value of the identificationSchemeAgencyID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentificationSchemeAgencyID() {
        return identificationSchemeAgencyID;
    }

    /**
     * Sets the value of the identificationSchemeAgencyID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentificationSchemeAgencyID(String value) {
        this.identificationSchemeAgencyID = value;
    }

    /**
     * Gets the value of the identificationSchemeAgencyName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentificationSchemeAgencyName() {
        return identificationSchemeAgencyName;
    }

    /**
     * Sets the value of the identificationSchemeAgencyName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentificationSchemeAgencyName(String value) {
        this.identificationSchemeAgencyName = value;
    }

    /**
     * Gets the value of the identificationSchemeVersionID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentificationSchemeVersionID() {
        return identificationSchemeVersionID;
    }

    /**
     * Sets the value of the identificationSchemeVersionID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentificationSchemeVersionID(String value) {
        this.identificationSchemeVersionID = value;
    }

    /**
     * Gets the value of the identificationSchemeURI property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentificationSchemeURI() {
        return identificationSchemeURI;
    }

    /**
     * Sets the value of the identificationSchemeURI property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentificationSchemeURI(String value) {
        this.identificationSchemeURI = value;
    }

    /**
     * Gets the value of the identificationSchemeDataURI property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdentificationSchemeDataURI() {
        return identificationSchemeDataURI;
    }

    /**
     * Sets the value of the identificationSchemeDataURI property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdentificationSchemeDataURI(String value) {
        this.identificationSchemeDataURI = value;
    }

}
