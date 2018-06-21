/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.localization;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>
 * <b>Purpose</b>: Any EclipseLink message in Foundation Library & J2EE Integration JARs
 * should be a subclass of this class.
 *
 * Creation date: (7/12/00)
 * @author Shannon Chen
 * @since TOPLink/Java 5.0
 */
public abstract class EclipseLinkLocalization {

    /**
     * Return the message for the given exception class and error number.
     */
    public static String buildMessage(String localizationClassName, String key, Object[] arguments) {
        return buildMessage(localizationClassName, key, arguments, true);
    }

    /**
     * INTERNAL:
     * Return the message for the given exception class and error number.
     * Based on the state of the translate flag - look up translation for the key:value message
     * @param localizationClassName
     * @param key
     * @param arguments
     * @param translate
     * @return
     */
    public static String buildMessage(String localizationClassName, String key, Object[] arguments, boolean translate) {
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

        try {
            bundle = ResourceBundle.getBundle("org.eclipse.persistence.internal.localization.i18n." + localizationClassName + "Resource", Locale.getDefault());
            message = bundle.getString(key);
        } catch (java.util.MissingResourceException mre) {
            if (translate) {
                // Found bundle, but couldn't find translation.
                // Get the current language's NoTranslationForThisLocale message.
                bundle = ResourceBundle.getBundle("org.eclipse.persistence.internal.localization.i18n.EclipseLinkLocalizationResource", Locale.getDefault());
                String noTranslationMessage = bundle.getString("NoTranslationForThisLocale");
                   return MessageFormat.format(message, arguments) + noTranslationMessage;
            }
        }
        return MessageFormat.format(message, arguments);
    }

}
