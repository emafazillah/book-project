package com.karankumar.bookproject.backend.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomShelf extends BaseEntity {
    /**
     * When setting the shelfName, it is expected that there are no name clashes with any other shelf
     */
    @NotNull
    @NotEmpty
    private String shelfName;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Book> books;

    public CustomShelf(@NotNull @NotEmpty String shelfName) {
        this.shelfName = shelfName;
    }

    public CustomShelf(@NotNull @NotEmpty String shelfName, Set<Book> books) {
        this.shelfName = shelfName;
        this.books = books;
    }

    public void setBooks(Set<Book> books) {
        if (this.books == null) {
            this.books = books;
        } else {
            this.books.addAll(books);
        }
    }
}
