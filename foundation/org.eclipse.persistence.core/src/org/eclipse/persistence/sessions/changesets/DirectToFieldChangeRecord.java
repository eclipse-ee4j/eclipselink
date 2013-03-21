/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.sessions.changesets;


/**
 * <p>
 * <b>Purpose</b>: This interface provides public API to the class responsible for holding the change made to a directToFieldMapping.
 * <p>
 * <b>Description</b>: This changeRecord stores the value that the direct to field was changed to.
 * <p>
 */
public interface DirectToFieldChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * Returns the new value assigned during the change
     * @return java.lang.Object
     */
    public Object getNewValue();

    /**
     * ADVANCED:
     * Return the old value of the attribute represented by this ChangeRecord.
     */
    public Object getOldValue();
}
