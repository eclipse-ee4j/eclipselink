/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
