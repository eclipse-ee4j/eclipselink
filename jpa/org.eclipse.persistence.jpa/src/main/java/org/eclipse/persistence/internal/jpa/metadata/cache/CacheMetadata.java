/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/16/2008-1.0M8 Guy Pelletier
//       - 218084: Implement metadata merging functionality between mapping files
//     04/27/2010-2.1 Guy Pelletier
//       - 309856: MappedSuperclasses from XML are not being initialized properly
//     03/24/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
package org.eclipse.persistence.internal.jpa.metadata.cache;

import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.config.CacheIsolationType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.invalidation.DailyCacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * Object to hold onto cache metadata.
 *
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 *
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class CacheMetadata extends ORMetadata {
    protected Boolean m_alwaysRefresh;
    protected Boolean m_disableHits;
    protected String m_isolation;
    protected Boolean m_refreshOnlyIfNewer;

    protected String m_coordinationType;
    protected String m_databaseChangeNotificationType;
    protected String m_type;

    protected Integer m_expiry;
    protected Integer m_size;

    protected TimeOfDayMetadata m_expiryTimeOfDay;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public CacheMetadata() {
        super("<cache>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public CacheMetadata(MetadataAnnotation cache, MetadataAccessor accessor) {
        super(cache, accessor);

        m_alwaysRefresh = cache.getAttributeBooleanDefaultFalse("alwaysRefresh");
        m_disableHits = cache.getAttributeBooleanDefaultFalse("disableHits");
        m_coordinationType = cache.getAttributeString("coordinationType");
        m_databaseChangeNotificationType = cache.getAttributeString("databaseChangeNotificationType");
        m_expiry = cache.getAttributeInteger("expiry");

        MetadataAnnotation expiryTimeOfDay = cache.getAttributeAnnotation("expiryTimeOfDay");

        if (expiryTimeOfDay != null) {
            m_expiryTimeOfDay = new TimeOfDayMetadata(expiryTimeOfDay, accessor);
        }

        m_isolation = cache.getAttributeString("isolation");
        m_size = cache.getAttributeInteger("size");
        m_type = cache.getAttributeString("type");
        m_refreshOnlyIfNewer = cache.getAttributeBooleanDefaultFalse("refreshOnlyIfNewer");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof CacheMetadata) {
            CacheMetadata cache = (CacheMetadata) objectToCompare;

            if (! valuesMatch(m_alwaysRefresh, cache.getAlwaysRefresh())) {
                return false;
            }

            if (! valuesMatch(m_disableHits, cache.getDisableHits())) {
                return false;
            }

            if (! valuesMatch(m_isolation, cache.getIsolation())) {
                return false;
            }

            if (! valuesMatch(m_refreshOnlyIfNewer, cache.getRefreshOnlyIfNewer())) {
                return false;
            }

            if (! valuesMatch(m_coordinationType, cache.getCoordinationType())) {
                return false;
            }

            if (! valuesMatch(m_databaseChangeNotificationType, cache.getDatabaseChangeNotificationType())) {
                return false;
            }

            if (! valuesMatch(m_type, cache.getType())) {
                return false;
            }

            if (! valuesMatch(m_expiry, cache.getExpiry())) {
                return false;
            }

            if (! valuesMatch(m_size, cache.getSize())) {
                return false;
            }

            return valuesMatch(m_expiryTimeOfDay, cache.getExpiryTimeOfDay());
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = m_alwaysRefresh != null ? m_alwaysRefresh.hashCode() : 0;
        result = 31 * result + (m_disableHits != null ? m_disableHits.hashCode() : 0);
        result = 31 * result + (m_isolation != null ? m_isolation.hashCode() : 0);
        result = 31 * result + (m_refreshOnlyIfNewer != null ? m_refreshOnlyIfNewer.hashCode() : 0);
        result = 31 * result + (m_coordinationType != null ? m_coordinationType.hashCode() : 0);
        result = 31 * result + (m_databaseChangeNotificationType != null ? m_databaseChangeNotificationType.hashCode() : 0);
        result = 31 * result + (m_type != null ? m_type.hashCode() : 0);
        result = 31 * result + (m_expiry != null ? m_expiry.hashCode() : 0);
        result = 31 * result + (m_size != null ? m_size.hashCode() : 0);
        result = 31 * result + (m_expiryTimeOfDay != null ? m_expiryTimeOfDay.hashCode() : 0);
        return result;
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
    public String getCoordinationType() {
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

    public String getIsolation(){
        return m_isolation;
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
    public String getType() {
       return m_type;
    }

    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor, MetadataClass javaClass) {
        // Set the cache flag on the metadata Descriptor.
        descriptor.setHasCache();

        // Process the cache metadata.
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();

        // Process type
        if (m_type == null) {
            // Leave as default.
        } else if (m_type.equals(CacheType.SOFT_WEAK.name())) {
            classDescriptor.useSoftCacheWeakIdentityMap();
        } else if (m_type.equals(CacheType.FULL.name())) {
            classDescriptor.getCachePolicy().useFullIdentityMap();
        } else if (m_type.equals(CacheType.WEAK.name())) {
            classDescriptor.getCachePolicy().useWeakIdentityMap();
        }  else if (m_type.equals(CacheType.SOFT.name())) {
            classDescriptor.getCachePolicy().useSoftIdentityMap();
        } else if (m_type.equals(CacheType.HARD_WEAK.name())) {
            classDescriptor.getCachePolicy().useHardCacheWeakIdentityMap();
        } else if (m_type.equals(CacheType.CACHE.name())) {
            classDescriptor.useCacheIdentityMap();
        } else if (m_type.equals(CacheType.NONE.name())) {
            classDescriptor.getCachePolicy().useNoIdentityMap();
        }

        // Process size.
        if (m_size != null) {
            classDescriptor.getCachePolicy().setIdentityMapSize(m_size);
        }

        // Process isolation.
        if (m_isolation != null){
            classDescriptor.getCachePolicy().setCacheIsolation(CacheIsolationType.valueOf(m_isolation));
        }

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
        if (m_alwaysRefresh != null) {
            classDescriptor.getCachePolicy().setShouldAlwaysRefreshCache(m_alwaysRefresh);
        }

        // Process refresh only if newer.
        if (m_refreshOnlyIfNewer != null) {
            classDescriptor.getCachePolicy().setShouldOnlyRefreshCacheIfNewerVersion(m_refreshOnlyIfNewer);
        }

        // Process disable hits.
        if (m_disableHits != null) {
            classDescriptor.getCachePolicy().setShouldDisableCacheHits(m_disableHits);
        }

        // Process coordination type.
        if (m_coordinationType == null) {
            // Leave as default.
        } else if (m_coordinationType.equals(CacheCoordinationType.SEND_OBJECT_CHANGES.name())) {
            classDescriptor.getCachePolicy().setCacheSynchronizationType(ClassDescriptor.SEND_OBJECT_CHANGES);
        } else if (m_coordinationType.equals(CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS.name())) {
            classDescriptor.getCachePolicy().setCacheSynchronizationType(ClassDescriptor.INVALIDATE_CHANGED_OBJECTS);
        } else if (m_coordinationType.equals(CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES.name())) {
            classDescriptor.getCachePolicy().setCacheSynchronizationType(ClassDescriptor.SEND_NEW_OBJECTS_WITH_CHANGES);
        } else if (m_coordinationType.equals(CacheCoordinationType.NONE.name())) {
            classDescriptor.getCachePolicy().setCacheSynchronizationType(ClassDescriptor.DO_NOT_SEND_CHANGES);
        }

        // Process database change notification type.
        if (m_databaseChangeNotificationType == null) {
            // Leave as default.
        } else if (m_databaseChangeNotificationType.equals(DatabaseChangeNotificationType.NONE.name())) {
            classDescriptor.getCachePolicy().setDatabaseChangeNotificationType(DatabaseChangeNotificationType.NONE);
        } else if (m_databaseChangeNotificationType.equals(DatabaseChangeNotificationType.INVALIDATE.name())) {
            classDescriptor.getCachePolicy().setDatabaseChangeNotificationType(DatabaseChangeNotificationType.INVALIDATE);
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
    public void setCoordinationType(String coordinationType) {
        m_coordinationType = coordinationType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDatabaseChangeNotificationType() {
        return m_databaseChangeNotificationType;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDatabaseChangeNotificationType(String databaseChangeNotificationType) {
        m_databaseChangeNotificationType = databaseChangeNotificationType;
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

    public void setIsolation(String isolation){
        m_isolation = isolation;
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
    public void setType(String type) {
       m_type = type;
    }
}
