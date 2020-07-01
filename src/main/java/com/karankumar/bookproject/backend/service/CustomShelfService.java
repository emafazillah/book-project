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

package com.karankumar.bookproject.backend.service;

import com.karankumar.bookproject.backend.model.CustomShelf;
import com.karankumar.bookproject.backend.repository.CustomShelfRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A Spring service that acts as the gateway to the {@code ShelfRepository} -- to use the {@code ShelfRepository},
 * a consumer should go via this {@code ShelfService}.
 */
@Service
@Log
public class CustomShelfService extends BaseService<CustomShelf, Long> {

    private final CustomShelfRepository customShelfRepository;

    public CustomShelfService(CustomShelfRepository customShelfRepository) {
        this.customShelfRepository = customShelfRepository;
    }

    @Override
    public CustomShelf findById(Long id) {
        return customShelfRepository.getOne(id);
    }

    @Override
    public void save(CustomShelf shelf) {
        customShelfRepository.save(shelf);
    }

    public List<CustomShelf> findAll() {
        return customShelfRepository.findAll();
    }

    public List<CustomShelf> findAll(String shelfName) {
        return customShelfRepository.findAll();
    }

    @Override
    public void delete(CustomShelf shelf) {
        customShelfRepository.delete(shelf);
    }

    @Override
    public void deleteAll() {
        customShelfRepository.deleteAll();
    }
}
