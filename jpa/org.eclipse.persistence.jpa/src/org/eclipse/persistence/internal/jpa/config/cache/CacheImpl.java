/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.config.cache;

import org.eclipse.persistence.internal.jpa.config.MetadataImpl;
import org.eclipse.persistence.internal.jpa.metadata.cache.CacheMetadata;
import org.eclipse.persistence.jpa.config.Cache;
import org.eclipse.persistence.jpa.config.TimeOfDay;

/**
 * JPA scripting API implementation.
 *
 * @author Guy Pelletier
 * @since EclipseLink 2.5.1
 */
public class CacheImpl extends MetadataImpl<CacheMetadata> implements Cache {

    public CacheImpl() {
        super(new CacheMetadata());
    }

    public Cache setAlwaysRefresh(Boolean alwaysRefresh) {
        getMetadata().setAlwaysRefresh(alwaysRefresh);
        return this;
    }

    public Cache setCoordinationType(String coordinationType) {
        getMetadata().setCoordinationType(coordinationType);
        return this;
    }

    public Cache setDatabaseChangeNotificationType(String databaseChangeNotificationType) {
        getMetadata().setDatabaseChangeNotificationType(databaseChangeNotificationType);
        return this;
    }

    public Cache setDisableHits(Boolean disableHits) {
        getMetadata().setDisableHits(disableHits);
        return this;
    }

    public Cache setExpiry(Integer expiry) {
        getMetadata().setExpiry(expiry);
        return this;
    }

    public TimeOfDay setExpiryTimeOfDay() {
        TimeOfDayImpl timeOfDay = new TimeOfDayImpl();
        getMetadata().setExpiryTimeOfDay(timeOfDay.getMetadata());
        return timeOfDay;
    }

    public Cache setIsolation(String isolation) {
        getMetadata().setIsolation(isolation);
        return this;
    }

    public Cache setRefreshOnlyIfNewer(Boolean refreshOnlyIfNewer) {
        getMetadata().setRefreshOnlyIfNewer(refreshOnlyIfNewer);
        return this;
    }

    public Cache setShared(Boolean shared) {
        getMetadata().setShared(shared);
        return this;
    }

    public Cache setSize(Integer size) {
        getMetadata().setSize(size);
        return this;
    }

    public Cache setType(String type) {
        getMetadata().setType(type);
        return this;
    }

}
