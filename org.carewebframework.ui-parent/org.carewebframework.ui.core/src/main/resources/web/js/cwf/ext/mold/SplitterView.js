/**
 * Mold: SplitterView.js
 */
function (out) {
	out.push('<div', this.domAttrs_(), '>');
	for (var child = this.firstChild; child; child = child.nextSibling)
		child.redraw(out);
	out.push('</div>');
}
