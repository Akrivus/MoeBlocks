package mod.moeblocks.init;

import mod.moeblocks.MoeMod;
import mod.moeblocks.message.OpenYearbookMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MoeMessages {
    private static final ArrayList<MessageContainer<?>> MESSAGES = new ArrayList<>();
    private static int currentMessageID = 1;

    static {
        MESSAGES.add(new MessageContainer<>(OpenYearbookMessage.class, OpenYearbookMessage::decode, OpenYearbookMessage::encode, OpenYearbookMessage::handle));
    }

    public static SimpleChannel register() {
        SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MoeMod.ID, "gui")).clientAcceptedVersions(version -> true).serverAcceptedVersions(version -> true).networkProtocolVersion(() -> MoeMod.VERSION).simpleChannel();
        MESSAGES.forEach(message -> channel.messageBuilder(message.getPacketClass(), message.getID()).decoder(message.getDecoder()).encoder(message.getEncoder()).consumer(message.getHandler()).add());
        return channel;
    }

    public static <MSG> void send(PlayerEntity player, MSG message) {
        if (player instanceof ServerPlayerEntity) {
            NetworkManager network = ((ServerPlayerEntity) player).connection.getNetworkManager();
            MoeMod.CHANNEL.sendTo(message, network, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static <MSG> void send(MSG message) {
        MoeMod.CHANNEL.sendToServer(message);
    }

    public static class MessageContainer<T> {
        private final Class packetClass;
        private final Function decoder;
        private final BiConsumer encoder;
        private final BiConsumer handler;
        private final int messageID;

        public <T> MessageContainer(Class packetClass, Function<PacketBuffer, T> decoder, BiConsumer<T, PacketBuffer> encoder, BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
            this.packetClass = packetClass;
            this.decoder = decoder;
            this.encoder = encoder;
            this.handler = handler;
            this.messageID = ++MoeMessages.currentMessageID;
        }

        public Function getDecoder() {
            return this.decoder;
        }

        public BiConsumer getEncoder() {
            return this.encoder;
        }

        public BiConsumer getHandler() {
            return this.handler;
        }

        public Class getPacketClass() {
            return this.packetClass;
        }

        public int getID() {
            return this.messageID;
        }
    }
}
