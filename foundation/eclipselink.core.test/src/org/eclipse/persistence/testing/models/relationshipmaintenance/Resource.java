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
package org.eclipse.persistence.testing.models.relationshipmaintenance;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.persistence.indirection.*;

public class Resource {
    public int id;
    public String name;
    public ValueHolderInterface office;
    protected PropertyChangeListener topLinkListener;

    public Resource() {
        office = new ValueHolder();
    }

    public Resource(String name, FieldOffice office) {
        this();
        this.name = name;
        this.office = new ValueHolder(office);
    }

    /**
     * @return int
     */
    public int getId() {
        return id;
    }

    public FieldOffice getOffice() {
        return (FieldOffice)office.getValue();
    }

    public String getName() {
        return this.name;
    }

    /**
     * Insert the method's description here.
     * Creation date: (1/30/01 6:53:59 PM)
     * @param newId int
     */
    public void setId(int newId) {
        propertyChange("id", new Integer(this.id), new Integer(newId));
        id = newId;
    }

    public void setOffice(FieldOffice newOffice) {
        propertyChange("office", office.getValue(), newOffice);
        office.setValue(newOffice);
    }

    public void setName(String newName) {
        propertyChange("name", this.name, newName);
        this.name = newName;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(this) + "(" + id + ", " + System.identityHashCode(this) + ")";
    }

    /**
     * PUBLIC:
     * Return the PropertyChangeListener for the object.
     */
    public PropertyChangeListener getTrackedPropertyChangeListener() {
        return this.topLinkListener;
    }

    /**
     * PUBLIC:
     * Set the PropertyChangeListener for the object.
     */
    public void setTrackedPropertyChangeListener(PropertyChangeListener listener) {
        this.topLinkListener = listener;
    }

    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (topLinkListener != null) {
            if (oldValue != newValue) {
                topLinkListener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
            }
        }
    }
}
