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
 *     09/23/2009-2.0  mobrien 
 *       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)  
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.metamodel;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Use Case: IdClass identifiers declared across multiple mappedSuperclasses in an inheritance hierarchy.
 * Note: The following MappedSuperclass defines 3 of 4 of the Id fields as part of the IdClass MSIdClassPK.
 * The 4th field is declared on the subclass.
 * The IdClass annotation can go on the subclass or the entity but not on this root.
 * As long as resolution of all fields in the IdClass are available - the configuration is good. 
 */
@MappedSuperclass
public abstract class MS_MS_Entity_Root implements java.io.Serializable {
    
    @Id
    @Column(name="TYPE")
    public String type;
    @Id
    @Column(name="LENGTH")
    protected String length;
    @Id
    @Column(name="WIDTH")
    protected String width;
  
    
    @Version
    @Column(name="MSMSENTITY_VERSION")
    private int version;
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }
    
    public int getVersion() { 
        return version; 
    }
    
    protected void setVersion(int version) {
        this.version = version;
    }
}
