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

module org.eclipse.persistence.core {

    requires java.desktop;
    requires java.management;
    requires java.naming;
    requires java.rmi;
    requires java.sql;

    requires static jakarta.activation;
    requires static jakarta.annotation;
    requires static jakarta.json;
    requires static jakarta.mail;
    requires static jakarta.persistence;
    requires static jakarta.xml.bind;

    requires static org.eclipse.persistence.asm;
    requires static org.eclipse.persistence.jpa.jpql;

    requires static jakarta.cdi; //AM
    requires static jakarta.el; //AM
    requires static jakarta.inject; //AM
    requires static jakarta.transaction; //AM

//    requires jakarta.interceptor.api;
//    requires jakarta.jms.api;
//    requires jakarta.resource.api;
//    requires glassfish.corba.omgapi;

    exports org.eclipse.persistence;
    exports org.eclipse.persistence.annotations;
    exports org.eclipse.persistence.config;
    exports org.eclipse.persistence.core.descriptors;
    exports org.eclipse.persistence.core.mappings;
    exports org.eclipse.persistence.core.mappings.converters;
    exports org.eclipse.persistence.core.mappings.transformers;
    exports org.eclipse.persistence.core.queries;
    exports org.eclipse.persistence.core.sessions;
    exports org.eclipse.persistence.descriptors;
    exports org.eclipse.persistence.descriptors.changetracking;
    exports org.eclipse.persistence.descriptors.copying;
    exports org.eclipse.persistence.descriptors.invalidation;
    exports org.eclipse.persistence.descriptors.partitioning;
    exports org.eclipse.persistence.dynamic;
    exports org.eclipse.persistence.eis;
    exports org.eclipse.persistence.eis.interactions;
    exports org.eclipse.persistence.eis.mappings;
    exports org.eclipse.persistence.exceptions;
    exports org.eclipse.persistence.exceptions.i18n;
    exports org.eclipse.persistence.expressions;
    exports org.eclipse.persistence.history;
    exports org.eclipse.persistence.indirection;
    exports org.eclipse.persistence.logging;
    exports org.eclipse.persistence.mappings;
    exports org.eclipse.persistence.mappings.converters;
    exports org.eclipse.persistence.mappings.foundation;
    exports org.eclipse.persistence.mappings.querykeys;
    exports org.eclipse.persistence.mappings.structures;
    exports org.eclipse.persistence.mappings.transformers;
    exports org.eclipse.persistence.mappings.xdb;
    exports org.eclipse.persistence.oxm;
    exports org.eclipse.persistence.oxm.annotations;
    exports org.eclipse.persistence.oxm.attachment;
    exports org.eclipse.persistence.oxm.documentpreservation;
    exports org.eclipse.persistence.oxm.json;
    exports org.eclipse.persistence.oxm.mappings;
    exports org.eclipse.persistence.oxm.mappings.converters;
    exports org.eclipse.persistence.oxm.mappings.nullpolicy;
    exports org.eclipse.persistence.oxm.platform;
    exports org.eclipse.persistence.oxm.record;
    exports org.eclipse.persistence.oxm.schema;
    exports org.eclipse.persistence.oxm.sequenced;
    exports org.eclipse.persistence.oxm.unmapped;
    exports org.eclipse.persistence.platform.database;
    exports org.eclipse.persistence.platform.database.converters;
    exports org.eclipse.persistence.platform.database.events;
    exports org.eclipse.persistence.platform.database.jdbc;
    exports org.eclipse.persistence.platform.database.oracle.annotations;
    exports org.eclipse.persistence.platform.database.oracle.jdbc;
    exports org.eclipse.persistence.platform.database.oracle.plsql;
    exports org.eclipse.persistence.platform.database.partitioning;
    exports org.eclipse.persistence.platform.server;
    exports org.eclipse.persistence.platform.server.glassfish;
    exports org.eclipse.persistence.platform.server.was;
    exports org.eclipse.persistence.platform.server.wls;
    exports org.eclipse.persistence.platform.xml;
    exports org.eclipse.persistence.queries;
    exports org.eclipse.persistence.sequencing;
    exports org.eclipse.persistence.services;
    exports org.eclipse.persistence.services.glassfish;
    exports org.eclipse.persistence.services.jboss;
    exports org.eclipse.persistence.services.mbean;
    exports org.eclipse.persistence.services.weblogic;
    exports org.eclipse.persistence.services.websphere;
    exports org.eclipse.persistence.sessions;
    exports org.eclipse.persistence.sessions.broker;
    exports org.eclipse.persistence.sessions.changesets;
    exports org.eclipse.persistence.sessions.coordination;
    exports org.eclipse.persistence.sessions.coordination.broadcast;
    exports org.eclipse.persistence.sessions.coordination.jms;
    exports org.eclipse.persistence.sessions.coordination.rmi;
    exports org.eclipse.persistence.sessions.factories;
    exports org.eclipse.persistence.sessions.remote;
    exports org.eclipse.persistence.sessions.remote.rmi;
    exports org.eclipse.persistence.sessions.serializers;
    exports org.eclipse.persistence.sessions.server;
    exports org.eclipse.persistence.tools.profiler;
    exports org.eclipse.persistence.tools.schemaframework;
    exports org.eclipse.persistence.tools.tuning;
    exports org.eclipse.persistence.transaction;
    exports org.eclipse.persistence.transaction.glassfish;
    exports org.eclipse.persistence.transaction.jboss;
    exports org.eclipse.persistence.transaction.oc4j;
    exports org.eclipse.persistence.transaction.sap;
    exports org.eclipse.persistence.transaction.was;
    exports org.eclipse.persistence.transaction.wls;

