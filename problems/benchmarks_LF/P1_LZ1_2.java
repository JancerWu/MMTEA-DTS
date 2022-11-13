package etmo.problems.benchmarks_LF;

import etmo.core.Problem;
import etmo.core.ProblemSet;
import etmo.problems.base.CEC2017.mmdtlz;

import java.io.IOException;

public class P1_LZ1_2 {
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
        LZ09 prob = new LZ09(21, 1, 21, 2, 10, 0.0, 1.0);
        prob.setHType("F1");
        ((Problem) prob).setName("lz1");
        problemSet.add(prob);
        return problemSet;
    }

    public static ProblemSet getT2() throws IOException {
        ProblemSet problemSet = new ProblemSet(1);
        LZ09 prob = new LZ09(21, 1, 22, 2, 10, -1.0, 1.0);
        prob.setHType("F2");
        ((Problem) prob).setName("lz2");
        problemSet.add(prob);
        return problemSet;
    }

}
