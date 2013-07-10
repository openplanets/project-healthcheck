/**
 * 
 */
package org.opf_labs.project.healthcheck;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.egit.github.core.client.GsonUtils;

import com.google.common.base.Joiner;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Command Line Interface class for the Project Healthcheck.
 * 
 * Initial cut simply to get the GitHub API working from Java. Aim to output a
 * list of OPF organisation and project details to STDOUT for now.
 * 
 * 
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 * Created 10 Jul 2013:10:44:23
 */
public final class HealthCheckCLI {
	
	// Constants for CLI Options
	private static final String FILE_OPT = "file";
	private static final String FILE_OPT_ARG = "Write output to file";
	private static final String FILE_OPT_DESC = "Path to file to create or overwrite.";
	private static final String HELP_OPT = "help";
	private static final String HELP_OPT_DESC = "print this message";
	private static final String HTML_OPT = "html";
	private static final String HTML_OPT_DESC = "output HTML, defaults to plain text";
	private static final String ORGANISATION_OPT = "org";
	private static final String ORGANISATION_OPT_ARG = "GithHub organisation";
	private static final String ORGANISATION_OPT_DESC = "GitHub org to retrieve details from, default openplanets";
	private static final String PASSWORD_OPT = "pass";
	private static final String PASSWORD_OPT_ARG = "GithHub password";
	private static final String PASSWORD_OPT_DESC = "GitHub password used to get OAuth token";
	private static final String USER_OPT = "user";
	private static final String USER_OPT_ARG = "GithHub ID";
	private static final String USER_OPT_DESC = "GitHub ID used to get OAuth token";
	
	// Default org name is openplanets
	private static final String DEFAULT_ORG_NAME = "openplanets";

	// Create the options object
	private static final Options OPTIONS = new Options();
	static {
		Option help = new Option(HELP_OPT, HELP_OPT_DESC);
		Option html = new Option(HTML_OPT, HTML_OPT_DESC);
		@SuppressWarnings("static-access")
		Option file = OptionBuilder.withArgName(FILE_OPT_ARG).hasArg()
				.withDescription(FILE_OPT_DESC).create(FILE_OPT);
		@SuppressWarnings("static-access")
		Option organisation = OptionBuilder.withArgName(ORGANISATION_OPT_ARG).hasArg()
				.withDescription(ORGANISATION_OPT_DESC).create(ORGANISATION_OPT);
		@SuppressWarnings("static-access")
		Option password = OptionBuilder.withArgName(PASSWORD_OPT_ARG).hasArg()
				.withDescription(PASSWORD_OPT_DESC).create(PASSWORD_OPT);
		@SuppressWarnings("static-access")
		Option user = OptionBuilder.withArgName(USER_OPT_ARG).hasArg()
				.withDescription(USER_OPT_DESC).create(USER_OPT);
		OPTIONS.addOption(help);
		OPTIONS.addOption(html);
		OPTIONS.addOption(file);
		OPTIONS.addOption(organisation);
		OPTIONS.addOption(password);
		OPTIONS.addOption(user);
	}

	/**
	 * Main CLI entry point, process command line arguments
	 * 
	 * @param args the command line args passed at invocation
	 */
	public final static void main(final String[] args) {
		// Create a command line parser
		CommandLineParser cmdParser = new GnuParser();
		// And a sysout writer
		Writer outWriter = new OutputStreamWriter(System.out);
		try {
			// Parse the command line arguments
			CommandLine cmd = cmdParser.parse(OPTIONS, args);

			// Print help if asked, will terminate if no other option
			outputHelp(cmd);

			// Parsed OK so let's get GitHub Client
			GitHubClient ghClient = createGitHubClient(cmd);
			
			// Now the organisation name
			User user = GitHubProjects.getUser(ghClient, getOrgName(cmd));
			
			// Get a file writer if requested
			if (!cmd.hasOption(FILE_OPT)) {
				outWriter.close();
				outWriter = getFileOutputWriter(cmd.getOptionValue(FILE_OPT));
			}

			List<GitHubProject> projects = GitHubProjects.createProjectList(ghClient, user.getLogin());
			if (cmd.hasOption(HTML_OPT)) {
				outputHtml(user, projects, outWriter);
			} else {
				outputPlainText(user, projects, outWriter);
			}
			outWriter.close();
		} catch (ParseException e) {
			// Ooops, parsing commands went wrong
			e.printStackTrace();
			System.err.println("Command parsing failed.  Reason: "
					+ e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to retrieve organisation from GitHub.  Reason: "
					+ e.getMessage());
			System.exit(1);
		} finally {
			try {
				if (outWriter != null) outWriter.close();
			} catch (IOException excep) {
				/**
				 * Empty catch block, we can ignore the close error
				 */
				excep.printStackTrace();
			}
		}
	}

	private final static void outputHelp(final CommandLine cmd) {
		// Check for help option
		if (cmd.hasOption(HELP_OPT)) {
			// OK help found
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("proj-heath", OPTIONS);
		}
	}

	private final static GitHubClient createGitHubClient(final CommandLine cmd) {
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
	
	private final static String getOrgName(final CommandLine cmd) {
		return (cmd.hasOption(ORGANISATION_OPT)) ? cmd.getOptionValue(ORGANISATION_OPT) : DEFAULT_ORG_NAME;
	}

	private final static Writer getFileOutputWriter(String filePath) throws IOException {
		File outFile = new File(filePath);
		if (!outFile.exists()) {
			if (!outFile.createNewFile()) throw new IOException();
		} else if (!outFile.isFile()) throw new IOException();
		return new FileWriter(outFile, false);
	}
	
	private final static void outputHtml(final User user, final List<GitHubProject> projects, final Writer outWriter) {
		Configuration cfg = new Configuration();
		cfg.setOutputEncoding("utf-8");
		cfg.setClassForTemplateLoading(HealthCheckCLI.class, getTemplateDir());
		try {
			Template indexTemplate = cfg.getTemplate("index.html");
			Map<String, Object> templateData = new HashMap<String, Object>();
			templateData.put("user", user);
			templateData.put("userJson", GsonUtils.toJson(user));
			List<String> projectsJson = new ArrayList<String>();
			for (GitHubProject project : projects) {
				projectsJson.add(GsonUtils.toJson(project));
			}
			Joiner joiner = Joiner.on(",");
			templateData.put("projectsJson", joiner.join(projectsJson));
			indexTemplate.process(templateData, outWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private final static void outputPlainText(final User user, final List<GitHubProject> projects, final Writer outWriter) throws IOException {
		outWriter.write(user.getName() + " Repositories");

		int repoCount = 0;
		for (GitHubProject project : projects) {
			outWriter.write(++repoCount + ": " + project.repo.getName()
					+ ", created: " + project.repo.getCreatedAt());
			outWriter.write(project.repo.getDescription());
		}
	}

	private final static String getTemplateDir() {
		return "/" + HealthCheckCLI.class.getPackage().getName().replace(".", "/") + "/templates";
	}
}