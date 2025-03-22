
public class Comfirm14Q1 {

	public static void main(String[] args) {
		Bus bus = new Bus();
		PatrolCar patrolCar = new PatrolCar();
		
		bus.put(5);
		bus.drive(20);
		bus.drop(3);
		patrolCar.siren();
		patrolCar.drive(10);
	}

}
