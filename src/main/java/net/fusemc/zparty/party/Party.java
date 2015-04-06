package net.fusemc.zparty.party;

import net.fusemc.zbungeecontrol.rank.Rank;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;

/**
 * Created by Marco on 27.07.2014.
 */
public class Party {

    private Type type;
    private String leader;
    private String targetServer;
    private ArrayList<String> members;
    private ArrayList<String> requests;

    public Party(String leader, String type){
        this.leader = leader;
        this.members = new ArrayList<String>();
        this.requests = new ArrayList<String>();

        this.type = Type.fromName(type);
    }

    public String getTargetServer(){
        return targetServer;
    }

    public void setTargetServer(String targetServer){
        this.targetServer = targetServer;
        for(String other: members){
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(other);
            if(p == null){
                continue;
            }
            p.connect(ProxyServer.getInstance().getServerInfo(targetServer));
        }
    }

    public String getLeader(){
        return leader;
    }

    public boolean isFull(){
        return this.getSize() >= this.type.getMaxSize();
    }

    public String[] getAllPlayers(){
        String[] result = new String[this.getSize()];

        result[0] = leader;
        for(int i = 0; i < members.size(); i++){
            result[i + 1] = members.get(i);
        }

        return result;
    }

    public ArrayList<String> getMembers(){
        return members;
    }

    public int getSize(){
        return this.members.size() + 1;
    }

    public int getNoneVipCount(){
        int i = 0;
        for(String string: this.members){
            if(!Rank.isTeam(string)){
                i++;
            }
        }
        if(!Rank.isTeam(this.leader)){
            i++;
        }
        return i;
    }

    public void setLeader(String leader){
        this.leader = leader;
        members.remove(leader);
        for(String other: getAllPlayers()){
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(other);
            if(p == null){
                continue;
            }
            p.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDer Leader hat die Party verlassen. Neuer Leader ist \u00A73" + leader +"\u00A7e!"));
        }
    }

    public void addMember(String member){
        if(isFull()){
            return;
        }
        if(members.contains(member)){
            return;
        }
        members.add(member);
        for(String other: getAllPlayers()){
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(other);
            if(p == null){
                continue;
            }
            p.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A73" + member + " \u00A7eist der Party beigetreten."));
        }
    }

    public void addRequest(String player){
        if(!this.requests.contains(player)){
            this.requests.add(player);
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);
            if(p == null){
                return;
            }
            p.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu wurdest in die Party von \u00A73" + leader + "\u00A7e eingeladen! Acceptiere mit \u00A73/party accept " + leader + "\u00A7e!"));
        }
    }

    public boolean hasRequest(String player){
        for(String request: requests){
            if(request.equalsIgnoreCase(player)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getRequests(){
        return requests;
    }

    public void removeMember(String member){
        if(members.contains(member)){
            members.remove(member);
            for(String other: getAllPlayers()){
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(other);
                if(p == null){
                    continue;
                }
                p.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A73" + member + " \u00A7ehat die Party verlassen."));
            }
        }
    }

    public void kickMember(String member){
        if(members.contains(member)){
            members.remove(member);
            ProxiedPlayer kick = ProxyServer.getInstance().getPlayer(member);
            if(kick != null){
                kick.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu wurdest aus der Party geworfen!"));
            }
            for(String other: getAllPlayers()){
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(other);
                if(p == null){
                    continue;
                }
                p.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A73" + member + " \u00A7ewurde aus der Party geworfen."));
            }
        }
    }

    public void sendMessage(String message){
        for(String other: getAllPlayers()){
            ProxiedPlayer p = ProxyServer.getInstance().getPlayer(other);
            if(p == null){
                continue;
            }
            p.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7e\u30b7\u00A78\u00A7l]\u00A7f " + message));
        }
    }

    public enum Type {

        NORMAL(3),
        VIP(7);

        private int maxSize;

        Type(int maxSize){
            this.maxSize = maxSize;
        }

        public static Type fromName(String name){
            for(Type type: Type.values()){
                if(type.name().equals(name)){
                    return type;
                }
            }
            return null;
        }

        public int getMaxSize() {
            return maxSize;
        }
    }
}
