/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     17/04/2011 - 2.3 Vikram Bhatia 
 *     342922: Unwanted insert statement generated when using ElementCollection 
 *     with lazy loading.
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table (name="ADV_SIMPLE_ENTITY")
public class SimpleEntity {

    @Id
    @Column(name = "SIMPLE_ID")
    private long id;

    private String description;

    @Version
    private long version;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "ADV_EC_SIMPLE", joinColumns = @JoinColumn(name = "SIMPLE_ID"))
    @Column(name = "SIMPLE_NATURE")
    private Collection<String> simpleNature = new ArrayList<String>();

    public SimpleEntity() {        
    }

    public long getId() {
        return id;
    }

    public void setId(long simpleId) {
        this.id = simpleId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String descrip) {
        this.description = descrip;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void setSimpleNature(Collection<String> simpleNature) {
        this.simpleNature = simpleNature;
    }

    public Collection<String> getSimpleNature() {
        return simpleNature;
    }

    public String addSimpleNature(int index) {
        this.simpleNature.add(SimpleNature.PERSONALITY[index]);
        return SimpleNature.PERSONALITY[index];
    }

    public String removeSimpleNature(int index) {
        this.simpleNature.remove(SimpleNature.PERSONALITY[index]);
        return SimpleNature.PERSONALITY[index];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SimpleEntity@");
        sb.append(Integer.toHexString(hashCode()));
        sb.append(" {id=");
        sb.append(this.getId());
        sb.append(", version=");
        sb.append(this.getVersion());
        sb.append(", description=");
        sb.append(this.getDescription());
        sb.append(", simpleNature");
        
        if (this.simpleNature != null) {
            sb.append("@");
            sb.append(Integer.toHexString(this.simpleNature.hashCode()));
            sb.append("=[");
            for (String sn : this.simpleNature) {
                sb.append(sn);
                sb.append(",");
            }
            sb.append("]");
        } else
            sb.append("=null");

        sb.append("}");
        return sb.toString();
    }
}
