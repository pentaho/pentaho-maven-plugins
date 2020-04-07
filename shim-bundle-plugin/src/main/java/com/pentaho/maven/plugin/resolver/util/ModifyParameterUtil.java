package com.pentaho.maven.plugin.resolver.util;

import com.pentaho.maven.plugin.resolver.ResolverFilter;
import org.apache.maven.plugin.logging.Log;

public class ModifyParameterUtil {
    public static ResolverFilter modifyIfNeedResolverFilter(Object resolverFilter, Log log) {
        ResolverFilter realResolverFilter;
        if ( ParameterValidatorUtil.isInstanceOfResolverFilter(resolverFilter) ) {
            realResolverFilter = ( ResolverFilter ) resolverFilter;
        }
        else {
            log.warn( "Object is not an instance of ResolverFilter.class, will be created default resolver filter." );
            realResolverFilter = new ResolverFilter();
        }

        modifyExcludes(realResolverFilter, log);
        modifyIncludes(realResolverFilter, log);
        modifyTransitive(realResolverFilter, log);

        return realResolverFilter;
    }

    private static void modifyExcludes( ResolverFilter resolverFilter, Log log ) {
        if ( ParameterValidatorUtil.isNullAndEmptyStringParams(resolverFilter.getExclude()) ) {
            log.warn( "Resolver filter has empty exclude section, nothing will be exclude." );
            resolverFilter.setExclude( "" );
        }
    }

    private static void modifyIncludes( ResolverFilter resolverFilter, Log log ) {
        if ( ParameterValidatorUtil.isNullAndEmptyStringParams(resolverFilter.getInclude()) ) {
            log.warn( "Resolver filter has empty include section, everything will be include." );
            resolverFilter.setInclude( "*" );
        }
    }

    private static void modifyTransitive( ResolverFilter resolverFilter, Log log ) {
        if ( ParameterValidatorUtil.isNullParameter(resolverFilter.getTransitive()) ) {
            log.warn( "Resolver filter has empty transitive section, will use non transitive search." );
            resolverFilter.setTransitive(false);
        }
    }
}
