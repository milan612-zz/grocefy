/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { GroupsComponentsPage, GroupsDeleteDialog, GroupsUpdatePage } from './groups.page-object';

const expect = chai.expect;

describe('Groups e2e test', () => {
    let navBarPage: NavBarPage;
    let signInPage: SignInPage;
    let groupsUpdatePage: GroupsUpdatePage;
    let groupsComponentsPage: GroupsComponentsPage;
    let groupsDeleteDialog: GroupsDeleteDialog;

    before(async () => {
        await browser.get('/');
        navBarPage = new NavBarPage();
        signInPage = await navBarPage.getSignInPage();
        await signInPage.autoSignInUsing('admin', 'admin');
        await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
    });

    it('should load Groups', async () => {
        await navBarPage.goToEntity('groups');
        groupsComponentsPage = new GroupsComponentsPage();
        await browser.wait(ec.visibilityOf(groupsComponentsPage.title), 5000);
        expect(await groupsComponentsPage.getTitle()).to.eq('Groups');
    });

    it('should load create Groups page', async () => {
        await groupsComponentsPage.clickOnCreateButton();
        groupsUpdatePage = new GroupsUpdatePage();
        expect(await groupsUpdatePage.getPageTitle()).to.eq('Create or edit a Groups');
        await groupsUpdatePage.cancel();
    });

    it('should create and save Groups', async () => {
        const nbButtonsBeforeCreate = await groupsComponentsPage.countDeleteButtons();

        await groupsComponentsPage.clickOnCreateButton();
        await promise.all([
            groupsUpdatePage.setTitleInput('title'),
            groupsUpdatePage.statusSelectLastOption(),
            groupsUpdatePage.setUserListInput('userList')
            // groupsUpdatePage.userSelectLastOption(),
        ]);
        expect(await groupsUpdatePage.getTitleInput()).to.eq('title');
        expect(await groupsUpdatePage.getUserListInput()).to.eq('userList');
        await groupsUpdatePage.save();
        expect(await groupsUpdatePage.getSaveButton().isPresent()).to.be.false;

        expect(await groupsComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
    });

    it('should delete last Groups', async () => {
        const nbButtonsBeforeDelete = await groupsComponentsPage.countDeleteButtons();
        await groupsComponentsPage.clickOnLastDeleteButton();

        groupsDeleteDialog = new GroupsDeleteDialog();
        expect(await groupsDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Groups?');
        await groupsDeleteDialog.clickOnConfirmButton();

        expect(await groupsComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    });

    after(async () => {
        await navBarPage.autoSignOut();
    });
});
