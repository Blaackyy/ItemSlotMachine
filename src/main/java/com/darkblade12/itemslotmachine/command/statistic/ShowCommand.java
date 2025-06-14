package com.darkblade12.itemslotmachine.command.statistic;

import com.darkblade12.itemslotmachine.ItemSlotMachine;
import com.darkblade12.itemslotmachine.Permission;
import com.darkblade12.itemslotmachine.plugin.Message;
import com.darkblade12.itemslotmachine.plugin.PluginBase;
import com.darkblade12.itemslotmachine.plugin.command.CommandBase;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachine;
import com.darkblade12.itemslotmachine.slotmachine.SlotMachineManager;
import com.darkblade12.itemslotmachine.statistic.PlayerStatistic;
import com.darkblade12.itemslotmachine.statistic.Record;
import com.darkblade12.itemslotmachine.statistic.SlotMachineStatistic;
import com.darkblade12.itemslotmachine.statistic.Statistic;
import com.darkblade12.itemslotmachine.statistic.StatisticManager;
import com.darkblade12.itemslotmachine.util.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class ShowCommand extends CommandBase<ItemSlotMachine> {
    private static final Random RANDOM = new Random();

    public ShowCommand() {
        super("show", Permission.COMMAND_STATISTIC_SHOW, "<slot/player>", "<name>");
    }

    @Override
    public void execute(ItemSlotMachine plugin, CommandSender sender, String label, String[] args) {
        StatisticManager statManager = plugin.getManager(StatisticManager.class);
        String statText;
        String name = args[1];
        String type = args[0].toLowerCase();
        switch (type) {
            case "slot":
                SlotMachine slot = plugin.getManager(SlotMachineManager.class).getSlotMachine(name);
                if (slot == null) {
                    plugin.sendMessage(sender, Message.SLOT_MACHINE_NOT_FOUND, name);
                    return;
                }
                name = slot.getName();

                SlotMachineStatistic slotStat = statManager.getSlotMachineStatistic(slot);
                if (slotStat == null) {
                    plugin.sendMessage(sender, Message.STATISTIC_UNAVAILABLE_SLOT_MACHINE, name);
                    return;
                }

                statText = toString(plugin, slotStat);
                plugin.sendMessage(sender, Message.COMMAND_STATISTIC_SHOW_SLOT_MACHINE, name, statText);
                break;
            case "player":
                OfflinePlayer player = plugin.getPlayer(name);
                if (player == null) {
                    plugin.sendMessage(sender, Message.PLAYER_NOT_FOUND, name);
                    return;
                }
                name = player.getName();

                PlayerStatistic playerStat = statManager.getPlayerStatistic(player);
                if (playerStat == null) {
                    plugin.sendMessage(sender, Message.STATISTIC_UNAVAILABLE_PLAYER, name);
                    return;
                }

                statText = toString(plugin, playerStat);
                plugin.sendMessage(sender, Message.COMMAND_STATISTIC_SHOW_PLAYER, name, statText);
                break;
            default:
                displayUsage(sender, label);
                break;
        }
    }

    @Override
    public List<String> getSuggestions(ItemSlotMachine plugin, CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return Arrays.asList("slot", "player");
            case 2:
                switch (args[0].toLowerCase()) {
                    case "slot":
                        return plugin.getManager(SlotMachineManager.class).getNames();
                    case "player":
                        return plugin.getManager(StatisticManager.class).getPlayerNames();
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    private static char randomDiceSymbol() {
        return (char) (0x2680 + RANDOM.nextInt(6));
    }

    private static String toString(PluginBase plugin, Statistic statistic) {
        StringBuilder text = new StringBuilder();
        for (Record record : statistic) {
            ChatColor color = MessageUtils.randomColorCode();
            ChatColor altColor = MessageUtils.similarColor(color);
            char dice = randomDiceSymbol();
            String category = StringUtils.capitalize(record.getCategory().getLocalizedName(plugin));
            Number value = record.getValue();
            String line = plugin.formatMessage(Message.COMMAND_STATISTIC_SHOW_LINE, dice, color, category, altColor, value);
            text.append("\n").append(ChatColor.RESET).append(line);
        }

        return text.toString();
    }
}
