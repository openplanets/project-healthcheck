/**
 * @namespace JS Encapsulation of information required to draw the project list.
 *            The project array info is provided in the HTML page and passed to
 *            the init function.
 */
var projectList = {
	// Holders for pre-defined periods that measure activity
	fourWeek : new Date(),
	threeMonth : new Date(),
	// Default value for the project filter, shows all projects
	filter : "All",
	// var for user details
	user : null,
	// Empty array for projects
	projects : [],
	// Current project
	currentProject : null,
	/**
	 * @function init
	 * 
	 * Initialisation of project list
	 * 
	 * @param user
	 *            details of the GitHub user whose projects are listed
	 * @param projects
	 *            an array of GitHub project details
	 */
	init : function(user, projects) {
		// Initialise the set periods
		projectList.fourWeek = projectList.fourWeek
				.setDate(projectList.fourWeek.getDate() - 28);
		projectList.threeMonth = projectList.threeMonth
				.setDate(projectList.threeMonth.getDate() - (30 * 3));
		// and the user details
		projectList.user = user;
		// Finally sort the projects by activity date
		projectList.projects = projects.sort(function(a, b) {
			if (a.updated < b.updated)
				return 1;
			if (a.updated > b.updated)
				return -1;
			return 0;
		});
		// Iterate through the filter list items
		$('#filters > li').each(function(index, element) {
			// Get the anchor and add an onclick event to filters
			$(element).children(":first").click(function(event) {
				// Set the filter value and re-draw
				projectList.filter = $(this).text();
				projectList.updateProjectList();
			});
		});
		// Finally draw the list
		projectList.updateProjectList();
	},
	/**
	 * @function updateProjectList
	 * 
	 * Updates the HTML list of projects.
	 */
	updateProjectList : function() {
		// Empty the current list
		$('#repos').empty();
		// Iterate through each item of the filtered list, filtering
		// is applied via a jQuery grep, each item is passed to
		// the addProjectToList function.
		$.each(jQuery.grep(projectList.projects, function(proj) {
			// List filter, if all the return
			if (projectList.filter == "All")
				return true;
			// Else check vendor against filter value
			return (proj.metadata.vendor == projectList.filter);
		}), projectList.addProjectToList);
		$("ul.proj-info > li").addClass("pull-left");
	},
	/**
	 * @function addProjectToList
	 * 
	 * Adds a projects details to the HTML list
	 * 
	 * @param index
	 *            the index of the project in the projects array
	 * @param proj
	 *            the project to be added to the list
	 */
	addProjectToList : function(index, proj) {
		// Set current project member, avoid param passing
		projectList.currentProject = proj;
		// Create the row div
		var div = $('<div>').attr({
			class : "row"
		});
		// Add project details
		projectList.getProjectDetails().appendTo(div);
		projectList.getProjectInfo().appendTo(div);
		projectList.getProjectCI().appendTo(div);
		// Finally create a list item and append the div
		var container = $('<li>').attr({
			class : "repoItem"
		});
		div.appendTo(container);
		container.appendTo('#repos');
		$("[data-toggle='tooltip']").tooltip();
	},
	getProjectDetails : function() {
		var container = $('<div>').attr({
			class : "span4"
		});
		container.append(projectList.getName());
		container.append(projectList.getDescription());
		return container;
	},
	getName : function() {
		var anchor = $('<a>').attr({
			href : projectList.currentProject.url
		}).text(" " + projectList.currentProject.name);
		bootstrapUtils.getIcon("github-alt").prependTo(anchor);
		return anchor;
	},
	getDescription : function() {
		var div = $('<div>').attr({
			class : "details"
		});
		var descContainer = $('<div class="desc">');
		var descPara = (projectList.currentProject.description) ? $('<p>')
				: $('<p class="text-warning">');
		descPara.append($('<i class="icon-file-alt"></i>'
				+ projectList.currentProject.description + '</p></div>'));
		descContainer.append(descPara);
		div.append(descContainer);
		var list = $('<ul>').attr({
			class : "proj-info"
		});
		list.append(projectList.getActivity());
		list.append(bootstrapUtils.listPebble("laptop",
				projectList.currentProject.language, "info"));
		list.append(bootstrapUtils.listPebble("tasks",
				projectList.currentProject.openIssues,
				(projectList.currentProject.openIssues > 0) ? "success"
						: "warning"));
		div.append(list);

		return div;
	},
	getActivity : function() {
		var updatedAt = new Date(projectList.currentProject.updated);
		var status = "success";
		if (updatedAt < projectList.fourWeek)
			status = "warning";
		if (updatedAt < projectList.threeMonth)
			status = "important";
		return bootstrapUtils.listPebble("calendar", projectList
				.formatDate(updatedAt), status)
	},
	getProjectInfo : function() {
		var div = $('<div>').attr({
			class : "span2"
		});
		div.append(projectList.getIndicators());
		return div;
	},
	getIndicators : function() {
		var container = $('<ul>');
		var pebble = bootstrapUtils.listPebble("book", "readme",
				projectList.currentProject.indicators.readMeUrl ? "success"
						: "important",
				projectList.currentProject.indicators.readMeUrl);
		container.append(pebble);
		pebble = bootstrapUtils.listPebble("legal", "license",
				projectList.currentProject.indicators.licenseUrl ? "success"
						: "important",
				projectList.currentProject.indicators.licenseUrl);
		container.append(pebble);
		pebble = bootstrapUtils.listPebble("info-sign", "metadata",
				projectList.currentProject.indicators.metadataUrl ? "success"
						: "important",
				projectList.currentProject.indicators.metadataUrl);
		container.append(pebble);
		return container;
	},
	getProjectCI : function() {
		var div = $('<div>').attr({
			class : "span2"
		});
		div.append(projectList.getTravisAnchor());
		return div;
	},
	getTravisAnchor : function() {
		if (!projectList.currentProject.ci.hasTravis) {
			return bootstrapUtils.listPebble("wrench", "No Travis Build",
					"important");
		}
		var anchor = $('<a>').attr(
				{
					href : "https://travis-ci.org/"
							+ projectList.currentProject.ownerLogin + "/"
							+ projectList.currentProject.name
				});
		anchor.append(bootstrapUtils.pebble("wrench", " Travis", "success"));
		anchor.append($('<img>').attr(
				{
					src : "https://travis-ci.org/"
							+ projectList.currentProject.ownerLogin + "/"
							+ projectList.currentProject.name + ".png"
				}));
		return anchor;
	},
	formatDate : function(date) {
		return date.getFullYear() + "-" + date.getMonth() + "-"
				+ date.getDate();
	}
};
