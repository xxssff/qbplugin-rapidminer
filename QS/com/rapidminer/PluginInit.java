package com.rapidminer;

import com.rapidminer.tools.math.similarity.DistanceMeasures;
import qbts.distances.*;

public class PluginInit {
  public static void initPlugin(){
		DistanceMeasures.registerMeasure(DistanceMeasures.NOMINAL_MEASURES_TYPE, "QSISimilarity", QSISimilarity.class);	
  }
}
