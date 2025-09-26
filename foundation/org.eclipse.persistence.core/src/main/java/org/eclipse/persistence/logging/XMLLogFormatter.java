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

/**
 * Format a TopLink LogRecord into a standard XML format.
 *
 * @deprecated Use {@link org.eclipse.persistence.logging.jul.XMLLogFormatter} instead
 */
@Deprecated(forRemoval=true, since="4.0.9")
public class XMLLogFormatter extends org.eclipse.persistence.logging.jul.XMLLogFormatter {

    /**
     * Creates an instance of TopLink LogRecord XML formatter.
     */
    public XMLLogFormatter() {
        super();
    }

}
