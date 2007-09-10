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

import java.util.*;
import java.io.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

/**
 * XMLWriteCall simply adds the assumption that the
 * query is a ModifyQuery.
 *
 * @see org.eclipse.persistence.queries.ModifyQuery
 *
 * @author Big Country
 * @since TOPLink/Java 3.0
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public abstract class XMLWriteCall extends XMLCall {

    /**
     * Default constructor.
     */
    public XMLWriteCall() {
        super();
    }

    /**
     * Write the necessary data. The translation row
     * holds the primary key for the data. But the modify row holds
     * all the data to be written.
     * Return a modify count.
     */
    public Object execute(AbstractRecord translationRow, Accessor accessor) throws XMLDataStoreException {
        Writer stream = this.getWriteStream(accessor, this.getRootElementName(), translationRow, this.getOrderedPrimaryKeyElements());
        AbstractRecord row = this.getModifyRow(accessor);
        row = (AbstractRecord)this.getFieldTranslator().translateForWrite(row);
        try {
            this.getXMLTranslator().write(stream, row);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                throw XMLDataStoreException.unableToCloseWriteStream(this, e);
            }
        }
        return new Integer(1);
    }

    /**
     * Return the row appropriate for logging.
     */
    protected AbstractRecord getLogRow() {
        return this.getModifyRow();
    }

    /**
     * Convenience method.
     * Return the modify row associated with the call.
     * This row contains all the data to be written to the datastore.
     */
    private AbstractRecord getModifyRow() {
        return ((ModifyQuery)this.getQuery()).getModifyRow();
    }

    /**
     * Convenience method.
     * Return the modify row associated with the call.
     * This row contains all the data to be written to the datastore.
     */
    protected AbstractRecord getModifyRow(Accessor accessor) {
        return (AbstractRecord)((XMLAccessor)accessor).convert(this.getModifyRow(), this.getSession());
    }

    /**
     * Return the appropriate write stream.
     */
    protected abstract Writer getWriteStream(Accessor accessor, String rootElementName, AbstractRecord translationRow, Vector orderedPrimaryKeyElements) throws XMLDataStoreException;
}