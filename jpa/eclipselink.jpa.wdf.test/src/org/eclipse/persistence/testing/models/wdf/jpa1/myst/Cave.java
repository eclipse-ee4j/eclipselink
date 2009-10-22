/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.myst;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "TMP_CAVE")
public class Cave {

    private int id;
    private String name;
    private Set<Creature> creatures;

    public Cave() {
    }

    public Cave(int aId) {
        id = aId;
    }

    @Id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany
    @JoinTable(name = "TMP_CAVE_CREATURE", joinColumns = { @JoinColumn(name = "CAVE_ID") }, inverseJoinColumns = { @JoinColumn(name = "CREATURE_ID") })
    public Set<Creature> getCreatures() {
        return creatures;
    }

    public void setCreatures(Set<Creature> aCreatures) {
        creatures = aCreatures;
    }
}
