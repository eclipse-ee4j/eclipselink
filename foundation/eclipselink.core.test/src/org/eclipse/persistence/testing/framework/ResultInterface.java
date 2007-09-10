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

import java.io.*;

/**
 * Must be implemented by the tests result.
 */
public interface ResultInterface {

    /**
     * Return if test has passed or not.
     */
    public boolean hasPassed();

    /**
     * Return if test should log the result.
     */
    public boolean shouldLogResult();

    /**
     * Log the results on the print stream.
     */
    public void logResult(Writer log);
}