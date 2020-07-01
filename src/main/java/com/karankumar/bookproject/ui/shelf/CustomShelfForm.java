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

import com.karankumar.bookproject.backend.model.CustomShelf;
import com.karankumar.bookproject.backend.service.CustomShelfService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import lombok.extern.java.Log;

import java.util.logging.Level;

/**
 * A Vaadin form for adding a new {@code CustomShelf}.
 */
@CssImport(
    value = "./styles/vaadin-dialog-overlay-styles.css",
    themeFor = "vaadin-dialog-overlay"
)
@Log
public class CustomShelfForm extends VerticalLayout {
    public final TextField shelfName = new TextField();
    public final Button saveButton = new Button();

    private final CustomShelfService customShelfService;
    private final Dialog dialog;

    public Button delete = new Button();

    Binder<CustomShelf> binder = new BeanValidationBinder<>(CustomShelf.class);

    public CustomShelfForm(CustomShelfService customShelfService) {
        this.customShelfService = customShelfService;

        configureSaveButton();

        dialog = new Dialog();
        dialog.setCloseOnOutsideClick(true);

        FormLayout formLayout = new FormLayout();
        dialog.add(formLayout);

        configureBinder();
        configureShelfName();

        formLayout.setResponsiveSteps();
        formLayout.addFormItem(shelfName, "Shelf name *");
        formLayout.add(saveButton);

        add(dialog);
    }

    private void configureShelfName() {
        shelfName.setPlaceholder("Enter a shelf name");
        shelfName.setClearButtonVisible(true);
        shelfName.setRequired(true);
        shelfName.setRequiredIndicatorVisible(true);
        shelfName.setMinWidth("23em");
    }

    private void configureSaveButton() {
        saveButton.setText("Add shelf");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    private void configureBinder() {
        binder.forField(shelfName)
            .asRequired("Please enter a shelf name")
            .bind(CustomShelf::getShelfName, CustomShelf::setShelfName);
    }

    public void addShelf() {
        openForm();
    }

    private void openForm() {
        dialog.open();
        LOGGER.log(Level.INFO, "Opened shelf form");
    }

}
