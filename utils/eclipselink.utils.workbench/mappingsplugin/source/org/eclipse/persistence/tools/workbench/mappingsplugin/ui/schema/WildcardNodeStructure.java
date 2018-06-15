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

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.Wildcard;

final class WildcardNodeStructure
    extends SchemaComponentNodeStructure
    implements SpecificParticleNodeStructure
{
    // **************** Constructors ******************************************

    WildcardNodeStructure(Wildcard wildcard) {
        super(wildcard);
    }

    // **************** SchemaComponentNodeStructure contract *****************

    public String displayString() {
        return "any";
    }

    // **************** ParticleTermNodeStructure contract ********************

    public void disengageParticle() {
        this.disengageComponent();
    }

    public ListIterator details(ListIterator particleDetails) {
        return particleDetails;
    }
}
