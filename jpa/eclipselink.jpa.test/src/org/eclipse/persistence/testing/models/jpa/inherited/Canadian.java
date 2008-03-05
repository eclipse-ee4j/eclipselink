/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.*;

import org.eclipse.persistence.annotations.Mutable;

import static javax.persistence.TemporalType.DATE;

@Entity
@Table(name="CMP3_CANADIAN")
@AssociationOverride(name="beerConsumer", joinColumns=@JoinColumn(name="CONSUMER_ID"))
public class Canadian extends Beer {
    public enum Flavor { LAGER, LIGHT, ICE, DRY }

    private Flavor flavor;
    private Date bornOnDate;
    private HashMap<String, Serializable> properties;
    
    public Canadian() {
        properties = new HashMap<String, Serializable>();
    }
    
    @Basic
    @Column(name="BORN")
    @Temporal(DATE)
    @Mutable
    public Date getBornOnDate() {
        return bornOnDate;
    }
    
    @Basic
    public Flavor getFlavor() {
        return flavor;
    }
    
    @Basic
    public HashMap<String, Serializable> getProperties() {
        return properties;    
    }
    
    public void setBornOnDate(Date bornOnDate) {
        this.bornOnDate = bornOnDate;
    }
    
    public void setFlavor(Flavor flavor) {
        this.flavor = flavor;
    }
    
    public void setProperties(HashMap<String, Serializable> properties) {
        this.properties = properties;
    }
}
