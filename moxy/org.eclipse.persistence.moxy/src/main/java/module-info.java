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

module org.eclipse.persistence.moxy {

    requires java.desktop;
    requires java.naming;
    requires java.xml;

    requires transitive jakarta.xml.bind;
    requires jakarta.activation;
    requires jakarta.mail;
    requires static jakarta.validation;
    requires static jakarta.ws.rs;
    requires transitive org.eclipse.persistence.core;
    requires static com.sun.tools.xjc;
    requires static com.sun.xml.bind.core;

    exports org.eclipse.persistence.jaxb;
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
    opens org.eclipse.persistence.jaxb.rs; //required for MOXyJSON provider injection

    //exported through MOXy PUBLIC API
    exports org.eclipse.persistence.internal.jaxb;

    provides jakarta.xml.bind.JAXBContextFactory with org.eclipse.persistence.jaxb.XMLBindingContextFactory;
    provides com.sun.tools.xjc.Plugin with org.eclipse.persistence.jaxb.plugins.BeanValidationPlugin;
}
