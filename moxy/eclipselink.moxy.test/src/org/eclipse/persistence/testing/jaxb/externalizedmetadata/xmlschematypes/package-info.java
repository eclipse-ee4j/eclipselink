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
// dmccann - December 10/2010 - 2.0 - Initial implementation
@javax.xml.bind.annotation.XmlSchemaTypes({
    @javax.xml.bind.annotation.XmlSchemaType(name="year", type=java.util.GregorianCalendar.class),
    @javax.xml.bind.annotation.XmlSchemaType(name="double", type=java.math.BigDecimal.class)
})
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes;
