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
 *     01/19/2010-2.1 Guy Pelletier 
 *       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.fetchgroup;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.InheritanceType.JOINED;

import org.eclipse.persistence.annotations.FetchAttribute;
import org.eclipse.persistence.annotations.FetchGroup;

@Entity
@Table(name="JPA_HOCKEY_GEAR")
@Inheritance(strategy=JOINED)
@DiscriminatorColumn(name="GEAR_TYPE")
@DiscriminatorValue("HOG")
@FetchGroup(name="MSRP", attributes={@FetchAttribute(name="msrp")})
public abstract class HockeyGear extends Gear {
    @Basic
    @Column(name="MSRP")
    public Double msrp;
    
    @Column(name="DESCRIP")
    public String description;
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="HOCKEY_GEAR_TABLE_GENERATOR")
    @TableGenerator(
        name="HOCKEY_GEAR_TABLE_GENERATOR", 
        table="JPA_HOCKEY_GEAR_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="HG_SEQ",
        initialValue=50
    )
    @Column(name="SERIAL_NUMBER")
    public Integer serialNumber;

    public HockeyGear() {}

    public String getDescription() {
        return description;
    }
    
    public Double getMsrp() {
        return msrp;
    }
        
    public Integer getSerialNumber() {
        return serialNumber;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setMsrp(Double msrp) {
        this.msrp = msrp;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
}
