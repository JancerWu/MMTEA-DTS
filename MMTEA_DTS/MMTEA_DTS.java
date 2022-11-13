package search.MMTEA_DTS;

import etmo.core.*;
import etmo.operators.crossover.CrossoverFactory;
import etmo.util.Distance;
import etmo.util.JMException;
import etmo.util.PORanking;
import etmo.util.PseudoRandom;
import etmo.util.comparators.CrowdingComparator;
import jmetal.metaheuristics.moead.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MMTEA_DTS extends Algorithm {

    private int populationSize_;
    /**
     * Stores the population
     */
    private SolutionSet[] population_;
    /**
     * Z vector (ideal point)
     */
    double[][] z_;
    /**
     * Lambda vectors
     */
    // Vector<Vector<Double>> lambda_ ;
    double[][][] lambda_;
    /**
     * T: neighbour size
     */
    int T_;
    /**
     * Neighborhood
     */
    int[][][] neighborhood_;
    /**
     * delta: probability that parent solutions are selected from neighbourhood
     */
    double delta_;
    /**
     * nr: maximal number of solutions replaced by each child solution
     */
    int nr_;
    //    Solution[] indArray_;
    String functionType_;
    int evaluations_;

    int taskNum;
    /**
     * Operators
     */

    int rate;

    int generation;

    int neiborType;

    ArrayList[] savedValues;

    double rmp;

    int id;

    int size;

    Operator crossover_;
    Operator mutation_;
    Operator selection;

    String dataDirectory_;

    double lastMediumIR;
    List<Double> lastIR = new ArrayList<>();
    boolean zeroFlag;

//
//    String[] pf;
//    int cntIgd;


    /**
     * Constructor
     *
     * @param problemSet The problem to be solved
     */
    public MMTEA_DTS(ProblemSet problemSet) {
        super(problemSet);
        functionType_ = "_TCHE1";
    }

    @Override
    public SolutionSet execute() throws JMException, ClassNotFoundException {
        return null;
    }


    public SolutionSet[] execute2(double[][] testUtilityTimes, int times) throws JMException, ClassNotFoundException {

        taskNum = problemSet_.size();

//        cntIgd = 0;


        int maxEvaluations;


        rate = 1;

        evaluations_ = 0;
        maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
        populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
        dataDirectory_ = this.getInputParameter("dataDirectory").toString();
        T_ = ((Integer) this.getInputParameter("T")).intValue();
        nr_ = ((Integer) this.getInputParameter("nr")).intValue();
        delta_ = ((Double) this.getInputParameter("delta")).doubleValue();
        rmp = ((Double) this.getInputParameter("rmp")).doubleValue();
        size = ((Integer) this.getInputParameter("size")).intValue();


        crossover_ = operators_.get("crossover"); // default: DE crossover
        mutation_ = operators_.get("mutation"); // default: polynomial mutation
        selection = operators_.get("selection");

        //        多任务的个体
        population_ = new SolutionSet[taskNum];
        neighborhood_ = new int[taskNum][][];
        z_ = new double[taskNum][];
        lambda_ = new double[taskNum][][];
        generation = 0;
        savedValues = new ArrayList[taskNum];


        for (int i = 0; i < taskNum; i++) {
            population_[i] = new SolutionSet(populationSize_);
            neighborhood_[i] = new int[populationSize_][T_];
            z_[i] = new double[problemSet_.get(i).getNumberOfObjectives()];
            lambda_[i] = new double[populationSize_][problemSet_.get(i).getNumberOfObjectives()];
            savedValues[i] = new ArrayList<>(populationSize_);


            initUniformWeight(i);

            initNeighborhood(i);


            initPopulation(i);


            initIdealPoint(i);
        }
//        cntIgd++;


        do {

            // inter DRA test start
//            List<int[]> list = tourSelectionInterDRA(10, size);

            int start = 1;
            double rmpAdp;
            List<double[]> list;
            if (generation != 0 && !zeroFlag) start = size;
            list = selectBestSubproblems(start);
            rmpAdp = rmp;


            for (int i = 0; i < list.size(); i++) {
                double[] idv = list.get(i);

                int taskId = (int) idv[0];
                int n = (int) idv[1]; // or int n = i;
//                testUtilityTimes[taskId][times]++;
                double rnd = PseudoRandom.randDouble();

                // STEP 2.1. Mating selection based on probability
                if (rnd < delta_) // if (rnd < realb)
                {
//                        neiborType = 1; // neighborhood
                    neiborType = 1;
                } else {
                    neiborType = 2; // whole population
                }


                int doTask = chooseTack(taskId, rmpAdp);
                if (doTask == taskId) {
                    Vector<Integer> p = new Vector<Integer>();
                    matingSelection(p, n, 2, neiborType, taskId);
                    Solution child;
                    Solution[] parents = new Solution[3];
                    parents[0] = population_[taskId].get(p.get(0));
                    parents[1] = population_[taskId].get(p.get(1));
                    parents[2] = population_[taskId].get(n);
                    child = (Solution) crossover_.execute(new Object[]{population_[taskId].get(n), parents});
                    mutation_.execute(child);
                    problemSet_.get(taskId).evaluate(child);
                    evaluations_++;
                    updateReference(child, taskId);
                    updateProblem(child, n, neiborType, taskId, taskId);
                } else {
                    double rdm = new Random().nextDouble();
                    if (rdm < 0.5) {
                        HashMap parameters = new HashMap();
                        parameters.put("probability", 0.9);
                        parameters.put("distributionIndex", 20.0);

                        //
                        Solution[] parents = new Solution[2];
                        parents[0] = (Solution) selection.execute(population_[taskId]);
                        parents[1] = (Solution) selection.execute(population_[doTask]);

                        Solution[] offSpring;
                        Operator crossover;
                        crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);
                        offSpring = (Solution[]) crossover.execute(parents, problemSet_, 0);
                        Solution child = offSpring[0];
                        mutation_.execute(child);
                        problemSet_.get(doTask).evaluate(child);
                        evaluations_++;
                        updateReference(child, doTask);
                        updateProblem(child, n, neiborType, PseudoRandom.randInt(0, taskNum - 1), doTask);
                    } else {
                        Vector<Integer> p = new Vector<Integer>();
                        matingSelection(p, n, 2, neiborType, taskId);
                        Solution child;
                        //
                        Solution[] parents = new Solution[3];
                        parents[0] = population_[taskId].get(p.get(0));
                        parents[1] = population_[taskId].get(p.get(1));
                        parents[2] = population_[taskId].get(n);


                        child = (Solution) crossover_.execute(new Object[]{population_[taskId].get(n), parents});
                        mutation_.execute(child);
                        problemSet_.get(doTask).evaluate(child);
                        evaluations_++;
                        updateReference(child, doTask);
                        updateProblem(child, n, neiborType, taskId, doTask);
                    }

//

                }
            }
            //inter DRA test end


            generation++;
            if (rate != 0 && generation % rate == 0) {
                updateUtilityDRA();
            }

//            if (generation % 20 == 0) {
//                System.out.println("generation : " + generation);
//                int cnt = 0;
//                for (int i = 0; i < taskNum; i++) {
//                    for (int j = 0; j < populationSize_; j++) {
//                        if (population_[i].get(j).getUtility() == 1) cnt++;
//                        System.out.println(population_[i].get(j).getUtility());
//                    }
//                }
//                System.out.println("=============generation = " + generation + "==========================");
//                System.out.println("the number of best : " + cnt);
//                System.out.println("the minIR : " + lastMediumIR);
//                System.out.println("=======================================");
//            }


//            if (generation % 10 == 0 || generation == 2495) {
//                for (int igd = 0; igd < taskNum; igd++) {
//                    QualityIndicator indicator = new QualityIndicator(problemSet_, pf[igd]);
//                    testIgd[igd][cntIgd] += indicator.getIGD2(population_[igd], igd);
//                }
//                cntIgd++;
//            }

        } while (evaluations_ < maxEvaluations);


        //打印看一下utility的值的分布：

//        for (int i = 0; i < taskNum; i++) {
//            for (int j = 0; j < populationSize_; j++) {
//                testUtility[i][j] += population_[i].get(j).getUtility();
//            }
//        }


        return population_;
    }

    private boolean isOverRandom(int[] flag) {
        for (int i = 1; i < flag.length; i++) {
            if (flag[i] != 1) return false;
        }
        return true;
    }


    void assignFitness(SolutionSet pop, int taskId) {
        for (int i = 0; i < pop.size(); i++)
            pop.get(i).setLocation(Integer.MAX_VALUE);
        rankSolutionOnTask(pop, taskId);
    }

    void rankSolutionOnTask(SolutionSet pop, int taskId) {
        int start = problemSet_.get(taskId).getStartObjPos();
        int end = problemSet_.get(taskId).getEndObjPos();

        boolean selec[] = new boolean[problemSet_.getTotalNumberOfObjs()];

        for (int i = 0; i < selec.length; i++) {
            if (i < start || i > end)
                selec[i] = false;
            else
                selec[i] = true;
        }

        PORanking pr = new PORanking(pop, selec);
        int loc = 0;
        for (int i = 0; i < pr.getNumberOfSubfronts(); i++) {
            SolutionSet front = pr.getSubfront(i);
            Distance distance = new Distance();
            distance.crowdingDistanceAssignment(front, problemSet_.getTotalNumberOfObjs(), selec);
            front.sort(new CrowdingComparator());
            for (int j = 0; j < front.size(); j++) {
                if (loc < front.get(j).getLocation())
                    front.get(j).setLocation(loc);
                loc++;
            }
        }
    }

