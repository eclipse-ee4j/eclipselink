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
 *     06/02/2009-2.0 Guy Pelletier 
 *       - 278768: JPA 2.0 Association Override Join Table
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.inherited;

import java.util.ArrayList;
import java.util.List;

public class Accredidation {
    private String details;
    private List<Official> officials;
    private List<Witness> witnesses;
    
    public Accredidation() {
        officials = new ArrayList<Official>();
        witnesses = new ArrayList<Witness>();
    }
    
    public void addOfficial(Official official) {
        officials.add(official);
    }
    
    public void addWitness(Witness witness) {
        witnesses.add(witness);
    }
    
    public String getDetails() {
        return details;
    }

    public List<Official> getOfficials() {
        return officials;
    }
    
    public List<Witness> getWitnesses() {
        return witnesses;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public void setOfficials(List<Official> officials) {
        this.officials = officials;
    }
    
    public void setWitnesses(List<Witness> witnesses) {
        this.witnesses = witnesses;
    }
}
