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

import oracle.jakarta.AQ.AQDequeueOption;

/**
 * Interaction spec for AQ JCA adapter.
 * Specifies a dequeue interaction for a queue.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class AQDequeueInteractionSpec extends AQInteractionSpec {
    protected AQDequeueOption options;

    /**
     * Default constructor.
     */
    public AQDequeueInteractionSpec() {
    }

    /**
     * Return the AQ specific dequeue options.
     */
    public AQDequeueOption getOptions() {
        return options;
    }

    /**
     * Set the AQ specific dequeue options.
     */
    public void setOptions(AQDequeueOption options) {
        this.options = options;
    }
}
