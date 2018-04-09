/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/14/2018-2.7 Will Dazey
 *       - 500753: Synchronize initialization of InsertQuery
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.concurrency.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="CONCURR_USER")
public class User {

    @Id private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_tags", joinColumns = @JoinColumn(name = "user_id"))
    private List<UserTag> tags = new ArrayList<UserTag>();

    public User() {}
    
    public User(int id) {
        this.setId(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<UserTag> getTags() {
        return tags;
    }

    public void setTags(List<UserTag> tags) {
        this.tags = tags;
    }
}
