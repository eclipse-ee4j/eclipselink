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

import org.eclipse.persistence.sessions.Record;


/**
 * This is a default implementation of the <code>FieldTranslator</code> interface.
 * It does nothing to the database row - it simply returns it unchanged.
 *
 * @see AbstractSDKCall
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public class DefaultFieldTranslator implements FieldTranslator {

    /**
     * Default constructor.
     */
    public DefaultFieldTranslator() {
        super();
    }

    /**
     * Translate and return the specified database row that was
     * read from the data store.
     * Simply return the row unchanged.
     */
    public Record translateForRead(Record row) {
        return row;
    }

    /**
     * Translate and return the specified database row that will
     * be written to the data store.
     * Simply return the row unchanged.
     */
    public Record translateForWrite(Record row) {
        return row;
    }
}