/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     03/15/2016 Jody Grassel
//       - 489794: Add support for WebSphere EJBEmbeddable platform.

package org.eclipse.persistence.platform.server.was;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.transaction.was.WebSphereEJBEmbeddableTransactionController;

public class WebSphere_EJBEmbeddable_Platform extends WebSphere_7_Platform {
    /**
     * INTERNAL: Default Constructor: All behavior for the default constructor is inherited
     */
    public WebSphere_EJBEmbeddable_Platform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction
     * controller to use for WebSphere EJBEmbeddable. This is read-only.
     * 
     * @return Class externalTransactionControllerClass
     * 
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#isJTAEnabled()
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#disableJTA()
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase#initializeExternalTransactionController()
     */
    @Override
    public Class getExternalTransactionControllerClass() {
        if (externalTransactionControllerClass == null) {
            externalTransactionControllerClass = WebSphereEJBEmbeddableTransactionController.class;
        }
        return externalTransactionControllerClass;
    }
}
