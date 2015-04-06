package net.fusemc.zparty.party;

import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.server.ServerData;

import java.util.ArrayList;

/**
 * Created by Marco on 27.07.2014.
 */
public class PartySystem {

    private final ArrayList<Party> parties = new ArrayList<>();

    private final PartyPacketListener listener;
    private final PartyConnectionListener connectionListener;

    public PartySystem(){
        listener = new PartyPacketListener();
        connectionListener = new PartyConnectionListener();
    }

    public void newParty(String leader, String type){
        parties.add(new Party(leader, type));
    }

    public void removeParty(String leader){
        parties.remove(getParty(leader));
    }

    public Party getParty(String leader){
        for(Party party: parties){
            if(party.getLeader().equalsIgnoreCase(leader)){
                return party;
            }
        }
        return null;
    }

    public Party getPartyMember(String member){
        Party party = getParty(member);
        if(party != null){
            return party;
        }
        for(Party par: parties){
            if(par.getMembers().contains(member)){
                return par;
            }
        }
        return null;
    }

    public boolean inParty(String player){
        return getPartyMember(player) != null;
    }

    public boolean moveParty(Party party, String server){
        ServerData stats = new ServerData(server);

        int max = stats.getMaxPlayers();
        int current = stats.getCurrentPlayers();
        int vip = max / 12 * 2;

        if(party.getSize() > max - current){
            //not enough space
            return false;
        }

        if(party.getNoneVipCount() > max - current - vip){
            //too much none vip
            return false;
        }

        //connect to server
        ZNetworkPlugin.getInstance().sendEvent("party_packet_event", new PartyPacket(party.getLeader(), server, PartyPacket.Action.MOVE_PARTY));
        return true;
    }

    public PartyPacketListener getPacketListener(){
        return listener;
    }

    public PartyConnectionListener getConnectionListener(){
        return connectionListener;
    }
}
