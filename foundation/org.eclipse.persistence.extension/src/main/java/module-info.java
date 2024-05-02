/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
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
 * Integrations of 3rd party libraries into EclipseLink.
 */
module org.eclipse.persistence.extension {
    requires transitive org.eclipse.persistence.core;
    requires static org.jgroups;
    requires static org.slf4j;

    exports org.eclipse.persistence.logging.slf4j;
    exports org.eclipse.persistence.sessions.coordination.jgroups;
    exports org.eclipse.persistence.sessions.serializers.kryo;
}
