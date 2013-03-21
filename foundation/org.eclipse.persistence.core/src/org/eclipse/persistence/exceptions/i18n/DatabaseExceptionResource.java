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
 * English ResourceBundle for DatabaseException messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: Xi Chen
 */
public class DatabaseExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "4003", "Configuration error.  Class [{0}] not found." },
                                           { "4005", "DatabaseAccessor not connected." },
                                           { "4006", "Error reading BLOB data from stream in getObject()." },
                                           { "4007", "Could not convert object type due to an internal error. {0}java.sql.TYPES: [{1}]" },
                                           { "4008", "You cannot log out while a transaction is in progress." },
                                           { "4009", "The sequence table information is not complete." },
                                           { "4011", "Error preallocating sequence numbers.  The sequence table information is not complete." },
                                           { "4014", "Cannot register SynchronizationListener." },
                                           { "4015", "Synchronized UnitOfWork does not support the commitAndResume() operation." },
                                           { "4016", "Configuration error.  Could not instantiate driver [{0}]." },
                                           { "4017", "Configuration error.  Could not access driver [{0}]." },
                                           { "4018", "The TransactionManager has not been set for the JTS driver." },
                                           { "4019", "Error while obtaining information about the database. Refer to the nested exception for more details." },
                                           { "4020", "Could not find the matched database field for the specified optimistic locking field[{0}]. Note, the matching is case sensitive,therefore,if you allowed the column name to default on the getter method,the name will be uppercased." },
                                           { "4021", "Unable to acquire a connection from driver [{0}], user [{1}] and URL [{2}].  Verify that you have set the expected driver class and URL.  Check your login, persistence.xml or sessions.xml resource.  The jdbc.driver property should be set to a class that is compatible with your database platform" },
                                           { "4022", "Accessor or its connection has been set to null.  This can occur if the ClientSession or UnitOfWork was released in a seperate thread, for instance if a Timeout occurred." }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
