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

package org.eclipse.persistence.json.bind.customization.model;

import javax.json.bind.annotation.JsonbProperty;

/**
 * @author Roman Grigoriadi
 */
public class JsonbNillableOverriddenWithJsonbProperty {

    @JsonbProperty("nillableOverriddenAccidentallyWithDefaultValue")
    private String nillableOverriddenWithFieldJsonbProperty;

    private String nillableOverriddenWithGetterJsonbProperty;

    private String nillableOverriddenWithSetterJsonbProperty;

    public String getNillableOverriddenWithFieldJsonbProperty() {
        return nillableOverriddenWithFieldJsonbProperty;
    }

    public void setNillableOverriddenWithFieldJsonbProperty(String nillableOverriddenWithFieldJsonbProperty) {
        this.nillableOverriddenWithFieldJsonbProperty = nillableOverriddenWithFieldJsonbProperty;
    }

    @JsonbProperty(nillable = false)
    public String getNillableOverriddenWithGetterJsonbProperty() {
        return nillableOverriddenWithGetterJsonbProperty;
    }

    public void setNillableOverriddenWithGetterJsonbProperty(String nillableOverriddenWithGetterJsonbProperty) {
        this.nillableOverriddenWithGetterJsonbProperty = nillableOverriddenWithGetterJsonbProperty;
    }

    public String getNillableOverriddenWithSetterJsonbProperty() {
        return nillableOverriddenWithSetterJsonbProperty;
    }

    @JsonbProperty(nillable = false)
    public void setNillableOverriddenWithSetterJsonbProperty(String nillableOverriddenWithSetterJsonbProperty) {
        this.nillableOverriddenWithSetterJsonbProperty = nillableOverriddenWithSetterJsonbProperty;
    }
}
