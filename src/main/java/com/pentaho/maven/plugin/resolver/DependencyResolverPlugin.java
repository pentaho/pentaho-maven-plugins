package com.pentaho.maven.plugin.resolver;

import com.pentaho.maven.plugin.resolver.api.ArtifactResolver;
import com.pentaho.maven.plugin.resolver.api.DependencyResolverConfiguration;
import com.pentaho.maven.plugin.resolver.util.ModifyParameterUtil;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.*;

import java.util.*;

@Mojo( name = "resolve", inheritByDefault = false, requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true )
public class DependencyResolverPlugin extends AbstractMojo implements DependencyResolverConfiguration {
    @Parameter( defaultValue = "", required = true )
    private List resolverFilters;

    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession mavenSession;

    @Parameter( defaultValue = "${localRepository}", required = true, readonly = true )
    private ArtifactRepository localRepository;

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    @Parameter( defaultValue = "${project.remoteArtifactRepositories}", required = true, readonly = true )
    private List<ArtifactRepository> remoteRepositories;

    @Component(role = ArtifactResolver.class)
    private ArtifactResolver artifactResolver;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if ( resolverFilters != null ) {
                artifactResolver.resolveProjectBaseDependencies( this );
                resolveArtifacts();
            }
            else {
                throw new MojoFailureException( "Invalid plugin configuration. Root configuration element must be resolverFilters." );
            }
        } catch (ResolverException e) {
            throw new MojoFailureException("Can't resolve dependencies. ", e);
        }
    }

    private void resolveArtifacts() {
        Set<Artifact> finalArtifacts = new HashSet<Artifact>();

        for ( Object object : resolverFilters ) {
            ResolverFilter resolverFilter = ModifyParameterUtil.modifyIfNeedResolverFilter(object, getLog());
            addArtifactsToFinalSet( artifactResolver.resolveArtifactsWithFilter( this, resolverFilter ), finalArtifacts );
        }

        project.setDependencyArtifacts( finalArtifacts );
    }

    private void addArtifactsToFinalSet( Set<Artifact> resolvedArtifacts, Set<Artifact> finalArtifacts ) {
        for ( Artifact artifact : resolvedArtifacts ) {
            if ( !finalArtifacts.contains( artifact ) ) {
                finalArtifacts.add( artifact );
            }
        }
    }

    public MavenProject getMavenProject() {
        return project;
    }

    public MavenSession getMavenSession() {
        return mavenSession;
    }

    public ArtifactRepository getLocalRepository() {
        return localRepository;
    }

    public List<ArtifactRepository> getRemoteRepositories() {
        return remoteRepositories;
    }
}
