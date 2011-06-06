/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
@Table(name = "CMP3_PCHARARRAY_TYPE")
public class CharArrayType implements java.io.Serializable {

    private int id;
    private char[] primitiveCharArrayData;

    public CharArrayType()
    {
    }

    public CharArrayType(char[] primitiveCharArrayData)
    {
        this.primitiveCharArrayData = primitiveCharArrayData;
    }

    @Id
    @Column(name="PCA_ID")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="PCHARARRAY_TABLE_GENERATOR")
    @TableGenerator(
        name="PCHARARRAY_TABLE_GENERATOR",
        table="CMP3_PCHARARRAY_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="PCHARARRAY_SEQ"
    )
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id= id;
    }

    @Column(name = "PCHARARRAY_DATA")
    public char[] getPrimitiveCharArrayData()
    {
        return primitiveCharArrayData;
    }
    public void setPrimitiveCharArrayData(char[] primitiveCharArrayData)
    {
        this.primitiveCharArrayData = primitiveCharArrayData;
    }

}
