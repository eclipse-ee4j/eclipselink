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

module org.eclipse.persistence.sdo {
    requires java.management;
    requires java.naming;

    requires transitive org.eclipse.persistence.moxy;
    requires jakarta.activation;
    requires jakarta.mail;
    requires jakarta.xml.bind;

    exports org.eclipse.persistence.sdo;
    exports org.eclipse.persistence.sdo.helper;
    exports org.eclipse.persistence.sdo.helper.delegates;
    exports org.eclipse.persistence.sdo.helper.jaxb;
    exports org.eclipse.persistence.sdo.i18n;
    exports org.eclipse.persistence.sdo.types;

    exports commonj.sdo;
    exports commonj.sdo.helper;
    exports commonj.sdo.impl;

    opens org.eclipse.persistence.sdo to org.eclipse.persistence.core;
}
