import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {HelperUtils} from '../../../../global/helper-utils.service';
import {UserService} from '../../../../services/user.service';
import {SignUpUser} from '../../../../dtos/user/sign-up-user';
import {AuthService} from '../../../../services/auth.service';
import {Router} from '@angular/router';
import {MessageService} from '../../../../services/message.service';

@Component({
  selector: 'app-users-detail',
  templateUrl: './users-detail.component.html',
  styleUrls: ['./users-detail.component.css']
})
export class UsersDetailComponent implements OnInit {
  public user: SignUpUser;
  public updateForm: FormGroup;
  public submitted: boolean = false;
  // When this is set, display an error
  public errorMessage: string = '';
  public changePassword = false;
  public infoMessage: string = '';
  public sure: boolean;
  public updateUser: SignUpUser;
  public pwerrorlength: boolean;
  public pwsame: boolean;
  public deleteSure: boolean = false;
  public deleteMessage: string = 'Your account will be deleted permanently. Doing so will NOT delete your tickets, you can still use them printed out but will be unable to access them here. This holds true both for bought and reserved tickets.';
  public pwButtonTexts = ['Change Password', 'Do not change Password'];
  public pwButtonIndex = 0;

  constructor(private formBuilder: FormBuilder, private userService: UserService, private helperUtils: HelperUtils, private authService: AuthService, private router: Router, private messageService: MessageService) {
    this.updateForm = this.formBuilder.group({
      firstname: [''],
      surname: [''],
      email: ['', [Validators.email]],
      password: [''],
      repeatpassword: ['']
    });
  }

  ngOnInit(): void {
    this.userService.getUser().subscribe(
      user => {
        this.user = user;
      },
      error => {
        this.errorMessage = this.helperUtils.getErrorText(error);
      }
    );
  }


  public delete() {
    this.userService.delete().subscribe(
      () => {
      },
      error => {
        this.errorMessage = error.error;
      }
    );
    this.authService.logoutUser();
    this.router.navigate(['/']);
  }

  public submit(data: any) {
    if (this.updateForm.valid) {
      this.submitted = true;
      this.pwsame = !(data.password === data.repeatpassword) && this.changePassword;
      this.pwerrorlength = data.password.length < 8 && this.changePassword;
      if (!(this.pwsame || this.pwerrorlength)) {
        this.sure = !this.sure;
      }
      this.updateUser = new SignUpUser(data.email, data.password, data.firstname, data.surname, data.isadmin);
      if (!this.changePassword) {
        this.updateUser.password = null;
      } else {
        if (data.password.length < 1) {
          this.updateUser.password = null;
        }
      }
      if (data.firstname.length < 1) {
        this.updateUser.firstname = null;
      }
      if (data.surname.length < 1) {
        this.updateUser.surname = null;
      }
      if (data.email.length < 1) {
        this.updateUser.email = null;
      }
      if (!(data.password === data.repeatpassword)) {
        this.errorMessage = 'Please repeat the correct password';
      } else {
        this.userService.updateUser(this.updateUser).subscribe(
          () => {
            this.messageService.publishMessage('User updated successfully');
            if (this.updateUser.password || this.updateUser.email) {
              this.authService.logoutUser();
              this.router.navigate(['/']);
            } else {
              this.ngOnInit();
            }
          },
          (error: any) => {
            console.log(error);
            this.errorMessage = error.error.error;
          }
        );
      }
    }
  }

  public pwButtonToggle() {
    this.changePassword = !this.changePassword;
    this.pwButtonIndex = (this.pwButtonIndex + 1) % 2;
  }


}
