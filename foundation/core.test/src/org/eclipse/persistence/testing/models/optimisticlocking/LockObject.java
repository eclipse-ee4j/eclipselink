/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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