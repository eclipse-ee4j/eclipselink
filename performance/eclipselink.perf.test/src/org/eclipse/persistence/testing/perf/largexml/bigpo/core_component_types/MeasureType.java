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
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:cct="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;CCT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Measure. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;A numeric value determined by measuring an object along with the specified unit of measure.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Measure&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Type&lt;/ccts:PropertyTerm&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for MeasureType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="MeasureType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>decimal">
 *       &lt;attribute name="measureUnitCode" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="measureUnitCodeListVersionID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MeasureType", propOrder = {
    "value"
})
@XmlSeeAlso({
    org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.MeasureType.class
})
public class MeasureType {

    @XmlValue
    protected BigDecimal value;
    @XmlAttribute(name = "measureUnitCode")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String measureUnitCode;
    @XmlAttribute(name = "measureUnitCodeListVersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String measureUnitCodeListVersionID;

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
     * Gets the value of the measureUnitCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMeasureUnitCode() {
        return measureUnitCode;
    }

    /**
     * Sets the value of the measureUnitCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMeasureUnitCode(String value) {
        this.measureUnitCode = value;
    }

    /**
     * Gets the value of the measureUnitCodeListVersionID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMeasureUnitCodeListVersionID() {
        return measureUnitCodeListVersionID;
    }

    /**
     * Sets the value of the measureUnitCodeListVersionID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMeasureUnitCodeListVersionID(String value) {
        this.measureUnitCodeListVersionID = value;
    }

}
