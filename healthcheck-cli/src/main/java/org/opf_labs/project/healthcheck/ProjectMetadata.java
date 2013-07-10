/**
 * 
 */
package org.opf_labs.project.healthcheck;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
@JsonIgnoreProperties(ignoreUnknown=true)
public final class ProjectMetadata {
	private final static String UNKNOWN = "unknown";
	/** The projects full name */
	public final String name;
	/** The projects vendor identifier, could be an individual, organisation, or project. */
	public final String vendor;
	//public final Maintainer maintainer;

	ProjectMetadata() {
		this(UNKNOWN, UNKNOWN);
	}

	private ProjectMetadata(final String name, final String vendor) {
		Preconditions.checkNotNull(name, "name is null.");
		Preconditions.checkNotNull(vendor, "vendor is null.");
		Preconditions.checkArgument(!name.isEmpty(), "name.isEmpty() == true");
		Preconditions.checkArgument(!vendor.isEmpty(),
				"vendor.isEmpty() == true");
		//Preconditions.checkNotNull(maintainer, "maintainer is null.");
		this.name = name;
		this.vendor = vendor;
		//this.maintainer = maintainer;
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
		ProjectMetadata pmd = null;
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			pmd = mapper.readValue(yamlStream, ProjectMetadata.class);
		} catch (JsonParseException excep) {
			// TODO Auto-generated catch block
			excep.printStackTrace();
		} catch (JsonMappingException excep) {
			// TODO Auto-generated catch block
			excep.printStackTrace();
		} catch (IOException excep) {
			// TODO Auto-generated catch block
			excep.printStackTrace();
		}
		return pmd;
	}
}
