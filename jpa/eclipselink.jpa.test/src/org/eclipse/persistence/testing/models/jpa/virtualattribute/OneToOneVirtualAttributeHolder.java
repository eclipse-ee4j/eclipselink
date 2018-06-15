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

import java.util.HashMap;

import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@Table(name="O_O_VIRTUAL")
public class OneToOneVirtualAttributeHolder  {

    private HashMap attributeMap;

    public static final String ID_KEY = "id";
    public static final String ATTRIBUTE_KEY = "attribute";

    public OneToOneVirtualAttributeHolder() {
        attributeMap = new HashMap();
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="O_O_VIRTUAL_ATTRIBUTE_TABLE_GENERATOR")
    @TableGenerator(
        name="O_O_VIRTUAL_ATTRIBUTE_TABLE_GENERATOR",
        table="CMP3_VIRTUAL_SEQ",
        pkColumnName="SEQ_NAME",
        valueColumnName="SEQ_COUNT",
        pkColumnValue="ONE_TO_ONE_VIRTUAL_ATTRIBUTE_SEQ"
    )
    @Column(name="O_O_VIRTUALID")
    public Integer getId(){
        return (Integer)attributeMap.get(ID_KEY);
    }

    public void setId(Integer id){
        attributeMap.remove(ID_KEY);
        attributeMap.put(ID_KEY, id);
    }

    @OneToOne(cascade=PERSIST, fetch=LAZY)
    @JoinColumn(name="VIRTUAL_ID")
    public VirtualAttribute getVirtualAttribute(){
        return (VirtualAttribute)attributeMap.get(ATTRIBUTE_KEY);
    }

    public void setVirtualAttribute(VirtualAttribute virtualAttribute){
        attributeMap.remove(ATTRIBUTE_KEY);
        attributeMap.put(ATTRIBUTE_KEY, virtualAttribute);
    }
}
