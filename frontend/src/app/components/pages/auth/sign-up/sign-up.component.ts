import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../../../services/auth.service';
import {SignUpUser} from '../../../../dtos/user/sign-up-user';
import {HelperUtils} from '../../../../global/helper-utils.service';
import { MessageService } from "../../../../services/message.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})

export class SignUpComponent implements OnInit {

  public signupForm: FormGroup;
  public submitted: boolean = false;
  // When this is set, display an error
  public errorMessage: string = '';

  constructor(private router: Router, private formBuilder: FormBuilder, private authService: AuthService, private helperUtils: HelperUtils, private messageService: MessageService) {
    this.signupForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      surname: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      repeatpassword: ['', [Validators.required, Validators.minLength(8)]]
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
        const signUpUser = new SignUpUser(data.email, data.password, data.name, data.surname, false);
        this.authService.signUp(signUpUser).subscribe(
          () => {
            // Here you see how to show your message
            this.messageService.publishMessage("Successfuly created Account");
            // Here you redirect
            this.router.navigate(['/login']);
          },
          (error: any) => {
            this.errorMessage = this.helperUtils.getErrorText(error);
          }
        );
      }
    }
  }

}
