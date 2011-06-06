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
 * dmccann - 2.3 - Initial implementation
 ******************************************************************************/
@XmlSchema(namespace="http://test.org",
	xmlns={
		@XmlNs(namespaceURI="http://test.org", prefix="t"), 
		@XmlNs(namespaceURI="http://test2.org", prefix="t2")})
package org.eclipse.persistence.testing.jaxb.schemagen.imports.relativeschemalocation.test;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

