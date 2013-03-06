/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.exceptions;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;

public abstract class AbstractExceptionMapper {

    public static MediaType getMediaType(HttpHeaders headers) {
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        if (headers != null) {
            List<MediaType> accepts = headers.getAcceptableMediaTypes();
            if (accepts != null && accepts.size() > 0) {
                try {
                    mediaType = StreamingOutputMarshaller.mediaType(accepts);
                } catch (Exception ex) {}
            }
        }
        return mediaType;
    }
}
