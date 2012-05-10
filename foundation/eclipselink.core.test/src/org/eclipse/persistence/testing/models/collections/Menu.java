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
package org.eclipse.persistence.testing.models.collections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.indirection.*;

public class Menu implements ChangeTracker{
    private String type;
    public ValueHolderInterface items = new ValueHolder(new Vector());
    private java.math.BigDecimal id;
    private ValueHolderInterface owner = new ValueHolder();
    public PropertyChangeListener listener;

    public Menu() {
        super();
    }

    public Menu(String menuType) {
        this();
        this.type = menuType;
    }

    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return listener;
    }

    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listener != null) {
            if (oldValue != newValue) {
                listener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
            }
        }
    }

    public void collectionChange(String propertyName, Collection changedCollection, Object newObject, int changeType, boolean isChangeApplied) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, changedCollection, newObject, changeType, isChangeApplied));
        }
    }

    protected static Menu example1(Restaurant aRestaurant) {
        Menu instance = new Menu();
        instance.setOwner(aRestaurant);
        instance.setType("dinner");

        LinkedList items = new LinkedList();
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example1(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example2(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example3(instance));
        instance.setItems(items);

        return instance;
    }

    protected static Menu example2(Restaurant aRestaurant) {
        Menu instance = new Menu();
        instance.setOwner(aRestaurant);
        instance.setType("lunch");

        LinkedList items = new LinkedList();
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example4(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example5(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example6(instance));
        instance.setItems(items);

        return instance;
    }

    protected static Menu example3(Restaurant aRestaurant) {
        Menu instance = new Menu();
        instance.setOwner(aRestaurant);
        instance.setType("dessert");

        LinkedList items = new LinkedList();
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example7(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example8(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example9(instance));
        instance.setItems(items);

        return instance;
    }

    protected static Menu example4(Restaurant aRestaurant) {
        Menu instance = new Menu();
        instance.setOwner(aRestaurant);
        instance.setType("dinner");

        LinkedList items = new LinkedList();
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example10(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example11(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example12(instance));
        instance.setItems(items);

        return instance;
    }

    protected static Menu example5(Restaurant aRestaurant) {
        Menu instance = new Menu();
        instance.setOwner(aRestaurant);
        instance.setType("lunch");

        LinkedList items = new LinkedList();
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example13(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example14(instance));
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example15(instance));
        instance.setItems(items);

        return instance;
    }

    protected static Menu example6(Restaurant aRestaurant) {
        Menu instance = new Menu();
        instance.setOwner(aRestaurant);
        instance.setType("brkf/lnch/dinner");

        LinkedList items = new LinkedList();
        items.add(org.eclipse.persistence.testing.models.collections.MenuItem.example16(instance));
        instance.setItems(items);

        return instance;
    }

    public java.math.BigDecimal getId() {
        return id;
    }

    public Collection getItems() {
        return (Collection)getItemsHolder().getValue();
    }

    public ValueHolderInterface getItemsHolder() {
        return items;
    }

    public String getKey() {
        return new String(this.getType());
    }

    public Restaurant getOwner() {
        return (Restaurant)getOwnerHolder().getValue();
    }

    public ValueHolderInterface getOwnerHolder() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public void setId(java.math.BigDecimal newValue) {
        this.id = newValue;
    }

    public void setItems(Collection value) {
        propertyChange("items", this.getItemsHolder().getValue(), value);
        getItemsHolder().setValue(value);
    }

    public void setItemsHolder(ValueHolderInterface newValue) {
        this.items = newValue;
    }

    public void setOwner(ValueHolderInterface newValue) {
        this.owner = newValue;
    }

    public void setOwner(Restaurant newValue) {
        propertyChange("owner", this.getOwnerHolder().getValue(), newValue);
        getOwnerHolder().setValue(newValue);
    }

    public void setOwnerHolder(ValueHolderInterface newValue) {
        this.owner = newValue;
    }

    public void setType(String newValue) {
        propertyChange("type", this.type, newValue);
        this.type = newValue;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_MENU");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("TYPE", String.class, 20);
        definition.addField("REST_ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    /**
     * Returns a String that represents the value of this object.
     * @return a string representation of the receiver
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(org.eclipse.persistence.internal.helper.Helper.getShortClassName(this.getClass()) + ": " + this.getType());
        buf.append(org.eclipse.persistence.internal.helper.Helper.cr());
        if (getItems() != null) {
            buf.append("MenuItems: " + org.eclipse.persistence.internal.helper.Helper.getShortClassName(getItems().getClass()));
            buf.append(getItems().toString());
            buf.append(org.eclipse.persistence.internal.helper.Helper.cr());
        }
        return buf.toString();
    }
}
