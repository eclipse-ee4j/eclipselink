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

module org.eclipse.persistence.dbws.builder {

    requires transitive java.compiler;

    requires jakarta.activation;
    requires jakarta.persistence;
    requires jakarta.xml.bind;

    requires jakarta.servlet;

    requires org.eclipse.persistence.asm;
    requires transitive org.eclipse.persistence.core;
    requires transitive org.eclipse.persistence.dbws;
    requires transitive org.eclipse.persistence.jpa;
    requires org.eclipse.persistence.jpa.jpql;
    requires transitive org.eclipse.persistence.moxy;
    requires transitive org.eclipse.persistence.oracleddlparser;

    exports org.eclipse.persistence.tools.dbws;
    exports org.eclipse.persistence.tools.dbws.jdbc;

    uses org.eclipse.persistence.tools.dbws.DBWSPackager;
    uses org.eclipse.persistence.tools.dbws.NamingConventionTransformer;

    provides org.eclipse.persistence.tools.dbws.DBWSPackager with
            org.eclipse.persistence.tools.dbws.JavasePackager,
            org.eclipse.persistence.tools.dbws.WeblogicPackager,
            org.eclipse.persistence.tools.dbws.WarPackager,
            org.eclipse.persistence.tools.dbws.JDevPackager,
            org.eclipse.persistence.tools.dbws.EclipsePackager,
            org.eclipse.persistence.tools.dbws.GlassfishPackager,
            org.eclipse.persistence.tools.dbws.JBossPackager,
            org.eclipse.persistence.tools.dbws.WebSpherePackager;
    provides org.eclipse.persistence.tools.dbws.NamingConventionTransformer with
            org.eclipse.persistence.tools.dbws.ToLowerTransformer,
            org.eclipse.persistence.tools.dbws.TypeSuffixTransformer,
            org.eclipse.persistence.tools.dbws.SQLX2003Transformer;

}
