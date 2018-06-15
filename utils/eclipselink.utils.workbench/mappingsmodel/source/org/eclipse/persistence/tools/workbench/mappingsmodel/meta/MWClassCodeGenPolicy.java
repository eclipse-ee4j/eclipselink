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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWAggregateMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWOneToOneMapping;


public interface MWClassCodeGenPolicy {

    MWMethodCodeGenPolicy getMethodCodeGenPolicy(MWMethod method);

    void addAccessorCodeGenPolicy(MWMethod method, MWMethodCodeGenPolicy methodCodeGenPolicy);


    String classComment(MWClass mwClass);

    String emptyMethodBodyComment();

    String collectionImplementationClassNotDeterminedComment(MWClassAttribute attribute, MWClass concreteValueType);

    String oneToOneMappingThatControlsWritingOfPrimaryKeyComment(MWOneToOneMapping mapping);

    String aggregateMappingDoesNotAllowNullImplementationClassNotDeterminedComment();
    String aggregateMappingDoesNotAllowNullComment(MWAggregateMapping mapping);
}