//    private void initParameter(int taskId) {
//        for (int i = 0; i < crossIR[taskId].length; i++) {
//            crossIR[taskId][i] = 1.0;
//        }
//    }

    private int chooseTack(int taskId, double testAdp) {
        double rd = PseudoRandom.randDouble();

        if (rd < testAdp) {
            neiborType = 2;
            int id = PseudoRandom.randInt(0, taskNum - 1);
            while (id == taskId) {
                id = PseudoRandom.randInt(0, taskNum - 1);
            }
            return id;
        }
        return taskId;
    }

    private void updateUtilityDRA() {

        if (lastIR.size() == 0) {
            zeroFlag = true;
            return;
        }

        zeroFlag = false;
        Collections.sort(lastIR);
        int irSize = lastIR.size();
        if (irSize % 2 == 1) {
            lastMediumIR = lastIR.get(irSize / 2);
        } else {
            lastMediumIR = (lastIR.get(irSize / 2 - 1) + lastIR.get(irSize / 2)) / 2.0;
        }
        lastIR.clear();


        for (int taskId = 0; taskId < taskNum; taskId++) {
            double f1, f2, uti, delta;
            for (int n = 0; n < populationSize_; n++) {
                f1 = fitnessFunction(population_[taskId].get(n), lambda_[taskId][n], taskId);
                f2 = fitnessFunction((Solution) savedValues[taskId].get(n), lambda_[taskId][n], taskId);
                delta = (f2 - f1) / f2;
                if (delta > lastMediumIR) {
                    population_[taskId].get(n).setUtility(1.0);
                } else if (delta >= 0) {
                    population_[taskId].get(n).setUtility(Math.max(population_[taskId].get(n).getUtility() + delta - lastMediumIR, 0.0));
                }
                savedValues[taskId].set(n, new Solution(population_[taskId].get(n)));

            }
        }
    }

    private void updateUtility1() {
        for (int taskId = 0; taskId < taskNum; taskId++) {
            double f1, f2, uti, delta;
            for (int n = 0; n < populationSize_; n++) {

                f1 = fitnessFunction(population_[taskId].get(n), lambda_[taskId][n], taskId);
                f2 = fitnessFunction((Solution) savedValues[taskId].get(n), lambda_[taskId][n], taskId);

                delta = (f2 - f1) / f2;
                int sourceId = population_[taskId].get(n).getSourceTask();
                int subId = population_[taskId].get(n).getSourceSubproblem();

//                if(f2 - f1 < 0){
//                    System.out.println("wrong!! the index is k = " + n);
//                }

                if (delta > 0.001) {
                    if (sourceId == taskId) {
                        population_[taskId].get(n).setUtility(1.0);
                    } else {
                        population_[taskId].get(subId).setTransUtility(1.0);
                    }
                } else {
                    if (sourceId == taskId) {
                        uti = (0.95 + (0.05 * delta / 0.001)) * ((double) population_[taskId].get(n).getUtility());
                        population_[taskId].get(n).setUtility(uti < 1.0 ? uti : 1.0);
                    } else {
                        uti = (0.95 + (0.05 * delta / 0.001)) * ((double) population_[sourceId].get(subId).getTransUtility());
                        population_[taskId].get(subId).setTransUtility(uti < 1.0 ? uti : 1.0);
                    }
                }
                savedValues[taskId].set(n, new Solution(population_[taskId].get(n)));

            }
        }
    }

    private void updateTransferUtility() {
        for (int taskId = 0; taskId < taskNum; taskId++) {
            double f1, f2, uti, delta;
            for (int n = 0; n < populationSize_; n++) {

                f1 = fitnessFunction(population_[taskId].get(n), lambda_[taskId][n], taskId);
                f2 = fitnessFunction((Solution) savedValues[taskId].get(n), lambda_[taskId][n], taskId);

                delta = (f2 - f1) / f2;
                if (delta > 0.001) {
                    population_[taskId].get(n).setUtility(1.0);
                } else {
//                uti = (0.95 + (0.05 * delta / 0.001)) * utility[n];
                    uti = (0.95 + (0.05 * delta / 0.001)) * ((double) population_[taskId].get(n).getUtility());
                    population_[taskId].get(n).setUtility(uti < 1.0 ? uti : 1.0);
                }

                savedValues[taskId].set(n, new Solution(population_[taskId].get(n)));
            }

        }
    }

    private void initUniformWeight(int taskId) {
        if ((problemSet_.get(taskId).getNumberOfObjectives() == 2) && (populationSize_ <= 300)) {
            for (int n = 0; n < populationSize_; n++) {
                double a = 1.0 * n / (populationSize_ - 1);
                lambda_[taskId][n][0] = a;
                lambda_[taskId][n][1] = 1 - a;
            } // for
        } // if
        else {
            String dataFileName;
            dataFileName = "W" + problemSet_.get(taskId).getNumberOfObjectives() + "D_" + populationSize_ + ".dat";

            try {
                // Open the file
                FileInputStream fis = new FileInputStream(dataDirectory_ + "/" + dataFileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);

                int numberOfObjectives = 0;
                int i = 0;
                int j = 0;
                String aux = br.readLine();
                while (aux != null) {
                    StringTokenizer st = new StringTokenizer(aux);
                    j = 0;
                    numberOfObjectives = st.countTokens();
                    while (st.hasMoreTokens()) {
                        double value = (new Double(st.nextToken())).doubleValue();
                        lambda_[taskId][i][j] = value;
                        // System.out.println("lambda["+i+","+j+"] = " + value)
                        // ;
                        j++;
                    }
                    aux = br.readLine();
                    i++;
                }
                br.close();
            } catch (Exception e) {
                System.out.println(
                        "initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName);
                e.printStackTrace();
            }
        } // else

    }

    private void initNeighborhood(int taskId) {
        double[] x = new double[populationSize_];
        int[] idx = new int[populationSize_];

        for (int i = 0; i < populationSize_; i++) {
            // calculate the distances based on weight vectors
            for (int j = 0; j < populationSize_; j++) {
                x[j] = Utils.distVector(lambda_[taskId][i], lambda_[taskId][j]);
                // x[j] = dist_vector(population[i].namda,population[j].namda);
                idx[j] = j;
                // System.out.println("x["+j+"]: "+x[j]+ ". idx["+j+"]:
                // "+idx[j]) ;
            } // for

            // find 'niche' nearest neighboring subproblems
            Utils.minFastSort(x, idx, populationSize_, T_);
            // minfastsort(x,idx,population.size(),niche);

            System.arraycopy(idx, 0, neighborhood_[taskId][i], 0, T_);
        } // for
    }

    private void initPopulation(int taskId) throws ClassNotFoundException, JMException {
        for (int i = 0; i < populationSize_; i++) {
            Solution newSolution = new Solution(problemSet_);
            newSolution.setUtility(1.0);
            newSolution.setTransUtility(1.0);
            newSolution.setSourceTask(taskId);
            problemSet_.get(taskId).evaluate(newSolution);
            evaluations_++;
            population_[taskId].add(newSolution);

            savedValues[taskId].add(new Solution(newSolution));

        } // for


    }

    private void initIdealPoint(int taskId) throws ClassNotFoundException, JMException {
        for (int i = 0; i < problemSet_.get(taskId).getNumberOfObjectives(); i++) {
//            z_[taskId][i] = 1.0e+30;
            z_[taskId][i] = 1.0e-30;
        } // for
        for (int i = 0; i < populationSize_; i++) {
            updateReference(population_[taskId].get(i), taskId);
        } // for
    }

    private void updateReference(Solution individual, int tasknum) {

        //移到当前任务目标值
        int turn = 0;
        for (int i = 0; i < tasknum; i++) {
            turn += problemSet_.get(i).getNumberOfObjectives();
        }


        for (int n = 0; n < problemSet_.get(tasknum).getNumberOfObjectives(); n++) {
            int curObjId = n + turn;
            if (individual.getObjective(curObjId) < z_[tasknum][n]) {
                z_[tasknum][n] = individual.getObjective(curObjId);
            }
        }

    }

    private void matingSelection(Vector<Integer> list, int cid, int size, int type, int taskId) {
        // list : the set of the indexes of selected mating parents
        // cid : the id of current subproblem
        // size : the number of selected mating parents
        // type : 1 - neighborhood; otherwise - whole population
        int ss;
        int r;
        int p;

        ss = neighborhood_[taskId][cid].length;
        while (list.size() < size) {
            if (type == 1) {
                r = PseudoRandom.randInt(0, ss - 1);
                p = neighborhood_[taskId][cid][r];
                // p = population[cid].table[r];
            } else {
                p = PseudoRandom.randInt(0, populationSize_ - 1);
            }
            boolean flag = true;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == p) // p is in the list
                {
                    flag = false;
                    break;
                }
            }

            // if (flag) list.push_back(p);
            if (flag) {
                list.addElement(p);
            }
        }

    }

    private void updateProblem(Solution indiv, int id, int type, int sourceId, int targetId) {
        // indiv: child solution
        // id: the id of current subproblem
        // type: update solutions in - neighborhood (1) or whole population
        // (otherwise)
        int size;
        int time;

        time = 0;

        //test
        if (type == 1) {
            size = neighborhood_[targetId][id].length;
        } else {
            size = populationSize_;
        }

//        test
//        size = populationSize_;

        int[] perm = new int[size];

        Utils.randomPermutation(perm, size);

        for (int i = 0; i < size; i++) {
            int k;
            if (type == 1) {
                k = neighborhood_[targetId][id][perm[i]];
            } else {
                k = perm[i]; // calculate the values of objective function
                // regarding the current subproblem
            }
            double f1, f2;


            f1 = fitnessFunction(population_[targetId].get(k), lambda_[targetId][k], targetId);
            f2 = fitnessFunction(indiv, lambda_[targetId][k], targetId);


            if (f2 < f1) {
                double delta = (f1 - f2) / f1;
                lastIR.add(delta);

                indiv.setSourceTask(sourceId);
                indiv.setSourceSubproblem(id);
                population_[targetId].replace(k, new Solution(indiv));
                time++;
            }

            if (time >= nr_) {
                return;
            }
        }

//        if(time < nr_){
//            System.out.println(nr_ - time);
//        }


    }

    private void updateProblemForCrossover(Solution indiv, int id, int type, int taskId) {

//        initParameter(taskId);

        int size;
        int time;
        time = 0;

        if (type == 1) {
            size = neighborhood_[taskId][id].length;
        } else {
            size = populationSize_;
        }
        int[] perm = new int[size];

        Utils.randomPermutation(perm, size);

        for (int i = 0; i < size; i++) {
            int k;
            if (type == 1) {
                k = neighborhood_[taskId][id][perm[i]];
            } else {
                k = perm[i]; // calculate the values of objective function
                // regarding the current subproblem
            }
            double f1, f2;

//            邻域内随机选择权重向量,我觉得会有重复
            f1 = fitnessFunction(population_[taskId].get(k), lambda_[taskId][k], taskId);
            f2 = fitnessFunction(indiv, lambda_[taskId][k], taskId);


            if (f2 < f1) {
                population_[taskId].replace(k, new Solution(indiv));
                time++;
//                crossIR[taskId][this.id] += (f1 - f2) / f1;
            }
            // the maximal number of solutions updated is not allowed to exceed
            // 'limit'
            if (time >= nr_) {
                return;
            }
        }


    }

    private void updateProblemGlobalIR(Solution indiv, int id, int type, int taskId) {
        // indiv: child solution
        // id: the id of current subproblem
        // type: update solutions in - neighborhood (1) or whole population
        // (otherwise)
        int size;
        int time;

        time = 0;

        if (type == 1) {
            size = neighborhood_[taskId][id].length;
        } else {
            size = populationSize_;
        }

        Map<Double, Integer> map = new TreeMap<>(new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
//                降序排序
                return o2.compareTo(o1);
            }
        });


        for (int i = 0; i < size; i++) {
            int k;
            if (type == 1) {
                k = neighborhood_[taskId][id][i];
            } else {
                k = i;
            }
            double f1, f2;
            f1 = fitnessFunction(population_[taskId].get(k), lambda_[taskId][k], taskId);
            f2 = fitnessFunction(indiv, lambda_[taskId][k], taskId);
            if (f2 < f1) {
                double IR = (f1 - f2) / f1;
                map.put(IR, k);
            }
        }

        Iterator iter = map.keySet().iterator();
        double dIR = 0.0;
        while (iter.hasNext()) {
            double ir = (double) iter.next();
            population_[taskId].replace(map.get(ir), new Solution(indiv));
            dIR += ir;
            time++;
            if (time >= nr_) {
//                crossIR[taskId][this.id] += dIR / 2;
                return;
            }
        }
    }

    private void updateProblemRandomIR(Solution indiv, int id, int type, int taskId) {
        // indiv: child solution
        // id: the id of current subproblem
        // type: update solutions in - neighborhood (1) or whole population
        // (otherwise)
        int size;
        int time;

        time = 0;

        if (type == 1) {
            size = neighborhood_[taskId][id].length;
        } else {
            size = populationSize_;
        }

        int[] perm = new int[size];
        Utils.randomPermutation(perm, size);

        double sumIr = 0.0;
        for (int i = 0; i < size; i++) {
            int k;
            if (type == 1) {
                k = neighborhood_[taskId][id][perm[i]];
            } else {
                k = perm[i]; // calculate the values of objective function
                // regarding the current subproblem
            }
            double f1, f2;

//            邻域内随机选择权重向量,我觉得会有重复
            f1 = fitnessFunction(population_[taskId].get(k), lambda_[taskId][k], taskId);
            f2 = fitnessFunction(indiv, lambda_[taskId][k], taskId);


            if (f2 < f1) {
                population_[taskId].replace(k, new Solution(indiv));
                time++;
                sumIr += (f1 - f2) / f1;
            }
            // the maximal number of solutions updated is not allowed to exceed
            // 'limit'
            if (time >= nr_) {
//                crossIR[taskId][this.id] += sumIr / 2;
                return;
            }
        }

    }


    private double fitnessFunction(Solution individual, double[] lambda, int taskId) {
        double fitness;
        fitness = 0.0;

        int turn = 0;
        for (int i = 0; i < taskId; i++) {
            turn += problemSet_.get(i).getNumberOfObjectives();
        }


        if (functionType_.equals("_TCHE1")) {
            double maxFun = -1.0e+30;

            //            eq2
//            double sum = 0;
//            for (double num : lambda) {
//                if (num == 0) sum += 1.0 / 1e-5;
//                else sum += 1.0 / num;
//            }

            for (int n = 0; n < problemSet_.get(taskId).getNumberOfObjectives(); n++) {

                int curObjId = n + turn;

                if (individual.getObjective(curObjId) - z_[taskId][n] < 0) {
                    System.out.println("wrong idea point!!!");
                }

                double diff = Math.abs(individual.getObjective(curObjId) - z_[taskId][n]);

                double feval;
//                             eq 1 start
                if (lambda[n] == 0) {
                    feval = 0.0001 * diff;
                } else {
                    feval = diff * lambda[n];
                }
//                eq1 end

//                eq2 start
//                if (lambda[n] == 0) {
//                    feval = diff / 1e-5;
//                } else {
//                    feval = diff / lambda[n];
//                }
//                feval = feval / sum;
//                eq2 end


                if (feval > maxFun) {
                    maxFun = feval;
                }
            } // for

            fitness = maxFun;
        } // if
        else {
            System.out.println("MOEAD.fitnessFunction: unknown type " + functionType_);
            System.exit(-1);
        }
        return fitness;


    }


    private List<double[]> selectBestSubproblems(int size) {
        List<double[]> selected = new ArrayList<>();
        int totalSize = populationSize_ * taskNum / size;
        PriorityQueue<double[]> maxHeap = new PriorityQueue<>((e1, e2) -> (int) (1e30 * (e2[2] - e1[2])));

        for (int i = 0; i < taskNum; i++) {
//            int[] permutation = new int[populationSize_];
//            Utils.randomPermutation(permutation, populationSize_);
            for (int j = 0; j < populationSize_; j++) {
                maxHeap.add(new double[]{i, j, population_[i].get(j).getUtility(), population_[i].get(j).getSourceTask()});
            }
        }
        while (totalSize-- > 0) {
            selected.add(maxHeap.poll());
        }

        return selected;
    }

    private List<int[]> tourSelectionInterDRA(int depth, int size) {
        List<int[]> selected = new ArrayList<>();
        List<Integer> candidate = new ArrayList<>();

        int totalSize = populationSize_ * taskNum;

        for (int n = 0; n < totalSize; n++) {
            candidate.add(n);
        }

        while (selected.size() < (int) (totalSize / size)) {

            int best_idd = (int) (PseudoRandom.randDouble() * candidate.size());
            int best_sub = candidate.get(best_idd) % populationSize_;
            int best_idd_taskId = best_idd / populationSize_;

            int i2;
            int s2;
            for (int i = 1; i < depth; i++) {
                i2 = (int) (PseudoRandom.randDouble() * candidate.size());
                s2 = candidate.get(i2) % populationSize_;
                int s2_taskId = i2 / populationSize_;


                if (population_[s2_taskId].get(s2).getUtility() >
                        population_[best_idd_taskId].get(best_sub).getUtility()) {
                    best_idd = i2;
                    best_sub = s2;
                    best_idd_taskId = s2_taskId;
                }
            }
            selected.add(new int[]{best_idd_taskId, best_sub});
            candidate.remove(best_idd);
        }
        return selected;
    }

    private List<Integer> tourSelection2(int depth) {
        List<Integer> selected = new ArrayList<Integer>();
        List<Integer> candidate = new ArrayList<Integer>();
//        id = PseudoRandom.randInt(0, 3);
        for (int n = 0; n < populationSize_ * taskNum; n++) {
            candidate.add(n);
        }

        while (selected.size() < (int) (populationSize_ * taskNum / 5.0)) {
            int best_idd = (int) (PseudoRandom.randDouble() * candidate.size());
            int i2;
            int best_sub = candidate.get(best_idd);
            int s2;
            for (int i = 1; i < depth; i++) {
                i2 = (int) (PseudoRandom.randDouble() * candidate.size());
                s2 = candidate.get(i2);
                if ((double) population_[s2 / populationSize_].get(s2 % populationSize_).getUtility() >
                        (double) population_[best_idd / populationSize_].get(best_idd % populationSize_).getUtility()) {
                    best_idd = i2;
                    best_sub = s2;
                }
            }
            selected.add(best_sub);
            candidate.remove(best_idd);
        }
        return selected;
    }

    private List<Integer> tourSelectionTransfer(int depth) {
        List<Integer> selected = new ArrayList<Integer>();
        List<Integer> candidate = new ArrayList<Integer>();
//        id = PseudoRandom.randInt(0, 3);
        for (int n = 0; n < populationSize_ * taskNum; n++) {
            candidate.add(n);
        }

        while (selected.size() < (int) (populationSize_ * taskNum / 5.0)) {
            int best_idd = (int) (PseudoRandom.randDouble() * candidate.size());
            int i2;
            int best_sub = candidate.get(best_idd);
            int s2;
            for (int i = 1; i < depth; i++) {
                i2 = (int) (PseudoRandom.randDouble() * candidate.size());
                s2 = candidate.get(i2);
                if ((double) population_[s2 / populationSize_].get(s2 % populationSize_).getTransUtility() >
                        (double) population_[best_idd / populationSize_].get(best_idd % populationSize_).getTransUtility()) {
                    best_idd = i2;
                    best_sub = s2;
                }
            }
            selected.add(best_sub);
            candidate.remove(best_idd);
        }
        return selected;
    }

    private List<Integer> tourSelectionWithinTask(int depth, int srcNum, int taskId) {
        List<Integer> selected = new ArrayList<Integer>();
        List<Integer> candidate = new ArrayList<Integer>();
        for (int n = 0; n < populationSize_; n++) {
            candidate.add(n);
        }

        while (selected.size() < srcNum) {
            int best_idd = (int) (PseudoRandom.randDouble() * candidate.size());
            int i2;
            int best_sub = candidate.get(best_idd);
            int s2;
            for (int i = 1; i < depth; i++) {
                i2 = (int) (PseudoRandom.randDouble() * candidate.size());
                s2 = candidate.get(i2);
                if ((double) population_[taskId].get(s2).getUtility() >
                        (double) population_[taskId].get(best_idd).getUtility()) {
                    best_idd = i2;
                    best_sub = s2;
                }
            }
            selected.add(best_sub);
            candidate.remove(best_idd);
        }
        return selected;
    }


}
