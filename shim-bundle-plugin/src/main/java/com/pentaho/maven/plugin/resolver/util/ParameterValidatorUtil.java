package com.pentaho.maven.plugin.resolver.util;

import com.pentaho.maven.plugin.resolver.ResolverFilter;

public class ParameterValidatorUtil {
    public static boolean isInstanceOfResolverFilter( Object resolverFilter ) {
        return resolverFilter instanceof ResolverFilter;
    }

    public static boolean isNullParameter( Object... params ) {
        return params == null || isNullParameterInArray(params);
    }

    public static boolean isNullAndEmptyStringParams( String... params ) {
        return params == null || isNullParameterStringArray(params);
    }

    private static boolean isNullParameterStringArray( String... params ) {
        for (String param : params) {
            if ( param == null || "".equals( param ) ) {
                return true;
            }
        }

        return false;
    }

    private static boolean isNullParameterInArray( Object... params ) {
        for (Object param : params) {
            if ( param == null ) {
                return true;
            }
        }

        return false;
    }
}
