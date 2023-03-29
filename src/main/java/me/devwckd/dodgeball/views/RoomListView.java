package me.devwckd.dodgeball.views;

import me.devwckd.dodgeball.history.History;
import me.devwckd.dodgeball.history.HistoryManager;
import me.devwckd.dodgeball.room.Room;
import me.devwckd.dodgeball.room.RoomManager;
import me.devwckd.dodgeball.states.AbstractJoinableState;
import me.devwckd.dodgeball.utils.ItemUtils;
import me.saiintbrisson.minecraft.PaginatedView;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.devwckd.dodgeball.states.AbstractJoinableState.JoinResult.FULL;
import static me.devwckd.dodgeball.states.AbstractJoinableState.JoinResult.SUCCESS;

public class RoomListView extends PaginatedView<Room> {

    private final RoomManager roomManager;

    public RoomListView(final @NotNull RoomManager roomManager, final @NotNull HistoryManager historyManager) {
        super(6, "Dodgeball Rooms:");
        this.roomManager = roomManager;
        scheduleUpdate(10);
        setLayout(
          "XXXXHXXXX",
          "XXXXXXXXX",
          "XOOOOOOOX",
          "XOOOOOOOX",
          "XOOOOOOOX",
          "XXXXXXXXX"
        );
        setSource($ -> roomManager.getSortedRoomList());

        setLayout('H', viewItem -> viewItem.onRender(context -> {
            final History history = historyManager.findCachedById(context.getPlayer().getUniqueId());
            if (history == null) {
                context.setItem(new ItemStack(Material.STONE));
            } else {
                context.setItem(ItemUtils.createProfileHead(history));
            }
        }));

        setCancelOnClick(true);
        setCancelOnDrag(true);
        setCancelOnClone(true);
        setCancelOnDrop(true);
        setCancelOnPickup(true);
        setCancelOnMoveOut(true);
        setCancelOnShiftClick(true);
    }

    @Override
    protected void onItemRender(@NotNull PaginatedViewSlotContext<Room> context, @NotNull ViewItem viewItem, @NotNull Room value) {
        final ItemStack roomItemStack;
        if (context.getPlayer().hasPermission("dodgeball.admin")) {
            roomItemStack = ItemUtils.createRoomItemStackAdmin(value);
        } else {
            roomItemStack = ItemUtils.createRoomItemStack(value);
        }

        viewItem
          .withItem(roomItemStack)
          .onClick(clickContext -> {
              final Room room = roomManager.findCachedById(value.getId());
              if (room == null) {
                  clickContext.update();
                  return;
              }
              if (clickContext.isRightClick() && clickContext.getPlayer().hasPermission("dodgeball.admin")) {
                  roomManager.deleteById(value.getId());
                  clickContext.update();
                  return;
              }

              if (room.getGame().getCurrentState() instanceof AbstractJoinableState joinableState) {
                  final Player player = clickContext.getPlayer();
                  if (!player.getInventory().isEmpty()) {
                      player.sendMessage("§c§l[!] §7Clear your inventory before joining a Dodgeball game!");
                      clickContext.close();
                      return;
                  }

                  final AbstractJoinableState.JoinResult result = joinableState.join(player);
                  if (result == SUCCESS) {
                      player.sendMessage("§a§l[»] §7You joined the game!");
                  } else if (result == FULL) {
                      player.sendMessage("§c§l[!] §7This game is full!");
                      clickContext.close();
                  }
              }
          });
    }

}
