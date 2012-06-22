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
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.viewcache;

import java.util.ArrayList;
import static org.eclipse.persistence.platform.database.oracle.publisher.viewcache.ViewCache.PARAMETER_USER;

public class CreateViewCacheThread extends Thread {

    ArrayList<String> m_viewCacheParameters;
    ViewCache m_viewCache;

    CreateViewCacheThread(ViewCache viewCache, ArrayList<String> viewCacheParameters) {
        m_viewCache = viewCache;
        m_viewCacheParameters = viewCacheParameters;
    }

    public void run() {
        try {
            if (m_viewCacheParameters == null || m_viewCacheParameters.isEmpty()) {
                m_viewCache.fetch(PARAMETER_USER, null);
            }
            else {
                for (int i = 0; i < m_viewCacheParameters.size(); i++) {
                    m_viewCache.fetch(m_viewCacheParameters.get(i), null);
                }
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
