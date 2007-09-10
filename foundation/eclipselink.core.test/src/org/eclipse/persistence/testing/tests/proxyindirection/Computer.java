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

public interface Computer  {
    public int getId();

    public String getSerialNumber();
    
    public String getDescription();

    public void setId(int id);

    public void setDescription(String aDescription);

    public void setSerialNumber(String serialNumber);
}