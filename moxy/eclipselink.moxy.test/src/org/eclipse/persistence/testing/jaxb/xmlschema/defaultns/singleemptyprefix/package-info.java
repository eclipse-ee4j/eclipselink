/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

@XmlSchema(
    elementFormDefault=XmlNsForm.QUALIFIED,
    namespace="http://www.example.com/FOO",
    xmlns={
        @XmlNs(prefix="", namespaceURI="http://www.example.com/FOO")
    }
)
package org.eclipse.persistence.testing.jaxb.xmlschema.defaultns.singleemptyprefix;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
