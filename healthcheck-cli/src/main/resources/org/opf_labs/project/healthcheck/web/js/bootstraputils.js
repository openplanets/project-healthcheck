var bootstrapUtils = {
  pebble: function(icon, text, state, href) {
	if (state) {
		state= ' label-' + state;
	} else {
		state = "";
	}
	var anchor = $('<a class="label' + state + '"> ' + text + '</a>');
	if (href) {
		anchor.attr("href", href);
	}
	anchor.prepend(bootstrapUtils.getIcon(icon));
    return anchor;
  },
  getIcon: function(icon) {
	icon = "icon-" + icon
	return $('<i>').attr({
		class: icon
	});
  },
  listPebble: function(icon, text, state, href) {
    return $('<li>').append(bootstrapUtils.pebble(icon, text, state, href));
  }
};