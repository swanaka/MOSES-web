package jp.ac.utokyo.is.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ac.utokyo.is.model.InputData;
import model.Demand;
import model.DiscretePrice;
import model.Fleet;
import model.Freight;
import model.FuelPrice;
import model.Market;
import model.Port;
import model.PortNetwork;
import model.Ship;
import model.ShipOperator;
import model.SimpleDemand;
import model.SimplePort;
import model.SimpleShip;
import model.SimpleShipOperator;
import model.Status.CargoType;
import model.Status.FuelType;
import simulation.Simulation4Workshop;
import util.CAPEXCalculator;
import util.CAPEXCalculator4Workshop;

public class SimulationLogic implements SimulationData{
	
	public Map<String, String> run(InputData data){
		Fleet.reset();
		PortNetwork.reset();
		Market.reset();
		
		List<Port> ports = new ArrayList<Port>();
		ports.add(settingForPort("Persian Gulf", Integer.parseInt(data.getNumOfBunkeringFacilitiesAtPersianGulf()), data.getBunkeringMethodAtPersianGulf()));
		ports.add(settingForPort("Japan", Integer.parseInt(data.getNumOfBunkeringFacilitiesAtJapan()), data.getBunkeringMethodAtJapan()));
		ports.add(settingForPort("Singapore", Integer.parseInt(data.getNumOfBunkeringFacilitiesAtSingapore()), data.getBunkeringMethodAtSingapore()));
		
		PortNetwork.setPortSettings(ports,routeMatrix);
		
		settingForMarket();

		ShipOperator operator = new SimpleShipOperator("MOL");
		Port initialPort = PortNetwork.getPort(initialPortName);
		Port bunkeringPort = PortNetwork.getPort("Singapore");
		for(int i = 0; i < Integer.parseInt(data.getNumOfHFO()); i++) {
			Ship ship = new SimpleShip(speed, cargoType, cargoAmount, focHFO, fuelCapacity, FuelType.HFO, initialPort, operatingCost, true, gasdieselFlag);
			ship.setName("HFO:" + String.valueOf(i));
			ship.setOwner(operator);
			ship.setBunkeringPort(bunkeringPort);
			Fleet.add(ship);
		}
		for(int i=0; i< Integer.parseInt(data.getNumOfLSFO()); i++) {
			Ship ship = new SimpleShip(speed, cargoType, cargoAmount, focLSFO, fuelCapacity, FuelType.LSFO, initialPort, operatingCost, false, gasdieselFlag);
			ship.setName("LFO:" + String.valueOf(i));
			ship.setOwner(operator);
			ship.setBunkeringPort(bunkeringPort);
			Fleet.add(ship);
		}
		for(int i=0; i<Integer.parseInt(data.getNumOfLNG()); i++) {
			Ship ship = new SimpleShip(speed, cargoType, cargoAmount, focLNG, fuelCapacity, FuelType.LNG, initialPort, operatingCost, false, gasdieselFlag);
			ship.setName("LNG:" + String.valueOf(i));
			ship.setOwner(operator);
			ship.setBunkeringPort(bunkeringPort);
			Fleet.add(ship);
		}
		for(int i=0; i< Integer.parseInt(data.getNumOfHFOLNG());i++) {
			Ship ship = new SimpleShip(speed, cargoType, cargoAmount, focHFOLNG, fuelCapacity, FuelType.HFOLNG, initialPort, operatingCost, true, gasdieselFlag);
			ship.setName("HFOLNG:" + String.valueOf(i));
			ship.setOwner(operator);
			ship.setBunkeringPort(bunkeringPort);
			Fleet.add(ship);
		}
		if(Integer.parseInt(data.getNumOfBunkeringFacilitiesAtSingapore()) > 0) {
			Fleet.setDetour(true);
		}else {
			Fleet.setDetour(false);
		}
		CAPEXCalculator calculator = new CAPEXCalculator4Workshop(data.getNumOfHFO(),data.getNumOfLSFO(), data.getNumOfLNG(), data.getNumOfHFOLNG(),data.getNumOfBunkeringFacilitiesAtPersianGulf(), data.getBunkeringMethodAtPersianGulf(),data.getNumOfBunkeringFacilitiesAtJapan(),data.getBunkeringMethodAtJapan(),data.getNumOfBunkeringFacilitiesAtSingapore(),data.getBunkeringMethodAtSingapore());

		Simulation4Workshop simulation = new Simulation4Workshop(endTime, calculator);
		simulation.execute();
		return simulation.getResult();
	}
	
