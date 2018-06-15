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
// Denise Smith - 2.3
@XmlSchema(namespace="examplenamespace",
    xmlns = {@XmlNs(prefix="x", namespaceURI="examplenamespace")}
)
@XmlNameTransformer(MyLowerNameTransformer.class)
package org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.classlevel.upper;
import javax.xml.bind.annotation.*;
import org.eclipse.persistence.oxm.annotations.XmlNameTransformer;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.upper.MyUpperNameTransformer;
