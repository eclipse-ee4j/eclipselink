/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     04/30/2021 - Will Dazey
 *       - 521402: Add support for Criteria queries with only literals
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.query.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SimpleQueryEntity.class)
public class SimpleQueryEntity_ {
    public static volatile SingularAttribute<SimpleQueryEntity, Long> id;
    public static volatile SingularAttribute<SimpleQueryEntity, Integer> intVal1;
    public static volatile SingularAttribute<SimpleQueryEntity, String> strVal1;
}
