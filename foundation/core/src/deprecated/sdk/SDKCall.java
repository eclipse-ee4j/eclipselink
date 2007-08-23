/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.sdk;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * <code>SDKCall</code> augments the <code>Call</code> interface
 * to define a bit more
 * behavior, as required by the <code>SDKQueryMechanism</code>:<ul>
 * <li> When a query is executed, it is cloned; the call must be
 * able to provide a clone of itself that corresponds to this cloned query.
 * <li> The query mechanism will invoke the call at execution time
 * and pass it the parameters via a database row. The query
 * mechanism will also pass the accessor to the call.
 * </ul>
 *
 * @see SDKQueryMechanism
 * @see SDKAccessor
 *
 * @author Big Country
 *    @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public interface SDKCall extends Call {

    /**
     * Execute the call and return the results.
     */
    Object execute(AbstractRecord translationRow, Accessor accessor) throws SDKDataStoreException;
}