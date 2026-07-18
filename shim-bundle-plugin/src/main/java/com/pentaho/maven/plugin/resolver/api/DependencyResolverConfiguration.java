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

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.List;

public interface DependencyResolverConfiguration {
    MavenProject getMavenProject();
    MavenSession getMavenSession();
    ArtifactRepository getLocalRepository();
    List<ArtifactRepository> getRemoteRepositories();
    Log getLog();
}
