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
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 *     02/18/2010-2.0.2 Guy Pelletier 
 *       - 294803: @Column(updatable=false) has no effect on @Basic mappings
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inherited;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SecondaryTable;
import javax.persistence.TableGenerator;

@Entity(name="JPA_OFFICIAL")
@NamedQuery(
  name="UpdateOfficalName",
  query="UPDATE JPA_OFFICIAL o set o.name = :name where o.id = :id"
)
@SecondaryTable(name="JPA_OFFICIAL_COMPENSATION")
public class Official {
    @Id
    @GeneratedValue(strategy=TABLE, generator="OFFICIAL_TABLE_GENERATOR")
    @TableGenerator(
        name="OFFICIAL_TABLE_GENERATOR", 
        table="CMP3_BEER_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="OFFICIAL_SEQ")
    private int id;
    
    @Column(name="NAME", updatable=false)
    private String name;
    
    @Column(insertable=false, updatable=true)
    private Integer age;
    
    @Embedded
    private ServiceTime serviceTime;
    
    @Column(table="JPA_OFFICIAL_COMPENSATION", insertable=true, updatable=false)
    private Integer salary;
    
    @Column(table="JPA_OFFICIAL_COMPENSATION", insertable=false, updatable=true)
    private Integer bonus;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "OFFICIAL_ENTRYID", insertable = false, updatable = false)
    private OfficialEntry officialEntry;

    @Basic
    @Column(name = "OFFICIAL_ENTRYID")
    private Integer officialEntryId;

    public Official() {}
    
    public Integer getAge() {
        return age;
    }
    
    public Integer getBonus() {
        return bonus;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public OfficialEntry getOfficialEntry() {
        return officialEntry;
    }
    
    public Integer getOfficialEntryId() {
        return officialEntryId;
    }

    public Integer getSalary() { 
        return salary; 
    }
    
    public ServiceTime getServiceTime() {
        return serviceTime;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
    
    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setOfficialEntry(OfficialEntry officialEntry) {
        this.officialEntry = officialEntry;
    }

    public void setOfficialEntryId(Integer officialEntryId) {
        this.officialEntryId = officialEntryId;
    }
    
    public void setSalary(Integer salary) { 
        this.salary = salary; 
    }
    
    public void setServiceTime(ServiceTime serviceTime) {
        this.serviceTime = serviceTime;
    }
    
    public String toString() {
        return this.name;
    }
}
