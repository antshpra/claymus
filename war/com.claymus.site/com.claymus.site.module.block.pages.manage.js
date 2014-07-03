$(function() {
	$("#claymus-Blocks ul, #claymus-Blocks-DISABLED")
		.sortable({
			connectWith: '.claymus-Blocks-Sortable'
		})
		.disableSelection();
});