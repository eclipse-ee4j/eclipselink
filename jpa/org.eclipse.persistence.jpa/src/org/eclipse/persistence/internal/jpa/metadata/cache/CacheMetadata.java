/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.cache;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.CacheCoordinationType;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * Object to hold onto cache metadata. This class should eventually be 
 * extended by an XMLCache.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class CacheMetadata extends ORMetadata {
    protected Boolean m_alwaysRefresh;
    protected Boolean m_disableHits;
    protected Boolean m_shared;
    protected Boolean m_refreshOnlyIfNewer;
    
    protected Enum m_coordinationType;
    protected Enum m_type;
    
    protected Integer m_expiry;
    protected Integer m_size;
    
    protected TimeOfDayMetadata m_expiryTimeOfDay;

    /**
     * INTERNAL:
     */
    public CacheMetadata() {
        super("<cache>");
    }
    
    /**
     * INTERNAL:
     */
    public CacheMetadata(Annotation cache, MetadataAccessibleObject accessibleObject) {
        super(cache, accessibleObject);
        
        m_alwaysRefresh = (Boolean) MetadataHelper.invokeMethod("alwaysRefresh", cache);
        m_disableHits = (Boolean) MetadataHelper.invokeMethod("disableHits", cache);
        m_coordinationType = (Enum) MetadataHelper.invokeMethod("coordinationType", cache);
        m_expiry = (Integer) MetadataHelper.invokeMethod("expiry", cache);

        Annotation expiryTimeOfDay = (Annotation) MetadataHelper.invokeMethod("expiryTimeOfDay", cache);
        
        if ((Boolean) MetadataHelper.invokeMethod("specified", expiryTimeOfDay)) {
            m_expiryTimeOfDay = new TimeOfDayMetadata(expiryTimeOfDay, accessibleObject);
        }
        
        m_shared = (Boolean) MetadataHelper.invokeMethod("shared", cache);
        m_size = (Integer) MetadataHelper.invokeMethod("size", cache);
        m_type = (Enum) MetadataHelper.invokeMethod("type", cache);
        m_refreshOnlyIfNewer = (Boolean) MetadataHelper.invokeMethod("refreshOnlyIfNewer", cache);
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
    public Boolean getRefreshOnlyIfNewer() {
       return m_refreshOnlyIfNewer; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Boolean getShared() {
       return m_shared; 
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
        // Set the cache flag on the metadata Descriptor.
        descriptor.setHasCache();
        
        // Process the cache metadata.
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();
        
        // Process type
        if (m_type == null ||m_type.name().equals(CacheType.SOFT_WEAK.name())) {
            classDescriptor.useSoftCacheWeakIdentityMap();
        } else if (m_type.name().equals(CacheType.FULL.name())) {
            classDescriptor.useFullIdentityMap();
        } else if (m_type.name().equals(CacheType.WEAK.name())) {
            classDescriptor.useWeakIdentityMap();
        }  else if (m_type.name().equals(CacheType.SOFT.name())) {
            classDescriptor.useSoftIdentityMap();
        } else if (m_type.name().equals(CacheType.HARD_WEAK.name())) {
            classDescriptor.useHardCacheWeakIdentityMap();
        } else if (m_type.name().equals(CacheType.CACHE.name())) {
            classDescriptor.useCacheIdentityMap();
        } else if (m_type.name().equals(CacheType.NONE.name())) {
            classDescriptor.useNoIdentityMap();
        }
        
        // Process size.
        classDescriptor.setIdentityMapSize((m_size == null) ? 100 : m_size);
        
        // Process shared.
        classDescriptor.setIsIsolated(m_shared == null ? false : ! m_shared);
        
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
        if (m_coordinationType == null || m_coordinationType.name().equals(CacheCoordinationType.SEND_OBJECT_CHANGES.name())) {
            classDescriptor.setCacheSynchronizationType(ClassDescriptor.SEND_OBJECT_CHANGES);
        } else if (m_coordinationType.name().equals(CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS.name())) {
            classDescriptor.setCacheSynchronizationType(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS);
        } else if (m_coordinationType.name().equals(CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES.name())) {
            classDescriptor.setCacheSynchronizationType(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES);
        } else if (m_coordinationType.name().equals(CacheCoordinationType.NONE.name())) {
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
    public void setRefreshOnlyIfNewer(Boolean refreshOnlyIfNewer) {
        m_refreshOnlyIfNewer = refreshOnlyIfNewer;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setShared(Boolean shared) {
       m_shared = shared; 
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
}
