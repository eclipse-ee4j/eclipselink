/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.logging;

import java.util.logging.Level;

/**
 * TopLink's own logging properties that will be formatted by a TopLink Formatter.
 *
 * @deprecated Use {@link org.eclipse.persistence.logging.jul.EclipseLinkLogRecord} instead
 */
@Deprecated(forRemoval=true, since="4.0.9")
public class EclipseLinkLogRecord extends org.eclipse.persistence.logging.jul.EclipseLinkLogRecord {

    /**
     * Creates a new instance of TopLink's own logging properties.
     *
     * @param level the log request level value
     * @param msg the message to be logged
     */
    public EclipseLinkLogRecord(Level level, String msg) {
        super(level, msg);
    }

}
