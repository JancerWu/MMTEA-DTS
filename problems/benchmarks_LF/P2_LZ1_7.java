package etmo.problems.benchmarks_LF;

import etmo.core.Problem;
import etmo.core.ProblemSet;

import java.io.IOException;

public class P2_LZ1_7 {

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
        LZ09 prob = new LZ09(21, 3, 21, 2, 10, 0.0, 1.0);
        prob.setHType("F7");
        ((Problem) prob).setName("lz7");
        problemSet.add(prob);
        return problemSet;
    }
}
