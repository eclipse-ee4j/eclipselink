/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.mappings;

import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;

/**
 * <p><b>Purpose</b>: A transformation mapping is used for a specialized translation between how
 * a value is represented in Java and its representation on the databae. Transformation mappings
 * should only be used when other mappings are inadequate.
 *
 * @author Sati
 * @since TOPLink/Java 1.0
 */
public class TransformationMapping extends AbstractTransformationMapping implements RelationalMapping {

    /**
     * PUBLIC:
     * Default constructor.
     */
    public TransformationMapping() {
        super();
    }

    /**
     * INTERNAL:
     */
    public boolean isRelationalMapping() {
        return true;
    }
}
