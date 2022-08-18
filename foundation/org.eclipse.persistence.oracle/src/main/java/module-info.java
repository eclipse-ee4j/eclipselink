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

module org.eclipse.persistence.oracle {

    requires transitive org.eclipse.persistence.core;
    requires transitive org.eclipse.persistence.json;
    requires transitive jakarta.persistence;
    requires transitive jakarta.json;
    requires transitive com.oracle.database.jdbc;
    requires com.oracle.database.ucp;

    exports org.eclipse.persistence.platform.database.oracle;
    exports org.eclipse.persistence.platform.database.oracle.converters;
    exports org.eclipse.persistence.platform.database.oracle.dcn;
    exports org.eclipse.persistence.platform.database.oracle.ucp;
    exports org.eclipse.persistence.platform.xml.xdk;
    exports org.eclipse.persistence.tools.profiler.oracle;

    //exported through PUBLIC API
    exports org.eclipse.persistence.internal.platform.database.oracle;
    exports org.eclipse.persistence.platform.database.oracle.json;
}
