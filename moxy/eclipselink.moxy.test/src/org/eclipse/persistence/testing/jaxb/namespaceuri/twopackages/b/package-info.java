/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
@XmlSchema(namespace="http://www.example.com/B", elementFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
    xmlns = {@javax.xml.bind.annotation.XmlNs(prefix="two", namespaceURI="twoURI")}
)
package org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.b;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
