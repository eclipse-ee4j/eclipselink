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
//     09/23/2008-1.1 Guy Pelletier
//       - 241651: JPA 2.0 Access Type support
//     03/27/2009-2.0 Guy Pelletier
//       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import jakarta.persistence.Basic;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;

/**
 * This class is mapped in:
 * resource/eclipselink-ddl-generation-model/merge-inherited-consumer.xml
 */
public class BeerConsumer {
    public int post_load_count = 0;
    public int post_persist_count = 0;
    public int post_remove_count = 0;
    public int post_update_count = 0;
    public int pre_persist_count = 0;
    public int pre_remove_count = 0;
    public int pre_update_count = 0;

    private Integer id;
    private String name;
    private Collection<Alpine> alpineBeersToConsume;
    private Map<Integer, Canadian> canadianBeersToConsume;
    private Map<Integer, Certification> certifications;
    private Map<TelephoneNumberPK, TelephoneNumber> telephoneNumbers;

    public BeerConsumer() {
        super();
        alpineBeersToConsume = new Vector<>();
        canadianBeersToConsume = new Hashtable<>();
        certifications = new Hashtable<>();
        telephoneNumbers = new Hashtable<>();
    }

    public void addAlpineBeerToConsume(Alpine alpine) {
        alpine.setBeerConsumer(this);
        alpineBeersToConsume.add(alpine);
    }

    public void addAlpineBeerToConsume(Alpine alpine, int index) {
        alpine.setBeerConsumer(this);
        ((Vector<Alpine>) alpineBeersToConsume).add(index, alpine);
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
        return ((Vector<Alpine>) alpineBeersToConsume).get(index);
    }

    public Map<Integer, Canadian> getCanadianBeersToConsume() {
        return canadianBeersToConsume;
    }

    public Map<Integer, Certification> getCertifications() {
        return certifications;
    }

    public Integer getId() {
        return id;
    }

    // This annotation is used
    @Basic
    public String getName() {
        return name;
    }

    // Define the relationship incorrectly, to ensure XML overrides annotation
    @OneToMany(mappedBy="thisIsVeryWrong", cascade=ALL, fetch=EAGER)
    @MapKey // key defaults to an instance of the composite pk class
    public Map<TelephoneNumberPK, TelephoneNumber> getTelephoneNumbers() {
        return telephoneNumbers;
    }

    public boolean hasTelephoneNumber(TelephoneNumber telephoneNumber) {
        Iterator<TelephoneNumberPK> iterator = telephoneNumbers.keySet().iterator();
        while (iterator.hasNext()) {
            TelephoneNumberPK key = iterator.next();

            if (telephoneNumbers.get(key).equals(telephoneNumber)) {
                return true;
            }
        }

        return false;
    }

    public Alpine moveAlpineBeerToConsume(int fromIndex, int toIndex) {
        Alpine alpine = ((Vector<Alpine>) alpineBeersToConsume).get(fromIndex);
        ((Vector<Alpine>) alpineBeersToConsume).remove(fromIndex);
        ((Vector<Alpine>) alpineBeersToConsume).add(toIndex, alpine);
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
        Alpine alpine = ((Vector<Alpine>) alpineBeersToConsume).get(index);
        alpine.setBeerConsumer(null);
        ((Vector<Alpine>) alpineBeersToConsume).remove(index);
        return alpine;
    }

    public void removePhoneNumber(TelephoneNumber telephoneNumber) {
        Iterator<TelephoneNumberPK> iterator = telephoneNumbers.keySet().iterator();
        while (iterator.hasNext()) {
            TelephoneNumberPK key = iterator.next();
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
}
