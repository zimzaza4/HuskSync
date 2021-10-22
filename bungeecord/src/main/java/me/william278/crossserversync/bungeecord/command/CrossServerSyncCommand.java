package me.william278.crossserversync.bungeecord.command;

import de.themoep.minedown.MineDown;
import me.william278.crossserversync.CrossServerSyncBungeeCord;
import me.william278.crossserversync.MessageStrings;
import me.william278.crossserversync.Settings;
import me.william278.crossserversync.redis.RedisMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CrossServerSyncCommand extends Command implements TabExecutor {

    private final static CrossServerSyncBungeeCord plugin = CrossServerSyncBungeeCord.getInstance();
    private final static String[] COMMAND_TAB_ARGUMENTS = {"about", "reload"};
    private final static String PERMISSION = "crossserversync.command.csc";

    //public CrossServerSyncCommand() { super("csc", PERMISSION, "crossserversync"); }
    public CrossServerSyncCommand() { super("csc"); }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            if (args.length == 1) {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "about", "info" -> sendAboutInformation(player);

                    default -> sender.sendMessage(new MineDown(MessageStrings.ERROR_INVALID_SYNTAX.replaceAll("%1%", "/csc <about>")).toComponent());
                }
            } else {
                sendAboutInformation(player);
            }
        }
    }

    /**
     * Send information about the plugin
     * @param player The player to send it to
     */
    private void sendAboutInformation(ProxiedPlayer player) {
        try {
            new RedisMessage(RedisMessage.MessageType.SEND_PLUGIN_INFORMATION,
                    new RedisMessage.MessageTarget(Settings.ServerType.BUKKIT, player.getUniqueId()),
                    plugin.getProxy().getName(), plugin.getDescription().getVersion()).send();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to serialize plugin information to send", e);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            if (!player.hasPermission(PERMISSION)) {
                return Collections.emptyList();
            }
            if (args.length == 1) {
                return Arrays.stream(COMMAND_TAB_ARGUMENTS).filter(val -> val.startsWith(args[0]))
                        .sorted().collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

}
