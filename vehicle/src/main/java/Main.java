import com.wedriveu.vehicle.ServerVehicleRabbitMQ;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerVehicleRabbitMQ server = new ServerVehicleRabbitMQ("veicolo1", 100.0);
        ServerVehicleRabbitMQ server2 = new ServerVehicleRabbitMQ("veicolo2", 50.0);
        ServerVehicleRabbitMQ server3 = new ServerVehicleRabbitMQ("veicolo3", 1.0);
        server.startVehicleServer();
        server2.startVehicleServer();
        server3.startVehicleServer();
    }
}