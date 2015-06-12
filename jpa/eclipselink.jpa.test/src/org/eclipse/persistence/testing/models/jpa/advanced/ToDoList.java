/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="CMP3_TODOLIST")
public class ToDoList {
    
    @Id
    protected int id;
    protected String name;    
    @ElementCollection(fetch=FetchType.LAZY, targetClass=String.class)
    @CollectionTable(
            name = "CMP3_TODOLISTITEM", 
            joinColumns = { @JoinColumn(name = "TODOLIST_ID") })
    @Column(name = "ITEM_TEXT")
    protected Set<String> items;
    
    public ToDoList() {
        this(0, "");
    }
    
    public ToDoList(int id, String name) {
        super();
        setId(id);
        setName(name);
        setItems(new HashSet<String>());
    }
    
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
    
    public Set<String> getItems() {
        return items;
    }
    
    public void setItems(Set<String> items) {
        this.items = items;
    }
    
    public void addItem(String item) {
        if (!getItems().contains(item)) {
            getItems().add(item);
        }
    }
    
    public void removeItem(String item) {
        if (getItems().contains(item)) {
            getItems().remove(item);
        }
    }
    
}
