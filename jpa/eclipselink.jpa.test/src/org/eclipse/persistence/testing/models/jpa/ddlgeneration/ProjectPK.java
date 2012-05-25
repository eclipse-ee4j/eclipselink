package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;
import javax.persistence.*;
import java.util.*;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/24/2012 ailitchev 
 *       Bug 380580 - DatabaseField mapped with Temporal in EmbeddedId has no type
 *             caused an NPE in ddl generation code for this class
 ******************************************************************************/  
@Embeddable
public class ProjectPK  implements Serializable{
   
    String projname;
    java.util.Date projdate;
   
    public ProjectPK(String pname, Date pdate){
        this.projname = pname;
        this.projdate = pdate;
    } 
    
    public ProjectPK() {}

    @Column(name="PROJNAME", length=50)     
    public String getProjname() {
        return projname;
    }
    
    public void setProjname(String pname) {
        this.projname = pname;
    }

    @Column(name="PROJDATE", length=50) 
    @Temporal(TemporalType.DATE)
    public java.util.Date getProjdate() {
        return projdate;
    }
    
    public void setProjdate(java.util.Date pdate) {
        this.projdate = pdate;
    }
      
    public boolean equals(Object obj)  {
        if (obj instanceof ProjectPK) {
            return (projname.equals(((ProjectPK)obj).projname) &&
            projdate == (((ProjectPK)obj).projdate));
       }
        
        return false;
   }
      
    public int hashCode() 
    {
        return projname.concat(projdate.toString()).hashCode();
    }

}
