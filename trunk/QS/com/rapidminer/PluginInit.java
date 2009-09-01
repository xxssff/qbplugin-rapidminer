package com.rapidminer;

import com.rapidminer.tools.math.similarity.DistanceMeasures;
import qbts.distances.*;


public class PluginInit {
  public static void initPlugin(){
		DistanceMeasures.registerMeasure(DistanceMeasures.NOMINAL_MEASURES_TYPE, "QSISimilarity", QSISimilarity.class);	
		DistanceMeasures.registerMeasure(DistanceMeasures.NOMINAL_MEASURES_TYPE, "IntervalKernel", IntervalKernel.class);
		DistanceMeasures.registerMeasure(DistanceMeasures.NOMINAL_MEASURES_TYPE, "ISone", ISone.class);
		DistanceMeasures.registerMeasure(DistanceMeasures.NOMINAL_MEASURES_TYPE, "ISone_2", ISone_2.class);
		DistanceMeasures.registerMeasure(DistanceMeasures.NOMINAL_MEASURES_TYPE, "IS-2", IS2.class);
		DistanceMeasures.registerMeasure(DistanceMeasures.NOMINAL_MEASURES_TYPE, "IS-2.2", IS2_2.class);
  }
}
