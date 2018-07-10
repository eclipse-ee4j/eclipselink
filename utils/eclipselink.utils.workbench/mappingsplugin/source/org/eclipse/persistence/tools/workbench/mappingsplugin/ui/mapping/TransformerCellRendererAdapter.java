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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.mapping;

import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWClassBasedTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMethodBasedTransformer;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWTransformer;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;


public class TransformerCellRendererAdapter
    extends AbstractCellRendererAdapter
{
    // **************** Variables *********************************************

    private final ResourceRepository resourceRepository;


    // **************** Constructors ******************************************

    public TransformerCellRendererAdapter(ResourceRepository resourceRepository) {
        super();
        this.resourceRepository = resourceRepository;
    }


    // **************** CellRendererAdapter contract **************************

    public Icon buildIcon(Object value) {
        MWTransformer transformer = (MWTransformer) value;
        String text = this.buildText(transformer);
        if (text == null) {
            return null;
        }
        if (transformer instanceof MWMethodBasedTransformer) {
            return this.resourceRepository.getIcon("method.public");
        }
        if (transformer instanceof MWClassBasedTransformer) {
            return this.resourceRepository.getIcon("class.public");
        }
        throw new IllegalArgumentException("unknown transformer: " + transformer);
    }

    public String buildText(Object value) {
        return ((MWTransformer) value).transformerDisplayString();
    }

    public String buildToolTipText(Object value) {
        MWTransformer transformer = (MWTransformer) value;
        String text = this.buildText(transformer);
        if (text == null) {
            return null;
        }
        if (transformer instanceof MWMethodBasedTransformer) {
            return this.resourceRepository.getString("TRANSFORMER_METHOD_TOOLTIP", text);
        }
        if (transformer instanceof MWClassBasedTransformer) {
            return this.resourceRepository.getString("TRANSFORMER_CLASS_TOOLTIP", text);
        }
        throw new IllegalArgumentException("unknown transformer: " + transformer);
    }

}
