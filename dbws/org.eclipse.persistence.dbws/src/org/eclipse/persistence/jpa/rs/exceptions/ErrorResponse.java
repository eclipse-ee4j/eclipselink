/*******************************************************************************
 * Copyright (c) 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      gonural - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.exceptions;

import javax.xml.bind.annotation.XmlRootElement;

/*
 * 
 * Model class for marshaling error response
 * 
 */
@XmlRootElement(name = "errorResponse")
public class ErrorResponse {
    private String problemType; // mandatory
    private String title; // mandatory
    private Integer httpStatus;
    private String detail;
    private String problemInstance;
    private String errorCode;
    private String errorPath;
    private String errorDetails;

    public ErrorResponse(String problemType, String title) {
        super();
        this.problemType = problemType;
        this.title = title;
    }

    /**
     * Gets the problem type.
     *
     * @return the problem type
     */
    public String getProblemType() {
        return problemType;
    }

    /**
     * Sets the problem type.
     *
     * @param problemType the new problem type
     */
    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the http status.
     *
     * @return the http status
     */
    public Integer getHttpStatus() {
        return httpStatus;
    }

    /**
     * Sets the http status.
     *
     * @param httpStatus the new http status
     */
    public void setHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
    }

    /**
     * Gets the detail.
     *
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Sets the detail.
     *
     * @param detail the new detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * Gets the problem instance.
     *
     * @return the problem instance
     */
    public String getProblemInstance() {
        return problemInstance;
    }

    /**
     * Sets the problem instance.
     *
     * @param problemInstance the new problem instance
     */
    public void setProblemInstance(String problemInstance) {
        this.problemInstance = problemInstance;
    }

    /**
     * Gets the error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the new error code
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the error path.
     *
     * @return the error path
     */
    public String getErrorPath() {
        return errorPath;
    }

    /**
     * Sets the error path.
     *
     * @param errorPath the new error path
     */
    public void setErrorPath(String errorPath) {
        this.errorPath = errorPath;
    }

    /**
     * Gets the error details.
     *
     * @return the error details
     */
    public String getErrorDetails() {
        return errorDetails;
    }

    /**
     * Sets the error details.
     *
     * @param errorDetails the new error details
     */
    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }
}
