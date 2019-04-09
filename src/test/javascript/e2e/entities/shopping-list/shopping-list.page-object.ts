import { element, by, ElementFinder } from 'protractor';

export class ShoppingListComponentsPage {
    createButton = element(by.id('jh-create-entity'));
    deleteButtons = element.all(by.css('jhi-shopping-list div table .btn-danger'));
    title = element.all(by.css('jhi-shopping-list div h2#page-heading span')).first();

    async clickOnCreateButton() {
        await this.createButton.click();
    }

    async clickOnLastDeleteButton() {
        await this.deleteButtons.last().click();
    }

    async countDeleteButtons() {
        return this.deleteButtons.count();
    }

    async getTitle() {
        return this.title.getText();
    }
}

export class ShoppingListUpdatePage {
    pageTitle = element(by.id('jhi-shopping-list-heading'));
    saveButton = element(by.id('save-entity'));
    cancelButton = element(by.id('cancel-save'));
    titleInput = element(by.id('field_title'));
    listInput = element(by.id('field_list'));
    stateSelect = element(by.id('field_state'));
    ownerSelect = element(by.id('field_owner'));
    shopperSelect = element(by.id('field_shopper'));

    async getPageTitle() {
        return this.pageTitle.getText();
    }

    async setTitleInput(title) {
        await this.titleInput.sendKeys(title);
    }

    async getTitleInput() {
        return this.titleInput.getAttribute('value');
    }

    async setListInput(list) {
        await this.listInput.sendKeys(list);
    }

    async getListInput() {
        return this.listInput.getAttribute('value');
    }

    async setStateSelect(state) {
        await this.stateSelect.sendKeys(state);
    }

    async getStateSelect() {
        return this.stateSelect.element(by.css('option:checked')).getText();
    }

    async stateSelectLastOption() {
        await this.stateSelect
            .all(by.tagName('option'))
            .last()
            .click();
    }

    async ownerSelectLastOption() {
        await this.ownerSelect
            .all(by.tagName('option'))
            .last()
            .click();
    }

    async ownerSelectOption(option) {
        await this.ownerSelect.sendKeys(option);
    }

    getOwnerSelect(): ElementFinder {
        return this.ownerSelect;
    }

    async getOwnerSelectedOption() {
        return this.ownerSelect.element(by.css('option:checked')).getText();
    }

    async shopperSelectLastOption() {
        await this.shopperSelect
            .all(by.tagName('option'))
            .last()
            .click();
    }

    async shopperSelectOption(option) {
        await this.shopperSelect.sendKeys(option);
    }

    getShopperSelect(): ElementFinder {
        return this.shopperSelect;
    }

    async getShopperSelectedOption() {
        return this.shopperSelect.element(by.css('option:checked')).getText();
    }

    async save() {
        await this.saveButton.click();
    }

    async cancel() {
        await this.cancelButton.click();
    }

    getSaveButton(): ElementFinder {
        return this.saveButton;
    }
}

export class ShoppingListDeleteDialog {
    private dialogTitle = element(by.id('jhi-delete-shoppingList-heading'));
    private confirmButton = element(by.id('jhi-confirm-delete-shoppingList'));

    async getDialogTitle() {
        return this.dialogTitle.getText();
    }

    async clickOnConfirmButton() {
        await this.confirmButton.click();
    }
}
