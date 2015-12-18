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

import java.util.function.Consumer;

/**
 * @author Roman Grigoriadi
 */
public class MethodModifiersClass {

    private Consumer<String> setterWithoutFieldConsumer = s -> {};

    public String publicFieldWithPrivateMethods;

    public String publicFieldWithoutMethods;

    public void setSetterWithoutFieldConsumer(Consumer<String> setterWithoutFieldConsumer) {
        this.setterWithoutFieldConsumer = setterWithoutFieldConsumer;
    }

    public String getGetterWithoutFieldValue() {
        return "GETTER_WITHOUT_FIELD";
    }

    public void setGetterWithoutFieldValue(String value) {
        setterWithoutFieldConsumer.accept(value);
    }

    private String getPublicFieldWithPrivateMethods() {
        return publicFieldWithPrivateMethods;
    }

    private void setPublicFieldWithPrivateMethods(String publicFieldWithPrivateMethods) {
        this.publicFieldWithPrivateMethods = publicFieldWithPrivateMethods;
    }
}
