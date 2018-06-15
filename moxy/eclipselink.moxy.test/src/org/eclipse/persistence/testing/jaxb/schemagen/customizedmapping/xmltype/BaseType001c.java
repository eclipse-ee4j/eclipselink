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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmltype;

import javax.xml.bind.annotation.*;

/**
 * Mapping: Class to Complex Type Definition
 * {base type definition} Component
 * if the class contains a mapped property or field
 * annotated with @XmlValue, then the schema
 * type to which mapped property or field's type is mapped.
 */
@XmlRootElement
public class BaseType001c {
    @XmlValue
    public boolean b01;

    public BaseType001c() {}
}
