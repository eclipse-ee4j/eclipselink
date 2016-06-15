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
 * Lower case with dashes
 *
 * <pre>
 *     myPropertyName -&gt; my-property-name
 * </pre>
 *
 * @author Roman Grigoriadi
 */
public class LowerCaseWithDashesStrategy extends LowerCaseStrategy {

    public final Character SEPARATOR = '-';

    @Override
    protected char getSerarator() {
        return SEPARATOR;
    }
}
