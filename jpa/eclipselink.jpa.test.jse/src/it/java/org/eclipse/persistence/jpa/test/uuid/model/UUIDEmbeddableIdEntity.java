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

import jakarta.persistence.Basic;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class UUIDEmbeddableIdEntity {
	@EmbeddedId
    private EmbeddableUUID_ID id;

    private String name;

    public UUIDEmbeddableIdEntity() {
    }

    public UUIDEmbeddableIdEntity(EmbeddableUUID_ID id) {
        this.id = id;
    }

    public UUIDEmbeddableIdEntity(EmbeddableUUID_ID id, String name) {
        this.id = id;
        this.name = name;
    }

    public EmbeddableUUID_ID getId() {
        return id;
    }

    public void setId(EmbeddableUUID_ID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UUIDEmbeddableIdEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
