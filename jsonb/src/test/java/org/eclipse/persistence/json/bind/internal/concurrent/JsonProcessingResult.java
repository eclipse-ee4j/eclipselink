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

/**
 * A result produced by marshalling or unmarshalling operation called on jsonb.
 * @param <T> For marshalling its String, for unmarshalling Customer.
 */
class JsonProcessingResult<T> {
    private final T result;

    private final String dispatcherThreadName;

    private final String jobId;

    public JsonProcessingResult(T result, String dispatcherThreadName, String jobId) {
        this.result = result;
        this.dispatcherThreadName = dispatcherThreadName;
        this.jobId = jobId;
    }

    public T getResult() {
        return result;
    }

    public String getDispatcherThreadName() {
        return dispatcherThreadName;
    }

    public String getJobId() {
        return jobId;
    }
}
