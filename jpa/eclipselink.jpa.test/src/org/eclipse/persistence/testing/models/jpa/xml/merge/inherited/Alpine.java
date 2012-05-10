/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import static javax.persistence.TemporalType.DATE;

/**
 * This class is mapped in:
 * resource/eclipselink-ddl-generation-model/merge-inherited-beers.xml
 */
public class Alpine extends Beer  {
    public enum Classification { STRONG, BITTER, SWEET }
    
    // Overidden in XML
    @Column(name="OVERRIDDEN_IN_XML")
    @Temporal(DATE)
    private Date bestBeforeDate;
    
    private Classification classification;
    
    @Transient 
    private String localTransientString;
    
    public static int ALPINE_PRE_PERSIST_COUNT = 0;
    
    public Alpine() {}
    
    @PrePersist
    public void celebrate() {
        ALPINE_PRE_PERSIST_COUNT++;
    }
    
    public Date getBestBeforeDate() {
        return bestBeforeDate;
    }
    
    public Classification getClassification() {
        return classification;    
    }
    
    public String getLocalTransientString() {
        return localTransientString;
    }

    public void setBestBeforeDate(Date bestBeforeDate) {
        this.bestBeforeDate = bestBeforeDate;
    }
    
    public void setClassification(Classification classification) {
        this.classification = classification;
    }
    
    public void setLocalTransientString(String localTransientString) {
        this.localTransientString=localTransientString;
    }
    
    public boolean equals(Object anotherAlpine) {
        if (anotherAlpine.getClass() != Alpine.class) {
            return false;
        }
        
        return (getId().equals(((Alpine)anotherAlpine).getId()));
    }
}
