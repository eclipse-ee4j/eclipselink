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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.SerializedObjectConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;

public final class MWSerializedObjectConverter extends MWConverter {

    // **************** Static methods *************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(MWSerializedObjectConverter.class);
        descriptor.getInheritancePolicy().setParentClass(MWConverter.class);

        return descriptor;
    }


    // ************* Constructors **************

    /** Default constructor - for TopLink use only */
    private MWSerializedObjectConverter() {
        super();
    }

    public MWSerializedObjectConverter(MWConverterMapping parent) {
        super(parent);
    }


    // *********** MWConverter implementation ***********

    /** Should ONLY be used in one place - the UI */
    public String accessibleNameKey() {
        return "ACCESSIBLE_SERIALIZED_MAPPING_NODE";
    }

    public String getType() {
        return SERIALIZED_OBJECT_CONVERTER;
    }

    public String iconKey() {
        return "mapping.serialized";
    }

    // ************* Runtime Conversion ************

    public Converter runtimeConverter(DatabaseMapping mapping) {
        return new SerializedObjectConverter(mapping);
    }

}
