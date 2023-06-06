/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.eis.adapters.aq;

import jakarta.resource.*;
import jakarta.resource.cci.*;
import org.w3c.dom.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.internal.eis.adapters.aq.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import oracle.jakarta.AQ.AQDequeueOption;
import oracle.jakarta.AQ.AQEnqueueOption;

/**
 * Platform for Oracle AQ JCA adapter.
 * This allows for Oracle AQ to be treated as a data source and accessed through EclipseLink.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQPlatform extends EISPlatform {

    /** AQ interaction spec properties. */
    public static String QUEUE = "queue";
    public static String SCHEMA = "schema";
    public static String QUEUE_OPERATION = "operation";
    public static String ENQUEUE = "enqueue";
    public static String DEQUEUE = "dequeue";
    public static String ENQUEUE_OPTIONS = "enqueue-options";
    public static String DEQUEUE_OPTIONS = "dequeue-options";

    /**
     * Default constructor.
     */
    public AQPlatform() {
        super();
        setShouldConvertDataToStrings(true);
        setIsMappedRecordSupported(false);
        setIsIndexedRecordSupported(true);
        setIsDOMRecordSupported(true);
        setSupportsLocalTransactions(true);
        setRequiresAutoCommit(true);
    }

    /**
     * Allow the platform to build the interaction spec based on properties defined in the interaction.
     */
    @Override
    public InteractionSpec buildInteractionSpec(EISInteraction interaction) {
        InteractionSpec spec = interaction.getInteractionSpec();
        if (spec == null) {
            AQInteractionSpec aqSpec;
            if (interaction.getProperty(QUEUE_OPERATION) == null) {
                throw EISException.resourceException(new ResourceException(QUEUE_OPERATION + " properties must be set."), interaction, null, null);
            }
            if (interaction.getProperty(QUEUE_OPERATION).equals(ENQUEUE)) {
                aqSpec = new AQEnqueueInteractionSpec();
                AQEnqueueOption options = (AQEnqueueOption)interaction.getProperty(ENQUEUE_OPTIONS);
                if (options != null) {
                    ((AQEnqueueInteractionSpec)aqSpec).setOptions(options);
                }
            } else {
                aqSpec = new AQDequeueInteractionSpec();
                AQDequeueOption options = (AQDequeueOption)interaction.getProperty(DEQUEUE_OPTIONS);
                if (options != null) {
                    ((AQDequeueInteractionSpec)aqSpec).setOptions(options);
                }
            }
            aqSpec.setQueue((String)interaction.getProperty(QUEUE));
            aqSpec.setSchema((String)interaction.getProperty(SCHEMA));
            spec = aqSpec;
        }
        return spec;
    }

    /**
     * Allow the platform to handle the creation of the DOM record.
     * Create an indexed record (mapped are not supported).
     */
    @Override
    public jakarta.resource.cci.Record createDOMRecord(String recordName, EISAccessor accessor) {
        try {
            return accessor.getRecordFactory().createIndexedRecord(recordName);
        } catch (ResourceException exception) {
            throw EISException.resourceException(exception, accessor, null);
        }
    }

    /**
     * Stores the XML DOM value into the record.
     * Convert the DOM to a RAW and add to the indexed record.
     */
    @Override
    public void setDOMInRecord(Element dom, jakarta.resource.cci.Record record, EISInteraction call, EISAccessor accessor) {
        IndexedRecord indexedRecord = (IndexedRecord)record;
        indexedRecord.add(new org.eclipse.persistence.oxm.record.DOMRecord(dom).transformToXML());
    }

    /**
     * Allow the platform to handle the creation of the Record for the DOM record.
     * Translate the indexed record RAW bytes into a DOM record.
     */
    @Override
    public AbstractRecord createDatabaseRowFromDOMRecord(jakarta.resource.cci.Record record, EISInteraction call, EISAccessor accessor) {
        EISDOMRecord domRecord = new EISDOMRecord();
        IndexedRecord indexedRecord = (IndexedRecord)record;
        if (indexedRecord.size() == 0) {
            return null;
        }
        byte[] bytes = (byte[])indexedRecord.get(0);
        domRecord.transformFromXML(new String(bytes));
        return domRecord;
    }
}
