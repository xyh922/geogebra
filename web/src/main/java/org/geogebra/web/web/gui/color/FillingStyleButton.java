package org.geogebra.web.web.gui.color;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.util.ButtonPopupMenu;
import org.geogebra.web.web.gui.util.GeoGebraIconW;
import org.geogebra.web.web.gui.util.PopupMenuButtonW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;

/**
 * button to get popup with filling options
 */
public class FillingStyleButton extends PopupMenuButtonW {

	private static final int FILL_TYPES_COUNT = 5;
	private FillType fillTypes[] = { FillType.STANDARD, FillType.HATCH,
			FillType.DOTTED, FillType.CROSSHATCHED, FillType.HONEYCOMB };

	/**
	 * Filling style for fillable Geos
	 * 
	 * @param app
	 *            application
	 */
	public FillingStyleButton(App app) {
		super(app, createDummyIcons(5), -1, 5, SelectionTable.MODE_ICON, false);
		ButtonPopupMenu pp = (ButtonPopupMenu) getMyPopup();
		// pp.addStyleName("fillingPopup");
		pp.addStyleName("mowPopup");
		createFillTable();
	}

	private void createFillTable() {
		ImageOrText[] icons = new ImageOrText[FILL_TYPES_COUNT];
		for (int i = 0; i < FILL_TYPES_COUNT; i++) {
			icons[i] = GeoGebraIconW.createFillStyleIcon(i);
		}
		getMyTable().populateModel(icons);
		getMyTable().addClickHandler(new ClickHandler() {

			@Override

			public void onClick(ClickEvent event) {
				handlePopupActionEvent();
			}

		});
	}

	private static ImageOrText[] createDummyIcons(int count) {

		ImageOrText[] a = new ImageOrText[count];
		for (int i = 0; i < count; i++) {
			a[i] = new ImageOrText();
		}
		return a;
	}

	/**
	 * Shows/hides fill table.
	 * 
	 * @param b
	 *            true if filling is enabled.
	 */
	public void setFillEnabled(boolean b) {
		getMyTable().setVisible(b);
		if (b && !app.has(Feature.MOW_COLORPOPUP_IMPROVEMENTS)) {
			getMyPopup().setHeight("125px");
		}
	}

	/**
	 * @return selected fill type
	 */
	public FillType getSelectedFillType() {
		int idx = getMyTable().getSelectedIndex();
		return idx != -1 ? fillTypes[idx] : FillType.STANDARD;
	}

	/**
	 * Sets the original fill type of the geo.
	 * 
	 * @param fillType
	 *            the type to select initially.
	 */
	public void setFillType(FillType fillType) {
		for (int i = 0; i < fillTypes.length; i++) {
			if (fillTypes[i] == fillType) {
				getMyTable().setSelectedIndex(i);
			}
		}
		// this.setIcon(getButtonIcon());
	}

	@Override
	public ImageOrText getButtonIcon() {
		/*
		 * if (getSelectedIndex() > -1) { return GeoGebraIconW
		 * .createFillStyleIcon(getMyTable().getSelectedIndex());
		 * 
		 * }
		 * 
		 * return new ImageOrText();
		 */
		return new ImageOrText(new ImageResourcePrototype(null,
				MaterialDesignResources.INSTANCE.filling_black().getSafeUri(),
				0, 0, 24, 24, false, false));
	}
}
