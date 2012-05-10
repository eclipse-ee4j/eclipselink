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
@Table(name = "CMP3_CHARACTERARRAY_TYPE")
public class CharacterArrayType implements java.io.Serializable {

    private int id;
    private Character[] characterArrayData;

    public CharacterArrayType()
    {
    }

    public CharacterArrayType(Character[] characterArrayData)
    {
        this.characterArrayData = characterArrayData;
    }

    @Id
    @Column(name="CA_ID")
    @GeneratedValue(strategy=GenerationType.TABLE, generator="CHARACTERARRAY_TABLE_GENERATOR")
    @TableGenerator(
        name="CHARACTERARRAY_TABLE_GENERATOR",
        table="CMP3_CHARACTERARRAY_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CHARACTERARRAY_SEQ"
    )
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id= id;
    }

    @Column(name = "CHARACTERARRAY_DATA")
    public Character[] getCharacterArrayData()
    {
        return characterArrayData;
    }
    public void setCharacterArrayData(Character[] characterArrayData)
    {
        this.characterArrayData = characterArrayData;
    }

}
