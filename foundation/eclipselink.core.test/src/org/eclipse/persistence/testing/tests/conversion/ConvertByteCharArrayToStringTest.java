/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.testing.framework.*;

//Bug#3854296.  Test if byte[], Byte[], char[] and Character[] are converted to String correctly.
public class ConvertByteCharArrayToStringTest extends AutoVerifyTestCase {
    String hexString = Helper.buildHexStringFromBytes(new byte[] { 1, 56, -128, 127 });
    String testString = "A test string";
    byte[] byteArray;
    Byte[] byteOArray;
    char[] charArray;
    Character[] charOArray;
    String byteString;
    String byteOString;
    String charString;
    String charOString;

    public ConvertByteCharArrayToStringTest() {
        setDescription("Test if byte[], Byte[], char[] and Character[] are converted to String correctly.");
    }

    public void setup() {
        //Convert to an array first
        byteArray = (byte[])ConversionManager.getDefaultManager().convertObject(hexString, ClassConstants.APBYTE);
        byteOArray = (Byte[])ConversionManager.getDefaultManager().convertObject(hexString, ClassConstants.ABYTE);
        charArray = (char[])ConversionManager.getDefaultManager().convertObject(testString, ClassConstants.APCHAR);
        charOArray = (Character[])ConversionManager.getDefaultManager().convertObject(testString, ClassConstants.ACHAR);
    }

    public void test() {
        //Convert back to a string
        byteString = (String)ConversionManager.getDefaultManager().convertObject(byteArray, ClassConstants.STRING);
        byteOString = (String)ConversionManager.getDefaultManager().convertObject(byteOArray, ClassConstants.STRING);
        charString = (String)ConversionManager.getDefaultManager().convertObject(charArray, ClassConstants.STRING);
        charOString = (String)ConversionManager.getDefaultManager().convertObject(charOArray, ClassConstants.STRING);
    }

    public void verify() {
        if (!byteString.equals(hexString)) {
            throw (new TestErrorException("Conversion of string to byte[] then to string failed.  The original string is" + hexString + ", the returned string is " + byteString));
        }
        if (!byteOString.equals(hexString)) {
            throw (new TestErrorException("Conversion of string to Byte[] then to string failed.  The original string is" + hexString + ", the returned string is " + byteOString));
        }
        if (!charString.equals(testString)) {
            throw (new TestErrorException("Conversion of string to char[] then to string failed.  The original string is" + testString + ", the returned string is " + charString));
        }
        if (!charOString.equals(testString)) {
            throw (new TestErrorException("Conversion of string to Character[] then to string failed.  The original string is" + testString + ", the returned string is " + charOString));
        }
    }
}
