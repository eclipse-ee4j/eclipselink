/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.descriptors;

import org.eclipse.persistence.mappings.TypedAssociation;

/**
 * <p><b>Purpose</b>: Used to define the interaction argument mapping.
 * This is used for the deployment XML mapping.
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class InteractionArgument extends TypedAssociation {

    /** The type of the query argument */
    protected String argumentName;

    /**
     * Default constructor.
     */
    public InteractionArgument() {
        super();
    }

    public InteractionArgument(String interactionArgumentName, String argumentName, Object value) {
        super(interactionArgumentName, value);
        this.argumentName = argumentName;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }
}