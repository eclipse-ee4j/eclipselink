/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     gpelletie Feb 19th 2008
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_1;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * A custom converter used for testing the EclipseLink ORM converter.
 */
public class ProvinceConverter implements Converter {
    public ProvinceConverter() {}
    
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue == null) {
        	return null;
        } else {
            String province = (String) dataValue;

            if (province.equals("AB")) {
                return "Alberta";
            } else if (province.equals("BC")) {
                return "British Columnbia";
            } else if (province.equals("MB")) {
                return "Manitoba";
            } else if (province.equals("NB")) {
                return "New Brunswick";
            } else if (province.equals("NT")) {
                return "Northwest Territories";
            } else if (province.equals("NS")) {
                return "Nova Scotia";
            } else if (province.equals("NU")) {
                return "Nunavut";
            } else if (province.equals("ON")) {
                return "Ontario";
            } else if (province.equals("PE")) {
                return "Prince Edward Island";
            } else if (province.equals("QC")) {
                return "Quebec";
            } else if (province.equals("SK")) {
                return "Saskatchewan";
            } else if (province.equals("YT")) {
                return "Yukon";
            } else {
            	return province;
            }
        }
    }
    
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue == null) {
        	return null;
        } else {
            String province = (String) objectValue;
        
            if (province.equalsIgnoreCase("Alberta")) {
                return "AB";
            } else if (province.equalsIgnoreCase("British Columnbia")) {
                return "BC";
            } else if (province.equalsIgnoreCase("Manitoba")) {
                return "MB";
            } else if (province.equalsIgnoreCase("New Brunswick")) {
                return "NB";
            } else if (province.equalsIgnoreCase("Northwest Territories")) {
                return "NT";
            } else if (province.equalsIgnoreCase("Nova Scotia")) {
                return "NS";
            } else if (province.equalsIgnoreCase("Nunavut")) {
                return "NU";
            } else if (province.equalsIgnoreCase("Ontario")) {
                return "ON";
            } else if (province.equalsIgnoreCase("Prince Edward Island")) {
                return "PE";
            } else if (province.equalsIgnoreCase("Quebec")) {
                return "QC";
            } else if (province.equalsIgnoreCase("Saskatchewan")) {
                return "SK";
            } else if (province.equalsIgnoreCase("Yukon")) {
                return "YT";
            } else {
            	return province;
            }
        }
    }

	public void initialize(DatabaseMapping mapping, Session session) {}
    
	public boolean isMutable() {
        return false;
    }
}

