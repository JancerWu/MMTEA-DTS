package search.MMTEA_DTS;

import etmo.core.*;
import etmo.metaheuristics.utils.printIGD;
import etmo.operators.crossover.CrossoverFactory;
import etmo.operators.mutation.MutationFactory;
import etmo.operators.selection.SelectionFactory;
import etmo.problems.Benchmarks_RMTF.*;
import etmo.util.JMException;
import etmo.util.comparators.LocationComparator;

import java.io.IOException;
import java.util.HashMap;

public class MMTEA_DTS_main_real {
    public static void main(String[] args) throws IOException, JMException, ClassNotFoundException {
        ProblemSet problemSet;

        Algorithm algorithm; // The algorithm to use
        Operator crossover; // Crossover operator
        Operator mutation; // Mutation operator
        Operator selection;

        HashMap parameters; // Operator parameters

        double[] CR = new double[15];
        for (int i = 0; i <= 10; i++) {
            CR[i] = (double) i / 10.0;
        }
        int[] id = new int[10];
        for (int i = 0; i <= 8; i++) {
            id[i] = i;
        }

        for (int crI = 0; crI <= 0; crI++) {
            for (int size = 1; size <= 1; size++) {
                System.out.println("rmp = " + CR[crI] + " size = " + size);
                for (int pCase = 0; pCase < 6; pCase++) {
                    switch (pCase) {
                        case 0:
                            problemSet = RMTF1.getProblem();
                            break;
                        case 1:
                            problemSet = RMTF2.getProblem();
                            break;
                        case 2:
                            problemSet = RMTF3.getProblem();
                            break;
                        case 3:
                            problemSet = RMTF4.getProblem();
                            break;
                        case 4:
                            problemSet = RMTF5.getProblem();
                            break;
                        case 5:
                            problemSet = RMTF6.getProblem();
                            break;
                        case 6:
                            problemSet = RMTF7.getProblem();
                            break;
                        default:
                            problemSet = RMTF1.getProblem();
                            break;
                    }

                    int taskNumber = problemSet.size();

                    algorithm = new MMTEA_DTS(problemSet);

                    algorithm.setInputParameter("populationSize", taskNumber * 50);
                    algorithm.setInputParameter("maxEvaluations", taskNumber * 10000);

                    algorithm.setInputParameter("dataDirectory", "resources/weightVectorFiles/moead");

                    // MMTO-DM delta : 0.3  cr : 0.8   UPDATE : 1  rmp: 0.3
                    // MFEAD-DRA delta : 0.8  cr: 0.9 Update : 30  rmp: 0.1

                    algorithm.setInputParameter("T", 10);
                    algorithm.setInputParameter("delta", 0.3);
                    algorithm.setInputParameter("nr", 2);
                    algorithm.setInputParameter("rmp", CR[crI]);
                    algorithm.setInputParameter("size", size);


                    parameters = new HashMap();
                    parameters.put("CR", 0.8);
                    parameters.put("F", 0.5);
                    crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);

                    // Mutation operator
                    parameters = new HashMap();
                    parameters.put("probability", 1.0 / problemSet.get(0).getNumberOfVariables());
                    parameters.put("distributionIndex", 20.0);
                    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

                    parameters = new HashMap();
                    parameters.put("comparator", new LocationComparator());
                    selection = SelectionFactory.getSelectionOperator("BinaryTournament",
                            parameters);


                    algorithm.addOperator("crossover", crossover);
                    algorithm.addOperator("mutation", mutation);
                    algorithm.addOperator("selection", selection);




                    int times = 20;
                    double[][] hvRecord = new double[taskNumber][times];
                    double[] hvSum = new double[taskNumber];

                    double[][] testUtilityTimes = new double[taskNumber][times];
                    for (int t = 0; t < times; t++) {
                        SolutionSet[] resPopulation = ((MMTEA_DTS) algorithm).execute2(testUtilityTimes, t - 1);

                        SolutionSet[] res = new SolutionSet[taskNumber];
                        for (int i = 0; i < taskNumber; i++) {
                            res[i] = new SolutionSet();
                            for (int k = 0; k < resPopulation[i].size(); k++) {
                                Solution sol = resPopulation[i].get(k);
                                int start = problemSet.get(i).getStartObjPos();
                                int end = problemSet.get(i).getEndObjPos();
                                Solution newSolution = new Solution(end - start + 1);

                                for (int l = start; l <= end; l++)
                                    newSolution.setObjective(l - start, sol.getObjective(l));
                                newSolution.setDecisionVariables(sol.getDecisionVariables());
                                res[i].add(newSolution);
                            }
                        }

                        double[] reference = {1.0, 1.0};
                        for (int i = 0; i < taskNumber; i++) {
                            if (res[i].size() == 0) {
                                System.out.println("population wrong!!!");
                            }
                            hvRecord[i][t] = computeHV.getHV2D(res[i], reference);

//                            System.out.println("hv: " + hvRecord[i][t]);
//                            System.out.println("test samples fail rate:");
//                            for (int j = 0; j < res[i].size(); j++) {
//                                System.out.println(((Training_DNN)(problemSet.get(i))).evaluateTestSamples(res[i].get(j)));
//                            }

                            hvSum[i] += hvRecord[i][t];

//                            res[i].printObjectivesToFile("MMTO-DTS_"+problemSet.get(i).getNumberOfObjectives()+"Obj_"+
//                                    problemSet.get(i).getName()+ "_" + problemSet.get(i).getNumberOfVariables() + "D_run"+t+".txt");
                        }


                    }

                    for (int i = 0; i < taskNumber; i++) {
                        System.out.println(hvSum[i] / (double)times);
                    }
                    String path = "MMTO-DTS_rmp = " + CR[crI] + " size = " + size + "_" + "RMTF" + ".txt";
                    printIGD.printIGDtoText(path, hvRecord, taskNumber, times);

                }

            }


        }


    }
}
