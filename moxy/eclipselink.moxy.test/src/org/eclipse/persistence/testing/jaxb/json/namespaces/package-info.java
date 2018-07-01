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
//     Praba Vijayaratnam - 2.3 - initial implementation
@XmlSchema(
        xmlns = {@XmlNs(prefix="ns3", namespaceURI="namespace3"),
                 @XmlNs(prefix="ns1", namespaceURI="namespace1"),
                 @XmlNs(prefix="ns0", namespaceURI="namespace0")
        }
)

package org.eclipse.persistence.testing.jaxb.json.namespaces;

import javax.xml.bind.annotation.*;
