/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.util.List;

public interface ParentInterface {

    public void addChild(Child cs);

    public List<Child> getChildren();

    public Integer getId();

    public String getSerialNumber();

    public int getVersion();

    public void setChildren(List<Child> children);

    public void setId(Integer id);

    public void setSerialNumber(String serialNumber);
}
