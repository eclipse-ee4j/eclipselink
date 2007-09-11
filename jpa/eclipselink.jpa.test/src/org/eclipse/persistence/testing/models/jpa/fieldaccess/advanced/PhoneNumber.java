/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.io.*;
import java.util.Collection;
import java.util.Vector;

import javax.persistence.*;
import static javax.persistence.FetchType.*;
import static javax.persistence.EnumType.STRING;

import org.eclipse.persistence.annotations.BasicCollection;
import org.eclipse.persistence.annotations.CollectionTable;

/**
 * <p><b>Purpose</b>: Describes an Employee's phone number.
 *    <p><b>Description</b>: Used in a 1:M relationship from an employee.
 */
@IdClass(org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.PhoneNumberPK.class)
@Entity(name="PhoneNumber")
@Table(name="CMP3_FA_PHONENUMBER")
public class PhoneNumber implements Serializable {
    public enum PhoneStatus { ACTIVE, ASSIGNED, UNASSIGNED, DEAD }
    
	@Column(name="NUMB")
	private String number;
	@Id
    @Column(name="TYPE")
	private String type;
	@ManyToOne
	@JoinColumn(name="OWNER_ID", referencedColumnName="EMP_ID")
	private Employee owner;
	@Id
	@Column(name="OWNER_ID", insertable=false, updatable=false)
    private Integer id;
	@Column(name="AREA_CODE")
    private String areaCode;
    
	@BasicCollection
    @CollectionTable(name="CMP3_FA_PHONE_STATUS")
    @Enumerated(STRING)
    private Collection<PhoneStatus> status;
	
    public PhoneNumber() {
        this("", "###", "#######");
    }

    public PhoneNumber(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
        this.owner = null;
        this.status = new Vector<PhoneStatus>();
    }

    public void addStatus(PhoneStatus status) {
        getStatus().add(status);
    }
        
	public Integer getId() { 
        return id; 
    }
    
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getNumber() { 
        return number; 
    }
    
	public void setNumber(String number) { 
        this.number = number; 
    }

    // Basic collection on an entity that uses a composite primary key.
    // We don't specify any of the primary key join columns on the collection
    // table because they should all default accordingly.    
    public Collection<PhoneStatus> getStatus() {
        return status;
    }
    
    public void setStatus(Collection<PhoneStatus> status) {
        this.status = status;
    }
        
	public String getType() { 
        return type; 
    }
    
	public void setType(String type) {
		this.type = type;
	}
	
	public String getAreaCode() { 
        return areaCode; 
    }
    
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	public Employee getOwner() { 
        return owner; 
    }
    
	public void setOwner(Employee owner) {
		this.owner = owner;
	}

    public void removeStatus(PhoneStatus status) {
        getStatus().remove(status);
    }
    
    /**
     * Example: Phone[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("PhoneNumber[");
        writer.write(getType());
        writer.write("]: (");
        writer.write(getAreaCode());
        writer.write(") ");

        int numberLength = this.getNumber().length();
        writer.write(getNumber().substring(0, Math.min(3, numberLength)));
        if (numberLength > 3) {
            writer.write("-");
            writer.write(getNumber().substring(3, Math.min(7, numberLength)));
        }

        return writer.toString();
    }
    
    /**
     * Builds the PhoneNumberPK for this class
     */
    public PhoneNumberPK buildPK(){
        PhoneNumberPK pk = new PhoneNumberPK();
        pk.setId(this.getOwner().getId());
        pk.setType(this.getType());
        return pk;
    }
}
