/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.metadata.cache;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.CacheCoordinationType;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

/**
 * Object to hold onto cache metadata. This class should eventually be 
 * extended by an XMLCache.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class CacheMetadata  {
	protected Boolean m_alwaysRefresh;
	protected Boolean m_disableHits;
	protected Boolean m_isolated;
	protected Boolean m_refreshOnlyIfNewer;
	
	protected Enum m_coordinationType;
    protected Enum m_type;
    
	protected Integer m_expiry;
	protected Integer m_size;
	
	protected TimeOfDayMetadata m_expiryTimeOfDay;

    /**
     * INTERNAL:
     */
    public CacheMetadata() {}
    
    /**
     * INTERNAL:
     */
    public CacheMetadata(Object cache) {
        setAlwaysRefresh((Boolean)MetadataHelper.invokeMethod("alwaysRefresh", cache, (Object[])null));
        setDisableHits((Boolean)MetadataHelper.invokeMethod("disableHits", cache, (Object[])null));
        setCoordinationType((Enum)MetadataHelper.invokeMethod("coordinationType", cache, (Object[])null));
        setExpiry((Integer)MetadataHelper.invokeMethod("expiry", cache, (Object[])null));

        Object expiryTimeOfDay = MetadataHelper.invokeMethod("expiryTimeOfDay", cache, (Object[])null);
        
        if ((Boolean)MetadataHelper.invokeMethod("specified", expiryTimeOfDay, (Object[])null)) {
            setExpiryTimeOfDay(new TimeOfDayMetadata(expiryTimeOfDay));
        }
        
        setIsolated((Boolean)MetadataHelper.invokeMethod("isolated", cache, (Object[])null));
        setSize((Integer)MetadataHelper.invokeMethod("size", cache, (Object[])null));
        setType((Enum)MetadataHelper.invokeMethod("type", cache, (Object[])null));
        setRefreshOnlyIfNewer((Boolean)MetadataHelper.invokeMethod("refreshOnlyIfNewer", cache, (Object[])null));
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getAlwaysRefresh() {
    	return m_alwaysRefresh; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getCoordinationType() {
    	return m_coordinationType; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getDisableHits() {
        return m_disableHits; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getExpiry() {
    	return m_expiry; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public TimeOfDayMetadata getExpiryTimeOfDay() { 
        return m_expiryTimeOfDay;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getIsolated() {
       return m_isolated; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getRefreshOnlyIfNewer() {
       return m_refreshOnlyIfNewer; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getSize() {
    	return m_size;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Enum getType() {
       return m_type;
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor, Class javaClass) {
        // Set the cache flag on the Metadata Descriptor.
        descriptor.setCacheIsSet();
        
        // Process the cache metadata.
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();
        
        // Process type
        if (m_type == null ||m_type.equals(CacheType.SOFT_WEAK)) {
            classDescriptor.useSoftCacheWeakIdentityMap();
        } else if (m_type.equals(CacheType.FULL)) {
            classDescriptor.useFullIdentityMap();
        } else if (m_type.equals(CacheType.WEAK)) {
            classDescriptor.useWeakIdentityMap();
        }  else if (m_type.equals(CacheType.SOFT)) {
            classDescriptor.useSoftIdentityMap();
        } else if (m_type.equals(CacheType.HARD_WEAK)) {
            classDescriptor.useHardCacheWeakIdentityMap();
        } else if (m_type.equals(CacheType.CACHE)) {
            classDescriptor.useCacheIdentityMap();
        } else if (m_type.equals(CacheType.NONE)) {
            classDescriptor.useNoIdentityMap();
        }
        
        // Process size.
        classDescriptor.setIdentityMapSize((m_size == null) ? 100 : m_size);
        
        // Process isolated.
        classDescriptor.setIsIsolated(m_isolated == null ? false : m_isolated);
        
        // Process expiry or expiry time of day.
        if (m_expiryTimeOfDay == null) {
            // Expiry time of day is not specified, look for an expiry.
            if (m_expiry != null && m_expiry != -1) {
                classDescriptor.setCacheInvalidationPolicy(new TimeToLiveCacheInvalidationPolicy(m_expiry));
            }
        } else {
            // Expiry time of day is specified, if expiry is also specified, 
            // throw an exception.
            if (m_expiry == null || m_expiry == -1) {
                classDescriptor.setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(m_expiryTimeOfDay.processHour(), m_expiryTimeOfDay.processMinute(), m_expiryTimeOfDay.processSecond(), m_expiryTimeOfDay.processMillisecond()));
            } else {
                throw ValidationException.cacheExpiryAndExpiryTimeOfDayBothSpecified(javaClass);
            }
        }
        
        // Process always refresh.
        classDescriptor.setShouldAlwaysRefreshCache(m_alwaysRefresh == null ? false : m_alwaysRefresh);
        
        // Process refresh only if newer.
        classDescriptor.setShouldOnlyRefreshCacheIfNewerVersion(m_refreshOnlyIfNewer == null ? false : m_refreshOnlyIfNewer);
        
        // Process disable hits.
        classDescriptor.setShouldDisableCacheHits(m_disableHits == null ? false : m_disableHits);
        
        // Process coordination type.
        if (m_coordinationType == null || m_coordinationType.equals(CacheCoordinationType.SEND_OBJECT_CHANGES)) {
            classDescriptor.setCacheSynchronizationType(ClassDescriptor.SEND_OBJECT_CHANGES);
        } else if (m_coordinationType.equals(CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)) {
            classDescriptor.setCacheSynchronizationType(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS);
        } else if (m_coordinationType.equals(CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES)) {
            classDescriptor.setCacheSynchronizationType(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES);
        } else if (m_coordinationType.equals(CacheCoordinationType.NONE)) {
            classDescriptor.setCacheSynchronizationType(ClassDescriptor.DO_NOT_SEND_CHANGES);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping 
     */
    public void setAlwaysRefresh(Boolean alwaysRefresh) {
    	m_alwaysRefresh = alwaysRefresh;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setCoordinationType(Enum coordinationType) {
        m_coordinationType = coordinationType; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDisableHits(Boolean disableHits) {
    	m_disableHits = disableHits; 
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setExpiry(Integer expiry) {
       m_expiry = expiry; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setExpiryTimeOfDay(TimeOfDayMetadata expiryTimeOfDay) { 
        m_expiryTimeOfDay = expiryTimeOfDay;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setIsolated(Boolean isolated) {
       m_isolated = isolated; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSize(Integer size) {
    	m_size = size;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setType(Enum type) {
       m_type = type;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setRefreshOnlyIfNewer(Boolean refreshOnlyIfNewer) {
    	m_refreshOnlyIfNewer = refreshOnlyIfNewer;
    }
}
