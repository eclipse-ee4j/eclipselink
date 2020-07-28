/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
//     05/30/2008-1.0M8 Guy Pelletier
//       - 230213: ValidationException when mapping to attribute in MappedSuperClass
//     06/20/2008-1.0 Guy Pelletier
//       - 232975: Failure when attribute type is generic
package org.eclipse.persistence.testing.models.jpa.inherited;

public interface Bluish<X, Y>   {

    X getUniqueKey();

    void setUniqueKey(Y uniqueKey);
}