	private Port settingForPort(String name, int numOfLNGfacility, String bunkeringMethod) {
		Port port = new SimplePort(name);
		int numOfHFOfacility = numOfFacilities;
		int numOfLSFOfacility = numOfFacilities;
		boolean bunkeringFlag = true;
		double bunkeringCapacity = bunkeringCapacityOthers;
		double bunkeringCapacityLNG = bunkeringCapacityOthers;
		if(bunkeringMethod.equals("Shore to Ship")) {
			port.setNumOfBunkers(-1);
		}else {
			bunkeringFlag = false;
			port.setNumOfBunkers(numOfFacilities);
			numOfLNGfacility = numOfFacilities;
		}
		if(bunkeringMethod.equals("Truck to Ship")) {
			bunkeringCapacityLNG = bunkeringCapacityTruck;
		}
		if(bunkeringMethod.equals("Ship to Ship")) {
			bunkeringCapacityLNG = bunkeringCapacityShip;
		}
		for(int j = 0; j < numOfFacilities; j++){
			List<FuelType> fuelTypeList = new ArrayList<FuelType>();
			List<Double> bunkeringCapacityList = new ArrayList<Double>();
			if(numOfHFOfacility > 0) {
				fuelTypeList.add(FuelType.HFO);
				bunkeringCapacityList.add(bunkeringCapacity);
				numOfHFOfacility -= 1;
			}
			if(numOfLNGfacility > 0) {
				fuelTypeList.add(FuelType.LNG);
				bunkeringCapacityList.add(bunkeringCapacityLNG);
				numOfLNGfacility -= 1;
			}
			if(numOfLSFOfacility > 0) {
				fuelTypeList.add(FuelType.LSFO);
				bunkeringCapacityList.add(bunkeringCapacity);
				numOfLSFOfacility -= 1;
			}
			CargoType loadingType = cargoType;
			port.addPortFacility(fuelTypeList, loadingType, bunkeringCapacityList, loadingCapacity, berthingFee, bunkeringFlag);
		}
		return port;
	}
	
	private void settingForMarket() {
		// Input freight data from freight data file "freight_config.csv"
		
		Freight freight = new Freight(
				cargoType,upforStandard,downforStandard,pforStandard,upforRate,
				downforRate,pforRate,initialStandard,initialRate);
		Market.addFreight(freight);

		FuelPrice fuelprice = null;
		for (FuelType fuelType : typelist){
			// Input oilprice data from oilprice data file "oilprice_config.csv"
			List<String> prices = new ArrayList<String>();
			switch(fuelType){
				case LNG:
					prices.add(maxLNG);
					prices.add(midLNG);
					prices.add(minLNG);
					fuelprice = new DiscretePrice(prices, initialLNG);
					fuelprice.setFuelType(fuelType);
					break;
				case HFO:
					prices.add(maxHFO);
					prices.add(midHFO);
					prices.add(minHFO);
					fuelprice = new DiscretePrice(prices, initialHFO);
					fuelprice.setFuelType(fuelType);
					break;
				case LSFO:
					prices.add(maxLSFO);
					prices.add(midLSFO);
					prices.add(minLSFO);
					fuelprice = new DiscretePrice(prices, initialLSFO);
					fuelprice.setFuelType(fuelType);
					break;
				default:
					;
					
			}
			Market.addFuelPrice(fuelprice);
		}
		Demand demand = new SimpleDemand(cargoType,limit,amount,duration,departure,destination);

		Market.addDemand(demand);
	}
}
