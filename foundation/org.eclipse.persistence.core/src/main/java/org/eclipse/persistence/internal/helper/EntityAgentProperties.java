/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.internal.helper;

/**
 * INTERNAL: Execution properties shared by the EntityAgent and core query machinery.
 */
public final class EntityAgentProperties {
    public static final String ENTITY_AGENT = "eclipselink.entity-agent";
    public static final String OPERATION = "eclipselink.entity-agent.operation";
    public static final String INTERNAL_READ = "eclipselink.entity-agent.internal-read";
    public static final String READ_CONTEXT = "eclipselink.entity-agent.read-context";
    public static final String MAPPING_LOCK = "eclipselink.entity-agent.mapping-lock";
    public static final String REFRESH_RESULTS = "eclipselink.entity-agent.refresh-results";
    public static final String SUPPRESSED_COPY_SOURCE = "eclipselink.entity-agent.suppressed-copy-source";
    public static final String ORIGINAL = "eclipselink.entity-agent.original";
    public static final String WRITE_SUCCEEDED = "eclipselink.entity-agent.write-succeeded";

    private EntityAgentProperties() {
    }
}
