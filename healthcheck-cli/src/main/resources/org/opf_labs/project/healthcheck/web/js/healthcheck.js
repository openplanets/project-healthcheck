var projectList = {
  fourWeek: new Date(),
  threeMonth: new Date(),
  user: null,
  projects: [],
  currentProject: null,
  init: function(user, projects) {
	projectList.fourWeek = projectList.fourWeek.setDate(projectList.fourWeek.getDate() - 28);
	projectList.threeMonth = projectList.threeMonth.setDate(projectList.threeMonth.getDate() - (30 * 3));
    projectList.user = user;
    projectList.projects = projects.sort(function(a, b) {
	if (a.repo.updated_at < b.repo.updated_at) return 1;
	if (a.repo.updated_at > b.repo.updated_at) return -1;
	return 0;
      });
    $('#repos').empty();
    $.each(projectList.projects, projectList.toListItem);
	$("ul.proj-info > li").addClass("pull-left");
  },
  toListItem: function(index, proj) {
    projectList.currentProject = proj;
    var container = $('<li>').attr({
		class: "repoItem"
	});
	var div = $('<div>').attr({
		class: "row"
	});
    projectList.getProjectDetails().appendTo(div);
    projectList.getProjectInfo().appendTo(div);
    projectList.getProjectCI().appendTo(div);
	div.appendTo(container);
    container.appendTo('#repos');
	 $("[data-toggle='tooltip']").tooltip();
  },
  getProjectDetails: function() {
	var container = $('<div>').attr({
		class: "span4"
	});
	container.append(projectList.getName());
	container.append(projectList.getDescription());
	return container;
  },
  getName: function() {
    var anchor = $('<a>').attr({
	href: projectList.currentProject.repo.html_url
    }).text(" " + projectList.currentProject.repo.name);
    bootstrapUtils.getIcon("github-alt").prependTo(anchor);
    return anchor;
  },
  getDescription: function() {
	var div = $('<div>').attr({
	    class: "details"
	});
	var descContainer = $('<div class="desc">');
	var descPara = (projectList.currentProject.repo.description) ? $('<p>') : $('<p class="text-warning">');
	descPara.append($('<i class="icon-file-alt"></i>' + projectList.currentProject.repo.description + '</p></div>'));
	descContainer.append(descPara);
	div.append(descContainer);
	var list = $('<ul>').attr({
		class: "proj-info"
	});
	list.append(projectList.getActivity());
	list.append(bootstrapUtils.listPebble("laptop", projectList.currentProject.repo.language, "info"));
	list.append(bootstrapUtils.listPebble("tasks", projectList.currentProject.repo.open_issues, (projectList.currentProject.repo.open_issues > 0) ? "success" : "warning"));
	div.append(list);
	
	return div;
  },
  getActivity: function() {
	var updatedAt = new Date(projectList.currentProject.repo.updated_at);
	var status = "success";
	if (updatedAt < projectList.fourWeek) status = "warning";
	if (updatedAt < projectList.threeMonth) status = "important";
	return bootstrapUtils.listPebble("calendar", projectList.currentProject.repo.updated_at.toString().slice(0, 10), status)
  },
  getProjectInfo: function() {
	var div = $('<div>').attr({
	    class: "span2"
	});
	div.append(projectList.getIndicators());
	return div;
  },
  getIndicators: function() {
	var container = $('<ul>');
	var pebble = bootstrapUtils.listPebble("book", "readme", projectList.currentProject.indicators.read_me_url ? "success" : "important", projectList.currentProject.indicators.read_me_url);
	container.append(pebble);
	pebble = bootstrapUtils.listPebble("legal", "license", projectList.currentProject.indicators.license_url ? "success" : "important", projectList.currentProject.indicators.license_url);
	container.append(pebble);
	pebble = bootstrapUtils.listPebble("info-sign", "metadata", projectList.currentProject.indicators.metadata_url ? "success" : "important", projectList.currentProject.indicators.metadata_url);
	container.append(pebble);
	return container;
  },
  getProjectCI: function() {
	var div = $('<div>').attr({
	    class: "span2"
	});
	div.append(projectList.getTravisAnchor());
	return div;
  },
  getTravisAnchor: function() {
	if (!projectList.currentProject.ci.has_travis) {
		return bootstrapUtils.listPebble("wrench", "No Travis Build", "important");
	}
	var anchor = $('<a>').attr({
	  href: "https://travis-ci.org/" + projectList.currentProject.repo.owner.login + "/" + projectList.currentProject.repo.name
	});
	anchor.append(bootstrapUtils.pebble("wrench", " Travis", "success"));
	anchor.append($('<img>').attr({
	    src: "https://travis-ci.org/" + projectList.currentProject.repo.owner.login + "/" + projectList.currentProject.repo.name + ".png"
	}));
	return anchor;
  }
};
