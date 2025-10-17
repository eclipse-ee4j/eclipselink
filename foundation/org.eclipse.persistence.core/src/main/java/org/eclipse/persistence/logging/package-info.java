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
 * Provides a comprehensive logging API for the EclipseLink.
 * <p>
 * The main logging entry point is the {@linkplain org.eclipse.persistence.logging.SessionLog} interface, which is used
 * by the codebase to make logging calls.
 * <p>
 * Logging levels are defined in the {@code LogLevel} enum with corresponding constants in the {@code SessionLog} interface.
 * Logging categories (namespaces) are defined in the {@code LogCategory} enum with corresponding constants in the
 * {@code SessionLog} interface.
 * <p>
 * An abstract implementation of the {@linkplain org.eclipse.persistence.logging.SessionLog} interface is available
 * as the {@linkplain org.eclipse.persistence.logging.AbstractSessionLog} class, with log message content stored
 * in {@linkplain org.eclipse.persistence.logging.SessionLogEntry}.
 * The default implementation of the session-independent logger is available
 * as {@linkplain org.eclipse.persistence.logging.DefaultSessionLog}.
 */
package org.eclipse.persistence.logging;
