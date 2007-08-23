/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

// Javase imports
import java.util.ArrayList;
import java.util.List;

// Java extension imports

// TopLink imports

/**
 * <p><b>INTERNAL</b>: abstract superclass for {@link InsertOperation Insert},
 * {@link UpdateOperation Update}, {@link DeleteOperation Delete} and
 * {@link QueryOperation Query} operations
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */

public abstract class Operation {
    
    protected String name;
    protected List<Parameter> parameters = new ArrayList<Parameter>();

    /**
     * <p><b>INTERNAL</b>:
     * @return this <code>Operation</code>'s name
     */
    public String getName() {
        return name;
    }

    /**
     * <p><b>INTERNAL</b>: Set this <code>Operation</code>'s name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Parameter> getParameters() {
        return parameters;
    }

    public boolean hasResponse() {
        return false;
    }

    public boolean isCollection() {
        return false;
    }

    public void validate(XRServiceAdapter xrService) {
        for (Parameter p : getParameters()) {
            p.validate(xrService, name);
        }
    }

    public void initialize(XRServiceAdapter xrService) {
    }

    public abstract Object invoke(XRServiceAdapter xrService, Invocation invocation);
   
}
