package com.pentaho.maven.plugin.resolver.api;

import com.pentaho.maven.plugin.resolver.ResolverException;
import com.pentaho.maven.plugin.resolver.ResolverFilter;
import org.apache.maven.artifact.Artifact;

import java.util.Set;

public interface ArtifactResolver {
    Set<Artifact> resolveArtifactsWithFilter(DependencyResolverConfiguration configuration,
                                             ResolverFilter resolverFilter );
    void resolveProjectBaseDependencies( DependencyResolverConfiguration configuration ) throws ResolverException;
}
