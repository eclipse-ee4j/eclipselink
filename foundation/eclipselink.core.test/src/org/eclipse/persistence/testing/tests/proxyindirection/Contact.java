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
 * Contact interface.
 *
 * Define behavior for Contact objects.
 *
 * @author        Rick Barkhouse
 * @since        08/25/2000 16:31:46
 */
public interface Contact {
    public int getID();

    public boolean isPublic();

    public void setID(int value);

    public void setIsPublic(boolean value);
}