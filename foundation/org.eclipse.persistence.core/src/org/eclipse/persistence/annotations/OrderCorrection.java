/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     06/18/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** 
 * OrderCorrection annotation may be specified together with OrderColumn annotation.
 * Its OrderCorrectionType value defines what should be done in case 
 * the order list read from the data base is invalid
 * (has nulls, duplicates, negative values, values greater/equal to list size -
 * the only valid order list of n elements is: {0, 1,..., n-1}).
 * 
 * If the annotation is not specified than OrderCorrectionValue.READ_WRITE used. 
 * 
 * @see org.eclipse.persistence.annotations.OrderColumn
 * @see org.eclipse.persistence.annotations.OrderCorrectionType.
 * 
 */ 
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface OrderCorrection {
    OrderCorrectionType value();
}
