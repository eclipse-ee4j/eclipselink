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

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWAttributeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaTypeDefinition;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeListIterator;


final class AttributeDeclarationNodeStructure
    extends NamedSchemaComponentNodeStructure
{
    // **************** Constructors ******************************************

    AttributeDeclarationNodeStructure(MWAttributeDeclaration attributeDeclaration) {
        super(attributeDeclaration);
    }

    // **************** SchemaComponentNodeStructure contract *****************

    protected ListIterator componentDetails() {
        return new CompositeListIterator(this.nameDetails(), this.attributeDetails());
    }


    // **************** NamedSchemaComponentNodeStructure contract *********************

    Integer topLevelOrderIndex() {
        return new Integer(2);
    }


    // **************** Internal **********************************************

    private ListIterator attributeDetails() {
        SchemaComponentDetail[] details = new SchemaComponentDetail[4];

        details[0] = this.buildTypeDetail();
        details[1] = this.buildDefaultDetail();
        details[2] = this.buildFixedDetail();
        details[3] = this.buildUseDetail();

        return CollectionTools.listIterator(details);
    }

    SchemaComponentDetail buildTypeDetail() {
        return new SchemaComponentQNamedDetail(this.getComponent()) {
            protected String getName() {
                return "type";
            }

            protected MWNamedSchemaComponent getQNamedComponent() {
                MWSchemaTypeDefinition type = ((MWAttributeDeclaration) this.component).getType();
                return (type.getName() == null) ? null : type;
            }
        };
    }

    SchemaComponentDetail buildDefaultDetail() {
        return new SchemaComponentDetail(this.getComponent()) {
            protected String getName() {
                return "default";
            }

            protected String getValueFromComponent() {
                String defaultValue = ((MWAttributeDeclaration) this.component).getDefaultValue();
                return (defaultValue == null) ? "" : defaultValue;
            }
        };
    }

    SchemaComponentDetail buildFixedDetail() {
        return new SchemaComponentDetail(this.getComponent()) {
            protected String getName() {
                return "fixed";
            }

            protected String getValueFromComponent() {
                String fixedValue = ((MWAttributeDeclaration) this.component).getFixedValue();
                return (fixedValue == null) ? "" : fixedValue;
            }
        };
    }

    SchemaComponentDetail buildUseDetail() {
        return new SchemaComponentDetail(this.getComponent()) {
            protected String getName() {
                return "use";
            }

            protected String getValueFromComponent() {
                return ((MWAttributeDeclaration) this.component).getUse();
            }
        };
    }
}
