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
 *     03/29/2010-2.1 Guy Pelletier 
 *       - 267217: Add Named Access Type to EclipseLink-ORM
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

import java.util.List;

/**
 * This class is used to test the extended orm named-access setting.
 * 
 * It is mapped as an entity in the following resource file:
 * 
 *  resource/eclipselinkorm/eclipselink-xml-extended-model/eclipselink-orm.xml
 *  
 * @author gpelleti
 */
public class Shovel {
    // id
    private Integer id;
    
    // embedded 
    private ShovelSections sections;
    
    // basic
    private Double cost;
    
    // version
    private Integer version;
    
    // Many to one
    private ShovelOwner owner;
    
    // one to many
    private List<ShovelDigger> operators;
    
    // many to many
    private List<ShovelProject> projects;
    
    public Shovel() {}
    
    public Object getMy(String attribute) {
        try {
            return getClass().getDeclaredField(attribute).get(this);
        } catch (Exception e) {
            throw new RuntimeException("Error occured getting the value of attributee: " + attribute);
        }
    }
    
    public void setMy(String attribute, Object value) {
        try {
            getClass().getDeclaredField(attribute).set(this, value);
        } catch (Exception e) {
            throw new RuntimeException("Error occured set the attribute: " + attribute + ", with value: " + value);
        }
    }
}
