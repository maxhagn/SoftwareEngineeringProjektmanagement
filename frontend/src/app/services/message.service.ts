import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private readonly _messageSubject: Subject<string>

  constructor() {
    this._messageSubject = new Subject<string>();
  }

  get messageSubject(): Subject<string> {
    return this._messageSubject;
  }

  public publishMessage(message: string) {
    this._messageSubject.next(message);
  }

}
