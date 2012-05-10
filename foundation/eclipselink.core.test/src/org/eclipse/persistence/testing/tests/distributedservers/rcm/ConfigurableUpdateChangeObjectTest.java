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
package org.eclipse.persistence.testing.tests.distributedservers.rcm;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.tests.distributedservers.UpdateChangeObjectTest;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


/**
 * Test that allows configuration of the CacheSynchronization type for descriptors.
 * You can provide a hashtable containing classes for descriptors and their CacheSynchronizationType
 * value.  This test will execute an UpdateObjectTest that assumes that after cache synchronization
 * the objects will be valid in the distributed cache.
 * 
 * @see UpdateChangeObjectTest to see what changes about the object so you can ensure the value will still
 * be valid
 */
public class ConfigurableUpdateChangeObjectTest extends UpdateChangeObjectTest {

    protected Hashtable cacheSyncConfigValues = null;
    protected Hashtable oldCacheSyncConfigValues = null;

    /**
     * Construtor
     * @param employee the employee to use for this update
     * @param cacheSyncConfigValues a Class-Integer hashtable where the Class represents the 
     * class of the object to change the cache synchronization type for and the Integer represents
     * the new cache synchronization type.
     */
    public ConfigurableUpdateChangeObjectTest(Employee employee, Hashtable cacheSyncConfigValues) {
        super(employee);
        this.cacheSyncConfigValues = cacheSyncConfigValues;
    }

    /**
     * Setup by setting the new cache synchronization type values on the appropriate descriptors.
     */
    public void setup() {
        oldCacheSyncConfigValues = new Hashtable();
        Enumeration keys = cacheSyncConfigValues.keys();
        while (keys.hasMoreElements()) {
            Class keyClass = (Class)keys.nextElement();
            ClassDescriptor descriptor = getSession().getDescriptor(keyClass);
            if (descriptor != null) {
                int cacheSyncType = descriptor.getCacheSynchronizationType();
                Object newCacheSyncType = cacheSyncConfigValues.get(keyClass);
                if (newCacheSyncType != null) {
                    oldCacheSyncConfigValues.put(keyClass, new Integer(cacheSyncType));
                    descriptor.setCacheSynchronizationType(((Integer)newCacheSyncType).intValue());
                }
            }
        }
        super.setup();
    }

    /**
     * This test assumes the object will be valid on the distributed server and
     * that the cache synchronization setting will allow the compareObjects call made
     * in the superclass work return true.
     */
    public void verify() {
        this.objectFromDatabase = getSession().executeQuery(this.query);
        if (isObjectValidOnDistributedServer(objectFromDatabase)) {
            throw new TestErrorException("Object was not invalidated in remote cache.");
        }
        super.verify();
    }

    /**
     * Reset the cache synchronization types.
     */
    public void reset() {
        super.reset();
        Enumeration keys = oldCacheSyncConfigValues.keys();
        while (keys.hasMoreElements()) {
            Class keyClass = (Class)keys.nextElement();
            ClassDescriptor descriptor = getSession().getDescriptor(keyClass);
            int newCacheSyncType = ((Integer)oldCacheSyncConfigValues.get(keyClass)).intValue();
            descriptor.setCacheSynchronizationType(newCacheSyncType);
        }
    }

}
