/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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