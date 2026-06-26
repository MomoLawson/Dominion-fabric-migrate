package cn.lunadeer.dominion.utils.stui;

import cn.lunadeer.dominion.utils.stui.components.Line;
import cn.lunadeer.dominion.utils.stui.components.Pagination;
import cn.lunadeer.dominion.utils.stui.components.buttons.FunctionalButton;
import cn.lunadeer.dominion.utils.stui.components.buttons.ListViewButton;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric port of ListView. Creates a paginated view that delegates to
 * View for rendering. Uses Pagination, Line, ListViewButton.
 */
public class ListView {
    private String title = "";
    private String subtitle = "";
    private String navigator = "";
    private String actionbar = "";

    private int currentPage = 1;
    private int itemsPerPage = 10;

    private final List<Line> allLines = new ArrayList<>();

    private ListViewButton previousButton;
    private ListViewButton nextButton;
    private FunctionalButton headerButton;

    public ListView setTitle(String title) {
        this.title = title;
        return this;
    }

    public ListView setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public ListView setNavigator(String navigator) {
        this.navigator = navigator;
        return this;
    }

    public ListView setActionbar(String actionbar) {
        this.actionbar = actionbar;
        return this;
    }

    public ListView setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public ListView setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
        return this;
    }

    public ListView addLine(Line line) {
        allLines.add(line);
        return this;
    }

    public ListView setPreviousButton(ListViewButton button) {
        this.previousButton = button;
        return this;
    }

    public ListView setNextButton(ListViewButton button) {
        this.nextButton = button;
        return this;
    }

    public ListView setHeaderButton(FunctionalButton button) {
        this.headerButton = button;
        return this;
    }

    /**
     * Builds the paginated view and shows it to the player.
     */
    public void show(ServerPlayer player) {
        Pagination pagination = new Pagination()
                .setCurrentPage(currentPage)
                .setTotalItems(allLines.size())
                .setItemsPerPage(itemsPerPage);

        if (previousButton != null) {
            pagination.setPreviousButton(previousButton);
        }
        if (nextButton != null) {
            pagination.setNextButton(nextButton);
        }
        if (headerButton != null) {
            pagination.setHeaderButton(headerButton);
        }

        // Calculate sub-list for current page
        int offset = pagination.getOffset();
        int end = Math.min(offset + itemsPerPage, allLines.size());
        List<Line> pageLines = (offset < allLines.size())
                ? allLines.subList(offset, end)
                : new ArrayList<>();

        // Build subtitle with pagination info
        String paginatedSubtitle = subtitle;
        if (allLines.size() > itemsPerPage) {
            paginatedSubtitle = subtitle + " (" + pagination.getCurrentPage()
                    + "/" + pagination.getTotalPages() + ")";
        }

        View view = new View()
                .setTitle(title)
                .setSubtitle(paginatedSubtitle)
                .setNavigator(navigator)
                .setActionbar(actionbar);

        // Add page content lines
        for (Line line : pageLines) {
            view.addContentLine(line);
        }

        // Add pagination navigation line as the last content line (only when needed)
        if (allLines.size() > itemsPerPage) {
            MutableComponent paginationText = pagination.build();
            Line paginationLine = Line.create().setDivider("").append(paginationText);
            view.addContentLine(paginationLine);
        }

        view.show(player);
    }
}
