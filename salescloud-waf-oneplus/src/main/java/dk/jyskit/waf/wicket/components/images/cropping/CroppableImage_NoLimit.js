$(function($) {
	$('#jcrop_target').Jcrop({
    	setSelect: [ 0, 0, 40, 40 ],
    	addClass: 'jcrop-dark',
		onSelect: updateCoords
	});
});

function updateCoords(c) {
	$('#x').val(c.x);
	$('#y').val(c.y);
	$('#w').val(c.w);
	$('#h').val(c.h);
};
