/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sequencing;

import org.eclipse.persistence.internal.databaseaccess.Accessor;

/**
 * <p>
 * <b>Purpose</b>: Define interface for sequencing callback.
 * <p>
 * <b>Description</b>: This interface accessed through
 * DatabaseSession.getSequencingHome().getCallback() method.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Provides sequencing callback to be called after transaction.
 * </ul>
 * @see org.eclipse.persistence.sequencing.SequencingValueGenerationPolicy
 * @see org.eclipse.persistence.sequencing.SequencingControl
 */
public interface SequencingCallback {

    /**
    * INTERNAL:
    * Called only by Session.afterTransaction method.
    * @param accessor Accessor used by transaction.
    * @param committed boolean true - transaction committed, false - transaction failed.
    */
    public void afterTransaction(Accessor accessor, boolean committed);
}