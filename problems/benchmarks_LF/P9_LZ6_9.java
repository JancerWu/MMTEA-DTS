package etmo.problems.benchmarks_LF;

import etmo.core.Problem;
import etmo.core.ProblemSet;

import java.io.IOException;

public class P9_LZ6_9 {

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
        LZ09 prob = new LZ09(31, 1, 32, 3, 10, -2.0, 2.0);
        prob.setHType("F6");
        ((Problem) prob).setName("lz6");
        problemSet.add(prob);
        return problemSet;
    }

    public static ProblemSet getT2() throws IOException {
        ProblemSet problemSet = new ProblemSet(1);
        LZ09 prob = new LZ09(22, 1, 22, 2, 30, -1.0, 1.0);
        prob.setHType("F9");
        ((Problem) prob).setName("lz9");
        problemSet.add(prob);
        return problemSet;
    }
}
