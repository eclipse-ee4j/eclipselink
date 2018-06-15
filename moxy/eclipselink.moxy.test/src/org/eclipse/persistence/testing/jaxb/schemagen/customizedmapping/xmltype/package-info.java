/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

@XmlSchema(
     namespace = "http://www.example.com/BaseType",
     xmlns = { @XmlNs( prefix = "bt",
                       namespaceURI="http://www.example.com/BaseType") }
          )
@XmlAccessorType(PUBLIC_MEMBER)
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmltype;

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlAccessorType;
import static javax.xml.bind.annotation.XmlAccessType.PUBLIC_MEMBER;
