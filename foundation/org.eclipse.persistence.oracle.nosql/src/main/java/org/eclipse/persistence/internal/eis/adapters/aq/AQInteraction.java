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
package org.eclipse.persistence.internal.eis.adapters.aq;

import jakarta.resource.*;
import jakarta.resource.cci.*;
import oracle.jakarta.AQ.AQDequeueOption;
import oracle.jakarta.AQ.AQEnqueueOption;
import oracle.jakarta.AQ.AQMessage;
import oracle.jakarta.AQ.AQQueue;
import oracle.jakarta.AQ.AQRawPayload;
import org.eclipse.persistence.eis.EISException;

/**
 * Interaction to Oracle AQ JCA adapter.
 * Executes the interaction spec to enqueue or dequeue a message.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQInteraction implements Interaction {

    /** Store the connection the interaction was created from. */
    protected AQConnection connection;

    /**
     * Default constructor.
     */
    public AQInteraction(AQConnection connection) {
        this.connection = connection;
    }

    @Override
    public void clearWarnings() {
    }

    @Override
    public void close() {
    }

    /**
     * Execute the interaction spec.
     * The spec is either an enqueue or dequeue interaction.
     */
    @Override
    public jakarta.resource.cci.Record execute(InteractionSpec spec, jakarta.resource.cci.Record input) throws ResourceException {
        AQRecord record = new AQRecord();
        execute(spec, input, record);
        return record;
    }

    /**
     * Execute the interaction and set the output into the output record.
     * The spec is either an enqueue or dequeue interaction.
     * Only raw messages are supported.
     */
    @Override
    public boolean execute(InteractionSpec spec, jakarta.resource.cci.Record input, jakarta.resource.cci.Record output) throws ResourceException {
        if (!(spec instanceof AQInteractionSpec)) {
            throw EISException.invalidAQInteractionSpecType();
        }
        if ((!(input instanceof AQRecord)) || (!(output instanceof AQRecord))) {
            throw EISException.invalidAQRecordType();
        }

        // Use auto-commit if not in a transaction
        boolean autocommit = false;
        if (!connection.getAQTransaction().isInTransaction()) {
            autocommit = true;
            connection.getAQTransaction().begin();
        }
        try {
            if (spec instanceof AQEnqueueInteractionSpec) {
                executeEnqueueInteraction((AQEnqueueInteractionSpec)spec, (AQRecord)input, (AQRecord)output);
            } else if (spec instanceof AQDequeueInteractionSpec) {
                executeDequeueInteraction((AQDequeueInteractionSpec)spec, (AQRecord)input, (AQRecord)output);
            }
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        } finally {
            if (autocommit) {
                connection.getAQTransaction().commit();
            }
        }
        return true;
    }

    /**
     * Execute the enqueue interaction.
     * Only raw messages are supported.
     */
    protected void executeEnqueueInteraction(AQEnqueueInteractionSpec spec, AQRecord input, AQRecord output) throws ResourceException {
        try {
            AQQueue queue = this.connection.getSession().getQueue(spec.getSchema(), spec.getQueue());
            AQMessage message = queue.createMessage();
            AQRawPayload payload = message.getRawPayload();
            if (input.size() != 1) {
                throw EISException.invalidAQInput();
            }
            byte[] bytes = null;
            if (input.get(0) instanceof String) {
                bytes = ((String)input.get(0)).getBytes();
            } else if (input.get(0) instanceof byte[]) {
                bytes = (byte[])input.get(0);
            } else {
                throw EISException.invalidAQInput();
            }
            payload.setStream(bytes, bytes.length);
            AQEnqueueOption enqueueOption = spec.getOptions();
            if (enqueueOption == null) {
                enqueueOption = new AQEnqueueOption();
            }
            queue.enqueue(enqueueOption, message);
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    /**
     * Execute the dequeue interaction.
     * Only raw messages are supported.
     */
    protected void executeDequeueInteraction(AQDequeueInteractionSpec spec, AQRecord input, AQRecord output) throws ResourceException {
        try {
            AQQueue queue = this.connection.getSession().getQueue(spec.getSchema(), spec.getQueue());
            AQDequeueOption dequeueOption = spec.getOptions();
            if (dequeueOption == null) {
                dequeueOption = new AQDequeueOption();
            }
            AQMessage message = queue.dequeue(dequeueOption);
            output.add(message.getRawPayload().getBytes());
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public ResourceWarning getWarnings() {
        return null;
    }
}
