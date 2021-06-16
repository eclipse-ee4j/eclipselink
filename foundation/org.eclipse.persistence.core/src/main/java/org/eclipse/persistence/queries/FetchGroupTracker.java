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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.queries;

import org.eclipse.persistence.sessions.Session;

/**
 * <p><b>Purpose</b>: The fetch group tracker interface provides a set of APIs which
 * the domain object must implement, in order to take advantage of the EclipseLink fetch group
 * performance enhancement feature.
 *
 * @see org.eclipse.persistence.queries.FetchGroup
 *
 * @author King Wang
 * @since TopLink 10.1.3
 */
public interface FetchGroupTracker {

    /**
     * Return the fetch group being tracked
     */
    FetchGroup _persistence_getFetchGroup();

    /**
     * Set a fetch group to be tracked.
     */
    void _persistence_setFetchGroup(FetchGroup group);

    /**
     * Return true if the attribute is in the fetch group being tracked.
     */
    boolean _persistence_isAttributeFetched(String attribute);

    /**
     * Reset all attributes of the tracked object to the unfetched state with initial default values.
     */
    void _persistence_resetFetchGroup();

    /**
     * Return true if the fetch group attributes should be refreshed.
     */
    boolean _persistence_shouldRefreshFetchGroup();

    /**
     * Set true if the fetch group attributes should be refreshed.
     */
    void _persistence_setShouldRefreshFetchGroup(boolean shouldRefreshFetchGroup);

    /**
     * Return the session for the object.
     */
    Session _persistence_getSession();

    /**
     * Set true if the fetch group attributes should be refreshed
     */
    void _persistence_setSession(Session session);
}
