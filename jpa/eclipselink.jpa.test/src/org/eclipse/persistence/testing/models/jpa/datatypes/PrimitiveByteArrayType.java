/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.datatypes;

import javax.persistence.*;

@Entity
@Table(name = "CMP3_PBYTEARRAY_TYPE")
public class PrimitiveByteArrayType implements java.io.Serializable {

    private int id;
    private byte[] primitiveByteArrayData;

    public PrimitiveByteArrayType()
    {
    }

    public PrimitiveByteArrayType(byte[] primitiveByteArrayData)
    {
        this.primitiveByteArrayData = primitiveByteArrayData;
    }

    @Id
    @Column(name="PBA_ID")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PBYTEARRAY_TABLE_GENERATOR")
    @TableGenerator(
        name="PBYTEARRAY_TABLE_GENERATOR",
        table="CMP3_PBYTEARRAY_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="PBYTEARRAY_SEQ"
    )
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id= id;
    }

    @Column(name = "PBYTEARRAY_DATA")
    @Lob
    public byte[] getPrimitiveByteArrayData()
    {
        return primitiveByteArrayData;
    }
    public void setPrimitiveByteArrayData(byte[] primitiveByteArrayData)
    {
        this.primitiveByteArrayData = primitiveByteArrayData;
    }

}
