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

module eclipselink {

    requires transitive java.desktop;
    requires java.logging;
    requires transitive java.instrument;
    requires transitive java.management;
    requires transitive java.naming;
    requires transitive java.rmi;
    requires transitive java.sql;
    requires transitive java.xml;
    requires jdk.unsupported;

    //following cannot be optional because we provide
    //implementation of its spi/extension here
    requires transitive jakarta.persistence;
    requires transitive jakarta.xml.bind;
    requires com.sun.tools.xjc;


    requires static jakarta.activation;
    requires static jakarta.annotation;
    requires static jakarta.json;
    requires static jakarta.mail;
    requires static jakarta.validation;
    requires static jakarta.ws.rs;

    requires static jakarta.cdi;
    requires static jakarta.el;
    requires static jakarta.inject;
    requires static jakarta.transaction;
    requires static jakarta.interceptor;
    requires static jakarta.messaging;
    requires static jakarta.resource;

    requires static com.sun.xml.bind.core;

    requires static org.objectweb.asm;
    requires static org.objectweb.asm.commons;
    requires static org.objectweb.asm.tree;
    requires static org.objectweb.asm.util;

    exports org.eclipse.persistence.jpa.jpql;
    exports org.eclipse.persistence.jpa.jpql.parser;
    exports org.eclipse.persistence.jpa.jpql.utility.iterable;
    exports org.eclipse.persistence.jpa.jpql.utility.iterator;

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

    exports org.eclipse.persistence.sessions.coordination.corba;
    exports org.eclipse.persistence.sessions.coordination.corba.sun;
    exports org.eclipse.persistence.sessions.remote.corba.sun;
    exports org.eclipse.persistence.sessions.remote.rmi.iiop;

    exports org.eclipse.persistence.platform.database.oracle;
    exports org.eclipse.persistence.platform.database.oracle.converters;
    exports org.eclipse.persistence.platform.database.oracle.dcn;
    exports org.eclipse.persistence.platform.database.oracle.ucp;
    exports org.eclipse.persistence.platform.xml.xdk;
    exports org.eclipse.persistence.tools.profiler.oracle;

    exports org.eclipse.persistence.jpa;
    exports org.eclipse.persistence.jpa.config;
    exports org.eclipse.persistence.jpa.dynamic;
    exports org.eclipse.persistence.jpa.exceptions;
    exports org.eclipse.persistence.jpa.exceptions.i18n;
    exports org.eclipse.persistence.jpa.metadata;
    exports org.eclipse.persistence.tools.weaving.jpa;
    exports org.eclipse.persistence.tools.weaving.jpa.i18n;

    exports org.eclipse.persistence.jaxb;
    exports org.eclipse.persistence.jaxb.attachment;
    exports org.eclipse.persistence.jaxb.compiler;
    exports org.eclipse.persistence.jaxb.compiler.builder;
    exports org.eclipse.persistence.jaxb.compiler.builder.helper;
    exports org.eclipse.persistence.jaxb.compiler.facets;
    exports org.eclipse.persistence.jaxb.dynamic;
    exports org.eclipse.persistence.jaxb.dynamic.metadata;
    exports org.eclipse.persistence.jaxb.i18n;
    exports org.eclipse.persistence.jaxb.javamodel;
    exports org.eclipse.persistence.jaxb.javamodel.oxm;
    exports org.eclipse.persistence.jaxb.javamodel.reflection;
    exports org.eclipse.persistence.jaxb.javamodel.xjc;
    exports org.eclipse.persistence.jaxb.json;
    exports org.eclipse.persistence.jaxb.metadata;
    exports org.eclipse.persistence.jaxb.plugins;
    exports org.eclipse.persistence.jaxb.rs;
    exports org.eclipse.persistence.jaxb.xmlmodel;

    exports org.eclipse.persistence.jaxb.xjc;

    exports commonj.sdo;
    exports commonj.sdo.helper;
    exports commonj.sdo.impl;

    exports org.eclipse.persistence.sdo;
    exports org.eclipse.persistence.sdo.helper;
    exports org.eclipse.persistence.sdo.helper.delegates;
    exports org.eclipse.persistence.sdo.helper.jaxb;
    exports org.eclipse.persistence.sdo.i18n;
    exports org.eclipse.persistence.sdo.types;

    exports org.eclipse.persistence.dbws;
    exports org.eclipse.persistence.dbws.i18n;
    exports org.eclipse.persistence.jpa.rs;
    exports org.eclipse.persistence.jpa.rs.annotations;
    exports org.eclipse.persistence.jpa.rs.exceptions;
    exports org.eclipse.persistence.jpa.rs.features;
    exports org.eclipse.persistence.jpa.rs.features.core.selflinks;
    exports org.eclipse.persistence.jpa.rs.features.fieldsfiltering;
    exports org.eclipse.persistence.jpa.rs.features.paging;
    exports org.eclipse.persistence.jpa.rs.logging;
    exports org.eclipse.persistence.jpa.rs.resources;
    exports org.eclipse.persistence.jpa.rs.resources.common;
    exports org.eclipse.persistence.jpa.rs.util;
    exports org.eclipse.persistence.jpa.rs.util.list;

