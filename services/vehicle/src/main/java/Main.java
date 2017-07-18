public class Main {

    public static void main(String[] args) {
        VehicleStoreImpl obj = new VehicleStoreImpl();
        obj.mapEntityToJson();
        Vehicle me;
        me = obj.getVehicle("MACCHINA1");
    }

}
