/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
