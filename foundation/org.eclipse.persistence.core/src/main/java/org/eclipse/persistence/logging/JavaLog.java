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
 * This is a wrapper class for java.util.logging. It is used when messages need to
 * be logged through {@code java.util.logging}.
 *
 * @deprecated Use {@link org.eclipse.persistence.logging.jul.JavaLog} instead
 */
@Deprecated(forRemoval=true, since="4.0.9")
public class JavaLog extends org.eclipse.persistence.logging.jul.JavaLog {

    /**
     * Creates a new instance of wrapper class for {@code java.util.logging}.
     */
    public JavaLog() {
        super();
    }

}
