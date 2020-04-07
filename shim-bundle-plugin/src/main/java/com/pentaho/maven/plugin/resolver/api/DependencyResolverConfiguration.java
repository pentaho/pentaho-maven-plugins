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
