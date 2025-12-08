package SchedulingAlgoStrategyPattern.ConcreteStrategies;

import CommandPatternIMP.ConcreteClasses.ElevatorRequest;
import CommonEnums.Direction;
import SchedulingAlgoStrategyPattern.SchedulingStrategy;
import UtilityClasses.Elevator;

import java.util.Queue;

/**
 * A First-Come, First-Served (FCFS) scheduling strategy for elevators.
 */
public class FCFSSchedulingStrategy implements SchedulingStrategy {
    /**
     * Determines the next stop for the elevator based on the FCFS algorithm.
     * @param elevator The elevator for which to determine the next stop.
     * @return The next floor to stop at.
     */
    @Override
    public int getNextStop(Elevator elevator) {
        // Get the elevator's current direction and floor
        Direction elevatorDirection = elevator.getDirection();
        int currentFloor = elevator.getCurrentFloor();

        // Retrieve the FIFO queue of floor requests
        Queue<ElevatorRequest> requestQueue = elevator.getRequestsQueue();

        // If the request queue is empty, stay on the current floor
        if (requestQueue.isEmpty())
            return currentFloor;

        // Fetch the next requested floor
        int nextRequestedFloor = requestQueue.poll().getFloor();

        // If the next floor is the current floor, return it
        if (nextRequestedFloor == currentFloor)
            return currentFloor;
        // Set elevator's direction based on its current state and next floor
        if (elevatorDirection == Direction.IDLE) {
            elevator.setDirection(
                    nextRequestedFloor > currentFloor ? Direction.UP : Direction.DOWN);
        } else if (elevatorDirection == Direction.UP
                && nextRequestedFloor < currentFloor) {
            elevator.setDirection(Direction.DOWN);
        } else if (nextRequestedFloor > currentFloor) {
            elevator.setDirection(Direction.UP);
        }

        // Return the next requested floor
        return nextRequestedFloor;
    }
}
