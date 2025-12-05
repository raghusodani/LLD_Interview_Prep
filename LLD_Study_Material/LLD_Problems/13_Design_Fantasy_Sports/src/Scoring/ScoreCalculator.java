package Scoring;

import Models.MatchEvent;

public interface ScoreCalculator {
    double calculatePoints(MatchEvent event);
}
