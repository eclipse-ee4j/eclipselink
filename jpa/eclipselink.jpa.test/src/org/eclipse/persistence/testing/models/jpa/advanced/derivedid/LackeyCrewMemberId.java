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
 *     Jun 17, 2009-2.0 Chris Delahunt 
 *       - Bug#280350: NoSuchFieldException on deploy when using parent's compound PK class as derived ID
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.derivedid;


/**
 * @author Chris Delahunt
 *
 */
public class LackeyCrewMemberId {
    int rank;
    MajorId lackey;
    
    public int getRank() {
        return rank;
    }
    
    public MajorId getMajorPK() {
        return lackey;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    public void setMajorPK(MajorId majorPK) {
        this.lackey = majorPK;
    }

}
