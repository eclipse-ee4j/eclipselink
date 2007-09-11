/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.cache;

import org.eclipse.persistence.annotations.Cache;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * Object to hold onto cache metadata. This class should eventually be 
 * extended by an XMLCache.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class MetadataCache  {
    private Cache m_cache;
    
    /**
     * INTERNAL:
     */
    protected MetadataCache() {}
    
    /**
     * INTERNAL:
     */
    public MetadataCache(Cache cache) {
        m_cache = cache;
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public boolean alwaysRefresh() {
       return m_cache.alwaysRefresh(); 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public boolean disableHits() {
       return m_cache.disableHits(); 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public String getCoordinationType() {
       return m_cache.coordinationType().name(); 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public int getExpiry() {
       return m_cache.expiry(); 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public MetadataTimeOfDay getExpiryTimeOfDay() {
        if (m_cache.expiryTimeOfDay().specified()) {
            return new MetadataTimeOfDay(m_cache.expiryTimeOfDay()); 
        } 
        
        return null;
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public String getIgnoreInheritanceSubclassCacheContext() {
       return MetadataLogger.IGNORE_INHERITANCE_SUBCLASS_CACHE_ANNOTATION; 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public String getIgnoreMappedSuperclassCacheContext() {
       return MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CACHE_ANNOTATION; 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public int getSize() {
       return m_cache.size();
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public String getType() {
       return m_cache.type().name();
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public boolean isIsolated() {
       return m_cache.isolated(); 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLCache)
     */
    public boolean refreshOnlyIfNewer() {
       return m_cache.refreshOnlyIfNewer(); 
    }
}
