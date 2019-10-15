/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.expressions;

import static org.eclipse.persistence.expressions.ExpressionOperator.SDO_FILTER;
import static org.eclipse.persistence.expressions.ExpressionOperator.SDO_NN;
import static org.eclipse.persistence.expressions.ExpressionOperator.SDO_RELATE;
import static org.eclipse.persistence.expressions.ExpressionOperator.SDO_WITHIN_DISTANCE;

import org.eclipse.persistence.expressions.ExpressionOperator;

/**
 * INTERNAL:
 * This class produces a number of ExpressionOperators for spatial classes
 * The static methods in this class are used by Oracle9Platform to initialize
 * these operators.
 *
 * @see "org.eclipse.persistence.platform.database.oracle.Oracle9Plaform.initializePlatformOperators()"
 *
 */
public class SpatialExpressionOperators {

    /**
     * ExpressionOperator for the MDSYS.SDO_WITHIN_DISTANCE Spatial Operator on the Oracle Database
     * Use of this operator requires the Java spatial classes
     * @return
     */
    public static ExpressionOperator withinDistance() {
        ExpressionOperator operator = ExpressionOperator.simpleThreeArgumentFunction(SDO_WITHIN_DISTANCE, "MDSYS.SDO_WITHIN_DISTANCE");
        operator.bePrefix();
        return operator;
    }

    /**
     * ExpressionOperator for the MDSYS.MDSYS.SDO_RELATE Spatial Operator on the Oracle Database
     * Use of this operator requires the Java spatial classes
     * @return
     */
    public static ExpressionOperator relate() {
        ExpressionOperator operator = ExpressionOperator.simpleThreeArgumentFunction(SDO_RELATE, "MDSYS.SDO_RELATE");
        operator.bePrefix();
        return operator;
    }

    /**
     * ExpressionOperator for the MDSYS.SDO_FILTER Spatial Operator on the Oracle Database
     * Use of this operator requires the Java spatial classes
     * @return
     */
    public static ExpressionOperator filter() {
        ExpressionOperator operator = ExpressionOperator.simpleThreeArgumentFunction(SDO_FILTER, "MDSYS.SDO_FILTER");
        operator.bePrefix();
        return operator;
    }

    /**
     * ExpressionOperator for the MDSYS.SDO_NN Spatial Operator on the Oracle Database
     * Use of this operator requires the Java spatial classes
     * @return
     */
    public static ExpressionOperator nearestNeighbor() {
        ExpressionOperator operator = ExpressionOperator.simpleThreeArgumentFunction(SDO_NN, "MDSYS.SDO_NN");
        operator.bePrefix();
        return operator;
    }
}
