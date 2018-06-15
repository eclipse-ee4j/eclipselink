/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Marcel Valovy - initial API and implementation
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
package org.eclipse.persistence.testing.jaxb.beanvalidation.special;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomAnnotationValidator.class)
public @interface CustomAnnotation {

    String message() default "{org.eclipse.persistence.moxy.CustomAnnotation.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
