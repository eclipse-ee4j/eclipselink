/*
 * Copyright (c) 2022 IBM and/or its affiliates. All rights reserved.
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
//     IBM - initial API and implementation

package org.eclipse.persistence.jpa.test.uuid.model;

import java.util.UUID;

public class XMLUUIDIdClassEntity {
    private UUID uuid_id;

    private long l_id;

    private String name;

    public XMLUUIDIdClassEntity() {

    }

    /**
     * @return the uuid_id
     */
    public UUID getUuid_id() {
        return uuid_id;
    }

    /**
     * @param uuid_id the uuid_id to set
     */
    public void setUuid_id(UUID uuid_id) {
        this.uuid_id = uuid_id;
    }

    /**
     * @return the l_id
     */
    public long getL_id() {
        return l_id;
    }

    /**
     * @param l_id the l_id to set
     */
    public void setL_id(long l_id) {
        this.l_id = l_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "XMLUUIDIdClassEntity [uuid_id=" + uuid_id + ", l_id=" + l_id + ", name=" + name + "]";
    }

}