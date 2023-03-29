package me.devwckd.dodgeball.commands;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.arena.Arena;
import me.devwckd.dodgeball.arena.ArenaManager;
import me.devwckd.dodgeball.edit_session.EditSession;
import me.devwckd.dodgeball.edit_session.EditSessionManager;
import me.devwckd.dodgeball.exception.ArenaException;
import me.devwckd.dodgeball.exception.EditSessionException;
import me.devwckd.dodgeball.exception.RoomException;
import me.devwckd.dodgeball.game.Game;
import me.devwckd.dodgeball.room.Room;
import me.devwckd.dodgeball.room.RoomManager;
import me.devwckd.dodgeball.utils.Cuboid;
import me.devwckd.dodgeball.views.RoomListView;
import me.saiintbrisson.minecraft.ViewFrame;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class RoomCommands {

    private final ArenaManager arenaManager;
    private final RoomManager roomManager;
    private final ViewFrame viewFrame;
    private final JavaPlugin plugin;

    /**
     * dbr
     */
    @Command(
      name = "dodgeballrooms",
      aliases = {"dbr"},
      target = CommandTarget.PLAYER,
      permission = "dodgeball.admin"
    )
    public void onMainCommand(final @NotNull Context<Player> context) {
        viewFrame.open(RoomListView.class, context.getSender());
    }

    @Command(
      name = "dodgeballrooms.create",
      target = CommandTarget.PLAYER
    )
    public void onCreateCommand(final @NotNull Context<Player> context, final @NotNull String arenaId) {
        final Player player = context.getSender();

        final Arena arena = arenaManager.findById(arenaId);
        if(arena == null) {
            player.sendMessage("§c§l[!] §eThe arena " + arenaId + " doesn't exist.");
            return;
        }

        final Room room = new Room(UUID.randomUUID(), arena, new Game<>(plugin));
        try {
            roomManager.insert(room);
        } catch (RoomException e) {
            player.sendMessage("§c§l[!] §eError: " + e.getMessage());
        }
    }


}
