/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.annotations.Multitenant;

public class Trowel {
    public int id;
    public String type;
    public Mason mason;

    public Trowel() {}

    public int getId() {
        return id;
    }

    public Mason getMason() {
        return mason;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMason(Mason mason) {
        this.mason = mason;
    }

    public void setType(String type) {
        this.type = type;
    }
}


