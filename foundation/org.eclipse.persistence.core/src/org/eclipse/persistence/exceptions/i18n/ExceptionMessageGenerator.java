/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2018 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     08/29/2016 Jody Grassel
//       - 500441: Eclipselink core has System.getProperty() calls that are not potentially executed under doPriv()
package org.eclipse.persistence.exceptions.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * INTERNAL:
 * Utility class to generate exception messages using ResourceBundles.
 *
 * Creation date: (12/7/00 10:30:38 AM)
 * @author: Rick Barkhouse
 */
public class ExceptionMessageGenerator {
    private final static String CR = PrivilegedAccessHelper.getSystemProperty("line.separator");

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
