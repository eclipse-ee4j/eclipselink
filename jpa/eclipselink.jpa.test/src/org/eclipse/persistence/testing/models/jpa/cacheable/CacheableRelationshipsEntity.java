/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Yiping Zhao - initial contribution
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.cacheable;

import static javax.persistence.GenerationType.TABLE;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity(name="JPA_CACHEABLE_RELATIONSHIPS")
@Table(name="JPA_CACHEREL")
@Cacheable // defaults to true
public class CacheableRelationshipsEntity {
    protected int id;

    protected String name;

    protected List<CacheableFalseEntity> cacheableFalses;

    protected List<CacheableProtectedEntity> cacheableProtecteds;

    protected CacheableForceProtectedEntity cacheableFPE;

    protected List<CacheableFalseDetail> cacheableFalseDetails;

    protected List<ProtectedEmbeddable> protectedEmbeddables;

    @Id
    @GeneratedValue(strategy=TABLE, generator="CACHEABLE_TABLE_GENERATOR")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setCacheableFalses(List<CacheableFalseEntity> cacheableFalses) {
        this.cacheableFalses = cacheableFalses;
    }

    /*cacheable true -> cachebale false(isolated)*/
    @OneToMany 
    @JoinColumn(name="SHARED_ISOLATED_REL_ID")
    public List<CacheableFalseEntity> getCacheableFalses() {
        return cacheableFalses;
    }

    public void addCacheableFalse(CacheableFalseEntity cacheableFalse) {
        cacheableFalses.add(cacheableFalse);
    }

    /*cacheable true -> cachebale false(protected)*/
    @OneToMany
    @JoinTable(
            name="SHARED_PROTECTED_REL",
            joinColumns=
            @JoinColumn(name="CACHBLE_REL_ID", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="CACHBLE_PRT_ID", referencedColumnName="ID")
    )
    public List<CacheableProtectedEntity> getCacheableProtecteds() {
        return cacheableProtecteds;
    }

    public void setCacheableProtecteds(List<CacheableProtectedEntity> cacheableProtecteds) {
        this.cacheableProtecteds = cacheableProtecteds;
    }

    public void addCacheableProtected(CacheableProtectedEntity cacheableProtected) {
        cacheableProtecteds.add(cacheableProtected);
    }
    /*cacheable true -> cacheable force protected (false, protected) */
    @ManyToOne 
    @JoinColumn(name="FORCE_PROTECTED_FK")
    public CacheableForceProtectedEntity getCacheableFPE() {
        return cacheableFPE;
    }
    
    public void setCacheableFPE(CacheableForceProtectedEntity cacheableFPE) {
        this.cacheableFPE = cacheableFPE;
    }

    public void setCacheableFalseDetails(List<CacheableFalseDetail> cacheableFalseDetails) {
        this.cacheableFalseDetails = cacheableFalseDetails;
    }
    /* cacheable true -> cache false */
    @ManyToMany
    @JoinTable(
            name="SHARED_FALSEDETAIL_REL",
            joinColumns=
            @JoinColumn(name="CACHBLE_REL_ID", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="CACHBLE_FLDETAIL_ID", referencedColumnName="ID")
    )
    public List<CacheableFalseDetail> getCacheableFalseDetails() {
        return cacheableFalseDetails;
    }

    public void addCacheableFalseDetail(CacheableFalseDetail cacheableFalseDetail) {
        cacheableFalseDetails.add(cacheableFalseDetail);
    }
    public void removeCacheableFalseDetail(CacheableFalseDetail cacheableFalseDetail) {
        getCacheableFalseDetails().remove(cacheableFalseDetail);
    }
    /* element collection with ebeddable (false protected)*/
    @ElementCollection
    @CollectionTable(
        name="CACHREL_PROTECTEMB",
        joinColumns=@JoinColumn(name="ID")
    )
    public List<ProtectedEmbeddable> getProtectedEmbeddables() {
        return protectedEmbeddables;
    }

    public void setProtectedEmbeddables(List<ProtectedEmbeddable> protectedEmbeddables) {
        this.protectedEmbeddables = protectedEmbeddables;
    }

    public void addProtectedEmbeddable(ProtectedEmbeddable protectedEmbeddable) {
        protectedEmbeddables.add(protectedEmbeddable);
    }

    public CacheableRelationshipsEntity() {
        cacheableFalses = new ArrayList<CacheableFalseEntity>();
        cacheableProtecteds = new ArrayList<CacheableProtectedEntity>();
        cacheableFalseDetails = new ArrayList<CacheableFalseDetail>();
        protectedEmbeddables = new ArrayList<ProtectedEmbeddable>();
    }
    
}
