/**
 * 
 */
package org.opf_labs.project.healthcheck;

import org.eclipse.egit.github.core.Repository;

import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc for Indicators.</p>
 * TODO Tests for Indicators.</p>
 * TODO Implementation for Indicators.</p>
 * 
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 * Created 10 Jul 2013:10:44:23
 */
public final class GitHubProject {
	final Repository repo;
	final Indicators indicators;
	final CiInfo ci;

	private GitHubProject() {
		throw new AssertionError();
	}

	private GitHubProject(final Repository repo, final Indicators indicators,
			CiInfo ci) {
		this.repo = repo;
		if (this.repo.getLanguage() == null)
			this.repo.setLanguage("unknown");
		this.indicators = indicators;
		this.ci = ci;
	}

	/**
	 * Factory method for GitHubProject class.
	 * 
	 * @param repo
	 *            the Repository instance for the GitHubProject
	 * @param indicators
	 *            the project health indicator object
	 * @param ci
	 *            the project Continuous Integration information
	 * @return a new instance created from the pass parameters
	 */
	public final static GitHubProject newInstance(final Repository repo,
			final Indicators indicators, CiInfo ci) {
		Preconditions.checkNotNull(repo, "repos == null");
		Preconditions.checkNotNull(indicators, "indicators == null");
		Preconditions.checkNotNull(ci, "ci == null");
		return new GitHubProject(repo, indicators, ci);
	}

	/**
	 * TODO JavaDoc for Indicators.</p>
	 * TODO Tests for Indicators.</p>
	 * TODO Implementation for Indicators.</p>
	 * 
	 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
	 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
	 * @version 0.1
	 * 
	 * Created 10 Jul 2013:10:44:23
	 */
	public static final class Indicators {
		final String readMeUrl;
		final String licenseUrl;
		final String metadataUrl;

		@SuppressWarnings("unused")
		private Indicators() {
			throw new AssertionError();
		}

		Indicators(final String readMeUrl, final String licenseUrl,
				final String metadataUrl) {
			this.readMeUrl = readMeUrl;
			this.licenseUrl = licenseUrl;
			this.metadataUrl = metadataUrl;
		}
	}

	/**
	 * TODO JavaDoc for CiInfo.</p>
	 * TODO Tests for CiInfo.</p>
	 * TODO Implementation for CiInfo.</p>
	 * 
	 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
	 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
	 * @version 0.1
	 * 
	 * Created 10 Jul 2013:10:45:08
	 */
	public static final class CiInfo {
		final boolean hasTravis;

		@SuppressWarnings("unused")
		private CiInfo() {
			throw new AssertionError();
		}

		CiInfo(final boolean hasTravis) {
			this.hasTravis = hasTravis;
		}
	}
}
