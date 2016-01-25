/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.concurrent;

import org.eclipse.persistence.json.bind.defaultmapping.specific.model.Customer;

import javax.json.bind.Jsonb;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Runs Jsonb unmarshaller, returns result.
 */
class UnmarshallerTask implements Callable<JsonProcessingResult<Customer>> {

    private final String json;

    private final Jsonb jsonb;

    private final String jobId;

    private final CountDownLatch latch;

    public UnmarshallerTask(CountDownLatch latch, String json, Jsonb jsonb, String jobId) {
        this.json = json;
        this.jsonb = jsonb;
        this.jobId = jobId;
        this.latch = latch;
    }

    @Override
    public JsonProcessingResult<Customer> call() throws Exception {
        latch.await();
        //unmarshalling will fail if JsonbConfig in ThreadLocal JsonbContext will not match expected json
        Customer customer = jsonb.fromJson(json, Customer.class);
        return new JsonProcessingResult<>(customer, Thread.currentThread().getName(), jobId);
    }
}
