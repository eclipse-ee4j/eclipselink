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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import org.eclipse.persistence.tools.workbench.framework.resources.StringRepository;
import org.eclipse.persistence.tools.workbench.uitools.cell.AbstractCellRendererAdapter;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;


/**
 * This <code>CellRendererAdapter</code> renders the <code>TriStateBoolean</code>
 * values with localized strings.
 *
 * By default it displays "Undefined", "True", and "False"; override the
 * appropriate methods to change the string resource keys.
 */
public class TriStateBooleanCellRendererAdapter
    extends AbstractCellRendererAdapter
{
    private final StringRepository stringRepository;

    public TriStateBooleanCellRendererAdapter(StringRepository stringRepository) {
        super();
        this.stringRepository = stringRepository;
    }

    public String buildText(Object value) {
        if (TriStateBoolean.TRUE.equals(value)) {
            return this.trueString();
        }
        if (TriStateBoolean.FALSE.equals(value)) {
            return this.falseString();
        }
        if (TriStateBoolean.UNDEFINED.equals(value)) {
            return this.undefinedString();
        }
        return null;
    }

    protected String trueString() {
        return this.stringRepository.getString(this.trueResourceKey());
    }

    protected String trueResourceKey() {
        return "TRI_STATE_BOOLEAN_TRUE";
    }

    protected String falseString() {
        return this.stringRepository.getString(this.falseResourceKey());
    }

    protected String falseResourceKey() {
        return "TRI_STATE_BOOLEAN_FALSE";
    }

    protected String undefinedString() {
        return this.stringRepository.getString(this.undefinedResourceKey());
    }

    protected String undefinedResourceKey() {
        return "TRI_STATE_BOOLEAN_UNDEFINED";
    }

}
