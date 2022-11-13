package search.MMTEA_DTS;

import etmo.core.Solution;
import etmo.core.SolutionSet;
import etmo.util.Ranking;

import java.util.*;

public class computeHV {
    public static double EPS = (int) (1e-9);

    public static double getHV2D(double[][] solutions, double[] reference) {
        double hv;
        hv = Math.abs((solutions[0][0] - reference[0]) * (solutions[0][1] - reference[1]));
        for (int i = 1; i < solutions.length; i++) {
            hv += Math.abs((solutions[i][0] - reference[0])
                    * (solutions[i][1] - solutions[i - 1][1]));
        }
        return hv;
    }

    public static double getHV2D(SolutionSet solutions, double[] reference) {
        double hv;
        // 去重
        for (int i = 0; i < solutions.size(); i++) {
            for (int j = i + 1; j < solutions.size(); j++) {
                double preObj0 = solutions.get(i).getObjective(0);
                double preObj1 = solutions.get(i).getObjective(1);
                double curObj0 = solutions.get(j).getObjective(0);
                double curObj1 = solutions.get(j).getObjective(1);
                if (Math.abs(preObj0 - curObj0) <= EPS && Math.abs(preObj1 - curObj1) <= EPS) {
                    solutions.get(j).setObjective(0, 1.0);
                    solutions.get(j).setObjective(1, 1.0);
                }
            }
        }
        // 非支配排序
        Ranking ranking = new Ranking(solutions);
        solutions = ranking.getSubfront(0);
        // 按照f1从小到大排序
        List<Solution> list = new ArrayList<>();
        for (int i = 0; i < solutions.size(); i++) {
            list.add(solutions.get(i));
        }
        Collections.sort(list, (a, b) -> a.getObjective(0) - b.getObjective(0) > EPS ? 1 : -1);
        hv = Math.abs((list.get(0).getObjective(0) - reference[0]) * (list.get(0).getObjective(1) - reference[1]));
        for (int i = 1; i < solutions.size(); i++) {
            hv += Math.abs((list.get(i).getObjective(0) - reference[0])
                    * (list.get(i).getObjective(1) - list.get(i-1).getObjective(1)));
        }
        return hv;
    }


    public static void main(String[] args) {


    }
}
