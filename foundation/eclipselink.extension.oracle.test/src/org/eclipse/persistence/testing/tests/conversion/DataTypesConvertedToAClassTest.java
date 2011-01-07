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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.conversion;

import java.util.Vector;
import java.util.Calendar;
import java.sql.*;
import java.math.*;
import java.lang.reflect.Field;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.conversion.ConversionDataObjectForSupportedTypes;

//This test retrieves all the classes that can be converted to a given class by 
//calling getDataTypesConvertedTo() in ConversionManager.  It then converts the 
//underlying objects of all the retrieved classes to the given class.  
//The underlying objects are obtained from ConversionDataObjectForSupportedTypes.example().
//Blob and Clob are not included in the retrieved classes for conversion.
public class DataTypesConvertedToAClassTest extends AutoVerifyTestCase {
    protected Object cm;
    protected Exception exception1;
    protected Exception exception2;
    protected Class sourceClass;
    protected Class targetClass;
    protected Class[] convertedToClasses = new Class[] { BigDecimal.class, BigInteger.class, Boolean.class, Byte.class, byte[].class, Byte[].class, Calendar.class, Character.class, Character[].class, char[].class, java.sql.Date.class, Double.class, Float.class, Integer.class, Long.class, Number.class, Short.class, String.class, Timestamp.class, Time.class, java.util.Date.class };

    public DataTypesConvertedToAClassTest() {
        setDescription("Test getDataTypesConvertedTo() in ConversionManager.");
    }

    public void setup() {
        cm = ConversionManager.getDefaultManager();
    }

    public void test() {
        Vector vec;
        int x;
        int y;
        int z;
        Object obj;
        Class type;
        CMAndPlatformWrapper wrapper = new CMAndPlatformWrapper(cm);

		ConversionDataObjectForSupportedTypes example = ConversionDataObjectForSupportedTypes.example();
        Field[] fields = ConversionDataObjectForSupportedTypes.class.getFields();
        for (x = 0; x < convertedToClasses.length; x++) {
            vec = wrapper.getDataTypesConvertedTo(convertedToClasses[x]);
            for (y = 0; y < vec.size(); y++) {
                for (z = 0; z < fields.length; z++) {
                    type = fields[z].getType();
                    if (vec.elementAt(y) == type) {
                        try {
                            obj = fields[z].get(example);
                        } catch (IllegalAccessException e) {
                            exception1 = e;
                            break;
                        }
                        if ((obj == null) || (type == Blob.class) || (type == Clob.class) || (fields[z].getName().equals("stringToInt") && !isNumber(convertedToClasses[x])) || (fields[z].getName().equals("stringToTimestamp") && !isTimestamp(convertedToClasses[x])) || (fields[z].getName().equals("aString") && !isChar(convertedToClasses[x]))) {
                            continue;
                        }
                        try {
                            wrapper.convertObject(obj, convertedToClasses[x]);
                        } catch (Exception e) {
                            exception2 = e;
                            sourceClass = type;
                            targetClass = convertedToClasses[x];
                        }
                    }
                }
            }
        }
    }

    public void verify() {
        if (exception1 != null) {
            throw (new TestErrorException(exception1.toString()));
        }
        if (exception2 != null) {
            throw (new TestErrorException("Conversion of '" + sourceClass + "' to '" + targetClass + "' falied."));
        }
    }

    protected boolean isNumber(Class aClass) {
        return Number.class.isAssignableFrom(aClass);
    }

    protected boolean isTimestamp(Class aClass) {
        return (aClass == java.util.Date.class) || (aClass == Timestamp.class) || (aClass == Calendar.class);
    }

    protected boolean isChar(Class aClass) {
        return (aClass == Character.class) || (aClass == Character[].class) || (aClass == char[].class) || (aClass == String.class) || (aClass == java.sql.Clob.class);
    }
}
