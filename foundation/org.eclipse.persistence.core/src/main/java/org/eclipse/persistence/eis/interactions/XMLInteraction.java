/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.eis.interactions;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.EISAccessor;
import org.eclipse.persistence.eis.EISDOMRecord;
import org.eclipse.persistence.eis.EISDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.Vector;

/**
 * Defines the specification for a call to a JCA interaction that uses XML.
 * Builds the input and output XML records from the arguments.
 * This extends MappedInteraction to allow for mapped style of argument input and output.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLInteraction extends MappedInteraction {

    /** The root element name to use for the input DOM. */
    protected String inputRootElementName;

    /** The root element name to use for the output DOM. */
    protected String outputRootElementName;

    /**
     * Default constructor.
     */
    public XMLInteraction() {
        super();
        this.inputRootElementName = "";
        this.outputRootElementName = "";
    }

    /**
     * PUBLIC:
     * Return the root element name to use for the input DOM.
     */
    public String getInputRootElementName() {
        return inputRootElementName;
    }

    /**
     * PUBLIC:
     * Set the root element name to use for the input DOM.
     */
    public void setInputRootElementName(String inputRootElementName) {
        this.inputRootElementName = inputRootElementName;
    }

    /**
     * PUBLIC:
     * Return the root element name to use for the output DOM.
     */
    public String getOutputRootElementName() {
        return outputRootElementName;
    }

    /**
     * PUBLIC:
     * Set the root element name to use for the output DOM.
     */
    public void setOutputRootElementName(String outputRootElementName) {
        this.outputRootElementName = outputRootElementName;
    }

    /**
     * Set the default record name from the descriptor.
     */
    @Override
    public void prepare(AbstractSession session) {
        DatabaseQuery q = getQuery();
        ClassDescriptor desc = q != null ? q.getDescriptor() : null;
        if (getInputRootElementName().isEmpty()) {
            if (desc != null && desc.isEISDescriptor()) {
                EISDescriptor descriptor = (EISDescriptor) desc;
                setInputRootElementName(descriptor.getDataTypeName());
            } else {
                setInputRootElementName("input");
            }
        }
        if (getOutputRootElementName().isEmpty()) {
            if (desc != null && desc.isEISDescriptor()) {
                EISDescriptor descriptor = (EISDescriptor) desc;
                setOutputRootElementName(descriptor.getDataTypeName());
            } else {
                setInputRootElementName("output");
            }
        }
        super.prepare(session);
    }

    /**
     * Create a DOM input record for this interaction.
     * Convert the database row or arguments into an XML DOM tree.
     */
    @Override
    public jakarta.resource.cci.Record createInputRecord(EISAccessor accessor) {
        jakarta.resource.cci.Record record = accessor.getEISPlatform().createDOMRecord(getInputRecordName(), accessor);
        Element dom = createInputDOM(accessor);
        accessor.getEISPlatform().setDOMInRecord(dom, record, this, accessor);
        if (record instanceof XMLRecord) {
          ((XMLRecord) record).setSession(this.getQuery().getSession());
        }
        return record;
    }

    /**
     * Create a DOM for this interaction.
     * Convert the database row or arguments into an XML DOM tree.
     */
    public Element createInputDOM(EISAccessor accessor) {
        Element dom = null;

        // The input record can either be build from the interaction arguments,
        // or the modify row.
        if ((getInputRow() != null) && (!hasArguments())) {
            if (getInputResultPath().isEmpty()) {
                if (getInputRow() instanceof XMLRecord) {
                    dom = (Element)((XMLRecord)getInputRow()).getDOM();
                    // Rename the root element if specified to be different.
                    if (!dom.getTagName().equals(getInputRootElementName())) {
                        XMLRecord parameterRow = createXMLRecord(getInputRootElementName());
                        parameterRow.put("/" + getInputRootElementName(), getInputRow());
                        dom = (Element)parameterRow.getDOM();
                    }
                } else {
                    XMLRecord parameterRow = createXMLRecord(getInputRootElementName());
                    for (int index = 0; index < getInputRow().size(); index++) {
                        parameterRow.put(getInputRow().getFields().elementAt(index), getInputRow().getValues().elementAt(index));
                    }
                    dom = (Element)parameterRow.getDOM();
                }
            } else {
                XMLRecord parameterRow = createXMLRecord(getInputRootElementName());
                parameterRow.put(getInputResultPath(), getInputRow());
                dom = (Element)parameterRow.getDOM();
            }
        } else {
            XMLRecord parameterRow = createXMLRecord(getInputRootElementName());
            for (int index = 0; index < getArgumentNames().size(); index++) {
                String parameterName = getArgumentNames().get(index);
                Object parameter = getParameters().get(index);

                // If no arguments were passed to the call execution find the paramter from the row.
                if ((parameter == null) && (getInputRow() != null)) {
                    parameter = getInputRow().get(parameterName);
                }
                parameterRow.put(parameterName, parameter);
            }
            dom = (Element)parameterRow.getDOM();
        }
        return dom;
    }

    /**
     * Build a database row from the record returned from the interaction.
     */
    @Override
    public AbstractRecord buildRow(jakarta.resource.cci.Record record, EISAccessor accessor) {
        if (record == null) {
            return null;
        }
        AbstractRecord row = accessor.getEISPlatform().createDatabaseRowFromDOMRecord(record, this, accessor);
        if (row == null) {
            return null;
        }
        if (!getOutputResultPath().isEmpty()) {
            row = (AbstractRecord)row.get(getOutputResultPath());
            // Handle the case were the output row is mapped into a database row of values.
        } else if (hasOutputArguments()) {
            row = createXMLRecord(getOutputRootElementName());
            for (int index = 0; index < getOutputArgumentNames().size(); index++) {
                DatabaseField field = getOutputArguments().get(index);
                row.put(field, row.get(getOutputArgumentNames().get(index)));
            }
        }
        return row;
    }

    /**
     * Build a collection of database rows from the Record returned from the interaction.
     */
    @Override
    public Vector<AbstractRecord> buildRows(jakarta.resource.cci.Record record, EISAccessor accessor) {
        Vector<AbstractRecord> rows = null;
        if (record == null) {
            return new Vector<>(0);
        }
        AbstractRecord row = accessor.getEISPlatform().createDatabaseRowFromDOMRecord(record, this, accessor);
        if (!getOutputResultPath().isEmpty()) {
            @SuppressWarnings({"unchecked"})
            Vector<AbstractRecord> values = (Vector<AbstractRecord>)row.getValues(getOutputResultPath());
            if (values == null) {
                values = new Vector<>(0);
            }
            rows = values;
        } else {
            rows = new Vector<>(1);
            rows.add(row);
        }
        return rows;
    }

    /**
     * Return the string for logging purposes.
     */
    @Override
    public String getLogString(Accessor accessor) {
        StringWriter writer = new StringWriter();
        writer.write("Executing ");
        writer.write(toString());
        writer.write(System.lineSeparator());
        writer.write("\tspec => ");
        writer.write(String.valueOf(getInteractionSpec()));
        writer.write(System.lineSeparator());
        writer.write("\tproperties => ");
        writer.write(String.valueOf(getProperties()));
        writer.write(System.lineSeparator());
        writer.write("\txml => ");
        Element dom = createInputDOM((EISAccessor)accessor);
        EISDOMRecord record = new EISDOMRecord(dom);
        record.transformToWriter(writer);
        return writer.toString();
    }

    /**
     * INTERNAL:
     */
    @Override
    protected DatabaseField createField(String fieldName) {
        if (getQuery().getDescriptor() != null) {
            return getQuery().getDescriptor().buildField(fieldName);
        }
        return new XMLField(fieldName);
    }

    /**
     * INTERNAL:
     * Use the createRecord method on ObjectBuilder in case the root element is namespace qualified
     */
    protected XMLRecord createXMLRecord(String rootName) {
        XMLRecord xmlRec;
        ClassDescriptor descriptor = getQuery().getDescriptor();
        if (descriptor != null && descriptor.isEISDescriptor() && descriptor.getObjectBuilder().isXMLObjectBuilder()) {
            xmlRec = (XMLRecord)((XMLObjectBuilder) descriptor.getObjectBuilder()).createRecord(getInputRootElementName(), getQuery().getSession());
        } else {
            xmlRec = new org.eclipse.persistence.oxm.record.DOMRecord(getInputRootElementName());
            xmlRec.setSession(getQuery().getSession());
        }
        return xmlRec;
    }
}
