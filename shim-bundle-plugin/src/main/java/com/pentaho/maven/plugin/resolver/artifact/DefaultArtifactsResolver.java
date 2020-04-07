package com.pentaho.maven.plugin.resolver.artifact;

import com.pentaho.maven.plugin.resolver.api.DependencyResolverConfiguration;
import com.pentaho.maven.plugin.resolver.ResolverException;
import com.pentaho.maven.plugin.resolver.ResolverFilter;
import com.pentaho.maven.plugin.resolver.api.ArtifactResolver;
import com.pentaho.maven.plugin.resolver.util.FilterMaskTransformerUtil;
import com.pentaho.maven.plugin.resolver.util.FilterUtil;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.shared.artifact.filter.resolve.ScopeFilter;
import org.apache.maven.shared.artifact.filter.resolve.transform.ArtifactIncludeFilterTransformer;
import org.apache.maven.shared.artifact.resolve.ArtifactResult;
import org.apache.maven.shared.dependencies.resolve.DependencyResolverException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component( role = ArtifactResolver.class )
public class DefaultArtifactsResolver implements ArtifactResolver {
    @Requirement
    private org.apache.maven.shared.dependencies.resolve.DependencyResolver dependencyResolver;

    @Requirement
    private RepositorySystem resolver;

    public Set<Artifact> resolveArtifactsWithFilter( DependencyResolverConfiguration configuration,
                                                     ResolverFilter resolverFilter ) {

        Set<Artifact> resolvedArtifacts = new HashSet<Artifact>();

        if ( resolverFilter.getTransitive() ) {
            resolvedArtifacts.addAll( resolveTransitively( configuration, configuration.getMavenProject().getDependencyArtifacts() ).getArtifacts() );
        }
        else {
            resolvedArtifacts.addAll( resolveNonTransitively( configuration, configuration.getMavenProject().getArtifacts() ) );
        }

        FilterUtil.filterArtifacts( resolvedArtifacts, FilterMaskTransformerUtil.transformFilterMasksToList( resolverFilter.getInclude() ),
                FilterMaskTransformerUtil.transformFilterMasksToList( resolverFilter.getExclude() ),
                resolverFilter.getTransitive(), configuration.getLog() );

        return resolvedArtifacts;
    }

    public void resolveProjectBaseDependencies( DependencyResolverConfiguration configuration ) throws ResolverException {
        Set<Artifact> dependencyArtifacts = configuration.getMavenProject().getDependencyArtifacts();
        if ( dependencyArtifacts == null )
        {
            try
            {
                ProjectBuildingRequest pbr = new DefaultProjectBuildingRequest( configuration.getMavenSession().getProjectBuildingRequest() );
                pbr.setRemoteRepositories( configuration.getMavenProject().getRemoteArtifactRepositories() );
                Iterable<ArtifactResult> artifactResults =
                        dependencyResolver.resolveDependencies( pbr, configuration.getMavenProject().getModel(),
                                ScopeFilter.including("compile", "runtime"));

                dependencyArtifacts = new HashSet<Artifact>();

                for ( ArtifactResult artifactResult : artifactResults )
                {
                    configuration.getLog().info("Resolve artifact dependency - " + artifactResult.getArtifact().getArtifactId());
                    dependencyArtifacts.add( artifactResult.getArtifact() );
                }

                configuration.getMavenProject().setDependencyArtifacts( dependencyArtifacts );
            }
            catch ( final DependencyResolverException e )
            {
                throw new ResolverException("Failed to create dependency artifacts for resolution. Assembly: " , e );
            }
        }
    }

    private ArtifactResolutionResult resolveTransitively( DependencyResolverConfiguration configuration,
                                                          Set<Artifact> dependencyArtifacts ) {
        ArtifactResolutionRequest req = new ArtifactResolutionRequest();
        req.setLocalRepository( configuration.getLocalRepository() );
        req.setResolveRoot( false );
        req.setRemoteRepositories( aggregateRemoteArtifactRepositories( configuration.getMavenProject(), configuration.getRemoteRepositories() ) );
        req.setResolveTransitively( true );
        req.setArtifact( configuration.getMavenProject().getArtifact() );
        req.setArtifactDependencies( dependencyArtifacts );
        req.setManagedVersionMap( configuration.getMavenProject().getManagedVersionMap() );
        req.setCollectionFilter( new ArtifactIncludeFilterTransformer().transform( ScopeFilter.including( "compile", "runtime" ) ) );
        req.setOffline( configuration.getMavenSession().isOffline() );
        req.setForceUpdate( configuration.getMavenSession().getRequest().isUpdateSnapshots() );
        req.setServers( configuration.getMavenSession().getRequest().getServers() );
        req.setMirrors( configuration.getMavenSession().getRequest().getMirrors() );
        req.setProxies( configuration.getMavenSession().getRequest().getProxies() );

        ArtifactResolutionResult result;

        result = resolver.resolve( req );

        return result;
    }

    private List<ArtifactRepository> aggregateRemoteArtifactRepositories( MavenProject project,
                                                                          List<ArtifactRepository> remoteRepositories) {
        final List<List<ArtifactRepository>> repoLists = new ArrayList<List<ArtifactRepository>>();

        repoLists.add( remoteRepositories );
        repoLists.add( project.getRemoteArtifactRepositories() );

        final List<ArtifactRepository> remoteRepos = new ArrayList<ArtifactRepository>();
        final Set<String> encounteredUrls = new HashSet<String>();

        for ( final List<ArtifactRepository> repositoryList : repoLists )
        {
            if ( ( repositoryList != null ) && !repositoryList.isEmpty() )
            {
                for ( final ArtifactRepository repo : repositoryList )
                {
                    if ( !encounteredUrls.contains( repo.getUrl() ) )
                    {
                        remoteRepos.add( repo );
                        encounteredUrls.add( repo.getUrl() );
                    }
                }
            }
        }

        return remoteRepos;
    }

    private Set<Artifact> resolveNonTransitively( DependencyResolverConfiguration configuration,
                                                  Set<Artifact> dependencyArtifacts) {
        Set<Artifact> resolved = new HashSet<Artifact>();

        for ( final Artifact depArtifact : dependencyArtifacts )
        {
            ArtifactResolutionRequest req = new ArtifactResolutionRequest();
            req.setLocalRepository( configuration.getLocalRepository() );
            req.setRemoteRepositories( configuration.getRemoteRepositories() );
            req.setArtifact( depArtifact );

            ArtifactResolutionResult resolve = resolver.resolve( req );
            if ( resolve.hasExceptions() )
            {
                configuration.getLog().error( "Can't resolve artifact - " + depArtifact.getArtifactId() );
            }
            else
            {
                resolved.add( depArtifact );
            }
        }

        return resolved;
    }
}
