package me.mcluke300.commandlistener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class CommandListener extends JavaPlugin {
    private final clistener clistener = new clistener();
    public final static HashMap<String, ArrayList<Block>> hashmap = new HashMap<String, ArrayList<Block>>();
    public static CommandListener plugin;

    public void onDisable() {
    }

    public void onEnable() {
        plugin = this;
        LoadConfiguration();
        Bukkit.getServer().getPluginManager().registerEvents(clistener, this);
    }

    public void LoadConfiguration() {
        String path0 = "BlackListCommands";
        String path1 = "BlockedCommands";
        String path2 = "OnCommandMessage";
        String path3 = "RemoveFromCwOnQuit";
        String path4 = "EnablePermissionExempt";
        List<String> words = new ArrayList<String>();
        words.add("/login");
        words.add("/changepassword");
        words.add("/register");
        getConfig().addDefault(path0, true);
        getConfig().addDefault(path1, words);
        getConfig().addDefault(path2, "&3[CW]&c&player &6&cmd");
        getConfig().addDefault(path3, true);
        getConfig().addDefault(path4, true);
        getConfig().options().copyDefaults(true);
        saveConfig();

    }

    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String [] args) {
        if ((commandLabel.equalsIgnoreCase("CW"))) {

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("commandwatch.reload"))
                        getConfig();
                    reloadConfig();
                    getServer().getPluginManager().disablePlugin(plugin);
                    getServer().getPluginManager().enablePlugin(plugin);
                    sender.sendMessage(ChatColor.GREEN + "CommandWatcher Config Reloaded");

                } else if (args[0].equalsIgnoreCase("check")) {
                    if (sender instanceof Player) {
                        Player players = (Player) sender;
                        if (sender.hasPermission("commandwatch.checkwatch")) {
                            players.sendMessage(ChatColor.AQUA + "--------" + ChatColor.GREEN + "People in CommandWatch" + ChatColor.AQUA + "--------");
                            for (String player1 : hashmap.keySet()) {
                                players.sendMessage(ChatColor.AQUA + player1);

                            }
                        }
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (sender.hasPermission("commandwatch.help")) {
                        sender.sendMessage(ChatColor.DARK_GREEN + "=====" + ChatColor.BLUE + "CommandWatcher" + ChatColor.DARK_GREEN + "=====");
                        sender.sendMessage(ChatColor.DARK_GREEN + "/cw" + ChatColor.BLUE + "  Enables or disables CommandWatcher for sender");
                        sender.sendMessage(ChatColor.DARK_GREEN + "/cw <PlayerName>" + ChatColor.BLUE + "  Enables CommandWatcher for the player.");
                        sender.sendMessage(ChatColor.DARK_GREEN + "/cw check" + ChatColor.BLUE + "  Lists all players in CommandWatcher.");
                        sender.sendMessage(ChatColor.DARK_GREEN + "/cw reload" + ChatColor.BLUE + "  Reloads CommandWatcher plugin and config.");
                    }
                } else {
                    Player player2 = getServer().getPlayerExact(args[0]);
                    if (player2 != null) {
                        if (sender.hasPermission("commandwatch.watchother")) {
                            if (sender instanceof Player) {
                                Player players = (Player) sender;
                                String player = players.getName();
                                String player3 = player2.getName();
                                if (hashmap.containsKey(player3)) {
                                    hashmap.remove(player3);
                                    players.sendMessage(ChatColor.DARK_RED + "Command Watcher Disabled for " + player3);
                                    player2.sendMessage(ChatColor.DARK_RED + "Command Watcher Disabled by " + player);
                                } else {
                                    hashmap.put(player3, null);
                                    players.sendMessage(ChatColor.DARK_GREEN + "Command Watcher Enabled for " + player3);
                                    player2.sendMessage(ChatColor.DARK_GREEN + "Command Watcher Enabled by " + player);
                                }
                            }
                        }
                    }
                }
            } else if (args.length == 0) {

                if (sender.hasPermission("commandwatch.watch")) {
                    {
                        if (sender instanceof Player) {
                            Player players = (Player) sender;
                            String player = players.getName();
                            if (hashmap.containsKey(player)) {
                                hashmap.remove(player);
                                players.sendMessage(ChatColor.DARK_RED + "Command Watcher Disabled");
                            } else {
                                hashmap.put(player, null);
                                players.sendMessage(ChatColor.DARK_GREEN + "Command Watcher Enabled");
                            }

                        }
                    }
                }
            }

        }
        return false;

    }

    @SuppressWarnings("deprecation")
    public static void oncommand(String msg, Player player, String x, String y, String z, String world) {
        String playername = player.getName();
        for (String player1 : hashmap.keySet()) {
            Player player2 = Bukkit.getPlayerExact(player1);
            String msg2 [] = msg.split(" ");
            if (player1 != null && !playername.equalsIgnoreCase(player1) && player2 != null) {
                if (!player.hasPermission("commandwatch.exempt") || player2.hasPermission("commandwatch.bypassexempt")
                        || !plugin.getConfig().getBoolean("EnablePermissionExempt")) {
                    if (plugin.getConfig().getBoolean("BlackListCommands") == true) {
                        if (!plugin.getConfig().getStringList("BlockedCommands").contains(msg2[0].toLowerCase())) {
                            String result = plugin.getConfig().getString("OnCommandMessage");
                            result = result.replaceAll("&player", playername);
                            result = result.replaceAll("&cmd", msg);
                            result = result.replaceAll("&x", x);
                            result = result.replaceAll("&y", y);
                            result = result.replaceAll("&z", z);
                            result = result.replaceAll("&world", world);
                                result = ChatColor.translateAlternateColorCodes('&', result);
                            player2.sendMessage(result);
                        }
                    }
                }
            }
        }
    }

    public static void onquit(String playname) {
        if (plugin.getConfig().getBoolean("RemoveFromCwOnQuit")) {
            if (hashmap.containsKey(playname)) {
                hashmap.remove(playname);

            }
        }
    }
}
