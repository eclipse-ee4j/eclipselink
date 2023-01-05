/*
 * Copyright (c) 2022, 2023 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.eclipse.persistence.nosql.annotations.NoSql;

import java.sql.Timestamp;
import java.util.Arrays;

@Entity
@Table(name = "DATA_TYPES_TAB")
@NamedQueries({
        @NamedQuery(name = "DataTypesEntity.findAll", query = "SELECT t FROM DataTypesEntity t"),
        @NamedQuery(name = "DataTypesEntity.findById", query = "SELECT t FROM DataTypesEntity t WHERE t.id = :id"),
        @NamedQuery(name = "DataTypesEntity.findByIdAndName", query = "SELECT t FROM DataTypesEntity t WHERE t.id = :id AND t.fieldString = :name")
})
@NoSql
@Cacheable(false)
public class DataTypesEntity {
    @Id
    private long id;
    @Lob
    @Column(name = "col_binary", columnDefinition = "BINARY")
    private byte[] fieldBinary;
    @Column(name = "col_boolean")
    private boolean fieldBoolean;
    @Column(name = "col_json_string", columnDefinition = "JSON")
    private String fieldJsonString;
    @Column(name = "col_json_object", columnDefinition = "JSON")
    private String fieldJsonObject;
    @Column(name = "col_null")
    private String fieldNull;
    @Column(name = "col_string")
    private String fieldString;
    @Column(name = "col_timestamp")
    private Timestamp fieldTimestamp;

    public DataTypesEntity() {
    }

    public DataTypesEntity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getFieldBinary() {
        return fieldBinary;
    }

    public void setFieldBinary(byte[] fieldBinary) {
        this.fieldBinary = fieldBinary;
    }

    public boolean isFieldBoolean() {
        return fieldBoolean;
    }

    public void setFieldBoolean(boolean fieldBoolean) {
        this.fieldBoolean = fieldBoolean;
    }

    public String getFieldJsonString() {
        return fieldJsonString;
    }

    public void setFieldJsonString(String fieldJsonString) {
        this.fieldJsonString = fieldJsonString;
    }

    public String getFieldJsonObject() {
        return fieldJsonObject;
    }

    public void setFieldJsonObject(String fieldJsonObject) {
        this.fieldJsonObject = fieldJsonObject;
    }

    public String getFieldNull() {
        return fieldNull;
    }

    public void setFieldNull(String fieldNull) {
        this.fieldNull = fieldNull;
    }

    public String getFieldString() {
        return fieldString;
    }

    public void setFieldString(String name) {
        this.fieldString = name;
    }

    public Timestamp getFieldTimestamp() {
        return fieldTimestamp;
    }

    public void setFieldTimestamp(Timestamp fieldTimestamp) {
        this.fieldTimestamp = fieldTimestamp;
    }

    @Override
    public String toString() {
        return "DataTypesEntity{" +
                "id=" + id +
                ", fieldBinary=" + Arrays.toString(fieldBinary) +
                ", fieldBoolean=" + fieldBoolean +
                ", fieldJsonString='" + fieldJsonString + '\'' +
                ", fieldJsonObject='" + fieldJsonObject + '\'' +
                ", fieldNull='" + fieldNull + '\'' +
                ", fieldString='" + fieldString + '\'' +
                ", fieldTimestamp=" + fieldTimestamp +
                '}';
    }
}
