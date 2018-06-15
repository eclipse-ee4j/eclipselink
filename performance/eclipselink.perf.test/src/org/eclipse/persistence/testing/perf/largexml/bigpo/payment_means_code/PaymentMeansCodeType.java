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
package org.eclipse.persistence.testing.perf.largexml.bigpo.payment_means_code;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;DT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Payment Means_ Code. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:RepresentationTerm&gt;Code&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataTypeQualifier&gt;Payment Means&lt;/ccts:DataTypeQualifier&gt;&lt;ccts:DataType&gt;Code. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Instance xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:CodeListID&gt;UN/ECE 4461&lt;/ccts:CodeListID&gt;&lt;ccts:CodeListAgencyID&gt;6&lt;/ccts:CodeListAgencyID&gt;&lt;ccts:CodeListAgencyName&gt;United Nations Economic Commission for Europe&lt;/ccts:CodeListAgencyName&gt;&lt;ccts:CodeListName&gt;Payment Means&lt;/ccts:CodeListName&gt;&lt;ccts:CodeListVersionID&gt;D03A&lt;/ccts:CodeListVersionID&gt;&lt;ccts:CodeListURI&gt;http://www.unece.org/trade/untdid/d03a/tred/tred4461.htm&lt;/ccts:CodeListURI&gt;&lt;ccts:CodeListSchemeURI&gt;urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0&lt;/ccts:CodeListSchemeURI&gt;&lt;/ccts:Instance&gt;
 * </pre>
 *
 *
 * <p>Java class for PaymentMeansCodeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PaymentMeansCodeType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0>PaymentMeansCodeContentType">
 *       &lt;attribute name="codeListID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" fixed="UN/ECE 4461" />
 *       &lt;attribute name="codeListAgencyID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" fixed="6" />
 *       &lt;attribute name="codeListAgencyName" type="{http://www.w3.org/2001/XMLSchema}string" fixed="United Nations Economic Commission for Europe" />
 *       &lt;attribute name="codeListName" type="{http://www.w3.org/2001/XMLSchema}string" fixed="Payment Means" />
 *       &lt;attribute name="codeListVersionID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" fixed="D03A" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="languageID" type="{http://www.w3.org/2001/XMLSchema}language" />
 *       &lt;attribute name="codeListURI" type="{http://www.w3.org/2001/XMLSchema}anyURI" fixed="http://www.unece.org/trade/untdid/d03a/tred/tred4461.htm" />
 *       &lt;attribute name="codeListSchemeURI" type="{http://www.w3.org/2001/XMLSchema}anyURI" fixed="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentMeansCodeType", propOrder = {
    "value"
})
public class PaymentMeansCodeType {

    @XmlValue
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String value;
    @XmlAttribute(name = "codeListID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String codeListID;
    @XmlAttribute(name = "codeListAgencyID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String codeListAgencyID;
    @XmlAttribute(name = "codeListAgencyName")
    protected String codeListAgencyName;
    @XmlAttribute(name = "codeListName")
    protected String codeListName;
    @XmlAttribute(name = "codeListVersionID")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String codeListVersionID;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "languageID")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    protected String languageID;
    @XmlAttribute(name = "codeListURI")
    @XmlSchemaType(name = "anyURI")
    protected String codeListURI;
    @XmlAttribute(name = "codeListSchemeURI")
    @XmlSchemaType(name = "anyURI")
    protected String codeListSchemeURI;

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
     * Gets the value of the codeListID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCodeListID() {
        if (codeListID == null) {
            return "UN/ECE 4461";
        } else {
            return codeListID;
        }
    }

    /**
     * Sets the value of the codeListID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCodeListID(String value) {
        this.codeListID = value;
    }

    /**
     * Gets the value of the codeListAgencyID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCodeListAgencyID() {
        if (codeListAgencyID == null) {
            return "6";
        } else {
            return codeListAgencyID;
        }
    }

    /**
     * Sets the value of the codeListAgencyID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCodeListAgencyID(String value) {
        this.codeListAgencyID = value;
    }

    /**
     * Gets the value of the codeListAgencyName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCodeListAgencyName() {
        if (codeListAgencyName == null) {
            return "United Nations Economic Commission for Europe";
        } else {
            return codeListAgencyName;
        }
    }

    /**
     * Sets the value of the codeListAgencyName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCodeListAgencyName(String value) {
        this.codeListAgencyName = value;
    }

    /**
     * Gets the value of the codeListName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCodeListName() {
        if (codeListName == null) {
            return "Payment Means";
        } else {
            return codeListName;
        }
    }

    /**
     * Sets the value of the codeListName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCodeListName(String value) {
        this.codeListName = value;
    }

    /**
     * Gets the value of the codeListVersionID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCodeListVersionID() {
        if (codeListVersionID == null) {
            return "D03A";
        } else {
            return codeListVersionID;
        }
    }

    /**
     * Sets the value of the codeListVersionID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCodeListVersionID(String value) {
        this.codeListVersionID = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the languageID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLanguageID() {
        return languageID;
    }

    /**
     * Sets the value of the languageID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLanguageID(String value) {
        this.languageID = value;
    }

    /**
     * Gets the value of the codeListURI property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCodeListURI() {
        if (codeListURI == null) {
            return "http://www.unece.org/trade/untdid/d03a/tred/tred4461.htm";
        } else {
            return codeListURI;
        }
    }

    /**
     * Sets the value of the codeListURI property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCodeListURI(String value) {
        this.codeListURI = value;
    }

    /**
     * Gets the value of the codeListSchemeURI property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCodeListSchemeURI() {
        if (codeListSchemeURI == null) {
            return "urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0";
        } else {
            return codeListSchemeURI;
        }
    }

    /**
     * Sets the value of the codeListSchemeURI property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCodeListSchemeURI(String value) {
        this.codeListSchemeURI = value;
    }

}
