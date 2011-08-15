/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     15/08/2011-2.3.1 Guy Pelletier 
 *       - 298494: JPQL exists subquery generates unnecessary table join
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.additionalcriteria;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.AdditionalCriteria;

@Entity
@Table(name="JPA_AC_RABBIT")
@AdditionalCriteria(" (exists ( select rf from RabbitFoot rf where rf.rabbitId = this.id ))")
public class Rabbit  {
    @Id
    @GeneratedValue
    public int id;
    
    @Column(name="NAME")
    public String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
