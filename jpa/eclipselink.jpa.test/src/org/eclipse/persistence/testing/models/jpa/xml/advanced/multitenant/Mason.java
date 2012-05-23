/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     14/05/2012-2.4 Guy Pelletier   
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Mason {
    public int id;
    public String name;
    public Trowel trowel;
    public Map<Date, String> awards;
    
    public Mason() {
        awards = new HashMap<Date, String>();
    }
    
    public void addAward(Date awardDate, String award) {
        awards.put(awardDate, award);
    }
    
    public Map<Date, String> getAwards() {
        return awards;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Trowel getTrowel() {
        return trowel;
    }
    
    public void setAwards(Map<Date, String> awards) {
        this.awards = awards;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setTrowel(Trowel trowel) {
        this.trowel = trowel;
    }
}
