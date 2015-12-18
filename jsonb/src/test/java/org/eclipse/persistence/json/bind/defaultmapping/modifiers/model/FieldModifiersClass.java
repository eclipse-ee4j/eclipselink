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

package org.eclipse.persistence.json.bind.defaultmapping.modifiers.model;

/**
 * @author Roman Grigoriadi
 */
public class FieldModifiersClass {

    private final String finalString = "FINAL_STRING";

    private static final Long serialVerisonUID = Long.MAX_VALUE;

    private static String staticString = "STATIC_STRING";

    private transient String transientString = "TRANSIENT_STRING";

    public String getFinalString() {
        return finalString;
    }

    public void setFinalString() {
        throw new IllegalStateException();
    }

    public static Long getSerialVerisonUID() {
        throw new IllegalStateException();
    }

    public String getTransientString() {
        throw new IllegalStateException();
    }

    public void setTransientString(String transientString) {
        throw new IllegalStateException();
    }

    public static String getStaticString() {
        throw new IllegalStateException();
    }

    public static void setStaticString(String staticString) {
        throw new IllegalStateException();
    }
}
