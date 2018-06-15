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

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWModelGroup;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeListIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementListIterator;


final class ModelGroupNodeStructure
    extends SchemaComponentNodeStructure
    implements SpecificParticleNodeStructure
{
    // **************** Constructors ******************************************

    ModelGroupNodeStructure(MWModelGroup modelGroup) {
        super(modelGroup);
    }


    // **************** SchemaComponentNode contract **************************

    protected ListIterator componentDetails() {
        return new SingleElementListIterator(this.buildCompositorDetail());
    }

    public String displayString() {
        return ((MWModelGroup) this.getComponent()).getCompositor();
    }


    // **************** ParticleTermNodeStructure contract ********************

    public void disengageParticle() {
        this.disengageComponent();
    }

    public ListIterator details(ListIterator particleDetails) {
        return new CompositeListIterator(this.details(), particleDetails);
    }


    // **************** Internal **********************************************

    SchemaComponentDetail buildCompositorDetail() {
        return new SchemaComponentDetail(this.getComponent()) {
            protected String getName() {
                return "compositor";
            }

            protected String getValueFromComponent() {
                return ((MWModelGroup) this.component).getCompositor();
            }
        };
    }
}
