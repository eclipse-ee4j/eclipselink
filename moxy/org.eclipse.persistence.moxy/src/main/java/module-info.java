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

module org.eclipse.persistence.moxy {

    requires java.desktop;
    requires java.naming;
    requires java.xml;

    requires transitive jakarta.xml.bind;
    requires jakarta.activation;
    requires jakarta.mail;
    requires static jakarta.validation;
    requires static jakarta.ws.rs;
    requires org.eclipse.persistence.asm;
    requires transitive org.eclipse.persistence.core;
    requires static com.sun.tools.xjc;
    requires static com.sun.xml.bind.core;

    exports org.eclipse.persistence.jaxb;
    opens org.eclipse.persistence.jaxb;
    exports org.eclipse.persistence.jaxb.attachment;
    exports org.eclipse.persistence.jaxb.compiler;
    exports org.eclipse.persistence.jaxb.compiler.builder;
    exports org.eclipse.persistence.jaxb.compiler.builder.helper;
    exports org.eclipse.persistence.jaxb.compiler.facets;
    exports org.eclipse.persistence.jaxb.dynamic;
    exports org.eclipse.persistence.jaxb.dynamic.metadata;
    exports org.eclipse.persistence.jaxb.javamodel;
    exports org.eclipse.persistence.jaxb.javamodel.oxm;
    exports org.eclipse.persistence.jaxb.javamodel.reflection;
    exports org.eclipse.persistence.jaxb.javamodel.xjc;
    exports org.eclipse.persistence.jaxb.json;
    exports org.eclipse.persistence.jaxb.metadata;
    exports org.eclipse.persistence.jaxb.plugins;
    exports org.eclipse.persistence.jaxb.rs;
    exports org.eclipse.persistence.jaxb.xmlmodel;
    opens org.eclipse.persistence.jaxb.xmlmodel to jakarta.xml.bind;

    // dbws
    exports org.eclipse.persistence.internal.jaxb;

    //tests
    exports org.eclipse.persistence.internal.jaxb.many;

    exports org.eclipse.persistence.internal.jaxb.json.schema;
    exports org.eclipse.persistence.internal.jaxb.json.schema.model;

    provides com.sun.tools.xjc.Plugin with org.eclipse.persistence.jaxb.plugins.BeanValidationPlugin;
}
