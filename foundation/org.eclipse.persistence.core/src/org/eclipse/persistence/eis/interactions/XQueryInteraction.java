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
package org.eclipse.persistence.eis.interactions;

import java.util.*;
import java.io.*;
import org.w3c.dom.Element;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.QueryStringCall;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.EmptyRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.eis.*;

/**
 * Defines the specification for a call to a JCA interaction that uses XQuery.
 * Translates the XQuery from the query arguments.
 * Builds the input and output XML records.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XQueryInteraction extends XMLInteraction implements QueryStringCall {
    protected String xQueryString;

    /**
     * Default constructor.
     */
    public XQueryInteraction() {
        super();
        this.xQueryString = "";
    }

    /**
     * Construct the interaction with the XQuery string.
     */
    public XQueryInteraction(String xQueryString) {
        super();
        this.xQueryString = xQueryString;
    }

    /**
     * PUBLIC:
     * Return the XQuery string.
     */
    public String getXQueryString() {
        return xQueryString;
    }

    /**
     * PUBLIC:
     * Set the XQuery string.
     */
    public void setXQueryString(String xQueryString) {
        this.xQueryString = xQueryString;
    }

    /**
     * INTERNAL:
     * Return the query string.
     */
    public String getQueryString() {
        return getXQueryString();
    }

    /**
     * INTERNAL:
     * Set the query string.
     */
    public void setQueryString(String queryString) {
        setXQueryString(queryString);
    }

    /**
     * INTERNAL:
     * Allow the call to translate the XQuery arguments.
     */
    public void translate(AbstractRecord translationRow, AbstractRecord modifyRow, AbstractSession session) {
        // may need to set the session on the translation row
        if (translationRow != EmptyRecord.getEmptyRecord() && getQuery() != null && getQuery().getDescriptor() != null) {
            ((XMLRecord) translationRow).setSession(session);
        }
        setInputRow(modifyRow);
        translateQueryString(translationRow, modifyRow, session);
    }

    /**
     * Create a DOM for this interaction.
     * Convert the database row or arguments into an XML DOM tree.
     * Handles arguments different as the XQuery and input can both have parameters.
     */
    public Element createInputDOM(EISAccessor accessor) {
        // The input record can either be build from the interaction arguments,
        // or the modify row.
        if ((getInputRow() != null) && (!hasArguments())) {
            return super.createInputDOM(accessor);
        }
        XMLRecord parameterRow = createXMLRecord(getInputRootElementName());
        for (int index = 0; index < getArgumentNames().size(); index++) {
            String parameterName = (String)getArgumentNames().get(index);
            Object parameter = getInputRow().get(parameterName);
            parameterRow.put(parameterName, parameter);
        }
        return (Element)parameterRow.getDOM();
    }

    /**
     * INTERNAL:
     * Translate the custom query markers.
     */
    public void prepare(AbstractSession session) {
        if (isPrepared()) {
            return;
        }
        super.prepare(session);
        translateCustomQuery();
        setIsPrepared(true);
    }

    /**
     * Return the string for logging purposes.
     */
    public String getLogString(Accessor accessor) {
        StringWriter writer = new StringWriter();
        writer.write("Executing ");
        writer.write(toString());
        writer.write(Helper.cr());
        writer.write("\tspec => ");
        writer.write(String.valueOf(getInteractionSpec()));
        writer.write(Helper.cr());
        writer.write("\txQuery => ");
        writer.write(getXQueryString());
        writer.write(Helper.cr());
        writer.write("\tinput => [");
        if (hasParameters()) {
            for (Iterator iterator = getParameters().iterator(); iterator.hasNext();) {
                Object parameter = iterator.next();
                writer.write(String.valueOf(parameter));
                if (iterator.hasNext()) {
                    writer.write(", ");
                } else {
                    writer.write("]");
                }
            }
        } else {
            writer.write(String.valueOf(getInputRow()));
            writer.write("]");
        }
        return writer.toString();
    }

    /**
     * INTERNAL:
     * Return the character to use for the argument marker.
     * ? is used in SQL, however other query languages such as XQuery need to use other markers.
     */
    protected char argumentMarker() {
        return '#';
    }

    /**
     * INTERNAL:
     * Return the characters that represent non-arguments names.
     */
    protected String whitespace() {
        return ",;\"'< \n\t";
    }

    public boolean isQueryStringCall() {
        return true;
    }
}
