import {Component, OnInit} from '@angular/core';
import {ListUserResult} from '../../../../dtos/user/list-user-result';
import {UserService} from '../../../../services/user.service';
import {HelperUtils} from '../../../../global/helper-utils.service';
import {PartialObserver} from 'rxjs';

@Component({
  selector: 'app-users-view',
  templateUrl: './users-view.component.html',
  styleUrls: ['./users-view.component.css']
})
export class UsersViewComponent implements OnInit {

  public currentPage: number = 0;
  public pagesToDisplay: number[] = [];

  /**
   * Info about the current content and page
   * */
  public page: ListUserResult = new ListUserResult(1, 0, []);

  /**
   * Description of error if one occured
   * */
  public errorMessage: string = '';

  /**
   * Description of info if user is reset
   * */
  public infoMessage: string = '';

  constructor(private userService: UserService, private helperUtils: HelperUtils) {
  }

  ngOnInit(): void {
    this.loadPage();
  }

  private loadPage() {
    this.userService.listUsers(this.currentPage).subscribe(
      (next) => {
        this.page = next;
        this.pagesToDisplay = this.pages();
      },
      (error: any) => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    );
  }

  /**
   * Provide method to get pagenumbers to display
   * */
  private pages(): number[] {
    let pagesToDisplay: number[] = [];

    for(let i = this.currentPage - 2; pagesToDisplay.length < 5 && i < this.page.totalPages; i++) {
      if(i >= 0) {
        pagesToDisplay.push(i);
      }
    }

    return pagesToDisplay;
  }

  /**
   * Sends request to reset password of user
   * */
  public reset(userID: number, username: string, usersurname: string) {
    this.userService.resetPassword(userID).subscribe(
      response => {
        this.infoMessage = 'Password reset link for "' + username + ' ' + usersurname + '" has been sent.';
      },
      error => {
        if (error.statusText === 'OK') {
          this.infoMessage = 'Password reset link for "' + username + ' ' + usersurname + '" has been sent.';
        } else {
          this.errorMessage = this.helperUtils.getErrorText(error);
        }
      }
    );
  }

  /**
   * @param userId id of user which should be locked or unlocked
   * @param lock wether user should be locked or unlocked
   * */
  public lock(userId: number, lock: boolean) {
    let subscriber: PartialObserver<void> = {
      next: _ => {
        this.page.content.forEach(user => {
          if (user.id == userId) {
            user.locked = lock;
          }
        });
      },
      error: err => {
        this.errorMessage = this.helperUtils.getErrorText(err);
      }
    };
    if (lock) {
      this.userService.lockUser(userId).subscribe(subscriber);
    } else {
      this.userService.unlockUser(userId).subscribe(subscriber);
    }
  }

  /**
   * Change page which is currently shown
   * @param page which should be loaded
   * */
  public changePage(page: number) {
    this.currentPage = page;
    this.loadPage();
  }

  public nextPage() {
    if(this.currentPage + 1 <= this.page.totalPages - 1) {
      this.currentPage = this.currentPage + 1;
      this.loadPage();
    }
  }

  public previousPage() {
    if(this.currentPage - 1 >= 0) {
      this.currentPage = this.currentPage - 1;
      this.loadPage();
    }
  }
}
