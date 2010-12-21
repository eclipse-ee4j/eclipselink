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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.util.Date;
import java.util.Map;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Collection;
import java.util.Enumeration;

public class BeerConsumer extends Consumer {
    private Integer id;
    private String name;
    private Collection<Alpine> alpineBeersToConsume;
    private Map becksBeersToConsume;
    private Map<Integer, Canadian> canadianBeersToConsume;
    private Map<CoronaTag, Corona> coronaBeersToConsume;
    private Map<Date, Heineken> heinekenBeersToConsume;
    private Map<Integer, Certification> certifications;
    private Map<TelephoneNumberPK, TelephoneNumber> telephoneNumbers;
    
    public BeerConsumer() {
        super();
        alpineBeersToConsume = new Vector<Alpine>();
        becksBeersToConsume = new Hashtable<BecksTag, Becks>();
        canadianBeersToConsume = new Hashtable<Integer, Canadian>();
        coronaBeersToConsume = new Hashtable<CoronaTag, Corona>();
        heinekenBeersToConsume = new Hashtable<Date, Heineken>();
        certifications = new Hashtable<Integer, Certification>();
        telephoneNumbers = new Hashtable<TelephoneNumberPK, TelephoneNumber>();
    }
    
    public void addAlpineBeerToConsume(Alpine alpine, int index) {
        alpine.setBeerConsumer(this);
        ((Vector) alpineBeersToConsume).insertElementAt(alpine, index);
    }
    
    public void addBecksBeerToConsume(Becks becks, BecksTag becksTag) {
        becks.setBeerConsumer(this);
        becksBeersToConsume.put(becksTag, becks);
    }
    
    public void addCoronaBeerToConsume(Corona corona, CoronaTag coronaTag) {
        corona.setBeerConsumer(this);
        coronaBeersToConsume.put(coronaTag, corona);
    }
    
    public void addHeinekenBeerToConsume(Heineken heineken, Date date) {
        heineken.setBeerConsumer(this);
        heinekenBeersToConsume.put(date, heineken);
    }
    
    /**
     * This model requires that a BeerConsumer be persisted prior to assigning
     * him/her a telephone number. This is because the BeerConsumer id is part
     * of the composite primary key, and that key is needed for the map that
     * holds the telephone numbers.
     */
    public void addTelephoneNumber(TelephoneNumber telephoneNumber) {
        telephoneNumber.setBeerConsumer(this);
        telephoneNumbers.put(telephoneNumber.buildPK(), telephoneNumber);
    }
    
    public Collection<Alpine> getAlpineBeersToConsume() {
        return alpineBeersToConsume;
    }
    
    public Alpine getAlpineBeerToConsume(int index) {
        return (Alpine) ((Vector) alpineBeersToConsume).elementAt(index);
    }

    public Map getBecksBeersToConsume() {
        return becksBeersToConsume;
    }
    
    public Map<Integer, Canadian> getCanadianBeersToConsume() {
        return canadianBeersToConsume;
    }
    
    public Map<Integer, Certification> getCertifications() {
        return certifications;
    }

    public Map<CoronaTag, Corona> getCoronaBeersToConsume() {
        return coronaBeersToConsume;
    }
    
    public Map<Date, Heineken> getHeinekenBeersToConsume() {
        return heinekenBeersToConsume;
    }
   
    // This method is here for testing purposes. Note: there is no
    // equivalent setMethod defined and the foo attribute has been
    // marked as a transient in inherited-entity-mappings.xml
    public int getFoo() {
        return 0;
    }
    
    public Integer getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
	public Map<TelephoneNumberPK, TelephoneNumber> getTelephoneNumbers() { 
        return telephoneNumbers; 
    }
    
    public boolean hasTelephoneNumber(TelephoneNumber telephoneNumber) {
        Enumeration keys = ((Hashtable) telephoneNumbers).keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            
            if (telephoneNumbers.get(key).equals(telephoneNumber)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void removeAlpineBeerToConsume(int index) {
        Alpine alpine = ((Vector<Alpine>) alpineBeersToConsume).elementAt(index);
        alpine.setBeerConsumer(null);
        ((Vector) alpineBeersToConsume).removeElementAt(index);
        
    }
    
    public void removePhoneNumber(TelephoneNumber telephoneNumber) {
        Enumeration keys = ((Hashtable) telephoneNumbers).keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            TelephoneNumber potentialTelephoneNumber = telephoneNumbers.get(key);
            
            if (potentialTelephoneNumber.equals(telephoneNumber)) {
                telephoneNumbers.remove(key);
                potentialTelephoneNumber.setBeerConsumer(null);
                return;
            }
        }
    }
    
    public void setAlpineBeersToConsume(Collection<Alpine> alpineBeersToConsume) {
        this.alpineBeersToConsume = alpineBeersToConsume;
    }
    
    public void setBecksBeersToConsume(Map becksBeersToConsume) {
        this.becksBeersToConsume = becksBeersToConsume;
    }
    public void setCanadianBeersToConsume(Map<Integer, Canadian> canadianBeersToConsume) {
        this.canadianBeersToConsume = canadianBeersToConsume;
    }
    
    public void setCertifications(Map<Integer, Certification> certifications) {
        this.certifications = certifications;
    }
    
    public void setCoronaBeersToConsume(Map<CoronaTag, Corona> coronaBeersToConsume) {
        this.coronaBeersToConsume = coronaBeersToConsume;
    }
    
    public void setHeinekenBeersToConsume(Map<Date, Heineken> heinekenBeersToConsume) {
        this.heinekenBeersToConsume = heinekenBeersToConsume;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setTelephoneNumbers(Map<TelephoneNumberPK, TelephoneNumber> telephoneNumbers) {
        this.telephoneNumbers = telephoneNumbers;
	}
}
