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
// dmccann - 2.3 - Initial implementation
@XmlSchema(namespace="http://test.org",
    xmlns={
        @XmlNs(namespaceURI="http://test.org", prefix="t"),
        @XmlNs(namespaceURI="http://test2.org", prefix="t2")})
package org.eclipse.persistence.testing.jaxb.schemagen.imports.relativeschemalocation.test;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

