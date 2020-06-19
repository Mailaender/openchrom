/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.xxd.process.supplier.templates.ui.swt.peaks;

import java.util.List;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.components.ISearchListener;
import org.eclipse.chemclipse.swt.ui.components.SearchSupportUI;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import net.openchrom.xxd.process.supplier.templates.model.DetectorSetting;
import net.openchrom.xxd.process.supplier.templates.preferences.PreferenceSupplier;
import net.openchrom.xxd.process.supplier.templates.ui.preferences.PagePeakDetector;
import net.openchrom.xxd.process.supplier.templates.ui.preferences.PreferencePage;
import net.openchrom.xxd.process.supplier.templates.ui.swt.PeakDetectorListUI;
import net.openchrom.xxd.process.supplier.templates.ui.wizards.ProcessDetectorSettings;

public class ProcessDetectorUI extends Composite {

	private DetectorController controller;
	private Composite toolbarSearch;
	private PeakDetectorListUI peakDetectorListUI;

	public ProcessDetectorUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setController(DetectorController controller) {

		this.controller = controller;
	}

	public void setInput(ProcessDetectorSettings processSettings) {

		if(processSettings != null) {
			List<DetectorSetting> detectorSettings = processSettings.getDetectorSettings();
			peakDetectorListUI.setInput(detectorSettings);
			if(detectorSettings.size() > 0) {
				peakDetectorListUI.getTable().select(0);
			}
		} else {
			peakDetectorListUI.setInput(null);
		}
	}

	public int getSelection() {

		return peakDetectorListUI.getTable().getSelectionIndex();
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(1, true);
		setLayout(gridLayout);
		//
		createToolbarMain(this);
		toolbarSearch = createToolbarSearch(this);
		peakDetectorListUI = createTable(this);
		//
		PartSupport.setCompositeVisibility(toolbarSearch, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createToggleToolbarSearch(composite);
		createButtonVisibilityTIC(composite);
		createButtonVisibilityXIC(composite);
		createButtonReplacePeak(composite);
		createSettingsButton(composite);
	}

	private SearchSupportUI createToolbarSearch(Composite parent) {

		SearchSupportUI searchSupportUI = new SearchSupportUI(parent, SWT.NONE);
		searchSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSupportUI.setSearchListener(new ISearchListener() {

			@Override
			public void performSearch(String searchText, boolean caseSensitive) {

				peakDetectorListUI.setSearchText(searchText, caseSensitive);
			}
		});
		//
		return searchSupportUI;
	}

	private Button createToggleToolbarSearch(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle search toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarSearch);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SEARCH, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonVisibilityTIC(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		adjustButtonVisibilityTIC(button);
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.toggleShowChromatogramDetectorTIC();
				adjustButtonVisibilityTIC(button);
				updateSelection();
			}
		});
		//
		return button;
	}

	private void adjustButtonVisibilityTIC(Button button) {

		if(PreferenceSupplier.isShowChromatogramDetectorTIC()) {
			button.setToolTipText("TIC is active.");
			button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_TIC_SHOW, IApplicationImage.SIZE_16x16));
		} else {
			button.setToolTipText("TIC is deactivated.");
			button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_TIC_HIDE, IApplicationImage.SIZE_16x16));
		}
	}

	private Button createButtonVisibilityXIC(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		adjustButtonVisibilityXIC(button);
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.toggleShowChromatogramDetectorXIC();
				adjustButtonVisibilityXIC(button);
				updateSelection();
			}
		});
		//
		return button;
	}

	private void adjustButtonVisibilityXIC(Button button) {

		if(PreferenceSupplier.isShowChromatogramDetectorXIC()) {
			button.setToolTipText("XIC is active.");
			button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_XIC_SHOW, IApplicationImage.SIZE_16x16));
		} else {
			button.setToolTipText("XIC is deactivated.");
			button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHROMATOGRAM_XIC_HIDE, IApplicationImage.SIZE_16x16));
		}
	}

	private Button createButtonReplacePeak(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		adjustDetectorButton(button);
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceSupplier.toggleDetectorReplaceNearestPeak();
				adjustDetectorButton(button);
			}
		});
		//
		return button;
	}

	private void adjustDetectorButton(Button button) {

		if(PreferenceSupplier.isDetectorReplaceNearestPeak()) {
			button.setToolTipText("Replace the nearest peak.");
			button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK_REPLACE, IApplicationImage.SIZE_16x16));
		} else {
			button.setToolTipText("Add the peak.");
			button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK_ADD, IApplicationImage.SIZE_16x16));
		}
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Open the Settings");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", new PagePeakDetector()));
				preferenceManager.addToRoot(new PreferenceNode("2", new PreferencePage()));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private PeakDetectorListUI createTable(Composite parent) {

		PeakDetectorListUI listUI = new PeakDetectorListUI(parent, SWT.BORDER);
		Table table = listUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		table.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateSelection();
			}
		});
		//
		return listUI;
	}

	private void applySettings() {

		updateSelection();
	}

	private DetectorSetting getDetectorSetting() {

		Object object = peakDetectorListUI.getStructuredSelection().getFirstElement();
		if(object instanceof DetectorSetting) {
			return (DetectorSetting)object;
		}
		return null;
	}

	private void updateSelection() {

		if(controller != null) {
			DetectorSetting detectorSetting = getDetectorSetting();
			controller.update(detectorSetting);
		}
	}
}