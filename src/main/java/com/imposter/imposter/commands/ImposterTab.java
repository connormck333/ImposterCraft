package com.imposter.imposter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ImposterTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> results = new ArrayList<>();

        if (args.length == 1) {
            results.add("");
        }

        return results;
    }
}
