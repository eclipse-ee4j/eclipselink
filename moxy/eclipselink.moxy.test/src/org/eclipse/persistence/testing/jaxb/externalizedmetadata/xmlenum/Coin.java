/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - January 12/2010 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlenum;

@javax.xml.bind.annotation.XmlType(name="coin-enum")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum Coin {
    @javax.xml.bind.annotation.XmlEnumValue("1") PENNY,
    @javax.xml.bind.annotation.XmlEnumValue("5") NICKEL,
    @javax.xml.bind.annotation.XmlEnumValue("10") DIME,
    @javax.xml.bind.annotation.XmlEnumValue("24") QUARTER
}