/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     11/22/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (index metadata support)
package org.eclipse.persistence.testing.models.jpa21.advanced.xml.ddl;

public class Utensil {
    public Integer id;
    public String serialTag;

    public Utensil() {}

    public Integer getId() {
        return id;
    }

    public String getSerialTag() {
        return serialTag;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSerialTag(String serialTag) {
        this.serialTag = serialTag;
    }
}
