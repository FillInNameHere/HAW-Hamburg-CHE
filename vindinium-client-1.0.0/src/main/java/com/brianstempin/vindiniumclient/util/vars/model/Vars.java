package com.brianstempin.vindiniumclient.util.vars.model;

/**
 * Created by Christian on 08.05.2016.
 */
public class Vars {
    private double explorationFactor; // 0.15
    private double learningRate; // 0.1

    public Vars() {
    }

    public Vars(double explorationFactor, double learningRate) {
        this.explorationFactor = explorationFactor;
        this.learningRate = learningRate;
    }

    public double getExplorationFactor() {
        return explorationFactor;
    }

    public void setExplorationFactor(double explorationFactor) {
        this.explorationFactor = explorationFactor;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
}
