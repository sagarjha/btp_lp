package device;

public class Device {
    public int id;
    public int region;
    public int zone;
    public int storage_cap;
    public Device (int id_, int region_, int zone_, int storage_cap_) {
	id = id_;
	region = region_;
	zone = zone_;
	storage_cap = storage_cap_;
    }
}
