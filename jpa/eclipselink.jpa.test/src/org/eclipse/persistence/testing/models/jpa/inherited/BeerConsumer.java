/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.util.Map;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Collection;
import java.util.Enumeration;
import javax.persistence.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.*;

@Entity
@Table(name="CMP3_CONSUMER")
public class BeerConsumer {
    public int post_load_count = 0;
    public int post_persist_count = 0;
    public int post_remove_count = 0;
    public int post_update_count = 0;
    public int pre_persist_count = 0;
    public int pre_remove_count = 0;
    public int pre_update_count = 0;
    
    private Integer id;
    private Integer version;
    private String name;
    private Collection<Alpine> alpineBeersToConsume;
    private Map<Integer, Blue> blueBeersToConsume;
    private Map<Integer, Canadian> canadianBeersToConsume;
    private Map<Integer, Certification> certifications;
    private Map<TelephoneNumberPK, TelephoneNumber> telephoneNumbers;
    
    public BeerConsumer() {
        super();
        alpineBeersToConsume = new Vector<Alpine>();
        blueBeersToConsume = new Hashtable<Integer, Blue>();
        canadianBeersToConsume = new Hashtable<Integer, Canadian>();
        certifications = new Hashtable<Integer, Certification>();
        telephoneNumbers = new Hashtable<TelephoneNumberPK, TelephoneNumber>();
    }
    
    public void addAlpineBeerToConsume(Alpine alpine) {
        alpine.setBeerConsumer(this);
        ((Vector) alpineBeersToConsume).add(alpine);
    }
    
    public void addAlpineBeerToConsume(Alpine alpine, int index) {
        alpine.setBeerConsumer(this);
        ((Vector) alpineBeersToConsume).insertElementAt(alpine, index);
    }
    
    public void addBlueBeerToConsume(Blue blue) {
        blue.setBeerConsumer(this);
        blueBeersToConsume.put(blue.getUniqueKey(), blue);
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
    
    @OneToMany(mappedBy="beerConsumer", cascade=ALL, fetch=EAGER)
    @OrderBy("bestBeforeDate ASC")
    public Collection<Alpine> getAlpineBeersToConsume() {
        return alpineBeersToConsume;
    }
    
    public Alpine getAlpineBeerToConsume(int index) {
        return (Alpine) ((Vector) alpineBeersToConsume).elementAt(index);
    }
    
    @OneToMany(mappedBy="beerConsumer", cascade=ALL)
    @MapKey(name="uniqueKey")
    public Map<Integer, Blue> getBlueBeersToConsume() {
        return blueBeersToConsume;
    }
    
    @OneToMany(mappedBy="beerConsumer", cascade=ALL)
    @MapKey // name should default to "id"
    public Map<Integer, Canadian> getCanadianBeersToConsume() {
        return canadianBeersToConsume;
    }
    
    @OneToMany(mappedBy="beerConsumer", cascade=ALL)
    @MapKey(name="id")
    public Map<Integer, Certification> getCertifications() {
        return certifications;
    }
   
    @Id
    @GeneratedValue(strategy=TABLE, generator="BEER_CONSUMER_TABLE_GENERATOR")
	@TableGenerator(
        name="BEER_CONSUMER_TABLE_GENERATOR", 
        table="CMP3_BEER_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CONSUMER_SEQ")
    public Integer getId() {
        return id;
    }
    
    @Basic
    public String getName() {
        return name;
    }
    
    @OneToMany(mappedBy="beerConsumer", cascade=ALL, fetch=EAGER)
    @MapKey // key defaults to an instance of the composite pk class 
	public Map<TelephoneNumberPK, TelephoneNumber> getTelephoneNumbers() { 
        return telephoneNumbers; 
    }
    
    @Version
    @Column(name="VERSION")
    public Integer getVersion() {
        return version; 
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
    
    public Alpine moveAlpineBeerToConsume(int fromIndex, int toIndex) {
        Alpine alpine = ((Vector<Alpine>) alpineBeersToConsume).elementAt(fromIndex);
        ((Vector) alpineBeersToConsume).removeElementAt(fromIndex);
        ((Vector) alpineBeersToConsume).add(toIndex, alpine);
        return alpine;
    }
    
    @PostLoad
	public void postLoad() {
        ++post_load_count;
	}
    
    @PostPersist
	public void postPersist() {
        ++post_persist_count;
	}
   
   	@PostRemove
	public void postRemove() {
        ++post_remove_count;
	}
    
    @PostUpdate
	public void postUpdate() {
        ++post_update_count;
	}
    
    @PrePersist
	public void prePersist() {
        ++pre_persist_count;
	}
    
    @PreRemove
	public void preRemove() {
        ++pre_remove_count;
	}

	@PreUpdate
	public void preUpdate() {
        ++pre_update_count;
	}
    
    public Alpine removeAlpineBeerToConsume(int index) {
        Alpine alpine = ((Vector<Alpine>) alpineBeersToConsume).elementAt(index);
        alpine.setBeerConsumer(null);
        ((Vector) alpineBeersToConsume).removeElementAt(index);
        return alpine;
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
    
    public void setBlueBeersToConsume(Map<Integer, Blue> blueBeersToConsume) {
        this.blueBeersToConsume = blueBeersToConsume;
    }
    
    public void setCanadianBeersToConsume(Map<Integer, Canadian> canadianBeersToConsume) {
        this.canadianBeersToConsume = canadianBeersToConsume;
    }
    
    public void setCertifications(Map<Integer, Certification> certifications) {
        this.certifications = certifications;
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
    
    public void setVersion(Integer version) {
        this.version = version;
    }
}
