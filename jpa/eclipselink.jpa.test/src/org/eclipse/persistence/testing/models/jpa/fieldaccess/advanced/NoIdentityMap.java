/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.util.*;
import java.io.Serializable;
import javax.persistence.*;

import static javax.persistence.CascadeType.*;

import org.eclipse.persistence.annotations.NamedStoredFunctionQuery;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.StoredProcedureParameter;
import org.eclipse.persistence.annotations.Convert;

import static org.eclipse.persistence.annotations.Direction.IN;
import static org.eclipse.persistence.annotations.Direction.OUT;
import static org.eclipse.persistence.annotations.Direction.IN_OUT;

/**
 * <p><b>Purpose</b>: Represents a simple Entity with no identity map
 */
@Entity
@Table(name="JPA_NOIDENTITYMAP")
public class NoIdentityMap implements Serializable {
    @Id
    @GeneratedValue()
    protected int id;
    @Version
    protected int version;
    protected String name;

    public NoIdentityMap() {
        name = "";
    }

    public int getID() { 
        return id; 
    }

    public void setID(int id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public int getVersion() {
        return version; 
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
