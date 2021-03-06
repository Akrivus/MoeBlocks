package block_party.message;

import block_party.convo.enums.Response;
import block_party.mob.Partyer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;

public class CDialogueRespond extends CNPCQuery {
    private final Response response;

    public CDialogueRespond(UUID id, Response response) {
        super(id);
        this.response = response;
    }

    public CDialogueRespond(FriendlyByteBuf buffer) {
        super(buffer);
        this.response = buffer.readEnum(Response.class);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeEnum(this.response);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        Partyer npc = this.npc.getServerEntity(player.getServer());
        if (npc != null) {
            //npc.setScene(this.response);
        }
    }
}
