/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.json.bind.internal.naming;

/**
 * @author Roman Grigoriadi
 */
public class NamingPojo {

    public NamingPojo() {
    }

    public NamingPojo(String upperCasedProperty, String _startingWithUnderscoreProperty, String CAPS_UNDERSCORE_PROPERTY) {
        this.upperCasedProperty = upperCasedProperty;
        this._startingWithUnderscoreProperty = _startingWithUnderscoreProperty;
        this.CAPS_UNDERSCORE_PROPERTY = CAPS_UNDERSCORE_PROPERTY;
    }

    public String upperCasedProperty;
    public String _startingWithUnderscoreProperty;
    public String CAPS_UNDERSCORE_PROPERTY;
}
