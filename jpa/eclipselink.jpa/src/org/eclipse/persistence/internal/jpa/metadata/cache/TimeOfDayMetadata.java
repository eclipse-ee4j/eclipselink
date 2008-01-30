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
public class TimeOfDayMetadata  {
	protected int m_hour;
	protected int m_millisecond;
	protected int m_minute;
	protected int m_second;
    
    /**
     * INTERNAL:
     * Default constructor.
     */
    public TimeOfDayMetadata() {
    	setHour(0);
        setMillisecond(0);
        setMinute(0);
        setSecond(0);	
    }
    
    /**
     * INTERNAL:
     */
    public TimeOfDayMetadata(TimeOfDay timeOfDay) {
    	this();
    	
        setHour(timeOfDay.hour());
        setMillisecond(timeOfDay.millisecond());
        setMinute(timeOfDay.minute());
        setSecond(timeOfDay.second());
    }
    
    /**
     * INTERNAL:
     */
    public int getHour() {
       return m_hour; 
    }
    
    /**
     * INTERNAL:
     */
    public int getMillisecond() {
       return m_millisecond; 
    }
    
    /**
     * INTERNAL:
     */
    public int getMinute() {
       return m_minute; 
    }
    
    /**
     * INTERNAL:
     */
    public int getSecond() {
       return m_second; 
    }
    
    /**
     * INTERNAL:
     */
    public void setHour(int hour) {
    	m_hour = hour;
        
    }
    
    /**
     * INTERNAL:
     */
    public void setMillisecond(int millisecond) {
    	m_millisecond = millisecond;
    }
    
    /**
     * INTERNAL:
     */
    public void setMinute(int minute) {
    	m_minute = minute;
    }
    
    /**
     * INTERNAL:
     */
    public void setSecond(int second) {
    	m_second = second; 
    }
}
