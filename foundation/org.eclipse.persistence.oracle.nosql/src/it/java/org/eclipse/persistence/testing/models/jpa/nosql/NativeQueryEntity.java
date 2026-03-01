/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.nosql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;

import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;

@Entity
@Table(name = "TEST_TAB")
@NamedNativeQuery(
        name="NativeQueryEntity.findByIdSQLNativeQueryEntity",
        query="DECLARE $id INTEGER; SELECT $tab.id, $tab.col_json_object.map[$element.m1 = 5] as component FROM TEST_TAB $tab WHERE id = $id",
        resultClass= org.eclipse.persistence.testing.models.jpa.nosql.NativeQueryEntity.class
)
//@NoSql -> it's not possible to use default (dataFormat= DataFormatType.XML) as QueryStringInteraction which is behind @NamedNativeQuery extends MappedInteraction not XMLInteraction
@NoSql(dataFormat= DataFormatType.MAPPED)
public class NativeQueryEntity {
    @Id
    private long id;
    @Column(name = "component", columnDefinition = "JSON")
    private String component;

    public NativeQueryEntity() {
    }

    public NativeQueryEntity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    @Override
    public String toString() {
        return "NativeQueryEntity{" +
                "id=" + id +
                ", component='" + component + '\'' +
                '}';
    }
}
