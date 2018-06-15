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
package org.eclipse.persistence.testing.perf.largexml.bigpo.order;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.acknowledgement_response_code.AcknowledgementResponseCodeType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.AllowanceChargeType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.BuyerPartyType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.CountryType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.DeliveryTermsType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.DeliveryType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.DocumentReferenceType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.OrderLineType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.PartyType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.PaymentMeansType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.SalesConditionsType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.SellerPartyType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.CopyIndicatorType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.ExpiryDateType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.ExtensionTotalAmountType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.IssueDateType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.NoteType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.TaxTotalAmountType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.VolumeMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.WeightMeasureType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.currency_code.CurrencyCodeType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.IdentifierType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ABIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Details&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;a document that contains information directly relating to the economic event of ordering products.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:AlternativeBusinessTerms&gt;Purchase Order&lt;/ccts:AlternativeBusinessTerms&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for OrderType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OrderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BuyersID" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}IdentifierType" minOccurs="0"/>
 *         &lt;element name="SellersID" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}IdentifierType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}CopyIndicator" minOccurs="0"/>
 *         &lt;element name="GUID" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}IdentifierType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}IssueDate"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}Note" minOccurs="0"/>
 *         &lt;element name="AcknowledgementResponseCode" type="{urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0}AcknowledgementResponseCodeType" minOccurs="0"/>
 *         &lt;element name="TransactionCurrencyCode" type="{urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0}CurrencyCodeType" minOccurs="0"/>
 *         &lt;element name="PricingCurrencyCode" type="{urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0}CurrencyCodeType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}EarliestDate" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}ExpiryDate" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}ValidityDurationMeasure" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}TaxTotalAmount" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}LineExtensionTotalAmount" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}TotalPackagesQuantity" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}GrossWeightMeasure" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}NetWeightMeasure" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}NetNetWeightMeasure" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}GrossVolumeMeasure" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}NetVolumeMeasure" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}LineItemCountNumeric" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}ContractDocumentReference" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}QuoteDocumentReference" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}AdditionalDocumentReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}BuyerParty"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}SellerParty"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}OriginatorParty" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}FreightForwarderParty" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}Delivery" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}DeliveryTerms" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}AllowanceCharge" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}SalesConditions" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}DestinationCountry" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}OrderLine" maxOccurs="unbounded"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}PaymentMeans" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderType", propOrder = {
    "buyersID",
    "sellersID",
    "copyIndicator",
    "guid",
    "issueDate",
    "note",
    "acknowledgementResponseCode",
    "transactionCurrencyCode",
    "pricingCurrencyCode",
    "earliestDate",
    "expiryDate",
    "validityDurationMeasure",
    "taxTotalAmount",
    "lineExtensionTotalAmount",
    "totalPackagesQuantity",
    "grossWeightMeasure",
    "netWeightMeasure",
    "netNetWeightMeasure",
    "grossVolumeMeasure",
    "netVolumeMeasure",
    "lineItemCountNumeric",
    "contractDocumentReference",
    "quoteDocumentReference",
    "additionalDocumentReference",
    "buyerParty",
    "sellerParty",
    "originatorParty",
    "freightForwarderParty",
    "delivery",
    "deliveryTerms",
    "allowanceCharge",
    "salesConditions",
    "destinationCountry",
    "orderLine",
    "paymentMeans"
})
public class OrderType {

