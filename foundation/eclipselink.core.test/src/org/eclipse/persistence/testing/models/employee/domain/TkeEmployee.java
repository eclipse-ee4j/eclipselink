/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 */
// Contributors:Suresh Balakrishnan 

package org.eclipse.persistence.testing.models.employee.domain;

import java.beans.PropertyChangeListener;
import java.util.Vector;

import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

public class TkeEmployee implements org.eclipse.persistence.testing.models.employee.interfaces.TkeEmployee, ChangeTracker {
    public PropertyChangeListener listener;

    public TkeEmployee() {
        this.contacts = new ValueHolder(new Vector());
    }

    public TkeEmployee(String name) {
        super();
        this.name = name;
        this.contacts = new ValueHolder(new Vector());
    }

    public long id;

    // private List<Contact> contacts = new ArrayList<>();

    public ValueHolderInterface contacts;

    public void setId(long id) {
        this.id = id;
    }

    public String name;

    public void addContact(Contact c) {
        if (c != null) {
            if (getContact().add(c)) {
                collectionChange("contacts", contacts, c, 0, true);
                c.employee = this;
            }

        }
    }

    @Override
    public String toString() {
        return "[" + name + "]";
    }

    public void collectionChange(String propertyName, Object oldValue, Object newValue, int changeType, boolean isChangeApplied) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, oldValue, newValue, changeType, isChangeApplied));
        }
    }

    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return listener;
    }

    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Notice that the usage of value holders does not effect the public
     * interface or usage of the class. The get/set methods must however be
     * changed to wrap/unwrap the value holder.
     */
    public Vector getContact() {
        return (Vector) contacts.getValue();
    }

}
