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
package org.eclipse.persistence.queries;

import java.io.Serializable;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.queries.*;

/**
 * Call defines the interface used primarily by EclipseLink queries
 * and query mechanisms to perform the necessary actions
 * (read, insert, update, delete) on the data store.
 * A Call can collaborate with an Accessor to perform its
 * responsibilities. The only explicit requirement of a Call is that
 * it be able to supply the appropriate query mechanism for
 * performing its duties. Otherwise, the Call is pretty much
 * unrestricted as to how it should perform its responsibilities.
 *
 * @see DatabaseQuery
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 */
public interface Call extends Cloneable, Serializable {

    /**
     * INTERNAL:
     * Return the appropriate mechanism,
     * with the call set as necessary.
     */
    DatabaseQueryMechanism buildNewQueryMechanism(DatabaseQuery query);

    /**
     * INTERNAL:
     * Return the appropriate mechanism,
     * with the call added as necessary.
     */
    DatabaseQueryMechanism buildQueryMechanism(DatabaseQuery query, DatabaseQueryMechanism mechanism);

    /**
     * INTERNAL:
     * Return a clone of the call.
     */
    Object clone();

    /**
     * INTERNAL:
     * Return a string appropriate for the session log.
     */
    String getLogString(Accessor accessor);

    /**
     * INTERNAL:
     * Return whether the call is finished returning
     * all of its results (e.g. a call that returns a cursor
     * will answer false).
     */
    boolean isFinished();

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    boolean isNothingReturned();

    /**
     * The return type is one of, NoReturn, ReturnOneRow or ReturnManyRows.
     */
    boolean isOneRowReturned();
}
