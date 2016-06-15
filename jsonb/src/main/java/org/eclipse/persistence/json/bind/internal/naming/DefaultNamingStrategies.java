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

import javax.json.bind.config.PropertyNamingStrategy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds and provides instances of known naming strategies.
 *
 * @author Roman Grigoriadi
 */
public class DefaultNamingStrategies {

    /**
     * Map of strategies with its name according to {@link javax.json.bind.config.PropertyNamingStrategy}
     */
    private static final Map<String, PropertyNamingStrategy> strategies;

    static {
        Map<String, PropertyNamingStrategy> collector = new HashMap<>();
        collector.put(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES, new LowerCaseWithUnderscoresStrategy());
        collector.put(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES, new LowerCaseWithDashesStrategy());
        collector.put(PropertyNamingStrategy.UPPER_CAMEL_CASE, new UpperCamelCaseStrategy());
        collector.put(PropertyNamingStrategy.UPPER_CAMEL_CASE_WITH_SPACES, new UpperCamelCaseWithSpacesStrategy());
        collector.put(PropertyNamingStrategy.IDENTITY, new IdentityStrategy());
        collector.put(PropertyNamingStrategy.CASE_INSENSITIVE, new CaseInsensitiveStrategy());
        strategies = Collections.unmodifiableMap(collector);
    }

    /**
     * Gets naming conversion strategy by name, see {@link javax.json.bind.config.PropertyNamingStrategy}
     *
     * @param strategyName name not null
     * @return strategy to use for conversion
     */
    public static PropertyNamingStrategy getStrategy(String strategyName) {
        return strategies.get(strategyName);
    }
}
