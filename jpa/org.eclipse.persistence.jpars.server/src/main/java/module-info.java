/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

module org.eclipse.persistence.jpars.server {

    requires java.desktop;
    requires java.naming;

    requires transitive jakarta.persistence;
    requires transitive jakarta.ws.rs;
    requires jakarta.xml.bind;

    requires org.eclipse.persistence.jpa;
    requires org.eclipse.persistence.jpa.jpql;
    requires transitive org.eclipse.persistence.moxy;

    exports org.eclipse.persistence.jpa.rs;
    exports org.eclipse.persistence.jpa.rs.annotations;
    exports org.eclipse.persistence.jpa.rs.exceptions;
    exports org.eclipse.persistence.jpa.rs.features;
    exports org.eclipse.persistence.jpa.rs.features.core.selflinks;
    exports org.eclipse.persistence.jpa.rs.features.fieldsfiltering;
    exports org.eclipse.persistence.jpa.rs.features.paging;
    exports org.eclipse.persistence.jpa.rs.resources;
    exports org.eclipse.persistence.jpa.rs.resources.common;
    exports org.eclipse.persistence.jpa.rs.util.list;

    //exported through PUBLIC API (for XML Binding)
    exports org.eclipse.persistence.internal.jpa.rs.metadata.model;
    exports org.eclipse.persistence.internal.jpa.rs.metadata.model.v2;

    uses org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;
}
