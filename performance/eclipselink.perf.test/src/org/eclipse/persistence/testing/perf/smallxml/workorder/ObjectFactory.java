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
package org.eclipse.persistence.testing.perf.smallxml.workorder;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.eclipse.persistence.testing.pef.smallxml.workorder package.
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

    private final static QName _DoWorkItem_QNAME = new QName("http://org.eclipse.persistence.testing.perf/workItem", "doWorkItem");
    private final static QName _DoWorkItemResponse_QNAME = new QName("http://org.eclipse.persistence.testing.perf/workItem", "doWorkItemResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.peristence.testing.perf.smallxml.workorder
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DoWorkItem }
     *
     */
    public DoWorkItem createDoWorkItem() {
        return new DoWorkItem();
    }

    /**
     * Create an instance of {@link DoWorkItemResponse }
     *
     */
    public DoWorkItemResponse createDoWorkItemResponse() {
        return new DoWorkItemResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoWorkItem }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://org.eclipse.persistence.testing.perf/workItem", name = "doWorkItem")
    public JAXBElement<DoWorkItem> createDoWorkItem(DoWorkItem value) {
        return new JAXBElement<DoWorkItem>(_DoWorkItem_QNAME, DoWorkItem.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DoWorkItemResponse }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://org.eclipse.persistence.testing.perf/workItem", name = "doWorkItemResponse")
    public JAXBElement<DoWorkItemResponse> createDoWorkItemResponse(DoWorkItemResponse value) {
        return new JAXBElement<DoWorkItemResponse>(_DoWorkItemResponse_QNAME, DoWorkItemResponse.class, null, value);
    }

}
