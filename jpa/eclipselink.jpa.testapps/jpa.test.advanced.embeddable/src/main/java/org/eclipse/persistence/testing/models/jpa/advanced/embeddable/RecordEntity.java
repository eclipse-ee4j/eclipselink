/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - Initial API and implementation.
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@NamedQueries({
        @NamedQuery(name = "findRecordEntityById",
                query = "SELECT OBJECT(o) FROM RecordEntity o WHERE o.id = :id"),
        @NamedQuery(name = "findRecordEntityByRecordAttribute",
                query = "SELECT OBJECT(o) FROM RecordEntity o WHERE o.recordAttribute = :recordAttribute")})
@Entity
@Table(name = "CMP3_EMBED_REC_VISITOR")
public class RecordEntity {

    @EmbeddedId
    private RecordPK id;

    @Embedded
    private RecordAttribute recordAttribute;

    private String name4;

    public RecordEntity() {
    }

    public RecordPK getId() {
        return id;
    }

    public void setId(RecordPK id) {
        this.id = id;
    }

    public RecordAttribute getRecordAttribute() {
        return recordAttribute;
    }

    public void setRecordAttribute(RecordAttribute recordAttribute) {
        this.recordAttribute = recordAttribute;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }
}
