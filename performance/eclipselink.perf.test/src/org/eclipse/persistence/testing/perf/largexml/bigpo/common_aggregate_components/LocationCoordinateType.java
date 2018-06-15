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
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LatitudeDegreesMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LatitudeMinutesMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LongitudeDegreesMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.LongitudeMinutesMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.latitude_direction_code.LatitudeDirectionCodeType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.longitude_direction_code.LongitudeDirectionCodeType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.CodeType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ABIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Location Coordinate. Details&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;contains information that enables location (of something) by a system of coordinates.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Location Coordinate&lt;/ccts:ObjectClass&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for LocationCoordinateType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LocationCoordinateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CoordinateSystemCode" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}CodeType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}LatitudeDegreesMeasure" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}LatitudeMinutesMeasure" minOccurs="0"/>
 *         &lt;element name="LatitudeDirectionCode" type="{urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0}LatitudeDirectionCodeType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}LongitudeDegreesMeasure" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}LongitudeMinutesMeasure" minOccurs="0"/>
 *         &lt;element name="LongitudeDirectionCode" type="{urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0}LongitudeDirectionCodeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocationCoordinateType", propOrder = {
    "coordinateSystemCode",
    "latitudeDegreesMeasure",
    "latitudeMinutesMeasure",
    "latitudeDirectionCode",
    "longitudeDegreesMeasure",
    "longitudeMinutesMeasure",
    "longitudeDirectionCode"
})
public class LocationCoordinateType {

    @XmlElement(name = "CoordinateSystemCode")
    protected CodeType coordinateSystemCode;
    @XmlElement(name = "LatitudeDegreesMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected LatitudeDegreesMeasureType latitudeDegreesMeasure;
    @XmlElement(name = "LatitudeMinutesMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected LatitudeMinutesMeasureType latitudeMinutesMeasure;
    @XmlElement(name = "LatitudeDirectionCode")
    protected LatitudeDirectionCodeType latitudeDirectionCode;
    @XmlElement(name = "LongitudeDegreesMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected LongitudeDegreesMeasureType longitudeDegreesMeasure;
    @XmlElement(name = "LongitudeMinutesMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected LongitudeMinutesMeasureType longitudeMinutesMeasure;
    @XmlElement(name = "LongitudeDirectionCode")
    protected LongitudeDirectionCodeType longitudeDirectionCode;

    /**
     * Gets the value of the coordinateSystemCode property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getCoordinateSystemCode() {
        return coordinateSystemCode;
    }

    /**
     * Sets the value of the coordinateSystemCode property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setCoordinateSystemCode(CodeType value) {
        this.coordinateSystemCode = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Location Coordinate. Latitude Degrees. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;specifies the degrees measurement of latitude as part of a detailed set of coordinates.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Location Coordinate&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Latitude Degrees&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link LatitudeDegreesMeasureType }
     *
     */
    public LatitudeDegreesMeasureType getLatitudeDegreesMeasure() {
        return latitudeDegreesMeasure;
    }

    /**
     * Sets the value of the latitudeDegreesMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link LatitudeDegreesMeasureType }
     *
     */
    public void setLatitudeDegreesMeasure(LatitudeDegreesMeasureType value) {
        this.latitudeDegreesMeasure = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Location Coordinate. Latitude Minutes. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;specifies the minutes measurement of latitude as part of a detailed set of coordinates.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Location Coordinate&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Latitude Minutes&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link LatitudeMinutesMeasureType }
     *
     */
    public LatitudeMinutesMeasureType getLatitudeMinutesMeasure() {
        return latitudeMinutesMeasure;
    }

    /**
     * Sets the value of the latitudeMinutesMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link LatitudeMinutesMeasureType }
     *
     */
    public void setLatitudeMinutesMeasure(LatitudeMinutesMeasureType value) {
        this.latitudeMinutesMeasure = value;
    }

    /**
     * Gets the value of the latitudeDirectionCode property.
     *
     * @return
     *     possible object is
     *     {@link LatitudeDirectionCodeType }
     *
     */
    public LatitudeDirectionCodeType getLatitudeDirectionCode() {
        return latitudeDirectionCode;
    }

    /**
     * Sets the value of the latitudeDirectionCode property.
     *
     * @param value
     *     allowed object is
     *     {@link LatitudeDirectionCodeType }
     *
     */
    public void setLatitudeDirectionCode(LatitudeDirectionCodeType value) {
        this.latitudeDirectionCode = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Location Coordinate. Longitude Degrees. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;specifies the degrees measurement of longitude as part of a detailed set of coordinates.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Location Coordinate&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Longitude Degrees&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link LongitudeDegreesMeasureType }
     *
     */
    public LongitudeDegreesMeasureType getLongitudeDegreesMeasure() {
        return longitudeDegreesMeasure;
    }

    /**
     * Sets the value of the longitudeDegreesMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link LongitudeDegreesMeasureType }
     *
     */
    public void setLongitudeDegreesMeasure(LongitudeDegreesMeasureType value) {
        this.longitudeDegreesMeasure = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Location Coordinate. Longitude Minutes. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;specifies the minutes measurement of longitude as part of a detailed set of coordinates.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Location Coordinate&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Longitude Minutes&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link LongitudeMinutesMeasureType }
     *
     */
    public LongitudeMinutesMeasureType getLongitudeMinutesMeasure() {
        return longitudeMinutesMeasure;
    }

    /**
     * Sets the value of the longitudeMinutesMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link LongitudeMinutesMeasureType }
     *
     */
    public void setLongitudeMinutesMeasure(LongitudeMinutesMeasureType value) {
        this.longitudeMinutesMeasure = value;
    }

    /**
     * Gets the value of the longitudeDirectionCode property.
     *
     * @return
     *     possible object is
     *     {@link LongitudeDirectionCodeType }
     *
     */
    public LongitudeDirectionCodeType getLongitudeDirectionCode() {
        return longitudeDirectionCode;
    }

    /**
     * Sets the value of the longitudeDirectionCode property.
     *
     * @param value
     *     allowed object is
     *     {@link LongitudeDirectionCodeType }
     *
     */
    public void setLongitudeDirectionCode(LongitudeDirectionCodeType value) {
        this.longitudeDirectionCode = value;
    }

}
