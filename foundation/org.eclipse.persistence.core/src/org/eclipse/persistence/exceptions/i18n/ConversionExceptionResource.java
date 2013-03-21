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

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for ConversionException messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: Rick Barkhouse
 */
public class ConversionExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "3001", "The object [{0}], of class [{1}], could not be converted to [{2}]." },
                                           { "3002", "The object [{0}], of class [{1}], from mapping [{2}] with descriptor [{3}], could not be converted to [{4}]." },
                                           { "3003", "Incorrect date format: [{0}] (expected [YYYY-MM-DD])" },
                                           { "3004", "Incorrect time format: [{0}] (expected [HH:MM:SS])" },
                                           { "3005", "Incorrect timestamp format: [{0}] (expected [YYYY-MM-DD HH:MM:SS.NNNNNNNNN])" },
                                           { "3006", "[{0}] must be of even length to be converted to a byte array." },
                                           { "3007", "The object [{0}], of class [{1}], could not be converted to [{2}].  Ensure that the class [{2}] is on the CLASSPATH.  You may need to use alternate API passing in the appropriate class loader as required, or setting it on the default ConversionManager" },
                                           { "3008", "Incorrect date-time format: [{0}] (expected [YYYY-MM-DD'T'HH:MM:SS])" }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
