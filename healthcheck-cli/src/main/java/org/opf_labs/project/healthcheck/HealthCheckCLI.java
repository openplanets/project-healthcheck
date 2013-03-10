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
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OrganizationService;

/**
 * Command Line Interface class for the Project Healthcheck.
 * 
 * Initial cut simply to get the GitHub API working from Java.  Aim to output
 * a list of OPF organisation and project details to STDOUT for now.
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
			System.out.println("Requests remaining: " + client.getRemainingRequests());
		} catch (IOException e) {
			System.err.println("Error retrieving organisation");
			e.printStackTrace();
		}
	}
}
