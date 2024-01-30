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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.weaving;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import org.eclipse.persistence.annotations.FetchAttribute;
import org.eclipse.persistence.annotations.FetchGroup;

@Entity
@FetchGroup(name="FloatingPointFieldsOnly",
        attributes={
                @FetchAttribute(name="floatField"),
                @FetchAttribute(name="doubleField")})
@Cacheable(false)
public class WeavingEntityInDir {
    private int id;
    private boolean booleanField;
    private byte byteField;
    private short shortField;
    private int intField;
    private long longField;
    private float floatField;
    private double doubleField;
    private String stringField;
    private byte[] blobField;

    public WeavingEntityInDir() {
    }

    public WeavingEntityInDir(int id) {
        this.id = id;
    }

    @Id
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBooleanField() {
        return booleanField;
    }

    public void setBooleanField(boolean booleanField) {
        this.booleanField = booleanField;
    }

    public byte getByteField() {
        return byteField;
    }

    public void setByteField(byte byteField) {
        this.byteField = byteField;
    }

    public short getShortField() {
        return shortField;
    }

    public void setShortField(short shortFiled) {
        this.shortField = shortFiled;
    }

    public int getIntField() {
        return intField;
    }

    public void setIntField(int intField) {
        this.intField = intField;
    }

    public long getLongField() {
        return longField;
    }

    public void setLongField(long longField) {
        this.longField = longField;
    }

    public float getFloatField() {
        return floatField;
    }

    public void setFloatField(float floatField) {
        this.floatField = floatField;
    }

    public double getDoubleField() {
        return doubleField;
    }

    public void setDoubleField(double doubleField) {
        this.doubleField = doubleField;
    }

    public String getStringField() {
        return stringField;
    }

    public void setStringField(String stringField) {
        this.stringField = stringField;
    }

    @Lob
    @Column(length=4800)
    @Basic(fetch= FetchType.LAZY)
    public byte[] getBlobField() {
        return blobField;
    }

    public void setBlobField(byte[] blobField) {
        this.blobField = blobField;
    }
}
