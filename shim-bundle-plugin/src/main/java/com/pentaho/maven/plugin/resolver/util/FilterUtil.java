package com.pentaho.maven.plugin.resolver.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.shared.artifact.filter.PatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.PatternIncludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.resolve.ScopeFilter;
import org.apache.maven.shared.artifact.filter.resolve.transform.ArtifactIncludeFilterTransformer;

import java.util.*;

public class FilterUtil {
    public static void filterArtifacts(final Set<Artifact> artifacts, final List<String> includes,
                                       final List<String> excludes, final boolean actTransitively,
                                       final Log log ) {
        final AndArtifactFilter filter = new AndArtifactFilter();

        filter.add( new ArtifactIncludeFilterTransformer().transform( ScopeFilter.including( "compile", "runtime" ) ) );

        if ( !includes.isEmpty() )
        {
            final ArtifactFilter includeFilter = new PatternIncludesArtifactFilter( includes, actTransitively );

            filter.add( includeFilter );
        }

        if ( !excludes.isEmpty() )
        {
            final ArtifactFilter excludeFilter = new PatternExcludesArtifactFilter( excludes, actTransitively );

            filter.add( excludeFilter );
        }

        for (final Iterator<Artifact> it = artifacts.iterator(); it.hasNext(); )
        {
            final Artifact artifact = it.next();

            if ( !filter.include( artifact ) )
            {
                it.remove();
            }
        }

        logFilterResult( artifacts, log );
    }

    private static void logFilterResult( Set<Artifact> resolvedArtifacts, final Log log ) {
        List<String> ids = new ArrayList<String>();
        for (Artifact artifact : resolvedArtifacts) {
            ids.add( artifact.getArtifactId() + ":" + artifact.getVersion() );
        }
        Collections.sort(ids);
        log.info( "Resolved artifacts: " );
        for (String id : ids) {
            log.info( id );
        }
    }
}
