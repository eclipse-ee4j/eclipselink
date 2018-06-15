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
package org.eclipse.persistence.testing.perf.largexml.bigpo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.eclipse.persistence.testing.perf.largexml.bigpo package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 * </p>
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _BigPurchaseOrder_QNAME = new QName("http://org.eclipse.persistence.testing.perf/bigPurchaseOrderworkItem", "BigPurchaseOrder");
    private final static QName _WrappedPurchaseOrder_QNAME = new QName("http://org.eclipse.persistence.testing.perf/bigPurchaseOrderworkItem", "WrappedPurchaseOrder");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.persistence.testing.perf.largexml.bigpo
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ItemType }
     *
     */
    public ItemType createItemType() {
        return new ItemType();
    }

    /**
     * Create an instance of {@link WrappedPurchaseOrderType }
     *
     */
    public WrappedPurchaseOrderType createWrappedPurchaseOrderType() {
        return new WrappedPurchaseOrderType();
    }

    /**
     * Create an instance of {@link BigPurchaseOrderType }
     *
     */
    public BigPurchaseOrderType createBigPurchaseOrderType() {
        return new BigPurchaseOrderType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigPurchaseOrderType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://org.eclipse.persistence.testing.perf/bigPurchaseOrderworkItem", name = "BigPurchaseOrder")
    public JAXBElement<BigPurchaseOrderType> createBigPurchaseOrder(BigPurchaseOrderType value) {
        return new JAXBElement<BigPurchaseOrderType>(_BigPurchaseOrder_QNAME, BigPurchaseOrderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WrappedPurchaseOrderType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://org.eclipse.persistence.testing.perf/bigPurchaseOrderworkItem", name = "WrappedPurchaseOrder")
    public JAXBElement<WrappedPurchaseOrderType> createWrappedPurchaseOrder(WrappedPurchaseOrderType value) {
        return new JAXBElement<WrappedPurchaseOrderType>(_WrappedPurchaseOrder_QNAME, WrappedPurchaseOrderType.class, null, value);
    }

}
