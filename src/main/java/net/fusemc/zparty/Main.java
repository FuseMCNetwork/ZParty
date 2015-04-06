package net.fusemc.zparty;

import com.xxmicloxx.znetworklib.PacketRegistry;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zparty.commands.PartyCommand;
import net.fusemc.zparty.party.PartyPacket;
import net.fusemc.zparty.party.PartySystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

/**
 * Created by Marco on 27.07.2014.
 */
public class Main extends Plugin {

    private static Main instance;

    private PartySystem partySystem;

    public void onEnable(){
        instance = this;

        this.partySystem = new PartySystem();
        registerListener();
        registerCommands();
        PacketRegistry.registerPacket(PartyPacket.class, 237104987);
        ZNetworkPlugin.getInstance().registerEvent("party_packet_event", partySystem.getPacketListener());
    }

    private void registerListener(){
        Listener[] listeners = new Listener[]{
            this.partySystem.getPacketListener(),
            this.partySystem.getConnectionListener()
        };
        PluginManager pm = ProxyServer.getInstance().getPluginManager();

        for(Listener listener: listeners){
            pm.registerListener(this, listener);
        }
    }

    private void registerCommands(){
        Command[] commands = new Command[]{
            new PartyCommand()
        };
        PluginManager pm = ProxyServer.getInstance().getPluginManager();

        for(Command command: commands){
            pm.registerCommand(this, command);
        }
    }

    public static void sendPacket(PartyPacket partyPacket){
        ZNetworkPlugin.getInstance().sendEvent("party_packet_event", partyPacket);
    }

    public static Main getInstance(){
        return instance;
    }

    public PartySystem getPartySystem(){
        return partySystem;
    }
}
