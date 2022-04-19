import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { SignInUser } from "../../../../dtos/user/sign-in-user";
import { HelperUtils } from "../../../../global/helper-utils.service";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  // Wether form was submit at least one time
  public submitted: boolean = false;
  // Form used to validate inputs and retrive data
  public signinForm: FormGroup;

  // As soon as this is set to an error an error dialog is shown
  public errorMessage: string = ''

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router, private helperUtils: HelperUtils) {
    this.signinForm = this.formBuilder.group({
      mail: ['', []],
      password: ['', []]
    })
  }

  ngOnInit() {
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the home page
   * @param data recieved from the form
   */
  public onSubmit(data: any) {
    this.submitted = true;
    if (this.signinForm.valid) {
      const signInUser: SignInUser = new SignInUser(data.mail, data.password);
      this.authService.signIn(signInUser).subscribe(
        () => {
          this.router.navigate(['/home']);
        },
        (error: any) => {
          this.errorMessage = this.helperUtils.getErrorText(error)
        }
      );
    }
  }

}
