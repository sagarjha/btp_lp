import java.io.*;
import com.joptimizer.functions.*;
import com.joptimizer.optimizers.*;
import com.joptimizer.algebra.*;
import com.joptimizer.solvers.*;
import com.joptimizer.util.*;

public class Sample {

    public static void main (String[] args) throws Exception{
	// Objective function
	double[][] P = new double[][] {{0.0025,0,0,0,0,0}, {0,0.01,0,0,0,0}, {0,0,0.0025,0,0,0}, {0,0,0,0.01,0,0}, {0,0,0,0,0.0025,0}, {0,0,0,0,0,0.01}};
	PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);

	//equalities
	double[][] A = new double[][]{{1,1,0,0,0,0}, {0,0,1,1,0,0}, {0,0,0,0,1,1}};
	double[] b = new double[]{1,1,1};

	//inequalities
	ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[8];
	inequalities[0] = new LinearMultivariateRealFunction(new double[]{1,0,0,0,0,0}, 1);
	inequalities[1] = new LinearMultivariateRealFunction(new double[]{0,1,0,0,0,0}, 1);
	inequalities[2] = new LinearMultivariateRealFunction(new double[]{0,0,1,0,0,0}, 1);
	inequalities[3] = new LinearMultivariateRealFunction(new double[]{0,0,0,1,0,0}, 1);
	inequalities[4] = new LinearMultivariateRealFunction(new double[]{0,1,0,0,1,0}, 1);
	inequalities[5] = new LinearMultivariateRealFunction(new double[]{0,1,0,0,0,1}, 1);
	inequalities[6] = new LinearMultivariateRealFunction(new double[]{10,0,10,0,10,0}, 40);
	inequalities[7] = new LinearMultivariateRealFunction(new double[]{0,10,0,10,0,10}, 20);	    
	    
	//optimization problem
	OptimizationRequest or = new OptimizationRequest();
	or.setF0(objectiveFunction);
	//or.setInitialPoint(new double[] { 0.66666666666, 0.33333333333, 0.666666666666, 0.3333333333333, 0.666666666666, 0.3333333333333});
	//or.setFi(inequalities); //if you want x>0 and y>0
	or.setA(A);
	or.setB(b);
	or.setToleranceFeas(1.E-12);
	or.setTolerance(1.E-12);
		
	//optimization
	JOptimizer opt = new JOptimizer();
	opt.setOptimizationRequest(or);
	int returnCode = opt.optimize();
	double[] sol = opt.getOptimizationResponse().getSolution();
	System.out.println(sol[0] + " " + sol[1] + " " + sol[2] + " " + sol[3] + " " + sol[4] + " " + sol[5]);
    }

}
