/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     13/01/2022-4.0.0 Tomas Kraus - 1391: JSON support in JPA
package org.eclipse.persistence.jpa.json.model;

import jakarta.json.JsonValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * JSON entity class.
 */
@Entity
public class JsonEntity {

    @Id
    private long id;

    private JsonValue value;

    public JsonEntity() {
        this.id = 0;
        this.value = null;
    }

    public JsonEntity(final long id, final JsonValue value) {
        this.id = id;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public JsonValue getValue() {
        return value;
    }

    public void setValue(final JsonValue value) {
        this.value = value;
    }

}
