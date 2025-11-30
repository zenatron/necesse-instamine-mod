package instamine;

import instamine.commands.InstamineToggleCommand;
import instamine.features.FeatureRegistry;
import necesse.engine.commands.CommandsManager;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.Server;

@ModEntry
public class InstamineMod {

    public void initResources() {
        System.out.println("[Zen Instamine] Resources initialized");
    }

    public void postInit() {
        FeatureRegistry.initialize();
        CommandsManager.registerServerCommand(new InstamineToggleCommand());
        CommandsManager.registerClientCommand(new InstamineToggleCommand());
    }

    public static void broadcast(Server server, String message) {
        if (server != null && message != null && !message.isEmpty()) {
            server.network.sendToAllClients(new PacketChatMessage("[Instamine] " + message));
        }
    }
}