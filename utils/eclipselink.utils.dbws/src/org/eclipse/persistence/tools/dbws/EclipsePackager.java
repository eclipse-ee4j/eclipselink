package org.eclipse.persistence.tools.dbws;

import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;

import java.io.File;
import java.io.FileNotFoundException;

public class EclipsePackager extends JDevPackager {
    
    public EclipsePackager() {
        super(null, "eclipse", noArchive);
    }

    public static final String PUBLIC_HTML_DIR = "WebContent";
    
    protected void buildPublicHTMLDir() throws FileNotFoundException {
        publicHTMLDir = new File(stageDir, PUBLIC_HTML_DIR);
        if (!publicHTMLDir.exists()) {
            boolean worked = publicHTMLDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    PUBLIC_HTML_DIR + " under " + stageDir);
            }
        }
    }
}
