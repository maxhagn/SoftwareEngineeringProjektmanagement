import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../../../services/user.service';
import {MessageService} from '../../../../services/message.service';

@Component({
  selector: 'app-reset-pw',
  templateUrl: './reset-pw.component.html',
  styleUrls: ['./reset-pw.component.css']
})
export class ResetPwComponent implements OnInit {

  private token = '';

  public pw = '';
  public pwRepeat = '';

  public errorMessage = '';

  public errors = {
    'length': false,
    'ident': false,
  };

  constructor(private route: ActivatedRoute,
              private router: Router,
              private userService: UserService,
              private messageService: MessageService) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.token = params.token;
    });
  }

  submit() {

    this.errors = {
      'length': false,
      'ident': false,
    };

    if (this.pw.length < 8 || this.pwRepeat.length < 8) {
      this.errors.length = true;
      return;
    }

    if (this.pw !== this.pwRepeat) {
      this.errors.ident = true;
      return;
    }


    this.userService.actuallyResetPw(this.token, this.pw).subscribe(response => {
      this.router.navigate(['/login']).then( c => {
        this.messageService.publishMessage('Sucessfully reset password');
      });
    }, error => {
        this.errorMessage = error.error.error;
      });
  }
}
