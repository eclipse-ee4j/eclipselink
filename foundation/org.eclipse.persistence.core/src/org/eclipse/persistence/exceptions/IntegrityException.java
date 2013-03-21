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
package org.eclipse.persistence.exceptions;

import java.io.*;
import java.util.*;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;

/**
 *    <p><b>Purpose</b>: IntegrityExceptions is used to throw all the Descriptors exceptions.
 *
 */
public class IntegrityException extends ValidationException {
    protected IntegrityChecker integrityChecker;

    /**
     * INTERNAL:
     * IntegrityExceptions is used to throw all the descriptor exceptions.
     */
    public IntegrityException() {
        super();
    }

    /**
     * INTERNAL:
     * To throw all the descriptor exceptions.
     */
    public IntegrityException(IntegrityChecker integrityChecker) {
        super();
        this.integrityChecker = integrityChecker;
    }

    /**
     * PUBLIC:
     * Return Integrity Checker.
     */
    public IntegrityChecker getIntegrityChecker() {
        return integrityChecker;
    }

    /**
     * PUBLIC:
     * This method is used to print out all the descriptor exceptions.
     */
    public String getMessage() {
        String cr = org.eclipse.persistence.internal.helper.Helper.cr();
        java.io.StringWriter swriter = new java.io.StringWriter();
        java.io.PrintWriter writer = new java.io.PrintWriter(swriter);
        writer.println(cr + ExceptionMessageGenerator.getHeader("DescriptorExceptionsHeader"));
        writer.println("---------------------------------------------------------");
        for (Enumeration enumtr = getIntegrityChecker().getCaughtExceptions().elements();
                 enumtr.hasMoreElements();) {
            Exception e = (Exception)enumtr.nextElement();
            if (e instanceof DescriptorException) {
                writer.println(cr + e);
            }
        }

        if (getIntegrityChecker().hasRuntimeExceptions()) {
            writer.println(cr + ExceptionMessageGenerator.getHeader("RuntimeExceptionsHeader"));
            writer.println("---------------------------------------------------------");
            for (Enumeration enumtr = getIntegrityChecker().getCaughtExceptions().elements();
                     enumtr.hasMoreElements();) {
                Exception e = (Exception)enumtr.nextElement();
                if (!(e instanceof DescriptorException)) {
                    writer.println(cr + e);
                }
            }
        }

        writer.flush();
        swriter.flush();
        return swriter.toString();
    }
    
    /**
     * PUBLIC:
     * Print both the normal and internal stack traces.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }
    
    /**
     * PUBLIC:
     * Print both the normal and internal stack traces.
     */
    public void printStackTrace(PrintStream outStream) {
        printStackTrace(new PrintWriter(outStream));
    }

    /**
     * PUBLIC:
     * Print both the normal and internal stack traces.
     */
    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        String cr = org.eclipse.persistence.internal.helper.Helper.cr();
        writer.println(cr + ExceptionMessageGenerator.getHeader("DescriptorExceptionsHeader"));
        writer.println("---------------------------------------------------------");
        for (Enumeration enumtr = getIntegrityChecker().getCaughtExceptions().elements();
                 enumtr.hasMoreElements();) {
            Exception e = (Exception)enumtr.nextElement();
            if (e instanceof DescriptorException) {
                writer.println(cr);
                e.printStackTrace(writer);
            }
        }

        if (getIntegrityChecker().hasRuntimeExceptions()) {
            writer.println(cr + ExceptionMessageGenerator.getHeader("RuntimeExceptionsHeader"));
            writer.println("---------------------------------------------------------");
            for (Enumeration enumtr = getIntegrityChecker().getCaughtExceptions().elements();
                     enumtr.hasMoreElements();) {
                Exception e = (Exception)enumtr.nextElement();
                if (!(e instanceof DescriptorException)) {
                    writer.println(cr);
                    e.printStackTrace(writer);
                }
            }
        }

        writer.flush();
    }
}
