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
package org.eclipse.persistence.testing.models.inheritance;

public class LadyBug extends Insect {
    protected Integer lb_ID;
    protected Integer lb_numberOfSpots;

    public Integer getLb_ID() {
        return lb_ID;
    }

    public Integer getLb_numberOfSpots() {
        return lb_numberOfSpots;
    }

    public void setLb_ID(Integer param1) {
        lb_ID = param1;
    }

    public void setLb_numberOfSpots(Integer param1) {
        lb_numberOfSpots = param1;
    }
}
