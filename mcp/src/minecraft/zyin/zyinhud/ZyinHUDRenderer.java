package zyin.zyinhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;
import zyin.zyinhud.gui.GuiOptionsOverride;
import zyin.zyinhud.helper.HUDEntityTrackerHelper;
import zyin.zyinhud.helper.RenderEntityTrackerHelper;
import zyin.zyinhud.mods.AnimalInfo;
import zyin.zyinhud.mods.DurabilityInfo;
import zyin.zyinhud.mods.InfoLine;
import zyin.zyinhud.mods.PotionTimers;
import zyin.zyinhud.mods.SafeOverlay;

/**
 * This class is in charge of rendering things onto the HUD and into the game world.
 */
public class ZyinHUDRenderer
{
	public static ZyinHUDRenderer instance = new ZyinHUDRenderer();
	private static Minecraft mc = Minecraft.getMinecraft();
	
	/**
	 * Event fired at various points during the GUI rendering process.
	 * We render anything that need to be rendered onto the HUD in this method.
	 * @param event
	 */
    @ForgeSubscribe
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event)
    {
    	/*
    	
    	//works:
    	 * TEXT
    	 * ALL
    	 * BOSSHEALTH
    	 * CROSSHAIRS
    	 * EXPERIENCE
    	 * HELMET
    	 * PORTAL
    	
    	//doesnt work:
    	 * AIR
    	 * ARMOR
    	 * FOOD
    	 * HEALTH
    	 * HEALTHMOUNT
    	 * HOTBAR
    	 * JUMPBAR
    	 
    	 */
    	
    	//render everything onto the screen
    	if(event.type == RenderGameOverlayEvent.ElementType.TEXT)
    	{
    		InfoLine.RenderOntoHUD();
            DurabilityInfo.RenderOntoHUD();
            PotionTimers.RenderOntoHUD();
            AnimalInfo.RenderOntoDebugMenu();
            HUDEntityTrackerHelper.RenderEntityInfo();	//calls other mods that need to render things on the HUD near entities
    	}
    	
    	
    	//change how the inventories are rendered (this has to be done on every game tick)
    	if (mc.currentScreen instanceof InventoryEffectRenderer)
    	{
    		PotionTimers.DisableInventoryPotionEffects((InventoryEffectRenderer)mc.currentScreen);
    	}
    }
    

    /**
     * Event fired when the world gets rendered.
     * We render anything that need to be rendered into the game world in this method.
     * @param event
     */
    @ForgeSubscribe
    public void RenderWorldLastEvent(RenderWorldLastEvent event)
    {
        //render unsafe positions (cache calculations are done from this render method)
        SafeOverlay.instance.RenderAllUnsafePositionsMultithreaded(event.partialTicks);
        
        //calls other mods that need to render things in the game world nearby other entities
        RenderEntityTrackerHelper.RenderEntityInfo(event.partialTicks);
    }
    
    
    /**
     * Event fired before a GUI is opened.
     * @param event
     */
    @ForgeSubscribe
    public void GuiOpenEvent(GuiOpenEvent event)
    {
    	//override the default Options screen with our custom one, which contains our custom button
    	if (event.gui instanceof GuiOptions && mc.theWorld != null)
        {
    		event.gui = new GuiOptionsOverride(new GuiIngameMenu(), mc.gameSettings);
        }
    }
    
}
