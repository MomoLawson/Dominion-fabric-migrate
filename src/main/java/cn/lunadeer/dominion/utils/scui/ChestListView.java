package cn.lunadeer.dominion.utils.scui;

import cn.lunadeer.dominion.utils.scui.configuration.ListViewConfiguration;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Paginated chest list view extending {@link ChestView}.
 * <p>
 * Ported from Bukkit to Fabric. Adds pagination with previous/next buttons.
 * The page size is calculated from the number of item symbol occurrences in the layout.
 */
public class ChestListView extends ChestView {

    public ChestListView(ServerPlayer viewOwner) {
        super(viewOwner);
    }

    private int pageSize = 45;
    private int currentPage = 1;
    private char itemSymbol = 'i';
    private boolean layoutSet = false;
    private ListViewConfiguration configCopy;
    private final List<ChestButton> items = new ArrayList<>();

    /**
     * Applies a list view configuration, setting up the layout and pagination.
     *
     * @param config      the list view configuration
     * @param currentPage the page number to display (1-based)
     * @return this instance for method chaining
     * @throws IllegalArgumentException if the page number is < 1, or the layout
     *                                  is missing required symbols
     */
    public ChestListView applyListConfiguration(ListViewConfiguration config, int currentPage) {
        this.configCopy = config;
        if (currentPage < 1) {
            throw new IllegalArgumentException("Page number must be greater than 0.");
        }
        this.currentPage = currentPage;
        // Validate the layout and symbols
        super.setLayout(config.layout);
        this.itemSymbol = config.itemSymbol.charAt(0);
        if (!this.getLayout().contains(String.valueOf(itemSymbol))) {
            throw new IllegalArgumentException("Layout must contain the item symbol: " + itemSymbol);
        }
        if (!this.getLayout().contains(String.valueOf(config.previewButton.getSymbol()))) {
            throw new IllegalArgumentException("Layout must contain the preview button symbol: " + config.previewButton.getSymbol());
        }
        if (!this.getLayout().contains(String.valueOf(config.nextButton.getSymbol()))) {
            throw new IllegalArgumentException("Layout must contain the next button symbol: " + config.nextButton.getSymbol());
        }
        this.pageSize = (int) this.getLayout().chars().filter(ch -> ch == itemSymbol).count();
        this.layoutSet = true;
        return this;
    }

    /**
     * Adds an item to the list view.
     *
     * @param item the ChestButton to add as a list item
     * @return this instance for method chaining
     */
    public ChestListView addItem(ChestButton item) {
        items.add(item);
        return this;
    }

    /**
     * Removes all current item buttons from the layout (those at item symbol positions).
     */
    public void clearCurrentItemButtons() {
        for (int i = 0; i < this.getLayout().length(); i++) {
            if (this.getLayout().charAt(i) == itemSymbol) {
                this.removeButton(i);
            }
        }
    }

    /**
     * Opens the list view, placing items for the current page and adding
     * pagination buttons (previous/next).
     */
    @Override
    public void open() {
        if (!layoutSet) {
            throw new IllegalStateException("List layout must be set before opening the view.");
        }
        this.clearCurrentItemButtons();
        int itemSymbolPosition = -1;
        // Place items for the current page
        for (int idx = 0; idx < items.size(); idx++) {
            if (items.get(idx).getSymbol() != itemSymbol) continue;
            if (idx < (currentPage - 1) * pageSize) {
                continue;
            }
            if (idx >= currentPage * pageSize) {
                break;
            }
            itemSymbolPosition = this.getLayout().indexOf(itemSymbol, itemSymbolPosition + 1);
            if (itemSymbolPosition >= this.getLayout().length()) {
                throw new IndexOutOfBoundsException("Not enough space in the layout for items.");
            }
            this.setButton(itemSymbolPosition, items.get(idx));
        }

        int totalPages = (int) Math.ceil((double) items.size() / pageSize);

        // Set Previous Page Button
        this.setButton(configCopy.previewButton.getSymbol(),
                new ChestButton(configCopy.previewButton) {
                    @Override
                    public void onClick(int slot) {
                        if (currentPage == 1) {
                            return;
                        }
                        applyListConfiguration(configCopy, currentPage - 1);
                        open();
                    }
                }.setLoreArgs(currentPage, totalPages));

        // Set Next Page Button
        this.setButton(configCopy.nextButton.getSymbol(),
                new ChestButton(configCopy.nextButton) {
                    @Override
                    public void onClick(int slot) {
                        if (currentPage * pageSize >= items.size()) {
                            return;
                        }
                        applyListConfiguration(configCopy, currentPage + 1);
                        open();
                    }
                }.setLoreArgs(currentPage, totalPages));

        super.open();
    }

    /**
     * Returns the current page number.
     *
     * @return the current page (1-based)
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Returns the page size (number of items per page).
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }
}
