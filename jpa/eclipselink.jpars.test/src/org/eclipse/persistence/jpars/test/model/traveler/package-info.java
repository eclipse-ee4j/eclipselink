/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

@XmlSchema(
        namespace = "http://www.example.org/traveler", elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = { @XmlNs(prefix = "ns1", namespaceURI = "http://www.example.org/traveler") })
package org.eclipse.persistence.jpars.test.model.traveler;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

