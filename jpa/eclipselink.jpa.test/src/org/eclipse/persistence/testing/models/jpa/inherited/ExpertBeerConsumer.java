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
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)   
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     01/26/2010-2.0.1 Guy Pelletier 
 *       - 299893: @MapKeyClass does not work with ElementCollection
 *     07/15/2010-2.2 Guy Pelletier 
 *       -311395 : Multiple lifecycle callback methods for the same lifecycle event
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyTemporal;
import javax.persistence.PrePersist;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.DATE;

@Entity(name="EXPERT_CONSUMER")
@DiscriminatorValue(value="EBC")
@AttributeOverrides({
    @AttributeOverride(name="records.date", column=@Column(name="RECORD_DATE")),
    @AttributeOverride(name="records.description", column=@Column(name="DESCRIPTION")),
    @AttributeOverride(name="records.venue.attendance", column=@Column(name="WITNESSES"))
})
@AssociationOverride(name="records.location", joinColumns=@JoinColumn(name="LOCATION_ID", referencedColumnName="ID"))
public class ExpertBeerConsumer extends RatedBeerConsumer<String, String, String> {
    public int ebc_pre_persist_count = 0;
    
    private Map<Date, String> quotes;
    //private Collection<byte[]> audio;
    private Map<Birthday, String> celebrations;
    private Map courses;
    
    public ExpertBeerConsumer() {
        super();
        //audio = new ArrayList<byte[]>();
        quotes = new HashMap<Date, String>();
        celebrations = new HashMap<Birthday, String>();
    }   
    
    public void addCelebration(Birthday birthday, String details) {
        celebrations.put(birthday, details);
    }
    
    public void addCommittee(Committee committee) {
        getCommittees().add(committee);
        committee.addExpertBeerConsumer(this);
    }
    
    // Commenting out this mapping until bug 272298 is resolved.
    //@BasicCollection
    //@Lob
    //public Collection<byte[]> getAudio() {
        //return audio;
    //}
    
    @ElementCollection(fetch=EAGER)
    @MapKeyClass(Birthday.class)
    @AttributeOverrides({
      @AttributeOverride(name="key.day", column=@Column(name="BIRTH_DAY")),
      @AttributeOverride(name="key.month", column=@Column(name="BIRTH_MONTH")),
      @AttributeOverride(name="key.year", column=@Column(name="BIRTH_YEAR"))
    })
    @Column(name="DETAILS")
    @CollectionTable(
            name="EXPERT_CELEBRATIONS",
            joinColumns=@JoinColumn(name="EBC_ID"))
    public Map<Birthday, String> getCelebrations() {
        return celebrations;
    }
    
	@ElementCollection(targetClass=String.class)
    @CollectionTable(
    		name="EXPERT_COURSES",
    		joinColumns=@JoinColumn(name="EBC_ID"))
    @MapKeyColumn(name="COURSE")
    @MapKeyClass(String.class)
    @Column(name="COURSE_GRADE")
    public Map getCourses() {
		return courses;
	}
    
    @ElementCollection
    @MapKeyColumn(name="Q_DATE")
    @MapKeyTemporal(DATE)
    @Column(name="QUOTE")
    @CollectionTable(
            name="EXPERT_QUOTES",
            joinColumns=@JoinColumn(name="EBC_ID"))
    public Map<Date, String> getQuotes() {
        return quotes;
    }

    //public void setAudio(Collection<byte[]> audio) {
      //  this.audio = audio;
    //}
    
    @PrePersist
    public void prePersist() {
        ++ebc_pre_persist_count;
    }
    
    public void setCelebrations(Map<Birthday, String> celebrations) {
        this.celebrations = celebrations;
    }
    
    public void setCourses(Map courses) {
		this.courses = courses;
	}
    
    public void setQuotes(Map<Date, String> quotes) {
        this.quotes = quotes;
    }
}
