/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.oxm.XMLUnmarshalListener;

/**
 * <p><b>Purpose</b>: Implementation of XMLUnmarshalListener used when unmarshalling XML to XMLDocuments
 * <p><b>Responsibilities</b>:<ul>
 * <li> When creating a DataObject we need to call setType and setHelperContext with the appropriate values
 * </ul>
 */
public class SDOCSUnmarshalListener implements XMLUnmarshalListener {
    private boolean isCSUnmarshalListener;
    /** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    protected HelperContext aHelperContext;

    public SDOCSUnmarshalListener(HelperContext aContext, boolean bIsCSUnmarshalListener) {
        aHelperContext = aContext;
        isCSUnmarshalListener = bIsCSUnmarshalListener;
    }

    public SDOCSUnmarshalListener(HelperContext aContext) {
        aHelperContext = aContext;
    }

    @Override
    public void beforeUnmarshal(Object target, Object parent) {
        if (target instanceof SDODataObject) {
            SDOType type = ((SDOTypeHelper) aHelperContext.getTypeHelper()).getTypeForImplClass(target.getClass());
            if(type.isWrapperType() || isCSUnmarshalListener) {
                // perform cleanup operations on objects that were instantiated with getInstance()
                SDODataObject aDataObject = (SDODataObject)target;
                // reset the HelperContext on target DataObject from default static context
                // setting the Type requires a helpercontext so following 2 calls must be in this order
                aDataObject._setHelperContext(aHelperContext);
                aDataObject._setType(type);
            }
        } else if (target instanceof SDOChangeSummary) {
            if (!isCSUnmarshalListener) {
                ((SDOChangeSummary)target).setHelperContext(aHelperContext);
            }
        }
    }

    @Override
    public void afterUnmarshal(Object target, Object parent) {
        if(target.getClass() == SDOChangeSummary.class) {
            ((SDOChangeSummary) target).setRootDataObject((DataObject) parent);
        }
    }

}
