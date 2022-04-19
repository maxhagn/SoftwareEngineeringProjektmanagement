import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../../../services/auth.service';
import {HelperUtils} from '../../../../global/helper-utils.service';
import {SignUpUser} from '../../../../dtos/user/sign-up-user';
import {MessageService} from '../../../../services/message.service';

@Component({
  selector: 'app-users-create',
  templateUrl: './users-create.component.html',
  styleUrls: ['./users-create.component.css']
})
export class UsersCreateComponent implements OnInit {

  public signupForm: FormGroup;
  public submitted: boolean = false;
  // When this is set, display an error
  public errorMessage: string = '';
  public created = false;
  public infoMessage: string = '';

  constructor(private formBuilder: FormBuilder, private helperUtils: HelperUtils, private authService: AuthService, private messageService: MessageService) {
    this.signupForm = this.formBuilder.group({
      firstname: ['', [Validators.required]],
      surname: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      repeatpassword: ['', [Validators.required, Validators.minLength(8)]],
      isadmin: [false, [Validators.required]]
    });
  }

  ngOnInit(): void {
  }

  public onSubmit(data: any) {
    this.submitted = true;
    if (this.signupForm.valid) {
      if (!(data.password === data.repeatpassword)) {
        this.errorMessage = 'Please repeat the correct password';
      } else {
        const signUpUser = new SignUpUser(data.email, data.password, data.firstname, data.surname, data.isadmin);
        this.authService.create(signUpUser).subscribe(
          () => {
            this.created = true;
            this.messageService.publishMessage('Account has been created successfully');
          },
          (error: any) => {
            this.errorMessage = this.helperUtils.getErrorText(error);
            setTimeout(() => this.errorMessage = '', 5000);
          }
        );
      }
    }
  }
}
