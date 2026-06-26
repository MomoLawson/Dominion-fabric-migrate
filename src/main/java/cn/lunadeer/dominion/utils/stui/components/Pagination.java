package cn.lunadeer.dominion.utils.stui.components;

import cn.lunadeer.dominion.utils.stui.ViewStyles;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric port of Pagination component. Renders page navigation buttons
 * using ListViewButton / FunctionalButton backed by Fabric MutableComponent.
 */
public class Pagination {
    private int currentPage = 1;
    private int totalItems = 0;
    private int itemsPerPage = 10;

    private ListViewButton previousButton;
    private ListViewButton nextButton;
    private FunctionalButton headerButton;

    public Pagination setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public Pagination setTotalItems(int totalItems) {
        this.totalItems = totalItems;
        return this;
    }

    public Pagination setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
        return this;
    }

    public Pagination setPreviousButton(ListViewButton previousButton) {
        this.previousButton = previousButton;
        return this;
    }

    public Pagination setNextButton(ListViewButton nextButton) {
        this.nextButton = nextButton;
        return this;
    }

    public Pagination setHeaderButton(FunctionalButton headerButton) {
        this.headerButton = headerButton;
        return this;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getOffset() {
        return (currentPage - 1) * itemsPerPage;
    }

    /**
     * Builds the pagination line: [< prev]  page X / Y  [next >]  [header]
     */
    public MutableComponent build() {
        MutableComponent result = Text.empty();

        MutableComponent leftEdge = Component.literal(" ").setStyle(Style.EMPTY.withColor(ViewStyles.SECONDARY));
        MutableComponent rightEdge = Component.literal(" ").setStyle(Style.EMPTY.withColor(ViewStyles.SECONDARY));

        // Previous button
        if (previousButton != null && currentPage > 1) {
            previousButton.setPage(currentPage - 1);
            previousButton.setText("<<");
            result.append(previousButton.build());
        } else {
            result.append(Component.literal("<<").setStyle(Style.EMPTY.withColor(ViewStyles.SECONDARY).withStrikethrough(true)));
        }

        // Page info
        MutableComponent pageInfo = Component.literal("  " + currentPage + " / " + getTotalPages() + "  ")
                .setStyle(Style.EMPTY.withColor(ViewStyles.PRIMARY));
        result.append(pageInfo);

        // Next button
        if (nextButton != null && currentPage < getTotalPages()) {
            nextButton.setPage(currentPage + 1);
            nextButton.setText(">>");
            result.append(nextButton.build());
        } else {
            result.append(Component.literal(">>").setStyle(Style.EMPTY.withColor(ViewStyles.SECONDARY).withStrikethrough(true)));
        }

        // Header button (optional)
        if (headerButton != null) {
            result.append(Component.literal("  "));
            result.append(headerButton.build());
        }

        return result;
    }
}
