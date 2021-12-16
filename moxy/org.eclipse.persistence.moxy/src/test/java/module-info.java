/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

open module org.eclipse.persistence.moxy.test {

    requires java.compiler;
    requires java.logging;
    requires java.sql;

    requires jakarta.mail;
    requires jakarta.json;
    requires jakarta.validation;
    requires jakarta.ws.rs;
    
    requires com.sun.xml.bind;
    requires com.sun.tools.xjc;
    requires org.eclipse.persistence.asm;
    requires org.eclipse.persistence.core;
    requires org.eclipse.persistence.moxy;
    requires org.eclipse.persistence.core.test;
    requires junit;
}
