/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.oxm;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.XMLMarshaller;

public class XMLBinaryDataHelper {
    protected static XMLBinaryDataHelper binaryDataHelper;
    public Class DATA_HANDLER;
    public Class IMAGE;
    public Class SOURCE;
    public Class MULTIPART;

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
        DATA_HANDLER = javax.activation.DataHandler.class;
        IMAGE = java.awt.Image.class;
        SOURCE = javax.xml.transform.Source.class;
        MULTIPART = javax.mail.internet.MimeMultipart.class;
    }

    public Object convertObject(Object obj, Class classification, AbstractSession session) {
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
        }

        return new EncodedData(output.toByteArray(), handler.getContentType());
    }

    public EncodedData getBytesForBinaryValue(Object attributeValue, XMLMarshaller marshaller, String mimeType) {
        if (attributeValue instanceof DataHandler) {
            return getBytesFromDataHandler((DataHandler) attributeValue);
        } else if (attributeValue instanceof Image) {
            return getBytesFromImage((Image) attributeValue, mimeType);
        } else if (attributeValue instanceof Source) {
            return getBytesFromSource((Source) attributeValue, marshaller, mimeType);
        } else if (attributeValue instanceof MimeMultipart) {
            return getBytesFromMultipart((MimeMultipart) attributeValue, marshaller);
        } else if (attributeValue.getClass() == ClassConstants.APBYTE) {
            return new EncodedData((byte[]) attributeValue, mimeType);
        } else if (attributeValue.getClass() == ClassConstants.ABYTE) {
            return getBytesFromByteObjectArray((Byte[]) attributeValue, mimeType);
        }

        return new EncodedData(new byte[0], null);
    }

    public EncodedData getBytesFromMultipart(MimeMultipart value, XMLMarshaller marshaller) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            ContentType contentType = new ContentType(value.getContentType());
            String boundary = contentType.getParameter("boundary");

            //output.write(boundary.getBytes());
            output.write(Helper.cr().getBytes());
            output.write(("Content-Type: " + contentType.getBaseType() + "; boundary=\"" + boundary + "\"\n").getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            //value.writeTo(System.out);
            value.writeTo(output);

            /*java.io.InputStreamReader in = new java.io.InputStreamReader(new ByteArrayInputStream(output.toByteArray()));
            int i = 0;
            while(i != -1) {
                System.out.print((char)i);
                i = in.read();
            }*/
        } catch (Exception ex) {
            //handle error
        }
        return new EncodedData(output.toByteArray(), value.getContentType());
    }

    public EncodedData getBytesFromSource(Source source, XMLMarshaller marshaller, String mimeType) {
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
            data[i] = bytes[i].byteValue();
        }

        return new EncodedData(data, mimeType);
    }

    public EncodedData getBytesFromImage(Image image, String mimeType) {
        if ((mimeType == null) || mimeType.startsWith("image/*")) {
            mimeType = "image/png";
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Iterator itr = ImageIO.getImageWritersByMIMEType(mimeType);
            if (itr.hasNext()) {
                ImageWriter w = (ImageWriter) itr.next();
                w.setOutput(ImageIO.createImageOutputStream(outputStream));
                w.write(convertToBufferedImage(image));
                w.dispose();
            } else {
                throw XMLMarshalException.noEncoderForMimeType(mimeType);
            }

            return new EncodedData(outputStream.toByteArray(), mimeType);
        } catch (Exception ex) {
        }

        return new EncodedData(new byte[0], null);
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
                if (((DataHandler) obj).getContent() instanceof MimeMultipart) {
                    MimeMultipart multipart = (MimeMultipart) ((DataHandler) obj).getContent();
                    return multipart;
                } else {
                    return new MimeMultipart(((DataHandler) obj).getDataSource());
                }
            } catch (Exception ex) {
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
                ex.printStackTrace();
                return null;
            }
        } else if (obj instanceof Byte[]) {
            Byte[] objectBytes = (Byte[]) obj;
            byte[] bytes = new byte[objectBytes.length];
            for (int i = 0; i < objectBytes.length; i++) {
                bytes[i] = objectBytes[i].byteValue();
            }
            try {
                return new MimeMultipart(new ByteArrayDataSource(bytes, "multipart/mixed"));
            } catch (Exception ex) {
                return null;
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
                if (((DataHandler) obj).getContent() instanceof Image) {
                    Image image = (Image) ((DataHandler) obj).getContent();
                    return image;
                } else {
                    return ImageIO.read(((DataHandler) obj).getInputStream());
                }
            } catch (Exception ex) {
            }
        } else if (obj instanceof byte[]) {
            ByteArrayInputStream stream = new ByteArrayInputStream((byte[]) obj);
            try {
                return ImageIO.read(stream);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        } else if (obj instanceof Byte[]) {
            Byte[] objectBytes = (Byte[]) obj;
            byte[] bytes = new byte[objectBytes.length];
            for (int i = 0; i < objectBytes.length; i++) {
                bytes[i] = objectBytes[i].byteValue();
            }
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            try {
                return ImageIO.read(stream);
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    public String stringFromDataHandler(DataHandler source, QName schemaTypeQName, AbstractSession session) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            source.writeTo(output);
            return (String) ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(output.toByteArray(), String.class, schemaTypeQName);
        } catch (Exception ex) {
        }

        return null;
    }

    public String stringFromDataHandler(Object source, QName schemaTypeQName, AbstractSession session) {
        return stringFromDataHandler((DataHandler) source, schemaTypeQName, session);
    }

    public String stringFromImage(Image image, QName schemaTypeQName, AbstractSession session) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            String mimeType = null;
            if ((mimeType == null) || mimeType.startsWith("image/*")) {
                mimeType = "image/png";
            }
            Iterator itr = ImageIO.getImageWritersByMIMEType(mimeType);
            if (itr.hasNext()) {
                ImageWriter w = (ImageWriter) itr.next();
                w.setOutput(ImageIO.createImageOutputStream(outputStream));
                w.write(convertToBufferedImage(image));
                w.dispose();
            } else {
                throw new RuntimeException("no encoder for MIME type " + mimeType);
            }

            return ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).buildBase64StringFromBytes(outputStream.toByteArray());
        } catch (Exception ex) {
        }

        return null;
    }

    public String stringFromSource(Source source, QName schemaTypeQName, AbstractSession session) {
        DataHandler handler = new DataHandler(source, "text/xml");
        return stringFromDataHandler(handler, schemaTypeQName, session);
    }

    public String stringFromMultipart(MimeMultipart multipart, QName schemaTypeQName, AbstractSession session) {
        DataHandler handler = new DataHandler(multipart, multipart.getContentType());
        return stringFromDataHandler(handler, schemaTypeQName, session);
    }

    public DataHandler convertObjectToDataHandler(Object sourceObject, AbstractSession session) {
        DataHandler handler = null;
        if (sourceObject instanceof DataHandler) {
            return (DataHandler) sourceObject;
        } else if (sourceObject instanceof byte[]) {
            byte[] bytes = (byte[]) sourceObject;
            handler = new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream"));

        } else if (sourceObject instanceof Byte[]) {
            byte[] bytes = (byte[]) session.getDatasourcePlatform().getConversionManager().convertObject(sourceObject, ClassConstants.APBYTE);
            handler = new DataHandler(new ByteArrayDataSource(bytes, "application/octet-stream"));
        }
        if (sourceObject instanceof String) {
            //assume base64 String
            byte[] bytes = ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertSchemaBase64ToByteArray(sourceObject);
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
        if (obj instanceof Source) {
            return obj;
        }
        if (obj instanceof DataHandler) {
            try {
                Source source = (Source) ((DataHandler) obj).getContent();
                return source;
            } catch (Exception ex) {
            }
        } else if (obj instanceof byte[]) {
            ByteArrayInputStream stream = new ByteArrayInputStream((byte[]) obj);
            return new StreamSource(stream);
        } else if (obj instanceof Byte[]) {
            Byte[] objectBytes = (Byte[]) obj;
            byte[] bytes = new byte[objectBytes.length];
            for (int i = 0; i < objectBytes.length; i++) {
                bytes[i] = objectBytes[i].byteValue();
            }
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            return new StreamSource(stream);
        }
        return null;
    }

    /**
     * INTERNAL
     * @author mmacivor
     *
     */
    public class EncodedData {
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
