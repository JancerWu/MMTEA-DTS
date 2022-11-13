package etmo.problems.base.CEC2019;

public class GFunctions {

	protected static double[] shiftValues_;
	protected static double[][] rotationMatrix_;	

	/*F4: Shifted and Rotated Rosenbrock’s Function*/	
	public static double getF4(double x[], double[] shiftValues, double[][] rotationMatrix) {
		double Fstar=0.0;
		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;
		
		shiftVariables(x);
		shrinkVariables(x,2.048/100);
		x=rotateVariables(x);		
		
//		for (int i = 0; i < x.length; i++)
//			x[i] += 1;		
		return BaseFunctions.getRosenbrock(x)+Fstar;
	}	

	/*F8: Shifted Rastrigin’s Function*/	
	public static double getF8(double x[], double[] shiftValues) {
		double Fstar=0.0;
		shiftValues_ = shiftValues;
		
		shiftVariables(x);
		shrinkVariables(x,5.12/100);
		
		return BaseFunctions.getRastrigin(x)+Fstar;
	}		
		
	/*F9: Shifted and Rotated Rastrigin’s Function*/	
	public static double getF9(double x[], double[] shiftValues, double[][] rotationMatrix) {
		double Fstar=0.0;
		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;
		
		shiftVariables(x);
		shrinkVariables(x,5.12/100);
		x=rotateVariables(x);
				
		return BaseFunctions.getRastrigin(x)+Fstar;
	}		
	
	/*F11: Shifted and Rotated Schwefel’s Function	*/	
	public static double getF11(double x[], double[] shiftValues, double[][] rotationMatrix) {
		double Fstar=0.0;
		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;
		
		shiftVariables(x);
		shrinkVariables(x,1000.0/100);
		x=rotateVariables(x);
		
		return BaseFunctions.getMSchwefel(x)+Fstar;
	}		
	
	/*F15: Shifted and Rotated Expanded Griewank’s plus Rosenbrock’s Function*/
	public static double getF15(double x[], double[] shiftValues, double[][] rotationMatrix) {
		double Fstar=0.0;
		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;
		
		shiftVariables(x);
		shrinkVariables(x,5.0/100);
		x=rotateVariables(x);
		
//		for (int i = 0; i < x.length; i++)
//			x[i] += 1;		
		return BaseFunctions.getExGriewRosen(x)+Fstar;
	}			
	
	/*F17: Hybrid Function 1*/
	public static double getF17(double x[], double[] shiftValues, double[][] rotationMatrix) {
		
		double Fstar=0.0;
		int dim = x.length;
		
		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;
		shiftVariables(x);
		x=rotateVariables(x);	
		
		double[] p = {0.3,0.3,0.4};
		int n1 = (int)Math.ceil(p[0]*dim);			
		int n2 = (int)Math.ceil(p[1]*dim);
		int n3 = dim-n1-n2;
		
		double[] xI = new double[n1];
		double[] xII = new double[n2];
		double[] xIII = new double[n3];
		
		for (int i = 0; i < n1; i++)
			xI[i]=x[i];
		for (int i = n1; i < n1+n2; i++)
			xII[i-n1]=x[i];		
		for (int i = n1+n2; i < dim; i++)
			xIII[i-n1-n2]=x[i];		

		shrinkVariables(xI,1000.0/100);
		shrinkVariables(xII,5.12/100);
//		xI=rotateVariables(xI);
//		xII=rotateVariables(xII);
//		xIII=rotateVariables(xIII);		
		
		return BaseFunctions.getMSchwefel(xI)+BaseFunctions.getRastrigin(xII)
				+BaseFunctions.getElliptic(xIII)+Fstar;
	}	

	/*F18: Hybrid Function 2*/
	public static double getF18(double x[], double[] shiftValues, double[][] rotationMatrix) {
		
		double Fstar=0.0;
		int dim = x.length;
		
		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;
		shiftVariables(x);
		x=rotateVariables(x);		
		
		double[] p = {0.3,0.3,0.4};
		int n1 = (int)Math.ceil(p[0]*dim);			
		int n2 = (int)Math.ceil(p[1]*dim);
		int n3 = dim-n1-n2;
		
		double[] xI = new double[n1];
		double[] xII = new double[n2];
		double[] xIII = new double[n3];
		
		for (int i = 0; i < n1; i++)
			xI[i]=x[i];
		for (int i = n1; i < n1+n2; i++)
			xII[i-n1]=x[i];		
		for (int i = n1+n2; i < dim; i++)
			xIII[i-n1-n2]=x[i];		
		
		shrinkVariables(xII,5.0/100);
		shrinkVariables(xIII,5.12/100);
//		xI=rotateVariables(xI);
//		xII=rotateVariables(xII);
//		xIII=rotateVariables(xIII);	
		return BaseFunctions.getCigar(xI)+BaseFunctions.getHGBat(xII)
				+BaseFunctions.getRastrigin(xIII)+Fstar;
	}	
	
