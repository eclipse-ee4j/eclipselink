/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//      tware - initial implementation
package org.eclipse.persistence.jpa.rs.eventlistener;

import org.eclipse.persistence.platform.database.events.DatabaseEventListener;
import org.eclipse.persistence.sessions.Session;

/**
 * Extends EclipseLink's database event listening capabilities by allowing a listener to subscribe to
 * to change notifications from the database
 *
 * This listener also expands the interface to support subscription on a descriptor basis rather than
 * the wholesale subscription provided by its superclass.
 *
 * @author tware
 *
 */
public interface DescriptorBasedDatabaseEventListener extends DatabaseEventListener {

    /**
     * Register for change notifications on a particular descriptor
     * @param session
     * @param queryName
     */
    public void register(Session session, String queryName);

    public void addChangeListener(ChangeListener listener);

    public void removeChangeListener(ChangeListener listener);
}
