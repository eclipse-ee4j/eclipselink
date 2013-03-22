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
package org.eclipse.persistence.exceptions.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * INTERNAL:
 * Utility class to generate exception messages using ResourceBundles.
 *
 * Creation date: (12/7/00 10:30:38 AM)
 * @author: Rick Barkhouse
 */
public class ExceptionMessageGenerator {
    
    /**
     * Return the loader for loading the resource bundles.
     */
    public static ClassLoader getLoader() {
        ClassLoader loader = ExceptionMessageGenerator.class.getClassLoader();
        
        if (loader == null) {
            loader = ConversionManager.getDefaultManager().getLoader();
        }
        
        return loader;
    }
    
    /**
     * Return the message for the given exception class and error number.
     */
    public static String buildMessage(Class exceptionClass, int errorNumber, Object[] arguments) {
        final String CR = System.getProperty("line.separator");

        String shortClassName = Helper.getShortClassName(exceptionClass);
        String message = "";
        ResourceBundle bundle = null;

        // JDK 1.1 MessageFormat can't handle null arguments
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] == null) {
                arguments[i] = "null";
            }
        }

        bundle = ResourceBundle.getBundle("org.eclipse.persistence.exceptions.i18n." + shortClassName + "Resource", Locale.getDefault(), getLoader());

        try {
            message = bundle.getString(String.valueOf(errorNumber));
        } catch (java.util.MissingResourceException mre) {
            // Found bundle, but couldn't find exception translation.
            // Get the current language's NoExceptionTranslationForThisLocale message.
            bundle = ResourceBundle.getBundle("org.eclipse.persistence.exceptions.i18n.ExceptionResource", Locale.getDefault(), getLoader());
            String noTranslationMessage = bundle.getString("NoExceptionTranslationForThisLocale");
            Object[] args = { CR };
            return format(message, arguments) + format(noTranslationMessage, args);
        }
        return format(message, arguments);
    }

    /**
     * Return the formatted message for the given exception class and error number.
     */
    //Bug#4619864  Catch any exception during formatting and try to throw that exception. One possibility is toString() to an argument
    protected static String format(String message, Object[] arguments) {
        try {
            return MessageFormat.format(message, arguments);
        } catch (Exception ex) {
            ResourceBundle bundle = null;
            bundle = ResourceBundle.getBundle("org.eclipse.persistence.exceptions.i18n.ExceptionResource", Locale.getDefault(), getLoader());
            String errorMessage = bundle.getString("ErrorFormattingMessage");
            Vector vec = new Vector();
            if (arguments != null) {
                for (int index = 0; index < arguments.length; index++) {
                    try {
                        vec.add(arguments[index].toString());
                    } catch (Exception ex2) {
                        vec.add(ex2);
                    }
                }
            }
            return MessageFormat.format(errorMessage, new Object[] {message, vec});
        }
    }

    /**
     * Get one of the generic headers used for the exception's toString().
     *
     * E.g., "EXCEPTION DESCRIPTION: ", "ERROR CODE: ", etc.
     */
    public static String getHeader(String headerLabel) {
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle("org.eclipse.persistence.exceptions.i18n.ExceptionResource", Locale.getDefault(), getLoader());
            return bundle.getString(headerLabel);
        } catch (java.util.MissingResourceException mre) {
            return "[" + headerLabel + "]";
        }
    }
}
