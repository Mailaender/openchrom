/*******************************************************************************
 * Copyright (c) 2013, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package net.openchrom.msd.converter.supplier.cdf.model;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractScan;
import org.eclipse.chemclipse.model.core.MarkedTraceModus;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.AbstractScanMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonBounds;
import org.eclipse.chemclipse.msd.model.core.IIonMSn;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IonBounds;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.implementation.ImmutableZeroIon;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

public class VendorScan extends AbstractScan implements IVendorScan {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -1358043207860234804L;

	private static final Logger logger = Logger.getLogger(VendorScan.class);

	private static final float NORMALIZATION_BASE = 100.0f;
	private static final int LIMIT_SIM_MEASUREMENT = 10; // 10 m/z values
	private static final int INITIAL_CAPACITY = 16;

	private boolean isNormalized = false;
	private float normalizationBase = 0.0f;

	private double[] valueArrayIon;
	private float[] valueArrayAbundance;
	private int numberOfIons;

	private ImmutableZeroIon immutableZeroIon;
	private IScanMSD optimizedMassSpectrum;

	public VendorScan() {

		super();
		init();
	}

	public VendorScan(double[] valueArrayIon, float[] valueArrayAbundance) {

		super();
		init();
		setArrays(valueArrayIon, valueArrayAbundance);
	}

	private void init() {

		/*
		 * If mass spectrum will be cloned, the ion list will be created as a new
		 * instance in the method createNewIonList().<br/> The ions of the object to be
		 * cloned will be stored in the new object again by each implementing class.
		 * We still initialize the inherited list for compatibility with the class
		 * hierarchy, but this implementation stores its data in arrays.
		 */
		immutableZeroIon = new ImmutableZeroIon();
		valueArrayIon = new double[INITIAL_CAPACITY];
		valueArrayAbundance = new float[INITIAL_CAPACITY];
		numberOfIons = 0;
	}

	private void setArrays(double[] ions, float[] abundances) {

		if(ions == null || abundances == null) {
			throw new IllegalArgumentException("Ion and abundance arrays must not be null.");
		}
		if(ions.length != abundances.length) {
			throw new IllegalArgumentException("Ion and abundance arrays must have the same length.");
		}
		int capacity = Math.max(INITIAL_CAPACITY, ions.length);
		valueArrayIon = new double[capacity];
		valueArrayAbundance = new float[capacity];
		System.arraycopy(ions, 0, valueArrayIon, 0, ions.length);
		System.arraycopy(abundances, 0, valueArrayAbundance, 0, abundances.length);
		numberOfIons = ions.length;
	}

	private void ensureCapacity(int minCapacity) {

		if(valueArrayIon.length >= minCapacity) {
			return;
		}
		int newCapacity = Math.max(minCapacity, Math.max(INITIAL_CAPACITY, valueArrayIon.length * 2));
		double[] ionArray = new double[newCapacity];
		float[] abundanceArray = new float[newCapacity];
		System.arraycopy(valueArrayIon, 0, ionArray, 0, numberOfIons);
		System.arraycopy(valueArrayAbundance, 0, abundanceArray, 0, numberOfIons);
		valueArrayIon = ionArray;
		valueArrayAbundance = abundanceArray;
	}

	private int indexOfIon(double ion) {

		for(int i = 0; i < numberOfIons; i++) {
			if(Double.compare(valueArrayIon[i], ion) == 0) {
				return i;
			}
		}
		return -1;
	}

	private void appendIon(double ion, float abundance) {

		ensureCapacity(numberOfIons + 1);
		valueArrayIon[numberOfIons] = ion;
		valueArrayAbundance[numberOfIons] = abundance;
		numberOfIons++;
		setDirty(true);
	}

	private IIon createIonAt(int index) {

		return new Ion(valueArrayIon[index], valueArrayAbundance[index]);
	}

	@Override
	public boolean checkIntensityCollisions() {

		return true;
	}

	@Override
	public VendorScan addIons(List<IIon> ions, boolean addIntensities) {

		for(IIon ion : ions) {
			if(ion == null) {
				continue;
			}
			if(addIntensities) {
				addIon(true, ion);
			} else {
				addIon(false, ion);
			}
		}
		return this;
	}

	@Override
	public VendorScan addIon(boolean addIntensity, IIon ion) {

		/*
		 * Return if the ion is null.
		 */
		if(ion == null) {
			logger.warn("The ion must be not null.");
			return this;
		}

		if(checkIntensityCollisions()) {
			int index = indexOfIon(ion.getIon());
			if(index >= 0) {
				/*
				 * Check whether the intensity should be added or only the higher intensity
				 * should be taken.<br/> Replace the abundance only, if the abundance is higher
				 * than the older one otherwise do nothing
				 */
				if(addIntensity) {
					addIntensities(index, ion);
				} else {
					if(ion.getAbundance() >= valueArrayAbundance[index]) {
						addHigherIntensity(index, ion);
					}
				}
				return this;
			}
		}
		/*
		 * Add a new ion.
		 */
		appendIon(ion.getIon(), ion.getAbundance());
		return this;
	}

	@Override
	public VendorScan addIon(IIon ion, boolean checked) {

		if(ion == null) {
			logger.warn("The ion must be not null.");
			return this;
		}
		if(checked) {
			addIon(ion);
		} else {
			appendIon(ion.getIon(), ion.getAbundance());
		}
		return this;
	}

	@Override
	public VendorScan addIon(IIon ion) {

		return addIon(false, ion);
	}

	@Override
	public VendorScan removeIon(IIon ion) {

		if(ion != null) {
			int index = indexOfIon(ion.getIon());
			if(index >= 0) {
				removeAt(index);
			}
		}
		return this;
	}

	@Override
	public VendorScan removeAllIons() {

		numberOfIons = 0;
		setDirty(true);
		return this;
	}

	@Override
	public VendorScan removeIon(int ion) {

		/*
		 * Initialize the list of mass over charge ratios (ion) to be removed.
		 */
		Set<Integer> ions = new HashSet<>();
		ions.add(ion);
		removeIons(ions);
		return this;
	}

	@Override
	public VendorScan removeIons(Set<Integer> ions) {

		if(ions == null) {
			// TODO maybe log warning?
			return this;
		}

		int writeIndex = 0;
		boolean removed = false;
		for(int readIndex = 0; readIndex < numberOfIons; readIndex++) {
			int nominalIon = AbstractIon.getIon(valueArrayIon[readIndex]);
			if(ions.contains(nominalIon)) {
				removed = true;
				continue;
			}
			if(writeIndex != readIndex) {
				valueArrayIon[writeIndex] = valueArrayIon[readIndex];
				valueArrayAbundance[writeIndex] = valueArrayAbundance[readIndex];
			}
			writeIndex++;
		}
		if(removed) {
			numberOfIons = writeIndex;
			setDirty(true);
		}
		return this;
	}

	@Override
	public VendorScan removeIons(IMarkedIons markedIons) {

		if(markedIons == null) {
			// TODO maybe log warning?
			return this;
		}

		Set<Integer> nominalIons = markedIons.getIonsNominal();
		MarkedTraceModus markedTraceModus = markedIons.getMarkedTraceModus();
		switch(markedTraceModus) {
			case INCLUDE:
				/*
				 * Remove all listed ions.
				 */
				removeIons(nominalIons);
				break;
			case EXCLUDE:
				/*
				 * Remove all except the listed ions.
				 */
				Set<Integer> removeIons = new HashSet<>();
				for(int i = 0; i < numberOfIons; i++) {
					int nominal = AbstractIon.getIon(valueArrayIon[i]);
					if(!nominalIons.contains(nominal)) {
						removeIons.add(nominal);
					}
				}
				removeIons(removeIons);
				break;
			default:
				/*
				 * Do nothing
				 */
				break;
		}

		return this;
	}

	/**
	 * Use this list only to iterate through the ions of this mass spectrum.<br/>
	 * To add and remove ions, use the methods of this class.
	 */
	@Override
	public List<IIon> getIons() {

		return Collections.unmodifiableList(new AbstractList<>() {

			@Override
			public IIon get(int index) {

				if(index < 0 || index >= numberOfIons) {
					throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + numberOfIons);
				}
				return createIonAt(index);
			}

			@Override
			public int size() {

				return numberOfIons;
			}
		});
	}

	public void clearIons() {

		numberOfIons = 0;
		setDirty(true);
	}

	@Override
	public float getTotalSignal(IMarkedIons markedIons) {

		float totalSignal = 0;
		/*
		 * If the excluded ions are null, return the total signal.
		 */
		if(markedIons == null || markedIons.isEmpty()) {
			totalSignal = getTotalSignal();
		} else {
			for(int i = 0; i < numberOfIons; i++) {
				IIon ion = createIonAt(i);
				if(useIon(ion, markedIons)) {
					totalSignal += ion.getAbundance();
				}
			}
		}
		return totalSignal;
	}

	private static boolean useIon(IIon ion, IMarkedIons filterIons) {

		Set<Integer> ionNominal = filterIons.getIonsNominal();
		switch(filterIons.getMarkedTraceModus()) {
			case EXCLUDE:
				return ionNominal.contains(AbstractIon.getIon(ion.getIon()));
			case INCLUDE:
				return !ionNominal.contains(AbstractIon.getIon(ion.getIon()));
			default:
				return true;
		}
	}

	@Override
	public float getTotalSignal() {

		float totalSignal = 0;
		for(int i = 0; i < numberOfIons; i++) {
			totalSignal += valueArrayAbundance[i];
		}
		return totalSignal;
	}

	@Override
	public IExtractedIonSignal getExtractedIonSignal() {

		if(hasIons()) {
			IIonBounds bounds = getIonBounds();
			double startIon = bounds.getLowestIon().getIon();
			double stopIon = bounds.getHighestIon().getIon();
			return getExtractedIonSignal(startIon, stopIon);
		} else {
			return new ExtractedIonSignal(0, 0);
		}
	}

	@Override
	public IExtractedIonSignal getExtractedIonSignal(double startIon, double stopIon) {

		ExtractedIonSignal extractedIonSignal;
		if(hasIons()) {
			extractedIonSignal = new ExtractedIonSignal(startIon, stopIon);
			for(int i = 0; i < numberOfIons; i++) {
				/*
				 * The ion.getIon() will be tested in the IExtractedIonSignal instance if it is
				 * valid (that means between startIon and stopIon).
				 */
				extractedIonSignal.setAbundance(createIonAt(i));
			}
			return extractedIonSignal;
		} else {
			return new ExtractedIonSignal(0, 0);
		}
	}

	@Override
	public double getBasePeak() {

		if(hasIons()) {
			return getHighestAbundance().getIon();
		} else {
			return 0.0f;
		}
	}

	@Override
	public float getBasePeakAbundance() {

		if(hasIons()) {
			return getHighestAbundance().getAbundance();
		} else {
			return 0.0f;
		}
	}

	@Override
	public IIon getHighestAbundance() {

		if(hasIons()) {
			int best = 0;
			for(int i = 1; i < numberOfIons; i++) {
				if(valueArrayAbundance[i] > valueArrayAbundance[best] || (Float.compare(valueArrayAbundance[i], valueArrayAbundance[best]) == 0 && Double.compare(valueArrayIon[i], valueArrayIon[best]) > 0)) {
					best = i;
				}
			}
			return createIonAt(best);
		} else {
			return immutableZeroIon;
		}
	}

	@Override
	public IIon getHighestIon() {

		if(hasIons()) {
			int best = 0;
			for(int i = 1; i < numberOfIons; i++) {
				if(Double.compare(valueArrayIon[i], valueArrayIon[best]) > 0 || (Double.compare(valueArrayIon[i], valueArrayIon[best]) == 0 && Float.compare(valueArrayAbundance[i], valueArrayAbundance[best]) > 0)) {
					best = i;
				}
			}
			return createIonAt(best);
		} else {
			return immutableZeroIon;
		}
	}

	@Override
	public IIon getLowestAbundance() {

		if(hasIons()) {
			int best = 0;
			for(int i = 1; i < numberOfIons; i++) {
				if(valueArrayAbundance[i] < valueArrayAbundance[best] || (Float.compare(valueArrayAbundance[i], valueArrayAbundance[best]) == 0 && Double.compare(valueArrayIon[i], valueArrayIon[best]) < 0)) {
					best = i;
				}
			}
			return createIonAt(best);
		} else {
			return immutableZeroIon;
		}
	}

	@Override
	public IIon getLowestIon() {

		if(hasIons()) {
			int best = 0;
			for(int i = 1; i < numberOfIons; i++) {
				if(Double.compare(valueArrayIon[i], valueArrayIon[best]) < 0 || (Double.compare(valueArrayIon[i], valueArrayIon[best]) == 0 && Float.compare(valueArrayAbundance[i], valueArrayAbundance[best]) < 0)) {
					best = i;
				}
			}
			return createIonAt(best);
		} else {
			return immutableZeroIon;
		}
	}

	@Override
	public IIonBounds getIonBounds() {

		if(hasIons()) {
			return new IonBounds(getLowestIon(), getHighestIon());
		} else {
			return null;
		}
	}

	@Override
	public int getNumberOfIons() {

		return numberOfIons;
	}

	@Override
	public boolean isEmpty() {

		return numberOfIons == 0;
	}

	@Override
	public IIon getIon(int ion) {

		if(hasIons()) {
			IExtractedIonSignal extractedIonSignal = new ExtractedIonSignal(ion, ion);
			for(int i = 0; i < numberOfIons; i++) {
				extractedIonSignal.setAbundance(createIonAt(i));
			}
			float abundance = extractedIonSignal.getAbundance(ion);
			if(abundance > 0) {
				return new Ion(ion, abundance);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	// TODO JUnit and optimize
	@Override
	public IIon getIon(double ion) {

		if(hasIons()) {
			int index = indexOfIon(ion);
			if(index >= 0) {
				return createIonAt(index);
			}
		}
		/*
		 * If there is no such ion.
		 */
		return new Ion(ion, 0.0f);
	}

	@Override
	public IIon getIon(double ion, int precision) {

		if(hasIons()) {
			double accurateRequestedIon = AbstractIon.getIon(ion, precision);
			for(int i = 0; i < numberOfIons; i++) {
				double accurateIon = AbstractIon.getIon(valueArrayIon[i], precision);
				if(Double.compare(accurateIon, accurateRequestedIon) == 0) {
					return new Ion(accurateIon, valueArrayAbundance[i]);
				}
			}
		}
		/*
		 * If there is no such ion.
		 */
		return new Ion(ion, 0.0f);
	}

	@Override
	public void adjustIons(float percentage) {

		/*
		 * Do nothing if out of range.
		 */
		if(percentage < -1.0f || percentage > 1.0f) {
			return;
		}
		/*
		 * If percentage == 0.0f, do nothing.
		 */
		if(percentage == 0.0f) {
			return;
		}
		for(int i = 0; i < numberOfIons; i++) {
			float abundance = valueArrayAbundance[i];
			abundance += abundance * percentage;
			valueArrayAbundance[i] = abundance;
		}
		setDirty(true);
	}

	@Override
	public void adjustTotalSignal(float totalSignal) {

		/*
		 * If the total signal is 0 there would be no ion stored in the list.<br/>
		 * That's not what we want.
		 */
		if(totalSignal <= 0.0f || Float.isNaN(totalSignal) || Float.isInfinite(totalSignal)) {
			return;
		}
		/*
		 * Do not cause a division by zero exception :-).
		 */
		float currentTotalSignal = getTotalSignal();
		if(currentTotalSignal == 0.0f) {
			return;
		}
		float base = 100.0f;
		float correctionFactor = ((base / currentTotalSignal) * totalSignal) / base;
		for(int i = 0; i < numberOfIons; i++) {
			float abundance = valueArrayAbundance[i] * correctionFactor;
			valueArrayAbundance[i] = abundance;
		}
		setDirty(true);
	}

	@Override
	public IScanMSD getMassSpectrum(IMarkedIons excludedIons) {

		IScanMSD massSpectrum;
		try {
			massSpectrum = makeDeepCopy();
			massSpectrum.removeIons(excludedIons);
		} catch(CloneNotSupportedException e) {
			massSpectrum = createNewMassSpectrum(excludedIons);
		}
		return massSpectrum;
	}

	/**
	 * Creates a new mass spectrum.
	 * 
	 * @param excludedIons
	 * @return IMassSpectrum
	 */
	private IScanMSD createNewMassSpectrum(IMarkedIons excludedIons) {

		if(excludedIons == null) {
			excludedIons = new MarkedIons(MarkedTraceModus.INCLUDE);
		}
		IScanMSD massSpectrum = new ScanMSD();
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		for(int i = 0; i < numberOfIons; i++) {
			int mz = (int)valueArrayIon[i];
			if(!excludedIonsNominal.contains(mz)) {
				massSpectrum.addIon(new Ion(valueArrayIon[i], valueArrayAbundance[i]));
			}
		}
		return massSpectrum;
	}

	@Override
	public boolean hasIons() {

		return numberOfIons > 0;
	}

	@Override
	public void enforceLoadScanProxy() {

		/*
		 * Normally, no action is required. The vendor proxy overrides this method by
		 * default.
		 */
	}

	@Override
	public boolean isNormalized() {

		return isNormalized;
	}

	@Override
	public float getNormalizationBase() {

		return normalizationBase;
	}

	@Override
	public IScanMSD normalize() {

		return normalize(NORMALIZATION_BASE);
	}

	@Override
	public IScanMSD normalize(float base) {

		/*
		 * Return if the base value is lower than zero.
		 */
		if(base <= 0) {
			return this;
		}
		/*
		 * Return if there are no ions stored.
		 */
		if(!hasIons()) {
			return this;
		}
		/*
		 * There is at least 1 ion stored in the list otherwise the code would not have
		 * reached this point.
		 */
		float highestAbundance = valueArrayAbundance[0];
		for(int i = 1; i < numberOfIons; i++) {
			if(valueArrayAbundance[i] > highestAbundance) {
				highestAbundance = valueArrayAbundance[i];
			}
		}
		/*
		 * Return if the highest abundance == 0.<br/> If yes a division through 0 would
		 * throw an ArithmeticException.
		 */
		double factor;
		if(highestAbundance == 0) {
			return this;
		} else {
			factor = base / highestAbundance;
			isNormalized = true;
			normalizationBase = base;
		}
		for(int i = 0; i < numberOfIons; i++) {
			float percentageAbundance = (float)(factor * valueArrayAbundance[i]);
			valueArrayAbundance[i] = percentageAbundance;
		}
		setDirty(true);
		return this;
	}

	@Override
	public void setOptimizedMassSpectrum(IScanMSD optimizedMassSpectrum) {

		this.optimizedMassSpectrum = optimizedMassSpectrum;
	}

	protected void setIons(Collection<? extends IIon> ions) {

		if(ions == null || ions.isEmpty()) {
			numberOfIons = 0;
			setDirty(true);
			return;
		}
		ensureCapacity(ions.size());
		numberOfIons = 0;
		for(IIon ion : ions) {
			if(ion != null) {
				valueArrayIon[numberOfIons] = ion.getIon();
				valueArrayAbundance[numberOfIons] = ion.getAbundance();
				numberOfIons++;
			}
		}
		setDirty(true);
	}

	@Override
	public IScanMSD getOptimizedMassSpectrum() {

		return optimizedMassSpectrum;
	}

	@Override
	public boolean isMeasurementSIM() {

		if(numberOfIons > 0 && numberOfIons <= LIMIT_SIM_MEASUREMENT) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isTandemMS() {

		int limit = (numberOfIons > 30) ? 30 : numberOfIons;
		for(int i = 0; i < limit; i++) {
			IIon ion = createIonAt(i);
			if(ion instanceof IIonMSn ionMSn && ionMSn.getIonTransition() != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isHighResolutionMS() {

		if(numberOfIons > 3000) {
			return true;
		} else {
			/*
			 * Detailed check.
			 */
			int counterNominal = 0;
			int counterHighRes = 0;
			int size = numberOfIons;
			int limit = 10;

			if(size <= limit) {
				/*
				 * Check all
				 */
				for(int i = 0; i < numberOfIons; i++) {
					String[] parts = Double.toString(valueArrayIon[i]).split("\\.");
					if(parts.length < 2 || parts[1].length() <= 1) {
						counterNominal++;
					} else {
						counterHighRes++;
					}
				}
			} else {
				int modulo = size / 10;
				for(int i = 0; i < numberOfIons; i++) {
					if(i % modulo == 0) {
						String[] parts = Double.toString(valueArrayIon[i]).split("\\.");
						if(parts.length < 2 || parts[1].length() <= 1) {
							counterNominal++;
						} else {
							counterHighRes++;
						}
					}
				}
			}

			return counterHighRes > counterNominal;
		}
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		AbstractScanMSD other = (AbstractScanMSD)otherObject;
		return getBasePeak() == other.getBasePeak() && getBasePeakAbundance() == other.getBasePeakAbundance() && getNumberOfIons() == other.getNumberOfIons() && getTotalSignal() == other.getTotalSignal() && isNormalized() == other.isNormalized() && getNormalizationBase() == other.getNormalizationBase();
	}

	@Override
	public int hashCode() {

		return 7 * Double.valueOf(getBasePeak()).hashCode() + 11 * Float.valueOf(getBasePeakAbundance()).hashCode() + 13 * Float.valueOf(getNumberOfIons()).hashCode() + 15 * Float.valueOf(getTotalSignal()).hashCode() + 13 * Boolean.valueOf(isNormalized).hashCode() + 11 * Float.valueOf(normalizationBase).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[basePeak=");
		builder.append(getBasePeak());
		builder.append(",basePeakAbundance=");
		builder.append(getBasePeakAbundance());
		builder.append(",numberOfIons=");
		builder.append(getNumberOfIons());
		builder.append(",totalSignal=");
		builder.append(getTotalSignal());
		builder.append(",isNormalized=");
		builder.append(isNormalized());
		if(isNormalized()) {
			builder.append(",normalizationBase=");
			builder.append(getNormalizationBase());
		}
		builder.append(",Ion/Abundance pairs: ");
		for(int i = 0; i < numberOfIons; i++) {
			builder.append(valueArrayIon[i]);
			builder.append(":");
			builder.append(valueArrayAbundance[i]);
			if(i + 1 < numberOfIons) {
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		VendorScan massSpectrum = (VendorScan)super.clone();
		massSpectrum.immutableZeroIon = new ImmutableZeroIon();
		massSpectrum.valueArrayIon = new double[Math.max(INITIAL_CAPACITY, numberOfIons)];
		massSpectrum.valueArrayAbundance = new float[Math.max(INITIAL_CAPACITY, numberOfIons)];
		System.arraycopy(valueArrayIon, 0, massSpectrum.valueArrayIon, 0, numberOfIons);
		System.arraycopy(valueArrayAbundance, 0, massSpectrum.valueArrayAbundance, 0, numberOfIons);
		massSpectrum.numberOfIons = numberOfIons;
		return massSpectrum;
	}

	/**
	 * Adds both intensities from firstIon to secondIon.
	 * 
	 * @param index
	 * @param secondIon
	 */
	private void addIntensities(int index, IIon secondIon) {

		valueArrayAbundance[index] = secondIon.getAbundance() + valueArrayAbundance[index];
		setDirty(true);
	}

	/**
	 * Adds the intensity of firstIon to secondIon.
	 * 
	 * @param index
	 * @param secondIon
	 */
	private void addHigherIntensity(int index, IIon secondIon) {

		valueArrayAbundance[index] = secondIon.getAbundance();
		setDirty(true);
	}

	private void removeAt(int index) {

		if(index < 0 || index >= numberOfIons) {
			return;
		}
		int elementsToMove = numberOfIons - index - 1;
		if(elementsToMove > 0) {
			System.arraycopy(valueArrayIon, index + 1, valueArrayIon, index, elementsToMove);
			System.arraycopy(valueArrayAbundance, index + 1, valueArrayAbundance, index, elementsToMove);
		}
		numberOfIons--;
		setDirty(true);
	}

	@Override
	public IVendorScan makeDeepCopy() throws CloneNotSupportedException {

		VendorScan massSpectrum = (VendorScan)super.clone();
		massSpectrum.immutableZeroIon = new ImmutableZeroIon();
		massSpectrum.valueArrayIon = new double[Math.max(INITIAL_CAPACITY, numberOfIons)];
		massSpectrum.valueArrayAbundance = new float[Math.max(INITIAL_CAPACITY, numberOfIons)];
		System.arraycopy(valueArrayIon, 0, massSpectrum.valueArrayIon, 0, numberOfIons);
		System.arraycopy(valueArrayAbundance, 0, massSpectrum.valueArrayAbundance, 0, numberOfIons);
		massSpectrum.numberOfIons = numberOfIons;
		return massSpectrum;
	}
}