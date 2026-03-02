/*
 * Copyright (c) 2021, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

module org.eclipse.persistence.core {

    requires transitive java.desktop;
    requires java.logging;
    requires transitive java.management; //server platforms
    requires transitive java.naming;
    requires transitive java.rmi;
    requires transitive java.sql;
    requires transitive java.xml;

    requires static jakarta.activation;
    requires static jakarta.annotation;
    requires static jakarta.json;
    requires static jakarta.mail;
    requires static jakarta.persistence;
    requires static jakarta.xml.bind;

    requires static org.eclipse.persistence.jpa.jpql;

    requires static jakarta.cdi;
    requires static jakarta.el;
    requires static jakarta.inject;
    requires static jakarta.transaction;

    requires static org.objectweb.asm; //AM
    requires static org.objectweb.asm.commons; //AM

    requires static jakarta.interceptor;
    requires static jakarta.messaging;
    requires static jakarta.resource;

    exports org.eclipse.persistence;
    exports org.eclipse.persistence.annotations;
    exports org.eclipse.persistence.asm;
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
    exports org.eclipse.persistence.eis.i18n;
    exports org.eclipse.persistence.eis.interactions;
    exports org.eclipse.persistence.eis.mappings;
    exports org.eclipse.persistence.exceptions;
    exports org.eclipse.persistence.exceptions.i18n;
    exports org.eclipse.persistence.expressions;
    exports org.eclipse.persistence.history;
    exports org.eclipse.persistence.indirection;
    exports org.eclipse.persistence.logging;
    exports org.eclipse.persistence.logging.jul;
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
    exports org.eclipse.persistence.oxm.exceptions;
    exports org.eclipse.persistence.oxm.exceptions.i18n;
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
    exports org.eclipse.persistence.platform.server.i18n;
    exports org.eclipse.persistence.platform.server.was;
    exports org.eclipse.persistence.platform.server.wls;
    exports org.eclipse.persistence.platform.xml;
    exports org.eclipse.persistence.platform.xml.i18n;
    exports org.eclipse.persistence.queries;
    exports org.eclipse.persistence.security;
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
    exports org.eclipse.persistence.sessions.coordination.i18n;
    exports org.eclipse.persistence.sessions.coordination.jms;
    exports org.eclipse.persistence.sessions.coordination.rmi;
    exports org.eclipse.persistence.sessions.factories;
    exports org.eclipse.persistence.sessions.factories.i18n;
    exports org.eclipse.persistence.sessions.interceptors;
    exports org.eclipse.persistence.sessions.remote;
    exports org.eclipse.persistence.sessions.remote.rmi;
    exports org.eclipse.persistence.sessions.serializers;
    exports org.eclipse.persistence.sessions.server;
    exports org.eclipse.persistence.tools;
    exports org.eclipse.persistence.tools.profiler;
    exports org.eclipse.persistence.tools.schemaframework;
    exports org.eclipse.persistence.tools.tuning;
    exports org.eclipse.persistence.transaction;
    exports org.eclipse.persistence.transaction.glassfish;
    exports org.eclipse.persistence.transaction.i18n;
    exports org.eclipse.persistence.transaction.jboss;
    exports org.eclipse.persistence.transaction.sap;
    exports org.eclipse.persistence.transaction.was;
    exports org.eclipse.persistence.transaction.wls;

    //exported through PUBLIC API
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
    exports org.eclipse.persistence.internal.identitymaps;
    exports org.eclipse.persistence.internal.indirection;
    exports org.eclipse.persistence.internal.oxm;
    exports org.eclipse.persistence.internal.oxm.mappings;
    exports org.eclipse.persistence.internal.oxm.record.namespaces;
    exports org.eclipse.persistence.internal.oxm.record;
    exports org.eclipse.persistence.internal.oxm.unmapped;
    exports org.eclipse.persistence.internal.queries;
    exports org.eclipse.persistence.internal.security;
    exports org.eclipse.persistence.internal.sequencing;
    exports org.eclipse.persistence.internal.sessions.coordination;
    exports org.eclipse.persistence.internal.sessions.coordination.jms;
    exports org.eclipse.persistence.internal.sessions.factories;
    exports org.eclipse.persistence.internal.sessions.factories.model;
    exports org.eclipse.persistence.internal.sessions.factories.model.property;
    exports org.eclipse.persistence.internal.sessions.remote;
    exports org.eclipse.persistence.internal.sessions;

    //exported through INTERNAL API
    exports org.eclipse.persistence.internal.helper.linkedlist;
    exports org.eclipse.persistence.internal.helper.type;
    exports org.eclipse.persistence.internal.sessions.cdi;
    exports org.eclipse.persistence.internal.sessions.coordination.broadcast;
    exports org.eclipse.persistence.internal.sessions.factories.model.event;
    exports org.eclipse.persistence.internal.sessions.factories.model.log;
    exports org.eclipse.persistence.internal.sessions.factories.model.login;
    exports org.eclipse.persistence.internal.sessions.factories.model.platform;
    exports org.eclipse.persistence.internal.sessions.factories.model.pool;
    exports org.eclipse.persistence.internal.sessions.factories.model.project;
    exports org.eclipse.persistence.internal.sessions.factories.model.rcm.command;
    exports org.eclipse.persistence.internal.sessions.factories.model.rcm;
    exports org.eclipse.persistence.internal.sessions.factories.model.sequencing;
    exports org.eclipse.persistence.internal.sessions.factories.model.session;
    exports org.eclipse.persistence.internal.sessions.factories.model.transport;
    exports org.eclipse.persistence.internal.sessions.factories.model.transport.discovery;
    exports org.eclipse.persistence.internal.sessions.factories.model.transport.naming;

    //INTERNAL ONLY exports
    exports org.eclipse.persistence.internal.jpa.jpql to org.eclipse.persistence.jpa;
    exports org.eclipse.persistence.internal.localization to
            org.eclipse.persistence.dbws,
            org.eclipse.persistence.jpa,
            org.eclipse.persistence.jpars.server,
            org.eclipse.persistence.moxy,
            org.eclipse.persistence.oracle,
            org.eclipse.persistence.sdo,
            org.eclipse.persistence.pgsql;
    exports org.eclipse.persistence.internal.mappings.converters to org.eclipse.persistence.jpa;
    exports org.eclipse.persistence.internal.weaving;
    exports org.eclipse.persistence.internal.oxm.schema to
            org.eclipse.persistence.dbws,
            org.eclipse.persistence.dbws.builder,
            org.eclipse.persistence.moxy,
            org.eclipse.persistence.sdo;
    exports org.eclipse.persistence.internal.oxm.schema.model to
            org.eclipse.persistence.dbws,
            org.eclipse.persistence.dbws.builder,
            org.eclipse.persistence.moxy,
            org.eclipse.persistence.sdo;
    exports org.eclipse.persistence.internal.oxm.conversion to org.eclipse.persistence.dbws;
    exports org.eclipse.persistence.internal.sessions.coordination.rmi to org.eclipse.persistence.corba;
    exports org.eclipse.persistence.internal.platform.database to org.eclipse.persistence.oracle;
    exports org.eclipse.persistence.mappings.converters.spi to
            org.eclipse.persistence.json,
            org.eclipse.persistence.jpa;
    exports org.eclipse.persistence.internal.databaseaccess.spi to
            org.eclipse.persistence.json,
            org.eclipse.persistence.pgsql,
            org.eclipse.persistence.oracle;

    uses org.eclipse.persistence.internal.databaseaccess.spi.JsonPlatformProvider;
}
