/*
 * Copyright (c) 2013, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Guy Pelletier - initial API and implementation
package org.eclipse.persistence.jpa.config;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public interface Cache {

    public Cache setAlwaysRefresh(Boolean alwaysRefresh);
    public Cache setCoordinationType(String coordinationType);
    public Cache setDatabaseChangeNotificationType(String databaseChangeNotificationType);
    public Cache setDisableHits(Boolean disableHits);
    public Cache setExpiry(Integer expiry);
    public TimeOfDay setExpiryTimeOfDay();
    public Cache setIsolation(String isolation);
    public Cache setRefreshOnlyIfNewer(Boolean refreshOnlyIfNewer);
    public Cache setSize(Integer size);
    public Cache setType(String type);

}
