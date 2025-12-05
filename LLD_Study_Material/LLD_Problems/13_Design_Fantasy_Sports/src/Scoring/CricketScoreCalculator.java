package Scoring;

import Models.MatchEvent;

public class CricketScoreCalculator implements ScoreCalculator {
    private static final double POINTS_PER_RUN = 1.0;
    private static final double POINTS_FOR_BOUNDARY = 1.0;
    private static final double POINTS_FOR_SIX = 2.0;
    private static final double POINTS_FOR_WICKET = 25.0;
    private static final double POINTS_FOR_CATCH = 8.0;

    @Override
    public double calculatePoints(MatchEvent event) {
        switch (event.getEventType()) {
            case RUN_SCORED:
                return event.getRuns() * POINTS_PER_RUN;
            case BOUNDARY:
                return POINTS_FOR_BOUNDARY;
            case SIX:
                return POINTS_FOR_SIX;
            case WICKET_TAKEN:
                return POINTS_FOR_WICKET;
            case CATCH_TAKEN:
                return POINTS_FOR_CATCH;
            default:
                return 0.0;
        }
    }
}
