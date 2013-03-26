/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.config;

public class ConfigDefaults {
    // Note: list groupping names need to be configurable (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=396430)
    // For backwards compatibility default is List, but user will be able to change it once bug mentioned above is fixed.
    public static final String JPARS_LIST_GROUPING_NAME = "List";
    public static final String JPARS_LIST_ITEM_NAME = "item";
}
