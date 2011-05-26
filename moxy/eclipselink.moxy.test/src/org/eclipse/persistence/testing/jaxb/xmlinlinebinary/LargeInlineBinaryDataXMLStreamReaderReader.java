/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlinlinebinary;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.activation.DataHandler;
import javax.mail.internet.MimeMultipart;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.internal.oxm.record.XMLStreamReaderReader;
import org.xml.sax.SAXException;

public class LargeInlineBinaryDataXMLStreamReaderReader extends XMLStreamReaderReader {

    static Image IMAGE = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB);
    static DataHandler DATA_HANDLER = new DataHandler(IMAGE, "iamge/jpg");
    static Source SOURCE  = new StreamSource(System.in);
    static MimeMultipart MIME_MULTIPART = new MimeMultipart();
    static LargeInlineBinaryCharSequence CHAR_SEQUENCE = new LargeInlineBinaryCharSequence();

    @Override
    protected void parseCharactersEvent(XMLStreamReader xmlStreamReader) throws SAXException {
        contentHandler.characters(CHAR_SEQUENCE);
    }

    @Override
    public Object getValue(CharSequence characters, Class<?> dataType) {
        return ((LargeInlineBinaryCharSequence) characters).getValue(dataType);
    }

    private static class LargeInlineBinaryCharSequence implements CharSequence {

        public int length() {
            return 0;
        }

        public char charAt(int index) {
            return 0;
        }

        public CharSequence subSequence(int start, int end) {
            return null;
        }

        public Object getValue(Class<?> clazz) {
            if(DataHandler.class == clazz) {
                return DATA_HANDLER;
            } else if(Image.class == clazz) {
                return IMAGE;
            } else if(Source.class == clazz) {
                return SOURCE;
            } else if(MimeMultipart.class == clazz) {
                return MIME_MULTIPART;
            } else {
                return null;
            }
        }
    }

}