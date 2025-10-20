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

/*
 * Provides {@link java.util.logging} specific extension of the EclipseLink logging.
 * <p>
 * {@linkplain org.eclipse.persistence.logging.jul.JavaLog} class extends {@linkplain org.eclipse.persistence.logging.AbstractSessionLog}
 * and implements JUL specific features.
 * {@linkplain org.eclipse.persistence.logging.jul.EclipseLinkLogRecord} is JUL specific alternative
 * of {@linkplain org.eclipse.persistence.logging.SessionLogEntry} to which this instance is being translated as part
 * of {@linkplain org.eclipse.persistence.logging.jul.JavaLog#log(org.eclipse.persistence.logging.SessionLogEntry)} execution.
 * <p>
 * JUL specific logging defines two logging name spaces:<ul>
 * <li><b>{@code org.eclipse.persistence.default}</b> for log output without {@code Session} context.</li>
 * <li><b>{@code org.eclipse.persistence.session.<name>}</b> for {@code Session} related log output. Value of {@code <name>}
 *     is the name of the current session.</li>
 * </ul>
 * Name of the {@linkplain org.eclipse.persistence.logging.LogCategory} may be appended to the name space as well when present.
 */
/**
 * Provides {@link java.util.logging} specific extensions for EclipseLink logging.
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
