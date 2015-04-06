package net.fusemc.zparty.party;

import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworkplugin.EventListener;
import net.fusemc.zparty.Main;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Marco on 27.07.2014.
 */
public class PartyPacketListener implements EventListener, Listener{

    @Override
    public void onEventReceived(String event, String sender, NetworkEvent networkEvent) {
        if(!(networkEvent instanceof PartyPacket)){
            return;
        }
        PartyPacket packet = (PartyPacket) networkEvent;
        packet.performAction();
    }

    @EventHandler
    public void onChat(ChatEvent event){
        if(!event.getMessage().startsWith("#")){
            return;
        }
        if(!(event.getSender() instanceof ProxiedPlayer)){
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        event.setCancelled(true);
        String message = "<\u00A73" + player.getName() + "\u00A7f> \u00A7e" + event.getMessage().replaceFirst("#", "");
        Party party = Main.getInstance().getPartySystem().getPartyMember(player.getName());
        if(party == null){
            player.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu bist in keiner Party!"));
            return;
        }
        Main.sendPacket(new PartyPacket(party.getLeader(), message, PartyPacket.Action.CHAT));
    }
}
