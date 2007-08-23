/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.util.Date;
import java.util.ArrayList;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.TemporalType.DATE;

@Entity
@Table(name="CMP3_ALPINE")
public class Alpine extends Beer  {
    public enum Classification { STRONG, BITTER, SWEET }
    
    private Date bestBeforeDate;
    private Classification classification;
    private SerialNumber serialNumber;
    private ArrayList inspectionDates;
    
    public static int ALPINE_PRE_PERSIST_COUNT = 0;

    protected Alpine(){};
    
    public Alpine(SerialNumber serialNumber) {
        inspectionDates = new ArrayList();
        setId(serialNumber.getNumber());
        serialNumber.setAlpine(this);
        setSerialNumber(serialNumber);
    }
    
    public void addInspectionDate(Date date) {
        getInspectionDates().add(date);
    }
    
    @PrePersist
    public void celebrate() {
        ALPINE_PRE_PERSIST_COUNT++;
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
