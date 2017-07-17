public class Main {

    public static void main(String[] args) {
        VehicleStoreImpl obj = new VehicleStoreImpl();
        obj.mapVehiclesToJSon();
        Vehicle me;
        me = obj.getVehicle("MACCHINA1");
    }

}
