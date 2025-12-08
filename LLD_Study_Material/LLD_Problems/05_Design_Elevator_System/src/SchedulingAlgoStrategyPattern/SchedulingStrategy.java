package SchedulingAlgoStrategyPattern;

import UtilityClasses.Elevator;

/**
 * Interface for elevator scheduling strategies.
 */
public interface SchedulingStrategy {
    /**
     * Determines the next stop for the given elevator.
     * @param elevator The elevator for which to determine the next stop.
     * @return The next floor to stop at.
     */
    int getNextStop(Elevator elevator);
}
