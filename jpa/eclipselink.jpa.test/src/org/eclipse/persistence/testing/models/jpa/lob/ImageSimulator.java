/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.lob;

import java.util.Random;

/**
 * The object simulates a set of byte array, char array and string to create
 */
public class ImageSimulator {
    public static Image generateImageNullLOB() {
        Image imageNullLOB = new Image();
        imageNullLOB.setId(2000);
        imageNullLOB.setPicture(null);
        imageNullLOB.setScript(null);
        imageNullLOB.setAudio(null);
        imageNullLOB.setCommentary(null);
        imageNullLOB.setCustomAttribute1(null);
        imageNullLOB.setCustomAttribute2(null);

        return imageNullLOB;
    }

    public static Image generateImage(int blobSize, int clobSize) {
        Image generatedImage = new Image();
        generatedImage.setId(1000);
        generatedImage.setPicture(initObjectByteBase(blobSize));
        generatedImage.setScript(initStringBase(clobSize / 100));
        generatedImage.setAudio(initByteBase(blobSize));
        generatedImage.setCommentary(initCharArrayBase(clobSize));
        generatedImage.setCustomAttribute1(new SerializableNonEntity(new Long(Long.MAX_VALUE)));
        generatedImage.setCustomAttribute2(new SerializableNonEntity(new Long(Long.MAX_VALUE)));
        generatedImage.setXml1(new SerializableNonEntity(new Long(Long.MIN_VALUE)));
        generatedImage.setXml2(new SerializableNonEntity(new Long(Long.MIN_VALUE)));
        generatedImage.setJson1(new SerializableNonEntity(new Long(Long.MIN_VALUE)));
        generatedImage.setJson2(new SerializableNonEntity(new Long(Long.MIN_VALUE)));

        return generatedImage;
    }

    public static String initStringBase(int cycle) {
        StringBuffer base = new StringBuffer();
        for (int count = 0; count < cycle; count++) {
            base.append("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        }
        return base.toString();
    }

    public static char[] initCharArrayBase(int cycle) {
        char[] base = new char[cycle];
        for (int count = 0; count < cycle; count++) {
            base[count] = 'c';
        }
        return base;
    }

    public static byte[] initByteBase(int cycle) {
        byte[] pictures = new byte[cycle];
        new Random().nextBytes(pictures);
        return pictures;
    }

    public static Byte[] initObjectByteBase(int cycle) {
        byte[] pictures = new byte[cycle];
        new Random().nextBytes(pictures);
        Byte[] pics = new Byte[cycle];
        for (int x = 0; x < cycle; x++) {
            pics[x] = new Byte(pictures[x]);
        }
        return pics;
    }
}
