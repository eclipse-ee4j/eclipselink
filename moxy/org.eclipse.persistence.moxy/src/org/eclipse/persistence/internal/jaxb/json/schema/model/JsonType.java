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
//     Matt MacIvor - 2.5.1 - Initial Implementation
package org.eclipse.persistence.internal.jaxb.json.schema.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
/**
 * INTERNAL:
 * <p><b>Purpose:</b>
 * Provides an enumeration of the json schema types for use by the JsonSchemaGenerator.
 *
 * @see JsonSchema
 * @author mmacivor
 *
 */
@XmlEnum
public enum JsonType {
    @XmlEnumValue("object")
    OBJECT,

    @XmlEnumValue("array")
    ARRAY,

    @XmlEnumValue("string")
    STRING,

    @XmlEnumValue("number")
    NUMBER,

    @XmlEnumValue("integer")
    INTEGER,

    @XmlEnumValue("boolean")
    BOOLEAN,

    //A marker type, used by schema generator to indicate a type that could be an
    //attachment
    @XmlEnumValue("binary")
    BINARYTYPE,

    //A marker type, used by schema generator to indicate an enumeration
    @XmlEnumValue("enum")
    ENUMTYPE
}
