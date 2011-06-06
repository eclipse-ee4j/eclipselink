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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.JoinColumn;
import static javax.persistence.FetchType.LAZY;

import java.sql.Timestamp;

/**
 * This class is mapped in:
 * resource/eclipselink-ddl-generation-model/merge-inherited-superclasses.xml
 * 
 * All annotations should be ignored in this class as the XML definition is
 * declared as metadata-complete=true. Not to mention the access is field
 * therefore any annotations on methods are not processed (where they were
 * before I moved them to the fields).
 */
public class Beer extends Beverage {
    // The version is defined in XML
    private Timestamp version;
    
    @Basic
    @Column(name="ALCOHOL_CONTENT")
    private double alcoholContent;
    
    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="TOTALLY_WRONG_ID")
    private BeerConsumer beerConsumer;
    
    private EmbeddedSerialNumber embeddedSerialNumber;
    
    public static int BEER_PRE_PERSIST_COUNT = 0;
    
    public Beer() {}
    
    @PrePersist
    public void celebrate() {
        BEER_PRE_PERSIST_COUNT++;
    }
    
    public double getAlcoholContent() {
        return alcoholContent;
    }
    
    public BeerConsumer getBeerConsumer() {
        return beerConsumer;
    }
    
    public Timestamp getVersion() {
        return version;
    }
    
    public void setAlcoholContent(double alcoholContent) {
        this.alcoholContent = alcoholContent;
    }
    
    public void setBeerConsumer(BeerConsumer beerConsumer) {
        this.beerConsumer = beerConsumer;
    }
    
    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public EmbeddedSerialNumber getEmbeddedSerialNumber() {
        return this.embeddedSerialNumber;
    }
    
    public void setEmbeddedSerialNumber(EmbeddedSerialNumber embeddedSerialNumber) {
        this.embeddedSerialNumber = embeddedSerialNumber;
    }
}
