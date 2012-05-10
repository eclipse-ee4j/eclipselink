/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.eis.aq;

import oracle.AQ.*;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.eis.adapters.aq.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Testing reading with a dequeue timeout.
 */
public class ReadTimeoutTest extends TestCase {
    public ReadTimeoutTest() {
        setName("ReadTimeoutTest");
        setDescription("Testing reading with a timeout.");
    }

    public void test() throws Exception {
        XMLInteraction interaction = new XMLInteraction();
        AQDequeueOption options = new AQDequeueOption();
        options.setWaitTime(1);
        interaction.setProperty(AQPlatform.QUEUE_OPERATION, AQPlatform.DEQUEUE);
        interaction.setProperty(AQPlatform.DEQUEUE_OPTIONS, options);
        boolean timeout = false;
        try {
            getSession().readObject(org.eclipse.persistence.testing.models.order.Order.class, interaction);
        } catch (EISException exception) {
            timeout = true;
            if (exception.getMessage().indexOf("timeout") == -1) {
                throw exception;
            }
        }
        if (!timeout) {
            throw new TestErrorException("Timeout exception did not occur, was a message in the queue.");
        }
    }
}
