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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
public final class GitHubProject {
	final String name;
	final String description;
	final String ownerLogin;
	final String url;
	final String language;
	final Date updated;
	final int openIssues;
	final ProjectMetadata metadata;
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
		this.metadata = metadata;
		this.indicators = indicators;
		this.ci = ci;

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
	private static GitHubProject fromValues(
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
		return new GitHubProject(
				name,
				description,
				ownerLogin,
				url,
				updated,
				(language == null || language.isEmpty()) ? GitHubProjects.UNKNOWN : language,
				openIssues, metadata, indicators, ci);
	}

	/**
	 * Builder class for creating GitHub project instances
	 * 
	 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl
	 *         Wilson</a>.</p> <a
	 *         href="https://github.com/carlwilson">carlwilson AT
	 *         github</a>.</p>
	 * @version 0.1
	 * 
	 *          Created 11 Jul 2013:10:12:45
	 */
	@SuppressWarnings("hiding") 
	static public final class Builder {
		private String name = GitHubProjects.UNKNOWN;
		private String description = GitHubProjects.UNKNOWN;
		private String ownerLogin = GitHubProjects.UNKNOWN;
		private String url = GitHubProjects.UNKNOWN;
		private String language = GitHubProjects.UNKNOWN;
		private Date updated = new Date(0);
		private int openIssues = 0;
		private ProjectMetadata metadata = ProjectMetadata.defaultInstance();
		private Indicators indicators;
		private CiInfo ci;

		/**
		 * Creates a new builder populated from an existing project, used to
		 * copy projects.
		 * 
		 * @param project
		 *            a GitHub project to copy
		 */
		public Builder(final GitHubProject project) {
			Preconditions.checkNotNull(project, "project == null");
			this.name(project.name);
			this.description(project.description);
			this.ownerLogin(project.ownerLogin);
			this.url(project.url);
			this.language(project.language);
			this.updated(project.updated);
		}

		/**
		 * Creates a new Builder populated from a GitHub repository object.
		 * 
		 * @param repo
		 *            the repository instance used to populate the Builder
		 */
		public Builder(final Repository repo) {
			Preconditions.checkNotNull(repo, "repo == null");
			this.name(repo.getName());
			this.description(repo.getDescription());
			this.ownerLogin(repo.getOwner().getLogin());
			this.url(repo.getHtmlUrl());
			this.language(repo.getLanguage());
			this.updated(repo.getUpdatedAt());
			this.openIssues(repo.getOpenIssues());
		}

		/**
		 * @param name
		 *            the name of the project
		 * @return the builder instance for chaining
		 */
		public Builder name(final String name) {
			Preconditions.checkNotNull(name, "name == null");
			Preconditions.checkArgument(!name.isEmpty(),
					"name.isEmpty() == true");
			this.name = name;
			return this;
		}

		/**
		 * @param description
		 *            a description of the project
		 * @return the builder instance for chaining
		 */
		public Builder description(final String description) {
			Preconditions.checkNotNull(description, "description == null");
			this.description = description;
			return this;
		}

		/**
		 * @param ownerLogin
		 *            the GitHub login of the project's owner
		 * @return the builder instance for chaining
		 */
		public Builder ownerLogin(final String ownerLogin) {
			Preconditions.checkNotNull(ownerLogin, "ownerLogin == null");
			Preconditions.checkArgument(!ownerLogin.isEmpty(),
					"ownerLogin.isEmpty() == true");
			this.ownerLogin = ownerLogin;
			return this;
		}

		/**
		 * @param url
		 *            the GitHub URL of the project, held as a string
		 * @return the builder instance for chaining
		 */
		public Builder url(final String url) {
			Preconditions.checkNotNull(url, "url == null");
			Preconditions
					.checkArgument(!url.isEmpty(), "url.isEmpty() == true");
			this.url = url;
			return this;
		}

		/**
		 * @param language
		 *            the main programming language of the project
		 * @return the builder instance for chaining
		 */
		public Builder language(final String language) {
			this.language = ((language == null) || language.isEmpty()) ? GitHubProjects.UNKNOWN
					: language;
			return this;
		}

		/**
		 * @param updated
		 *            the date that the project was last updated
		 * @return the builder instance for chaining
		 */
		public Builder updated(final Date updated) {
			Preconditions.checkNotNull(updated, "updated == null");
			Preconditions.checkArgument(updated.before(new Date()),
					"Updated date is in the future.");
			this.updated = new Date(updated.getTime());
			return this;
		}

		/**
		 * @param openIssues
		 *            the number of open issues for the project
		 * @return the builder instance for chaining
		 */
		public Builder openIssues(final int openIssues) {
			Preconditions.checkArgument(openIssues >= 0, "openIssues < 0");
			this.openIssues = openIssues;
			return this;
		}

		/**
		 * @param metadata
		 *            the projects metadata block parsed from .opf.yml
		 * @return the builder instance for chaining
		 */
		public Builder metadata(final ProjectMetadata metadata) {
			Preconditions.checkNotNull(metadata, "metadata == null");
			this.metadata = metadata;
			return this;
		}

		/**
		 * @param indicators
		 *            the healthcheck indicators for the project
		 * @return the builder instance for chaining
		 */
		public Builder indicators(final Indicators indicators) {
			Preconditions.checkNotNull(indicators, "indicators == null");
			this.indicators = indicators;
			return this;
		}

		/**
		 * @param ci
		 *            the Travis CI info for the project
		 * @return the builder instance for chaining
		 */
		public Builder ci(final CiInfo ci) {
			Preconditions.checkNotNull(ci, "ci == null");
			this.ci = ci;
			return this;
		}

		/**
		 * Instantiate a new gitHubProject instance.
		 * 
		 * @return a GitHubProject instance with values populated with those of
		 *         the builder
		 */
		@SuppressWarnings("synthetic-access")
		public GitHubProject build() {
			return new GitHubProject(this);
		}
	}

	@SuppressWarnings("synthetic-access")
	private GitHubProject(Builder builder) {
		this.name = builder.name;
		this.description = builder.description;
		this.ownerLogin = builder.ownerLogin;
		this.url = builder.url;
		this.language = builder.language;
		this.updated = builder.updated;
		this.openIssues = builder.openIssues;
		this.metadata = builder.metadata;
		this.indicators = builder.indicators;
		this.ci = builder.ci;
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
	@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
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
		public static Indicators fromValues(
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
	@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
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
		public static CiInfo fromValues(
				@JsonProperty("hasTravis") final boolean hasTravis) {
			return new CiInfo(hasTravis);
		}
	}
}
