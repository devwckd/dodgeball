package me.devwckd.dodgeball;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import me.devwckd.dodgeball.arena.ArenaCache;
import me.devwckd.dodgeball.arena.ArenaManager;
import me.devwckd.dodgeball.arena.ArenaRepository;
import me.devwckd.dodgeball.codec.ArenaCodec;
import me.devwckd.dodgeball.codec.HistoryCodec;
import me.devwckd.dodgeball.codec.HistoryEntryCodec;
import me.devwckd.dodgeball.codec.RoomDefinitionCodec;
import me.devwckd.dodgeball.commands.DodgeballCommands;
import me.devwckd.dodgeball.commands.EditCommands;
import me.devwckd.dodgeball.commands.RoomCommands;
import me.devwckd.dodgeball.edit_session.EditSessionManager;
import me.devwckd.dodgeball.history.HistoryCache;
import me.devwckd.dodgeball.history.HistoryManager;
import me.devwckd.dodgeball.history.HistoryRepository;
import me.devwckd.dodgeball.listener.EditSessionListener;
import me.devwckd.dodgeball.listener.HistoryListener;
import me.devwckd.dodgeball.listener.RoomListener;
import me.devwckd.dodgeball.room.RoomCache;
import me.devwckd.dodgeball.room.RoomDefinitionRepository;
import me.devwckd.dodgeball.room.RoomManager;
import me.devwckd.dodgeball.views.RoomListView;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bson.codecs.configuration.CodecRegistries;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public class DodgeballPlugin extends JavaPlugin {

    private MongoClient mongoClient;
    private EditSessionManager editSessionManager;
    private ArenaManager arenaManager;
    private RoomManager roomManager;
    private HistoryManager historyManager;

    private ViewFrame viewFrame;

    @Override
    public void onEnable() {
        createFolders();
        connectToMongoDB();

        historyManager = new HistoryManager(new HistoryCache(), new HistoryRepository(mongoClient));
        editSessionManager = new EditSessionManager(this);
        arenaManager = new ArenaManager(new ArenaCache(), new ArenaRepository(mongoClient));
        roomManager = new RoomManager(this, new RoomCache(), new RoomDefinitionRepository(mongoClient), arenaManager);

        registerViews();
        registerCommands();
        registerListeners();
    }

    private void createFolders() {
        final File dataFolder = getDataFolder();
        if(!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        saveDefaultConfig();

        final File mapsFolder = new File(dataFolder, "maps");
        if(!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
    }

    private void connectToMongoDB() {
        mongoClient = MongoClients.create(MongoClientSettings.builder()
          .codecRegistry(CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromCodecs(
              ArenaCodec.INSTANCE,
              HistoryCodec.INSTANCE,
              HistoryEntryCodec.INSTANCE,
              RoomDefinitionCodec.INSTANCE
            )
          ))
          .build());
    }

    private void registerViews() {
        viewFrame = ViewFrame.of(this, new RoomListView(roomManager, historyManager)).register();
    }

    private void registerCommands() {
        final BukkitFrame bukkitFrame = new BukkitFrame(this);
        bukkitFrame.registerCommands(new EditCommands(editSessionManager, arenaManager));
        bukkitFrame.registerCommands(new RoomCommands(arenaManager, roomManager, viewFrame, this));
        bukkitFrame.registerCommands(new DodgeballCommands(roomManager, viewFrame));
    }

    private void registerListeners() {
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new EditSessionListener(editSessionManager), this);
        pluginManager.registerEvents(new RoomListener(roomManager), this);
        pluginManager.registerEvents(new HistoryListener(historyManager), this);
    }

}
