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
package org.eclipse.persistence.internal.localization.i18n;

/**
 * English ResourceBundle for EclipseLinkLocalization messages.
 * <p>
 * Creation date: (07/25/02)
 * @author Shannon Chen
 * @since TOPLink/Java 5.0
 */
public class EclipseLinkLocalizationResource extends java.util.ListResourceBundle {
    static final Object[][] contents = {
                                           { "NoTranslationForThisLocale", " (There is no English translation for this message.)" },
    };

    /**
     * Return the lookup table.
     */
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
