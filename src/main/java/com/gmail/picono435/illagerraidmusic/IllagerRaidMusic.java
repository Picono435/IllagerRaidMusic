package com.gmail.picono435.illagerraidmusic;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IllagerRaidMusic.MODID)
public class IllagerRaidMusic {

    public static final String MODID = "illagerraidmusic";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static final RegistryObject<SoundEvent> RAID_SOUND_EVENT = SOUND_EVENTS.register("raid_music", IllagerRaidMusic::getSoundEvent);
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
                try {
                    Constructor<Music> constructor = Music.class.getConstructor(SoundEvent.class, int.class, int.class, boolean.class);
                    RAID_MUSIC = constructor.newInstance(RAID_SOUND_EVENT.get(), 20, 40, true);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException exception) {
                    RAID_MUSIC = new Music(RAID_SOUND_EVENT.getHolder().get(), 20, 40, true);
                }
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

    private static SoundEvent getSoundEvent() {
        ResourceLocation resourceLocation = new ResourceLocation(MODID, "raid_music");
        try {
            // 1.19.2
            Constructor<SoundEvent> constructor = SoundEvent.class.getConstructor(ResourceLocation.class);
            return constructor.newInstance(resourceLocation);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ex) {
            return SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "raid_music"));
        }
    }
}
