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
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.CacheCoordinationType;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

/**
 * Object to hold onto cache metadata. This class should eventually be 
 * extended by an XMLCache.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class CacheMetadata  {
	protected boolean m_alwaysRefresh;
	protected boolean m_disableHits;
	protected boolean m_isIsolated;
	protected boolean m_refreshOnlyIfNewer;
	
	protected int m_expiry;
	protected int m_size;
	
	protected TimeOfDayMetadata m_expiryTimeOfDay;
	
	protected String m_coordinationType;
	protected String m_type;

    /**
     * INTERNAL:
     * Default constructor.
     */
    public CacheMetadata() {
    	setAlwaysRefresh(false);
    	setDisableHits(false);
    	setCoordinationType(CacheCoordinationType.SEND_OBJECT_CHANGES.name());
    	setExpiry(-1);
        setExpiryTimeOfDay(null);
        setIsIsolated(false);
        setSize(100);
    	setType(CacheType.SOFT_WEAK.name());
    	setRefreshOnlyIfNewer(false);
    }
    
    /**
     * INTERNAL:
     */
    public CacheMetadata(Cache cache) {
    	this();
    	
    	setAlwaysRefresh(cache.alwaysRefresh());
    	setDisableHits(cache.disableHits());
    	setCoordinationType(cache.coordinationType().name());
    	setExpiry(cache.expiry());
    	
        if (cache.expiryTimeOfDay().specified()) {
        	setExpiryTimeOfDay(new TimeOfDayMetadata(cache.expiryTimeOfDay()));
        }
        
        setIsIsolated(cache.isolated());
        setSize(cache.size());
    	setType(cache.type().name());
    	setRefreshOnlyIfNewer(cache.refreshOnlyIfNewer());
    }
    
    /**
     * INTERNAL:
     */
    public boolean alwaysRefresh() {
    	return m_alwaysRefresh; 
    }
    
    /**
     * INTERNAL:
     */
    public boolean disableHits() {
    	return m_disableHits; 
    }
    
    /**
     * INTERNAL:
     */
    public String getCoordinationType() {
    	return m_coordinationType; 
    }
    
    /**
     * INTERNAL:
     */
    public int getExpiry() {
    	return m_expiry; 
    }
    
    /**
     * INTERNAL:
     */
    public TimeOfDayMetadata getExpiryTimeOfDay() { 
        return m_expiryTimeOfDay;
    }
    
    /**
     * INTERNAL:
     */
    public String getIgnoreInheritanceSubclassCacheContext() {
    	return MetadataLogger.IGNORE_INHERITANCE_SUBCLASS_CACHE_ANNOTATION; 
    }
    
    /**
     * INTERNAL:
     */
    public String getIgnoreMappedSuperclassCacheContext() {
    	return MetadataLogger.IGNORE_MAPPED_SUPERCLASS_CACHE_ANNOTATION; 
    }
    
    /**
     * INTERNAL:
     */
    public int getSize() {
    	return m_size;
    }
    
    /**
     * INTERNAL:
     */
    public String getType() {
       return m_type;
    }
    
    /**
     * INTERNAL:
     */
    public boolean isIsolated() {
       return m_isIsolated; 
    }
    
    /**
     * INTERNAL: 
     */
    public void setAlwaysRefresh(boolean alwaysRefresh) {
    	m_alwaysRefresh = alwaysRefresh;
    }
    
    /**
     * INTERNAL:
     */
    public void setDisableHits(boolean disableHits) {
    	m_disableHits = disableHits; 
    }
    
    /**
     * INTERNAL:
     */
    public void setCoordinationType(String coordinationType) {
    	m_coordinationType = coordinationType; 
    }
    
    /**
     * INTERNAL:
     */
    public void setExpiry(int expiry) {
       m_expiry = expiry; 
    }
    
    /**
     * INTERNAL:
     */
    public void setExpiryTimeOfDay(TimeOfDayMetadata expiryTimeOfDay) { 
        m_expiryTimeOfDay = expiryTimeOfDay;
    }
    
    /**
     * INTERNAL:
     */
    public void setIsIsolated(boolean isIsolated) {
       m_isIsolated = isIsolated; 
    }
    
    /**
     * INTERNAL:
     */
    public void setSize(int size) {
    	m_size = size;
    }
    
    /**
     * INTERNAL:
     */
    public void setType(String type) {
       m_type = type;
    }
    
    /**
     * INTERNAL:
     */
    public boolean refreshOnlyIfNewer() {
       return m_refreshOnlyIfNewer; 
    }
    
    /**
     * INTERNAL:
     */
    public void setRefreshOnlyIfNewer(boolean refreshOnlyIfNewer) {
    	m_refreshOnlyIfNewer = refreshOnlyIfNewer;
    }
}
