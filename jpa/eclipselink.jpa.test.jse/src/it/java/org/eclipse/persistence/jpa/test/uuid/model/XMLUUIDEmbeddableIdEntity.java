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

public class XMLUUIDEmbeddableIdEntity {
    private XMLEmbeddableUUID_ID id;

    private String name;

    public XMLUUIDEmbeddableIdEntity() {

    }

    /**
     * @return the id
     */
    public XMLEmbeddableUUID_ID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(XMLEmbeddableUUID_ID id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "XMLUUIDEmbeddableIdEntity [id=" + id + ", name=" + name + "]";
    }

}
