package com.karankumar.bookproject.ui.shelf;

import com.karankumar.bookproject.backend.model.PredefinedShelf;

public final class ShelfUtils {
    private ShelfUtils() {
    }

    /**
     * Checks whether a shelf name matches a predefined shelf name.
     *
     * @param shelfName the name of the shelf to check whether it matches a predefined shelf
     * @return the enum value of the PredefinedShelf. If the passed in shelf name does not match the name of a
     * predefined shelf, null is returned.
     */
    public static PredefinedShelf.ShelfName whichPredefinedShelf(String shelfName) {
        PredefinedShelf.ShelfName predefinedShelf = null;
        PredefinedShelf.ShelfName[] predefinedShelfNames = PredefinedShelf.ShelfName.values();
        for (PredefinedShelf.ShelfName predefinedShelfName : predefinedShelfNames) {
            if (predefinedShelfName.toString().equals(shelfName)) {
                predefinedShelf = predefinedShelfName;
                break;
            }
        }
        return predefinedShelf;
    }
}
