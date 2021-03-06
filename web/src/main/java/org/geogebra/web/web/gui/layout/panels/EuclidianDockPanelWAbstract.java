package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GetViewId;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.util.ZoomPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.view.consprotocol.ConstructionProtocolNavigationW;
import org.geogebra.web.web.main.AppWapplet;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class for all "euclidian" panels.
 * 
 * @author arpad (based on EuclidianDockPanelAbstract by Mathieu)
 * @remark {@link #getEuclidianView()} has to be overridden if
 *         {@link #getComponent()} does not return the euclidian view directly
 */
public abstract class EuclidianDockPanelWAbstract extends DockPanelW
		implements GetViewId {
	private ConstructionProtocolNavigationW consProtNav;
	private boolean hasEuclidianFocus;
	private boolean isViewForZoomPanel = false;
	/**
	 * panel with home,+,-,fullscreen btns
	 */
	ZoomPanel zoomPanel;

	/**
	 * default constructor
	 * 
	 * @param id
	 *            id
	 * @param title
	 *            title
	 * @param toolbar
	 *            toolbar string
	 * @param hasStyleBar
	 *            whether to show stylebar
	 * @param hasZoomPanel
	 *            - true if it has zoom panel
	 * @param menuOrder
	 *            The location of this view in the view menu, -1 if the view
	 *            should not appear at all
	 * @param shortcut
	 *            letter for Ctrl+Shift+letter shortcut
	 */
	public EuclidianDockPanelWAbstract(int id, String title, String toolbar,
			boolean hasStyleBar, boolean hasZoomPanel, int menuOrder,
			char shortcut) {
		super(id, title, toolbar, hasStyleBar, menuOrder,
				shortcut);
		this.isViewForZoomPanel = hasZoomPanel;
	}

	/**
	 * sets this euclidian panel to have the "euclidian focus"
	 * 
	 * @param hasFocus
	 *            whether to focus
	 */
	public final void setEuclidianFocus(boolean hasFocus) {
		hasEuclidianFocus = hasFocus;
	}
	
	@Override
	protected boolean titleIsBold(){
		return super.titleIsBold() || hasEuclidianFocus;
	}

	@Override
	public boolean updateResizeWeight(){
		return true;
	}
	
	/**
	 * @return view in this dock panel
	 */
	abstract public EuclidianView getEuclidianView();

	@Override
	public void setVisible(boolean sv) {
		super.setVisible(sv);
		// if (getEuclidianView() != null) {// also included in:
		if (getEuclidianView() instanceof EuclidianViewWInterface) {
			((EuclidianViewWInterface) getEuclidianView()).updateFirstAndLast(
					sv,
						false);
			}
		// }
	}

	/**
	 * Adds navigation bar
	 */
	public final void addNavigationBar() {
		consProtNav = (ConstructionProtocolNavigationW) (app.getGuiManager()
				.getConstructionProtocolNavigation(id));
		consProtNav.getImpl().addStyleName("consProtNav");
		if (getEuclidianPanel() == null) {
			loadComponent();
		}
		getEuclidianPanel().add(consProtNav.getImpl()); // may be invisible, but
														// made
													// visible later
		updateNavigationBar();
	}

	@Override
	public final void updateNavigationBar() {
		// ConstructionProtocolSettings cps = app.getSettings()
		// .getConstructionProtocol();
		// ((ConstructionProtocolNavigationW) consProtNav).settingsChanged(cps);
		// cps.addListener((ConstructionProtocolNavigation)consProtNav);

		if (app.getShowCPNavNeedsUpdate(id)) {
			app.setShowConstructionProtocolNavigation(
					app.showConsProtNavigation(id), id);
		}
		if (app.showConsProtNavigation(id)
				&& consProtNav == null) {
			this.addNavigationBar();
		}
		if (consProtNav != null) {
			consProtNav.update();
			consProtNav.setVisible(app.showConsProtNavigation(id));
			getEuclidianPanel().onResize();
		}
	}

	@Override
	public int navHeight() {
		if (this.consProtNav != null
				&& this.consProtNav.getImpl().getOffsetHeight() != 0) {
			return this.consProtNav.getImpl().getOffsetHeight();
		}
		return 30;
	}

	/**
	 * Wrapper of euclidian view
	 */
	public static class EuclidianPanel extends FlowPanel
			implements RequiresResize {

		/** dock panel */
		EuclidianDockPanelWAbstract dockPanel;
		/** panel for positioning furniture */
		AbsolutePanel absoluteEuclidianPanel;
		/** current height */
		int oldHeight = 0;
		/** current width */
		int oldWidth = 0;

		/**
		 * @param dockPanel
		 *            parent dock panel
		 */
		public EuclidianPanel(EuclidianDockPanelWAbstract dockPanel) {
			this(dockPanel, new AbsolutePanel());
			getElement().setAttribute("role", "application");
		}

		/**
		 * @param dockPanel
		 *            parent dock panel
		 * @param absPanel
		 *            absolute panel (for positioning stuff over canvas)
		 */
		public EuclidianPanel(EuclidianDockPanelWAbstract dockPanel,
				AbsolutePanel absPanel) {
			super();
			this.dockPanel = dockPanel;
			add(absoluteEuclidianPanel = absPanel);
			absoluteEuclidianPanel.addStyleName("EuclidianPanel");
			absoluteEuclidianPanel.getElement().getStyle()
					.setOverflow(Overflow.VISIBLE);
			checkFocus();
		}

		@Override
		public void onResize() {
			if (dockPanel.getApp() != null) {
				int h = dockPanel.getComponentInteriorHeight()
						- dockPanel.navHeightIfShown();
				int w = dockPanel.getComponentInteriorWidth();
				// TODO handle this better?
				// exit if new size cannot be determined
				// one dimension may be intentionally 0, resize to avoid DOM
				// overflow
				if (h < 0 || w < 0 || (w == 0 && h == 0)) {
					return;
				}
				if (h != oldHeight || w != oldWidth) {
					dockPanel.resizeView(w, h);
					oldHeight = h;
					oldWidth = w;
				} else {
					// it's possible that the width/height didn't change but the
					// position of EV did
					dockPanel.calculateEnvironment();
				}
				dockPanel.checkZoomPanelFits(h);
			}
		}

		// hack to fix GGB-697
		private native void checkFocus() /*-{
			var that = this;
			var forceResize = function() {
				that.@org.geogebra.web.web.gui.layout.panels.EuclidianDockPanelWAbstract.EuclidianPanel::forceResize()()
			};

			$wnd.visibilityEventMain(forceResize, forceResize);
		}-*/ ;

		private void forceResize() {
			EuclidianView view = dockPanel.getEuclidianView();
			if (view instanceof EuclidianViewWInterface) {
				((EuclidianViewWInterface) view).getG2P().forceResize();
				view.repaintView();
				view.suggestRepaint();
			}
		}

		@Override
		public boolean remove(Widget w) {
			return absoluteEuclidianPanel.remove(w);
		}

		public AbsolutePanel getAbsolutePanel() {
			return absoluteEuclidianPanel;
		}
	}

	protected abstract EuclidianPanel getEuclidianPanel();

	public AppW getApp() {
		return app;
	}

	public final AbsolutePanel getAbsolutePanel() {
		return getEuclidianPanel() == null ? null : getEuclidianPanel()
				.getAbsolutePanel();
	}

	private boolean allowZoomPanel() {
		return isViewForZoomPanel && ZoomPanel.neededFor(app);
	}

	@Override
	protected void addZoomPanel(MyDockLayoutPanel dockPanel) {
		if (allowZoomPanel()) {
			// This causes EV overlap toolbar
			// dockPanel.getElement().getStyle().setProperty("minHeight",
			// zoomPanel.getMinHeight());
			dockPanel.addSouth(zoomPanel, 0);

		}
	}

	@Override
	public void onResize() {
		super.onResize();
		if (((AppWapplet) app).getAppletFrame().getMOWToorbar() != null
				&& app.isWhiteboardActive()) {
			((AppWapplet) app).getAppletFrame().getMOWToorbar()
					.updateFloatingButtonsPosition();
		}
	}

	@Override
	protected void tryBuildZoomPanel() {
		if (allowZoomPanel()) {
			zoomPanel = new ZoomPanel(getEuclidianView());
		}
	}

	public abstract void calculateEnvironment();

	public abstract void resizeView(int width, int height);

	/**
	 * updates icon on the full screen button.
	 */
	public void updateFullscreen() {
		if (zoomPanel != null) {
			zoomPanel.updateFullscreen();
		}
	}
	
	@Override
	public final void setLabels() {
		super.setLabels();
		if (zoomPanel != null) {
			zoomPanel.setLabels();
		}
		if (graphicsContextMenuBtn != null) {
			String titletext = app.getLocalization().getMenu("Settings");
			graphicsContextMenuBtn.setTitle(titletext);
			graphicsContextMenuBtn.setAltText(titletext);
		}
	}

	/**
	 * Hides zoom buttons.
	 */
	public void hideZoomPanel() {
		if (zoomPanel != null) {
			zoomPanel.addStyleName("hidden");
		}
	}

	/**
	 * Shows zoom buttons.
	 */
	public void showZoomPanel() {
		if (zoomPanel != null) {
			zoomPanel.removeStyleName("hidden");
		}
	}

	/**
	 * Moves the zoom panel up for MOW toolbar
	 * 
	 * @param up
	 *            true if zoom panel should move up, false if zoom panel should
	 *            move down
	 */
	public void moveZoomPanelUpOrDown(boolean up) {
		if (up) {
			zoomPanel.removeStyleName("hideMowSubmenu");
			zoomPanel.addStyleName("showMowSubmenu");
		} else {
			zoomPanel.removeStyleName("showMowSubmenu");
			zoomPanel.addStyleName("hideMowSubmenu");
		}
	}

	/**
	 * Sets the bottom attribute of zoomPanel
	 * 
	 * @param add
	 *            true if needs to be set, false if needs to be removed
	 */
	public void setZoomPanelBottom(boolean add) {
		if (add) {
			zoomPanel.getElement().getStyle().setBottom(0, Unit.PX);
		} else {
			zoomPanel.getElement().getStyle().clearBottom();
		}
	}
	/**
	 * Focus the next available element on GUI. after geos.
	 */
	public void focusNextGUIElement() {
		if (zoomPanel != null) {
			zoomPanel.focusFirstButton();
		} else {
			// TODO add focus somewhere else like burger menu.
		}
	}

	/**
	 * Focus the last available element on GUI before geos.
	 */
	public void focusLastGUIElement() {
		if (graphicsContextMenuBtn != null) {
			graphicsContextMenuBtn.getElement().focus();
		}
	}

	/**
	 * Focus the last zoom button available or settings button.
	 */
	public void focusLastZoomButton() {
		if (zoomPanel != null) {
			zoomPanel.focusLastButton();
		} else {
			focusLastGUIElement();
		}
	}
	/**
	 * Checks if zoom panel fit on Euclidian View with given height and
	 * shows/hides it respectively.
	 * 
	 * @param height
	 *            Height of EV.
	 */
	public void checkZoomPanelFits(int height) {
		if (zoomPanel != null && ZoomPanel.neededFor(app)) {
			if (height < zoomPanel.getMinHeight()) {
				hideZoomPanel();
			} else {
				showZoomPanel();
			}
		}
	}
}
