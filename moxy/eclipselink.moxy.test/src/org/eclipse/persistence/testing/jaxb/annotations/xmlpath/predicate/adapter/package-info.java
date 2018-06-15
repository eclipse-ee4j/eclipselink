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
//  - rbarkhouse - 04 May 2012 - 2.4 - Initial implementation
@XmlSchema(
   xmlns = {
      @XmlNs(prefix="atom", namespaceURI="http://www.w3.org/2005/Atom"),
      @XmlNs(prefix="atomic", namespaceURI="http://www.w3.org/2005/Atomic")
   }
)
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.adapter;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
