$(function() {
	$("#claymus-Contents ul, #claymus-Contents-DISABLED")
		.sortable({
			connectWith: '.claymus-Contents-Sortable'
		})
		.disableSelection();
});