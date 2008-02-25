package qbts.distances;


/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2007 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as 
 *  published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version. 
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA.
 */

import java.util.List;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.InputDescription;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.similarity.SimilarityMeasure;
import com.rapidminer.operator.similarity.SimilarityUtil;
import com.rapidminer.parameter.ParameterType;


/**
 * This class represents an operator that creates a similarity measure based on an ExampleSet.
 * 
 * @author Michael Wurst, Ingo Mierswa
 * @version $Id: ExampleSet2Similarity.java,v 1.2 2007/08/11 02:27:18 ingomierswa Exp $
 */
public class QBDistances  extends Operator {

	String PARAMETER_MEASURE = "measure";
	String[] DEFAULT_SIM_MEASURES = {
			"qbts.distances.QSISimilarity"	
			};
	
	public QBDistances(OperatorDescription description) {
		super(description);
	}

	public IOObject[] apply() throws OperatorException {
		ExampleSet es = (ExampleSet) getInput(ExampleSet.class).clone();		
		return new IOObject[] {
			SimilarityUtil.resolveSimilarityMeasure(getParameters(), null, es)
		};
	}

	public static SimilarityMeasure resolveSimilarityMeasure(Parameters parameters, IOContainer ioContainer, ExampleSet es) throws OperatorException,
	MissingIOObjectException, UndefinedParameterError, UserError {
		Tools.checkAndCreateIds(es);
		String simClassName = (String) parameters.getParameter(PARAMETER_MEASURE);
		if ((ioContainer != null) && ioContainer.contains(SimilarityMeasure.class)) {
			if (simClassName != null)
				es.getLog().log("External similarity measure found. This measure is used instead of the one specified in the parameter \"measure\"");
			return ioContainer.get(SimilarityMeasure.class);
		} else if (simClassName != null) {
			ExampleBasedSimilarityMeasure sim = (ExampleBasedSimilarityMeasure) SIM_CLASSES_MAP.getInstantiation(simClassName);
			sim.init(es);
			return sim;
		} else {
			return null;
		}
	}

	public Class[] getInputClasses() {
		return new Class[0];
	}

	
	public Class[] getOutputClasses() {
		return new Class[] {
			SimilarityMeasure.class
		};
	}

	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add(SimilarityUtil.generateSimilarityParameter());
		return types;
	}
}
