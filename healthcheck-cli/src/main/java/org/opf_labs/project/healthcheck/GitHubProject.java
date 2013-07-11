/**
 * 
 */
package org.opf_labs.project.healthcheck;

import java.util.Date;

import org.eclipse.egit.github.core.Repository;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

/**
 * Class to hold immutable GitHub project details. This class is simply a
 * property bean, the hard work is currently done in the CLI class.
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 *          Created 10 Jul 2013:10:44:23
 */
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NON_PRIVATE)
public final class GitHubProject {
	final String name;
	final String description;
	final String ownerLogin;
	final String url;
	final String language;
	final Date updated;
	final int openIssues;
	final ProjectMetadata opfMetadata;
	final Indicators indicators;
	final CiInfo ci;

	private GitHubProject() {
		throw new AssertionError("In GitHub Project no-arg constructor.");
	}

	private GitHubProject(final String name, final String description,
			final String ownerLogin, final String url, final Date updated,
			final String language, final int openIssues,
			final ProjectMetadata metadata, final Indicators indicators,
			final CiInfo ci) {
		this.name = name;
		this.description = description;
		this.ownerLogin = ownerLogin;
		this.url = url;
		this.updated = updated;
		this.language = language;
		this.openIssues = openIssues;
		this.opfMetadata = metadata;
		this.indicators = indicators;
		this.ci = ci;

	}

	/**
	 * Factory method for GitHubProject class.
	 * 
	 * @param repo
	 *            the Repository instance for the GitHubProject
	 * @param metadata
	 *            the parsed project OPF metadata file
	 * @param indicators
	 *            the project health indicator object
	 * @param ci
	 *            the project Continuous Integration information
	 * @return a new instance created from the pass parameters
	 */
	public final static GitHubProject newInstance(final Repository repo,
			final ProjectMetadata metadata, final Indicators indicators,
			CiInfo ci) {
		Preconditions.checkNotNull(repo, "repo == null");
		return fromValues(repo.getName(), repo.getDescription(), repo
				.getOwner().getLogin(), repo.getHtmlUrl(), repo.getUpdatedAt(),
				repo.getLanguage(), repo.getOpenIssues(), metadata, indicators,
				ci);
	}

	/**
	 * @param name
	 *            the name of the project repo
	 * @param description
	 *            the GitHub description of the project
	 * @param ownerLogin
	 *            the GitHub login name of the project owner
	 * @param url
	 *            the GitHub URL of the project
	 * @param updated
	 *            the date the project was last updated
	 * @param language
	 *            the primary language of the project
	 * @param openIssues
	 *            the number of open GitHub Issues for the project
	 * @param metadata
	 *            the metadata object
	 * @param indicators
	 *            the project health indicators
	 * @param ci
	 *            the Travis CI information for the project
	 * @return a new GitHub instance created from the param values
	 */
	@JsonCreator
	public final static GitHubProject fromValues(
			@JsonProperty("name") final String name,
			@JsonProperty("description") final String description,
			@JsonProperty("ownerLogin") final String ownerLogin,
			@JsonProperty("url") final String url,
			@JsonProperty("updated") final Date updated,
			@JsonProperty("language") final String language,
			@JsonProperty("openIssues") final int openIssues,
			@JsonProperty("metadata") final ProjectMetadata metadata,
			@JsonProperty("indicators") final Indicators indicators,
			@JsonProperty("ci") final CiInfo ci) {
		Preconditions.checkNotNull(name, "name == null");
		Preconditions.checkArgument(!name.isEmpty(), "name.isEmpty() == true");
		Preconditions.checkNotNull(description, "description == null");
		Preconditions.checkNotNull(ownerLogin, "ownerLogin == null");
		Preconditions.checkArgument(!ownerLogin.isEmpty(),
				"description.isEmpty() == true");
		Preconditions.checkNotNull(url, "url == null");
		Preconditions.checkArgument(!url.isEmpty(), "url.isEmpty() == true");
		Preconditions.checkNotNull(updated, "updated == null");
		Preconditions.checkArgument(openIssues >= 0, "openIssues < 0");
		Preconditions.checkNotNull(indicators, "indicators == null");
		Preconditions.checkNotNull(ci, "ci == null");
		Preconditions.checkNotNull(metadata, "metadata == null");
		return new GitHubProject(name, description, ownerLogin, url, updated,
				(language == null || language.isEmpty()) ? "unknown" : language, openIssues,
				metadata, indicators, ci);
	}

	/**
	 * Immutable wrapper for project indicator information. String URLs for the
	 * project readme, license and OPF Metadata.
	 * 
	 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl
	 *         Wilson</a>.</p> <a
	 *         href="https://github.com/carlwilson">carlwilson AT
	 *         github</a>.</p>
	 * @version 0.1
	 * 
	 *          Created 10 Jul 2013:10:44:23
	 */
	@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NON_PRIVATE)
	public static final class Indicators {
		final String readMeUrl;
		final String licenseUrl;
		final String metadataUrl;

		private Indicators() {
			throw new AssertionError();
		}

		private Indicators(final String readMeUrl, final String licenseUrl,
				final String metadataUrl) {
			this.readMeUrl = readMeUrl;
			this.licenseUrl = licenseUrl;
			this.metadataUrl = metadataUrl;
		}

		/**
		 * @param readMeUrl
		 * @param licenseUrl
		 * @param metadataUrl
		 * @return a new Indicators instance from the passed parameters
		 */
		@JsonCreator
		public static final Indicators fromValues(
				@JsonProperty("readMeUrl") final String readMeUrl,
				@JsonProperty("licenseUrl") final String licenseUrl,
				@JsonProperty("metadataUrl") final String metadataUrl) {
			Preconditions.checkNotNull(readMeUrl, "readMeUrl == null");
			Preconditions.checkNotNull(licenseUrl, "licenseUrl == null");
			Preconditions.checkNotNull(metadataUrl, "metadataUrl == null");
			return new Indicators(readMeUrl, licenseUrl, metadataUrl);
		}
	}

	/**
	 * Immutable wrapper for Travis CI information, package protected variables.
	 * 
	 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl
	 *         Wilson</a>.</p> <a
	 *         href="https://github.com/carlwilson">carlwilson AT
	 *         github</a>.</p>
	 * @version 0.1
	 * 
	 *          Created 10 Jul 2013:10:45:08
	 */
	@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.NON_PRIVATE)
	public static final class CiInfo {
		final boolean hasTravis;

		private CiInfo() {
			throw new AssertionError();
		}

		private CiInfo(final boolean hasTravis) {
			this.hasTravis = hasTravis;
		}

		/**
		 * @param hasTravis
		 *            boolean, true if project has a Travis CI build.
		 * @return a new CiInfo instance from the param value
		 */
		@JsonCreator
		public static final CiInfo fromValues(
				@JsonProperty("hasTravis") final boolean hasTravis) {
			return new CiInfo(hasTravis);
		}
	}
}
