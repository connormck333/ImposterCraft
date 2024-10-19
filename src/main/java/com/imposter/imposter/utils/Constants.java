package com.imposter.imposter.utils;


import org.bukkit.ChatColor;

public class Constants {

    public static final String BOLD_RED = ChatColor.RED + ChatColor.BOLD.toString();
    public static final String BOLD_GREEN = ChatColor.GREEN + ChatColor.BOLD.toString();
    public static final String BOLD_YELLOW = ChatColor.YELLOW + ChatColor.BOLD.toString();
    public static final String INVALID_CMD = "Invalid command - try /imposter help";
    public static final int FINAL_SLOT_INDEX = 53;
    public static final String EXIT_BTN_TITLE = BOLD_YELLOW + "Close Task";
    public static final String SKIP_BTN_TITLE = BOLD_YELLOW + "Skip";
    public static final String TASK_SIGN_TITLE = BOLD_GREEN + "Task Sign";
    public static final String MEETING_SIGN_TITLE = BOLD_GREEN + "Emergency Meeting Sign";
    public static final String VENT_TRAPDOOR_TITLE = BOLD_GREEN + "Vent Trapdoor";
    public static final String VENT_NEXT_ITEM_TITLE = BOLD_GREEN + "Next Vent";
    public static final String VENT_EXIT_ITEM_TITLE = BOLD_RED + "Exit Vent";
    public static final String TURN_OFF_LIGHTS_ITEM_TITLE = BOLD_RED + "Turn off lights";
    public static final String DEPLETE_O2_ITEM_TITLE = BOLD_RED + "Deplete Oxygen";
    public static final String REACTOR_ITEM_TITLE = BOLD_RED + "Trigger reactor meltdown";
    public static final String DOOR_SHUT_BOOK = BOLD_RED + "Shut Doors";
    public static final String CAMERAS_ITEM_TITLE = BOLD_GREEN + "Cameras";
    public static final String CAMERAS_NEXT_TITLE = BOLD_GREEN + "Next Camera";
    public static final String CAMERAS_EXIT_TITLE = BOLD_GREEN + "Exit Cameras";
    public static final String VOTE_BOOK_TITLE = ChatColor.GREEN + ChatColor.BOLD.toString() + "Vote";

    // Role items
    public static final String BOMB_ITEM = BOLD_RED + "Bomb";
    public static final String BRUSH_ITEM = BOLD_RED + "Sweep Bodies";
    public static final String CAMOUFLAGE_ITEM = BOLD_RED + "Camouflage";
    public static final String SHERIFF_ITEM = BOLD_GREEN + "Sheriff Weapon";
    public static final String DEPUTY_ITEM = BOLD_GREEN + "Handcuffs";
    public static final String PROTECTOR_ITEM = BOLD_GREEN + "Protection Bubble";
    public static final String GUESSER_ITEM = BOLD_RED + "Guess Roles";
}
