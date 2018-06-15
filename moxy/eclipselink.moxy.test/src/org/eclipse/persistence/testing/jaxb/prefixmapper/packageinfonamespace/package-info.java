/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/16/2018-2.7.2 Radek Felcman
//       - 531349 - @XmlSchema Prefix is not honoured if root element is nil

@XmlSchema(
        namespace = "NS", elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(namespaceURI = "extraUri", prefix = "PRE"),
                @XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi") })
        package org.eclipse.persistence.testing.jaxb.prefixmapper.packageinfonamespace;

            import javax.xml.bind.annotation.XmlNs;
            import javax.xml.bind.annotation.XmlNsForm;
            import javax.xml.bind.annotation.XmlSchema;
