/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
@XmlSchema(
		xmlns = {
		@javax.xml.bind.annotation.XmlNs(prefix = "po",namespaceURI="http://www.example.com/myPO1"),

        @javax.xml.bind.annotation.XmlNs(prefix = "xs",namespaceURI="http://www.w3.org/2001/XMLSchema")
})
package org.eclipse.persistence.testing.jaxb.javadoc.xmlns;

import javax.xml.bind.annotation.*;