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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import javax.persistence.Access;
import static javax.persistence.AccessType.FIELD;
import static javax.persistence.AccessType.PROPERTY;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The owning class has field access, and so to process this class as
 * property access we must specify the Access(PROPERTY) 
 */
@Embeddable
@Access(PROPERTY)
public class EmbeddedSerialNumber {
    @Access(FIELD)
    @Column(name="ES_NUMBER")
    public Integer number;
    
    private String breweryCode;
    
    public EmbeddedSerialNumber() {}
    
    public String getBreweryCode() {
        return this.breweryCode;
    }

    public void setBreweryCode(String breweryCode) {
        this.breweryCode = breweryCode;
    }
}
