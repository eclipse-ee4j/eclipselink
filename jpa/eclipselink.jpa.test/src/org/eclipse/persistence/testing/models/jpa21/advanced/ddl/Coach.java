/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/12/2013-2.5 Guy Pelletier 
 *       - 405640: JPA 2.1 schema generation drop operation fails to include dropping defaulted fk constraints.
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa21.advanced.ddl;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="JPA21_DDL_COACH")
public class Coach {
    @Id
    @GeneratedValue
    protected Integer id;
    
    @ManyToMany(mappedBy="coaches")
    protected List<Runner> runners;

    public Coach() {}
    
    
    public Integer getId() {
        return id;
    }

    public List<Runner> getRunners() {
        return runners;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public void setRunners(List<Runner> runners) {
        this.runners = runners;
    }
    
}
