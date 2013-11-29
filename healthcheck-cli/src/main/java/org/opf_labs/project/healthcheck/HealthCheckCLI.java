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
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	private static final Logger LOGGER = Logger.getLogger(HealthCheckCLI.class);

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
	
	private HealthCheckCLI() {
		throw new AssertionError("Should never enter HealthCheckCLI default constructor.");
	}

	/**
	 * Main CLI entry point, process command line arguments
	 * 
	 * @param args the command line args passed at invocation
	 */
	public static void main(final String[] args) {
		BasicConfigurator.configure();
		// Create a command line parser
		CommandLineParser cmdParser = new GnuParser();
		Writer outWriter = null; 
		try {
			// Parse the command line arguments
			CommandLine cmd = cmdParser.parse(OPTIONS, args);

			// Print help if asked, will terminate if no other option
			if (cmd.hasOption(HELP_OPT)) {
				// OK help found
				outputHelp(cmd);
				System.exit(0);
			}

			// Parsed OK so let's get GitHub Client
			GitHubClient ghClient = createGitHubClient(cmd);
			
			// Now the organisation name
			LOGGER.info("Getting GitHub user");
			User user = GitHubProjects.getUser(ghClient, getOrgName(cmd));
			
			LOGGER.info("Reading project data for GitHub user " + user.getName());

			List<GitHubProject> projects = GitHubProjects.createProjectList(ghClient, user.getLogin());
			// Get a file writer if requested
			if (cmd.hasOption(FILE_OPT)) {
				outWriter = getFileOutputWriter(cmd.getOptionValue(FILE_OPT));
			} else {
				outWriter = new OutputStreamWriter(System.out);
			}

			if (cmd.hasOption(HTML_OPT)) {
				outputHtml(user, projects, outWriter);
			} else {
				outputPlainText(user, projects, outWriter);
			}
			outWriter.close();
		} catch (ParseException e) {
			LOGGER.info("There was a problem parsing the command line arguments.");
			logFatalExceptionAndExit(e);
		} catch (IOException e) {
			LOGGER.info("There was a problem with the output writer.");
			logFatalExceptionAndExit(e);
		} finally {
			try {
				if (outWriter != null) {
					outWriter.close();
				}
			} catch (IOException excep) {
				/**
				 * Empty catch block, we can ignore the close error
				 */
				LOGGER.warn(excep.getMessage());
			}
		}
	}

	private static void outputHelp(final CommandLine cmd) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("proj-heath", OPTIONS);
	}

	private static GitHubClient createGitHubClient(final CommandLine cmd) {
		GitHubClient client = new GitHubClient();
		if (cmd.hasOption(USER_OPT)) {
			String user = cmd.getOptionValue(USER_OPT);
			LOGGER.info("User:" + user);
			if (cmd.hasOption(PASSWORD_OPT)) {
				String password = cmd.getOptionValue(PASSWORD_OPT);
				client.setCredentials(user, password);
			}
		}
		// Return client
		return client;
	}
	
	private static String getOrgName(final CommandLine cmd) {
		return (cmd.hasOption(ORGANISATION_OPT)) ? cmd.getOptionValue(ORGANISATION_OPT) : DEFAULT_ORG_NAME;
	}

	private static Writer getFileOutputWriter(String filePath) throws IOException {
		File outFile = new File(filePath);
		if (!outFile.exists()) {
			if (!outFile.createNewFile()) {
				throw new IOException();
			}
		} else if (!outFile.isFile()) {
			throw new IOException();
		}
		return new FileWriter(outFile, false);
	}
	
	private static void outputHtml(final User user, final List<GitHubProject> projects, final Writer outWriter) {
		Configuration cfg = new Configuration();
		cfg.setOutputEncoding("utf-8");
		cfg.setClassForTemplateLoading(HealthCheckCLI.class, getTemplateDir());
		try {
			Template indexTemplate = cfg.getTemplate("index.html");
			Map<String, Object> templateData = new HashMap<>();
			templateData.put("user", user);
			ObjectMapper mapper = new ObjectMapper(new JsonFactory());
			templateData.put("userJson", mapper.writeValueAsString(user));
			List<String> projectsJson = new ArrayList<>();
			Set<String> vendors = new TreeSet<>();
			for (GitHubProject project : projects) {
				projectsJson.add(mapper.writeValueAsString(project));
				if (project.metadata != ProjectMetadata.defaultInstance()) {
					vendors.add(project.metadata.vendor);
				}
			}
			Joiner joiner = Joiner.on(",");
			templateData.put("projectsJson", joiner.join(projectsJson));
			templateData.put("vendors", vendors);
			indexTemplate.process(templateData, outWriter);
		} catch (TemplateException | IOException e) {
			// TODO Auto-generated catch block
			LOGGER.warn(e.getMessage());
			LOGGER.warn(e.getStackTrace());
		}
	}
	
	private static void outputPlainText(final User user, final List<GitHubProject> projects, final Writer outWriter) throws IOException {
		outWriter.write(user.getName() + " Repositories");

		int repoCount = 0;
		for (GitHubProject project : projects) {
			outWriter.write(++repoCount + ": " + project.name
					+ ", updated: " + project.updated);
			outWriter.write(project.description);
		}
	}

	private static String getTemplateDir() {
		return "/" + HealthCheckCLI.class.getPackage().getName().replace(".", "/") + "/templates";
	}

	private static void logFatalExceptionAndExit(final Exception excep) {
		LOGGER.fatal("Exception Stack Trace:");
		LOGGER.fatal(excep);
		LOGGER.fatal("Exception Message:");
		LOGGER.fatal(excep.getMessage());
		System.exit(1);
	}
}
