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

/**
 * Provides {@linkplain java.util.logging} specific extensions for EclipseLink logging.
 * <p>
 * The {@linkplain org.eclipse.persistence.logging.jul.JavaLog} class extends {@linkplain org.eclipse.persistence.logging.AbstractSessionLog},
 * implementing features specific to Java Util Logging (JUL).
 * <p>
 * JUL-specific logging in EclipseLink defines two primary namespaces:
 * <ul>
 * <li><i>{@code org.eclipse.persistence.default}</i> – used for log output that does not have a {@code Session} context.</li>
 * <li><i>{@code org.eclipse.persistence.session.<name>}</i> – used for {@code Session}-related log output, where
 *     {@code <name>} is the name of the session.</li>
 * </ul>
 * The name of the {@linkplain org.eclipse.persistence.logging.LogCategory}, when present,
 * may be appended to the namespace to further qualify the log output.
 */
package org.eclipse.persistence.logging.jul;
