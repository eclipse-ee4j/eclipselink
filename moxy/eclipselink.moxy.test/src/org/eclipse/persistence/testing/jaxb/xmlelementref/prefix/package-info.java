/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - Denise Smith February 12, 2013
@XmlSchema(namespace = "http://www.test.org", xmlns={
        @XmlNs(namespaceURI = "http://www.test.org", prefix = "test")}, elementFormDefault = XmlNsForm.QUALIFIED, attributeFormDefault = XmlNsForm.UNQUALIFIED)
package org.eclipse.persistence.testing.jaxb.xmlelementref.prefix;

import javax.xml.bind.annotation.*;
