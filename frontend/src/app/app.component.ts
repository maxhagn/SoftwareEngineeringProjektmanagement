import { Component, OnInit } from '@angular/core';
import { MessageService } from "./services/message.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  public showMessage: boolean = false;
  public pushMessage: string = '';

  private currentTimer: number = null;

  constructor(private messageService: MessageService) {
  }

  ngOnInit() {
    this.messageService.messageSubject.subscribe(
      (next) => {
        this.showNextMessage(next)
      }
    )
  }

  private showNextMessage(message: string) {
    this.pushMessage = message;
    this.showMessage = true;
    if(this.currentTimer != null) {
      clearTimeout(this.currentTimer);
    }
    this.currentTimer = setTimeout(() => {
      this.vanishMessage();
    }, 3000)
  }

  public vanishMessage() {
    this.showMessage = false;
  }

}
