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
 * dmccann - December 10/2010 - 2.0 - Initial implementation
 ******************************************************************************/
@javax.xml.bind.annotation.XmlSchemaTypes({
    @javax.xml.bind.annotation.XmlSchemaType(name="year", type=java.util.GregorianCalendar.class),
    @javax.xml.bind.annotation.XmlSchemaType(name="double", type=java.math.BigDecimal.class)
})
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematypes;
