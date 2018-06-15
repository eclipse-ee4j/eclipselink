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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWModelGroupDefinition;


final class ModelGroupDefinitionNodeStructure
    extends NamedSchemaComponentNodeStructure
{
    // **************** Constructors ******************************************

    ModelGroupDefinitionNodeStructure(MWModelGroupDefinition modelGroupDefinition) {
        super(modelGroupDefinition);
    }


    // **************** SchemaComponentNodeStructure contract *****************

    protected ListIterator componentDetails() {
        return this.nameDetails();
    }


    // **************** NamedSchemaComponentNodeStructure contract ************

    Integer topLevelOrderIndex() {
        return new Integer(1);
    }
}
