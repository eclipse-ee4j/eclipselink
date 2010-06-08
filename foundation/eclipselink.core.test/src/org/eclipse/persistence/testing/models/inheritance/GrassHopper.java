/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.testing.models.inheritance.Insect;

public class GrassHopper extends Insect {
    protected Integer gh_ID;
    protected Integer gh_maximumJump;

    public Integer getGh_ID() {
        return gh_ID;
    }

    public Integer getGh_maximumJump() {
        return gh_maximumJump;
    }

    public void setGh_ID(Integer param1) {
        gh_ID = param1;
    }

    public void setGh_maximumJump(Integer param1) {
        gh_maximumJump = param1;
    }
}
