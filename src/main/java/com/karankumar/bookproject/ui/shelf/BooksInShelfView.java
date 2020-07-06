/*
    The book project lets a user keep track of different books they've read, are currently reading or would like to read
    Copyright (C) 2020  Karan Kumar

    This program is free software: you can redistribute it and/or modify it under the terms of the
    GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
    warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program.
    If not, see <https://www.gnu.org/licenses/>.
 */

package com.karankumar.bookproject.ui.shelf;

import com.karankumar.bookproject.backend.model.Book;
import com.karankumar.bookproject.backend.model.CustomShelf;
import com.karankumar.bookproject.backend.model.PredefinedShelf;
import com.karankumar.bookproject.backend.service.BookService;
import com.karankumar.bookproject.backend.service.CustomShelfService;
import com.karankumar.bookproject.backend.service.PredefinedShelfService;
import com.karankumar.bookproject.ui.MainView;
import com.karankumar.bookproject.ui.book.BookForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import lombok.extern.java.Log;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.karankumar.bookproject.ui.shelf.ShelfUtils.whichPredefinedShelf;

/**
 * Contains a {@code BookForm} and a Grid containing a list of books in a given {@code Shelf}.
 */

@Route(value = "", layout = MainView.class)
@RouteAlias(value = "myBooks", layout = MainView.class)
@PageTitle("My Books | Book Project")
@Log
public class BooksInShelfView extends VerticalLayout {
    public static final String TITLE_KEY = "title";
    public static final String AUTHOR_KEY = "author";
    public static final String GENRE_KEY = "genre";
    public static final String PAGES_KEY = "numberOfPages";
    public static final String RATING_KEY = "rating";
    public static final String DATE_STARTED_KEY = "dateStartedReading";
    public static final String DATE_FINISHED_KEY = "dateFinishedReading";

    public final Grid<Book> bookGrid = new Grid<>(Book.class);
    //    public final ComboBox<PredefinedShelf.ShelfName> whichShelf;
    public final ComboBox<String> whichShelf;

    private final BookForm bookForm;
    private final CustomShelfForm shelfForm;
    private final BookService bookService;
    private final PredefinedShelfService predefinedShelfService;
    private final CustomShelfService customShelfService;
    private final TextField filterByTitle;

    //    private PredefinedShelf.ShelfName chosenShelf;
    private String chosenShelf;
    private String bookTitle; // the book to filter by (if specified)

    public BooksInShelfView(BookService bookService,
                            PredefinedShelfService predefinedShelfService, CustomShelfService customShelfService) {
        this.bookService = bookService;
        this.predefinedShelfService = predefinedShelfService;
        this.customShelfService = customShelfService;

        whichShelf = new ComboBox<>();
        configureChosenShelf();

        filterByTitle = new TextField();
        configureFilter();

        bookForm = new BookForm(predefinedShelfService, customShelfService);
        shelfForm = new CustomShelfForm(customShelfService);

        Button addBook = new Button("Add book");
        addBook.addClickListener(e -> bookForm.addBook());
        Button createShelf = new Button("Create shelf");
        createShelf.addClickListener(e -> shelfForm.addShelf());
        HorizontalLayout horizontalLayout = new HorizontalLayout(whichShelf, filterByTitle, addBook, createShelf);
        horizontalLayout.setAlignItems(Alignment.END);

        configureBookGrid();
        add(horizontalLayout, bookGrid);

        add(bookForm);
        add(shelfForm);

        bookForm.addListener(BookForm.SaveEvent.class, this::saveBook);
        bookForm.addListener(BookForm.DeleteEvent.class, this::deleteBook);

        shelfForm.addListener(CustomShelfForm.SaveEvent.class, this::saveShelf);

        bookGrid
                .asSingleSelect()
                .addValueChangeListener(
                        event -> {
                            if (event == null) {
                                LOGGER.log(Level.FINE, "Event is null");
                            } else if (event.getValue() == null) {
                                LOGGER.log(Level.FINE, "Event value is null");
                            } else {
                                editBook(event.getValue());
                            }
                        }
                );
    }

    private void configureChosenShelf() {
        whichShelf.setPlaceholder("Select shelf");
        updateShelfList();
        whichShelf.setRequired(true);
        whichShelf.addValueChangeListener(event -> {
            if (event.getValue() == null) {
                LOGGER.log(Level.FINE, "No choice selected");
            } else {
                chosenShelf = event.getValue();
                updateList();
                showOrHideGridColumns(chosenShelf);
            }
        });
    }

    void showOrHideGridColumns(String shelfName) {
        PredefinedShelf.ShelfName predefinedShelfName = whichPredefinedShelf(shelfName);
        if (predefinedShelfName == null) {
            toggleColumn(RATING_KEY, true);
            toggleColumn(DATE_STARTED_KEY, true);
            toggleColumn(DATE_FINISHED_KEY, true);
            return;
        }

        switch (predefinedShelfName) {
            case TO_READ:
                toggleColumn(RATING_KEY, false);
                toggleColumn(DATE_STARTED_KEY, false);
                toggleColumn(DATE_FINISHED_KEY, false);
                break;
            case READING:
            case DID_NOT_FINISH:
                toggleColumn(RATING_KEY, false);
                toggleColumn(DATE_STARTED_KEY, true);
                toggleColumn(DATE_FINISHED_KEY, false);
                break;
            case READ:
                toggleColumn(RATING_KEY, true);
                toggleColumn(DATE_STARTED_KEY, true);
                toggleColumn(DATE_FINISHED_KEY, true);
                break;
            default:
        }
    }

