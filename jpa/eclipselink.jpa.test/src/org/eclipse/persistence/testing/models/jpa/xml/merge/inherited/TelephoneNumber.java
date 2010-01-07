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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import java.io.*;
import javax.persistence.*;

/**
 * This class is mapped in:
 * resource/eclipselink-ddl-generation-model/merge-inherited-consumer.xml
 * 
 * <p><b>Purpose</b>: Describes an BeerConsumers's telephone number.
 * <p><b>Description</b>: Used in a 1:M relationship from a BeerConsumer.
 */
public class TelephoneNumber extends ParentTelephoneNumber implements Serializable {
    private String type;
    private String number;
    private String areaCode;
    private BeerConsumer beerConsumer;
	
    public TelephoneNumber() {
        this.type = "Unknown";
        this.areaCode = "###";
        this.number = "#######";
        this.beerConsumer = null;
    }

    public TelephoneNumberPK buildPK(){
        TelephoneNumberPK pk = new TelephoneNumberPK();
        pk.setType(getType());
        pk.setNumber(getNumber());
        pk.setAreaCode(getAreaCode());
        return pk;
    }
    
    public boolean equals(Object telephoneNumber) {
        if (telephoneNumber.getClass() != TelephoneNumber.class) {
            return false;
        }
        
        return ((TelephoneNumber) telephoneNumber).buildPK().equals(buildPK());
    }
    
	public String getAreaCode() { 
        return areaCode; 
    }
    
	public BeerConsumer getBeerConsumer() { 
        return beerConsumer; 
    }
    
    // This component of the composite primary key is defined here, the remaining
    // elements of the composite PK are defined in the XML mapping file.
    @Id
    @Column(name="TNUMBER")
	public String getNumber() { 
        return number; 
    }
    
	public String getType() { 
        return type; 
    }
    
    public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
    
    public void setBeerConsumer(BeerConsumer beerConsumer) {
		this.beerConsumer = beerConsumer;
	}
    
	public void setNumber(String number) { 
        this.number = number; 
    }

	public void setType(String type) {
		this.type = type;
	}

    /**
     * Example: TelephoneNumber[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("TelephoneNumber[");
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
}
