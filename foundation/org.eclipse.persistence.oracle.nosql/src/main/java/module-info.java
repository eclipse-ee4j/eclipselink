/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
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
 * Support for Oracle NoSQL DBs
 */
module org.eclipse.persistence.oracle.nosql {

    requires transitive jakarta.resource;

    requires transitive org.eclipse.persistence.core;
    requires oracle.nosql.client;
    requires nosqldriver;

    exports org.eclipse.persistence.eis.adapters.aq;
    exports org.eclipse.persistence.nosql.adapters.nosql;
    exports org.eclipse.persistence.nosql.adapters.sdk;

    //exported through PUBLIC API
    exports org.eclipse.persistence.internal.nosql.adapters.nosql;
    exports org.eclipse.persistence.internal.nosql.adapters.sdk;
}
