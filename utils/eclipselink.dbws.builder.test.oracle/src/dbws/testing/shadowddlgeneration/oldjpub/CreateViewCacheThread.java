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
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

import java.util.ArrayList;
import static dbws.testing.shadowddlgeneration.oldjpub.ViewCache.PARAMETER_USER;

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
