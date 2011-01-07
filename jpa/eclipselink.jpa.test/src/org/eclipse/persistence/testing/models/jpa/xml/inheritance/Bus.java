/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.xml.inheritance;

import java.util.ArrayList;
import javax.persistence.EntityListeners;

// These listeners are overridden in XML. BusListener2 callbacks should be
// called before BusListener callbacks. A BusListener3 has also been added
// and should be called after BusListener2 and before BusListener.
@EntityListeners({
    org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners.BusListener.class, 
    org.eclipse.persistence.testing.models.jpa.xml.inheritance.listeners.BusListener2.class
})
public class Bus extends FueledVehicle {
    public int pre_persist_count = 0;
    public int post_persist_count = 0;
    public int pre_remove_count = 0;
    public int post_remove_count = 0;
    public int pre_update_count = 0;
    public int post_update_count = 0;
    public int post_load_count = 0;
    
    private Person busDriver;
    public ArrayList prePersistCalledListeners = new ArrayList();
    public ArrayList postPersistCalledListeners = new ArrayList();
    
    public void addPostPersistCalledListener(Class listener) {
        postPersistCalledListeners.add(listener);
    }
    
    public void addPrePersistCalledListener(Class listener) {
        prePersistCalledListeners.add(listener);
    }
    
    public Person getBusDriver() {
        return busDriver;
    }

    public int postPersistCalledListenerCount() {
        return postPersistCalledListeners.size();
    }
    
    public int prePersistCalledListenerCount() {
        return prePersistCalledListeners.size();
    }
    
    public Class getPostPersistCalledListenerAt(int index) {
        return (Class) postPersistCalledListeners.get(index);
    }
    
    public Class getPrePersistCalledListenerAt(int index) {
        return (Class) prePersistCalledListeners.get(index);
    }
    
    public void setBusDriver(Person busDriver) {
        this.busDriver = busDriver;
    }

    // CALLBACK METHODS //
    public void prePersist() {
        pre_persist_count++;
    }
    
    protected void postPersist() {
        post_persist_count++;
    }
    
    private void preRemove() {
        pre_remove_count++;
    }
    
    void postRemove() {
        post_remove_count++;
    }
    
    public void preUpdate() {
        pre_update_count++;
    }
    
    public void postUpdate() {
        post_update_count++;
    }
    
    public void postLoad() {
        post_load_count++;
    }
}
