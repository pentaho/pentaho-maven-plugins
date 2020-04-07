package com.pentaho.maven.plugin.resolver.util;

import org.apache.maven.artifact.Artifact;

import java.util.ArrayList;
import java.util.List;

public class FilterMaskTransformerUtil {
    public static List<String> transformFilterMasksToList( String filterMaskString ) {
        List<String> result;

        if ( !ParameterValidatorUtil.isNullAndEmptyStringParams(filterMaskString) ) {
            String[] splitFilter = filterMaskString.split( "," );
            result = new ArrayList<String>( splitFilter.length );

            for( String split : splitFilter ) {
                result.add( split.trim() );
            }
        }
        else {
            result = new ArrayList<String>();
        }

        return result;
    }

    public static String[] splitSingleMask( String filterMask ) {
        switch ( filterMask.split( ":" ).length ) {
            case 1 : return splitArtifactId( filterMask );
            case 2 : return splitGroupAndArtifactId( filterMask.split(":") );
            case 3 : return splitGroupAndArtifactAndTypeId( filterMask.split(":") );
            default: return filterMask.split(":");
        }
    }

    public static boolean filterWithMask(Artifact artifact, String[] filterMask ) {
        return filterWithMask( artifact.getGroupId(), filterMask[0].trim() ) && filterWithMask( artifact.getArtifactId(), filterMask[1].trim() )
                && filterWithMask( artifact.getType(), filterMask[2].trim() ) && filterWithMask( artifact.getClassifier(), filterMask[3].trim() );
    }

    private static boolean filterWithMask( String checkString, String filterMask ) {
        return filterMask.equals( "*" ) || checkString.matches( filterMask );
    }

    private static String[] splitArtifactId( String artifactId ) {
        return new String[]{ "*", artifactId, "*", "*" };
    }

    private static String[] splitGroupAndArtifactId( String[] split ) {
        return new String[]{ split[0], split[1], "*", "*" };
    }

    private static String[] splitGroupAndArtifactAndTypeId( String[] split ) {
        return new String[]{ split[0], split[1], split[2], "*" };
    }
}
