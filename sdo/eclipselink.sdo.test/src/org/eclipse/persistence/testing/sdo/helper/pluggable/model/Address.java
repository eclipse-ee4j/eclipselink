/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
/*
   DESCRIPTION
    <short description of component this file declares/defines>

   PRIVATE CLASSES
    <list of private classes defined - with one-line descriptions>

   NOTES
    <other useful comments, qualifications, etc.>

   MODIFIED    (MM/DD/YY)
    mfobrien    07/28/06 - Creation
 */

/**
 *  @version $Header: Address.java 22-aug-2006.16:30:57 mfobrien Exp $
 *  @author  mfobrien
 *  @since   release specific (what release of product did this appear in)
 */
package org.eclipse.persistence.testing.sdo.helper.pluggable.model;


/**
 * This simple POJO class has a loose 1-1 aggregation relationship with Employee.<br>
 * @author mfobrien
 */
public class Address extends POJO {
    // instance variables
    private String street;

    // default constructor    
    public Address(String aStreet) {
        street = aStreet;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    // overloaded function to handle setName(null)
    public void setStreet() {
        street = null;
    }

    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(getClass().getSimpleName());
        aBuffer.append("(");
        // use reflection instead
        aBuffer.append(street);
        aBuffer.append(")");
        return aBuffer.toString();
    }
}