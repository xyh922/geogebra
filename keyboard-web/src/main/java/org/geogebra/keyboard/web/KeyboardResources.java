package org.geogebra.keyboard.web;

import org.geogebra.web.resources.SassResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface KeyboardResources extends ClientBundle {

	KeyboardResources INSTANCE = GWT.create(KeyboardResources.class);

	// ONSCREENKEYBOARD
	@Source("org/geogebra/common/icons/png/keyboard/view_close.png")
	ImageResource keyboard_close();

	@Source("org/geogebra/common/icons/png/keyboard/shift_purple.png")
	ImageResource keyboard_shiftDown();

	@Source("org/geogebra/common/icons/png/keyboard/shift_black.png")
	ImageResource keyboard_shift();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_shiftDown.png")
	ImageResource keyboard_shiftDownOld();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_shift.png")
	ImageResource keyboard_shiftOld();

	@Source("org/geogebra/common/icons/png/keyboard/backspace.png")
	ImageResource keyboard_backspace();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_backspace.png")
	ImageResource keyboard_backspaceOld();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_enter.png")
	ImageResource keyboard_enter();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_arrowLeft.png")
	ImageResource keyboard_arrowLeft();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_arrowRight.png")
	ImageResource keyboard_arrowRight();

	@Source("org/geogebra/common/icons/png/keyboard/keyboard_open.png")
	ImageResource keyboard_show();

	@Source("org/geogebra/keyboard/css/keyboard-styles.scss")
	SassResource keyboardStyle();

	@Source("com/materializecss/sass/components/_waves.scss")
	SassResource wavesStyle();

	@Source("com/materializecss/js/waves.js")
	TextResource wavesScript();

	@Source("org/geogebra/common/icons/png/keyboard/integral.png")
	ImageResource integral();

	@Source("org/geogebra/common/icons/png/keyboard/d_dx.png")
	ImageResource derivative();

	@Source("org/geogebra/common/icons/png/keyboard/nroot.png")
	ImageResource nroot();

	@Source("org/geogebra/common/icons/png/keyboard/sqrt.png")
	ImageResource sqrt();

}