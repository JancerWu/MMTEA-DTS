package etmo.problems.base.CEC2019;

import etmo.core.Problem;
import etmo.core.Solution;
import etmo.util.JMException;

public class MMDTLZ extends Problem {
	String gType_;
	Integer alpha_;

	
	public MMDTLZ(int numberOfObjectives, int numberOfVariables, int alpha, double lg, double ug) {
		numberOfObjectives_ = numberOfObjectives;
		numberOfVariables_ = numberOfVariables;

		gType_ = "sphere";

		alpha_ = alpha;

		int num = numberOfVariables_ - numberOfObjectives_ + 1;

		// System.out.println(num);

		shiftValues_ = new double[num];
		rotationMatrix_ = new double[num][num];

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		for (int var = 0; var < numberOfObjectives_ - 1; var++) {
			lowerLimit_[var] = 0.0;
			upperLimit_[var] = 1.0;
		} // for

		for (int var = numberOfObjectives_ - 1; var < numberOfVariables; var++) {
			lowerLimit_[var] = lg;
			upperLimit_[var] = ug;
		}

		for (int i = 0; i < num; i++)
			shiftValues_[i] = 0;

		for (int i = 0; i < num; i++) {
			for (int j = 0; j < num; j++) {
				if (i != j)
					rotationMatrix_[i][j] = 0;
				else
					rotationMatrix_[i][j] = 1;
			}
		}

		if (numberOfObjectives == 2)
			hType_ = "circle";
		else
			hType_ = "sphere";
	}

	public MMDTLZ(int numberOfObjectives, int numberOfVariables, int alpha, double lg, double ug, String gType,
			double[] shiftValues, double[][] rotationMatrix) {
		numberOfObjectives_ = numberOfObjectives;
		numberOfVariables_ = numberOfVariables;

		alpha_ = alpha;
		gType_ = gType;
		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		for (int var = 0; var < numberOfObjectives_ - 1; var++) {
			lowerLimit_[var] = 0.0;
			upperLimit_[var] = 1.0;
		} // for

		for (int var = numberOfObjectives_ - 1; var < numberOfVariables; var++) {
			lowerLimit_[var] = lg;
			upperLimit_[var] = ug;
		}
		if (numberOfObjectives == 2)
			hType_ = "circle";
		else
			hType_ = "sphere";
	}

	public void evaluate(Solution solution) throws JMException {
		double vars[] = scaleVariables(solution);

		double[] xI = new double[numberOfObjectives_ - 1];
		double[] xII = new double[numberOfVariables_ - numberOfObjectives_ + 1];

		for (int i = 0; i < numberOfObjectives_ - 1; i++)
			xI[i] = vars[i];

		for (int i = numberOfObjectives_ - 1; i < numberOfVariables_; i++)
			xII[i - numberOfObjectives_ + 1] = vars[i];
//		xII = transformVariables(xII);

		double[] f = new double[numberOfObjectives_];

		double g = evalG(xII);

		for (int i = 0; i < numberOfObjectives_; i++)
			f[i] = 1 + g;

		solution.setGFunValue(1 + g);

		for (int i = 0; i < numberOfObjectives_; i++) {
			for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)
				f[i] *= Math.cos(Math.pow(xI[j], alpha_) * 0.5 * Math.PI);
			if (i != 0) {
				int aux = numberOfObjectives_ - (i + 1);
				f[i] *= Math.sin(Math.pow(xI[aux], alpha_) * 0.5 * Math.PI);
			} // if
		} // for

		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(startObjPos_ + i, f[i]);
	}

	@Override
	public void dynamicEvaluate(Solution solution, int currentGeneration) throws JMException {

	}

	double evalG(double[] xII) throws JMException {
		if (gType_.equalsIgnoreCase("F4"))
			return GFunctions.getF4(xII, shiftValues_, rotationMatrix_);
		else if (gType_.equalsIgnoreCase("F8"))
			return GFunctions.getF8(xII, shiftValues_);
		else if (gType_.equalsIgnoreCase("F9"))
			return GFunctions.getF9(xII, shiftValues_, rotationMatrix_);
		else if (gType_.equalsIgnoreCase("F11"))
			return GFunctions.getF11(xII, shiftValues_, rotationMatrix_);
		else if (gType_.equalsIgnoreCase("F15"))
			return GFunctions.getF15(xII, shiftValues_, rotationMatrix_);
		else if (gType_.equalsIgnoreCase("F17"))
			return GFunctions.getF17(xII, shiftValues_, rotationMatrix_);
		else if (gType_.equalsIgnoreCase("F18"))
			return GFunctions.getF18(xII, shiftValues_, rotationMatrix_);
		else if (gType_.equalsIgnoreCase("F19"))
			return GFunctions.getF19(xII, shiftValues_, rotationMatrix_);
		else if (gType_.equalsIgnoreCase("F20"))
			return GFunctions.getF20(xII, shiftValues_, rotationMatrix_);
		else if (gType_.equalsIgnoreCase("F22"))
			return GFunctions.getF22(xII, shiftValues_, rotationMatrix_);
		else {
			System.out.println("Error: g function type " + gType_ + " invalid");
			return Double.NaN;
		}
	}

	public void setGType(String gType) {
		gType_ = gType;
	}

	public String getHType() {
		return hType_;
	}

}
