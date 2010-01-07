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
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

public class Certification  {
    private Integer id;
    private String description;
    private BeerConsumer beerConsumer;
    
    public Certification() {}
    
    public BeerConsumer getBeerConsumer() {
        return beerConsumer;
    }
    
    public String getDescription() {
        return description;    
    }
    
    public Integer getId() {
        return id;
    }
    
    // Used as a map key (in xml) for certifications.
    public Integer getMapKey() {
        return getId();
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setBeerConsumer(BeerConsumer beerConsumer) {
        this.beerConsumer = beerConsumer;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setMapKey(Integer mapKey) {
        // just ignore it ...
    }
}
