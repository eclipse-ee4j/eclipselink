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
 * Provides an API for logging messages in the EclipseLink codebase.
 * <p>
 * The main logging entry point is the {@code SessionLog} interface, which is used by the codebase to make logging calls.
 * <p>
 * Logging levels are defined in the {@code LogLevel} enum with corresponding constants in the {@code SessionLog} interface.
 * Logging categories (namespaces) are defined in the {@code LogCategory} enum with corresponding constants in the
 * {@code SessionLog} interface.
 * <p>
 * An abstract implementation of the {@code SessionLog} interface is available as the {@code AbstractSessionLog} class, with
 * log message content stored in {@code SessionLogEntry}.
 * The default implementation of the session-independent logger is available as {@code DefaultSessionLog} and may be used
 * when a {@code Session} instance is not available.
 *
 * @see org.eclipse.persistence.logging.SessionLog
 * @see org.eclipse.persistence.logging.LogLevel
 * @see org.eclipse.persistence.logging.LogCategory
 * @see org.eclipse.persistence.logging.AbstractSessionLog
 * @see org.eclipse.persistence.logging.SessionLogEntry
 * @see org.eclipse.persistence.logging.DefaultSessionLog
 */
package org.eclipse.persistence.logging;
