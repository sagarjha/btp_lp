package lp;
import device.*;
import java.io.*;

public class LP {
    // objective function
    public double [][] P;
    // equalities
    public double [][] A;
    public double [] b;
    // inequalities
    public double [][] G1, G2;
    public double [] h1, h2;

    public void printJOpt () throws IOException{
	BufferedWriter br1 = new BufferedWriter (new FileWriter ("P"));
	BufferedWriter br2 = new BufferedWriter (new FileWriter ("A"));
	BufferedWriter br3 = new BufferedWriter (new FileWriter ("b"));
	BufferedWriter br4 = new BufferedWriter (new FileWriter ("G1"));
	BufferedWriter br5 = new BufferedWriter (new FileWriter ("h1"));
	BufferedWriter br6 = new BufferedWriter (new FileWriter ("G2"));
	BufferedWriter br7 = new BufferedWriter (new FileWriter ("h2"));	
	
	for (int i = 0; i < P.length; ++i) {
	    for (int j = 0; j < P[i].length; ++j) {
		br1.write(P[i][j] + " ");
	    }
	    br1.write("\n");
	}
	br1.close();

	for (int i = 0; i < A.length; ++i) {
	    for (int j = 0; j < A[i].length; ++j) {
		br2.write(A[i][j] + " ");
	    }
	    br2.write("\n");
	}
	br2.close();

	for (int i = 0; i < b.length; ++i) {
	    br3.write(b[i] + " ");
	}
	br3.write("\n");
	br3.close();
	
	for (int i = 0; i < G1.length; ++i) {
	    for (int j = 0; j < G1[i].length; ++j) {
		br4.write(G1[i][j] + " ");
	    }
	    br4.write("\n");
	}
	br4.close();	

	for (int i = 0; i < h1.length; ++i) {
	    br5.write(h1[i] + " ");
	}
	br5.write("\n");
	br5.close();	

	for (int i = 0; i < G2.length; ++i) {
	    for (int j = 0; j < G2[i].length; ++j) {
		br6.write(G2[i][j] + " ");
	    }
	    br6.write("\n");
	}
	br6.close();	

	for (int i = 0; i < h2.length; ++i) {
	    br7.write(h2[i] + " ");
	}
	br7.write("\n");
	br7.close();	
	
    }

