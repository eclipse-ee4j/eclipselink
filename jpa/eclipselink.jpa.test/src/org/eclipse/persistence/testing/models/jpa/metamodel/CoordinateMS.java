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
 *     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
 *       of the Metamodel implementation for EclipseLink 2.0 release involving
 *       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.MappedSuperclass;

/**
 * A MappedSuperclass can extend an Entity in an inheritance tree such as
 * Entity -- MappedSuperclass -- Entity
 * See the following references
 * http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/cache_api#Test_Model_1
 */
@MappedSuperclass
public class CoordinateMS extends GalacticPosition {

    // Any reference to this embedded key requires a bidirectional relationship (not unidirectional)
/*    @EmbeddedId
    @Column(name="GALACTIC_ID")    
    protected EmbeddedPK primaryKey;
*/
    
    private Integer longitude;
    private Integer Lattitude;
    private Integer elevation;
    
    
    
    public CoordinateMS() {}

    public Integer getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    public Integer getLattitude() {
        return Lattitude;
    }

    public void setLattitude(Integer lattitude) {
        Lattitude = lattitude;
    }

    public Integer getElevation() {
        return elevation;
    }

    public void setElevation(Integer elevation) {
        this.elevation = elevation;
    }
    
/*    public EmbeddedPK getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(EmbeddedPK primaryKey) {
        this.primaryKey = primaryKey;
    }
*/
    
}
