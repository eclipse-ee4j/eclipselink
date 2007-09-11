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

import org.eclipse.persistence.annotations.TimeOfDay;

/**
 * Object to hold onto time of day metadata. This class should eventually be 
 * extended by an XMLTimeOfDay.
 * 
 * @author Guy Pelletier
 * @since TopLink 11g
 */
public class MetadataTimeOfDay  {
    private TimeOfDay m_timeOfDay;
    
    /**
     * INTERNAL:
     */
    protected MetadataTimeOfDay() {}
    
    /**
     * INTERNAL:
     */
    public MetadataTimeOfDay(TimeOfDay timeOfDay) {
        m_timeOfDay = timeOfDay;
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLTimeOfDay)
     */
    public int getHour() {
       return m_timeOfDay.hour(); 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLTimeOfDay)
     */
    public int getMillisecond() {
       return m_timeOfDay.millisecond(); 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLTimeOfDay)
     */
    public int getMinute() {
       return m_timeOfDay.minute(); 
    }
    
    /**
     * INTERNAL: (Future: Overridden in XMLTimeOfDay)
     */
    public int getSecond() {
       return m_timeOfDay.second(); 
    }
}
