package com.amanse.anthony.cloudcoins.Models;

public class ParticipantPredictionModel {
    float prediction;
    float currentParticipants;

    public ParticipantPredictionModel(float prediction, float currentParticipants) {
        this.prediction = prediction;
        this.currentParticipants = currentParticipants;
    }

    public float getPrediction() {
        return prediction;
    }

    public float getCurrentParticipants() {
        return currentParticipants;
    }
}
