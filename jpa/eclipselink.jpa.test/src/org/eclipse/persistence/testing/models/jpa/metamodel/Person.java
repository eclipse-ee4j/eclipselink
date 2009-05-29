/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;

@MappedSuperclass
public abstract class Person {
    @Id
    @GeneratedValue(strategy=TABLE, generator="PERSON_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="PERSON_MM_TABLE_GENERATOR", 
        table="CMP3_MM_PERSON_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    
    // InstanceVariableAttributeAccessor testing
    @Column(name="PERSON_ID")    
    private Integer id;
    
    private String name;

    public String getName() {
        return name;
    }

    @Column(name="PERSON_NAME")
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getId() {    
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
