/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.lob;

/**
 * The domain object for the LOB test model, which is used to test 
 * the TopLink BLOB/CLOB support.
 *
 * @see LOBTestModel
 * @author King Wang (Aug. 2002)
 * @since TopLink/Java 5.0
 */
public class Image implements Cloneable {
    private int id;
    private byte[] audio;
    private char[] commentary;
    //Bug#3128838  Test Byte[] support
    private Byte[] picture;
    private String script;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public byte[] getAudio() {
        return audio;
    }

    public char[] getCommentary() {
        return commentary;
    }

    public int getId() {
        return id;
    }

    public Byte[] getPicture() {
        return picture;
    }

    public String getScript() {
        return script;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }

    public void setCommentary(char[] commentary) {
        this.commentary = commentary;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPicture(Byte[] picture) {
        this.picture = picture;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String printout() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Image(");
        buffer.append(id + ")");
        buffer.append("\tPicture size = ");
        if (picture != null) {
            buffer.append(picture.length);
        } else {
            //"-1" means NULL, it is different than size 0, which could be significant empty space.
            buffer.append("-1");
        }

        buffer.append("\tScript  size = ");
        if (script != null) {
            buffer.append(script.length());
        } else {
            buffer.append("-1");
        }

        buffer.append("\tAudio size = ");
        if (audio != null) {
            buffer.append(audio.length);
        } else {
            buffer.append("-1");
        }

        buffer.append("\tCommentary size = ");
        if (commentary != null) {
            buffer.append(commentary.length);
        } else {
            buffer.append("-1");
        }
        return buffer.toString();
    }
}
