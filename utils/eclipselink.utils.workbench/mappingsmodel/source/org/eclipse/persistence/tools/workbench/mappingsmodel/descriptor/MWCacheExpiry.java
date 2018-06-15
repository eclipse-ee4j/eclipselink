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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;

import org.eclipse.persistence.descriptors.ClassDescriptor;

public interface MWCacheExpiry extends MWNode {

    // Cache Expiry Types
    String CACHE_EXPIRY_NO_EXPIRY = "No Expiry";
    String CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY = "Time to Live Expiry";
    String CACHE_EXPIRY_DAILY_EXPIRY = "Daily Expiry";
    String DEFAULT_CACHE_EXPIRY = CACHE_EXPIRY_NO_EXPIRY;

    String getExpiryType();
    void setExpiryType(String expiryType);
        String CACHE_EXPIRY_TYPE_PROPERTY = "expiryType";

    // Daily Expiry Time
    Date DEFAULT_DAILY_EXPIRY_TIME = new Date(0, 0, 0, 0, 0, 0);

    Date getDailyExpiryTime();
    void setDailyExpiryTime(Date dailyExpiryTime);
    void setDailyExpiryTime(Calendar dailyExpiryTime);
        String DAILY_EXPIRY_TIME_PROPERTY = "dailyExpiryTime";

    // Time to Live Expiry
    Long DEFAULT_TIME_TO_LIVE_EXPIRY = new Long(0);

    Long getTimeToLiveExpiry();
    void setTimeToLiveExpiry(Long timeToLiveExpiry);
        String TIME_TO_LIVE_EXPIRY_PROPERTY = "timeToLiveExpiry";


    // Update Read Time on Update
    boolean DEFAULT_UPDATE_READ_TIME_ON_UPDATE = false;

    boolean getUpdateReadTimeOnUpdate();
    void setUpdateReadTimeOnUpdate(boolean updateReadTimeOnUpdate);
        String UPDATE_READ_TIME_ON_UPDATE_PROPERTY = "updateReadTimeOnUpdate";


    void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor);

    MWCacheExpiry getPersistedPolicy();

}
