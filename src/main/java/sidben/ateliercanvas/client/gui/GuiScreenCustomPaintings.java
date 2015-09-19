package sidben.ateliercanvas.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Property;
import sidben.ateliercanvas.client.config.CustomPaintingConfigItem;
import sidben.ateliercanvas.client.config.PaintingSelectorListEntry;
import sidben.ateliercanvas.handler.ConfigurationHandler;
import sidben.ateliercanvas.reference.ColorTable;
import sidben.ateliercanvas.reference.Reference;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


/**
 * <p>
 * Screen where the player can see all custom paintings installed, see details of each painting and open the GUI to add a new painting.
 * </p>
 * 
 * <p>
 * A custom painting is considered 'installed' when the entry is added to the mod config file, so it's not enough to just add PNG files to the mod config folder. A player will need to use this GUI to notify the mod of the images and also provide more info, like painting name or author.
 * </p>
 * 
 * <p>
 * This GUI was created using the ResourcePack selector GUI as reference.
 * </p>
 * 
 * 
 * @see sidben.ateliercanvas.client.gui.GuiCustomPaintingList
 * @see sidben.ateliercanvas.client.config.PaintingSelectorListEntry
 * @see net.minecraft.client.gui.GuiScreenResourcePacks
 * @author sidben
 * 
 */
@SideOnly(Side.CLIENT)
public class GuiScreenCustomPaintings extends GuiScreen
{

    private static final int                BT_ID_DONE    = 1;
    private static final int                BT_ID_ADDNEW  = 2;

    public final GuiConfig                  parentScreen;
    public final boolean                    isWorldRunning;

    private List<PaintingSelectorListEntry> paintingList;
    private GuiCustomPaintingList           guiPaintingList;
    private GuiPaintingDetails              guiPaintingDetails;
    private int                             selectedIndex = -1;




    public GuiScreenCustomPaintings(GuiConfig parentScreen) {
        this.mc = Minecraft.getMinecraft();
        this.parentScreen = parentScreen;
        this.isWorldRunning = mc.theWorld != null;
    }



    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void initGui()
    {
        // Buttons
        this.buttonList.add(new GuiOptionButton(BT_ID_ADDNEW, this.width / 2 - 154, this.height - 48, StatCollector.translateToLocal(getLanguageKey("add_new"))));
        this.buttonList.add(new GuiOptionButton(BT_ID_DONE, this.width / 2 + 4, this.height - 48, StatCollector.translateToLocal("gui.done")));

        // Paintings data
        this.paintingList = new ArrayList();
        for (final CustomPaintingConfigItem item : ConfigurationHandler.mahPaintings) {
            this.paintingList.add(new PaintingSelectorListEntry(this, item));
        }

        // Paintings listbox
        this.guiPaintingList = new GuiCustomPaintingList(this.mc, 200, this.height, this.paintingList);
        this.guiPaintingList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.guiPaintingList.registerScrollButtons(7, 8);

        // Paintings details screen
        this.guiPaintingDetails = new GuiPaintingDetails(this, null);
    }


    /*
     * param3 = 0.3861351 (random? frame? varies from 0 to 1)
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float param3)
    {
        // Clear the background
        this.drawBackground(0);

        // Selected painting extra info
        if (this.selectedIndex > -1) {
            this.guiPaintingDetails.drawScreen(mouseX, mouseY, param3);
        }

        // Draws the listbox
        this.guiPaintingList.drawScreen(mouseX, mouseY, param3);

        // Texts - Title, Total paintings installed
        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal(getLanguageKey("title")), this.width / 2, 16, ColorTable.WHITE);
        this.drawCenteredString(this.fontRendererObj, String.format(StatCollector.translateToLocal(getLanguageKey("installed_counter")), this.paintingList.size()), this.width / 2, this.height - 20,
                ColorTable.GRAY);

        // Parent call (draws buttons)
        super.drawScreen(mouseX, mouseY, param3);


        // Tooltips (OBS: this must come after [super.drawScreen], or else the buttons will get a weird gray overlay
        if (!this.guiPaintingList.getTooltip().isEmpty()) {
            this.drawToolTip(this.mc.fontRenderer.listFormattedStringToWidth(this.guiPaintingList.getTooltip(), 300), mouseX, mouseY);
        } else if (!this.guiPaintingDetails.getTooltip().isEmpty()) {
            this.drawToolTip(this.mc.fontRenderer.listFormattedStringToWidth(this.guiPaintingDetails.getTooltip(), 300), mouseX, mouseY);
        }

    }



    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled) {

            if (button.id == BT_ID_DONE) {
                // TODO: save the config - saveConfigElements() - if possible, use a separate file (?)
                final String configID = ConfigurationHandler.CATEGORY_PAINTINGS;
                final boolean requiresMcRestart = false;

                // Fires the related event. I don't use this, but I'm following GuiConfig code. Maybe Forge needs this info)
                final ConfigChangedEvent event = new OnConfigChangedEvent(Reference.ModID, configID, isWorldRunning, requiresMcRestart);
                FMLCommonHandler.instance().bus().post(event);
                if (!event.getResult().equals(Result.DENY)) {
                    FMLCommonHandler.instance().bus().post(new PostConfigChangedEvent(Reference.ModID, configID, isWorldRunning, requiresMcRestart));
                }



                // Clear all content of the category
                ConfigurationHandler.config.getCategory(configID).clear();

                // Re-adds all the valid entries
                Property configProp;
                int c = 0;

                for (final PaintingSelectorListEntry item : this.paintingList) {
                    final String configKey = String.format("%s_%03d", ConfigurationHandler.PAINTINGS_ARRAY_KEY, c);

                    configProp = ConfigurationHandler.config.get(configID, configKey, new String[] {});
                    configProp.set(item._entryData.ToStringArray());

                    c++;
                }

                // Saves the config file
                ConfigurationHandler.config.save();     // TODO: Only save if there were changes (?)



                // Returns to the parent screen
                this.mc.displayGuiScreen(this.parentScreen);

            } else if (button.id == BT_ID_ADDNEW) {


            }

        }
    }



    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int clickType)
    {
        super.mouseClicked(mouseX, mouseY, clickType);
        this.guiPaintingList.func_148179_a(mouseX, mouseY, clickType);
    }


    /**
     * Called when the mouse is moved or a mouse button is released. Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which)
    {
        super.mouseMovedOrUp(mouseX, mouseY, which);
    }



    @SuppressWarnings("rawtypes")
    public void drawToolTip(List stringList, int x, int y)
    {
        this.parentScreen.drawToolTip(stringList, x, y);
    }



    public void displayDetails(int index)
    {
        this.selectedIndex = -1;

        if (index >= 0 && index < this.paintingList.size()) {
            final PaintingSelectorListEntry entry = this.paintingList.get(index);
            this.guiPaintingDetails.updateConfigItem(entry._entryData);
            this.selectedIndex = index;
        }
    }



    /**
     * Returns the full language key for elements of this GUI.
     */
    protected String getLanguageKey(String name)
    {
        return "sidben.ateliercanvas.config.painting_selector." + name;
    }

}