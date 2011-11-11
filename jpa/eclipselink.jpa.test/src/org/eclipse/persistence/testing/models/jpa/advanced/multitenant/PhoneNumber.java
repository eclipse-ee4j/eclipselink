/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import java.io.*;
import java.util.Collection;
import java.util.Vector;

import javax.persistence.*;
import static javax.persistence.EnumType.STRING;

import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.TenantDiscriminatorColumn;
import org.eclipse.persistence.queries.FetchGroupTracker;

@Entity(name="MT_PHONENUMBER")
@IdClass(PhoneNumberPK.class)
@Multitenant
@TenantDiscriminatorColumn(name="TENANT_ID", contextProperty="tenant.id", primaryKey=true)
@Table(name="JPA_MT_PHONE_NUMBER")
public class PhoneNumber implements Serializable {
    private String areaCode;
    private String number;
    private String type;
    private Integer id;
    
    public PhoneNumber() {
        this("", "###", "#######");
    }

    public PhoneNumber(String type, String areaCode, String number) {
        this.type = type;
        this.areaCode = areaCode;
        this.number = number;
    }
    
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    
    @Column(name="AREA_CODE")
    public String getAreaCode() { 
        return areaCode; 
    }
    
    @Id
    @GeneratedValue
    public Integer getId() { 
        return id; 
    }
    
    @Column(name="NUMB")
    public String getNumber() { 
        return number; 
    }
    
    @Id
    @Column(name="TYPE")
    public String getType() { 
        return type; 
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumber(String number) { 
        this.number = number; 
    }
    
    public void setType(String type) {
        this.type = type;
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
    
        int numberLength = getNumber().length();
        writer.write(getNumber().substring(0, Math.min(3, numberLength)));
        if (numberLength > 3) {
            writer.write("-");
            writer.write(getNumber().substring(4, Math.min(7, numberLength)));
        }

        return writer.toString();
    }
    
    /**
     * Builds the PhoneNumberPK for this class
     */
    public PhoneNumberPK buildPK(){
        PhoneNumberPK pk = new PhoneNumberPK();
        pk.setId(getId());
        pk.setType(getType());
        return pk;
    }
}

