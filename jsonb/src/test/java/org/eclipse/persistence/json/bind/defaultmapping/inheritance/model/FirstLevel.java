/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.defaultmapping.inheritance.model;

/**
 * @author Roman Grigoriadi
 */
public class FirstLevel extends AbstractZeroLevel {

    private String inFirstLevel;

    @Override
    public String getInZeroOverriddenInFirst() {
        return inZeroOverriddenInFirst;
    }

    @Override
    public void setInZeroOverriddenInFirst(String value) {
        inZeroOverriddenInFirst = value;
    }

    public String getInFirstLevel() {
        return inFirstLevel;
    }

    public void setInFirstLevel(String inFirstLevel) {
        this.inFirstLevel = inFirstLevel;
    }
}
