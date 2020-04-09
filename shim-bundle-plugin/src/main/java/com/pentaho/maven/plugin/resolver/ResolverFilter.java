package com.pentaho.maven.plugin.resolver;

public class ResolverFilter {
    private String include;
    private String exclude;
    private Boolean transitive;

    public String getInclude() {
        return include;
    }

    public String getExclude() {
        return exclude;
    }

    public Boolean getTransitive() {
        return transitive;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public void setTransitive(Boolean transitive) {
        this.transitive = transitive;
    }
}
