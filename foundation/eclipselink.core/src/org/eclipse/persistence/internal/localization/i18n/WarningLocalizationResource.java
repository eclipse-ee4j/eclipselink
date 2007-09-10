/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.localization.i18n;

import java.util.ListResourceBundle;

/**
 * English ResourceBundle for ExceptionLocalization messages.
 *
 * @author Gordon Yorke
 * @since TOPLink/Java 5.0
 */
public class WarningLocalizationResource extends ListResourceBundle {
    static final Object[][] contents = {
          { "named_argument_not_found_in_query_parameters", "Missing Query parameter for named argument: {0} 'null' will be substituted." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}