/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("-2")
public class ReadOnlyEntitySubclass extends ReadOnlyEntity {

    public ReadOnlyEntitySubclass() {
        super();
    }

    public ReadOnlyEntitySubclass(int i, String string) {
        super(i, string);
    }

    @Basic
    @Column(name = "ADDON")
    protected String addon;

    public String getNumber() {
        return addon;
    }

    public void setNumber(String addon) {
        this.addon = addon;
    }
}
