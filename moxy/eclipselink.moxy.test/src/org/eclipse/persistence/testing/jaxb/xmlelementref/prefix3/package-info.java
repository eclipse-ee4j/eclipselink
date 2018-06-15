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
@XmlSchema(namespace = "namespace3", xmlns={
            @XmlNs(namespaceURI = "namespace3", prefix = "otherPrefix")}, elementFormDefault = XmlNsForm.QUALIFIED, attributeFormDefault = XmlNsForm.QUALIFIED)
            package org.eclipse.persistence.testing.jaxb.xmlelementref.prefix3;

import javax.xml.bind.annotation.*;
