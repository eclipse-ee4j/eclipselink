/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Matt MacIvor -  August 2011
package org.eclipse.persistence.testing.jaxb.typemappinginfo.xsitype.self;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="employee", namespace="someuri")
public class Employee {

    public String name;

    public String id;

    public boolean equals(Object obj) {
        return name.equals(((Employee)obj).name) && id.equals(((Employee)obj).id);
    }
}
