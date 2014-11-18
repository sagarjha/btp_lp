package device;

public class Variable {

    public int policy;
    public int partition;
    public int replica;
    public int device_id;

    public Variable (int policy_, int partition_, int replica_, int device_id_) {
	policy = policy_;
	partition = partition_;
	replica = replica_;
	device_id = device_id_;
    }

    public String printString () {
	return "x_" + policy + "_" + partition + "_" + replica + "_" + device_id;
    }
    
}
