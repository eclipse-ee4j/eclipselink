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
package org.eclipse.persistence.tools.workbench.mappingsmodel.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;

/**
 * An MWXpathContext describes an object that parents an MWXmlField object.
 * In addition to serving as a parent, it provides the MWXmlField the information
 * it needs in order to resolve itself to an actual schema object, and it provides
 * information about what kinds of xpaths are allowable for this xml field in this
 * context.
 */
public interface MWXpathContext
    extends MWNode
{
    /**
     * Return the schema component associated with the xml field in this context
     * (Different xml fields may have different components in the same context)
     */
    MWSchemaContextComponent schemaContext(MWXmlField xmlField);

    /**
     * Return the xpath spec associated with the xml field in this context
     * (Different xml fields may have different specs in the same context)
     */
    MWXpathSpec xpathSpec(MWXmlField xmlField);
}
