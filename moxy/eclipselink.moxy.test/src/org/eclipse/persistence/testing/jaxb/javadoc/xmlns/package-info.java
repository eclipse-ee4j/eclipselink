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
//     Praba Vijayaratnam - 2.3 - initial implementation
@XmlSchema(
        xmlns = {
        @javax.xml.bind.annotation.XmlNs(prefix = "po",namespaceURI="http://www.example.com/myPO1"),

        @javax.xml.bind.annotation.XmlNs(prefix = "xs",namespaceURI="http://www.w3.org/2001/XMLSchema")
})
package org.eclipse.persistence.testing.jaxb.javadoc.xmlns;

import javax.xml.bind.annotation.*;
