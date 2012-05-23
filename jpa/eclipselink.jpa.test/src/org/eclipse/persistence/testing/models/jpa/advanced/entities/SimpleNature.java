/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     17/04/2011 - 2.3 Vikram Bhatia 
 *     342922: Unwanted insert statement generated when using ElementCollection 
 *     with lazy loading.
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.entities;

import java.util.HashMap;
import java.util.Map;

public class SimpleNature {
    public static final String[] PERSONALITY = 
        {"Active", "Reactive", "Aggressive", "Optimistic", "Pessimistic", "Lazy"};
}
