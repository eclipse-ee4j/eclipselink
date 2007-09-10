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
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.localization.TraceLocalization;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.sessions.Record;

/**
 * This class checks the XML data store for the existence of the
 * XML document.
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class XMLDoesExistCall extends XMLCall {

    /**
     * Default constructor.
     */
    public XMLDoesExistCall() {
        super();
    }

    /**
     * If the data exists, return the row, otherwise return null.
     */
    public Object execute(AbstractRecord translationRow, Accessor accessor) throws XMLDataStoreException {
        Reader stream = this.getStreamPolicy().getExistenceCheckStream(this.getRootElementName(), translationRow, this.getOrderedPrimaryKeyElements(), accessor);
        if (stream == null) {
            return null;
        }

        Record row = this.getXMLTranslator().read(stream);
        return this.getFieldTranslator().translateForRead(row);
    }

    /**
     * Append a string describing the call to the specified writer.
     */
    protected void writeLogDescription(PrintWriter writer) {
        writer.write(TraceLocalization.buildMessage("XML_existence_check", (Object[])null));
    }
}