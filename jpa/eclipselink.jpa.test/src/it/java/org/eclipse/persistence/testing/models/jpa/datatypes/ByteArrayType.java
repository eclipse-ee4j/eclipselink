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
@Table(name = "CMP3_BYTEARRAY_TYPE")
public class ByteArrayType implements java.io.Serializable {

    private int id;
    private Byte[] byteArrayData;

    public ByteArrayType()
    {
    }

    public ByteArrayType(Byte[] byteArrayData)
    {
        this.byteArrayData = byteArrayData;
    }

    @Id
    @Column(name="BA_ID")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="BYTEARRAY_TABLE_GENERATOR")
    @TableGenerator(
        name="BYTEARRAY_TABLE_GENERATOR",
        table="CMP3_BYTEARRAY_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="BYTEARRAY_SEQ"
    )
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id= id;
    }

    @Column(name = "BYTEARRAY_DATA")
    public Byte[] getByteArrayData()
    {
        return byteArrayData;
    }
    public void setByteArrayData(Byte[] byteArrayData)
    {
        this.byteArrayData = byteArrayData;
    }

}
