package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * <b>Purpose:</b><p>English ResourceBundle for DBWSException.</p>
 */
public class DBWSExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
        {"47000", "Could not locate file [{0}]"},
        {"47001", "Could not locate descriptor [{0}] for operation [{1}] in the O-R project"},
        {"47002", "Could not locate query [{0}] for descriptor [{1}]"},
        {"47003", "Could not locate query [{0}] for session [{1}]"},
        {"47004", "Parameter type [{0}] for operation [{1}] does not exist in the schema"},
        {"47005", "Parameter type [{0}] for operation [{1}] has no O-X mapping"},
        {"47006", "Result type [{0}] for operation [{1}] does not exist in the schema"},
        {"47007", "Result type [{0}] for operation [{1}] has no O-X mapping"},
        {"47008", "Only Simple XML Format queries support multiple output arguments"},
        {"47009", "INOUT cursor parameters are not supported"},
        {"47010", "Could not locate O-R session for service [{0}]"},
        {"47011", "Could not locate O-X session for service [{0}]"},
    };

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
