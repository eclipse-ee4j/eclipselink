/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.adapters.aq;

import oracle.AQ.*;

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
