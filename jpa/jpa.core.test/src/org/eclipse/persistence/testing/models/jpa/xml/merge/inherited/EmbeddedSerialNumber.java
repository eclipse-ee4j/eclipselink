/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EmbeddedSerialNumber {
    private Integer number;
    private String breweryCode;
    
    public EmbeddedSerialNumber() {}
    
    @Column(name="ES_NUMBER")
    public Integer getNumber() {
        return this.number;
    }
    
    public String getBreweryCode() {
        return this.breweryCode;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setBreweryCode(String breweryCode) {
        this.breweryCode = breweryCode;
    }
}
