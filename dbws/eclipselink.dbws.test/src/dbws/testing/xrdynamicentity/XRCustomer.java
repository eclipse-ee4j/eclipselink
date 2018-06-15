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
//     Mike Norman - May 2008, created DBWS test package
package dbws.testing.xrdynamicentity;

import org.eclipse.persistence.internal.xr.XRDynamicEntity;
import org.eclipse.persistence.internal.xr.XRDynamicPropertiesManager;

public class XRCustomer extends XRDynamicEntity {

    public static XRDynamicPropertiesManager DPM = new XRDynamicPropertiesManager();

    public XRCustomer() {
        super();
    }

    public XRDynamicPropertiesManager fetchPropertiesManager() {
        return DPM;
    }

}
