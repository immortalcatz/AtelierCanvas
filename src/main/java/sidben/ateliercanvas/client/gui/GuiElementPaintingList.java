package sidben.ateliercanvas.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



/**
 * This class is the ListBox that displays each installed painting, with a little
 * icon, name and extra info, similar to Recourse Packs.
 * 
 * 
 * @see sidben.ateliercanvas.client.gui.GuiElementPaintingListEntry
 * @see net.minecraft.client.gui.GuiResourcePackList
 * @author sidben
 * 
 */
@SideOnly(Side.CLIENT)
public class GuiElementPaintingList extends GuiListExtended
{

    protected final Minecraft mc;

    @SuppressWarnings("rawtypes")
    protected final List      valueList;

    private final GuiScreen   _parentScreen;



    @SuppressWarnings("rawtypes")
    public GuiElementPaintingList(Minecraft minecraft, int width, int height, List list, GuiScreen parentScreen) {
        super(minecraft, width, height, 32, height - 55, 36);
        this.mc = minecraft;
        this.valueList = list;
        this.field_148163_i = false;        // ? - maybe related to scrolling
        this._parentScreen = parentScreen;
    }


    /**
     * Mouse clicked.
     */
    @Override
    public boolean func_148179_a(int mouseX, int mouseY, int mouseEvent)
    {
        if (this.func_148141_e(mouseY)) {
            final int index = this.func_148124_c(mouseX, mouseY);

            if (index >= 0) {
                final int i1 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                final int j1 = this.top + 4 - this.getAmountScrolled() + index * this.slotHeight + this.headerPadding;
                final int relativeX = mouseX - i1;
                final int relativeY = mouseY - j1;

                // Check if the selected item is marked for removal. In that case, no callback is called
                final boolean markedToRemove = this.getListEntry(index).removed();

                if (!markedToRemove) {

                    // Custom callback
                    this._parentScreen.confirmClicked(true, index);

                    if (this.getListEntry(index).mousePressed(index, mouseX, mouseY, mouseEvent, relativeX, relativeY)) {
                        this.func_148143_b(false);
                        return true;
                    }

                } // (!markedToRemove)

                else {
                    this._parentScreen.confirmClicked(true, -1);
                }

            } // (index >= 0)
        }

        return false;
    }



    @SuppressWarnings("rawtypes")
    public List func_148201_l()
    {
        return this.valueList;
    }

    @Override
    protected int getSize()
    {
        return this.func_148201_l().size();
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    @Override
    public GuiElementPaintingListEntry getListEntry(int p_148180_1_)
    {
        return (GuiElementPaintingListEntry) this.func_148201_l().get(p_148180_1_);
    }

    /**
     * Gets the width of the list
     */
    @Override
    public int getListWidth()
    {
        return this.width;
    }

    @Override
    protected int getScrollBarX()
    {
        return this.right - 6;
    }



    /**
     * Tooltip for the list item under the mouse.
     */
    public String getTooltip()
    {
        // Index of the list item below the mouse cursor.
        final int indexMouseHovering = this.func_148124_c(mouseX, mouseY);

        // If the mouse is above an item, return the tooltip of that item.
        if (indexMouseHovering >= 0 && indexMouseHovering < this.valueList.size()) {
            return this.getListEntry(indexMouseHovering).getTooltip();
        } else {
            return "";
        }
    }

}
