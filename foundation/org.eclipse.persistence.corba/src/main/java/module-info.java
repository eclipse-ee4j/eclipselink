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

/**
 * CORBA support for EclipseLink.
 */
module org.eclipse.persistence.corba {

    requires transitive java.rmi;

    requires transitive org.eclipse.persistence.core;

    exports org.eclipse.persistence.sessions.coordination.corba;
    exports org.eclipse.persistence.sessions.coordination.corba.sun;
    exports org.eclipse.persistence.sessions.remote.corba.sun;
    exports org.eclipse.persistence.sessions.remote.rmi.iiop;

    //exported through PUBLIC API
    exports org.eclipse.persistence.internal.sessions.coordination.corba;
}
