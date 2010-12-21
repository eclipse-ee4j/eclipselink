/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.descriptors.changetracking.*;

public abstract class Person implements Comparable, ChangeTracker {
    private String firstName;
    private String lastName;
    private java.math.BigDecimal id;
    public PropertyChangeListener listener;

    public Person() {
        super();
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

    public void collectionChange(String propertyName, Collection changedCollection, Object newObject, int changeType) {
        if (listener != null) {
            listener.propertyChange(new CollectionChangeEvent(this, propertyName, changedCollection, newObject, changeType));
        }
    }

    public void mapChange(String propertyName, Map changedCollection, Object key, Object newObject, int changeType) {
        if (listener != null) {
            listener.propertyChange(new MapChangeEvent(this, propertyName, changedCollection, key, newObject, changeType));
        }
    }

    public int compareTo(Object o) {
        return this.lastName.compareTo(((Person)o).getLastName());
    }

    public String getFirstName() {
        return firstName;
    }

    public java.math.BigDecimal getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String newValue) {
        propertyChange("firstName", this.firstName, newValue);
        this.firstName = newValue;
    }

    public void setId(java.math.BigDecimal newValue) {
        propertyChange("id", this.id, newValue);
        this.id = newValue;
    }

    public void setLastName(String newValue) {
        propertyChange("lastName", this.lastName, newValue);
        this.lastName = newValue;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static org.eclipse.persistence.tools.schemaframework.TableDefinition tableDefinition() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition definition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        definition.setName("COL_PERS");

        definition.addIdentityField("ID", java.math.BigDecimal.class);
        definition.addField("CLASS", String.class, 1);
        definition.addField("F_NAME", String.class, 40);
        definition.addField("L_NAME", String.class, 40);
        definition.addField("SPECIALT", String.class, 100);
        definition.addField("W_RST_ID", java.math.BigDecimal.class, 15);

        return definition;
    }

    /**
     * Returns a String that represents the value of this object.
     * @return a string representation of the receiver
     */
    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(this.getClass()) + ": " + this.getFirstName() + " " + this.getLastName();
    }
}
