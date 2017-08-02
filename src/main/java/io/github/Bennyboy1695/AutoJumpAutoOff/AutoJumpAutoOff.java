package io.github.Bennyboy1695.AutoJumpAutoOff;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "autojumpautooff", name = "AutoJumpAutoOff", version = "1.0.0", clientSideOnly = true, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.10.2,1.12]")
public class AutoJumpAutoOff {

    static boolean notified;
    static final String prefix = TextFormatting.WHITE + "[" + TextFormatting.BLUE + "AutoJumpAutoOff" + TextFormatting.WHITE + "] ";
    static final String logprefix = "[AutoJumpAutoOff] ";
    static boolean soundEnabled = true;
    static boolean messageEnabled = true;


    static {
        AutoJumpAutoOff.notified = false;
    }

    final SoundEvent sound = (SoundEvent) SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.experience_orb.pickup"));


    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());

        soundEnabled = config.getBoolean("soundEnabled", "General", soundEnabled, "Disables or Enables the sound when the AutoJump gets turned off!");
        messageEnabled = config.getBoolean("messageEnabled", "General", messageEnabled, "Disables or Enables the message sent to the player when AutoJump gets turned off!");

        if (config.hasChanged()) {
            config.save();
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onServerJoin(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        if (Minecraft.getMinecraft().player != null && (Minecraft.getMinecraft().player.ticksExisted > 20 || Minecraft.getMinecraft().isGamePaused())) {
            GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
            boolean jump = gameSettings.autoJump;
            if (jump && !AutoJumpAutoOff.notified ) {
                gameSettings.setOptionValue(GameSettings.Options.AUTO_JUMP, 0);
                gameSettings.saveOptions();
                AutoJumpAutoOff.notified = true;
                if (soundEnabled) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1));
                }
                FMLLog.info(logprefix + "Set AutoJump to false!");
                if (messageEnabled) {
                    FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(Minecraft.getMinecraft().player.getUniqueID()).sendMessage(new TextComponentString(prefix + TextFormatting.GREEN + "AutoJump has been turned off!"));
                }
            } else if (!AutoJumpAutoOff.notified){
                FMLLog.info(logprefix + "This setting is already false!");
                AutoJumpAutoOff.notified = true;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null && (Minecraft.getMinecraft().player.ticksExisted > 20 || Minecraft.getMinecraft().isGamePaused())) {
            GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
            boolean jump = gameSettings.autoJump;
            if (jump && !AutoJumpAutoOff.notified) {
                gameSettings.setOptionValue(GameSettings.Options.AUTO_JUMP, 0);
                gameSettings.saveOptions();
                AutoJumpAutoOff.notified = true;
                if (soundEnabled) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1));}
                FMLLog.info(logprefix + "Set AutoJump to false!");
                if (messageEnabled) {
                FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(Minecraft.getMinecraft().player.getUniqueID()).sendMessage(new TextComponentString(prefix + TextFormatting.GREEN +"AutoJump has been turned off!"));}
            } else {
                if (!AutoJumpAutoOff.notified) {
                    FMLLog.info(logprefix + "This setting is already false!");
                    AutoJumpAutoOff.notified = true;
                }
            }
        }
    }
}
