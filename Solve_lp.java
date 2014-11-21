import java.io.*;
import device.*;
import lp.*;
import com.joptimizer.functions.*;
import com.joptimizer.optimizers.*;
import com.joptimizer.algebra.*;
import com.joptimizer.solvers.*;
import com.joptimizer.util.*;

public class Solve_lp {

    public static void main (String[] argv) throws IOException, NullPointerException{
	if (argv.length != 2) {
	    System.out.println("Provide the name of the output file and your choice of lp");
	    System.exit(0);
	}
	String fileName = argv[0];
	int choice = Integer.parseInt(argv[1]);
	
	BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
	int numPolicies;
	String line = br.readLine();
	numPolicies = Integer.parseInt(line);
	int [] numPartitions = new int [numPolicies];
	int [] replication_factor = new int [numPolicies];
	int [] partition_size = new int [numPolicies];

	for (int i = 0; i < numPolicies; ++i) {
	    line = br.readLine();
	    String [] vals = line.split(" ");
	    numPartitions[i] = Integer.parseInt(vals[0]);
	    replication_factor[i] = Integer.parseInt(vals[1]);
	    partition_size[i] = Integer.parseInt(vals[2]);
	}

	int deviceCount = 0;
	int numRegions;
	line = br.readLine();
	numRegions = Integer.parseInt(line);
	Region [] regions = new Region [numRegions];

	for (int i = 0; i < numRegions; ++i) {
	    int numZones;
	    line = br.readLine();
	    numZones = Integer.parseInt(line);
	    regions[i] = new Region ();
	    regions[i].zones = new Zone [numZones];
	    for (int j = 0; j < numZones; ++j) {
		int numLocalDevices;
		line = br.readLine();
		numLocalDevices = Integer.parseInt(line);
		regions[i].zones[j] = new Zone ();
		regions[i].zones[j].devs = new Device [numLocalDevices];
		line = br.readLine();
		String [] vals = line.split(" ");
		for (int k = 0; k < numLocalDevices; ++k) {
		    regions[i].zones[j].devs[k] = new Device (deviceCount, i, j, Integer.parseInt (vals[k]));
		    deviceCount++;
		}
	    }
	}

	Device [] deviceArray = new Device [deviceCount];
	int count = 0;
	for (int i = 0; i < numRegions; ++i) {
	    int numZones = regions[i].zones.length;
	    for (int j = 0; j < numZones; ++j) {
		int numLocalDevices = regions[i].zones[j].devs.length;
		for (int k = 0; k < numLocalDevices; ++k) {
		    deviceArray[count] = regions[i].zones[j].devs[k];
		    count++;
		}
	    }
	}

	int variableCount = 0;
	for (int i = 0; i < numPolicies; ++i) {
	    int numPartition = numPartitions[i];
	    int numReplica = replication_factor[i];
	    for (int j = 0; j < numPartition; ++j) {
		for (int k = 0; k < numReplica; ++k) {
		    for (int l = 0; l < deviceCount; ++l) {
			variableCount++;
		    }
		}
	    }
	}

	count = 0;
	Variable [] variableArray = new Variable [variableCount];
	for (int i = 0; i < numPolicies; ++i) {
	    int numPartition = numPartitions[i];
	    int numReplica = replication_factor[i];
	    for (int j = 0; j < numPartition; ++j) {
		for (int k = 0; k < numReplica; ++k) {
		    for (int l = 0; l < deviceCount; ++l) {
			variableArray[count] = new Variable (i, j, k, l);
			count++;
		    }
		}
	    }
	}

	LP linear_program = new LP ();
	linear_program.P = new double [variableCount][variableCount];

	for (int i = 0; i < variableCount; ++i) {
	    Variable var1 = variableArray[i];
	    int storage_cap = deviceArray[var1.device_id].storage_cap;
	    int part_size = partition_size[var1.policy];
	    for (int j = 0; j < variableCount; ++j) {
		if (i != j) {
		    Variable var2 = variableArray[j];
		    if (var1.device_id == var2.device_id) {
			linear_program.P[i][j] = (part_size*partition_size[var2.policy]* (deviceCount-1)+0.0)/(storage_cap*storage_cap*deviceCount+0.0);
		    }
		    else {
			linear_program.P[i][j] = -(part_size*partition_size[var2.policy]+0.0)/(deviceCount*storage_cap*deviceArray[var2.device_id].storage_cap+0.0);
		    }
		}
		else {
		    linear_program.P[i][j] = (part_size*part_size*(deviceCount-1)+0.0)/(deviceCount*storage_cap*storage_cap+0.0);
		}
	    }
	}

	int num_eqns_type1 = 0;
	for (int i = 0; i < numPolicies; ++i) {
	    num_eqns_type1 += (numPartitions[i]*replication_factor[i]);
	}

	linear_program.A = new double [num_eqns_type1][variableCount];
	linear_program.b = new double [num_eqns_type1];

	count = 0;
	for (int i = 0; i < numPolicies; ++i) {
	    int numPartition = numPartitions[i];
	    int numReplica = replication_factor[i];

	    for (int j = 0; j < numPartition; ++j) {

		for (int k = 0; k < numReplica; ++k) {

		    for (int c = 0; c < variableCount; ++c) {
			Variable var = variableArray[c];
			if ((var.policy == i) && (var.partition == j) && (var.replica == k)) {
			    linear_program.A[count][c] = 1;
			}
			else {
			    linear_program.A[count][c] = 0;
			}
		    }
		    linear_program.b[count] = 1;
		    count++;
		}
	    }
	}

	int num_eqns_type2 = 0;
	for (int i = 0; i < numPolicies; ++i) {
	    num_eqns_type2 += (numPartitions[i]*deviceCount);
	}

	linear_program.G1 = new double [num_eqns_type2][variableCount];
	linear_program.h1 = new double [num_eqns_type2];

	count = 0;
	for (int i = 0; i < numPolicies; ++i) {
	    int numPartition = numPartitions[i];

	    for (int j = 0; j < numPartition; ++j) {

		for (int l = 0; l < deviceCount; ++l) {

		    for (int c = 0; c < variableCount; ++c) {
			Variable var = variableArray[c];
			if ((var.policy == i) && (var.partition == j) && (var.device_id == l)) {
			    linear_program.G1[count][c] = 1;
			}
			else {
			    linear_program.G1[count][c] = 0;
			}
		    }

		    linear_program.h1[count] = 1;
		    count++;
		}
	    }
	}

	linear_program.G2 = new double [deviceCount][variableCount];
	linear_program.h2 = new double [deviceCount];

	for (int l = 0; l < deviceCount; ++l) {
	    for (int c = 0; c < variableCount; ++c) {
		Variable var = variableArray[c];
		if (var.device_id == l) {
		    int policy = var.policy;
		    int part_size = partition_size[policy];
		    linear_program.G2[l][c] = part_size;
		}
		else {
		    linear_program.G2[l][c] = 0;
		}
	    }
	    linear_program.h2[l] = deviceArray[l].storage_cap;
	}

	if (choice <= 4) {
	    linear_program.printCPLEX (fileName, variableArray, deviceArray, partition_size, choice);
	}
	else {
	    linear_program.printUtilization(fileName, variableArray, deviceArray, partition_size, choice);
	}
    }
}
