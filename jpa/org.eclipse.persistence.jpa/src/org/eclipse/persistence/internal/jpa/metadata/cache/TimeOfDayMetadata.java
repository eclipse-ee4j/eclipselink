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

import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

/**
 * Object to hold onto time of day metadata.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class TimeOfDayMetadata extends ORMetadata {
    private Integer m_hour;
    private Integer m_millisecond;
    private Integer m_minute;
    private Integer m_second;
    
    /**
     * INTERNAL:
     */
    public TimeOfDayMetadata() {
        super("<time-of-day>");
    }
    
    /**
     * INTERNAL:
     */
    public TimeOfDayMetadata(Annotation timeOfDay, MetadataAccessibleObject accessibleObject) {
        super(timeOfDay, accessibleObject);
        
        m_hour = (Integer) MetadataHelper.invokeMethod("hour", timeOfDay);
        m_millisecond = (Integer) MetadataHelper.invokeMethod("millisecond", timeOfDay);
        m_minute = (Integer) MetadataHelper.invokeMethod("minute", timeOfDay);
        m_second = (Integer) MetadataHelper.invokeMethod("second", timeOfDay);
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getHour() {
       return m_hour; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getMillisecond() {
       return m_millisecond; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getMinute() {
       return m_minute; 
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public Integer getSecond() {
       return m_second; 
    }
    
    /**
     * INTERNAL:
     */
    public Integer processHour() {
        return (m_hour == null) ? 0 : m_hour;
    }
    
    /**
     * INTERNAL:
     */
    public Integer processMillisecond() {
        return (m_millisecond == null) ? 0 : m_millisecond;
    }
    
    /**
     * INTERNAL:
     */
    public Integer processMinute() {
        return (m_minute == null) ? 0 : m_minute;
    }
    
    /**
     * INTERNAL:
     */
    public Integer processSecond() {
        return (m_second == null) ? 0 : m_second;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setHour(Integer hour) {
        m_hour = hour;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMillisecond(Integer millisecond) {
        m_millisecond = millisecond;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMinute(Integer minute) {
        m_minute = minute;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSecond(Integer second) {
        m_second = second; 
    }
}
