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
