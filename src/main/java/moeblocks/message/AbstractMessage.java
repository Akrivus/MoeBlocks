package moeblocks.message;

import moeblocks.init.MoeMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.awt.image.DirectColorModel;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractMessage {
    public AbstractMessage(PacketBuffer buffer) { }
    
    public AbstractMessage() { }
    
    public abstract void encode(PacketBuffer buffer);
    
    public abstract void handle(NetworkEvent.Context context, ServerPlayerEntity player);
    
    public abstract void handle(NetworkEvent.Context context, Minecraft minecraft);
    
    public static void prepare(AbstractMessage message, PacketBuffer buffer) {
        message.encode(buffer);
    }
    
    public static void consume(AbstractMessage message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        NetworkDirection direction = context.getDirection();
        if (direction == NetworkDirection.PLAY_TO_CLIENT)
            context.enqueueWork(() -> message.handle(context, Minecraft.getInstance()));
        if (direction == NetworkDirection.PLAY_TO_SERVER)
            context.enqueueWork(() -> message.handle(context, context.getSender()));
        context.setPacketHandled(true);
    }
}
