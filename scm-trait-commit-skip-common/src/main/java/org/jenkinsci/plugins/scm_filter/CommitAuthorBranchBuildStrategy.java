package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.regex.Pattern;

import hudson.model.TaskListener;
import jenkins.branch.BranchBuildStrategy;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceOwner;

/**
 * A strategy for avoiding automatic builds for commits with authors that contain a specific pattern.
 */
public abstract class CommitAuthorBranchBuildStrategy extends BranchBuildStrategy {

    private final String pattern;

    private transient Pattern compiledPattern;

    public static String getDisplayName() {
        return Messages.CommitAuthorBranchBuildStrategy_DisplayName();
    }

    public String getPattern() {
        return pattern;
    }

    public Pattern getCompiledPattern() {
        if (compiledPattern == null) {
            compiledPattern = Pattern.compile(pattern);
        }
        return compiledPattern;
    }

    public abstract String getAuthor(SCMSource source, SCMRevision revision) throws CouldNotGetCommitDataException;

    public CommitAuthorBranchBuildStrategy(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean isAutomaticBuild(@NonNull SCMSource source,
                                    @NonNull SCMHead head,
                                    @NonNull SCMRevision currRevision,
                                    @CheckForNull SCMRevision lastBuiltRevision,
                                    @CheckForNull SCMRevision lastSeenRevision,
                                    @NonNull TaskListener listener) {
        String author = null;
        try {
            author = getAuthor(source, currRevision);
        } catch (CouldNotGetCommitDataException e) {
            listener.error("Could not attempt to prevent automatic build by commit author pattern "
                    + "because of an error when fetching the commit author: %s", e);
            return true;
        }
        if (author == null) {
            listener.getLogger().println("Could not attempt to prevent automatic build by commit author pattern "
                    + "because commit author is null");
            return true;
        }
        if (getCompiledPattern().matcher(author).find()) {
            String ownerDisplayName = "Global";
            SCMSourceOwner owner = source.getOwner();
            if (owner != null) {
                ownerDisplayName = owner.getDisplayName();
            }
            listener.getLogger().format("Automatic build prevented for job [{}] because commit author [{}] "
                    + "matched expression [{}]%n", ownerDisplayName, author, pattern);
            return false;
        }
        return true;
    }
}