    @XmlElement(name = "BuyersID")
    protected IdentifierType buyersID;
    @XmlElement(name = "SellersID")
    protected IdentifierType sellersID;
    @XmlElement(name = "CopyIndicator", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected CopyIndicatorType copyIndicator;
    @XmlElement(name = "GUID")
    protected IdentifierType guid;
    @XmlElement(name = "IssueDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0", required = true)
    protected IssueDateType issueDate;
    @XmlElement(name = "Note", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected NoteType note;
    @XmlElement(name = "AcknowledgementResponseCode")
    protected AcknowledgementResponseCodeType acknowledgementResponseCode;
    @XmlElement(name = "TransactionCurrencyCode")
    protected CurrencyCodeType transactionCurrencyCode;
    @XmlElement(name = "PricingCurrencyCode")
    protected CurrencyCodeType pricingCurrencyCode;
    @XmlElement(name = "EarliestDate")
    protected EarliestDateType earliestDate;
    @XmlElement(name = "ExpiryDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected ExpiryDateType expiryDate;
    @XmlElement(name = "ValidityDurationMeasure")
    protected ValidityDurationMeasureType validityDurationMeasure;
    @XmlElement(name = "TaxTotalAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected TaxTotalAmountType taxTotalAmount;
    @XmlElement(name = "LineExtensionTotalAmount", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected ExtensionTotalAmountType lineExtensionTotalAmount;
    @XmlElement(name = "TotalPackagesQuantity")
    protected PackagesQuantityType totalPackagesQuantity;
    @XmlElement(name = "GrossWeightMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected WeightMeasureType grossWeightMeasure;
    @XmlElement(name = "NetWeightMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected WeightMeasureType netWeightMeasure;
    @XmlElement(name = "NetNetWeightMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected WeightMeasureType netNetWeightMeasure;
    @XmlElement(name = "GrossVolumeMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected VolumeMeasureType grossVolumeMeasure;
    @XmlElement(name = "NetVolumeMeasure", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected VolumeMeasureType netVolumeMeasure;
    @XmlElement(name = "LineItemCountNumeric")
    protected LineItemCountNumericType lineItemCountNumeric;
    @XmlElement(name = "ContractDocumentReference")
    protected DocumentReferenceType contractDocumentReference;
    @XmlElement(name = "QuoteDocumentReference")
    protected DocumentReferenceType quoteDocumentReference;
    @XmlElement(name = "AdditionalDocumentReference")
    protected List<DocumentReferenceType> additionalDocumentReference;
    @XmlElement(name = "BuyerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0", required = true)
    protected BuyerPartyType buyerParty;
    @XmlElement(name = "SellerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0", required = true)
    protected SellerPartyType sellerParty;
    @XmlElement(name = "OriginatorParty")
    protected PartyType originatorParty;
    @XmlElement(name = "FreightForwarderParty")
    protected PartyType freightForwarderParty;
    @XmlElement(name = "Delivery", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0")
    protected List<DeliveryType> delivery;
    @XmlElement(name = "DeliveryTerms", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0")
    protected DeliveryTermsType deliveryTerms;
    @XmlElement(name = "AllowanceCharge", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0")
    protected List<AllowanceChargeType> allowanceCharge;
    @XmlElement(name = "SalesConditions", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0")
    protected SalesConditionsType salesConditions;
    @XmlElement(name = "DestinationCountry")
    protected CountryType destinationCountry;
    @XmlElement(name = "OrderLine", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0", required = true)
    protected List<OrderLineType> orderLine;
    @XmlElement(name = "PaymentMeans", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0")
    protected PaymentMeansType paymentMeans;

    /**
     * Gets the value of the buyersID property.
     *
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *
     */
    public IdentifierType getBuyersID() {
        return buyersID;
    }

    /**
     * Sets the value of the buyersID property.
     *
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *
     */
    public void setBuyersID(IdentifierType value) {
        this.buyersID = value;
    }

    /**
     * Gets the value of the sellersID property.
     *
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *
     */
    public IdentifierType getSellersID() {
        return sellersID;
    }

    /**
     * Sets the value of the sellersID property.
     *
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *
     */
    public void setSellersID(IdentifierType value) {
        this.sellersID = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Copy. Indicator&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;Indicates whether a document is a copy (true) or not (false)&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Copy&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Indicator&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Indicator. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link CopyIndicatorType }
     *
     */
    public CopyIndicatorType getCopyIndicator() {
        return copyIndicator;
    }

    /**
     * Sets the value of the copyIndicator property.
     *
     * @param value
     *     allowed object is
     *     {@link CopyIndicatorType }
     *
     */
    public void setCopyIndicator(CopyIndicatorType value) {
        this.copyIndicator = value;
    }

    /**
     * Gets the value of the guid property.
     *
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *
     */
    public IdentifierType getGUID() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     *
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *
     */
    public void setGUID(IdentifierType value) {
        this.guid = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Issue Date. Date&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;a date (and potentially time) stamp denoting when the Order was issued.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Issue Date&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Date&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Date_Date Time. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link IssueDateType }
     *
     */
    public IssueDateType getIssueDate() {
        return issueDate;
    }

    /**
     * Sets the value of the issueDate property.
     *
     * @param value
     *     allowed object is
     *     {@link IssueDateType }
     *
     */
    public void setIssueDate(IssueDateType value) {
        this.issueDate = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Note. Text&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;contains any free form text pertinent to the entire document or to the document message itself. This element may contain notes or any other similar information that is not contained explicitly in another structure.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Note&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Text&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Text. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link NoteType }
     *
     */
    public NoteType getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     *
     * @param value
     *     allowed object is
     *     {@link NoteType }
     *
     */
    public void setNote(NoteType value) {
        this.note = value;
    }

    /**
     * Gets the value of the acknowledgementResponseCode property.
     *
     * @return
     *     possible object is
     *     {@link AcknowledgementResponseCodeType }
     *
     */
    public AcknowledgementResponseCodeType getAcknowledgementResponseCode() {
        return acknowledgementResponseCode;
    }

    /**
     * Sets the value of the acknowledgementResponseCode property.
     *
     * @param value
     *     allowed object is
     *     {@link AcknowledgementResponseCodeType }
     *
     */
    public void setAcknowledgementResponseCode(AcknowledgementResponseCodeType value) {
        this.acknowledgementResponseCode = value;
    }

    /**
     * Gets the value of the transactionCurrencyCode property.
     *
     * @return
     *     possible object is
     *     {@link CurrencyCodeType }
     *
     */
    public CurrencyCodeType getTransactionCurrencyCode() {
        return transactionCurrencyCode;
    }

    /**
     * Sets the value of the transactionCurrencyCode property.
     *
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeType }
     *
     */
    public void setTransactionCurrencyCode(CurrencyCodeType value) {
        this.transactionCurrencyCode = value;
    }

    /**
     * Gets the value of the pricingCurrencyCode property.
     *
     * @return
     *     possible object is
     *     {@link CurrencyCodeType }
     *
     */
    public CurrencyCodeType getPricingCurrencyCode() {
        return pricingCurrencyCode;
    }

    /**
     * Sets the value of the pricingCurrencyCode property.
     *
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeType }
     *
     */
    public void setPricingCurrencyCode(CurrencyCodeType value) {
        this.pricingCurrencyCode = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Earliest Date. Date&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the starting date on or after which Order should be considered valid&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Earliest Date&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Date&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Date_Date Time. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link EarliestDateType }
     *
     */
    public EarliestDateType getEarliestDate() {
        return earliestDate;
    }

    /**
     * Sets the value of the earliestDate property.
     *
     * @param value
     *     allowed object is
     *     {@link EarliestDateType }
     *
     */
    public void setEarliestDate(EarliestDateType value) {
        this.earliestDate = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Expiry Date. Date&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the date on or after which Order should be cancelled if not satisfied.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Expiry Date&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Date&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Date_Date Time. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link ExpiryDateType }
     *
     */
    public ExpiryDateType getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     *
     * @param value
     *     allowed object is
     *     {@link ExpiryDateType }
     *
     */
    public void setExpiryDate(ExpiryDateType value) {
        this.expiryDate = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Validity Duration. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the period for which the Order is valid.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Validity Duration&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link ValidityDurationMeasureType }
     *
     */
    public ValidityDurationMeasureType getValidityDurationMeasure() {
        return validityDurationMeasure;
    }

    /**
     * Sets the value of the validityDurationMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link ValidityDurationMeasureType }
     *
     */
    public void setValidityDurationMeasure(ValidityDurationMeasureType value) {
        this.validityDurationMeasure = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Tax Total. Amount&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the total tax amount to be paid for the Order.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Tax Total&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Amount&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Amount. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link TaxTotalAmountType }
     *
     */
    public TaxTotalAmountType getTaxTotalAmount() {
        return taxTotalAmount;
    }

    /**
     * Sets the value of the taxTotalAmount property.
     *
     * @param value
     *     allowed object is
     *     {@link TaxTotalAmountType }
     *
     */
    public void setTaxTotalAmount(TaxTotalAmountType value) {
        this.taxTotalAmount = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Line_ Extension Total. Amount&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the total of line item extension amounts for the entire Order, but not adjusted by any payment settlement discount or taxation.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Line&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Extension Total&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Amount&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Amount. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link ExtensionTotalAmountType }
     *
     */
    public ExtensionTotalAmountType getLineExtensionTotalAmount() {
        return lineExtensionTotalAmount;
    }

    /**
     * Sets the value of the lineExtensionTotalAmount property.
     *
     * @param value
     *     allowed object is
     *     {@link ExtensionTotalAmountType }
     *
     */
    public void setLineExtensionTotalAmount(ExtensionTotalAmountType value) {
        this.lineExtensionTotalAmount = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Total_ Packages Quantity. Quantity&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the count of the total number of packages contained in the Order.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Total&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Packages Quantity&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Quantity&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Quantity. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link PackagesQuantityType }
     *
     */
    public PackagesQuantityType getTotalPackagesQuantity() {
        return totalPackagesQuantity;
    }

    /**
     * Sets the value of the totalPackagesQuantity property.
     *
     * @param value
     *     allowed object is
     *     {@link PackagesQuantityType }
     *
     */
    public void setTotalPackagesQuantity(PackagesQuantityType value) {
        this.totalPackagesQuantity = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Gross_ Weight. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the total gross weight of the order. (goods plus packaging plus transport equipment)&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Gross&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Weight&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link WeightMeasureType }
     *
     */
    public WeightMeasureType getGrossWeightMeasure() {
        return grossWeightMeasure;
    }

    /**
     * Sets the value of the grossWeightMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link WeightMeasureType }
     *
     */
    public void setGrossWeightMeasure(WeightMeasureType value) {
        this.grossWeightMeasure = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Net_ Weight. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the total net weight of the order. (goods plus packaging)&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Net&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Weight&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link WeightMeasureType }
     *
     */
    public WeightMeasureType getNetWeightMeasure() {
        return netWeightMeasure;
    }

    /**
     * Sets the value of the netWeightMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link WeightMeasureType }
     *
     */
    public void setNetWeightMeasure(WeightMeasureType value) {
        this.netWeightMeasure = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Net Net_ Weight. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the weight (mass) of the goods themselves without any packing.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Net Net&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Weight&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link WeightMeasureType }
     *
     */
    public WeightMeasureType getNetNetWeightMeasure() {
        return netNetWeightMeasure;
    }

    /**
     * Sets the value of the netNetWeightMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link WeightMeasureType }
     *
     */
    public void setNetNetWeightMeasure(WeightMeasureType value) {
        this.netNetWeightMeasure = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Gross_ Volume. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the total volume of the goods plus packaging on the Order.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Gross&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Volume&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link VolumeMeasureType }
     *
     */
    public VolumeMeasureType getGrossVolumeMeasure() {
        return grossVolumeMeasure;
    }

    /**
     * Sets the value of the grossVolumeMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link VolumeMeasureType }
     *
     */
    public void setGrossVolumeMeasure(VolumeMeasureType value) {
        this.grossVolumeMeasure = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Net_ Volume. Measure&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the total volume of the Order. (goods less packaging)&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Net&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Volume&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Measure&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Measure. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link VolumeMeasureType }
     *
     */
    public VolumeMeasureType getNetVolumeMeasure() {
        return netVolumeMeasure;
    }

    /**
     * Sets the value of the netVolumeMeasure property.
     *
     * @param value
     *     allowed object is
     *     {@link VolumeMeasureType }
     *
     */
    public void setNetVolumeMeasure(VolumeMeasureType value) {
        this.netVolumeMeasure = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. LineItem Count. Numeric&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the number of line items&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;LineItem Count&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Numeric&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Numeric. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link LineItemCountNumericType }
     *
     */
    public LineItemCountNumericType getLineItemCountNumeric() {
        return lineItemCountNumeric;
    }

    /**
     * Sets the value of the lineItemCountNumeric property.
     *
     * @param value
     *     allowed object is
     *     {@link LineItemCountNumericType }
     *
     */
    public void setLineItemCountNumeric(LineItemCountNumericType value) {
        this.lineItemCountNumeric = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Contract_ Document Reference. Document Reference&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with a previously agreed Contract.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Contract&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Document Reference&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Document Reference&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link DocumentReferenceType }
     *
     */
    public DocumentReferenceType getContractDocumentReference() {
        return contractDocumentReference;
    }

    /**
     * Sets the value of the contractDocumentReference property.
     *
     * @param value
     *     allowed object is
     *     {@link DocumentReferenceType }
     *
     */
    public void setContractDocumentReference(DocumentReferenceType value) {
        this.contractDocumentReference = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Quote_ Document Reference. Document Reference&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with a prior quote.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Quote&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Document Reference&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Document Reference&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link DocumentReferenceType }
     *
     */
    public DocumentReferenceType getQuoteDocumentReference() {
        return quoteDocumentReference;
    }

    /**
     * Sets the value of the quoteDocumentReference property.
     *
     * @param value
     *     allowed object is
     *     {@link DocumentReferenceType }
     *
     */
    public void setQuoteDocumentReference(DocumentReferenceType value) {
        this.quoteDocumentReference = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Additional_ Document Reference. Document Reference&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with one or more other identification means&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..n&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Additional&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Document Reference&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Document Reference&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     * Gets the value of the additionalDocumentReference property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalDocumentReference property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalDocumentReference().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentReferenceType }
     *
     *
     */
    public List<DocumentReferenceType> getAdditionalDocumentReference() {
        if (additionalDocumentReference == null) {
            additionalDocumentReference = new ArrayList<DocumentReferenceType>();
        }
        return this.additionalDocumentReference;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Buyer Party&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with information about the buyer involved in the transaction.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Buyer Party&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Buyer Party&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link BuyerPartyType }
     *
     */
    public BuyerPartyType getBuyerParty() {
        return buyerParty;
    }

    /**
     * Sets the value of the buyerParty property.
     *
     * @param value
     *     allowed object is
     *     {@link BuyerPartyType }
     *
     */
    public void setBuyerParty(BuyerPartyType value) {
        this.buyerParty = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Seller Party&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with information about the seller involved in the transaction.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Seller Party&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Seller Party&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link SellerPartyType }
     *
     */
    public SellerPartyType getSellerParty() {
        return sellerParty;
    }

    /**
     * Sets the value of the sellerParty property.
     *
     * @param value
     *     allowed object is
     *     {@link SellerPartyType }
     *
     */
    public void setSellerParty(SellerPartyType value) {
        this.sellerParty = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Originator_ Party. Party&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with information about the originator of the transaction.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Originator&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Party&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Party&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link PartyType }
     *
     */
    public PartyType getOriginatorParty() {
        return originatorParty;
    }

    /**
     * Sets the value of the originatorParty property.
     *
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *
     */
    public void setOriginatorParty(PartyType value) {
        this.originatorParty = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Freight Forwarder_ Party. Party&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with information about the freight forwarder involved in the transaction.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Freight Forwarder&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Party&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Party&lt;/ccts:AssociatedObjectClass&gt;&lt;ccts:AlternativeBusinessTerms&gt;Carrier&lt;/ccts:AlternativeBusinessTerms&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link PartyType }
     *
     */
    public PartyType getFreightForwarderParty() {
        return freightForwarderParty;
    }

    /**
     * Sets the value of the freightForwarderParty property.
     *
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *
     */
    public void setFreightForwarderParty(PartyType value) {
        this.freightForwarderParty = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Delivery&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with a delivery  (or deliveries)&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..n&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Delivery&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Delivery&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     * Gets the value of the delivery property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the delivery property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDelivery().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeliveryType }
     *
     *
     */
    public List<DeliveryType> getDelivery() {
        if (delivery == null) {
            delivery = new ArrayList<DeliveryType>();
        }
        return this.delivery;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Delivery Terms&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with the delivery terms agreed between seller and buyer with regard to the delivery of goods.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Delivery Terms&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Delivery Terms&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link DeliveryTermsType }
     *
     */
    public DeliveryTermsType getDeliveryTerms() {
        return deliveryTerms;
    }

    /**
     * Sets the value of the deliveryTerms property.
     *
     * @param value
     *     allowed object is
     *     {@link DeliveryTermsType }
     *
     */
    public void setDeliveryTerms(DeliveryTermsType value) {
        this.deliveryTerms = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Allowance Charge&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with one or more pricing components for overall charges allowances etc.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..n&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Allowance Charge&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Allowance Charge&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     * Gets the value of the allowanceCharge property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowanceCharge property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowanceCharge().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AllowanceChargeType }
     *
     *
     */
    public List<AllowanceChargeType> getAllowanceCharge() {
        if (allowanceCharge == null) {
            allowanceCharge = new ArrayList<AllowanceChargeType>();
        }
        return this.allowanceCharge;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Sales Conditions&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with a sales condition applying to the whole order.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Sales Conditions&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Sales Conditions&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link SalesConditionsType }
     *
     */
    public SalesConditionsType getSalesConditions() {
        return salesConditions;
    }

    /**
     * Sets the value of the salesConditions property.
     *
     * @param value
     *     allowed object is
     *     {@link SalesConditionsType }
     *
     */
    public void setSalesConditions(SalesConditionsType value) {
        this.salesConditions = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Destination_ Country. Country&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with the country of destination (for Customs purposes).&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Destination&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Country&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Country&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link CountryType }
     *
     */
    public CountryType getDestinationCountry() {
        return destinationCountry;
    }

    /**
     * Sets the value of the destinationCountry property.
     *
     * @param value
     *     allowed object is
     *     {@link CountryType }
     *
     */
    public void setDestinationCountry(CountryType value) {
        this.destinationCountry = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Order Line&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with one or more Line items.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1..n&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Order Line&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Order Line&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     * Gets the value of the orderLine property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the orderLine property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderLine().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderLineType }
     *
     *
     */
    public List<OrderLineType> getOrderLine() {
        if (orderLine == null) {
            orderLine = new ArrayList<OrderLineType>();
        }
        return this.orderLine;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:Order-1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:res="urn:oasis:names:specification:ubl:schema:xsd:AcknowledgementResponseCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Order. Payment Means&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the Order with the expected means of payment.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Order&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Payment Means&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Payment Means&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link PaymentMeansType }
     *
     */
    public PaymentMeansType getPaymentMeans() {
        return paymentMeans;
    }

    /**
     * Sets the value of the paymentMeans property.
     *
     * @param value
     *     allowed object is
     *     {@link PaymentMeansType }
     *
     */
    public void setPaymentMeans(PaymentMeansType value) {
        this.paymentMeans = value;
    }

}
