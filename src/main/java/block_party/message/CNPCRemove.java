package block_party.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;

public class CNPCRemove extends CNPCQuery {
    protected boolean removed;

    public CNPCRemove(UUID id) {
        super(id);
    }

    public CNPCRemove(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        this.removed = this.npc.isDeadOrEstrangedFrom(player) && this.list.remove(this.npc.getID());
    }
}
