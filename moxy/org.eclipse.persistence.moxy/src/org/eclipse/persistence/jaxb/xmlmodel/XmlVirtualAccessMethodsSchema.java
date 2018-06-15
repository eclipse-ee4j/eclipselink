/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// rbarkhouse - 2011 May 09 - 2.3 - Initial implementation
package org.eclipse.persistence.jaxb.xmlmodel;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p><b>Purpose: </b> XmlVirtualAccessMethodsSchema enumeration is used in conjunction with XmlVirtualAcessMethods
 * to configure how virtual properties are generated into the schema. A value of NODES indicates that
 * each virtual property should have an individual node generated for it into the schema, whereas
 * a value of ANY indicates that a single {@literal <xs:any>} value should be generated to encapsulate all of
 * the virtual properties on this class.
 *
 * @see XmlVirtualAccessMethods
 */
@XmlType(name = "xml-virtual-access-methods-schema")
@XmlEnum
public enum XmlVirtualAccessMethodsSchema {

    NODES,
    ANY;

    public String value() {
        return name();
    }

    public static XmlVirtualAccessMethodsSchema fromValue(String v) {
        return valueOf(v);
    }

}
