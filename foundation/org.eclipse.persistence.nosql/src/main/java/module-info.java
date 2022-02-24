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

module org.eclipse.persistence.nosql {

    requires jakarta.transaction;
    requires jakarta.annotation;
    requires static jakarta.cdi;

    requires transitive org.eclipse.persistence.core;

    exports org.eclipse.persistence.eis.adapters.jms;
    exports org.eclipse.persistence.eis.adapters.xmlfile;
    exports org.eclipse.persistence.nosql.adapters.mongo;
    exports org.eclipse.persistence.nosql.annotations;
}
