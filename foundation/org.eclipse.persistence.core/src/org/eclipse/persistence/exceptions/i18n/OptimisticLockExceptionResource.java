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
 * English ResourceBundle for OptimisticLockException messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: Xi Chen
 */
public class OptimisticLockExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "5001", "An attempt was made to delete the object [{0}], but it has no version number in the identity map. {3}It may not have been read before the delete was attempted. {3}Class> {1} Primary Key> {2}" },
                                           { "5003", "The object [{0}] cannot be deleted because it has changed or been deleted since it was last read. {3}Class> {1} Primary Key> {2}" },
                                           { "5004", "An attempt was made to update the object [{0}], but it has no version number in the identity map. {3}It may not have been read before the update was attempted. {3}Class> {1} Primary Key> {2}" },
                                           { "5006", "The object [{0}] cannot be updated because it has changed or been deleted since it was last read. {3}Class> {1} Primary Key> {2}" },
                                           { "5007", "The object [{0}] must have a non-read-only mapping to the version lock field." },
                                           { "5008", "Must map the version lock field to java.sql.Timestamp when using Timestamp Locking" },
                                           { "5009", "The object of class [{1}] with primary key [{0}] cannot be unwrapped because it was deleted since it was last read." },
                                           { "5010", "The object [{0}] cannot be merged because it has changed or been deleted since it was last read. {2}Class> {1}" },
                                           { "5011", "One or more objects cannot be updated because it has changed or been deleted since it was last read" }
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
