/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
 * <b>Purpose</b>: Any EclipseLink message in Foundation Library &amp; J2EE Integration JARs
 * should be a subclass of this class.
 * <p>
 * Creation date: (7/12/00)
 * @author Shannon Chen
 * @since TOPLink/Java 5.0
 */
public abstract class EclipseLinkLocalization {

    // Get the current language's NoTranslationForThisLocale message.
    private static final String NO_TRANSLATION_MESSAGE = ResourceBundle.getBundle("org.eclipse.persistence.internal.localization.i18n.EclipseLinkLocalizationResource", Locale.getDefault()).getString("NoTranslationForThisLocale");

    /**
     * Return the message for the given exception class and error number.
     * <p>
     * Deliberately not varargs. Every subclass declares its own
     * {@code buildMessage(String key, Object... arguments)}, and inherits this one. If both
     * are varargs then a call such as
     * {@code ExceptionLocalization.buildMessage("some-key", name, queryString)} resolves to
     * this method rather than the subclass's, because a three-parameter signature is more
     * specific than a two-parameter one. The message key is then silently taken as the
     * localization class name, and the bundle lookup fails with MissingResourceException.
     * Keeping the array form makes this method inapplicable to the unwrapped call and lets
     * the intended subclass method win.
     */
    public static String buildMessage(String localizationClassName, String key, Object[] arguments) {
        return buildMessage(localizationClassName, key, arguments, true);
    }

    /**
     * INTERNAL:
     * Return the message for the given exception class and error number.
     * Based on the state of the translate flag - look up translation for the key:value message
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
            bundle = ResourceBundle.getBundle("org.eclipse.persistence.internal.localization.i18n." + localizationClassName + "Resource",
                    Locale.getDefault());
            message = bundle.getString(key);
        } catch (java.util.MissingResourceException mre) {
            if (translate) {
                // Found bundle, but couldn't find translation.
                // Use the current language's NoTranslationForThisLocale message.
                if (arguments == null) {
                    return message + NO_TRANSLATION_MESSAGE;
                }

                return MessageFormat.format(message, arguments) + NO_TRANSLATION_MESSAGE;
            }
        }

        if (arguments == null) {
            return message;
        }

        return MessageFormat.format(message, arguments);
    }

}
