package org.opf_labs.project.healthcheck;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

/**
 * Unit tests for ProjectMetadata class.
 * 
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 * Created 10 Jul 2013:14:43:40
 */
public class ProjectMetadataTest {

	/**
	 * Null name test for {@link org.opf_labs.project.healthcheck.ProjectMetadata#getInstance(String, String)}
	 */
	@Test(expected=NullPointerException.class)
	public void testGetInstanceNullName() {
		ProjectMetadata.getInstance(null, "vendor");
	}

	/**
	 * Null vendor test for {@link org.opf_labs.project.healthcheck.ProjectMetadata#getInstance(String, String)}
	 */
	@Test(expected=NullPointerException.class)
	public void testGetInstanceNullVendor() {
		ProjectMetadata.getInstance("name", null);
	}

	/**
	 * Empty name test for {@link org.opf_labs.project.healthcheck.ProjectMetadata#getInstance(String, String)}
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetInstanceEmptyName() {
		ProjectMetadata.getInstance("", "vendor");
	}

	/**
	 * Empty vendor test for {@link org.opf_labs.project.healthcheck.ProjectMetadata#getInstance(String, String)}
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testGetInstanceEmptyVendor() {
		ProjectMetadata.getInstance("name", "");
	}

	/**
	 * Null name test for {@link org.opf_labs.project.healthcheck.ProjectMetadata#getInstance(InputStream)}
	 */
	@Test(expected=NullPointerException.class)
	public void testGetInstanceInputStreamNull() {
		ProjectMetadata.getInstance(null);
	}

}
