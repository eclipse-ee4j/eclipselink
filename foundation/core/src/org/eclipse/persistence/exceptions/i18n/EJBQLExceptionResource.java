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
 * English ResourceBundle for EJBQLException.
 *
 */
public class EJBQLExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "8001", "Syntax Recognition Problem parsing the query [{0}]. The parser returned the following [{1}]." },
                                           { "8002", "General Problem parsing the query [{0}]. The parser returned the following [{1}]." },
                                           { "8003", "The class [{0}] was not found. The parser returned the following [{1}]." },
                                           { "8004", "A parsing problem was encountered resolving the alias - [{0}]." },
                                           { "8006", "A problem was encountered resolving the class name - The descriptor for [{0}] was not found." },
                                           { "8005", "A problem was encountered resolving the class name - The class [{0}] was not found." },
                                           { "8007", "A problem was encountered resolving the class name - The mapping for [{0}] was not found." },
                                           { "8008", "A problem was encountered building the query expression - The expressionBuilder for [{0}] was not found." },
                                           { "8009", "The expression [{0}] is currently not supported." },
                                           { "8010", "General Problem parsing the query [{0}]." },
                                           { "8011", "Invalid collection member declaration [{0}], expected collection valued association-field." },
                                           { "8012", "Not yet implemented: {0}." },
                                           { "8013", "Constructor class ''{0}'' not found." },
                                           { "8014", "Invalid SIZE argument [{0}], expected collection valued association-field." },
                                           { "8015", "Invalid enum literal. The enum type {0} does not have an enum constant {1}." },
                                           { "8016", "Invalid SELECT expression [{0}] for query with grouping [{1}]. Only aggregates, GROUP BY items or constructor expressions of these are allowed in the SELECT clause of a GROUP BY query." },
                                           { "8017", "Invalid HAVING expression [{0}] for query with grouping [{1}]. The HAVING clause must specify search conditions over the grouping items or aggregate functions that apply to grouping items." },
                                           { "8018", "Invalid multiple use of parameter [{0}] assuming different parameter types [{1}] and [{2}]." },
                                           { "8019", "Multiple declaration of identification variable [{0}], previously declared as [{1} {0}]." },
                                           { "8020", "Invalid {0} function argument [{1}], expected argument of type [{2}]." },
                                           { "8021", "Invalid ORDER BY item [{0}] of type [{1}], expected expression of an orderable type." },
                                           { "8022", "Invalid {0} expression argument [{1}], expected argument of type [{2}]." },
                                           { "8023", "Syntax error parsing the query [{0}]." },
                                           { "8024", "Syntax error parsing the query [{0}] at [{1}]." },
                                           { "8025", "Syntax error parsing the query [{0}], unexpected token [{1}]." },
                                           { "8026", "Syntax error parsing the query [{0}], unexpected char [{1}]." },
                                           { "8027", "Syntax error parsing the query [{0}], expected char [{1}], found [{1}]." },
                                           { "8028", "Syntax error parsing the query [{0}], unexpected end of query." },
                                           { "8029", "Invalid navigation expression [{0}], cannot navigate expression [{1}] of type [{2}] inside a query." },
                                           { "8030", "Unknown state or association field [{0}] of class [{1}]." }
    };

    /**
    * Return the lookup table.
    */
    protected Object[][] getContents() {
        return contents;
    }
}