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

module org.eclipse.persistence.sdo {
    requires java.management;
    requires java.naming;

    requires org.eclipse.persistence.asm;
    requires org.eclipse.persistence.core;
    requires org.eclipse.persistence.moxy;
    requires jakarta.activation;
    requires jakarta.mail;
    requires jakarta.xml.bind;

    exports org.eclipse.persistence.sdo;
    exports org.eclipse.persistence.sdo.helper;
    exports org.eclipse.persistence.sdo.types;

    exports commonj.sdo;
    exports commonj.sdo.helper;
    exports commonj.sdo.impl;

    opens org.eclipse.persistence.sdo to org.eclipse.persistence.core;
}
