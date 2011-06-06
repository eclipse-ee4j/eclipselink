/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - 2.3
 ******************************************************************************/
@XmlSchema(namespace="examplenamespace",
	xmlns = {@XmlNs(prefix="x", namespaceURI="examplenamespace")}
)
@XmlNameTransformer(MyUpperNameTransformer.class)
package org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.upper;
import javax.xml.bind.annotation.*;
import org.eclipse.persistence.oxm.annotations.XmlNameTransformer;
import org.eclipse.persistence.testing.jaxb.annotations.xmlnametransformer.upper.MyUpperNameTransformer;
