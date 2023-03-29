package me.devwckd.dodgeball.commands;

import lombok.RequiredArgsConstructor;
import me.devwckd.dodgeball.room.Room;
import me.devwckd.dodgeball.room.RoomManager;
import me.devwckd.dodgeball.states.AbstractState;
import me.devwckd.dodgeball.views.RoomListView;
import me.saiintbrisson.minecraft.ViewFrame;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DodgeballCommands {

    private final RoomManager roomManager;
    private final ViewFrame viewFrame;

    /**
     * dodgeball
     */
    @Command(
      name = "dodgeball",
      aliases = {"db"},
      target = CommandTarget.PLAYER
    )
    public void onMainCommand(final @NotNull Context<Player> context) {
        viewFrame.open(RoomListView.class, context.getSender());
    }

    /**
     * dodgeball quit
     */
    @Command(
      name = "dodgeball.quit",
      target = CommandTarget.PLAYER
    )
    public void onQuitCommand(final @NotNull Context<Player> context) {
        final Player player = context.getSender();
        final Room room = roomManager.findByPlayer(player);
        if (room == null) {
            player.sendMessage("§c§l[!] §7You are not in a Dodgeball game!");
            player.sendMessage("§c§l[?] §7Hint: §eJoin one using /dodgeball");
            return;
        }

        room.getCastedState(AbstractState.class).quit(player);
        player.sendMessage("§a§l[»] §7You have quit the dodgeball match!");
    }

}
