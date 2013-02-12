/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - Denise Smith February 12, 2013
 ******************************************************************************/
@XmlSchema(namespace = "http://www.test.org", xmlns={
        @XmlNs(namespaceURI = "http://www.test.org", prefix = "test")}, elementFormDefault = XmlNsForm.QUALIFIED, attributeFormDefault = XmlNsForm.UNQUALIFIED)
package org.eclipse.persistence.testing.jaxb.xmlelementref.prefix;

import javax.xml.bind.annotation.*;
