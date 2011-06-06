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
 *     12/2/2009-2.1 Guy Pelletier 
 *       - 296289: Add current annotation metadata support on mapped superclasses to EclipseLink-ORM.XML Schema 
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.xml.relationships;

public class Company {
    private CEO ceo;
    private String name;

    public CEO getCeo() {
        return ceo;
    }
    
    public String getName() {
        return name;
    }

    public void setCeo(CEO ceo) {
        this.ceo = ceo;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
