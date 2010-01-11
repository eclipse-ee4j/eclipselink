/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
