/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package net.openchrom.chromatogram.xxd.report.supplier.excel.template.settings;

import java.io.File;

import org.eclipse.chemclipse.model.settings.AbstractProcessSettings;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.support.settings.FileSettingProperty;
import org.eclipse.chemclipse.support.settings.FileSettingProperty.DialogType;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChromatogramReportSettings extends AbstractProcessSettings implements IProcessSettings {

	@JsonProperty(value = "ESCAPE Master Template", defaultValue = ".xltx")
	@FileSettingProperty(dialogType = DialogType.OPEN_DIALOG, allowEmpty = false)
	private File template = null;

	@JsonProperty(value = "ESCAPE Analysis Report", defaultValue = ".xlsx")
	@FileSettingProperty(dialogType = DialogType.SAVE_DIALOG, allowEmpty = false)
	private File report = null;

	public File getTemplate() {

		return template;
	}

	public void setTemplate(File template) {

		this.template = template;
	}

	public File getReport() {

		return report;
	}

	public void setReport(File report) {

		this.report = report;
	}
}