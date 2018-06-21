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
package org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWConverterMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTypeConversionConverter;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class MWXmlTypeConversionConverter extends MWTypeConversionConverter {


    // **************** Static methods ****************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(MWXmlTypeConversionConverter.class);
        descriptor.getInheritancePolicy().setParentClass(MWTypeConversionConverter.class);
        return descriptor;
    }


    // **************** Constructors ******************

    /** Default constructor - for TopLink use only */
    private MWXmlTypeConversionConverter() {
        super();
    }

    public MWXmlTypeConversionConverter(MWConverterMapping parent) {
        super(parent);
    }

}
