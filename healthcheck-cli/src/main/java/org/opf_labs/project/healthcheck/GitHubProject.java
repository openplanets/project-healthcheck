/**
 * 
 */
package org.opf_labs.project.healthcheck;

import org.eclipse.egit.github.core.Repository;

import com.google.common.base.Preconditions;

/**
 * @author carl
 * 
 */
public final class GitHubProject {
	public final Repository repo;
	public final Indicators indicators;
	public final CiInfo ci;

	private GitHubProject() {
		throw new AssertionError();
	}

	private GitHubProject(final Repository repo, final Indicators indicators, CiInfo ci) {
		this.repo = repo;
		if (this.repo.getLanguage() == null) this.repo.setLanguage("unknown");
		this.indicators = indicators;
		this.ci = ci;
	}

	public final static GitHubProject newInstance(final Repository repo, final Indicators indicators, CiInfo ci) {
		Preconditions.checkNotNull(repo, "repos == null");
		Preconditions.checkNotNull(indicators, "indicators == null");
		Preconditions.checkNotNull(ci, "ci == null");
		return new GitHubProject(repo, indicators, ci);
	}

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
