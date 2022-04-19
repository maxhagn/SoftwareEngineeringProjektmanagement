import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ListUser } from "../../../dtos/user/list-user";

@Component({
  selector: 'app-users-entry',
  templateUrl: './users-entry.component.html',
  styleUrls: ['./users-entry.component.css']
})
export class UsersEntryComponent implements OnInit {

  @Input('user')
  public user: ListUser = new ListUser(0, "", "", false);

  @Output('reset')
  public resetEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output('toggleLock')
  public lockEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor() { }

  ngOnInit(): void {
  }

}
