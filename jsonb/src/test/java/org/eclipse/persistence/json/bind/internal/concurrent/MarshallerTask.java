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
 * Runs JSON marshalling, returns Json result.
 */
class MarshallerTask implements Callable<JsonProcessingResult<MarshallerTaskResult>> {

    private final Jsonb jsonb;
    private final String jobId;
    private final MultiTenancyTest.ConfigurationType configurationType;
    private final CountDownLatch latch;
    private final Customer customer;

    public MarshallerTask(CountDownLatch latch, MultiTenancyTest.ConfigurationType jsonbConfiguration, String jobId, Customer customer) {
        this.jsonb = jsonbConfiguration.getJsonbInstance();
        this.jobId = jobId;
        this.configurationType = jsonbConfiguration;
        this.latch = latch;
        this.customer = customer;
    }

    @Override
    public JsonProcessingResult<MarshallerTaskResult> call() throws Exception {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        //marshalling will produce wrong json result if JsonbConfig in ThreadLocal JsonbContext will not match expected result
        String json = jsonb.toJson(customer);
        return new JsonProcessingResult<>(new MarshallerTaskResult(json, configurationType), Thread.currentThread().getName(), jobId);
    }
}
