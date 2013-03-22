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
import javax.resource.*;
import javax.resource.cci.*;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.eis.*;

/**
 * Defines the specification for a call to a JCA interaction using Mapped records.
 * Builds the input and output records from the arguments.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class MappedInteraction extends EISInteraction {
    protected String inputResultPath;
    protected Vector argumentNames;

    /**
     * Default constructor.
     */
    public MappedInteraction() {
        super();
        this.inputResultPath = "";
    }

    /**
     * PUBLIC:
     * Define the argument to the interaction and the field/argument name to be substitute for it.
     * This is only required if an input row is not used.
     * The parameterAndArgumentFieldName is the name of the input record argument,
     * and is the field or argument name to be used to pass to the interaction.
     * These names are assumed to be the same, if not this method can be called with two arguments.
     */
    public void addArgument(String parameterAndArgumentFieldName) {
        addArgument(parameterAndArgumentFieldName, parameterAndArgumentFieldName);
    }

    /**
     * PUBLIC:
     * Define the argument to the interaction and the field/argument name to be substitute for it.
     * This is only required if an input row is not used.
     * The parameterName is the name of the input record argument.
     * The argumentFieldName is the field or argument name to be used to pass to the interaction.
     * If these names are the same (as they normally are) this method can be called with a single argument.
     */
    public void addArgument(String parameterName, String argumentFieldName) {
        getArgumentNames().addElement(parameterName);
        getArguments().addElement(new DatabaseField(argumentFieldName));
    }

    /**
     * PUBLIC:
     * Define the argument to the interaction and the value name to be input for it.
     * This is only required if an input row is not used.
     * The parameterName is the name of the input record argument.
     * The argumentValue is the value of the input record argument.
     */
    public void addArgumentValue(String parameterName, Object argumentValue) {
        getArgumentNames().addElement(parameterName);
        getArguments().addElement(argumentValue);
    }

    /**
     * PUBLIC:
     * The input result path defines the root key for the MappedRecord that
     * the interaction argument is nested into.
     * This is required for write interaction that take the row build from the mapped object
     * and need the input to contain that row record as part of the input, instead of the entire input.
     */
    public String getInputResultPath() {
        return inputResultPath;
    }

    /**
     * PUBLIC:
     * The input result path defines the root key for the MappedRecord that
     * the interaction argument is nested into.
     * This is required for write interaction that take the row build from the mapped object
     * and need the input to contain that row record as part of the input, instead of the entire input.
     */
    public void setInputResultPath(String inputResultPath) {
        this.inputResultPath = inputResultPath;
    }

    /**
     * The argument names for the input record.
     */
    public Vector getArgumentNames() {
        // This is lazy initialized to conserv space on calls that have no parameters.
        if (argumentNames == null) {
            argumentNames = new Vector();
        }
        return argumentNames;
    }

    /**
     * INTERNAL:
     * The argument names for the input record.
     */
    public void setArgumentNames(Vector argumentNames) {
        this.argumentNames = argumentNames;
    }

    /**
     * Create a mapped input record for this interaction.
     * Populate the data into the record from this interaction's arguments.
     */
    public Record createInputRecord(EISAccessor accessor) {
        try {
            MappedRecord record = null;

            // The input record can either be build from the interaction arguments,
            // or the modify row.
            if ((getInputRow() != null) && (!hasArguments())) {
                if (getInputResultPath().length() == 0) {
                    record = (MappedRecord)createRecordElement(getInputRecordName(), getInputRow(), accessor);
                } else {
                    record = accessor.getRecordFactory().createMappedRecord(getInputRecordName());
                    Object nestedRecord = createRecordElement(getInputResultPath(), getInputRow(), accessor);
                    accessor.getEISPlatform().setValueInRecord(getInputResultPath(), nestedRecord, record, accessor);
                }
            } else {
                record = accessor.getRecordFactory().createMappedRecord(getInputRecordName());
                for (int index = 0; index < getArgumentNames().size(); index++) {
                    String parameterName = (String)getArgumentNames().get(index);
                    Object parameter = getParameters().get(index);

                    // If no arguments were passed to the call execution find the parameter from the row.
                    if ((parameter == null) && (getInputRow() != null)) {
                        parameter = getInputRow().get(parameterName);
                    }

                    // Allow for conversion of nested rows into nested records.
                    parameter = createRecordElement(parameterName, parameter, accessor);
                    // Allow for the platform to perform any platform specific record access.
                    accessor.getEISPlatform().setValueInRecord(parameterName, parameter, record, accessor);
                }
            }
            return record;
        } catch (ResourceException exception) {
            throw EISException.resourceException(exception, accessor, null);
        }
    }

    /**
     * Create a mapped input record for this interaction.
     * Populate the data into the record from this interaction's translation row.
     */
    public Record createTranslationRecord(AbstractRecord transaltionRow, EISAccessor accessor) {
        return (MappedRecord)createRecordElement(getInputRecordName(), transaltionRow, accessor);
    }

    /**
     * Build a database row from the record returned from the interaction.
     */
    public AbstractRecord buildRow(Record record, EISAccessor accessor) {
        if (record == null) {
            return  null;
        }
    	AbstractRecord row = null;

        if (record instanceof IndexedRecord) {
            IndexedRecord indexedRecord = (IndexedRecord)record;
            if (indexedRecord.isEmpty()) {
                return null;
            }
            if (indexedRecord.get(0) instanceof Record) {
                return buildRow((Record)indexedRecord.get(0), accessor);
            }
        }
        // If not a mapped record then just put it as a result value in the row.
        if (!(record instanceof MappedRecord)) {
            row = new DatabaseRecord(1);
            row.put(getOutputResultPath(), record);
            return row;
        }
        MappedRecord mappedRecord = (MappedRecord)record;

        // The desired result is either the entire output record,
        // or a translation of the output with the output arguments.
        if (hasOutputArguments()) {
            row = new DatabaseRecord(getOutputArgumentNames().size());
            for (int index = 0; index < getOutputArgumentNames().size(); index++) {
                DatabaseField field = (DatabaseField)getOutputArguments().get(index);
                row.put(field, mappedRecord.get(getOutputArgumentNames().get(index)));
            }
            return row;
        } else if (getOutputResultPath().length() > 0) {
            // Extract the desired nested record from the output.
            mappedRecord = (MappedRecord)mappedRecord.get(getOutputResultPath());
        }

        // Wrapped the record in a database to avoid lossing any information in conversion to database row,
        // also gets around problem of some adatpers not supporting keySet or entrySet.
        row = new EISMappedRecord(mappedRecord, accessor);
        return row;
    }
}