    exports org.eclipse.persistence.internal.cache;
    exports org.eclipse.persistence.internal.codegen;
    exports org.eclipse.persistence.internal.core.databaseaccess;
    exports org.eclipse.persistence.internal.core.descriptors;
    exports org.eclipse.persistence.internal.core.helper;
    exports org.eclipse.persistence.internal.core.queries;
    exports org.eclipse.persistence.internal.core.sessions;
    exports org.eclipse.persistence.internal.databaseaccess;
    exports org.eclipse.persistence.internal.descriptors;
    exports org.eclipse.persistence.internal.descriptors.changetracking;
    exports org.eclipse.persistence.internal.dynamic;
    exports org.eclipse.persistence.internal.expressions;
    exports org.eclipse.persistence.internal.helper;
    exports org.eclipse.persistence.internal.helper.linkedlist;
    exports org.eclipse.persistence.internal.helper.type;
    exports org.eclipse.persistence.internal.history;
    exports org.eclipse.persistence.internal.identitymaps;
    exports org.eclipse.persistence.internal.indirection;
    exports org.eclipse.persistence.internal.jpa.jpql;
    exports org.eclipse.persistence.internal.localization;
    exports org.eclipse.persistence.internal.mappings.converters;
    exports org.eclipse.persistence.internal.oxm;
    exports org.eclipse.persistence.internal.oxm.accessor;
    exports org.eclipse.persistence.internal.oxm.conversion;
    exports org.eclipse.persistence.internal.oxm.documentpreservation;
    exports org.eclipse.persistence.internal.oxm.mappings;
    exports org.eclipse.persistence.internal.oxm.record;
    exports org.eclipse.persistence.internal.oxm.record.deferred;
    exports org.eclipse.persistence.internal.oxm.record.json;
    exports org.eclipse.persistence.internal.oxm.record.namespaces;
    exports org.eclipse.persistence.internal.oxm.schema;
    exports org.eclipse.persistence.internal.oxm.schema.model;
    exports org.eclipse.persistence.internal.oxm.unmapped;
    exports org.eclipse.persistence.internal.platform.database;
    exports org.eclipse.persistence.internal.queries;
    exports org.eclipse.persistence.internal.security;
    exports org.eclipse.persistence.internal.sequencing;
    exports org.eclipse.persistence.internal.sessions;
    exports org.eclipse.persistence.internal.sessions.cdi;
    exports org.eclipse.persistence.internal.sessions.coordination;
    exports org.eclipse.persistence.internal.sessions.coordination.broadcast;
    exports org.eclipse.persistence.internal.sessions.coordination.jms;
    exports org.eclipse.persistence.internal.sessions.coordination.rmi;
    exports org.eclipse.persistence.internal.sessions.factories;
    exports org.eclipse.persistence.internal.sessions.factories.model;
    exports org.eclipse.persistence.internal.sessions.factories.model.event;
    exports org.eclipse.persistence.internal.sessions.factories.model.log;
    exports org.eclipse.persistence.internal.sessions.factories.model.login;
    exports org.eclipse.persistence.internal.sessions.factories.model.platform;
    exports org.eclipse.persistence.internal.sessions.factories.model.pool;
    exports org.eclipse.persistence.internal.sessions.factories.model.project;
    exports org.eclipse.persistence.internal.sessions.factories.model.property;
    exports org.eclipse.persistence.internal.sessions.factories.model.rcm;
    exports org.eclipse.persistence.internal.sessions.factories.model.rcm.command;
    exports org.eclipse.persistence.internal.sessions.factories.model.sequencing;
    exports org.eclipse.persistence.internal.sessions.factories.model.session;
    exports org.eclipse.persistence.internal.sessions.factories.model.transport;
    exports org.eclipse.persistence.internal.sessions.factories.model.transport.discovery;
    exports org.eclipse.persistence.internal.sessions.factories.model.transport.naming;
    exports org.eclipse.persistence.internal.sessions.remote;
    exports org.eclipse.persistence.internal.weaving;
}
