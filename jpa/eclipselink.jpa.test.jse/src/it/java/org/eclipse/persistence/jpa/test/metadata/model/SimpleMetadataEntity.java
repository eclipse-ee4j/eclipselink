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
//     08/02/2022-4.0.0 Tomas Kraus - 1613: FileBasedProjectCache retrieveProject contains readObject with no data filtering
package org.eclipse.persistence.jpa.test.metadata.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SimpleMetadataEntity {

    // Only positive values are valid ID numbers
    @Id
    private Integer id;

    private String name;

    public SimpleMetadataEntity(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public SimpleMetadataEntity() {
        this(null, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
