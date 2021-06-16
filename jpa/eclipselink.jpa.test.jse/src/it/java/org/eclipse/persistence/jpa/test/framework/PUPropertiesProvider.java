/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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

// Contributors:
//     01/13/2015 - Rick Curtis
//       - 438871 : Add support for writing statement terminator character(s) when generating ddl to script.
package org.eclipse.persistence.jpa.test.framework;

import java.util.Map;

/**
 * This is a mechanism that allows a test to be called back prior to EMF
 * creation to pass additional persistence unit properties. This is in place as
 * you can't always pass properties via the annotation route.
 */
public interface PUPropertiesProvider {

    public Map<String, Object> getAdditionalPersistenceProperties(String puName);

}
