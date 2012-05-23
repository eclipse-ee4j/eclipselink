/*******************************************************************************
 * Copyright (c) 2010, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.platform.database.oracle.ucp;

import java.lang.reflect.Method;
import javax.sql.DataSource;

import oracle.ucp.jdbc.oracle.DataBasedConnectionAffinityCallback;

import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

/**
 * PUBLIC:
 * Integrates with WebLogic GirdLink's data affinity support.
 * 
 * @see org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy
 * @author James Sutherland
 * @since EclipseLink 2.3
 */
public class GridLinkDataPartitioningCallback extends UCPDataPartitioningCallback {
    /** The id is stored in a static thread local. */
    protected static ThreadLocal partitionId = new ThreadLocal();
    
    public static boolean isRegistered = false;
    
    /**
     * Registration only occurs once in WLS (against all data sources), so must be static registered.
     */
    public void register(DataSource datSource, Session session) {
        if (isRegistered) {
            return;
        }
        register(session);
    }
    
    /**
     * Register with WLS through reflection.
     */
    public static synchronized void register(Session session) {
        if (isRegistered) {
            return;
        }
        try {
            Class dataSourceManager = PrivilegedAccessHelper.getClassForName("weblogic.jdbc.common.internal.DataSourceManager");
            Method getInstance = PrivilegedAccessHelper.getMethod(dataSourceManager, "getInstance", null, false);
            Object instance = PrivilegedAccessHelper.invokeMethod(getInstance, null, null);
            Method getDataSourceService = PrivilegedAccessHelper.getMethod(instance.getClass(), "getDataSourceService", null, false);
            Object service = PrivilegedAccessHelper.invokeMethod(getDataSourceService, instance, null);
            Class[] argumentTypes = new Class[] {DataBasedConnectionAffinityCallback.class};
            Method registerDataAffinityCallback = PrivilegedAccessHelper.getMethod(service.getClass(), "registerDataAffinityCallback", argumentTypes, false);
            Object[] arguments = new Object[] {new GridLinkDataPartitioningCallback()};
            PrivilegedAccessHelper.invokeMethod(registerDataAffinityCallback, service, arguments);
            isRegistered = true;
        } catch (Exception exception) {
            session.getSessionLog().logThrowable(SessionLog.WARNING, SessionLog.CONNECTION, exception);
        }
    }
    
    /**
     * Set the partition id for this thread.
     */
    public void setPartitionId(int id) {
        partitionId.set(id);
    }
    
    public int getPartitionId() {
        Integer id = (Integer)partitionId.get();
        if (id == null) {
            return 0;
        }
        return id;
    }
}
