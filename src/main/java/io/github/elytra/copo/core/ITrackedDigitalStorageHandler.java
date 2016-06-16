package io.github.elytra.copo.core;

public interface ITrackedDigitalStorageHandler extends IDigitalStorageHandler {
	int getTypes();
	int getBits();
	
	int getTypeAllocationCost();
	
	int getMaxTypes();
	int getMaxBits();
}
