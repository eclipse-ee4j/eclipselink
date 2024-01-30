/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     05/30/2008-1.0M8 Guy Pelletier
//       - 230213: ValidationException when mapping to attribute in MappedSuperClass
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
package org.eclipse.persistence.testing.models.jpa.inherited;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKey;
import jakarta.persistence.MapKeyClass;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.MapKeyTemporal;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Version;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;

import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.TABLE;
import static jakarta.persistence.InheritanceType.JOINED;
import static jakarta.persistence.TemporalType.DATE;

@Entity
@Table(name="CMP3_CONSUMER")
@Inheritance(strategy=JOINED)
@DiscriminatorValue(value="BC")
public class BeerConsumer<T> implements ChangeTracker, Cloneable{
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
    private Map<SerialNumber, String> commentLookup;
    private Collection<BlueLight> blueLightBeersToConsume;

    private Map<BecksTag, Becks> becksBeersToConsume;
    private Map<BigInteger, Blue> blueBeersToConsume;
    private Map<Integer, Canadian> canadianBeersToConsume;
    private Map<CoronaTag, Corona> coronaBeersToConsume;
    private Map<Date, Heineken> heinekenBeersToConsume;
    private Map<T, RedStripe> redStripeBeersToConsume;
    private Map<Double, RedStripe> redStripesByAlcoholContent;
    private Map<Integer, Certification> certifications;
    private Map<TelephoneNumberPK, TelephoneNumber> telephoneNumbers;

    //Added for OrderListWithAttributeChangeTrackingTest though other tests using this class use deferred change tracking.
    public PropertyChangeListener listener;

