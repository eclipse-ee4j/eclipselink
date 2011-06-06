/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

/**
 * Test direct interactions through the AQ Java driver. Requires AQ installed on this database and
 * an aquser defined.
 */
public class JavaDirectInteractionTest extends JavaDirectConnectTest {
    public JavaDirectInteractionTest() {
        this.setDescription("Test direct interactions through the AQ Java driver.");
    }

    public void test() throws Exception {
        AQSession session = connect();

        AQQueue queue = session.getQueue("aquser", "raw_order_queue");
        getSession().logMessage(queue.toString());

        //QueueReceiver receiver = session.createReceiver(queue);
        //getSession().logMessage(receiver.toString());
        AQMessage message = queue.createMessage();
        getSession().logMessage(message.toString());
        AQRawPayload payload = message.getRawPayload();
        byte[] bytes = "hello".getBytes();
        payload.setStream(bytes, bytes.length);
        AQEnqueueOption enqueueOption = new AQEnqueueOption();
        queue.enqueue(enqueueOption, message);
        this.connection.commit();

        AQDequeueOption dequeueOption = new AQDequeueOption();
        message = queue.dequeue(dequeueOption);
        getSession().logMessage(message.toString());
        getSession().logMessage(new String(message.getRawPayload().getBytes()));
        connection.commit();

        session.close();
        this.connection.close();
    }
}
