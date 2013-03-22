/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.mappings;

/**
 * <p><b>Purpose</b>: Define the relationship to be a ManyToOne.
 * This is mainly functionally the same as OneToOneMapping.
 *
 * @author James Sutherland
 * @since EclipseLink 2.1
 */
public class ManyToOneMapping extends OneToOneMapping {
    /**
     * PUBLIC:
     * Default constructor.
     */
    public ManyToOneMapping() {
        super();
    }

    /**
     * INTERNAL:
     * Related mapping should implement this method to return true.
     */
    @Override
    public boolean isManyToOneMapping() {
        return true;
    }
}
