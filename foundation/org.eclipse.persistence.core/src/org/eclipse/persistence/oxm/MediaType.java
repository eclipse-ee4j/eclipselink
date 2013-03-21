/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.oxm;

/**
 * This enum represents the different media types supported by EclipseLink MOXy.
 * @since EclipseLink 2.4
 */
public enum MediaType implements org.eclipse.persistence.internal.oxm.MediaType {

    APPLICATION_XML("application/xml"), APPLICATION_JSON("application/json");

    private final String mediaType;

    private MediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public static MediaType getMediaType(String mediaType){
        if(APPLICATION_JSON.getMediaType().equals(mediaType)){
            return APPLICATION_JSON;
        }else if(APPLICATION_XML.getMediaType().equals(mediaType)){
            return APPLICATION_XML;
        }else{
            return null;
        }
    }

    public String getMediaType() {
        return mediaType;
    }

    @Override
    public boolean isApplicationJSON() {
        return this == APPLICATION_JSON;
    }

    @Override
    public boolean isApplicationXML() {
        return this == APPLICATION_XML;
    }

}