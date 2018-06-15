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
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_basic_components.PaymentDateType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.payment_means_code.PaymentMeansCodeType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.CodeType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ABIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Payment Means. Details&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;information directly relating to the means of payment.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Payment Means&lt;/ccts:ObjectClass&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for PaymentMeansType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PaymentMeansType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PaymentMeansCode" type="{urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0}PaymentMeansCodeType"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0}DuePaymentDate" minOccurs="0"/>
 *         &lt;element name="PaymentChannelCode" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}CodeType" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}CardAccount" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}PayerFinancialAccount" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}PayeeFinancialAccount" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}CreditAccount" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}Payment" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentMeansType", propOrder = {
    "paymentMeansCode",
    "duePaymentDate",
    "paymentChannelCode",
    "cardAccount",
    "payerFinancialAccount",
    "payeeFinancialAccount",
    "creditAccount",
    "payment"
})
public class PaymentMeansType {

    @XmlElement(name = "PaymentMeansCode", required = true)
    protected PaymentMeansCodeType paymentMeansCode;
    @XmlElement(name = "DuePaymentDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0")
    protected PaymentDateType duePaymentDate;
    @XmlElement(name = "PaymentChannelCode")
    protected CodeType paymentChannelCode;
    @XmlElement(name = "CardAccount")
    protected CardAccountType cardAccount;
    @XmlElement(name = "PayerFinancialAccount")
    protected FinancialAccountType payerFinancialAccount;
    @XmlElement(name = "PayeeFinancialAccount")
    protected FinancialAccountType payeeFinancialAccount;
    @XmlElement(name = "CreditAccount")
    protected CreditAccountType creditAccount;
    @XmlElement(name = "Payment")
    protected PaymentType payment;

    /**
     * Gets the value of the paymentMeansCode property.
     *
     * @return
     *     possible object is
     *     {@link PaymentMeansCodeType }
     *
     */
    public PaymentMeansCodeType getPaymentMeansCode() {
        return paymentMeansCode;
    }

    /**
     * Sets the value of the paymentMeansCode property.
     *
     * @param value
     *     allowed object is
     *     {@link PaymentMeansCodeType }
     *
     */
    public void setPaymentMeansCode(PaymentMeansCodeType value) {
        this.paymentMeansCode = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Payment Means. Due_ Payment Date. Date&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;the point in time at which the payment is to be made.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Payment Means&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Due&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Payment Date&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Date&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Date_Date Time. Type&lt;/ccts:DataType&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link PaymentDateType }
     *
     */
    public PaymentDateType getDuePaymentDate() {
        return duePaymentDate;
    }

    /**
     * Sets the value of the duePaymentDate property.
     *
     * @param value
     *     allowed object is
     *     {@link PaymentDateType }
     *
     */
    public void setDuePaymentDate(PaymentDateType value) {
        this.duePaymentDate = value;
    }

    /**
     * Gets the value of the paymentChannelCode property.
     *
     * @return
     *     possible object is
     *     {@link CodeType }
     *
     */
    public CodeType getPaymentChannelCode() {
        return paymentChannelCode;
    }

    /**
     * Sets the value of the paymentChannelCode property.
     *
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *
     */
    public void setPaymentChannelCode(CodeType value) {
        this.paymentChannelCode = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Payment Means. Card Account&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the payment means with information about the credit/debit card specifed as the way payment would be made.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Payment Means&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Card Account&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Card Account&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link CardAccountType }
     *
     */
    public CardAccountType getCardAccount() {
        return cardAccount;
    }

    /**
     * Sets the value of the cardAccount property.
     *
     * @param value
     *     allowed object is
     *     {@link CardAccountType }
     *
     */
    public void setCardAccount(CardAccountType value) {
        this.cardAccount = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Payment Means. Payer_ Financial Account. Financial Account&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the payment means with information about bank account of the Payer (the party to make the payment), given as the way payment would be made.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Payment Means&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Payer&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Financial Account&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Financial Account&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link FinancialAccountType }
     *
     */
    public FinancialAccountType getPayerFinancialAccount() {
        return payerFinancialAccount;
    }

    /**
     * Sets the value of the payerFinancialAccount property.
     *
     * @param value
     *     allowed object is
     *     {@link FinancialAccountType }
     *
     */
    public void setPayerFinancialAccount(FinancialAccountType value) {
        this.payerFinancialAccount = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Payment Means. Payee_ Financial Account. Financial Account&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the payment means with information about bank account of the Payee (the party to receive the payment).&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Payment Means&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Payee&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Financial Account&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Financial Account&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link FinancialAccountType }
     *
     */
    public FinancialAccountType getPayeeFinancialAccount() {
        return payeeFinancialAccount;
    }

    /**
     * Sets the value of the payeeFinancialAccount property.
     *
     * @param value
     *     allowed object is
     *     {@link FinancialAccountType }
     *
     */
    public void setPayeeFinancialAccount(FinancialAccountType value) {
        this.payeeFinancialAccount = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Payment Means. Credit Account&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the payment means with an on account credit account.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Payment Means&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Credit Account&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Credit Account&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link CreditAccountType }
     *
     */
    public CreditAccountType getCreditAccount() {
        return creditAccount;
    }

    /**
     * Sets the value of the creditAccount property.
     *
     * @param value
     *     allowed object is
     *     {@link CreditAccountType }
     *
     */
    public void setCreditAccount(CreditAccountType value) {
        this.creditAccount = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Payment Means. Payment&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates the payment means with information about the payment to be made by that means.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Payment Means&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Payment&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Payment&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link PaymentType }
     *
     */
    public PaymentType getPayment() {
        return payment;
    }

    /**
     * Sets the value of the payment property.
     *
     * @param value
     *     allowed object is
     *     {@link PaymentType }
     *
     */
    public void setPayment(PaymentType value) {
        this.payment = value;
    }

}
