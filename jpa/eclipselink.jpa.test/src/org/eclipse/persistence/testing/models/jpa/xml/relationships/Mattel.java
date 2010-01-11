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
 *     03/26/2008-1.0M6 Guy Pelletier 
 *       - 211302: Add variable 1-1 mapping support to the EclipseLink-ORM.XML Schema 
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.relationships;

import java.util.List;

public class Mattel implements Manufacturer {
    private Integer id;
    private String name;
    
    // These are bogus attributes that normally would cause default mapping 
    // generation. However the exclude-default-mappings is set to true for this 
    // class (persistence-unit-metadata setting) in the
    // relationships-extended-entity-mappings file.
    private String ignoredBasic;
    private Item ignoredOneToOne;
    private Mattel ignoredVariableOneToOne;
    private List<Customer> ignoredOneToMany;
    
    public Mattel() {}
    
    public Integer getId() { 
        return id; 
    }
    
    public String getIgnoredBasic() {
        return ignoredBasic;
    }

    public List<Customer> getIgnoredOneToMany() {
        return ignoredOneToMany;
    }
    
    public Item getIgnoredOneToOne() {
        return ignoredOneToOne;
    }
    
    public Mattel getIgnoredVariableOneToOne() {
        return ignoredVariableOneToOne;
    }
    
    public String getName() {
        return name;
    }
    
    public void setId(Integer id) { 
        this.id = id;
    }
    
    public void setIgnoredBasic(String ignoredBasic) {
        this.ignoredBasic = ignoredBasic;
    }

    public void setIgnoredOneToMany(List<Customer> ignoredOneToMany) {
        this.ignoredOneToMany = ignoredOneToMany;
    }
    
    public void setIgnoredOneToOne(Item ignoredOneToOne) {
        this.ignoredOneToOne = ignoredOneToOne;
    }

    public void setIgnoredVariableOneToOne(Mattel ignoredVariableOneToOne) {
        this.ignoredVariableOneToOne = ignoredVariableOneToOne;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
