package moeblocks.message;

import moeblocks.client.screen.ControllerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.UUID;

public abstract class SOpenController extends SNPCList {
    protected final UUID uuid;
    protected final Hand hand;
    protected ItemStack stack;
    
    public SOpenController(List<UUID> npcs, UUID uuid, Hand hand) {
        super(npcs);
        this.uuid = uuid == null ? UUID.randomUUID() : uuid;
        this.hand = hand;
    }
    
    public SOpenController(PacketBuffer buffer) {
        super(buffer);
        this.uuid = buffer.readUniqueId();
        this.hand = buffer.readEnumValue(Hand.class);
    }
    
    @Override
    public void encode(PacketBuffer buffer) {
        super.encode(buffer);
        buffer.writeUniqueId(this.uuid);
        buffer.writeEnumValue(this.hand);
    }
    
    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.player.getHeldItem(this.hand).getItem() != this.getItem()) { return; }
        if (minecraft.currentScreen != null) { return; }
        minecraft.displayGuiScreen(this.getScreen());
    }
    
    protected abstract Item getItem();
    
    protected abstract ControllerScreen getScreen();
}
