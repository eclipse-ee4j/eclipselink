/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      tware -
package org.eclipse.persistence.jpa.rs.logging;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.persistence.internal.localization.EclipseLinkLocalization;

public class LoggingLocalization extends EclipseLinkLocalization {
    /**
     * Return the message for the given exception class and error number.
     */
    public static String buildMessage(String key, Object[] arguments) {
        return buildMessage(key, arguments, true);
    }

    /**
     * INTERNAL:
     * Return the message for the given exception class and error number.
     * Based on the state of the translate flag - look up translation for the key:value message
     * @param key
     * @param arguments
     * @param translate
     * @return
     */
    public static String buildMessage(String key, Object[] arguments, boolean translate) {
        String message = key;
        ResourceBundle bundle = null;

        // JDK 1.1 MessageFormat can't handle null arguments
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] == null) {
                    arguments[i] = "null";
                }
            }
        }

        bundle = ResourceBundle.getBundle("org.eclipse.persistence.jpa.rs.logging.i18n.LoggingLocalizationResource", Locale.getDefault());

        try {
            message = bundle.getString(key);
        } catch (java.util.MissingResourceException mre) {
            // Found bundle, but couldn't find translation.
            // Get the current language's NoTranslationForThisLocale message.
            bundle = ResourceBundle.getBundle("org.eclipse.persistence.internal.localization.i18n.EclipseLinkLocalizationResource", Locale.getDefault());
            String noTranslationMessage = bundle.getString("NoTranslationForThisLocale");

            if(translate) {
                return MessageFormat.format(message, arguments) + noTranslationMessage;
            } else {
                // For FINE* logs there is no translation
                return MessageFormat.format(message, arguments);
            }
        }
        return MessageFormat.format(message, arguments);
    }
}
