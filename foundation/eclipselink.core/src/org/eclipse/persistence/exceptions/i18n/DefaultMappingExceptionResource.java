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
 * English ResourceBundle for DefaultMappingException messages.
 *
 * @author: King Wang
 * @since: OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class DefaultMappingExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "20001", "Could not find the parameter type: [{2}], defined in the ejb-jar.xml, of the finder: [{1}] in the entity bean: [{0}]." },
                                           { "20002", "The finder method: [{1}] with the parameters as: [{2}], defined in the ejb-jar.xml, is not found in the home of bean: [{0}]." },
                                           { "20003", "The ejbSelect method: [{1}] with the parameters as: [{2}], defined in the ejb-jar.xml, is not found in the bean class of bean: [{0}]." },
                                           { "20004", "The finder method: [{1}] of bean: [{0}] in ejb-jar.xml file is not well defined. It should start with either 'find' or 'ejbSelect'." },
                                           { "20005", "The abstract getter method: [{0}] is not defined in the bean: [{1}]." },
                                           { "20006", "The cmp field: [{0}] is not defined in the bean: [{1}]." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}