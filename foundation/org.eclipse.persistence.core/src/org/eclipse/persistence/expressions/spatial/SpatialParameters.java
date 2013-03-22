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

import java.io.StringWriter;

/**
 * PUBLIC:
 * A utility class used to set parameters on spatial operators within TopLink's
 * expression framework. This class allows the aptial operator parameters to be 
 * passed in directly as a string or to be programatically configured using the
 * attributes defined and the enum types. Each spatial operator offers different
 * parameter arguments and values. This class does not enforce these rules but
 * instead leaves it to the caller to decide what values they want included.
 * <p>
 * When providing the parameter string through setParams or the constructor none 
 * of the other values will be used. Instead the string as provided will be used.
 * <p>
 * Creating an instance of SpatialParameters without configuring it and passing 
 * it into the SpatialExpressionFactory call is equivalent to passing in null. 
 * The resulting SQL will have NULL writen out for the parameters argument to the
 * spatial operator.
 * 
 * @since Oracle TopLink 11.1.1.0.0
 */
public class SpatialParameters {
    private String params = null;
    private Number minResolution = null;
    private Number maxResolution = null;
    private Units units = null;
    private Number distance = null;
    private QueryType queryType = null;
    private Mask[] masks = null;

    private static String UNIT_PARAM = "UNIT=";
    private static String MAX_RES_PARAM = "MAX_RESOLUTION=";
    private static String MIN_RES_PARAM = "MIN_RESOLUTION=";
    private static String DISTANCE_PARAM = "DISTANCE=";
    private static String QUERYTYPE_PARAM = "QUERYTYPE=";
    private static String MASK_PARAM = "MASK=";

    public SpatialParameters() {
    }
    
    public SpatialParameters(String params) {
        setParams(params);
    }

    /**
     * PUBLIC:
     * Set the PARAMS (String) value. If this value is set then no other param
     * values will be used.
     * @param params
     * @return this instance of SpatialParameters
     */
    public SpatialParameters setParams(String params) {
        this.params = params;
        return this;
    }

    public String getParams() {
        return params;
    }

    /**
     * PUBLIC:
     * Set the MIN_RESOLUTION parameter
     * @param minResolution
     * @return this instance of SpatialParameters
     */
    public SpatialParameters setMinResolution(Number minResolution) {
        this.minResolution = minResolution;
        return this;
    }

    public Number getMinResolution() {
        return minResolution;
    }

    /**
     * PUBLIC:
     * Set the MAX_RESOLUTION parameter
     * @param maxResolution
     * @return this instance of SpatialParameters
     */
    public SpatialParameters setMaxResolution(Number maxResolution) {
        this.maxResolution = maxResolution;
        return this;
    }

    public Number getMaxResolution() {
        return maxResolution;
    }

    /**
     * PUBLIC:
     * Set the UNIT parameter
     * @param units a value from the SpatialParameters.Units enum
     * @return this instance of SpatialParameters
     */
    public SpatialParameters setUnits(SpatialParameters.Units units) {
        this.units = units;
        return this;
    }

    public SpatialParameters.Units getUnits() {
        return units;
    }

    /**
     * PUBLIC:
     * Set the DISTANCE parameter
     * @param distance
     * @return this instance of SpatialParameters
     */
    public SpatialParameters setDistance(Number distance) {
        this.distance = distance;
        return this;
    }

    public Number getDistance() {
        return distance;
    }

    /**
     * PUBLIC:
     * Set the QUERY_TYPE parameter
     * @param queryType a value from the SpatialParameters.QueryType enum
     * @return this instance of SpatialParameters
     */
    public SpatialParameters setQueryType(QueryType queryType) {
        this.queryType = queryType;
        return this;
    }

    public SpatialParameters.QueryType getQueryType() {
        return queryType;
    }

    /**
     * PUBLIC:
     * Set the MASK parameter
     * @param masks an array of values from the SpatialParmeters.Mask enum
     * @return this instance of SpatialParameters
     */
    public SpatialParameters setMasks(Mask[] masks) {
        this.masks = masks;
        return this;
    }

    /**
     * PUBLIC:
     * Set the MASK parameter
     * @param mask a value from the SpatialParmeters.Mask enum
     * @return this instance of SpatialParameters
     */
    public SpatialParameters setMask(Mask mask) {
        this.masks = new Mask[] { mask };
        return this;
    }

    public Mask[] getMasks() {
        return masks;
    }

    /**
     * PUBLIC:
     * build a String describing this set of parameters that can be used in conjunction with an Oracle Spatial function
     * @return
     */
    public String getParameterString() {
        if (getParams() != null) {
            return getParams();
        }
        
        StringWriter writer = new StringWriter();
        boolean hasParams = false;

        hasParams = writeParam(writer, DISTANCE_PARAM, getDistance(), hasParams);
        hasParams = writeParam(writer, MAX_RES_PARAM, getMaxResolution(), hasParams);
        hasParams = writeParam(writer, MIN_RES_PARAM, getMinResolution(), hasParams);
        hasParams = writeParam(writer, UNIT_PARAM, getUnits(), hasParams);
        hasParams = writeParam(writer, MASK_PARAM, getMasks(), hasParams);
        hasParams = writeParam(writer, QUERYTYPE_PARAM, getQueryType(), hasParams);

        return hasParams ? writer.toString() : null;
    }


    /**
     * INTERNAL:
     * Write the parameter onto the StringWriter being used to construct the
     * params string. Only write something if there is a value and return
     * true indicating if a parameter is written.
     */
    private boolean writeParam(StringWriter writer, String paramName, Object paramValue, boolean hasParams) {
        if (paramValue != null) {
            if (hasParams) {
                writer.write(" ");
            }
            if (paramValue.getClass().isArray()) {
                Object[] values = (Object[])paramValue;

                if (values.length == 0) {
                    return false;
                }
                writer.write(paramName);
                writer.write(values[0].toString());
                for (int index = 1; index < values.length; index++) {
                    writer.write("+");
                    writer.write(values[index].toString());
                }

            } else {
                writer.write(paramName);
                writer.write(paramValue.toString());
            }
            return true;
        }
        return hasParams;
    }


    public enum Units {
        M, // Meter
        KM, // Kilometer
        CM, //Centimeter
        MM, // Millimeter
        MILE, // Mile
        NAUT_MILE, // Nautical Mile
        FOOT, // Foot
        INCH,
        ; // Inch
    }

    public enum QueryType {
        WINDOW,
        JOIN,
        FILTER,
        ;
    }

    public enum Mask {
        TOUCH,
        OVERLAPBDYDISJOINT,
        OVERLAPBDYINTERSECT,
        EQUAL,
        INSIDE,
        COVEREDBY,
        CONTAINS,
        COVERS,
        ANYINTERACT,
        ON,
        ;
    }
}
