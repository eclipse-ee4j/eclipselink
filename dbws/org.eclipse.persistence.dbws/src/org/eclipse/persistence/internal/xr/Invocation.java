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

package org.eclipse.persistence.internal.xr;

// Javase imports
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Java extension imports

// EclipseLink imports

/**
 * <p><b>INTERNAL</b>: An <code>Invocation</code> holds runtime argument values that are used by an
 * {@link XRServiceAdapter} to invoke a named {@link Operation}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class Invocation {

    protected String name;
    protected Map<String, Object> parameters = new HashMap<String, Object>();

    public Invocation() {
        super();
    }

    public Invocation(String name) {
        super();
        this.name = name;
    }

    /**
     * <p><b>INTERNAL</b>: Get the name of this <code>Invocation</code>
     * @return  name of this <code>Invocation</code>
     */
    public String getName() {
        return name;
    }
    /**
     * <p><b>INTERNAL</b>: Set the name of this <code>Invocation</code>
     * @param  name of this <code>Invocation</code>
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p><b>PUBLIC</b>: Get the runtime argument value with the given name
     * @param   name of argument
     * @return  desired runtime argument value
     */
    public Object getParameter(String key) {
        return parameters.get(key);
    }
    /**
     * <p><b>INTERNAL</b>: Set a runtime argument value
     * @param name of argument
     * @param parameter runtime argument value
     */
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    /**
     * <p><b>INTERNAL</b>: Get the runtime argument values
     * @return  runtime argument values
     */
    public Collection<Object> getParameters() {
        return parameters.values();
    }
}