    @Override
    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return listener;
    }

    @Override
    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    public BeerConsumer() {
        super();
        alpineBeersToConsume = new Vector<>();
        blueLightBeersToConsume = new Vector<>();
        becksBeersToConsume = new Hashtable<>();
        blueBeersToConsume = new Hashtable<>();
        canadianBeersToConsume = new Hashtable<>();
        coronaBeersToConsume = new Hashtable<>();
        heinekenBeersToConsume = new Hashtable<>();
        redStripeBeersToConsume = new Hashtable<>();
        redStripesByAlcoholContent = new HashMap<>();
        certifications = new Hashtable<>();
        telephoneNumbers = new Hashtable<>();
    }

    public void addAlpineBeerToConsume(Alpine alpine) {
        alpine.setBeerConsumer(this);
        alpineBeersToConsume.add(alpine);
    }

    public void addCommentLookup(SerialNumber number, String comment) {
        commentLookup.put(number, comment);
    }

    public void addAlpineBeerToConsume(Alpine alpine, int index) {
        alpine.setBeerConsumer(this);
        ((Vector<Alpine>) alpineBeersToConsume).insertElementAt(alpine, index);
    }

    public void addBecksBeerToConsume(Becks becks, BecksTag becksTag) {
        becks.setBeerConsumer(this);
        becksBeersToConsume.put(becksTag, becks);
    }

    public void addBlueBeerToConsume(Blue blue) {
        blue.setBeerConsumer(this);
        blueBeersToConsume.put(blue.getUniqueKey(), blue);
    }

    public void addBlueLightBeerToConsume(BlueLight blueLight) {
        blueLight.setBeerConsumer(this);
        blueLightBeersToConsume.add(blueLight);
    }

    public void addCoronaBeerToConsume(Corona corona, CoronaTag coronaTag) {
        corona.setBeerConsumer(this);
        coronaBeersToConsume.put(coronaTag, corona);
    }

    public void addHeinekenBeerToConsume(Heineken heineken, Date date) {
        heineken.setBeerConsumer(this);
        heinekenBeersToConsume.put(date, heineken);
    }

    public void addRedStripeBeersToConsume(RedStripe redStripe, T t) {
        redStripeBeersToConsume.put(t, redStripe);
    }

    public void addRedStripeByAlcoholContent(RedStripe redStripe) {
        redStripesByAlcoholContent.put(redStripe.getAlcoholContent(), redStripe);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        @SuppressWarnings({"unchecked"})
        BeerConsumer<T> consumer = (BeerConsumer<T>)super.clone();
        consumer.setAlpineBeersToConsume(new Vector<>());
        for (Alpine alpine : this.getAlpineBeersToConsume()) {
            consumer.addAlpineBeerToConsume(alpine.clone());
        }

        consumer.setBlueLightBeersToConsume(new Vector<>());
        for (Blue blue : this.getBlueLightBeersToConsume()) {
            consumer.addBlueLightBeerToConsume((BlueLight) blue.clone());
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

    // the target of this relationship should be serialized
    @ElementCollection
    @Column(name="DATA")
    @CollectionTable(name="CMP3_ALPINE_LOOKUP")
    @MapKeyJoinColumn(name="S_NUMBER")
    public Map<SerialNumber, String> getCommentLookup(){
        return commentLookup;
    }

    public Alpine getAlpineBeerToConsume(int index) {
        return ((Vector<Alpine>) alpineBeersToConsume).elementAt(index);
    }

    @OneToMany(targetEntity=Becks.class, mappedBy="beerConsumer", cascade=ALL, orphanRemoval=true)
    @MapKeyClass(BecksTag.class)
    @MapKeyJoinColumn(name="TAG_ID", referencedColumnName="ID")
    public Map<BecksTag, Becks> getBecksBeersToConsume() {
        return becksBeersToConsume;
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

    @OneToMany(mappedBy="beerConsumer", cascade=ALL)
    // MapKeyClass to be picked up
    @AttributeOverride(name="key.number", column=@Column(name="TAG_NUMBER"))
    public Map<CoronaTag, Corona> getCoronaBeersToConsume() {
        return coronaBeersToConsume;
    }

    @OneToMany(mappedBy="beerConsumer", cascade=ALL, fetch=EAGER)
    @MapKeyColumn(name="BOTTLED_DATE")
    @MapKeyTemporal(DATE)
    public Map<Date, Heineken> getHeinekenBeersToConsume() {
        return heinekenBeersToConsume;
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

    @ElementCollection
    // TODO: Correct resolving the T type without specifying the map key class
    // Map key class will get figured out through generic types.
    @MapKeyClass(String.class)
    @MapKeyColumn(name="RS_KEY")
    @CollectionTable(name="CONSUMER_REDSTRIPES", joinColumns=@JoinColumn(name="C_ID", referencedColumnName="ID"))
    public Map<T, RedStripe> getRedStripes() {
        return redStripeBeersToConsume;
    }

    @ElementCollection
    @CollectionTable(name="CONSUMER_REDSTRIPE_CONTENT",
            joinColumns={
                @JoinColumn(name="C_ID", referencedColumnName="ID")
            }
    )
    @MapKey(name="alcoholContent")
    public Map<Double, RedStripe> getRedStripesByAlcoholContent(){
        return redStripesByAlcoholContent;
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
        Iterator<TelephoneNumberPK> iterator = ((Hashtable<TelephoneNumberPK, TelephoneNumber>) telephoneNumbers).keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();

            if (telephoneNumbers.get(key).equals(telephoneNumber)) {
                return true;
            }
        }

        return false;
    }

    public Alpine moveAlpineBeerToConsume(int fromIndex, int toIndex) {
        Alpine alpine = ((Vector<Alpine>) alpineBeersToConsume).elementAt(fromIndex);
        ((Vector<?>) alpineBeersToConsume).removeElementAt(fromIndex);
        alpineBeersToConsume.add(toIndex, alpine);
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
        ((Vector<?>) alpineBeersToConsume).removeElementAt(index);
        return alpine;
    }

    public String removeCommentLookup(SerialNumber number){
        return commentLookup.remove(number);
    }

    public void removePhoneNumber(TelephoneNumber telephoneNumber) {
        Iterator<TelephoneNumberPK> iterator = ((Hashtable<TelephoneNumberPK, TelephoneNumber>) telephoneNumbers).keySet().iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
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

    public void setCommentLookup(Map<SerialNumber, String> commentLookUp){
        this.commentLookup = commentLookUp;
    }

    public void setBecksBeersToConsume(Map<BecksTag, Becks> becksBeersToConsume) {
        this.becksBeersToConsume = becksBeersToConsume;
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

    public void setRedStripes(Map<T, RedStripe> redStripeBeersToConsume) {
        this.redStripeBeersToConsume = redStripeBeersToConsume;
    }

    public void setRedStripesByAlcoholContent(Map<Double, RedStripe> redStripesByAlcoholContent) {
        this.redStripesByAlcoholContent = redStripesByAlcoholContent;
    }

    public void setTelephoneNumbers(Map<TelephoneNumberPK, TelephoneNumber> telephoneNumbers) {
        this.telephoneNumbers = telephoneNumbers;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
