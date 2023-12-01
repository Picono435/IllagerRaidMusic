package com.gmail.picono435.illagerraidmusic.mixin.client;

import com.gmail.picono435.illagerraidmusic.IllagerRaidMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleBossUpdate", at = @At("HEAD"))
    public void handleBossUpdate(ClientboundBossEventPacket packet, CallbackInfo ci) {
        if(packet.operation.getType() == ClientboundBossEventPacket.OperationType.ADD && packet.operation instanceof ClientboundBossEventPacket.AddOperation) {
            ClientboundBossEventPacket.AddOperation addOperation = (ClientboundBossEventPacket.AddOperation) packet.operation;
            if(addOperation.name.getString().equals(Raid.RAID_NAME_COMPONENT.getString())) {
                Minecraft.getInstance().getMusicManager().stopPlaying();
                Minecraft.getInstance().getMusicManager().startPlaying(IllagerRaidMusic.RAID_MUSIC);
                IllagerRaidMusic.shouldPlayMusic = true;
            }
        } else if(packet.operation.getType() == ClientboundBossEventPacket.OperationType.REMOVE) {
            if(Minecraft.getInstance().gui.getBossOverlay().events.get(packet.id).getName().getString().startsWith(Raid.RAID_NAME_COMPONENT.getString())) {
                if(Minecraft.getInstance().getMusicManager().isPlayingMusic(IllagerRaidMusic.RAID_MUSIC)) {
                    IllagerRaidMusic.shouldPlayMusic = false;
                    Minecraft.getInstance().getMusicManager().stopPlaying();
                }
            }
        } else if(packet.operation.getType() == ClientboundBossEventPacket.OperationType.UPDATE_NAME) {
            ClientboundBossEventPacket.UpdateNameOperation updateOperation = (ClientboundBossEventPacket.UpdateNameOperation) packet.operation;
            if(updateOperation.name.getString().startsWith(Raid.RAID_NAME_COMPONENT.getString() + " - ")) {
                if(Minecraft.getInstance().getMusicManager().isPlayingMusic(IllagerRaidMusic.RAID_MUSIC)) {
                    IllagerRaidMusic.shouldPlayMusic = false;
                    Minecraft.getInstance().getMusicManager().stopPlaying();
                }
            }
        }
    }
}
