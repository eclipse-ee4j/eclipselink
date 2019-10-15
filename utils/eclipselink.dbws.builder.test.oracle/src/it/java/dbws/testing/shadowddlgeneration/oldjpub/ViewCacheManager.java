/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

//javase imports
import java.sql.Connection;

public class ViewCacheManager {

    @SuppressWarnings("unused")
    private Connection m_conn;
    private ViewCachePool m_viewCachePool;

    public ViewCacheManager(Connection conn) {
        m_conn = conn;
        m_viewCachePool = new ViewCachePool();
    }

    public void add(ViewCache viewCache) {
        m_viewCachePool.add(viewCache);
    }

    public ViewCache get(String schema) {
        return (ViewCache)m_viewCachePool.get(schema);
    }

    /**
     * The CLASSPATH_MARKER is used as a prefix to the ViewCacheDir. If it is used, it means we
     * should try and look up the view cache as a class path resource instead of as a file.
     */
    public static String CLASSPATH_MARKER = ":;";

    public void setViewCachePoolCapacity(int size) {
        m_viewCachePool.setCapacity(size);
    }

    public void clearViewCache() {
        m_viewCachePool.clear();
    }

}
