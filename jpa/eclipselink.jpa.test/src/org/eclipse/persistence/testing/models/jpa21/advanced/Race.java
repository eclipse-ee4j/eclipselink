/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/19/2012-2.5 Guy Pelletier 
 *       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.advanced;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="JPA21_RACE")
public class Race {
    @Id
    @GeneratedValue
    public Integer id;
    
    @Basic
    public String name;
    
    @ManyToMany(mappedBy="races")
    public List<Runner> runners;

    public Race() {}

    public String getName() {
        return name;
    }
    
    public List<Runner> getRunners() {
        return runners;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setRunners(List<Runner> runners) {
        this.runners = runners;
    }
}