	/*F19: Hybrid Function 3*/
	public static double getF19(double x[], double[] shiftValues, double[][] rotationMatrix) {
		
		double Fstar=0.0;
		int dim = x.length;

		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;
		shiftVariables(x);
		x=rotateVariables(x);
		
		double[] p = {0.2, 0.2, 0.3, 0.3};
		int n1 = (int)Math.ceil(p[0]*dim);			
		int n2 = (int)Math.ceil(p[1]*dim);
		int n3 = (int)Math.ceil(p[2]*dim);
		int n4 = dim-n1-n2-n3;
		
		double[] xI = new double[n1];
		double[] xII = new double[n2];
		double[] xIII = new double[n3];
		double[] xIV = new double[n4];
		
		for (int i = 0; i < n1; i++)
			xI[i]=x[i];
		for (int i = n1; i < n1+n2; i++)
			xII[i-n1]=x[i];		
		for (int i = n1+n2; i < n1+n2+n3; i++)			
			xIII[i-n1-n2]=x[i];		
		for (int i = n1+n2+n3; i < dim; i++)
			xIV[i-n1-n2-n3]=x[i];		

		shrinkVariables(xI,600.0/100);
		shrinkVariables(xII,0.5/100);
		shrinkVariables(xIII,2.048/100);
//		xI=rotateVariables(xI);
//		xII=rotateVariables(xII);
//		xIII=rotateVariables(xIII);			
//		xIV=rotateVariables(xIV);		
		
		
		return BaseFunctions.getGriewank(xI)+BaseFunctions.getWeierstrass(xII)
				+BaseFunctions.getRosenbrock(xIII)+BaseFunctions.getScafferF6(xIV)+Fstar;
	}	

	
	/*F20: Hybrid Function 4*/
	public static double getF20(double x[], double[] shiftValues, double[][] rotationMatrix) {
		
		double Fstar=0.0;
		int dim = x.length;

		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;
		shiftVariables(x);
		x=rotateVariables(x);
		
		
		double[] p = {0.2, 0.2, 0.3, 0.3};
		int n1 = (int)Math.ceil(p[0]*dim);			
		int n2 = (int)Math.ceil(p[1]*dim);
		int n3 = (int)Math.ceil(p[2]*dim);
		int n4 = dim-n1-n2-n3;
		
		double[] xI = new double[n1];
		double[] xII = new double[n2];
		double[] xIII = new double[n3];
		double[] xIV = new double[n4];
		
		for (int i = 0; i < n1; i++)
			xI[i]=x[i];
		for (int i = n1; i < n1+n2; i++)
			xII[i-n1]=x[i];		
		for (int i = n1+n2; i < n1+n2+n3; i++)			
			xIII[i-n1-n2]=x[i];		
		for (int i = n1+n2+n3; i < dim; i++)
			xIV[i-n1-n2-n3]=x[i];	
		
		shrinkVariables(xI,5.0/100);		
		shrinkVariables(xIII,5.0/100);
		shrinkVariables(xIV,5.12/100);
//		xI=rotateVariables(xI);
//		xII=rotateVariables(xII);
//		xIII=rotateVariables(xIII);			
//		xIV=rotateVariables(xIV);	
		return BaseFunctions.getHGBat(xI)+BaseFunctions.getDiscus(xII)
				+BaseFunctions.getExGriewRosen(xIII)+BaseFunctions.getRastrigin(xIV)+Fstar;
	}		
	
	/*F22: Hybrid Function 6*/
	public static double getF22(double x[], double[] shiftValues, double[][] rotationMatrix) {
		
		double Fstar=0.0;
		int dim = x.length;

		shiftValues_ = shiftValues;
		rotationMatrix_ = rotationMatrix;
		shiftVariables(x);
		x=rotateVariables(x);
		
		double[] p = {0.1, 0.2, 0.2, 0.2, 0.3};
		int n1 = (int)Math.ceil(p[0]*dim);			
		int n2 = (int)Math.ceil(p[1]*dim);
		int n3 = (int)Math.ceil(p[2]*dim);
		int n4 = (int)Math.ceil(p[3]*dim);
		int n5 = dim-n1-n2-n3-n4;
		
		double[] xI = new double[n1];
		double[] xII = new double[n2];
		double[] xIII = new double[n3];
		double[] xIV = new double[n4];
		double[] xV = new double[n5];
		
		for (int i = 0; i < n1; i++)
			xI[i]=x[i];
		for (int i = n1; i < n1+n2; i++)
			xII[i-n1]=x[i];		
		for (int i = n1+n2; i < n1+n2+n3; i++)			
			xIII[i-n1-n2]=x[i];		
		for (int i = n1+n2+n3; i < n1+n2+n3+n4; i++)
			xIV[i-n1-n2-n3]=x[i];			
		for (int i = n1+n2+n3+n4; i < dim; i++)
			xV[i-n1-n2-n3-n4]=x[i];	
		
		shrinkVariables(xI,5.0/100);
		shrinkVariables(xII,5.0/100);
		shrinkVariables(xIII,5.0/100);
		shrinkVariables(xIV,1000.0/100);
//		xI=rotateVariables(xI);
//		xII=rotateVariables(xII);
//		xIII=rotateVariables(xIII);			
//		xIV=rotateVariables(xIV);	
//		xV=rotateVariables(xV);
		
		return BaseFunctions.getKatsuura(xI)+BaseFunctions.getHappyCat(xII)
				+BaseFunctions.getExGriewRosen(xIII)+BaseFunctions.getMSchwefel(xIV)
				+BaseFunctions.getAckley(xV)+Fstar;
	}	
	
	
	protected static void shiftVariables(double x[]) {
		for (int i = 0; i < x.length; i++) {
			x[i] -= shiftValues_[i];
		}
	}

	protected static void shrinkVariables(double x[], double mul) {
		for (int i = 0; i < x.length; i++) 
			x[i] *= mul;
	}	
	
	protected static double[] rotateVariables(double x[]) {
		int len = x.length;
		double res[] = new double[len];

		for (int i = 0; i < len; i++) {
			double[] y = rotationMatrix_[i];

			double sum = 0;
			for (int j = 0; j < len; j++)
				sum += x[j] * y[j];
			res[i] = sum;
//			res[i] = x[i];
		}

		return res;
	}	
	
}
