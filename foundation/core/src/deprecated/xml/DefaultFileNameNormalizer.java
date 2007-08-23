/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;

import deprecated.xml.tools.EncodedWord;

/**
 * Default implementation of file name normalizer.
 * Replace any invalid characters with an escape sequence that looks
 * like this:<p>
 *     '/' => "&#x2f;"
 *
 * @see DefaultXMLFileAccessorFilePolicy
 *
 * @author Big Country
 * @since TOPLink/Java 4.5
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public class DefaultFileNameNormalizer implements FileNameNormalizer {

    /** A list of the invalid file name characters that will be morphed into escape sequences. */
    private String invalidFileNameCharacters;

    /** A list of the default invalid file name characters that will be morphed into escape sequences.
    <p>        \ is the filename separator in DOS/Windows and the escape character in Unix
    <p>        / is the filename separator in Unix and the command option tag in DOS
    <p>        : is the filename separator in MacOS and the drive indicator in DOS
    <p>        * is a DOS wildcard character
    <p>        ? is a DOS wildcard character
    <p>        " is used by DOS to delimit file names with spaces
    <p>        < is a DOS redirection character
    <p>        > is a DOS redirection character
    <p>        | is a DOS redirection character
    <p>        & is our own escape character
     */
    public static String DEFAULT_INVALID_FILE_NAME_CHARACTERS = "\\/:*?\"<>|&";

    /** Cache the value of the highest invalid file name character */
    private int maxInvalidFileNameChar;

    public DefaultFileNameNormalizer() {
        super();
        this.initialize();
    }

    // FileNameNormalizer implementation

    /*
     * @see FileNameNormalizer#normalize(String)
     */
    public String normalize(String unnormalizedFileName) {
        // allow for a few invalid characters
        StringBuffer sb = new StringBuffer(unnormalizedFileName.length() + 20);
        for (int i = 0; i < unnormalizedFileName.length(); i++) {
            this.normalizeFileNameCharOn(unnormalizedFileName.charAt(i), sb);
        }

        //added this call to make the filename i18n safe
        return EncodedWord.encode(sb.toString());
    }

    /**
     * Convert the specified (unqualified) file name character
     * into something that should be palatable in a file name.
     */
    protected void normalizeFileNameCharOn(char c, StringBuffer sb) {
        if (this.charIsInvalidForAFileName(c)) {
            this.normalizeInvalidFileNameCharOn(c, sb);
        } else {
            sb.append(c);
        }
    }

    /**
     * Return whether the specified character is an
     * invalid character for a file name.
     */
    protected boolean charIsInvalidForAFileName(int c) {
        return (c <= maxInvalidFileNameChar) && (invalidFileNameCharacters.indexOf(c) >= 0);
    }

    /**
     * Convert the specified invalid (unqualified) file name character
     * into something that should be palatable in a file name
     * (e.g. '/' => "&#x2f;").
     */
    protected void normalizeInvalidFileNameCharOn(char c, StringBuffer sb) {
        sb.append("&#x");
        sb.append(Integer.toString((int)c, 16));
        sb.append(';');
    }

    /**
     * Return a list of the invalid file name characters
     * that will be morphed into escape sequences.
     */
    public String getInvalidFileNameCharacters() {
        return invalidFileNameCharacters;
    }

    /**
     * Set the list of the invalid file name characters
     * that will be morphed into escape sequences.
     */
    public void setInvalidFileNameCharacters(String invalidFileNameCharacters) {
        this.invalidFileNameCharacters = invalidFileNameCharacters;
        this.calculateMaxInvalidFileNameChar();
    }

    /**
     *    Initialize the set of invalid file name characters.
     */
    protected void initialize() {
        this.setInvalidFileNameCharacters(this.buildDefaultInvalidFileNameCharacters());
    }

    /**
     *    Return the default set of invalid file name characters.
     */
    protected String buildDefaultInvalidFileNameCharacters() {
        return DEFAULT_INVALID_FILE_NAME_CHARACTERS;
    }

    /**
     * Calculate the maximum value of an invalid file name character.
     * This will be used to short-circuit the search for an
     * invalid file name character.
     * @see charIsInvalidForAFileName(int)
     */
    private void calculateMaxInvalidFileNameChar() {
        if (invalidFileNameCharacters == null) {
            throw new NullPointerException();
        }
        maxInvalidFileNameChar = 0;
        for (int i = 0; i < invalidFileNameCharacters.length(); i++) {
            char c = invalidFileNameCharacters.charAt(i);
            if (maxInvalidFileNameChar < c) {
                maxInvalidFileNameChar = c;
            }
        }
    }
}