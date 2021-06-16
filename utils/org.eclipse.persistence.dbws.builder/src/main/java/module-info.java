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

module org.eclipse.persistence.dbws.builder {

    requires java.compiler;

    requires jakarta.activation;
    requires jakarta.persistence;
    requires jakarta.xml.bind;

    requires jakarta.servlet; //AM

    requires org.eclipse.persistence.asm;
    requires org.eclipse.persistence.core;
    requires org.eclipse.persistence.dbws;
    requires org.eclipse.persistence.jpa;
    requires org.eclipse.persistence.jpa.jpql;
    requires org.eclipse.persistence.moxy;
    requires org.eclipse.persistence.oracleddlparser;

    exports org.eclipse.persistence.tools.dbws;

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
