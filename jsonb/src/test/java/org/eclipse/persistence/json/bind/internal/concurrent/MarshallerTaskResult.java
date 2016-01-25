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

package org.eclipse.persistence.json.bind.internal.concurrent;

/**
 * Wraps a resulted JSON string with jsonb configuration type, to know against which json to check result.
 */
class MarshallerTaskResult {
    private final String producedJson;
    private final MultiTenancyTest.ConfigurationType configurationType;

    public MarshallerTaskResult(String producedJson, MultiTenancyTest.ConfigurationType configurationType) {
        this.producedJson = producedJson;
        this.configurationType = configurationType;
    }

    public String getProducedJson() {
        return producedJson;
    }

    public MultiTenancyTest.ConfigurationType getConfigurationType() {
        return configurationType;
    }
}
