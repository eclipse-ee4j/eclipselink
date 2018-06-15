/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - initial implementation
package org.eclipse.persistence.jpa.rs.util;

import java.util.Arrays;

public class MethodExitLogData {
    private Object[] result;

    /**
     * Instantiates a new method exit log data.
     *
     * @param result the result
     */
    public MethodExitLogData(Object[] result) {
        super();
        if (result != null) {
            this.result = Arrays.copyOf(result, result.length);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                Object object = result[i];
                if (i == 0) {
                    message.append((object != null) ? (object.toString()) : "null");
                } else {
                    message.append((object != null) ? (" " + object.toString()) : " null");
                }
            }
        }
        return message.toString().trim();
    }
}
