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
package org.eclipse.persistence.tools.workbench.mappingsmodel.schema;

import java.util.List;

import org.apache.xerces.xs.XSIDCDefinition;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

public final class IdentityConstraintDefinition
    extends MWModel
{
    private volatile String name;

    /** This can be a unique, key, or keyref constraint */
    private volatile IdentityConstraint constraint;

    /** Toplink use only */
    private IdentityConstraintDefinition() {
        super();
    }

    IdentityConstraintDefinition(MWModel parent, String name) {
        super(parent);
        this.name = name;
    }


    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(IdentityConstraintDefinition.class);

        descriptor.addDirectMapping("name", "name/text()");

        XMLCompositeObjectMapping constraintMapping = new XMLCompositeObjectMapping();
        constraintMapping.setAttributeName("constraint");
        constraintMapping.setGetMethodName("getConstraintForToplink");
        constraintMapping.setSetMethodName("setConstraintForToplink");
        constraintMapping.setReferenceClass(IdentityConstraint.class);
        constraintMapping.setXPath("constraint");
        descriptor.addMapping(constraintMapping);

        return descriptor;
    }

    protected void addChildrenTo(List children) {
        super.addChildrenTo(children);
        children.add(this.constraint);
    }
    public String getName() {
        return this.name;
    }
    void reload(org.apache.xerces.impl.xs.identity.IdentityConstraint idConstraint) {
        if (idConstraint.getCategory() == XSIDCDefinition.IC_UNIQUE) {
            this.constraint = new UniqueIdentityConstraint(this);
        }
        else if (idConstraint.getCategory() == XSIDCDefinition.IC_KEY) {
            this.constraint = new KeyIdentityConstraint(this);
        }
        else /* (idConstraint.getCategory() == XSIDCDefinition.IC_KEYREF) */ {
            this.constraint = new KeyRefIdentityConstraint(this);
        }

        this.constraint.reload(idConstraint);
    }
    /********************************** Toplink persistence use ******************/
    private IdentityConstraint getConstraintForToplink() {
        return this.constraint;
    }
    private void setConstraintForToplink(IdentityConstraint constraint) {
        this.constraint = constraint;
    }

}
