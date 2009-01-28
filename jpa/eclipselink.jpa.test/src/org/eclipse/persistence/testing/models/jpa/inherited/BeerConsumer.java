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
 *     05/30/2008-1.0M8 Guy Pelletier 
 *       - 230213: ValidationException when mapping to attribute in MappedSuperClass
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Collection;
import java.util.List;
import java.util.Enumeration;
import javax.persistence.*;

import static javax.persistence.FetchType.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.*;
import static javax.persistence.InheritanceType.*;
import org.eclipse.persistence.descriptors.changetracking.*;

@Entity
@Table(name="CMP3_CONSUMER")
@Inheritance(strategy=JOINED)
@DiscriminatorValue(value="BC")
public class BeerConsumer implements ChangeTracker, Cloneable{
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
    
    private List<Alpine> alpineBeersToConsume;
    private Collection<BlueLight> blueLightBeersToConsume;
    
    private Map<BigInteger, Blue> blueBeersToConsume;
    private Map<Integer, Canadian> canadianBeersToConsume;
    private Map<Integer, Certification> certifications;
    private Map<TelephoneNumberPK, TelephoneNumber> telephoneNumbers;
    
    //Added for OrderListWithAttributeChangeTrackingTest though other tests using this class use deferred change tracking.  
    public PropertyChangeListener listener;

    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return listener;
    }

    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }
    
    public BeerConsumer() {
        super();
        alpineBeersToConsume = new Vector<Alpine>();
        blueLightBeersToConsume = new Vector<BlueLight>();
        
        blueBeersToConsume = new Hashtable<BigInteger, Blue>();
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
    
    public void addBlueLightBeerToConsume(BlueLight blueLight) {
        blueLight.setBeerConsumer(this);
        ((Vector) blueLightBeersToConsume).add(blueLight);
    }
    
    public Object clone() throws CloneNotSupportedException {
        BeerConsumer consumer = (BeerConsumer)super.clone();
        consumer.setAlpineBeersToConsume(new Vector());
        Iterator<Alpine> alpineIterator = this.getAlpineBeersToConsume().iterator();
        while (alpineIterator.hasNext()) {
            Alpine alpine = alpineIterator.next();
            consumer.addAlpineBeerToConsume(alpine.clone());
        }
        
        consumer.setBlueLightBeersToConsume(new Vector());
        Iterator<BlueLight> blueLightIterator = this.getBlueLightBeersToConsume().iterator();
        while (blueLightIterator.hasNext()) {
            Blue blue = blueLightIterator.next();
            consumer.addBlueLightBeerToConsume((BlueLight)blue.clone());
        }
        return consumer;
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
    
    @OneToMany(mappedBy="beerConsumer", cascade=ALL, fetch=LAZY)
    @OrderBy("bestBeforeDate ASC")
    public List<Alpine> getAlpineBeersToConsume() {
        return alpineBeersToConsume;
    }
    
    public Alpine getAlpineBeerToConsume(int index) {
        return (Alpine) ((Vector) alpineBeersToConsume).elementAt(index);
    }
    
    @OneToMany(mappedBy="beerConsumer", cascade=ALL)
    @MapKey(name="uniqueKey")
    public Map<BigInteger, Blue> getBlueBeersToConsume() {
        return blueBeersToConsume;
    }
    
    @OneToMany(mappedBy="beerConsumer", cascade=ALL)
    public Collection<BlueLight> getBlueLightBeersToConsume() {
        return blueLightBeersToConsume;
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

    public void setAlpineBeersToConsume(List<Alpine> alpineBeersToConsume) {
        this.alpineBeersToConsume = alpineBeersToConsume;
    }
    
    public void setBlueBeersToConsume(Map<BigInteger, Blue> blueBeersToConsume) {
        this.blueBeersToConsume = blueBeersToConsume;
    }
    
    public void setBlueLightBeersToConsume(Collection<BlueLight> blueLightBeersToConsume) {
        this.blueLightBeersToConsume = blueLightBeersToConsume;
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
