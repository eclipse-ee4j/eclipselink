/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     07/15/2010-2.2 Guy Pelletier 
 *       -311395 : Multiple lifecycle callback methods for the same lifecycle event
 *     08/11/2010-2.2 Guy Pelletier 
 *       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;

import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.TemporalType.DATE;

@Entity
@Table(name="CMP3_ALPINE")
@TableGenerator(
  name="USED_TO_TEST_A_LOG_MESSAGE", 
  //table="leave this commented out", 
  pkColumnName="SEQ_NAME", 
  valueColumnName="SEQ_COUNT",
  pkColumnValue="BEVERAGE_SEQ")
public class Alpine extends Beer<Integer, Double, Alpine> implements Cloneable, Serializable {
    public enum Classification { STRONG, BITTER, SWEET, NONE }
    
    private Date bestBeforeDate;
    private Classification classification;
    private SerialNumber serialNumber;
    private ArrayList inspectionDates;
    
    public static int ALPINE_PRE_PERSIST_COUNT = 0;
    public static int ALPINE_POST_PERSIST_COUNT = 0;

    protected Alpine(){};
    
    public Alpine(SerialNumber serialNumber) {
        inspectionDates = new ArrayList();
        setId(serialNumber.getNumber());
        serialNumber.setAlpine(this);
        setSerialNumber(serialNumber);
    }
    
    public Alpine clone() throws CloneNotSupportedException {
        return (Alpine)super.clone();
    }
    
    public void addInspectionDate(Date date) {
        getInspectionDates().add(date);
    }
    
    @PrePersist
    public void celebrate() {
        if (classification != null && classification == Classification.NONE) {
            classification = Classification.STRONG;
        }
        
        ALPINE_PRE_PERSIST_COUNT++;
    }
    
    @PostPersist
    public void celebrateAgain() {
        ALPINE_POST_PERSIST_COUNT++;
    }
    
    @Column(name="BB_DATE")
    @Temporal(DATE)
    public Date getBestBeforeDate() {
        return bestBeforeDate;
    }
    
    public Classification getClassification() {
        return classification;    
    }

    @Column(name="I_DATES")
    public ArrayList getInspectionDates() {
        return inspectionDates;    
    }
    
    @OneToOne(cascade=ALL)
    @PrimaryKeyJoinColumn
    public SerialNumber getSerialNumber() {
        return serialNumber;
    }
    
    public void setBestBeforeDate(Date bestBeforeDate) {
        this.bestBeforeDate = bestBeforeDate;
    }
    
    public void setClassification(Classification classification) {
        this.classification = classification;
    }
    
    public void setInspectionDates(ArrayList inspectionDates) {
        this.inspectionDates = inspectionDates;
    }
    
    protected void setSerialNumber(SerialNumber serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public boolean equals(Object anotherAlpine) {
        if (anotherAlpine.getClass() != Alpine.class) {
            return false;
        }
        
        return (getId().equals(((Alpine)anotherAlpine).getId()));
    }
}
