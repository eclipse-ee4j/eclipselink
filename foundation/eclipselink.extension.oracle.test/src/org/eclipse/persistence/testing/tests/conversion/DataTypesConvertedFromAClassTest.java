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
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.conversion;

import java.util.Vector;
import java.util.Calendar;
import java.sql.Timestamp;
import java.lang.reflect.Field;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.conversion.ConversionDataObjectForSupportedTypes;

//This test retrieves all the classes that can be converted from a given class by 
//calling getDataTypesConvertedFrom() in ConversionManager.  It then converts the underlying
//object of the given class to all the returned classes from getDataTypesConvertedFrom().  
//The given objects are obtained from ConversionDataObjectForSupportedTypes.example().
//Blob and Clob are not included as source object.
public class DataTypesConvertedFromAClassTest extends AutoVerifyTestCase {
    protected Object cm;
    protected Exception exception1;
    protected Exception exception2;
    protected Class sourceClass;
    protected Class targetClass;

    public DataTypesConvertedFromAClassTest() {
        setDescription("Test getDataTypesConvertedFrom() in ConversionManager.");
    }

    public void setup() {
        cm = ConversionManager.getDefaultManager();
    }

    public void test() {
        Vector vec;
        int x;
        int y;
        Object obj;
        Class type;
        CMAndPlatformWrapper wrapper = new CMAndPlatformWrapper(cm);

		ConversionDataObjectForSupportedTypes example = ConversionDataObjectForSupportedTypes.example();
        Field[] fields = ConversionDataObjectForSupportedTypes.class.getFields();
        for (x = 0; x < fields.length; x++) {
            try {
                obj = fields[x].get(example);
            } catch (IllegalAccessException e) {
                exception1 = e;
                break;
            }
            type = fields[x].getType();
            if ((obj != null) && !type.isPrimitive()) {
                vec = wrapper.getDataTypesConvertedFrom(type);
                for (y = 0; y < vec.size(); y++) {
                    //Different string formats are required for number, date and other types
                    if ((fields[x].getName().equals("stringToInt") && !isNumber((Class)vec.elementAt(y))) || (fields[x].getName().equals("stringToTimestamp") && !isTimestamp((Class)vec.elementAt(y))) || (fields[x].getName().equals("aString") && !isChar((Class)vec.elementAt(y)))) {
                        continue;
                    }
                    try {
                        wrapper.convertObject(obj, (Class)vec.elementAt(y));
                    } catch (Exception e) {
                        exception2 = e;
                        sourceClass = fields[x].getType();
                        targetClass = (Class)vec.elementAt(y);
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

    protected ConversionManager getCm() {
        return (ConversionManager)cm;
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
