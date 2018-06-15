/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational;

import java.util.Iterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWReference;


public interface MWTableReferenceMapping
    extends MWNode
{
    // **************** Batch Reading *****************************************

    boolean usesBatchReading();
    void setUsesBatchReading(boolean newValue);
        public final static String BATCH_READING_PROPERTY = "usesBatchReading";


    // **************** Reference *********************************************

    MWReference getReference();
    void setReference(MWReference reference);
        public final static String REFERENCE_PROPERTY = "reference";

    Iterator candidateReferences();
    boolean referenceIsCandidate(MWReference reference);
}
