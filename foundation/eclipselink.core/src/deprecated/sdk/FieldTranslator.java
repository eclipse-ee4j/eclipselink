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

import java.io.*;

import org.eclipse.persistence.sessions.Record;


/**
 * This interface defines a mechanism for translating the field names in a
 * <code>Record</code> from those defined in the data store to
 * those expected by the appropriate <code>ClassDescriptor</code>(s)
 * and vice versa.
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.eis}
 */
public interface FieldTranslator extends Serializable {

    /**
     * Translate and return the specified database row that was
     * read from the data store.
     */
	Record translateForRead(Record row);

    /**
     * Translate and return the specified database row that will
     * be written to the data store.
     */
	Record translateForWrite(Record row);
}