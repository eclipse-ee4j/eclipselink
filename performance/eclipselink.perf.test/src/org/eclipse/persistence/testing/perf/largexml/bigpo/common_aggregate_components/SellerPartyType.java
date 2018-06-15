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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.unspecialized_data_types.IdentifierType;


/**
 *
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ABIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Seller Party. Details&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;details of an individual, a group or a body having a role in a business function.&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Seller Party&lt;/ccts:ObjectClass&gt;&lt;/ccts:Component&gt;
 * </pre>
 *
 *
 * <p>Java class for SellerPartyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SellerPartyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BuyerAssignedAccountID" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}IdentifierType" minOccurs="0"/>
 *         &lt;element name="SellerAssignedAccountID" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}IdentifierType" minOccurs="0"/>
 *         &lt;element name="AdditionalAccountID" type="{urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0}IdentifierType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}Party" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}ShippingContact" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}AccountsContact" minOccurs="0"/>
 *         &lt;element ref="{urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0}OrderContact" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SellerPartyType", propOrder = {
    "buyerAssignedAccountID",
    "sellerAssignedAccountID",
    "additionalAccountID",
    "party",
    "shippingContact",
    "accountsContact",
    "orderContact"
})
public class SellerPartyType {

    @XmlElement(name = "BuyerAssignedAccountID")
    protected IdentifierType buyerAssignedAccountID;
    @XmlElement(name = "SellerAssignedAccountID")
    protected IdentifierType sellerAssignedAccountID;
    @XmlElement(name = "AdditionalAccountID")
    protected List<IdentifierType> additionalAccountID;
    @XmlElement(name = "Party")
    protected PartyType party;
    @XmlElement(name = "ShippingContact")
    protected ContactType shippingContact;
    @XmlElement(name = "AccountsContact")
    protected ContactType accountsContact;
    @XmlElement(name = "OrderContact")
    protected ContactType orderContact;

    /**
     * Gets the value of the buyerAssignedAccountID property.
     *
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *
     */
    public IdentifierType getBuyerAssignedAccountID() {
        return buyerAssignedAccountID;
    }

    /**
     * Sets the value of the buyerAssignedAccountID property.
     *
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *
     */
    public void setBuyerAssignedAccountID(IdentifierType value) {
        this.buyerAssignedAccountID = value;
    }

    /**
     * Gets the value of the sellerAssignedAccountID property.
     *
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *
     */
    public IdentifierType getSellerAssignedAccountID() {
        return sellerAssignedAccountID;
    }

    /**
     * Sets the value of the sellerAssignedAccountID property.
     *
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *
     */
    public void setSellerAssignedAccountID(IdentifierType value) {
        this.sellerAssignedAccountID = value;
    }

    /**
     * Gets the value of the additionalAccountID property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalAccountID property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalAccountID().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdentifierType }
     *
     *
     */
    public List<IdentifierType> getAdditionalAccountID() {
        if (additionalAccountID == null) {
            additionalAccountID = new ArrayList<IdentifierType>();
        }
        return this.additionalAccountID;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Seller Party. Party&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates (optionally) the seller party with general details about the party&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Seller Party&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Party&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Party&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link PartyType }
     *
     */
    public PartyType getParty() {
        return party;
    }

    /**
     * Sets the value of the party property.
     *
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *
     */
    public void setParty(PartyType value) {
        this.party = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Seller Party. Shipping_ Contact. Contact&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates (optionally) the party with information on the shipping contact&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Seller Party&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Shipping&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Contact&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Contact&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link ContactType }
     *
     */
    public ContactType getShippingContact() {
        return shippingContact;
    }

    /**
     * Sets the value of the shippingContact property.
     *
     * @param value
     *     allowed object is
     *     {@link ContactType }
     *
     */
    public void setShippingContact(ContactType value) {
        this.shippingContact = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Seller Party. Accounts_ Contact. Contact&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates (optionally) the party with information that identifies the Seller's contact person or department on Accounts matters, together with information about how they can be contacted.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Seller Party&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Accounts&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Contact&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Contact&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link ContactType }
     *
     */
    public ContactType getAccountsContact() {
        return accountsContact;
    }

    /**
     * Sets the value of the accountsContact property.
     *
     * @param value
     *     allowed object is
     *     {@link ContactType }
     *
     */
    public void setAccountsContact(ContactType value) {
        this.accountsContact = value;
    }

    /**
     *
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:oasis:names:specification:ubl:schema:xsd:CoreComponentParameters-1.0" xmlns="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-1.0" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-1.0" xmlns:chc="urn:oasis:names:specification:ubl:schema:xsd:ChipCode-1.0" xmlns:chn="urn:oasis:names:specification:ubl:schema:xsd:ChannelCode-1.0" xmlns:cnt="urn:oasis:names:specification:ubl:schema:xsd:CountryIdentificationCode-1.0" xmlns:cur="urn:oasis:names:specification:ubl:schema:xsd:CurrencyCode-1.0" xmlns:ero="urn:oasis:names:specification:ubl:schema:xsd:OperatorCode-1.0" xmlns:lat="urn:oasis:names:specification:ubl:schema:xsd:LatitudeDirectionCode-1.0" xmlns:lon="urn:oasis:names:specification:ubl:schema:xsd:LongitudeDirectionCode-1.0" xmlns:lstat="urn:oasis:names:specification:ubl:schema:xsd:LineStatusCode-1.0" xmlns:pty="urn:oasis:names:specification:ubl:schema:xsd:PaymentMeansCode-1.0" xmlns:rsn="urn:oasis:names:specification:ubl:schema:xsd:AllowanceChargeReasonCode-1.0" xmlns:sdt="urn:oasis:names:specification:ubl:schema:xsd:SpecializedDatatypes-1.0" xmlns:sst="urn:oasis:names:specification:ubl:schema:xsd:SubstitutionStatusCode-1.0" xmlns:stat="urn:oasis:names:specification:ubl:schema:xsd:DocumentStatusCode-1.0" xmlns:udt="urn:oasis:names:specification:ubl:schema:xsd:UnspecializedDatatypes-1.0" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Seller Party. Order_ Contact. Contact&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;associates (optionally) the party with information on the order contact (of the seller)&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Seller Party&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Order&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Contact&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Contact&lt;/ccts:AssociatedObjectClass&gt;&lt;/ccts:Component&gt;
     * </pre>
     *
     *
     * @return
     *     possible object is
     *     {@link ContactType }
     *
     */
    public ContactType getOrderContact() {
        return orderContact;
    }

    /**
     * Sets the value of the orderContact property.
     *
     * @param value
     *     allowed object is
     *     {@link ContactType }
     *
     */
    public void setOrderContact(ContactType value) {
        this.orderContact = value;
    }

}
