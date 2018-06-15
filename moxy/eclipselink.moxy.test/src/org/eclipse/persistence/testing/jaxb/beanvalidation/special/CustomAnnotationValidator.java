/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Marcel Valovy - initial API and implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation.special;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Marcel Valovy
 */
public class CustomAnnotationValidator implements ConstraintValidator<CustomAnnotation, Integer> {

    @Override
    public void initialize(CustomAnnotation constraintAnnotation) {

    }

    @Override
    public boolean isValid(Integer object, ConstraintValidatorContext constraintContext) {
        return false;
    }
}
