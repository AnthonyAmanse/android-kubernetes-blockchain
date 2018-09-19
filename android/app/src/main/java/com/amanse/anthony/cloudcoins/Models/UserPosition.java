package com.amanse.anthony.cloudcoins.Models;

public class UserPosition {
    int userPosition;
    int count;
    int steps;

    public UserPosition(int userPosition, int count, int steps) {
        this.userPosition = userPosition;
        this.count = count;
        this.steps = steps;
    }

    public int getUserPosition() {
        return userPosition;
    }

    public int getCount() {
        return count;
    }

    public int getSteps() {
        return steps;
    }
}
