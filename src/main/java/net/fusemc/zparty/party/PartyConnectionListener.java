package net.fusemc.zparty.party;

import net.fusemc.zparty.Main;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Marco on 28.07.2014.
 */
public class PartyConnectionListener implements Listener{

    @EventHandler
    public void onConnect(ServerConnectEvent event){
        if(event.getTarget().getName().startsWith("LOBBY")){
            return;
        }
        Party party = Main.getInstance().getPartySystem().getPartyMember(event.getPlayer().getName());
        if(party == null){
            return;
        }
        if(event.getTarget().getName().equals(party.getTargetServer())){
            return;
        }
        if(!party.getLeader().equals(event.getPlayer().getName())){
            event.setCancelled(true);
            event.getPlayer().sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7e\u30b7\u00A78\u00A7l] \u00A7eDu kannst nur als Partyleader einem Spiel beitreten!"));
        } else {
            Main.getInstance().getPartySystem().moveParty(party, event.getTarget().getName());
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event){
        Party party = Main.getInstance().getPartySystem().getPartyMember(event.getPlayer().getName());
        if(party == null){
            return;
        }
        if(party.getLeader().equals(event.getPlayer().getName())){
            if(party.getMembers().size() != 0){
                Main.sendPacket(new PartyPacket(party.getLeader(), party.getMembers().get(0), PartyPacket.Action.LEADER_UPDATE));
            } else {
                Main.sendPacket(new PartyPacket(party.getLeader(), "", PartyPacket.Action.PARTY_DESTROY));
            }
        } else {
            Main.sendPacket(new PartyPacket(party.getLeader(), event.getPlayer().getName(), PartyPacket.Action.MEMBER_QUIT));
        }
    }
}
