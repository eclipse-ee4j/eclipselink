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

import javax.persistence.*;
import static javax.persistence.GenerationType.*;

/**
 * This class is mapped in:
 * resource/eclipselink-ddl-generation-model/merge-inherited-certification.xml
 */
public class Certification  {
    private Integer id;
    private String description;
    private BeerConsumer beerConsumer;
    
    public Certification() {}
    
    // Relationship incorrectly defined and overidden by XML
    @ManyToOne
	@JoinColumn(name="WRONG_CONSUMER_ID")  
    public BeerConsumer getBeerConsumer() {
        return beerConsumer;
    }
    
    public String getDescription() {
        return description;    
    }
    
    // This annotation is used for the Id
    @Id
    @GeneratedValue(strategy=TABLE, generator="MERGE_CERTIFICATION_TABLE_GENERATOR")
	@TableGenerator(
        name="MERGE_CERTIFICATION_TABLE_GENERATOR", 
        table="CMP3_MERGE_BEER_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="CERTIFICATION_SEQ")
    public Integer getId() {
        return id;
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
}
