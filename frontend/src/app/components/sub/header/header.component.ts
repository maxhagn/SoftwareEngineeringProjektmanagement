import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../../services/auth.service';
import {Role} from '../../../global/role';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  ADMIN = Role.ADMIN;

  constructor(public authService: AuthService) { }

  ngOnInit() {
  }

}
