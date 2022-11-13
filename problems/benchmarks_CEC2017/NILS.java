package etmo.problems.benchmarks_CEC2017;

import etmo.core.Problem;
import etmo.core.ProblemSet;
//import etmo.problems.base.*;
import etmo.problems.base.CEC2017.IO;
import etmo.problems.base.CEC2017.mmdtlz;
import etmo.problems.base.CEC2017.mmzdt;

import java.io.IOException;


public class NILS {


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
		mmdtlz prob = new mmdtlz(3, 25, 1, -50,50);
		prob.setGType("griewank");

		
		double shiftValues[] = IO.readShiftValuesFromFile("MData/CEC2017/S_NILS_1.txt");
		prob.setShiftValues(shiftValues);
				
		((Problem)prob).setName("NILS1");
		
		problemSet.add(prob);
		return problemSet;
	}

	public static ProblemSet getT2() throws IOException {
		ProblemSet problemSet = new ProblemSet(1);
		
		mmzdt prob = new mmzdt(50, 2,  -100,100);
		prob.setGType("ackley");
		prob.setHType("concave");
		
		((Problem)prob).setName("NILS2");

		problemSet.add(prob);
		return problemSet;
	}
}
