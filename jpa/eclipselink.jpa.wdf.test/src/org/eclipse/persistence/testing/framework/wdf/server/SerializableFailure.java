/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     SAP - initial API and implementation
package org.eclipse.persistence.testing.framework.wdf.server;

import java.io.Serializable;

import org.junit.runner.notification.Failure;

/**
 * Serializable version of org.junit.runner.notification.Failure.
 */
public class SerializableFailure implements Serializable {

    private static final long serialVersionUID = 1L;
    private final SerializableDescription description;
    private final Throwable thrownException;

    private SerializableFailure(SerializableDescription theDescription, Throwable throwable) {
        description = theDescription;
        thrownException = throwable;
    }

    /**
     * Create a SerializableFailure object from an org.junit.runner.notification.Failure object.
     * @param the failure object to be converted
     * @return a SerializableFailure object converted from an org.junit.runner.notification.Failure object
     */
    public static SerializableFailure create(Failure failure) {
        return new SerializableFailure(SerializableDescription.create(failure.getDescription()),failure.getException());
    }

    /**
     * Restore an org.junit.runner.notification.Failure object from this SerializableFailure object.
     * @return the restored org.junit.runner.notification.Failure object
     */
    public Failure restore() {
        return new Failure(description.restore(), thrownException);
    }

}
