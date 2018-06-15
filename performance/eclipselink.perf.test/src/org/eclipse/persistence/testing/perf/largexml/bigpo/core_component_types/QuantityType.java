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

import java.math.BigDecimal;
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
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:cct="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;CCT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Quantity. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;A counted number of non-monetary units possibly including fractions.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Quantity&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Type&lt;/ccts:PropertyTerm&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for QuantityType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QuantityType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>decimal">
 *       &lt;attribute name="quantityUnitCode" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="quantityUnitCodeListID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="quantityUnitCodeListAgencyID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="quantityUnitCodeListAgencyName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuantityType", propOrder = {
    "value"
})
@XmlSeeAlso({
    org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.QuantityType.class
})
public class QuantityType {

    @XmlValue
    protected BigDecimal value;
    @XmlAttribute(name = "quantityUnitCode")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String quantityUnitCode;
    @XmlAttribute(name = "quantityUnitCodeListID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String quantityUnitCodeListID;
    @XmlAttribute(name = "quantityUnitCodeListAgencyID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String quantityUnitCodeListAgencyID;
    @XmlAttribute(name = "quantityUnitCodeListAgencyName")
    protected String quantityUnitCodeListAgencyName;

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the value of the quantityUnitCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getQuantityUnitCode() {
        return quantityUnitCode;
    }

    /**
     * Sets the value of the quantityUnitCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setQuantityUnitCode(String value) {
        this.quantityUnitCode = value;
    }

    /**
     * Gets the value of the quantityUnitCodeListID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getQuantityUnitCodeListID() {
        return quantityUnitCodeListID;
    }

    /**
     * Sets the value of the quantityUnitCodeListID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setQuantityUnitCodeListID(String value) {
        this.quantityUnitCodeListID = value;
    }

    /**
     * Gets the value of the quantityUnitCodeListAgencyID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getQuantityUnitCodeListAgencyID() {
        return quantityUnitCodeListAgencyID;
    }

    /**
     * Sets the value of the quantityUnitCodeListAgencyID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setQuantityUnitCodeListAgencyID(String value) {
        this.quantityUnitCodeListAgencyID = value;
    }

    /**
     * Gets the value of the quantityUnitCodeListAgencyName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getQuantityUnitCodeListAgencyName() {
        return quantityUnitCodeListAgencyName;
    }

    /**
     * Sets the value of the quantityUnitCodeListAgencyName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setQuantityUnitCodeListAgencyName(String value) {
        this.quantityUnitCodeListAgencyName = value;
    }

}
