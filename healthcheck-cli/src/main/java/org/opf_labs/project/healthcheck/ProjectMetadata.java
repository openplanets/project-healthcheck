/**
 * 
 */
package org.opf_labs.project.healthcheck;

import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Preconditions;

/**
 * Class that parses an Open Planets Foundation Project Metadata YAML file. File
 * specification is available <a href=
 * "http://wiki.opf-labs.org/display/PT/GitHub+Guide#GitHubGuide-ProjectInformation"
 * >here</a>.
 * 
 * TODO Tests for ProjectMetadata.</p>
 * 
 * TODO Implementation for ProjectMetadata.</p>
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 *          Created 10 Jul 2013:12:58:11
 */

public final class ProjectMetadata {
	final String name;
	final String vendor;

	private ProjectMetadata() {
		throw new AssertionError("In ProjectMetadata no-arg constructor.");
	}

	private ProjectMetadata(final String name, final String vendor) {
		this.name = name;
		this.vendor = vendor;
	}

	/**
	 * Factory method for ProjectMetadata, returns a new instance from values.
	 * 
	 * @param name
	 *            the name of the project read from the metadata file
	 * @param vendor
	 *            the project vendor name, read from the metadata file.
	 * @return a populated project metadata instance
	 */
	public static final ProjectMetadata getInstance(final String name,
			final String vendor) {
		Preconditions.checkNotNull(name, "name is null.");
		Preconditions.checkNotNull(vendor, "vendor is null.");
		Preconditions.checkArgument(!name.isEmpty(), "name.isEmpty() == true");
		Preconditions.checkArgument(!vendor.isEmpty(),
				"vendor.isEmpty() == true");
		return new ProjectMetadata(name, vendor);
	}

	/**
	 * Factory method for ProjectMetadata, returns a new instance from an
	 * InputStream to YAML metadata.
	 * 
	 * @param yamlStream
	 *            an java.io.InputSt5ream of YAML metadata
	 * @return a populated project metadata instance
	 */
	public static final ProjectMetadata getInstance(InputStream yamlStream) {
		Preconditions.checkNotNull(yamlStream, "yamlStream is null");
		Yaml yaml = new Yaml();
		Object parsedYaml = yaml.load(yamlStream);
		System.out.println(parsedYaml.getClass());
		return null;
	}
}
