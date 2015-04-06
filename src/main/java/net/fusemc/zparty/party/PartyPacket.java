package net.fusemc.zparty.party;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;
import net.fusemc.zparty.Main;

import java.io.Serializable;

/**
 * Created by Marco on 27.07.2014.
 */
public class PartyPacket implements NetworkEvent, Serializable{

    private static final long serialVersionUID = 192834790182738490L;

    private String leader;
    private String player;
    private Action action;

    public PartyPacket(){

    }

    public PartyPacket(String leader, String player, Action action){
        this.leader = leader;
        this.player = player;
        this.action = action;
    }

    @Override
    public CodecResult write(PacketWriter packetWriter) {
        packetWriter.writeString(this.leader);
        packetWriter.writeString(this.player);
        packetWriter.writeInt(action.getId());
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader packetReader) {
        this.leader = packetReader.readString();
        this.player = packetReader.readString();
        this.action = Action.fromId(packetReader.readInt());
        return CodecResult.OK;
    }

    public void performAction(){
        this.action.perform(this.leader, this.player);
    }

    public enum Action {

        PARTY_CREATE(0){
            @Override
            public void perform(String leader, String type) {
                Main.getInstance().getPartySystem().newParty(leader, type);
            }
        },
        PARTY_DESTROY(1){
            @Override
            public void perform(String leader, String player) {
                Main.getInstance().getPartySystem().removeParty(leader);
            }
        },
        MEMBER_ADD(2){
            @Override
            public void perform(String leader, String player) {
                Main.getInstance().getPartySystem().getParty(leader).addMember(player);
            }
        },
        MEMBER_QUIT(3){
            @Override
            public void perform(String leader, String player) {
                Main.getInstance().getPartySystem().getParty(leader).removeMember(player);
            }
        },
        LEADER_UPDATE(4){
            @Override
            public void perform(String leader, String player) {
                Main.getInstance().getPartySystem().getParty(leader).setLeader(player);
            }
        },
        MEMBER_REQUEST(5){
            @Override
            public void perform(String leader, String player) {
                Main.getInstance().getPartySystem().getParty(leader).addRequest(player);
            }
        },
        MEMBER_KICK(6){
            @Override
            public void perform(String leader, String player) {
                Main.getInstance().getPartySystem().getParty(leader).kickMember(player);
            }
        },
        CHAT(7){
            @Override
            public void perform(String leader, String message) {
                Main.getInstance().getPartySystem().getParty(leader).sendMessage(message);
            }
        },
        MOVE_PARTY(8){
            @Override
            public void perform(String leader, String server) {
                Main.getInstance().getPartySystem().getParty(leader).setTargetServer(server);
            }
        };

        public int id;

        Action(int id) {
            this.id = id;
        }

        public static Action fromId(int id){
            for(Action action: Action.values()){
                if(action.getId() == id){
                    return action;
                }
            }
            return null;
        }

        public int getId(){
            return id;
        }

        public abstract void perform(String leader, String player);
    }
}
