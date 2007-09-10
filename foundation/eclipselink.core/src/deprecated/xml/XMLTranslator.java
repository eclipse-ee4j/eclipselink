/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;

import java.io.*;

import org.eclipse.persistence.sessions.Record;

/**
 * This interface defines the protocol from translating an XML document
 * to a database row and vice versa.
 *
 * @author Les Davis
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public interface XMLTranslator extends Serializable {

    /**
     * Build a database row from the XML document contained
     * in the specified stream.
     */
    Record read(Reader stream);

    /**
     * Write an XML document representing the specified database
     * row on the specified stream.
     */
    void write(Writer stream, Record row);
}