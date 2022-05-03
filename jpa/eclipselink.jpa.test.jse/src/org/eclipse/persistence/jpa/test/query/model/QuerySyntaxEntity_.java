/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/04/2022 - Will Dazey
 *       - Add support for partial parameter binding for DB2
 ******************************************************************************/
package org.eclipse.persistence.jpa.test.query.model;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(QuerySyntaxEntity.class)
public class QuerySyntaxEntity_ {
    public static volatile SingularAttribute<QuerySyntaxEntity, Long> id;
    public static volatile SingularAttribute<QuerySyntaxEntity, String> strVal1;
    public static volatile SingularAttribute<QuerySyntaxEntity, String> strVal2;
    public static volatile SingularAttribute<QuerySyntaxEntity, Integer> intVal1;
    public static volatile SingularAttribute<QuerySyntaxEntity, Integer> intVal2;
    public static volatile CollectionAttribute<QuerySyntaxEntity, String> colVal1;
}
