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
 *     08/20/2009-2.0  mobrien - JPA 2.0 Metadata API test model
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;

@Embeddable
public class Observation {
    // This class is embedded inside a GalacticPosition
    
    // nested embeddable
    @Embedded
    private ObservationDetail detail;
    
/*    @ManyToMany
    @MapKey(name = "data")
    private Map<String, ObservationDetail> details;

    public Map<String, ObservationDetail> getDetails() {
        return details;
    }

    public void setDetails(Map<String, ObservationDetail> details) {
        this.details = details;
    }
*/

    public ObservationDetail getDetail() {
        return detail;
    }

    public void setDetail(ObservationDetail detail) {
        this.detail = detail;
    }


    private String date;
    
    private String text;
    
    public Observation() {        
    }
    
    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    
}

