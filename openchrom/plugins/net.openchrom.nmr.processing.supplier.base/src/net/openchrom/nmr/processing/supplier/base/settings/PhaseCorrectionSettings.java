package net.openchrom.nmr.processing.supplier.base.settings;

import java.util.Observable;

/**
 * this class is final, synchronized, {@link Cloneable} and {@link Observable} since it participates in the dynamic settings framework
 *
 */
public final class PhaseCorrectionSettings extends Observable implements Cloneable {

	public enum PivotPointSelection {
		LEFT("pivot @ far left end of the spectrum"), //
		MIDDLE("pivot @ middle of the spectrum"), //
		PEAK_MAX("pivot @ biggest peak of the spectrum"), //
		USER_DEFINED("pivot @ user defined position"), //
		NOT_DEFINED("phasing without specified pivot point");//

		private String pivotPosition = "";

		private PivotPointSelection(String pivotPosition) {
			this.pivotPosition = pivotPosition;
		}

		@Override
		public String toString() {

			return pivotPosition;
		}
	}

	private PivotPointSelection pivotPointSelection = PivotPointSelection.PEAK_MAX;
	private double zeroOrderPhaseCorrection = 0.0;
	private double firstOrderPhaseCorrection = 0.0;
	private double userDefinedPivotPointValue = 0.0;
	private double dspPhaseFactor = 0.0;

	public synchronized double getDspPhaseFactor() {

		return dspPhaseFactor;
	}

	public synchronized void setDspPhaseFactor(double dspPhaseFactor) {

		this.dspPhaseFactor = dspPhaseFactor;
		fireUpdate();
	}

	public synchronized PivotPointSelection getPivotPointSelection() {

		return pivotPointSelection;
	}

	public synchronized void setPivotPointSelection(PivotPointSelection pivotPointSelection) {

		this.pivotPointSelection = pivotPointSelection;
		fireUpdate();
	}

	public synchronized double getZeroOrderPhaseCorrection() {

		return zeroOrderPhaseCorrection;
	}

	public synchronized void setZeroOrderPhaseCorrection(double zeroOrderPhaseCorrection) {

		this.zeroOrderPhaseCorrection = zeroOrderPhaseCorrection;
		fireUpdate();
	}

	public synchronized double getFirstOrderPhaseCorrection() {

		return firstOrderPhaseCorrection;
	}

	public synchronized void setFirstOrderPhaseCorrection(double firstOrderPhaseCorrection) {

		this.firstOrderPhaseCorrection = firstOrderPhaseCorrection;
		fireUpdate();
	}

	public synchronized double getUserDefinedPivotPointValue() {

		return userDefinedPivotPointValue;
	}

	public synchronized void setUserDefinedPivotPointValue(double userDefinedPivotPointValue) {

		this.userDefinedPivotPointValue = userDefinedPivotPointValue;
		fireUpdate();
	}

	private synchronized void fireUpdate() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				setChanged();
				notifyObservers(this);
			}
		}).start();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PhaseCorrectionSettings [zeroOrderPhaseCorrection=");
		builder.append(zeroOrderPhaseCorrection);
		builder.append(", firstOrderPhaseCorrection=");
		builder.append(firstOrderPhaseCorrection);
		builder.append(", pivotPointSelection=");
		builder.append(pivotPointSelection);
		if(pivotPointSelection == PivotPointSelection.USER_DEFINED) {
			builder.append(", userDefinedPivotPointValue=");
			builder.append(userDefinedPivotPointValue);
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public synchronized PhaseCorrectionSettings clone() {

		PhaseCorrectionSettings settings = new PhaseCorrectionSettings();
		settings.dspPhaseFactor = dspPhaseFactor;
		settings.firstOrderPhaseCorrection = firstOrderPhaseCorrection;
		settings.pivotPointSelection = pivotPointSelection;
		settings.userDefinedPivotPointValue = userDefinedPivotPointValue;
		settings.zeroOrderPhaseCorrection = zeroOrderPhaseCorrection;
		return settings;
	}
}
