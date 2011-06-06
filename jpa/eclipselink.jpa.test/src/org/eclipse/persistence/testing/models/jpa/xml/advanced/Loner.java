/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM  
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to test the extended orm attribute-type settings.
 * 
 * It is mapped as an entity in the following resource file:
 * 
 *  resource/eclipselinkorm/eclipselink-xml-extended-model/eclipselink-orm.xml
 *  
 * @author gpelleti
 */
public class Loner {
    // Basic mapping
    private Object id;
    
    // Version mapping
    private Object version;
    
    // One to many mapping
    private Object confidants;
    
    // Element collection mapping
    private Object characteristics;
    
    // Embedded
    private Object name;

    public Loner() {
        confidants = new ArrayList<Confidant>();
        characteristics  = new ArrayList<String>();
    }
    
    public void addCharacteristic(Object characteristic) {
        ((List) characteristics).add(characteristic);
    }
    
    public void addConfidant(Confidant confidant) {
        confidant.setLoner(this);
        ((List) confidants).add(confidant);
    }
    
    public Object getCharacteristics() {
        return characteristics;
    }

    public Object getConfidants() {
        return confidants;
    }
    
    public Object getId() {
        return id;
    }

    public Object getName() {
        return name;
    }
    
    public Object getVersion() {
        return version;
    }

    public void setCharacteristics(Object characteristics) {
        this.characteristics = characteristics;
    }
    
    public void setConfidants(Object confidants) {
        this.confidants = confidants;
    }
    
    public void setId(Object id) {
        this.id = id;
    }

    public void setName(Object name) {
        this.name = name;
    }
    
    public void setVersion(Object version) {
        this.version = version;
    }
}
