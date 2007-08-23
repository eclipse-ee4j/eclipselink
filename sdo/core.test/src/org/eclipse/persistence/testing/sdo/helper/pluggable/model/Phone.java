/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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
 *  @version $Header: Phone.java 22-aug-2006.16:31:05 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11
 */
package org.eclipse.persistence.testing.sdo.helper.pluggable.model;


/**
 * This simple POJO class has a loose 1-n many aggregation relationship with Employee.<br>
 * @author mfobrien
 */
public class Phone extends POJO {
    // instance variables
    // TODO: we do not handle primitive types yet because the unset will return a wrapper object int->Integer
    private String number;
    private String phoneId;

    // class level constants
    public static final String PHONE_TYPE_HOME = "1";
    public static final String PHONE_TYPE_WORK = "2";
    public static final String PHONE_TYPE_CELL = "3";

    // default constructor
    public Phone() {
    }

    public Phone(String anId, String aNumber) {
        number = aNumber;
        phoneId = anId;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public String getNumber() {
        return number;
    }

    public void setPhoneId(String anId) {
        phoneId = anId;
    }

    public void setPhoneId() {
        phoneId = null;
    }

    // TEMP workaround: issue20060809-2: Handle primitives wrapped by their Objects
    //    public void setPhoneType(Integer aType) {
    //    	if(aType instanceof Integer) {
    //    		phoneType = ((Integer)aType).intValue();
    //    	}
    //    }
    public void setNumber(String number) {
        this.number = number;
    }

    // overloaded function to handle setName(null)
    public void setNumber() {
        number = null;
    }

    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(getClass().getSimpleName());
        aBuffer.append("(");
        aBuffer.append(number);
        aBuffer.append(",");
        aBuffer.append(phoneId);
        aBuffer.append(")");
        return aBuffer.toString();
    }
}