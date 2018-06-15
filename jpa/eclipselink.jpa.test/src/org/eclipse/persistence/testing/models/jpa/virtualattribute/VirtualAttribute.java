/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.testing.models.jpa.virtualattribute;

import javax.persistence.*;
import static javax.persistence.GenerationType.*;

@Entity
@Table(name="CMP3_VIRTUAL")
public class VirtualAttribute  {

    private int id;
    private String description;

    @Id
    @GeneratedValue(strategy=TABLE, generator="VIRTUAL_ATTRIBUTE_TABLE_GENERATOR")
    @TableGenerator(
        name="VIRTUAL_ATTRIBUTE_TABLE_GENERATOR",
        table="CMP3_VIRTUAL_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="VIRTUAL_ATTRIBUTE_SEQ"
    )
    @Column(name="CMP3_VIRTUALID")
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
