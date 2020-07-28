/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.optimisticlocking;


/**
 * <b>Purpose</b>: <p>
 * <b>Description</b>: <p>
 * <b>Responsibilities</b>:<ul>
 * <li>
 * </ul>
 * @since TopLink 2.0
 * @author Peter Krogh
 */
public abstract class LockObject {
    public int id;
    public String value;

    /**
     * LockObject constructor comment.
     */
    public LockObject() {
        super();
    }

    abstract public void verify(org.eclipse.persistence.testing.framework.TestCase testCase);
}
