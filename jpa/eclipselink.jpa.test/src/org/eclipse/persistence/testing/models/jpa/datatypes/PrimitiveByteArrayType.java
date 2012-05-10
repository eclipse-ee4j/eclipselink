/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
