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
 * English ResourceBundle for ConcurrencyException messages.
 *
 * Creation date: (12/6/00 9:47:38 AM)
 * @author: Xi Chen
 */
public class ConcurrencyExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "2001", "Wait was interrupted. {0}Message: [{1}]" },
                                           { "2002", "Wait failure on ServerSession." },
                                           { "2003", "Wait failure on ClientSession." },
                                           { "2004", "A signal was attempted before wait() on ConcurrencyManager. This normally means that an attempt was made to {0}commit or rollback a transaction before it was started, or to rollback a transaction twice." },
                                           { "2005", "Wait failure on Sequencing Connection Handler for DatabaseSession." },
                                           { "2006", "Attempt to acquire sequencing values through a single Connection({0}) simultaneously in multiple threads" },
                                           { "2007", "Max number of attempts to lock object: {0} exceded.  Failed to clone the object." },
                                           { "2008", "Max number of attempts to lock object: {0} exceded.  Failed to merge the transaction." },
                                           { "2009", "Max number of attempts to lock object exceded.  Failed to build the object. Thread: {0} has a lock on the object but thread: {1} is building the object"},
                                           { "2010", "Lock has already been transitioned to a Deferred Lock.  A second attempt to transition the lock has been requested by thread: {0} during merge."}

    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
