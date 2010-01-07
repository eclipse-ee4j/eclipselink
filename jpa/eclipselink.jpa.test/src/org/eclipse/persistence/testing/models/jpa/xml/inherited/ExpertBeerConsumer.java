/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes   
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExpertBeerConsumer extends RatedBeerConsumer<String, String, String> {
    private Map<Date, String> quotes;
    //private Collection<byte[]> audio;
    private Map<Birthday, String> celebrations;
    
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
    //@ElementCollection
    //@Lob
    //public Collection<byte[]> getAudio() {
      //  return audio;
    //}
    
    public Map<Birthday, String> getCelebrations() {
        return celebrations;
    }
    
    public Map<Date, String> getQuotes() {
        return quotes;
    }

    //public void setAudio(Collection<byte[]> audio) {
      //  this.audio = audio;
    //}
    
    public void setCelebrations(Map<Birthday, String> celebrations) {
        this.celebrations = celebrations;
    }
    
    public void setQuotes(Map<Date, String> quotes) {
        this.quotes = quotes;
    }
}
