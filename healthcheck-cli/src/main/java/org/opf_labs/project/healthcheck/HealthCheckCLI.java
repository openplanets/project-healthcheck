/**
 * 
 */
package org.opf_labs.project.healthcheck;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.util.Base64;

/**
 * Command Line Interface class for the Project Healthcheck.
 * 
 * Initial cut simply to get the GitHub API working from Java. Aim to output a
 * list of OPF organisation and project details to STDOUT for now.
 * 
 * @author Carl Wilson
 */
public class HealthCheckCLI {
	// Constants for CLI
	private static final String HELP_OPT = "help";
	private static final String HELP_OPT_DESC = "print this message";
	private static final String USER_OPT = "user";
	private static final String USER_OPT_ARG = "GithHub ID";
	private static final String USER_OPT_DESC = "GitHub ID used to get OAuth token";
	private static final String PASSWORD_OPT = "pass";
	private static final String PASSWORD_OPT_ARG = "GithHub password";
	private static final String PASSWORD_OPT_DESC = "GitHub password used to get OAuth token";

	private static Options OPTIONS = new Options();
	static {
		Option help = new Option(HELP_OPT, HELP_OPT_DESC);
		@SuppressWarnings("static-access")
		Option user = OptionBuilder.withArgName(USER_OPT_ARG).hasArg()
				.withDescription(USER_OPT_DESC).create(USER_OPT);
		@SuppressWarnings("static-access")
		Option password = OptionBuilder.withArgName(PASSWORD_OPT_ARG).hasArg()
				.withDescription(PASSWORD_OPT_DESC).create(PASSWORD_OPT);
		OPTIONS.addOption(help);
		OPTIONS.addOption(user);
		OPTIONS.addOption(password);
	}

	/**
	 * Main method, no arg processing for now, just get API working.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Create a command line parser
		CommandLineParser cmdParser = new GnuParser();
		try {
			// Parse the command line arguments
			CommandLine cmd = cmdParser.parse(OPTIONS, args);

			// Print help if asked, will terminate if no other option
			outputHelp(cmd);

			// Parsed OK so let's get GitHub Client
			GitHubClient client = createGitHubClient(cmd);
			outputOrgInfo(client);
			outputOrgRepos(client);
		} catch (ParseException e) {
			// Ooops, parsing commands went wrong
			e.printStackTrace();
			System.err.println("Command parsing failed.  Reason: "
					+ e.getMessage());
			System.exit(1);
		}
	}

	private static void outputHelp(CommandLine cmd) {
		// Check for help option
		if (cmd.hasOption(HELP_OPT)) {
			// OK help found
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("proj-heath", OPTIONS);
		}
	}

	private static GitHubClient createGitHubClient(CommandLine cmd) {
		GitHubClient client = new GitHubClient();
		if (cmd.hasOption(USER_OPT)) {
			String user = cmd.getOptionValue(USER_OPT);
			if (cmd.hasOption(PASSWORD_OPT)) {
				String password = cmd.getOptionValue(PASSWORD_OPT);
				client.setCredentials(user, password);
			}
		}
		// Return client
		return client;
	}

	private static void outputOrgInfo(GitHubClient client) {
		try {
			OrganizationService orgService = new OrganizationService(client);
			User user = orgService.getOrganization("openplanets");
			System.out.println("Organisation: " + user.getName());
			System.out.println("Public Repos: " + user.getPublicRepos());
			System.out.println("Private Repos: " + user.getOwnedPrivateRepos());
		} catch (IOException e) {
			System.err.println("Error retrieving organisation");
			e.printStackTrace();
		}
	}

	private static void outputOrgRepos(GitHubClient client) {
		RepositoryService repoService = new RepositoryService(client);
		System.out.println();
		System.out.println("OpenPlanets Repositories");
		System.out.println("========================");
		System.out.println("Requests remaining: "
				+ client.getRemainingRequests());
		System.out.println();
		try {
			int repoCount = 0;
//			MarkdownService mdService = new MarkdownService(client);
			for (Repository repo : repoService
					.getOrgRepositories("openplanets")) {
				if (repo.isPrivate())
					continue;
				System.out.println(++repoCount + ": " + repo.getName()
						+ ", created: " + repo.getCreatedAt());
				System.out.println(repo.getDescription());
				System.out.println();
				String readMe;
				try {
//					readMe = mdService.getHtml(
//							new String(Base64.decode(repoService.getContents(
//									repo, "README.md").getContent())), "gfm");
					readMe = new String(Base64.decode(repoService.getContents(
									repo, "README.md").getContent()));
				} catch (RequestException excep) {
					// excep.printStackTrace();
					readMe = "NO README.md";
				}
				System.out.println(readMe);
				String license;
				try {
					license = new String(Base64.decode(repoService.getContents(
									repo, "LICENSE").getContent()));
				} catch (RequestException excep) {
					// excep.printStackTrace();
					license = "NO LICENSE";
				}
				System.out.println(license);
				String opfYml;
				try {
					opfYml = new String(Base64.decode(repoService.getContents(
									repo, ".opf.yml").getContent()));
				} catch (RequestException excep) {
					// excep.printStackTrace();
					opfYml = "NO YAML Metadata";
				}
				System.out.println(opfYml);
				System.out.println();
				//repoService.getContents(repo, "/");
			}
			System.out.println("Requests remaining: "
					+ client.getRemainingRequests());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
