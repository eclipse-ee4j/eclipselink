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
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * XMLDataDeleteCall simply deletes the appropriate
 * XML document.
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLDataDeleteCall extends XMLDataCall {

    /**
     * Default constructor.
     */
    public XMLDataDeleteCall() {
        super();
    }

    /**
     * Delete the appropriate data. The translation row
     * holds the primary key for the data.
     * Return a delete count.
     */
    public Object execute(AbstractRecord translationRow, Accessor accessor) throws XMLDataStoreException {
        Record row = this.convertToFullyQualifiedRow(translationRow);
        return this.getStreamPolicy().deleteStream(this.getRootElementName(), translationRow, this.getOrderedPrimaryKeyElements(), accessor);
    }

    /**
     * Append a string describing the call to the specified writer.
     */
    protected void writeLogDescription(PrintWriter writer) {
        writer.write(TraceLocalization.buildMessage("XML_data_delete", (Object[])null));
    }
}