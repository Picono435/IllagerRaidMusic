package com.gmail.picono435.illagerraidmusic;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IllagerRaidMusic.MODID)
public class IllagerRaidMusic {

    public static final String MODID = "illagerraidmusic";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final RegistryObject<SoundEvent> RAID_SOUND_EVENT = SOUND_EVENTS.register("raid_music", () -> new SoundEvent(new ResourceLocation(MODID, "raid_music")));
    public static Music RAID_MUSIC;

    public static boolean shouldPlayMusic;

    public IllagerRaidMusic() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        SOUND_EVENTS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(TickEvent.ClientTickEvent event) {
            if(RAID_MUSIC == null) {
                RAID_MUSIC = new Music(RAID_SOUND_EVENT.get(), 20, 40, true);
            }



            if(shouldPlayMusic) {
                if(Minecraft.getInstance().level == null) {
                    shouldPlayMusic = false;
                } else {
                    if(!Minecraft.getInstance().getMusicManager().isPlayingMusic(RAID_MUSIC)) {
                        Minecraft.getInstance().getMusicManager().stopPlaying();
                        Minecraft.getInstance().getMusicManager().startPlaying(IllagerRaidMusic.RAID_MUSIC);
                    }
                }
            } else {
                if(Minecraft.getInstance().getMusicManager().isPlayingMusic(RAID_MUSIC)) {
                    Minecraft.getInstance().getMusicManager().stopPlaying();
                }
            }
        }
    }
}
