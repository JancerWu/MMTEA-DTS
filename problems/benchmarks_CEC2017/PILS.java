package etmo.problems.benchmarks_CEC2017;

import etmo.core.Problem;
import etmo.core.ProblemSet;
//import etmo.problems.base.*;
import etmo.problems.base.CEC2017.IO;
import etmo.problems.base.CEC2017.mmdtlz;

import java.io.IOException;


public class PILS {
	public static ProblemSet getProblem() throws IOException {
		ProblemSet ps1 = getT1();
		ProblemSet ps2 = getT2();
		ProblemSet problemSet = new ProblemSet(2);

		problemSet.add(ps1.get(0));
		problemSet.add(ps2.get(0));
		return problemSet;

	}

	public static ProblemSet getT1() throws IOException {
		ProblemSet problemSet = new ProblemSet(1);

		mmdtlz prob = new mmdtlz(2, 50, 1, -50,50);
		prob.setGType("griewank");

		((Problem)prob).setName("PILS1");
		
		problemSet.add(prob);
		return problemSet;
	}

	public static ProblemSet getT2() throws IOException {
		ProblemSet problemSet = new ProblemSet(1);

		mmdtlz prob = new mmdtlz(2, 50, 1, -100,100);
		prob.setGType("ackley");

		double[] shiftValues= IO.readShiftValuesFromFile("MData/CEC2017/S_PILS_2.txt");
		prob.setShiftValues(shiftValues);
		
		((Problem)prob).setName("PILS2");
		
		problemSet.add(prob);
		return problemSet;
	}
	
}