    private void toggleColumn(String columnKey, boolean isOn) {
        bookGrid.getColumnByKey(columnKey).setVisible(isOn);
    }

    private void configureBookGrid() {
        addClassName("book-grid");
        bookGrid.setColumns(TITLE_KEY, AUTHOR_KEY, GENRE_KEY);

        bookGrid.addColumn(new LocalDateRenderer<>(
                Book::getDateStartedReading, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .setHeader("Date started reading")
                .setComparator(Comparator.comparing(Book::getDateStartedReading))
                .setKey(DATE_STARTED_KEY);

        bookGrid.addColumn(new LocalDateRenderer<>(
                Book::getDateFinishedReading, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .setHeader("Date finished reading")
                .setComparator(Comparator.comparing(Book::getDateStartedReading))
                .setSortable(true)
                .setKey(DATE_FINISHED_KEY);

        bookGrid.addColumn(RATING_KEY);
        bookGrid.addColumn(PAGES_KEY);
    }

    private void updateList() {
        if (chosenShelf == null) {
            LOGGER.log(Level.FINEST, "Chosen shelf is null");
            return;
        }

        PredefinedShelf.ShelfName predefinedShelfName = whichPredefinedShelf(chosenShelf);
        if (predefinedShelfName == null) {
            List<CustomShelf> matchingShelves = customShelfService.findAll(chosenShelf);

            if (!matchingShelves.isEmpty()) {
                if (bookTitle != null && !bookTitle.isEmpty()) {
                    LOGGER.log(Level.INFO, "Searching for the filter " + bookTitle);
                    bookGrid.setItems(bookService.findAll(bookTitle));
                } else if (matchingShelves.size() == 1) {
                    LOGGER.log(Level.INFO, "Found 1 shelf: " + matchingShelves.get(0));
                    CustomShelf selectedShelf = matchingShelves.get(0);
                    bookGrid.setItems(selectedShelf.getBooks());
                } else {
                    LOGGER.log(Level.SEVERE, matchingShelves.size() + " matching shelves found for " + chosenShelf);
                }
            } else {
                LOGGER.log(Level.SEVERE, "No matching shelves found for " + chosenShelf);
            }
        } else {
            // Find the shelf that matches the chosen shelf's name
            List<PredefinedShelf> matchingShelves = predefinedShelfService.findAll(predefinedShelfName);

            if (!matchingShelves.isEmpty()) {
                if (bookTitle != null && !bookTitle.isEmpty()) {
                    LOGGER.log(Level.INFO, "Searching for the filter " + bookTitle);
                    bookGrid.setItems(bookService.findAll(bookTitle));
                } else if (matchingShelves.size() == 1) {
                    LOGGER.log(Level.INFO, "Found 1 shelf: " + matchingShelves.get(0));
                    PredefinedShelf selectedShelf = matchingShelves.get(0);
                    bookGrid.setItems(selectedShelf.getBooks());
                } else {
                    LOGGER.log(Level.SEVERE, matchingShelves.size() + " matching shelves found for " + chosenShelf);
                }
            } else {
                LOGGER.log(Level.SEVERE, "No matching shelves found for " + chosenShelf);
            }
        }
    }

    private void updateShelfList() {
        List<String> shelfNames = new ArrayList<>();
        // first add the predefined shelves
        for (PredefinedShelf.ShelfName name : PredefinedShelf.ShelfName.values()) {
            shelfNames.add(name.toString());
        }
        LOGGER.log(Level.INFO, "custom shelf service size: " + customShelfService.findAll().size());
        shelfNames.addAll(customShelfService.findAll().stream()
                .map(CustomShelf::getShelfName)
                .collect(Collectors.toList()));
        whichShelf.setItems(shelfNames);

        LOGGER.log(Level.INFO, "in updateShelfList()");
    }

    private void configureFilter() {
        filterByTitle.setPlaceholder("Filter by book title");
        filterByTitle.setClearButtonVisible(true);
        filterByTitle.setValueChangeMode(ValueChangeMode.LAZY);
        filterByTitle.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                bookTitle = event.getValue();
            }
            updateList();
        });
    }

    private void editBook(Book book) {
        if (book != null && bookForm != null) {
            bookForm.setBook(book);
            bookForm.openForm();
        }
    }

    private void deleteBook(BookForm.DeleteEvent event) {
        LOGGER.log(Level.INFO, "Deleting book...");
        bookService.delete(event.getBook());
        updateList();
    }

    private void saveBook(BookForm.SaveEvent event) {
        LOGGER.log(Level.INFO, "Saving book...");
        if (event.getBook() == null) {
            LOGGER.log(Level.SEVERE, "Retrieved book from event is null");
        } else {
            LOGGER.log(Level.INFO, "Book is not null");
            bookService.save(event.getBook());
            updateList();
        }
    }

    private void saveShelf(CustomShelfForm.SaveEvent event) {
        if (event.getCustomShelf() == null) {
            LOGGER.log(Level.SEVERE, "Retrieved shelf from event is null");
        } else {
            customShelfService.save(event.getCustomShelf());
            updateShelfList();
        }
    }

}
