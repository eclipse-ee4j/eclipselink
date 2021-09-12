/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.core.helper;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.w3c.dom.Node;

@SuppressWarnings({"rawtypes"})
public class CoreClassConstants {

    public static final Class ABYTE = Byte[].class;
    public static final Class APBYTE = byte[].class;
    public static final Class APCHAR = char[].class;
    public static final Class ASTRING = String[].class;
    public static final Class ArrayList_class = ArrayList.class;
    public static final Class<BigDecimal> BIGDECIMAL = BigDecimal.class;
    public static final Class<BigInteger> BIGINTEGER = BigInteger.class;
    public static final Class<Boolean> BOOLEAN = Boolean.class;
    public static final Class<Byte> BYTE = Byte.class;
    public static final Class<Calendar> CALENDAR = Calendar.class;
    public static final Class<Character> CHAR = Character.class;
    public static final Class<Class> CLASS = Class.class;
    public static final Class<Collection> Collection_Class = Collection.class;
    public static final Class<Double> DOUBLE = Double.class;
    public static final Class<Duration> DURATION = Duration.class;
    public static final Class<Float> FLOAT = Float.class;
    public static final Class<GregorianCalendar> GREGORIAN_CALENDAR = GregorianCalendar.class;
    public static final Class<Integer> INTEGER = Integer.class;
    public static final Class<List> List_Class = List.class;
    public static final Class<Long> LONG = Long.class;
    public static final Class<Map> Map_Class = Map.class;
    public static final Class<Node> NODE = Node.class;
    public static final Class<Number> NUMBER = Number.class;
    public static final Class<Object> OBJECT = Object.class;
    public static final Class<Boolean> PBOOLEAN = boolean.class;
    public static final Class<Byte> PBYTE = byte.class;
    public static final Class<Character> PCHAR = char.class;
    public static final Class<Double> PDOUBLE = double.class;
    public static final Class<Float> PFLOAT = float.class;
    public static final Class<Integer> PINT = int.class;
    public static final Class<Long> PLONG = long.class;
    public static final Class<Short> PSHORT = short.class;
    public static final Class<QName> QNAME = QName.class;
    public static final Class<Set> Set_Class = Set.class;
    public static final Class<Short> SHORT = Short.class;
    public static final Class<Date> SQLDATE = Date.class;
    public static final Class<String> STRING = String.class;
    public static final Class<Time> TIME = Time.class;
    public static final Class<Timestamp> TIMESTAMP = Timestamp.class;
    public static final Class<URL> URL_Class = URL.class;
    public static final Class<java.util.Date> UTILDATE = java.util.Date.class;
    public static final Class<XMLGregorianCalendar> XML_GREGORIAN_CALENDAR = XMLGregorianCalendar.class;
    public static final Class<File> FILE = File.class;


    protected CoreClassConstants() {
        //no instance please
        //for ClassConstants
    }
}
