/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
    public static int PRE_PERSIST_COUNT = 0;
    public static int POST_PERSIST_COUNT = 0;
    public static int PRE_REMOVE_COUNT = 0;
    public static int POST_REMOVE_COUNT = 0;
    public static int PRE_UPDATE_COUNT = 0;
    public static int POST_UPDATE_COUNT = 0;
    public static int POST_LOAD_COUNT = 0;
    
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
        PRE_PERSIST_COUNT++;
    }
    
    protected void postPersist() {
        POST_PERSIST_COUNT++;
    }
    
    private void preRemove() {
        PRE_REMOVE_COUNT++;
    }
    
    void postRemove() {
        POST_REMOVE_COUNT++;
    }
    
    public void preUpdate() {
        PRE_UPDATE_COUNT++;
    }
    
    public void postUpdate() {
        POST_UPDATE_COUNT++;
    }
    
    public void postLoad() {
        POST_LOAD_COUNT++;
    }
}
