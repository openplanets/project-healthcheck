package org.opf_labs.project.healthcheck;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Class to hold general test methods and run all unit tests.
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 *          Created 10 Jul 2013:15:07:22
 */
@RunWith(Suite.class)
@SuiteClasses({ ProjectMetadataTest.class })
public class AllTests {
	/** Root test */
	private final static String TEST_ROOT = "org/opf_labs/project/healthcheck";
	/** Root yaml */
	public final static String YAML_ROOT = TEST_ROOT + "/yaml";
	/** Test YAML */
	public final static String TEST_YAML = YAML_ROOT + "/test.yml";
	/** Incomplete YAML */
	public final static String INCOMPLETE_YAML = YAML_ROOT + "/incomplete.yml";

	/**
	 * @return the test OPF Metadata YAML file
	 * @throws URISyntaxException
	 *             if the resource can't be opened
	 */
	public final static File getTestYamlFile() throws URISyntaxException {
		return getResourceAsFile(TEST_YAML);
	}

	/**
	 * @return the test OPF Metadata YAML file
	 * @throws URISyntaxException
	 *             if the resource can't be opened
	 */
	public final static File getTestIncompleteYamlFile() throws URISyntaxException {
		return getResourceAsFile(INCOMPLETE_YAML);
	}

	/**
	 * Given a string resource path and name returns a File object. Used to load
	 * test data, not meant to be a general utility method.
	 * 
	 * @param resName
	 *            the name of the resource to retrieve a file for
	 * @return the java.io.File for the named resource
	 * @throws URISyntaxException
	 *             if the named resource can't be converted to a URI
	 */
	public final static File getResourceAsFile(String resName)
			throws URISyntaxException {
		return new File(ClassLoader.getSystemResource(resName).toURI());
	}
}
