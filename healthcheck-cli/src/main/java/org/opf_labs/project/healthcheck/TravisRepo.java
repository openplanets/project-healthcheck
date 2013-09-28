/**
 * 
 */
package org.opf_labs.project.healthcheck;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

/**
 * TODO JavaDoc for TravisRepo.</p> TODO Tests for TravisRepo.</p> TODO
 * Implementation for TravisRepo.</p>
 * 
 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *         <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 *          Created 15 Jul 2013:17:02:37
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class TravisRepo {
	final int id;
	final String slug;
	final int lastBuildId;
	final String lastBuildStatus;
	final String lastBuildResult;
	final int lastBuildDuration;

	private TravisRepo(final int id, final String slug, final int lastBuildId,
			final String lastBuildStatus, final String lastBuildResult,
			final int lastBuildDuration) {
		this.id = id;
		this.slug = slug;
		this.lastBuildId = lastBuildId;
		this.lastBuildStatus = lastBuildStatus;
		this.lastBuildResult = lastBuildResult;
		this.lastBuildDuration = lastBuildDuration;
	}

	/**
	 * @param id
	 *            the integer id of the travis repository
	 * @param slug
	 * @param lastBuildId
	 * @param lastBuildStatus
	 * @param lastBuildResult
	 * @param lastBuildDuration
	 * @return a TravisRepo object created from the values
	 */
	@JsonCreator
	public static final TravisRepo fromValues(@JsonProperty("id") final int id,
			@JsonProperty("slug") final String slug,
			@JsonProperty("last_build_id") final int lastBuildId,
			@JsonProperty("last_build_status") final String lastBuildStatus,
			@JsonProperty("last_build_result") final String lastBuildResult,
			@JsonProperty("last_build_duration") final int lastBuildDuration) {
		return new TravisRepo(id, Strings.nullToEmpty(slug), lastBuildId, Strings.nullToEmpty(lastBuildStatus),
				Strings.nullToEmpty(lastBuildResult), lastBuildDuration);
	}

	@Override
	public String toString() {
		return "{TravisResp:{id:" + this.id + ", slug:" + this.slug
				+ ", lastBuildId:" + this.lastBuildId + ", lastBuildStatus:"
				+ this.lastBuildStatus + ", lastBuildResult:"
				+ this.lastBuildResult + ", lastBuildDuration:"
				+ this.lastBuildDuration + "}}";

	}
}
