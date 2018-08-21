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
import static javax.persistence.GenerationType.TABLE;

@Entity
@Table(name = "CMP3_PRIMITIVE_TYPES")
public class PrimitiveTypes implements java.io.Serializable {

    private int id;
    private boolean booleanData;
    private byte byteData;
    private char charData;
    private short shortData;
    private int intData;
    private long longData;
    private float floatData;
    private double doubleData;
    private String stringData;

    public PrimitiveTypes()
    {
    }

    public PrimitiveTypes(int id, boolean booleanData, byte byteData, char charData, short shortData, int intData, long longData, float floatData, double doubleData, String stringData)
    {
        this.id = id;
        this.booleanData = booleanData;
        this.byteData = byteData;
        this.charData = charData;
        this.shortData = shortData;
        this.intData = intData;
        this.longData = longData;
        this.floatData = floatData;
        this.doubleData = doubleData;
        this.stringData = stringData;
    }

    @Id
    @Column(name="PT_ID")
    @GeneratedValue(strategy=TABLE, generator="PT_TABLE_GENERATOR")
    @TableGenerator(
        name="PT_TABLE_GENERATOR",
        table="CMP3_PT_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="PT_SEQ"
    )
   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id= id;
   }

    @Column(name = "BOOLEAN_DATA")
    public boolean getBooleanData(){
        return booleanData;

    }
    public void setBooleanData(boolean booleanData)
    {
        this.booleanData = booleanData;
    }

    @Column(name = "BYTE_DATA")
    public byte getByteData()
    {
        return byteData;
    }
    public void setByteData(byte byteData)
    {
        this.byteData= byteData;
    }

    @Column(name = "CHAR_DATA")
    public char getCharData()
    {
        return charData;
    }
    public void setCharData(char charData)
    {
        this.charData = charData;
    }

    @Column(name = "SHORT_DATA")
    public short getShortData(){
        return shortData;
    }
    public  void setShortData(short shortData)
    {
        this.shortData = shortData;
    }

    @Column(name = "INT_DATA")
    public int getIntData(){
        return intData;
    }
    public void setIntData(int intData)
    {
        this.intData = intData;
    }

    @Column(name = "LONG_DATA")
    public long getLongData(){
        return longData;
    }
    public void setLongData(long longData)
    {
        this.longData = longData;
    }

    @Column(name = "FLOAT_DATA")
    public float getFloatData(){
        return floatData;
    }
    public void setFloatData(float floatData)
    {
        this.floatData = floatData;
    }

    @Column(name = "DOUBLE_DATA")
    public double getDoubleData(){
        return doubleData;
    }
    public void setDoubleData(double doubleData)
    {
        this.doubleData = doubleData;
    }

    @Column(name = "STRING_DATA")
    public String getStringData(){
        return stringData;
    }
    public void setStringData(String stringData)
    {
        this.stringData = stringData;
    }

}
