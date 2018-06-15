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
package org.eclipse.persistence.internal.platform.database.oracle;

import oracle.sql.*;

/**
 * Oracle TIMESTAMP types are defined here to make sure deployment xml has no dependency
 * on jdbc jar.
 */
public class TIMESTAMPTypes {
    public static final Class TIMESTAMP_CLASS = TIMESTAMP.class;
    public static final Class TIMESTAMPLTZ_CLASS = TIMESTAMPLTZ.class;
    public static final Class TIMESTAMPTZ_CLASS = TIMESTAMPTZ.class;
}
