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
package org.eclipse.persistence.internal.databaseaccess;

import java.io.Writer;
import java.io.IOException;

/**
 * INTERNAL:
 * <p>
 * <b>Purpose</b>: To provide an interface for customary parameters' types
 * used by DatabasePlatform to append parameter to DatabaseCall:
 * descendants of DatabasePlatform may create instances of implementers of this class
 * of this interface in customModifyInDatabaseCall method.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * </ul>
 */
public interface AppendCallCustomParameter {

    /**
    * INTERNAL:
    * Called only by DatabasePlatform.appendParameter()
    */
    abstract public void append(Writer writer) throws IOException;
}
