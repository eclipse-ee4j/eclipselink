/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial implementation 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.util;

public class MethodExitLogData {
    private Object[] result;

    /**
     * Instantiates a new method exit log data.
     *
     * @param result the result
     */
    public MethodExitLogData(Object[] result) {
        super();
        this.result = result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer message = new StringBuffer();
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
