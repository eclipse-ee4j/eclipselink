/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4.2 - initial implementation
@XmlSchema(
    elementFormDefault=XmlNsForm.QUALIFIED,
    namespace="urn:foo",
    xmlns={
        @XmlNs(namespaceURI = "urn:foo", prefix = "foo")
    }
)
package org.eclipse.persistence.testing.jaxb.unmarshaller.validation;

import javax.xml.bind.annotation.*;
