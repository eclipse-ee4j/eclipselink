/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
