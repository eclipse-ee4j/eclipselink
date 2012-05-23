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
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServer;
import org.eclipse.persistence.testing.tests.distributedservers.DistributedServersModel;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;


/**
 * A test that allows cache synchronization type to be set-up for multiple descriptors.
 * A hashtable of Class - Cache Sync type can be provided.
 * This test assumes it works on a DistributedServersModel and provides some convenience methods
 * for getting information from the cache of a distributed servers model.
 */
public class ConfigurableCacheSyncDistributedTest extends AutoVerifyTestCase {

    protected Hashtable cacheSyncConfigValues = null; // The cache synchronization type values to use for this test
    protected Hashtable oldCacheSyncConfigValues = null; // The old cache synchronization type values

    /**
     * Constructor.
     * @param cacheSyncConfigValues a hashtable of Class-Integer.  The class represents the class
     * to change the cache synchronization setting for.  The Integer represents the value to change it to.
     */
    public ConfigurableCacheSyncDistributedTest(Hashtable cacheSyncConfigValues) {
        super();
        this.cacheSyncConfigValues = cacheSyncConfigValues;
    }

    /**
     * Default constructor for subclasses.  Assumes the subclass will add the cacheSyncConfigValues
     */
    public ConfigurableCacheSyncDistributedTest() {
        super();
        cacheSyncConfigValues = new Hashtable();
    }

    /**
     * This method iterates over the distributed servers until one
     * returns an object.
     * @param query org.eclipse.persistence.queries.DatabaseQuery
     */
    public Object getObjectFromDistributedSession(DatabaseQuery query) {
        Enumeration servers = DistributedServersModel.getDistributedServers().elements();
        while (servers.hasMoreElements()) {
            try {
                Object result = ((DistributedServer)servers.nextElement()).getDistributedSession().executeQuery(query);
                if (result != null) {
                    return result;
                }
            } catch (Exception exception) {
            }
        }
        return null;
    }

    /**
     * Search through the Distributed servers and get an object with the same primary key
     * as the input argument from the cache.
     * @param object the object so search the cache for.
     */
    public Object getObjectFromDistributedCache(Object object) {
        Enumeration servers = DistributedServersModel.getDistributedServers().elements();
        while (servers.hasMoreElements()) {
            try {
                DistributedServer server = (DistributedServer)servers.nextElement();
                Object result = server.getDistributedSession().getIdentityMapAccessor().getFromIdentityMap(object);
                if (result != null) {
                    return result;
                }
            } catch (Exception exception) {
            }
        }
        return null;
    }

    /**
     * Search the distributed cache for the input argument.  When it is found return whether it
     * is valid on the distributed cache.
     */
    public boolean isObjectValidOnDistributedServer(Object object) {
        Enumeration servers = DistributedServersModel.getDistributedServers().elements();
        while (servers.hasMoreElements()) {
            try {
                DistributedServer server = (DistributedServer)servers.nextElement();
                Object result = server.getDistributedSession().getIdentityMapAccessor().getFromIdentityMap(object);
                if (result != null) {
                    return server.isObjectValid(object);
                }
            } catch (Exception exception) {
            }
        }
        return true;
    }

    /**
     * Setup the test by saving to old cache synchronization types and replacing them
     * with the new cache synchronization types.
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
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getAbstractSession().beginTransaction();
    }

    /**
     * Reset the test by returning the cache synchronization types to their original values.
     */
    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Enumeration enumtr = DistributedServersModel.getDistributedServers().elements();
        while (enumtr.hasMoreElements()) {
            (((DistributedServer)enumtr.nextElement()).getDistributedSession()).getIdentityMapAccessor().initializeAllIdentityMaps();
        }
        Enumeration keys = oldCacheSyncConfigValues.keys();
        while (keys.hasMoreElements()) {
            Class keyClass = (Class)keys.nextElement();
            ClassDescriptor descriptor = getSession().getDescriptor(keyClass);
            int newCacheSyncType = ((Integer)oldCacheSyncConfigValues.get(keyClass)).intValue();
            descriptor.setCacheSynchronizationType(newCacheSyncType);
        }
    }

}
