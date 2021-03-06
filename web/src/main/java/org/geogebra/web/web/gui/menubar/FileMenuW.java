package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.App;
import org.geogebra.common.main.ExamEnvironment;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialsManagerI;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.views.BooleanRenderable;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.StringHandler;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.export.PrintPreviewW;
import org.geogebra.web.web.gui.browser.SignInButton;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.exam.ExamDialog;
import org.geogebra.web.web.gui.util.SaveDialogW;
import org.geogebra.web.web.gui.util.ShareDialogW;

import com.google.gwt.user.client.ui.MenuItem;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Web implementation of FileMenu
 */
public class FileMenuW extends GMenuBar implements BooleanRenderable {
	
	private MenuItem shareItem;
	/** clear construction and reset GUI */
	Runnable newConstruction;
	private MenuItem printItem;
	private Localization loc;
	
	/**
	 * @param app application
	 */
	public FileMenuW(final AppW app) {
		super(true, "file", app);
		this.loc = app.getLocalization();
	    this.newConstruction = new Runnable() {
			
			@Override
			public void run() {
				app.setWaitCursor();
				app.fileNew();
				app.setDefaultCursor();
				
				if (!app.isUnbundledOrWhiteboard()) {
					app.showPerspectivesPopup();
				}
			}
		};
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName("matStackPanel");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
	    initActions();
	}
	
	/**
	 * @return whether native JS function for sharing is present
	 */
	public native static boolean nativeShareSupported()/*-{
		if ($wnd.android && $wnd.android.share) {
			return true;
		}
		return false;
	}-*/;

	/**
	 * Exit exam and restore normal mode
	 */
	protected void exitAndResetExam() {
		getApp().getLAF().toggleFullscreen(false);
		ExamEnvironment exam = getApp().getExam();
		exam.exit();
		boolean examFile = getApp().getArticleElement().hasDataParamEnableGraphing();
		String buttonText = null;
		AsyncOperation<String[]> handler = null;
		if (examFile) {
			if (getApp().getVersion().isAndroidWebview()) {
				handler = new AsyncOperation<String[]>() {
					@Override
					public void callback(String[] dialogResult) {
						// for android tablets we just want to exit app
						ExamDialog.exitApp();
					}
				};
				buttonText = loc.getMenu("Exit");
			} else {
				handler = new AsyncOperation<String[]>() {
					@Override
					public void callback(String[] dialogResult) {
						getApp().setNewExam();
						ExamDialog.startExam(null, getApp());
					}
				};
				buttonText = loc.getMenu("Restart");
			}
			exam.setHasGraph(true);
			boolean supportsCAS = getApp().getSettings().getCasSettings()
					.isEnabled();
			boolean supports3D = getApp().getSettings().getEuclidian(-1).isEnabled();
			if (!supports3D && supportsCAS) {
					getApp().showMessage(
						exam.getLog(getApp().getLocalization(), getApp().getSettings()),
						loc.getMenu("ExamCAS"), buttonText, handler);
			} else if (!supports3D && !supportsCAS) {
				if (getApp().enableGraphing()) {
					getApp().showMessage(
							exam.getLog(getApp().getLocalization(),
									getApp().getSettings()),
							loc.getMenu("ExamGraphingCalc.long"), buttonText,
							handler);
				} else {
					getApp().showMessage(
							exam.getLog(getApp().getLocalization(),
									getApp().getSettings()),
							loc.getMenu("ExamSimpleCalc.long"), buttonText,
							handler);
				}
			}
		} else {
			getApp().showMessage(exam.getLog(loc, getApp().getSettings()),
					loc.getMenu("exam_log_header") + " "
							+ getApp().getVersionString(),
					buttonText, handler);
		}
		getApp().setExam(null);
		getApp().resetViewsEnabled();
		Layout.initializeDefaultPerspectives(getApp(), 0.2);
		getApp().getLAF().addWindowClosingHandler(getApp());
		getApp().fireViewsChangedEvent();
		getApp().getGuiManager().updateToolbarActions();
		getApp().getGuiManager().setGeneralToolBarDefinition(
				ToolBar.getAllToolsNoMacros(true, false, getApp()));
		getApp().getGuiManager().resetMenu();
		getApp().setActivePerspective(0);
	}
	
