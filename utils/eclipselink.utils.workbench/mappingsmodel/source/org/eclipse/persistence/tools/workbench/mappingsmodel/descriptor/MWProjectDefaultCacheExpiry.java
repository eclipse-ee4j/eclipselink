/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;

public final class MWProjectDefaultCacheExpiry extends MWModel implements MWCacheExpiry {

    // ********** Constructors **********
    
    /** For TopLink use only */
    private MWProjectDefaultCacheExpiry() {
        super();
    }

    MWProjectDefaultCacheExpiry(MWCachingPolicy parent) {
        super(parent);
    }
    
    private MWCacheExpiry getProjectCacheExpiry() {
        return getProject().getDefaultsPolicy().getCachingPolicy().getCacheExpiry();
    }

    
    public Date getDailyExpiryTime() {
        return getProjectCacheExpiry().getDailyExpiryTime();
    }

    private boolean newExpiryTimeDifferent(Date newExpiryTime) {
    	Date oldExpiryTime = getDailyExpiryTime();
    	if (newExpiryTime.getHours() != oldExpiryTime.getHours()
    			|| newExpiryTime.getMinutes() != oldExpiryTime.getMinutes()
    			||  newExpiryTime.getSeconds() !=  oldExpiryTime.getSeconds()) {
    		return true;
    	}
    	return false;
    }
    
    public void setDailyExpiryTime(Date dailyExpiryTime) {
        if (newExpiryTimeDifferent(dailyExpiryTime)) {
            getCachingPolicy().setUseProjectDefaultCacheExpiry(false);
            getCachingPolicy().getCacheExpiry().setDailyExpiryTime(dailyExpiryTime);
        }
    }

    public void setDailyExpiryTime(Calendar dailyExpiryTime) {
        if (valuesAreDifferent(dailyExpiryTime.getTime(), getDailyExpiryTime())) {
            getCachingPolicy().setUseProjectDefaultCacheExpiry(false);
            getCachingPolicy().getCacheExpiry().setDailyExpiryTime(dailyExpiryTime);
        }
    }

    public String getExpiryType() {
        return getProjectCacheExpiry().getExpiryType();    
    }

    public void setExpiryType(String expiryType) {
        if (valuesAreDifferent(expiryType, getExpiryType())) {
            getCachingPolicy().setUseProjectDefaultCacheExpiry(false);
            getCachingPolicy().getCacheExpiry().setExpiryType(expiryType);
        }
    }
    
    public boolean getUpdateReadTimeOnUpdate() {
        return getProjectCacheExpiry().getUpdateReadTimeOnUpdate();
    }

    public void setUpdateReadTimeOnUpdate(boolean updateReadTimeOnUpdate) {
        if (updateReadTimeOnUpdate != getUpdateReadTimeOnUpdate()) {
            getCachingPolicy().setUseProjectDefaultCacheExpiry(false);
            getCachingPolicy().getCacheExpiry().setUpdateReadTimeOnUpdate(updateReadTimeOnUpdate);
        }
    }


    public Long getTimeToLiveExpiry() {
        return getProjectCacheExpiry().getTimeToLiveExpiry();
    }
    
    public void setTimeToLiveExpiry(Long timeToLiveExpiry) {
        if (timeToLiveExpiry != getTimeToLiveExpiry()) {
            getCachingPolicy().setUseProjectDefaultCacheExpiry(false);
            getCachingPolicy().getCacheExpiry().setTimeToLiveExpiry(timeToLiveExpiry);
        }
    }

    private MWDescriptorCachingPolicy getCachingPolicy() {
        return (MWDescriptorCachingPolicy) getParent();
    }
    
    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
        if (getExpiryType() == CACHE_EXPIRY_NO_EXPIRY) {
            // Do nothing, default case
        }
        else if (getExpiryType() == CACHE_EXPIRY_DAILY_EXPIRY) {
            Date expiryTime = getDailyExpiryTime();
            runtimeDescriptor.setCacheInvalidationPolicy(
                    new DailyCacheInvalidationPolicy(expiryTime.getHours(), expiryTime.getMinutes(), 
                                                     expiryTime.getSeconds(), 0));
        }
        else if (getExpiryType() == CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY) {
            runtimeDescriptor.setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(getTimeToLiveExpiry().longValue()));
        }
    }
    
    public MWCacheExpiry getPersistedPolicy() {
        return null;
    }
}
