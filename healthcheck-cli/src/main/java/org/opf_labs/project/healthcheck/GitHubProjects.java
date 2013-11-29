/**
 * 
 */
package org.opf_labs.project.healthcheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.opf_labs.project.healthcheck.GitHubProject.Builder;
import org.opf_labs.project.healthcheck.GitHubProject.CiInfo;
import org.opf_labs.project.healthcheck.GitHubProject.Indicators;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.Base64;

/**
 * Utility class for the GitHubProject bean. Gathers project information from
 * GitHub and Travis REST APIs.
 * 
 * TODO Tests for GitHubProjects.</p>
 * 
 * TODO Implementation for GitHubProjects.</p>
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 *          Created 10 Jul 2013:12:12:11
 */

public final class GitHubProjects {
	/** String constant for unknown values **/
	public static final String UNKNOWN = "unknown";

	// String constants for info files
	private static final String README = "readme";
	private static final String LICENSE = "license";
	private static final String OPF_YAML = ".opf.yml";

	private static final Logger LOGGER = Logger.getLogger(GitHubProjects.class);

	// String constants for Travis
	private static final String TRAVIS_ROOT = "https://api.travis-ci.org/";
	private static final String TRAVIS_REPO_ROOT = TRAVIS_ROOT + "repos/";
	private GitHubProjects() {
		throw new AssertionError("In GitHubProjects constructor.");
	}

	/**
	 * Returns an EGit User object for a GitHub login.
	 * 
	 * @param ghClient
	 *            an EGit GitHub client object, holds credentials for gitHub
	 *            connection.
	 * @param login
	 *            the GitHub login of the user to retrieve.
	 * @return the EGit User for the login
	 * @throws IOException
	 *             if there's a problem calling the GitHub API.
	 */
	public static User getUser(final GitHubClient ghClient,
			final String login) throws IOException {
		// Create the organisation info object from Git Hub information
		UserService userService = new UserService(ghClient);
		return userService.getUser(login);
	}

	/**
	 * Get the healthcheck indicators for a GitHub project. Checks for a README,
	 * a LICENSE, and an OPF YAML file.
	 * 
	 * @param ghClient
	 *            an EGit GitHub client object, holds credentials for gitHub
	 *            connection.
	 * @param repo
	 *            an EGit GitHub repository object for the project
	 * @return a healthcheck indicator object
	 * @throws IOException
	 *             if there's a problem calling the GitHub API
	 */
	public static Indicators getProjectIndicators(
			final GitHubClient ghClient, final Repository repo)
			throws IOException {
		String readMeUrl = "", licenseUrl = "", metadataUrl = "";
		CommitService commitService = new CommitService(ghClient);
		DataService dataService = new DataService(ghClient);
		List<RepositoryCommit> commits = commitService.getCommits(repo);
		Tree tree = commits.get(0).getCommit().getTree();
		String treeSha = tree.getSha();
		tree = dataService.getTree(repo, treeSha);
		for (TreeEntry treeEntry : tree.getTree()) {
			if (!(treeEntry.getType().equals(TreeEntry.TYPE_BLOB))) {
				continue;
			}
			String baseName = FilenameUtils.getBaseName(treeEntry.getPath());
			if (baseName.equalsIgnoreCase(README)) {
				readMeUrl = repo.getHtmlUrl() + "#readme";
			} else if (baseName.equalsIgnoreCase(LICENSE)) {
				licenseUrl = repo.getHtmlUrl() + "/blob/master/"
						+ treeEntry.getPath();
			} else if (treeEntry.getPath().equalsIgnoreCase(OPF_YAML)) {
				metadataUrl = repo.getHtmlUrl() + "/blob/master/"
						+ treeEntry.getPath();
			}
		}
		return Indicators.fromValues(readMeUrl, licenseUrl, metadataUrl);
	}

	/**
	 * @param ghClient
	 *            an EGit GitHub client object, holds credentials for gitHub
	 *            connection.
	 * @param repo
	 *            an EGit GitHub repository object for the project
	 * @return a ProjectMetadata instance.
	 * @throws IOException
	 *             if there's a problem calling the GitHub API
	 */
	public static ProjectMetadata getMetadata(
			GitHubClient ghClient, final Repository repo)
			throws IOException {
		ContentsService contentService = new ContentsService(ghClient);
		try {
			List<RepositoryContents> contents = contentService.getContents(
					repo, OPF_YAML);
			return ProjectMetadata.fromYamlString(Base64.base64Decode(contents
					.get(0).getContent()));
		} catch (RequestException excep) {
			// No YAML file found
			return ProjectMetadata.defaultInstance();
		}
	}

	/**
	 * Retrieves the Travis Continuous Integration information for a software
	 * project.
	 * 
	 * @param ownerLogin
	 *            the GitHub login (id) of the repository
	 * @param repoName
	 *            the name of the repository
	 * 
	 * @return the Travis CI Information for the project.
	 */
	public static CiInfo getTravisInfo(final String ownerLogin,
			final String repoName) {
		ClientConfig cc = new DefaultClientConfig();
		cc.getClasses().add(JacksonJsonProvider.class);
		Client restClient = Client.create(cc);
		WebResource travisCall = restClient.resource(TRAVIS_REPO_ROOT
				+ ownerLogin + "/" + repoName);
		ClientResponse response = travisCall.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		if (response.getClientResponseStatus() == ClientResponse.Status.NOT_FOUND) {
			return CiInfo.fromValues(false);
		}
		TravisRepo entity = response.getEntity(TravisRepo.class);
		return CiInfo.fromValues(entity.lastBuildId != 0);
	}

	/**
	 * @param ghClient
	 *            an EGit GitHub client object, holds credentials for gitHub
	 *            connection.
	 * @param ghLogin
	 *            the string GitHub login of the GitHub user who's project info's
	 *            retrieved.
	 * @return a java.util.List of GitHub projects for the user identified by the
	 *         login string
	 * @throws IOException
	 */
	public static List<GitHubProject> createProjectList(
			final GitHubClient ghClient, final String ghLogin)
			throws IOException {
		RepositoryService repoService = new RepositoryService(ghClient);
		List<GitHubProject> projects = new ArrayList<>();
		List<Repository> repos = repoService.getOrgRepositories(ghLogin);
		for (Repository repo : repos) {
			LOGGER.debug(repo.getName());
			// Skip the private repos
			if (repo.isPrivate()) {
				continue;
			}
			ProjectMetadata metadata = getMetadata(ghClient, repo);
			Builder projBuilder = (new Builder(repo)).metadata(metadata)
					.indicators(getProjectIndicators(ghClient, repo))
					.ci(getTravisInfo(ghLogin, repo.getName()));
			projects.add(projBuilder.build());
		}
		return projects;
	}
}
