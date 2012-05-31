/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 04 May 2012 - 2.4 - Initial implementation
 ******************************************************************************/
@XmlSchema(
   xmlns = {
      @XmlNs(prefix="atom", namespaceURI="http://www.w3.org/2005/Atom"),
      @XmlNs(prefix="atomic", namespaceURI="http://www.w3.org/2005/Atomic")
   }
)
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.adapter;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
