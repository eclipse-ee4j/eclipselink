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
package org.eclipse.persistence.testing.models.jpa.datetime;

import java.io.Serializable;

import java.util.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.persistence.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Table(name="CMP3_DATE_TIME")

public class DateTime implements Serializable {
    private Integer id;
    private java.sql.Date date;
    private Time time;
    private Timestamp timestamp;
    private Date utilDate;
    private Calendar calendar;

    public DateTime() {
    }

    public DateTime(java.sql.Date date, Time time, Timestamp timestamp, Date utilDate, Calendar calendar) {
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
        this.utilDate = utilDate;
        this.calendar = calendar;
    }

    @Id
    @GeneratedValue(strategy=TABLE, generator="DATETIME_TABLE_GENERATOR")
    @TableGenerator(
        name="DATETIME_TABLE_GENERATOR", 
        table="CMP3_DATETIME_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT"
    )
    @Column(name="DT_ID")
    public Integer getId() { 
        return id; 
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }

    @Column(name="SQL_DATE")
    public java.sql.Date getDate() { 
        return date; 
    }
    
    public void setDate(java.sql.Date date) { 
        this.date = date; 
    }

    @Column(name="SQL_TIME")
    public Time getTime() { 
        return time; 
    }
    
    public void setTime(Time date) { 
        this.time = date; 
    }

    @Column(name="SQL_TS")
    public Timestamp getTimestamp() { 
        return timestamp; 
    }
    
    public void setTimestamp(Timestamp date) { 
        this.timestamp = date; 
    }

    @Column(name="UTIL_DATE")
    @Temporal(TIMESTAMP)
    public Date getUtilDate() { 
        return utilDate; 
    }
    
    public void setUtilDate(Date date) { 
        this.utilDate = date; 
    }

    @Column(name="CAL")
    @Temporal(TIMESTAMP)
    public Calendar getCalendar() { 
        return calendar; 
    }
    
    public void setCalendar(Calendar date) { 
        this.calendar = date; 
    }
}
