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
 *     03/08/2010-2.1 Guy Pelletier 
 *       - 303632: Add attribute-type for mapping attributes to EclipseLink-ORM  
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

/**
 * This class is used to test the extended orm attribute-type settings.
 * 
 * It is mapped as an entity in the following resource file:
 * 
 *  resource/eclipselinkorm/eclipselink-xml-extended-model/eclipselink-orm.xml
 *  
 * @author gpelleti
 */
public class Confidant {
    // Id mapping
    private Object id;
    
    // One to one
    private Object loner;

    public Object getId() {
        return id;
    }
    
    public Object getLoner() {
        return loner;
    }
    
    public void setId(Object id) {
        this.id = id;
    }
    
    public void setLoner(Object loner) {
        this.loner = loner;
    }
}
