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
import java.util.*;
import org.eclipse.persistence.descriptors.changetracking.*;
import org.eclipse.persistence.indirection.*;

public class FieldOffice implements ChangeTracker {
    public int id;
    public ValueHolderInterface manager;
    public ValueHolderInterface location;
    public java.util.Collection salespeople;
    public Collection resources;
    protected PropertyChangeListener topLinkListener;

    public FieldOffice() {
        manager = new ValueHolder();
        location = new ValueHolder();
        salespeople = new HashSet();
        resources = new ArrayList();
    }

    public void addSalesPerson(SalesPerson aSalesPerson) {
        getSalespeople().add(aSalesPerson);
    }

    public int getId() {
        return id;
    }

    public FieldLocation getLocation() {
        return (FieldLocation)location.getValue();
    }

    public FieldManager getManager() {
        return (FieldManager)manager.getValue();
    }

    public Collection getSalespeople() {
        return this.salespeople;
    }

    public void setId(int newId) {
        propertyChange("id", new Integer(this.id), new Integer(newId));
        id = newId;
    }

    public void setLocation(FieldLocation newLocation) {
        propertyChange("location", location.getValue(), newLocation);
        location.setValue(newLocation);
    }

    public void setManager(FieldManager newManager) {
        propertyChange("manager", manager.getValue(), newManager);
        manager.setValue(newManager);
    }

    public void setSalespeople(Collection newSalespeople) {
        propertyChange("salespeople", this.salespeople, newSalespeople);
        this.salespeople = newSalespeople;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(this) + "(" + id + ", " + System.identityHashCode(this) + ")";
    }

    /**
     * PUBLIC:
     * Return the PropertyChangeListener for the object.
     */
    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return this.topLinkListener;
    }

    /**
     * PUBLIC:
     * Set the PropertyChangeListener for the object.
     */
    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.topLinkListener = listener;
    }

    public void propertyChange(String propertyName, Object oldValue, Object newValue) {
        if (topLinkListener != null) {
            if (oldValue != newValue) {
                topLinkListener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
            }
        }
    }

    public Collection getResources() {
        return resources;
    }

    public void setResources(Collection resources) {
        propertyChange("resources", this.resources, resources);
        this.resources = resources;
    }
}
