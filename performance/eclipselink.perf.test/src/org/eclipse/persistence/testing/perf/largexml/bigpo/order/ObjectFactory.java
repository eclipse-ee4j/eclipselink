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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.CountryType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.DocumentReferenceType;
import org.eclipse.persistence.testing.perf.largexml.bigpo.common_aggregate_components.PartyType;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.eclipse.persistence.testing.perf.largexml.bigpo.order package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _PackagesQuantity_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "PackagesQuantity");
    private final static QName _TotalPackagesQuantity_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "TotalPackagesQuantity");
    private final static QName _AdditionalDocumentReference_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "AdditionalDocumentReference");
    private final static QName _ValidityDurationMeasure_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "ValidityDurationMeasure");
    private final static QName _QuoteDocumentReference_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "QuoteDocumentReference");
    private final static QName _FreightForwarderParty_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "FreightForwarderParty");
    private final static QName _EarliestDate_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "EarliestDate");
    private final static QName _OriginatorParty_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "OriginatorParty");
    private final static QName _ContractDocumentReference_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "ContractDocumentReference");
    private final static QName _Order_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "Order");
    private final static QName _LineItemCountNumeric_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "LineItemCountNumeric");
    private final static QName _DestinationCountry_QNAME = new QName("urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", "DestinationCountry");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.persistence.testing.perf.largexml.bigpo.order
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LineItemCountNumericType }
     *
     */
    public LineItemCountNumericType createLineItemCountNumericType() {
        return new LineItemCountNumericType();
    }

    /**
     * Create an instance of {@link PackagesQuantityType }
     *
     */
    public PackagesQuantityType createPackagesQuantityType() {
        return new PackagesQuantityType();
    }

    /**
     * Create an instance of {@link ValidityDurationMeasureType }
     *
     */
    public ValidityDurationMeasureType createValidityDurationMeasureType() {
        return new ValidityDurationMeasureType();
    }

    /**
     * Create an instance of {@link OrderType }
     *
     */
    public OrderType createOrderType() {
        return new OrderType();
    }

    /**
     * Create an instance of {@link EarliestDateType }
     *
     */
    public EarliestDateType createEarliestDateType() {
        return new EarliestDateType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PackagesQuantityType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "PackagesQuantity")
    public JAXBElement<PackagesQuantityType> createPackagesQuantity(PackagesQuantityType value) {
        return new JAXBElement<PackagesQuantityType>(_PackagesQuantity_QNAME, PackagesQuantityType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PackagesQuantityType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "TotalPackagesQuantity")
    public JAXBElement<PackagesQuantityType> createTotalPackagesQuantity(PackagesQuantityType value) {
        return new JAXBElement<PackagesQuantityType>(_TotalPackagesQuantity_QNAME, PackagesQuantityType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentReferenceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "AdditionalDocumentReference")
    public JAXBElement<DocumentReferenceType> createAdditionalDocumentReference(DocumentReferenceType value) {
        return new JAXBElement<DocumentReferenceType>(_AdditionalDocumentReference_QNAME, DocumentReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidityDurationMeasureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "ValidityDurationMeasure")
    public JAXBElement<ValidityDurationMeasureType> createValidityDurationMeasure(ValidityDurationMeasureType value) {
        return new JAXBElement<ValidityDurationMeasureType>(_ValidityDurationMeasure_QNAME, ValidityDurationMeasureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentReferenceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "QuoteDocumentReference")
    public JAXBElement<DocumentReferenceType> createQuoteDocumentReference(DocumentReferenceType value) {
        return new JAXBElement<DocumentReferenceType>(_QuoteDocumentReference_QNAME, DocumentReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PartyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "FreightForwarderParty")
    public JAXBElement<PartyType> createFreightForwarderParty(PartyType value) {
        return new JAXBElement<PartyType>(_FreightForwarderParty_QNAME, PartyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EarliestDateType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "EarliestDate")
    public JAXBElement<EarliestDateType> createEarliestDate(EarliestDateType value) {
        return new JAXBElement<EarliestDateType>(_EarliestDate_QNAME, EarliestDateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PartyType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "OriginatorParty")
    public JAXBElement<PartyType> createOriginatorParty(PartyType value) {
        return new JAXBElement<PartyType>(_OriginatorParty_QNAME, PartyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentReferenceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "ContractDocumentReference")
    public JAXBElement<DocumentReferenceType> createContractDocumentReference(DocumentReferenceType value) {
        return new JAXBElement<DocumentReferenceType>(_ContractDocumentReference_QNAME, DocumentReferenceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrderType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "Order")
    public JAXBElement<OrderType> createOrder(OrderType value) {
        return new JAXBElement<OrderType>(_Order_QNAME, OrderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineItemCountNumericType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "LineItemCountNumeric")
    public JAXBElement<LineItemCountNumericType> createLineItemCountNumeric(LineItemCountNumericType value) {
        return new JAXBElement<LineItemCountNumericType>(_LineItemCountNumeric_QNAME, LineItemCountNumericType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "urn:oasis:names:specification:ubl:schema:xsd:Order-1.0", name = "DestinationCountry")
    public JAXBElement<CountryType> createDestinationCountry(CountryType value) {
        return new JAXBElement<CountryType>(_DestinationCountry_QNAME, CountryType.class, null, value);
    }

}
