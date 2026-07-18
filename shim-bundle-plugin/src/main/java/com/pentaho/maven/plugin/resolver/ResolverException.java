/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/
package com.pentaho.maven.plugin.resolver;

public class ResolverException extends Exception {
    public ResolverException() {
    }

    public ResolverException(String message) {
        super(message);
    }

    public ResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolverException(Throwable cause) {
        super(cause);
    }

    public ResolverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
