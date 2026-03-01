/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.3.3 - initial implementation
@XmlSchema(namespace="http://www.example.com/B", elementFormDefault=jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    xmlns = {@jakarta.xml.bind.annotation.XmlNs(prefix="two", namespaceURI="twoURI")}
)
package org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.b;

import jakarta.xml.bind.annotation.XmlSchema;
