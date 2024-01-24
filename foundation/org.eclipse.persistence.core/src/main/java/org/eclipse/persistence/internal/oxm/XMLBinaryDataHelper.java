/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.oxm;

import jakarta.activation.DataHandler;
import jakarta.mail.internet.ContentType;
import jakarta.mail.internet.MimeMultipart;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XMLBinaryDataHelper {
    protected static XMLBinaryDataHelper binaryDataHelper;
    public Class<?> DATA_HANDLER;
    public Class<?> IMAGE;
    public Class<?> SOURCE;
    public Class<?> MULTIPART;

    public XMLBinaryDataHelper() {
        if (DATA_HANDLER == null) {
            initializeDataTypes();
        }
    }

    public static XMLBinaryDataHelper getXMLBinaryDataHelper() {
        if (binaryDataHelper == null) {
            setXMLBinaryDataHelper(new XMLBinaryDataHelper());
        }
        return binaryDataHelper;
    }

    public static void setXMLBinaryDataHelper(XMLBinaryDataHelper helper) {
        binaryDataHelper = helper;
    }

    public void initializeDataTypes() {
        //initialize types in this method to avoid loading them when the class is loaded
        DATA_HANDLER = jakarta.activation.DataHandler.class;
        IMAGE = java.awt.Image.class;
        SOURCE = javax.xml.transform.Source.class;
        MULTIPART = jakarta.mail.internet.MimeMultipart.class;
    }

    public Object convertObject(Object obj, Class<?> classification, CoreAbstractSession session, CoreContainerPolicy cp) {
        if( obj instanceof List theList && cp != null){
            Object container = cp.containerInstance(theList.size());
            for(int i=0; i<theList.size(); i++){
                Object next = theList.get(i);
                cp.addInto(convertSingleObject(next, classification, session), container, session);
            }
            return container;
        }
        return convertSingleObject(obj, classification, session);

    }
    public Object convertSingleObject(Object obj, Class<?> classification, CoreAbstractSession session) {
        if (classification == DATA_HANDLER) {
            return convertObjectToDataHandler(obj, session);
        } else if (classification == IMAGE) {
            return convertObjectToImage(obj);
        } else if (classification == SOURCE) {
            return convertObjectToSource(obj);
        } else if (classification == MULTIPART) {
            return convertObjectToMultipart(obj);
        } else {
            return session.getDatasourcePlatform().getConversionManager().convertObject(obj, classification);
        }
    }
    public EncodedData getBytesFromDataHandler(DataHandler handler) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            handler.writeTo(output);
        } catch (IOException ex) {
            throw ConversionException.couldNotBeConverted(handler, byte[].class, ex);
        }

        return new EncodedData(output.toByteArray(), handler.getContentType());
    }

    public List<byte[]> getBytesListForBinaryValues(List attributeValue, Marshaller marshaller, String mimeType){
        List returnList = new ArrayList(attributeValue.size());
        for(int i=0;i<attributeValue.size(); i++){
            Object next = attributeValue.get(i);
            EncodedData nextEncodedData = getBytesForSingleBinaryValue(next, marshaller, mimeType);
            returnList.add(nextEncodedData.getData());
        }
        return returnList;
    }

    public EncodedData getBytesForBinaryValue(Object attributeValue, Marshaller marshaller, String mimeType) {
        return getBytesForSingleBinaryValue(attributeValue, marshaller, mimeType);

    }

    public EncodedData getBytesForSingleBinaryValue(Object attributeValue, Marshaller marshaller, String mimeType) {

        if (attributeValue instanceof DataHandler) {
            return getBytesFromDataHandler((DataHandler) attributeValue);
        } else if (attributeValue instanceof Image) {
            return getBytesFromImage((Image) attributeValue, mimeType);
        } else if (attributeValue instanceof Source) {
            return getBytesFromSource((Source) attributeValue, marshaller, mimeType);
        } else if (attributeValue instanceof MimeMultipart) {
            return getBytesFromMultipart((MimeMultipart) attributeValue, marshaller);
        } else if (attributeValue.getClass() == CoreClassConstants.APBYTE) {
            return new EncodedData((byte[]) attributeValue, mimeType);
        } else if (attributeValue.getClass() == CoreClassConstants.ABYTE) {
            return getBytesFromByteObjectArray((Byte[]) attributeValue, mimeType);
        }

        return new EncodedData(new byte[0], null);
    }

    public EncodedData getBytesFromMultipart(MimeMultipart value, Marshaller marshaller) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ContentType contentType = new ContentType(value.getContentType());
            String boundary = contentType.getParameter("boundary");

            output.write(System.lineSeparator().getBytes());
            output.write(("Content-Type: " + contentType.getBaseType() + "; boundary=\"" + boundary + "\"\n").getBytes());
        } catch (Exception ex) {
            throw ConversionException.couldNotBeConverted(value, byte[].class, ex);
        }

        try {
            value.writeTo(output);
        } catch (Exception ex) {
            throw ConversionException.couldNotBeConverted(value, byte[].class, ex);
        }
        return new EncodedData(output.toByteArray(), value.getContentType());
    }

    public EncodedData getBytesFromSource(Source source, Marshaller marshaller, String mimeType) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        marshaller.getTransformer().transform(source, result);
        if (mimeType == null) {
            mimeType = "application/xml";
        }
        return new EncodedData(out.toByteArray(), mimeType);
    }

    public EncodedData getBytesFromByteObjectArray(Byte[] bytes, String mimeType) {
        byte[] data = new byte[bytes.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = bytes[i];
        }
        return new EncodedData(data, mimeType);
    }

    public EncodedData getBytesFromImage(Image image, String mimeType) {
        if ((mimeType == null) || mimeType.startsWith("image/*")) {
            mimeType = "image/png";
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Iterator<ImageWriter> itr = ImageIO.getImageWritersByMIMEType(mimeType);
            if (itr.hasNext()) {
                ImageWriter w = itr.next();
                w.setOutput(ImageIO.createImageOutputStream(outputStream));
                w.write(convertToBufferedImage(image));
                w.dispose();
            } else {
                throw XMLMarshalException.noEncoderForMimeType(mimeType);
            }

            return new EncodedData(outputStream.toByteArray(), mimeType);
        } catch (Exception ex) {
            throw ConversionException.couldNotBeConverted(image, byte[].class, ex);
        }
    }

    private BufferedImage convertToBufferedImage(Image image) throws IOException {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        } else {
            MediaTracker tracker = new MediaTracker(new Component() {
            });// not sure if this is the right thing to do.
            tracker.addImage(image, 0);
            try {
                tracker.waitForAll();
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage());
            }
            BufferedImage bufImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

            Graphics g = bufImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            return bufImage;
        }
    }

    public Object convertObjectToMultipart(Object obj) {
        if (obj instanceof MimeMultipart) {
            return obj;
        }
        if (obj instanceof DataHandler) {
            try {
                if (((DataHandler) obj).getContent() instanceof MimeMultipart multipart) {
                    return multipart;
                } else {
                    return new MimeMultipart(((DataHandler) obj).getDataSource());
                }
            } catch (Exception ex) {
                throw ConversionException.couldNotBeConverted(obj, MULTIPART, ex);
            }
        } else if (obj instanceof byte[]) {
            try {
                byte[] bytes = (byte[]) obj;
                java.io.InputStreamReader in = new java.io.InputStreamReader(new ByteArrayInputStream(bytes));
                int i = 0;
                while (i != -1) {
                    i = in.read();
                }
                return new MimeMultipart(new ByteArrayDataSource((byte[]) obj, "multipart/mixed"));
            } catch (Exception ex) {
                throw ConversionException.couldNotBeConverted(obj, MULTIPART, ex);
            }
        } else if (obj instanceof Byte[] objectBytes) {
            byte[] bytes = new byte[objectBytes.length];
            for (int i = 0; i < objectBytes.length; i++) {
                bytes[i] = objectBytes[i];
            }
            try {
                return new MimeMultipart(new ByteArrayDataSource(bytes, "multipart/mixed"));
            } catch (Exception ex) {
                throw ConversionException.couldNotBeConverted(obj, MULTIPART, ex);
            }
        }
        return null;
    }

    public Object convertObjectToImage(Object obj) {
        if (obj instanceof Image) {
            return obj;
        }
        if (obj instanceof DataHandler) {
            try {
                if (((DataHandler) obj).getContent() instanceof Image image) {
                    return image;
                } else {
                    return ImageIO.read(((DataHandler) obj).getInputStream());
                }
            } catch (Exception ex) {
                throw ConversionException.couldNotBeConverted(obj, IMAGE, ex);
            }
        } else if (obj instanceof byte[]) {
            ByteArrayInputStream stream = new ByteArrayInputStream((byte[]) obj);
            try {
                return ImageIO.read(stream);
            } catch (Exception ex) {
                throw ConversionException.couldNotBeConverted(obj, IMAGE, ex);
            }
        } else if (obj instanceof Byte[] objectBytes) {
            byte[] bytes = new byte[objectBytes.length];
            for (int i = 0; i < objectBytes.length; i++) {
                bytes[i] = objectBytes[i];
            }
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            try {
                return ImageIO.read(stream);
            } catch (Exception ex) {
                throw ConversionException.couldNotBeConverted(obj, IMAGE, ex);
            }
        }
        return null;
    }

    public String stringFromDataHandler(DataHandler source, QName schemaTypeQName, CoreAbstractSession session) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            source.writeTo(output);
            return ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(output.toByteArray(), String.class, schemaTypeQName);
        } catch (Exception ex) {
            throw ConversionException.couldNotBeConverted(source, CoreClassConstants.STRING, ex);
        }
    }

    public String stringFromDataHandler(Object source, QName schemaTypeQName, CoreAbstractSession session) {
        return stringFromDataHandler((DataHandler) source, schemaTypeQName, session);
    }

    public String stringFromImage(Image image, QName schemaTypeQName, CoreAbstractSession session) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            String mimeType = "image/png";
            Iterator<ImageWriter> itr = ImageIO.getImageWritersByMIMEType(mimeType);
            if (itr.hasNext()) {
                ImageWriter w = itr.next();
                w.setOutput(ImageIO.createImageOutputStream(outputStream));
                w.write(convertToBufferedImage(image));
                w.dispose();
            } else {
                throw XMLMarshalException.noEncoderForMimeType(mimeType);
            }

            return ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).buildBase64StringFromBytes(outputStream.toByteArray());
        } catch (Exception ex) {
            throw ConversionException.couldNotBeConverted(image, byte[].class, ex);
        }
    }

    public String stringFromSource(Source source, QName schemaTypeQName, CoreAbstractSession session) {
        DataHandler handler = new DataHandler(source, "text/xml");
        return stringFromDataHandler(handler, schemaTypeQName, session);
    }

    public String stringFromMultipart(MimeMultipart multipart, QName schemaTypeQName, CoreAbstractSession session) {
        DataHandler handler = new DataHandler(multipart, multipart.getContentType());
        return stringFromDataHandler(handler, schemaTypeQName, session);
    }

    public DataHandler convertObjectToDataHandler(Object sourceObject, CoreAbstractSession session) {
        DataHandler handler = null;
        if (sourceObject instanceof DataHandler) {
            return (DataHandler) sourceObject;
        } else if (sourceObject instanceof byte[] bytes) {
            handler = new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream"));

        } else if (sourceObject instanceof Byte[]) {
            byte[] bytes = (byte[]) session.getDatasourcePlatform().getConversionManager().convertObject(sourceObject, CoreClassConstants.APBYTE);
            handler = new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream"));
        }
        if (sourceObject instanceof String) {
            //assume base64 String
            byte[] bytes = ((ConversionManager) session.getDatasourcePlatform().getConversionManager()).convertSchemaBase64ToByteArray(sourceObject);
            handler = new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream"));
        } else if (sourceObject instanceof Image) {
            handler = new DataHandler(sourceObject, "image/jpeg");
        } else if (sourceObject instanceof Source) {
            handler = new DataHandler(sourceObject, "text/xml");
        } else if (sourceObject instanceof MimeMultipart) {
            handler = new DataHandler(sourceObject, ((MimeMultipart) sourceObject).getContentType());
        }
        return handler;
    }

    public Object convertObjectToSource(Object obj) {
        if(obj == null) {
            return null;
        }
        if (obj instanceof Source) {
            return obj;
        }
        if(obj.getClass() == CoreClassConstants.STRING) {
            return new StreamSource(new StringReader((String)obj));
        }
        if (obj instanceof DataHandler) {
            try {
                InputStream object = ((DataHandler)obj).getInputStream();
                return new StreamSource(object);
            } catch (Exception ex) {
                try {
                    Object object = ((DataHandler)obj).getContent();
                    convertObjectToSource(object);
                } catch(Exception ioException) {
                    throw ConversionException.couldNotBeConverted(obj, Source.class);
                }
            }
        } else if (obj instanceof byte[]) {
            return new ByteArraySource((byte[])obj);
        } else if (obj instanceof Byte[] objectBytes) {
            byte[] bytes = new byte[objectBytes.length];
            for (int i = 0; i < objectBytes.length; i++) {
                bytes[i] = objectBytes[i];
            }
            return new ByteArraySource(bytes);
        } else if(obj instanceof InputStream) {
            return new StreamSource((InputStream)obj);
        }
        return null;
    }

    // Made static final for performance reasons.
    /**
     * INTERNAL
     * @author mmacivor
     *
     */
    public static final class EncodedData {
        private byte[] data;
        private String mimeType;

        public EncodedData(byte[] bytes, String contentType) {
            data = bytes;
            mimeType = contentType;
        }

        public String getMimeType() {
            return mimeType;
        }

        public byte[] getData() {
            return data;
        }

        public void setMimeType(String type) {
            mimeType = type;
        }

        public void setData(byte[] bytes) {
            data = bytes;
        }
    }
}
