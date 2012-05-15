/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.nosql.mapped;

import javax.persistence.Embeddable;

import org.eclipse.persistence.annotations.DataFormatType;
import org.eclipse.persistence.annotations.NoSql;


/**
 * Model address class, maps to ADDRESS record.
 */
@Embeddable
@NoSql(dataFormat=DataFormatType.MAPPED)
public class Address {
    public String addressee;
    public String street;
    public String city;
    public String state;
    public String country;
    public String zipCode;

    public String toString() {
        return "Address(" + addressee + ", " + street + ", " + city + ", " + state + ", " + country + ", " + zipCode + ")";
    }
}
