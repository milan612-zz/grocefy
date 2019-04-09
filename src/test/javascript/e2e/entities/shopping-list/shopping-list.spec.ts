/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { ShoppingListComponentsPage, ShoppingListDeleteDialog, ShoppingListUpdatePage } from './shopping-list.page-object';

const expect = chai.expect;

describe('ShoppingList e2e test', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let shoppingListUpdatePage: ShoppingListUpdatePage;
    let shoppingListComponentsPage: ShoppingListComponentsPage;
    let shoppingListDeleteDialog: ShoppingListDeleteDialog;

    before(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage();
        signInPage = await navBarPage.getSignInPage();
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
    });

    it('should load ShoppingLists', async () => {
        await navBarPage.goToEntity('shopping-list');
        shoppingListComponentsPage = new ShoppingListComponentsPage();
        await browser.wait(ec.visibilityOf(shoppingListComponentsPage.title), 5000);
        expect(await shoppingListComponentsPage.getTitle()).to.eq('Shopping Lists');
    });

    it('should load create ShoppingList page', async () => {
        await shoppingListComponentsPage.clickOnCreateButton();
        shoppingListUpdatePage = new ShoppingListUpdatePage();
        expect(await shoppingListUpdatePage.getPageTitle()).to.eq('Create or edit a Shopping List');
        await shoppingListUpdatePage.cancel();
    });

    it('should create and save ShoppingLists', async () => {
        const nbButtonsBeforeCreate = await shoppingListComponentsPage.countDeleteButtons();

        await shoppingListComponentsPage.clickOnCreateButton();
        await promise.all([
            shoppingListUpdatePage.setTitleInput('title'),
            shoppingListUpdatePage.setListInput('list'),
            shoppingListUpdatePage.stateSelectLastOption(),
            shoppingListUpdatePage.ownerSelectLastOption(),
            shoppingListUpdatePage.shopperSelectLastOption()
        ]);
        expect(await shoppingListUpdatePage.getTitleInput()).to.eq('title');
        expect(await shoppingListUpdatePage.getListInput()).to.eq('list');
        await shoppingListUpdatePage.save();
        expect(await shoppingListUpdatePage.getSaveButton().isPresent()).to.be.false;

        expect(await shoppingListComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });

    it('should delete last ShoppingList', async () => {
        const nbButtonsBeforeDelete = await shoppingListComponentsPage.countDeleteButtons();
        await shoppingListComponentsPage.clickOnLastDeleteButton();

        shoppingListDeleteDialog = new ShoppingListDeleteDialog();
        expect(await shoppingListDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Shopping List?');
        await shoppingListDeleteDialog.clickOnConfirmButton();

        expect(await shoppingListComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });

    after(async () => {
        await navBarPage.autoSignOut();
    });
});
