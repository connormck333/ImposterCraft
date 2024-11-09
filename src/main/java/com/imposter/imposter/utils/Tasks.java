package com.imposter.imposter.utils;

import lombok.Getter;

import java.util.List;
import java.util.Random;

@Getter
public enum Tasks {
    ALIGN_ENGINE("align_engine"),
    ASTEROIDS("asteroids"),
    CALIBRATE_DISTRIBUTOR("calibrate_distributor"),
    CLEAR_VENT("clear_vent"),
    DIVERT_POWER("divert_power"),
    DOWNLOAD("download"),
    ENABLE_POWER("enable_power"),
    MEDICAL_SCAN("medical_scan"),
    NAVIGATION("navigation"),
    NUMBERS("numbers"),
    SHIELDS("shields"),
    EMPTY_TRASH("empty_trash"),
    UPLOAD("upload"),
    FIX_LIGHTS("fix_lights"),
    REACTOR_MELTDOWN("reactor_meltdown"),
    OXYGEN_DEPLETION("oxygen_depletion");

    private final String task;

    private static final List<Tasks> VALUES = List.of(values());
    private static final Random RANDOM = new Random();

    Tasks(String task) {
        this.task = task;
    }

    public String getString() {
        return switch (task) {
            case "align_engine" -> "Align Engine";
            case "asteroids" -> "Destroy Asteroids";
            case "calibrate_distributor" -> "Calibrate Distributor";
            case "clear_vent" -> "Clear Vent";
            case "divert_power" -> "Divert Power";
            case "download" -> "Download File";
            case "enable_power" -> "Enable Power";
            case "medical_scan" -> "Medical Scan";
            case "navigation" -> "Realign Navigation";
            case "numbers" -> "Start Reactor";
            case "shields" -> "Activate Shields";
            case "empty_trash" -> "Empty Trash";
            case "upload" -> "Upload File";
            case "fix_lights" -> "Fix Lights";
            case "reactor_meltdown" -> "Reactor Meltdown";
            case "oxygen_depletion" -> "Oxygen Depleting";
            default -> null;
        };
    }

    public boolean isSabotage() {
        return this == FIX_LIGHTS || this == REACTOR_MELTDOWN || this == OXYGEN_DEPLETION;
    }

    public static Tasks getRandomTask() {
        return VALUES.get(RANDOM.nextInt(VALUES.size()));
    }
}