	private void initActions() {
		// if (!app.has(Feature.NEW_START_SCREEN)) {
		if (getApp().isExam()) {
			addItem(MainMenu.getMenuBarHtml(GuiResources.INSTANCE
					.menu_icon_sign_out().getSafeUri().asString(),
					loc.getMenu("exam_menu_exit"), true), true,
					new MenuCommand(getApp()) { // Close

				@Override
				public void doExecute() {
							showExamExitDialog();
						}
					});
			return;
		}
		/*
		 * } else { if (app.isExam()) { return; } }
		 */
		// this is enabled always
		addItem(MainMenu.getMenuBarHtml(
				getApp().isUnbundledOrWhiteboard()
						? MaterialDesignResources.INSTANCE.add_black()
								.getSafeUri().asString()
						: GuiResources.INSTANCE
				.menu_icon_file_new().getSafeUri().asString(),
				loc.getMenu("New"), true), true, new MenuCommand(getApp()) {

			@Override
			public void doExecute() {
						((DialogManagerW) getApp().getDialogManager()).getSaveDialog().showIfNeeded(newConstruction);
			}
		});
		// open menu is always visible in menu
		addItem(MainMenu.getMenuBarHtml(
				getApp().isUnbundledOrWhiteboard()
						? MaterialDesignResources.INSTANCE.search_black()
								.getSafeUri().asString()
						: GuiResources.INSTANCE
				.menu_icon_file_open().getSafeUri().asString(),
				loc.getMenu("Open"), true), true, new MenuCommand(getApp()) {
    		
				@Override
				public void doExecute() {
			        getApp().openSearch(null);
				}
				});
		if(getApp().getLAF().undoRedoSupported()) {
			addItem(MainMenu.getMenuBarHtml(
					getApp().isUnbundledOrWhiteboard()
							? MaterialDesignResources.INSTANCE.save_black()
									.getSafeUri().asString()
							: GuiResources.INSTANCE
					.menu_icon_file_save().getSafeUri().asString(),
					loc.getMenu("Save"), true), true, new MenuCommand(getApp()) {
		
				@Override
				public void doExecute() {
			        getApp().getGuiManager().save();
				}
			});			
		}
		addSeparator();
		shareItem = addItem(MainMenu.getMenuBarHtml(
				getApp().isUnbundledOrWhiteboard()
						? MaterialDesignResources.INSTANCE.share_black()
								.getSafeUri().asString()
						: GuiResources.INSTANCE
					.menu_icon_file_share().getSafeUri().asString(),
					loc.getMenu("Share"), true), true, new MenuCommand(getApp()) {

						@Override
						public void doExecute() {
							if (!nativeShareSupported()) {
								showShareDialog();
							} else {
								getApp().getGgbApi().getBase64(true,
										getShareStringHandler(getApp()));
							}
					}
			});
		if (getApp().getLAF().exportSupported() && !getApp().isUnbundledOrWhiteboard()) {
			addItem(MainMenu.getMenuBarHtml(
					GuiResources.INSTANCE
							.menu_icons_file_export().getSafeUri().asString(),
					loc.getMenu("DownloadAs") + Unicode.ELLIPSIS, true),
					true, new ExportMenuW(getApp()), true);
		}
		if (getApp().getLAF().printSupported()) {
			printItem = new MenuItem(
					MainMenu.getMenuBarHtml(
							getApp().isUnbundledOrWhiteboard()
							? MaterialDesignResources.INSTANCE.print_black()
									.getSafeUri().asString()
							: GuiResources.INSTANCE
							.menu_icons_file_print().getSafeUri().asString(),
					loc.getMenu("PrintPreview"), true),
					true, new MenuCommand(
					getApp()) {

				@Override
						public void doExecute() {
							if (getApp().getGuiManager()
									.showView(App.VIEW_EUCLIDIAN)
									|| getApp().getGuiManager().showView(
											App.VIEW_EUCLIDIAN2)
									|| getApp().getGuiManager().showView(
											App.VIEW_ALGEBRA)
									|| getApp().getGuiManager().showView(
											App.VIEW_CONSTRUCTION_PROTOCOL)) {
								new PrintPreviewW(getApp()).show();
							}
						}
			});
			// updatePrintMenu();
			addItem(printItem);
		}
	    getApp().getNetworkOperation().getView().add(this);
	    if (!getApp().getNetworkOperation().isOnline()) {
	    	render(false);    	
	    }
	}

