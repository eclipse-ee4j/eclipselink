/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.4 - initial implementation
@XmlSchema(
  namespace = Namespace.NAME, elementFormDefault = XmlNsForm.QUALIFIED,
  xmlns = {
    @XmlNs(namespaceURI = Namespace.NAME, prefix = Namespace.PREFIX),
    @XmlNs(namespaceURI = "http://www.w3.org/2001/XMLSchema-instance", prefix = "xsi") })
package org.eclipse.persistence.testing.jaxb.json.type.modelns;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;