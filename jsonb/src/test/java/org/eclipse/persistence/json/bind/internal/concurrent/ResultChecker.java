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

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Checks result of marshalling / unmarshalling operation.
 */
abstract class ResultChecker<T> implements Runnable {

    private final CompletionService<JsonProcessingResult<T>> completionService;

    public ResultChecker(CompletionService<JsonProcessingResult<T>> completionService) {
        this.completionService = completionService;
    }

    /**
     * Polls Callable results from CompletionService result queue and checks validity.
     */
    @Override
    public void run() {
        Future<JsonProcessingResult<T>> resultFuture;
        try {
            while ((resultFuture = completionService.poll(500, TimeUnit.MILLISECONDS)) != null) {
                checkResult(resultFuture.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void checkResult(JsonProcessingResult<T> result);
}
