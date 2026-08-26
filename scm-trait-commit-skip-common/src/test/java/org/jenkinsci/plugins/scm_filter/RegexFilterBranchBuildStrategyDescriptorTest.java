package org.jenkinsci.plugins.scm_filter;

import hudson.util.FormValidation;
import org.junit.Test;

import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;

import static org.junit.Assert.assertEquals;

/**
 * Covers the pattern validation in {@link RegexFilterBranchBuildStrategyDescriptor},
 * previously implemented with Commons Lang 2 StringUtils.isBlank.
 */
public class RegexFilterBranchBuildStrategyDescriptorTest {

	/**
	 * Minimal strategy/descriptor pair mirroring how the Bitbucket and GitHub
	 * modules subclass {@link RegexFilterBranchBuildStrategyDescriptor}: the
	 * descriptor must be an inner class of a describable for
	 * {@link hudson.model.Descriptor}'s outer-class inference to work.
	 */
	public static class TestStrategy extends CommitMessageBranchBuildStrategy {
		public TestStrategy(String pattern) {
			super(pattern);
		}

		@Override
		public String getMessage(SCMSource source, SCMRevision revision) {
			return "";
		}

		public static class DescriptorImpl extends RegexFilterBranchBuildStrategyDescriptor {
		}
	}

	private final RegexFilterBranchBuildStrategyDescriptor descriptor = new TestStrategy.DescriptorImpl();

	@Test
	public void nullPatternIsRejected() {
		assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckPattern(null).kind);
	}

	@Test
	public void emptyPatternIsRejected() {
		assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckPattern("").kind);
	}

	@Test
	public void whitespaceOnlyPatternIsRejected() {
		assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckPattern("   ").kind);
	}

	@Test
	public void invalidRegexIsRejected() {
		assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckPattern("[unclosed").kind);
	}

	@Test
	public void validPatternIsAccepted() {
		assertEquals(FormValidation.Kind.OK, descriptor.doCheckPattern("^release/.*$").kind);
	}
}
