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
package org.eclipse.persistence.testing.tests.lob;

import java.util.Random;

/**
 * The object simulates a set of byte array, char array and string to create
 * Image object to be used in the LOB test model.
 *
 * @see Image
 * @author King Wang (Aug. 2002)
 * @since TopLink/Java 5.0
 */
public class ImageSimulator {
    public static Image generateImageNullLOB() {
        Image imageNullLOB = new Image();
        imageNullLOB.setPicture(null);
        imageNullLOB.setScript(null);
        imageNullLOB.setAudio(null);
        imageNullLOB.setCommentary(null);

        return imageNullLOB;
    }

    public static Image generateImage(int blobSize, int clobSize) {
        Image generatedImage = new Image();
        //Bug#3128838  Test Byte[] support
        generatedImage.setPicture(initObjectByteBase(blobSize));
        generatedImage.setScript(initStringBase(clobSize / 100));
        generatedImage.setAudio(initByteBase(blobSize));
        generatedImage.setCommentary(initCharArrayBase(clobSize));

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
