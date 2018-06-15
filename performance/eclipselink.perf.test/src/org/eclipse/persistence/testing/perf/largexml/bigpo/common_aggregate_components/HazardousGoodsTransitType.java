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
package org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.CodeType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ABIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Hazardous Goods Transit. Details&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;information related to the shipping and packaging of hazardous goods.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Hazardous Goods Transit&lt;/ccts:ObjectClass&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for HazardousGoodsTransitType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="HazardousGoodsTransitType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransportEmergencyCardCode" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}CodeType" minOccurs="0"/>
 *         &lt;element name="PackingCriteriaCode" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}CodeType" minOccurs="0"/>
 *         &lt;element name="RegulationCode" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}CodeType" minOccurs="0"/>
 *         &lt;element name="InhalationToxicityZoneCode" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}CodeType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}MaximumTemperature" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}MinimumTemperature" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HazardousGoodsTransitType", propOrder = {
    "transportEmergencyCardCode",
    "packingCriteriaCode",
    "regulationCode",
    "inhalationToxicityZoneCode",
    "maximumTemperature",
    "minimumTemperature"
})
public class HazardousGoodsTransitType {

    @XmlElement(name = "TransportEmergencyCardCode")
    protected CodeType transportEmergencyCardCode;
    @XmlElement(name = "PackingCriteriaCode")
    protected CodeType packingCriteriaCode;
    @XmlElement(name = "RegulationCode")
    protected CodeType regulationCode;
    @XmlElement(name = "InhalationToxicityZoneCode")
    protected CodeType inhalationToxicityZoneCode;
    @XmlElement(name = "MaximumTemperature")
    protected TemperatureType maximumTemperature;
    @XmlElement(name = "MinimumTemperature")
    protected TemperatureType minimumTemperature;

    /**
     * Gets the value of the transportEmergencyCardCode property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getTransportEmergencyCardCode() {
        return transportEmergencyCardCode;
    }

    /**
     * Sets the value of the transportEmergencyCardCode property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setTransportEmergencyCardCode(CodeType value) {
        this.transportEmergencyCardCode = value;
    }

    /**
     * Gets the value of the packingCriteriaCode property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getPackingCriteriaCode() {
        return packingCriteriaCode;
    }

    /**
     * Sets the value of the packingCriteriaCode property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setPackingCriteriaCode(CodeType value) {
        this.packingCriteriaCode = value;
    }

    /**
     * Gets the value of the regulationCode property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getRegulationCode() {
        return regulationCode;
    }

    /**
     * Sets the value of the regulationCode property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setRegulationCode(CodeType value) {
        this.regulationCode = value;
    }

    /**
     * Gets the value of the inhalationToxicityZoneCode property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getInhalationToxicityZoneCode() {
        return inhalationToxicityZoneCode;
    }

    /**
     * Sets the value of the inhalationToxicityZoneCode property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setInhalationToxicityZoneCode(CodeType value) {
        this.inhalationToxicityZoneCode = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Hazardous Goods Transit. Maximum_ Temperature. Temperature&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the maximum temperature at which the hazardous item can be safely transported.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Hazardous Goods Transit&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Maximum&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Temperature&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Temperature&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link TemperatureType }
     *
     */
    public TemperatureType getMaximumTemperature() {
        return maximumTemperature;
    }

    /**
     * Sets the value of the maximumTemperature property.
     *
     * @param value
     *     allowed object is
     *     {@link TemperatureType }
     *
     */
    public void setMaximumTemperature(TemperatureType value) {
        this.maximumTemperature = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Hazardous Goods Transit. Minimum_ Temperature. Temperature&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the minimum temperature at which the hazardous item can be safely transported.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Hazardous Goods Transit&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Minimum&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Temperature&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Temperature&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link TemperatureType }
     *
     */
    public TemperatureType getMinimumTemperature() {
        return minimumTemperature;
    }

    /**
     * Sets the value of the minimumTemperature property.
     *
     * @param value
     *     allowed object is
     *     {@link TemperatureType }
     *
     */
    public void setMinimumTemperature(TemperatureType value) {
        this.minimumTemperature = value;
    }

}
