/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
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
