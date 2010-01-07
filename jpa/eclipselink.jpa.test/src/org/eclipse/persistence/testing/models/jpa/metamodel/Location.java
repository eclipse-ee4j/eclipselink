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
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="LocationMetamodel")
@Table(name="CMP3_MM_GALACTIC")//LOCATION")
public class Location extends CoordinateMS implements java.io.Serializable {
    /*@Id
    @GeneratedValue(strategy=TABLE, generator="LOCATION_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="LOCATION_MM_TABLE_GENERATOR", 
        table="CMP3_MM_LOCATION_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    @Column(name="LOCATION_ID")    
    private Integer id;*/
    
/*    // Any reference to this embedded key requires a bidirectional relationship (not unidirectional)
    @EmbeddedId
    @Column(name="LOCATION_ID")    
    protected EmbeddedPK primaryKey;*/
    
/*    @Version
    @Column(name="LOCATION_VERSION")
    private int version;*/
    
    public Location() {}

/*    public EmbeddedPK getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(EmbeddedPK primaryKey) {
        this.primaryKey = primaryKey;
    }*/
    
/*    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }*/

/*    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }*/
    
}
