/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.framework;


/**
 * Interface used to notify test execution progress.
 */
public interface TestEventListener {

    /**
     * Notify that a test has been started.
     */
    public void testFinished(junit.framework.Test test);

    /**
     * Notify that a test has been started.
     */
    public void testStarted(junit.framework.Test test);
}