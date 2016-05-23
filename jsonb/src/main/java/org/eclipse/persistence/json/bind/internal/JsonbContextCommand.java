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

package org.eclipse.persistence.json.bind.internal;

import java.util.Objects;

/**
 * Manages setting and removing of jsonb context to/from thread local.
 *
 * @author Roman Grigoriadi
 */
abstract class JsonbContextCommand<T> {

    /**
     * Set JsonbContext to thread local, call business and unset thereafter.
     *
     * @param context jsonb context not null
     */
    public final T execute(ProcessingContext context) {
        Objects.nonNull(context);
        try {
            ProcessingContext.setInstance(context);
            return doInProcessingContext();
        } finally {
            ProcessingContext.removeInstance();
        }
    }

    /**
     * Implement you business work, which will have ProcessingContext available in thread local here.
     */
    protected abstract T doInProcessingContext();
}
