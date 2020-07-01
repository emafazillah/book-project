package com.karankumar.bookproject.backend.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomShelf extends BaseEntity {
    @NotNull
    @NotEmpty
    private String shelfName;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Book> books;

    public CustomShelf(@NotNull @NotEmpty String shelfName) {
        this.shelfName = shelfName;
    }

    public CustomShelf(@NotNull @NotEmpty String shelfName, Set<Book> books) {
        setShelfName(shelfName);
        this.books = books;
    }

    /**
     * @param shelfName name of the shelf
     * @return true if the shelf name was set and false otherwise
     */
    public boolean setShelfName(String shelfName) {
        List<PredefinedShelf.ShelfName> predefinedShelves = List.of(PredefinedShelf.ShelfName.values());
        boolean set = false;

        if (!predefinedShelves.contains(shelfName)) {
            this.shelfName = shelfName;
            set = true;
        }
        return set;
    }

    public void setBooks(Set<Book> books) {
        if (this.books == null) {
            this.books = books;
        } else {
            this.books.addAll(books);
        }
    }
}
