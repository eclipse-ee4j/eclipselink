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
package org.eclipse.persistence.testing.tests.proxyindirection;


/*
 * Project interface.
 *
 * Define behavior for Project objects.
 *
 * @author        Rick Barkhouse
 * @since        08/23/2000 15:51:34
 */
public interface Project {
    public String getDescription();

    public int getID();

    public String getName();

    public void setDescription(String value);

    public void setID(int value);

    public void setName(String value);
}
