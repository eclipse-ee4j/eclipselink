/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.remote;

import java.util.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.framework.*;

public class RefreshMaintainIdentityTest extends TestCase {

    private Vector masters = null;
    private Vector slaves = null;
    private boolean mastersShouldRefresh = false;
    private boolean slavesShouldRefresh = false;

    public RefreshMaintainIdentityTest() {
        setDescription("Test to ensure identity is maintained across valueholders in remote sessions.");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getSession().getDescriptor(Master.class).setShouldAlwaysRefreshCacheOnRemote(mastersShouldRefresh);
        getSession().getDescriptor(Slave.class).setShouldAlwaysRefreshCacheOnRemote(slavesShouldRefresh);

    }

    public void setup() {
        mastersShouldRefresh = getSession().getDescriptor(Master.class).shouldAlwaysRefreshCacheOnRemote();
        slavesShouldRefresh = getSession().getDescriptor(Slave.class).shouldAlwaysRefreshCacheOnRemote();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        getAbstractSession().beginTransaction();


    }

    public void test() {
        getSession().getDescriptor(Master.class).setShouldAlwaysRefreshCacheOnRemote(true);
        getSession().getDescriptor(Slave.class).setShouldAlwaysRefreshCacheOnRemote(true);

        slaves = getAllSlaves();
        masters = getAllMasters();

        for (Enumeration masterEnum = masters.elements(); masterEnum.hasMoreElements(); ) {
            Master master = (Master)masterEnum.nextElement();
            master.getSlaves();
        }

        slaves = getAllSlaves();
        masters = getAllMasters();
    }

    protected void verify() {
        for (Enumeration slave_enum = slaves.elements(); slave_enum.hasMoreElements(); ) {
            Slave slave = (Slave)slave_enum.nextElement();
            if (!slave.getMaster().getSlaves().contains(slave)) {
                throw new TestErrorException("Identity Violated");
            }
        }
    }

    protected Vector getAllMasters() {
        ReadAllQuery query = new ReadAllQuery(Master.class);
        return (Vector)getSession().executeQuery(query);
    }

    protected Vector getAllSlaves() {
        ReadAllQuery query = new ReadAllQuery(Slave.class);
        return (Vector)getSession().executeQuery(query);
    }

}