	/**
	 * Show exit exam dialog
	 */
	protected void showExamExitDialog() {
		// set Firefox dom.allow_scripts_to_close_windows in about:config to
		// true to make this work
		String[] optionNames = { loc.getMenu("Cancel"), loc.getMenu("Exit") };
		getApp().getGuiManager().getOptionPane().showOptionDialog(getApp(),
				loc.getMenu("exam_exit_confirmation"), // ExitExamConfirm
				loc.getMenu("exam_exit_header"), // ExitExamConfirmTitle
				1, GOptionPane.WARNING_MESSAGE, null, optionNames,
				new AsyncOperation<String[]>() {
					@Override
					public void callback(String[] obj) {
						if ("1".equals(obj[0])) {
							exitAndResetExam();
						}
					}
				});
	}

	/**
	 * SHow the custom share dialog
	 */
	protected void showShareDialog() {
		Runnable shareCallback = new Runnable() {

			@Override
			public void run() {
				ShareDialogW sd = new ShareDialogW(getApp());
				sd.setVisible(true);
				sd.center();
			}
		};
		if (getApp().getActiveMaterial() == null
				|| "P".equals(getApp().getActiveMaterial().getVisibility())) {
			if (!getApp().getLoginOperation().isLoggedIn()) {
				// not saved, not logged in
				getApp().getLoginOperation().getView().add(new EventRenderable() {

					@Override
					public void renderEvent(BaseEvent event) {
						if (event instanceof LoginEvent
								&& ((LoginEvent) event).isSuccessful()) {
							showShareDialog();
						}
					}
				});
				((SignInButton) getApp().getLAF().getSignInButton(getApp())).login();
			} else {
				// not saved, logged in
				((DialogManagerW) getApp().getDialogManager()).getSaveDialog()
						.setDefaultVisibility(SaveDialogW.Visibility.Shared)
					.showIfNeeded(shareCallback, true);
			}
		} else {
			// saved
			shareCallback.run();
		}
	}

	/**
	 * Go to geogebra.org or close iframe if we are running in one
	 */
	protected native void backToGeoGebra() /*-{
		if ($wnd != $wnd.parent) {
			$wnd.parent.postMessage("{\"type\":\"closesingleton\"}",
					location.protocol + "//" + location.host);
		} else {
			$wnd.location.assign("/");
		}
	}-*/;
	/**
	 * 
	 * @param app
	 *            application
	 * @return handler for native sharing
	 */
	public static StringHandler getShareStringHandler(final AppW app) {
		return new StringHandler(){
			@Override
			public void handle(String s) {
				String title = app.getKernel().getConstruction().getTitle();
				MaterialsManagerI fm = app.getFileManager();
				fm.nativeShare(s, "".equals(title) ? "construction" : title);
			}
		};
	}

	/**
	 * @param online wether the application is online
	 * renders a the online - offline state of the FileMenu
	 */
	@Override
	public void render(boolean online) {
		shareItem.setEnabled(online);
	    if (!online) {
			shareItem.setTitle(loc.getMenu("Offline"));
		} else {
			shareItem.setTitle("");
		}
    }


}
