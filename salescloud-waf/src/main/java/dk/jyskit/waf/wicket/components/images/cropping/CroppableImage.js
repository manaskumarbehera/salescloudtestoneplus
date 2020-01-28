$(function($) {
	$('#jcrop_target').Jcrop({
    	setSelect: [ 0, 0, ${component.targetWidth} - 1, ${component.targetHeight} - 1 ],
    	addClass: 'jcrop-dark',
		aspectRatio: ${component.targetWidth} / ${component.targetHeight},
		onSelect: updateCoords
	});
});

function updateCoords(c) {
	$('#x').val(c.x);
	$('#y').val(c.y);
	$('#w').val(c.w);
	$('#h').val(c.h);
};
