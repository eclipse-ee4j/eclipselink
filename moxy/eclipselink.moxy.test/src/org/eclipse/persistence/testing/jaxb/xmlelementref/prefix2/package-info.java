/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - Denise Smith February 12, 2013
@XmlSchema(namespace = "namespace2", xmlns={
            @XmlNs(namespaceURI = "namespace2", prefix = "childPrefix")}, elementFormDefault = XmlNsForm.QUALIFIED, attributeFormDefault = XmlNsForm.QUALIFIED)
            package org.eclipse.persistence.testing.jaxb.xmlelementref.prefix2;

import javax.xml.bind.annotation.*;
