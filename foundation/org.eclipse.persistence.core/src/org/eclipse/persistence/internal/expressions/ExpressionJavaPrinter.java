/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.expressions;

import java.io.*;
import java.math.*;
import java.util.Calendar;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;

/**
 * <p><b>Purpose</b>: Expression Java printer.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Print an expression in Java format.
 * <li> Used in project class generation.
 * </ul>
 *    @since TOPLink10.1.3
 */
public class ExpressionJavaPrinter {

    // What we write on
    protected StringWriter writer;

    // ExpressionBuilder string, e.g. expBuilder
    protected String builderString;

    // Database platform for the project
    protected DatabasePlatform platform;

    public ExpressionJavaPrinter(String builderString, StringWriter writer, DatabasePlatform platform) {
        this.builderString = builderString;
        this.writer = writer;
        this.platform = platform;
    }

    public String getBuilderString() {
        return builderString;
    }

    public DatabasePlatform getPlatform() {
        return platform;
    }

    public StringWriter getWriter() {
        return writer;
    }

    public void printString(String value) {
        getWriter().write(value);
   }

    //Used to convert a java object to java print format
    public void printJava(Object object) {
        if (object == null) {
            printString("null");
        } else if (object.getClass() == ClassConstants.STRING) {
            printString("\"");
            printString((String)object);
            printString("\"");
        } else if (object instanceof Calendar) {
            printString("new java.util.Date(");
            printString(String.valueOf(((Calendar)object).getTimeInMillis()));
            printString("L)");
        } else if (object.getClass() == ClassConstants.TIMESTAMP) {
            printString("new java.sql.Timestamp(");
            printString(String.valueOf(((java.sql.Timestamp)object).getTime()));
            printString("L)");
        } else if (object.getClass() == ClassConstants.SQLDATE) {
            printString("new java.sql.Date(");
            printString(String.valueOf(((java.sql.Date)object).getTime()));
            printString("L)");
        } else if (object.getClass() == ClassConstants.TIME) {
            printString("new java.sql.Time(");
            printString(String.valueOf(((java.sql.Time)object).getTime()));
            printString("L)");
        } else if (object.getClass() == ClassConstants.UTILDATE) {
            printString("new java.util.Date(");
            printString(String.valueOf(((java.util.Date)object).getTime()));
            printString("L)");
        } else if (object.getClass() == ClassConstants.BYTE) {
            printByte((Byte)object);
        } else if (object.getClass() == ClassConstants.APBYTE) {
            printString("new byte[] {");
            byte[] bytes = (byte[])object;
            if (bytes.length > 0) {
                printString(String.valueOf(bytes[0]));                
                for (int index = 1; index < bytes.length; index++) {
                    printString(",");                
                    printString(String.valueOf(bytes[index]));                
                }
            }
            printString("}");
        } else if (object.getClass() == ClassConstants.ABYTE) {
            printString("new Byte[] {");
            Byte[] bytes = (Byte[])object;
            if (bytes.length > 0) {
                printByte(bytes[0]);                
                for (int index = 1; index < bytes.length; index++) {
                    printString(",");                
                    printByte(bytes[index]);                
                }
            }
            printString("}");
        } else if (object.getClass() == ClassConstants.CHAR) {
            printCharacter((Character)object);
        } else if (object.getClass() == ClassConstants.APCHAR) {
            printString("new char[] {");
            char[] chars = (char[])object;
            if (chars.length > 0) {
                printString("'");                                
                printString(String.valueOf(chars[0]));                
                printString("'");                                
                for (int index = 1; index < chars.length; index++) {
                    printString(",");                
                    printString("'");                                
                    printString(String.valueOf(chars[index]));                
                    printString("'");                                
                }
            }
            printString("}");
        } else if (object.getClass() == ClassConstants.ACHAR) {
            printString("new Character[] {");
            Character[] chars = (Character[])object;
            if (chars.length > 0) {
                printCharacter(chars[0]);                
                for (int index = 1; index < chars.length; index++) {
                    printString(",");                
                    printCharacter(chars[index]);                
                }
            }
            printString("}");
        } else if (object.getClass() == ClassConstants.BIGDECIMAL) {
            printString("new java.math.BigDecimal(\"");
            printString(((BigDecimal)object).toString());
            printString("\")");                
        } else if (object.getClass() == ClassConstants.BIGINTEGER) {
            printString("new java.math.BigInteger(\"");
            printString(((BigInteger)object).toString());
            printString("\")");                
        } else {
            printString((String)ConversionManager.getDefaultManager().convertObject(object, String.class));
        }
        
    }
    
    public void printByte(Byte aByte) {
            printString("new Byte((byte)");
            printString((aByte).toString());
            printString(")");        
    }

    public void printCharacter(Character aCharacter) {
            printString("new Character('");
            printString((aCharacter).toString());
            printString("')");
    }
}
