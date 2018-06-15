/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

@XmlSchema(namespace="employeeNamespace",
    xmlns = {@XmlNs(prefix="x", namespaceURI="employeeNamespace"),@XmlNs(prefix="y", namespaceURI="addressNamespace")}
)
@XmlSchemaTypes({@XmlSchemaType(name="time", type=Calendar.class)})
package org.eclipse.persistence.testing.jaxb.schemagen.imports;

import java.util.Calendar;

import javax.xml.bind.annotation.*;
