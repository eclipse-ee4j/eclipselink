/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.util.UUID;

public class WithoutXmlRootElementRoot {

    private String name;
    private UUID uuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if(null == o || o.getClass() != this.getClass()) {
            return false;
        }
        WithoutXmlRootElementRoot test = (WithoutXmlRootElementRoot) o;
        if(null == name) {
            return null == test.getName();
        } else if (!name.equals(test.getName())){
            return false;
        }
        if(null == uuid) {
            return null == test.getUuid();
        } else {
            return uuid.equals(test.getUuid());
        }
    }

}
