/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     gpelletie Feb 19th 2008
package org.eclipse.persistence.testing.models.jpa.xml.composite.advanced.member_1;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * A custom converter used for testing the EclipseLink ORM converter.
 */
public class ProvinceConverter implements Converter {
    public ProvinceConverter() {}

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue == null) {
            return null;
        } else {
            String province = (String) dataValue;

            return switch (province) {
                case "AB" -> "Alberta";
                case "BC" -> "British Columnbia";
                case "MB" -> "Manitoba";
                case "NB" -> "New Brunswick";
                case "NT" -> "Northwest Territories";
                case "NS" -> "Nova Scotia";
                case "NU" -> "Nunavut";
                case "ON" -> "Ontario";
                case "PE" -> "Prince Edward Island";
                case "QC" -> "Quebec";
                case "SK" -> "Saskatchewan";
                case "YT" -> "Yukon";
                default -> province;
            };
        }
    }

    @Override
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

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {}

    @Override
    public boolean isMutable() {
        return false;
    }
}

