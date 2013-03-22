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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.expressions.spatial;

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.FunctionExpression;

import static org.eclipse.persistence.expressions.ExpressionOperator.SDO_WITHIN_DISTANCE;
import static org.eclipse.persistence.expressions.ExpressionOperator.SDO_RELATE;
import static org.eclipse.persistence.expressions.ExpressionOperator.SDO_FILTER;
import static org.eclipse.persistence.expressions.ExpressionOperator.SDO_NN;

/**
 * This class is used to construct expressions that use Oracle Spatial operators
 * 
 * <pre><blockquote>
 * ExpressionBuilder builder = new ExpressionBuilder();
 * Expression withinDistance = SpatialExpressions.withinDistance(myJGeometry1, myJGeometry2, "DISTANCE=10");
 * session.readAllObjects(GeometryHolder.class, withinDistance);
 * </blockquote></pre></p>
 *
 * @since Oracle TopLink 11.1.1.0.0
 */
public class SpatialExpressionFactory {

    /**
     * PUBLIC:
     * Return an Expression for the MDSYS.SDO_WITHIN_DISTANCE Spatial Operator on the Oracle Database
     * Use of this expression requires the Java spatial classes
     * 
     * <pre><blockquote>
     * SpatialParameters parameters = new SpatialParameters();
     *parameters.setDistance(10d);
     * Expression selectCriteria = SpatialExpressions.withinDistance(jGeometry1, jGeometry2, parameters);
     * </blockquote></pre>
     * 
     * @param geom1 an Expression representing a JGeometryObject
     * @param geom2 a JGeometryObject or an Expression representing a JGeometryObject
     * @param params a SpatialParameters object configured with the parameters to the call
     * @return a TopLink Expression
     */
    public static Expression withinDistance(Expression geom1, Object geom2, SpatialParameters params) {
        String stringParameters = params == null ? null : params.getParameterString();
        return getSpatialExpression(SDO_WITHIN_DISTANCE, geom1, geom2, stringParameters);
    }

    /**
     * PUBLIC:
     * Return an Expression for the MDSYS.MDSYS.SDO_RELATE Spatial Operator on the Oracle Database
     * Use of this operator requires the Java spatial classes
     * 
     * <pre><blockquote>
     * SpatialParameters parameters = new SpatialParameters();
     * parameters..setMask(Mask.ANYINTERACT).setQueryType(QueryType.WINDOW);
     * Expression selectCriteria = SpatialExpressions.relate(jGeometry1, jGeometry2, parameters);
     * </blockquote></pre>
     * 
     * @param geom1 an Expression representing a JGeometryObject
     * @param geom2 a JGeometryObject or an Expression representing a JGeometryObject
     * @param params a SpatialParameters object configured with the parameters to the call
     * @return a TopLink Expression
     */
    public static Expression relate(Expression geom1, Object geom2, SpatialParameters params) {
        String stringParameters = params == null ? null : params.getParameterString();
        return getSpatialExpression(SDO_RELATE, geom1, geom2, stringParameters);
    }

    /**
     * PUBLIC:
     * Return an Expression for the MDSYS.SDO_FILTER Spatial Operator on the Oracle Database
     * Use of this operator requires the Java spatial classes
     * 
     * <pre><blockquote>
     * SpatialParameters parameters = new SpatialParameters();
     * parameters.setQueryType(QueryType.WINDOW);
     * Expression selectCriteria = SpatialExpressions.filter(jGeometry1, jGeometry2, parameters);
     * </blockquote></pre>
     * 
     * @param geom1 an Expression representing a JGeometryObject
     * @param geom2 a JGeometryObject or an Expression representing a JGeometryObject
     * @param params a SpatialParameters object configured with the parameters to the call
     * @return a TopLink Expression
     */
    public static Expression filter(Expression geom1, Object geom2, SpatialParameters params) {
        String stringParameters = params == null ? null : params.getParameterString();
        return getSpatialExpression(SDO_FILTER, geom1, geom2, stringParameters);
    }

    /**
     * PUBLIC:
     * Return an Expression for the MDSYS.SDO_NN Spatial Operator on the Oracle Database
     * Use of this operator requires the Java spatial classes
     * 
     * <pre><blockquote>
     * SpatialParameters parameters = new SpatialParameters();
     * parameters.setUnit(Units.M);
     * Expression selectCriteria = SpatialExpressions.nearestNeighbor(jGeometry1, jGeometry2, parameters);
     * </blockquote></pre>
     * 
     * @param geom1 an Expression representing a JGeometryObject
     * @param geom2 a JGeometryObject or an Expression representing a JGeometryObject
     * @param params a SpatialParameters object configured with the parameters to the call
     * @return a TopLink Expression
     */
    public static Expression nearestNeighbor(Expression geom1, Object geom2, SpatialParameters params) {
        String stringParameters = params == null ? null : params.getParameterString();
        return getSpatialExpression(SDO_NN, geom1, geom2, stringParameters);
    }

    /**
     * INTERNAL:
     * A utility method to build a SpatialExpression
     * 
     * @param operator the ordinal of the operator
     * @param geom1
     * @param geom2
     * @param params
     * @return
     */
    public static Expression getSpatialExpression(int operator, Expression geom1, Object geom2, String params) {
        Vector vParameters = new Vector(2);
        vParameters.add(geom2);
        //Bug 5885276, the empty string either like " " or "" needs to be substituted
        //by null prior to passing to Geometry call.
        if (params==null || params.trim().equals("")){
            vParameters.add(null);
        }else{
            vParameters.add(params);
        }
        ExpressionOperator anOperator = geom1.getOperator(operator);
        FunctionExpression expression = new FunctionExpression();
        expression.create(geom1, vParameters, anOperator);
        Expression finalExpression = expression.equal("TRUE");
        return finalExpression;
    }
}
