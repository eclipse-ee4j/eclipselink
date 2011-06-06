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
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.NoExpiryCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public final class MWDescriptorCacheExpiry extends MWModel implements MWCacheExpiry {
    
    //******* cache Invalidation *******
    private volatile String expiryType;
    
    private volatile boolean updateReadTimeOnUpdate; //does not apply for expiryType CACHE_EXPIRY_NO_EXIPRY

    private volatile Long timeToLiveExpiry; //only used used for expiryType CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY

    // This is a Date for convenience because initially, the information was tracked as a Date in foundation.
    // This no longer appears to be the case.  Perhaps this should be changed to a better type now?
    private volatile Date dailyExpiryTime; //only used for expiryType CACHE_EXPIRY_DAILY_EXPIRY

    
    // ********** static methods **********
    
    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MWDescriptorCacheExpiry.class);

        // Cache Expiry
        ObjectTypeConverter cacheExpiryMappingConverter = new ObjectTypeConverter();
        cacheExpiryMappingConverter.addConversionValue(
                CACHE_EXPIRY_NO_EXPIRY,
                CACHE_EXPIRY_NO_EXPIRY);
        cacheExpiryMappingConverter.addConversionValue(
                CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY,
                CACHE_EXPIRY_TIME_TO_LIVE_EXPIRY);
        cacheExpiryMappingConverter.addConversionValue(
                CACHE_EXPIRY_DAILY_EXPIRY,
                CACHE_EXPIRY_DAILY_EXPIRY);
        XMLDirectMapping cacheExpiryMapping = new XMLDirectMapping();
        cacheExpiryMapping.setAttributeName("expiryType");
        cacheExpiryMapping.setNullValue(DEFAULT_CACHE_EXPIRY);
        cacheExpiryMapping.setXPath("expiry-type/text()");
        cacheExpiryMapping.setConverter(cacheExpiryMappingConverter);
        descriptor.addMapping(cacheExpiryMapping);

        // Time to Live Expiry
        XMLDirectMapping timeToLiveExpiryMapping = (XMLDirectMapping) descriptor.addDirectMapping("timeToLiveExpiry", "time-to-live-expiry/text()");
        timeToLiveExpiryMapping.setNullValue(DEFAULT_TIME_TO_LIVE_EXPIRY);

        // Daily Expire Time
        XMLDirectMapping dailyExpireTimeMapping = (XMLDirectMapping) descriptor.addDirectMapping("dailyExpiryTime", "daily-expiry-time/text()");
        dailyExpireTimeMapping.setNullValue(DEFAULT_DAILY_EXPIRY_TIME);

        // Update Read Time on Update

        XMLDirectMapping updateReadTimeOnUpdateMapping = (XMLDirectMapping) descriptor.addDirectMapping("updateReadTimeOnUpdate", "update-read-time-on-update/text()");
        updateReadTimeOnUpdateMapping.setNullValue(Boolean.valueOf(DEFAULT_UPDATE_READ_TIME_ON_UPDATE));
        
        return descriptor;
    }

    
    // ********** Constructors **********
    
    /** For TopLink use only */
    private MWDescriptorCacheExpiry() {
        super();
    }

    public MWDescriptorCacheExpiry(MWCachingPolicy parent) {
        super(parent);
    }
    
    protected void initialize(Node parent) {
        super.initialize(parent);
        this.dailyExpiryTime        = DEFAULT_DAILY_EXPIRY_TIME;
        this.expiryType             = DEFAULT_CACHE_EXPIRY;
        this.timeToLiveExpiry       = DEFAULT_TIME_TO_LIVE_EXPIRY;
        this.updateReadTimeOnUpdate = DEFAULT_UPDATE_READ_TIME_ON_UPDATE;
    }

    
    public Date getDailyExpiryTime() {
        return this.dailyExpiryTime;
    }

    public void setDailyExpiryTime(Date dailyExpiryTime) {
        Date oldDailyExpiryTime = this.dailyExpiryTime;
        if (newExpiryTimeDifferent(dailyExpiryTime)) {
        	this.dailyExpiryTime = dailyExpiryTime;
        	firePropertyChanged(DAILY_EXPIRY_TIME_PROPERTY, oldDailyExpiryTime, this.dailyExpiryTime);
        }
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
    
    /**
     * Convenience method for setting daily expiry time.  Dealing with Date objects can be painful, especially
     * when trying to work with ms offsets into the current second.
     * 
     * @param dailyExpiryTime
     */
    public void setDailyExpiryTime(Calendar dailyExpiryTime) {
        setDailyExpiryTime(dailyExpiryTime.getTime());
    }

    public String getExpiryType() {
        return this.expiryType;
    }

    public void setExpiryType(String expiryType) {
        String oldExpiryType = this.expiryType;
        this.expiryType = expiryType;
        firePropertyChanged(CACHE_EXPIRY_TYPE_PROPERTY, oldExpiryType, this.expiryType);
        if (valuesAreDifferent(oldExpiryType, this.expiryType)) {
            if (this.expiryType == CACHE_EXPIRY_NO_EXPIRY) {
                //This setting does not apply when the cache expiry is set to no expiry
                setUpdateReadTimeOnUpdate(false);
            }
        }
    }
    
    public boolean getUpdateReadTimeOnUpdate() {
        return this.updateReadTimeOnUpdate;
    }

    public void setUpdateReadTimeOnUpdate(boolean updateReadTimeOnUpdate) {
        boolean oldUpdateReadTimeOnUpdate = this.updateReadTimeOnUpdate;
        this.updateReadTimeOnUpdate = updateReadTimeOnUpdate;
        firePropertyChanged(UPDATE_READ_TIME_ON_UPDATE_PROPERTY, oldUpdateReadTimeOnUpdate, this.updateReadTimeOnUpdate);
    }


    public Long getTimeToLiveExpiry() {
        return this.timeToLiveExpiry;
    }
    
    public void setTimeToLiveExpiry(Long timeToLiveExpiry) {
        Long oldTimeToLive = this.timeToLiveExpiry;
        this.timeToLiveExpiry = timeToLiveExpiry;
        firePropertyChanged(TIME_TO_LIVE_EXPIRY_PROPERTY, oldTimeToLive, this.timeToLiveExpiry);
    }

    public MWMappingDescriptor getOwningDescriptor() {
        return (MWMappingDescriptor) ((MWTransactionalPolicy) this.getParent()).getParent();
    }

    
    // ***************** runtime conversion **********************
    
    public void adjustRuntimeDescriptor(ClassDescriptor runtimeDescriptor) {
        if (getExpiryType() == CACHE_EXPIRY_NO_EXPIRY) {
            runtimeDescriptor.setCacheInvalidationPolicy(new NoExpiryCacheInvalidationPolicy());
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
        return this;
    }

}
