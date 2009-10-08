/*******************************************************************************
 * Copyright (c) 2009 Sun Microsystems, Inc. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 ******************************************************************************/
 
package org.eclipse.persistence.testing.models.jpa.beanvalidation;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity(name="CMP3_BV_PROJECT")
public class Project {
    public static final int NAME_MAX_SIZSE = 5;

    @Id
    private int	    id;
    @Size(max = NAME_MAX_SIZSE)
    private String	name;

    public Project() {}

    public Project(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}