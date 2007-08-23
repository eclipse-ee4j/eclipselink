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

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.Association;

/**
 * <p><b>Purpose</b>: Used to define aggregate mapping field translations deployment XML mappings.
 *
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class FieldTranslation extends Association {

    /**
     * Default constructor.
     */
    public FieldTranslation() {
        super();
    }

    /**
     * Create an association.
     */
    public FieldTranslation(DatabaseField sourceField, DatabaseField targetField) {
        super(sourceField, targetField);
    }
}