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
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:cct="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentTypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;CCT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Amount. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;A number of monetary units specified in a currency where the unit of the currency is explicit or implied.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Amount&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Type&lt;/ccts:PropertyTerm&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for AmountType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AmountType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>decimal">
 *       &lt;attribute name="amountCurrencyID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;attribute name="amountCurrencyCodeListVersionID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountType", propOrder = {
    "value"
})
@XmlSeeAlso({
    org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.AmountType.class
})
public class AmountType {

    @XmlValue
    protected BigDecimal value;
    @XmlAttribute(name = "amountCurrencyID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String amountCurrencyID;
    @XmlAttribute(name = "amountCurrencyCodeListVersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String amountCurrencyCodeListVersionID;

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
     * Gets the value of the amountCurrencyID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAmountCurrencyID() {
        return amountCurrencyID;
    }

    /**
     * Sets the value of the amountCurrencyID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAmountCurrencyID(String value) {
        this.amountCurrencyID = value;
    }

    /**
     * Gets the value of the amountCurrencyCodeListVersionID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAmountCurrencyCodeListVersionID() {
        return amountCurrencyCodeListVersionID;
    }

    /**
     * Sets the value of the amountCurrencyCodeListVersionID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAmountCurrencyCodeListVersionID(String value) {
        this.amountCurrencyCodeListVersionID = value;
    }

}
