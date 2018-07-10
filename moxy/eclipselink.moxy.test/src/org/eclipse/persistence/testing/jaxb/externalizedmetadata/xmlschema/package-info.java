/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - June 17/2009 - 2.0 - Initial implementation
@javax.xml.bind.annotation.XmlSchema(
        namespace="http://www.eclipse.org/eclipselink/xsds/persistence/oxm/junk",
        attributeFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        elementFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        xmlns= {
            @javax.xml.bind.annotation.XmlNs(prefix="nsx", namespaceURI="http://www.example.com/xsds/fake")
        })
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema;
