/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - November 09/2010 - 2.2 - Initial implementation
@javax.xml.bind.annotation.XmlSchema(
        namespace="http://www.eclipse.org/eclipselink/xsds/persistence/oxm/junk",
        attributeFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        elementFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        xmlns= {
            @javax.xml.bind.annotation.XmlNs(prefix="nsx", namespaceURI="http://www.example.com/xsds/fake")
        })
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete;
