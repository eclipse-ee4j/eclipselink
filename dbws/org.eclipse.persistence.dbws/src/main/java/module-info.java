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

module org.eclipse.persistence.dbws {

    requires java.desktop;
    requires java.naming;

    requires jakarta.activation;
    requires jakarta.mail;
    requires transitive jakarta.persistence;
    requires jakarta.xml.bind;
    requires jakarta.xml.soap;
    requires jakarta.xml.ws;

    requires jakarta.servlet;

    requires org.eclipse.persistence.jpa;
    requires org.eclipse.persistence.jpa.jpql;
    requires transitive org.eclipse.persistence.moxy;

    exports org.eclipse.persistence.dbws;

    //exported through DBWS PUBLIC API
    exports org.eclipse.persistence.internal.xr;

    //exported DEV
    exports org.eclipse.persistence.internal.xr.sxf;

    //INTERNAL ONLY exports
    exports org.eclipse.persistence.internal.dbws to org.eclipse.persistence.dbws.builder;

}
