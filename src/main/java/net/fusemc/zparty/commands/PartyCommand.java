package net.fusemc.zparty.commands;

import net.fusemc.zbungeecontrol.rank.Rank;
import net.fusemc.zbungeecontrol.server.PlayerData;
import net.fusemc.zparty.DEF;
import net.fusemc.zparty.Main;
import net.fusemc.zparty.party.Party;
import net.fusemc.zparty.party.PartyPacket;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;

/**
 * Created by Marco on 28.07.2014.
 */
public class PartyCommand extends Command implements TabExecutor{

    private static final String[] commands = {
        "create", "leave", "info",
        "help", "invite", "accept",
        "join", "quit", "kick",
        "?", "list"
    };

    private static final String[] lobbyCommands = {
        "create", "invite", "accept",
        "join"
    };

    public PartyCommand() {
        super("party");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(!(commandSender instanceof ProxiedPlayer)){
            commandSender.sendMessage(TextComponent.fromLegacyText("Nicht von der Console ausf\u00FChrbar"));
            return;
        }

        if(args.length != 0) {
            if(isLobbyCommand(args[0])){
                if(!((ProxiedPlayer) commandSender).getServer().getInfo().getName().startsWith("LOBBY")){
                    sendMessage(commandSender, "\u00a78\u00a7l[\u00a7e\u30b7\u00a78\u00a7l] \u00a7eDieses Kommando kannst du nur in der Lobby ausführen!");
                    return;
                }
            }
        }

        if(args.length == 0){
            sendMessage(commandSender, DEF.HELP);
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("create")){
                if(Main.getInstance().getPartySystem().getPartyMember(commandSender.getName()) == null){
                    Main.sendPacket(new PartyPacket(commandSender.getName(), Rank.isTeam(commandSender.getName()) ? Party.Type.VIP.name() : Party.Type.NORMAL.name(), PartyPacket.Action.PARTY_CREATE));
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eParty erstellt! \u00A73/party invite <name>\u00A7e läd einen Spieler zu deiner Party ein.");
                } else {
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu bist bereits in einer Party!");
                }
            } else if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("quit")){
                Party party = Main.getInstance().getPartySystem().getPartyMember(commandSender.getName());
                if(party == null){
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu bist in keiner Party!");
                } else {
                    if(party.getLeader().equals(commandSender.getName())){
                        if(party.getMembers().size() == 0){
                            Main.sendPacket(new PartyPacket(commandSender.getName(), "", PartyPacket.Action.PARTY_DESTROY));
                        } else {
                            Main.sendPacket(new PartyPacket(commandSender.getName(), party.getMembers().get(0), PartyPacket.Action.LEADER_UPDATE));
                        }
                    } else {
                        Main.sendPacket(new PartyPacket(party.getLeader(), commandSender.getName(), PartyPacket.Action.MEMBER_QUIT));
                    }
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu hast die Party verlassen.");
                }
            } else if(args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("list")){
                Party party = Main.getInstance().getPartySystem().getPartyMember(commandSender.getName());
                if(party == null) {
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu bist in keiner Party!");
                } else {
                    sendPartyInfo(commandSender, party);
                }
            } else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")){
                sendMessage(commandSender, DEF.HELP);
            }
        } else if(args.length == 2){
            if(args[0].equalsIgnoreCase("invite")){
                Party party = Main.getInstance().getPartySystem().getPartyMember(commandSender.getName());
                if(party == null){
                    Main.sendPacket(new PartyPacket(commandSender.getName(), Rank.isTeam(commandSender.getName()) ? Party.Type.VIP.name() : Party.Type.NORMAL.name(), PartyPacket.Action.PARTY_CREATE));
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu hast eine Party erstellt!");
                    return;
                }
                if(!party.getLeader().equals(commandSender.getName())) {
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDiesen Command kann nur ein Partyleader!");
                    return;
                }
                if(commandSender.getName().equalsIgnoreCase(args[1])){
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00a7eDu kannst dich nicht selbst einladen!");
                    return;
                }
                PlayerData data = new PlayerData(commandSender.getName());
                if(!data.exists() || !data.isOnline()){
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A73" + args[1] + "\u00a7e ist nicht online!");
                    return;
                }
                Main.sendPacket(new PartyPacket(commandSender.getName(), args[1], PartyPacket.Action.MEMBER_REQUEST));
                sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu hast \u00A73" + args[1] + "\u00A7e in die Party eingeladen.");
            } else if(args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("accept")){
                Party party = Main.getInstance().getPartySystem().getParty(args[1]);
                if(Main.getInstance().getPartySystem().inParty(commandSender.getName())){
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDu bist bereits in einer Party.");
                } else {
                    if(party == null){
                        sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7e\u00A73" + args[1] + "\u00A7e hat dich nicht in seine Party eingeladen.");
                    } else {
                        if(!party.hasRequest(commandSender.getName())){
                            sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7e\u00A73" + party.getLeader() + "\u00A7e hat dich nicht in seine Party eingeladen.");
                        } else {
                            if(party.isFull()){
                                sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDie Party ist leider voll!");
                            } else {
                                Main.sendPacket(new PartyPacket(party.getLeader(), commandSender.getName(), PartyPacket.Action.MEMBER_ADD));
                            }
                        }
                    }
                }
            } else if(args[0].equalsIgnoreCase("kick")){
                Party party = Main.getInstance().getPartySystem().getParty(commandSender.getName());
                if(party == null){
                    sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7eDiesen Command kann nur ein Partyleader!");
                } else {
                    String m = null;
                    for(String member: party.getMembers()){
                        if(member.equalsIgnoreCase(args[1])){
                            m = member;
                            break;
                        }
                    }
                    if(m == null){
                        sendMessage(commandSender, "\u00A78\u00A7l[\u00A7e\u30B7\u00A78\u00A7l] \u00A7e\u00A73" + args[1] + "\u00A7e ist nicht in deiner Party.");
                    } else {
                        Main.sendPacket(new PartyPacket(party.getLeader(), m, PartyPacket.Action.MEMBER_KICK));
                    }
                }
            }
        } else {
            sendMessage(commandSender, DEF.HELP);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        ArrayList<String> result = new ArrayList<>();

        if(args.length != 0) {
            if(isLobbyCommand(args[0])){
                if(!((ProxiedPlayer) commandSender).getServer().getInfo().getName().startsWith("LOBBY")){
                    return result;
                }
            }
        }

        if(args.length == 0 || !isValidCommand(args[0])){
            String begin = "";
            if(args.length == 1){
                begin = args[0];
            }
            for(int i = 0; i < commands.length; i++){
                String command = commands[i];
                if(command.startsWith(begin.toLowerCase())){
                    result.add(command);
                }
            }
            return result;
        }
        /*
        else if(args.length == 1 || args.length == 2){
            if(args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("join")){
                String begin = "";
                if(args.length == 2) {
                    begin = args[1];
                }
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.getName().toLowerCase().startsWith(begin.toLowerCase()))
                        result.add(player.getName());
                }
            } else if(args[0].equalsIgnoreCase("kick")){
                String begin = "";
                if(args.length == 2) {
                    begin = args[1];
                }
                Party party = Main.getInstance().getPartySystem().getPartyMember(commandSender.getName());
                if(party != null){
                    for(String member: party.getMembers()){
                        if(member.toLowerCase().startsWith(begin.toLowerCase())){
                            result.add(member);
                        }
                    }
                }
            }
        }
        */
        return null;
    }

    public boolean isLobbyCommand(String command) {
        for(int i = 0; i < lobbyCommands.length; i++){
            if(lobbyCommands[i].equalsIgnoreCase(command)){
                return true;
            }
        }
        return false;
    }

    private static boolean isValidCommand(String arg){
        for(int i = 0; i < commands.length; i++){
            if(commands[i].equalsIgnoreCase(arg)){
                return true;
            }
        }
        return false;
    }

    public void sendPartyInfo(CommandSender commandSender, Party party){
        sendMessage(commandSender, "\u00A77\u2554 \u00A7ePartybesitzer: \u00A73" + party.getLeader());
        sendMessage(commandSender, "\u00A77\u255A \u00A7eMitglieder: " + getMembersAsString(party));
    }

    private String getMembersAsString(Party party){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < party.getMembers().size(); i++){
            if(i != 0){
                builder.append(", ");
            }
            builder.append(party.getMembers().get(i));
        }
        return builder.toString();
    }

    public void sendMessage(CommandSender sender, String ... messages) {
        for (String message : messages) {
            sender.sendMessage(TextComponent.fromLegacyText(message));
        }
    }
}
