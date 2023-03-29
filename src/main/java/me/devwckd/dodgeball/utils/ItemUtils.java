package me.devwckd.dodgeball.utils;

import me.devwckd.dodgeball.context.DodgeballContext;
import me.devwckd.dodgeball.game.Game;
import me.devwckd.dodgeball.history.History;
import me.devwckd.dodgeball.room.Room;
import me.devwckd.dodgeball.states.AbstractState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public final class ItemUtils {

    private ItemUtils() {
    }

    public static ItemStack createProfileHead(final @NotNull History history) {
        final long totalDeaths = history.getTotalDeaths();
        final long totalKills = history.getTotalKills();
        final long totalGames = history.getTotalGames();
        final long totalWins = history.getTotalWins();

        final ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(history.getId()));
        skullMeta.setDisplayName("§a§l[»] " + history.getNickname());
        skullMeta.setLore(List.of(
          " ",
          " §fWin Rate: §e" + String.format("%.2f", NumberUtils.percentage(totalWins, totalGames) * 100D) + "% ",
          " §fK/D Ratio: §c" + String.format("%.2f", NumberUtils.percentage(totalKills, totalDeaths)) + " ",
          " ",
          " §fTotal Kills: §a" + totalKills + " ",
          " §fTotal Deaths: §a" + totalDeaths + " ",
          " ",
          " §fTotal Wins: §a" + totalWins + " ",
          " §fTotal Games: §a" + totalGames + " ",
          " "
        ));
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }



    public static ItemStack createRoomItemStack(final @NotNull Room room) {
        final Game<DodgeballContext> game = room.getGame();
        if (!game.isStarted()) {
            return new ItemStack(Material.STONE);
        }

        final AbstractState currentState = (AbstractState) room.getGame().getCurrentState();
        final int playerAmount = currentState.getPlayerCount();

        final Material type;
        final String itemName;
        final List<String> lore;
        final boolean enchanted;

        final DodgeballContext context = game.getContext();
        switch (room.getStage()) {
            case WAITING -> {
                type = Material.GREEN_TERRACOTTA;
                itemName = "§a§l[»] §aWaiting for participants...";
                lore = List.of(
                  " ",
                  " §aArena: §7" + context.getArena().getDisplayName(),
                  " §aPlayers: §7" + playerAmount + "/20",
                  " ",
                  "§7§oClick to join!"
                );
                enchanted = true;
            }
            case STARTING -> {
                type = Material.YELLOW_TERRACOTTA;
                itemName = "§e§l[»] §eStarting...";
                lore = List.of(
                  " ",
                  " §eArena: §7" + context.getArena().getDisplayName(),
                  " §ePlayers: §7" + playerAmount + "/20",
                  " ",
                  "§7§oClick to join!"
                );
                enchanted = true;
            }
            case PLAYING -> {
                type = Material.RED_TERRACOTTA;
                itemName = "§c§l[»] §cPlaying...";
                lore = List.of(
                  " ",
                  " §cArena: §7" + context.getArena().getDisplayName(),
                  " §cPlayers: §7" + playerAmount + "/20",
                  " "
                );
                enchanted = false;
            }
            case ENDING -> {
                type = Material.BLACK_TERRACOTTA;
                itemName = "§8§l[»] §8Ending...";
                lore = List.of(
                  " ",
                  " §8Arena: §7" + context.getArena().getDisplayName(),
                  " §8Players: §7" + playerAmount + "/20",
                  " ",
                  "§7§oThis room will be available in moments!"
                );
                enchanted = false;
            }
            default -> {
                return new ItemStack(Material.STONE);
            }
        }

        final ItemStack itemStack = new ItemStack(type);
        itemStack.setAmount(Math.max(1, playerAmount));
        if (enchanted) {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemName);
        itemMeta.setLore(lore);
        if (enchanted) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static ItemStack createRoomItemStackAdmin(final @NotNull Room room) {
        final ItemStack itemStack = createRoomItemStack(room);
        if (itemStack.getType() == Material.STONE) return itemStack;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final List<String> lore = itemMeta.getLore();
        lore.add(" ");
        lore.add(" §cRight click to delete! ");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static void spawnSnowball(Location location) {
        location.getWorld().dropItem(location, new ItemStack(Material.SNOWBALL));
    }

}
