/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
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