    //exported through EclipseLink PUBLIC API
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
    exports org.eclipse.persistence.internal.jaxb;
    exports org.eclipse.persistence.internal.jaxb.many;
    exports org.eclipse.persistence.internal.jpa;
    exports org.eclipse.persistence.internal.jpa.deployment;
    exports org.eclipse.persistence.internal.jpa.metadata.xml;
    exports org.eclipse.persistence.internal.jpa.rs.metadata.model;
    exports org.eclipse.persistence.internal.jpa.rs.metadata.model.v2;
    exports org.eclipse.persistence.internal.localization;
    exports org.eclipse.persistence.internal.oxm;
    exports org.eclipse.persistence.internal.oxm.mappings;
    exports org.eclipse.persistence.internal.oxm.record;
    exports org.eclipse.persistence.internal.oxm.record.namespaces;
    exports org.eclipse.persistence.internal.oxm.schema.model;
    exports org.eclipse.persistence.internal.oxm.unmapped;
    exports org.eclipse.persistence.internal.platform.database;
    exports org.eclipse.persistence.internal.platform.database.oracle;
    exports org.eclipse.persistence.internal.queries;
    exports org.eclipse.persistence.internal.security;
    exports org.eclipse.persistence.internal.sequencing;
    exports org.eclipse.persistence.internal.sessions;
    exports org.eclipse.persistence.internal.sessions.coordination;
    exports org.eclipse.persistence.internal.sessions.coordination.corba;
    exports org.eclipse.persistence.internal.sessions.coordination.jms;
    exports org.eclipse.persistence.internal.sessions.factories;
    exports org.eclipse.persistence.internal.sessions.factories.model;
    exports org.eclipse.persistence.internal.sessions.remote;
    exports org.eclipse.persistence.internal.weaving;
    exports org.eclipse.persistence.internal.xr;

    //exported through EclipseLink INTERNAL API
    exports org.eclipse.persistence.internal.helper.linkedlist;
    exports org.eclipse.persistence.internal.helper.type;
    exports org.eclipse.persistence.internal.jpa.metadata;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors.classes;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;
    exports org.eclipse.persistence.internal.jpa.metadata.accessors.objects;
    exports org.eclipse.persistence.internal.jpa.metadata.additionalcriteria;
    exports org.eclipse.persistence.internal.jpa.metadata.cache;
    exports org.eclipse.persistence.internal.jpa.metadata.changetracking;
    exports org.eclipse.persistence.internal.jpa.metadata.columns;
    exports org.eclipse.persistence.internal.jpa.metadata.converters;
    exports org.eclipse.persistence.internal.jpa.metadata.copypolicy;
    exports org.eclipse.persistence.internal.jpa.metadata.graphs;
    exports org.eclipse.persistence.internal.jpa.metadata.inheritance;
    exports org.eclipse.persistence.internal.jpa.metadata.listeners;
    exports org.eclipse.persistence.internal.jpa.metadata.locking;
    exports org.eclipse.persistence.internal.jpa.metadata.mappings;
    exports org.eclipse.persistence.internal.jpa.metadata.multitenant;
    exports org.eclipse.persistence.internal.jpa.metadata.nosql;
    exports org.eclipse.persistence.internal.jpa.metadata.partitioning;
    exports org.eclipse.persistence.internal.jpa.metadata.queries;
    exports org.eclipse.persistence.internal.jpa.metadata.sequencing;
    exports org.eclipse.persistence.internal.jpa.metadata.sop;
    exports org.eclipse.persistence.internal.jpa.metadata.structures;
    exports org.eclipse.persistence.internal.jpa.metadata.tables;
    exports org.eclipse.persistence.internal.jpa.metadata.transformers;
    exports org.eclipse.persistence.internal.oxm.schema;
    exports org.eclipse.persistence.internal.sessions.cdi;
    exports org.eclipse.persistence.internal.sessions.coordination.broadcast;
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
    exports org.eclipse.persistence.internal.xr.sxf;

    uses org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;
    uses org.eclipse.persistence.mappings.converters.spi.ConverterProvider;
    uses org.eclipse.persistence.internal.databaseaccess.spi.JsonPlatformProvider;

    provides jakarta.persistence.spi.PersistenceProvider with org.eclipse.persistence.jpa.PersistenceProvider;
    provides jakarta.xml.bind.JAXBContextFactory with org.eclipse.persistence.jaxb.XMLBindingContextFactory;
    provides com.sun.tools.xjc.Plugin with org.eclipse.persistence.jaxb.plugins.BeanValidationPlugin;

}
