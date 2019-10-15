/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.largexml.bigpo.longitude_direction_code;

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
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;DT&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Longitude Direction_ Code. Type&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:RepresentationTerm&gt;Code&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataTypeQualifier&gt;Longitude Direction&lt;/ccts:DataTypeQualifier&gt;&lt;ccts:DataType&gt;Code. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Instance xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:CodeListID&gt;Longitude Direction&lt;/ccts:CodeListID&gt;&lt;ccts:CodeListAgencyID&gt;UBL&lt;/ccts:CodeListAgencyID&gt;&lt;ccts:CodeListAgencyName&gt;OASIS Universal Business Language&lt;/ccts:CodeListAgencyName&gt;&lt;ccts:CodeListName&gt;Longitude Direction&lt;/ccts:CodeListName&gt;&lt;ccts:CodeListVersionID&gt;1.0&lt;/ccts:CodeListVersionID&gt;&lt;ccts:CodeListSchemeURI&gt;urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0&lt;/ccts:CodeListSchemeURI&gt;&lt;ccts:LanguageID&gt;en&lt;/ccts:LanguageID&gt;&lt;/ccts:Instance&gt;
 * </pre>
 *
 *
 * <p>Java class for LongitudeDirectionCodeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LongitudeDirectionCodeType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0>LongitudeDirectionCodeContentType">
 *       &lt;attribute name="codeListID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" fixed="Longitude Direction" />
 *       &lt;attribute name="codeListAgencyID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" fixed="UBL" />
 *       &lt;attribute name="codeListAgencyName" type="{http://www.w3.org/2001/XMLSchema}string" fixed="OASIS Universal Business Language" />
 *       &lt;attribute name="codeListName" type="{http://www.w3.org/2001/XMLSchema}string" fixed="Longitude Direction" />
 *       &lt;attribute name="codeListVersionID" type="{http://www.w3.org/2001/XMLSchema}normalizedString" fixed="1.0" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="languageID" type="{http://www.w3.org/2001/XMLSchema}language" fixed="en" />
 *       &lt;attribute name="codeListURI" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="codeListSchemeURI" type="{http://www.w3.org/2001/XMLSchema}anyURI" fixed="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LongitudeDirectionCodeType", propOrder = {
    "value"
})
public class LongitudeDirectionCodeType {

    @XmlValue
    protected LongitudeDirectionCodeContentType value;
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
     *     {@link LongitudeDirectionCodeContentType }
     *
     */
    public LongitudeDirectionCodeContentType getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link LongitudeDirectionCodeContentType }
     *
     */
    public void setValue(LongitudeDirectionCodeContentType value) {
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
            return "Longitude Direction";
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
            return "UBL";
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
            return "OASIS Universal Business Language";
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
            return "Longitude Direction";
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
            return "1.0";
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
        if (languageID == null) {
            return "en";
        } else {
            return languageID;
        }
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
        return codeListURI;
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
            return "urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0";
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
