/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English ResourceBundle for XMLDataStoreException messages.
 *
 * Creation date: (2/28/01 9:47:38 AM)
 * @author TopLink Maintenance Team
 */
public class XMLDataStoreExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "13000", "File not found: [{0}]" },
                                           { "13001", "Unable to close write stream for: [{0}]" },
                                           { "13002", "Not a directory: [{0}]" },
                                           { "13003", "Unable to create directory: [{0}]" },
                                           { "13004", "Directory not found: [{0}]" },
                                           { "13005", "File already exists: [{0}]" },
                                           { "13006", "Unable to create write stream for: [{0}]" },
                                           { "13007", "Invalid field value. The field value must be a String or SDKFieldValue. {3}Field name: [{0}] {3}Field value: [{1}] {3}Field value class: [{2}]" },
                                           { "13008", "The class loader could not find the class: [{0}]" },
                                           { "13009", "** Parsing error" },
                                           { "13010", "[{0}]" },
                                           { "13011", "[{0}]" },
                                           { "13012", "Unable to close read stream for: [{0}]" },
                                           { "13013", "Heterogeneous child elements in an XML file: [{0}]" },
                                           { "13014", "The class [{0}] has no such method: [{1}]" },
                                           { "13015", "IllegalAccessException when invoking method: [{0}]" },
                                           { "13016", "InvocationTargetException when invoking method: [{0}] {2}TargetException: [{1}]" },
                                           { "13017", "InstantiationException when instantiating class: [{0}]" },
                                           { "13018", "IllegalAccessException when instantiating class: [{0}]" },
                                           { "13019", "All the fields in the database row must have the same table name: {1}[{0}]" },
                                           { "13020", "An element data type name is required for these elements: {1}[{0}]" }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}