    public void printCPLEX (String fileName, Variable [] variableArray, Device [] deviceArray, int [] partition_size, int choice) throws IOException {
	BufferedWriter br = new BufferedWriter (new FileWriter (fileName));
	if (choice != 3) {
	    br.write("Minimize\n");
	}
	else {
	    br.write("Maximize\n");
	}
	br.write("obj: ");
	if (choice != 2 && choice != 3 && choice != 4) {
	    br.write("[ ");
	    // print P here
	    for (int i = 0; i < P.length; ++i) {
		Variable var1 = variableArray[i];
		for (int j = 0; j < P[i].length; ++j) {
		    Variable var2 = variableArray[j];
		    if (i == P.length-1 & j == P[i].length-1) {
			br.write(P[i][j] + " " + var1.printString() + " * " + var2.printString());
		    }
		    else {
			br.write(P[i][j] + " " + var1.printString() + " * " + var2.printString() + " + ");
		    }
		}
	    }
	    br.write(" ]/2");
	}

	else if (choice == 2 || choice == 4){
	    br.write("1");
	}

	else {
	    br.write("[ ");
	    for (int i = 0; i < P.length; ++i) {
		Variable var1 = variableArray[i];
		if (i == 0) {
		br.write("0.0000001 " + var1.printString() + " ^2 ");		    
		}
		br.write(" + 0.0000001 " + var1.printString() + " ^2 ");
	    }
	    br.write(" ]/2");
	}
	
	br.write("\n");
	
	// print constraints
	br.write("Subject To\n");
	for (int i = 0; i < A.length; ++i) {
	    boolean first = true;
	    for (int j = 0; j < A[i].length; ++j) {
		if (A[i][j] != 0.0) {
		    if (first == true) {
			first = false;
			br.write(A[i][j] + " " + variableArray[j].printString());			
		    }
		    else{
			br.write(" + " + A[i][j] + " " + variableArray[j].printString());
		    }
		}
	    }
	    br.write(" = " + b[i]);
	    br.write("\n");
	}
	for (int i = 0; i < G1.length; ++i) {
	    boolean first = true;
	    for (int j = 0; j < G1[i].length; ++j) {
		if (G1[i][j] != 0.0) {
		    if (first == true) {
			first = false;
			br.write(G1[i][j] + " " + variableArray[j].printString());			
		    }
		    else{
			br.write(" + " + G1[i][j] + " " + variableArray[j].printString());
		    }
		}
	    }
	    br.write(" <= " + h1[i]);
	    br.write("\n");
	}

	for (int i = 0; i < G2.length; ++i) {
	    boolean first = true;
	    for (int j = 0; j < G2[i].length; ++j) {
		if (G2[i][j] != 0.0) {
		    if (first == true) {
			first = false;
			br.write(G2[i][j] + " " + variableArray[j].printString());			
		    }
		    else{
			br.write(" + " + G2[i][j] + " " + variableArray[j].printString());
		    }
		}
	    }
	    br.write(" <= " + h2[i]);
	    br.write("\n");
	}

	if (choice == 2 || choice == 3 || choice == 4) {
	    for (int i = 0; i < deviceArray.length-1; ++i) {
		int storage_cap1 = deviceArray[i].storage_cap;
		int storage_cap2 = deviceArray[i+1].storage_cap;
		boolean first = true;
		for (int j = 0; j < variableArray.length; ++j) {
		    if (variableArray[j].device_id == i) {
			Variable var1 = variableArray[j];
			int part_size = partition_size[var1.policy];
			double coeff = (part_size+0.0)/(storage_cap1+0.0);
			if (first == true) {
			    br.write(coeff + " " + var1.printString());
			}
			else {
			    br.write(" + " + coeff + " " + var1.printString());
			}
		    }
		    else if (variableArray[j].device_id == i+1) {
			first = false;
			Variable var1 = variableArray[j];
			int part_size = partition_size[var1.policy];
			double coeff = (part_size+0.0)/(storage_cap2+0.0);
			br.write(" - " + coeff + " " + var1.printString());
		    }
		}
		br.write (" = 0\n");
	    }
	}

	if (choice != 0 && choice != 3 && choice != 4) {
	    br.write("Binary");
	    br.write("\n");
	    for (int i = 0; i < variableArray.length; ++i) {
		br.write (variableArray[i].printString() + " ");
	    }
	}
	else {
	    for (int i = 0; i < variableArray.length; ++i) {
		br.write (variableArray[i].printString() + " >= 0\n");
	    }
	}

	// print constraints
	for (int i = 0; i < 10; ++i) {
	    boolean first = true;
	    for (int j = 0; j < deviceArray.length; ++j) {
		if (deviceArray[j].region == 0 | deviceArray[j].region == 1) {
		    if (first == true) {
			first = false;
			br.write("x_0_" + i + "_0_"+j);
		    }
		    else {
			br.write (" + x_0_" + i + "_0_"+j);
		    }
		}
	    }
	    br.write(" = 1\n");
	}

	for (int i = 0; i < 10; ++i) {
	    for (int j = 0; j < 4; ++j) {
		boolean first = true;
		for (int k = 0; k < deviceArray.length; ++k) {
		    if (deviceArray[k].region == j) {
			if (first == true) {
			    first = false;
			    br.write("x_0_" + i + "_1_"+k);
			}
			else {
			    br.write (" + x_0_" + i + "_1_"+k);
			}
		    }
		}
		first = true;
		for (int k = 0; k < deviceArray.length; ++k) {
		    if (deviceArray[k].region == j) {
			if (first == true) {
			    first = false;
			    br.write(" - x_0_" + i + "_2_"+k);
			}
			else {
			    br.write (" - x_0_" + i + "_2_"+k);
			}
		    }
		}
		
		br.write(" = 0\n");
	    }
	}
	
	br.close();
    }

    public void printUtilization (String fileName, Variable [] variableArray, Device [] deviceArray, int [] partition_size, int choice) throws IOException {
	BufferedReader br = new BufferedReader (new FileReader (fileName));
	boolean val [] = new boolean [variableArray.length];
	String line;
	double utilization [] = new double [deviceArray.length];
	while ((line = br.readLine()) != null) {
	    if (line == "") {
		continue;
	    }
	    String [] fields = line.split(" ");
	    String var = fields[0];
	    String [] info = var.split("_");
	    int policy = Integer.parseInt(info[1]), part = Integer.parseInt(info[2]), replica = Integer.parseInt(info[3]), dev = Integer.parseInt(info[4]);
	    // System.out.println(policy + " " + part + " " + replica + " " + dev);
	    utilization[dev] += partition_size[policy];
	    
	    for (int i = 0; i < variableArray.length; ++i) {
		Variable variable = variableArray[i];
		if (variable.policy == policy && variable.partition == part && variable.replica == replica && variable.device_id == dev) {
		    val[i] = true;
		}
	    }
	}

	for (int i = 0; i < deviceArray.length; ++i) {
	    System.out.println((100.0 * utilization[i])/deviceArray[i].storage_cap);
	}

    }
}
