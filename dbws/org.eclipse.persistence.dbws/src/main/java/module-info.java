/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

module org.eclipse.persistence.dbws {

    requires java.desktop;
    requires java.naming;

    requires jakarta.activation;
    requires jakarta.mail;
    requires jakarta.persistence;
    requires static jakarta.ws.rs;
    requires jakarta.xml.bind;
    requires static jakarta.xml.soap;
    requires static jakarta.xml.ws;

    requires static jakarta.servlet; //AM

    requires org.eclipse.persistence.asm;
    requires org.eclipse.persistence.core;
    requires org.eclipse.persistence.jpa;
    requires org.eclipse.persistence.jpa.jpql;
    requires org.eclipse.persistence.moxy;

    exports org.eclipse.persistence.dbws;
    exports org.eclipse.persistence.jpa.rs;
    exports org.eclipse.persistence.jpa.rs.annotations;
    exports org.eclipse.persistence.jpa.rs.eventlistener;
    exports org.eclipse.persistence.jpa.rs.exceptions;
    exports org.eclipse.persistence.jpa.rs.features;
    exports org.eclipse.persistence.jpa.rs.features.core.selflinks;
    exports org.eclipse.persistence.jpa.rs.features.fieldsfiltering;
    exports org.eclipse.persistence.jpa.rs.features.paging;
    exports org.eclipse.persistence.jpa.rs.logging;
    exports org.eclipse.persistence.jpa.rs.resources;
    exports org.eclipse.persistence.jpa.rs.resources.common;
    exports org.eclipse.persistence.jpa.rs.resources.unversioned;
    exports org.eclipse.persistence.jpa.rs.util;
    exports org.eclipse.persistence.jpa.rs.util.list;
    exports org.eclipse.persistence.jpa.rs.util.metadatasources;
    exports org.eclipse.persistence.jpa.rs.util.xmladapters;

    // dbws builder
    exports org.eclipse.persistence.internal.dbws;
    exports org.eclipse.persistence.internal.xr;
    exports org.eclipse.persistence.internal.xr.sxf;

    uses org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;
}
