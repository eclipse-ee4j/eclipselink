/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - initial implementation
package org.eclipse.persistence.jpa.rs.exceptions;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.jpa.rs.ReservedWords;

/*
 *
 * Model class for marshaling error response
 *
 */
@XmlRootElement(name = ReservedWords.ERROR_RESPONSE_LABEL)
@XmlType(propOrder = { "problemType", "title", "httpStatus", "detail", "problemInstance", "errorCode", "errorPath", "errorDetails", "requestId" })
/*
 *    {
 *        "$schema": "http://json-schema.org/draft-04/schema#",
 *        "id": "rest-schemas/exception#",
 *        "title": "Error Detail",
 *        "description": "This complex type is used to capture the error detail during REST calls",
 *        "type": "object",
 *        "properties": {
 *              "problemType": {
 *                   "type": "string",
 *                   "description": "An absolute URI that identifies the problem type.  When dereferenced, it SHOULD provide human-readable documentation for the problem type (e.g., using HTML)."
 *               },
 *               "title": {
 *                   "type": "string",
 *                   "description": "A short, human-readable summary of the problem type.  It SHOULD NOT change from occurrence to occurrence of the problem, except for purposes of localisation."
 *               },
 *               "httpStatus": {
 *                   "type": "integer",
 *                   "description": "The HTTP status code set by the origin server for this occurrence of the problem."
 *               },
 *               "detail": {
 *                   "type": "string",
 *                    "description": "An human readable explanation specific to this occurrence of the problem."
 *               },
 *               "problemInstance": {
 *                   "type": "string",
 *                    "description": "An absolute URI that identifies the specific occurrence of the problem.  It may or may not yield further information if dereferenced."
 *               },
 *               "errorCode": {
 *                   "type": "string",
 *                    "description": "application error code, which is different from HTTP error code"
 *               },
 *               "errorPath": {
 *                   "type": "string",
 *                   "description": "path to the problem, it could be at a resource level, or maybe at property level"
 *               },
 *               "errorDetails": {
 *                   "$ref": "rest-schemas/exception#",
 *                   "description": "contains details of the error message. This is basically a hierarchical tree structure."
 *               }
 *        },
 *        "required": ["problemType", "title"]
 *    }
 */
public class ErrorResponse {
    private String problemType; // mandatory
    private String title; // mandatory
    private int httpStatus;
    private String detail;
    private String problemInstance;
    private String errorCode;
    private String errorPath;
    private ErrorResponse errorDetails;
    private String requestId;

    /**
     * Instantiates a new error response.
     */
    public ErrorResponse() {
    }

    /**
     * Instantiates a new error response.
     *
     * @param problemType the problem type
     * @param title the title
     * @param errorCode the error code
     */
    public ErrorResponse(String problemType, String title, String errorCode) {
        this.problemType = problemType;
        this.title = title;
        this.errorCode = errorCode;
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
    public int getHttpStatus() {
        return httpStatus;
    }

    /**
     * Sets the http status.
     *
     * @param httpStatus the new http status
     */
    public void setHttpStatus(int httpStatus) {
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
    public ErrorResponse getErrorDetails() {
        return errorDetails;
    }

    /**
     * Sets the error details.
     *
     * @param errorDetails the new error details
     */
    public void setErrorDetails(ErrorResponse errorDetails) {
        this.errorDetails = errorDetails;
    }

    /**
     * Gets the request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the request unique id.
     *
     * @param requestId the new request unique id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
